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
import com.boardwalk.user.UserManager;
import com.boardwalk.member.Member;
import com.boardwalk.exception.*;

import java.util.Date;
import java.sql.*;                  // JDBC package
import javax.sql.*;                 // extended JDBC packa

public class xlGetTransactionsLogic extends xlServiceLogic
{

	public final static String Seperator = new Character((char)1).toString();
	public final static String ContentDelimeter = new Character((char)2).toString();
	StringTokenizer st;
	int userId;
	String userName;
	//String userPassword; //Modified by Tekvision on 20180207 for Clear Text Password(Issue Id: 14241)
	int nhId;
	int  memberId;
	String nhName;
	int tableId;
	String viewPref;
	int importTxId;
	String reportType;

	long rowCount;
	long columnCount;
	long transactionId;
	long local_offset;
	long difference_in_MiliSec;

	xlError xle;

	String m_period;
	String m_StartDate;
	String m_EndDate;

	HttpServletRequest req;
	HttpServletResponse res;

	public xlGetTransactionsLogic(xlGetTransactions srv) {
		super(srv);
	}

    public void service (HttpServletRequest request, HttpServletResponse response) 	throws ServletException, IOException
    {
		req = request;
		res = response;

		xle = null;
        BoardwalkMessages bwMsgs = new BoardwalkMessages();

		String buf = getRequestBuffer(request).toString();
		//System.out.println("Data from client" + buf);
		st = new StringTokenizer( buf );

		String wrkstr;
		wrkstr = st.nextToken(Seperator);
		userId = Integer.parseInt(wrkstr);

		wrkstr = st.nextToken(Seperator);
		nhId = Integer.parseInt(wrkstr);

		wrkstr = st.nextToken(Seperator);
		viewPref = wrkstr;

		wrkstr = st.nextToken (Seperator);
		tableId = Integer.parseInt(wrkstr);

		wrkstr = st.nextToken (Seperator);
		m_period = wrkstr;

		wrkstr = st.nextToken (Seperator);
		m_StartDate = wrkstr;

		wrkstr = st.nextToken (Seperator);
		m_EndDate = wrkstr;

		wrkstr = st.nextToken (Seperator);
		local_offset = Long.parseLong(wrkstr);


		wrkstr = st.nextToken (Seperator);
		importTxId = Integer.parseInt(wrkstr);

		wrkstr = st.nextToken (Seperator);
		reportType = wrkstr;


		Calendar cal_GMT = Calendar.getInstance(TimeZone.getTimeZone("GMT"));

		long server_Millis = cal_GMT.getTimeInMillis();

		difference_in_MiliSec = local_offset - server_Millis;

		System.out.println("Local Server (gmt) in miliSeconds is " + server_Millis );

		System.out.println("The difference in Server and Clietnis " + (local_offset - server_Millis ));

        int action;

		if (reportType.toUpperCase().equals("DURATION"))
		{
			getTransactions();
		}
		else if (reportType.toUpperCase().equals("AFTERIMPORT"))
			getTransactionsAfterImport();

    }


	public void getTransactions() throws ServletException, IOException
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

		//Setting start and end Transaction id to -1
		int stid = -1;
		int etid = -1;

		Connection connection = null;
		try
		{
			DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
			connection = databaseloader.getConnection();
			Hashtable transactionList = TableManager.getTransactionList(connection,
																      tableId,
																      stid,
																      etid,
																      startDate,
																      endDate,
																      userId,
																      nhId,
																      viewPref,
																      true);
			System.out.println("Number of transactions = " + transactionList.size());
			req.setAttribute("transactionList", transactionList);
			System.out.println("stid = " + stid);
			System.out.println("etid = " + etid);


			StringBuffer responseToUpdate = new StringBuffer();
			String responseBuffer = null;
			responseToUpdate.append("Success" + ContentDelimeter);

			Vector tvec = new Vector(transactionList.keySet());
			Collections.sort(tvec);
		    Iterator i = tvec.iterator();

		    while (i.hasNext())
			{
		    	Integer tid = (Integer)i.next();
			    Vector  vt = (Vector)transactionList.get(tid);
			    Transaction t = (Transaction)vt.elementAt(0);
			    String rowadd = "";
			    String rowdel = "";
			    String coladd = "";
			    String cellupd = "";
			    String frmupd = "";
			    String blnadd = "";
			    Iterator j = vt.iterator();
			    String checkImage = "";
			    while (j.hasNext())
			    {
			    	Transaction ts = (Transaction)j.next();
			    	String descr = ts.getDescription();
			    	//System.out.println("descr=" + descr);
			    	if (descr.toUpperCase().startsWith("ROWADD"))
			    	{
			    		rowadd = "Y";
			    		//System.out.println("rowadd set to true");
			    	}
			    	else if (descr.toUpperCase().startsWith("ROWDEL"))
			    	{
			    		rowdel = "Y";
			    		//System.out.println("rowdel set to true");
			    	}
			    	else if (descr.toUpperCase().startsWith("COLADD"))
			    	{
			    		coladd = "Y";
			    		//System.out.println("coladd set to true");
			    	}
			    	else if (descr.toUpperCase().startsWith("CELLUPD"))
			    	{
			    		cellupd = "Y";
			    		//System.out.println("cellupd set to true");
			    	}
			    	else if (descr.toUpperCase().startsWith("FRMUPD"))
			    	{
			    		frmupd = "Y";
			    		//System.out.println("frmupd set to true");
			    	}
			    	else if (descr.toUpperCase().startsWith("BLNADD"))
			    	{
			    		blnadd = "Y";
			    		//System.out.println("blnadd set to true");
			    	}
			    }
			    int id = t.getId();
			    String updatedBy = t.getCreatedByUserAddress();
				long transactionTime = t.getCreatedOnTime() + difference_in_MiliSec;

			    //String updatedOn = t.getCreatedOn();
			    String comment = t.getComment();
			    String descr = t.getDescription();

				responseToUpdate.append( id + Seperator);
				responseToUpdate.append( updatedBy + Seperator);
				responseToUpdate.append( transactionTime + Seperator);
				responseToUpdate.append( comment + Seperator);
				responseToUpdate.append( rowadd + Seperator);
				responseToUpdate.append( rowdel + Seperator);
				responseToUpdate.append( coladd + Seperator);
				responseToUpdate.append( cellupd + Seperator);
				responseToUpdate.append( frmupd + Seperator);
				responseToUpdate.append( blnadd + Seperator);
				responseToUpdate.append( t.getCreatedOnTime() + Seperator);
				responseToUpdate.append( ContentDelimeter );
			}


			//System.out.println(" responseToIpdate is +++++++++++ " + responseToUpdate);

			responseBuffer = responseToUpdate.toString();
			commitResponseBuffer(responseBuffer, res);

		}
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


