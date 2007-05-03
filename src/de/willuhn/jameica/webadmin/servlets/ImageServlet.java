/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.webadmin/src/de/willuhn/jameica/webadmin/servlets/Attic/ImageServlet.java,v $
 * $Revision: 1.2 $
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



/**
 * Servlet zum Laden von Bildern aus den Resource-Verzeichnissen.
 */
public class ImageServlet extends ResourceServlet
{
  

  /**
   * @see de.willuhn.jameica.webadmin.servlets.AbstractResourceServlet#getPath()
   */
  protected String getPath()
  {
    return "img";
  }
}


/**********************************************************************
 * $Log: ImageServlet.java,v $
 * Revision 1.2  2007/05/03 23:39:52  willuhn
 * @N Vorbereitungen fuer Integration von GWT (Google Web Toolkit)
 *
 * Revision 1.1  2007/04/16 00:12:39  willuhn
 * @N Image-Handler
 *
 **********************************************************************/
