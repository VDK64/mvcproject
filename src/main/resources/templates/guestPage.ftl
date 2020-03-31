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
          <#if friend.avatar=="default">
            <img src="/img/avatar.png" class="img-thumbnail" style="width:150px">
          <#else>
            <img src="/img/${friend.id}/${friend.avatar}" class="img-thumbnail" style="width:150px">
          </#if>
          <div class="row">
            <h1>${friend.firstname} ${friend.username} ${friend.lastname}</h1>
            <#if friend.isOnline>
             online
            </#if>
          </div>
        </div>
        <form method="post">
          <input name="${_csrf.parameterName}" value="${_csrf.token}" type="hidden">
          <input name="friendId" value="${friend.id}" type="hidden">
          <button name="sendMessageToFriend"
          type="submit" class="btn btn-primary btn-sm button">Send message</button>
        </form>
      </div>
    </div>

    <script src="/static/js/sock.js"></script>
    <script src="/static/js/stomp.js"></script>
    <script src="/static/js/messageWebscoket.js"></script>
    <script src="/static/js/betWebscoket.js"></script>

  </@h.header>
