/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.webadmin/src/de/willuhn/jameica/webadmin/server/HttpServiceImpl.java,v $
 * $Revision: 1.1 $
 * $Date: 2007/04/09 17:12:01 $
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
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;

import de.willuhn.jameica.webadmin.rmi.HttpService;


/**
 * Implementierung des HTTP-Services.
 */
public class HttpServiceImpl extends UnicastRemoteObject implements HttpService
{

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
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * @see de.willuhn.datasource.Service#isStartable()
   */
  public boolean isStartable() throws RemoteException
  {
    // TODO Auto-generated method stub
    return false;
  }

  /**
   * @see de.willuhn.datasource.Service#isStarted()
   */
  public boolean isStarted() throws RemoteException
  {
    // TODO Auto-generated method stub
    return false;
  }

  /**
   * @see de.willuhn.datasource.Service#start()
   */
  public void start() throws RemoteException
  {
    // TODO Auto-generated method stub

  }

  /**
   * @see de.willuhn.datasource.Service#stop(boolean)
   */
  public void stop(boolean arg0) throws RemoteException
  {
    // TODO Auto-generated method stub

  }

}


/**********************************************************************
 * $Log: HttpServiceImpl.java,v $
 * Revision 1.1  2007/04/09 17:12:01  willuhn
 * *** empty log message ***
 *
 **********************************************************************/
