<#macro scripter class>

<script src="/static/js/sock.js"></script>
<script src="/static/js/stomp.js"></script>
<script type="text/javascript">
  'use strict';
  var stompClient = Stomp.over(new SockJS('/room'));
  stompClient.debug = null;
  var username = null;
  var headerName = "${_csrf.headerName}";
  var token = "${_csrf.token}";
  var headers = {};
  headers[headerName] = token;
  var newMessages = '${newMessages?c}';
  var once = false;
  var url = window.location.href.toString();

  // console.log(url);

  onMessage('');

  // console.log('once ' + once);

  // console.log('newMessages ' + newMessages);

  // event = new Event("message");

  document.addEventListener("message", function(event) {
    onMessage('true', event.detail);
  });

  function showNotification(html) {
    let notification = document.createElement('div');
    let div = document.getElementById('${class}');
    notification.className = "alert alert-info notification";
    notification.setAttribute('role', 'alert');
    notification.setAttribute('style', 'margin-top:10px; right:20px')
    notification.innerHTML = html;
    div.prepend(notification);

    setTimeout(() => notification.remove(), 5000);
  }

  stompClient.connect(headers, function(frame) {
    stompClient.subscribe('/user/queue/updates', function(msgOut) {
      newMessages = true;
      let message = JSON.parse(msgOut.body);
      showNotification('You have new message');
      let event = new CustomEvent("message", {'detail':message.from});
      // console.log(event);
      document.dispatchEvent(event);
    });
    username = frame.headers['user-name'];
  });

  function onMessage(arg, username) {
    if (arg === 'true' || newMessages == 'true') {
      if (!once) {
        setInterval(function() {
          var a = document.getElementById('messagesId').style.opacity || 1;
          document.getElementById('messagesId').style.opacity = ((parseInt(a)) ? 0 : 1);
        }, 450);
        if (url.includes('/dialogs')) { dialogStyle(username); }
        once = true;
      }
    }
  }

  function dialogStyle(username) {
    let elem = document.getElementById(username + '-new');
    if (!elem && username !== undefined) {
      let a = document.getElementById(username);
      console.log(a);
      let td = a.parentElement;
      let tr = td.parentElement;
      let tbody = tr.parentElement;
      let b = document.createElement('b');
      b.setAttribute('id', username + '-new');
      b.append(a);
      td.prepend(b);
      tr.prepend(td);
      tbody.prepend(tr);
    }
  }
</script>
</#macro>
