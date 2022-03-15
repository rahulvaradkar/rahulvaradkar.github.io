package servlets;
/*
 *  This presents a list of collaboration available to a user
 */
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import com.boardwalk.table.*;
import com.boardwalk.excel.*;
import com.boardwalk.exception.*;
import com.boardwalk.database.*;
import java.sql.*;                  // JDBC package
import javax.sql.*;                 // extended JDBC packa
import boardwalk.common.*;
import com.boardwalk.member.Member;
import com.boardwalk.user.UserManager;


public class xlNamedSelectionService extends xlService implements SingleThreadModel
{

	int contentDelimPosition = -1;

	String header = null;
	String TableDetails;
	String[] sharedNameInfo;
	int userId = -1;
	int tid = -1;
	public final static String Seperator = new Character((char)1).toString();
	public final static String ContentDelimeter = new Character((char)2).toString();

	public void service(HttpServletRequest request,
		HttpServletResponse response)throws ServletException, IOException
	{


		getElapsedTime();

		// Failure String
		String failureReason = "";
		
		TableDetails = getRequestBuffer(request).toString();

		contentDelimPosition = TableDetails.indexOf(ContentDelimeter);
		header = TableDetails.substring(0, contentDelimPosition);
		
		sharedNameInfo = header.split(Seperator);
		
		String action = "";				//contains SET OR GET
		action	= sharedNameInfo[0];


		StringBuffer responseToUpdate = new StringBuffer();
		String responseBuffer = "";


		if(action.toUpperCase().equals("SET"))
		{
	
			int sharedNameRowId			 = -1;
			int sharedNameColId			 = -1;
			String sharedNamevalue		 = "";
			int NextContentDelimPosition = -1;
			String userComments			 = null;
			String HighlightedRowColIds  = null;
			String[] colRowArrStr		 = null;

			userId = Integer.parseInt(sharedNameInfo[1]);
			sharedNameRowId				 = Integer.parseInt(sharedNameInfo[2]);
			sharedNameColId				 = Integer.parseInt(sharedNameInfo[3]);
			sharedNamevalue				 = sharedNameInfo[4];


			if(BoardwalkUtility.checkIfNullOrBlank(sharedNameInfo[5]))
				userComments = "Named Selection Created"; // Default Value
			else
				userComments				 = sharedNameInfo[5];

			//Getting Row Id, Column id string  between first and second ContentDelimeter
			NextContentDelimPosition	 = TableDetails.indexOf(ContentDelimeter, contentDelimPosition+1);

			HighlightedRowColIds		 = TableDetails.substring(contentDelimPosition+1, NextContentDelimPosition);

			colRowArrStr				= HighlightedRowColIds.split(Seperator);

			responseBuffer				= createNamedSelection(sharedNameRowId, sharedNameColId, sharedNamevalue, userComments, colRowArrStr);

			System.out.println("................responseBuffer -> " + responseBuffer);
	

		}
		else if(action.toUpperCase().equals("GET"))
		{
			int sharedNameRowId = -1;
			int sharedNameColId = -1;
			int importId		= -1;

			userId = Integer.parseInt(sharedNameInfo[1]);
			sharedNameRowId		= Integer.parseInt(sharedNameInfo[2]);
			sharedNameColId		= Integer.parseInt(sharedNameInfo[3]);
			importId			= Integer.parseInt(sharedNameInfo[4]);

			responseBuffer = fetchNamedSelection(sharedNameRowId, sharedNameColId, importId);
			System.out.println("................responseBuffer -> " + responseBuffer);
		}

		commitResponseBuffer(responseBuffer, response);
		System.out.println("Time to prepare response = " + getElapsedTime());

	}
		
	public String createNamedSelection(int sharedRowId, int sharedColId, String sharedNamevalue, String tComment,  String[] colRowArrStr)
	{
		String strReturn = "" ;
		boolean success = false;
		TransactionManager tm = null;
		Connection connection = null;

		try
		{
			ArrayList rowIds	= new ArrayList();
			ArrayList columnIds = new ArrayList();

			double stringValueId = -1;
			DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
	        connection = databaseloader.getConnection();
			tm = new TransactionManager( connection, userId); // txid
            tid = tm.startTransaction("Insert Named Selection", tComment);

			stringValueId = SharedNameSelection.createStringValueId(connection, sharedRowId, sharedColId, sharedNamevalue, tid);

			int selColId = -1;
			int selRowId = -1;
			String colIdStr = null;
			String RowIdStr = null;

			for (int count = 0; count < colRowArrStr.length; count = count + 2)
			{
				int pcOffset = 1;
				RowIdStr = colRowArrStr[count];
				colIdStr = colRowArrStr[count + 1];
				selColId = Integer.parseInt(colIdStr);
				columnIds.add(new Integer(selColId));
				try
				{
					selRowId = Integer.parseInt(RowIdStr);
					rowIds.add(new Integer(selRowId));
				}
				catch (NumberFormatException exp)
				{
					RowIdStr = RowIdStr.substring(0,RowIdStr.length() - 1);
					selRowId = Integer.parseInt(RowIdStr);
					rowIds.add(new Integer(selRowId));
				}
			}


			success = SharedNameSelection.populateTblNamedSelection(connection, stringValueId, rowIds, columnIds);

			tm.commitTransaction();

		}
		catch (Exception exe)
		{
			exe.printStackTrace();
			try
			{
				if(tm != null)
					tm.rollbackTransaction();
			} catch (Exception e1)
			{
				e1.printStackTrace();
			}
		}
		finally
		{
			try
			{
				if(connection != null)
					connection.close();
			}
			catch( SQLException sql2 )
			{
					sql2.printStackTrace();
			}
		}

		if (success)
			strReturn = "Success" + ContentDelimeter + tid + ContentDelimeter ;
		else
			strReturn = "Failure"+ ContentDelimeter;

		return strReturn;
	}

	public String fetchNamedSelection(int sharedNameRowId, int sharedNameColId, int importId)
	{
		String strReturn = "" ;
		strReturn = SharedNameSelection.getNamesSelection(sharedNameRowId, sharedNameColId, importId, userId);
		return strReturn;
	}
}

