package servlets;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import com.boardwalk.database.*;
import com.boardwalk.user.*;
import com.boardwalk.exception.*;

import java.sql.*;                  // JDBC package
import javax.sql.*;                 // extended JDBC package
import com.boardwalk.util.*;
import com.boardwalk.member.*;


public class BW_UsersLogic extends BWServletLogic
{
    ServletContext sc;
    HttpServletRequest req;
    HttpServletResponse res;

    public BW_UsersLogic(HttpServlet srv) {
        super(srv);
    }

    public void doPost (HttpServletRequest request,
	    HttpServletResponse response)throws ServletException, IOException
    {
		// check the action requested
        String action = request.getParameter ("action");
        sc = getServletContext();
		req = request;
		res = response;

		if (action != null && action.equals("commitUser"))
		{
			System.out.println(" BW_Users action = " + action);
			userCommit();
			return;
		}

		if (authenticate(request, response) == false)
		{
			return;
		}
		// get the session and go to login if necessary
        System.out.println(" BW_Users action = " + action);
		// no action means go to the user main page
		if (action == null) {
			userReport();
		}
		else if (action.equals("createUser")) {
			userCreate();
		}
		else if (action.equals("editProfile")) {
				editProfile();
		}
		else if (action.equals("updateProfile")) {
					updateProfile();
		}
		else if (action.equals("changePassword")) {
					changePassword();
		}
		else if (action.equals("commitPassword")) {
					commitPassword();
		}
		else if (action.equals("cancel")) {
					cancel();
		}
		else {
			userReport();
		}
    }

