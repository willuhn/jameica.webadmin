/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.webadmin/src/de/willuhn/jameica/webadmin/deploy/AbstractWebAppDeployer.java,v $
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
import org.mortbay.jetty.handler.ContextHandler;
import org.mortbay.jetty.webapp.WebAppContext;

import de.willuhn.logging.Logger;

/**
 * Abstrakte Basis-Implementierung eines Deployers, der Web-Anwendungen deployen kann.
 * Zum Etablieren einer Webanwendung muss lediglich von dieser Klasse abgeleitet werden.
 */
public abstract class AbstractWebAppDeployer implements Deployer
{

  /**
   * @see de.willuhn.jameica.webadmin.deploy.Deployer#deploy(org.mortbay.jetty.Server, org.mortbay.jetty.HandlerContainer)
   */
  public void deploy(Server server, HandlerContainer container)
  {
    String path    = getPath();
    String context = getContext();

    Logger.info("deploying " + context + " (" + path + ")");
    ContextHandler app = new WebAppContext(path,context);
    container.addHandler(app);
  }
  
  /**
   * Liefert den Pfad im Dateisystem zu der Web-Anwendung.
   * Also das Verzeichnis, in dem sich die index.jsp befindet.
   * @return Pfad im Dateisystem zur Webanwendung.
   */
  protected abstract String getPath();
  
  /**
   * Liefert den Namen des Contextes.
   * Soll die Webanwendung also unter "http://server/test" erreichbar
   * sein, muss die Funktion "/test" zurueckliefern.
   * @return der Name des Context.
   */
  protected abstract String getContext();

}


/*********************************************************************
 * $Log: AbstractWebAppDeployer.java,v $
 * Revision 1.1  2007/05/15 13:42:36  willuhn
 * @N Deployment von Webapps, WARs fertig und konfigurierbar
 *
 **********************************************************************/