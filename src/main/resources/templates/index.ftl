<#import "/header.ftl" as h>
  <@h.header admin=admin user=user position="home">

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
    </div>

  </@h.header>
