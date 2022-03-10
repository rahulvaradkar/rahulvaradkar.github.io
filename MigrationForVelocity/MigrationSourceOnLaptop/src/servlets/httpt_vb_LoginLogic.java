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
import com.boardwalk.table.TableManager; //Added by Lakshman on 20190530 for ID Independent Template

import java.sql.*;                  // JDBC package
import javax.sql.*;                 // extended JDBC packa

public class httpt_vb_LoginLogic extends xlServiceLogic {

    int userId;
    String userName;
    //String userPassword; // auth fix -shirish 20150716
	String templateMode;
	String nhHierarchy = null;
	int manifestId = -1; //Modified by Lakshman on 20190530 for ID Independent Template
	String manifestCuboidPath = null; //Added by Lakshman on 20190530 for ID Independent Template
    int memberId = -1;
    int nhid = -1;
	String nhName = "";
    
    public httpt_vb_LoginLogic(httpt_vb_Login srv) {
        super(srv);
    }

    public void service (HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException
    {
		//System.out.println( "came to httpt login servlet");
		userName = (String)request.getAttribute("userName");
		//userPassword = (String)request.getAttribute("userPassword");
		templateMode = (String)request.getAttribute("templateMode"); //Added to get the Membership ID in case of Multiple Membership
		nhHierarchy = (String)request.getAttribute("nhHierarchy"); //Added to get the Membership ID in case of Multiple Membership
		//manifestId = (String)request.getAttribute("manifestId"); //Added to get the Membership ID in case of Multiple Membership
		manifestCuboidPath = (String)request.getAttribute("manifestCuboidPath"); //Added by Lakshman on 20190530 for ID Independent Template
		
		System.out.println("httpt_vb_Login : templateMode 		= " + templateMode);
		System.out.println("httpt_vb_Login : nhHierarchy 		= " + nhHierarchy);
		System.out.println("httpt_vb_Login : manifestCuboidPath = " + manifestCuboidPath);

        // auth fix shirish 20150716
		//System.out.println("httpt_vb_Login : userPassword = " + userPassword);
		if (  userName == null  || userName.equals("")
				/*|| userPassword ==null || userPassword == "" */)
		{
			commitResponseBuffer("failure", response);
		}
		else
		{
			Connection connection = null;
			Connection connection1 = null;

            try
            {
                DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
                connection = databaseloader.getConnection();

/*				userId = -1;
                userId = UserManager.authenticateUser(connection, userName,userPassword);
*/

                userId = -1;
                User u = UserManager.getUser(connection, userName);

                if (u != null)
				{
					System.out.println(" check before ");
	                userId = u.getId();
					// auth fix shirish 20150716
     				HttpSession hs = request.getSession(true);
        			hs.setAttribute("userId", userId);
				}

				System.out.println(" check before "+userId);

                if ( userId > 0 )
                {
					DatabaseLoader databaseloader1 = new DatabaseLoader(new Properties());
					connection1 = databaseloader1.getConnection();

					//Changes related to Login Enhancements for Password Complexity and User Authentication on 20170524 (And fix for Issue Id: 14200)
					UserManager.userLoginSuccess(connection1, userId);
					
					System.out.println(" Hashtable memberships check before "+userId);
					Hashtable memberships  = UserManager.getMembershipsForUser( connection1, userId );
					System.out.println(" Hashtable memberships check Aftr "+userId);
					Enumeration memberIds = memberships.keys();

					if (  memberships.size() == 1 )
					{

						memberId =((Integer) memberIds.nextElement()).intValue();
						System.out.println("httpt_vb_Login: Single Member Id: "+memberId);
						
						nhid =((Member) memberships.get( new Integer(memberId) )).getNeighborhoodId();
						nhName = ((Member) memberships.get( new Integer(memberId) )).getNeighborhoodName();

						commitResponseBuffer("Success:" + userId + xlServiceLogic.Seperator + memberId + xlServiceLogic.Seperator + nhid + xlServiceLogic.Seperator + nhName + xlServiceLogic.Seperator , response);
						System.out.println("Success:" + userId + xlServiceLogic.Seperator + memberId + xlServiceLogic.Seperator + nhid + xlServiceLogic.Seperator + nhName + xlServiceLogic.Seperator);
					}
					else
					if (  memberships.size() > 1 )
					{
						//Added by Lakshman on 20190530 for ID Independent Template
						if (manifestCuboidPath != null)
							manifestId = TableManager.getTableIdFromPath(connection, manifestCuboidPath);

						//Added to get the Membership ID in case of Multiple Membership
						memberId = UserManager.checkMembershipStatus(connection, userId, templateMode, nhHierarchy, manifestId);
						System.out.println("httpt_vb_Login: Multiple Member Id: "+memberId);

						if (memberId != -1)
						{
							nhid =((Member) memberships.get(new Integer(memberId))).getNeighborhoodId();
							nhName = ((Member) memberships.get(new Integer(memberId))).getNeighborhoodName();
							
							commitResponseBuffer("Success:" + userId + xlServiceLogic.Seperator + memberId + xlServiceLogic.Seperator + nhid + xlServiceLogic.Seperator + nhName + xlServiceLogic.Seperator, response);
							System.out.println("Success:" + userId + xlServiceLogic.Seperator + memberId + xlServiceLogic.Seperator + nhid + xlServiceLogic.Seperator + nhName + xlServiceLogic.Seperator);
						}
						else
						{
							BoardwalkMessage bwmsg= new BoardwalkMessages().getBoardwalkMessage( 11007 );
							commitResponseBuffer("failure" + xlServiceLogic.ContentDelimeter + bwmsg.getCause() + "," + bwmsg.getPotentialSolution(), response );
						}
					}
					else
					if (  memberships.size() == 0 )
					{
						// User isn't a member of any neighborhood ...if the refering URL allows access to public users then we can show it
						// if  not the user has to join a relevant neighborhood
						 //servletOut.println("Success:"+userId+xlServiceLogic.Seperator);
						 BoardwalkMessage bwmsg= new BoardwalkMessages().getBoardwalkMessage( 11001 );
						 commitResponseBuffer("failure"
						 					+ xlServiceLogic.ContentDelimeter
											+ bwmsg.getCause() + "," + bwmsg.getPotentialSolution(), response );
					}

                }
                else
                {
					BoardwalkMessage bwmsg = null;
					System.out.println(" Check here userId >>>>>>>>>"+userId);

					if (userId == -1)
					{
						bwmsg= new BoardwalkMessages().getBoardwalkMessage( 11004 );
					}
					if (userId == 0)
					{
						bwmsg= new BoardwalkMessages().getBoardwalkMessage( 11006 );
					}

					 commitResponseBuffer("failure"
                    										+ xlServiceLogic.ContentDelimeter
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
