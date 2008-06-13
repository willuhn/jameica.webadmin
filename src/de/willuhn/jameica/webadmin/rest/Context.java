/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.webadmin/src/de/willuhn/jameica/webadmin/rest/Attic/Context.java,v $
 * $Revision: 1.2 $
 * $Date: 2008/06/13 15:11:01 $
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
  private String params                = null;

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

  /**
   * Liefert zusaetzliche Parameter - insofern vorhanden.
   * @return die zusaetzlichen Parameter.
   */
  public String getParams()
  {
    return this.params;
  }

  /**
   * Speichert zusaetzliche Parameter.
   * @param params zusaetzliche Parameter.
   */
  public void setParams(String params)
  {
    this.params = params;
  }
  
  

}


/*********************************************************************
 * $Log: Context.java,v $
 * Revision 1.2  2008/06/13 15:11:01  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2008/06/13 14:11:04  willuhn
 * @N Mini REST-API
 *
 **********************************************************************/