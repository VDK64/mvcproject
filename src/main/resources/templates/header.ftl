<#macro header admin user position>
  <!doctype html>
  <html lang="en">

  <head>
    <!-- Required meta tags -->
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no" charset="UTF-8">
    <#if position=="messagesD">
      <title>dialogs</title>
    <#else>
      <title>${position}</title>
    </#if>
    <!-- Bootstrap CSS -->
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css" integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">
    <!-- <link rel="stylesheet" href="../static/css/style.css"> -->
  </head>

  <body>
    <nav id="navbar-trg" class="navbar navbar-expand-md navbar-dark bg-dark">
      <div class="navbar-collapse collapse w-100 order-1 order-md-0 dual-collapse2">
        <ul class="navbar-nav mr-auto">
          <#if position=="home">
            <li class="nav-item active">
              <a class="nav-link" href="/">Home</a>
            </li>
            <#else>
              <li class="nav-item">
                <a class="nav-link" href="/">Home</a>
              </li>
          </#if>
          <#if position=="friends">
            <li class="nav-item active">
              <a class="nav-link" href="/friends">Friends</a>
            </li>
            <#else>
              <li class="nav-item">
                <a class="nav-link" href="/friends">Friends</a>
              </li>
          </#if>
          <#if position=="bets">
            <li id="betsId" class="nav-item active">
              <a class="nav-link" href="/bets">Bets</a>
            </li>
            <#else>
              <li id="betsId" class="nav-item">
                <a class="nav-link" href="/bets">Bets</a>
              </li>
          </#if>
          <#if admin>
            <#if position=="admin">
              <li class="nav-item active">
                <a class="nav-link" href="/admin/userList">Admin Panel</a>
              </li>
              <#else>
                <li class="nav-item">
                  <a class="nav-link" href="/admin/userList">Admin Panel</a>
                </li>
            </#if>
            <#if position="email">
              <li class="nav-item active">
                <a class="nav-link" href="/">Confirm Email</a>
              </li>
            </#if>

            <#if position=="editU">
              <li class="nav-item active">
                <a class="nav-link" href="/admin/userList">Edit User</a>
              </li>
            </#if>
          </#if>
          <#if position=="settings">
            <li class="nav-item active">
              <a class="nav-link" href="/settings">Settings</a>
            </li>
            <#else>
              <li class="nav-item">
                <a class="nav-link" href="/settings">Settings</a>
              </li>
          </#if>
          <#if position=="messages" || position=="messagesD">
            <li id="messagesId" class="nav-item active">
              <a class="nav-link" href="/dialogs">Messages</a>
            </li>
            <#else>
              <li id="messagesId" class="nav-item">
                <a class="nav-link" href="/dialogs">Messages</a>
              </li>
          </#if>
        </ul>
      </div>
      <div class="mx-auto order-0">
        <a class="navbar-brand mx-auto">
          <font color="#dcf2ee"> FriendsBets </font>
        </a>
        <button class="navbar-toggler" type="button" data-toggle="collapse" data-target=".dual-collapse2">
          <span class="navbar-toggler-icon"></span>
        </button>
      </div>
      <div class="navbar-collapse collapse w-100 order-3 dual-collapse2">
        <ul class="navbar-nav ml-auto">
          <li class="nav-item">
            <a class="nav-link">
              <font id="depositOfUser" color="#dcf2ee">Deposit: ${user.deposit} &#8381</font>
            </a>
          </li>
          <li class="nav-item">
            <a class="nav-link">
              <#if user.username??>
                <font color="#dcf2ee">${user.username}</font>
              </#if>
            </a>
          </li>
          <li class="nav-item">
            <a class="nav-link" href="/logout">Sign out</a>
          </li>
        </ul>
      </div>
    </nav>
    <#nested>
      <script src="https://code.jquery.com/jquery-3.3.1.slim.min.js" integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo" crossorigin="anonymous"></script>
      <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js" integrity="sha384-UO2eT0CpHqdSJQ6hJty5KVphtPhzWj9WO1clHTMGa3JDZwrnQq4sF86dIHNDz0W1" crossorigin="anonymous"></script>
      <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js" integrity="sha384-JjSmVgyd0p3pXB1rRibZUAYoIIy6OrQ6VrjIEaFf/nJGzIxFDsf4x0xIM+B07jRM" crossorigin="anonymous"></script>
  </body>

  </html>
</#macro>
