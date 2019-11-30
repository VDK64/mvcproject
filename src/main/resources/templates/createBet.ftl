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
              <select name="game" class="form-control" id="game">
                <option>Dota2</option>
              </select>
            </div>
            <div class="form-group">
              <label for="gamemode">Chose a gamemode</label>
              <select name="gamemode" class="form-control" id="gamemode">
                <option>1x1</option>
                <option>who win next match</option>
              </select>
            </div>
            <label>Bet value</label>
            <div class="input-group mb-3">
              <div class="input-group-prepend">
                <span class="input-group-text">$</span>
              </div>
              <input type="text" name="value" class="form-control" aria-label="Amount (to the nearest dollar)">
              <div class="input-group-append">
                <span class="input-group-text">.00</span>
              </div>
            </div>
            <button type="submit" class="btn btn-success" name="button">Create bet</button>
          </form>
        </div>
      </div>

      <@s.scripter class="container-fluid" />

    </@h.header>
