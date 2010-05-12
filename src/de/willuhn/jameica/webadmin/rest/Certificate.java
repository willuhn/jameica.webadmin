/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.webadmin/src/de/willuhn/jameica/webadmin/rest/Certificate.java,v $
 * $Revision: 1.6 $
 * $Date: 2010/05/12 10:59:20 $
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

import org.json.JSONArray;

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
  private final static DateFormat DATEFORMAT = new SimpleDateFormat("dd.MM.yyyy");

  /**
   * Schreibt die installierten Zertifikate in den Response-Writer.
   * @return die Liste der Zertifikate
   * @throws IOException
   */
  @Doc(value="Liefert eine Liste der installierten SSL-Zertifikate",
       example="certs/list")
  @Path("/certs/list$")
  public JSONArray list() throws IOException
  {
    try
    {
      return new JSONArray(getList());
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

  /**
   * Listet die installierten Zertifikate auf.
   * @throws Exception
   */
  public List getList() throws Exception
  {
    ArrayList list = new ArrayList();
    X509Certificate[] certs = Application.getSSLFactory().getTrustedCertificates();
    for (int i=0;i<certs.length;++i)
    {
      de.willuhn.jameica.security.Certificate cert = new de.willuhn.jameica.security.Certificate(certs[i]);

      Map data = new HashMap();

      Map valid = new HashMap();
      valid.put("from",DATEFORMAT.format(certs[i].getNotBefore()));
      valid.put("to",  DATEFORMAT.format(certs[i].getNotAfter()));
      data.put("valid",valid);
      
      Map subject = new HashMap();
      Principal ps = cert.getSubject();
      subject.put(Principal.COMMON_NAME,        ps.getAttribute(Principal.COMMON_NAME));
      subject.put(Principal.COUNTRY,            ps.getAttribute(Principal.COUNTRY));
      subject.put(Principal.DISTINGUISHED_NAME, ps.getAttribute(Principal.DISTINGUISHED_NAME));
      subject.put(Principal.LOCALITY,           ps.getAttribute(Principal.LOCALITY));
      subject.put(Principal.ORGANIZATION,       ps.getAttribute(Principal.ORGANIZATION));
      subject.put(Principal.ORGANIZATIONAL_UNIT,ps.getAttribute(Principal.ORGANIZATIONAL_UNIT));
      subject.put(Principal.STATE,              ps.getAttribute(Principal.STATE));
      data.put("subject",subject);

      Map issuer = new HashMap();
      Principal pi = cert.getIssuer();
      issuer.put(Principal.COMMON_NAME,        pi.getAttribute(Principal.COMMON_NAME));
      issuer.put(Principal.COUNTRY,            pi.getAttribute(Principal.COUNTRY));
      issuer.put(Principal.DISTINGUISHED_NAME, pi.getAttribute(Principal.DISTINGUISHED_NAME));
      issuer.put(Principal.LOCALITY,           pi.getAttribute(Principal.LOCALITY));
      issuer.put(Principal.ORGANIZATION,       pi.getAttribute(Principal.ORGANIZATION));
      issuer.put(Principal.ORGANIZATIONAL_UNIT,pi.getAttribute(Principal.ORGANIZATIONAL_UNIT));
      issuer.put(Principal.STATE,              pi.getAttribute(Principal.STATE));
      data.put("issuer",issuer);

      Map certprops = new HashMap();
      certprops.put("serial", certs[i].getSerialNumber().toString());
      certprops.put("md5", cert.getMD5Fingerprint());
      certprops.put("sha1",cert.getSHA1Fingerprint());
      data.put("cert",certprops);

      list.add(data);
    }
    return list;
  }

}


/**********************************************************************
 * $Log: Certificate.java,v $
 * Revision 1.6  2010/05/12 10:59:20  willuhn
 * @N Automatische Dokumentations-Seite fuer die REST-Beans basierend auf der Annotation "Doc"
 *
 * Revision 1.5  2010/05/11 14:59:48  willuhn
 * @N Automatisches Deployment von REST-Beans
 *
 * Revision 1.4  2010/03/18 09:29:35  willuhn
 * @N Wenn REST-Beans Rueckgabe-Werte liefern, werrden sie automatisch als toString() in den Response-Writer geschrieben
 *
 * Revision 1.3  2009/08/05 09:03:40  willuhn
 * @C Annotations in eigenes Package verschoben (sind nicht mehr REST-spezifisch)
 *
 * Revision 1.2  2008/11/11 23:59:22  willuhn
 * @N Dualer Aufruf (via JSON und Map/List)
 *
 * Revision 1.1  2008/11/06 23:36:43  willuhn
 * @N REST-Bean fuer Anzeige der installierten Zertifikate
 *
 **********************************************************************/
