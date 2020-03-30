<#import "/header.ftl" as h>
  <@h.header admin=admin user=user position="admin">
  <link rel="stylesheet" href="../static/css/style.css">

  <input id="csrfHeaderName" value="${_csrf.headerName}" type="hidden">
  <input id="csrfToken" value="${_csrf.token}" type="hidden">
  <input id="newMessages" value="${newMessages?c}" type="hidden">
  <input id="newBets" value="${newBets?c}" type="hidden">


    <div id="mainDiv" class="container" style="margin-top: 50px">
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

      <div class="row" style="margin-top: 20px">
        <form method="post">
          <input name="${_csrf.parameterName}" value="${_csrf.token}" type="hidden">
          <button type="submit" name="getToken">Get Token</button>
        </form>
      </div>
      <div id="secretToken" class="row" style="margin-top:20px">
      <#if token??>
        Token: ${token}
      </#if>
    </div>
    </div>
    <script src="/static/js/sock.js"></script>
    <script src="/static/js/stomp.js"></script>
    <script src="/static/js/messageWebscoket.js"></script>
    <script src="/static/js/betWebscoket.js"></script>
    <script type="text/javascript">
      let elem = document.getElementById('secretToken');
      if (elem !== null) {
        setTimeout(() => {
          elem.remove();
        }, 5000);
      }
      document.body.style.overflow = "auto";
      document.body.style['overflow-x'] = "hidden";      
    </script>
  </@h.header>
