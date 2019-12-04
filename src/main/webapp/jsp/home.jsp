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
<script type="text/javascript">
function validateForm() {
	  var x = document.forms["uploadFile"]["files"].value;
	  if (x == "") {
	    alert("Please choose SPDX files");
	    return false;
	  }
	}
</script>
</head>

<ul>
  <li class = "active"><a href="/import">Import SPDX</a></li>
  <li><a href="/import/export" style="display:inline-block;">Export SPDX</a></li>
</ul>
<body>
  <div class="container h-200">
  <div class="h-200">
    <div class="row h-200 justify-content-center align-items-center">
      <div class="col-sm-8">
      <br>
        <h3>Upload SPDX Files to SW360</h3>
        <br>
        <form method="POST" enctype="multipart/form-data" id="fileUploadForm" action="/import/upload" name="uploadFile" onsubmit="return validateForm()">
          <div class="form-group">
            <input type="file" class="form-control" id="files" 
                placeholder="Upload Multiple Files"  name="files" multiple></input>
          </div>
          <br>
          <center><button type="submit" class="btn btn-default" id="btnSubmit" style="background: rgba(0,123,255,.5);">Upload SPDX FILE</button></center>
          <br>
        </form>
        <div>
          <hr>
        </div>
        <div style="overflow-x: scroll; height: 600px;">
        <c:if test="${not empty response}">
            <c:forEach items="${response}" var="item">
            	<h6>FileName:  ${item.fileName} </h6>
        		<c:if test="${not empty item.message}">
        		<h6>Messages</h6>
        		<c:forEach items="${item.message}" var="message">
        		<ul>
        			<li>${message}</li>
        		</ul>
        		</c:forEach>
        		</c:if>
        		 <hr>
        	</c:forEach>
        </c:if>
		</div>
      </div>
    </div>
  </div>
  </div>
</body>
</html>