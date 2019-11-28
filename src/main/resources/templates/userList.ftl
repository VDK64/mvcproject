<#import "/header.ftl" as h>
<#import "/scripter.ftl" as s>
  <@h.header admin=admin user=user position="admin">

    <div id="container" class="container" style="margin-top: 50px">
      <table class="table">
        <#assign i=0>
          <thead><b>
              <h3>Список пользователей</h3>
            </b></thead>
          <thead>
            <tr>
              <th scope="col">#</th>
              <th scope="col">Username</th>
              <th scope="col">Имя</th>
              <th scope="col">Фамилия</th>
              <th scope="col">Роли</th>
              <th scope="col">Модерация</th>
            </tr>
          </thead>
          <tbody>
            <#list users as user>
              <tr>
                <th scope="row">
                  <#assign i++> ${i}
                </th>
                <td>${user.username}</td>
                <td>${user.firstname}</td>
                <td>${user.lastname}</td>
                <td>
                  <#list user.authorities as authority> ${authority} <#sep>, </#sep>
                  </#list>
                </td>
                <td><a href="/admin/${user.id}"> Изменить </a></td>
              </tr>
            </#list>
          </tbody>
      </table>
    </div>

    <@s.scripter class="container" />

  </@h.header>
