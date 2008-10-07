/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.webadmin/src/de/willuhn/jameica/webadmin/server/RestServiceImpl.java,v $
 * $Revision: 1.8 $
 * $Date: 2008/10/07 23:45:16 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn software & services
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.webadmin.server;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.willuhn.datasource.BeanUtil;
import de.willuhn.jameica.messaging.Message;
import de.willuhn.jameica.messaging.MessageConsumer;
import de.willuhn.jameica.messaging.QueryMessage;
import de.willuhn.jameica.messaging.TextMessage;
import de.willuhn.jameica.system.Application;
import de.willuhn.jameica.webadmin.Plugin;
import de.willuhn.jameica.webadmin.rest.Context;
import de.willuhn.jameica.webadmin.rmi.RestService;
import de.willuhn.logging.Logger;
import de.willuhn.util.Settings;

/**
 * Implementierung des REST-Services.
 */
public class RestServiceImpl implements RestService
{
  private Settings settings          = null;
  private MessageConsumer register   = new RestConsumer(true);
  private MessageConsumer unregister = new RestConsumer(false);
  
  /**
   * @see de.willuhn.jameica.webadmin.rmi.RestService#handleRequest(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
   */
  public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws IOException
  {
    if (!this.isStarted())
      throw new IOException("REST service not started");

    // Wie Umlaute im Query-String (also URL+GET-Parameter) codiert sind,
    // ist nirgends so richtig spezifiziert. Also schicken es die
    // Browser unterschiedlich. Jetty interpretiert es als UTF-8.
    // Wenn man auf dem Client jedoch ISO-8859-15 als Zeichensatz
    // eingestellt hat, schickt es auch der Browser damit.
    // Effekt: Der Server weiss nicht, wie er die Umlaute interpretieren soll.
    // Das fuehrt bisweilen soweit, dass nicht nur die Umlaute in request.getParameter(name)
    // kaputt sind sondern ggf. auch noch die Buchstaben hinter dem Umlaut
    // verloren gehen.
    // Immerhin bietet Jetty eine Funktion an, um das Encoding
    // vorzugeben. Das gaenge mit explizit mit:
    // ((org.mortbay.jetty.Request)request).setQueryEncoding(String);
    // oder implizit via (damit muss der Request nicht auf org.mortbay.jetty.Request gecastet werden:
    // request.setAttribute("org.mortbay.jetty.Request.queryEncoding","String");
    
    // siehe hierzu auch
    // http://jira.codehaus.org/browse/JETTY-113
    // http://jetty.mortbay.org/jetty5/faq/faq_s_900-Content_t_International.html
    String queryencoding = de.willuhn.jameica.webadmin.Settings.SETTINGS.getString("http.queryencoding",null);
    if (queryencoding != null)
    {
      Logger.debug("query encoding: " + queryencoding);
      request.setAttribute("org.mortbay.jetty.Request.queryEncoding",queryencoding);
    }
    
    String command = request.getPathInfo();
    if (command == null)
      throw new IOException("missing REST command");
    
    String[] patterns = settings.getAttributes();
    if (patterns == null || patterns.length == 0)
      throw new IOException("no REST urls defined");

    try
    {
      Context context = new Context(request,response);
      for (int i=0;i<patterns.length;++i)
      {
        Pattern pattern = Pattern.compile(patterns[i]);
        Matcher match = pattern.matcher(command);
        
        if (match.matches())
        {
          String[] params = new String[match.groupCount()];
          for (int k=0;k<params.length;++k)
            params[k] = match.group(k+1); // wir fangen bei "1" an, weil an Pos. 0 der Pattern selbst steht

          String s = settings.getString(patterns[i],null);
          if (s == null || s.length() == 0)
          {
            Logger.warn("no command defined for REST url " + patterns[i]);
            continue;
          }
          
          int pos = s.lastIndexOf('.');
          if (pos == -1)
            throw new IOException("invalid command defined for command " + command);

          Object bean = Application.getClassLoader().load(s.substring(0,pos)).newInstance();
          String method = s.substring(pos+1);
          
          try
          {
            Logger.debug("trying to apply context");
            BeanUtil.set(bean,"context",context);
          } catch (Exception e) {
            Logger.debug("failed, skipping context");
          }
          
          Logger.debug("executing command " + command + ", class " + bean.getClass().getName() + "." + method);
          BeanUtil.invoke(bean,method,params);
          return;
        }
      }
    }
    catch (IOException e)
    {
      throw e;
    }
    catch (Exception e2)
    {
      Logger.error("error while executing command " + command,e2);
      throw new IOException("error while executing command " + command);
    }
    
    throw new IOException("no command found for REST url " + command);
  }

  /**
   * @see de.willuhn.datasource.Service#getName()
   */
  public String getName() throws RemoteException
  {
    return "REST-API Service";
  }

  /**
   * @see de.willuhn.datasource.Service#isStartable()
   */
  public boolean isStartable() throws RemoteException
  {
    return !this.isStarted();
  }

  /**
   * @see de.willuhn.datasource.Service#isStarted()
   */
  public boolean isStarted() throws RemoteException
  {
    return this.settings != null;
  }

