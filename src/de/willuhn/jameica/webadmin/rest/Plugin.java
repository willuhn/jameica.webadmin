/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.webadmin/src/de/willuhn/jameica/webadmin/rest/Plugin.java,v $
 * $Revision: 1.9 $
 * $Date: 2008/12/30 15:24:37 $
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

import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;

import de.willuhn.jameica.plugin.Dependency;
import de.willuhn.jameica.plugin.Manifest;
import de.willuhn.jameica.system.Application;
import de.willuhn.jameica.webadmin.rest.annotation.Path;
import de.willuhn.jameica.webadmin.rest.annotation.Response;


/**
 * REST-Kommando fuer den Zugriff auf Plugins.
 */
public class Plugin
{
  @Response
  private HttpServletResponse response = null;
  
  /**
   * Schreibt die installierten Plugins in den Response-Writer.
   * @throws IOException
   */
  @Path("/plugins/list$")
  public void list() throws IOException
  {
    response.getWriter().print(new JSONArray(getList()).toString());
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
      data.put("name",        notNull(mf.getName()));
      data.put("builddate",   notNull(mf.getBuildDate()));
      data.put("buildnumber", notNull(mf.getBuildnumber()));
      data.put("description", notNull(mf.getDescription()));
      data.put("homepage",    notNull(mf.getHomepage()));
      data.put("license",     notNull(mf.getLicense()));
      data.put("pluginclass", notNull(mf.getPluginClass()));
      data.put("plugindir",   notNull(mf.getPluginDir()));
      data.put("url",         notNull(mf.getURL()));
      data.put("version",     mf.getVersion().toString());

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
