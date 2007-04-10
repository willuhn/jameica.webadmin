/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.webadmin/src/de/willuhn/jameica/webadmin/server/HttpServiceImpl.java,v $
 * $Revision: 1.2 $
 * $Date: 2007/04/10 00:11:39 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn software & services
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.webadmin.server;

import java.io.IOException;
import java.io.PrintStream;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Properties;

import Acme.Serve.Serve;
import Acme.Serve.Serve.BasicAuthRealm;
import de.willuhn.jameica.system.Application;
import de.willuhn.jameica.system.Settings;
import de.willuhn.jameica.webadmin.Plugin;
import de.willuhn.jameica.webadmin.rmi.HttpService;
import de.willuhn.logging.Logger;
import de.willuhn.logging.LoggerOutputStream;


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
    this.worker = new Worker();
    this.worker.start();
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
    finally
    {
      this.worker = null;
    }
  }

  /**
   * Worker-Thread fuer den Webserver.
   */
  private class Worker extends Thread
  {
    private Server server = null;
    
    /**
     * ct.
     */
    private Worker()
    {
      super("[jameica.webadmin] http-worker");
      setDaemon(true);
    }

    /**
     * @see java.lang.Thread#run()
     */
    public void run()
    {
      try
      {
        Settings settings = Application.getPluginLoader().getPlugin(Plugin.class).getResources().getSettings();
        Properties props = new Properties();
        props.put("port",new Integer(settings.getInt("listener.port",Serve.DEF_PORT)));
        props.put(Serve.ARG_NOHUP,Serve.ARG_NOHUP);

        this.server = new Server(props);
        this.server.addDefaultServlets(null);
        this.server.serve();
      }
      catch (Exception e)
      {
        Logger.error("unable to start http-server",e);
        this.server = null;
      }
    }

    /**
     * @see java.lang.Thread#interrupt()
     */
    public void interrupt()
    {
      try
      {
        if (this.server != null)
        {
          Logger.info("stopping http-server");
          try
          {
            this.server.notifyStop();
          }
          catch (IOException ioe)
          {
            Logger.error("error while notifying sevrlets",ioe);
          }
          this.server.destroyAllServlets();
        }
      }
      finally
      {
        super.interrupt();
      }
    }
  }
  
  /**
   * Ueberschrieben, um die Log-Ausgaben bequemer umzuleiten.
   */
  private class Server extends Serve
  {
    /**
     * @see Acme.Serve.Serve#log(java.lang.String, java.lang.Throwable)
     */
    public void log(String message, Throwable throwable)
    {
      Logger.error(message,throwable);
    }

    /**
     * @see Acme.Serve.Serve#log(java.lang.String)
     */
    public void log(String message)
    {
      Logger.info(message);
    }

    /**
     * ct.
     * @param params
     */
    private Server(Properties params)
    {
      super(params,new PrintStream(new LoggerOutputStream(Logger.getLevel())));
      // TODO: Konstruktor von "BasicAuthRealm" ist "package default" und damit unsichtbar
//      PathTreeDictionary pt = new PathTreeDictionary();
//      BasicAuthRealm realm = new BasicAuthRealm("Jameica-Webadmin Login");
//      realm.put("admin",Application.getCallback().getPassword());
//      pt.put("/",realm);
//      this.setRealms(pt);
    }
  }
}


/**********************************************************************
 * $Log: HttpServiceImpl.java,v $
 * Revision 1.2  2007/04/10 00:11:39  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2007/04/09 17:12:01  willuhn
 * *** empty log message ***
 *
 **********************************************************************/
