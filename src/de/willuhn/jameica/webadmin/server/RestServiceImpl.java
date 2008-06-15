/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.webadmin/src/de/willuhn/jameica/webadmin/server/RestServiceImpl.java,v $
 * $Revision: 1.3 $
 * $Date: 2008/06/15 22:48:24 $
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
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.willuhn.jameica.system.Application;
import de.willuhn.jameica.webadmin.Plugin;
import de.willuhn.jameica.webadmin.rest.Command;
import de.willuhn.jameica.webadmin.rest.Context;
import de.willuhn.jameica.webadmin.rmi.RestService;
import de.willuhn.logging.Logger;
import de.willuhn.util.ClassFinder;
import de.willuhn.util.MultipleClassLoader;

/**
 * Implementierung des REST-Services.
 */
public class RestServiceImpl implements RestService
{
  private Hashtable registry = null;
  
  /**
   * @see de.willuhn.jameica.webadmin.rmi.RestService#handleRequest(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
   */
  public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws IOException
  {
    if (!this.isStarted())
      throw new IOException("REST service not started");

    String command = request.getPathInfo();
    if (command == null)
      throw new IOException("missing REST command");
  
    // Slash am Anfang abschneiden
    if (command.startsWith("/"))
      command = command.substring(1);

    if (command.length() == 0)
      throw new IOException("missing REST command");
    
    // Ein REST-Command sieht z.Bsp. so aus:
    // (http://server:8080/webadmin/) "plugins/hibiscus/services/database"
    // Die Verzeichnisses sind also immer im Wechsel Kommando und dann Parameter.
    // In dem Fall also erst ein Kommando "plugins" mit dem Parameter "hibiscus"
    // und dann ein Kommando "plugins" mit dem Parameter "database" innerhalb
    // des Context "plugins". Wir rufen die Commandos daher als Chain in
    // dieser Reihenfolge auf.
    
    String[] values = command.split("/");
    List queue = new LinkedList();
    queue.addAll(Arrays.asList(values));

    Command c = null;
    
    for (int i=0;i<100;++i)
    {
      if (queue.size() == 0)
        return; // end of chain
      
      final Context context = new Context(request,response);
      context.setParent(c);

      String current = (String) queue.remove(0);

      // Mal schauen, ob wir ein Kommando haben
      c = (Command) this.registry.get(current);
      if (c == null)
        throw new IOException("REST command not found: " + command);

      // Es haengt noch ein Kommando dran
      if (queue.size() > 0)
        context.setParameter((String)queue.remove(0));

      Logger.debug("executing command " + command + ", class " + c.getClass().getName());
      c.execute(context);
    }


  }

  /**
   * @see de.willuhn.datasource.Service#getName()
   */
  public String getName() throws RemoteException
  {
    return "REST-API Service";
  }

  /**
   * @see de.willuhn.datasource.Service#isStartable()
   */
  public boolean isStartable() throws RemoteException
  {
    return !this.isStarted();
  }

  /**
   * @see de.willuhn.datasource.Service#isStarted()
   */
  public boolean isStarted() throws RemoteException
  {
    return this.registry != null;
  }

  /**
   * @see de.willuhn.datasource.Service#start()
   */
  public void start() throws RemoteException
  {
    if (isStarted())
    {
      Logger.warn("service allread started, skipping request");
      return;
    }
    
    Logger.info("init REST registry");

    this.registry = new Hashtable();
    try
    {
      MultipleClassLoader loader = Application.getPluginLoader().getPlugin(Plugin.class).getResources().getClassLoader();
      ClassFinder finder = loader.getClassFinder();
      Class[] commands = finder.findImplementors(Command.class);
      StringBuffer sb = new StringBuffer();
      
      if (commands != null && commands.length > 0)
      {
        for (int i=0;i<commands.length;++i)
        {
          try
          {
            Command c = (Command) commands[i].newInstance();
            String name = c.getName();
            Logger.debug("register REST command: " + name + ", class: " + c.getClass().getName());
            registry.put(name,c);
            sb.append(name);
            if (i < commands.length - 1)
              sb.append(", ");
          }
          catch (Exception e)
          {
            Logger.error("unable to load command " + commands[i] + " - skipping",e);
          }
        }
        Logger.info("available REST commands: " + sb.toString());
      }
    }
    catch (ClassNotFoundException e)
    {
      Logger.warn("no REST commands found");
    }
  }

  /**
   * @see de.willuhn.datasource.Service#stop(boolean)
   */
  public void stop(boolean arg0) throws RemoteException
  {
    if (!this.isStarted())
    {
      Logger.warn("service not started, skipping request");
      return;
    }
    
    Logger.info("REST service stopped");
    this.registry = null;
  }
}


/*********************************************************************
 * $Log: RestServiceImpl.java,v $
 * Revision 1.3  2008/06/15 22:48:24  willuhn
 * @N Command-Chains
 *
 * Revision 1.2  2008/06/13 15:11:01  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2008/06/13 14:11:04  willuhn
 * @N Mini REST-API
 *
 **********************************************************************/