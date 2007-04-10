/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.webadmin/src/de/willuhn/jameica/webadmin/Plugin.java,v $
 * $Revision: 1.1 $
 * $Date: 2007/04/10 00:11:39 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn software & services
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.webadmin;

import java.io.File;

import de.willuhn.jameica.plugin.AbstractPlugin;
import de.willuhn.util.ApplicationException;


/**
 * Plugin-Klasse.
 */
public class Plugin extends AbstractPlugin
{

  /**
   * ct.
   * @param file
   */
  public Plugin(File file)
  {
    super(file);
  }

  /**
   * @see de.willuhn.jameica.plugin.AbstractPlugin#init()
   */
  public void init() throws ApplicationException
  {
  }

  /**
   * @see de.willuhn.jameica.plugin.AbstractPlugin#install()
   */
  public void install() throws ApplicationException
  {
  }

  /**
   * @see de.willuhn.jameica.plugin.AbstractPlugin#shutDown()
   */
  public void shutDown()
  {
  }

  /**
   * @see de.willuhn.jameica.plugin.AbstractPlugin#update(double)
   */
  public void update(double oldVersion) throws ApplicationException
  {
  }

}


/**********************************************************************
 * $Log: Plugin.java,v $
 * Revision 1.1  2007/04/10 00:11:39  willuhn
 * *** empty log message ***
 *
 **********************************************************************/
