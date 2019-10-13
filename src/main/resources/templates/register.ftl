<!DOCTYPE html>
<html lang="en">
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- Latest compiled and minified CSS -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css"
          integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
    <!-- Optional theme -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap-theme.min.css"
          integrity="sha384-rHyoN1iRsVXV4nD0JutlnGaslCJuC7uwjduW9SVrLvRYooPp2bWYgmgJQIXwl/Sp" crossorigin="anonymous">
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
                            <input type="text" class="form-control" id="firstname" name="firstname"
                                   aria-describedby="firstname"
                                   placeholder="Enter firstname" name="firstname">
                        </div>
                        <div class="form-group">
                            <label for="lastname">lastname</label>
                            <input type="text" class="form-control" id="lastname" name="lastname"
                                   aria-describedby="lastname"
                                   placeholder="Enter lastname">
                        </div>
                        <div class="form-group">
                            <label for="username">username</label>
                            <input type="text" class="form-control" id="username" name="username"
                                   aria-describedby="username"
                                   placeholder="Enter username">
                            <small id="loginHelp" class="form-text text-muted">Username must be unique</small>
                        </div>
                        <div class="form-group">
                            <label for="password">password</label>
                            <input type="password" class="form-control" id="password" name="password"
                                   placeholder="password">
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
</body>
</html>