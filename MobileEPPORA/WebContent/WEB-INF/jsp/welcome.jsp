<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>  
<%@taglib uri="http://java.sun.com/jsp/jstl/core"  prefix="c"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<html>
   <head>
	<!-- Dajung : Including jquery and jquery mobile scripts -->
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<!-- <link rel="stylesheet" href="jquery.mobile-1.4.2/jquery.mobile-1.4.2.css">
	<script src="jquery.mobile-1.4.2/jquery.mobile-1.4.2.js"></script>
	<script src="jquery.mobile-1.4.2/jquery-1.11.0.js"></script>-->
	       <link rel="stylesheet" href="http://code.jquery.com/mobile/1.2.0/jquery.mobile-1.2.0.min.css">
       <script src="http://code.jquery.com/jquery-1.8.2.min.js"></script> 
       <script src="http://code.jquery.com/mobile/1.2.0/jquery.mobile-1.2.0.min.js">
       </script>
	<!-- Dajung : End -->
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>EPPORA</title>
        
        <style>
<!-- CSS STYLES-->




</style>
    </head>
    <body>
   	<!-- Dajung : Upper bar -->
   	
    
		<section data-role="page" id="Div3">

		<div data-position="fixed" data-role="header" data-theme="c" align="center"> 
	<h1>EPPORA</h1>
	</div>
    <!-- Dajung : End -->
    	<div align="center" data-role="content" id="Div4">
    		<!-- EPPORA LOGO images -->
    		<img src="images/EPPORA-logo.png">
    		
        	<form id="Form1" action="login.do" method="post" >
            
        	<div data-role="fieldcontain">
        	<!--<label for="url">Email </label> -->
        	<input class="required" id="Text2" name="stEmail" type="email" placeholder="E-Mail">
        	</div>

        	<div data-role="fieldcontain">
        	<!--<label for="url">Password </label> -->
        	<input id="Password" name="stPwd" type="password" placeholder="Password">
        	</div>
        	
            <c:set var="userq" value="${question}"/>
            <c:if test="${userq != 'null'}">
            	<div data-role="fieldcontain">
            	<input id="userquestion" name="answer" type="text" placeholder="${question}">
            	</div>
            </c:if>
    	    <button data-theme="c" type="submit" name="login" value="login">Login</button>

    	   
    	   
	        <button data-theme="a" type="submit" name="forgotpassword" value="forgotpassword">Forgot Password</button>


        	</form>
		<p align="center">${popupMessage}</p>
		</div>
		<footer data-role="footer"  data-theme="c" data-position="fixed" ><h1>04/30/2014 Mobile EPPORA</h1></footer>
    	</section>
    </body>
</html>

