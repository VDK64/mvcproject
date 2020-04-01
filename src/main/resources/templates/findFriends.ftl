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
      &nbsp&nbsp
      &nbsp&nbsp

      <#if error??>
        ${error}
      <#else>
      <#if findUser??>
      <div class="row">
        <#if findUser.avatar=="default">
          <img src="/img/avatar.png" class="img-thumbnail" style="width:50px">
          <#else>
            <img src="/img/${findUser.id}/${findUser.avatar}" class="img-thumbnail" style="width:150px">
        </#if>
      </div>
      <div class="row">
        <a href="/friend/${findUser.id}">
          ${findUser.firstname} ${findUser.username} ${findUser.lastname}
        </a>
        <#if findUser.isOnline>
        <p>&nbsp&nbsp Online</p>
        </#if>
        &nbsp&nbsp
        <form method="post">
          <input name="${_csrf.parameterName}" value="${_csrf.token}" type="hidden">
          <input name="friendId" value="${findUser.username}" type="hidden">
          <button name="addFriend"
          type="submit" class="btn btn-success btn-sm button">add to friends</button>
        </form>
    </div>
    </#if>
    </#if>
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
