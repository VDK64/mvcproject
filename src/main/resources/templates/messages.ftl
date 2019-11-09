<!doctype html>
<html lang="en">

<head>
  <!-- Required meta tags -->
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no" charset="UTF-8">

  <!-- Bootstrap CSS -->
  <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css" integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">
</head>

<script src="./static/js/sock.js"></script>
<script src="./static/js/stomp.js"></script>
<script type="text/javascript">
  var stompClient = null;

  function connect() {
    stompClient = Stomp.over(new SockJS('/secured/room'));
    stompClient.connect({}, function(frame) {
      console.log('Connected: ' + frame);
      stompClient.subscribe('/spring-security-mvc-socket/secured/room', function(messageOutput) {
        showMessageOutput(JSON.parse(messageOutput.body));
      });
    });
  }


  function disconnect() {
    if (stompClient != null) {
      stompClient.disconnect();
    }
    console.log("Disconnected");
  }

  function sendMessage() {
    var text = document.getElementById('text').value;
    var to = "${interlocutor.username}";
    var from = "${user.username}";
    var response = document.getElementById('response-area');
    var p = document.createElement('p');
    p.setAttribute('id', 'message-from');
    p.setAttribute('align', 'right');
    p.setAttribute('class', 'message-from');
    p.appendChild(document.createTextNode(text));
    response.appendChild(p);
    stompClient.send("/spring-security-mvc-socket/secured/room", {},
      JSON.stringify({
        'from': from,
        'to' : to,
        'text': text,
        'date' : null
      }));
  }

  function showMessageOutput(messageOutput) {
    var response = document.getElementById('response-area');
    var p = document.createElement('p');
    p.style.wordWrap = 'break-word';
    p.appendChild(document.createTextNode(messageOutput.from + ": " +
      messageOutput.text + " (" + messageOutput.time + ")"));
    response.appendChild(p);
  }

  connect();
</script>



<body>
  <style>
    body {
      background: #f5f5f5;
    }

    .message-from {
      color: #FF00FF;
    }

    .message-to {
      color: #0000FF;
    }

    .time-info {
      font-weight: lighter;
    }
  </style>
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
  <div class="container" id="response-area" style="margin-top: 15px">
    <#list messages as message>
      <#if message.from==user.username>
        <p align="right" id="message-from" class="message-from">
          <#if user.avatar??>
            <img src="/img/${user.id}/${user.avatar}" class="img-thumbnail" style="width:50px">
            <#else>
              <img src="/img/avatar.png" class="img-thumbnail" style="width:50px">
          </#if>
          ${message.text}
          <p align="right" id="time" class="time-info"><i> ${message.date} </i></p>
          <hr>
          <#else>
            <p align="left" id="message-to" class="message-to">
              <#if interlocutor.avatar??>
                <img src="/img/${interlocutor.id}/${interlocutor.avatar}" class="img-thumbnail" style="width:50px">
                <#else>
                  <img src="/img/avatar.png" class="img-thumbnail" style="width:50px">
              </#if>
              ${message.text}
              <p align="left" id="time" class="time-info"><i> ${message.date} </i></p>
              <hr>
      </#if>
    </#list>
    <input type="text" id="text" placeholder="Write a message..."/>
    <button id="sendMessage" onclick="sendMessage();">Send</button>
  </div>

  <script src="https://code.jquery.com/jquery-3.3.1.slim.min.js" integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo" crossorigin="anonymous"></script>
  <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js" integrity="sha384-UO2eT0CpHqdSJQ6hJty5KVphtPhzWj9WO1clHTMGa3JDZwrnQq4sF86dIHNDz0W1" crossorigin="anonymous"></script>
  <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js" integrity="sha384-JjSmVgyd0p3pXB1rRibZUAYoIIy6OrQ6VrjIEaFf/nJGzIxFDsf4x0xIM+B07jRM" crossorigin="anonymous"></script>

</body>

</html>
