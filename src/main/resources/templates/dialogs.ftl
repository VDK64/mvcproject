<#import "/header.ftl" as h>

  <@h.header admin=admin user=user position="messagesD">
  <link rel="stylesheet" href="../static/css/style.css">

  <input id="csrfHeaderName" value="${_csrf.headerName}" type="hidden">
  <input id="csrfToken" value="${_csrf.token}" type="hidden">
  <input id="newMessages" value="${newMessages?c}" type="hidden">
  <input id="newBets" value="${newBets?c}" type="hidden">

    <div id="mainDiv" class="container col-sm-6" style="margin-bottom: 30px">
      <table class="table">
        <thead align="center">
          <tr>
            <th scope="col">Dialogs</th>
          </tr>
        </thead>
        <tbody align="center">
          <#if dialogs??>
            <#list dialogs as dialog>
              <tr>
                <td>
                  <#if dialog.haveNewMessages && newMessages>
                  <b id="${dialog.username}-new"><a id="${dialog.username}" class="nav-link" href="/messages/${dialog.dialogId}">${dialog.firstname} ${dialog.username} ${dialog.lastname}</a></b>
                  <#else>
                  <a id="${dialog.username}" class="nav-link" href="/messages/${dialog.dialogId}">${dialog.firstname} ${dialog.username} ${dialog.lastname}</a>
                  </#if>
                </td>
              </tr>
            </#list>
            <#else>
              <tr>
                <td>
                  Sorry, no Dialogs
                </td>
              </tr>
          </#if>
        </tbody>
      </table>
    </div>

    <script src="/static/js/sock.js"></script>
    <script src="/static/js/stomp.js"></script>
    <script src="/static/js/messageWebscoket.js"></script>
    <script src="/static/js/betWebscoket.js"></script>

    <script type="text/javascript">
      document.body.style.overflow = "auto"
    </script>

    </@h.header>
