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
import boardwalk.table.*;

public class xlCallPeriodShift extends xlService implements SingleThreadModel
{
	public void service(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException
	{
		Connection connection		= null;
		Connection connectionQuery	= null;
		String responseBuffer		= "FAILURE";

		response.setContentType("text/plain");
		ServletOutputStream servletOut = response.getOutputStream();

		int liStateTableId		= -1;
		String lsTableName		= "";
		String lsCurrStateName	= "";
		String lsNextStateName	= "";
		String lsTableNameForPS	= "";
		String lsTableIdsForPS	= "";
		int liSuperUserId		= -1;

		try
		{
			System.out.println("In PeriodShift Process ");
			// parse the request

			// read the request
//			BufferedReader br	= request.getReader();
//			BufferedReader br	= getRequestBuffer(request).toString();
//			StringBuffer sb		= new StringBuffer();
//			String line			= new String();
//			line = br.readLine();

//			while (line != null)
//			{
//				sb.append(line);
//				line = br.readLine();
//				if (line != null)
//				{
//					sb.append("\n");
//				}
//			}

			String buf = getRequestBuffer(request).toString();
			// parse the information
			String[] headerInfo		= buf.split(Seperator);
			liStateTableId			= Integer.parseInt(headerInfo[0]);

			System.out.println("State Table id  = " + liStateTableId);

			lsCurrStateName			= headerInfo[1];
			System.out.println("Current State Name  = " + lsCurrStateName);

			lsNextStateName			= headerInfo[2];
			System.out.println("Next State Name = " + lsNextStateName);

			lsTableNameForPS		= headerInfo[3];
			System.out.println("Table name For PS = " + lsTableNameForPS);

			System.out.println("headerInfo[4] -->" + headerInfo[4]+"<----");

			liSuperUserId			= Integer.parseInt(headerInfo[4].trim());
			System.out.println("Super user Id is  = " + liSuperUserId);

			// Start a connection
			DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
			connectionQuery = databaseloader.getConnection();
			
			String lsQuery = "SELECT ID FROM BW_TBL WHERE NAME = '"+ lsTableNameForPS +"' ";
			System.out.println("lsQuery = " + lsQuery);

			Statement	statement	= null;
			ResultSet	resultset	= null;

			statement = connectionQuery.createStatement();
			resultset = statement.executeQuery(lsQuery);

			while(resultset.next())
			{
				lsTableIdsForPS = "'" + resultset.getString(1) + "'";
			}

			statement = null;
			resultset = null;

			connection = databaseloader.getConnection();

			System.out.println("Starting Periodic Columns Realignment");

			APP_PeriodicColumnManager pcm = new APP_PeriodicColumnManager();

			// set new period set for short term plans
			// 9 is table Id and 2 is user Id
			if(pcm.processState(connection, liStateTableId, lsCurrStateName, lsNextStateName, lsTableIdsForPS))
			{
				pcm.setNewPeriods(connection, liSuperUserId, lsTableIdsForPS);
				System.out.println("Done with Periodic Columns Realignment");
				responseBuffer = "Sucess :: Done with Periodic Columns Realignment";
			}
			else
				System.out.println("Errors in processing Current State, Please Check ");

		}
		catch (BoardwalkException bwe)
		{
			System.out.println("Boardwalk error code = " + bwe.getErrorCode());
			responseBuffer = "Failure :: Please" + Seperator + bwe.getErrorCode() + Seperator + bwe.getMessage();
		}
		catch (Exception e)
		{
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

//			response.setContentLength(responseBuffer.length());
//			servletOut.print(responseBuffer);
//			servletOut.close();

		}
	}


}

