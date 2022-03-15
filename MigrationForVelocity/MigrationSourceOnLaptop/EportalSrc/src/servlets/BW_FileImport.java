package servlets;
/*
 *  This presents a list of collaboration available to a user
 */
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;


import com.boardwalk.exception.*;
import com.boardwalk.database.*;
import boardwalk.common.*;
import boardwalk.table.*;
import com.boardwalk.table.*;

import java.sql.*;                  // JDBC package
import javax.sql.*;                 // extended JDBC packa

public class BW_FileImport extends BWServlet
{

	ServletContext sc;
    HttpServletRequest req;
    HttpServletResponse res;

    public void doPost (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
		sc = getServletContext();
		req = request;
		res = response;

		int id =Integer.parseInt(req.getParameter("id"));
		String FileType=req.getParameter("FileType");
		
		Connection connection = null;
		try
		{
			DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
			connection = databaseloader.getConnection();
			getLatestDocument(connection,id,FileType, res);
		}
		catch (SQLException sql)
		{
			sql.printStackTrace();
		   return;
		}
		finally
		{
		  try
		  {
			if ( connection != null )
				connection.close();
		  }
		  catch ( SQLException sql )
		  {
			sql.printStackTrace();
		  }
		}

	}

	public void doGet (HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException
    {
		doPost(request, response);
    }

	public static void getLatestDocument(
							Connection connection,
							int id,
							String FileType,
							javax.servlet.http.HttpServletResponse res
							 )
    throws SQLException
    {
		ResultSet resultset = null;
		PreparedStatement statement = null;

		try
		{
			if (id==-1)
			{
				statement = connection.prepareStatement("SELECT IMPORT_SPECIFICATION.FNAME, IMPORT_SPECIFICATION.EXT, IMPORT_SPECIFICATION.TYPE, IMPORT_SPECIFICATION.CLIENT, IMPORT_SPECIFICATION.DOC FROM IMPORT_SPECIFICATION where id = (SELECT MAX(ID) FROM IMPORT_SPECIFICATION where FileType='" + FileType + "' And IsActive=1)");
			}
			else
			{
				statement = connection.prepareStatement("SELECT IMPORT_SPECIFICATION.FNAME, IMPORT_SPECIFICATION.EXT, IMPORT_SPECIFICATION.TYPE, IMPORT_SPECIFICATION.CLIENT, IMPORT_SPECIFICATION.DOC FROM IMPORT_SPECIFICATION where id =" +id);
			}
			resultset = statement.executeQuery();
			
			if (resultset.next())
			{

				String fileName = resultset.getString("FNAME");
				String extension = resultset.getString("EXT");
				String type = resultset.getString("TYPE");
				String client = resultset.getString("CLIENT");
				BufferedInputStream in = new BufferedInputStream(resultset.getBinaryStream("DOC"));

				res.setContentType(type);
				File f = new File(fileName);
				System.out.println("filename = " + f.getName());
				res.setHeader("Content-Disposition", "filename=" + f.getName());
				//res.setContentLength( buffer.length());
				javax.servlet.ServletOutputStream out = res.getOutputStream();
				int b;
				byte[] buffer = new byte[10240]; // 10kb buffer
				while ((b = in.read(buffer, 0, 10240)) != -1)
				{
					out.write(buffer, 0, b);
				}
			}
			else
			{
				res.sendError(res.SC_NOT_FOUND);
			}
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
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
}

