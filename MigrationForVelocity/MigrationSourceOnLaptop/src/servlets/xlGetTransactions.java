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

public class xlGetTransactions extends HttpServlet implements SingleThreadModel
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

    public void service (HttpServletRequest request, HttpServletResponse response) 	throws ServletException, IOException
    {
			xlGetTransactionsLogic logic = new xlGetTransactionsLogic(this);
			logic.service(request, response);
    }
}
