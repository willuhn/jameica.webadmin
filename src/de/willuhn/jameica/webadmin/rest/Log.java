/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.webadmin/src/de/willuhn/jameica/webadmin/rest/Log.java,v $
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

import de.willuhn.logging.Level;
import de.willuhn.logging.Logger;

/**
 * Logger-Command.
 * Schreibt die uebergebene Nachricht ins lokale Log.
 */
public class Log implements Command
{
  /**
   * @see de.willuhn.jameica.webadmin.rest.Command#getName()
   */
  public String getName()
  {
    return "log";
  }

  /**
   * @see de.willuhn.jameica.webadmin.rest.Command#execute(de.willuhn.jameica.webadmin.rest.Context)
   */
  public void execute(Context context) throws IOException
  {
    HttpServletRequest request = context.getRequest();

    // Ohne Text koennen wir gleich aufhoeren
    String text = request.getParameter("text");
    if (text == null || text.length() == 0)
      return;

    String level  = request.getParameter("level");
    String host   = request.getRemoteHost();
    String clazz  = request.getParameter("class");
    String method = request.getParameter("method");

    Level l = Level.findByName(level != null ? level : Level.DEFAULT.getName());
    if (l == null) l = Level.DEFAULT;
    
    Logger.write(l,host,clazz,method,text,null);
  }
}


/*********************************************************************
 * $Log: Log.java,v $
 * Revision 1.1  2008/06/13 14:11:04  willuhn
 * @N Mini REST-API
 *
 **********************************************************************/