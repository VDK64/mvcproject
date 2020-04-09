<#import "/header.ftl" as h>
  <@h.header admin=admin user=user position="editU">

  <input id="csrfHeaderName" value="${_csrf.headerName}" type="hidden">
  <input id="csrfToken" value="${_csrf.token}" type="hidden">
  <input id="newMessages" value="${newMessages?c}" type="hidden">
  <input id="newBets" value="${newBets?c}" type="hidden">

    <div class="container-fluid" style="margin-top:45px">
      <#if error??>
        <div class="alert alert-danger" role="alert">${error}</div>
      </#if>
      <div class="row">
        <div class="col-md-6 col-md-offset-3">
          <form method="post">
            <input name="${_csrf.parameterName}" value="${_csrf.token}" type="hidden">
            <div class="form-group">
              <label for="firstname">Firstname</label>
              <input type="text" class="form-control" name="firstname" placeholder=${find.firstname}>
            </div>
            <div class="form-group">
              <label for="lastname">Lastname</label>
              <input type="text" class="form-control" name="lastname" placeholder=${find.lastname}>
            </div>
            <div class="form-group">
              <label for="password">Password</label>
              <input type="password" class="form-control" name="password" placeholder="Password">
            </div>

            <#assign i=0>
              <#list authorities as authority>
                <#assign i++>
                  <div class="form-check">
                    <input class="form-check-input" type="checkbox"
                    name="authority${i}" value=${authority}
                    ${find.authorities?seq_contains(authority)?string("checked", "" )}>
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

    <script src="/static/js/sock.js"></script>
    <script src="/static/js/stomp.js"></script>
    <script src="/static/js/messageWebscoket.js"></script>
    <script src="/static/js/betWebscoket.js"></script>

  </@h.header>
