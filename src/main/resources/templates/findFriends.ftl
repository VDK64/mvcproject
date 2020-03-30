<#import "/header.ftl" as h>
  <@h.header admin=admin user=user position="friends">
    <link rel="stylesheet" href="../static/css/style.css">

    <input id="csrfHeaderName" value="${_csrf.headerName}" type="hidden">
    <input id="csrfToken" value="${_csrf.token}" type="hidden">
    <input id="newMessages" value="${newMessages?c}" type="hidden">
    <input id="newBets" value="${newBets?c}" type="hidden">

    <div id="mainDiv" class="container-fluid" style="margin-left:10px">
      <div class="row">
        <div class="col-md-12" style="margin-top: 10px">
          It's page to find friends!
          <form method="post">
            <input name="${_csrf.parameterName}" value="${_csrf.token}" type="hidden">
            <button type="submit" class="btn btn-primary">Primary</button>
          </form>
        </div>
      </div>
    </div>

    <script src="/static/js/sock.js"></script>
    <script src="/static/js/stomp.js"></script>
    <script src="/static/js/messageWebscoket.js"></script>
    <script src="/static/js/betWebscoket.js"></script>
    <script type="text/javascript">
      document.body.style.overflow = "auto";
      document.body.style['overflow-x'] = "hidden";
    </script>

  </@h.header>
