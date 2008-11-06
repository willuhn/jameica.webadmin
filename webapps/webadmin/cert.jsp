<%@ include file="inc.top.jspf" %>

<%@page import="java.security.cert.X509Certificate"%>
<%@page import="de.willuhn.jameica.security.Certificate"%>
<%@page import="de.willuhn.jameica.security.Principal"%>
<%@page import="de.willuhn.jameica.system.Application"%>

<%
  String __certFp = request.getParameter("cert");
  Certificate __cert = null;
  
  X509Certificate[] __certList = Application.getSSLFactory().getTrustedCertificates();
  for (int i=0;i<__certList.length;++i)
  {
    Certificate cert = new Certificate(__certList[i]);
    if (cert.getSHA1Fingerprint().equals(__certFp))
    {
      __cert = cert;
      break;
    }
  }
  
  if (__cert == null)
  {
    Application.addWelcomeMessage("Das Zertifikat wurde nicht gefunden");
    response.sendRedirect("index.jsp");
  }
%>

<div id="breadcrumbs">
  <a href="/webadmin/">Index</a>
  &raquo;
  <a href="cert.jsp?cert=<%= __certFp %>">Zertifikat <%= __cert.getSubject().getAttribute(Principal.COMMON_NAME) %></a>
</div>

<h2>Zertifikat <%= __cert.getSubject().getAttribute(Principal.COMMON_NAME) %></h2>

<table class="data">
  <tr>
    <th>Namea</th>
    <th>Wert</th>
  </tr>
  <tr>
    <td>Ausgestellt f�r</td>
    <td><%= __cert.getSubject().getAttribute(Principal.COMMON_NAME) %></td>
  </tr>
  <tr>
    <td>Ausgestellt von</td>
    <td><%= __cert.getIssuer().getAttribute(Principal.COMMON_NAME) %></td>
  </tr>
  <tr>
    <td>Fingerabdruck</td>
    <td>
      SHA1: <%= __cert.getSHA1Fingerprint() %>
      <br/>
      MD5: <%= __cert.getMD5Fingerprint() %>
    </td>
  </tr>
</table>

<%@ include file="inc.bottom.jspf" %>
