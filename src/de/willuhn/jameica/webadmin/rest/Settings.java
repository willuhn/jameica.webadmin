/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.webadmin/src/de/willuhn/jameica/webadmin/rest/Attic/Settings.java,v $
 * $Revision: 1.1 $
 * $Date: 2008/06/16 22:31:53 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn software & services
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.webadmin.rest;

import java.io.File;

import de.willuhn.jameica.system.Application;
import de.willuhn.jameica.webadmin.Plugin;


/**
 * Ueberschrieben, um die properties-Datei aus dem
 * Plugin-Verzeichnis als System-Preset zu verwenden.
 */
public class Settings extends de.willuhn.util.Settings
{

  /**
   * Erzeugt eine neue Instanz der Settings, die exclusiv
   * nur fuer diese Klasse gelten. Existieren bereits Settings
   * fuer die Klasse, werden sie gleich geladen.
   * @param clazz Klasse, fuer die diese Settings gelten.
   */
  public Settings(Class clazz)
  {
    super(Application.getPluginLoader().getPlugin(Plugin.class).getResources().getPath() + File.separator + "cfg",
          Application.getConfig().getConfigDir(),clazz);
  }
}


/**********************************************************************
 * $Log: Settings.java,v $
 * Revision 1.1  2008/06/16 22:31:53  willuhn
 * @N weitere REST-Kommandos
 *
 **********************************************************************/
