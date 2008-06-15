/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.webadmin/src/de/willuhn/jameica/webadmin/rest/Plugin.java,v $
 * $Revision: 1.1 $
 * $Date: 2008/06/15 22:48:23 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn software & services
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.webadmin.rest;

import java.io.IOException;
import java.util.List;

import de.willuhn.jameica.plugin.AbstractPlugin;
import de.willuhn.jameica.system.Application;


/**
 * REST-Kommando fuer Plugins.
 * 
 */
public class Plugin implements Command
{
  private AbstractPlugin plugin = null;
  
  /**
   * @see de.willuhn.jameica.webadmin.rest.Command#execute(de.willuhn.jameica.webadmin.rest.Context)
   */
  public void execute(Context context) throws IOException
  {
    String pluginName = context.getParameter();
    
    // Plugin suchen
    List list = Application.getPluginLoader().getInstalledPlugins();
    StringBuffer names = new StringBuffer();
    
    for (int i=0;i<list.size();++i)
    {
      AbstractPlugin p = (AbstractPlugin) list.get(i);
      String name = p.getManifest().getName();
      if (name == null)
        continue;

      // Wenn ein Plugin angegeben wurde, brechen wir hier ab
      if (pluginName != null && name.equals(pluginName))
      {
        this.plugin = p;
        return;
      }

      names.append(name);
      if (i<list.size()-1)
        names.append(",");
    }
  
    // Kein Plugin angegeben? Dann auflisten
    context.getResponse().getWriter().print(names.toString());
  }
  
  /**
   * Liefert das ausgewaehlte Plugin.
   * @return das ausgewaehlte Plugin.
   */
  AbstractPlugin getPlugin()
  {
    return this.plugin;
  }

  /**
   * @see de.willuhn.jameica.webadmin.rest.Command#getName()
   */
  public String getName()
  {
    return "plugins";
  }

}


/**********************************************************************
 * $Log: Plugin.java,v $
 * Revision 1.1  2008/06/15 22:48:23  willuhn
 * @N Command-Chains
 *
 **********************************************************************/
