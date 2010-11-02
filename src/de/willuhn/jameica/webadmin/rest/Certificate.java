/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.webadmin/src/de/willuhn/jameica/webadmin/rest/Certificate.java,v $
 * $Revision: 1.7 $
 * $Date: 2010/11/02 00:56:31 $
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
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import de.willuhn.jameica.security.Principal;
import de.willuhn.jameica.system.Application;
import de.willuhn.jameica.webadmin.annotation.Doc;
import de.willuhn.jameica.webadmin.annotation.Path;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;


/**
 * REST-Kommando fuer den Zugriff auf die Zertifikate.
 */
@Doc("System: Liefert Informationen über die in Jameica installierten SSL-Zertifikate")
public class Certificate implements AutoRestBean
{
  /**
   * Liefert die Details des angegebenen Zertifikates.
   * Die Funktion erwartet als Parameter den SHA1-Hash des Zertifikates.
   * @return die Details des angegebenen Zertifikates.
   * @throws IOException
   */
  @Doc(value="Liefert die Details des angegebenen Zertifikates",
       example="certs/get/12:34:56:78:90")
  @Path("/certs/get/(.*)$")
  public JSONObject getDetails(String sha1) throws IOException
  {
    if (sha1 == null || sha1.length() == 0)
      throw new IOException("no sha1 hash given");
    
    try
    {
      X509Certificate[] certs = Application.getSSLFactory().getTrustedCertificates();
      for (X509Certificate c:certs)
      {
        de.willuhn.jameica.security.Certificate cert = new de.willuhn.jameica.security.Certificate(c);
        if (!cert.getSHA1Fingerprint().equals(sha1))
          continue;

        Map all = new HashMap();

        {
          Map map = new HashMap();
          DateFormat DATEFORMAT = new SimpleDateFormat("dd.MM.yyyy");
          map.put("from",DATEFORMAT.format(c.getNotBefore()));
          map.put("to",  DATEFORMAT.format(c.getNotAfter()));
          all.put("valid",map);
        }
        
        {
          Map map = new HashMap();
          Principal ps = cert.getSubject();
          map.put(Principal.COMMON_NAME,        StringUtils.trimToEmpty(ps.getAttribute(Principal.COMMON_NAME)));
          map.put(Principal.COUNTRY,            StringUtils.trimToEmpty(ps.getAttribute(Principal.COUNTRY)));
          map.put(Principal.DISTINGUISHED_NAME, StringUtils.trimToEmpty(ps.getAttribute(Principal.DISTINGUISHED_NAME)));
          map.put(Principal.LOCALITY,           StringUtils.trimToEmpty(ps.getAttribute(Principal.LOCALITY)));
          map.put(Principal.ORGANIZATION,       StringUtils.trimToEmpty(ps.getAttribute(Principal.ORGANIZATION)));
          map.put(Principal.ORGANIZATIONAL_UNIT,StringUtils.trimToEmpty(ps.getAttribute(Principal.ORGANIZATIONAL_UNIT)));
          map.put(Principal.STATE,              StringUtils.trimToEmpty(ps.getAttribute(Principal.STATE)));
          all.put("subject",map);
        }

        {
          Map map = new HashMap();
          Principal pi = cert.getIssuer();
          map.put(Principal.COMMON_NAME,        StringUtils.trimToEmpty(pi.getAttribute(Principal.COMMON_NAME)));
          map.put(Principal.COUNTRY,            StringUtils.trimToEmpty(pi.getAttribute(Principal.COUNTRY)));
          map.put(Principal.DISTINGUISHED_NAME, StringUtils.trimToEmpty(pi.getAttribute(Principal.DISTINGUISHED_NAME)));
          map.put(Principal.LOCALITY,           StringUtils.trimToEmpty(pi.getAttribute(Principal.LOCALITY)));
          map.put(Principal.ORGANIZATION,       StringUtils.trimToEmpty(pi.getAttribute(Principal.ORGANIZATION)));
          map.put(Principal.ORGANIZATIONAL_UNIT,StringUtils.trimToEmpty(pi.getAttribute(Principal.ORGANIZATIONAL_UNIT)));
          map.put(Principal.STATE,              StringUtils.trimToEmpty(pi.getAttribute(Principal.STATE)));
          all.put("issuer",map);
        }

        {
          Map map = new HashMap();
          map.put("serial", c.getSerialNumber().toString());
          map.put("md5", cert.getMD5Fingerprint());
          map.put("sha1",cert.getSHA1Fingerprint());
          all.put("cert",map);
        }
        return new JSONObject(all);
      }
      throw new IOException("certificate " + sha1 + " not found");
    }
    catch (ApplicationException ae)
    {
      throw new IOException(ae.getMessage());
    }
    catch (Exception e)
    {
      Logger.error("unable to load certificate " + sha1,e);
      throw new IOException("unable to load certificate " + sha1);
    }
  }
  
  /**
   * Liefert die installierten Zertifikate.
   * @return die Liste der Zertifikate
   * @throws IOException
   */
  @Doc(value="Liefert eine Liste der installierten SSL-Zertifikate",
       example="certs/list")
  @Path("/certs/list$")
  public JSONArray getList() throws IOException
  {
    try
    {
      List<JSONObject> list = new ArrayList();
      X509Certificate[] certs = Application.getSSLFactory().getTrustedCertificates();
      for (int i=0;i<certs.length;++i)
      {
        de.willuhn.jameica.security.Certificate cert = new de.willuhn.jameica.security.Certificate(certs[i]);
        list.add(getDetails(cert.getSHA1Fingerprint()));
      }
      return new JSONArray(list);
    }
    catch (ApplicationException ae)
    {
      throw new IOException(ae.getMessage());
    }
    catch (Exception e)
    {
      Logger.error("unable to load certificates",e);
      throw new IOException("unable to load certificates: " + e.getMessage());
    }
  }
}


/**********************************************************************
 * $Log: Certificate.java,v $
 * Revision 1.7  2010/11/02 00:56:31  willuhn
 * @N Umstellung des Webfrontends auf Velocity/Webtools
 *
 * Revision 1.6  2010/05/12 10:59:20  willuhn
 * @N Automatische Dokumentations-Seite fuer die REST-Beans basierend auf der Annotation "Doc"
 *
 * Revision 1.5  2010/05/11 14:59:48  willuhn
 * @N Automatisches Deployment von REST-Beans
 *
 * Revision 1.4  2010/03/18 09:29:35  willuhn
 * @N Wenn REST-Beans Rueckgabe-Werte liefern, werrden sie automatisch als toString() in den Response-Writer geschrieben
 **********************************************************************/