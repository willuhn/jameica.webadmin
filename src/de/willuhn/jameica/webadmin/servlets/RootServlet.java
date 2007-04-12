/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.webadmin/src/de/willuhn/jameica/webadmin/servlets/Attic/RootServlet.java,v $
 * $Revision: 1.1 $
 * $Date: 2007/04/12 00:02:56 $
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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;


/**
 * Root-Servlet des Webservers.
 */
public class RootServlet extends HttpServlet
{
  private VelocityContext context = new VelocityContext();
  
  /**
   * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
   */
  protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {
    response.setContentType("text/html");

    try
    {
      Template template = Velocity.getTemplate("index.vm");
      template.merge(context,response.getWriter());
    }
    catch (IOException ioe)
    {
      throw ioe;
    }
    catch (ServletException se)
    {
      throw se;
    }
    catch (Exception e)
    {
      throw new ServletException(e);
    }
  }

}


/**********************************************************************
 * $Log: RootServlet.java,v $
 * Revision 1.1  2007/04/12 00:02:56  willuhn
 * @C replaced winstone with jetty (because of ssl support via custom socketfactory)
 *
 **********************************************************************/
