/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.webadmin/src/de/willuhn/jameica/webadmin/rest/Service.java,v $
 * $Revision: 1.9 $
 * $Date: 2009/11/19 22:53:35 $
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import de.willuhn.jameica.plugin.AbstractPlugin;
import de.willuhn.jameica.plugin.ServiceDescriptor;
import de.willuhn.jameica.system.Application;
import de.willuhn.jameica.webadmin.annotation.Path;
import de.willuhn.jameica.webadmin.annotation.Response;
import de.willuhn.logging.Logger;

/**
 * REST-Kommandos zum Starten und Stoppen von Services.
 */
public class Service
{
  @Response
  private HttpServletResponse response = null;
  
  /**
   * Startet den Service.
   * @param plugin Name des Plugins.
   * @param service Name des Services.
   * @throws IOException
   */
  @Path("/plugins/(.*?)/services/(.*?)/start$")
  public void start(String plugin, String service) throws IOException
  {
    try
    {
      de.willuhn.datasource.Service s = find(plugin,service);
      s.start();
      response.getWriter().print(new JSONObject().put("started",Boolean.toString(s.isStarted())).toString());
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
  @Path("/plugins/(.*?)/services/(.*?)/stop$")
  public void stop(String plugin, String service) throws IOException
  {
    try
    {
      de.willuhn.datasource.Service s = find(plugin,service);
      s.stop(false);
      response.getWriter().print(new JSONObject().put("started","false").toString());
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
  @Path("/plugins/(.*?)/services/(.*?)/status$")
  public void status(String plugin, String service) throws IOException
  {
    try
    {
      de.willuhn.datasource.Service s = find(plugin,service);
      response.getWriter().print(new JSONObject().put("started",s.isStarted() ? "true" : "false").toString());
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
   * Listet die Services eines Plugins auf.
   * @param plugin Name des Plugins.
   * @throws IOException
   */
  @Path("/plugins/(.*?)/services/list$")
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
        data.put("autostart", services[i].autostart());
        data.put("shared",    services[i].share());
        data.put("started",   s.isStarted());
        json.add(data);
      }
      catch (Exception e)
      {
        Logger.error("unable to load service " + services[i].getName(),e);
      }
    }
    response.getWriter().print(new JSONArray(json).toString());
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
}


/*********************************************************************
 * $Log: Service.java,v $
 * Revision 1.9  2009/11/19 22:53:35  willuhn
 * @B tatsaechlichen Start-Status zurueckliefern
 *
 * Revision 1.8  2009/08/05 09:03:40  willuhn
 * @C Annotations in eigenes Package verschoben (sind nicht mehr REST-spezifisch)
 *
 * Revision 1.7  2009/01/06 01:44:14  willuhn
 * @N Code zum Hinzufuegen von Servern erweitert
 *
 * Revision 1.6  2008/10/21 22:33:47  willuhn
 * @N Markieren der zu registrierenden REST-Kommandos via Annotation
 *
 * Revision 1.5  2008/10/08 21:38:23  willuhn
 * @C Nur noch zwei Annotations "Request" und "Response"
 *
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