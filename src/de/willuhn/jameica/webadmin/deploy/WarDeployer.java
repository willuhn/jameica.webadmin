/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.webadmin/src/de/willuhn/jameica/webadmin/deploy/WarDeployer.java,v $
 * $Revision: 1.1 $
 * $Date: 2007/05/15 13:42:36 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn software & services
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.webadmin.deploy;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.mortbay.jetty.HandlerContainer;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.ContextHandler;
import org.mortbay.jetty.webapp.WebAppContext;

import de.willuhn.io.FileFinder;
import de.willuhn.jameica.plugin.AbstractPlugin;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;

/**
 * Durchsucht alle Plugins nach WAR-Dateien und deployed sie.
 */
public class WarDeployer implements Deployer
{

  /**
   * @see de.willuhn.jameica.webadmin.deploy.Deployer#deploy(org.mortbay.jetty.Server, org.mortbay.jetty.HandlerContainer)
   */
  public void deploy(Server server, HandlerContainer container)
  {
    ArrayList dirs = new ArrayList();
    
    // Erstmal alle Plugins ermitteln
    List list = Application.getPluginLoader().getInstalledPlugins();
    for (int i=0;i<list.size();++i)
    {
      AbstractPlugin plugin = (AbstractPlugin) list.get(i);
      dirs.add(new File(plugin.getResources().getPath()));
    }

    for (int i=0;i<dirs.size();++i)
    {
      File dir = (File) dirs.get(i);

      FileFinder finder = new FileFinder(dir);
      finder.extension("war");
      File[] wars = finder.findRecursive();
      
      if (wars == null || wars.length == 0)
      {
        Logger.info("no war files found in " + dir.getAbsolutePath());
        return;
      }

      for (int k=0;k<wars.length;++k)
      {
        final String path    =       wars[k].getAbsolutePath();
        final String context = "/" + wars[k].getName().replaceFirst("\\.war$",""); // ".war" am Ende noch abschneiden 

        Logger.info("deploying " + context + " (" + path + ")");

        try
        {
          ContextHandler app = new WebAppContext(path,context);
          container.addHandler(app);
        }
        catch (Exception e)
        {
          Logger.error("unable to deploy " + context, e);
        }
      }
    }
  }

}


/*********************************************************************
 * $Log: WarDeployer.java,v $
 * Revision 1.1  2007/05/15 13:42:36  willuhn
 * @N Deployment von Webapps, WARs fertig und konfigurierbar
 *
 **********************************************************************/