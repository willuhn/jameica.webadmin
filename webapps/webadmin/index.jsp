<%@ include file="inc.top.jspf" %>

<div id="breadcrumbs">
  <a href="/webadmin/">Index</a>
</div>

<table style="width:100%">
  <tr>
    <td style="border-right:1px dotted #909090;padding-right: 20px;width:650px">
      <%@ include file="inc.status.jspf" %>
      <%@ include file="inc.plugins.jspf" %>
      <%@ include file="inc.certs.jspf" %>
    </td>
    <td style="padding-left: 20px;width:300px">
      <%@ include file="inc.messages.jspf" %>
    </td>
  </tr>
</table>

<div style="margin-top:20px">
  <%@ include file="inc.log.jspf" %>
</div>

<%@ include file="inc.bottom.jspf" %>
