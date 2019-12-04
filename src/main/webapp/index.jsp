<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml"
    xmlns:th="http://www.thymeleaf.org"
    xmlns:sec="http://www.thymeleaf.org">
<head>
    <title>Upload Multiple Files</title>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
  <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.1.0/css/bootstrap.min.css">
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
  <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.0/umd/popper.min.js"></script>
  <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.1.0/js/bootstrap.min.js"></script>
  <style>
ul {
  list-style-type: none;
  margin: 0;
  padding: 0;
  overflow: hidden;
  background-color: rgba(0,123,255,.5);
}

li {
  float: left;
}

li a {
  display: block;
  color: white;
  text-align: center;
  padding: 14px 16px;
  text-decoration: none;
}

li a:hover:not(.active) {
  background-color: rgba(0,123,255,.5);
}

.active {
  background-color: rgba(0,123,255,.5);
}
</style>
</head>

<ul>
  <li><a href="/import">Import SPDX</a></li>
  <li><a href="/import/export">Export SPDX</a></li>
</ul>
<body>
  <div class="container h-200">
  <div class="h-200">
    <div class="row h-200 justify-content-center align-items-center">
      <div class="col-sm-8">
      <br>
        <h3>Import export SPDX Files</h3>
      </div>
    </div>
  </div>
  </div>
</body>
</html>