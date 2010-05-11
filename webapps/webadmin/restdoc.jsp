<%@ include file="inc.top.jspf" %>

<%@page import="de.willuhn.jameica.webadmin.rmi.RestService"%>
<%@page import="de.willuhn.jameica.system.Application"%>
<%@page import="de.willuhn.jameica.webadmin.Plugin"%>
<%@page import="de.willuhn.jameica.webadmin.beans.RestBeanDoc"%>
<%@page import="de.willuhn.jameica.webadmin.beans.RestMethodDoc"%>

<%
  RestService __rs = (RestService) Application.getServiceFactory().lookup(Plugin.class,"rest");
%>


<div id="breadcrumbs">
  <a href="/webadmin/">Index</a>
  &raquo;
  <a href="/rest">REST-Services</a>
</div>

<h2>REST-Services</h2>

<% if (__rs.isStarted()) { %>
  <% for (RestBeanDoc __bd:__rs.getDoc()) { %>
    <h3><%= __bd.getBeanClass().getSimpleName() %></h3>

    <% if (__bd.getText() != null) { %>
      <%= __bd.getText() %><br/><br/>
    <% } %>
    
    <table class="data">
      <tr>
        <th colspan="2">Funktionen</th>
      </tr>
      <% for (RestMethodDoc __md:__bd.getMethods()) { %>
        <tr>
          <td><b><%= __md.getPath() %></b></td>
          <td>
            <%= __md.getText() != null ? __md.getText() : "" %>
            <% if (__md.getExample() != null) { %>
              <br/><br/><i>Beispiel: <%= __md.getExample() %></i><br/>
            <% } %>
            <br/>
          </td>
        </tr>
      <% } %>
    </table>
  <% } %>

<% } else { %>
  Der REST-Service ist nicht gestartet.
<% } %>

<%@ include file="inc.bottom.jspf" %>
