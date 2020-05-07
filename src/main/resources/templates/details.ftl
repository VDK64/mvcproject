<#import "/header.ftl" as h>
  <@h.header admin=admin user=user position="bets">
    <link rel="stylesheet" href="../static/css/style.css">

    <input id="csrfHeaderName" value="${_csrf.headerName}" type="hidden">
    <input id="csrfToken" value="${_csrf.token}" type="hidden">
    <input id="newMessages" value="${newMessages?c}" type="hidden">
    <input id="newBets" value="${newBets?c}" type="hidden">
    <input id="username" value="${user.username}" type="hidden">

    <div id="mainDiv" class="container" style="margin-top:10px">
      <div class="row">
        <p style="color: #CE5C5C;">
          <b>
            Attention! You must open dota2 and will be ready to find a lobby,
            that created after pushing the button "Ready!". When you and your opponent would be ready, you have only 30 sec. to
            enter the lobby. If you or your opponent are late to come into the lobby - all repeats.
            Also, if you are not owner of this bet - you must confitm this bet to start the game. Just push button "confirm"
            if you see it (must be opponent).
          </b>
        </p>
      </div>

      <div class="row">
        <#if error??>
          <div id="errorMessage" class="alert alert-danger" role="alert">
            ${error}
          </div>
        </#if>

      </div>

      <#if bet??>
        <input id="betUser" value="${bet.user.username}" type="hidden">
        <input id="betOpponent" value="${bet.opponent.username}" type="hidden">
        <div class="row">
          <#if bet.opponent.username==user.username && !bet.isConfirm>
            <#assign itIsOpponent = true>
            <form method="post">
              <input name="${_csrf.parameterName}" value="${_csrf.token}" type="hidden">
              <button type="submit" name="confirmBet" class="btn btn-success">Confirm</button>
            </form>
          </#if>

        </div>

        <#if bet.isConfirm>
          <div class="row">
            <h1>Lobby name is <b><u>${bet.game.lobbyName}</u></b> Password of the lobby is <b><u>${bet.game.password}</u></b></h1>
          </div>
          <div class="row">
            <div id="userButton" class="col-6">
              <h1>Owner</h1>
              <p>${bet.user.firstname} ${bet.user.username} ${bet.user.lastname}</p>

              <#if bet.game.isUserReady>
                <p id="readyInfo-user">Ready!!!</p>
                <#else>
                  <#if bet.user.username==user.username>
                    <p><button id="button1" onclick="deleteButtonOnClick();" type="button" class="btn btn-success">Ready!</button></p>
                  </#if>
              </#if>

            </div>
            <div id="opponentButton" class="col-6">
              <h1>Opponent</h1>
              <p>${bet.opponent.firstname} ${bet.opponent.username} ${bet.opponent.lastname}</p>

              <#if bet.game.isOpponentReady>
                <p id="readyInfo-opponent">Ready!!!</p>
                <#else>
                  <#if bet.opponent.username==user.username>
                    <p><button id="button2" onclick="deleteButtonOnClick();" type="button" class="btn btn-success">Ready!</button></p>
                  </#if>
              </#if>

            </div>
          </div>
          <div id="mainRow" class="row">

            <#if bet.game.isUserReady?? && bet.game.isOpponentReady?? && bet.game.status??>
              <#if bet.game.isUserReady && bet.game.isOpponentReady && bet.game.status !="POSITIVE_LEAVE" && bet.game.status !="STARTED">
                <p id="loadingP">Creating lobby</p>
                <div id="loadingDiv" class="spinner-grow" style="width: 3rem; height: 3rem;" role="status">
                  <span id="loadingSpan" class="sr-only">Loading...</span>
                </div>
              </#if>
            </#if>

            <#if bet.game.status??>
              <#if bet.game.status=='STARTED'>
                <p>Lobby is created! Go to the dota 2</p>
              </#if>
              <#if bet.game.status=='POSITIVE_LEAVE'>
                <p>Push this button when the battle will be ended.
                  (Don't push for fun! It will be nothing while battle not over!)
                </p>
                <p>
                  <button id="checkBattleStatusButton" type="button" onclick="checkBattleStatus()" class="btn btn-info">check battle status</button>
                </p>
              </#if>
            </#if>
          </div>
          <#else>
            <#if itIsOpponent??>
              <p style="margin-top:30px">You must confirm this bet at first!!!</p>
            <#else>
              <p style="margin-top:30px">Opponent must confirm this bet at first!!!</p>
            </#if>
        </#if>

        <script src="/static/js/sock.js"></script>
        <script src="/static/js/stomp.js"></script>
        <script src="/static/js/messageWebscoket.js"></script>
        <script src="/static/js/betWebscoket.js"></script>

        <script type="text/javascript">
          let but1 = document.getElementById('button1');
          let but2 = document.getElementById('button2');

          function deleteButtonOnClick(event) {
            deleteButton();
            sendMessageAboutBet('ready');
          }

          function checkBattleStatus(event) {
            if (stompClient2) {
              let BetDto = {
                id: '${bet.id}',
                user: '${bet.user.username}',
                opponent: '${bet.opponent.username}',
                game: null,
                info: 'check'
              };
              let button = document.getElementById('checkBattleStatusButton');
              if (button != null) {
                button.parentNode.removeChild(button);
              }
              stompClient2.send("/app/bet", {}, JSON.stringify(BetDto));
            }
          }

          function ready() {
            if (but1 === null) {
              let div = document.getElementById('userButton');
              let p = document.createElement('p');
              p.setAttribute('id', 'readyInfo-user');
              p.innerHTML = 'Ready!!!';
              div.append(p);
            } else {
              let div = document.getElementById('opponentButton');
              let p = document.createElement('p');
              p.setAttribute('id', 'readyInfo-opponent');
              p.innerHTML = 'Ready!!!';
              div.append(p);
            }
          }

          function deleteButton() {
            if (but1 !== null) {
              let elem = but1.parentElement;
              but1.remove();
              let p = document.createElement('p');
              p.setAttribute('id', 'readyInfo-user');
              p.innerHTML = 'Ready!!!';
              elem.append(p);
            } else {
              let elem = but2.parentElement;
              but2.remove();
              let p = document.createElement('p');
              p.setAttribute('id', 'readyInfo-opponent');
              p.innerHTML = 'Ready!!!';
              elem.append(p);
            }
          }

          function sendMessageAboutBet(message) {
            if (message && stompClient2) {
              let BetDto = {
                id: '${bet.id}',
                user: '${bet.user.username}',
                opponent: '${bet.opponent.username}',
                game: null,
                info: message
              };
              stompClient2.send("/app/bet", {}, JSON.stringify(BetDto));
              let username = document.getElementById('username').value;
              let betUser = document.getElementById('betUser').value;
              let betOpponent = document.getElementById('betOpponent').value;
              if (username !== betUser) {
                let elem = document.getElementById('readyInfo-user');
                if (elem !== null) {
                  formLoading()
                }
              }
              if (username !== betOpponent) {
                let elem = document.getElementById('readyInfo-opponent');
                if (elem !== null) {
                  formLoading()
                }
              }
            }
          }

          function formLoading() {
            let text = document.createElement('p');
            text.setAttribute('id', 'loadingP');
            text.innerHTML = 'Creating lobby';
            let div = document.createElement('div');
            div.setAttribute('id', 'loadingDiv');
            div.setAttribute('class', 'spinner-grow');
            div.setAttribute('style', 'width: 3rem; height: 3rem;');
            div.setAttribute('role', 'status');
            let span = document.createElement('span');
            span.setAttribute('id', 'loadingSpan');
            span.setAttribute('class', 'sr-only');
            span.innerHTML = 'Loading...';
            let row = document.getElementById('mainRow');
            row.append(text);
            row.append(div);
            row.append(span);
          }
        </script>

      </#if>
    </div>




  </@h.header>
