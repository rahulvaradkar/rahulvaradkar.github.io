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
import com.boardwalk.user.UserManager;

import java.sql.*;                  // JDBC package
import javax.sql.*;                 // extended JDBC packa


public class GetFormulaBuffer extends HttpServlet
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

	public GetFormulaBuffer()
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

			try
			{
				DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
				connection = databaseloader.getConnection();


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

				// get the cell formulae
				Vector fs = FormulaManager.getFormulae(connection, tableId);

				// add to buffer
				output = output.append(Integer.toString(fs.size()));
				output = output.append(Seperator);

				Iterator ci = fs.iterator();
				while(ci.hasNext())
				{
					RangeFormula cf = (RangeFormula)ci.next();
					int fid = cf.getId();
					int scid = cf.getStartColumnId();
					int ecid = cf.getEndColumnId();
					int srid = cf.getStartRowId();
					int erid = cf.getEndRowId();
					int rcid = cf.getRefColumnId();
					int rrid = cf.getRefRowId();
					String fr = cf.getFormula();
					output = output.append(Integer.toString(fid));
					output = output.append(ContentDelimeter);
					output = output.append(Integer.toString(scid));
					output = output.append(ContentDelimeter);
					output = output.append(Integer.toString(ecid));
					output = output.append(ContentDelimeter);
					output = output.append(Integer.toString(srid));
					output = output.append(ContentDelimeter);
					output = output.append(Integer.toString(erid));
					output = output.append(ContentDelimeter);
					output = output.append(Integer.toString(rcid));
					output = output.append(ContentDelimeter);
					output = output.append(Integer.toString(rrid));
					output = output.append(ContentDelimeter);
					output = output.append(fr);
					output = output.append(Seperator);
					System.out.println("GetFormulaBuffer: " + output.toString());
				}


				String resp = output.toString();
				System.out.println("response="+ resp);
				response.setContentLength ( resp.length() );
				System.out.println("response content length ="+  resp.length());
				servletOut.print(resp);
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
