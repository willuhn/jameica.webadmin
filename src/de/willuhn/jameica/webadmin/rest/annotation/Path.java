/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.webadmin/src/de/willuhn/jameica/webadmin/rest/annotation/Attic/Path.java,v $
 * $Revision: 1.1 $
 * $Date: 2008/10/21 22:33:47 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn software & services
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.webadmin.rest.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation, um eine Bean an eine URL zu binden.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Path {
  String value();
}


/*********************************************************************
 * $Log: Path.java,v $
 * Revision 1.1  2008/10/21 22:33:47  willuhn
 * @N Markieren der zu registrierenden REST-Kommandos via Annotation
 *
 * Revision 1.1  2008/10/08 16:01:38  willuhn
 * @N REST-Services via Injection (mittels Annotation) mit Context-Daten befuellen
 *
 **********************************************************************/