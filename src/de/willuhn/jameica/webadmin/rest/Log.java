/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.webadmin/src/de/willuhn/jameica/webadmin/rest/Log.java,v $
 * $Revision: 1.7 $
 * $Date: 2008/11/07 00:17:16 $
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;

import de.willuhn.jameica.webadmin.rest.annotation.Path;
import de.willuhn.jameica.webadmin.rest.annotation.Request;
import de.willuhn.jameica.webadmin.rest.annotation.Response;
import de.willuhn.logging.Level;
import de.willuhn.logging.Logger;
import de.willuhn.logging.Message;

/**
 * Logger-Command.
 * Schreibt die uebergebene Nachricht ins lokale Log.
 */
public class Log
{
  @Response
  private HttpServletResponse response = null;
  
  @Request
  private HttpServletRequest request   = null;
  
  /**
   * Liefert die letzten Zeilen des Logs.
   * @throws IOException
   */
  @Path("/log/last$")
  public void last() throws IOException
  {
    last(null);
  }

  /**
   * Liefert eine Liste der beim Systemstart aufgelaufenen Nachrichten.
   * @throws IOException
   */
  @Path("/log/welcome$")
  public void welcome() throws IOException
  {
    try
    {
      response.getWriter().print(new JSONArray(de.willuhn.jameica.system.Application.getWelcomeMessages()).toString());
    }
    catch (JSONException e)
    {
      Logger.error("unable to encode via json",e);
      throw new IOException("error while encoding into json");
    }
  }

  /**
   * Liefert die letzten Zeilen des Logs.
   * @param lines Anzahl der Zeilen.
   * @throws IOException
   */
  @Path("/log/last/([0-9]{1,2})$")
  public void last(String lines) throws IOException
  {
    int last = -1;
    try
    {
      last = Integer.parseInt(lines);
    } catch (Exception e) {}
    
    ArrayList json = new ArrayList();

    int count = last;
    
    Message[] msg = Logger.getLastLines();
    for (int i=msg.length-1;i>=0;--i)
    {
      if (last > 0 && count-- <= 0)
        break;

      Map data = new HashMap();
      data.put("date",  msg[i].getDate());
      data.put("host",  msg[i].getHost());
      data.put("level", msg[i].getLevel().getName());
      data.put("class", msg[i].getLoggingClass());
      data.put("method",msg[i].getLoggingMethod());
      data.put("text",  msg[i].getText());
      json.add(data);
    }
    response.getWriter().print(new JSONArray(json).toString());
  }
  
  /**
   * Loggt die Nachricht als INFO.
   * @param clazz Ausloesende Klasse.
   * @param method Ausloesende Methode.
   * @param text zu loggender Text.
   * @throws IOException
   */
  @Path("/log/info/(.*?)/(.*?)/(.*?)")
  public void info(String clazz, String method, String text) throws IOException
  {
    write(Level.INFO,clazz,method,text);
  }

  /**
   * Loggt die Nachricht als Warnung.
   * @param clazz Ausloesende Klasse.
   * @param method Ausloesende Methode.
   * @param text zu loggender Text.
   * @throws IOException
   */
  @Path("/log/warn/(.*?)/(.*?)/(.*?)")
  public void warn(String clazz, String method, String text) throws IOException
  {
    write(Level.WARN,clazz,method,text);
  }

  /**
   * Loggt die Nachricht als Fehler.
   * @param clazz Ausloesende Klasse.
   * @param method Ausloesende Methode.
   * @param text zu loggender Text.
   * @throws IOException
   */
  @Path("/log/error/(.*?)/(.*?)/(.*?)")
  public void error(String clazz, String method, String text) throws IOException
  {
    write(Level.ERROR,clazz,method,text);
  }

  /**
   * Loggt die Nachricht.
   * @param level Log-Level.
   * @param clazz Ausloesende Klasse.
   * @param method Ausloesende Methode.
   * @param text zu loggender Text.
   * @throws IOException
   */
  private void write(Level level, String clazz, String method, String text) throws IOException
  {
    Logger.write(level,request.getRemoteHost(),clazz,method,text,null);
  }
}


/*********************************************************************
 * $Log: Log.java,v $
 * Revision 1.7  2008/11/07 00:17:16  willuhn
 * @N Welcome-Nachrichten
 *
 * Revision 1.6  2008/10/21 22:33:47  willuhn
 * @N Markieren der zu registrierenden REST-Kommandos via Annotation
 *
 * Revision 1.5  2008/10/08 21:38:23  willuhn
 * @C Nur noch zwei Annotations "Request" und "Response"
 *
 * Revision 1.4  2008/10/08 16:01:38  willuhn
 * @N REST-Services via Injection (mittels Annotation) mit Context-Daten befuellen
 *
 * Revision 1.3  2008/06/20 13:24:29  willuhn
 * @N REST-Command zum Abrufen der letzten Log-Zeilen
 *
 * Revision 1.2  2008/06/16 14:22:11  willuhn
 * @N Mapping der REST-URLs via Property-Datei
 *
 * Revision 1.1  2008/06/13 14:11:04  willuhn
 * @N Mini REST-API
 *
 **********************************************************************/