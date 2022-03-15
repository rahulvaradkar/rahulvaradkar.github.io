package servlets;
/*
 *  This presents a list of collaboration available to a user
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
import com.boardwalk.user.UserManager;
import com.boardwalk.member.Member;
import com.boardwalk.exception.*;

import java.util.Date;
import java.sql.*;                  // JDBC package
import javax.sql.*;                 // extended JDBC packa

public class xlGetCellUpdates extends xlService implements SingleThreadModel
{

	public final static String Seperator = new Character((char)1).toString();
	public final static String ContentDelimeter = new Character((char)2).toString();
	StringTokenizer st;
	int userId;
	String userName;
	String userPassword;
	int nhId;
	int  memberId;
	String nhName;
	int tid;
	String m_ViewPreference;
	String m_SortPreference;
	String fileSavedAt;
	long rowCount;
	long columnCount;
	long transactionId;
	long local_offset;
	long difference_in_MiliSec;

	xlError xle;

	String m_period;
	String m_StartDate;
	String m_EndDate;
	int rowId;
	int colId;
	int cellId;

	HttpServletRequest req;
	HttpServletResponse res;

    public void service (HttpServletRequest request, HttpServletResponse response) 	throws ServletException, IOException
    {
		req = request;
		res = response;

		xle = null;
        StringBuffer sb = new 	StringBuffer ();
        BoardwalkMessages bwMsgs = new BoardwalkMessages();

		StringBuffer responseToUpdate = new StringBuffer();
        String responseBuffer = null;

		String buf = getRequestBuffer(request).toString();
		//System.out.println("Data from client" + buf);
		st = new StringTokenizer( buf );

		String wrkstr;

		wrkstr = st.nextToken (Seperator);
		tid = Integer.parseInt(wrkstr);

		wrkstr = st.nextToken (Seperator);
		m_period = wrkstr;

		wrkstr = st.nextToken (Seperator);
		m_StartDate = wrkstr;

		wrkstr = st.nextToken (Seperator);
		m_EndDate = wrkstr;

		wrkstr = st.nextToken (Seperator);
		rowId = Integer.parseInt(wrkstr);

		wrkstr = st.nextToken (Seperator);
		colId = Integer.parseInt(wrkstr);

		wrkstr = st.nextToken (Seperator);
		cellId = Integer.parseInt(wrkstr);

		wrkstr = st.nextToken (Seperator);
		local_offset = Long.parseLong(wrkstr);


		Calendar cal_GMT = Calendar.getInstance(TimeZone.getTimeZone("GMT"));

		long server_Millis = cal_GMT.getTimeInMillis();

		difference_in_MiliSec = local_offset - server_Millis;

		System.out.println("Local Server (gmt) in miliSeconds is " + server_Millis );

		System.out.println("The difference in Server and Clietnis " + (local_offset - server_Millis ));

		getCellVersions();

    }


	public void getCellVersions() throws ServletException, IOException
	{

		long endDate;
		long startDate;

		java.util.Date d = new java.util.Date();
		endDate = d.getTime();
		startDate = 0;

		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		cal.setTime(d);

		if (m_period.equals("Week"))
		{
			cal.add(Calendar.DATE, -7);
			startDate = cal.getTime().getTime();
		}
		else if (m_period.equals("Month"))
		{
			cal.add(Calendar.MONTH, -1);
			startDate = cal.getTime().getTime();
		}
		else if (m_period.equals("Quarter"))
		{
			cal.add(Calendar.MONTH, -3);
			startDate = cal.getTime().getTime();
		}
		else if (m_period.equals("Year"))
		{
			cal.add(Calendar.YEAR, -1);
			startDate = cal.getTime().getTime();

		}
		else if (m_period.equals("Custom") && m_EndDate != null && m_StartDate != null)
		{
			endDate = Long.parseLong(m_EndDate) - difference_in_MiliSec;
			startDate = Long.parseLong(m_StartDate) - difference_in_MiliSec;
		}

		System.out.println("++++++++++++++++ startdate = "  + startDate + " end date = " + endDate );



		Connection connection = null;
		try
		{
			DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
			connection = databaseloader.getConnection();
			Hashtable cellVersions = TableManager.getCellVersions( connection,
																      cellId,
																  	  rowId,
																  	  colId,
																      startDate,
																      endDate);
			System.out.println("Number of cell versions between startdate = "  + startDate + " end date = " + endDate + " = " + cellVersions.size());

			StringBuffer responseToUpdate = new StringBuffer();
			String responseBuffer = null;
			responseToUpdate.append("Success" + ContentDelimeter);


			Vector tvec = new Vector(cellVersions.keySet());
			Collections.sort(tvec);
		    Iterator i = tvec.iterator();
		    boolean grey = false;
		    String currFormula = null;
		    while (i.hasNext())
		    {

				System.out.println(" inside WHILE ");

		        Integer tid = (Integer)i.next();
			    VersionedCell  vc = (VersionedCell)cellVersions.get(tid);
			    Transaction t = vc.getTransaction();
			    String vcFormula = vc.getFormula();
			    boolean formula_updated = false;

			    if (currFormula == null )
			    {
			    	if ( vcFormula != null )
			    	{
			    	    formula_updated = true;
			    	    currFormula = vcFormula;
			    	}
			    	else
			    	{
			    	    formula_updated = false;
			    	}
			    }
			    else
			    {
			    	if ( !vcFormula.equals(currFormula) )
			    	{
			    		formula_updated = true;
						currFormula = vcFormula;
			    	}
			    	else
			    	{
			    		formula_updated = false;
			    		currFormula = vcFormula;
			    	}
			    }

			    int id = t.getId();
			    String updatedBy = t.getCreatedByUserAddress();
			    //String updatedOn = t.getCreatedOn();
			    String comment = t.getComment();
			    String descr = t.getDescription();
				String cellValue ;
				String cellFormula;

				cellValue = vc.getValueAsString();
				
				if (formula_updated == true)
				{
					cellFormula = vc.getFormula();
				}
				else
				{
					cellFormula = " ";
		        }

				responseToUpdate.append( cellValue + Seperator);
				responseToUpdate.append( currFormula + Seperator);
				if (formula_updated == true)
				{
					responseToUpdate.append( "Y" + Seperator);
				}
				else
				{
					responseToUpdate.append( "" + Seperator);
		        }
				responseToUpdate.append( updatedBy + Seperator);
				responseToUpdate.append( t.getCreatedOnTime() + difference_in_MiliSec +  Seperator);
				responseToUpdate.append( comment );
				responseToUpdate.append(ContentDelimeter);

		     } // transactions

			//System.out.println(" responseToIpdate is +++++++++++ " + responseToUpdate);

			responseBuffer = responseToUpdate.toString();
			//System.out.println("Response = " + responseBuffer);
			commitResponseBuffer(responseBuffer, res);

		}// ends here
		catch (SQLException sql)
		{
			sql.printStackTrace();
		   return;
		}
		finally
		{
		  try
		  {
			if ( connection != null )
				connection.close();
		  }
		  catch ( SQLException sql )
		  {
			sql.printStackTrace();
		  }
		}

	}
}
