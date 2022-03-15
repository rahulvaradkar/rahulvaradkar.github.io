package servlets;
/*
 *  This presents a list of collaboration available to a user
 */
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.*;

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
import org.apache.commons.codec.binary.Base64;

//  JSON Changes
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileReader;
import java.util.Iterator;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import java.util.zip.*;
import java.io.ByteArrayOutputStream;

import java.io.BufferedWriter;
import java.io.Writer;
import java.io.File;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
//  JSON Changes

public class xlExportChangesServiceJsonAPI extends xlServiceJsonAPI implements SingleThreadModel
{
	// global variables : should be cleaned up in finally
	int userId = -1;
	int memberId = -1;
	int tableId = -1;
	int importTid = -1;
	int exportTid = -1;
	int isCritical = -1;
	int criticalLevel = 1;
	int numColumns = 0;
	int numRows = 0;
	String asTxComment = "";
	// Error vector to all the Exceptions
	Vector xlErrorCells = new Vector();
	// access variables
	boolean canAddRows = false;
	boolean canDeleteRows = false;
	boolean canAdministerColumns = false;
	StringBuffer newRowBuffer = new StringBuffer();
	StringBuffer newColBuffer = new StringBuffer();
	// access filter
	// see if there is a criterea table associated with this table
	int criteriaTableId = -1;
	// column access
	HashMap accCols = new HashMap();
	int defaultAccess = 2;
	HashMap colCellAccess = new HashMap();
	HashMap accessQueryXrowSet = new HashMap();
	HashMap rowIdHash = new HashMap();
	Vector xlDeleteRows = new Vector();
	HashMap colIdHash = new HashMap();
	boolean RowsDeleted = false; // This will help in detecting if a row was deleted or not.
	boolean ColsDeleted = false; // This will help in detecting if a column was deleted or not.
	ArrayList columnIds = null;


	int MAX_RETRY_ATTEMPTS = 5;
	int RETRY_WAIT_TIME_MIN = 1000;
	int RETRY_WAIT_TIME_MAX = 3000;

	public void service(HttpServletRequest request,
		HttpServletResponse response)throws ServletException, IOException
	{
		Connection connection = null;
		TransactionManager tm = null;
		int tid = -1;
		String inputData = null; //  JSON Changes
		
		// start the timer
		getElapsedTime();
		//String[] fullTableArr = getRequestBuffer(request).toString().split(ContentDelimeter);
		BoardwalkRequestReader reader = getRequestReader(request);
		//System.out.println(fullTable);
		System.out.println("xlExportChangesServiceJsonAPI: Time to read the buffer = " + getElapsedTime());

		// Failure String
		String failureReason = "";
		StringBuffer responseToUpdate = new StringBuffer();
		String responseBuffer;

		try
		{
			MAX_RETRY_ATTEMPTS = Integer.parseInt(getServletConfig().getInitParameter("MAX_RETRY_ATTEMPTS"));
			RETRY_WAIT_TIME_MIN = Integer.parseInt(getServletConfig().getInitParameter("RETRY_WAIT_TIME_MIN"));
			RETRY_WAIT_TIME_MAX = Integer.parseInt(getServletConfig().getInitParameter("RETRY_WAIT_TIME_MAX"));
			System.out.println("MAX_RETRY_ATTEMPTS=" + MAX_RETRY_ATTEMPTS);
			System.out.println("RETRY_WAIT_TIME_MIN=" + RETRY_WAIT_TIME_MIN);
			System.out.println("RETRY_WAIT_TIME_MAX=" + RETRY_WAIT_TIME_MAX);
		}
		catch (Exception e)
		{
			System.out.println("Deadlock parameters not set. Using defaults...");
		}


		for (int ti = 0; ti < MAX_RETRY_ATTEMPTS; ti++)
		{
			try
			{
				failureReason = "";
				responseBuffer = "";
				responseToUpdate = new StringBuffer();
				// Start a connection
				DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
				connection = databaseloader.getConnection();
				//  JSON Changes
				List<String> listheader = new ArrayList<String>();
				String[] headerInfo=null;
				inputData = reader.getNextContent()+""+reader.getNextContent()+""+reader.getNextContent()+"";
				String celldatatemp=reader.getNextContent();
				if(celldatatemp.equals(""))
				{
					inputData = inputData + " ";
				}
				else{
					inputData = inputData + celldatatemp;
				}
				System.out.println("string data" + inputData);
				try {
					File fileDir = new File("d:/tomcat7/webapps/EPortal/Staging_JSON/JSON_Data_File.txt");
					Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileDir), "UTF8"));
				out.append(inputData);
				out.flush();
				out.close();

				} catch (IOException e) {
				e.printStackTrace();
			}
			BoardwalkJsonReaderJsonAPI bjrf=new BoardwalkJsonReaderJsonAPI();
			bjrf.filewrite("d:/tomcat7/webapps/EPortal/Staging_JSON/JSON_Data_File.txt");

			BoardwalkJsonReaderJsonAPI bjr=new BoardwalkJsonReaderJsonAPI("d:/tomcat7/webapps/EPortal/Staging_JSON/JSON_Data_File.json");
			processHeader(connection, bjr.jsonreader("HeaderData"));

