/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.webadmin/src/de/willuhn/jameica/webadmin/rest/Server.java,v $
 * $Revision: 1.5 $
 * $Date: 2010/05/12 10:59:20 $
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
import de.willuhn.jameica.webadmin.annotation.Doc;
import de.willuhn.jameica.webadmin.annotation.Path;
import de.willuhn.jameica.webadmin.annotation.Request;
import de.willuhn.jameica.webadmin.annotation.Response;
import de.willuhn.logging.Logger;
import de.willuhn.util.I18N;

/**
 * Command zum Hinzufuegen und Entfernen von Servern.
 */
@Doc("System: Ermöglicht das Hinzufügen weiterer Jameica-Server zur Management-Console")
public class Server implements AutoRestBean
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
  @Doc(value="Fügt einen weiteren Jameica-Server zur Management-Console hinzu." +
  		       "Die Funktion erwartet folgende 4 Parameter via GET oder POST.<br/>" +
  		       "<ul>" +
  		       "  <li><b>host</b>: Hostname des Jameica-Servers</li>" +
             "  <li><b>ssl</b>: &quot;true&quot; wenn für den Zugriff HTTPS verwendet werden soll</li>" +
             "  <li><b>port</b>: TCP-Port der Management-Console des entfernten Jameica-Servers (meist 8080)</li>" +
             "  <li><b>password</b>: Das Master-Passwort des entfernten Jameica-Servers</li>" +
  		       "</ul>",
  		 example="server/add")
  @Path("/server/add")
  public void add() throws IOException
  {
    try
    {
      String host = request.getParameter("host");
      if (host != null && host.length() > 0)
      {
        boolean ssl = (request.getParameter("ssl") != null && "true".equals(request.getParameter("ssl")));
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
 * Revision 1.5  2010/05/12 10:59:20  willuhn
 * @N Automatische Dokumentations-Seite fuer die REST-Beans basierend auf der Annotation "Doc"
 *
 * Revision 1.4  2010/05/11 14:59:48  willuhn
 * @N Automatisches Deployment von REST-Beans
 *
 * Revision 1.3  2009/08/05 09:03:40  willuhn
 * @C Annotations in eigenes Package verschoben (sind nicht mehr REST-spezifisch)
 *
 * Revision 1.2  2009/01/07 00:30:20  willuhn
 * @N Hinzufuegen weiterer Jameica-Server
 * @N Auto-Host-Check
 *
 * Revision 1.1  2009/01/06 01:44:14  willuhn
 * @N Code zum Hinzufuegen von Servern erweitert
 *
 **********************************************************************/