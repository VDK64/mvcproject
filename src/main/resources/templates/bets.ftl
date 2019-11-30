<#import "/header.ftl" as h>
  <#import "/scripter.ftl" as s>
    <@h.header admin=admin user=user position="bets">
      <link rel="stylesheet" href="../static/css/style.css">

      <div id="container-fluid" class="container-fluid" style="margin-left:10px; margin-top:10px">
        <div class="row">
          <p>Bets info.</p>
        </div>
        <div class="row">

        </div>
      </div>

      <@s.scripter class="container-fluid" />

    </@h.header>
