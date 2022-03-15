package servlets;

import java.io.*;
import java.lang.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import com.boardwalk.database.*;
import com.boardwalk.user.*;
import com.boardwalk.exception.*;
import com.boardwalk.user.*;
import com.boardwalk.neighborhood.*;
import com.boardwalk.member.*;
import com.boardwalk.util.*;
import com.boardwalk.table.*;
import com.boardwalk.user.*;



import java.sql.*;                  // JDBC package
import javax.sql.*;                 // extended JDBC package



public class LoginServlet extends HttpServlet implements SingleThreadModel {
    public void doGet (HttpServletRequest request,
	    HttpServletResponse response) throws ServletException, IOException
    {
		
		
		// Collect the username and password from the form
		String useraddress = request.getParameter("username");
		String password = request.getParameter("password");

		ServletContext sc = getServletContext();

		HttpSession hs = request.getSession(true);
		
		String referer = (String)hs.getAttribute("Referer");
		//sujith
		if ( referer == null )
			{
				String requestURL = request.getRequestURI();
				System.out.println("requestURL" +requestURL);
				
				String path = requestURL;
				int pos = path.lastIndexOf("/");
				//String x =path.substring(pos+1 , path.length()-1);
				String x =path.substring(0, pos);
				System.out.println("X S :" +x);
				referer= x + "/MyCollaborations";
				System.out.println("referer S :" +referer);
				
			} 
		//sujith

        // if this is a registration request then send him off to the registration page
        String action = request.getParameter("action");
        if ( action != null && action.equalsIgnoreCase("register" ))
        {
          sc.getRequestDispatcher("/jsp/admin/register.jsp").forward(request, response);
		  return;
        }

		boolean uservalid = false;
        int userId = -1;

		// check if  useraddress is correctly supplied then we try to authenticate the user
		 if ( useraddress != null && !useraddress.equals("") )
		 {
			Connection connection = null;
			try
			{
				DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
				connection = databaseloader.getConnection();
				// actual authentication happens in the filter ExcelAuthenticationFilter
				// The filter might be implemented by third party, so the authentication will happen outside Boardwalk.
				// Here we simply make sure the user is present in the Boardwalk database
				User u = UserManager.getUser(connection, useraddress); 
				if (u != null)
					userId = u.getId();
				if (userId > 0  )
				{
					//Changes related to Login Enhancements for Password Complexity and User Authentication on 20170524
					UserManager.userLoginSuccess(connection, userId);
					
					uservalid = true;
				}
				else
				{
					if (userId == -1)
						request.setAttribute("com.boardwalk.exception.BoardwalkException", new BoardwalkException(11004));
					if (userId == 0)
						request.setAttribute("com.boardwalk.exception.BoardwalkException", new BoardwalkException(11006));

					sc.getRequestDispatcher("/jsp/admin/login.jsp").forward(request, response);
					return;
				}

			}
			catch( Exception e )
			{
				e.printStackTrace();
			}
			finally
			{
				try
				{
					if ( connection != null )
						connection.close();
				}
				catch( SQLException sql )
				{
					sql.printStackTrace();
				}
			}
        }
        else
        {
			System.out.println("dispatching to login");
			sc.getRequestDispatcher("/jsp/admin/login.jsp").include(request, response);
			return;
		}

		if ( uservalid )
		{

			System.out.println("User is valid; id = " + userId);

			hs.setAttribute("userId", new Integer(userId));
			hs.setAttribute("userEmailAddress", useraddress);
			Connection connection = null;
			Connection connection1 = null;
			Connection connection2 =  null;
				
			// Now let us figure out the relevenat neighborhoodId for the user

			try
			{
				DatabaseLoader databaseloader1 = new DatabaseLoader(new Properties());
				connection1 = databaseloader1.getConnection();
				Hashtable memberships  = UserManager.getMembershipsForUser( connection1, userId );
				Vector membershipList = UserManager.getMembershipListForUser( connection1, userId );
				Enumeration memberIds = memberships.keys();
				int memberId = -1;
				int nhid = -1;
				
				
				
				if (  memberships.size() == 1 )
				{
					memberId =((Integer) memberIds.nextElement()).intValue();
					nhid =((Member) memberships.get(        new Integer(memberId)   )).getNeighborhoodId();
					hs.setAttribute("nhId", new Integer(nhid));
					hs.setAttribute("memberId", new Integer(memberId));
					hs.setAttribute("nhName", ((Member) memberships.get(        new Integer(memberId)   )).getNeighborhoodName());
					hs.setAttribute("neigborhoods", memberships);
					System.out.println("memberId is " + memberId + " nh id is " + nhid);


					BoardwalkSession bws = new BoardwalkSession(connection1);
					bws.userId = new Integer(userId);
					bws.memberId = new Integer(memberId);
					bws.nhId = new Integer(nhid);
					bws.nhName = ((Member) memberships.get(new Integer(memberId))).getNeighborhoodName();
					bws.selNhid = new Integer(nhid);
					bws.Referer = referer;
					bws.userEmailAddress = useraddress;
					bws.membershipList = membershipList;
					bws.memberIdToMember = memberships;
					hs.setAttribute("bwSession", bws);

					request.setAttribute("bwSession",bws);
					//DefaultHTTPUtilities utilities=new DefaultHTTPUtilities();

					if ( referer != null)
					{
						System.out.println("Sending it back to the referring page: " + referer);
						referer = referer.replaceAll("\\n|\\r","");
//						try {
//							utilities.sendRedirect(referer);
//						} catch (Exception e) {
//							e.printStackTrace();
//						}
						response.sendRedirect(referer);
					}
					else
					{
						response.sendRedirect(request.getContextPath() + "/MyCollaborations");
					}
				}
				else if (  memberships.size() > 1 )
				{
					
					
					
					hs.setAttribute("neigborhoods", memberships);

					// Now let us try to figure out of the nh based on the refereing URL if not then ask the user to choose a nh

					if ( referer != null)
					{
						System.out.println(" referer is " + referer );
						int tableidIndex   = referer.indexOf("tableId=");
						int collabidIndex   = referer.indexOf("collabId=");

						if ( tableidIndex != -1 )
						{
							String tableIdSubString =  referer.substring(tableidIndex, referer.length() );
							int nextparamIndex = tableIdSubString.indexOf("&");
							int tableid = -1;

							if ( nextparamIndex == -1 )
							{
								tableIdSubString = tableIdSubString.substring( new String("tableId=").length() , tableIdSubString.length() ) ;
								tableid = new  Integer(tableIdSubString).intValue();
							}
							else
							{
								tableIdSubString = tableIdSubString.substring( new String("tableId=").length() , nextparamIndex) ;
								tableid = new  Integer(tableIdSubString).intValue();
							}

							System.out.println(" Tableid is " + tableid );
							DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
							connection2 = databaseloader1.getConnection();

							MemberTableAccess mtblAccess = MemberManager.inferNeighborhoodForTable( connection2,  userId, tableid );

							if ( mtblAccess != null )
							{
								// set the nh to recommended neighborhood and redirect to the url
								hs.setAttribute("nhId", new Integer(mtblAccess.getNeighborhoodId())   );
								hs.setAttribute("memberId", new Integer( mtblAccess.getId()));
								hs.setAttribute("nhName", mtblAccess.getNeighborhoodName());
								System.out.println("memberId is " + memberId + " nh id is " + nhid);


								BoardwalkSession bws = new BoardwalkSession(connection1);
								bws.userId = new Integer(userId);
								bws.memberId = new Integer( mtblAccess.getId());
								bws.nhId =new Integer(mtblAccess.getNeighborhoodId());
								bws.nhName = mtblAccess.getNeighborhoodName();
								bws.selNhid = bws.nhId;
								bws.Referer = referer;
								bws.userEmailAddress = useraddress;
								bws.membershipList = membershipList;
								bws.memberIdToMember = memberships;
								hs.setAttribute("bwSession", bws);
								request.setAttribute("bwSession",bws);

								System.out.println("Sending it back to the referring page: " + referer);
								referer = referer.replaceAll("\\n|\\r","");
//								DefaultHTTPUtilities utilities=new DefaultHTTPUtilities();

								response.sendRedirect(referer);
//								try {
//									utilities.sendRedirect(referer);
//								} catch (Exception e) {
//									e.printStackTrace();
//								}

							}
							else
							{
								// set the nh to the first of the many availaible and redirect to the URL
								System.out.println("memberId is " + memberId + " nh id is " + nhid);
								memberId =((Integer) memberIds.nextElement()).intValue();
								nhid =((Member) memberships.get(new Integer(memberId))).getNeighborhoodId();
								hs.setAttribute("nhId", new Integer(nhid));
								hs.setAttribute("memberId", new Integer(memberId));
								hs.setAttribute("nhName", ((Member) memberships.get(        new Integer(memberId)   )).getNeighborhoodName());

								BoardwalkSession bws = new BoardwalkSession(connection1);
								bws.userId = new Integer(userId);
								bws.memberId = new Integer(memberId);
								bws.nhId =new Integer(nhid);
								bws.nhName = ((Member) memberships.get(        new Integer(memberId)   )).getNeighborhoodName();
								bws.selNhid = bws.nhId;
								bws.Referer = referer;
								bws.userEmailAddress = useraddress;
								bws.membershipList = membershipList;
								bws.memberIdToMember = memberships;
								hs.setAttribute("bwSession", bws);
								request.setAttribute("bwSession",bws);
							}
						}
						else
						{
							memberId =((Integer) memberIds.nextElement()).intValue();
							nhid =((Member) memberships.get(new Integer(memberId))).getNeighborhoodId();
							System.out.println("memberId is " + memberId + " nh id is " + nhid);
							hs.setAttribute("nhId", new Integer(nhid));
							hs.setAttribute("memberId", new Integer(memberId));
							hs.setAttribute("nhName", ((Member) memberships.get(        new Integer(memberId)   )).getNeighborhoodName());


							BoardwalkSession bws = new BoardwalkSession(connection1);
							bws.userId = new Integer(userId);
							bws.memberId = new Integer(memberId);
							bws.nhId =new Integer(nhid);
							bws.nhName = ((Member) memberships.get(        new Integer(memberId)   )).getNeighborhoodName();
							bws.selNhid = bws.nhId;
							bws.Referer = referer;
							bws.userEmailAddress = useraddress;
							bws.membershipList = membershipList;
							bws.memberIdToMember = memberships;
							hs.setAttribute("bwSession", bws);
							request.setAttribute("bwSession",bws);

							System.out.println("Sending it back to the referring page: " + referer);
							referer = referer.replaceAll("\\n|\\r","");
//							DefaultHTTPUtilities utilities=new DefaultHTTPUtilities();

							response.sendRedirect(referer);
//							try {
//								utilities.sendRedirect(referer);
//							} catch (Exception e) {
//								e.printStackTrace();
//								}
						}
					}
					else
					{
						sc.getRequestDispatcher("/jsp/admin/chooseNeighborhood.jsp").forward(request, response);
					}
				}
				else if (  memberships.size() == 0 )
				{
					// User isn't a member of any neighborhood ...if the refering URL allows access to public users then we can show it
					// if  not the user has to join a relevant neighborhood
					DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
					connection = databaseloader.getConnection();

				   Vector nhTree = NeighborhoodManager.getNeighborhoodTree(connection, userId);
					request.setAttribute("nhTitle","Join a Neighborhood");
					request.setAttribute("nhTree", nhTree);
					hs.setAttribute("nhId", new Integer(-1));
					hs.setAttribute("memberId", new Integer(-1));
					hs.setAttribute("nhName", "");

					BoardwalkSession bws = new BoardwalkSession(connection1);
					bws.userId = new Integer(userId);
					bws.Referer = referer;
					bws.userEmailAddress = useraddress;
					hs.setAttribute("bwSession", bws);
					request.setAttribute("bwSession",bws);

					sc.getRequestDispatcher("/BW_Neighborhoods").forward(request, response);
				}
			}
			catch( Exception e )
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

					if ( connection2 != null )
						connection2.close();
				}
				catch( SQLException sql )
				{
					sql.printStackTrace();
				}
			}

	  	 }
    }

    public void doPost (HttpServletRequest request,
	    HttpServletResponse response) throws ServletException, IOException
    {
		doGet(  request, response );
    }

    public void init(ServletConfig config) throws ServletException
	{
		BWLogger log = new BWLogger(System.out); //Added by Jeetendra to print the TimeStamp in Tomcat logs on 20170601
		super.init(config);
		if ( ! DatabaseLoader.databaseInit)
		{
			System.out.println("Fetching init params");
			Properties databaseProps  = new Properties();
			databaseProps.setProperty( "databasename" , config.getInitParameter("databasename"));
			databaseProps.setProperty( "InstanceName" , config.getInitParameter("InstanceName"));
			databaseProps.setProperty( "user" ,  config.getInitParameter("user"));
			databaseProps.setProperty( "password" , config.getInitParameter("password"));
			databaseProps.setProperty( "credentialsEncrypted" , config.getInitParameter("credentialsEncrypted")); //Added by Lakshman on 20190118 to fix the Issue Id 15721
			databaseProps.setProperty( "server" , config.getInitParameter("server"));
			databaseProps.setProperty( "port" , config.getInitParameter("port"));
			databaseProps.setProperty( "databasetype" , config.getInitParameter("databasetype"));
			databaseProps.setProperty( "sqlpath" , config.getInitParameter("sqlpath"));
			databaseProps.setProperty( "templatedir" , config.getInitParameter("templatedir"));
			databaseProps.setProperty( "sourcexml", config.getInitParameter("sourcexml"));
			databaseProps.setProperty( "targetxml", config.getInitParameter("targetxml"));
			databaseProps.setProperty( "enableSsl", config.getInitParameter("enableSsl"));
			databaseProps.setProperty( "encrypt", config.getInitParameter("encrypt"));
			databaseProps.setProperty( "trustServerCertificate", config.getInitParameter("trustServerCertificate"));
			databaseProps.setProperty( "hostNameInCertificate", config.getInitParameter("hostNameInCertificate"));

			
			DatabaseLoader dbLoader = new DatabaseLoader(databaseProps,config.getServletContext());
			//DatabaseLoader dbLoader = new DatabaseLoader(new Properties());//sjcd
			
			
			System.out.println("Database test");
			Connection conn = null;
			try
			{
				conn = 	dbLoader.getConnection();				
				if ( conn != null )
				conn.close();
				System.out.println("Database OK");
			}
			catch( SQLException sqe )
			{
				DatabaseLoader.databaseStatus="There is a Database connection problem, either the database is down or the connection parameters are wrong";
				sqe.printStackTrace();
				try
				{
					if ( conn != null )
					conn.close();
				}
				catch( Exception e )
				{
					e.printStackTrace();
				}

			}
		}
	}

	

}
