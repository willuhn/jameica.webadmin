/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.webadmin/src/de/willuhn/jameica/webadmin/deploy/WebAdminDeployer.java,v $
 * $Revision: 1.2 $
 * $Date: 2007/05/15 15:33:17 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn software & services
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.webadmin.deploy;

import java.io.File;

import de.willuhn.jameica.plugin.AbstractPlugin;
import de.willuhn.jameica.system.Application;
import de.willuhn.jameica.webadmin.Plugin;

/**
 * Deployed die Admin-Console.
 */
public class WebAdminDeployer extends AbstractWebAppDeployer
{
  /**
   * @see de.willuhn.jameica.webadmin.deploy.AbstractWebAppDeployer#getContext()
   */
  protected String getContext()
  {
    return "/webadmin";
  }

  /**
   * @see de.willuhn.jameica.webadmin.deploy.AbstractWebAppDeployer#getPath()
   */
  protected String getPath()
  {
    AbstractPlugin plugin = Application.getPluginLoader().getPlugin(Plugin.class);
    return plugin.getResources().getPath() + File.separator + "webapps" + File.separator + "webadmin";
  }

}


/*********************************************************************
 * $Log: WebAdminDeployer.java,v $
 * Revision 1.2  2007/05/15 15:33:17  willuhn
 * @N helloworld.war
 * @C Webadmin komplett auf JSP umgestellt
 * @C build-Script angepasst
 *
 * Revision 1.1  2007/05/15 13:42:36  willuhn
 * @N Deployment von Webapps, WARs fertig und konfigurierbar
 *
 **********************************************************************/