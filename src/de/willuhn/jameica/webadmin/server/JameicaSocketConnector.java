/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.webadmin/src/de/willuhn/jameica/webadmin/server/JameicaSocketConnector.java,v $
 * $Revision: 1.1 $
 * $Date: 2007/04/12 13:35:17 $
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
import java.net.InetAddress;
import java.net.ServerSocket;

import javax.net.ssl.SSLServerSocketFactory;

import org.mortbay.jetty.security.SslSocketConnector;

import de.willuhn.jameica.system.Application;
import de.willuhn.jameica.webadmin.Settings;
import de.willuhn.logging.Logger;

/**
 * Ueberschrieben, um die SSL-Socketfactory von Jameica zu verwenden.
 */
public class JameicaSocketConnector extends SslSocketConnector
{
  private boolean auth;
  
  /**
   * ct.
   */
  public JameicaSocketConnector()
  {
    this.auth = Settings.getUseAuth();
    Logger.info("  auth enabled: " + auth);
    Logger.info("  ssl enabled : true");
  }

  /**
   * @see org.mortbay.jetty.security.SslSocketConnector#createFactory()
   */
  protected SSLServerSocketFactory createFactory() throws Exception
  {
    return Application.getSSLFactory().getSSLContext().getServerSocketFactory();
  }

  /**
   * @see org.mortbay.jetty.security.SslSocketConnector#getNeedClientAuth()
   */
  public boolean getNeedClientAuth()
  {
    return auth;
  }

  /**
   * @see org.mortbay.jetty.security.SslSocketConnector#newServerSocket(java.lang.String, int, int)
   */
  protected ServerSocket newServerSocket(String host, int port, int backlog) throws IOException
  {
    try
    {
      Logger.info("creating server socket at port: " + port);
      InetAddress a = Settings.getAddress();
      if (a == null)
        return createFactory().createServerSocket(port,backlog);
      return createFactory().createServerSocket(port,backlog,a);
    }
    catch (IOException ioe)
    {
      throw ioe;
    }
    catch (Exception e)
    {
      Logger.error("unable to create server socket",e);
    }
    return super.newServerSocket(host,port,backlog);
  }
}


/*********************************************************************
 * $Log: JameicaSocketConnector.java,v $
 * Revision 1.1  2007/04/12 13:35:17  willuhn
 * @N SSL-Support
 * @N Authentifizierung
 * @N Korrektes Logging
 *
 **********************************************************************/