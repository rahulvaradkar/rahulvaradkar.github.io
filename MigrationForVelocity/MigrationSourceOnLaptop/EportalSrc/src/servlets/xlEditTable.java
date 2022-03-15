package servlets;
/*
 *  This presents a list of collaboration available to a user
 * Added by RahulV on 08 October 2007
 */
import java.io.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import com.boardwalk.database.DatabaseLoader;
import com.boardwalk.exception.NeighborhoodException;
import com.boardwalk.exception.SystemException;
import com.boardwalk.collaboration.CollaborationManager;
import com.boardwalk.collaboration.Collaboration;
import com.boardwalk.collaboration.CollaborationTreeNode;
import com.boardwalk.whiteboard.WhiteboardManager;
import com.boardwalk.whiteboard.WhiteboardTreeNode;
import com.boardwalk.neighborhood.NeighborhoodManager;
import com.boardwalk.database.*;
import com.boardwalk.table.*;
import com.boardwalk.excel.*;
import com.boardwalk.query.*;
import com.boardwalk.user.UserManager;
import com.boardwalk.member.Member;
import com.boardwalk.exception.*;

import java.util.Date;
import java.sql.*;                  // JDBC package
import javax.sql.*;                 // extended JDBC packa

public class xlEditTable extends HttpServlet implements SingleThreadModel
{

	public final static String Seperator = new Character((char)1).toString();
	public final static String ContentDelimeter = new Character((char)2).toString();
	public final static String Diff_Seperator = new Character((char)3).toString();

	StringTokenizer st;
	int userId;
	String userName;
	String userPassword;
	int nhId;
	int  memberId;
	String nhName;
	int tableId;
	String tableName;
	String viewPref;
	int baselineId;
	String QueryPreference;
	String strShowChangesOnly;
	String strShowDiffPercent;
	String strShowDiffAbsolute;
	String strShowDiffNone;


	boolean ShowDiffPercent;
	boolean ShowDiffAbsolute;
	boolean ShowDiffNone;

	Date dt = null;
	String rowOwner = "";
	Hashtable ordColValues = null;

	int asOfTid = -1;
	int compTid = -1;
//	boolean trackState ;

	long asOfDate;
	long compDate ;

	long rowCount;
	long columnCount;
	long transactionId;
	long local_offset;
	long difference_in_MiliSec;

	boolean hideUnchangedRows;
	boolean hideNewRows  ;
	boolean hideDeletedRows ;
	boolean hideChangedRows ;
	boolean showDiffSideBySide ;
	boolean showStateChange ;

	String Color1 = "";

	TableContents tbcon;
	TableContents tbchg;
	TableInfo tbi;
	FormDefinition fd ;
	Hashtable  UIPreferences;
	Hashtable tablesUsingLkpForCol;

	xlError xle;

//	String m_period;
//	String m_StartDate;
//	String m_EndDate;

	HttpServletRequest req;
	HttpServletResponse res;

