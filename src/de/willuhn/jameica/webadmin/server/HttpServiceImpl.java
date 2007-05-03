/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.webadmin/src/de/willuhn/jameica/webadmin/server/HttpServiceImpl.java,v $
 * $Revision: 1.10 $
 * $Date: 2007/05/03 23:39:52 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn software & services
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.webadmin.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.HandlerList;
import org.mortbay.jetty.security.Constraint;
import org.mortbay.jetty.security.ConstraintMapping;
import org.mortbay.jetty.security.SecurityHandler;
import org.mortbay.jetty.servlet.ServletHandler;

import de.willuhn.jameica.webadmin.Settings;
import de.willuhn.jameica.webadmin.rmi.HttpService;
import de.willuhn.jameica.webadmin.servlets.ImageServlet;
import de.willuhn.jameica.webadmin.servlets.PluginServlet;
import de.willuhn.jameica.webadmin.servlets.ResourceServlet;
import de.willuhn.jameica.webadmin.servlets.RootServlet;
import de.willuhn.logging.Logger;


/**
 * Implementierung des HTTP-Services.
 */
public class HttpServiceImpl extends UnicastRemoteObject implements HttpService
{
  private Server server = null;

  /**
   * @throws RemoteException
   */
  public HttpServiceImpl() throws RemoteException
  {
    super();
  }

  /**
   * @see de.willuhn.datasource.Service#getName()
   */
  public String getName() throws RemoteException
  {
    return "Webadmin HTTP-Service";
  }

  /**
   * @see de.willuhn.datasource.Service#isStartable()
   */
  public boolean isStartable() throws RemoteException
  {
    return !isStarted();
  }

  /**
   * @see de.willuhn.datasource.Service#isStarted()
   */
  public boolean isStarted() throws RemoteException
  {
    return this.server != null;
  }

  /**
   * @see de.willuhn.datasource.Service#start()
   */
  public void start() throws RemoteException
  {
    if (isStarted())
    {
      Logger.warn("service allready started, skipping request");
      return;
    }
    
    try
    {
      // Logging zu uns umleiten
      System.setProperty("org.mortbay.log.class",JettyLogger.class.getName());

      Logger.info("started webserver at port " + Settings.getPort());
      this.server = new Server(Settings.getPort());
      this.server.setStopAtShutdown(false);
      if (Settings.getUseSSL())
        this.server.setConnectors(new Connector[]{new JameicaSocketConnector()});

      HandlerList list = new HandlerList();
      
      // Servlets
      ServletHandler handler = new ServletHandler();

      handler.addServletWithMapping(ResourceServlet.class, "/res");
      handler.addServletWithMapping(ImageServlet.class, "/img");

      // a) Velocity
      handler.addServletWithMapping(RootServlet.class, "/");
      handler.addServletWithMapping(PluginServlet.class, "/plugin");
      
      // b) GWT
      // TODO: Funktioniert noch nicht
//      ServletHolder holder = new ServletHolder(DefaultServlet.class);
//      holder.setInitParameter("dirAllowed","true");
//      holder.setInitParameter("resourceBase","/work/willuhn/eclipse/jameica.webadmin/src/res/gwt/de.willuhn.jameica.webadmin.gwt.WebAdmin");
//      handler.addServletWithMapping(holder,"/*");
      
      list.addHandler(handler);
      
      if (Settings.getUseAuth())
      {
        Constraint constraint = new Constraint();
        constraint.setName(Constraint.__BASIC_AUTH);;
        constraint.setRoles(new String[]{"admin"});
        constraint.setAuthenticate(true);

        ConstraintMapping cm = new ConstraintMapping();
        cm.setConstraint(constraint);
        cm.setPathSpec("/*");

        SecurityHandler sh = new SecurityHandler();
        sh.setUserRealm(new JameicaUserRealm());
        sh.setConstraintMappings(new ConstraintMapping[]{cm});

        // Authentifizierung drum rum wrappen
        sh.setHandler(list);
        this.server.setHandler(sh);
      }
      else
      {
        // Ansonsten direkt die Haendler-Liste an den Server geben
        this.server.setHandler(list);
      }
      
      this.server.start();
    }
    catch (Exception e)
    {
      Logger.error("unable to init http-server",e);
    }
  }

  /**
   * @see de.willuhn.datasource.Service#stop(boolean)
   */
  public void stop(boolean arg0) throws RemoteException
  {
    if (!isStarted())
    {
      Logger.warn("service not started, skipping request");
      return;
    }
    try
    {
      this.server.stop();
    }
    catch (Exception e)
    {
      Logger.error("unable to stop http-server",e);
    }
    finally
    {
      this.server = null;
    }
  }
}


/**********************************************************************
 * $Log: HttpServiceImpl.java,v $
 * Revision 1.10  2007/05/03 23:39:52  willuhn
 * @N Vorbereitungen fuer Integration von GWT (Google Web Toolkit)
 *
 * Revision 1.9  2007/04/16 13:44:45  willuhn
 * @N display logs
 * @N display installed plugins
 * @N display plugin details
 * @N ability to start/stop services
 *
 * Revision 1.8  2007/04/16 11:22:15  willuhn
 * @N display log
 *
 * Revision 1.7  2007/04/16 00:12:39  willuhn
 * @N Image-Handler
 *
 * Revision 1.6  2007/04/12 13:35:17  willuhn
 * @N SSL-Support
 * @N Authentifizierung
 * @N Korrektes Logging
 *
 * Revision 1.5  2007/04/12 00:02:55  willuhn
 * @C replaced winstone with jetty (because of ssl support via custom socketfactory)
 *
 * Revision 1.4  2007/04/10 00:52:32  willuhn
 * @C moved to winstone (better realm integration)
 *
 * Revision 1.3  2007/04/10 00:11:55  willuhn
 * *** empty log message ***
 *
 * Revision 1.2  2007/04/10 00:11:39  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2007/04/09 17:12:01  willuhn
 * *** empty log message ***
 *
 **********************************************************************/
