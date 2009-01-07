/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.webadmin/src/de/willuhn/jameica/webadmin/rest/Server.java,v $
 * $Revision: 1.2 $
 * $Date: 2009/01/07 00:30:20 $
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.willuhn.jameica.messaging.StatusBarMessage;
import de.willuhn.jameica.webadmin.Settings;
import de.willuhn.jameica.webadmin.rest.annotation.Path;
import de.willuhn.jameica.webadmin.rest.annotation.Request;
import de.willuhn.jameica.webadmin.rest.annotation.Response;
import de.willuhn.logging.Logger;
import de.willuhn.util.I18N;

/**
 * Command zum Hinzufuegen und Entfernen von Servern.
 */
public class Server
{
  private static I18N i18n = de.willuhn.jameica.system.Application.getPluginLoader().getPlugin(de.willuhn.jameica.webadmin.Plugin.class).getResources().getI18N();

  @Request
  private HttpServletRequest request = null;
  
  @Response
  private HttpServletResponse response = null;

  /**
   * Fuegt einen Server hinzu.
   * @throws IOException
   */
  @Path("/server/add")
  public void add() throws IOException
  {
    try
    {
      String host = request.getParameter("host");
      if (host != null && host.length() > 0)
      {
        boolean ssl = (request.getParameter("ssl") != null);
        int port = 8080;
        try
        {
          port = Integer.parseInt(request.getParameter("port"));
        } catch (Exception e) {/*ignore*/}
        if (port <= 0)
          port = 8080;
        
        String url = "http" + (ssl ? "s" : "") + "://" + host + ":" + port + "/webadmin";
        Settings.addServer(host,url);
        Settings.setJameicaInstancePassword(url,request.getParameter("password"));
        
        de.willuhn.jameica.system.Application.getMessagingFactory().sendMessage(new StatusBarMessage(i18n.tr("Server {0} hinzugefügt",host),StatusBarMessage.TYPE_SUCCESS));
        response.sendRedirect("/webadmin/"); // Damit beim Reload nicht erneut abgesendet wird
      }
    }
    catch (Exception e)
    {
      Logger.error("unable to add server",e);
      throw new IOException("unable to add server");
    }
  }
}


/*********************************************************************
 * $Log: Server.java,v $
 * Revision 1.2  2009/01/07 00:30:20  willuhn
 * @N Hinzufuegen weiterer Jameica-Server
 * @N Auto-Host-Check
 *
 * Revision 1.1  2009/01/06 01:44:14  willuhn
 * @N Code zum Hinzufuegen von Servern erweitert
 *
 **********************************************************************/