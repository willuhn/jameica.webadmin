/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.webadmin/src/de/willuhn/jameica/webadmin/rest/Service.java,v $
 * $Revision: 1.2 $
 * $Date: 2008/06/16 14:22:11 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn software & services
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.webadmin.rest;

import java.io.IOException;
import java.util.List;

import org.json.JSONObject;

import de.willuhn.jameica.plugin.AbstractPlugin;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;

/**
 * REST-Kommandos zum Starten und Stoppen von Services.
 */
public class Service
{
  private Context context = null;

  /**
   * Startet den Service.
   * @param plugin Name des Plugins.
   * @param service Name des Services.
   * @throws IOException
   */
  public void start(String plugin, String service) throws IOException
  {
    try
    {
      de.willuhn.datasource.Service s = find(plugin,service);
      s.start();
      context.getResponse().getWriter().print(new JSONObject().put("started","true").toString());
    }
    catch (IOException e)
    {
      throw e;
    }
    catch (Exception e2)
    {
      Logger.error("unable to start service",e2);
      throw new IOException("unable to start service");
    }
  }
  
  /**
   * Stoppt den Service.
   * @param plugin Name des Plugins.
   * @param service Name des Services.
   * @throws IOException
   */
  public void stop(String plugin, String service) throws IOException
  {
    try
    {
      de.willuhn.datasource.Service s = find(plugin,service);
      s.stop(false);
      context.getResponse().getWriter().print(new JSONObject().put("started","false").toString());
    }
    catch (IOException e)
    {
      throw e;
    }
    catch (Exception e2)
    {
      Logger.error("unable to stop service",e2);
      throw new IOException("unable to stop service");
    }
  }

  /**
   * Liefert den Service-Status.
   * @param plugin Name des Plugins.
   * @param service Name des Services.
   * @throws IOException
   */
  public void status(String plugin, String service) throws IOException
  {
    try
    {
      de.willuhn.datasource.Service s = find(plugin,service);
      context.getResponse().getWriter().print(new JSONObject().put("started",s.isStarted() ? "true" : "false").toString());
    }
    catch (IOException e)
    {
      throw e;
    }
    catch (Exception e2)
    {
      Logger.error("unable to get service status",e2);
      throw new IOException("unable to get service status");
    }
  }

  /**
   * Sucht den angegebenen Service im Plugin.
   * @param plugin das Plugin.
   * @param service der Service-Name.
   * @return Instanz des Services.
   * @throws Exception
   */
  private de.willuhn.datasource.Service find(String plugin, String service) throws Exception
  {
    List plugins = Application.getPluginLoader().getInstalledPlugins();
    for (int i=0;i<plugins.size();++i)
    {
      AbstractPlugin p = (AbstractPlugin) plugins.get(i);
      String name = p.getManifest().getName();
      if (name == null || name.length() == 0)
        continue;
      
      if (name.equals(plugin))
        return Application.getServiceFactory().lookup(p.getClass(),service);
    }
    
    throw new IOException("service not found");
  }

  /**
   * Speichert den Context des Aufrufs.
   * @param context Context.
   */
  public void setContext(Context context)
  {
    this.context = context;
  }

}


/*********************************************************************
 * $Log: Service.java,v $
 * Revision 1.2  2008/06/16 14:22:11  willuhn
 * @N Mapping der REST-URLs via Property-Datei
 *
 **********************************************************************/