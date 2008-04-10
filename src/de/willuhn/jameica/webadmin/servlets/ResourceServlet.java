/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.webadmin/src/de/willuhn/jameica/webadmin/servlets/Attic/ResourceServlet.java,v $
 * $Revision: 1.4 $
 * $Date: 2008/04/10 13:02:29 $
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
   * @return den Pfad zur Ressource.
   */
  protected String getResource(HttpServletRequest request)
  {
    return request.getServletPath() + request.getPathInfo();
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
        Logger.warn(HttpServletResponse.SC_NOT_FOUND + ": unable to find " + name);
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
 * Revision 1.4  2008/04/10 13:02:29  willuhn
 * @N Zweischritt-Deployment. Der Server wird zwar sofort initialisiert, wenn der Jameica-Service startet, gestartet wird er aber erst, wenn die ersten Handler resgistriert werden
 * @N damit koennen auch nachtraeglich zur Laufzeit weitere Handler hinzu registriert werden
 * @R separater Worker in HttpServiceImpl entfernt. Der Classloader wird nun direkt von den Deployern gesetzt. Das ist wichtig, da Jetty fuer die Webanwendungen sonst den System-Classloader nutzt, welcher die Plugins nicht kennt
 *
 * Revision 1.3  2007/05/15 11:21:12  willuhn
 * *** empty log message ***
 *
 * Revision 1.2  2007/05/07 22:21:28  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2007/05/03 23:39:52  willuhn
 * @N Vorbereitungen fuer Integration von GWT (Google Web Toolkit)
 *
 **********************************************************************/
