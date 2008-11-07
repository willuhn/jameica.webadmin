/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.webadmin/src/de/willuhn/jameica/webadmin/rest/Application.java,v $
 * $Revision: 1.1 $
 * $Date: 2008/11/07 00:14:37 $
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
   * Liefert Datum und Uhrzeit des Systemstarts.
   * @throws IOException
   */
  @Path("/system/started$")
  public void started() throws IOException
  {
    try
    {
      response.getWriter().print(new JSONObject().put("started",DATEFORMAT.format(de.willuhn.jameica.system.Application.getStartDate())).toString());
    }
    catch (JSONException e)
    {
      Logger.error("unable to encode via json",e);
      throw new IOException("error while encoding into json");
    }
  }

  /**
   * Liefert die System-Konfiguration.
   * @throws IOException
   */
  @Path("/system/config$")
  public void config() throws IOException
  {
    try
    {
      JSONObject json = new JSONObject();
      Config config = de.willuhn.jameica.system.Application.getConfig();
      
      json.put("locale",config.getLocale().toString());
      
      Map rmi = new HashMap();
      rmi.put("port",      String.valueOf(config.getRmiPort()));
      rmi.put("ssl",       String.valueOf(config.getRmiSSL()));
      rmi.put("clientauth",String.valueOf(config.getRmiUseClientAuth()));
      json.put("rmi",rmi);

      Map backup = new HashMap();
      backup.put("dir",    config.getBackupDir());
      backup.put("enabled",String.valueOf(config.getUseBackup()));
      backup.put("count",  String.valueOf(config.getBackupCount()));
      json.put("backup",backup);
      
      Map dir = new HashMap();
      dir.put("config",config.getConfigDir());
      dir.put("work",config.getWorkDir());
      Map plugins = new HashMap();
      plugins.put("system",config.getSystemPluginDir().getAbsolutePath());
      plugins.put("user",config.getUserPluginDir().getAbsolutePath());
      plugins.put("config",new JSONArray(config.getPluginDirs()));
      dir.put("plugins",plugins);
      json.put("dir",dir);
      
      Map log = new HashMap();
      log.put("file",config.getLogFile());
      log.put("level",config.getLogLevel());
      json.put("log",log);
      
      Map proxy = new HashMap();
      proxy.put("host",config.getProxyHost() == null ? "" : config.getProxyHost());
      proxy.put("port",config.getProxyPort() == -1 ? "" : String.valueOf(config.getProxyPort()));
      json.put("proxy",proxy);
      
      Map service = new HashMap();
      service.put("multicastlookup",String.valueOf(config.getMulticastLookup()));
      service.put("shareservices",String.valueOf(config.getShareServices()));
      json.put("service",service);
      
      response.getWriter().print(json.toString());
    }
    catch (ApplicationException ae)
    {
      throw new IOException(ae.getMessage());
    }
    catch (JSONException e)
    {
      Logger.error("unable to encode via json",e);
      throw new IOException("error while encoding into json");
    }
  }
}


/*********************************************************************
 * $Log: Application.java,v $
 * Revision 1.1  2008/11/07 00:14:37  willuhn
 * @N REST-Bean fuer Anzeige von System-Infos (Start-Zeit, Config)
 *
 **********************************************************************/