/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.webadmin/src/de/willuhn/jameica/webadmin/rest/Service.java,v $
 * $Revision: 1.4 $
 * $Date: 2008/10/08 16:01:38 $
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
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import de.willuhn.jameica.plugin.AbstractPlugin;
import de.willuhn.jameica.plugin.ServiceDescriptor;
import de.willuhn.jameica.system.Application;
import de.willuhn.jameica.webadmin.rest.annotation.Writer;
import de.willuhn.logging.Logger;

/**
 * REST-Kommandos zum Starten und Stoppen von Services.
 */
public class Service
{
  @Writer
  private PrintWriter writer = null;
  
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
      writer.print(new JSONObject().put("started","true").toString());
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
      writer.print(new JSONObject().put("started","false").toString());
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
      writer.print(new JSONObject().put("started",s.isStarted() ? "true" : "false").toString());
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
   * Listet die Services eines Plugins auf.
   * @param plugin Name des Plugins.
   * @throws IOException
   */
  public void list(String plugin) throws IOException
  {
    if (plugin == null || plugin.length() == 0)
      throw new IOException("no plugin name given");
    
    AbstractPlugin p = null;
    List plugins = Application.getPluginLoader().getInstalledPlugins();
    for (int i=0;i<plugins.size();++i)
    {
      AbstractPlugin ap = (AbstractPlugin) plugins.get(i);
      String name = ap.getManifest().getName();
      if (name == null || name.length() == 0)
        continue;
      if (plugin.equals(name))
      {
        p = ap;
        break;
      }
    }
    
    if (p == null)
      throw new IOException("plugin " + plugin + " not found");

    ArrayList json = new ArrayList();
    ServiceDescriptor[] services = p.getManifest().getServices();
    for (int i=0;i<services.length;++i)
    {
      try
      {
        Map data = new HashMap();
        de.willuhn.datasource.Service s = Application.getServiceFactory().lookup(p.getClass(),services[i].getName());
        data.put("name",      services[i].getName());
        data.put("class",     services[i].getClassname());
        data.put("depends",   services[i].depends());
        data.put("autostart", Boolean.toString(services[i].autostart()));
        data.put("shared",    Boolean.toString(services[i].share()));
        data.put("started",   Boolean.toString(s.isStarted()));
        json.add(data);
      }
      catch (Exception e)
      {
        Logger.error("unable to load service " + services[i].getName(),e);
      }
    }
    writer.print(new JSONArray(json).toString());
  }
}


/*********************************************************************
 * $Log: Service.java,v $
 * Revision 1.4  2008/10/08 16:01:38  willuhn
 * @N REST-Services via Injection (mittels Annotation) mit Context-Daten befuellen
 *
 * Revision 1.3  2008/06/16 22:31:53  willuhn
 * @N weitere REST-Kommandos
 *
 * Revision 1.2  2008/06/16 14:22:11  willuhn
 * @N Mapping der REST-URLs via Property-Datei
 *
 **********************************************************************/