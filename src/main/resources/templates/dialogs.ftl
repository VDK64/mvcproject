<#import "/header.ftl" as h>
<#import "/scripter.ftl" as s>
  <@h.header admin=admin user=user position="messagesD">
  <link rel="stylesheet" href="../static/css/style.css">

    <div id="container col-sm-6" class="container col-sm-6">
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

    <@s.scripter class="container col-sm-6" />
    </@h.header>
