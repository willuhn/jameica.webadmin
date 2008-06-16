/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.webadmin/src/de/willuhn/jameica/webadmin/rest/Log.java,v $
 * $Revision: 1.2 $
 * $Date: 2008/06/16 14:22:11 $
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

import de.willuhn.logging.Level;
import de.willuhn.logging.Logger;

/**
 * Logger-Command.
 * Schreibt die uebergebene Nachricht ins lokale Log.
 */
public class Log
{
  private Context context = null;
  
  /**
   * Loggt die Nachricht als INFO.
   * @param clazz Ausloesende Klasse.
   * @param method Ausloesende Methode.
   * @param text zu loggender Text.
   * @throws IOException
   */
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
    Logger.write(level,context.getRequest().getRemoteHost(),clazz,method,text,null);
  }

  /**
   * Speichert den Context.
   * @param context der Context.
   */
  public void setContext(Context context)
  {
    this.context = context;
  }
}


/*********************************************************************
 * $Log: Log.java,v $
 * Revision 1.2  2008/06/16 14:22:11  willuhn
 * @N Mapping der REST-URLs via Property-Datei
 *
 * Revision 1.1  2008/06/13 14:11:04  willuhn
 * @N Mini REST-API
 *
 **********************************************************************/