<#import "/header.ftl" as h>
  <@h.header admin=admin user=user position="home">
    <link rel="stylesheet" href="../static/css/style.css">

    <div id="container-fluid" class="container-fluid">
      <div class="row">
        <div class="col-md-12" style="margin-top: 10px">
          <#if user.avatar=="default">
            <img src="/img/avatar.png" class="img-thumbnail" style="width:150px">
            <#else>
              <img src="/img/${user.id}/${user.avatar}" class="img-thumbnail" style="width:150px">
          </#if>
          <div class="row">
            <h1>${user.firstname} ${user.username} ${user.lastname}</h1>
          </div>
        </div>
      </div>
    </div>

    <script src="/static/js/sock.js"></script>
    <script src="/static/js/stomp.js"></script>
    <script type="text/javascript">
      'use strict';
      var stompClient = Stomp.over(new SockJS('/room'));
      // stompClient.debug = null;
      var username = null;
      var headerName = "${_csrf.headerName}";
      var token = "${_csrf.token}";
      var headers = {};
      headers[headerName] = token;
      var newMessages = '${newMessages?c}';
      var once = false;

      onMessage('');

      console.log('once ' + once);

      console.log('newMessages ' + newMessages);

      event = new Event("message");

      document.addEventListener("message", function(event) {
        onMessage('true');
      });

      function showNotification(html) {
        let notification = document.createElement('div');
        let div = document.querySelector('#container-fluid');
        notification.className = "alert alert-info notification";
        notification.setAttribute('role', 'alert');
        notification.setAttribute('style', 'margin-top:10px; right:10px')
        notification.innerHTML = html;
        div.prepend(notification);
        setTimeout(() => notification.remove(), 5000);
      }

      stompClient.connect(headers, function(frame) {
        stompClient.subscribe('/user/queue/updates', function(msgOut) {
          newMessages = true;
          showNotification('You have new message');
          document.dispatchEvent(event);
        });
        username = frame.headers['user-name'];
      });

      function onMessage(arg) {
      if (arg === 'true' || newMessages == 'true') {
        if (!once) {
        setInterval(function() {
          var a = document.getElementById('messagesId').style.opacity || 1;
          document.getElementById('messagesId').style.opacity = ((parseInt(a)) ? 0 : 1);
        }, 450);
        once = true;
      }
    }
  }

    </script>

  </@h.header>
