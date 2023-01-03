<!DOCTYPE html">
<%
	String sqlStatement = (String) session.getAttribute("sqlStatement");
	if (sqlStatement == null)
   		sqlStatement = "select * from suppliers";

	String results = (String) session.getAttribute("results");
   	if (results == null) 
   		results = "";
%>
<html>
<head>
	<title>Root User App Servlet</title>
	<style type='text/css'>
	<!--  body{background-color:black; color:white; font-family:calibri;}
	 h1{font-size:40pt; text-align:center; color: yellow;} h2{font-size:35pt; text-align:center; color: lime;} 
	 p{font-size:15pt; text-align:center;}
	 textarea{display: block; margin-left: auto; margin-right: auto; background-color: blue; color: white; font-size: 14pt;}
	 input{background-color:#3b3a3a; font-weight:bolder; font-size:13pt; margin:15pt 5pt 15pt 5pt;} .execute{color:lime;} .reset{color:red;} .clear{color:yellow;}
	 div{display:flex; justify-content:center;}
	 .results{font-weight:bold;}
	-->
	</style>
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
	<script type="text/javascript">
		function eraseText() {
			$("#userCommand").html("");
		}
	</script>
	<script type="text/javascript">
		function eraseData() {
			$("#data").remove();
		}
	</script>
</head>
<body>
	<h1>Welcome to Spring 2022 Project 4 Enterprise Database System</h1>
	<h2>A Servlet/JSP-based Multi-tiered Enterprise Application Using a Tomcat Container</h2>
	<hr></hr>
	<p>You are connected to the Project 4 Enterprise System database as a <font color=red>root-level</font> user.<br>
	Please enter any valid SQL query or update command in the box below.</p>
	<form action="RootUserAppServlet" method="post">
		<textarea id="userCommand" name="sqlStatement" rows="15" cols="90"><%=sqlStatement%></textarea>
		<div><input type="submit" class="execute" value="Execute Command"/>
		<input type="reset" class="reset" value="Reset Form" onclick="javascript:eraseText();"/>
		<input type="button" class="clear" value="Clear Results" onclick="javascript:eraseData();"/></div>
	</form>
	<p>All execution results will appear below this line.</p>
	<hr></hr>
	<p class="results">Database Results</p>
	<table id="data" cellspacing="5" align="center">
	<%=results%>
	</table>
</body>
</html>