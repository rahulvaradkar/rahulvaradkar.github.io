package servlets;
/*
 *  This presents a list of collaboration available to a user
 */
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import com.boardwalk.exception.*;
import com.boardwalk.database.*;
import java.sql.*;                  // JDBC package
import javax.sql.*;                 // extended JDBC packa
import boardwalk.common.*;
import com.boardwalk.table.TableViewManager;


public class AggregationService extends xlService implements SingleThreadModel
{
	public void service(HttpServletRequest request,
		HttpServletResponse response)throws ServletException, IOException
	{

		TransactionManager tm = null;
		Connection connection = null;
		String responseBuffer = "FAILURE";


		String lsSourceTable = "";
		String lsTargetTable = "";

		String fullString = getRequestBuffer(request).toString();
		System.out.println("Data from client" + fullString);
		
		String[] receivedTokens = fullString.split(Seperator);
		System.out.println(" Seperator " + ".." + Seperator + "..");
		if (receivedTokens.length == 2 )
		{
			lsSourceTable	= receivedTokens[0]; // Source Table name
			lsTargetTable	= receivedTokens[1]; // Target Table name
		}
		if (receivedTokens.length == 1 )
			lsSourceTable	= receivedTokens[0]; // Source Table name

		try
		{
			System.out.println("Inside AggregationService lsSourceTable = " + lsSourceTable + "..");
			// parse the request

			// read the request
			//int aggrTblId = Integer.parseInt(request.getParameter("aggrTblId"));
			//System.out.println("Using aggregation definition in bw table id = " + aggrTblId);

			// Start a connection
			DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
			connection = databaseloader.getConnection();

			// Start a transaction
			//tm = new TransactionManager(connection, 1);
			//int tid = tm.startTransaction("Aggregation", "Aggregation");

			TableViewManager.aggregate(connection, lsSourceTable, lsTargetTable );

			//tm.commitTransaction();
			responseBuffer = "SUCCESS";

		}
		catch (Exception e)
		{
			try
			{
				tm.rollbackTransaction();
			}
			catch (SQLException se)
			{
				se.printStackTrace();
			}
			e.printStackTrace();
			responseBuffer = e.toString();
		}
		finally
		{
			try
			{
				if (connection != null)
					connection.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			commitResponseBuffer(responseBuffer, response);

		}
	}
}