    public void service (HttpServletRequest request, HttpServletResponse response) 	throws ServletException, IOException
    {
		req = request;
		res = response;

		res.setContentType ( "text/plain");
		ServletOutputStream servletOut = res.getOutputStream ();
		BufferedReader br = request.getReader ();
		xle = null;
        StringBuffer sb = new 	StringBuffer ();
        BoardwalkMessages bwMsgs = new BoardwalkMessages();

		hideUnchangedRows = false;
		hideNewRows = false ;
		hideDeletedRows = false;
		hideChangedRows = false;
		showDiffSideBySide = false;
		showStateChange = false;

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
		System.out.println("Data from client" + buf);
		st = new StringTokenizer( buf );

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
		viewPref = wrkstr;

		wrkstr = st.nextToken (Seperator);
		tableId = Integer.parseInt(wrkstr);

		wrkstr = st.nextToken (Seperator);
		tableName = wrkstr;

//		wrkstr = st.nextToken (Seperator);
//		m_period = wrkstr;

//		wrkstr = st.nextToken (Seperator);
//		m_StartDate = wrkstr;

//		wrkstr = st.nextToken (Seperator);
//		m_EndDate = wrkstr;

		wrkstr = st.nextToken (Seperator);
		local_offset = Long.parseLong(wrkstr);


		wrkstr = st.nextToken (Seperator);
		baselineId  = Integer.parseInt(wrkstr);

		wrkstr = st.nextToken (Seperator);
		asOfTid = Integer.parseInt(wrkstr);

		wrkstr = st.nextToken (Seperator);
		asOfDate = Long.parseLong(wrkstr);

		wrkstr = st.nextToken (Seperator);
		compTid = Integer.parseInt(wrkstr);

		wrkstr = st.nextToken (Seperator);
		compDate = Long.parseLong(wrkstr);

		wrkstr = st.nextToken (Seperator);
		strShowChangesOnly = wrkstr;

		wrkstr = st.nextToken (Seperator);
		if (wrkstr.toUpperCase().equals("TRUE"))
		{
			hideUnchangedRows = true;
		}

		wrkstr = st.nextToken (Seperator);
		if (wrkstr.toUpperCase().equals("TRUE"))
		{
			hideNewRows = true;
		}

		wrkstr = st.nextToken (Seperator);
		if (wrkstr.toUpperCase().equals("TRUE"))
		{
			hideDeletedRows = true;
		}

		wrkstr = st.nextToken (Seperator);
		if (wrkstr.toUpperCase().equals("TRUE"))
		{
			hideChangedRows = true;
		}

		wrkstr = st.nextToken (Seperator);
		if (wrkstr.toUpperCase().equals("TRUE"))
		{
			showDiffSideBySide = true;
		}

		wrkstr = st.nextToken (Seperator);
		if (wrkstr.toUpperCase().equals("TRUE"))
		{
			showStateChange = true;
		}

		wrkstr = st.nextToken (Seperator);
		if (wrkstr.toUpperCase().equals("TRUE"))
		{
			ShowDiffPercent = true;
		}

		wrkstr = st.nextToken (Seperator);
		if (wrkstr.toUpperCase().equals("TRUE"))
		{
			ShowDiffAbsolute = true;
		}

		wrkstr = st.nextToken (Seperator);
		if (wrkstr.toUpperCase().equals("TRUE"))
		{
			ShowDiffNone = true;
		}

		System.out.println("++++++++++++++++++++ hideUnchangedRows " + hideUnchangedRows);
		System.out.println("++++++++++++++++++++ hideNewRows " + hideNewRows);
		System.out.println("++++++++++++++++++++ hideDeletedRows " + hideDeletedRows);
		System.out.println("++++++++++++++++++++ hideChangedRows " + hideChangedRows);
		System.out.println("++++++++++++++++++++ showDiffSideBySide " + showDiffSideBySide);
		System.out.println("++++++++++++++++++++ showStateChange " + showStateChange);
		System.out.println("++++++++++++++++++++ ShowDiffPercent " + ShowDiffPercent);
		System.out.println("++++++++++++++++++++ ShowDiffAbsolute " + ShowDiffAbsolute);
		System.out.println("++++++++++++++++++++ ShowDiffNone " + ShowDiffNone);

		Calendar cal_GMT = Calendar.getInstance(TimeZone.getTimeZone("GMT"));

		long server_Millis = cal_GMT.getTimeInMillis();

		difference_in_MiliSec = local_offset - server_Millis;

		System.out.println("Local Server (gmt) in miliSeconds is " + server_Millis );

		System.out.println("The difference in Server and Clietnis " + (local_offset - server_Millis ));
		
		
		System.out.println("Login User");
        if ( loginUser() )
        {
            int action;
  			System.out.println("user is valid");
			editTable();
		}
		else
		{
			System.out.println("user is invalid");
			String invalid = new String("userinvalid");
			res.setContentLength (invalid.length());
			servletOut.print(invalid);
		}

        servletOut.close ();
    }


