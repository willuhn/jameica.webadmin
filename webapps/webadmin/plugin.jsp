<%@ include file="inc.top.jspf" %>

<%@page import="java.util.List"%>
<%@page import="de.willuhn.datasource.Service"%>
<%@page import="de.willuhn.jameica.plugin.AbstractPlugin"%>
<%@page import="de.willuhn.jameica.plugin.Manifest"%>
<%@page import="de.willuhn.jameica.plugin.ServiceDescriptor"%>

<%
  String __pluginName = request.getParameter("plugin");
  Manifest __pluginMf = null;
  
  List __mfList = Application.getPluginLoader().getInstalledManifests();
  for (int i=0;i<__mfList.size();++i)
  {
    Manifest mf = (Manifest) __mfList.get(i);
    if (mf.getName().equals(__pluginName))
    {
      __pluginMf = mf;
      break;
    }
  }
  
  if (__pluginMf == null)
  {
    Application.addWelcomeMessage("Das Plugin " + __pluginName + " wurde nicht gefunden");
    response.sendRedirect("index.jsp");
  }
  
  Class __pluginClass = Application.getClassLoader().load(__pluginMf.getPluginClass());

  String __serviceAction = request.getParameter("action");
  String __serviceName   = request.getParameter("service");
  
  if (__serviceAction != null && __serviceName != null)
  {
    Service si = Application.getServiceFactory().lookup(__pluginClass,__serviceName);
    if (__serviceAction.equals("start"))
      si.start();
    else if (__serviceAction.equals("stop"))
      si.stop(true);
  }
  
%>

<div id="breadcrumbs">
  <a href="/webadmin/">Index</a>
  &raquo;
  <a href="plugin.jsp?plugin=<%= __pluginName %>">Plugin <%= __pluginName %></a>
</div>

<h2>Plugin <%= __pluginName %></h2>

<h3>Kenndaten</h3>

<table class="data">
  <tr>
    <td>Beschreibung</td>
    <td><%= __pluginMf.getDescription() %></td>
  </tr>
  <tr>
    <td>Version</td>
    <td><%= __pluginMf.getVersion() %> [Build <%= __pluginMf.getBuildnumber() %>]</td>
  </tr>
  <tr>
    <td>Verzeichnis</td>
    <td><%= __pluginMf.getPluginDir() %></td>
  </tr>
</table>

<h3>Services</h3>

<table class="data">
  <tr>
    <th>Identifier</th>
    <th>Name</th>
    <th>Klasse</th>
    <th>Abhängigkeiten</th>
    <th>Status</th>
    <th>Aktion</th>
  </tr>
  <%
    ServiceDescriptor[] __serviceList = __pluginMf.getServices();
  if (__serviceList != null)
  {
    for (int i=0;i<__serviceList.length;++i)
    {
      Service si = Application.getServiceFactory().lookup(__pluginClass,__serviceList[i].getName());
      String[] deps = __serviceList[i].depends();
      %>
        <tr onmouseover="change_to(this);" onmouseout="change_back(this);">
          <td><%= __serviceList[i].getName() %></td>
          <td><%= si.getName() %></td>
          <td><%= __serviceList[i].getClassname() %></td>
          <td>
            <% for (int k=0;k<deps.length;++k) { %>
              <%= deps[k] %>
              <br/>
            <% } %>
          </td>
    
          <% if (si.isStarted()) { %>
            <td style="color:#46824A">gestartet</td>
            <td><a onclick="return window.confirm('Service <%= __serviceList[i].getName() %> wirklich stoppen?');" href="plugin.jsp?plugin=<%= __pluginName %>&service=<%= __serviceList[i].getName() %>&action=stop">Stoppen</a>
          <% } else { %>
            <td style="color:#6E1416">NICHT gestartet</td>
            <td><a href="plugin.jsp?plugin=<%= __pluginName %>&service=<%= __serviceList[i].getName() %>&action=start">Starten</a>
          <% } %>
        </tr>
      <%
    }
  }
  %>
</table>

<%@ include file="inc.bottom.jspf" %>
