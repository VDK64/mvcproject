<#import "/header.ftl" as h>
  <#import "/scripter.ftl" as s>
    <@h.header admin=admin user=user position="bets">
      <link rel="stylesheet" href="../static/css/style.css">


      <div id="container" class="container">

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
              <th scope="col"> ${tableName} firstname </th>
              <th scope="col"> ${tableName} username </th>
              <th scope="col">${tableName} lastname</th>
              <th scope="col"> ${tableName} firstname </th>
              <th scope="col"> ${tableName} username </th>
              <th scope="col"> ${tableName} lastname </th>
              <th scope="col"> ${tableName} value </th>
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
                    <td> ${item.user.firstname} </td>
                    <td> ${item.user.username} </td>
                    <td> ${item.user.lastname} </td>
                    <td> ${item.opponent.firstname} </td>
                    <td> ${item.opponent.username} </td>
                    <td> ${item.opponent.lastname} </td>
                    <td> ${item.value} </td>
                    <td> ${item.isConfirm?c} </td>
                    <#if item.whoWin??>
                      <td> ${item.whoWin} </td>
                      <#else>
                        <td> undefined </td>
                    </#if>
                    <#if item.whoWin??>
                      <#else>
                        <td> <a class="nav-link" href="/bets/${item.id}"> See details</a> </td>
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

      <@s.scripter class="container" />

      <script type="text/javascript">
        document.body.style.overflow = "auto"

        function createBet() {
          document.location.href = "/bets/createBet";
        }
      </script>

    </@h.header>
