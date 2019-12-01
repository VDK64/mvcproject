<#import "/header.ftl" as h>
  <#import "/scripter.ftl" as s>
    <@h.header admin=admin user=user position="bets">
      <link rel="stylesheet" href="../static/css/style.css">


      <div id="container" class="container">

        <button type="button" class="btn btn-success" onclick="createBet();" name="button" style="margin-top: 10px">Create bet</button>

        <h1 align="center">Bets info.</h1>

        <h2 align="center"> Owner</h2>
        <table class="table">
          <thead class="thead-dark">
            <tr>
              <th scope="col">#</th>
              <th scope="col"> Owner firstname </th>
              <th scope="col"> Owner username </th>
              <th scope="col">Owner lastname</th>
              <th scope="col"> Opponent firstname </th>
              <th scope="col"> Opponent username </th>
              <th scope="col"> Opponent lastname </th>
              <th scope="col"> Bet value </th>
              <th scope="col"> Is confirmed </th>
              <th scope="col"> Win </th>
              <th scope="col"> Details </th>
            </tr>
          </thead>
          <tbody>
            <#assign i=0>
              <#list owners as owner>
                <#assign i++>
                  <tr>
                    <th scope="row"> ${i} </th>
                    <td> ${owner.user.firstname} </td>
                    <td> ${owner.user.username} </td>
                    <td> ${owner.user.lastname} </td>
                    <td> ${owner.opponent.firstname} </td>
                    <td> ${owner.opponent.username} </td>
                    <td> ${owner.opponent.lastname} </td>
                    <td> ${owner.value} </td>
                    <td> ${owner.isConfirm?c} </td>
                    <#if owner.whoWin??>
                      <td> ${owner.whoWin} </td>
                      <#else>
                        <td> undefined </td>
                    </#if>
                    <#if owner.whoWin??>
                      <#else>
                        <td> <a class="nav-link" href="/bets/${owner.id}"> See details</a> </td>
                    </#if>
                  </tr>
              </#list>
          </tbody>
        </table>

        <h2 align="center"> Opponent</h2><br>

        <table class="table">
          <thead class="thead-custom" style="background-color: #b5b1b1;">
            <tr>
              <th scope="col">#</th>
              <th scope="col"> Owner firstname </th>
              <th scope="col"> Owner username </th>
              <th scope="col">Owner lastname</th>
              <th scope="col"> Opponent firstname </th>
              <th scope="col"> Opponent username </th>
              <th scope="col"> Opponent lastname </th>
              <th scope="col"> Bet value </th>
              <th scope="col"> Is confirmed </th>
              <th scope="col"> Win </th>
              <th scope="col"> Details </th>
            </tr>
          </thead>
          <tbody>
            <#assign i=0>
              <#list opponents as opponent>
                <#assign i++>
                  <tr>
                    <th scope="row"> ${i} </th>
                    <td> ${opponent.user.firstname} </td>
                    <td> ${opponent.user.username} </td>
                    <td> ${opponent.user.lastname} </td>
                    <td> ${opponent.opponent.firstname} </td>
                    <td> ${opponent.opponent.username} </td>
                    <td> ${opponent.opponent.lastname} </td>
                    <td> ${opponent.value} </td>
                    <td> ${opponent.isConfirm?c} </td>
                    <#if opponent.whoWin??>
                      <td> ${opponent.whoWin} </td>
                      <#else>
                        <td> undefined </td>
                    </#if>
                    <#if opponent.whoWin??>
                      <#else>
                        <td> <a class="nav-link" href="/bets/${opponent.id}"> See details</a> </td>
                    </#if>
                  </tr>
              </#list>
          </tbody>
        </table>

      </div>

      <@s.scripter class="container" />

      <script type="text/javascript">
        document.body.style.overflow = "auto"

        function createBet() {
          document.location.href = "/bets/createBet";
        }
      </script>

    </@h.header>
