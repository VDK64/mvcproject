<#import "/header.ftl" as h>
  <@h.header admin=admin user=user position="settings">

    <div class="container-fluid">
      <div class="row">
        <div class="col-md-12" style="margin-top: 10px">
          <#if user.avatar??>
            <img src="/img/${user.id}/${user.avatar}" class="img-thumbnail" style="width:150px">
            <#else>
              <img src="/img/avatar.png" class="img-thumbnail" style="width:150px">
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
      </form>
      <div class="row">
        <#if error??>
          <div class="alert alert-danger alert-dismissible fade show" role="alert">
            ${error}
            <button type="button" class="close" data-dismiss="alert" aria-label="Close">
              <span aria-hidden="true">&times;</span>
            </button>
          </div>
        </#if>
      </div>
    </div>
    </div>

  </@h.header>
