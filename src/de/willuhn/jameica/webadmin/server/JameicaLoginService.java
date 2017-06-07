/**********************************************************************
 *
 * Copyright (c) by Olaf Willuhn
 * All rights reserved
 * GPLv2
 *
 **********************************************************************/

package de.willuhn.jameica.webadmin.server;

import java.security.Principal;

import javax.security.auth.Subject;
import javax.servlet.ServletRequest;

import org.eclipse.jetty.security.IdentityService;
import org.eclipse.jetty.security.LoginService;
import org.eclipse.jetty.server.UserIdentity;

import de.willuhn.jameica.system.Application;
import de.willuhn.jameica.webadmin.Settings;
import de.willuhn.logging.Logger;

/**
 * Implementierung des Jetty-LoginServices, um das Login mittels Jameica-Masterpasswort abwickeln zu koennen.
 */
public class JameicaLoginService implements LoginService
{
  private final static UserIdentity ADMIN = new JameicaUserIdentity("admin");
  
  private IdentityService is = null;
  
  /**
   * @see org.eclipse.jetty.security.LoginService#getName()
   */
  @Override
  public String getName()
  {
    return "jameica.webadmin";
  }
  
  /**
   * @see org.eclipse.jetty.security.LoginService#login(java.lang.String, java.lang.Object, javax.servlet.ServletRequest)
   */
  @Override
  public UserIdentity login(String username, Object credentials, ServletRequest request)
  {
    if (!Settings.getUseAuth())
      return ADMIN;
    
    if (username == null || username.length() == 0)
      return null;
    if (credentials == null)
      return null;
    
    String pw = credentials.toString();
    
    if (pw == null || pw.length() == 0)
      return null;
    
    try
    {
      // Den Usernamen vergleichen wir nicht.
      if (pw.equals(Application.getCallback().getPassword()))
        return ADMIN;
    }
    catch (Exception e)
    {
      Logger.error("error while checking password, denying request",e);
    }
    
    Logger.warn("invalid password for user " + username);
    return null;
  }

  /**
   * @see org.eclipse.jetty.security.LoginService#validate(org.eclipse.jetty.server.UserIdentity)
   */
  @Override
  public boolean validate(UserIdentity user)
  {
    return user != null && user.equals(ADMIN);
  }

  /**
   * @see org.eclipse.jetty.security.LoginService#getIdentityService()
   */
  @Override
  public IdentityService getIdentityService()
  {
    return this.is;
  }

  /**
   * @see org.eclipse.jetty.security.LoginService#setIdentityService(org.eclipse.jetty.security.IdentityService)
   */
  @Override
  public void setIdentityService(IdentityService service)
  {
    this.is = service;
  }

  /**
   * @see org.eclipse.jetty.security.LoginService#logout(org.eclipse.jetty.server.UserIdentity)
   */
  @Override
  public void logout(UserIdentity user)
  {
  }

  /**
   * Implementierung fuer den Jameica-Admin-User.
   */
  private static class JameicaUserIdentity implements UserIdentity
  {
    private Subject subject = null;
    private Principal principal = null;
    
    /**
     * ct.
     * @param name
     */
    private JameicaUserIdentity(String name)
    {
      this.principal = new JameicaPrincipal(name);
      this.subject = new Subject();
      this.subject.getPrincipals().add(this.principal);
    }

    /**
     * @see org.eclipse.jetty.server.UserIdentity#getSubject()
     */
    @Override
    public Subject getSubject()
    {
      return this.subject;
    }
    
    /**
     * @see org.eclipse.jetty.server.UserIdentity#getUserPrincipal()
     */
    @Override
    public Principal getUserPrincipal()
    {
      return this.principal;
    }
    
    /**
     * @see org.eclipse.jetty.server.UserIdentity#isUserInRole(java.lang.String, org.eclipse.jetty.server.UserIdentity.Scope)
     */
    @Override
    public boolean isUserInRole(String role, Scope scope)
    {
      return true;
    }
  }
  
  /**
   * Implementierung fuer den Jameica-Admin-User.
   */
  private static class JameicaPrincipal implements Principal
  {
    private String name = null;
    
    /**
     * ct.
     * @param name
     */
    private JameicaPrincipal(String name)
    {
      this.name = name;
    }
    
    /**
     * @see java.security.Principal#getName()
     */
    public String getName()
    {
      return this.name;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj)
    {
      if (obj == null || !(obj instanceof Principal))
        return false;
      return this.name.equals(((Principal)obj).getName());
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
      return this.name.hashCode();
    }
    
  }

}
