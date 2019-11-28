<#import "/header.ftl" as h>
<#import "/scripter.ftl" as s>
  <@h.header admin=admin user=user position="friends">
    <link rel="stylesheet" href="../static/css/style.css">

    <div id="container-fluid" class="container-fluid">
      <#list friends as friend>
       <div class="row">
         <#if friend.avatar=="default">
           <img src="/img/avatar.png" class="img-thumbnail" style="width:50px">
           <#else>
             <img src="/img/${friend.id}/${friend.avatar}" class="img-thumbnail" style="width:150px">
         </#if>
       </div>
       <div class="row">
         <a href="#">${friend.firstname} ${friend.username} ${friend.lastname}</a>
       </div>
      </#list>
    </div>

    <@s.scripter class="container-fluid" />

  </@h.header>
