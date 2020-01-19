<#import "/header.ftl" as h>
    <@h.header admin=admin user=user position="">
      <link rel="stylesheet" href="../static/css/style.css">

      <input id="csrfHeaderName" value="${_csrf.headerName}" type="hidden">
      <input id="csrfToken" value="${_csrf.token}" type="hidden">
      <input id="newMessages" value="${newMessages?c}" type="hidden">
      <input id="newBets" value="${newBets?c}" type="hidden">

      <div id="mainDiv" class="container-fluid">
        <div align="center">
          <h1>ERROR!!!ACCESS DENIED!!!</h1>
        </div>
      </div>

      <script src="/static/js/sock.js"></script>
      <script src="/static/js/stomp.js"></script>
      <script src="/static/js/messageWebscoket.js"></script>
      <script src="/static/js/betWebscoket.js"></script>

    </@h.header>
