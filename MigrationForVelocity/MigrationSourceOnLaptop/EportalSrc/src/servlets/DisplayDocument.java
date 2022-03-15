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
import java.sql.*;                  // JDBC package
import javax.sql.*;                 // extended JDBC packa
import boardwalk.common.*;
import boardwalk.table.*;
import com.boardwalk.table.*;

public class DisplayDocument extends BWServlet implements SingleThreadModel
{

	ServletContext sc;
    HttpServletRequest req;
    HttpServletResponse res;

    public void doPost (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
		sc = getServletContext();
		req = request;
		res = response;

		int blobId = Integer.parseInt(req.getParameter("id"));

		Connection connection = null;
		try
		{
			DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
			connection = databaseloader.getConnection();
			BlobManager.getDocument(connection, blobId, res);
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

}

