<#import "/header.ftl" as h>
<#import "/scripter.ftl" as s>
  <@h.header admin=admin user=user position="home">
    <link rel="stylesheet" href="../static/css/style.css">

    <div id="container-fluid" class="container-fluid" style="margin-left:10px">
      <div class="row">
        <div class="col-md-12" style="margin-top: 10px">
          <#if user.avatar=="default">
            <img src="/img/avatar.png" class="img-thumbnail" style="width:150px">
            <#else>
              <img src="/img/${user.id}/${user.avatar}" class="img-thumbnail" style="width:150px">
          </#if>
          <div class="row">
            <h1>${user.firstname} ${user.username} ${user.lastname}</h1>
          </div>
        </div>
      </div>

      <table class="table">
        <thead align="center">
          <tr>
            <th scope="col">Users</th>
          </tr>
        </thead>
        <tbody align="center">
          <#if users??>
            <#list users as user>
              <tr>
                <td>
                  ${user}
                </td>
              </tr>
            </#list>
            <#else>
              <tr>
                <td>
                  Sorry, no Users
                </td>
              </tr>
          </#if>
        </tbody>
      </table>


    </div>

    <@s.scripter class="container-fluid" />

  </@h.header>