  /**
   * @see de.willuhn.datasource.Service#start()
   */
  public void start() throws RemoteException
  {
    if (isStarted())
    {
      Logger.warn("service allread started, skipping request");
      return;
    }
    
    Logger.info("init REST registry");
    this.settings = new RestSettings();
    this.settings.setStoreWhenRead(false);
    Application.getMessagingFactory().getMessagingQueue("jameica.webadmin.rest.register").registerMessageConsumer(this.register);
    Application.getMessagingFactory().getMessagingQueue("jameica.webadmin.rest.unregister").registerMessageConsumer(this.unregister);
  }

  /**
   * @see de.willuhn.datasource.Service#stop(boolean)
   */
  public void stop(boolean arg0) throws RemoteException
  {
    if (!this.isStarted())
    {
      Logger.warn("service not started, skipping request");
      return;
    }
    
    Application.getMessagingFactory().getMessagingQueue("jameica.webadmin.rest.register").unRegisterMessageConsumer(this.register);
    Application.getMessagingFactory().getMessagingQueue("jameica.webadmin.rest.unregister").unRegisterMessageConsumer(this.unregister);
    Logger.info("REST service stopped");
    this.settings = null;
  }

  /**
   * @see de.willuhn.jameica.webadmin.rmi.RestService#register(java.lang.String, java.lang.String)
   */
  public void register(String urlPattern, String command) throws RemoteException
  {
    if (!isStarted())
      throw new RemoteException("REST service not started");
    Logger.info("register REST command " + command + ", URL pattern: " + urlPattern);
    this.settings.setAttribute(urlPattern,command);
  }
  
  /**
   * @see de.willuhn.jameica.webadmin.rmi.RestService#unregister(java.lang.String)
   */
  public void unregister(String urlPattern) throws RemoteException
  {
    if (!isStarted())
      throw new RemoteException("REST service not started");
    Logger.info("un-register URL pattern: " + urlPattern);
    this.settings.setAttribute(urlPattern,(String) null);
  }

  /**
   * Ueberschrieben, um die properties-Datei aus dem
   * Plugin-Verzeichnis als System-Preset zu verwenden.
   */
  private class RestSettings extends de.willuhn.util.Settings
  {
    /**
     * ct.
     */
    public RestSettings()
    {
      super(Application.getPluginLoader().getPlugin(Plugin.class).getResources().getPath() + File.separator + "cfg",
            Application.getConfig().getConfigDir(),
            RestService.class);
    }
  }
  
  /**
   * Hilfsklasse zum Registrieren von REST-Kommandos via Messaging
   */
  private class RestConsumer implements MessageConsumer
  {
    private boolean register = false;
    
    /**
     * @param register true zum Registrieren, false zum De-Registrieren.
     */
    private RestConsumer(boolean register)
    {
      this.register = register;
    }
    /**
     * @see de.willuhn.jameica.messaging.MessageConsumer#autoRegister()
     */
    public boolean autoRegister()
    {
      return false;
    }

    /**
     * @see de.willuhn.jameica.messaging.MessageConsumer#getExpectedMessageTypes()
     */
    public Class[] getExpectedMessageTypes()
    {
      return new Class[]{QueryMessage.class, TextMessage.class};
    }

    /**
     * @see de.willuhn.jameica.messaging.MessageConsumer#handleMessage(de.willuhn.jameica.messaging.Message)
     */
    public void handleMessage(Message message) throws Exception
    {
      String pattern = null;
      String command = null;
      
      if (message instanceof QueryMessage)
      {
        QueryMessage m = (QueryMessage) message;
        pattern = m.getName();
        Object data = m.getData();
        if (data != null)
          command = data.toString();
      }
      else if (message instanceof TextMessage)
      {
        TextMessage m = (TextMessage) message;
        pattern = m.getTitle();
        command = m.getText();
      }

      if (pattern == null || pattern.length() == 0)
      {
        Logger.warn("no pattern given: " + pattern);
        return;
      }
      
      if (register)
      {
        if (command == null || command.length() == 0)
        {
          Logger.warn("no command given: " + command);
          return;
        }
        register(pattern,command);
      }
      else
      {
        unregister(pattern);
      }
    }
  }
}


/*********************************************************************
 * $Log: RestServiceImpl.java,v $
 * Revision 1.8  2008/10/07 23:45:16  willuhn
 * @N Registrieren/Deregistrieren von REST-Commands via Messaging
 *
 * Revision 1.7  2008/09/09 14:40:09  willuhn
 * @D Hinweise zum Encoding. Siehe auch http://www.willuhn.de/blog/index.php?/archives/415-Umlaute-in-URLs-sind-Mist.html
 *
 * Revision 1.6  2008/07/11 15:38:55  willuhn
 * @N Service-Deployment
 *
 * Revision 1.5  2008/06/16 22:31:53  willuhn
 * @N weitere REST-Kommandos
 *
 * Revision 1.4  2008/06/16 14:22:11  willuhn
 * @N Mapping der REST-URLs via Property-Datei
 *
 * Revision 1.3  2008/06/15 22:48:24  willuhn
 * @N Command-Chains
 *
 * Revision 1.2  2008/06/13 15:11:01  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2008/06/13 14:11:04  willuhn
 * @N Mini REST-API
 *
 **********************************************************************/