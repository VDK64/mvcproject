<#import "/header.ftl" as h>
<#import "/scripter.ftl" as s>
  <@h.header admin=admin user=user position="friends">
    <link rel="stylesheet" href="../static/css/style.css">

    <div id="container-fluid" class="container-fluid" style="margin-left:10px; margin-top:10px">
      <#list friends as friend>
       <div class="row">
         <#if friend.avatar=="default">
           <img src="/img/avatar.png" class="img-thumbnail" style="width:50px">
           <#else>
             <img src="/img/${friend.id}/${friend.avatar}" class="img-thumbnail" style="width:150px">
         </#if>
       </div>
       <div class="row">
         <a href="/${friend.id}">${friend.firstname} ${friend.username} ${friend.lastname} </a>
         <#if friend.isOnline>
         <p>&nbsp&nbsp Online</p>
         </#if>
       </div>
      </#list>
    </div>

    <@s.scripter class="container-fluid" />

  </@h.header>
