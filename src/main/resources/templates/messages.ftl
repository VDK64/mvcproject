<!doctype html>
<html lang="en">

<head>
  <!-- Required meta tags -->
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no" charset="UTF-8">

  <!-- Bootstrap CSS -->
  <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css" integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">
  <link rel="stylesheet" href="../static/css/style.css">
</head>

<body>

  <nav class="navbar navbar-expand-md navbar-dark bg-dark">
    <div class="navbar-collapse collapse w-100 order-1 order-md-0 dual-collapse2">
      <ul class="navbar-nav mr-auto">
        <li class="nav-item">
          <a class="nav-link" href="/">Home</a>
        </li>
        <#if admin??>
          <li class="nav-item">
            <a class="nav-link" href="/admin/userList">Admin Panel</a>
          </li>
        </#if>
        <li class="nav-item">
          <a class="nav-link" href="/settings">Settings</a>
        </li>
        <li class="nav-item active">
          <a class="nav-link" href="/dialogs">Messages</a>
        </li>
      </ul>
    </div>

    <div class="mx-auto order-0">
      <a class="navbar-brand mx-auto">
        <font color="#dcf2ee"> FriendBets </font>
      </a>
      <button class="navbar-toggler" type="button" data-toggle="collapse" data-target=".dual-collapse2">
        <span class="navbar-toggler-icon"></span>
      </button>
    </div>
    <div class="navbar-collapse collapse w-100 order-3 dual-collapse2">
      <ul class="navbar-nav ml-auto">
        <li class="nav-item">
          <a class="nav-link">
            <font color="#dcf2ee">${user.username}</font>
          </a>
        </li>
        <li class="nav-item">
          <a class="nav-link" href="/logout">Sign out</a>
        </li>
      </ul>
    </div>
  </nav>

    <div class="container" style="margin-top: 15px">
      <#if error??>
        <div class="alert alert-danger" role="alert">
          ${error}
        </div>
        <a href="/" class="badge badge-primary">Main page</a>
      <#else>
        <a href="/" class="badge badge-primary">Main page(exit)</a>
    </div>
    <div id="chat-page" class="">
      <div class="chat-container">
        <div class="chat-header">
          <h2>Spring WebSocket Chat Demo</h2>
        </div>
        <ul id="messageArea">
        </ul>
        <div class="form-group">
          <div class="input-group clearfix">
            <input type="text" id="message" placeholder="Write a message..." autocomplete="off" class="form-control" />
            <input type="text" id="to" placeholder="Write destination" autocomplete="off" class="form-control" />
            <button onclick="sendMessage();" class="primary">Send</button>
          </div>
        </div>
      </div>
    </div>
      </#if>

    <script src="/static/js/sock.js"></script>
    <script src="/static/js/stomp.js"></script>
    <script type="text/javascript">
      'use strict';
      var usernamePage = document.querySelector('#username-page');
      var chatPage = document.querySelector('#chat-page');
      var messageForm = document.querySelector('#messageForm');
      var messageInput = document.querySelector('#message');
      var messageArea = document.querySelector('#messageArea');
      var stompClient = Stomp.over(new SockJS('/room'));
      var username = null;
      var colors = [
        '#2196F3', '#32c787', '#00BCD4', '#ff5652',
        '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
      ];

      stompClient.connect({}, function(frame) {
        stompClient.subscribe('/user/queue/updates', function(msgOut) {
          onMessageReceived(msgOut);
        });
        username = frame.headers['user-name'];
      });

      function sendMessage(event) {
        var messageContent = messageInput.value.trim();
        var to = document.getElementById('to').value;
        if (messageContent && stompClient) {
          var chatMessage = {
            from: username,
            to: to,
            text: messageContent
          };
          stompClient.send("/app/room", {}, JSON.stringify(chatMessage));
          messageInput.value = '';
          var messageElement = document.createElement('li');
          messageElement.classList.add('chat-message');
          var avatarElement = document.createElement('i');
          var image = document.createElement('img');
          image.setAttribute('src', '/img/avatar.png');
          image.setAttribute('class', 'img-thumbnail');
          image.setAttribute('style', 'width:50px');
          image.setAttribute('border', '0');
          avatarElement.appendChild(image);
          messageElement.appendChild(avatarElement);
          var usernameElement = document.createElement('span');
          var usernameText = document.createTextNode(username);
          usernameElement.appendChild(usernameText);
          messageElement.appendChild(usernameElement);
          var textElement = document.createElement('p');
          var messageText = document.createTextNode(messageContent);
          textElement.appendChild(messageText);
          messageElement.appendChild(textElement);
          messageArea.appendChild(messageElement);
          messageArea.scrollTop = messageArea.scrollHeight;
        }
      }

      function onMessageReceived(payload) {
        var message = JSON.parse(payload.body);
        var messageElement = document.createElement('li');
        messageElement.classList.add('chat-message');
        var avatarElement = document.createElement('i');
        var image = document.createElement('img');
        image.setAttribute('src', '/img/avatar.png');
        image.setAttribute('class', 'img-thumbnail');
        image.setAttribute('style', 'width:50px');
        image.setAttribute('border', '0');
        avatarElement.appendChild(image);
        messageElement.appendChild(avatarElement);
        var usernameElement = document.createElement('span');
        var usernameText = document.createTextNode(message.from);
        usernameElement.appendChild(usernameText);
        messageElement.appendChild(usernameElement);
        var textElement = document.createElement('p');
        var messageText = document.createTextNode(message.text);
        textElement.appendChild(messageText);
        messageElement.appendChild(textElement);
        messageArea.appendChild(messageElement);
        messageArea.scrollTop = messageArea.scrollHeight;
      }

      function getAvatarColor(messageSender) {
        var hash = 0;
        for (var i = 0; i < messageSender.length; i++) {
          hash = 31 * hash + messageSender.charCodeAt(i);
        }
        var index = Math.abs(hash % colors.length);
        return colors[index];
      }
    </script>

    <script src="https://code.jquery.com/jquery-3.3.1.slim.min.js" integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo" crossorigin="anonymous"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js" integrity="sha384-UO2eT0CpHqdSJQ6hJty5KVphtPhzWj9WO1clHTMGa3JDZwrnQq4sF86dIHNDz0W1" crossorigin="anonymous"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js" integrity="sha384-JjSmVgyd0p3pXB1rRibZUAYoIIy6OrQ6VrjIEaFf/nJGzIxFDsf4x0xIM+B07jRM" crossorigin="anonymous"></script>

  </body>

  </html>
