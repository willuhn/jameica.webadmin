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
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.security.Constraint;

import de.willuhn.jameica.webadmin.Settings;
import de.willuhn.jameica.webadmin.server.JameicaLoginService;
import de.willuhn.logging.Logger;

/**
 * Abstrakte Basis-Implementierung eines Deployers, der Servlets deployen kann.
 */
public abstract class AbstractServletDeployer implements Deployer
{

  /**
   * @see de.willuhn.jameica.webadmin.deploy.Deployer#deploy()
   */
  public final Handler[] deploy()
  {
    String context = getContext();
    Class servlet  = getServletClass();

    Logger.info("deploying " + context + " (" + servlet.getName() + ")");

    boolean auth = Settings.getUseAuth();
    int flags = ServletContextHandler.NO_SESSIONS;
    if (auth)
      flags |= ServletContextHandler.SECURITY;

    ServletContextHandler app = new ServletContextHandler(flags);
    app.addServlet(new ServletHolder(servlet),context + "/*");
    
    // Classloader explizit angeben. Sonst verwendet Jetty den System-Classloader, der nichts kennt
    app.setClassLoader(this.getClass().getClassLoader());

    if (auth)
    {
      LoginService login = this.getLoginService();
      if (login == null)
      {
        Logger.error("  no login service defined but authentication activated. fallback to login via master password");
        login = new JameicaLoginService();
      }
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
   * Liefert den Namen des Contextes.
   * Soll die Webanwendung also unter "http://server/test" erreichbar
   * sein, muss die Funktion "/test" zurueckliefern.
   * @return der Name des Context.
   */
  protected abstract String getContext();
  
  /**
   * Liefert das zu deployende Servlet.
   * @return das Servlet.
   */
  protected abstract Class getServletClass();
  
  /**
   * Liefert die Benutzer-Rollen, die im Servlet zur Verfuegung stehen.
   * Dummy-Implementierung, die keine Rollen zurueckliefert.
   * Kann jedoch ueberschrieben werden.
   * @return Liste der Rollen des Servlets.
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
