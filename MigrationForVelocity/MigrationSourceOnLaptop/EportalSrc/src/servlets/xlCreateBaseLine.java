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
import com.boardwalk.collaboration.*;

public class xlCreateBaseLine extends xlService implements SingleThreadModel
{
	public void service(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException
	{
		Connection connection		= null;
		Connection connectionQuery	= null;
		String responseBuffer		= "FAILURE";

		response.setContentType("text/plain");
		ServletOutputStream servletOut = response.getOutputStream();

		int liCollabId			= -1;
		int liUserId			= -1;
		String lsBaseLineName	= "";
		String lsBaseLineDesc	= "";
		TransactionManager tm	= null;
		 int tid				= -1;
	
		try
		{
			System.out.println(" Create Baseline from XL ");
			// parse the request

			// read the request
			BufferedReader br	= request.getReader();
			StringBuffer sb		= new StringBuffer();
			String line			= new String();
			line = br.readLine();

			while (line != null)
			{
				sb.append(line);
				line = br.readLine();
				if (line != null)
				{
					sb.append("\n");
				}
			}

			String buf = sb.toString();
			// parse the information
			String[] headerInfo		= buf.split(Seperator);
			liCollabId				= Integer.parseInt(headerInfo[0]);

			System.out.println("Collaboration ID = " + liCollabId);

			lsBaseLineName			= headerInfo[1];
			System.out.println("Baseline Name = " + lsBaseLineName);

			lsBaseLineDesc			= headerInfo[2];
			System.out.println("lsBaseLineName Purpose = " + lsBaseLineDesc);

			liUserId				= Integer.parseInt(headerInfo[3]);
			System.out.println(" User Id is  = " + liUserId);

			// Start a connection
			DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
			connection = databaseloader.getConnection();

            tm = new TransactionManager( connection,liUserId);
            tid = tm.startTransaction();
            int m_baseline_id =CollaborationManager.createBaseline(
            connection,
            lsBaseLineName,
            lsBaseLineDesc,
            liCollabId,
            tid
            );
            tm.commitTransaction();

			if (m_baseline_id > 0)
			{
				responseBuffer = "SUCESS";
				System.out.println("Created Baseline named "+ lsBaseLineName);
			}
			else
				System.out.println("Baseline Creation failed");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			responseBuffer = e.toString();
		}
		finally
		{
			try
			{
				if (connection != null)
					connection.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			response.setContentLength(responseBuffer.length());
			servletOut.println(responseBuffer);
			servletOut.close();

		}
	}

}

