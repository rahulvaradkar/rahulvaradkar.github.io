package servlets;

/* *
	This servlet will check if there is any valid significant update after this particular Tx_id.
 */
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import com.boardwalk.util.*;
import com.boardwalk.database.*;
import com.boardwalk.table.*;
import boardwalk.table.*;
import com.boardwalk.exception.*;
import boardwalk.connection.*;
import com.boardwalk.query.*;

import java.sql.*;      // JDBC package
import javax.sql.*;     // extended JDBC packa

public class CheckSignificantUpdate extends xlService implements SingleThreadModel
{
	int userId = -1;
	int memberId = -1;
	int nhId = -1;
	int tableId = -1;
	String view = null;
	int importTid = -1;

	String msReturnVal = "";

	Connection connection = null;
	PreparedStatement preparedstatement = null;
	ResultSet resultset = null;


	private static String CALL_BW_GET_TBL_TXLIST_CRITICAL_UPD = "{CALL BW_GET_TBL_TXLIST_CRITICAL_UPD(?,?,?,?,?,?)}";

	public CheckSignificantUpdate()
	{
		System.out.println(" in servlet");

	}

	public void init(ServletConfig config) throws ServletException
	{
		super.init(config);

		//// Single instance of Connection object
		//try
		//{
		//    DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
		//    connection = databaseloader.getConnection();
		//    if(connection == null)
		//        System.out.println(" connection is null" );
		//}
		//catch (SQLException sql)
		//{
		//    sql.printStackTrace();
		//}
		//catch (Exception e)
		//{
		//    e.printStackTrace();
		//}
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)   throws ServletException, IOException
	{

		String fullTable = getRequestBuffer(request).toString();
		//System.out.println(fullTable);


		// parse the buffer
		String sub = null;
		int i = 0;
		int j = fullTable.indexOf(ContentDelimeter);
		int jcount = 0;

		try
		{
			while (j >= 0)
			{
				sub = fullTable.substring(i, j);

				if (jcount == 0) // header
				{
					System.out.println("header = " + sub);
					String[] headerInfo = sub.split(Seperator);
					userId = Integer.parseInt(headerInfo[0]);
					String userName = headerInfo[1];
					String userPassword = headerInfo[2];
					memberId = Integer.parseInt(headerInfo[3]);
					nhId = Integer.parseInt(headerInfo[4]);
					tableId = Integer.parseInt(headerInfo[5]);
					view = headerInfo[6];
					//					numColumns = Integer.parseInt(headerInfo[7]);
					//					numRows = Integer.parseInt (headerInfo[8]);
					importTid = Integer.parseInt(headerInfo[9]);
				}
				i = j + 1;
				j = fullTable.indexOf(ContentDelimeter, i);
				jcount++;
			}

			/*			System.out.println(" In servlet for Significant update userId "+ userId);
						System.out.println(" In servlet for Significant update memberId "+ memberId);
						System.out.println(" In servlet for Significant update nhId "+ nhId);
						System.out.println(" In servlet for Significant update tableId "+ tableId);
						System.out.println(" In servlet for Significant update view "+ view);
						System.out.println(" In servlet for Significant update importTid "+ importTid); */
			DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
			connection = databaseloader.getConnection();

			// check if there are any critical updates since last import
			int critTid = -1;
			String query = " SELECT MAX(BW_SIGNIFICANT_TXS.TX_ID)" +
						   " FROM BW_SIGNIFICANT_TXS " +
						   " WHERE " +
						   " BW_TBL_ID = ? " +
						   " GROUP BY  BW_TBL_ID ";
			preparedstatement = connection.prepareStatement(query);
			preparedstatement.setInt(1, tableId);
			resultset = preparedstatement.executeQuery();
			String lsResponseStr = null;
			
			if (resultset.next())
			{
				critTid = resultset.getInt(1);
			}
			preparedstatement.close();
			preparedstatement = null;
			if (resultset != null)
			{
				resultset.close();
				resultset = null;
			}

			if (importTid < critTid)
			{
				System.out.println("Found Critical updates after the last import");
				lsResponseStr = getSignificantUpdateIds(connection, tableId, importTid, userId, nhId, view, memberId);
			}
			else
			{
				System.out.println("No Critical updates after the last import");
				lsResponseStr = " ";
			}
			
			commitResponseBuffer(lsResponseStr, response);
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
				if (connection != null)
				{
					connection.close();
					connection = null;
				}
			}
			catch (SQLException sql)
			{
				sql.printStackTrace();
			}
		}
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)  throws ServletException, IOException
	{
		doPost(request, response);
	}

	public String getSignificantUpdateIds(Connection connection, int aiTblId, int aiTransId, int aiUserId, int aiNhid, String asViewPref, int aiMemberId)throws SQLException
	{
		int liRetTxId = aiTransId;
		StringBuffer lsbReturn = new StringBuffer();
		String lsReturnValue = " ";

		try
		{
			if (connection == null)
				System.out.println(" connection is closed " + connection);

			int criteriaTableId = TableViewManager.getCriteriaTable(connection, aiTblId, userId);
			System.out.println("Using criterea table id = " + criteriaTableId);
			if (criteriaTableId == -1)
			{
				preparedstatement = connection.prepareStatement(CALL_BW_GET_TBL_TXLIST_CRITICAL_UPD);

				preparedstatement.setInt(1, aiTblId);		// Table Id
				preparedstatement.setInt(2, aiTransId);		// Start Id or Import Transaction Id.
				preparedstatement.setInt(3, aiUserId);		// User id
				preparedstatement.setInt(4, aiNhid);			// NHid
				preparedstatement.setString(5, asViewPref);	// View Prefrence  // Here it will be My Rows by default
				preparedstatement.setInt(6, aiMemberId);		// View Prefrence  // Here it will be My Rows by default
			}
			else
			{
				String lsRowQuery = TableViewManager.getRowQuery(connection, aiTblId, aiUserId, criteriaTableId, false, asViewPref);
				String lsSql = QueryMaker.getFiltredCriticalUpdate(lsRowQuery);

				preparedstatement = connection.prepareStatement(lsSql);

				preparedstatement.setInt(1, aiTransId);
				preparedstatement.setInt(2, aiUserId);

				preparedstatement.setInt(3, aiTransId);
				preparedstatement.setInt(4, aiUserId);

				preparedstatement.setInt(5, aiTransId);
				preparedstatement.setInt(6, aiUserId);

				//				@TABLE_ID, @USER_ID, @MEMBER_ID
				preparedstatement.setInt(7, aiTblId);
				preparedstatement.setInt(8, aiUserId);
				preparedstatement.setInt(9, aiMemberId);
				preparedstatement.setInt(10, aiTransId);

			}

			resultset = preparedstatement.executeQuery();

			int liTransId = 0;
			String lsCreatedOn = "";
			String lsCreatedBy = "";
			String lsComment = "";
			String lsAction = "";

			while (resultset.next())
			{
				liTransId = resultset.getInt(1);
				lsCreatedOn = resultset.getString(2);
				lsCreatedBy = resultset.getString(3);
				lsComment = resultset.getString(4);
				lsAction = resultset.getString(5);

				lsbReturn.append("Action :");
				lsbReturn.append(lsAction);
				lsbReturn.append(" Updated By : ");
				lsbReturn.append(lsCreatedBy);
				lsbReturn.append(" Updated On : ");
				lsbReturn.append(lsCreatedOn);
				if (lsComment != null && !lsComment.equals(""))
				{
					lsbReturn.append(" Comments ");
					lsbReturn.append(lsComment);
				}
				else
					lsbReturn.append(" "); // We apped a Space here to get proper tokens later on

				lsbReturn.append("\n");
			}

			//			System.out.println("lsbReturn Values String  >>>>>>>>>>> "+lsbReturn.toString());


			if (lsbReturn.toString().length() > 0)
				lsReturnValue = " There are following Significant updates for this Table \n \n " + lsbReturn.toString();

		}
		catch (SQLException sqlexception)
		{
			sqlexception.printStackTrace();
			throw sqlexception;
		}
		catch (SystemException sysexcept)
		{
			sysexcept.printStackTrace();
		}
		finally
		{
			try
			{
				if (resultset != null)
				{
					resultset.close();
					resultset = null;
				}
				if (preparedstatement != null)
				{
					preparedstatement.close();
					preparedstatement = null;
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

		}

		return lsReturnValue;
	}
}

