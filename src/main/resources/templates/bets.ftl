<#import "/header.ftl" as h>
    <@h.header admin=admin user=user position="bets">
      <link rel="stylesheet" href="../static/css/style.css">

      <input id="csrfHeaderName" value="${_csrf.headerName}" type="hidden">
      <input id="csrfToken" value="${_csrf.token}" type="hidden">
      <input id="newMessages" value="${newMessages?c}" type="hidden">
      <input id="newBets" value="${newBets?c}" type="hidden">

      <div id="mainDiv" class="container">

        <button type="button" class="btn btn-success" onclick="createBet();" name="button" style="margin-top: 10px">Create bet</button>

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
                        <#if item.whoWin??>
                          <#else>
                            <td> <a class="nav-link" href="/bets/${item.id}"> See details</a> </td>
                        </#if>
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
                          <#if item.whoWin??>
                            <#else>
                              <td> <a class="nav-link" href="/bets/${item.id}"> See details</a> </td>
                          </#if>
                      </#if>
                    </tr>
                </#list>
            </tbody>
          </table>
          <form method="post" style="margin-bottom: 30px">
            <input name="${_csrf.parameterName}" value="${_csrf.token}" type="hidden">
            <input type="number" size="3" name="page" min="1" max="${totalPages}" value="1" step="1">
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

        function createBet() {
          document.location.href = "/bets/createBet";
        }
      </script>

    </@h.header>
