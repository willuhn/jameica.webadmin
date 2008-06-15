/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.webadmin/src/de/willuhn/jameica/webadmin/rest/Attic/Status.java,v $
 * $Revision: 1.1 $
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

import java.io.IOException;


/**
 * REST-Kommando fuer einen Service-Status.
 */
public class Status implements Command
{

  /**
   * @see de.willuhn.jameica.webadmin.rest.Command#execute(de.willuhn.jameica.webadmin.rest.Context)
   */
  public void execute(Context context) throws IOException
  {
    Command prev = context.getParent();
    if (prev == null || !(prev instanceof Service))
      throw new IOException("no service given");
    
    de.willuhn.datasource.Service s = ((Service)prev).getService();
    
    context.getResponse().getWriter().print(s.isStarted() ? "started" : "not started");
  }

  /**
   * @see de.willuhn.jameica.webadmin.rest.Command#getName()
   */
  public String getName()
  {
    return "status";
  }

}


/**********************************************************************
 * $Log: Status.java,v $
 * Revision 1.1  2008/06/15 22:48:23  willuhn
 * @N Command-Chains
 *
 **********************************************************************/
