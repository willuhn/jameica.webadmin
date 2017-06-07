/**********************************************************************
 *
 * Copyright (c) by Olaf Willuhn
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.webadmin.rmi;

import java.rmi.RemoteException;

import org.eclipse.jetty.server.Handler;

import de.willuhn.datasource.Service;


/**
 * Service, der den HTTP-Dienst startet und beendet.
 */
public interface HttpService extends Service
{
  /**
   * Fuegt dem Server einen Handler hinzu. 
   * @param handler der neue Handler.
   * @throws RemoteException
   */
  public void addHandler(Handler handler) throws RemoteException;
}
