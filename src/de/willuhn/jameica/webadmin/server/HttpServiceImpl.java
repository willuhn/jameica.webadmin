/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.webadmin/src/de/willuhn/jameica/webadmin/server/HttpServiceImpl.java,v $
 * $Revision: 1.17 $
 * $Date: 2007/12/03 23:43:49 $
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
import java.util.ArrayList;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.ContextHandlerCollection;
import org.mortbay.jetty.handler.DefaultHandler;
import org.mortbay.jetty.handler.HandlerCollection;
import org.mortbay.jetty.security.Constraint;
import org.mortbay.jetty.security.ConstraintMapping;
import org.mortbay.jetty.security.SecurityHandler;

import de.willuhn.jameica.system.Application;
import de.willuhn.jameica.webadmin.Settings;
import de.willuhn.jameica.webadmin.deploy.Deployer;
import de.willuhn.jameica.webadmin.rmi.HttpService;
import de.willuhn.logging.Logger;


/**
 * Implementierung des HTTP-Services.
 */
public class HttpServiceImpl extends UnicastRemoteObject implements HttpService
{
  private Worker worker = null;

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
    return this.worker != null;
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

    worker = new Worker();
    worker.start();
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
      this.worker.interrupt();
    }
    catch (Exception e)
    {
      Logger.error("unable to stop http-server",e);
    }
    finally
    {
      this.worker = null;
    }
  }
  
  
  private class Worker extends Thread
  {
    private Server server = null;

    private Worker()
    {
      // Ich weiss nicht warum, aber Jetty braucht das so
      ClassLoader cl = HttpServiceImpl.this.getClass().getClassLoader();
      setContextClassLoader(cl);
    }
    
    /**
     * @see java.lang.Thread#run()
     */
    public void run()
    {
      try
      {
        // Logging zu uns umleiten
        System.setProperty("org.mortbay.log.class",JettyLogger.class.getName());

        Logger.info("started webserver at port " + Settings.getPort());
        this.server = new Server(Settings.getPort());
        this.server.setStopAtShutdown(false);
        if (Settings.getUseSSL())
          this.server.setConnectors(new Connector[]{new JameicaSocketConnector()});

        ContextHandlerCollection collection = new ContextHandlerCollection();

        ArrayList deployer = new ArrayList();
        try
        {
          Class[] cl = Application.getClassLoader().getClassFinder().findImplementors(Deployer.class);
          for (int i=0;i<cl.length;++i)
          {
            try
            {
              deployer.add((Deployer) cl[i].newInstance());
            }
            catch (Exception e)
            {
              Logger.error("error while loading deployer " + cl[i].getName() + ", skipping",e);
            }
          }
        }
        catch (ClassNotFoundException e)
        {
          // Dann halt nicht
        }
        
        if (deployer.size() == 0)
        {
          Logger.warn("no deployers found, skipping http-server");
          return;
        }
        
        for (int i=0;i<deployer.size();++i)
        {
          Deployer d = (Deployer) deployer.get(i);
          try
          {
            d.deploy(this.server,collection);
          }
          catch (Exception e)
          {
            Logger.error("error while starting deployer " + d.getClass().getName() + ", skipping");
          }
        }

        // Wir erzeugen eine Handler-Collection mit Default-Handler.
        HandlerCollection handlers = new HandlerCollection();
        handlers.addHandler(collection);
        handlers.addHandler(new DefaultHandler());

        if (Settings.getUseAuth())
        {
          Logger.info("activating authentication");
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
          sh.setHandler(handlers);
          this.server.setHandler(sh);
        }
        else
        {
          // Ansonsten direkt die Haendler-Liste an den Server geben
          this.server.setHandler(handlers);
        }
        
        
        this.server.start();
        this.server.join();
      }
      catch (InterruptedException ie)
      {
        Logger.info("http-server stopped");
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
      catch (Exception e)
      {
        Logger.error("unable to init http-server",e);
      }
    }
  }
}


/**********************************************************************
 * $Log: HttpServiceImpl.java,v $
 * Revision 1.17  2007/12/03 23:43:49  willuhn
 * *** empty log message ***
 *
 * Revision 1.16  2007/12/03 19:00:19  willuhn
 * *** empty log message ***
 *
 * Revision 1.15  2007/05/15 13:42:36  willuhn
 * @N Deployment von Webapps, WARs fertig und konfigurierbar
 *
 * Revision 1.14  2007/05/15 11:21:12  willuhn
 * *** empty log message ***
 *
 * Revision 1.13  2007/05/15 00:22:20  willuhn
 * @N Vorbereitung fuer WAR-Deployment
 *
 * Revision 1.12  2007/05/14 23:42:36  willuhn
 * @R removed GWT - sucks!
 *
 * Revision 1.11  2007/05/07 22:21:28  willuhn
 * *** empty log message ***
 *
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
