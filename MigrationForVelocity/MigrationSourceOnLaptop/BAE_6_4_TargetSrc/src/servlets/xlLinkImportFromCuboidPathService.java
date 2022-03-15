/*
*  Added by Lakshman on 20190530 for ID Independent Template
*/

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

public class xlLinkImportFromCuboidPathService extends HttpServlet implements SingleThreadModel
{

	public final static String Seperator = new Character((char)1).toString();
	public final static String ContentDelimeter = new Character((char)2).toString();

	public void service(HttpServletRequest request,
		HttpServletResponse response)throws ServletException, IOException
	{
		xlServiceLogic logic = new xlServiceLogic(this);

		String responseBuffer = "Failure";
		logic.getElapsedTime();

		// Error vector to all the Exceptions
		Vector xlErrorCells = new Vector();
		// access variables
        boolean canAddRows = false;
        boolean canDeleteRows = false;
        boolean canAdministerColumns = false;

		// Failure String
		String failureReason = "";

		String buff = logic.getRequestBuffer(request).toString();
		//System.out.println(buff);
		System.out.println("Time to read the buffer = " + logic.getElapsedTime());

		// Get the database connection
		Connection connection = null;
		TransactionManager tm = null;
		int tid = -1;

		try
		{
			//Modified by Tekvision on 20180207 for Clear Text Password(Issue Id: 14241) - START
			String[] requestInfo = buff.split(Seperator);
			int userId = Integer.parseInt(requestInfo[0]);
			String userName = requestInfo[1];
			//String userPassword = requestInfo[2];
			int memberId = Integer.parseInt(requestInfo[2]);
			int nhId = Integer.parseInt(requestInfo[3]);
			String cuboidPath = requestInfo[4];
			int baselineId = Integer.parseInt(requestInfo[5]);
			String view = requestInfo[6];
			int mode = Integer.parseInt(requestInfo[7]);
			//Modified by Tekvision on 20180207 for Clear Text Password(Issue Id: 14241) - END

			// Start a connection
			DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
			connection = databaseloader.getConnection();

			System.out.println("xlLinkImportFromCuboidPathService: Cuboid Path: " + cuboidPath);
			int tableId = TableManager.getTableIdFromPath(connection, cuboidPath);
			System.out.println("xlLinkImportFromCuboidPathService: tableId: " + tableId);
			
			//Added to get the Membership ID in case of Multiple Membership
			if( requestInfo.length == 9) //Modified by Tekvision on 20180207 for Clear Text Password(Issue Id: 14241)
			{
				String nhHierarchy = requestInfo[8]; //Modified by Tekvision on 20180207 for Clear Text Password(Issue Id: 14241)
				String templateMode = "USER";
				
				int checkMembershipStatus = UserManager.checkMembershipStatus(connection, userId, templateMode, nhHierarchy, tableId);

				System.out.println("xlLinkImportFromCuboidPathService: nhHierarchy: " + nhHierarchy);
				System.out.println("xlLinkImportFromCuboidPathService: checkMembershipStatus: " + checkMembershipStatus);
				
				if( checkMembershipStatus <= 0 ){
					failureReason = "checkMembershipStatusFalse";
					System.out.println("Inside xlLinkImportFromCuboidPathService: Failure: " + failureReason);
					return;
				}
			}

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
			Member memberObj = UserManager.authenticateMember(connection, userName, memberId); //Modified by Tekvision on 20180207 for Clear Text Password(Issue Id: 14241)
			if (memberObj == null)
			{
				//System.out.println("Authentication failed for user : " + userName);
				responseBuffer = "Failure";//TBD : Description of the Error
				xlErrorCells.add( new xlErrorNew( tableId, 0, 0, 11004));
				throw new BoardwalkException(11004);
			}
			else
			{
				//System.out.println("Authentication succeeded for user : " + userName);
				nhId = memberObj.getNeighborhoodId();
				
			}

			System.out.println("Time to authenticate user = " + logic.getElapsedTime());

			// This happens only when the user has no access to the said table
			// But in this case the user does not have provision to select the table
			if(view.equals("None"))
			{
				xlErrorCells.add( new xlErrorNew(tableId, 0, 0, 10005));
				throw new BoardwalkException(11005);
			}

			//Added by Lakshman on 20180613 to fix Issue Id: 14284
			tm = new TransactionManager(connection, userId);
			tid = tm.startTransaction("Link Import table id = " + tableId, "");

			// get the tableBuffer
			responseBuffer = TableViewManager.getTableBuffer(connection, tableId, userId, memberId, nhId, baselineId, view, mode);
			//System.out.println Time to getTableBuffer
			System.out.println("Time to getTableBuffer  = " + logic.getElapsedTime());

			tm.commitTransaction(); //Added by Lakshman on 20180613 to fix Issue Id: 14284
		}
		catch (BoardwalkException bwe)
		{
			//Added by Lakshman on 20180613 to fix Issue Id: 14284
			try
			{
				if (tm != null)
					tm.rollbackTransaction();
			}
			catch (Exception e1)
			{
				e1.printStackTrace();
			}

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
			//Added by Lakshman on 20180613 to fix Issue Id: 14284
			try
			{
				if (tm != null)
					tm.rollbackTransaction();
			}
			catch (Exception e1)
			{
				e1.printStackTrace();
			}

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
				logic.commitResponseBuffer(responseBuffer, response);
				System.out.println("Time to prepare response = " + logic.getElapsedTime());
			}
			//Added to get the Membership ID in case of Multiple Membership
			else if(failureReason.equals("checkMembershipStatusFalse"))
			{
				failureReason = "FAILURE"+Seperator+failureReason;
				System.out.println("Inside xlLinkImportFromCuboidPathService: Failure: " + failureReason);
				logic.commitResponseBuffer(failureReason, response);
				System.out.println("Time to prepare response = " + logic.getElapsedTime());
			}
			else
			{
				failureReason = "FAILURE"+ContentDelimeter+failureReason;
				System.out.println("Inside xlLinkImportFromCuboidPathService: Failure: " + failureReason);
				logic.commitResponseBuffer(failureReason, response);
				System.out.println("Time to prepare response = " + logic.getElapsedTime());
			}

			tm = null; //Added by Lakshman on 20180613 to fix Issue Id: 14284
			responseBuffer = null;
		}
	}
}
