<#import "/header.ftl" as h>
  <@h.header admin=admin user=user position="messagesD">
  

    <div class="container col-sm-6">
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
                  <a class="nav-link" href="/messages/${dialog.dialogId}"> ${dialog.username} </a>
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

    </@h.header>
