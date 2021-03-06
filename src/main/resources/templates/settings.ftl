<#import "/header.ftl" as h>
  <@h.header admin=admin user=user position="settings">
  <link rel="stylesheet" href="../static/css/style.css">

  <input id="csrfHeaderName" value="${_csrf.headerName}" type="hidden">
  <input id="csrfToken" value="${_csrf.token}" type="hidden">
  <input id="newMessages" value="${newMessages?c}" type="hidden">
  <input id="newBets" value="${newBets?c}" type="hidden">

    <div id="mainDiv" class="container-fluid">
      <div class="row">
        <div class="col-md-12" style="margin-top: 10px">
          <#if user.avatar=="default">
            <img src="/img/avatar.png" class="img-thumbnail" style="width:150px">
            <#else>
            <img src="/img/${user.id}/${user.avatar}" class="img-thumbnail" style="width:150px">
          </#if>
        </div>
      </div>
      <form method="POST" enctype="multipart/form-data">
        <input name="${_csrf.parameterName}" value="${_csrf.token}" type="hidden">
        <input type="file" name="file" /><br /><br />
        <input type="submit" value="Submit" />
      </form><br />
      <form method="POST">
        <input name="${_csrf.parameterName}" value="${_csrf.token}" type="hidden">
        <input type="submit" name="button" placeholder="Delete avatar" value="Delete avatar" />
      </form><br />

      <div class="row">
        <div class="form-group">
          <div class="col-md-12" style="margin-top: 10px">
            <div class="form-group">
              Choose your data
              <form method="POST">
                <input name="${_csrf.parameterName}" value="${_csrf.token}" type="hidden">
                <input type="text" class="form-control" name="firstname" placeholder=${user.firstname}>
                <input type="text" class="form-control" name="lastname" placeholder=${user.lastname}>
                <button type="submit" name="changeData" class="btn btn-success" style="margin-top: 5px">Change data</button>
              </form>
            </div>
          </div>
        </div>

        <div class="form-group">
          <div class="col-md-12" style="margin-top: 10px">
            <div class="form-group">
              Draw or withdraw your deposit
              <form method="POST">
                <input name="${_csrf.parameterName}" value="${_csrf.token}" type="hidden">
                <input type="text" class="form-control" name="value" placeholder=${user.deposit}>
                <button type="submit" name="deposit" class="btn btn-success" style="margin-top: 5px">Deposit</button>
                <button type="submit" name="withdraw" class="btn btn-secondary" style="margin-top: 5px">Withdraw</button>
              </form>
            </div>
          </div>
        </div>

        <#if !user.steamId??>
          <div class="form-group">
            <div class="col-md-12" style="margin-top: 10px">
              <div class="form-group">
                Add your SteamId by pushing this:
                <a href="${auth}"><img src="/img/sign.png" class="img-thumbnail"></a>
              </div>
            </div>
          </div>
        </#if>
        </div>

      <div class="row">
        <div class="form-group">
        <div class="col-md-12" style="margin-top: 10px">
          <#if error??>
            <div id="errorSit" class="alert alert-danger alert-dismissible fade show" role="alert">
              ${error}
            </div>
          </#if>
          <#if ok??>
            <div id="successSit" class="alert alert-success alert-dismissible fade show" role="alert">
              ${ok}
            </div>
          </#if>
        </div>
      </div>
      </div>
      </div>

      <script type="text/javascript">
        let not = document.getElementById('errorSit');
        let ok = document.getElementById('successSit');
        if (not !== null) {
          setTimeout(() => {
            not.remove();
          }, 5000);
        }
        if (ok !== null) {
          setTimeout(() => {
            ok.remove();
          }, 5000);
        }
      </script>

      <script src="/static/js/sock.js"></script>
      <script src="/static/js/stomp.js"></script>
      <script src="/static/js/messageWebscoket.js"></script>
      <script src="/static/js/betWebscoket.js"></script>
  </@h.header>
