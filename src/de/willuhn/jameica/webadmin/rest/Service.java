/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.webadmin/src/de/willuhn/jameica/webadmin/rest/Service.java,v $
 * $Revision: 1.1 $
 * $Date: 2008/06/15 22:48:23 $
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

import de.willuhn.jameica.plugin.AbstractPlugin;
import de.willuhn.jameica.plugin.Manifest;
import de.willuhn.jameica.plugin.ServiceDescriptor;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;


/**
 * REST-Kommando fuer einen Service.
 */
public class Service implements Command
{
  private de.willuhn.datasource.Service service = null;

  /**
   * @see de.willuhn.jameica.webadmin.rest.Command#execute(de.willuhn.jameica.webadmin.rest.Context)
   */
  public void execute(Context context) throws IOException
  {
    Command c = context.getParent();
    if (c == null || !(c instanceof Plugin))
      throw new IOException("no plugin given");

    AbstractPlugin plugin = ((Plugin) c).getPlugin();
    String service        = context.getParameter();
    

    // Service angegeben?
    if (service != null && service.length() > 0)
    {
      try
      {
        this.service = Application.getServiceFactory().lookup(plugin.getClass(),service);
        return;
      }
      catch (Exception e)
      {
        Logger.error("unable to load service",e);
        throw new IOException("unable to load service " + service);
      }
    }

    // Kein Service angegeben. Auflisten
    Manifest mf = plugin.getManifest();
    ServiceDescriptor[] services = mf.getServices();
    
    StringBuffer names = new StringBuffer();
    if (services != null)
    {
      for (int i=0;i<services.length;++i)
      {
        String name = services[i].getName();
        if (name == null || name.length() == 0)
          continue;
        
        names.append(name);
        if (i<services.length-1)
          names.append(",");
      }
    }
    context.getResponse().getWriter().print(names.toString());
  }

  /**
   * @see de.willuhn.jameica.webadmin.rest.Command#getName()
   */
  public String getName()
  {
    return "services";
  }
  
  /**
   * Liefert den ausgewaehlten Service.
   * @return der ausgewaehlte Service.
   */
  de.willuhn.datasource.Service getService()
  {
    return this.service;
  }

}


/**********************************************************************
 * $Log: Service.java,v $
 * Revision 1.1  2008/06/15 22:48:23  willuhn
 * @N Command-Chains
 *
 **********************************************************************/
