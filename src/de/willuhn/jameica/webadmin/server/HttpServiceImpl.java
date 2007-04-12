/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.webadmin/src/de/willuhn/jameica/webadmin/server/HttpServiceImpl.java,v $
 * $Revision: 1.5 $
 * $Date: 2007/04/12 00:02:55 $
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

import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.ServletHandler;

import de.willuhn.jameica.system.Application;
import de.willuhn.jameica.system.Settings;
import de.willuhn.jameica.webadmin.JettyLogger;
import de.willuhn.jameica.webadmin.Plugin;
import de.willuhn.jameica.webadmin.rmi.HttpService;
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

      Settings settings = Application.getPluginLoader().getPlugin(Plugin.class).getResources().getSettings();
      int port = new Integer(settings.getInt("listener.http.port",8080)).intValue();
      this.server = new Server(port);
      this.server.setStopAtShutdown(false);

      ServletHandler handler = new ServletHandler();
      handler.addServletWithMapping(RootServlet.class, "/");      
      this.server.setHandler(handler);

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
