<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<jsp:useBean id="certs" class="de.willuhn.jameica.webadmin.rest.Certificate" scope="session"/>

<%@ include file="inc.top.jspf" %>

<div id="breadcrumbs">
  <a href="/webadmin/">Index</a>
</div>

<c:forEach items="${certs.list}" var="cert">
  <c:if test="${cert.cert.sha1 == param.cert}">
    <h2>Details des Zertifikats</h2>
    
    <table class="data">
      <tr>
        <th>Name</th>
        <th>Wert</th>
      </tr>
      <tr>
        <td>Ausgestellt f�r</td>
        <td>
          <table>
            <tr><td>distinguished name</td><td>${cert.subject.DN}</td></tr>
            <tr><td>common name</td><td>${cert.subject.CN}</td></tr>
            <tr><td>organization</td><td>${cert.subject.O}</td></tr>
            <tr><td>organizational unit</td><td>${cert.subject.OU}</td></tr>
            <tr><td>locality</td><td>${cert.subject.L}</td></tr>
            <tr><td>state</td><td>${cert.subject.ST}</td></tr>
            <tr><td>country</td><td>${cert.subject.C}</td></tr>
          </table>
        </td>
      </tr>
      <tr>
        <td>Ausgestellt von</td>
        <td>
          <table>
            <tr><td>distinguished name</td><td>${cert.issuer.DN}</td></tr>
            <tr><td>common name</td><td>${cert.issuer.CN}</td></tr>
            <tr><td>organization</td><td>${cert.issuer.O}</td></tr>
            <tr><td>organizational unit</td><td>${cert.issuer.OU}</td></tr>
            <tr><td>locality</td><td>${cert.issuer.L}</td></tr>
            <tr><td>state</td><td>${cert.issuer.ST}</td></tr>
            <tr><td>country</td><td>${cert.issuer.C}</td></tr>
          </table>
        </td>
      </tr>
      <tr>
        <td>G�ltigkeit</td>
        <td>${cert.valid.from} - ${cert.valid.to}</td>
      </tr>
      <tr>
        <td>Seriennummer</td>
        <td>${cert.cert.serial}</td>
      </tr>
      <tr>
        <td>Fingerabdruck</td>
        <td>
          <table>
            <tr><td>SHA1</td><td>${cert.cert.sha1}</td></tr>
            <tr><td>MD5</td><td>${cert.cert.md5}</td></tr>
          </table>
        </td>
      </tr>
    </table>
  </c:if>
</c:forEach>


<%@ include file="inc.bottom.jspf" %>
