/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.webadmin/src/de/willuhn/jameica/webadmin/deploy/AbstractWebAppDeployer.java,v $
 * $Revision: 1.4 $
 * $Date: 2007/12/04 12:13:48 $
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
import org.mortbay.jetty.security.Constraint;
import org.mortbay.jetty.security.ConstraintMapping;
import org.mortbay.jetty.security.SecurityHandler;
import org.mortbay.jetty.security.UserRealm;
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
  public final void deploy(Server server, HandlerContainer container)
  {
    String path    = getPath();
    String context = getContext();

    Logger.info("deploying " + context + " (" + path + ")");
    WebAppContext app = new WebAppContext(path,context);

    UserRealm realm = getUserRealm();
    if (realm != null)
    {
      Logger.info("  activating authentication");
      Constraint constraint = new Constraint();
      constraint.setName(Constraint.__BASIC_AUTH);
      String[] roles = getSecurityRoles();
      if (roles != null)
      {
        Logger.info("  roles:");
        for (int i=0;i<roles.length;++i)
        {
          Logger.info("    " + roles[i]);
        }
        constraint.setRoles(roles);
      }
      constraint.setAuthenticate(true);

      ConstraintMapping cm = new ConstraintMapping();
      cm.setConstraint(constraint);
      cm.setPathSpec("/*");

      SecurityHandler sh = new SecurityHandler();
      sh.setUserRealm(realm);
      sh.setConstraintMappings(new ConstraintMapping[]{cm});
      sh.setHandler(app);
      container.addHandler(sh);
    }
    else
    {
      container.addHandler(app);
    }
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
  
  /**
   * Liefert die Benutzer-Rollen, die in der Web-Applikation zur Verfuegung stehen.
   * Dummy-Implementierung, die keine Rollen zurueckliefert.
   * Kann jedoch ueberschrieben werden.
   * @return Liste der Rollen der Webanwendung. 
   */
  protected String[] getSecurityRoles()
  {
    return null;
  }
  
  /**
   * Liefert das Login-Handle, welches fuer die Web-Applikation verwendet werden soll.
   * Dummy-Implementierung, die kein Login-Handle zurueckliefert.
   * Kann jedoch ueberschrieben werden.
   * @return das Login-Handle.
   */
  protected UserRealm getUserRealm()
  {
    return null;
  }
}


/*********************************************************************
 * $Log: AbstractWebAppDeployer.java,v $
 * Revision 1.4  2007/12/04 12:13:48  willuhn
 * @N Login pro Webanwendung konfigurierbar
 *
 * Revision 1.3  2007/12/03 23:43:49  willuhn
 * *** empty log message ***
 *
 * Revision 1.2  2007/12/03 19:00:19  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2007/05/15 13:42:36  willuhn
 * @N Deployment von Webapps, WARs fertig und konfigurierbar
 *
 **********************************************************************/