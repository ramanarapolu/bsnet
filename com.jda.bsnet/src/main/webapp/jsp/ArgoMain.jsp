<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<title>ARGO</title>
		<link rel="stylesheet" type="text/css" href="css/jquery-ui.css" />
		<link rel="stylesheet" type="text/css" href="css/argo.css">
	</head>
	<body>
		<div id="argoheader"><jsp:include page="headermain.jsp" /></div>
		<div id="argobody" class="marginTop10"><jsp:include page="login.jsp" /></div>
		<div id="argofooter"><jsp:include page="footer.jsp" /></div>
		<div id="argohtmlcontent" class="hideBlock"></div>
	</body>
	<script type="text/javascript" src="js/jquery-1.9.1.min.js"></script>
	<script type="text/javascript" src="js/jquery-ui.js"></script>
	<script type="text/javascript" src="js/argo.js"></script>
</html>