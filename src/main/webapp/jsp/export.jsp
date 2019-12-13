<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml"
	xmlns:th="http://www.thymeleaf.org"
	xmlns:sec="http://www.thymeleaf.org">
<head>
<title>Export excel file</title>
<meta charset="utf-8" />
<meta name="viewport" content="width=device-width, initial-scale=1" />
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/4.1.0/css/bootstrap.min.css">
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
<script
	src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.0/umd/popper.min.js"></script>
<script
	src="https://maxcdn.bootstrapcdn.com/bootstrap/4.1.0/js/bootstrap.min.js"></script>
<link rel="stylesheet" type="text/css"
	href="//cdnjs.cloudflare.com/ajax/libs/prettify/r298/prettify.min.css">
<script
	src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap4-duallistbox/4.0.1/jquery.bootstrap-duallistbox.js"></script>
<link rel="stylesheet" type="text/css"
	href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap4-duallistbox/4.0.1/bootstrap-duallistbox.css">
<style>

ul {
	list-style-type: none;
	margin: 0;
	padding: 0;
	overflow: hidden;
	background-color: rgba(0, 123, 255, .5);
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

li a :hover :not (
.active
)
{
background-color:rgba(0,123,255,.5);}
.active {
	background-color: rgba(0, 123, 255, .5);
}
</style>
<script>
function validateForm() {
	  var x = document.forms["downloadFile"]["spdxFields"].value;
	  if (x == "") {
	    alert("Please choose SPDX fields");
	    return false;
	  }
	}
</script>
</head>
<ul>
	<li><a href="/import">Import SPDX</a></li>
	<li class="active"><a href="/import/export"
		style="display: inline-block;">Export SPDX</a></li>
</ul>
<body>
	<div class="container h-200">
	<br>
		<h2>Choose SPDX fields for exporting</h2>
		<br> <br>
		<form id="fileDownload" name="downloadFile" method="POST" action="/import/download">
			<div class="row">
				<select multiple="multiple" name="spdxFields" class="spdxfields" id = "spdxexcel">
					<c:forEach var="mandatory" items="${mandatory}">
						<option value="${mandatory.spdxField}" selected="selected">${mandatory.header}</option>
					</c:forEach>
					<c:forEach var="optional" items="${optional}">
						<option value="${optional.spdxField}">${optional.header}</option>
					</c:forEach>
				</select>
				<script>
					var options = $('#spdxexcel').bootstrapDualListbox({
								nonSelectedListLabel : 'Spdx fields available: ',
								selectedListLabel : 'Spdx fields selected: ',
								preserveSelectionOnMove : 'moved',
								moveOnSelect : false,
								sortByInputOrder : true,
								removeSelectedLabel : true
							});
				</script>

			</div>
			<br> <br>
			<button type="submit" class="btn btn-default" id="btnSubmit" style="background: rgba(0, 123, 255, .5);" onclick="return validateForm()">Export SPDX lite</button>
		</form>
	</div>
</body>
</html>