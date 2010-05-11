/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.webadmin/src/de/willuhn/jameica/webadmin/rest/Echo.java,v $
 * $Revision: 1.8 $
 * $Date: 2010/05/11 14:59:48 $
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

import org.json.JSONException;
import org.json.JSONObject;

import de.willuhn.jameica.webadmin.annotation.Path;
import de.willuhn.logging.Logger;

/**
 * Test-Command, welches den uebergebenen QueryString zurueckschickt.
 * Schreibt die uebergebene Nachricht ins lokale Log.
 */
public class Echo implements AutoRestBean
{
  /**
   * Fuehrt das Echo aus.
   * @param echo zurueckzuliefernder Text.
   * @return das Echo-Response.
   * @throws IOException
   */
  @Path("/echo/(.*)")
  public JSONObject echo(String echo) throws IOException
  {
    try
    {
      return new JSONObject().put("echo",echo);
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
 * Revision 1.8  2010/05/11 14:59:48  willuhn
 * @N Automatisches Deployment von REST-Beans
 *
 * Revision 1.7  2010/03/18 09:29:35  willuhn
 * @N Wenn REST-Beans Rueckgabe-Werte liefern, werrden sie automatisch als toString() in den Response-Writer geschrieben
 *
 * Revision 1.6  2009/08/05 09:03:40  willuhn
 * @C Annotations in eigenes Package verschoben (sind nicht mehr REST-spezifisch)
 *
 * Revision 1.5  2008/10/21 22:33:47  willuhn
 * @N Markieren der zu registrierenden REST-Kommandos via Annotation
 *
 * Revision 1.4  2008/10/08 21:38:23  willuhn
 * @C Nur noch zwei Annotations "Request" und "Response"
 *
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