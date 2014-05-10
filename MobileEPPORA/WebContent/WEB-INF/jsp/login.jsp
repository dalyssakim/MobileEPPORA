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
		
		
		
<script type="text/javascript">

$('a.edit').live('click', (function(){

	
    var value = "command=" + $(this).attr("id");

    
 
    
    $.ajax({
    	type : "GET",
    	url : "edit.do",
    	data : value,
    	success : function(response){
   
    		 window.location.replace("./edit2.do");
    		}
    });
 
    
    $.mobile.loading('show');

    
}));

$('a.hover').live('click',(function(){
	   confirm($(this).attr("id"));
}));

$('a.info').live('click', (function(){

	   confirm($(this).attr("id")+" is clicked.");
    var value = "command=" + $(this).attr("id");
    $.ajax({
    	type : "GET",
    	url : "info.do",
    	data : value,
    	success : function(response){
   
    		 window.location.replace("./info2.do");
    		}
    });
  

    
    $.mobile.loading('show');

    
}));

</script>

		
		<div data-position="fixed" data-role="header" data-theme="c" data-text-align="center"> 
    <!-- 	<a data-icon="home" data-iconpos="notext" href="http://www.blogger.com/post-create.g?blogID=892712659100500876#home">Home</a>
    	  
    	  data-rel="back" for back button
    	  -->
 <a href="./login.do" data-role="button" data-theme="c" >Back</a>
	<h1>	EPPORA - ${client.firstName}</h1> <a href="logout.do" type="submit" data-theme="c" class="ui-btn-right">Sign Out</a>	
			</div>


<!--	   confirm($(this).attr("id")+" is clicked."); -->

<h2>${pageName}</h2> <p align="right">Last Record : ${lastrecord}</p>
<!-- ${pageName} here -->
<!-- Dajung : Here should be form -->
<div data-role="content" align="center">
<table>
<tr align="center">
<c:forEach var="c" items="${columns}">
<td  ><c:out value="${c}"/></td>
</c:forEach>
</tr>

<c:set var="loadPage" value="${pageName}"/>
<c:choose>
<c:when test="${loadPage == 'Work Flow'}">
<c:forEach var="workflowlist" items="${wflist}" varStatus="status">
<tr align="center">
<td    ><a class="edit" href="" data-method="GET" data-theme="none" data-corner="false" id="${loadPage}-${workflowlist.taskId}"><img src="images/b_edit.png" height="30" width="30"></a>
<a class="info" href="" id="${loadPage}-${workflowlist.taskId}"><img src="images/b_info.png" height="30" width="30"></a>
</td>
<td> <a class="hover" href="" id="${workflowlist.projectName}"><img src="images/b_description.gif" height="30" width="30"></a></td>
<td    ><c:out value="${workflowlist.schId}"/></td>
<td  ><c:out value="${workflowlist.status}"/><br>
<td  ></td>
</tr>
</c:forEach>
</c:when>

<c:when test="${loadPage == 'Messages'}">
<c:forEach var="messagelist" items="${list}">
<tr align="center">
<td  ><a class="edit" href="" id="${loadPage}-${messagelist.recId}"><img src="images/b_drop.png" height="30" width="30"></a></td>
<td  ><a class="hover" href="" id="${messagelist.projectName}"><img src="images/b_description.gif" height="30" width="30"></a></td>
<td  ><c:out value="${messagelist.message}"/></td>
<td  ><a class="hover" href="" id="${messagelist.description}"><img src="images/b_description.gif" height="30" width="30"></a></td>
</tr>
</c:forEach>
</c:when>

<c:when test="${loadPage == 'Work Flow of Project Team Members'}">
<c:forEach var="ptmlist" items="${list}">
<tr align="center">
<td  ><a class="edit" href="" data-method="GET" data-theme="none" data-corner="false" id="${loadPage}-${ptmlist.taskId}"><img src="images/b_edit.png"></a></td>
<td  ><a class="hover" href="" id="${ptmlist.projectName}"><img src="images/b_description.gif" height="30" width="30"></a></td>
<td  ><c:out value="${ptmlist.schId}"/></td>
<td  ><c:out value="${ptmlist.status}"/></td>
<td  ><a class="info" href="" id="${loadPage}-${ptmlist.taskId}"><img src="images/b_info.png" height="30" width="30"></a></td>
</tr>
</c:forEach>
</c:when>

<c:when test="${loadPage == 'Executive Project Portfolio Dashboard'}">
<c:forEach var="dashboard" items="${list}">
<tr align="center">
<td  ><a class="hover" href="" id="${dashboard.projectName}"><img src="images/b_description.gif"></a></td>
<td  ><c:out value="${dashboard.spi}"/></td>
<td  ><c:out value="${dashboard.cpi}"/></td>
<td  ><a class="info" href="" id="${loadPage}-${dashboard.projectName}"><img src="images/b_info.png"></a></td>

</tr>
</c:forEach>
</c:when>

</c:choose>
</table>

</div>
		<footer data-role="footer"  data-theme="c" data-position="fixed" ><h1>05/05/2014 Mobile EPPORA</h1></footer>


</div>
</body>


</html>








