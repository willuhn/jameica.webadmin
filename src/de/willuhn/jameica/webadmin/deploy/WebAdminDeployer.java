/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.webadmin/src/de/willuhn/jameica/webadmin/deploy/WebAdminDeployer.java,v $
 * $Revision: 1.1 $
 * $Date: 2007/05/15 13:42:36 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn software & services
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.webadmin.deploy;

import org.mortbay.jetty.HandlerContainer;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;

import de.willuhn.jameica.webadmin.servlets.PluginServlet;
import de.willuhn.jameica.webadmin.servlets.ResourceServlet;
import de.willuhn.jameica.webadmin.servlets.RootServlet;
import de.willuhn.logging.Logger;

/**
 * Deployed die Admin-Console.
 */
public class WebAdminDeployer implements Deployer
{

  /**
   * @see de.willuhn.jameica.webadmin.deploy.Deployer#deploy(org.mortbay.jetty.Server, org.mortbay.jetty.HandlerContainer)
   */
  public void deploy(Server server, HandlerContainer container)
  {
    Logger.info("deploying /webadmin");
    Context webadmin = new Context(server,"/webadmin",Context.ALL);
    webadmin.addServlet(ResourceServlet.class, "/res/*");
    webadmin.addServlet(RootServlet.class,     "/");
    webadmin.addServlet(PluginServlet.class,   "/plugin");
    container.addHandler(webadmin);
  }

}


/*********************************************************************
 * $Log: WebAdminDeployer.java,v $
 * Revision 1.1  2007/05/15 13:42:36  willuhn
 * @N Deployment von Webapps, WARs fertig und konfigurierbar
 *
 **********************************************************************/