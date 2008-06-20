/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.webadmin/src/de/willuhn/jameica/webadmin/rest/Attic/JSONClient.java,v $
 * $Revision: 1.2 $
 * $Date: 2008/06/20 15:07:12 $
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import de.willuhn.logging.Logger;


/**
 * Fuehrt JSON-Aufrufe an localhost oder fremde Rechner durch.
 */
public class JSONClient
{
  /**
   * @param url
   * @param username
   * @param password
   * @return json-Daten.
   * @throws IOException
   */
  private static String execute(String url, final String username, final String password) throws IOException
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
  
  /**
   * @param url
   * @param username
   * @param password
   * @return
   * @throws Exception
   */
  public static Collection asList(String url, String username, String password) throws Exception
  {
    JSONArray array = new JSONArray(execute(url,username,password));
    ArrayList result = new ArrayList();
    for (int i=0;i<array.length();++i)
    {
      result.add(toMap(array.getJSONObject(i)));
    }
    return result;
  }
  
  public static Map toMap(JSONObject object) throws JSONException
  {
    Map map = new HashMap();
    Iterator  keys = object.keys();
    while (keys.hasNext()) {
      String key = (String) keys.next();
      map.put(key,object.get(key));
    }
    return map;
  }
}


/**********************************************************************
 * $Log: JSONClient.java,v $
 * Revision 1.2  2008/06/20 15:07:12  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2008/06/16 23:27:28  willuhn
 * @N JSON-Client-Code zum Abfragen der Daten von anderen Jameica-Servern
 *
 **********************************************************************/
