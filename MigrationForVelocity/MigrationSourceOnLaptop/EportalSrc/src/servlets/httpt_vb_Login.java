package servlets;
/*
 *  This presents a list of collaboration available to a user
 */
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import com.boardwalk.database.DatabaseLoader;
import com.boardwalk.database.*;
import com.boardwalk.user.UserManager;
import com.boardwalk.user.User;
import com.boardwalk.member.Member;
import com.boardwalk.exception.*;

import java.sql.*;                  // JDBC package
import javax.sql.*;                 // extended JDBC packa

public class httpt_vb_Login extends xlService   implements
 SingleThreadModel  {

    int userId;
    String userName;
    String userPassword;
    int memberId = -1;
    int nhid = -1;
	String nhName = "";

    public void service (HttpServletRequest request,
	    HttpServletResponse response)throws ServletException, IOException
    {
	  // System.out.println( "came to httpt login servlet");
        
		userName = (String)request.getAttribute("userName");
		userPassword = (String)request.getAttribute("userPassword");
		System.out.println("httpt_vb_Login : userName = " + userName);
		System.out.println("httpt_vb_Login : userPassword = " + userPassword);
		if (  userName == null  || userName == ""  || userPassword ==null || userPassword == "" )
		{
			commitResponseBuffer("failure", response);
		}
		else
		{
			Connection connection = null;
			Connection connection1 = null;;
            try
            {
                DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
                connection = databaseloader.getConnection();
/*
				userId = -1;
                userId = UserManager.authenticateUser(connection, userName,userPassword);
 */

                userId = -1;
                User u = UserManager.getUser(connection, userName);
                if (u != null)
                	userId = u.getId();

                if ( userId != -1 )
                {
					DatabaseLoader databaseloader1 = new DatabaseLoader(new Properties());
					connection1 = databaseloader1.getConnection();
					Hashtable memberships  = UserManager.getMembershipsForUser( connection1, userId );
					Enumeration memberIds = memberships.keys();
					System.out.println("Authenticating user " + userName + "/" +  userPassword + " membershipid=" + memberId);
					System.out.println("num memberships = " + memberships.size());
					if (  memberships.size() == 1 )
					{

						memberId =((Integer) memberIds.nextElement()).intValue();
						nhid =((Member) memberships.get(        new Integer(memberId)   )).getNeighborhoodId();
						nhName = ((Member) memberships.get(        new Integer(memberId)   )).getNeighborhoodName();

						commitResponseBuffer("Success:" + userId + xlService.Seperator + memberId + xlService.Seperator + nhid + xlService.Seperator + nhName + xlService.Seperator , response);
						  // System.out.println("Success: Username  "+userId + " Member Id " + memberId);
						System.out.println("Success:" + userId + xlService.Seperator + memberId + xlService.Seperator + nhid + xlService.Seperator + nhName + xlService.Seperator);
					}
					else
					if (  memberships.size() > 1 )
					{
						for (int i=0; i<memberships.size(); i++)
						{
						memberId =((Integer) memberIds.nextElement()).intValue();
						nhid =((Member) memberships.get(        new Integer(memberId)   )).getNeighborhoodId();
						nhName = ((Member) memberships.get(        new Integer(memberId)   )).getNeighborhoodName();
						System.out.println("LoopXXX:" + userId + xlService.Seperator + memberId + xlService.Seperator + nhid + xlService.Seperator + nhName + xlService.Seperator);
						}
						//servletOut.println("Success:" + userId + xlService.Seperator + memberId + xlService.Seperator + nhid + xlService.Seperator + nhName + xlService.Seperator );
						commitResponseBuffer("Success:" + userId + xlService.Seperator + memberId + xlService.Seperator + nhid + xlService.Seperator + nhName + xlService.Seperator, response);
	
					}
					else
					if (  memberships.size() == 0 )
					{
						// User isn't a member of any neighborhood ...if the refering URL allows access to public users then we can show it
						// if  not the user has to join a relevant neighborhood
						 //servletOut.println("Success:"+userId+xlService.Seperator);
						 BoardwalkMessage bwmsg= new BoardwalkMessages().getBoardwalkMessage( 11001 );
						 commitResponseBuffer("failure"
						 					+ xlService.ContentDelimeter
											+ bwmsg.getCause() + "," + bwmsg.getPotentialSolution(), response );
					}

                }
                else
                {
					 BoardwalkMessage bwmsg= new BoardwalkMessages().getBoardwalkMessage( 11004 );
					 commitResponseBuffer("failure"
                    										+ xlService.ContentDelimeter
                    										+ bwmsg.getCause() + "," + bwmsg.getPotentialSolution(),
                    									response);
				}

            }
            catch ( Exception e )
            {
               e.printStackTrace();
            }
            finally
            {
              try
              {
				  if ( connection != null )
                		connection.close();
               	if ( connection1 != null )
                connection1.close();
              }
              catch ( SQLException sql )
              {
                sql.printStackTrace();
              }
            }
    	}
    }
}
