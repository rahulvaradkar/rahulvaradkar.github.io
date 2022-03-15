package servlets;

/*
 * Sarang 2/18/05
 * Assign rows from Excel
 *
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
import com.boardwalk.excel.*;
import com.boardwalk.user.UserManager;
import com.boardwalk.exception.*;

import java.util.Date;
import java.sql.*;                  // JDBC package
import javax.sql.*;                 // extended JDBC packa


public class RowAssignBuffer extends HttpServlet
implements SingleThreadModel
{

	public final static String Seperator = new Character((char)1).toString();
	public final static String ContentDelimeter = new Character((char)2).toString();
	StringTokenizer st;
	int userId;
	String userName;
	String userPassword;
	int nhId;
	int  memberId;
	String nhName;
	int tid;
	String m_ViewPreference;
	String m_SortPreference;
	int rowCount;
	int columnCount;
	int transactionId;
	xlError xle;

    public void service (HttpServletRequest request,
	    					HttpServletResponse response)
	throws ServletException, IOException
    {
		response.setContentType ( "text/plain");
		ServletOutputStream servletOut = response.getOutputStream ();
		BufferedReader br = request.getReader ();
		xle = null;
        StringBuffer sb = new 	StringBuffer ();
        BoardwalkMessages bwMsgs = new BoardwalkMessages();

        String  line = new String();
        line = br.readLine ();
	    while( line != null )
	    {
			sb.append(line);
			line = br.readLine ();
			if ( line != null )
			{
				sb.append("\n");
			}
		}
		String buf = sb.toString();
		st = new StringTokenizer( buf );

        if ( loginUser() )
        {
            String responseToUpdate;
			System.out.println("user is valid");

			TransactionManager tm = null;
			Connection connection = null;

			try
			{
				DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
				connection = databaseloader.getConnection();
				tm = new TransactionManager(connection, userId);
				tid = tm.startTransaction();


				String wrkstr;

				int tableId;
				wrkstr = st.nextToken (Seperator);
				tableId = Integer.parseInt(wrkstr);
				System.out.println("tableId = " + tableId);
/*
				// cannot assign rows while the table is locked
				TableLockInfo tblock = TableManager.isTableLocked( connection,  tableId );
				if ( tblock.isLocked()  && tblock.getLockedByUserId() != userId )
				{
					String m_failureReason =  "Failure" + Seperator + " The table is locked by  " + tblock.getLockedByuser()  + " since " +  tblock.getLockTime();
					tm.rollbackTransaction();
					response.setContentLength ( m_failureReason.length() );
					servletOut.print(m_failureReason);
					return;
				}


*/				// read the rest of the junk
				wrkstr = st.nextToken (Seperator);
				m_ViewPreference = wrkstr;
				//System.out.println("m_ViewPreference = " + m_ViewPreference);
				wrkstr = st.nextToken (Seperator);
				m_SortPreference = wrkstr;
				//System.out.println("m_SortPreference = " + m_SortPreference);
				wrkstr = st.nextToken (Seperator);
				transactionId =Integer.parseInt(wrkstr);
				//System.out.println("tableId = " + tableId);
				wrkstr = st.nextToken (Seperator);
				columnCount =Integer.parseInt(wrkstr);
				//System.out.println("tableId = " + tableId);
				wrkstr = st.nextToken (Seperator);
				rowCount =Integer.parseInt(wrkstr);
				//System.out.println("tableId = " + tableId);
				wrkstr = st.nextToken (Seperator);
				int baselineid = Integer.parseInt(wrkstr);

				// assign rows while parsing the buffer
				int startRowId = -1;
				int endRowId = -1;
				String username = "";
				while (wrkstr != null)
				{
					try
					{
						wrkstr = st.nextToken (Seperator);
						startRowId = Integer.parseInt(wrkstr);
						wrkstr = st.nextToken (Seperator);
						endRowId = Integer.parseInt(wrkstr);
						username = st.nextToken (Seperator);


						System.out.println("Assigning Rows ::" + startRowId +"-"+ endRowId
									+ " of table::" + tableId
									+ " to " + username);

						int result = RowManager.assignRows(connection, startRowId,
																endRowId, username, tid);
					}
					catch ( NoSuchElementException e )
					{
						wrkstr = null;
					}
					catch (Exception e)
					{
						BoardwalkMessage bwMsg = bwMsgs.getBoardwalkMessage(12015 );
						xle = new xlError( tableId,startRowId,-1,-1,-1,  12015, "TABLE UPDATE EXCEPTION", "The user "+username+" is not valid");
						throw new BoardwalkException(12015);
					}
				}

				tm.commitTransaction();

				responseToUpdate = "Success";
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
				connection.close();
				responseToUpdate = "Failure";
				if (xle != null)
				{
					responseToUpdate = responseToUpdate + Seperator + xle.buildTokenString();
				}
				response.setContentLength ( responseToUpdate.length() );
				servletOut.print(responseToUpdate);
				return;
			  }
			  catch ( SQLException sql )
			  {
				sql.printStackTrace();
			  }
			}
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
		wrkstr = st.nextToken (Seperator);
		userId = Integer.parseInt(wrkstr);

		wrkstr = st.nextToken (Seperator);
		userName = wrkstr;

		wrkstr = st.nextToken (Seperator);
		userPassword = wrkstr;

		wrkstr = st.nextToken (Seperator);
		memberId = Integer.parseInt(wrkstr);

		wrkstr = st.nextToken (Seperator);
		nhId = Integer.parseInt(wrkstr);

		wrkstr = st.nextToken (Seperator);
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

				int db_userId = UserManager.authenticateUser(connection, userName,userPassword);

				if ( userId != -1 && userId == db_userId )
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
			// System.out.println("End loginUser : " + getElapsedTime());
		}
	}
}
