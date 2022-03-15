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
import com.boardwalk.whiteboard.WhiteboardManager;
import com.boardwalk.database.*;
import com.boardwalk.table.*;
import com.boardwalk.user.UserManager;

import java.util.Date;
import java.sql.*;                  // JDBC package
import javax.sql.*;                 // extended JDBC packa

public class httpt_vb_deleteRow extends HttpServlet   implements
 SingleThreadModel  {

	public final static String Seperator = new Character((char)1).toString();
	public final static String ContentDelimeter = new Character((char)2).toString();

	Connection connection;
	StringTokenizer st;
	String m_inputbuffer;
	int userId;
	String userName;
	String userPassword;
	int nhId;
	int  memberId;
	String nhName;
	String m_ViewPreference;
    String m_SortPreference;
    int transactionId;


    public void service (HttpServletRequest request,
	    HttpServletResponse response)throws ServletException, IOException
    {
		int tid;

		System.out.println( "came to httpt_vb_deleteRow");

		response.setContentType ( "text/plain");

		ServletOutputStream servletOut = response.getOutputStream ();

		System.out.println( "Got output stream");
		BufferedReader br = request.getReader ();


        System.out.println( "Got input stream");


        StringBuffer sb = new 	StringBuffer ();

        String  line = new String();
        line = br.readLine ();
	    while( line != null )
	    {
			System.out.println("appending line:::::: " + line);
			sb.append(line);
			line = br.readLine ();
			if ( line != null )
			{
				sb.append("\n");
			}
		}
		m_inputbuffer = sb.toString();

        System.out.println("Deleting Row::::::: " + m_inputbuffer);

        if ( loginUser() )
        {
            String responseToUpdate;
            System.out.println("user is valid");
////////////////////////////////////////////////

            TransactionManager tm = null;

			try
			{
				Date d = new Date();
				DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
				connection = databaseloader.getConnection();
				tm = new TransactionManager(connection, userId);
				tid = tm.startTransaction();


				String wrkstr;

				int rowId, tableId;
				wrkstr = st.nextToken (xlService.Seperator);
				tableId = Integer.parseInt(wrkstr);
				wrkstr = st.nextToken (xlService.Seperator);
				m_ViewPreference = wrkstr;

				//		System.out.println("m_ViewPreference  " + m_ViewPreference )


				wrkstr = st.nextToken (xlService.Seperator);
				m_SortPreference = wrkstr;
				//		System.out.println("m_SortPreference  " + m_SortPreference );

				wrkstr = st.nextToken (xlService.Seperator);
				System.out.println("transactionID wrkstr = " + wrkstr);
				transactionId =Integer.parseInt(wrkstr);

				wrkstr = st.nextToken (xlService.Seperator);
				System.out.println("column count wrkstr = " + wrkstr);

				wrkstr = st.nextToken (xlService.Seperator);
				System.out.println("row count wrkstr = " + wrkstr);


				  TableLockInfo tblock = TableManager.isTableLocked( connection,  tableId );
				if ( tblock.isLocked()  && tblock.getLockedByUserId() != userId )
				{
					String m_failureReason =  "Failure" + xlService.Seperator + " The table is locked by  " + tblock.getLockedByuser()  + " since " +  tblock.getLockTime();
					tm.rollbackTransaction();
					response.setContentLength ( m_failureReason.length() );
					servletOut.print(m_failureReason);
					return;

				}


				while (wrkstr != null) {
					try {
							wrkstr = st.nextToken (xlService.Seperator);
							rowId = Integer.parseInt(wrkstr);

							System.out.println("Deleting Row ::" + rowId + " of table::" + tableId);

							RowManager.deactivateRow(connection, rowId, tid);
						} catch ( NoSuchElementException e ) {
									wrkstr = null;
                	}
				}
           		tm.commitTransaction();

				Date d4 = new Date();
				responseToUpdate = "Success";
				responseToUpdate = responseToUpdate + Seperator + "XID" + ContentDelimeter;
				responseToUpdate = responseToUpdate + tid + ContentDelimeter;


				System.out.println("UpdateBoardwalkResponse: " + responseToUpdate );
				response.setContentLength ( responseToUpdate.length() );
				servletOut.print(responseToUpdate);

			}
			catch ( Exception e1 )
			{
				try
				{
					tm.rollbackTransaction();
				} catch (Exception e){
					e.printStackTrace();
				}
				e1.printStackTrace();
			}
			finally
			{
			  try
			  {
				 System.out.println("Inside fiannnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnly");
				connection.close();
				responseToUpdate = "Failure";
				response.setContentLength ( responseToUpdate.length() );
				servletOut.print(responseToUpdate);
				return;
			  }
			  catch ( SQLException sql )
			  {
				sql.printStackTrace();
			  }
			}
////////////////////////////////////////////

		}
        else
        {
            System.out.println("user is invalid");
            String invalid = new String("userinvalid");
            response.setContentLength (invalid.length());
            servletOut.print(invalid);
        }

        servletOut.close ();

    }


    public boolean  loginUser ()
	{


		String wrkstr;

		st = new StringTokenizer( m_inputbuffer );
		wrkstr = st.nextToken (xlService.Seperator);
		userId = Integer.parseInt(wrkstr);
		wrkstr = st.nextToken (xlService.Seperator);
		userName = wrkstr;
		wrkstr = st.nextToken (xlService.Seperator);
		userPassword = wrkstr;
		wrkstr = st.nextToken (xlService.Seperator);
		memberId =  Integer.parseInt(wrkstr);
		wrkstr = st.nextToken (xlService.Seperator);
		nhId = Integer.parseInt(wrkstr);
		wrkstr = st.nextToken (xlService.Seperator);
		nhName =wrkstr;



		Connection connection = null;
		try
		{
			DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
			connection = databaseloader.getConnection();

			if (  userName == null  || userName == ""  || userPassword ==null || userPassword == "" )
			{
				return false;
			}
			else
			{

				userId = UserManager.authenticateUser(connection, userName,userPassword);
				if ( userId != -1 )
				{
					return true;
				}
				else
				{
					return false;
				}

			}
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


}
