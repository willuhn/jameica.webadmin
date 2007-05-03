/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.webadmin/src/de/willuhn/jameica/webadmin/servlets/Attic/ResourceServlet.java,v $
 * $Revision: 1.1 $
 * $Date: 2007/05/03 23:39:52 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn software & services
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.webadmin.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;


/**
 * Servlet zum Laden von Ressourcen aus dem Verzeichnis "res".
 */
public class ResourceServlet extends HttpServlet
{
  /**
   * Liefert den Pfad, aus dem gelesen werden soll.
   * Kann ueberschrieben werden.
   * @return der Pfad, aus dem gelesen werden soll.
   */
  protected String getPath()
  {
    return "res";
  }

  /**
   * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
   */
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {
    String name = request.getQueryString();

    if (name == null || name.length() == 0)
      throw new IOException("no filename given");
    
    InputStream is = null;
    
    String path = getPath() + "/" + name;
    
    try
    {
      is = Application.getClassLoader().getResourceAsStream(path);
      if (is == null)
      {
        Logger.warn("404: " + path);
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
        return;
      }
      
      OutputStream os = response.getOutputStream();
      
      byte[] buffer = new byte[4096];
      int read = 0;
      
      do
      {
        read = is.read(buffer);
        if (read > 0)
          os.write(buffer,0,read);
      }
      while (read > 0);
      
      os.flush();
      Logger.debug("200: " + path);
    }
    finally
    {
      if (is != null)
        is.close();
    }
  }

}


/**********************************************************************
 * $Log: ResourceServlet.java,v $
 * Revision 1.1  2007/05/03 23:39:52  willuhn
 * @N Vorbereitungen fuer Integration von GWT (Google Web Toolkit)
 *
 **********************************************************************/
