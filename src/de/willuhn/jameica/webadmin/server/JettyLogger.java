/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.webadmin/src/de/willuhn/jameica/webadmin/server/Attic/JettyLogger.java,v $
 * $Revision: 1.2 $
 * $Date: 2007/11/27 16:28:52 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn software & services
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.webadmin.server;

import java.text.MessageFormat;

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
    de.willuhn.logging.Logger.write(Level.DEBUG,format(arg0,arg1,arg2));
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
    de.willuhn.logging.Logger.write(Level.INFO,format(arg0,arg1,arg2));
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
    de.willuhn.logging.Logger.write(Level.WARN,format(arg0,arg1,arg2));
  }
  
  /**
   * Formatiert die Log-Nachricht.
   * @param msg
   * @param param1
   * @param param2
   * @return die formatierte Log-Nachricht.
   */
  private String format(String msg, Object param1, Object param2)
  {
    msg = msg.replaceFirst("\\{\\}","{0}");
    msg = msg.replaceFirst("\\{\\}","{1}");
    
    try
    {
      return MessageFormat.format(msg,new Object[]{param1,param2});
    }
    catch (Exception e)
    {
      // ignore
    }
    return msg;
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
 * Revision 1.2  2007/11/27 16:28:52  willuhn
 * @B ignore errors while formatting merssages
 *
 * Revision 1.1  2007/04/12 13:35:17  willuhn
 * @N SSL-Support
 * @N Authentifizierung
 * @N Korrektes Logging
 *
 * Revision 1.1  2007/04/12 00:02:55  willuhn
 * @C replaced winstone with jetty (because of ssl support via custom socketfactory)
 *
 **********************************************************************/
