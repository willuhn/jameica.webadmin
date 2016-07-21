/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.webadmin/src/de/willuhn/jameica/webadmin/deploy/WarDeployer.java,v $
 * $Revision: 1.11 $
 * $Date: 2012/03/29 21:11:30 $
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
import de.willuhn.jameica.system.Application;
import de.willuhn.jameica.webadmin.Plugin;
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
    List<Config> wars = new ArrayList<Config>();

    ////////////////////////////////////////////////////////////////////////////
    // 1. Wir suchen nach War-Datein in ~/.jameica/jameica.webadmin/webapps
    {
      String work = Application.getPluginLoader().getPlugin(Plugin.class).getResources().getWorkPath();
      File dir = new File(work,"webapps");

      FileFinder finder = new FileFinder(dir);
      finder.extension("war");
      File[] files = finder.findRecursive();
      for (File f:files)
      {
        wars.add(new Config(f,null));
      }
    }
    ////////////////////////////////////////////////////////////////////////////
    

    
    ////////////////////////////////////////////////////////////////////////////
    // 2. Wir suchen in den Plugin-Verzeichnissen

    List<de.willuhn.jameica.plugin.Plugin> list = Application.getPluginLoader().getInstalledPlugins();

    for (int i=0;i<list.size();++i)
    {
      de.willuhn.jameica.plugin.Plugin plugin = list.get(i);
      File dir = new File(plugin.getManifest().getPluginDir());

      FileFinder finder = new FileFinder(dir);
      finder.extension("war");
      File[] files = finder.findRecursive();
      for (File f:files)
      {
        wars.add(new Config(f,plugin));
      }
    }
    ////////////////////////////////////////////////////////////////////////////

    if (wars.size() == 0)
    {
      Logger.info("no war files found");
      return null;
    }

    List<Handler> handlers = new ArrayList<Handler>();

    for (Config c:wars)
    {
      final String path    = c.file.getAbsolutePath();
      final String context = "/" + c.file.getName().replaceFirst("\\.war$",""); // ".war" am Ende noch abschneiden 

      Logger.info("deploying " + context + " (" + path + ")");

      try
      {
        final WebAppContext ctx = new WebAppContext(path,context);

        // Classloader explizit angeben. Sonst verwendet Jetty den System-Classloader, der nichts kennt
        if (c.plugin != null)
          ctx.setClassLoader(c.plugin.getManifest().getClassLoader());

        handlers.add(ctx);
      }
      catch (Exception e)
      {
        Logger.error("unable to deploy " + context, e);
      }
    }
    return handlers.toArray(new Handler[handlers.size()]);
  }
  
  /**
   * Hilfsklasse, um WAR-Datei und Plugin zusammenzuhalten.
   */
  private class Config
  {
    private File file = null;
    private de.willuhn.jameica.plugin.Plugin plugin = null;
    
    /**
     * ct.
     * @param file
     * @param plugin
     */
    private Config(File file, de.willuhn.jameica.plugin.Plugin plugin)
    {
      this.file = file;
      this.plugin = plugin;
    }
  }
}
