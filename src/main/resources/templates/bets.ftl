<#import "/header.ftl" as h>
  <#import "/scripter.ftl" as s>
    <@h.header admin=admin user=user position="bets">
      <link rel="stylesheet" href="../static/css/style.css">

      <div id="container-fluid" class="container-fluid" style="margin-left:10px; margin-top:10px">
        <div class="row">
          <p>It's a page, where you can make a bets with your friends. Please, choose a game and set a requered parameters, marking by *.</p>
        </div>
        <div class="row">
          <form method="post">
            <input name="${_csrf.parameterName}" value="${_csrf.token}" type="hidden">
            <div class="form-group">
              <label for="game">Choose a game</label>
              <select class="form-control" id="game">
                <option>Dota2</option>
              </select>
            </div>
            <div class="form-group">
              <label for="gamemode">Chose a gamemode</label>
              <select class="form-control" id="gamemode">
                <option>1x1</option>
              </select>
            </div>
            <div class="form-group">
              <label for="exampleFormControlTextarea1">Example textarea</label>
              <textarea class="form-control" id="exampleFormControlTextarea1" rows="3"></textarea>
            </div>
          </form>
        </div>
      </div>

      <@s.scripter class="container-fluid" />

    </@h.header>
