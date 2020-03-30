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
         <a href="/friend/${friend.id}">${friend.firstname} ${friend.username} ${friend.lastname} </a>
         <#if friend.isOnline>
         <p>&nbsp&nbsp Online</p>
         </#if>
         &nbsp&nbsp <button type="button" class="btn btn-primary btn-sm button">Send message</button>
         &nbsp&nbsp <a href="/friends/find_friends"><button type="button" class="btn btn-success btn-sm button">Find Friends</button></a>
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
