/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.webadmin/src/de/willuhn/jameica/webadmin/Attic/JettyLogger.java,v $
 * $Revision: 1.1 $
 * $Date: 2007/04/12 00:02:55 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn software & services
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.webadmin;

import org.mortbay.log.Logger;

import de.willuhn.logging.Level;


/**
 * Implementiert, um Jetty-Logausgaben zu uns umzuleiten.
 */
public class JettyLogger implements Logger
{

  /**
   * @see org.mortbay.log.Logger#debug(java.lang.String, java.lang.Throwable)
   */
  public void debug(String arg0, Throwable arg1)
  {
    de.willuhn.logging.Logger.write(Level.DEBUG,arg0,arg1);
  }

  /**
   * @see org.mortbay.log.Logger#debug(java.lang.String, java.lang.Object, java.lang.Object)
   */
  public void debug(String arg0, Object arg1, Object arg2)
  {
    de.willuhn.logging.Logger.write(Level.DEBUG,arg0);
  }

  /**
   * @see org.mortbay.log.Logger#getLogger(java.lang.String)
   */
  public Logger getLogger(String arg0)
  {
    return this;
  }

  /**
   * @see org.mortbay.log.Logger#info(java.lang.String, java.lang.Object, java.lang.Object)
   */
  public void info(String arg0, Object arg1, Object arg2)
  {
    de.willuhn.logging.Logger.write(Level.INFO,arg0);
  }

  /**
   * @see org.mortbay.log.Logger#isDebugEnabled()
   */
  public boolean isDebugEnabled()
  {
    return de.willuhn.logging.Logger.getLevel().getValue() <= Level.DEBUG.getValue();
  }

  /**
   * @see org.mortbay.log.Logger#setDebugEnabled(boolean)
   */
  public void setDebugEnabled(boolean arg0)
  {
    // ignore
  }

  /**
   * @see org.mortbay.log.Logger#warn(java.lang.String, java.lang.Throwable)
   */
  public void warn(String arg0, Throwable arg1)
  {
    de.willuhn.logging.Logger.write(Level.WARN,arg0,arg1);
  }

  /**
   * @see org.mortbay.log.Logger#warn(java.lang.String, java.lang.Object, java.lang.Object)
   */
  public void warn(String arg0, Object arg1, Object arg2)
  {
    de.willuhn.logging.Logger.write(Level.WARN,arg0);
  }

  /**
   * @see java.lang.Object#toString()
   */
  public String toString()
  {
    return "Jameica-Logger";
  }
  
  

}


/**********************************************************************
 * $Log: JettyLogger.java,v $
 * Revision 1.1  2007/04/12 00:02:55  willuhn
 * @C replaced winstone with jetty (because of ssl support via custom socketfactory)
 *
 **********************************************************************/
