/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.webadmin/src/de/willuhn/jameica/webadmin/rest/Application.java,v $
 * $Revision: 1.5 $
 * $Date: 2009/01/06 01:44:14 $
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.willuhn.jameica.system.Config;
import de.willuhn.jameica.webadmin.rest.annotation.Path;
import de.willuhn.jameica.webadmin.rest.annotation.Response;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;

/**
 * REST-Bean zum Abfragen von System-Infos.
 */
public class Application
{
  private final static DateFormat DATEFORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

  @Response
  private HttpServletResponse response = null;

  /**
   * Schreibt die Uptime und Startzeit des Servers in den Response-Writer.
   * @throws IOException
   */
  @Path("/system/uptime$")
  public void uptime() throws IOException
  {
    response.getWriter().print(new JSONObject(getUptime()).toString());
  }

  /**
   * Liefert die Uptime und Startzeit des Servers.
   */
  public Map getUptime()
  {
    Date started = de.willuhn.jameica.system.Application.getStartDate();

    ////////////////////////////////////////////////////////////////////////////
    // Uptime ausrechnen
    long minutes = (System.currentTimeMillis() - started.getTime()) / 1000L / 60L;
    long hours   = minutes / 60;

    minutes %= 60; // Restminuten abzueglich Stunden

    // ggf. ne "0" vorn dran schreiben
    String mins = (minutes < 10 ? ("0" + minutes) : "" + minutes);

    String uptime = null;
    if (hours < 24) // weniger als 1 Tag?
    {
      uptime = hours + ":" + mins + " h";
    }
    else
    {
      long days = hours / 24;
      uptime = days + " Tag(e), " + (hours % 24) + ":" + mins + " h";
    }
    ////////////////////////////////////////////////////////////////////////////
    
    Map o = new HashMap();
    o.put("started",DATEFORMAT.format(started));
    o.put("uptime",uptime);
    return o;
  }
  
  /**
   * Schreibt die System-Konfiguration in den Response-Writer.
   * @throws IOException
   */
  @Path("/system/config$")
  public void config() throws IOException
  {
    try
    {
      response.getWriter().print(new JSONObject(getConfig()).toString());
    }
    catch (ApplicationException ae)
    {
      throw new IOException(ae.getMessage());
    }
  }

  /**
   * Liefert die System-Konfiguration.
   */
  public Map getConfig() throws ApplicationException
  {
    Map all = new HashMap();
    
    Config config = de.willuhn.jameica.system.Application.getConfig();
    
    all.put("locale",config.getLocale().toString());
    
    Map rmi = new HashMap();
    rmi.put("port",      config.getRmiPort());
    rmi.put("ssl",       config.getRmiSSL());
    rmi.put("clientauth",config.getRmiUseClientAuth());
    all.put("rmi",rmi);

    Map backup = new HashMap();
    backup.put("dir",    config.getBackupDir());
    backup.put("enabled",config.getUseBackup());
    backup.put("count",  config.getBackupCount());
    all.put("backup",backup);
    
    Map dir = new HashMap();
    dir.put("config",config.getConfigDir());
    dir.put("work",config.getWorkDir());
    Map plugins = new HashMap();
    plugins.put("system",config.getSystemPluginDir().getAbsolutePath());
    plugins.put("user",  config.getUserPluginDir().getAbsolutePath());
    plugins.put("config",config.getPluginDirs());
    dir.put("plugins",plugins);
    all.put("dir",dir);
    
    Map log = new HashMap();
    log.put("file",config.getLogFile());
    log.put("level",config.getLogLevel());
    all.put("log",log);
    
    Map proxy = new HashMap();
    proxy.put("host",config.getProxyHost());
    proxy.put("port",config.getProxyPort() == -1 ? "" : String.valueOf(config.getProxyPort()));
    all.put("proxy",proxy);
    
    Map service = new HashMap();
    service.put("multicastlookup",config.getMulticastLookup());
    service.put("shareservices",config.getShareServices());
    all.put("service",service);
    
    return all;
  }

  /**
   * Schreibt die Liste der beim Systemstart aufgelaufenen Nachrichten in den Response-Writer.
   * @throws IOException
   */
  @Path("/system/welcome$")
  public void welcome() throws IOException
  {
    try
    {
      response.getWriter().print(new JSONArray(getWelcome()).toString());
    }
    catch (JSONException e)
    {
      Logger.error("unable to encode via json",e);
      throw new IOException("error while encoding into json");
    }
  }

  /**
   * Liefert eine Liste der beim Systemstart aufgelaufenen Nachrichten.
   * @throws IOException
   */
  public String[] getWelcome()
  {
    return de.willuhn.jameica.system.Application.getWelcomeMessages();
  }

}


/*********************************************************************
 * $Log: Application.java,v $
 * Revision 1.5  2009/01/06 01:44:14  willuhn
 * @N Code zum Hinzufuegen von Servern erweitert
 *
 * Revision 1.4  2008/11/11 23:59:22  willuhn
 * @N Dualer Aufruf (via JSON und Map/List)
 *
 * Revision 1.3  2008/11/11 01:06:49  willuhn
 * @R testcode entfernt
 *
 * Revision 1.2  2008/11/11 01:06:22  willuhn
 * @N Mehr REST-Kommandos
 *
 * Revision 1.1  2008/11/07 00:14:37  willuhn
 * @N REST-Bean fuer Anzeige von System-Infos (Start-Zeit, Config)
 *
 **********************************************************************/