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
import java.sql.*;                  // JDBC package
import javax.sql.*;                 // extended JDBC packa
import com.boardwalk.member.Member;
import com.boardwalk.user.UserManager;

public class xlLinkImportService extends xlService implements SingleThreadModel
{

	public final static String Seperator = new Character((char)1).toString();
	public final static String ContentDelimeter = new Character((char)2).toString();

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

		String buff = getRequestBuffer(request).toString();
		//System.out.println(buff);
		System.out.println("Time to read the buffer = " + getElapsedTime());

		// Get the database connection
		Connection connection = null;
		try
		{
			String[] requestInfo = buff.split(Seperator);
			int userId = Integer.parseInt(requestInfo[0]);
			String userName = requestInfo[1];
			String userPassword = requestInfo[2];
			int memberId = Integer.parseInt(requestInfo[3]);
			int nhId = Integer.parseInt(requestInfo[4]);
			int tableId = Integer.parseInt(requestInfo[5]);
			int baselineId = Integer.parseInt(requestInfo[6]);
			String view = requestInfo[7];
			int mode = Integer.parseInt(requestInfo[8]);


			// Start a connection
			DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
			connection = databaseloader.getConnection();

			//	Access control checks
			TableInfo tinfo = TableManager.getTableInfo(connection, userId, tableId);
			TableAccessList ftal = TableViewManager.getSuggestedAccess(connection, tinfo, userId, memberId, nhId);

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

			System.out.println("Time to authenticate user = " + getElapsedTime());

			// This happens only when the user has no access to the said table
			// But in this case the user does not have provision to select the table
			if(view.equals("None"))
			{
				xlErrorCells.add( new xlErrorNew(tableId, 0, 0, 10005));
				throw new BoardwalkException(11005);
			}

			// get the tableBuffer
			responseBuffer = TableViewManager.getTableBuffer(connection, tableId, userId, memberId, nhId, baselineId, view, mode);
//System.out.println Time to getTableBuffer
			System.out.println("Time to getTableBuffer  = " + getElapsedTime());
		}
		catch (BoardwalkException bwe)
		{
			if( xlErrorCells.size() > 0 )
			{
				StringBuffer  errorBuffer  = new StringBuffer();

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
				commitResponseBuffer(responseBuffer, response);
				System.out.println("Time to prepare response = " + getElapsedTime());
			}
			else
			{
				failureReason = "FAILURE"+ContentDelimeter+failureReason;
				commitResponseBuffer(failureReason, response);
				System.out.println("Time to prepare response = " + getElapsedTime());
			}

			responseBuffer = null;
		}
	}
}
