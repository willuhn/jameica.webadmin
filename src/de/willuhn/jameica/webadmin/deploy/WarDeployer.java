/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.webadmin/src/de/willuhn/jameica/webadmin/deploy/WarDeployer.java,v $
 * $Revision: 1.5 $
 * $Date: 2007/12/04 18:43:27 $
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

import org.mortbay.jetty.Handler;
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
   * @see de.willuhn.jameica.webadmin.deploy.Deployer#deploy()
   */
  public Handler[] deploy()
  {
    List list = Application.getPluginLoader().getInstalledPlugins();

    ArrayList handlers = new ArrayList();

    for (int i=0;i<list.size();++i)
    {
      AbstractPlugin plugin = (AbstractPlugin) list.get(i);
      File dir = new File(plugin.getResources().getPath());

      FileFinder finder = new FileFinder(dir);
      finder.extension("war");
      File[] wars = finder.findRecursive();
      
      if (wars == null || wars.length == 0)
      {
        Logger.debug("no war files found in " + dir.getAbsolutePath());
        continue;
      }

      for (int k=0;k<wars.length;++k)
      {
        final String path    =       wars[k].getAbsolutePath();
        final String context = "/" + wars[k].getName().replaceFirst("\\.war$",""); // ".war" am Ende noch abschneiden 

        Logger.info("deploying " + context + " (" + path + ")");

        try
        {
          handlers.add(new WebAppContext(path,context));
        }
        catch (Exception e)
        {
          Logger.error("unable to deploy " + context, e);
        }
      }
    }
    return (Handler[]) handlers.toArray(new Handler[handlers.size()]);
  }

}


/*********************************************************************
 * $Log: WarDeployer.java,v $
 * Revision 1.5  2007/12/04 18:43:27  willuhn
 * @N Update auf Jetty 6.1.6
 * @N request.getRemoteUser() geht!!
 *
 * Revision 1.4  2007/12/03 23:43:49  willuhn
 * *** empty log message ***
 *
 * Revision 1.3  2007/12/03 19:00:19  willuhn
 * *** empty log message ***
 *
 * Revision 1.2  2007/06/01 09:36:23  willuhn
 * @B war deployer bricht mit der Suche zu frueh ab
 *
 * Revision 1.1  2007/05/15 13:42:36  willuhn
 * @N Deployment von Webapps, WARs fertig und konfigurierbar
 *
 **********************************************************************/