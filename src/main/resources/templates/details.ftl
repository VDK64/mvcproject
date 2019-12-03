<#import "/header.ftl" as h>
  <#import "/scripter.ftl" as s>
    <@h.header admin=admin user=user position="bets">
      <link rel="stylesheet" href="../static/css/style.css">

      <div id="container" class="container" style="margin-top:10px">
        <div class="row">
          <div class="col-6">
            <h1>Owner</h1>
              <p>${bet.user.firstname} ${bet.user.username} ${bet.user.lastname}</p>
              <#if bet.user.username == user.username>
              <p><button id="button1" onclick="deleteButtonOnClick();" type="button" class="btn btn-success">Start server</button></p>
              </#if>
          </div>
          <div class="col-6">
            <h1>Opponent</h1>
              <p>${bet.opponent.firstname} ${bet.opponent.username} ${bet.opponent.lastname}</p>
              <#if bet.opponent.username == user.username>
              <p><button id="button2" onclick="deleteButtonOnClick();" type="button" class="btn btn-success">Start server</button></p>
              </#if>
          </div>
        </div>
      </div>


      <@s.scripter class="container" />

      <script type="text/javascript">
        let but1 = document.getElementById('button1');
        let but2 = document.getElementById('button2');

        function deleteButtonOnClick(event) {
          deleteButton();
          sendMessageAboutBet('launchLobby')
        }

        function deleteButton() {
          if (but1 !== null) {
            but1.remove();
          } else {
            but2.remove();
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
        }
      }
      </script>

    </@h.header>
