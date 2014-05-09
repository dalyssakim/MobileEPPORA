<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>  
<%@taglib uri="http://java.sun.com/jsp/jstl/core"  prefix="c"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<html>
<head>
	<!-- Dajung : Including jquery and jquery mobile scripts -->
	<meta name="viewport" content="width=device-width, initial-scale=1">
<!-- 	<link rel="stylesheet" href="http://code.jquery.com/mobile/1.3.2/jquery.mobile-1.3.2.min.css">
	<script src="http://code.jquery.com/jquery-1.8.3.min.js"></script>
	<script src="http://code.jquery.com/mobile/1.3.2/jquery.mobile-1.3.2.min.js"></script>
	 -->
	        <link rel="stylesheet" href="http://code.jquery.com/mobile/1.2.0/jquery.mobile-1.2.0.min.css">
       <script src="http://code.jquery.com/jquery-1.8.2.min.js"></script> 
       <script src="http://code.jquery.com/mobile/1.2.0/jquery.mobile-1.2.0.min.js">
       </script>
	<!-- Dajung : End -->
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<script type="text/javascript">

</script>

<style>

table {
    color: black;
    background: #fff;
    border: 1px solid #b4b4b4;
    font: bold 17px helvetica;
    padding: 0;
    margin-top:5px;
    width: 100%;
    align: center;
    -webkit-border-radius: 8px;
}
     
table tr td {
    color: #666;
    border-bottom: 1px solid #b4b4b4;
    border-right: 1px solid #b4b4b4;
    align: center;
    padding: 5px 5px 5px 5px;
    background-images: -webkit-linear-gradient(top, #fdfdfd, #eee);
}
         
table tr td:last-child {
    border-right: none;
}


</style>

<title>EPPORA</title>
</head>
<body>
		<div data-role="page" id="Div3">
		<div data-position="fixed" data-role="header"  data-theme="c" data-text-align="center"> 
<a data-icon="home" data-iconpos="notext" href="./welcome.do"></a>

	<h2>	EPPORA - ${client.firstName}</h2> <a href="logout.do" type="submit"  data-theme="c" class="ui-btn-right">Sign Out</a>	
			</div>

<h3>Home</h3>
<ul data-role="listview">
<c:forEach var="homelist" items="${list}">
<li><a href="${homelist}.do"><c:out value="${homelist}"/></a></li>
</c:forEach>
</ul>

<!-- Dajung : Here should be form -->




<!-- <div data-role="controlgroup" data-direction="horizontal">
<c:forEach var="homelist" items="${list}">

	<a class="submit" data-method="POST" href="${homelist}.do"  data-role="button" data-action="${homelist}.do"  type="submit" id="${homelist}" ><c:out value="${homelist}"/></a>


<form method="post" action="${homelist}.do">
	<a href="${homelist}.do" data-role="button" data-method="post" type="submit" id="${homelist}" ><c:out value="${homelist}"/></a>
</form>

</c:forEach>
</div> -->

		<footer data-role="footer"  data-theme="c" data-position="fixed" ><h1>04/30/2014 Mobile EPPORA</h1></footer>
</div>

</body>
</html>