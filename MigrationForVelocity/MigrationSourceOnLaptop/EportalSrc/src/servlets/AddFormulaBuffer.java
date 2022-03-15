package servlets;

/*
 * Sarang 3/08/05
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
import com.boardwalk.user.UserManager;

import java.sql.*;                  // JDBC package
import javax.sql.*;                 // extended JDBC packa


public class AddFormulaBuffer extends HttpServlet
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
	int transactionId;
	int columnCount;
	int rowCount;
	StringBuffer output;

	public AddFormulaBuffer()
	{
	}

    public void service (HttpServletRequest request,
	    					HttpServletResponse response)
	throws ServletException, IOException
    {
		output = new StringBuffer();
		response.setContentType ( "text/plain");
		ServletOutputStream servletOut = response.getOutputStream ();
		BufferedReader br = request.getReader ();

        StringBuffer sb = new 	StringBuffer ();

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
			System.out.println("user is valid");
			Connection connection = null;
			TransactionManager tm = null;
			int tid;

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
				wrkstr = st.nextToken (Seperator);
				m_ViewPreference = wrkstr;
				wrkstr = st.nextToken (Seperator);
				m_SortPreference = wrkstr;
				wrkstr = st.nextToken (Seperator);
				transactionId =Integer.parseInt(wrkstr);
				wrkstr = st.nextToken (Seperator);
				columnCount =Integer.parseInt(wrkstr);
				wrkstr = st.nextToken (Seperator);
				rowCount =Integer.parseInt(wrkstr);

				// the formula
				int start_col_id;
				int end_col_id;
				int start_row_id;
				int end_row_id;
				int ref_col_id;
				int ref_row_id;
				String formula;
				int formula_id;

				wrkstr = st.nextToken (Seperator);
				start_col_id = Integer.parseInt(wrkstr);
				wrkstr = st.nextToken (Seperator);
				end_col_id = Integer.parseInt(wrkstr);
				wrkstr = st.nextToken (Seperator);
				start_row_id = Integer.parseInt(wrkstr);
				wrkstr = st.nextToken (Seperator);
				end_row_id = Integer.parseInt(wrkstr);
				wrkstr = st.nextToken (Seperator);
				ref_col_id = Integer.parseInt(wrkstr);
				wrkstr = st.nextToken (Seperator);
				ref_row_id = Integer.parseInt(wrkstr);
				wrkstr = st.nextToken (Seperator);
				formula = wrkstr;

				formula_id = FormulaManager.addFormula(connection,
													   tableId,
						           					   start_col_id,
						           					   end_col_id,
						           					   start_row_id,
						           					   end_row_id,
						           					   ref_col_id,
						           					   ref_row_id,
						           					   formula,
						           					   tid);
				// commit
				tm.commitTransaction();

				servletOut.println("Success"+Seperator + formula_id);
			}
			catch ( Exception e1 )
			{
				e1.printStackTrace();
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
