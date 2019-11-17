<#import "/header.ftl" as h>
  <@h.header admin=admin user=user position="editU">

    <div class="container" style="margin-top:45px">
      <div class="row">
        <div class="col-md-6 col-md-offset-3">
          <#if error??>
            <div class="alert alert-danger" role="alert">
              Username is already exist! Try another!
            </div>
          </#if>
          <form method="post">
            <input name="${_csrf.parameterName}" value="${_csrf.token}" type="hidden">
            <div class="form-group">
              <label for="firstname">Firstname</label>
              <input type="text" class="form-control" name="firstname" placeholder=${user.firstname}>
            </div>
            <div class="form-group">
              <label for="lastname">Lastname</label>
              <input type="text" class="form-control" name="lastname" placeholder=${user.lastname}>
            </div>
            <div class="form-group">
              <label for="username">Username</label>
              <input type="text" class="form-control" name="username" placeholder=${user.username}>
            </div>
            <div class="form-group">
              <label for="password">Password</label>
              <input type="password" class="form-control" name="password" placeholder="Password">
            </div>

            <#assign i=0>
              <#list authorities as authority>
                <#assign i++>
                  <div class="form-check">
                    <input class="form-check-input" type="checkbox" name="authority${i}" value=${authority} ${user.authorities?seq_contains(authority)?string("checked", "" )}>
                    <label class="form-check-label" for="exampleRadios1">
                      ${authority}
                    </label>
                  </div>
              </#list>
              <button type="submit" class="btn btn-primary" style="margin-top: 20px">Submit</button>
          </form>
        </div>
      </div>
    </div>

  </@h.header>
