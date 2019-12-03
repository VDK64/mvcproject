'use strict';
var stompClient2 = Stomp.over(new SockJS('/bet'));
// stompClient2.debug = null;
var username = null;
var headerName = document.getElementById('csrfHeaderName').value;
var token = document.getElementById('csrfToken').value;
var headers = {};
headers[headerName] = token;
var newBets = document.getElementById('newBets').value;
var onceBet = false;
var urlBet = window.location.href.toString();

onBet('');

document.addEventListener("bet", function(event) {
  onBet('true');
  onNewBet();
});

function onNewBet(callback) {
  if (urlBet.includes('/bets')) {
    document.location.href = "/bets";
  }
}

function showNotification(html) {
  let notification = document.createElement('div');
  notification.setAttribute('id', 'notification');
  let div = document.getElementById('mainDiv');
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

stompClient2.connect(headers, function(frame) {
  stompClient2.subscribe('/user/queue/events', function(msgOut) {
    let message = JSON.parse(msgOut.body);
    if (message.info !== null) {
      betInfo(message.info);
    } else {
      newBets = true;
      if (!urlBet.includes('/bets')) {
        showNotification('Your friend call your to play!');
      }
      let event = new CustomEvent("bet");
      document.dispatchEvent(event);
    }
  });
  username = frame.headers['user-name'];
});

function betInfo(info) {
  switch (info) {
    case 'launchLobby':
      deleteButton();
      break;
  }
}

function onBet(arg) {
  if (arg === 'true' || newBets == 'true') {
    if (!onceBet) {
      setInterval(function() {
        var a = document.getElementById('betsId').style.opacity || 1;
        document.getElementById('betsId').style.opacity = ((parseInt(a)) ? 0 : 1);
      }, 450);
      onceBet = true;
    }
  }
}
