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
import com.boardwalk.member.Member;
import com.boardwalk.user.UserManager;

public class xlImportChangesService extends HttpServlet implements SingleThreadModel
{

	public final static String Seperator = new Character((char)1).toString();
	public final static String ContentDelimeter = new Character((char)2).toString();

	public void service(HttpServletRequest request,
		HttpServletResponse response)throws ServletException, IOException
	{
		xlServiceLogic logic = new xlServiceLogic(this);
		
		String responseBuffer = "Failure";
		String failureReason = "";
		Vector xlErrorCells = new Vector();
		logic.getElapsedTime();
		String buff = logic.getRequestBuffer(request).toString();
		//System.out.println(buff);
		System.out.println("Time to read the buffer = " + logic.getElapsedTime());

		// Get the database connection
		int tableId = -1;
		TransactionManager tm = null;
		Connection connection = null;
		PreparedStatement stmt = null;
		try
		{
			String[] buffArray = buff.split(ContentDelimeter);

			//Modified by Tekvision on 20180207 for Clear Text Password(Issue Id: 14241) - START
			String[] requestInfo = buffArray[0].split(Seperator);
			int userId = Integer.parseInt(requestInfo[0]);
			String userName = requestInfo[1];
			//String userPassword = requestInfo[2];
			int memberId = Integer.parseInt(requestInfo[2]);
			int nhId = Integer.parseInt(requestInfo[3]);
			tableId = Integer.parseInt(requestInfo[4]);
			int baselineId = Integer.parseInt(requestInfo[5]);
			String view = requestInfo[6];
			int importTid = Integer.parseInt(requestInfo[7]);
			int exportTid = Integer.parseInt(requestInfo[8]);
			int mode = Integer.parseInt(requestInfo[9]);
			int synch = Integer.parseInt(requestInfo[10]);
			//Modified by Tekvision on 20180207 for Clear Text Password(Issue Id: 14241) - END

			HashMap localRowHash = new HashMap();
			if (buffArray.length > 1)
			{
				String[] rowInfo = buffArray[1].split(Seperator);

				for (int ri = 0; ri < rowInfo.length; ri++)
				{
					String rowIdStr = rowInfo[ri];
					if (!rowIdStr.equalsIgnoreCase(""))
					{
						int rowId = -1;
						rowId = Integer.parseInt(rowIdStr);
						localRowHash.put(new Integer(rowId), new Integer(rowId));
					}
				}
			}
			System.out.println("Time to create localRowHash from buffer = " + logic.getElapsedTime());
			int maxTransactionId = importTid;

			// Start a connection
			DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
			connection = databaseloader.getConnection();

			//Added by Lakshman on 20171121 to fix an issue with Id: 14024 - START
			// see if there is a criterea table associated with this table
			int criteriaTableId = TableViewManager.getCriteriaTable(connection, tableId, userId);
			System.out.println("Using criterea table id = " + criteriaTableId);

			//String lsRowQuery = TableViewManager.getRowQuery(connection, tableId, userId, criteriaTableId, true, view, "TABLE");
			
			// try the old sheet check
			if (synch == 0)
			{
				/*
				if (view == null || view.trim().equals("") || view.equalsIgnoreCase("Latest"))
				{
					oldSheetCheck(connection, tableId, memberId, userId, exportTid, view);
				}
				else
				{
					oldSheetCheck(connection, tableId, memberId, userId, exportTid, lsRowQuery);
				}
				*/
				logic.oldSheetCheck(connection, tableId, memberId, userId, criteriaTableId, exportTid, view); //Moved back inside the IF condition by Lakshman on 20190826 to fix the Issue Id: 17332
				System.out.println("Time to perform old sheet check = " + logic.getElapsedTime());
			}

			//Added by Lakshman on 20171121 to fix an issue with Id: 14024 - END
			// authenticate the user
			Member memberObj = UserManager.authenticateMember(connection, userName, memberId); //Modified by Tekvision on 20180207 for Clear Text Password(Issue Id: 14241)
			if (memberObj == null)
			{
				//System.out.println("Authentication failed for user : " + userName);
				responseBuffer = "Failure";//TBD : Description of the Error
				xlErrorCells.add( new xlErrorNew( tableId, 0, 0, 11005));
				throw new BoardwalkException(11005);
			}
			else
			{
				//System.out.println("Authentication succeeded for user : " + userName);
				nhId = memberObj.getNeighborhoodId();
			}
			System.out.println("Time to authenticate user = " + logic.getElapsedTime());

			// Check access control :: TBD
			TableInfo tinfo = TableManager.getTableInfo(connection, userId, tableId);
			TableAccessList ftal = TableViewManager.getSuggestedAccess(connection, tinfo, userId, memberId, nhId);
			if (view == null || view.trim().equals(""))
			{
				view = ftal.getSuggestedViewPreferenceBasedOnAccess();
				System.out.println("Suggested view pref = " + view);
				if (view == null)
				{
					xlErrorCells.add(new xlErrorNew(tableId, 0, 0, 10005));
					throw new BoardwalkException(10005);
				}
			}
			// Check access control :: TBD
			int raccess = 1;
			int ACLFromDB = ftal.getACL();
			TableAccessRequest wAccess = new TableAccessRequest(tableId, view, true);
			int wACL = wAccess.getACL();
			int awACL = wACL & ACLFromDB;
			if (awACL == wACL)
			{
				raccess = 2;
				System.out.println("Rows have write access");
			}
			else
			{
				System.out.println("Rows are readonly");
			}

			//Moved to the top to fix the issue with Id: 14024
			// see if there is a criterea table associated with this table
			//int criteriaTableId = TableViewManager.getCriteriaTable(connection, tableId, userId);
			//System.out.println("Using criterea table id = " + criteriaTableId);
			
			//Added to fix Refresh issue when the Column Access is applied from Template side 2016/11/16
			int accessTableId = TableViewManager.getAccessTable(connection, tableId, userId);
			HashSet restrColumnList = new HashSet();
			if (accessTableId > 0)
			{
				System.out.println("Using access table id = " + accessTableId);
				// read the access for the user
				restrColumnList = TableViewManager.getRestrictedColumnsForImport(connection, tableId, accessTableId, userId);
			}
			
			StringBuffer resData = new StringBuffer(10000000);
			System.out.println("Time(sec) to check access = " + logic.getElapsedTime());
			// Get the columns
			Vector colv = ColumnManager.getXlColumnsForImport(connection, tableId, userId, memberId);
			System.out.println("Time(sec) to execute column query for = " + logic.getElapsedTime());
			HashMap colHash = new HashMap();
			//ColObjsByColId = new HashMap();
			Iterator ci = colv.iterator();
			//while (ci.hasNext())
			//{
			//    xlColumn_import coli = (xlColumn_import)ci.next();
			//    ColObjsByColId.put(new Integer(coli.getId()), coli);
			//}
			// columns
			System.out.println("Fetched columns : " + colv.size() + " time taken = " + logic.getElapsedTime());

			for (int c = 0; c < colv.size(); c++)
			{
				xlColumn_import col = (xlColumn_import)colv.elementAt(c);

				//Added to fix Refresh issue when the Column Access is applied from Template side 2016/11/16
				if (restrColumnList != null && restrColumnList.size() > 0)
				{
					if (restrColumnList.contains(new Integer(col.getId())))
					{
						System.out.println("Skip restricted column " + col.getName());
						continue;
					}
				}
				
				if (maxTransactionId < col.getCreationTid())
				{
					maxTransactionId = col.getCreationTid();
				}

				if (maxTransactionId < col.getAccessTid())
				{
					maxTransactionId = col.getAccessTid();
				}
				resData.append(col.getId() + Seperator);
				resData.append(col.getName() + Seperator);
				// Mark New Columns, or Deleted Columns
				if (col.getCreationTid() > importTid)
				{
					resData.append("N" + Seperator);
				}
				else if (col.getAccessTid() > importTid && col.getAccess() > 0)
				{
					resData.append("N" + Seperator);
				}
				else
				{
					resData.append(Seperator);
				}
				//System.out.println("putting in hash column = " + col.getId());
				colHash.put(new Integer(col.getId()), col);
			}
			resData.replace(resData.length() - 1,resData.length(), ContentDelimeter);
			System.out.println("Time(sec) to fetch columns = " + logic.getElapsedTime());
			//System.out.println(resData.toString());
			// Get the rows
			boolean viewIsDynamic = false;
			if (view.indexOf("?") == 0)
			{
				System.out.println("View is dynamic = " + view);
				viewIsDynamic = true;
			}
			TableRowInfo tbrowInfo = null;
			if (criteriaTableId == -1 && !viewIsDynamic)
			{
				tbrowInfo = RowManager.getTableRows(connection, tableId, userId, nhId, baselineId, view, 1, -1, -1);
			}
			//condition added ashishB
			if (criteriaTableId > 0 && viewIsDynamic)
			{
				tbrowInfo = RowManager.getTableRows(connection, tableId, userId, nhId, baselineId, view, 1, -1, -1);
			}
			else
			{
				tbrowInfo = RowManager.getTableRows(connection, tableId, userId, nhId, baselineId, view, 1, -1, -1);
			}
			Vector rowv = tbrowInfo.getRowVector();
			Hashtable rowHash = tbrowInfo.getRowHash();
			// rows
			//System.out.println("transaction start");
			tm = new TransactionManager(connection, userId);
			int tid = tm.startTransaction("Import changes for table id = " + tableId, "");
			tm.commitTransaction();
			tm = null;
			//System.out.println("transaction commit");
			stmt = connection.prepareStatement("UPDATE BW_ROW SET OWNER_TID = ? WHERE ID = ?");
			int numNewRows = 0;
			for (int r = 0; r < rowv.size(); r++)
			{
				Row rowObject = (Row)rowv.elementAt(r);
				int rowId = rowObject.getId();
				if (maxTransactionId < rowObject.getCreationTid())
				{
					maxTransactionId = rowObject.getCreationTid();
				}

				if (maxTransactionId < rowObject.getOwnershipAssignedTid())
				{
					maxTransactionId = rowObject.getOwnershipAssignedTid();
				}
				resData.append(rowId + Seperator);

				//if (rowObject.getCreationTid() > importTid || rowObject.getOwnershipAssignedTid() > importTid)
				if (localRowHash.get(new Integer(rowId)) == null)
				{
					resData.append("N" + Seperator);
					stmt.setInt(1, tid);
					stmt.setInt(2, rowId);
					stmt.addBatch();
					numNewRows++;
				}
				else
				{
					resData.append(Seperator);
				}
			}
			resData.replace(resData.length() - 1, resData.length(), ContentDelimeter);
			if (numNewRows > 0)
			{
				stmt.executeBatch();
				stmt.clearBatch();
			}
			stmt.close();
			stmt = null;
			System.out.println("Time(sec) to fetch rows = " + logic.getElapsedTime());
			//System.out.println(resData.toString());
			// Get the cells TBD : views other than latest
			String q = null;
			if (synch == 0)
			{
				q = "{CALL BW_IMPORT_CHANGES(?,?,?,?,?,?,?)}";
				System.out.println("Calling BW_IMPORT_CHANGES ");
			}
			else
			{
				q = "{CALL BW_IMPORT_CHANGES_ALL(?,?,?,?,?,?,?)}";
				System.out.println("Calling BW_IMPORT_CHANGES_ALL ");
			}
			//cellv = TableManager.getLatestCellsForTable(connection, m_tableid, userId, memberId, nhId, ViewPreference);
			stmt = connection.prepareStatement(q);
			stmt.setInt(1, tableId);
			stmt.setInt(2, userId);
			stmt.setInt(3, memberId);
			stmt.setInt(4, nhId);
			stmt.setString(5, view);
			stmt.setInt(6, importTid);
			stmt.setInt(7, tid);

			System.out.println("tableId = " + tableId);
			System.out.println("userId = " + userId);
			System.out.println("memberId = " + memberId);
			System.out.println("nhId = " + nhId);
			//System.out.println("view = " + view);
			System.out.println("importTid = " + importTid);
			System.out.println("newTid = " + tid);
			System.out.println("mode = " + mode);
			System.out.println("synch = " + synch);

			ResultSet rs = stmt.executeQuery ();
			System.out.println("Time(sec) to execute cell query = " + logic.getElapsedTime());
			while (rs.next())
			{
				String sval = rs.getString(1);
				String fmla = rs.getString(2);
				int rowId = rs.getInt(3);
				int colId = rs.getInt(4);
				if (maxTransactionId < rs.getInt(5))
				{
					maxTransactionId = rs.getInt(5);
				}
				if (rowHash.get(new Integer(rowId)) == null)
					continue;
				xlColumn_import col = (xlColumn_import)colHash.get(new Integer(colId));
				if (col == null)
					continue;
				if (fmla == null || fmla.indexOf("=") < 0 || mode == 1)
				{
					fmla = "";
				}
				else
				{
					fmla = fmla.trim();
				}

				//System.out.println("Got column for id = " + colId);
				int colAccess = col.getAccess();
				int cellAccess = java.lang.Math.min(raccess, colAccess);
				resData.append(rowId + Seperator + colId + Seperator + sval.trim() + Seperator + fmla + Seperator + cellAccess + Seperator);
			}
			//System.out.println(resData.toString());
			resData.replace(resData.length() - 1, resData.length(), ContentDelimeter);
			//System.out.println(resData.toString());
			stmt.close();
			rs.close();
			rs = null;
			stmt = null;
			System.out.println("Time(sec) to fetch changed cells = " + logic.getElapsedTime());

			int maxdeletedcell_tid;
			maxdeletedcell_tid = 0;
			ResultSet rs1 = null;


			try
			{
				stmt = connection.prepareStatement
					("SELECT MAX(BW_ROW.TX_ID) FROM BW_ROW WHERE BW_ROW.TX_ID > ? AND BW_ROW.BW_TBL_ID = ? AND BW_ROW.IS_ACTIVE = 0 UNION SELECT MAX(BW_COLUMN.TX_ID) FROM BW_COLUMN WHERE BW_COLUMN.TX_ID > ? AND BW_COLUMN.BW_TBL_ID = ? AND BW_COLUMN.IS_ACTIVE = 0");
				stmt.setInt(1, importTid);
				stmt.setInt(2, tableId);
				stmt.setInt(3, importTid);
				stmt.setInt(4, tableId);
				rs1 = stmt.executeQuery ();
				while( rs1.next() )
				{
					if(rs1.getInt(1) > maxdeletedcell_tid)
						maxdeletedcell_tid = rs1.getInt(1);
				}
				rs1.close();
				stmt.close();
				stmt = null;
			}
			catch(Exception e11)
			{	e11.printStackTrace();
				try
				{
					rs1.close();
					stmt.close();
					stmt = null;

				}
				catch (Exception e12)
				{
					e12.printStackTrace();
				}
			}

			System.out.println("Time(sec) to getmaxtid for deleted cells = " + logic.getElapsedTime());




			if ( maxdeletedcell_tid > maxTransactionId )
			{
				maxTransactionId = maxdeletedcell_tid;
				System.out.println("maxtid reset by cell deactivation to = " + maxTransactionId);
			}


			System.out.println("maxtid = " + maxTransactionId);

			// write the header to the response
			StringBuffer resHeader = new StringBuffer();
			resHeader.append("Success" + Seperator);
			resHeader.append(colv.size() + Seperator);
			resHeader.append(rowv.size() + Seperator);
			resHeader.append(maxTransactionId + ContentDelimeter);

			responseBuffer = resHeader.toString() + resData.toString();
		}
		catch (BoardwalkException bwe)
		{
			if (xlErrorCells.size() <= 0)
			{
				responseBuffer = "Failure";//TBD : Description of the Error
				xlErrorCells.add(new xlErrorNew(tableId, 0, 0, bwe.getErrorCode()));
			}
			StringBuffer errorBuffer = new StringBuffer();

			for (int errorIndex = 0; errorIndex < xlErrorCells.size(); errorIndex++)
			{
				xlErrorNew excelError = (xlErrorNew)(xlErrorCells.elementAt(errorIndex));
				errorBuffer.append(excelError.buildTokenString());
			}
			errorBuffer.append(Seperator);
			failureReason = errorBuffer.toString();
			System.out.println(" Failure Reason *****" + failureReason);

			try
			{
				if (tm != null)
					tm.rollbackTransaction();
			}
			catch (Exception e1)
			{
				e1.printStackTrace();
			}
		}
		catch (Exception e)
		{
			try
			{
				if (tm != null)
					tm.rollbackTransaction();
			}
			catch (Exception e1)
			{
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		finally
		{
			// close the connection
			try
			{
				if (connection != null)
					connection.close();
			}
			catch (SQLException sql)
			{
				sql.printStackTrace();
			}

			// The response
			if(failureReason.length() == 0)
			{
				logic.commitResponseBuffer(responseBuffer, response);
				System.out.println("Time to prepare response = " + logic.getElapsedTime());
			}
			else
			{
				failureReason = "FAILURE"+ContentDelimeter+failureReason;
				logic.commitResponseBuffer(failureReason, response);
				System.out.println("Time to prepare response = " + logic.getElapsedTime());
			}
		}
	}
}
