<%@ include file="inc.top.jspf" %>

<%@page import="de.willuhn.jameica.webadmin.rmi.RestService"%>
<%@page import="de.willuhn.jameica.system.Application"%>
<%@page import="de.willuhn.jameica.webadmin.Plugin"%>
<%@page import="de.willuhn.jameica.webadmin.beans.RestBeanDoc"%>
<%@page import="de.willuhn.jameica.webadmin.beans.RestMethodDoc"%>

<%
  RestService __rs = (RestService) Application.getServiceFactory().lookup(Plugin.class,"rest");
%>

<style>
  div.service {
    border: 1px dotted #909090;
    background-color: #f9f9f9;
    background-image: url("/webadmin/img/panel-gray.gif");
    background-repeat: repeat-x;
    background-position: bottom;
    margin-bottom: 20px;
  }
  div.service h1 {
    background-color: #ECE7E5;
    border-bottom: 1px dotted #909090;
    color: #000000;
    font-weight: bold;
    font-size: 10pt;
    margin: 0px;
    padding: 3px 5px 3px 5px;
  }
  
  div.method {
    background-color: #ffffff;
    border: 1px solid #d0d0d0;
    margin-bottom: 10px;
  }
  
  div.method h1 {
    border-bottom: 1px dotted #d0d0d0;
    background-color: #f6f6f6;
    color: #505050;
    font-weight: bold;
    font-size: 9pt;
    margin: 0px;
    padding: 3px 5px 3px 5px;
  }

  div.content {
    padding: 10px;
  }

  div.description {
    padding: 0px 0px 15px 0px;
  }
  
  div.example {
    font-family: monospace;
  }
  
</style>

<div id="breadcrumbs">
  <a href="/webadmin/">Index</a>
  &raquo;
  <a href="/rest">REST-Services</a>
</div>

<h2>REST-Services</h2>

<% if (__rs.isStarted()) { %>
  <% for (RestBeanDoc __bd:__rs.getDoc()) { %>
  
    <div class="service">
      <h1><%= __bd.getBeanClass().getSimpleName() %></h1>

      <div class="content">
        <% if (__bd.getText() != null) { %>
          <div class="description"><%= __bd.getText() %></div>
        <% } %>
  
        <% for (RestMethodDoc __md:__bd.getMethods()) { %>
          <div class="method">
            <h1><%= __md.getPath() %></h1>
            
            <div class="content">
              <% if (__md.getText() != null) { %>
                <div class="description"><%= __md.getText() %></div>
              <% } %>
  
              <% if (__md.getExample() != null) { %>
                <div class="example">
                  Beispiel: <a href="<%= request.getRequestURI() %>/<%= __md.getExample() %>"><%= request.getRequestURI() %>/<%= __md.getExample() %></a>
                </div>
              <% } %>
            </div>

          </div>
        <% } %>
      </div>
    </div>
  <% } %>

<% } else { %>
  Der REST-Service ist nicht gestartet.
<% } %>

<%@ include file="inc.bottom.jspf" %>
