<#import "/header.ftl" as h>
  <@h.header admin=admin user=user position="messages">
    <link rel="stylesheet" href="../static/css/style.css">

    <input id="newBets" value="${newBets?c}" type="hidden">
    <input id="csrfHeaderName" value="${_csrf.headerName}" type="hidden">
    <input id="csrfToken" value="${_csrf.token}" type="hidden">

    <div id="chat-page" class="chat-page">
      <div id="chat-container" class="chat-container">
        <div class="chat-header">
          <h2>Chat with ${interlocutor.username}</h2>
        </div>
        <ul id="messageArea">
          <#if messages??>
            <#list messages as message>
              <li class='chat-message'>
                <i>
                  <#if message.from==user.username>
                    <#if user.avatar="default">
                      <img src="/img/avatar.png" class="img-thumbnail" style="width:50px">
                      <#else>
                      <img src="/img/${user.id}/${user.avatar}" class="img-thumbnail" style="width:50px">
                    </#if>
                    <#else>
                      <#if interlocutor.avatar="default">
                        <img src="/img/avatar.png" class="img-thumbnail" style="width:50px">
                        <#else>
                        <img src="/img/${interlocutor.id}/${interlocutor.avatar}" class="img-thumbnail" style="width:50px">
                      </#if>
                  </#if>
                </i>
                <span>
                  ${message.from}
                </span>
                <p>
                  ${message.text}
                </p>
                <p id="messageDate">
                  ${message.date}
                </p>
              </li>
            </#list>
          </#if>
        </ul>
        <div class="form-group">
          <div class="input-group clearfix">
            <input type="text" id="message" placeholder="Write a message..." autocomplete="off" class="form-control" />
            <button onclick="sendMessage();" class="primary">Send</button>
          </div>
        </div>
      </div>
    </div>

    <script src="/static/js/sock.js"></script>
    <script src="/static/js/stomp.js"></script>
    <script src="/static/js/betWebscoket.js"></script>

    <script type="text/javascript">
      'use strict';
      var block = document.querySelector('#messageArea');
      block.scrollTop = block.scrollHeight;
      var usernamePage = document.querySelector('#username-page');
      var chatPage = document.querySelector('#mainDiv');
      var messageForm = document.querySelector('#messageForm');
      var messageInput = document.querySelector('#message');
      var messageArea = document.querySelector('#messageArea');
      var stompClient = Stomp.over(new SockJS('/room'));
      var stompClientM = Stomp.over(new SockJS('/newMessage'));
      stompClient.debug = null;
      var username = null;
      var headerName = "${_csrf.headerName}";
      var token = "${_csrf.token}";
      var headers = {};
      headers[headerName] = token;



      stompClient.connect(headers, function(frame) {
        stompClient.subscribe('/user/queue/updates', function(msgOut) {
          onMessageReceived(msgOut);
        });
        username = frame.headers['user-name'];
      });

      function updateMessage(payload) {
        let message = JSON.parse(payload.body);
        let chatMessage = {
          from: message.from,
          to: message.to,
          text: message.text,
          date: message.date,
          dialogId: '${dialogId}'
        };
          stompClientM.send("/app/newMessage", headers, JSON.stringify(chatMessage))
      }

      function sendMessage(event) {
        var messageContent = messageInput.value.trim();
        if (messageContent && stompClient) {
          var chatMessage = {
            from: username,
            to: '${interlocutor.username}',
            text: messageContent,
            date: new Date().toLocaleString(),
            dialogId: ${dialogId}
          };
          stompClient.send("/app/room", headers, JSON.stringify(chatMessage));
          messageInput.value = '';
          var messageElement = document.createElement('li');
          messageElement.classList.add('chat-message');
          var avatarElement = document.createElement('i');
          var image = document.createElement('img');
          var avatar = '${user.avatar}';
          if (avatar == 'default') {
            image.setAttribute('src', '/img/avatar.png');
          } else {
            image.setAttribute('src', '/img/${user.id}/${user.avatar}');
          }
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
          var textDate = document.createElement('p');
          textDate.setAttribute('id', 'messageDate');
          var messageText = document.createTextNode(messageContent);
          var messageDate = document.createTextNode(chatMessage.date);
          textElement.appendChild(messageText);
          textDate.appendChild(messageDate);
          messageElement.appendChild(textElement);
          messageElement.appendChild(textDate);
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
        var avatar = '${interlocutor.avatar}';
        if (avatar == "default") {
          image.setAttribute('src', '/img/avatar.png');
        } else {
          image.setAttribute('src', '/img/${interlocutor.id}/${interlocutor.avatar}');
        }
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
        var textDate = document.createElement('p');
        textDate.setAttribute('id', 'messageDate');
        var messageText = document.createTextNode(message.text);
        var messageDate = document.createTextNode(message.date);
        textElement.appendChild(messageText);
        textDate.appendChild(messageDate);
        messageElement.appendChild(textElement);
        messageElement.appendChild(textDate);
        messageArea.appendChild(messageElement);
        messageArea.scrollTop = messageArea.scrollHeight;
        updateMessage(payload);
      }


    </script>

  </@h.header>
