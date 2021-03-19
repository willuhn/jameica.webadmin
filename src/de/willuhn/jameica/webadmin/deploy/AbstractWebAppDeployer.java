/**********************************************************************
 *
 * Copyright (c) by Olaf Willuhn
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.webadmin.deploy;

import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.DefaultIdentityService;
import org.eclipse.jetty.security.LoginService;
import org.eclipse.jetty.security.authentication.BasicAuthenticator;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.util.security.Constraint;
import org.eclipse.jetty.webapp.WebAppContext;

import de.willuhn.logging.Logger;

/**
 * Abstrakte Basis-Implementierung eines Deployers, der Web-Anwendungen deployen kann.
 * Zum Etablieren einer Webanwendung muss lediglich von dieser Klasse abgeleitet werden.
 */
public abstract class AbstractWebAppDeployer implements Deployer
{

  /**
   * @see de.willuhn.jameica.webadmin.deploy.Deployer#deploy()
   */
  public final Handler[] deploy()
  {
    String path    = getPath();
    String context = getContext();

    Logger.info("deploying " + context + " (" + path + ")");
    WebAppContext app = new WebAppContext(path,context);

    // Classloader explizit angeben. Sonst verwendet Jetty den System-Classloader, der nichts kennt
    app.setClassLoader(this.getClass().getClassLoader());

    LoginService login = this.getLoginService();
    if (login != null)
    {
      Logger.info("  activating authentication via " + login.getName());
      Constraint constraint = new Constraint();
      constraint.setName(Constraint.__BASIC_AUTH);
      constraint.setAuthenticate(true);
      String[] roles = getSecurityRoles();
      if (roles != null)
      {
        constraint.setRoles(roles);
      }

      ConstraintMapping cm = new ConstraintMapping();
      cm.setConstraint(constraint);
      cm.setPathSpec("/*");

      // Wir nehmen uns den Security-Handler der Webapp und passen
      // ihne fuer uns an.
      ConstraintSecurityHandler sh = (ConstraintSecurityHandler) app.getSecurityHandler();
      sh.setLoginService(login);
      sh.setIdentityService(new DefaultIdentityService());
      sh.setAuthenticator(new BasicAuthenticator());
      sh.setConstraintMappings(new ConstraintMapping[]{cm});
    }
    
    return new Handler[]{app};
  }
  
  /**
   * Liefert den Pfad im Dateisystem zu der Web-Anwendung.
   * Also das Verzeichnis, in dem sich die index.html befindet.
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
  protected LoginService getLoginService()
  {
    return null;
  }
}
