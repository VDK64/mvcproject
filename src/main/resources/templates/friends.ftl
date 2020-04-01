<#import "/header.ftl" as h>
  <@h.header admin=admin user=user position="friends">
    <link rel="stylesheet" href="../static/css/style.css">

    <style>
      .button {
        height: 5px;
        padding: 5px 5px;
      }
    </style>

    <input id="csrfHeaderName" value="${_csrf.headerName}" type="hidden">
    <input id="csrfToken" value="${_csrf.token}" type="hidden">
    <input id="newMessages" value="${newMessages?c}" type="hidden">
    <input id="newBets" value="${newBets?c}" type="hidden">

    <div id="mainDiv" class="container-fluid" style="margin-left:10px; margin-top:10px">
      <#if invites?size != 0>
      <#list invites as invite>
       <div class="row">
         <#if invite.avatar=="default">
           <img src="/img/avatar.png" class="img-thumbnail" style="width:50px">
           <#else>
             <img src="/img/${invite.id}/${invite.avatar}" class="img-thumbnail" style="width:150px">
         </#if>
       </div>
       <div class="row">
         ${invite.firstname} ${invite.username} ${invite.lastname}
         <#if invite.isOnline>
         <p>&nbsp&nbsp Online</p>
         </#if>
         &nbsp&nbsp
         <form method="post">
           <input name="${_csrf.parameterName}" value="${_csrf.token}" type="hidden">
           <input name="inviteUsername" value="${invite.username}" type="hidden">
           <button name="confirmInvite" type="submit" class="btn btn-success btn-sm button">
             Confirm invite!
           </button>
         </form>
       </div>
      </#list>
      <#else>

      <#if error??>
        <div class="alert alert-danger" role="alert">
          ${error}
        </div>
      </#if>




      <#if friends?size == 0>
      <div class="row">
        Sorry, you have not friends yet.
      </div>
      <#else>
      <#list friends as friend>
       <div class="row">
         <#if friend.avatar=="default">
           <img src="/img/avatar.png" class="img-thumbnail" style="width:50px">
           <#else>
             <img src="/img/${friend.id}/${friend.avatar}" class="img-thumbnail" style="width:150px">
         </#if>
       </div>
       <div class="row">
         <a href="/friend/${friend.id}">
           ${friend.firstname} ${friend.username} ${friend.lastname}
         </a>
         <#if friend.isOnline>
         <p>&nbsp&nbsp Online</p>
         </#if>
         &nbsp&nbsp
         <form method="post">
           <input name="${_csrf.parameterName}" value="${_csrf.token}" type="hidden">
           <input name="friendId" value="${friend.id}" type="hidden">
           <button name="sendMessageToFriend"
           type="submit" class="btn btn-primary btn-sm button">Send message</button>
         </form>
         &nbsp&nbsp
         <a href="/friends/find_friends">
           <button type="button" class="btn btn-success btn-sm button">Find Friends
           </button>
         </a>
       </div>
      </#list>
      </#if>
      <#list unconfirmeds as unconfirmed>
       <div class="row">
         <#if unconfirmed.avatar=="default">
           <img src="/img/avatar.png" class="img-thumbnail"
           style="width:50px">
           <#else>
             <img src="/img/${unconfirmed.id}/${unconfirmed.avatar}"
             class="img-thumbnail" style="width:150px">
         </#if>
       </div>
       <div class="row">
         <a href="/friend/${unconfirmed.id}">
           ${unconfirmed.firstname} ${unconfirmed.username} ${unconfirmed.lastname}
         </a>
         <#if unconfirmed.isOnline>
         <p>&nbsp&nbsp Online</p>
         </#if>
         &nbsp&nbsp Waiting for confirmation!
         &nbsp&nbsp
         <a href="/friends/find_friends">
           <button type="button" class="btn btn-success btn-sm button">Find Friends</button>
         </a>
       </div>
      </#list>
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