			//processHeader(connection, headerInfo);
				// Start a transaction
				tm = new TransactionManager(connection, userId);
				tid = tm.startTransaction("Export Changes on table id = " + tableId, asTxComment);
				//  JSON Changes
				//	processColumns(connection, tid, reader.getNextContent());
				processColumns(connection, tid,  bjr.jsonreader("ColumnData"));
				//reader.getNextContent();
				processRows(connection, tid,bjr.jsonreader("RowData"));
				processCells(connection, tid, bjr.jsonreader("CellData"));
				//  JSON Changes
				String cellColBuffer = null;
				
				//  JSON Changes
				  
				/* while ((cellColBuffer = bjr.jsonreader("CellData")) != null )
				{
					if (!cellColBuffer.trim().equals(""))
					{
						processCells(connection, tid, cellColBuffer);
					}
				}   */
				//  JSON Changes
				String query = "{CALL BW_UPD_CELL_FROM_RCSV(?,?,?,?)}";
				CallableStatement cstmt = connection.prepareCall(query);
				cstmt.setInt(1, tid);
				cstmt.setInt(2, importTid);
				cstmt.setInt(3, tableId);
				cstmt.setInt(4, userId);
				int updCount = cstmt.executeUpdate();
				cstmt.close();
				cstmt = null;
				System.out.println("Time to update cells for table= " + getElapsedTime());
				
				PreparedStatement stmt = null;
				ResultSet rs = null;
				// filter consistency check
				if (criteriaTableId > 0)
				{
					HashSet accessibleRowsAfterSubmit = new HashSet();
					
					String lsRowQuery  = TableViewManagerJsonAPI.getRowQuery(connection, tableId, userId, criteriaTableId, true, "LATEST");

					stmt = connection.prepareStatement(lsRowQuery);
					rs = stmt.executeQuery();
					while (rs.next())
					{
						accessibleRowsAfterSubmit.add(new Integer(rs.getInt(1)));
					}
					
					Iterator rowIdsFromClientIter = rowIdHash.keySet().iterator();
					while (rowIdsFromClientIter.hasNext())
					{
						System.out.println ("xlExpoortChangesService::service(): xlDeleteRows = " + xlDeleteRows.toString());
						Integer rowIdFromClient = (Integer) rowIdHash.get(rowIdsFromClientIter.next());
						if (rowIdFromClient > 0 && !accessibleRowsAfterSubmit.contains(rowIdFromClient) 
								&& !xlDeleteRows.contains(rowIdFromClient)) // deleted rows will not be fetched
						{
							System.out.println("The row id = " + rowIdFromClient + " is not accessible for user");
							xlErrorCells.add(new xlErrorNew(tableId, rowIdFromClient.intValue(), 0, 12018));
							throw new BoardwalkException(12018, "Access Filter violation");
						}
					}
					rs.close();
					stmt.close();
				}

				System.out.println("xlExportChangesServiceJsonAPI: xlErrorCells.size() " + xlErrorCells.size());
				if (xlErrorCells.size() > 0)
				{
					throw new BoardwalkException(12011);
				}

				// if transaction is to be made critical
				if (isCritical > 0)
					tm.addSigTransaction(asTxComment, tableId, tid);

				tm.commitTransaction();

