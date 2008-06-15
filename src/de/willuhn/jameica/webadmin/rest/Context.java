/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.webadmin/src/de/willuhn/jameica/webadmin/rest/Attic/Context.java,v $
 * $Revision: 1.3 $
 * $Date: 2008/06/15 22:48:23 $
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
  private Command parent               = null;
  private String parameter             = null;

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
   * Liefert einen zusaetzlichen Parameter - insofern vorhanden.
   * @return der zusaetzliche Parameter.
   */
  public String getParameter()
  {
    return this.parameter;
  }

  /**
   * Speichert einen zusaetzlichen Parameter.
   * @param params zusaetzlicher Parameter.
   */
  public void setParameter(String parameter)
  {
    this.parameter = parameter;
  }

  /**
   * Liefert das vorherige Kommando, insofern eines existierte.
   * @return das vorherige Kommando oder <code>null</code>.
   */
  public Command getParent()
  {
    return this.parent;
  }
  
  /**
   * Speichert das vorherige Kommando.
   * @param parent das vorherige Kommando.
   */
  public void setParent(Command parent)
  {
    this.parent = parent;
  }
  

}


/*********************************************************************
 * $Log: Context.java,v $
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