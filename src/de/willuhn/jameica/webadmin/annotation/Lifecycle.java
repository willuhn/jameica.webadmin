/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.webadmin/src/de/willuhn/jameica/webadmin/annotation/Attic/Lifecycle.java,v $
 * $Revision: 1.2 $
 * $Date: 2009/08/05 11:03:17 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn software & services
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.webadmin.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation, mit der der Lifecycle einer Bean festgelegt werden kann.
 * Es ist der Anwendung, die diesen Lifecycle auswertet, selbst ueberlassen,
 * welchen Lifecycle-Typ sie verwendet, wenn an der konkreten Bean
 * keine entsprechende Annotation definiert ist.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Lifecycle {
  
  
  /**
   * Definiert die Licecycle-Typen.
   */
  public static enum Type
  {
    /**
     * Bean-Instanz lebt fuer die Dauer der Anwendung.
     */
    CONTEXT,
    
    /**
     * Bean-Instanz lebt fuer die Dauer der Session (des Users).
     */
    SESSION,
    
    /**
     * Bean-Instanz lebt lediglich fuer die Dauer eines einzelnen Requests.
     */
    REQUEST,
  }
  
  /**
   * Typ des Lifecycle.
   * @return Typ des Lifecycle 
   */
  Lifecycle.Type value();
}


/*********************************************************************
 * $Log: Lifecycle.java,v $
 * Revision 1.2  2009/08/05 11:03:17  willuhn
 * @N Neue Annotation "Lifecycle"
 *
 * Revision 1.1  2009/08/05 09:03:40  willuhn
 * @C Annotations in eigenes Package verschoben (sind nicht mehr REST-spezifisch)
 *
 **********************************************************************/