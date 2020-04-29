<#import "/header.ftl" as h>
  <@h.header admin=admin user=user position="bets">
    <link rel="stylesheet" href="../static/css/style.css">

    <input id="csrfHeaderName" value="${_csrf.headerName}" type="hidden">
    <input id="csrfToken" value="${_csrf.token}" type="hidden">
    <input id="newMessages" value="${newMessages?c}" type="hidden">
    <input id="newBets" value="${newBets?c}" type="hidden">

    <div id="mainDiv" class="container">

      <#if user.steamId??>
        <button type="button" class="btn btn-success" onclick="createBet();" name="button" style="margin-top: 10px">Create bet</button>
        <#else>
          <b style="color:#CDA31A">Please, go to the settings and add your steamId.</b>
      </#if>

      <h1 align="center">Bets info</h1>

      <form method="post">
        <input name="${_csrf.parameterName}" value="${_csrf.token}" type="hidden">
        <div class="form-group">
          <label for="table">Choose a table</label>
          <select name="table" class="form-control" id="table">
            <option>Owner</option>
            <option>Opponent</option>
          </select>
        </div>
        <button name="chooseTable" type="submit">Submit</button>
      </form>
      <h2 align="center">${tableName}</h2>
      <#if items??>
        <table class="table">
          <thead class="thead-dark">
            <tr>
              <th scope="col">#</th>
              <th scope="col"> firstname </th>
              <th scope="col"> username </th>
              <th scope="col"> lastname</th>
              <th scope="col"> firstname </th>
              <th scope="col"> username </th>
              <th scope="col"> lastname </th>
              <th scope="col"> value </th>
              <th scope="col"> Is confirmed </th>
              <th scope="col"> Win </th>
              <th scope="col"> Details </th>
              <th scope="col"> Delete </th>
            </tr>
          </thead>
          <tbody>
            <#assign i=0>
              <#list items as item>
                <#assign i++>
                  <tr>
                    <th scope="row"> ${i} </th>
                    <#if item.isNew>
                      <td><b>${item.user.firstname}</b></td>
                      <td><b>${item.user.username}</b></td>
                      <td><b>${item.user.lastname}</b></td>
                      <td><b>${item.opponent.firstname}</b></td>
                      <td><b>${item.opponent.username}</b></td>
                      <td><b>${item.opponent.lastname}</b></td>
                      <td><b>${item.value}</b></td>
                      <td><b>${item.isConfirm?c}</b></td>
                      <#if item.whoWin??>
                        <td><b>${item.whoWin}</b></td>
                        <#else>
                          <td><b>undefined</b></td>
                      </#if>
                      <td>
                        <button onclick="toBetDetails(${item.id})" type="button"<#if item.whoWin?? || !user.steamId??>disabled</#if>>
                          Details
                        </button>
                      </td>
                      <td>
                        <form method="post">
                          <input name="${_csrf.parameterName}" value="${_csrf.token}" type="hidden">
                          <input name="betId" value="${item.id}" type="hidden">
                          <input name="table" value="${tableName}" type="hidden">
                          <button name="deleteBet" type="submit" <#if item.isConfirm>disabled</#if>>Delete</button>
                        </form>
                      </td>
                      <#else>
                        <td>${item.user.firstname}</td>
                        <td>${item.user.username}</td>
                        <td>${item.user.lastname}</td>
                        <td>${item.opponent.firstname}</td>
                        <td>${item.opponent.username}</td>
                        <td>${item.opponent.lastname}</td>
                        <td>${item.value}</td>
                        <td>${item.isConfirm?c}</td>
                        <#if item.whoWin??>
                          <td>${item.whoWin}</td>
                          <#else>
                            <td>undefined</td>
                        </#if>
                        <td>
                          <button onclick="toBetDetails(${item.id})" type="button"<#if item.whoWin?? || !user.steamId??>disabled</#if>>
                            Details
                          </button>
                        </td>
                        <td>
                          <form method="post">
                            <input name="${_csrf.parameterName}" value="${_csrf.token}" type="hidden">
                            <input name="betId" value="${item.id}" type="hidden">
                            <input name="table" value="${tableName}" type="hidden">
                            <button name="deleteBet" type="submit" <#if item.isConfirm>disabled</#if>>Delete</button>
                          </form>
                        </td>
                    </#if>
                  </tr>
              </#list>
          </tbody>
        </table>
        <form method="post" style="margin-bottom: 30px">
          <input name="${_csrf.parameterName}" value="${_csrf.token}" type="hidden">
          <input type="number" size="3" name="page" min="1" max="${totalPages}" value="${currentPage}" step="1">
          <input name="tableName" value="${tableName}" type="hidden">
          <button name="tablePage" type="submit">Browse</button>
        </form>
      </#if>
    </div>

    <script src="/static/js/sock.js"></script>
    <script src="/static/js/stomp.js"></script>
    <script src="/static/js/messageWebscoket.js"></script>
    <script src="/static/js/betWebscoket.js"></script>

    <script type="text/javascript">
      document.body.style.overflow = "auto"
      document.body.style['overflow-x'] = "hidden";

      function createBet() {
        document.location.href = "/bets/createBet";
      }

      function toBetDetails(id) {
        window.location = "/bets/" + id;
      }
    </script>

  </@h.header>
