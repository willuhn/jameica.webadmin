/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.webadmin/src/de/willuhn/jameica/webadmin/rest/Plugin.java,v $
 * $Revision: 1.4 $
 * $Date: 2008/10/08 16:01:38 $
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
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;

import de.willuhn.jameica.plugin.Manifest;
import de.willuhn.jameica.system.Application;
import de.willuhn.jameica.webadmin.rest.annotation.Writer;


/**
 * REST-Kommando fuer den Zugriff auf Plugins.
 */
public class Plugin
{
  @Writer
  private PrintWriter writer = null;
  
  /**
   * Listet die installierten Plugins auf.
   * @throws IOException
   */
  public void list() throws IOException
  {
    ArrayList json = new ArrayList();
    List plugins = Application.getPluginLoader().getInstalledManifests();
    for (int i=0;i<plugins.size();++i)
    {
      Manifest mf = (Manifest) plugins.get(i);
      Map data = new HashMap();
      data.put("name",        mf.getName());
      data.put("builddate",   mf.getBuildDate());
      data.put("buildnumber", mf.getBuildnumber());
      data.put("dependencies",mf.getDependencies());
      data.put("description", mf.getDescription());
      data.put("homepage",    mf.getHomepage());
      data.put("license",     mf.getLicense());
      data.put("pluginclass", mf.getPluginClass());
      data.put("plugindir",   mf.getPluginDir());
      data.put("url",         mf.getURL());
      data.put("version",     Double.toString(mf.getVersion()));
      json.add(data);
    }
    writer.print(new JSONArray(json).toString());
  }
}


/**********************************************************************
 * $Log: Plugin.java,v $
 * Revision 1.4  2008/10/08 16:01:38  willuhn
 * @N REST-Services via Injection (mittels Annotation) mit Context-Daten befuellen
 *
 * Revision 1.3  2008/06/16 22:31:53  willuhn
 * @N weitere REST-Kommandos
 *
 **********************************************************************/
