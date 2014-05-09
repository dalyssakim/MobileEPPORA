<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>  
<%@ taglib uri="http://java.sun.com/jsp/jstl/core"  prefix="c"%>
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

<title>EPPORA</title>
</head>
<body>
		<div data-role="page" id="Div3">
		<div data-position="fixed" data-role="header"  data-theme="c" data-text-align="center"> 
    <!-- 	<a data-icon="home" data-iconpos="notext" href="http://www.blogger.com/post-create.g?blogID=892712659100500876#home">Home</a>
    	  -->
 <a href="./Work Flow of Project Team Members.do"  data-role="button" data-theme="c">Back</a>
	<h1>	EPPORA - ${client.firstName}</h1> <a href="logout.do" type="submit"  data-theme="c" class="ui-btn-right">Sign Out</a>	
			</div>

<h2>${pageName}</h2>

<br>
<div class="ui-grid-solo">
Project Name : ${wfptm.projectName} <br>
</div>
<div class="ui-grid-solo">
Task Id : ${wfptm.taskId}</div><br>
<div class="ui-grid-solo">
Project Team Member : ${wfptm.projectTeamMember}</div> <br>
<div class="ui-grid-solo">
Start Date : ${wfptm.startDate}</div> <br>
<div class="ui-grid-solo">
Expended Hours To Date : ${wfptm.expendedHoursToDate} </div><br>
<div class="ui-grid-solo">
Estimated Hours : ${wfptm.estimatedHours}</div> <br>
<div class="ui-grid-solo">
Expended Hours Today : ${wfptm.expendedHoursToday}</div><br>
<div class="ui-grid-solo">
Progress: <c:set var="userq" value="${wfptm.pgDescription}"/>
 <c:if test="${userq == 'no progress'}">
	${wfptm.pgDescription}<br>
</c:if> <br>
 <c:set var="userq" value="${wfptm.pgDescription}"/>
 <c:if test="${userq != 'no progress'}">
	Description : ${wfptm.pgDescription}<br>
	Planned Quantity : ${wfptm.pgPlannedQuantity}<br>
	Incremental Progress : ${wfptm.pgIncrementalProgress}<br>
	Accomplished To Date : ${wfptm.pgAccomplishedToDate}<br>
</c:if></div>
<div class="ui-grid-solo">
Status : ${wfptm.status}</div><br>
<div class="ui-grid-solo">
Description : ${wfptm.description}</div><br>

		<footer data-role="footer"   data-theme="c" data-position="fixed" ><h1>04/30/2014 Mobile EPPORA</h1></footer>
</div>
</body>
</html>