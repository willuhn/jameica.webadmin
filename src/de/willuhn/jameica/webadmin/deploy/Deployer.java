/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.webadmin/src/de/willuhn/jameica/webadmin/deploy/Deployer.java,v $
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

/**
 * Basis-Interface fuer Applikations-Deployer.
 */
public interface Deployer
{
  /**
   * Deployed ein oder mehrere Webanwendungen in den Server.
   * @param server der Server.
   * @param container der Container, in den deployed werden soll.
   */
  public void deploy(Server server, HandlerContainer container);
}


/*********************************************************************
 * $Log: Deployer.java,v $
 * Revision 1.1  2007/05/15 13:42:36  willuhn
 * @N Deployment von Webapps, WARs fertig und konfigurierbar
 *
 **********************************************************************/