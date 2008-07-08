/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.webadmin/src/de/willuhn/jameica/webadmin/JSONClient.java,v $
 * $Revision: 1.2 $
 * $Date: 2008/07/08 14:26:59 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn software & services
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.webadmin;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import de.willuhn.jameica.messaging.CheckTrustMessage;
import de.willuhn.jameica.messaging.Message;
import de.willuhn.jameica.messaging.MessageConsumer;
import de.willuhn.jameica.system.Application;
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
   * @throws Exception
   */
  private static String execute(String url, String restCommand) throws Exception
  {
    AutoTrust trust = null;
    try
    {
      if (url.startsWith("https://"))
      {
        trust = new AutoTrust(url);
        Application.getMessagingFactory().registerMessageConsumer(trust);
      }
      HttpURLConnection connection = (HttpURLConnection) new URL(url + restCommand).openConnection(); 
      connection.setDoOutput(true);
      
      final String password = Settings.getJameicaInstancePassword(url);
      if (password != null && password.length() > 0)
      {
        Authenticator.setDefault(new Authenticator() {
          protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication("admin",password.toCharArray());
          }
        });
      }
  
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
    finally
    {
      if (trust != null)
        Application.getMessagingFactory().unRegisterMessageConsumer(trust);
    }
  }
  
  /**
   * Ruft einen JSON-Service auf und liefert das Ergebnis als Liste von Objekten zurueck.
   * @param url Basis-URL des JSON-Services.
   * @param restCommand das REST-Kommando.
   * @return Liste der Datensaetze.
   * @throws Exception
   */
  public static List asList(String url, String restCommand) throws Exception
  {
    JSONArray array = new JSONArray(execute(url, restCommand));
    ArrayList result = new ArrayList();
    for (int i=0;i<array.length();++i)
    {
      result.add(toMap(array.getJSONObject(i)));
    }
    return result;
  }
  
  /**
   * Konvertiert ein JSON-Objekt in eine Map.
   * @param object JSON-Objekt.
   * @return Java-Map.
   * @throws JSONException
   */
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
  
  /**
   * Gewaehrleistet die automatische Vertrauensstellung von fremden
   * Jameica-Zertifikaten, wenn sich der Admin explizit dorthin verbindet. 
   */
  private static class AutoTrust implements MessageConsumer
  {
    private String url = null;
    
    /**
     * ct
     * @param url
     */
    private AutoTrust(String url)
    {
      this.url = url;
    }

    /**
     * @see de.willuhn.jameica.messaging.MessageConsumer#autoRegister()
     */
    public boolean autoRegister()
    {
      return false;
    }

    /**
     * @see de.willuhn.jameica.messaging.MessageConsumer#getExpectedMessageTypes()
     */
    public Class[] getExpectedMessageTypes()
    {
      return new Class[]{CheckTrustMessage.class};
    }

    /**
     * @see de.willuhn.jameica.messaging.MessageConsumer#handleMessage(de.willuhn.jameica.messaging.Message)
     */
    public void handleMessage(Message message) throws Exception
    {
      CheckTrustMessage msg = (CheckTrustMessage) message;
      msg.setTrusted(true,this.getClass().getName() + " at " + new Date().toString() + " for " + this.url);
    }
    
  }
}


/**********************************************************************
 * $Log: JSONClient.java,v $
 * Revision 1.2  2008/07/08 14:26:59  willuhn
 * @D javadoc
 *
 * Revision 1.1  2008/07/02 17:43:00  willuhn
 * @N Remote-Administrierbarkeit
 *
 * Revision 1.2  2008/06/20 15:07:12  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2008/06/16 23:27:28  willuhn
 * @N JSON-Client-Code zum Abfragen der Daten von anderen Jameica-Servern
 *
 **********************************************************************/
