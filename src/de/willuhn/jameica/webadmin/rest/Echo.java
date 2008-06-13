/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.webadmin/src/de/willuhn/jameica/webadmin/rest/Echo.java,v $
 * $Revision: 1.1 $
 * $Date: 2008/06/13 14:11:04 $
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Test-Command, welches den uebergebenen QueryString zurueckschickt.
 * Schreibt die uebergebene Nachricht ins lokale Log.
 */
public class Echo implements Command
{
  /**
   * @see de.willuhn.jameica.webadmin.rest.Command#getName()
   */
  public String getName()
  {
    return "echo";
  }

  /**
   * @see de.willuhn.jameica.webadmin.rest.Command#execute(de.willuhn.jameica.webadmin.rest.Context)
   */
  public void execute(Context context) throws IOException
  {
    HttpServletRequest request   = context.getRequest();
    HttpServletResponse response = context.getResponse();
   
    String query = request.getQueryString();
    response.getWriter().print("QUERY-STRING: " + (query == null ? "<none>" : query));
  }
}


/*********************************************************************
 * $Log: Echo.java,v $
 * Revision 1.1  2008/06/13 14:11:04  willuhn
 * @N Mini REST-API
 *
 **********************************************************************/