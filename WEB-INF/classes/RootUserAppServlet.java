// A simple servlet to process get requests.
// Main servlet in first-example web-app
/*   Name: Joshua Frazer 
     Course: CNT 4714 – Spring 2022 – Project Four 
     Assignment title:  A Three-Tier Distributed Web-Based Application 
     Date:  April 24, 2022 
*/ 
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.*;
import java.util.Properties;

import com.mysql.cj.jdbc.MysqlDataSource;

public class RootUserAppServlet extends HttpServlet {   
   // process "get" requests from clients
	private Connection connection;
	private Statement statement;
	
	public void getDBConnection()
	{
	   // TECHNIQUE 1: using a properties file  
	   //--------------------------------------
	    Properties properties = new Properties();
	    FileInputStream filein = null;
	    MysqlDataSource dataSource = null;
	    //read a properties file
	    try {
	    	filein = new FileInputStream("C:/Program Files/Apache Software Foundation/Tomcat 10.0/webapps/Project4/WEB-INF/lib/root.properties");
	    	properties.load(filein);
	    	dataSource = new MysqlDataSource();
	    	dataSource.setURL(properties.getProperty("MYSQL_DB_URL"));
	    	dataSource.setUser(properties.getProperty("MYSQL_DB_USERNAME"));
	    	dataSource.setPassword(properties.getProperty("MYSQL_DB_PASSWORD"));	 
	    	connection = dataSource.getConnection();
		    statement = connection.createStatement();
	    }
	    catch (SQLException e){
			e.printStackTrace();	
		}
	    catch (IOException e) {
	    	e.printStackTrace();
	    }
	}
	
	@Override
	protected void doPost( HttpServletRequest request, 
	  HttpServletResponse response ) throws ServletException, IOException  
	{
		  // Get SQL Statement and Initialized Variables
		  String sqlStatement = request.getParameter("sqlStatement");
		  String[] splitSqlStatement = sqlStatement.split(" ");
		  String finishedResults = "";
		  StringBuffer pendingResults = new StringBuffer();
		  boolean isInitialized = false;
		  boolean isAlternated = false;
		  
		  // Get Database Connection
		  getDBConnection();
		  
		  try 
		  {
			  if(splitSqlStatement[0].trim().toLowerCase().equals("select")) 
			  {
				  ResultSet resultIterator = statement.executeQuery(sqlStatement);
				  ResultSetMetaData metadata = resultIterator.getMetaData();
				  int columnCount = metadata.getColumnCount();
				  // Process Table Data For Search
				  while (resultIterator.next()) 
				  {
					  if (!isInitialized)
					  {
						  pendingResults.append("<tr bgcolor=red>");
			                for (int i = 1; i <= columnCount; i++)
			                {
			                    isInitialized = true;
			                    pendingResults.append("<td style=\"text-align:center; padding: 0px 5px 0px 5px; color: black; font-weight:bolder\">");
			                    pendingResults.append(metadata.getColumnName(i).substring(0, 1).toUpperCase() + metadata.getColumnName(i).substring(1));
			                    pendingResults.append("</td>");
			                }
			                pendingResults.append("</tr>");
					  }
					  
					  if (!isAlternated)
						  pendingResults.append("<tr bgcolor=lightgrey>");
					  else
						  pendingResults.append("<tr bgcolor=white>");
					  
					  isAlternated = !isAlternated;
					  
					  for (int i = 0; i < columnCount; i ++) 
					  {
						  pendingResults.append("<td style=\"text-align:center; padding: 0px 5px 0px 5px; color: black;\">");
						  pendingResults.append(resultIterator.getString(i + 1));
						  pendingResults.append("</td>");
					  }
					  pendingResults.append("</tr>");
				  }
			  }
			  else
			  {
				  statement.addBatch("drop table if exists beforeShipments;");
				  statement.addBatch("create table beforeShipments like shipments;");
				  statement.addBatch("insert into beforeShipments select * from shipments;");
				  statement.executeBatch();
				  int affectedRows = statement.executeUpdate(sqlStatement);
				  pendingResults.append("<tr bgcolor=lime><td style=\"text-align:center; font-weight:bolder\"><font color=black>");
				  pendingResults.append("The statement executed successfully.");
				  int suppliersChanged = statement.executeUpdate("update suppliers"
				  		+ " set status = status + 5"
				  		+ " where suppliers.snum in"
				  		+ " (select distinct snum"
				  		+ " from shipments"
				  		+ " where shipments.quantity >= 100"
				  		+ " and"
				  		+ " not exists (select *"
				  		+ " from beforeShipments"
				  		+ " where shipments.snum = beforeShipments.snum"
				  		+ " and shipments.pnum = beforeShipments.pnum"
				  		+ " and shipments.jnum = beforeShipments.jnum"
				  		+ " and beforeShipments.quantity >= 100"
				  		+ " ));");
				  statement.execute("drop table beforeShipments;");
				  
				  boolean detectedBusinessLogic = false;
				  if (suppliersChanged > 0)
					  detectedBusinessLogic = true;
				  else
					  detectedBusinessLogic = false;
				  
				  if (detectedBusinessLogic)
				  {
					  pendingResults.append("<br>");
					  pendingResults.append(affectedRows + " row(s) was affected.<br><br>");
					  pendingResults.append("Business Logic Detected! - Updating Supplier Status<br><br>");
					  pendingResults.append("Business Logic updated " + suppliersChanged + " supplier status marks.");
				  }
				  else 
				  {
					  pendingResults.append(" A total of " + affectedRows + " rows were updated.<br><br> Business Logic Not Triggered!<br><br>");
				  }
				  pendingResults.append("</font></td></tr>");
			  }
			  
			  // Grab Final Results To Display To User
			  finishedResults = pendingResults.toString();
			  
			  statement.close();
		  }
		  catch (SQLException ex) 
		  {
			  finishedResults = "<tr bgcolor=#ff0000><td><font color=#ffffff><b>Error executing the sql statement:</b><br>" + ex.getMessage() + "</td></tr></font>";
		  }
		  HttpSession session = request.getSession();
		  session.setAttribute("sqlStatement", sqlStatement);
		  session.setAttribute("results", finishedResults);
		  RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/rootHome.jsp");
		  dispatcher.forward(request, response);
	}
}
