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
          <#if user.avatar=="default">
            <img src="/img/avatar.png" class="img-thumbnail" style="width:150px">
            <#else>
              <img src="/img/${user.id}/${user.avatar}" class="img-thumbnail" style="width:150px">
          </#if>
          <div class="row">
            <h1>${user.firstname} ${user.username} ${user.lastname}</h1>
            <#if user.isOnline>
             online
            </#if>
          </div>
        </div>
      </div>
    </div>

    <script src="/static/js/sock.js"></script>
    <script src="/static/js/stomp.js"></script>
    <script src="/static/js/messageWebscoket.js"></script>
    <script src="/static/js/betWebscoket.js"></script>

  </@h.header>
