/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.webadmin/src/de/willuhn/jameica/webadmin/servlets/Attic/ResourceServlet.java,v $
 * $Revision: 1.2 $
 * $Date: 2007/05/07 22:21:28 $
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
   * Liefert Pfad und Resource der zu ladenden Ressource.
   * Sie muss sich mittels im Classpath befinden, um gefunden zu werden.
   * @param request
   * @return
   */
  protected String getResource(HttpServletRequest request)
  {
    return request.getRequestURI();
  }

  /**
   * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
   */
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {
    InputStream is = null;
    
    try
    {
      String name = getResource(request);
      
      // Wir entfernen ggf. den Slash
      if (name.startsWith("/"))
        name = name.substring(1);
      
      is = Application.getClassLoader().getResourceAsStream(name);
      if (is == null)
      {
        Logger.warn("404: unable to find " + name);
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
 * Revision 1.2  2007/05/07 22:21:28  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2007/05/03 23:39:52  willuhn
 * @N Vorbereitungen fuer Integration von GWT (Google Web Toolkit)
 *
 **********************************************************************/
