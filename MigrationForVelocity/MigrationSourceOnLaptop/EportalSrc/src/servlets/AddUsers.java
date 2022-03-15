package servlets;
/*
 *  This presents a list of collaboration available to a user
 */
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import com.boardwalk.database.*;
import com.boardwalk.exception.NeighborhoodException;
import com.boardwalk.exception.*;
import com.boardwalk.collaboration.*;

import com.boardwalk.whiteboard.WhiteboardManager;
import com.boardwalk.user.*;
import com.boardwalk.util.*;
import com.boardwalk.table.*;
import com.boardwalk.wizard.*;


import com.boardwalk.neighborhood.*;
import java.lang.Exception;

import java.sql.*;                  // JDBC package
import javax.sql.*;                 // extended JDBC package

public class AddUsers extends xlService implements SingleThreadModel

{
    //    BoardwalkDB bwdb;

	StringBuffer responseToUpdate = new StringBuffer();


//    public void doPost(HttpServletRequest request,
//    HttpServletResponse response)throws ServletException, IOException
//    {
//
//        System.out.println("In AddUsers");
//        userName = (String)request.getParameter("userName");
//        if (authenticate(request, response) == false)
//        	return;
//        HttpSession hs = request.getSession(true);
//		BoardwalkSession bws = (BoardwalkSession)hs.getAttribute("bwSession");
//		hs.setAttribute("bwSession",bws);
//
//		int userId = bws.userId.intValue();
//		int memberid = bws.memberId.intValue();
//		int nhId = bws.nhId.intValue();
//
//
//
//
//        // check the action requested
//        String action = request.getParameter("action");
//
//        System.out.println(" action = " + action );
//        if ( action == null )
//        {
//			 System.out.println(" calling  showAddUserWizard " );
//			 showAddUserWizard(request, response, getServletContext());
//        }
//        else if (action.equals("commitAddUsers"))
//        {
//            commitAddUsers(request, response, getServletContext());
//        }
//    }


		String msSmtpServer;
		String msSmtpPort;
		String msUserName;
		String msPassword;

	public final static String Seperator = new Character((char)1).toString();
	public final static String ContentDelimeter = new Character((char)2).toString();
	StringTokenizer st;


    public void service (HttpServletRequest request,
	    					HttpServletResponse response)
	throws ServletException, IOException
    {

        
		ServletConfig config = getServletConfig();

		msSmtpServer	= getServletConfig().getInitParameter("smptserver");
		msSmtpPort		= getServletConfig().getInitParameter("smtpport");
		msUserName		= getServletConfig().getInitParameter("username");
		msPassword		= getServletConfig().getInitParameter("password");

		String buf = getRequestBuffer(request).toString();
		System.out.println("Recieved Buffer = " + buf);
		st = new StringTokenizer( buf );



		commitAddUsers(request,response);
		
    }

	 public void showAddUserWizard(HttpServletRequest req, HttpServletResponse res, ServletContext sc)
	    {
			System.out.println("selectTableForWizard");
			Connection connection = null;

	        try
	        {
	            sc.getRequestDispatcher("/jsp/wizards/adduser_wizard.jsp"). forward(req, res);
	        }
	        catch ( Exception e )
	        {
	           e.printStackTrace();
	        }
	    }


    public void commitAddUsers(HttpServletRequest req, HttpServletResponse res)
    throws ServletException, IOException 
	{

		String wrkstr;
        int userId =  Integer.parseInt(  st.nextToken (Seperator) );
		int tableId = Integer.parseInt(  st.nextToken (Seperator) );
        int memberId =Integer.parseInt(  st.nextToken (Seperator) );
		int nhId =    Integer.parseInt(  st.nextToken (Seperator) );


		System.out.println("userid" + userId);
		System.out.println("tableId" + tableId);
		System.out.println("memberId" + memberId);
		System.out.println("nhId" + nhId);

	





		 Connection connection = null;
		 TransactionManager tm = null;
		 String result =null;

			try
			{
				DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
				connection = databaseloader.getConnection();
				tm = new TransactionManager( connection, userId);
				int tid = tm.startTransaction();
				BoardwalkUserWizards buw = new BoardwalkUserWizards();
				result =  buw.addUsersInBulk( connection, tableId, userId,memberId, nhId, tid, msSmtpServer, msSmtpPort, msUserName, msPassword );

			
				tm.commitTransaction();
				result = "Success";

			}
			catch ( Exception e )
			{

				e.printStackTrace();

			   try
			   {
						tm.rollbackTransaction();
			   }
			   catch( SQLException sql )
			   {
				   sql.printStackTrace();
			   }
			   e.printStackTrace();
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



		   try
			{
				if (result.equalsIgnoreCase("Success"))
				{
					commitResponseBuffer("Success:" + "result" + xlService.Seperator + result + xlService.Seperator + "tableId" + xlService.Seperator + tableId, res);	// added by Prem Pawar on 05/22/12 for FirstName/LastName of User
				}
				else
				{
					commitResponseBuffer("Failure:" + "result" + xlService.Seperator + result + xlService.Seperator + "tableId" + xlService.Seperator + tableId, res);	// added by Prem Pawar on 05/22/12 for FirstName/LastName of User

				}
			}
			catch ( Exception e )
			{
			   e.printStackTrace();
			}


    }


//    public void doGet(HttpServletRequest request,
//    HttpServletResponse response)throws ServletException, IOException
//
//    {
//        doPost(request, response);
//    }
}