    public void editTable()    throws ServletException, IOException
    {
		String reqColsCSV = null;
		String reqColsCompCSV = null;

//		System.out.println(" View preference from the browser " + viewPref );
//		System.out.println(" QueryPreference from the browser " + QueryPreference );
//		System.out.println(" baselineId " + baselineId );

		tbcon = null;
		tbchg = null;
        tbi = null;
		fd = null;

        Connection connection = null;
		UIPreferences = null;
        tablesUsingLkpForCol = null;

		reqColsCSV ="416,411,414,413,412,415,417,418,419,420,421,422,423,424,";
		
        try
        {


			DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
			connection = databaseloader.getConnection();
//			System.out.println(" sending request to get tablecontents asOf " + atid + ":" + asOfDate +
//								"comparing with " +
//								compTid + ":" + compDate);

			tbcon = TableManagerXL.getTableContents_t(  connection,
													  tableId,
													  userId,
													  memberId,
													  nhId,
													  baselineId,
													  viewPref,
													  QueryPreference,
													  new Vector(),
													  false,
													  -1,
													  10000000,
													  -1,
													  10000000,
													  asOfTid,
													  asOfDate,
													  -1,
													  -1,
													  reqColsCSV
													 );

			System.out.println("Got tbcon " + tbcon.getRowIds().size() + " rows " + tbcon.getColumnNames().size() + " columns ");

			if (compTid > -1 || compDate > -1)
			{
				tbchg = TableManagerXL.getTableContents_t(  connection,
														  tableId,
														  userId,
														  memberId,
														  nhId,
														  baselineId,
														  viewPref,
														  QueryPreference,
														  new Vector(),
														  false,
														  -1,
														  10000000,
														  -1,
														  10000000,
														  asOfTid,
														  asOfDate,
														  compTid,
														  compDate,
														  reqColsCompCSV
														 );
				System.out.println("Got tbchg  " + tbchg.getRowIds() + " rows " + tbchg.getColumnNames().size() + " columns ");
			}

			System.out.println(" sending request to get getTableInfo" );
			tbi = TableManager.getTableInfo(connection,userId,tableId);
			System.out.println(" got  request to get getTableInfo" );

			System.out.println(" sending request to get UIPreferences" );
			UIPreferences = TableManager.getTableActionUIValues( connection, tableId);
			System.out.println(" got  request to get UIPreferences" );
			int defTableId = FormManager.getDefinitionTable(connection, tableId, nhId);

			int criteriaTableId = TableViewManager.getCriteriaTable(connection, tableId, userId);


			 System.out.println(" sending request to get tablesUsingLkpForCol" );
			 tablesUsingLkpForCol = TableManager.getTablesUsingLookup(connection, tableId);
			 System.out.println(" got request to get tablesUsingLkpForCol" );

			 System.out.println("MyTables.editTable()::tablesUsingLkpForCol = " + tablesUsingLkpForCol);

			Vector rowids = new Vector();
			rowids = tbcon.getRowIds();
			System.out.println("++++++++++++++= number of rows = " + rowids.size());


			generateResponseBuffer();


        }
        catch ( BoardwalkException e )
        {
		   e.printStackTrace();
//           req.setAttribute("com.boardwalk.exception.BoardwalkException", e);

        }
        catch (SystemException s)
		{
			s.printStackTrace();
//		    req.setAttribute("com.boardwalk.exception.SystemException", s);
        }
        catch (SQLException sql)
		{
		 	sql.printStackTrace();
		   return;
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
	}		// end of editTable()    


	private void generateResponseBuffer() throws  IOException
	{

		StringBuffer responseToUpdate = new StringBuffer();
		String responseBuffer = "";

		responseToUpdate.append("Success" + ContentDelimeter);

		String formulaChangedComment;
		String strComment = "" ;

		boolean tableIsReadOnly = false;		// not required confirm xxxx
		boolean isTableLocked = false;
		boolean showFormulae = false;
		boolean showTableChanges = false;


		DeltaColumnConfiguration dcc = new DeltaColumnConfiguration();

		//int tableId = ((Integer)request.getAttribute("TableId")).intValue();	defined
		int noOfColumns = 0;

		//Integer baselineIdInteger = (Integer)request.getAttribute("baselineId");  defined
		if ( baselineId > 0 )
		{
			tableIsReadOnly = true;
		}

		if (asOfTid > 0)
		{
			tableIsReadOnly = true;
		}

		if ( tbcon!= null )
		{
			Vector vecNoOfcolumns = tbcon.getColumnNames();
			if ( vecNoOfcolumns != null )
			{
				noOfColumns = vecNoOfcolumns.size();
			}
		}

		boolean showChangesOnly = false;

		Hashtable cellsByRowId_tbchg = new Hashtable();
		Hashtable columns_tbchg = new Hashtable();
		if (tbchg != null)
		{
			cellsByRowId_tbchg = tbchg.getCellsByRowId();
			columns_tbchg = tbchg.getColumnsByColumnId();
			tableIsReadOnly = true;
			showTableChanges = true;
		}

		System.out.println("+++++++++++ showTableChanges = " + showTableChanges);

		if (strShowChangesOnly.toUpperCase().equals("TRUE"))
		{
			showChangesOnly = true;
		}

		System.out.println("+++++++++++ showChangesOnly = " + showChangesOnly);

		Transaction maxTableTransaction = null;
		String tableUpdatedBy = "";
		String tableUpdatedOn = "";

		//System.out.println("Now rendering the the table;");

		//String viewPref = (String)request.getAttribute("viewPref");
//		String QueryPreference =(String)request.getAttribute("QueryPreference");

		//Hashtable tablesUsingLkpForColumn = (Hashtable)request.getAttribute("TablesUsingLkpForCol");   defined
		////System.out.println("edit_table.jsp:: tablesUsingLkpForColumn = " + tablesUsingLkpForColumn);


		TableAccessList tbACL = null;
		Hashtable columns = new Hashtable();
		Vector columnNames = new Vector();
		Hashtable cellsByRowId =new Hashtable();
		Vector columnsSortedBySeqNum = new Vector();
		Vector  rowids = new Vector();
		Hashtable rowObjsById = null;

		if ( tbcon != null )
		{
			tbACL = tbcon.getTableAccessList();
			columns = tbcon.getColumnsByColumnId();
			columnNames = tbcon.getColumnNames();
			columnsSortedBySeqNum = tbcon.getColumnsSortedBySeqNum();

			//System.out.println(" column size " + columns.size() + " " + columnNames.size() + " " + columnNames.size() + " " );

			cellsByRowId = tbcon.getCellsByRowId();
			rowids = tbcon.getRowIds();

			rowObjsById = tbcon.getRowObjsByRowId();
			isTableLocked =  tbi.isLocked();

/*		to set 		tableIsReadOnly = true;		BLOCK IS REMOVED BY RPV  #223 TO 253 IN COPY_OF_EDIT_TABLE.JSP*/
		}

		// columns HIDDEN AND FILTERED COLUMNS IS REMOVED BY RPV   Ref. #268 tp 305 in IN COPY_OF_EDIT_TABLE.JSP

//		if ( isTableLocked && tbi.getLockedByUserId() != bws.userId.intValue()         )
//		{
//			tableIsReadOnly = true;
//		}

//Columnssss stareted here...................RPV
		String columnName ;
		if (columnsSortedBySeqNum.size() > 0)
		{
			//First column is for Row Added/Deleted/Upeated ETC
			responseToUpdate.append("" +  Seperator);	//RPV  
			for ( int columnIndex = 0; columnIndex< columnsSortedBySeqNum.size(); columnIndex++ )
			{
				Column c = (Column)columnsSortedBySeqNum.elementAt(columnIndex);;
				columnName = c.getColumnName();
				responseToUpdate.append(columnName +  Seperator);	//RPV

				if (showTableChanges == true && showDiffSideBySide == true)
				{
					Column c_tbchg = (Column)columns_tbchg.get(new Integer(c.getId()));
					if (c_tbchg != null)
					{
						responseToUpdate.append(columnName + " (old)" + Seperator); // RPV
					}
				}
			}

			responseToUpdate.append("Owned By" +  Seperator);	//RPV
			responseToUpdate.append("Updated By" +  Seperator);	//RPV
			responseToUpdate.append("Updated On" +  Seperator);	//RPV
			responseToUpdate.append("Comments" +  ContentDelimeter);	//RPV
	
		}

		int cellId = 0;
		int dispRows = 0;
		int processedRows = 0;
		if ( rowids.size() > 0 )
		{

			Transaction maxRowTransaction = null;
			String versionCellUserName  = "";
			String lastUpdate = "";

			System.out.println("Now of rows to process = " + rowids.size() );

			for ( int rowIndex=0; rowIndex < rowids.size() ; rowIndex++ )
			{
				//System.out.println("Processing next row");
				boolean upRow = false;
				boolean downRow = false;
				boolean noChangeRow = false;
				boolean deletedRow = false;
				boolean displayRow = true;
				boolean newRow = false;
				boolean changedRow = false;
				Integer a_rowIntegerId = (Integer)rowids.elementAt( rowIndex);
				Vector cv = null;
				Vector cells = null;
				//System.out.println("Processing row Id " + a_rowIntegerId);

				// see if the row is blank
				try
				{
					cv = (Vector)cellsByRowId.get(a_rowIntegerId);
					if (cv.size() == 0)
					{
						//System.out.println("Row Deactivated in current view");
						deletedRow = true;
					}
					else
					{
						//System.out.println("Row not deactivated in current view");
						cells = (Vector)cv.elementAt(0);
					}
				}
				catch (Exception e)
				{
					//e.printStackTrace();
				}

				// table changes
				Vector cv_tbchg = null;
				Vector cells_tbchg = null;
				boolean rowUnchanged = false;
				if (showTableChanges)
				{
					cv_tbchg = (Vector)cellsByRowId_tbchg.get(a_rowIntegerId);
					if (cv_tbchg != null)
					{
						//System.out.println("cv_tbchg != null");
						if (cv_tbchg.size() > 0) // status/value diff found in this row
						{
							//System.out.println("status/value diff found in this row");
							if (deletedRow == true)
							{
								if (hideDeletedRows == true)
								{
									System.out.println("++++++++++++++++++++++++++++ displayRow set False in hideDeleteRows check ");
									displayRow = false;
									continue;
								}
								cells_tbchg = (Vector)cv_tbchg.elementAt(0);
								//System.out.println("Row Active in earlier view.. was deleted along the way");
								cells = new Vector();
								//System.out.println("Need to add deleted for for " + cells.size() + "cells");
								if (columnsSortedBySeqNum.size() > 0)
								{
									//System.out.println("start creating deleted row for display");
									for ( int columnIndex = 0; columnIndex< columnsSortedBySeqNum.size(); columnIndex++ )
									{
										//System.out.println("Next column index");
										Column c = (Column)columnsSortedBySeqNum.elementAt(columnIndex);
										//try to find cell in old row with this column id
										VersionedCell foundCell = null;
										for (int cidx = 0; cidx < columns_tbchg.size(); cidx++)
										{
											//System.out.println("trying to find cell");
											try
											{
												VersionedCell cl = (VersionedCell)cells_tbchg.elementAt(cidx);
												if (cl.getColumnId() == c.getId())
												{
													//System.out.println("found one cell");
													foundCell = cl;
													break;
												}
											}
											catch (Exception e)
											{
												// do nothing, means cell was not changed , should catch ClassCastException
												// since the placeholder is new Integer(0)
												//e.printStackTrace();
											}
										}
										if (foundCell != null)
										{
											cells.addElement(foundCell);
											//System.out.println("added found cell to row");
										}
										else
										{
											cells.addElement(new Integer(0));
											//System.out.println("added dummy cell to row");
										}
									}

									//System.out.println("Finished creating deleted row for display purposes");
								}
							}
							else // change detected inside a row
							{
								//System.out.println("This row has changed");
								changedRow = true;
								if (hideChangedRows == true)
								{
									System.out.println("++++++++++++++++++++++++++++ displayRow set False in hideChangedRows check ");
									displayRow = false;
									continue;
								}
								cells_tbchg = (Vector)cv_tbchg.elementAt(0);
							}
						}
						else
						{
							if (deletedRow == true)
							{
								//System.out.println("Row not in earlier view either .. don't display");
								System.out.println("++++++++++++++++++++++++++++ displayRow set False in deletedRow == TRUE check ");
								displayRow = false;
								continue;
							}
							else
							{
								//System.out.println("Row is unchanged");
								rowUnchanged = true;
								if (hideUnchangedRows == true)
								{
									System.out.println("++++++++++++++++++++++++++++ displayRow set False in hideUnchangedRows check ");
									displayRow = false;
									continue;
								}
							}
						}
					}
					else // row not present in earlier view
					{
						newRow = true;
						//System.out.println("Row not in earlier view");
						if (hideNewRows == true)
						{
							System.out.println("++++++++++++++++++++++++++++ displayRow set False in hideNewRows check ");
							displayRow = false;
							continue;
						}
					}

					if (rowUnchanged == true && showChangesOnly == true)
					{
						//System.out.println("Row Unchanged");
						System.out.println("++++++++++++++++++++++++++++ displayRow set False in (rowUnchanged + showChangesOnly) check ");
						displayRow = false;
						continue;
					}
				}

				if (cells == null)
				{
					System.out.println("++++++++++++++++++++++++++++ displayRow set False in (cells == NULL) check ");
					displayRow = false;
					continue;
				}
				int rowId = ((Integer)rowids.elementAt(rowIndex)).intValue();
				Row row = (Row)rowObjsById.get( a_rowIntegerId );
				rowOwner = row.getOwnerName();
				// check if you need to filter out the row
				for ( int cellIndex=0; cellIndex < cells.size(); cellIndex++ )
				{
					VersionedCell cell = null;
					try
					{
						cell = (VersionedCell) cells.elementAt( cellIndex );
					}
					catch (Exception e)
					{
						continue;
					}
					//System.out.println("Processing filters");
					// if the column is filtered
/*
					Vector colFilters = (Vector)filterColumnIds.get(new Integer(cell.getColumnId()));
					if ( colFilters != null)
					{
						for (int iFil = 0; iFil < colFilters.size() ; iFil++)
						{
							String colFilterStr = (String)colFilters.elementAt(iFil);
							//System.out.println("Column id=" + cell.getColumnId() + " is filtered by the criterion : " + colFilterStr );
							StringTokenizer st = new StringTokenizer(colFilterStr, ":");
							String colFilterCondition = st.nextToken();
							String colFilterStrVal = st.nextToken();
							String columnType = cell.getType();
							if (columnType.equals("STRING") || columnType.equals("TABLE"))
							{
								boolean negativeMatch = false;
								Pattern pattern = null;

								if (colFilterCondition.equals("equals"))
								{
									pattern = Pattern.compile(colFilterStrVal);
									negativeMatch = false;
								}
								else if (colFilterCondition.equals("doesNotEqual"))
								{
									pattern = Pattern.compile(colFilterStrVal);
									negativeMatch = true;

								}
								else if (colFilterCondition.equals("beginsWith"))
								{
									pattern = Pattern.compile("^"+colFilterStrVal+"[\\s\\S]*");
									negativeMatch = false;
								}
								else if (colFilterCondition.equals("doesNotBeginWith"))
								{
									pattern = Pattern.compile("^"+colFilterStrVal+"[\\s\\S]*");
									negativeMatch = true;
								}
								else if (colFilterCondition.equals("endsWith"))
								{
									pattern = Pattern.compile("[\\s\\S]*"+colFilterStrVal+"$");
									negativeMatch = false;
								}
								else if (colFilterCondition.equals("doesNotEndWith"))
								{
									pattern = Pattern.compile("[\\s\\S]*"+colFilterStrVal+"$");
									negativeMatch = true;;
								}
								else if (colFilterCondition.equals("contains"))
								{
									pattern = Pattern.compile("[\\s\\S]*"+colFilterStrVal+"[\\s\\S]*");
									negativeMatch = false;
								}
								else if (colFilterCondition.equals("doesNotContain"))
								{
									pattern = Pattern.compile("[\\s\\S]*"+colFilterStrVal+"[\\s\\S]*");
									negativeMatch = true;
								}

								String cellValue = cell.getValueAsString();
								Matcher m = pattern.matcher(cellValue);
								boolean matchFound = m.find();
								if (negativeMatch == false)
								{
									if (matchFound == false )
									{
										displayRow = false;
									}
								}
								else
								{
									if (matchFound == true )
									{
										displayRow = false;
									}
								}
							}
							else if (columnType.equals("INTEGER") || columnType.equals("FLOAT"))
							{

								double cellValue ;
								double cellFilterValue;
								if (columnType.equals("INTEGER"))
								{
									Integer cellIntValue = new Integer(cell.getIntValue());
									cellValue = cellIntValue.doubleValue();
									Integer cellFilterIntValue = new Integer(colFilterStrVal);
									cellFilterValue = cellFilterIntValue.doubleValue();
								}
								else
								{
									Double cellDblValue = new Double(cell.getDoubleValue());
									cellValue = cellDblValue.doubleValue();
									Double cellFilterDblValue = new Double(colFilterStrVal);
									cellFilterValue = cellFilterDblValue.doubleValue();
								}

								//System.out.println("Checking if cell Value : " + cellValue + " is " + colFilterCondition + " " + cellFilterValue);

								if (colFilterCondition.equals("equals"))
								{
									if (cellValue != cellFilterValue)
									{
										displayRow = false;
									}
								}
								else if (colFilterCondition.equals("doesNotEqual"))
								{
									if (cellValue == cellFilterValue)
									{
										displayRow = false;
									}

								}
								else if (colFilterCondition.equals("isGreaterThan"))
								{
									if (cellValue <= cellFilterValue)
									{
										displayRow = false;
									}
								}
								else if (colFilterCondition.equals("isGreaterThanOrEqualTo"))
								{
									if (cellValue < cellFilterValue)
									{
										displayRow = false;
									}
								}
								else if (colFilterCondition.equals("isLessThan"))
								{
									if (cellValue >= cellFilterValue)
									{
										displayRow = false;
									}
								}
								else if (colFilterCondition.equals("isLessThanOrEqualTo"))
								{
									if (cellValue > cellFilterValue)
									{
										displayRow = false;
									}
								}
							}// else if
						}

						//see if the cell is OK or needs to be filtered out
						//if (!cell.getValueAsString().equals(colPreference)) {
						//	displayRow = false;
						//} //end if
					}// end if

*/  //THIS SI REQUIRED LATER
				}// end for

				// check for filtering in state column

				System.out.println(" ++++++++++++++++++ displayRow = " + displayRow);

				if (!displayRow) 
				{
					//System.out.println("Hiding row " + a_rowIntegerId);
					continue;
				}
				//System.out.println("Display this row");

				processedRows++;
				dispRows++;

/*				if (newRow == true)
				{
					responseToUpdate.append("color=green" +  Seperator);	//RPV
				}
				else if (deletedRow == true)
				{
					responseToUpdate.append("color=red" +  Seperator);	//RPV
				}
				else if (changedRow == true)
				{
					responseToUpdate.append("color=brown" +  Seperator);	//RPV
				}
*/
				if ( showTableChanges )
				{
					if ( newRow == true )
					{
						responseToUpdate.append("N" +  Seperator);	//RPV
					}
					else if (deletedRow == true)
					{
						responseToUpdate.append("D" +  Seperator);	//RPV
					}
					else if (changedRow == true)
					{
						responseToUpdate.append("U" +  Seperator);	//RPV
					}
					else
					{
						responseToUpdate.append("-" +  Seperator);	//RPV
					}
				}

	//////////////////////////////////////

				for ( int cellIndex=0; cellIndex < cells.size(); cellIndex++ )
				{
					//System.out.println("Processing Next Cell");
					VersionedCell cell = null;
					try
					{
						cell = (VersionedCell) cells.elementAt( cellIndex );
					}
					catch(Exception e)
					{
						continue;
					}
					VersionedCell oldCellValue = null;
					String oldFormula = null;
					String titleString = "";
					String cellFormula = cell.getFormula();
					boolean hasFormula = false;
					
					// System.out.println("FFFFFFFFFFFFFFFormula=" + cellFormula + ":" );
					if (cellFormula != null && !cellFormula.trim().equals(""))
						hasFormula = true;
					else
						cellFormula = null;
					
					boolean formulaChanged = false;
					boolean valueChanged = false;

					if ( showTableChanges )
					{
						//oldCellValue = (VersionedCell)tableConf.getCellsByCellId().get(  new Integer ( cell.getId() )    );
						if (changedRow == true)
						{
							try
							{
								oldCellValue = (VersionedCell)cells_tbchg.elementAt(cellIndex);
								oldFormula = oldCellValue.getFormula();
								String comp1 = cellFormula;
								String comp2 = oldFormula;
								if (comp1 == null)
									comp1 = "";
								if (comp2 == null)
									comp2 = "";
								//System.out.println("Comparing formulae " + comp1 + ":" + comp2);
								if (!comp1.equals(comp2))
								{
									formulaChanged = true;
									//System.out.println("Formula has changed");
								}
								else
								{
									formulaChanged = false;
									//System.out.println("Formula has not changed");
								}
								
								comp1 = oldCellValue.getValueAsString();
								comp2 = cell.getValueAsString();
								if (comp1 == null)
									comp1 = "";
								else
									comp1 = comp1.trim();
								
								if (comp2 == null)
									comp2 = "";
								else
									comp2.trim();
								//System.out.println("Comparing formulae " + comp1 + ":" + comp2);
								if (!comp1.equals(comp2))
								{
									valueChanged = true;
									//System.out.println("Formula has changed");
								}
								else
								{
									valueChanged = false;
									//System.out.println("Formula has not changed");
								}
							}
							catch (Exception e)
							{
								//e.printStackTrace();
								// do nothing, means cell was not changed , should catch ClassCastException
								// since the placeholder is new Integer(0)
							}
						}

						// need attn by RPV xxxxxxxxx
						//ordColValues = (Hashtable)request.getAttribute("OrdCol" + cell.getColumnId());
					}

					if (maxRowTransaction == null)
					{
						maxRowTransaction = cell.getTransaction();
					}
					else if ( maxRowTransaction.getId() < cell.getTransaction().getId() )
					{
						maxRowTransaction = cell.getTransaction();
					}

					// max table transaction
					if (maxTableTransaction == null)
					{
						maxTableTransaction = cell.getTransaction();
					}
					else if ( maxTableTransaction.getId() < cell.getTransaction().getId() )
					{
						maxTableTransaction = cell.getTransaction();
					}

					if (! ((Column)columns.get(  new Integer(cell.getColumnId()))).getIsEnumerated()   )
					{
						boolean canWrite = ((Column)columns.get(  new Integer(cell.getColumnId()))).canWrite();
						//System.out.println("canWrite = " + canWrite);

						Color1 = valueChanged?"pink\n":canWrite?"lightblue\n":hasFormula?"#ffffff\n":"";
		//		<%=valueChanged?"bgcolor='pink'":""%>  <%=canWrite?"":"bgcolor='lightblue' "%> <%=hasFormula?"bgcolor='#ffffff' ":""%>>

						if ( showTableChanges )
						{
							strComment = "";
							formulaChangedComment = "";
							if  ( oldCellValue != null &&  valueChanged == true )
							{

								Date d = new Date(cell.getTransaction().getCreatedOnTime());
								strComment = cell.getTransaction().getCreatedByUserAddress() + ", " + d.toString() + ":\n" + "changed value from " + oldCellValue.getValueAsString() + " to " + cell.getValueAsString() ;
								//formatDateTime(d,"NNN dd, yyyy hh:mm:ssa")
								//Comment in Cell
								//responseToUpdate.append(Diff_Seperator + "\nColor="+Color1 + " " + strComment + "\n" + Diff_Seperator);	//RPV
								responseToUpdate.append(Diff_Seperator + strComment +  Diff_Seperator);	//RPV
							}
							if (formulaChanged == true)
							{

								if ( oldCellValue.getFormula() != null )
								{
									if (!strComment.equals(""))
									{
										formulaChangedComment =  Diff_Seperator + "\n and the formula changed from " + oldCellValue.getFormula() + " to " + cellFormula + "\n" + Diff_Seperator;
									}
									else
									{
										formulaChangedComment =  Diff_Seperator + "The formula changed from " + oldCellValue.getFormula() + " to " + cellFormula + "\n" + Diff_Seperator;
									}

									responseToUpdate.append(formulaChangedComment);	//RPV
								}
								else
								{
									if (!strComment.equals(""))
									{
										formulaChangedComment = Diff_Seperator + "\n and new formula =" + cellFormula  + "\n" + Diff_Seperator;
									}
									else
									{
										formulaChangedComment = Diff_Seperator + "New formula =" + cellFormula  + "\n" + Diff_Seperator;
									}

									responseToUpdate.append(formulaChangedComment);	//RPV
								}
							}
						}
						//This statement prints all values   xxxxxxxxxxxxxxxxxxxxx
						responseToUpdate.append(cell.getValueAsHtmlString());	//RPV

						if (oldCellValue != null )
						{
							if (!ShowDiffNone)		//(ShowDiffNone == null)
							{
								//System.out.println(" See if you need to show numeric differences");
								DeltaValue dv = dcc.getDiff(cell,oldCellValue);
								if (dv != null && !dv.difference.equals(""))
								{
									//System.out.println("Found numeric difference " + dv.difference);
									double dd = dv.getNewDblValue();
									double dp = 0;
									String diffcolor = "green";
									if (dv.getOldDblValue() != 0)
									{
										if ( (dv.getNewDblValue() - dv.getOldDblValue()) < 0.0)
										{
											diffcolor = "red";
											dp = ( dv.getOldDblValue() - dv.getNewDblValue())/dv.getOldDblValue();
										}
										else
										{
											if ( dv.getNewDblValue() != 0 )
											{
												 dp = dd/dv.getOldDblValue();
											}
										}
									}

									if (  dv.getNewDblValue() < 0 && dv.getOldDblValue() == 0 )
									{
										diffcolor = "red";
									}

									if (ShowDiffPercent) 
									{
										String dpStr = "";
										if ( dv.getOldDblValue() != 0 && dv.getNewDblValue() != 0 )
										{
											java.text.NumberFormat nf = java.text.NumberFormat.getPercentInstance();
											nf.setMaximumFractionDigits(2);
											if ( dp < 0 )
											{
											  dp = dp-dp-dp;
											}
											dpStr = nf.format(dp);
										}
										else if ( dv.getNewDblValue() == 0 )
										{
											dpStr = "previous value "+dv.getOldDblValue();
										}
										else
										{
											dpStr = "previous value 0";
										}
										
										responseToUpdate.append("\ncolor=" + diffcolor + " " + dpStr );	//RPV
									}
									else if (ShowDiffAbsolute)
									{
										responseToUpdate.append("\ncolor=" + diffcolor + " " + dv.difference );	//RPV
									}
								}
								//System.out.println("showed changes inline");
							}

							if (ordColValues != null)// track state
							{
								//System.out.println("Show lifecycle changes");
								Integer seq1 = (Integer)ordColValues.get(oldCellValue.getStringValue());
								Integer seq2 = (Integer)ordColValues.get(cell.getStringValue());
								if (seq1 != null && seq2 != null)
								{
									if (seq1.intValue() > seq2.intValue()) // up
									{
										responseToUpdate.append("\ncolor=greenDot" + " " + "State moved up" );	//RPV
									}
									else // down
									{
										responseToUpdate.append("\ncolor=readDot" + " " + "State moved down" );	//RPV
									}
								}
							}
						} // show inline changes

						if (showTableChanges == true && showDiffSideBySide == true)
						{
							//System.out.println("show differences side by side");
							// see if the column is required to display the changed cells
							// rpv displays OLD VALUE IN NEXT CELL by rpv
							if (oldCellValue != null)   
								responseToUpdate.append( Seperator + oldCellValue.getValueAsString() );	//RPV
							else
								responseToUpdate.append(Seperator + "" );	//RPV
						}
					}
					else if (((Column)columns.get(new Integer(cell.getColumnId()))).getIsEnumerated())
					{
						//if ( showTableChanges  )
						//{
							//oldCellValue = (VersionedCell)tableConf.getCellsByCellId().get(  new Integer ( cell.getId() )    );
						//	if  ( oldCellValue != null )
						//	{
								//<img  style="display:inline;"  src="images/track.gif"  height=12 width=12 >
						//	}
						//}

						// xxxxxxxxxxxxxxxxxxx important this displays current value
						responseToUpdate.append(cell.getValueAsString() + Seperator );	//RPV

						if (showTableChanges == true && showDiffSideBySide == true)
						{
							//System.out.println("show differences side by side");
							// see if the column is required to display the changed cells

							if (oldCellValue != null && !oldCellValue.getValueAsString().equals(""))
								responseToUpdate.append("\ncolor=pink\n" + oldCellValue.getValueAsString() + Seperator );	//RPV
							else
								responseToUpdate.append(""  + Seperator );	//RPV
						}
					}

					responseToUpdate.append(Seperator);	//RPV
					//System.out.println("Finished Processing this cell");
					cellId++;
				}//for ( int cellIndex=0; ....

				//dt = new Date(maxRowTransaction.getCreatedOnTime() );		// RPV

				responseToUpdate.append(rowOwner + Seperator + maxRowTransaction.getCreatedByUserAddress() + Seperator +  (maxRowTransaction.getCreatedOnTime() + difference_in_MiliSec) + Seperator );	//RPV	

				if (maxRowTransaction.getComment()!=null)
					responseToUpdate.append(maxRowTransaction.getComment() ); //RPV

				responseToUpdate.append(ContentDelimeter);	//RPV

				//System.out.println(" responseToIpdate is +++++++++++ " + responseToUpdate);
				
				maxRowTransaction = null;

			} // for int rowIndex=0 ....
		}//end if rowids.size() > 0

		System.out.println("Done processing rows");

/*   NOT REQUIRED CONFIRM RPV
		if(maxTableTransaction != null)
		{
			rowOwner = maxTableTransaction.getCreatedByUserAddress();  //RPV
			dt = new Date(maxTableTransaction.getCreatedOnTime());  //RPV

			responseToUpdate.append(rowOwner + Seperator + maxTableTransaction.getCreatedByUserAddress() + Seperator +  dt.toString() + Seperator );	//RPV	

			if (maxTableTransaction.getComment()!=null)
				responseToUpdate.append(maxTableTransaction.getComment() ); //RPV

			responseToUpdate.append(ContentDelimeter);	//RPV
		}
*/

		//System.out.println(" responseToIpdate is +++++++++++ " + responseToUpdate);

		responseBuffer = responseToUpdate.toString();
		//System.out.println("Response = " + responseBuffer);
		System.out.println("Response length= " + responseBuffer.length());
		res.setContentLength ( responseBuffer.length() );
		res.getOutputStream().print(responseBuffer);

	} // END OF FUNCTION


    public boolean  loginUser()
	{

//		wrkstr = st.nextToken (Seperator);
//		nhName =wrkstr;

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
				System.out.println("Authenticating User : " + userName + ":" + userPassword);

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