				// create the response
				responseToUpdate.append("Success" + ContentDelimeter + tid + ContentDelimeter);
				// new rows
				responseToUpdate.append(newRowBuffer.toString() + ContentDelimeter);
				// new columns
				responseToUpdate.append(newColBuffer.toString() + ContentDelimeter);
				// info for delete column / row
				responseToUpdate.append(RowsDeleted + ContentDelimeter + ColsDeleted + ContentDelimeter);
				// number of rows and columns for WriteCacheFromSheet call
				responseToUpdate.append(rowIdHash.size() + ContentDelimeter + colIdHash.size() + ContentDelimeter);
				ti = MAX_RETRY_ATTEMPTS;
				
			}
			catch (SQLException sqe)
			{

				sqe.printStackTrace();
				// rollback the transaction
				try
				{
					if (tm != null)
						tm.rollbackTransaction();
				}
				catch (Exception e1)
				{
					e1.printStackTrace();
				}

				//deadlock exception
				if (sqe.getErrorCode() == 1205)
				{
					if (ti == MAX_RETRY_ATTEMPTS - 1)
					{
						failureReason = (new xlErrorNew(tableId, 0, 0, 13001)).buildTokenString();
						System.out.println("xlExportChangesServiceJsonAPI:  Deadlock maximum attempts exhausted. Sending server busy message to client ");
					}
					else
					{
						System.out.println("xlExportChangesServiceJsonAPI: Deadlock Attempt number = " + (ti + 1) + " out of max = " + MAX_RETRY_ATTEMPTS);
						try
						{
							int sleepTime = RETRY_WAIT_TIME_MIN + (new Random()).nextInt(RETRY_WAIT_TIME_MAX - RETRY_WAIT_TIME_MIN);
							System.out.println("Sleeping for " + sleepTime + "ms");
							Thread.sleep(sleepTime);
						}
						catch (InterruptedException e2)
						{
							e2.printStackTrace();
						}
					}
				}
				else
				{
					ti = MAX_RETRY_ATTEMPTS; // dont try again
					failureReason = sqe.getMessage();
				}
			}
			catch (BoardwalkException bwe)
			{
				ti = MAX_RETRY_ATTEMPTS;
				bwe.printStackTrace();
				if (xlErrorCells.size() <= 0)
				{
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
				try
				{
					// We check the null here because if the user has nop access what so ever then
					// Why create a transaction in the first place itself
					if (tm != null)
						tm.rollbackTransaction();
				}
				catch (Exception e1)
				{
					e1.printStackTrace();
				}

			}
			catch (Exception e) //unknown exeception
			{
				ti = MAX_RETRY_ATTEMPTS;
				try
				{
					if (tm != null)
						tm.rollbackTransaction();
				}
				catch (Exception e1)
				{
					e1.printStackTrace();
				}
				failureReason = e.getMessage();
				e.printStackTrace();
			}
			finally
			{
				try
				{
					// close the reader
					reader.close();
					// close the connection
					connection.close();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}

		
				// cleanup
				connection = null;
				tm = null;
				tid = -1;
				userId = -1;
				memberId = -1;
				tableId = -1;
				importTid = -1;
				exportTid = -1;
				isCritical = -1;
				criticalLevel = 1;
				criteriaTableId = -1;
				
				numColumns = 0;
				numRows = 0;

				asTxComment = "";
				xlErrorCells = new Vector();
				canAddRows = false;
				canDeleteRows = false;
				canAdministerColumns = false;

				newRowBuffer = new StringBuffer();
				newColBuffer = new StringBuffer();
				rowIdHash = new HashMap();
				 xlDeleteRows = new Vector();
				colIdHash = new HashMap();
				RowsDeleted = false; // This will help in detecting if a row was deleted or not.
				ColsDeleted = false;
				columnIds = null;
				accCols = new HashMap();
				colCellAccess = new HashMap();
				accessQueryXrowSet = new HashMap();
				defaultAccess = 2;
			}
		}
		
		//fullTableArr = null;
		// The response
		if (failureReason.length() == 0)
		{
			responseBuffer = responseToUpdate.toString();
			commitResponseBuffer(responseBuffer, response);
			System.out.println("xlExportChangesServiceJsonAPI: Success : Time to prepare response = " + getElapsedTime());
		}
		else
		{
			failureReason = "FAILURE" + ContentDelimeter + failureReason;
			commitResponseBuffer(failureReason, response);
			System.out.println("xlExportChangesServiceJsonAPI: Failure : Time to prepare response = " + getElapsedTime());
			System.out.println("xlExportChangesServiceJsonAPI: failureReason = " + failureReason);
		}
	}

	public void processHeader(Connection connection, String[] headerInfo) throws BoardwalkException, SystemException, SQLException
	{
		getElapsedTime();

		String view = null;
		int nhId = -1;

		//  JSON Changes 
/*		System.out.println("header = " + headerInfo[0]);
		String[] headerInfo = sub.split(Seperator);
		System.out.println("headerInfo[0] = " + headerInfo[0]);
		System.out.println("headerInfo[1] = " + headerInfo[1]);
		System.out.println("headerInfo[2] = " + headerInfo[2]);
		System.out.println("headerInfo[3] = " + headerInfo[3]);
		System.out.println("headerInfo[4] = " + headerInfo[4]);
		System.out.println("headerInfo[5] = " + headerInfo[5]);
		System.out.println("headerInfo[6] = " + headerInfo[6]);
		System.out.println("headerInfo[7] = " + headerInfo[7]);
		System.out.println("headerInfo[8] = " + headerInfo[8]);
		System.out.println("headerInfo[9] = " + headerInfo[9]);
		System.out.println("headerInfo[10] = " + headerInfo[10]);
		System.out.println("headerInfo[11] = " + headerInfo[11]);
		System.out.println("headerInfo[12] = " + headerInfo[12]);
		System.out.println("headerInfo[13] = " + headerInfo[13]); 
		*/
		//  JSON Changes

		userId = Integer.parseInt(headerInfo[0]);
		String userName = headerInfo[1];
		String userPassword = headerInfo[2];
		memberId = Integer.parseInt (headerInfo[3]);
		nhId = Integer.parseInt(headerInfo[4]);
		tableId = Integer.parseInt (headerInfo[5]);
		view = headerInfo[6];
		numColumns = Integer.parseInt(headerInfo[7]);
		numRows = Integer.parseInt (headerInfo[8]);
		importTid = Integer.parseInt(headerInfo[9]);
		exportTid = Integer.parseInt(headerInfo[10]);
		isCritical = Integer.parseInt(headerInfo[11]);
		criticalLevel = Integer.parseInt(headerInfo[12]);
		asTxComment = headerInfo[13];
	
		// authenticate the user
		Member memberObj = UserManager.authenticateMember(connection, userName,userPassword, memberId);
		if (memberObj == null)
		{
			System.out.println("Authentication failed for user : " + userName);
			xlErrorCells.add( new xlErrorNew( tableId, 0, 0, 11005));
			throw new BoardwalkException(11005);
		}
		else
		{
			System.out.println("Authentication succeeded for user : " + userName);
			nhId = memberObj.getNeighborhoodId();
		}
		
		// try the old sheet check
		criteriaTableId = TableViewManagerJsonAPI.getCriteriaTable(connection, tableId, userId);
		System.out.println("Using criterea table id = " + criteriaTableId);
		
		if (!(view == null || view.trim().equals("") || view.equalsIgnoreCase("Latest")))
		{
			System.out.println ("View = " + view );
			String lsRowQuery = TableViewManagerJsonAPI.getRowQuery(connection, tableId, userId, criteriaTableId, true, view);
			oldSheetCheck(connection, tableId, memberId, userId, exportTid, lsRowQuery);
		}
		else
		{
			oldSheetCheck(connection, tableId, memberId, userId, exportTid, view);
		}
		
		/*********	g2	27may11	skipped checksignificant servlet********/
		PreparedStatement preparedstatement1 = null;
		ResultSet resultset1 = null;
		// check if there are any critical updates since last import
		// Added a check on the users so that we do not process critical update of Same user
		int critTid1 = -1;
		String query1 = " SELECT MAX(BW_SIGNIFICANT_TXS.TX_ID)" +
					   " FROM BW_SIGNIFICANT_TXS " +
					   " WHERE " +
					   " BW_TBL_ID = ? AND BW_SIGNIFICANT_TXS.CREATED_BY <> " + userId +
					   " GROUP BY  BW_TBL_ID ";
		preparedstatement1 = connection.prepareStatement(query1);
		preparedstatement1.setInt(1, tableId);
		resultset1 = preparedstatement1.executeQuery();
		String lsResponseStr = null;
		
		if (resultset1.next())
		{
			critTid1 = resultset1.getInt(1);
		}
		preparedstatement1.close();
		preparedstatement1 = null;
		if (resultset1 != null)
		{
			resultset1.close();
			resultset1 = null;
		}

		if (importTid < critTid1)
		{
			System.out.println("Found Critical updates after the last import ----g2");
			//lsResponseStr = getSignificantUpdateIds(connection, tableId, importTid, userId, nhId, view, memberId);
			throw new BoardwalkException(12017);
		}
		System.out.println("No Critical updates Found after the last import ----g2");
		/*****************/
		
		
		// see if there is a criterea table associated with this table
		//criteriaTableId = TableViewManagerJsonAPI.getCriteriaTable(connection, tableId, userId);
		//System.out.println("Using criterea table id = " + criteriaTableId);

		//	Access control checks
		TableInfo tinfo = TableManager.getTableInfo(connection, userId, tableId);
		TableAccessList ftal = TableViewManagerJsonAPI.getSuggestedAccess(connection, tinfo, userId, memberId, nhId);
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

		canAddRows				= ftal.canAddRow();
		canDeleteRows			= ftal.canDeleteRow();
		canAdministerColumns	= ftal.canAdministerColumn();

		// No Access to Table
		if ( awACL != wACL && canAddRows == false && canDeleteRows == false && canAdministerColumns == false)
		{
			xlErrorCells.add( new xlErrorNew(tableId, 0, 0, 10005));
			throw new BoardwalkException(10005);
		}
		
		// see if there is a criterea table associated with this table
		criteriaTableId = TableViewManagerJsonAPI.getCriteriaTable(connection, tableId, userId);
		System.out.println("Using criterea table id = " + criteriaTableId);
		int accessTableId = TableViewManagerJsonAPI.getAccessTable(connection, tableId, userId);
		if (accessTableId > 0)
		{
			Integer defAccess = new Integer(2);
			colCellAccess = TableViewManagerJsonAPI.getColumnAcccess(connection, tableId, accessTableId, userId);
			Iterator columnConditionalAccessIter = colCellAccess.keySet().iterator();
			while (columnConditionalAccessIter.hasNext())
			{
				Integer colId = (Integer) columnConditionalAccessIter.next();
				if (colCellAccess.get(colId) instanceof String){
					String accessString = (String) colCellAccess.get(colId);
					Pattern pattern = Pattern.compile("(\\d)(\\?.*)");
					Matcher matcher = pattern.matcher(accessString);
					if(matcher.matches())
					{
						int access = Integer.parseInt(matcher.group(1));
						String accessInstr = matcher.group(2);
						System.out.println("column acess for colid = " + colId + " is " + access +
								" if row matches accessQuery = " + accessInstr);

						HashSet rowSet = (HashSet) accessQueryXrowSet.get(accessInstr);
						if (rowSet == null)
						{
							String rowQuery = TableViewManagerJsonAPI.getRowQuery(connection, 
									TableViewManagerJsonAPI.getCriteriaForDynamicView(accessInstr), tableId, true);
		
							PreparedStatement stmt = connection.prepareStatement(rowQuery);
							ResultSet rs = stmt.executeQuery();
							while (rs.next())
							{
								int rowId = rs.getInt(1);
								if (rowSet != null) {
									rowSet.add(new Integer(rowId));
								}
								else
								{
									rowSet = new HashSet();
									rowSet.add(new Integer(rowId));
									accessQueryXrowSet.put(accessInstr, rowSet);
								}
							}

							System.out.println("rowSet = " + rowSet);
						}
					}
				}
			}
			defaultAccess = (Integer) colCellAccess.get(new Integer(-1));
			System.out.println("processHeader():defaultAccess = " + defaultAccess);
		}
		
		System.out.println("Time to process header = " + getElapsedTime());
	}

	public void processColumns(Connection connection, int tid, String[] columnArrStr) //  JSON Changes
		throws BoardwalkException, SystemException, SQLException
	{
		// default access
		boolean setDefaultAccess = false;
		boolean ExceptionAdministerColumns = false;
		boolean newColsAdded = false;


		ArrayList columnNames = null;
		Vector dcv = new Vector();
		
		//String param = getServletConfig().getInitParameter("BW_RESTRICT_NEW_COLUMN");
		//System.out.println("BW_RESTRICT_NEW_COLUMN=" + param);
		//if (param != null)
		//{
		//    setDefaultAccess = true;
		//}

		columnIds = new ArrayList(numColumns);
		columnNames = new ArrayList(numColumns);
		//System.out.println("Column Names = " + sub);
		//String[] columnArrStr = sub.split(Seperator);
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
			//System.out.println("colIdStr = " + colIdStr);
			//System.out.println("colName = " + colName);
			if (colIdStr.trim().equals(""))
			{
				if (canAdministerColumns)
				{
					try
					{
						lockTableForUpdate(connection, tableId);
						System.out.println("Inserting column after col = " + prevColId + " with offset of " + pcOffset);
					}
					catch (Exception e)
					{
						xlErrorCells.add(new xlErrorNew(tableId, 0, 0, 12008));
						throw new BoardwalkException(12008);
					}

					try
					{
						colId = TableManager.createColumnXL(
												connection,
												tableId,
												columnArrStr[cni + 1],
												prevColId,
												pcOffset,
												tid
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
					xlErrorCells.add(new xlErrorNew(tableId, 0, prevColId, 12010));
					ExceptionAdministerColumns = true;
					System.out.println("No access to add column");
				}
			}
			else
			{
				try
				{
					colId = Integer.parseInt(colIdStr);
					prevColId = colId;
					pcOffset = 1;
				}
				catch (NumberFormatException nfe)
				{
					colIdStr = colIdStr.substring(0, colIdStr.length() - 1);
					if (!(colIdStr.equals("")))
					{
						colId = Integer.parseInt(colIdStr);
						dcv.addElement(new Integer(colIdStr));
					}
				}
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
			if (isCritical <= 0)
			{
				if ((criticalLevel & (1 << 1)) == (1 << 1))
				{
					isCritical = 1;
					System.out.println("Transaction critical because columns added");
				}
			}
			TableManager.resequenceColumns(connection, tableId);
		}

		//// find columns to be deleted : This can be optimized, so that the following will be called
		//// only if rows have to be deleted. The client knows if there are any rows to be deleted
		//// get the current rows from the server, to find deleted rows
		//String query = "SELECT * FROM BW_GetColumnAccess(?,?,?) AS COL";
		//PreparedStatement stmt = connection.prepareStatement(query);
		//stmt.setInt(1, tableId);
		//stmt.setInt(2, userId);
		//stmt.setInt(3, memberId);
		//ResultSet rs = stmt.executeQuery();

		//while (rs.next())
		//{
		//    /*
		//     * colid     int,
		//     * access_ int,
		//     * prev_access int,
		//     * access_tid int,
		//     * sequence_number float
		//     */
		//    int sColId = rs.getInt(1);
		//    int saccess = rs.getInt(2);
		//    int sprevaccess = rs.getInt(3);
		//    int saccesstid = rs.getInt(4);
		//    if (colIdHash.get(new Integer(sColId)) == null) // active row on server is not in xl
		//    {
		//        if (saccesstid <= importTid)
		//        {
		//            System.out.println("Deactivating column id = " + sColId);
		//            dcv.addElement(new Integer(sColId));
		//        }
		//    }
		//    else
		//    {
		//        // This particular Hash map can be used to get the access right later when updating cells
		//        //System.out.println(" Access sColId = " + sColId);
		//        //System.out.println(" saccess sColId = " + saccess);
		//        accCols(new Integer(sColId), new Integer(saccess));
		//    }
		//}
		//rs.close();
		//stmt.close();

		//rs = null;
		//stmt = null;
		//query = null;
		int ColTobeDeactivated = dcv.size();

		if (ColTobeDeactivated > 0 && canAdministerColumns == false)
		{
			// Throw exception
			// User does not have access to remove columns
			// Same message as of Add column will be shown here
			Iterator dcvi = dcv.iterator();
			while (dcvi.hasNext())
			{
				int dColId = ((Integer)dcvi.next()).intValue();
				xlErrorCells.add(new xlErrorNew(tableId, 0, dColId, 12010));
				if (ExceptionAdministerColumns == false)
					ExceptionAdministerColumns = true;
			}
			System.out.println("No access to delete column");
		}

		Iterator dcvi = dcv.iterator();
		while (dcvi.hasNext() && canAdministerColumns)
		{
			int dColId = ((Integer)dcvi.next()).intValue();
			if (colIdHash.get(new Integer(dColId)) != null)
				colIdHash.remove(new Integer(dColId));

			ColsDeleted = ColumnManager.deleteColumn(connection, dColId, tid);
		}
		if (ColTobeDeactivated > 0)
		{
			if (isCritical <= 0)
			{
				if ((criticalLevel & (1 << 2)) == (1 << 2))
				{
					isCritical = 1;
					System.out.println("Transaction critical because columns deleted");
				}
			}
		}
		System.out.println("Time to read/create/delete columns = " + getElapsedTime());

	}

	public void processRows(Connection connection, int tid,  String[] sub) //  JSON Changes
		throws BoardwalkException, SystemException, SQLException
	{
		getElapsedTime();
		boolean newRowsAdded = false;

			
		// The Vectore which holds the Row ids marked for Deletion from client
		
		boolean ExceptionAddRows = false;
		boolean ExceptionDeleteRows = false;
		HashMap newRowHash = new HashMap();

		System.out.println("--------------Processing Rows--------------");
		//rowIds = new ArrayList(numRows);
		int rowId = -1;
		int prevRowId = -1;
		int prOffset = 1;
		int ri = 0;
		//int rj = sub.indexOf(Seperator);  // First substring //  JSON Changes
		int ccount = 0;
		String rowIdStr = null;
		Vector nrv = new Vector();
		boolean isDeletedRow = false;
		//int rowIdx = 0;
		//while (rj >= 0) //  JSON Changes
		while (ri < sub.length-1) //  JSON Changes
		{
			rowId = -1;
			//  JSON Changes
			//rowIdStr = sub.substring(ri, rj);
			rowIdStr = sub[ri];
			System.out.println("rowIdStr="+rowIdStr);
			//  JSON Changes
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
						newXLRow nr = new newXLRow(prevRowId, prOffset, ccount);
						nrv.addElement(nr);
						//rowId = TableManager.createRowXL(connection, tableId, "", prevRowId, prOffset, tid);
						//System.out.println("Added row with id = " + rowId);
						//newRowBuffer.append(new Integer(rowIds.size() + 1).toString() + Seperator + rowId + Seperator);
						newRowsAdded = true;
						prOffset++;
					}
					catch (Exception e)
					{
						xlErrorCells.add(new xlErrorNew(tableId, prevRowId, 0, 12001));
						throw new BoardwalkException(12001);
					}
				}
				else
				{
					xlErrorCells.add(new xlErrorNew(tableId, prevRowId, 0, 12012));
					ExceptionAddRows = true;
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
				catch (NumberFormatException numex)
				{
					rowIdStr = rowIdStr.substring(0, rowIdStr.length() - 1);
					if (!(rowIdStr.equals("")))
					{
						rowId = Integer.parseInt(rowIdStr);
						xlDeleteRows.addElement(new Integer(rowIdStr));
					}
				}

			}
			//rowIds.add(new Integer(rowId));
			if (rowId != -1 && rowIdHash.get(new Integer(ccount)) == null && isDeletedRow == false)
			{
				rowIdHash.put(new Integer(ccount), new Integer(rowId));
			}

			//rowIdx = rowIdx + 1;
			ccount++;
			//  JSON Changes
			//ri=rj+1;
			ri = ri + 1;
			//rj = sub.indexOf(Seperator, ri);   // Rest of substrings
			//System.out.println("rj 915="+rj);
		}
		//rowIdStr =sub.substring(ri);// Last substring
		rowIdStr = sub[ri];
		System.out.println("rowIdStr 919="+rowIdStr);
		//  JSON Changes

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
					throw new BoardwalkException(12008);
				}

				try
				{
					newXLRow nr = new newXLRow(prevRowId, prOffset, ccount);
					nrv.addElement(nr);
					//rowId = TableManager.createRowXL(connection, tableId, "", prevRowId, prOffset, tid);
					//System.out.println("Added row with id = " + rowId);
					//newRowBuffer.append(new Integer(rowIds.size() + 1).toString() + Seperator + rowId + Seperator);
					newRowsAdded = true;
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			else
			{
				xlErrorCells.add(new xlErrorNew(tableId, prevRowId, 0, 12012));
				ExceptionAddRows = true;
				System.out.println("No access to add rows");
			}
		}
		else
		{
			//System.out.println("=============canAddRows Value of rowid============="+rowId);
			try
			{
				rowId = Integer.parseInt(rowIdStr);
				prevRowId = rowId;
				prOffset = 1;
			}
			catch (NumberFormatException exp)
			{
				System.out.println("rowIdStr 969="+rowIdStr); //  JSON Changes
				rowIdStr = rowIdStr.substring(0, rowIdStr.length() - 1);
				if (!(rowIdStr.equals("")))
				{
					rowId = Integer.parseInt(rowIdStr);
					xlDeleteRows.addElement(new Integer(rowIdStr));
				}

			}

		}
		//rowIds.add(new Integer(rowId));
		//System.out.println("Processing rowId = " + rowId);
		if (rowId != -1 && rowIdHash.get(new Integer(ccount)) == null && isDeletedRow == false)
		{
			rowIdHash.put(new Integer(ccount), new Integer(rowId));
		}

		System.out.println("Time to process rows = " + getElapsedTime());

		// resequence the rows
		if (newRowsAdded == true)
		{
			// add the new rows
			String query =
				"INSERT INTO BW_NEW_ROW " +
				"(PREV_ROW_ID, TX_ID, OFFSET) " +
				"VALUES " +
				"(?, ?, ?) ";
			PreparedStatement stmt = connection.prepareStatement(query);
			Iterator nri = nrv.iterator();
			while (nri.hasNext())
			{
				newXLRow nr = (newXLRow)nri.next();
				stmt.setInt(1, nr.getPreviousRowId());
				stmt.setInt(2, tid);
				stmt.setInt(3, nr.getIndex());
				stmt.addBatch();
			}
			int[] rescnt = stmt.executeBatch();
			stmt.clearBatch();
			stmt.close();
			stmt = null;
			System.out.println("Time to create new rows in tmp table = " + getElapsedTime());

			query = "{CALL BW_CR_ROWS_XL(?,?,?)}";
			CallableStatement cstmt = connection.prepareCall(query);
			cstmt.setInt(1, tableId);
			cstmt.setInt(2, userId);
			cstmt.setInt(3, tid);
			cstmt.executeUpdate();
			cstmt.close();
			cstmt = null;
			System.out.println("Time to create new rows = " + getElapsedTime());


			// create the buffer
			query = "SELECT BW_ROW.ID, BW_ROW.NAME FROM BW_ROW WHERE TX_ID = ?";
			stmt = connection.prepareStatement(query);
			stmt.setInt(1, tid);
			ResultSet rs = stmt.executeQuery();
			while (rs.next())
			{
				int rid = rs.getInt(1);
				int ridx = Integer.parseInt(rs.getString(2));
				rowIdHash.put(new Integer(ridx), new Integer(rid));
				//newRowBuffer.append(new Integer(rowIds.size() + 1).toString() + Seperator + rid + Seperator);
				newRowBuffer.append((ridx + 1) + Seperator + rid + Seperator);
			}

			stmt.close();
			rs.close();
			stmt = null;
			rs = null;
			System.out.println("Time to read back new rows = " + getElapsedTime());

			// resequence the rows
			TableManager.resequenceRows(connection, tableId);
			System.out.println("Time to resequence rows = " + getElapsedTime());

			if (isCritical <= 0)
			{
				System.out.println("critical level = " + criticalLevel);
				System.out.println("(1<<3) = " + (1 << 3));
				System.out.println("(criticalLevel & (1 << 3)) = " + (criticalLevel & (1 << 3)));
				if ((criticalLevel & (1 << 3)) == (1 << 3))
				{
					isCritical = 1;
					System.out.println("Transaction critical because rows added");
				}
			}
		}

		// delete the rows
		if (xlDeleteRows.size() > 0)
		{
			Iterator rowI = xlDeleteRows.iterator();
			System.out.println("canDeleteRows="+canDeleteRows+"----------------Deleting rows---------- " + xlDeleteRows.size());
			if (canDeleteRows == false)
			{
				// Throw exception
				// User does not have access to remove rows


				while (rowI.hasNext())
				{
					int delrowId = ((Integer)rowI.next()).intValue();
					xlErrorCells.add(new xlErrorNew(tableId, delrowId, 0, 12013));
					ExceptionDeleteRows = true;
				}
				System.out.println("No access to Delete rows");
			}
			else
			{
				// this will remove the row from the hash map here itself
				//while(rowI.hasNext())
				//{
				//    int delrowId = ((Integer)rowI.next()).intValue();
				//    if (rowIdHash.get(new Integer(delrowId)) != null)
				//    {
				//        rowIdHash.remove(new Integer(delrowId));
				//        System.out.println("Deleting rowid from rowIdHash : " + delrowId);
				//        System.out.println("rowidHash.size() = " + rowIdHash.size());
				//    }

				//}

				//			System.out.println("----------Inside Deactivate Row-----------"+delrowId);
				RowsDeleted = RowManager.deactivateRows(connection, xlDeleteRows, tid);
				if (isCritical <= 0)
				{
					if ((criticalLevel & (1 << 4)) == (1 << 4))
					{
						isCritical = 1;
						System.out.println("Transaction critical because rows deleted");
					}
				}
			}
		}
		//if (serverSideDeletedRows.size() > 0)
		//{
		//    Iterator rowI = serverSideDeletedRows.iterator();
		//    while(rowI.hasNext())
		//    {
		//        int delrowId = ((Integer)rowI.next()).intValue();
		//        if(rowIdHash.get(new Integer(delrowId)) != null)
		//            rowIdHash.remove(new Integer(delrowId));

		//    }
		//}
		System.out.println("Time to delete rows = " + getElapsedTime());

	}

	public void processCells(Connection connection, int tid, String[] sub) //  JSON Changes
		throws BoardwalkException, SystemException, SQLException
	{
	if(sub.length!=1) //  JSON Changes
	{
		getElapsedTime();
		ArrayList formulaIds = null;
		ArrayList strValIds = null;
		String formulaArrayAdd[] = null;
		String formulaArrayDel[] = null;
		int numCellsChanged = 0;

		System.out.println("Updating cells in batch . Default access = " + defaultAccess);
		//System.out.println("Changed cell data = " + sub);
		String xlcellval = null;
		String xlFormula = null;
		int xlRowIdx = -1;
		int xlColIdx = -1;
		int xlRowId = -1;
		int xlColId = -1;
		int cellChangeFlag = 1;
		int ci = 0;
		//int cj = sub.indexOf(Seperator);  // First substring //  JSON Changes
		int cj = 0; //  JSON Changes
		int ccount = 0;

		String query = " INSERT INTO BW_RC_STRING_VALUE VALUES(?, ?, ?, ?, ?, ?) ";

		PreparedStatement stmt = connection.prepareStatement(query);

		int batchSize = 10000;
		int batchCounter = 0;
		
		//  JSON Changes
		while (ci < sub.length)
		{
			//xlRowIdx = Integer.parseInt(sub.substring(ci, cj)); // 
			xlRowIdx = Integer.parseInt(sub[ci]);
			System.out.println("xlRowIdx = " + xlRowIdx);
			ci = ci + 1;
			//cj = sub.indexOf(Seperator, ci);
			//xlColIdx = Integer.parseInt(sub.substring(ci, cj));

			xlColIdx=Integer.parseInt(sub[ci]);
			System.out.println("xlColIdx = " + xlColIdx);
			ci = ci + 1;
			//cj = sub.indexOf(Seperator, ci);
			//xlcellval = sub.substring(ci, cj);
			xlcellval=sub[ci];
			System.out.println("xlcellval="+xlcellval);
			ci = ci + 1;
			//cj = sub.indexOf(Seperator, ci);
			
			//System.out.println("cj after celval"+cj);
			//if (cj == -1)
			if(ci== sub.length)
				//xlFormula = sub.substring(ci);
				xlFormula=sub[ci];
			else
				//xlFormula = sub.substring(ci, cj);
				xlFormula=sub[ci];
				
				System.out.println("xlFormula="+xlFormula);
			//  JSON Changes
			if (xlFormula.indexOf("=") < 0)
			{
				xlFormula = null;
			}
			System.out.println("xlcellval = " + xlcellval);

			//  JSON Changes
			ci = ci + 1;
			//cj = sub.indexOf(Seperator, ci);
			cellChangeFlag = Integer.parseInt(sub[ci]);
			System.out.println("cellChangeFlag="+cellChangeFlag);
			//  JSON Changes
			//xlRowId = ((Integer)rowIds.get(xlRowIdx)).intValue();
			xlRowId = ((Integer)rowIdHash.get(new Integer(xlRowIdx))).intValue();
			xlColId = ((Integer)columnIds.get(xlColIdx)).intValue();
			System.out.println("xlRowId="+xlRowId+"xlColId="+xlColId); //  JSON Changes
			int ColAcess = defaultAccess; // assuming column access is implemented in client
			if (accCols.get(new Integer(xlColId)) != null)
				ColAcess = ((Integer)accCols.get(new Integer(xlColId))).intValue();
			
			// override access from access table
			if (colCellAccess != null && colCellAccess.size() > 0)
			{ 
				Object cA = colCellAccess.get(new Integer(xlColId));
				if (cA != null){
					if (cA instanceof Integer)
					{
						ColAcess = ((Integer)cA).intValue();
						System.out.println("column acess for colid = " + xlColId + " is " + ColAcess);
					}
					else
					{
						String accessString = (String)cA;
						System.out.println(accessString);
						Pattern pattern = Pattern.compile("(\\d)(\\?.*)");
						Matcher matcher = pattern.matcher(accessString);
						System.out.println("match count = " + matcher.groupCount());
						if(matcher.matches())
						{
							int access = Integer.parseInt(matcher.group(1));
							String accessInstr = matcher.group(2);
							System.out.println("column access for colid = " + xlColId + " is " + access +
									" if row matches accessQuery = " + accessInstr);
							System.out.println("Otherwise using defaultAccess = " + defaultAccess);
							System.out.println(accessQueryXrowSet.toString());
							if (((HashSet) accessQueryXrowSet.get(accessInstr)).contains(new Integer(xlRowId)))
							{
								ColAcess = access;
								System.out.println("Using access = " + ColAcess + "for cell with rowId = " + xlRowId + "matching condition " + accessInstr);
							}
						}
					}
				}
			}
					
			// If anything other change other than value change/formula change (row added, column added etc)
			if (cellChangeFlag > 2 && xlRowId > 0 && xlColId > 0)
			{
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
				}

			}
			else if ((ColAcess == 2 && xlRowId > 0 && xlColId > 0) ||
					 (ColAcess == 1 && xlRowId > 0 && xlColId > 0 && xlFormula != null && cellChangeFlag == 1)) 
				// value or formula changed by user, cellChangeFlag = 1 or 2 ||
				// value changed by formula, cellChangeFlag = 1, access = 1
			{

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
				}
			}
			else if (ColAcess == 0 || ColAcess == 1)
			{
				if (ColAcess == 1)
				{
					System.out.println("processCells():Cell access violation " +
							"rowId=" + xlRowId + " colId=" + xlColId +
							"value=" + xlcellval +
							"frmla=" + xlFormula +
							"cellChangeFlag=" + cellChangeFlag);
					if (xlRowId != -1)
						xlErrorCells.add(new xlErrorNew(tableId, xlRowId, xlColId, 12016));
				}
				else
				{
					System.out.println("New Column without Access Right Added");
					if (xlColId != -1 || xlRowId != -1)
						xlErrorCells.add(new xlErrorNew(tableId, xlRowId, xlColId, 12016));
				}
			}
			ccount++;
			//  JSON Changes
			if (ci<sub.length)
			{
				ci = ci + 1;
				//cj = sub.indexOf(Seperator, ci);   // Rest of substrings
				//System.out.println("cj last if="+cj);
			}
			//  JSON Changes
		}
		//if(numCellsChanged > 0 && ExceptionDeleteRows == false && ExceptionAddRows == false && ExceptionAdministerColumns == false)
		if (batchCounter > 0) // the last batch
		{
			int[] rescnt = stmt.executeBatch();
			stmt.clearBatch();
			System.out.print(".");
		}
		stmt.close();
		stmt = null;
		query = null;
		if (numCellsChanged > 0)
		{
			if (isCritical <= 0)
			{
				if ((criticalLevel & (1 << 5)) == (1 << 5))
				{
					isCritical = 1;
					System.out.println("Transaction critical because cells changed");
				}
			}
		}
		System.out.println("Time to insert " + numCellsChanged + " rcsv for table= " + getElapsedTime());
		// update the cells based on the rcsv table
		query = "{CALL BW_UPD_CELL_FROM_RCSV(?,?,?,?)}";
		/*CallableStatement cstmt = connection.prepareCall(query);
		cstmt.setInt(1, tid);
		cstmt.setInt(2, importTid);
		cstmt.setInt(3, tableId);
		cstmt.setInt(4, userId);				
		int updCount = 0;
//		updCount = cstmt.executeUpdate();
		cstmt.close();
		cstmt = null;
System.out.println("tid " + tid);
System.out.println("importTid " + importTid);
System.out.println("tableId " + tableId);
System.out.println("userId " + userId);
		System.out.println("Time to update " + updCount + " cells for table= " + getElapsedTime());*/

}
	}
}
