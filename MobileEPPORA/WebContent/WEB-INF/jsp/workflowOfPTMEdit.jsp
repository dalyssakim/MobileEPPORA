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

table{
	width: 80%;
}

.looks_like_input {
    border: 0px;
    box-shadow: none;
    background-color: transparent;
    font-weight: bold;
}
</style>

<title>EPPORA</title>
</head>
<body>
		<div data-role="page" id="Div3" style="font-weight: bold;">
		<div data-position="fixed" data-role="header"  data-theme="c" data-text-align="center"> 
    <!-- 	<a data-icon="home" data-iconpos="notext" href="http://www.blogger.com/post-create.g?blogID=892712659100500876#home">Home</a>
    	  -->
 <a href="./Work Flow of Project Team Members.do"  data-role="button" data-theme="c">Back</a>
	<h1>	EPPORA - ${client.firstName}</h1> <a href="logout.do" type="submit"  data-theme="c" class="ui-btn-right">Sign Out</a>	
			</div>

<h2>${pageName}</h2>

<br>
<form:form modelAttribute="wfptm" id="target" method="POST" action="workflowofPtmUpdate.do">
<h4> <form:input class="looks_like_input" data-theme="none" path="projectName" readonly="true"/></h4>

<table data-role="table" id="expended">
<thead>
<tr style="border-bottom: 1px solid rgba(0, 0, 0, 1);" style="text-align:left;">
<th>Task Id :</th> <th> <form:input class="looks_like_input" data-theme="none" path="taskId" readonly="true"/> </th>
</tr>
</thead>
</table>
<b>
Project Team Member : ${wfptm.projectTeamMember}<br>
</b>
<table data-role="table" id="expended">
<thead>
<tr style="border-bottom: 1px solid rgba(0, 0, 0, 1);" style="text-align:left;">
<th>
Status</th><th> <select name="status">
	<option value="Not Started">Not Started</option>
	<option value="In progress">In progress</option>
	<option value="Done">Done</option>
</select>
</th>
</tr>
</thead>
</table>
<b>
Start Date : ${wfptm.startDate} <br>
Expended Hours To Date : ${wfptm.expendedHoursToDate} <br>
Estimated Hours : ${wfptm.estimatedHours} <br>
</b>

<table data-role="table" id="expended">
<thead>
<tr style="border-bottom: 1px solid rgba(0, 0, 0, 1);" style="text-align:left;">
<th>Expended Hours Today</th>  <th> <input id="expendedHoursToday" name="expendedHoursToday" type="text" placeholder="${wfptm.expendedHoursToday}"></th>
</tr>
</thead>
</table>
<b>
Progress :<c:set var="userq" value="${wfptm.pgDescription}"/>
 <c:if test="${userq == 'no progress'}">
 ${wfptm.pgDescription} <br>
 <input type="hidden" name="incrementalProgress" value="0">
 </c:if>
 <c:set var="userq" value="${wfptm.pgDescription}"/>
 <c:if test="${userq != 'no progress'}">
Description: ${wfptm.pgDescription}<br>
Incremental progress : <input id="incrementalProgress" name="incrementalProgress" type="text" placeholder="${wfptm.pgIncrementalProgress}"><br>
Description : ${wfptm.description}<br>
</c:if>
Message : <input id="message" name="message" type="text" value="${wfptm.message}"><br>
</b>
    	    <button  type="submit" name="Update">Update</button>
    	    <button  type="submit" name="Reject">Reject</button>
    	    <button  type="submit" name="Cancel">Cancel</button>

</form:form>
		<footer data-role="footer"  data-theme="c" data-position="fixed" ><h1>04/30/2014 Mobile EPPORA</h1></footer>
</div>
</body>
</html>