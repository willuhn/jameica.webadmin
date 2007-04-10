/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.webadmin/src/de/willuhn/jameica/webadmin/server/HttpServiceImpl.java,v $
 * $Revision: 1.4 $
 * $Date: 2007/04/10 00:52:32 $
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
import java.util.HashMap;
import java.util.Map;

import winstone.Launcher;
import de.willuhn.jameica.system.Application;
import de.willuhn.jameica.system.Settings;
import de.willuhn.jameica.webadmin.Plugin;
import de.willuhn.jameica.webadmin.rmi.HttpService;
import de.willuhn.logging.Logger;


/**
 * Implementierung des HTTP-Services.
 */
public class HttpServiceImpl extends UnicastRemoteObject implements HttpService
{
  private Launcher server = null;

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
      Settings settings = Application.getPluginLoader().getPlugin(Plugin.class).getResources().getSettings();

      Map args = new HashMap();
      args.put("httpPort",new Integer(settings.getInt("listener.port",8080)));
      // httpListenAddress
      // webroot
      // -debug                  = set the level of debug msgs (1-9). Default is 5 (INFO level)
//      --httpsPort              = set the https listening port. -1 to disable, Default is disabled
//      --httpsListenAddress     = set the https listening address. Default is all interfaces
//      --httpsKeyStore          = the location of the SSL KeyStore file. Default is ./winstone.ks
//      --httpsKeyStorePassword  = the password for the SSL KeyStore file. Default is null
//      --httpsKeyManagerType    = the SSL KeyManagerFactory type (eg SunX509, IbmX509). Default is SunX509
//      --handlerCountStartup    = set the no of worker threads to spawn at startup. Default is 5
//      --handlerCountMax        = set the max no of worker threads to allow. Default is 300
//      --handlerCountMaxIdle    = set the max no of idle worker threads to allow. Default is 50
//
//      --directoryListings      = enable directory lists (true/false). Default is true
//      
//      --realmClassName               = Set the realm class to use for user authentication. Defaults to ArgumentsRealm class
//      --accessLoggerClassName        = Set the access logger class to use for user authentication. Defaults to disabled
//      --simpleAccessLogger.format    = The log format to use. Supports combined/common/resin/custom (SimpleAccessLogger only)
//      --simpleAccessLogger.file      = The location pattern for the log file(SimpleAccessLogger only)
      Launcher.initLogger(args);
      this.server = new Launcher(args);
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
      this.server.shutdown();
    }
    finally
    {
      this.server = null;
    }
  }
}


/**********************************************************************
 * $Log: HttpServiceImpl.java,v $
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
