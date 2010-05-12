/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.webadmin/src/de/willuhn/jameica/webadmin/rest/Plugin.java,v $
 * $Revision: 1.14 $
 * $Date: 2010/05/12 10:59:20 $
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;

import de.willuhn.jameica.plugin.Dependency;
import de.willuhn.jameica.plugin.Manifest;
import de.willuhn.jameica.system.Application;
import de.willuhn.jameica.webadmin.annotation.Doc;
import de.willuhn.jameica.webadmin.annotation.Path;


/**
 * REST-Kommando fuer den Zugriff auf Plugins.
 */
@Doc("System: Liefert Informationen über die installierten Plugins")
public class Plugin implements AutoRestBean
{
  /**
   * Schreibt die installierten Plugins in den Response-Writer.
   * @return die installierten Plugins.
   * @throws IOException
   */
  @Doc(value="Liefert eine Liste der installierten Plugins",
       example="plugins/list")
  @Path("/plugins/list$")
  public JSONArray list() throws IOException
  {
    return new JSONArray(getList());
  }
  
  /**
   * Listet die installierten Plugins auf.
   * @throws IOException
   */
  public List getList()
  {
    ArrayList list = new ArrayList();
    List plugins = Application.getPluginLoader().getInstalledManifests();
    for (int i=0;i<plugins.size();++i)
    {
      Manifest mf = (Manifest) plugins.get(i);
      Map data = new HashMap();
      Object version = mf.getVersion();
      data.put("name",        notNull(mf.getName()));
      data.put("builddate",   notNull(mf.getBuildDate()));
      data.put("buildnumber", notNull(mf.getBuildnumber()));
      data.put("description", notNull(mf.getDescription()));
      data.put("homepage",    notNull(mf.getHomepage()));
      data.put("license",     notNull(mf.getLicense()));
      data.put("pluginclass", notNull(mf.getPluginClass()));
      data.put("plugindir",   notNull(mf.getPluginDir()));
      data.put("url",         notNull(mf.getURL()));
      data.put("version",     version.toString());

      ArrayList deps = new ArrayList();
      Dependency[] d = mf.getDependencies();
      if (d != null && d.length > 0)
      {
        for (int k=0;k<d.length;++k)
        {
          Map dep = new HashMap();
          dep.put("name",d[k].getName());
          dep.put("version",d[k].getVersion());
          deps.add(dep);
        }
      }
      data.put("dependencies",deps);
      
      list.add(data);
    }
    return list;
  }

  
  /**
   * Liefert einen Leerstring, wenn s = NULL.
   * @param s String.
   * @return String s oder Leerstring, niemals NULL.
   */
  private String notNull(String s)
  {
    return s == null ? "" : s;
  }
}


/**********************************************************************
 * $Log: Plugin.java,v $
 * Revision 1.14  2010/05/12 10:59:20  willuhn
 * @N Automatische Dokumentations-Seite fuer die REST-Beans basierend auf der Annotation "Doc"
 *
 * Revision 1.13  2010/05/11 14:59:48  willuhn
 * @N Automatisches Deployment von REST-Beans
 *
 * Revision 1.12  2010/03/18 09:29:35  willuhn
 * @N Wenn REST-Beans Rueckgabe-Werte liefern, werrden sie automatisch als toString() in den Response-Writer geschrieben
 *
 * Revision 1.11  2009/08/05 09:03:40  willuhn
 * @C Annotations in eigenes Package verschoben (sind nicht mehr REST-spezifisch)
 *
 * Revision 1.10  2009/01/06 23:26:52  willuhn
 * @C Jameica 1.7 compatibility
 *
 * Revision 1.9  2008/12/30 15:24:37  willuhn
 * @N Umstellung auf neue Versionierung
 *
 * Revision 1.8  2008/11/11 23:59:22  willuhn
 * @N Dualer Aufruf (via JSON und Map/List)
 *
 * Revision 1.7  2008/11/11 01:06:22  willuhn
 * @N Mehr REST-Kommandos
 *
 * Revision 1.6  2008/10/21 22:33:47  willuhn
 * @N Markieren der zu registrierenden REST-Kommandos via Annotation
 *
 * Revision 1.5  2008/10/08 21:38:23  willuhn
 * @C Nur noch zwei Annotations "Request" und "Response"
 *
 * Revision 1.4  2008/10/08 16:01:38  willuhn
 * @N REST-Services via Injection (mittels Annotation) mit Context-Daten befuellen
 *
 * Revision 1.3  2008/06/16 22:31:53  willuhn
 * @N weitere REST-Kommandos
 *
 **********************************************************************/
