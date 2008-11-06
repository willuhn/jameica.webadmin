/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.webadmin/src/de/willuhn/jameica/webadmin/rest/Certificate.java,v $
 * $Revision: 1.1 $
 * $Date: 2008/11/06 23:36:43 $
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
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;

import de.willuhn.jameica.security.Principal;
import de.willuhn.jameica.system.Application;
import de.willuhn.jameica.webadmin.rest.annotation.Path;
import de.willuhn.jameica.webadmin.rest.annotation.Response;


/**
 * REST-Kommando fuer den Zugriff auf die Zertifikate.
 */
public class Certificate
{
  private final static DateFormat DATEFORMAT = new SimpleDateFormat("dd.MM.yyyy");

  @Response
  private HttpServletResponse response = null;
  
  /**
   * Listet die installierten Zertifikate auf.
   * @throws IOException
   */
  @Path("/certs/list$")
  public void list() throws Exception
  {
    ArrayList json = new ArrayList();
    X509Certificate[] list = Application.getSSLFactory().getTrustedCertificates();
    for (int i=0;i<list.length;++i)
    {
      de.willuhn.jameica.security.Certificate cert = new de.willuhn.jameica.security.Certificate(list[i]);

      Map data = new HashMap();

      Map valid = new HashMap();
      valid.put("from",DATEFORMAT.format(list[i].getNotBefore()));
      valid.put("to",  DATEFORMAT.format(list[i].getNotAfter()));
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
      certprops.put("serial", list[i].getSerialNumber().toString());
      certprops.put("md5", cert.getMD5Fingerprint());
      certprops.put("sha1",cert.getSHA1Fingerprint());
      data.put("cert",certprops);

      json.add(data);
    }
    response.getWriter().print(new JSONArray(json).toString());
  }
}


/**********************************************************************
 * $Log: Certificate.java,v $
 * Revision 1.1  2008/11/06 23:36:43  willuhn
 * @N REST-Bean fuer Anzeige der installierten Zertifikate
 *
 **********************************************************************/
