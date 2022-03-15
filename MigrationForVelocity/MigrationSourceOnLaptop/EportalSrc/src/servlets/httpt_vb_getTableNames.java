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
import com.boardwalk.member.Member;


import java.sql.*;                  // JDBC package
import javax.sql.*;                 // extended JDBC packa

public class httpt_vb_getTableNames extends HttpServlet   implements
 SingleThreadModel {

    int userId;
    String userName;
    String userPassword;

    public void service (HttpServletRequest request,
	    HttpServletResponse response)throws ServletException, IOException
    {
	System.out.println( "came to httpt_vb_getTableNames servlet");
        response.setContentType ( "text/plain");

	ServletOutputStream servletOut = response.getOutputStream ();
	ServletContext sc = getServletContext();

	System.out.println( "Got output stream");
	BufferedReader br = request.getReader ();
	System.out.println( "Got input stream");
        StringBuffer sb = new 	StringBuffer ();
	String  line = new String();
	line = br.readLine ();
	System.out.println( line);

        String wrkstr;
	StringTokenizer st = new StringTokenizer( line );
	System.out.println("Using token " + xlService.Seperator );
	wrkstr = st.nextToken (xlService.Seperator);
	System.out.println("Using token " + xlService.Seperator + " gpt " + wrkstr);
	userName = wrkstr;
	wrkstr = st.nextToken (xlService.Seperator);
	System.out.println("Using token " + xlService.Seperator + " gpt " + wrkstr);
    userPassword = wrkstr;

        if (  userName == null  || userName == ""  || userPassword ==null || userPassword == "" )
        {
            servletOut.println("failure");
        }
        else
        {

            Connection connection = null;
            Connection connection1 = null;;

            try
            {
                DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
                connection = databaseloader.getConnection();
                System.out.println("Authenticating user " + userName + "/" +  userPassword);

                userId = UserManager.authenticateUser(connection, userName,userPassword);
                if ( userId != -1 )
                {





						// Now let us figure out the relevenat neighborhoodId for the user


								DatabaseLoader databaseloader1 = new DatabaseLoader(new Properties());
								connection1 = databaseloader1.getConnection();
								Hashtable memberships  = UserManager.getMembershipsForUser( connection1, userId );
								Enumeration memberIds = memberships.keys();
								int memberId = -1;
								int nhid = -1;
								String nhName = "";

								if (  memberships.size() == 1 )
								{

									memberId =((Integer) memberIds.nextElement()).intValue();
									nhid =((Member) memberships.get(        new Integer(memberId)   )).getNeighborhoodId();
									nhName = ((Member) memberships.get(        new Integer(memberId)   )).getNeighborhoodName();

									servletOut.println("Success:" + userId + xlService.Seperator + memberId + xlService.Seperator + nhid + xlService.Seperator + nhName + xlService.Seperator );
                    				System.out.println("Success: Username  "+userId + " Member Id " + memberId);

								}
								else
								if (  memberships.size() > 1 )
								{
									sc.getRequestDispatcher("/jsp/admin/chooseNeighborhood.jsp").forward(request, response);
								}
								else
								if (  memberships.size() == 0 )
								{
									// User isn't a member of any neighborhood ...if the refering URL allows access to public users then we can show it
									// if  not the user has to join a relevant neighborhood
									sc.getRequestDispatcher("/jsp/admin/joinNeighborhood.jsp").forward(request, response);
								}

                }
                else
                {
                    servletOut.println("failure");

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
	servletOut.close ();
    }
}
