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
<form method="post">
    <input name="${_csrf.parameterName}" value="${_csrf.token}" type="hidden">
    <div class="form-group">
        <label for="firstname">firstname</label>
        <input type="text" class="form-control" id="firstname" name="firstname" aria-describedby="firstname"
               placeholder="Enter firstname" name="firstname">
    </div>
    <div class="form-group">
        <label for="lastname">lastname</label>
        <input type="text" class="form-control" id="lastname" name="lastname" aria-describedby="lastname"
               placeholder="Enter lastname">
    </div>
    <div class="form-group">
        <label for="username">username</label>
        <input type="text" class="form-control" id="username" name="username" aria-describedby="username"
               placeholder="Enter username">
        <small id="loginHelp" class="form-text text-muted">Login must be unique</small>
    </div>
    <div class="form-group">
        <label for="password">password</label>
        <input type="password" class="form-control" id="password" name="password" placeholder="password">
    </div>
    <button type="submit" class="btn btn-primary">Register</button>
        <#if error??>
            <div class="alert alert-danger" role="alert">${error}</div>
        </#if>
</form>
</body>
</html>