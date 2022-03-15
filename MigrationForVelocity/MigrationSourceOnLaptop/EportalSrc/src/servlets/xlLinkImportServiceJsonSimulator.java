package servlets;
/*
 *  This presents a list of collaboration available to a user
 */
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import com.boardwalk.table.*;
import com.boardwalk.excel.*;
import com.boardwalk.exception.*;
import com.boardwalk.database.*;
import java.sql.*; // JDBC package
import javax.sql.*; // extended JDBC packa

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.boardwalk.member.Member;
import com.boardwalk.user.UserManager;

public class xlLinkImportServiceJsonSimulator extends xlServiceJsonSimulator implements SingleThreadModel
{
	public final static String Seperator = new Character((char)1).toString();
	public final static String ContentDelimeter = new Character((char)2).toString();

	//For Setter Getter method for accessing tableid.
	org.json.JSONObject jsonresponseBuffer = new org.json.JSONObject();

	public void service(HttpServletRequest request,
		HttpServletResponse response)throws ServletException, IOException
	{
		String responseBuffer = "Failure";
		getElapsedTime();

		// Error vector to all the Exceptions
		Vector xlErrorCells = new Vector();
		// access variables
        boolean canAddRows = false;
        boolean canDeleteRows = false;
        boolean canAdministerColumns = false;

		// Failure String
		String failureReason = "";

		//String buff = getRequestBuffer(request).toString();
		//System.out.println(buff);
		System.out.println("Time to read the buffer = " + getElapsedTime());

		// Get the database connection
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		PrintWriter out = response.getWriter();

		BufferedReader brr = new BufferedReader(new InputStreamReader(request.getInputStream()));
		StringBuilder sb = new StringBuilder();
		String json = "";
		out.println(json);
		if (brr != null) {
			json = brr.readLine();
			System.out.println("json==" + json);
		}

		JSONParser parser = new JSONParser();

		Connection connection = null;
		try
		{

			JSONObject jsonob = (JSONObject) parser.parse(json);
			JSONArray msg = (JSONArray) jsonob.get("HeaderData");
			System.out.println("jsonconvert==" + msg);
			// String[] requestInfo = buff.split(Seperator);
			int userId = Integer.parseInt((String) msg.get(0));
			String userName = (String) msg.get(1);
		    //String userPassword = (String) msg.get(2);
			//int userPassword = Integer.parseInt((String) msg.get(2));
			int memberId = Integer.parseInt((String) msg.get(3));
			int nhId = Integer.parseInt((String) msg.get(4));
			int tableId = Integer.parseInt((String) msg.get(5));
			int baselineId = Integer.parseInt((String) msg.get(6));
			String view = (String) msg.get(7);
			int mode = Integer.parseInt((String) msg.get(8));

			System.out.println("userid is:-->" + userId);

			// Start a connection
			DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
			connection = databaseloader.getConnection();

			//Access control checks
			TableInfo tinfo = TableManager.getTableInfo(connection, userId, tableId);
			TableAccessList ftal = TableViewManagerJsonSimulator.getSuggestedAccess(connection, tinfo, userId, memberId, nhId);

			if (view == null || view.trim().equals(""))
			{
				view = ftal.getSuggestedViewPreferenceBasedOnAccess();
				System.out.println("Suggested view pref = " + view);
				if(view == null)
					view = "None";
			}
			// Check access control :: TBD
			int raccess = 1;
			int ACLFromDB = ftal.getACL();
			TableAccessRequest wAccess = new TableAccessRequest(tableId, view, true);
			int wACL = wAccess.getACL();
			int awACL = wACL & ACLFromDB;
			if (awACL == wACL)
			{
				raccess = 2;
				System.out.println("Rows have write access");
			}
			else
			{
				System.out.println("Rows are readonly");
			}

			// Check and see if the user has atleast the read access
			// if he does not have read access then throw exception.



// authenticate the user
		/*
			Member memberObj = UserManager.authenticateMember(connection, userName,userPassword, memberId);
			if (memberObj == null)
			{
				System.out.println("Authentication failed for user : " + userName);
				responseBuffer = "Failure";//TBD : Description of the Error
				xlErrorCells.add( new xlErrorNew( tableId, 0, 0, 11004));
				throw new BoardwalkException(11004);
			}
			else
			{
				System.out.println("Authentication succeeded for user : " + userName);
				nhId = memberObj.getNeighborhoodId();
				
			}
			*/

			System.out.println("Time to authenticate user = " + getElapsedTime());

			// This happens only when the user has no access to the said table
			// But in this case the user does not have provision to select the table
			if(view.equals("None"))
			{
				xlErrorCells.add( new xlErrorNew(tableId, 0, 0, 10005));
				throw new BoardwalkException(11005);
			}

			// get the tableBuffer
			jsonresponseBuffer = TableViewManagerJsonSimulator.getTableBuffer(connection, tableId, userId, memberId, nhId, baselineId, view, mode);
			//System.out.println Time to getTableBuffer
			System.out.println("  jsonresponseBuffer:" + jsonresponseBuffer);
			response.getWriter().print(jsonresponseBuffer);
			System.out.println("Time to getTableBuffer  = " + getElapsedTime());
		}
		catch (BoardwalkException bwe)
		{
			if( xlErrorCells.size() > 0 )
			{
				StringBuffer errorBuffer = new StringBuffer();

				for ( int errorIndex = 0; errorIndex< xlErrorCells.size(); errorIndex++ )
				{
					xlErrorNew excelError = (xlErrorNew)(xlErrorCells.elementAt(errorIndex));
					errorBuffer.append( excelError.buildTokenString() );
				}
				errorBuffer.append( Seperator);
				failureReason =  errorBuffer.toString();
				System.out.println(failureReason);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			// close the connection
			try
			{
				connection.close();
			}
			catch (SQLException sql)
			{
				sql.printStackTrace();
			}

			// The response
			//responseBuffer = responseToUpdate.toString();
			// The response
			if(failureReason.length() == 0)
			{
				//commitResponseBuffer(jsonresponseBuffer, response);
				System.out.println("Time to prepare response = " + getElapsedTime());
			}
			else
			{
				failureReason = "FAILURE"+ContentDelimeter+failureReason;
				//commitResponseBuffer(failureReason, response);
				System.out.println("Time to prepare response = " + getElapsedTime());
			}

			responseBuffer = null;
		}
	}
}
