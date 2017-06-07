/**********************************************************************
 *
 * Copyright (c) by Olaf Willuhn
 * All rights reserved
 * GPLv2
 *
 **********************************************************************/

package de.willuhn.jameica.webadmin.deploy;

import java.io.File;

import org.eclipse.jetty.security.LoginService;

import de.willuhn.jameica.system.Application;
import de.willuhn.jameica.webadmin.Plugin;
import de.willuhn.jameica.webadmin.Settings;
import de.willuhn.jameica.webadmin.server.JameicaLoginService;

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
    return Application.getPluginLoader().getPlugin(Plugin.class).getManifest().getPluginDir() + File.separator + "webapps" + File.separator + "webadmin";
  }

  /**
   * @see de.willuhn.jameica.webadmin.deploy.AbstractWebAppDeployer#getSecurityRoles()
   */
  protected String[] getSecurityRoles()
  {
    return new String[]{"admin"};
  }
  
  /**
   * @see de.willuhn.jameica.webadmin.deploy.AbstractWebAppDeployer#getLoginService()
   */
  @Override
  protected LoginService getLoginService()
  {
    return Settings.getUseAuth() ? new JameicaLoginService() : null;
  }
}
