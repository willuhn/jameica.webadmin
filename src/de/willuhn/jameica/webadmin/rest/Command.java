/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.webadmin/src/de/willuhn/jameica/webadmin/rest/Attic/Command.java,v $
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

/**
 * Interface fuer ein REST-Kommando.
 */
public interface Command
{
  /**
   * Fuehrt das Kommando aus.
   * @param context
   * @throws IOException
   */
  public void execute(Context context) throws IOException;
  
  /**
   * Liefert den Namen, unter dem das Kommando erreichbar sein soll.
   * @return Name des Kommandos.
   */
  public String getName();
}


/*********************************************************************
 * $Log: Command.java,v $
 * Revision 1.1  2008/06/13 14:11:04  willuhn
 * @N Mini REST-API
 *
 **********************************************************************/