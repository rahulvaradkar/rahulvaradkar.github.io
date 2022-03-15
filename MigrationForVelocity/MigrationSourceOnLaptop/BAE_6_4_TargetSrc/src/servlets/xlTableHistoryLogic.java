package servlets;
/*
 *  This presents a list of collaboration available to a user
 * Added by RahulV on 08 October 2007
 */
import java.io.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import com.boardwalk.database.DatabaseLoader;
import com.boardwalk.exception.NeighborhoodException;
import com.boardwalk.exception.SystemException;
import com.boardwalk.collaboration.CollaborationManager;
import com.boardwalk.collaboration.Collaboration;
import com.boardwalk.collaboration.CollaborationTreeNode;
import com.boardwalk.whiteboard.WhiteboardManager;
import com.boardwalk.whiteboard.WhiteboardTreeNode;
import com.boardwalk.neighborhood.NeighborhoodManager;
import com.boardwalk.database.*;
import com.boardwalk.table.*;
import com.boardwalk.excel.*;
import com.boardwalk.query.*;
import com.boardwalk.user.UserManager;
import com.boardwalk.member.Member;
import com.boardwalk.exception.*;

import java.util.Date;
import java.sql.*;                  // JDBC package
import javax.sql.*;                 // extended JDBC packa

public class xlTableHistoryLogic extends xlServiceLogic 
{

	StringTokenizer st;

	int	userId				= -1;
	String userName			= "";
	//String userPassword	= ""; //Modified by Tekvision on 20180207 for Clear Text Password(Issue Id: 14241)
	int	tableId				= -1;
	int	memberId			= -1;
	int	nhId				= -1;
	String viewPref			= "";
	int	asOfTid				= -1;
	int	compTid				= -1;
	String lsShowChangesOnly= "";
	boolean	bShowChangesOnly= false;
	String reportType		= "";
	long localTimeAfter1970 = 0;
	long difference_in_MiliSec = 0;

	HttpServletRequest req;
	HttpServletResponse res;

	public xlTableHistoryLogic(xlTableHistory srv) {
		super(srv);
	}

    public void service (HttpServletRequest request, HttpServletResponse response) 	throws ServletException, IOException
    {
		req = request;
		res = response;

		String buf = getRequestBuffer(request).toString();
		//System.out.println("Data from client" + buf);
		st = new StringTokenizer(buf);

		String wrkstr;
		wrkstr = st.nextToken(Seperator);
		userId = Integer.parseInt(wrkstr); // User Id

		wrkstr = st.nextToken(Seperator);
		memberId = Integer.parseInt(wrkstr);

		wrkstr = st.nextToken(Seperator);
		nhId = Integer.parseInt(wrkstr);

		wrkstr = st.nextToken(Seperator);
		viewPref = wrkstr;

		wrkstr = st.nextToken(Seperator);
		tableId = Integer.parseInt(wrkstr);

		wrkstr = st.nextToken(Seperator);
		asOfTid = Integer.parseInt(wrkstr);

		wrkstr = st.nextToken(Seperator);
		compTid = Integer.parseInt(wrkstr);

		wrkstr = st.nextToken(Seperator);
		lsShowChangesOnly = wrkstr;

		if(lsShowChangesOnly != null && lsShowChangesOnly.equalsIgnoreCase("true"))
			bShowChangesOnly= true;

		wrkstr = st.nextToken(Seperator);
		reportType = wrkstr;

		wrkstr = st.nextToken(Seperator);
		localTimeAfter1970 = Long.parseLong(wrkstr.trim ());

		Calendar cal_GMT = Calendar.getInstance(TimeZone.getTimeZone("GMT"));

		long server_Millis = cal_GMT.getTimeInMillis();

		difference_in_MiliSec = localTimeAfter1970 - server_Millis;

		System.out.println("Local Server (gmt) in miliSeconds is " + server_Millis );
		System.out.println("The difference in Server and Clietnis " + (difference_in_MiliSec));

		getTableHistory();
    }

    public void getTableHistory()    throws ServletException, IOException
	{
		String responseBuffer = "";

        Connection connection = null;

        try
        {

			DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
			connection = databaseloader.getConnection();

			String completeTableWithChangesBuffer = "";

			if (reportType.equalsIgnoreCase("CompareTwoUpdates"))
			{
					completeTableWithChangesBuffer = TableHistoryManager.compareTwoTransactions(
												connection,
												tableId,
												null,
												userId,
												memberId,
												nhId,
												asOfTid,
												compTid,
												viewPref,
												difference_in_MiliSec
												);

			}
			else if (reportType.equalsIgnoreCase("CompleteTableWithChanges"))
			{
					completeTableWithChangesBuffer = TableHistoryManager.getCompleteTableWithChanges(
												connection,
												tableId,
												null,
												userId,
												memberId,
												nhId,
												asOfTid,
												viewPref,
												difference_in_MiliSec
												);
			}
			else if (reportType.equalsIgnoreCase("ChangesAfterImport"))
			{
					System.out.println("...........calling  TableHistoryManager.getChangesAfterImport...");
					completeTableWithChangesBuffer = TableHistoryManager.getChangesAfterImport(
												connection,
												tableId,
												null,
												userId,
												memberId,
												nhId,
												compTid,
												viewPref
												);
			}

//			System.out.println(" responseBuffer is +++++++++++ " + completeTableWithChangesBuffer);

//			System.out.println("Response length= " + completeTableWithChangesBuffer.length());
			commitResponseBuffer(completeTableWithChangesBuffer, res);

		}
		catch (SQLException sql)
		{
			sql.printStackTrace();
		   return;
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}
		finally
		{
		  try
		  {
			connection.close();
		  }
		  catch ( SQLException sql )
		  {
			sql.printStackTrace();
		  }
		}
	}

}
