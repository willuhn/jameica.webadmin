/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.webadmin/src/de/willuhn/jameica/webadmin/rmi/RestService.java,v $
 * $Revision: 1.2 $
 * $Date: 2008/06/16 14:22:11 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn software & services
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.webadmin.rmi;

import java.io.IOException;
import java.rmi.RemoteException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.willuhn.datasource.Service;

/**
 * Service, der eine Mini-REST-API bereitstellt.
 */
public interface RestService extends Service
{
  /**
   * Verarbeitet einen Request.
   * @param request
   * @param response
   * @throws IOException
   */
  public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws IOException;
  
  /**
   * Registriert ein Kommando auf eine URL.
   * @param urlPattern Pattern mit einer URL.
   * @param command zugehoerige Klasse und Methode, die aufgerufen werden soll.
   * @throws RemoteException
   */
  public void register(String urlPattern, String command) throws RemoteException;
}


/*********************************************************************
 * $Log: RestService.java,v $
 * Revision 1.2  2008/06/16 14:22:11  willuhn
 * @N Mapping der REST-URLs via Property-Datei
 *
 * Revision 1.1  2008/06/13 14:11:04  willuhn
 * @N Mini REST-API
 *
 **********************************************************************/