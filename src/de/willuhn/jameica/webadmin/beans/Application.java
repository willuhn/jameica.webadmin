/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.webadmin/src/de/willuhn/jameica/webadmin/beans/Attic/Application.java,v $
 * $Revision: 1.3 $
 * $Date: 2009/01/06 01:44:14 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn software & services
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.webadmin.beans;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.willuhn.jameica.plugin.Manifest;
import de.willuhn.jameica.plugin.PluginLoader;
import de.willuhn.jameica.system.Config;
import de.willuhn.logging.Logger;
import de.willuhn.logging.Message;

/**
 * Bean zum Anzeigen von System-Infos.
 */
public class Application
{
  private final static DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
  private String server = null;

  /**
   * Liefert Startzeit und -datum des Serverstarts.
   * @return Zeit und Datum des Server-Starts.
   */
  public String getStarted()
  {
    return dateFormat.format(de.willuhn.jameica.system.Application.getStartDate());
  }
  
  /**
   * Liefert einen String mit der Uptime.
   * @return String mit der Uptime.
   */
  public String getUptime()
  {
    long minutes = (System.currentTimeMillis() - de.willuhn.jameica.system.Application.getStartDate().getTime()) / 1000L / 60L;
    long hours   = minutes / 60;

    minutes %= 60; // Restminuten abzueglich Stunden

    // ggf. ne "0" vorn dran schreiben
    String mins = (minutes < 10 ? ("0" + minutes) : "" + minutes);

    if (hours < 24) // weniger als 1 Tag?
      return hours + ":" + mins + " h";
    
    long days = hours / 24;
    return days + " Tag(e), " + (hours % 24) + ":" + mins + " h";
  }
  
  /**
   * Liefert das System-Manifest.
   * @return das System-Manifest.
   */
  public Manifest getManifest()
  {
    return de.willuhn.jameica.system.Application.getManifest();
  }
  
  /**
   * Liefert die System-Config.
   * @return System-Confif.
   */
  public Config getConfig()
  {
    return de.willuhn.jameica.system.Application.getConfig();
  }
  
  /**
   * Liefert den Pluginloader.
   * @return der Pluginloader.
   */
  public PluginLoader getPluginLoader()
  {
    return de.willuhn.jameica.system.Application.getPluginLoader();
  }
  
  /**
   * Liefert eine Liste der beim Systemstart aufgelaufenen Nachrichten.
   * @return Welcome-Messages.
   */
  public String[] getWelcomeMessages()
  {
    return de.willuhn.jameica.system.Application.getWelcomeMessages();
  }
  
  /**
   * Liefert eine Liste der letzten Log-Nachrichten.
   * Die Nachrichten sind in umgekehrt chronologischer Reihenfolge sortiert (also neueste zuerst).
   * @return Liste der letzten Log-Nachrichten.
   */
  public Message[] getLog()
  {
    List<Message> list = Arrays.asList(Logger.getLastLines());
    Collections.reverse(list);
    return list.toArray(new Message[list.size()]);
  }

  /**
   * Liefert den aktuell ausgewaehlten Server.
   * @return der aktuell ausgewaehlte Server oder NULL.
   */
  public String getCurrentServer()
  {
    return this.server == null ? "localhost" : this.server;
  }

  /**
   * Speichert den aktuell ausgewaehlten Server.
   * @param server der aktuell ausgewaehlte Server.
   */
  public void setCurrentServer(String server)
  {
    this.server = "".equals(server) ? null : server;
  }
  
  
}


/*********************************************************************
 * $Log: Application.java,v $
 * Revision 1.3  2009/01/06 01:44:14  willuhn
 * @N Code zum Hinzufuegen von Servern erweitert
 *
 * Revision 1.2  2008/11/06 11:38:24  willuhn
 * @N Schritt-fuer-Schritt-Umstellung auf JSTL
 *
 * Revision 1.1  2008/11/06 01:22:19  willuhn
 * @R javafaces und richfaces entfernt. Bloat!
 *
 * Revision 1.1  2007/05/15 15:33:17  willuhn
 * @N helloworld.war
 * @C Webadmin komplett auf JSP umgestellt
 * @C build-Script angepasst
 *
 **********************************************************************/