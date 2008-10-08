/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.webadmin/src/de/willuhn/jameica/webadmin/rest/Echo.java,v $
 * $Revision: 1.3 $
 * $Date: 2008/10/08 16:01:38 $
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
import java.io.PrintWriter;

import org.json.JSONException;
import org.json.JSONObject;

import de.willuhn.jameica.webadmin.rest.annotation.Writer;
import de.willuhn.logging.Logger;

/**
 * Test-Command, welches den uebergebenen QueryString zurueckschickt.
 * Schreibt die uebergebene Nachricht ins lokale Log.
 */
public class Echo
{
  @Writer
  private PrintWriter writer = null;

  /**
   * Fuehrt das Echo aus.
   * @param echo zurueckzuliefernder Text.
   * @throws IOException
   */
  public void echo(String echo) throws IOException
  {
    try
    {
      writer.print(new JSONObject().put("echo",echo).toString());
    }
    catch (JSONException e)
    {
      Logger.error("unable to encode via json",e);
      throw new IOException("error while encoding into json");
    }
  }
}


/*********************************************************************
 * $Log: Echo.java,v $
 * Revision 1.3  2008/10/08 16:01:38  willuhn
 * @N REST-Services via Injection (mittels Annotation) mit Context-Daten befuellen
 *
 * Revision 1.2  2008/06/16 14:22:11  willuhn
 * @N Mapping der REST-URLs via Property-Datei
 *
 * Revision 1.1  2008/06/13 14:11:04  willuhn
 * @N Mini REST-API
 *
 **********************************************************************/