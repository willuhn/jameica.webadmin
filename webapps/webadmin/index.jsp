
<jsp:include page="inc.top.jspf"/>

<div id="breadcrumbs">
  <a href="/webadmin/">Index</a>
</div>

<table style="width:100%">
  <tr>
    <td style="border-right:1px dotted #909090;padding-right: 20px;width:650px">
    <jsp:include page="inc.status.jspf"/>
    <jsp:include page="inc.plugins.jspf"/>
    <jsp:include page="inc.certs.jspf"/>
    </td>
    <td style="padding-left: 20px;width:300px">
      <jsp:include page="inc.messages.jspf"/>
      <br/>
      <jsp:include page="inc.server.jspf"/>
      <br/>
      <jsp:include page="inc.rest.jspf"/>
    </td>
  </tr>
</table>

<div style="margin-top:20px">
  <jsp:include page="inc.log.jspf"/>
</div>

<jsp:include page="inc.bottom.jspf"/>
