/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.webadmin/src/de/willuhn/jameica/webadmin/servlets/Attic/GwtServlet.java,v $
 * $Revision: 1.1 $
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

import javax.servlet.http.HttpServletRequest;


/**
 * 
 */
public class GwtServlet extends ResourceServlet
{
  private static String gwt = "res/gwt/de.willuhn.jameica.webadmin.gwt.WebAdmin";

  /**
   * @see de.willuhn.jameica.webadmin.servlets.ResourceServlet#getResource(javax.servlet.http.HttpServletRequest)
   */
  protected String getResource(HttpServletRequest request)
  {
    String name = super.getResource(request);
    if (name == null || name.length() == 0 || name.equals("/"))
      name = "WebAdmin.html";
    return gwt + "/" + name;
  }

}


/**********************************************************************
 * $Log: GwtServlet.java,v $
 * Revision 1.1  2007/05/07 22:21:28  willuhn
 * *** empty log message ***
 *
 **********************************************************************/
