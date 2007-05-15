/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.webadmin/src/de/willuhn/jameica/webadmin/beans/Attic/Uptime.java,v $
 * $Revision: 1.1 $
 * $Date: 2007/05/15 15:33:17 $
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

import de.willuhn.jameica.system.Application;

/**
 * Bean zum Anzeigen der Uptime.
 */
public class Uptime
{
  private final static DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

  /**
   * Liefert Startzeit und -datum des Serverstarts.
   * @return Zeit und Datum des Server-Starts.
   */
  public String getStarted()
  {
    return dateFormat.format(Application.getStartDate());
  }
  
  /**
   * Liefert einen String mit der Uptime.
   * @return String mit der Uptime.
   */
  public String getUptime()
  {
    long minutes = (System.currentTimeMillis() - Application.getStartDate().getTime()) / 1000L / 60L;
    long hours   = minutes / 60;

    minutes %= 60; // Restminuten abzueglich Stunden

    // ggf. ne "0" vorn dran schreiben
    String mins = (minutes < 10 ? ("0" + minutes) : "" + minutes);

    if (hours < 24) // weniger als 1 Tag?
      return hours + ":" + mins + " h";
    
    long days = hours / 24;
    return days + " Tag(e), " + (hours % 24) + ":" + mins + " h";
  }
}


/*********************************************************************
 * $Log: Uptime.java,v $
 * Revision 1.1  2007/05/15 15:33:17  willuhn
 * @N helloworld.war
 * @C Webadmin komplett auf JSP umgestellt
 * @C build-Script angepasst
 *
 **********************************************************************/