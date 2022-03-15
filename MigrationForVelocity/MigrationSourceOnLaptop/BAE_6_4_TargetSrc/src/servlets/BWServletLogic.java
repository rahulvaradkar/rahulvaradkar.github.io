/*
 * BWServlet.java
 *
 * Created on June 17, 2002, 7:48 PM
 */

package servlets;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.lang.Exception;


import com.boardwalk.database.*;
import com.boardwalk.user.*;
import com.boardwalk.member.*;


import java.sql.*;                  // JDBC package
import com.boardwalk.util.*;

/**
 *
 * @author  Sarang Kulkarni
 */
public abstract class BWServletLogic 
 {
    ServletContext sc;
  	protected  int userId;
    protected  String userName;
	protected  String userPassword;
	protected  int memberid;
	protected  int nhId;
    protected  String nhName;
    protected HttpServlet bwServlet;

    public BWServletLogic(HttpServlet srv) {
        this.bwServlet = srv;
    }

	public boolean setSession(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException
	{
		boolean status = true;
        userName = (String)request.getParameter("userName");
		HttpSession hs = request.getSession(true);
        if ( (  userName != null ) && ( !userName.equals("")) )
        {
			System.out.println("Authenticating a Non Browser request");
			// set the session
			boolean isValidUser = authenticateNonBrowserRequest(request,response);
			if ( !isValidUser )
			{
				ServletOutputStream servletOut = response.getOutputStream();
				String responseToRequest = "Invalid User";
				response.setContentLength ( responseToRequest.length() );
				servletOut.print(responseToRequest);
				servletOut.close();
				return false;
			}


			hs = request.getSession(true);
			String referer = (String)hs.getAttribute("Referer");

			// hs.setAttribute("userId", new Integer(userId));
			//  hs.setAttribute("userEmailAddress", userName);
			// request.removeAttribute("userName");
			// request.removeAttribute("userPassword");
			//String redirectURL = "/MyCollaborations";
			//System.out.println("redirectURL  " + redirectURL );
			//response.sendRedirect(request.getContextPath() + redirectURL);
			//return;

        }
        else
        {
			// authenticate user
            status = authenticate(request, response);
            if (status == false)
            	return false;
			//hs = request.getSession(true);

			userId = ((Integer)hs.getAttribute("userId")).intValue();
			userName = (String)hs.getAttribute("userEmailAddress");
			memberid = ((Integer)hs.getAttribute("memberId")).intValue();
			nhId = ((Integer)hs.getAttribute("nhId")).intValue();
			nhName = (String)hs.getAttribute("nhName");

			//BoardwalkSession bws = 	(BoardwalkSession)hs.getAttribute("bwSession");
			//if ( bws != null )
			//    request.setAttribute("bwSession",bws);


        }

		return status;
	}

    public boolean authenticate(HttpServletRequest req, HttpServletResponse res)
        throws IOException, ServletException
    {
		boolean retval = true;
        ///////////////////////////////////////////////////
        // Common stuff for all BW servlets, should be done
        // in a Filter
        /////////////////////////////////////////////////

        // get the session and go to login if necessary
        //sc = getServletContext();
        HttpSession hs = req.getSession(true);

        Integer userId = (Integer)hs.getAttribute("userId");
		if (userId == null)
		{
			String orignialReferer = (String)hs.getAttribute("Referer");
			System.out.println("OriginalReferer = " + orignialReferer);
			if ( orignialReferer == null )
			{
				String requestURL = req.getRequestURI();
				String requestQueryString = req.getQueryString();
				if ( requestQueryString != null )
				{
					requestURL = requestURL +"?"+ requestQueryString;
				}
				System.out.println(" Setting referring page to " +requestURL);
				hs.setAttribute("Referer",requestURL);
			}

			//System.out.println(sc.getServerInfo());
			System.out.println(" returning a login form");
			try
			{
				retval = false;
				req.getRequestDispatcher("/jsp/admin/login.jsp").forward(req,res);
			}
			catch( Exception e )
			{
				e.printStackTrace();
			}
		}
		else
		{
			Object obj = hs.getAttribute("neigborhoods");
			if ( obj != null )
			{
				req.setAttribute("neigborhoods",obj);
			}
		}

		return retval;
    }

	// create a session object for user
	public boolean authenticateNonBrowserRequest(HttpServletRequest request, HttpServletResponse response)
	throws IOException, ServletException
	{
		///////////////////////////////////////////////////
		// Common stuff for all BW servlets, should be done
		// in a Filter
		/////////////////////////////////////////////////
		HttpSession hs = request.getSession(true);
		BoardwalkSession exbws = (BoardwalkSession)hs.getAttribute("bwSession");
		if (exbws != null)
		{
			//System.out.println("debug::authenticateNonBrowserRequest before  bws userEmailAddress  " + exbws.userEmailAddress);
		}
		else
		{
			System.out.println("debug::authenticateNonBrowserRequest before bws is null");
		}

		Connection connection = null;

		if ((userName != null) && (!userName.equals("")))
		{
			BoardwalkSession bws = new BoardwalkSession();
			try
			{
				userName = (String)request.getParameter("userName");
				userPassword = (String)request.getParameter("userPassword");
				userId = Integer.parseInt(request.getParameter("userId"));
				memberid = Integer.parseInt(request.getParameter("memberId"));
				nhId = Integer.parseInt(request.getParameter("nhId"));
				nhName = (String)request.getParameter("nhName");

				//System.out.println("Creating session for user " + " " + userName + " " + userPassword + " " + userId);

				hs.setAttribute("userId", new Integer(userId));
				hs.setAttribute("userEmailAddress", userName);
				hs.setAttribute("memberId", new Integer(memberid));
				hs.setAttribute("nhId", new Integer(nhId));
				hs.setAttribute("nhName", nhName);

				bws.userId = new Integer(userId);
				bws.memberId = new Integer(memberid);
				bws.nhId = new Integer(nhId);
				bws.nhName = nhName;
				bws.selNhid = new Integer(nhId);
				// bws.Referer = referer;
				bws.userEmailAddress = userName;
			}
			catch (Exception e) // if all the session parameters are not defined
			{
				request.getRequestDispatcher("/jsp/admin/login.jsp").forward(request, response);
				return false;
			}
			try
			{
				DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
				connection = databaseloader.getConnection();

				Hashtable memberships = UserManager.getMembershipsForUser(connection, userId);
				Vector membershipList = UserManager.getMembershipListForUser(connection, userId);
				bws.membershipList = membershipList;
				bws.memberIdToMember = memberships;
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return false;
			}
			finally
			{
				try
				{
					if (connection != null)
						connection.close();
				}
				catch (SQLException sql)
				{
					sql.printStackTrace();
					return false;
				}
			}

			hs.setAttribute("bwSession", bws);
			System.out.println("BWSession set for non-browser authentication " + bws.memberId + " " + bws.nhName + " memberships = " + bws.memberIdToMember.size());

			return true;

		}
		else
		{
			return false;

		}
	}
	public boolean  loginUser ()
	{
		Connection connection = null;
		try
		{
			DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
			connection = databaseloader.getConnection();

			if (  userName != null  && !"".equals(userName)  && userPassword != null && !"".equals(userPassword) )
			{

				int dbUserId = UserManager.authenticateUser(connection, userName,userPassword, false);
				System.out.println("dbuserId = " + dbUserId );
				if ( dbUserId != -1 && dbUserId  == userId)
				{
					return true;
				}
				else
				{
					return false;
				}
			}
			else
				return false;
		}
		catch ( Exception e )
		{
		   e.printStackTrace();
		   return false;
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
			return false;
		  }
		}
	}



	public void switchCurrentMembership(HttpServletRequest req, HttpServletResponse res)
	throws IOException, ServletException
	{
		String switchMembershipToParam = req.getParameter("switchMembershipTo");
		String redirectURL = "/MyCollaborations";
		if ( switchMembershipToParam != null && !switchMembershipToParam.trim().equalsIgnoreCase("") )
		{
			Integer switchMembershipToId = new Integer(switchMembershipToParam);
			HttpSession hs = req.getSession(true);
			BoardwalkSession bws = (BoardwalkSession)hs.getAttribute("bwSession");
			Member  mb = (Member)bws.memberIdToMember.get(switchMembershipToId );
			System.out.println(" Inside switchCurrentMembership " + mb.getNeighborhoodId() + " " + mb.getNeighborhoodName() + " " + mb.getNeighborhoodId() );
			bws.memberId = switchMembershipToId;
			bws.nhId = new Integer(mb.getNeighborhoodId());
			bws.nhName =mb.getNeighborhoodName();
			bws.selNhid = new Integer(mb.getNeighborhoodId());
			redirectURL= redirectURL+"?SelNhid="+mb.getNeighborhoodId();

			hs.setAttribute("nhId", new Integer(mb.getNeighborhoodId()));
			hs.setAttribute("memberId", new Integer(mb.getId())  );
			hs.setAttribute("nhName", mb.getNeighborhoodName());
		}

		res.sendRedirect(req.getContextPath() + redirectURL);
    }
    
    protected ServletContext getServletContext() {
        return this.bwServlet.getServletContext();
    }
}
