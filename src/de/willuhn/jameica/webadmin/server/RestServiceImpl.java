/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.webadmin/src/de/willuhn/jameica/webadmin/server/RestServiceImpl.java,v $
 * $Revision: 1.12 $
 * $Date: 2008/10/21 22:33:47 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn software & services
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.webadmin.server;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.willuhn.jameica.messaging.Message;
import de.willuhn.jameica.messaging.MessageConsumer;
import de.willuhn.jameica.messaging.QueryMessage;
import de.willuhn.jameica.system.Application;
import de.willuhn.jameica.webadmin.rest.Echo;
import de.willuhn.jameica.webadmin.rest.Log;
import de.willuhn.jameica.webadmin.rest.Plugin;
import de.willuhn.jameica.webadmin.rest.Service;
import de.willuhn.jameica.webadmin.rest.annotation.Path;
import de.willuhn.jameica.webadmin.rest.annotation.Request;
import de.willuhn.jameica.webadmin.rest.annotation.Response;
import de.willuhn.jameica.webadmin.rmi.RestService;
import de.willuhn.logging.Logger;

/**
 * Implementierung des REST-Services.
 */
public class RestServiceImpl implements RestService
{
  private Map<String,Method> commands = null;
  private MessageConsumer register    = new RestConsumer(true);
  private MessageConsumer unregister  = new RestConsumer(false);
  
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
    
    Iterator<String> patterns = this.commands.keySet().iterator();

    try
    {
      while (patterns.hasNext())
      {
        String path     = patterns.next();
        Method method   = this.commands.get(path);

        Pattern pattern = Pattern.compile(path);
        Matcher match   = pattern.matcher(command);
        
        if (match.matches())
        {
          Object[] params = new Object[match.groupCount()];
          for (int k=0;k<params.length;++k)
            params[k] = match.group(k+1); // wir fangen bei "1" an, weil an Pos. 0 der Pattern selbst steht

          Object bean = method.getDeclaringClass().newInstance();
          applyAnnotations(bean, request, response);

          Logger.debug("applying command " + path + " to " + method);
          method.invoke(bean,params);
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
   * @see de.willuhn.jameica.webadmin.rmi.RestService#register(java.lang.Object)
   */
  public void register(Object bean) throws RemoteException
  {
    if (!isStarted())
      throw new RemoteException("REST service not started");

    Hashtable<String,Method> found = eval(bean);
    if (found.size() > 0)
    {
      Logger.info("register REST commands for " + bean.getClass());
      this.commands.putAll(found);
    }
    else
    {
      Logger.warn(bean.getClass() + " contains no valid annotated methods, skip bean");
    }
  }

  /**
   * @see de.willuhn.jameica.webadmin.rmi.RestService#unregister(java.lang.Object)
   */
  public void unregister(Object bean) throws RemoteException
  {
    if (!isStarted())
      throw new RemoteException("REST service not started");

    Hashtable<String,Method> found = eval(bean);
    if (found.size() > 0)
    {
      Logger.info("un-register REST commands for " + bean.getClass());
      Iterator<String> s = found.keySet().iterator();
      while (s.hasNext())
        this.commands.remove(s.next());
    }
    else
    {
      Logger.warn(bean.getClass() + " contains no valid annotated methods, skip bean");
    }
  }
  
  /**
   * Analysiert die Bean und deren Annotations und liefert sie zurueck.
   * @param bean die zu evaluierende Bean.
   * @return Hashtable mit den URLs und zugehoerigen Methoden.
   * @throws RemoteException
   */
  private Hashtable<String,Method> eval(Object bean) throws RemoteException
  {
    if (bean == null)
      throw new RemoteException("no REST bean given");

    Hashtable<String,Method> found = new Hashtable<String,Method>();
    Method[] methods = bean.getClass().getMethods();
    for (Method m:methods)
    {
      Path path = m.getAnnotation(Path.class);
      if (path == null)
        continue;

      String s = path.value();
      if (s == null || s.length() == 0)
      {
        Logger.warn("no path specified for method " + m + ", skipping");
        continue;
      }

      m.setAccessible(true);
      Logger.debug("REST command " + m + ", URL pattern: " + s);
      found.put(s,m);
    }
    return found;
  }

  /**
   * Injiziert die Annotations.
   * @param bean die Bean.
   * @throws IOException
   */
  private void applyAnnotations(Object bean, HttpServletRequest request, HttpServletResponse response) throws IOException
  {
    Field[] fields = bean.getClass().getDeclaredFields();
    for (Field f:fields)
    {
      Object value = null;
      if (f.getAnnotation(Request.class) != null)       value = request;
      else if (f.getAnnotation(Response.class) != null) value = response;
      
      if (value == null)
        return;
      
      try
      {
        f.setAccessible(true);
        f.set(bean,value);
      }
      catch (Exception e)
      {
        Logger.error("unable to inject context",e);
        throw new IOException("unable to inject context");
      }
    }
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
    return this.commands != null;
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
    this.commands = new Hashtable<String,Method>();
    
    // eigene REST-Kommandos deployen
    register(new Echo());
    register(new Log());
    register(new Plugin());
    register(new Service());

    Application.getMessagingFactory().getMessagingQueue("jameica.webadmin.rest.register").registerMessageConsumer(this.register);
    Application.getMessagingFactory().getMessagingQueue("jameica.webadmin.rest.unregister").registerMessageConsumer(this.unregister);

    // Fremdsysteme benachrichtigen, dass wir online sind.
    Application.getMessagingFactory().getMessagingQueue("jameica.webadmin.rest.start").sendMessage(new QueryMessage());
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
    
    try
    {
      Application.getMessagingFactory().getMessagingQueue("jameica.webadmin.rest.register").unRegisterMessageConsumer(this.register);
      Application.getMessagingFactory().getMessagingQueue("jameica.webadmin.rest.unregister").unRegisterMessageConsumer(this.unregister);
    }
    finally
    {
      Logger.info("REST service stopped");
      this.commands = null;
    }
  }


  
  /**
   * Hilfsklasse zum Registrieren von REST-Kommandos via Messaging
   */
  private class RestConsumer implements MessageConsumer
  {
    private boolean r = false;
    
    /**
     * @param register true zum Registrieren, false zum De-Registrieren.
     */
    private RestConsumer(boolean register)
    {
      this.r = register;
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
      return new Class[]{QueryMessage.class};
    }

    /**
     * @see de.willuhn.jameica.messaging.MessageConsumer#handleMessage(de.willuhn.jameica.messaging.Message)
     */
    public void handleMessage(Message message) throws Exception
    {
      QueryMessage m = (QueryMessage) message;
      Object bean = m.getData();
      if (bean == null)
        return;
      
      if (r)
        register(bean);
      else
        unregister(bean);
    }
  }
}


/*********************************************************************
 * $Log: RestServiceImpl.java,v $
 * Revision 1.12  2008/10/21 22:33:47  willuhn
 * @N Markieren der zu registrierenden REST-Kommandos via Annotation
 *
 * Revision 1.11  2008/10/08 21:38:23  willuhn
 * @C Nur noch zwei Annotations "Request" und "Response"
 *
 * Revision 1.10  2008/10/08 17:54:32  willuhn
 * @B message an der falschen Stelle geschickt
 *
 * Revision 1.9  2008/10/08 16:01:38  willuhn
 * @N REST-Services via Injection (mittels Annotation) mit Context-Daten befuellen
 *
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