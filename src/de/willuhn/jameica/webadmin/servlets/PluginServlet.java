/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.webadmin/src/de/willuhn/jameica/webadmin/servlets/Attic/PluginServlet.java,v $
 * $Revision: 1.1 $
 * $Date: 2007/04/16 13:44:45 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn software & services
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.webadmin.servlets;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.willuhn.datasource.Service;
import de.willuhn.jameica.plugin.Manifest;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;


/**
 * Servlet zum Anzeigen der Details eines Plugins.
 */
public class PluginServlet extends RootServlet
{
  /**
   * @see de.willuhn.jameica.webadmin.servlets.RootServlet#prepareContext(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
   */
  protected void prepareContext(HttpServletRequest request, HttpServletResponse response) throws ApplicationException
  {
    // Name des gewuenschten Plugins.
    String name = request.getParameter("plugin");

    // Mal schauen, ob wir das Plugin kennen
    Manifest manifest = null;
    if (name != null && name.length() > 0)
    {
      List manifests = Application.getPluginLoader().getInstalledManifests();
      for (int i=0;i<manifests.size();++i)
      {
        Manifest mf = (Manifest) manifests.get(i);
        if (name.equals(mf.getName()))
        {
          // Plugin gefunden
          manifest = mf;
          break;
        }
      }
    }

    // Kein Plugin angegeben oder nicht gefunden. Also Startseite laden.
    if (manifest == null)
      throw new ApplicationException(i18n.tr("Das Plugin \"{0}\" wurde nicht gefunden",name));

    // Context befuellen.
    context.put("plugin",manifest);
    context.put("contentfile","plugin.vm");

    // Ggf. Aktion ausfuehren.
    String service = request.getParameter("service");
    String action  = request.getParameter("action");
    
    if (service == null || service.length() == 0)
      return;
    
    if (action == null || action.length() == 0)
      return;
    
    // Service suchen
    try
    {
      Class plugin = Application.getClassLoader().load(manifest.getPluginClass());
      Service s = Application.getServiceFactory().lookup(plugin,service);
      if (action.equals("start"))
        s.start();
      else if (action.equals("stop"))
        s.stop(true);
      else
      {
        Logger.warn("invalid action " + action);
        Application.addWelcomeMessage(i18n.tr("Ungültige Aktion \"{0}\"",action));
      }
      
      // Wir senden immer ein Redirect, um action und service aus der URL zu kriegen
      response.sendRedirect("plugin?plugin=" + name);
    }
    catch (Exception e)
    {
      Logger.error("unable to load service",e);
      throw new ApplicationException(i18n.tr("Fehler beim Laden des Services " + service));
    }
  }
}


/**********************************************************************
 * $Log: PluginServlet.java,v $
 * Revision 1.1  2007/04/16 13:44:45  willuhn
 * @N display logs
 * @N display installed plugins
 * @N display plugin details
 * @N ability to start/stop services
 *
 **********************************************************************/
