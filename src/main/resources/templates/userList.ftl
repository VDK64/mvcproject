<!DOCTYPE html>
<html lang="en">
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1" charset="UTF-8">
    <!-- Latest compiled and minified CSS -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css"
          integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
    <!-- Optional theme -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap-theme.min.css"
          integrity="sha384-rHyoN1iRsVXV4nD0JutlnGaslCJuC7uwjduW9SVrLvRYooPp2bWYgmgJQIXwl/Sp" crossorigin="anonymous">
</head>
<body>
<!--<div class="container">-->
    <table class="table">
        <#assign i=0>
        <caption><b><h3>Список пользователей</h3></b></caption>
        <thead>
        <tr>
            <th scope="col">#</th>
            <th scope="col">Username</th>
            <th scope="col">Имя</th>
            <th scope="col">Фамилия</th>
            <th scope="col">Роли</th>
            <th scope="col">Модерация</th>
        </tr>
        </thead>
        <tbody>
        <#list users as user>
        <tr>
            <th scope="row"> <#assign i++> ${i} </th>
            <td>${user.username}</td>
            <td>${user.firstname}</td>
            <td>${user.lastname}</td>
            <td><#list user.authorities as authority>${authority} <#sep>, </#sep> </#list></td>
            <td><a href="/admin/${user.id}"> Изменить </a> </td>
        </tr>
        </#list>
        </tbody>
    </table>
<!--</div>-->

</body>
</html>