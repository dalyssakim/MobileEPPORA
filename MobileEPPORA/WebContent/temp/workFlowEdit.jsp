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

<title>EPPORA</title>
</head>
<body>
		<div data-role="page" id="Div3">
		<div data-position="fixed" data-role="header"  data-theme="c" data-text-align="center"> 
    <!-- 	<a data-icon="home" data-iconpos="notext" href="http://www.blogger.com/post-create.g?blogID=892712659100500876#home">Home</a>
    	  -->
 <a href="./Work Flow.do"  data-role="button" data-theme="c">Back</a>
	<h1>	EPPORA - ${client.firstName}</h1> <a href="logout.do" type="submit"  data-theme="c" class="ui-btn-right">Sign Out</a>	
			</div>

<h2>${pageName}</h2>

<br>
<form:form modelAttribute="wf" id="target" method="post" action="workflowedit.do">
<div id="pnfield" data-role="fieldcontain">
<label id="projectNameTag" for="projectname">Project Name :</label>
<form:input id="projectname" name="projectname" path="projectName" readonly="true"/>
</div>
Task Id : <form:input path="schId" readonly="true"/> 
Status : <select name="status">
	<option value="Not Started">Not Started</option>
	<option value="In progress">In progress</option>
	<option value="Done">Done</option>
</select>
Start Date : ${wf.startDate} <br>
Expended Hours To Date : ${wf.expectedHoursToDate} <br>
Estimated Hours : ${wf.estimatedHours} <br>

<!-- <div id="expendedHoursField" data-role="fieldcontain"> -->
<table data-role="table" id="expended">
<thead>
<tr>
<th>Expended Hours Today</th>
<th><input type="text" name="expendedHoursToday" id="expendedHoursToday" value=""/></th>
</tr>
</thead>
</table>


<br>Progress :<c:set var="userq" value="${wf.pgDescription}"/>
 <c:if test="${userq == 'no progress'}">
 ${wf.pgDescription} <br>
 <input type="hidden" name="incrementalProgress" value="0">
 </c:if>
 <c:set var="userq" value="${wf.pgDescription}"/>
 <c:if test="${userq != 'no progress'}">
Description: ${wf.pgDescription}<br>
Incremental progress : <input maxlength="10" id="incrementalProgress" name="incrementalProgress" type="text" placeholder="${wf.pgIncrementalProgress}"><br>
Description : ${wf.description}<br>
</c:if>
Message : ${wf.message}<br>

    	    <button  type="submit" name="send" value="send">Send</button>
    	    <button type="submit" name="Cancel" value="cancel">Cancel</button>
</form:form>
		<footer data-role="footer"  data-theme="c" data-position="fixed" ><h1>05/02/2014 Mobile EPPORA</h1></footer>
</div>
</body>
</html>