	public void getTransactionsAfterImport() throws ServletException, IOException
	{

		Connection connection = null;
		try
		{
			DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
			connection = databaseloader.getConnection();
			Hashtable transactionList = TableManager.getTransactionListAfterImport(connection,
																      tableId,
																      importTxId,
																      userId,
																      nhId,
																      viewPref);

			System.out.println("Number of transactions = " + transactionList.size());
			req.setAttribute("transactionList", transactionList);
			System.out.println("importTxId = " + importTxId);

			StringBuffer responseToUpdate = new StringBuffer();
			String responseBuffer = null;
			responseToUpdate.append("Success" + ContentDelimeter);

			Vector tvec = new Vector(transactionList.keySet());
			Collections.sort(tvec);
		    Iterator i = tvec.iterator();

		    while (i.hasNext())
			{
		    	Integer tid = (Integer)i.next();
			    Vector  vt = (Vector)transactionList.get(tid);
			    Transaction t = (Transaction)vt.elementAt(0);
			    String rowadd = "";
			    String rowdel = "";
			    String coladd = "";
			    String cellupd = "";
			    String frmupd = "";
			    String blnadd = "";
			    Iterator j = vt.iterator();
			    String checkImage = "";
			    while (j.hasNext())
			    {
			    	Transaction ts = (Transaction)j.next();
			    	String descr = ts.getDescription();
			    	//System.out.println("descr=" + descr);
			    	if (descr.toUpperCase().startsWith("ROWADD"))
			    	{
			    		rowadd = "Y";
			    		//System.out.println("rowadd set to true");
			    	}
			    	else if (descr.toUpperCase().startsWith("ROWDEL"))
			    	{
			    		rowdel = "Y";
			    		//System.out.println("rowdel set to true");
			    	}
			    	else if (descr.toUpperCase().startsWith("COLADD"))
			    	{
			    		coladd = "Y";
			    		//System.out.println("coladd set to true");
			    	}
			    	else if (descr.toUpperCase().startsWith("CELLUPD"))
			    	{
			    		cellupd = "Y";
			    		//System.out.println("cellupd set to true");
			    	}
			    	else if (descr.toUpperCase().startsWith("FRMUPD"))
			    	{
			    		frmupd = "Y";
			    		//System.out.println("frmupd set to true");
			    	}
			    	else if (descr.toUpperCase().startsWith("BLNADD"))
			    	{
			    		blnadd = "Y";
			    		//System.out.println("blnadd set to true");
			    	}
			    }
			    int id = t.getId();
			    String updatedBy = t.getCreatedByUserAddress();
				long transactionTime = t.getCreatedOnTime() + difference_in_MiliSec;

			    //String updatedOn = t.getCreatedOn();
			    String comment = t.getComment();
			    String descr = t.getDescription();

				responseToUpdate.append( id + Seperator);
				responseToUpdate.append( updatedBy + Seperator);
				responseToUpdate.append( transactionTime + Seperator);
				responseToUpdate.append( comment + Seperator);
				responseToUpdate.append( rowadd + Seperator);
				responseToUpdate.append( rowdel + Seperator);
				responseToUpdate.append( coladd + Seperator);
				responseToUpdate.append( cellupd + Seperator);
				responseToUpdate.append( frmupd + Seperator);
				responseToUpdate.append( blnadd + Seperator);
				responseToUpdate.append( t.getCreatedOnTime() + Seperator);
				responseToUpdate.append( ContentDelimeter );
			}


			//System.out.println(" responseToIpdate is +++++++++++ " + responseToUpdate);

			responseBuffer = responseToUpdate.toString();
			//System.out.println("Response = " + responseBuffer);
			commitResponseBuffer(responseBuffer, res);

		}
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
