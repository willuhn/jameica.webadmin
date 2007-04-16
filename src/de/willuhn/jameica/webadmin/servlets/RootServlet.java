/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.webadmin/src/de/willuhn/jameica/webadmin/servlets/Attic/RootServlet.java,v $
 * $Revision: 1.3 $
 * $Date: 2007/04/16 11:22:15 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn software & services
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.webadmin.servlets;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import de.willuhn.jameica.system.Application;
import de.willuhn.jameica.webadmin.Plugin;
import de.willuhn.logging.Logger;
import de.willuhn.util.I18N;


/**
 * Root-Servlet des Webservers.
 */
public class RootServlet extends HttpServlet
{
  // Start-Datum von Jameica
  private final static Date started          = new Date();
  
  // Datums-Format
  private final static DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

  private I18N i18n = null;
  
  static
  {
    
  }
  
  
  private VelocityContext context = new VelocityContext();
  
  /**
   * ct.
   */
  public RootServlet()
  {
    this.i18n = Application.getPluginLoader().getPlugin(Plugin.class).getResources().getI18N();
    
    context.put("manifest",      Application.getManifest());
    context.put("pluginloader",  Application.getPluginLoader());
    context.put("servicefactory",Application.getServiceFactory());
    context.put("sslfactory",    Application.getSSLFactory());
    context.put("config",        Application.getConfig());
    context.put("messages",      Application.getWelcomeMessages());
    context.put("dateformat",    dateFormat);
  }
  
  /**
   * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
   */
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {
    response.setContentType("text/html");

    try
    {
      prepareContext(request);
      Template template = Velocity.getTemplate("template.vm");
      template.merge(context,response.getWriter());
    }
    catch (IOException ioe)
    {
      throw ioe;
    }
    catch (ServletException se)
    {
      throw se;
    }
    catch (Exception e)
    {
      throw new ServletException(e);
    }
  }

  /**
   * Schreibt Daten in den Context, die sich bei jedem Request aendern.
   * Sie gehoeren nicht in den Konstruktor weil Servlets per Spezifikation
   * mehrfach verwendet werden duerfen.
   * @param request der Request.
   */
  private void prepareContext(HttpServletRequest request)
  {
    String name = "index.vm";
    String query = request.getQueryString();
    if (query != null && query.length() > 0)
      name = query + ".vm";
    
    // Wir zeugen die neuesten Eintraege zuerst an
    List log = Arrays.asList(Logger.getLastLines());
    Collections.reverse(log);
    context.put("log",log.toArray());
    
    context.put("uptime",i18n.tr("System gestartet am: " + dateFormat.format(started) + "<br/>Uptime: " + uptime()));
    context.put("contentfile",name);
  }
  
  /**
   * Ermittelt die Uptime.
   * @return uptime.
   */
  private String uptime()
  {
    long minutes = (System.currentTimeMillis() - started.getTime()) / 1000L / 60L;
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


/**********************************************************************
 * $Log: RootServlet.java,v $
 * Revision 1.3  2007/04/16 11:22:15  willuhn
 * @N display log
 *
 * Revision 1.2  2007/04/16 00:12:39  willuhn
 * @N Image-Handler
 *
 * Revision 1.1  2007/04/12 00:02:56  willuhn
 * @C replaced winstone with jetty (because of ssl support via custom socketfactory)
 *
 **********************************************************************/
