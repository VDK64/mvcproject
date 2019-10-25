<!doctype html>
<html lang="en">

<head>
  <!-- Required meta tags -->
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no" charset="UTF-8">

  <!-- Bootstrap CSS -->
  <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css" integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">

</head>

<body>
  <div class="container">
    <div class="row">
      <div class="col-md-6 col-md-offset-3">
        <div class="panel panel-default" style="margin-top:45px">
          <div class="panel-heading">
            <h3 class="panel-title">Registration</h3>
          </div>
          <div class="panel-body">
            <form method="post">
              <input name="${_csrf.parameterName}" value="${_csrf.token}" type="hidden">
              <div class="form-group">
                <label for="firstname">firstname</label>
                <input type="text" class="form-control" id="firstname" name="firstname" aria-describedby="firstname" placeholder="Enter firstname" name="firstname">
              </div>
              <div class="form-group">
                <label for="lastname">lastname</label>
                <input type="text" class="form-control" id="lastname" name="lastname" aria-describedby="lastname" placeholder="Enter lastname">
              </div>
              <div class="form-group">
                <label for="username">username</label>
                <input type="text" class="form-control" id="username" name="username" aria-describedby="username" placeholder="Enter username">
                <small id="loginHelp" class="form-text text-muted">Username must be unique</small>
              </div>
              <div class="form-group">
                <label for="password">password</label>
                <input type="password" class="form-control" id="password" name="password" placeholder="password">
              </div>
              <button type="submit" class="btn btn-primary">Registration</button>
              <a href="/login" style="margin-left: 30px">Sign in</a>
              <#if error??>
                <div class="alert alert-danger" role="alert">${error}</div>
              </#if>
          </div>
        </div>
      </div>
    </div>
  </div>
  </form>

  <script src="https://code.jquery.com/jquery-3.3.1.slim.min.js" integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo" crossorigin="anonymous"></script>
  <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js" integrity="sha384-UO2eT0CpHqdSJQ6hJty5KVphtPhzWj9WO1clHTMGa3JDZwrnQq4sF86dIHNDz0W1" crossorigin="anonymous"></script>
  <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js" integrity="sha384-JjSmVgyd0p3pXB1rRibZUAYoIIy6OrQ6VrjIEaFf/nJGzIxFDsf4x0xIM+B07jRM" crossorigin="anonymous"></script>

</body>

</html>
