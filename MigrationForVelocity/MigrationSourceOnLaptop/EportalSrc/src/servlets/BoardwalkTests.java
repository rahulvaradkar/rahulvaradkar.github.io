package servlets;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.net.URLEncoder;


import com.boardwalk.database.DatabaseLoader;
import com.boardwalk.exception.*;
import com.boardwalk.collaboration.CollaborationManager;
import com.boardwalk.whiteboard.WhiteboardManager;
import com.boardwalk.table.*;
import com.boardwalk.database.*;
import com.boardwalk.user.UserManager;
import com.boardwalk.query.*;
import com.boardwalk.neighborhood.NeighborhoodManager;
import com.boardwalk.util.*;

import java.sql.*;                  // JDBC package
import javax.sql.*;                 // extended JDBC package

public class BoardwalkTests extends BWServlet
{


	HttpServletRequest req;
	HttpServletResponse res;

	ServletContext sc;

	public void doPost(HttpServletRequest request,
	HttpServletResponse response)throws ServletException, IOException
	{

		sc = getServletContext();
		req = request;
		res = response;


		String testName = req.getParameter("testName");

		if (testName.equals("ConcurrentTableUpdate"))
		{
			System.out.println("TESTING BOARDWALK : " + testName);
			ConcurrentTableUpdate();
		}
	}

	// This test creates a thread for every row in the able and then continues to rotate
	// values in the row to eventually get the row back to the original state
	// At the end of this test the table should look the same as it was, with each cell
	// having added n versions, where n is the number of cells in any row
	public  void ConcurrentTableUpdate()
	{
		int tableId = Integer.parseInt(req.getParameter("tableId"));
		String ViewPreference = req.getParameter("ViewPreference");
		String QueryPreference = req.getParameter("QueryPreference");
		int userId;
		int memberId;
		int nhId;

		HttpSession hs = req.getSession(true);

		Integer userIdIntr = (Integer)hs.getAttribute("userId");
		Integer memberIdIntr = (Integer)hs.getAttribute("memberId");
		Integer nhIdIntr = (Integer)hs.getAttribute("nhId");


		if ( userIdIntr != null )
			userId =	userIdIntr.intValue();
		else
			return;

		if ( memberIdIntr != null )
			memberid =	memberIdIntr.intValue();
		else
			return;

		if ( nhIdIntr != null )
					nhId =	nhIdIntr.intValue();
				else
			return;

		Connection connection = null;
		try
		{
			DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
			connection = databaseloader.getConnection();
			TableContents tbcon = TableManager.getTableContents(connection,
												  tableId,
												  userId,
												  memberid,
												  nhId,
												  -1,
												  ViewPreference,
												  QueryPreference,
												  new Vector(),
												  false,
												  -1,
												  10000000,
												  -1,
												  10000000
												  );
			Vector rowids = tbcon.getRowIds();
			Hashtable cellsByRowId = tbcon.getCellsByRowId();
			Vector threads = new Vector();
			for ( int rowIndex=0; rowIndex < rowids.size(); rowIndex++ )
			{

				Integer a_rowIntegerId = (Integer)rowids.elementAt( rowIndex);
				// System.out.println("Displaying row Id " + a_rowIntegerId);
				Vector cells = (Vector) ( (Vector)cellsByRowId.get(a_rowIntegerId)).elementAt(0);
				threads.addElement(new RotateRowThread(a_rowIntegerId.toString(), tableId, cells));
			}

			// wait for all the thread to finish
			for (int rowIndex=0; rowIndex < rowids.size(); rowIndex++ )
			{
				RotateRowThread rrt = (RotateRowThread)threads.elementAt(rowIndex);
				Thread t = rrt.getThread();
				t.join();
			}


			System.out.println("Completed test");
			sc.getRequestDispatcher("/MyTables").forward(req,res);


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

	public void doGet (HttpServletRequest request,
	HttpServletResponse response)throws ServletException, IOException

	{
		doPost(request, response);
	}



}// class