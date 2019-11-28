<#import "/header.ftl" as h>
  <@h.header admin=admin user=user position="settings">

    <div class="container-fluid">
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
              <form method="POST">
                <input name="${_csrf.parameterName}" value="${_csrf.token}" type="hidden">
                <input type="text" class="form-control" name="firstname" placeholder=${user.firstname}>
                <input type="text" class="form-control" name="lastname" placeholder=${user.lastname}>
                <input type="text" class="form-control" name="username" placeholder=${user.username}>
                <!-- <button type="submit" name="button2">Change data</button> -->
                <button type="submit" name="button2" class="btn btn-success">Change data</button>
              </form>
            </div>
          </div>
        </div>
      </div>

      <div class="row">
        <div class="form-group">
        <div class="col-md-12" style="margin-top: 10px">
          <#if error??>
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
              ${error}
              <button type="button" class="close" data-dismiss="alert" aria-label="Close">
              <span aria-hidden="true">&times;</span>
              </button>
            </div>
          </#if>
          <#if ok??>
            <div class="alert alert-success alert-dismissible fade show" role="alert">
              Please, relogin to update changes!
              <button type="button" class="close" data-dismiss="alert" aria-label="Close">
              <span aria-hidden="true">&times;</span>
              </button>
            </div>
          </#if>
        </div>
      </div>
      </div>
      </div>
      </div>

  </@h.header>
