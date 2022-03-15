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
import boardwalk.common.BoardwalkUtility;
import com.boardwalk.member.Member;
import com.boardwalk.user.UserManager;

public class xlExportMergeService extends xlService implements SingleThreadModel
{
	public void service(HttpServletRequest request,
		HttpServletResponse response)throws ServletException, IOException
	{
		int userId = -1;
		int memberId = -1;
		int nhId = -1;

		int tableId = -1;

		getElapsedTime();

		// Error vector to all the Exceptions
		Vector xlErrorCells = new Vector();
		// The Vectore which holds the Row ids marked for Deletion from client
		Vector xlDeleteRows = new Vector();
		Vector serverSideDeletedRows = new Vector();
		// access variables
		boolean canAddRows = false;
		boolean canDeleteRows = false;
		boolean canAdministerColumns = false;

		boolean ExceptionAddRows = false;
		boolean ExceptionDeleteRows = false;
		boolean ExceptionAdministerColumns = false;

		// Failure String
		String failureReason = "";

		String fullTable = getRequestBuffer(request).toString();
		//System.out.println(fullTable);
		System.out.println("Time to read the buffer = " + getElapsedTime());

		// Get the database connection
		Connection connection = null;
		PreparedStatement stmt = null;
		String query = null;
		ResultSet rs = null;

		// Start a transaction
		TransactionManager tm = null;
		int tid = -1;

		HashMap rcCells = null;
		HashMap rcFormula = null;

		// parse the buffer
		String sub = null;
		int i = 0;
		int j = fullTable.indexOf(ContentDelimeter);
		int jcount = 0;
		StringBuffer responseToUpdate = new StringBuffer();
		String responseBuffer;
		try
		{
			int numColumns = 0;
			int numRows = 0;
			String view = null;
			int importTid = -1;
			int exportTid = -1;
			boolean newRowsAdded = false;
			boolean newColsAdded = false;
			StringBuffer newRowBuffer = new StringBuffer();
			StringBuffer newColBuffer = new StringBuffer();
			ArrayList columnIds = null;
			ArrayList columnNames = null;
			ArrayList rowIds = null;
			//HashMap sRowIds = new HashMap();
			//HashMap sColIds = new HashMap();
			ArrayList strValIds = null;
			ArrayList formulaIds = null;
			String[] formulaArrayAdd = null; // No case of delete here
			HashMap accCols = new HashMap();
			HashMap rowIdHash = new HashMap();
			int criteriaTableId = -1;
			int isCritical = -1;
			int criticalLevel = 1;
			String asTxComment = "";
			int numCellsChanged = 0;
			int batchSize = 10000;
			int batchCounter = 0;

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
					numColumns = Integer.parseInt(headerInfo[7]);
					numRows = Integer.parseInt(headerInfo[8]);
					importTid = Integer.parseInt(headerInfo[9]);
					exportTid = Integer.parseInt(headerInfo[10]);
					isCritical = Integer.parseInt(headerInfo[11]);
					criticalLevel = Integer.parseInt(headerInfo[12]);
					asTxComment = headerInfo[13];
					// Start a connection
					DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
					connection = databaseloader.getConnection();

					// check for old sheet
					oldSheetCheck(connection, tableId, memberId, userId, exportTid, view );

					// authenticate the user
					Member memberObj = UserManager.authenticateMember(connection, userName, userPassword, memberId);
					if (memberObj == null)
					{
						System.out.println("Authentication failed for user : " + userName);
						responseToUpdate.append("Failure");
						xlErrorCells.add(new xlErrorNew(tableId, 0, 0, 11005));
						throw new BoardwalkException(11005);
					}
					else
					{
						System.out.println("Authentication succeeded for user : " + userName);
						nhId = memberObj.getNeighborhoodId();
					}
					// see if there is a criterea table associated with this table
					criteriaTableId = TableViewManager.getCriteriaTable(connection, tableId, userId);
					System.out.println("Using criterea table id = " + criteriaTableId);

					//	Access control checks
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

					canAddRows = ftal.canAddRow();
					canDeleteRows = ftal.canDeleteRow();
					canAdministerColumns = ftal.canAdministerColumn();


					// No Access to Table
					if (awACL != wACL && canAddRows == false && canDeleteRows == false && canAdministerColumns == false)
					{
						xlErrorCells.add(new xlErrorNew(tableId, 0, 0, 10005));
						throw new BoardwalkException(10005);
					}

					// fetch data from the server
					rcCells = new HashMap(numColumns * numRows);
					rcFormula = new HashMap(numColumns * numRows);

					query = "{CALL BW_GET_RC_CELLS_AT_EXPORT3(?,?,?)}";
					stmt = connection.prepareStatement(query);
					stmt.setInt(1, tableId);
					stmt.setInt(2, importTid);
					stmt.setInt(3, userId);
					boolean results = stmt.execute();
					int rsCount = 0;
					System.out.println("Time to execute query to fetch server data = " + getElapsedTime());
					//Loop through the available result sets.
					do
					{
						if (results)
						{
							rs = stmt.getResultSet();
							if (rsCount == 0)// import cells
							{
								while (rs.next())
								{
									int rowId = rs.getInt(3);
									int colId = rs.getInt(4);
									rcCells.put(new String(rowId + ":" + colId), rs.getString(1));
									rcFormula.put(new String(rowId + ":" + colId), rs.getString(2));
									//sRowIds.put(new Integer(rowId), new Integer(rowId));
									//sColIds.put(new Integer(colId), new Integer(colId));
								}
							}
							else // export cells
							{
								while (rs.next())
								{
									int rowId = rs.getInt(3);
									int colId = rs.getInt(4);
									rcCells.put(new String(rowId + ":" + colId), rs.getString(1));
									rcFormula.put(new String(rowId + ":" + colId), rs.getString(2));
								}
							}
							rsCount++;
							rs.close();
						}
						results = stmt.getMoreResults();
					} while (results);

					// clean up
					stmt.close();
					stmt = null;
					rs = null;

					// Start a transaction
					tm = new TransactionManager(connection, userId);
					tid = tm.startTransaction("Export Merge on table id = " + tableId, asTxComment);
					System.out.println("Time to fetch server data = " + getElapsedTime());
				}
				else if (jcount == 1) // columns
				{
					// default access
					HashMap colIdHash = new HashMap();

					boolean setDefaultAccess = false;
					//String param = getServletConfig().getInitParameter("BW_RESTRICT_NEW_COLUMN");
					//System.out.println("BW_RESTRICT_NEW_COLUMN="+param);
					//if (param != null)
					//{
					//    setDefaultAccess = true;
					//}

					columnIds = new ArrayList(numColumns);
					columnNames = new ArrayList(numColumns);
					//System.out.println("Column Names = " + sub);
					String[] columnArrStr = sub.split(Seperator);
					//System.out.println("columnArrStr.length = " + columnArrStr.length);
					int prevColId = -1;
					for (int cni = 0; cni < columnArrStr.length; cni = cni + 2)
					{
						int colId = -1;
						String colName = "";
						int pcOffset = 1;
						String colIdStr = null;
						colIdStr = columnArrStr[cni];
						colName = columnArrStr[cni + 1];
						if (colIdStr.trim().equals(""))
						{
							if (canAdministerColumns)
							{
								try
								{
									lockTableForUpdate(connection, tableId);
								}
								catch (Exception e)
								{
									xlErrorCells.add(new xlErrorNew(tableId, 0, 0, 12008));
									throw new BoardwalkException(12008);
								}

								try
								{
									System.out.println("Inserting column after col = " + prevColId + " with offset of " + pcOffset);
									colId = TableManager.createColumn(
															connection,
															tableId,
															columnArrStr[cni + 1],
															"STRING",
															"",
															-1,
															1.0,
															-1,
															"",
															-1,
															-1,
															-1,
															-1,
															prevColId,
															pcOffset,
															tid,
															setDefaultAccess,
															-1
															);
									newColBuffer.append(new Integer(columnIds.size() + 1).toString() + Seperator + colId + Seperator);
									newColsAdded = true;
									pcOffset++;
									prevColId = colId;
								}
								catch (Exception e)
								{
									// unique column violation
									xlErrorCells.add(new xlErrorNew(tableId, 0, prevColId, 12001));
									System.out.println("unique column violation");
									throw new BoardwalkException(12001);
								}
							}
							else
							{
								// User does not have access to add columns
								ExceptionAdministerColumns = true;
								xlErrorCells.add(new xlErrorNew(tableId, 0, prevColId, 12010));
								System.out.println("No access to add column");
								//throw new BoardwalkException(12010);
							}
						}
						else
						{
							colId = Integer.parseInt(colIdStr);
							prevColId = colId;
							pcOffset = 1;
						}

						//System.out.println("Column Id = " + columnArrStr[cni] + " Name = " + columnArrStr[cni + 1]);
						columnIds.add(new Integer(colId));
						columnNames.add(colName);

						if (colIdHash.get(new Integer(colId)) == null)
						{
							colIdHash.put(new Integer(colId), new Integer(colId));
						}
					}

					if (newColsAdded == true)
					{
						if (isCritical < 0)
						{
							if ((criticalLevel & (1 << 1)) == (1 << 1))
							{
								isCritical = 1;
								System.out.println("Transaction critical because columns added");
							}
						}
						TableManager.resequenceColumns(connection, tableId);
					}

					// find rows to be deleted : This can be optimized, so that the following will be called
					// only if rows have to be deleted. The client knows if there are any rows to be deleted
					// get the current rows from the server, to find deleted rows
					query = "SELECT * FROM BW_GetColumnAccess(?,?,?) AS COL";
					stmt = connection.prepareStatement(query);
					stmt.setInt(1, tableId);
					stmt.setInt(2, userId);
					stmt.setInt(3, memberId);
					rs = stmt.executeQuery();

					Vector dcv = new Vector();

					while (rs.next())
					{
						/*
						 * colid     int,
						 * access_ int,
						 * prev_access int,
						 * access_tid int,
						 * sequence_number float
						 */
						int sColId = rs.getInt(1);
						int saccess = rs.getInt(2);
						int sprevaccess = rs.getInt(3);
						int saccesstid = rs.getInt(4);
						if (colIdHash.get(new Integer(sColId)) == null) // active row on server is not in xl
						{
							if (saccesstid <= importTid)
							{
								System.out.println("Deactivating column id = " + sColId);
								dcv.addElement(new Integer(sColId));
							}
						}
						else
						{
							// This particular Hash map can be used to get the access right later when updating cells
							//System.out.println(" Access sColId = " + sColId);
							//System.out.println(" saccess sColId = " + saccess);
							accCols.put(new Integer(sColId), new Integer(saccess));
						}
					}
					rs.close();
					stmt.close();

					rs = null;
					stmt = null;
					query = null;
					int ColTobeDeactivated = dcv.size();

					if (ColTobeDeactivated > 0 && canAdministerColumns == false)
					{
						// Throw exception
						// User does not have access to remove columns
						// Same message as of Add column will be shown here
						Iterator dcvi = dcv.iterator();
						while (dcvi.hasNext())
						{
							ExceptionAdministerColumns = true;
							int dColId = ((Integer)dcvi.next()).intValue();
							xlErrorCells.add(new xlErrorNew(tableId, 0, dColId, 12010));
						}
						System.out.println("No access to delete column");
					}

					Iterator dcvi = dcv.iterator();
					while (dcvi.hasNext() && canAdministerColumns)
					{
						int dColId = ((Integer)dcvi.next()).intValue();
						ColumnManager.deleteColumn(connection, dColId, tid);
					}
					if (ColTobeDeactivated > 0)
					{
						if (isCritical < 0)
						{
							if ((criticalLevel & (1 << 2)) == (1 << 2))
							{
								isCritical = 1;
								System.out.println("Transaction critical because columns deleted");
							}
						}
					}
					System.out.println("Time to read/create/delete columns = " + getElapsedTime());

					System.out.println("Time to read columns = " + getElapsedTime());
				}
				else if (jcount == 2) // rows
				{
					rowIds = new ArrayList(numRows);
					int rowId = -1;
					int prevRowId = -1;
					int prOffset = 1;
					int ri = 0;
					int rj = sub.indexOf(Seperator);  // First substring
					int ccount = 0;
					String rowIdStr = null;
					Vector drv = new Vector();
					while (rj >= 0)
					{
						rowId = -1;
						rowIdStr = sub.substring(ri, rj);
						if (rowIdStr.trim().equals(""))
						{
							if (canAddRows)
							{
								try
								{
									lockTableForUpdate(connection, tableId);
								}
								catch (Exception sq)
								{
									xlErrorCells.add(new xlErrorNew(tableId, 0, 0, 12008));
									throw new BoardwalkException(12008);
								}

								try
								{
									rowId = TableManager.createRow(connection, tableId, "", prevRowId, prOffset, tid);
									newRowBuffer.append(new Integer(rowIds.size() + 1).toString() + Seperator + rowId + Seperator);
									newRowsAdded = true;
									prOffset++;
								}
								catch (Exception e)
								{
									xlErrorCells.add(new xlErrorNew(tableId, 0, 0, 12001));
									throw new BoardwalkException(12001);
								}
							}
							else
							{
								ExceptionAddRows = true;
								xlErrorCells.add(new xlErrorNew(tableId, prevRowId, 0, 12012));
								System.out.println("No access to add rows");
							}
						}
						else
						{
							try
							{
								rowId = Integer.parseInt(rowIdStr);
								prevRowId = rowId;
								prOffset = 1;
							}
							catch (NumberFormatException exp)
							{
								rowIdStr = rowIdStr.substring(0,rowIdStr.length() - 1);
								if(!(rowIdStr.equals("")))
								{
									rowId = Integer.parseInt(rowIdStr);
									boolean isActiveRow = false;
									isActiveRow = RowManager.isRowActive(rowId);
									if(isActiveRow)
									{
										xlDeleteRows.addElement(new Integer(rowIdStr));
										prevRowId = Integer.parseInt(rowIdStr);
										prOffset = 1;
									}
									else
										serverSideDeletedRows.addElement(new Integer(rowIdStr));
								}

							}

						}

						rowIds.add(new Integer(rowId));
						if (rowIdHash.get(new Integer(rowId)) == null)
						{
							rowIdHash.put(new Integer(rowId), new Integer(rowId));
						}
						ccount++;
						ri = rj + 1;
						rj = sub.indexOf(Seperator, ri);   // Rest of substrings
					}

					rowIdStr = sub.substring(ri);// Last substring

					if (rowIdStr.trim().equals(""))
					{
						if (canAddRows)
						{
							try
							{
								lockTableForUpdate(connection, tableId);
							}
							catch (Exception sq)
							{
								xlErrorCells.add(new xlErrorNew(tableId, 0, 0, 12008));
								System.out.println("Table Lock Error");
							}

							try
							{
								rowId = TableManager.createRow(connection, tableId, "", prevRowId, prOffset, tid);
								newRowBuffer.append(new Integer(rowIds.size() + 1).toString() + Seperator + rowId + Seperator);
								newRowsAdded = true;
							}
							catch (SQLException sq)
							{
								xlErrorCells.add(new xlErrorNew(tableId, prevRowId, 0, 12001));
								throw new BoardwalkException(12001);
							}
						}
						else
						{
							ExceptionAddRows = true;
							xlErrorCells.add(new xlErrorNew(tableId, prevRowId, 0, 12012));
							System.out.println("No access to add rows");
						}
					}
					else
					{
						try
						{
							rowId = Integer.parseInt(rowIdStr);
							prevRowId = rowId;
							prOffset = 1;
						}
						catch (NumberFormatException exp)
						{
							rowIdStr = rowIdStr.substring(0,rowIdStr.length() - 1);
							if(!(rowIdStr.equals("")))
							{
								rowId = Integer.parseInt(rowIdStr);
								boolean isActiveRow = false;
								isActiveRow = RowManager.isRowActive(rowId);
								if(isActiveRow)
								{
									xlDeleteRows.addElement(new Integer(rowIdStr));
									prevRowId = Integer.parseInt(rowIdStr);
									prOffset = 1;
								}
								else
									serverSideDeletedRows.addElement(new Integer(rowIdStr));
							}
						}
					}
					rowIds.add(new Integer(rowId));
					if (rowIdHash.get(new Integer(rowId)) == null)
					{
						rowIdHash.put(new Integer(rowId), new Integer(rowId));
					}

					// resequence the rows
					if (newRowsAdded == true)
					{
						if (isCritical < 0)
						{
							if ((criticalLevel & (1 << 3)) == (1 << 3))
							{
								isCritical = 1;
								System.out.println("Transaction critical because rows added");
							}
						}
						TableManager.resequenceRows(connection, tableId);
					}

					//// find rows to be deleted
					//// get the current rows from the server, to find deleted rows
					//query = "{CALL BW_GET_TBL_ACTIVE_ROWS(?,?,?,?,?)}";
					//if (criteriaTableId > 0)
					//{
					//    String rowQuery = TableViewManager.getRowQuery(connection, tableId, userId, criteriaTableId);
					//    if (rowQuery != null)
					//    {
					//        query = "SELECT BW_ROW.ID AS ROWID, BW_ROW.TX_ID, BW_ROW.OWNER_ID, BW_ROW.OWNER_TID FROM " +
					//                    "(" + rowQuery + ") AS R " +
					//                    " , BW_ROW " +
					//                    " WHERE BW_ROW.ID = R.ID AND BW_ROW.IS_ACTIVE = 1";
					//    }
					//}
					//stmt = connection.prepareStatement(query);
					//if (criteriaTableId == -1)
					//{

					//    stmt.setInt(1, tableId);
					//    stmt.setInt(2, userId);
					//    stmt.setInt(3, memberId);
					//    stmt.setInt(4, nhId);
					//    stmt.setString(5, view);
					//}
					//rs = stmt.executeQuery();

					//while (rs.next())
					//{
					//    int srowId = rs.getInt(1);
					//    int srtid = rs.getInt(2);
					//    int sroid = rs.getInt(3);
					//    int srotid = rs.getInt(4);
					//    if (rowIdHash.get(new Integer(srowId)) == null)
					//    {
					//        if (view.equals(ViewPreferenceType.MY_ROWS))
					//        {
					//            if (sroid == userId && srotid <= importTid)
					//            {
					//                // this row was owned by me and was present and under my ownership when I did my last import
					//                drv.addElement(new Integer(srowId));
					//                System.out.println("Deactivating row = " + srowId);
					//            }
					//        }
					//        else  // any other view
					//        {
					//            if (srtid <= importTid)
					//            {
					//                // This row was  created before the last import
					//                //drv.addElement(new Integer(srowId));
					//                System.out.println("Deactivating row = " + srowId);
					//            }
					//        }
					//    }
					//}
					//rs.close();
					//stmt.close();

					//rs = null;
					//stmt = null;
					//query = null;

					// delete the rows
					if (xlDeleteRows.size() > 0)
					{

						if (canDeleteRows == false)
						{
							// Throw exception
							// User does not have access to remove rows
							Iterator rowI = xlDeleteRows.iterator();
							while (rowI.hasNext())
							{
								ExceptionDeleteRows = true;
								int delrowId = ((Integer)rowI.next()).intValue();
								xlErrorCells.add(new xlErrorNew(tableId, delrowId, 0, 12013));
							}
							System.out.println("No access to Delete rows");
							//throw new BoardwalkException(12011);
						}
						else
						{
							if (isCritical < 0)
							{
								if ((criticalLevel & (1 << 4)) == (1 << 4))
								{
									isCritical = 1;
									System.out.println("Transaction critical because rows deleted");
								}
							}
							RowManager.deactivateRows(connection, xlDeleteRows, tid);
						}
					}
					System.out.println("Time to read/create/delete rows = " + getElapsedTime());
				}
				if (serverSideDeletedRows.size() > 0)
				{
					Iterator rowI = serverSideDeletedRows.iterator();
					while(rowI.hasNext())
					{
						int delrowId = ((Integer)rowI.next()).intValue();
						if(rowIdHash.get(new Integer(delrowId)) != null)
							rowIdHash.remove(new Integer(delrowId));
								
					}
				}
				else if (jcount > 2 && jcount <= 2 * numColumns + 2) // data
				{
					//System.out.println("Processing Cell Data for column " + new Integer(jcount -3));
					// Create new cells
					String xlcellval = null;
					String xlFormula = null;
					int xlRowId = -1;
					int xlColId = -1;
					int ci = 0;
					int cj = sub.indexOf(Seperator);  // First substring
					int ccount = 0;
					if (jcount == 3)
					{
						query = " INSERT INTO BW_RC_STRING_VALUE VALUES(?, ?, ?, ?, ?, ?) ";
						stmt = connection.prepareStatement(query);
					}


					// next array is formulae for this column
					i = j + 1;
					j = fullTable.indexOf(ContentDelimeter, i);
					jcount++;
					String fsub = fullTable.substring(i, j);
					int fi = 0;
					int fj = fsub.indexOf(Seperator); // First formula
					//System.out.println("formulae for column = " + fsub);
					int ColAcess = 0;
					while (ccount < rowIds.size() - 1) // all but last row
					{
						//System.out.println("ccount = " + ccount);
						//System.out.println("ci = " + ci);
						//System.out.println("cj = " + cj);
						//System.out.println("fi = " + fi);
						//System.out.println("fj = " + fj);
						int cellChangeFlag = 1;
						String bwcellval = null;
						String bwcellformula = null;
						xlcellval = sub.substring(ci, cj);
						xlRowId = ((Integer)rowIds.get(ccount)).intValue();

						xlColId = ((Integer)columnIds.get(jcount / 2 - 2)).intValue();
						bwcellval = (String)rcCells.get(new String(xlRowId + ":" + xlColId));
						bwcellformula = (String)rcFormula.get(new String(xlRowId + ":" + xlColId));
						xlFormula = fsub.substring(fi, fj);
						bwcellformula = (String)rcFormula.get(new String(xlRowId + ":" + xlColId));
						if (accCols.get(new Integer(xlColId)) != null)
							ColAcess = ((Integer)accCols.get(new Integer(xlColId))).intValue();
						//System.out.println(" Merge ColAcess "+ColAcess);
						boolean formulaChanged = false;
						if (xlFormula.indexOf("=") < 0)
						{
							xlFormula = null;
							if (bwcellformula != null && bwcellformula.indexOf("=") >= 0)
							{
								formulaChanged = true;
								cellChangeFlag = 2;
							}
						}
						else
						{
							if (bwcellformula == null)
							{
								formulaChanged = true;
								cellChangeFlag = 2;
							}
							else
							{
								if (bwcellformula.indexOf("=") > 0)
								{
									if (!bwcellformula.equals(xlFormula))
									{
										formulaChanged = true;
										cellChangeFlag = 2;
									}
								}
							}
						}
						//System.out.println("evaluating bwcell row id = " + xlRowId + " column id = " + xlColId + " local value = " + xlcellval + " server value = " + bwcellval + "xl formula=" + xlFormula);

						if (bwcellval == null || !bwcellval.trim().equals(xlcellval.trim()) || formulaChanged == true)//new cell or changed cell
						{
							if (ColAcess == 2 && xlRowId > 0 && xlColId > 0)
							{
								//System.out.println("updated bwcell row id = " + xlRowId + " column id = " + xlColId + " local value = " + xlcellval + " server value = " + bwcellval + " xl formula=" + xlFormula + " bw formula=" + bwcellformula);
								//query = " INSERT INTO BW_RC_STRING_VALUE VALUES(?, ?, ?, ?, ?) ";
								//stmt = connection.prepareStatement(query);
								stmt.setInt(1, xlRowId);
								stmt.setInt(2, xlColId);
								stmt.setString(3, xlcellval);
								stmt.setString(4, xlFormula);
								stmt.setInt(5, tid);
								stmt.setInt(6, cellChangeFlag);
								stmt.addBatch();
								numCellsChanged = numCellsChanged + 1;
								batchCounter = batchCounter + 1;
								if (batchCounter == batchSize)
								{
									int[] rescnt = stmt.executeBatch();
									stmt.clearBatch();
									batchCounter = 0;
									System.out.print(".");
								}

							}
							else if (ColAcess == 0 || ColAcess == 1)
							{
								if (ColAcess == 1)
								{
									if (xlRowId != -1)
									{
										System.out.println("Old Column one " + xlRowId + " >> " + xlColId);
										xlErrorCells.add(new xlErrorNew(tableId, xlRowId, xlColId, 12016));
									}
								}
								else
								{
									if (xlColId != -1 || xlRowId != -1)
									{
										System.out.println("New Column without Access Right Added one" + xlRowId + " >> " + xlColId);
										xlErrorCells.add(new xlErrorNew(tableId, xlRowId, xlColId, 12016));
									}
								}
							}
						}
						ccount++;
						ci = cj + 1;
						fi = fj + 1;
						cj = sub.indexOf(Seperator, ci);   // Rest of substrings
						fj = fsub.indexOf(Seperator, fi);
					}

					int cellChangeFlag = 1;
					String bwcellval = null;
					String bwcellformula = null;
					xlRowId = ((Integer)rowIds.get(ccount)).intValue();
					xlColId = ((Integer)columnIds.get(jcount / 2 - 2)).intValue();
					xlcellval = sub.substring(ci); // Last substring
					bwcellval = (String)rcCells.get(new String(xlRowId + ":" + xlColId));
					bwcellformula = (String)rcFormula.get(new String(xlRowId + ":" + xlColId));
					//System.out.println("evaluating bwcell row id = " + xlRowId + " column id = " + xlColId + " local value = " + xlcellval + " server value = " + bwcellval);

					if (accCols.get(new Integer(xlColId)) != null)
						ColAcess = ((Integer)accCols.get(new Integer(xlColId))).intValue();
					//System.out.println(" Merge ColAcess last "+ColAcess);

					xlFormula = fsub.substring(fi);
					boolean formulaChanged = false;
					if (xlFormula.indexOf("=") < 0)
					{
						xlFormula = null;
						if (bwcellformula != null && bwcellformula.indexOf("=") >= 0)
						{
							formulaChanged = true;
							cellChangeFlag = 2;
						}
					}
					else // xl has formula
					{
						if (bwcellformula == null) // new cell
						{
							formulaChanged = true;
							cellChangeFlag = 2;
						}
						else
						{
							if (bwcellformula.indexOf("=") > 0) // server has formula
							{
								if (!bwcellformula.equals(xlFormula)) // and the xl formula is different from server
								{
									formulaChanged = true;
									cellChangeFlag = 2;
								}
							}
						}
					}
					//System.out.println("evaluating bwcell row id = " + xlRowId + " column id = " + xlColId + " local value = " + xlcellval + " server value = " + bwcellval + "xl formula=" + xlFormula);

					if (bwcellval == null || !bwcellval.trim().equals(xlcellval.trim()) || formulaChanged == true)//new cell or changed cell
					{
						if (ColAcess == 2 && xlRowId > 0 && xlColId > 0)
						{
							//System.out.println("updated bwcell row id = " + xlRowId + " column id = " + xlColId + " local value = " + xlcellval + " server value = " + bwcellval + " xl formula=" + xlFormula + " bw formula=" + bwcellformula);
							//query = " INSERT INTO BW_RC_STRING_VALUE VALUES(?, ?, ?, ?, ?) ";
							//stmt = connection.prepareStatement(query);
							stmt.setInt(1, xlRowId);
							stmt.setInt(2, xlColId);
							stmt.setString(3, xlcellval);
							stmt.setString(4, xlFormula);
							stmt.setInt(5, tid);
							stmt.setInt(6, cellChangeFlag);
							stmt.addBatch();
							numCellsChanged = numCellsChanged + 1;
							batchCounter = batchCounter + 1;
							if (batchCounter == batchSize)
							{
								int[] rescnt = stmt.executeBatch();
								stmt.clearBatch();
								batchCounter = 0;
								System.out.print(".");
							}

						}
						else if (ColAcess == 0 || ColAcess == 1)
						{
							if (ColAcess == 1)
							{

								if (xlRowId != -1)
								{
									System.out.println("Old Column two" + xlRowId + " >> " + xlColId);
									xlErrorCells.add(new xlErrorNew(tableId, xlRowId, xlColId, 12016));
								}
							}
							else
							{
								if (xlColId != -1 || xlRowId != -1)
								{
									System.out.println("New Column without Access Right Added" + xlRowId + " >> " + xlColId);
									xlErrorCells.add(new xlErrorNew(tableId, xlRowId, xlColId, 12016));
								}
							}
						}
					}

				}
				i = j + 1;
				j = fullTable.indexOf(ContentDelimeter, i);
				jcount++;
			}
			if (batchCounter > 0) // the last batch of changed cells
			{
				int[] rescnt = stmt.executeBatch();
				stmt.clearBatch();
				System.out.print(".");
			}
			if (stmt != null)
			{
				stmt.close();
				stmt = null;
			}
			System.out.println("Time to compare/insert " + numCellsChanged + " rcsv for table= " + getElapsedTime());
			System.out.println(" xlErrorCells.size() " + xlErrorCells.size());