	public void cancel()
	{
		try
		{
		//sc.getRequestDispatcher("/jsp/admin/nh_members.jsp").forward(req,res);
			String selNhid = req.getParameter("selNhid");
			String redirectURL = "/BW_Neighborhoods?action=membersNH&selNhid="+ Integer.parseInt(selNhid);
			 res.sendRedirect(req.getContextPath() + redirectURL);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	public void updateProfile()throws ServletException, IOException
	{
		// get the user ID
		HttpSession hs = req.getSession(true);
		int userId = ((Integer)hs.getAttribute("userId")).intValue();

		// get the profile updates
		String firstName = req.getParameter("firstName");
		String lastName = req.getParameter("lastName");
		String emailAddress = req.getParameter("emailAddress");
		String alias = req.getParameter("alias");

		// commit the changes to the server
		Connection connection  = null;
		int tid = -1;
		TransactionManager tm = null;

		try {
			DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
			connection = databaseloader.getConnection();

			tm = new TransactionManager( connection,1);
			tid = tm.startTransaction();
			UserManager.updateProfile(connection, userId, firstName, lastName,emailAddress, alias);
			tm.commitTransaction();

			// forward the request
			sc.getRequestDispatcher("/MyCollaborations?action=collaborationTree").forward(req,res);


		} catch (Exception e) {
			try
			{
				tm.rollbackTransaction();
			}
			catch( SQLException sqlfatal )
			{
				sqlfatal.printStackTrace();
			}

			if ( e.getClass().getName().equals("com.boardwalk.exception.BoardwalkException") )
			{
				BoardwalkException bwe = (BoardwalkException)e;
				req.setAttribute("com.boardwalk.exception.BoardwalkException", bwe);
				sc.getRequestDispatcher("/jsp/admin/change_password.jsp").forward(req,res);
			}
			else
			{
				e.printStackTrace();
			}

		} finally {
			try {
				connection.close();
			}
			catch( SQLException sql ) {
				sql.printStackTrace();
			}
		}



	}

	public void changePassword()throws ServletException, IOException
	{
		String userAddress = req.getParameter("userAddress");
		// forward the request
		sc.getRequestDispatcher("/jsp/admin/change_password.jsp").forward(req,res);
	}

	public void commitPassword()throws ServletException, IOException
	{
		// get the user ID
		HttpSession hs = req.getSession(true);
		int userId = ((Integer)hs.getAttribute("userId")).intValue();

		String oldPassword = req.getParameter("oldPassword"); // old password
        String newPassword = req.getParameter("newPassword"); // new password

		Connection connection  = null;
		int tid = -1;
		TransactionManager tm = null;

		try {
			DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
			connection = databaseloader.getConnection();

			tm = new TransactionManager( connection,1);
            tid = tm.startTransaction();
			UserManager.updatePassword(connection, userId, oldPassword, newPassword);
			System.out.println("commitPassword()::Updated Password OK");
			tm.commitTransaction();

			NewUser nu = UserManager.getUserProfile(connection, userId);
			req.setAttribute("UserProfile", nu);
			// forward the request
			sc.getRequestDispatcher("/jsp/admin/user_profile.jsp").forward(req,res);


		} catch (Exception e) {
			System.out.println("commitPassword()::Updated Password PROBLEM");
			try
			{
				tm.rollbackTransaction();
			}
			catch( SQLException sqlfatal )
			{
				sqlfatal.printStackTrace();
            }

			if ( e.getClass().getName().equals("com.boardwalk.exception.BoardwalkException") )
			{
				System.out.println("BW_Users::commitPassword()->Caught BoardwalkException");
				BoardwalkException bwe = (BoardwalkException)e;
				req.setAttribute("com.boardwalk.exception.BoardwalkException", bwe);
				sc.getRequestDispatcher("/jsp/admin/change_password.jsp").forward(req,res);
            }
            else
            {
				e.printStackTrace();
			}

		} finally {
			try {
				connection.close();
			}
			catch( SQLException sql ) {
				sql.printStackTrace();
			}
		}

	}


	public void editProfile() throws ServletException, IOException {
		// get the user ID
		HttpSession hs = req.getSession(true);
		int userId = ((Integer)hs.getAttribute("userId")).intValue();
		Connection connection  = null;
		try {
			DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
			connection = databaseloader.getConnection();

			NewUser nu = UserManager.getUserProfile(connection, userId);

			req.setAttribute("UserProfile", nu);
			// forward the request
			sc.getRequestDispatcher("/jsp/admin/user_profile.jsp").forward(req,res);


		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				connection.close();
			}
			catch( SQLException sql ) {
				sql.printStackTrace();
			}
		}
	}
    public void userCreate() throws ServletException, IOException
    {
		// forward the request
		sc.getRequestDispatcher("/admin/user_create.jsp").forward(req,res);
    }

	public static boolean isActiveUser(String emailAdd)
	{
		 Connection connection 	= null;
		 String isActive 		= "";
		 boolean retValue 		= false;
		 PreparedStatement stmt = null;
		   try {
			    DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
				connection = databaseloader.getConnection();
				//System.out.println("value of email address^^^^^^^^^"+emailAdd);
				 String query = "SELECT ACTIVE FROM BW_USER WHERE EMAIL_ADDRESS = ? ";
				 stmt = connection.prepareStatement(query);
				 stmt.setString(1, emailAdd);
				 ResultSet rs = stmt.executeQuery();
				 while (rs.next())
						{
							isActive	= rs.getString("ACTIVE");
						}
						System.out.println("________value of is active__________" +isActive);
						if(isActive.equals("1"))
							retValue = true;
						else
							retValue = false;
				}
				catch ( Exception e )
					{
						e.printStackTrace();
					}
			return retValue;

	}
	
	//Modified by Lakshman on 20181011 to fix the Issue Id: 14347
	public static boolean IsCreatorOfCollab(int objectId, String objectType)
	{
		 Connection connection = null;
		 int countMemberId = -1;
		 boolean retValue = false;
		PreparedStatement stmt = null;
		   try {
			    DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
				connection = databaseloader.getConnection();
				System.out.println("Value of objectId: " + objectId);

				String query = "";
				if (objectType.equals("user")){
					query = "SELECT COUNT(BW_COLLAB.MEMBER_ID) COUNT FROM BW_COLLAB,BW_MEMBER WHERE BW_COLLAB.MEMBER_ID = BW_MEMBER.ID AND BW_MEMBER.USER_ID =? ";
				}
				if (objectType.equals("member")){
					query = "SELECT COUNT(BW_COLLAB.MEMBER_ID) COUNT FROM BW_COLLAB,BW_MEMBER WHERE BW_COLLAB.MEMBER_ID = BW_MEMBER.ID AND BW_MEMBER.ID = ? ";
				}
				 
				 stmt = connection.prepareStatement(query);
				 stmt.setInt(1, objectId);
				 ResultSet rs = stmt.executeQuery();
				 while (rs.next())
						{
							countMemberId	= rs.getInt("COUNT");
						}
						System.out.println("________value of is countMemberId _________" +countMemberId);
						if(countMemberId > 0)
							retValue = true;
						else
							retValue = false;
				}
				catch ( Exception e )
					{
						e.printStackTrace();
					}
			return retValue;

	}

    public void userCommit() throws ServletException, IOException
    {
		System.out.println("userCommit");
		String	username	= req.getParameter("username"); // user email address
        String	password	= req.getParameter("password"); // password
        String	fname		= req.getParameter("fname"); // first name
        String	lname		= req.getParameter("lname"); // last name
		int		is_active_flag = 1;
		int		selNhid = -1;
		//boolean check_access = false;
		selNhid = Integer.parseInt(req.getParameter("selNhid"));
		String check_access ="";
		check_access = req.getParameter("checkAccess");
		System.out.println("-----------Value of check access-------"+check_access);



        int    m_user_id = -1;
        Connection connection = null;
        int tid = -1;
        TransactionManager tm = null;

        try {
			System.out.println("userCommit try");
			NewUser nu = new NewUser(username , password, fname, lname, is_active_flag);

            DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
            System.out.println("getting a connection");
            connection = databaseloader.getConnection();

            tm = new TransactionManager( connection,1);
            tid = tm.startTransaction();
            System.out.println("creating a user");
            m_user_id = UserManager.createUser(connection, nu);
            System.out.println("m_user_id=" + m_user_id);
            if ( m_user_id > 0 )
            {
				if(check_access.equals("true"))
				{
					int Member_id = MemberManager.createMember(connection,tid,m_user_id,selNhid);
				}
				 tm.commitTransaction();
				 // sc.getRequestDispatcher("/MyCollaborations").forward(req,res);
				 //String selNhid = req.getParameter("selNhid");
				 String redirectURL = "/BW_Neighborhoods?action=membersNH&selNhid="+selNhid;
				 res.sendRedirect(req.getContextPath() + redirectURL);
				// res.sendRedirect(req.getContextPath() + "/MyCollaborations");
			}
			else
			{
				try
					{
						System.out.println("roll back transaction");
						tm.rollbackTransaction();
					}
					catch( SQLException sqlfatal )
					{
							 sqlfatal.printStackTrace();
            	}
            	catch( Exception e )
            	{
					e.printStackTrace();
				}
				req.setAttribute("username", username);
				req.setAttribute("fname", fname);
				req.setAttribute("lname", lname);
				BoardwalkException bw = new  BoardwalkException( 11003 );
                req.setAttribute("com.boardwalk.exception.BoardwalkException", bw);
				System.out.println("forwarded the request " + username + fname + lname);
				sc.getRequestDispatcher("/jsp/admin/register.jsp").forward(req, res);
				return;
			}

        }

        catch ( Exception e )
        {
            e.printStackTrace();

            try
            {
                tm.rollbackTransaction();
            }
            catch( SQLException sqlfatal )
            {
                sqlfatal.printStackTrace();
            }

            BoardwalkException bw = new  BoardwalkException( 11004, e );
			req.setAttribute("com.boardwalk.exception.BoardwalkException", bw);
                sc.getRequestDispatcher("/jsp/admin/login.jsp").forward(req,res);

        }
        finally
        {
			System.out.println("in finally");
            try {
                connection.close();
            }
            catch ( SQLException sql )
            {
                sql.printStackTrace();
				System.out.println("sending exception");
                BoardwalkException bw = new  BoardwalkException( 11004, sql );
                req.setAttribute("com.boardwalk.exception.BoardwalkException", bw);
                 sc.getRequestDispatcher("/jsp/admin/login.jsp").forward(req,res);
            }
             catch ( Exception ex )
			            {
			                ex.printStackTrace();
							System.out.println("sending exception");
			                BoardwalkException bw = new  BoardwalkException( 11004, ex );
			                req.setAttribute("com.boardwalk.exception.BoardwalkException", bw);
			                 sc.getRequestDispatcher("/jsp/admin/login.jsp").forward(req,res);
            }
        }
    }



    public void userReport() throws ServletException, IOException
    {
//	Vector userList = userappl.getuserlist();
//	req.setAttribute("userList", userList);
	// forward the request
	sc.getRequestDispatcher("/admin/user_report.jsp").forward(req,res);
    }

    // Handle the get methods here, forward the request and response to
    // the post method to handle
    public void doGet (HttpServletRequest request,
	    HttpServletResponse response)throws ServletException, IOException

    {
	doPost(request, response);
    }
}
