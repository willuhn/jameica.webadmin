/**********************************************************************
 *
 * Copyright (c) by Olaf Willuhn
 * All rights reserved
 * GPLv2
 *
 **********************************************************************/

package de.willuhn.jameica.webadmin.server;

import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.server.ConnectionFactory;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.webapp.Configuration;

import de.willuhn.jameica.messaging.TextMessage;
import de.willuhn.jameica.system.Application;
import de.willuhn.jameica.webadmin.Settings;
import de.willuhn.jameica.webadmin.rmi.HttpService;
import de.willuhn.logging.Logger;


/**
 * Implementierung des HTTP-Services.
 */
public class HttpServiceImpl extends UnicastRemoteObject implements HttpService
{
  private Server server      = null;
  private ArrayList handlers = new ArrayList();

  /**
   * @throws RemoteException
   */
  public HttpServiceImpl() throws RemoteException
  {
    super();
  }

  /**
   * @see de.willuhn.jameica.webadmin.rmi.HttpService#addHandler(org.eclipse.jetty.server.Handler)
   */
  public void addHandler(Handler handler) throws RemoteException
  {
    this.handlers.add(handler);
    this.stop(true);
    this.start();
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
    if (this.isStarted())
    {
      Logger.warn("service already started, skipping request");
      return;
    }
    
    // Wir wollen keinen Default-Handler.
    if (this.handlers.size() == 0)
    {
      Logger.info("no handlers, skip server start");
      return;
    }
    
    this.server = new Server(Settings.getPort());
    this.server.setStopAtShutdown(false);
    
    ServerConnector conn = null;
    if (Settings.getUseSSL())
    {
      try
      {
        SslContextFactory sf = new SslContextFactory();
        sf.setCertAlias(Settings.SETTINGS.getString("keystore.alias","jameica"));
        sf.setNeedClientAuth(false);
        sf.setSslContext(Application.getSSLFactory().getSSLContext());
        
        HttpConfiguration conf = new HttpConfiguration();
        conf.setSendServerVersion(false);
        
        // Keine Ahnung, warum. Aber wenn man das weglaesst und eine Context-URL ohne "/" am Ende
        // eingibt, dann erzeugt der Jetty ein Redirect zwar auf die passende Context-URL MIT "/",
        // "vergisst" dabei aber das "https" und macht einen Redirect auf "HTTP". Die folgende
        // Zeile bewirkt ein sauberes Redirect auf "https".
        // Siehe https://stackoverflow.com/questions/20175924/embedded-jetty-in-secure-https-server-contexthandler-redirects-to-http-uri
        conf.addCustomizer(new SecureRequestCustomizer());
        
        HttpConnectionFactory http  = new HttpConnectionFactory(conf);
        SslConnectionFactory  https = new SslConnectionFactory(sf,"HTTP/1.1");
        
        conn = new ServerConnector(this.server);
        
        Collection<ConnectionFactory> factories = new ArrayList<ConnectionFactory>();
        factories.add(https);
        factories.add(http);
        conn.setConnectionFactories(factories);
        
        conn.setDefaultProtocol("SSL");
      }
      catch (Exception e)
      {
        IOUtils.closeQuietly(conn);
        throw new RemoteException("unable to init ssl",e);
      }
    }
    else
    {
      conn = new ServerConnector(this.server);
    }
    conn.setPort(Settings.getPort());
    
    InetAddress bindAddress = Settings.getAddress();
    if (bindAddress != null)
    {
      Logger.info("  bound to: " + bindAddress.getHostAddress());
      conn.setHost(bindAddress.getHostAddress());
    }
    this.server.setConnectors(new Connector[]{conn});
    
    // JSP-Support aktivieren. Siehe
    // http://www.eclipse.org/jetty/documentation/current/embedded-examples.html#embedded-webapp-jsp
    Configuration.ClassList classlist = Configuration.ClassList.setServerDefault(server);
    classlist.addBefore("org.eclipse.jetty.webapp.JettyWebXmlConfiguration","org.eclipse.jetty.annotations.AnnotationConfiguration" );

    ContextHandlerCollection contexts = new ContextHandlerCollection();
    Handler[] list = (Handler[]) this.handlers.toArray(new Handler[this.handlers.size()]);
    contexts.setHandlers(list);
    
    HandlerCollection coll = new HandlerCollection();
    coll.setHandlers(new Handler[]{contexts});
    this.server.setHandler(coll);

    try
    {
      this.server.start();
      Application.getMessagingFactory().getMessagingQueue("jameica.webadmin.started").sendMessage(new TextMessage("web server deployed"));
      Logger.info("started webserver at port " + Settings.getPort());
    }
    catch (Exception e)
    {
      throw new RemoteException("unable to start handler",e);
    }
  }

  /**
   * @see de.willuhn.datasource.Service#stop(boolean)
   */
  public void stop(boolean arg0) throws RemoteException
  {
    if (!this.isStarted())
    {
      Logger.debug("service not started, skipping request");
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