			if (xlErrorCells.size() > 0)
			{
				throw new BoardwalkException(12011);
			}



			// update the cells based on the rcsv table
			if (numCellsChanged > 0)
			{
				query = "{CALL BW_UPD_CELL_FROM_RCSV(?,?)}";
				CallableStatement cstmt = connection.prepareCall(query);
				cstmt.setInt(1, tid);
				cstmt.setInt(2, importTid);
				cstmt.setInt(3, tableId);
				cstmt.setInt(4, userId);
				cstmt.executeUpdate();
				cstmt.close();
				cstmt = null;
				if (isCritical < 0)
				{
					if ((criticalLevel & (1 << 5)) == (1 << 5))
					{
						isCritical = 1;
						System.out.println("Transaction critical because cells changed");
					}
				}
			}
			System.out.println("Time to update " + numCellsChanged + " cells for table= " + getElapsedTime());

			// if transaction is to be made critical
			if (isCritical > 0)
				tm.addSigTransaction(asTxComment, tableId, tid);

			// commit the transaction
			tm.commitTransaction();
			//tm.rollbackTransaction(); // FOR NOW
			// create the response
			responseToUpdate.append("Success" + ContentDelimeter + tid + ContentDelimeter);
			// new rows
			responseToUpdate.append(newRowBuffer.toString() + ContentDelimeter);
			// new columns
			responseToUpdate.append(newColBuffer.toString() + ContentDelimeter);
			// TBD : Send back information about new rows and columns

			//for (int ri = 0; ri < numRows; ri++)
			//{
			//    responseToUpdate.append(rowIds.get(ri) + Seperator);
			//}
			rowIds = null;
			columnIds = null;
			columnNames = null;
			rcCells = null;
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
			e.printStackTrace();
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
		finally
		{
			rcCells = null;
			fullTable = null;
			// close the connection
			try
			{
				connection.close();
				connection = null;
			}
			catch (SQLException sql)
			{
				sql.printStackTrace();
			}

			// The response
			if (failureReason.length() == 0)
			{
				responseBuffer = responseToUpdate.toString();
				commitResponseBuffer(responseBuffer, response);
				System.out.println("Time to prepare response = " + getElapsedTime());
			}
			else
			{
				failureReason = "FAILURE" + ContentDelimeter + failureReason;
				commitResponseBuffer(failureReason, response);
				System.out.println("Time to prepare response = " + getElapsedTime());
			}
		}
	}
}
