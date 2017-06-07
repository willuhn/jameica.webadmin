/**********************************************************************
 *
 * Copyright (c) by Olaf Willuhn
 * All rights reserved
 * GPLv2
 *
 **********************************************************************/

package de.willuhn.jameica.webadmin.messaging;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.HandlerCollection;

import de.willuhn.jameica.messaging.Message;
import de.willuhn.jameica.messaging.MessageConsumer;
import de.willuhn.jameica.messaging.SystemMessage;
import de.willuhn.jameica.system.Application;
import de.willuhn.jameica.webadmin.Plugin;
import de.willuhn.jameica.webadmin.deploy.Deployer;
import de.willuhn.jameica.webadmin.rmi.HttpService;
import de.willuhn.logging.Logger;

/**
 * Das Deployen der Web-Anwendungen koennen wir erst machen, nachdem alle
 * Plugins geladen sind. Daher via Message-Consumer.
 */
public class DeployMessageConsumer implements MessageConsumer
{

  /**
   * @see de.willuhn.jameica.messaging.MessageConsumer#autoRegister()
   */
  public boolean autoRegister()
  {
    return true;
  }

  /**
   * @see de.willuhn.jameica.messaging.MessageConsumer#getExpectedMessageTypes()
   */
  public Class[] getExpectedMessageTypes()
  {
    return new Class[]{SystemMessage.class};
  }

  /**
   * @see de.willuhn.jameica.messaging.MessageConsumer#handleMessage(de.willuhn.jameica.messaging.Message)
   */
  public void handleMessage(Message message) throws Exception
  {
    if (((SystemMessage)message).getStatusCode() != SystemMessage.SYSTEM_STARTED)
      return;
    
    
    ContextHandlerCollection collection = new ContextHandlerCollection();

    try
    {
      Class[] cl = Application.getClassLoader().getClassFinder().findImplementors(Deployer.class);
      for (int i=0;i<cl.length;++i)
      {
        try
        {
          Logger.info("init deployer " + cl[i].getName());
          Deployer d = (Deployer) cl[i].newInstance();
          Handler[] handlers = d.deploy();
          if (handlers == null || handlers.length == 0)
          {
            Logger.info("skipping deployer " + d.getClass() + " - contains no handlers");
            continue;
          }
          for (Handler h:handlers)
          {
            collection.addHandler(h);
          }
          
        }
        catch (Exception e)
        {
          Logger.error("error while loading deployer " + cl[i].getName() + ", skipping",e);
        }
      }
    }
    catch (ClassNotFoundException e)
    {
      Logger.warn("no deployers found, skipping http-server");
      return;
    }
    
    // Wir erzeugen eine Handler-Collection.
    HandlerCollection handlers = new HandlerCollection();
    handlers.addHandler(collection);
    
    HttpService server = (HttpService) Application.getServiceFactory().lookup(Plugin.class,"listener.http");
    server.addHandler(handlers);
  }

}
