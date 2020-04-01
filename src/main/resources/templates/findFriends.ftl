<#import "/header.ftl" as h>
  <@h.header admin=admin user=user position="friends">
    <link rel="stylesheet" href="../static/css/style.css">

    <input id="csrfHeaderName" value="${_csrf.headerName}" type="hidden">
    <input id="csrfToken" value="${_csrf.token}" type="hidden">
    <input id="newMessages" value="${newMessages?c}" type="hidden">
    <input id="newBets" value="${newBets?c}" type="hidden">

    <div id="mainDiv" class="container-fluid" style="margin-left:10px">
      <div class="row">
        <div class="col-md-3" style="margin-top: 10px">
          <form method="post">
            <div class="input-group input-group-sm mb-3">
              <div class="input-group-prepend">
                <span class="input-group-text" id="inputGroup-sizing-sm">username</span>
              </div>
              <input name="username" type="text" class="form-control"
              aria-label="Sizing example input" aria-describedby="inputGroup-sizing-sm">
            </div>
              <input name="${_csrf.parameterName}" value="${_csrf.token}" type="hidden">
              <button type="submit" class="btn btn-primary">Searc Friend</button>
          </form>
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
