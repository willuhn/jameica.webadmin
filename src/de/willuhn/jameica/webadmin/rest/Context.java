/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.webadmin/src/de/willuhn/jameica/webadmin/rest/Attic/Context.java,v $
 * $Revision: 1.4 $
 * $Date: 2008/06/16 14:22:11 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn software & services
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.webadmin.rest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**Kommando-Context.
 */
public class Context
{
  private HttpServletRequest request   = null;
  private HttpServletResponse response = null;

  /**
   * ct
   * @param request
   * @param response
   */
  public Context(HttpServletRequest request, HttpServletResponse response)
  {
    this.request  = request;
    this.response = response;
  }

  /**
   * Liefert den Request.
   * @return der Request.
   */
  public HttpServletRequest getRequest()
  {
    return this.request;
  }

  /**
   * Liefert den Response.
   * @return der Response.
   */
  public HttpServletResponse getResponse()
  {
    return this.response;
  }
}


/*********************************************************************
 * $Log: Context.java,v $
 * Revision 1.4  2008/06/16 14:22:11  willuhn
 * @N Mapping der REST-URLs via Property-Datei
 *
 * Revision 1.3  2008/06/15 22:48:23  willuhn
 * @N Command-Chains
 *
 * Revision 1.2  2008/06/13 15:11:01  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2008/06/13 14:11:04  willuhn
 * @N Mini REST-API
 *
 **********************************************************************/