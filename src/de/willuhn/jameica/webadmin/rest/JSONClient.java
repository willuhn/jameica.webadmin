/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.webadmin/src/de/willuhn/jameica/webadmin/rest/Attic/JSONClient.java,v $
 * $Revision: 1.1 $
 * $Date: 2008/06/16 23:27:28 $
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
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONTokener;

import de.willuhn.logging.Logger;


/**
 * Fuehrt JSON-Aufrufe an localhost oder fremde Rechner durch.
 */
public class JSONClient
{
  public static String execute(String url, final String username, final String password) throws IOException
  {
    try
    {
      HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection(); 
      connection.setDoOutput(true);
      Authenticator.setDefault(new Authenticator() {
        protected PasswordAuthentication getPasswordAuthentication() {
          return new PasswordAuthentication(username,password.toCharArray());
        }
      });
  
      // Response
      StringBuffer builder = new StringBuffer(1024);
      char[] buffer = new char[1024];
      Reader reader = new InputStreamReader(connection.getInputStream());
      while (true)
      {
        int bytesRead = reader.read(buffer);
        if (bytesRead < 0)
          break;
        builder.append(buffer, 0, bytesRead);
      }
      reader.close();
      JSONTokener tokener = new JSONTokener(builder.toString());
      Object response = tokener.nextValue();
      if (response == null)
        throw new IOException("invalid JSON response");
      return response.toString();
    }
    catch (JSONException ex)
    {
      Logger.error("unable to execute JSON request",ex);
      throw new IOException("unable to execute JSON request");
    }
  }
}


/**********************************************************************
 * $Log: JSONClient.java,v $
 * Revision 1.1  2008/06/16 23:27:28  willuhn
 * @N JSON-Client-Code zum Abfragen der Daten von anderen Jameica-Servern
 *
 **********************************************************************/
