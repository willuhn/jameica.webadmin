/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.webadmin/src/de/willuhn/jameica/webadmin/servlets/Attic/ImageServlet.java,v $
 * $Revision: 1.1 $
 * $Date: 2007/04/16 00:12:39 $
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


/**
 * Servlet zum Laden von Bildern aus den Resource-Verzeichnissen.
 */
public class ImageServlet extends HttpServlet
{

  /**
   * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
   */
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {
    String name = request.getQueryString();
    String fav  = request.getServletPath();
    
    if (fav != null && fav.equals("/favicon.ico"))
      name = "hibiscus-icon-64x64.png";

    if (name == null || name.length() == 0)
      throw new IOException("no filename given");
    
    // Wir laden aus Sicherheitsgruenden hart alles aus den "img"-Verzeichnissen
    InputStream is = null;
    
    try
    {
      is = Application.getClassLoader().getResourceAsStream("img/" + name);
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
 * $Log: ImageServlet.java,v $
 * Revision 1.1  2007/04/16 00:12:39  willuhn
 * @N Image-Handler
 *
 **********************************************************************/
