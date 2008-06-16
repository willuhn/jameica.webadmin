/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.webadmin/src/de/willuhn/jameica/webadmin/server/RestServiceImpl.java,v $
 * $Revision: 1.5 $
 * $Date: 2008/06/16 22:31:53 $
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
  private Settings settings = null;
  
  /**
   * @see de.willuhn.jameica.webadmin.rmi.RestService#handleRequest(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
   */
  public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws IOException
  {
    if (!this.isStarted())
      throw new IOException("REST service not started");

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
    this.settings.setAttribute(urlPattern,command);
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
}


/*********************************************************************
 * $Log: RestServiceImpl.java,v $
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