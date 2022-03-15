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
import com.boardwalk.exception.*;
import com.boardwalk.excel.*;		// added by shirish on 06/30/10
import com.boardwalk.member.Member; // added by shirish on 06/30/10
import com.boardwalk.user.UserManager;// added by shirish on 06/30/10

public class xlGetQueryList extends HttpServlet implements SingleThreadModel
{
	public void service(HttpServletRequest request,
		HttpServletResponse response)throws ServletException, IOException
	{
		xlServiceLogic logic = new xlServiceLogic(this);
		Connection connection = null;
		CallableStatement callableStatement = null;
		ResultSet rs = null;
		String responseBuffer = "Failure";
		int tableId  = -1;
		int userId	= -1;

		// Failure String
		String failureReason = "";
		Vector xlErrorCells = new Vector();

		response.setContentType("text/plain");
		ServletOutputStream servletOut = response.getOutputStream();
		try
		{
			System.out.println("Inside xlGetQueryList");
			
			String buf = logic.getRequestBuffer(request).toString();
			// parse the information
			//String[] headerInfo = buf.split(Seperator);
			String[] requestInfo = buf.split(xlServiceLogic.Seperator);

			//tableId = Integer.parseInt(headerInfo[0]);
			//System.out.println("tableId = " + tableId);
			//System.out.println("buf = " + buf);
			//userId = Integer.parseInt(headerInfo[1]);
			//System.out.println("userId headerInfo = " + userId);

			// authenticate the user - added by shirish on 06/30/10
			//Modified by Tekvision on 20180207 for Clear Text Password(Issue Id: 14241) - START
			userId = Integer.parseInt(requestInfo[0]);
			System.out.println("userId requestInfo = " + userId);
			String userName = requestInfo[1];
			//String userPassword = requestInfo[2];
			int memberId = Integer.parseInt(requestInfo[2]);
			int nhId = Integer.parseInt(requestInfo[3]);
			tableId = Integer.parseInt(requestInfo[4]);
			int baselineId = Integer.parseInt(requestInfo[5]);
			String view = requestInfo[6];
			int importTid = Integer.parseInt(requestInfo[7]);
			int exportTid = Integer.parseInt(requestInfo[8]);

			// Start a connection
			DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
			connection = databaseloader.getConnection();

			// authenticate the user - added by shirish on 06/30/10
			Member memberObj = UserManager.authenticateMember(connection, userName, memberId);
			//Modified by Tekvision on 20180207 for Clear Text Password(Issue Id: 14241) - END
			if (memberObj == null)
			{
				//System.out.println("Authentication failed for user : " + userName);
				responseBuffer = "Failure";//TBD : Description of the Error
				xlErrorCells.add( new xlErrorNew( tableId, 0, 0, 11005));
				throw new BoardwalkException(11005);
			}
			else
			{
				//System.out.println("Authentication succeeded for user : " + userName);
				nhId = memberObj.getNeighborhoodId();
			}
			System.out.println("Time to authenticate user = " + logic.getElapsedTime());
			//end  authenticate the user

			callableStatement = connection.prepareCall("{CALL BW_GET_USER_DEFINED_QUERY_LIST(?,?)}");
			callableStatement.setInt(1, tableId);
			callableStatement.setInt(2, userId);
			rs = callableStatement.executeQuery();
			responseBuffer = "Success";
			while (rs.next())
			{
				responseBuffer = responseBuffer + xlServiceLogic.Seperator + rs.getString(1);
			}

			callableStatement.close();
			rs.close();
		}
		catch (BoardwalkException bwe)
		{
			if (xlErrorCells.size() <= 0)
			{
				responseBuffer = "Failure";//TBD : Description of the Error
				xlErrorCells.add(new xlErrorNew(tableId, 0, 0, bwe.getErrorCode()));
			}
			StringBuffer errorBuffer = new StringBuffer();

			for (int errorIndex = 0; errorIndex < xlErrorCells.size(); errorIndex++)
			{
				xlErrorNew excelError = (xlErrorNew)(xlErrorCells.elementAt(errorIndex));
				errorBuffer.append(excelError.buildTokenString());
			}
			errorBuffer.append(xlServiceLogic.Seperator);
			failureReason = errorBuffer.toString();
			System.out.println(" Failure Reason *****" + failureReason);
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
				if (callableStatement != null)
					callableStatement.close();
				if (rs != null)
					rs.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			
			logic.commitResponseBuffer(responseBuffer, response);

		}
	}
}

