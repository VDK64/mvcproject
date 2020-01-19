'use strict';
var stompClient = Stomp.over(new SockJS('/room'));
stompClient.debug = null;
var username = null;
var headerName = document.getElementById('csrfHeaderName').value;
var token = document.getElementById('csrfToken').value;
var headers = {};
headers[headerName] = token;
var newMessages = document.getElementById('newMessages').value;
var onceMessage = false;
var url = window.location.href.toString();

onMessage('');

document.addEventListener("message", function(event) {
  onMessage('true', event.detail);
});

function showNotification(html) {
  let notification = document.createElement('div');
  notification.setAttribute('id', 'notification');
  let div = document.getElementById('mainDiv');
  console.log(div);
  notification.className = "alert alert-info notification";
  notification.setAttribute('role', 'alert');
  notification.setAttribute('style', 'margin-top:10px; right:20px')
  notification.innerHTML = html;
  let preNot = document.getElementById('notification');
  if (preNot !== null) {
    preNot.append(notification);
  } else {
    div.prepend(notification);
  }
  setTimeout(() => notification.remove(), 5000);
}

stompClient.connect(headers, function(frame) {
  stompClient.subscribe('/user/queue/updates', function(msgOut) {
    newMessages = true;
    let message = JSON.parse(msgOut.body);
    showNotification('You have new message');
    let event = new CustomEvent("message", {
      'detail': message.from
    });
    document.dispatchEvent(event);
  });
  username = frame.headers['user-name'];
});

function onMessage(arg, username) {
  if (arg === 'true' || newMessages == 'true') {
    if (!onceMessage) {
      setInterval(function() {
        var a = document.getElementById('messagesId').style.opacity || 1;
        document.getElementById('messagesId').style.opacity = ((parseInt(a)) ? 0 : 1);
      }, 450);
      if (url.includes('/dialogs')) {
        dialogStyle(username);
      }
      onceMessage = true;
    }
  }
}

function dialogStyle(username) {
  let elem = document.getElementById(username + '-new');
  if (!elem && username !== undefined) {
    let a = document.getElementById(username);
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
