package servlets;
/*
 * The Whiteboard contains a list of tables to collaborate with
 * It provides methods to create new tables as well as to delete
 * and edit existing tables
 */
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
import boardwalk.table.*;
import com.boardwalk.database.*;
import com.boardwalk.user.UserManager;
import com.boardwalk.query.*;
import com.boardwalk.neighborhood.NeighborhoodManager;
import com.boardwalk.util.*;

import org.apache.commons.fileupload.*;
import org.apache.commons.fileupload.disk.*;
import org.apache.commons.fileupload.servlet.*;
//import org.apache.commons.io.output.*;
import javax.activation.MimetypesFileTypeMap;

import java.sql.*;                  // JDBC package
import javax.sql.*;                 // extended JDBC package

public class MyTables  extends BWServlet
{
    ServletContext sc;
    HttpServletRequest req;
    HttpServletResponse res;
    String tableName;
    int	tableId;
    Hashtable rows;
    String userEmailAddress;

    public void doPost (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
		sc = getServletContext();
		req = request;
		res = response;
		tableName = new String();
		rows = new Hashtable();

		String tableCreateAction = (String)request.getParameter("action");

		if (tableCreateAction.equals("getBlob"))
		{}// Do nothing
		else{
			setSession(request, response);
		}

//		setSession(request, response);

		 // check the action requested
		System.out.println("MyTables.java:doPost()->action = " + tableCreateAction);
		System.out.println("memberId is " + memberid + " nh id is " + nhId);

		if (tableCreateAction == null) // multi-part request
		{
			commitDocumentToCell();
		}
		else if (tableCreateAction.equals("createTable"))
		{
		// createTable presents the user with page to prompt basic info
		// to create a table in the database

		createTable();

		} else if (tableCreateAction.equals("commitTable")) {
		// commitTable uses the inputs from the user to create a
			// a new table with no rows or columns. After commiting
		// the table it takes the user back to the list of tables
			// from where he may edit the table
		tableCommit();
		}
		else if (tableCreateAction.equals("copyTable")) {
			copyTable();
		}
		else if (tableCreateAction.equals("editTable")) {
		// user calls to edit table passing in the table id
		editTable();

		}
		else if (tableCreateAction.equals("viewTable")) {
		// user calls to edit table passing in the table id
		viewTable();
		}
		else if (tableCreateAction.equals("removeTable")) {
		// user calls to edit table passing in the table id
		removeTable();
		} else if (tableCreateAction.equals("selectTable")) {
		// user calls to select table
		selectTable();
		}
		else if (tableCreateAction.equals("selectTableForLookup")) {
		// user calls to select table
		selectTableForLookup();

		}
		else if (tableCreateAction.equals("updateTableCell")) {
		// update table cell
		updateTableCell();
		} else if (tableCreateAction.equals("addRow")) {
		// add a row to the table you are editing
		addRow();
		} else if (tableCreateAction.equals("editRow")) {
		// add a row to the table you are editing
		editRow();
		} else if (tableCreateAction.equals("commitRow")) {
		// add a row to the table you are editing
		commitRow();
		} else if (tableCreateAction.equals("dataForm")) {
			System.out.println("DATA FORM MODE");
			dataForm();
		}else if (tableCreateAction.equals("commitCells")) {
		// add a row to the table you are editing
		commitCells();

		}  else if (tableCreateAction.equals("commitTableEdits")) {
		// add a row to the table you are editing
		commitTableEdits();
		}
		else if (tableCreateAction.equals("bwsFormat")) {
						// user calls to edit table passing in the table id
			bwsFormat();
		}
		else if (tableCreateAction.equals("deleteRow")) {
		// add a row to the table you are editing
		purgeRow();
		} else if (tableCreateAction.equals("addColumn")) {
		addColumn();
		} else if (tableCreateAction.equals("commitColumnAndDone")) {
		// Commit the column and go back to the table editor
		commitColumn(false);
		} else if (tableCreateAction.equals("commitColumnAndAddAnother")) {
		// Commit a column and continue to add another column
		commitColumn(true);
		} else if (tableCreateAction.equals("Done")) {
		// Done should take you back to the whiteboard screen that lists
		// all the tables
		editTableDone();
		}
		else if ( tableCreateAction.equals("openTableBaseline"))
		{
			openTableBaseline();
		}
		//else if (tableCreateAction.equals("sendQuery")) {
			// Commit a column and continue to add another column
			//System.out.println("sendQuery");
			//queryTable();
		//}
		else if (tableCreateAction.equals("displayInPlainHTML")) {
			// Commit a column and continue to add another column
			System.out.println("displayInPlainHTML");
			displayInPlainHTML();
		}
		else if (tableCreateAction.equals("chooseNhForTableContents"))
		{
			// Commit a column and continue to add another column
			System.out.println("chooseNhForTableContents");
			chooseNhForTableContents();
		}
		else if (tableCreateAction.equals("getDesignValues")) {
			// Commit a column and continue to add another column
			System.out.println("getDesignValues");
			editTable();
		}
		else if (tableCreateAction.equals("editTableAccess")) {
			// Commit a column and continue to add another column
			System.out.println("editTableAccess");
			editTableAccess();
		}
		else if (tableCreateAction.equals("editTableAdmin")) {
			// Commit a column and continue to add another column
			System.out.println("editTableAdmin");
			editTableAdmin();
		}
		else if (tableCreateAction.equals("commitTableAccess")) {
			// Commit a column and continue to add another column
			System.out.println("commitTableAccess");
			tableAccessCommit();
		}
		else if (tableCreateAction.equals("editColumnAccess")) {
			// Commit a column and continue to add another column
			System.out.println("editColumnAccess");
			editColumnAccess();
		}
		else if (tableCreateAction.equals("commitColumnAccess")) {
			// Commit a column and continue to add another column
			System.out.println("commitColumnAccess");
			commitColumnAccess();
		}
		else if (tableCreateAction.equals("addColumnAccess")) {
			// Commit a column and continue to add another column
			System.out.println("addColumnAccess");
			addColumnAccess();
		}
		else if (tableCreateAction.equals("deleteColumnAccess")) {
			// Commit a column and continue to add another column
			System.out.println("deleteColumnAccess");
			deleteColumnAccess();
		}
		else if (tableCreateAction.equals("editTableUIPreferences")) {
			// Commit a column and continue to add another column
			System.out.println("editTableUIPreferences");
			editTableUIPreferences();
		}
		else if (tableCreateAction.equals("commitTableUIPreferences")) {
			// Commit a column and continue to add another column
			System.out.println("commitTableUIPreferences");
			commitTableUIPreferences();
		}
		else if (tableCreateAction.equals("commitTableProperties")) {
			// Commit a column and continue to add another column
			System.out.println("commitTableProperties");
			commitTableProperties();
		}
		else if (tableCreateAction.equals("commitCopyTable")) {
			// Commit a column and continue to add another column
			System.out.println("commitCopyTable");
			commitCopyTable();
		}
		else if (tableCreateAction.equals("deleteColumn")) {
		// Delete a column
		deleteColumn();
		}
		else if (tableCreateAction.equalsIgnoreCase("switchCurrentMembership"))
		{
			switchCurrentMembership(req,res);
		}
		else if (tableCreateAction.equalsIgnoreCase("changeOwnershipForm"))
		{
			changeOwnershipForm();
		}
		else if (tableCreateAction.equalsIgnoreCase("ChangeRowOwnership"))
		{
			changeRowOwnership();
		}
		else if (tableCreateAction.equalsIgnoreCase("lockTable"))
		{
			lockTable();
		}
		else if (tableCreateAction.equalsIgnoreCase("unlockTable"))
		{
			unlockTable();
		}
		else if (tableCreateAction.equals("updateColumn"))
		{
			updateColumn();
		}
		else if (tableCreateAction.equals("showLkpReferences"))
		{
			showLookupColumnReferences();
		}
		else if (tableCreateAction.equals("addRowViaFormRequest"))
		{
			addRowViaFormRequest();
		}
		else if (tableCreateAction.equals("addRowUsingAForm"))
		{
			addRowUsingAForm();
		}
		else if (tableCreateAction.equals("commitNewRow"))
		{
			commitNewRow();
		}
		else if (tableCreateAction.equals("getTransactions"))
		{
			getTransactions();
		}
		else if (tableCreateAction.equals("getCellVersions"))
		{
			getCellVersions();
		}
		else if (tableCreateAction.equals("addDocumentToCell"))
		{
			addDocumentToCell();
		}
		else if (tableCreateAction.equals("getBlob"))
		{
			getBlob();
		}
		else if (tableCreateAction.equals("getRowVersions"))
		{
			getRowVersions();
		}

	}

	public void getBlob()throws ServletException, IOException
    {
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

    public void addDocumentToCell()throws ServletException, IOException
    {
		sc.getRequestDispatcher("/jsp/collaboration/tables/add_doc.jsp"). forward(req,res);
	}

    public void commitDocumentToCell()throws ServletException, IOException
    {
		int selectedCellId = -1;
		int tableId = -1;
		int wbid = -1;
		String ViewPreference = null;
		String textToDisplay = "Click Here";
		String screenTip = "Click here to fetch the document";
		String ext = "";
		long fileSize = 0;
		InputStream in = null;
		String fileName = null;
		String contentType = null;

		boolean isMultipart = FileUpload.isMultipartContent(req);
		// Create a factory for disk-based file items
		FileItemFactory factory = new DiskFileItemFactory();

		// Create a new file upload handler
		ServletFileUpload upload = new ServletFileUpload(factory);
		try
		{
			// Parse the request
			List /* FileItem */ items = upload.parseRequest(req);
			// Process the uploaded items
			Iterator iter = items.iterator();
			while (iter.hasNext()) {
				FileItem item = (FileItem) iter.next();

				if (item.isFormField()) {
					String name = item.getFieldName();
					String value = item.getString();

					System.out.println("FileUpload: name = "+ name + " value = " + value);

					if (name.equals("selectedCellId"))
					{
						selectedCellId = Integer.parseInt(value);
					}
					else if(name.equals("tableId"))
					{
						tableId = Integer.parseInt(value);
					}
					else if(name.equals("wbid"))
					{
						wbid = Integer.parseInt(value);
					}
					else if(name.equals("ViewPreference"))
					{
						ViewPreference = value;
					}
					else if(name.equals("textToDisplay"))
					{
						textToDisplay = value;
					}
					else if(name.equals("screenTip"))
					{
						screenTip = value;
					}
				} else {
					fileName = item.getName();
					contentType = sc.getMimeType(fileName);
					fileSize = item.getSize();

					System.out.println("Uploading file = " + fileName);
					if (contentType != null)
					{
						System.out.println("content-type = " + contentType);
					}
					else
					{
						System.out.println("content-type = null");
					}
					int dotPlace = fileName.lastIndexOf ( '.' );

					if ( dotPlace >= 0 )
					{
						// possibly empty
						ext = fileName.substring( dotPlace + 1 );
					}
					else
					{
						ext = "";
					}
					in = item.getInputStream();
				}
			}
		}
		catch (FileUploadException fue)
		{
			fue.printStackTrace();
		}

		Connection connection = null;
		TransactionManager tm = null;
		try
		{
            DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
            connection = databaseloader.getConnection();
            tm = new TransactionManager(connection, userId);
            int tid = tm.startTransaction();
			BlobManager.addDocumentToCell(
							connection,
							tid,
							in,
							(int)fileSize,
							fileName,
							ext,
							contentType,
							"",
							selectedCellId,
							textToDisplay,
							screenTip
							 );
			TableManager.updateUserExportTid(connection, tableId, userId, tid);
			tm.commitTransaction();
			in.close();

			String redirectURL = "/MyTables?tableId="+tableId+"&wbid="+wbid+"&ViewPreference="+ViewPreference+"&action=editTable";
			res.sendRedirect(req.getContextPath() + redirectURL);
		}
        catch ( Exception e )
        {
           e.printStackTrace();
           try
           {
                tm.rollbackTransaction();
           }
           catch( SQLException sqlfatal )
           {
               sqlfatal.printStackTrace();
           }
        }
        finally
        {
            try
            {
				if ( connection != null )
					connection.close();
            }
            catch ( Exception e )
            {
                e.printStackTrace();
            }
        }

	}

	public void bwsFormat()
	throws ServletException, IOException
	{

		tableId = Integer.parseInt(req.getParameter("tableId"));
		String ViewPreference = req.getParameter("ViewPreference");
		String QueryPreference = req.getParameter("QueryPreference");
		String  baselineIdStr =  req.getParameter("baselineId");
		String formMode = req.getParameter("formMode");

		System.out.println(" baselineIdStr " + baselineIdStr );

		int baselineId = -1;
		if ( baselineIdStr != null )
		{
			baselineId = Integer.parseInt( baselineIdStr );
		}

		System.out.println(" View preference from the browser " + ViewPreference );
		System.out.println(" QueryPreference from the browser " + QueryPreference );
		System.out.println(" baselineId " + baselineId );

		if ( ViewPreference == null )
		ViewPreference = "";

		if ( QueryPreference == null  || QueryPreference.trim().equals("") )
			QueryPreference = QueryPreferenceType.ROWS_BY_ROW_SEQ_ID;

		ViewPreference = ViewPreference.trim();
		QueryPreference = QueryPreference.trim();

		req.setAttribute("TableId", new Integer(tableId));

		tableName = "Unknown Table Name TBCh";

		req.setAttribute("TableName", tableName);
		req.setAttribute("title", "Table Details");
		TableContents tbcon = null;
		TableInfo tbi = null;
		Connection connection = null;
		Hashtable  UIPreferences = null;
		Hashtable tablesUsingLkpForCol = null;
		FormDefinition fd = null;
		try
		{
			DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
			connection = databaseloader.getConnection();
			System.out.println(" sending request to get tablecontents" );
			tbcon = TableManager.getTableContents(connection,
											  tableId,
											  userId,
											  memberid,
											  nhId,
											  baselineId,
											  ViewPreference,
											  QueryPreference,
											  new Vector(),
											  false,
												-1,
												10000000,
												-1,
												10000000
											  );


			System.out.println(" sending request to get getTableInfo" );
			tbi = TableManager.getTableInfo(connection,userId,tableId);
			System.out.println(" got  request to get getTableInfo" );

			// String buffer = com.boardwalk.transformers.BoardwalkBuffer.transformTableContents( userId, memberid, nhId, tableId, ViewPreference, QueryPreference,  tbcon	);

			String buffer = "a";
			res.setContentType("application/xlet");
			res.setHeader("Content-Disposition", "filename=" + "tbl.xlet");
			res.setContentLength( buffer.length());
			ServletOutputStream so = res.getOutputStream();
			so.print(buffer);
			so.flush();
			so.close();

			//java.io.PrintWriter pw = res.getWriter();
			//pw.print(buffer);
		}
		catch ( BoardwalkException e )
		{
		   e.printStackTrace();
		   req.setAttribute("com.boardwalk.exception.BoardwalkException", e);

		}
		catch (SystemException s)
		{
			s.printStackTrace();
			req.setAttribute("com.boardwalk.exception.SystemException", s);
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
	}



	public void getCellVersions() throws ServletException, IOException
	{

		long endDate;
		long startDate;

		String endDateStr = req.getParameter("endDate");
		String startDateStr = req.getParameter("startDate");
		java.util.Date d = new java.util.Date();
		endDate = d.getTime();
		startDate = 0;

		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		cal.setTime(d);

		String period = req.getParameter("period");
		if (period.equals("Week"))
		{
			cal.add(Calendar.DATE, -7);
			startDate = cal.getTime().getTime();
		}
		else if (period.equals("Month"))
		{
			cal.add(Calendar.MONTH, -1);
			startDate = cal.getTime().getTime();
		}
		else if (period.equals("Quarter"))
		{
			cal.add(Calendar.MONTH, -3);
			startDate = cal.getTime().getTime();
		}
		else if (period.equals("Year"))
		{
			cal.add(Calendar.YEAR, -1);
			startDate = cal.getTime().getTime();

		}
		else if (period.equals("Custom") && endDateStr != null && startDateStr != null)
		{
			endDate = Long.parseLong(endDateStr);
			startDate = Long.parseLong(startDateStr);
		}

		int cellId = Integer.parseInt(req.getParameter("selectedCellId"));
		int rowId = -1;
		int colId = -1;

		if ( cellId < 1  )
		{
			rowId = Integer.parseInt(req.getParameter("selectedRowId"));
			colId = Integer.parseInt(req.getParameter("selectedColumnId"));
		}


		Connection connection = null;
		try
		{
			DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
			connection = databaseloader.getConnection();
			Hashtable cellVersions = TableManager.getCellVersions( connection,
																      cellId,
																  	  rowId,
																  	  colId,
																      startDate,
																      endDate);
			System.out.println("Number of cell versions between startdate = "  + startDate + " end date = " + endDate + " = " + cellVersions.size());
			req.setAttribute("cellVersions", cellVersions);
			req.setAttribute("startDate", new Long(startDate));
			req.setAttribute("endDate", new Long(endDate));
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

		sc.getRequestDispatcher("/jsp/collaboration/tables/cellVersions.jsp"). forward(req,res);
	}

	public void getTransactions() throws ServletException, IOException
	{
		HttpSession hs = req.getSession(true);
		int userId = ((Integer)hs.getAttribute("userId")).intValue();
		int nhId = ((Integer)hs.getAttribute("nhId")).intValue();
		int tableId = Integer.parseInt(req.getParameter("tableId"));
		String stidStr = req.getParameter("stid");
		String etidStr = req.getParameter("etid");
		String sdateStr = req.getParameter("startDate");
		String edateStr = req.getParameter("endDate");
		String viewPref = req.getParameter("ViewPreference");
		int stid = -1;
		int etid = -1;
		java.util.Date d = new java.util.Date();
		long sDate = 0;
		long eDate = d.getTime();

		if (stidStr != null)
		{
			stid = Integer.parseInt(stidStr);
		}
		if (etidStr != null)
		{
			etid = Integer.parseInt(etidStr);
		}
		if (sdateStr != null)
		{
			sDate = Long.parseLong(sdateStr);
		}

		if (edateStr != null)
		{
			eDate = Long.parseLong(edateStr);
		}

		String period = req.getParameter("period");

		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		cal.setTime(d);
		if (period != null)
		{
			if (period.equals("Week"))
			{
				cal.add(Calendar.DATE, -7);
				sDate = cal.getTime().getTime();
			}
			else if (period.equals("Month"))
			{
				cal.add(Calendar.MONTH, -1);
				sDate = cal.getTime().getTime();
			}
			else if (period.equals("Quarter"))
			{
				cal.add(Calendar.MONTH, -3);
				sDate = cal.getTime().getTime();
			}
			else if (period.equals("Year"))
			{
				cal.add(Calendar.YEAR, -1);
				sDate = cal.getTime().getTime();

			}
		}
		Connection connection = null;
		try
		{
			DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
			connection = databaseloader.getConnection();
			Hashtable transactionList = TableManager.getTransactionList(connection,
																      tableId,
																      stid,
																      etid,
																      sDate,
																      eDate,
																      userId,
																      nhId,
																      viewPref,
																      true);
			System.out.println("Number of transactions = " + transactionList.size());
			req.setAttribute("transactionList", transactionList);
			System.out.println("stid = " + stid);
			System.out.println("etid = " + etid);

			try
			{

				if ( stid > -1 )
				{
					sDate =  TransactionManager.getTransactionTime(connection,stid);
				}

				if ( etid > -1 )
				{
					eDate =  TransactionManager.getTransactionTime(connection,etid);
				}
			}
			catch( Exception e )
			{

			}
			req.setAttribute("startDate", new Long(sDate));
			req.setAttribute("endDate", new Long(eDate));

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

		sc.getRequestDispatcher("/jsp/collaboration/tables/transactionList.jsp"). forward(req,res);
	}

	public void showLookupColumnReferences()throws ServletException, IOException
    {

		HttpSession hs = req.getSession(true);
		int memberId = ((Integer)hs.getAttribute("memberId")).intValue();
		int userId = ((Integer)hs.getAttribute("userId")).intValue();

		Connection connection = null;

		int lkpTableId = Integer.parseInt(req.getParameter ("tableId"));
		int lkpColumnId = Integer.parseInt(req.getParameter ("lkpColumnId"));
		String lkpColumnName = req.getParameter("lkpColumnName");
		String lkpValue = req.getParameter("lkpValue");
		if ( lkpValue == null )
			lkpValue = "";

		Hashtable ht = null;
		Vector vtulc = null;
		Vector TablesWithAccess = new Vector();
		Hashtable tablesAccessChecked = new Hashtable();

		try
		{
			DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
			connection = databaseloader.getConnection();

			// Since the column was already identified as being used for lookup, these should not be null..
			ht = TableManager.getTablesAndColumnsUsingLookup(connection, lkpTableId,lkpColumnId);
			// get tables for the selected column
			vtulc = (Vector)ht.get(new Integer(lkpColumnId));

			//let us check the access control

			if ( vtulc.size() > 0 )
			{
				for ( int v = 0; v < vtulc.size(); v++ )
				{
					TablesUsingLkpColumn tbulc = (TablesUsingLkpColumn)vtulc.elementAt(v);

					if (   tablesAccessChecked.get ( new Integer( tbulc.getTableUsingLookup_Id()  ) )== null )
					{

						TableAccessList tbacl = TableManager.getTableAccessForMember(  connection, memberId, tbulc.getTableUsingLookup_Id() );

						if ( tbacl.canReadWriteOnMyLatestView() ||
							tbacl.canReadLatestViewOfAll() ||
							tbacl.canReadLatestViewOfAllChildren() ||
							tbacl.canReadLatestOfTable() ||
							tbacl.canReadWriteLatestOfMyRows() ||
							tbacl.canWriteLatestOfTable() ||
							tbacl.canReadLatestofMyGroup() ||
							tbacl.canReadLatestofMyGroupAndImmediateChildren() ||
							tbacl.canReadWriteLatestofMyGroupAndImmediateChildren() ||
							tbacl.canReadLatestofMyGroupAndAllChildren() ||
							tbacl.canReadWriteLatestofMyGroupAndAllChildren()
							)
						{
									TablesWithAccess.addElement( tbulc );
									tablesAccessChecked.put( new Integer( tbulc.getTableUsingLookup_Id()  ) , new Boolean(true) );
						}
						else
						{
									tablesAccessChecked.put( new Integer( tbulc.getTableUsingLookup_Id()  ) , new Boolean(false) );
						}
					}
					else
					{
						if (  ((Boolean)tablesAccessChecked.get( new Integer( tbulc.getTableUsingLookup_Id()  ))).booleanValue() == true )
						{
									TablesWithAccess.addElement( tbulc );
						}
					}
				}
			}

		}
		catch (SystemException s)
		{
			req.setAttribute("com.boardwalk.exception.SystemException", s);
		}
		catch (SQLException sql)
		{
			sql.printStackTrace();
		   return;
		}
		catch (Exception e)
		{
			e.printStackTrace();
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


		req.setAttribute("tablesUsingLkpColumn", TablesWithAccess);
		req.setAttribute("lkpColumnName", lkpColumnName);
		req.setAttribute("lkpValue", lkpValue);
		req.setAttribute("lkpColumnId", new Integer(lkpColumnId));

		sc.getRequestDispatcher("/jsp/collaboration/tables/tables_using_lkp_column.jsp"). forward(req,res);

	}

    public void copyTable()throws ServletException, IOException
    {

		HttpSession hs = req.getSession(true);
		int memberId = ((Integer)hs.getAttribute("memberId")).intValue();
		int userId = ((Integer)hs.getAttribute("userId")).intValue();

		Connection connection = null;

		int source_table_id = Integer.parseInt(req.getParameter ("tableId"));
		int baselineId = Integer.parseInt(req.getParameter ("baselineId"));


		TableAccessList tbACL = null;
		TableInfo tbi = null;


		try
		{
			DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
			connection = databaseloader.getConnection();

			 tbi = TableManager.getTableInfo(connection,userId,source_table_id);

			tbACL = TableManager.getTableAccessForMember( connection, memberId, source_table_id );

		}
		catch (SystemException s)
		{
			req.setAttribute("com.boardwalk.exception.SystemException", s);
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

		req.setAttribute("tableInfo", tbi);
		req.setAttribute("tableAccessList", tbACL);
		req.setAttribute("baselineId", new Integer(baselineId));

		sc.getRequestDispatcher("/jsp/collaboration/tables/copy_table.jsp"). forward(req,res);

	}

    public void createTable()throws ServletException, IOException
    {

		   HttpSession hs = req.getSession(true);
		    Integer  memberId = (Integer)hs.getAttribute("memberId");

			if ( memberId == null || memberId.intValue() == -1 )
			{
				req.setAttribute("com.boardwalk.exception.BoardwalkException",
                                new BoardwalkException( 10007 ));
				 sc.getRequestDispatcher("/jsp/collaboration/edit_whiteboard.jsp"). forward(req,res);

			}

			Connection connection = null;
			try
			{
				DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
				connection = databaseloader.getConnection();
				Hashtable  relationships = NeighborhoodManager.getNeighborhoodRelationships( connection, nhId);
				req.setAttribute("relationships", relationships);
			}
			catch (SystemException s)
			{
				req.setAttribute("com.boardwalk.exception.SystemException", s);
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

		sc.getRequestDispatcher("/jsp/collaboration/tables/create_table_with_access.jsp"). forward(req,res);

	}



public void  tableCommit()
    throws ServletException, IOException {

         int a_table_id = -1;
	// get table name and whiteboard id
	tableName = req.getParameter("tableName");
    String tableDescr = req.getParameter("tableDescr");
	int wbid = Integer.parseInt(req.getParameter("wbid"));



	String ViewPreference = req.getParameter("ViewPreference");


	HttpSession hs = req.getSession(true);
	int memberId = ((Integer)hs.getAttribute("memberId")).intValue();

     Connection connection = null;
     TransactionManager tm = null;
     try
      {
            DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
            connection = databaseloader.getConnection();
            tm = new TransactionManager(connection, userId);
            int tid = tm.startTransaction();

             a_table_id = TableManager.createTable (
			                           connection,
			                           wbid,
			                           tableName,
			                           tableDescr,
			                           2, 1, 1,ViewPreference,
			                           memberId,
			                           tid,
			                           1
                        			 );

            Vector accessLists = new Vector();

            Hashtable  relationships = NeighborhoodManager.getNeighborhoodRelationships( connection, nhId);

			//CREATOR, PUBLIC

			Enumeration relationKeys = relationships.keys();

			if ( relationships.size() > 0 )
			{
				while ( relationKeys.hasMoreElements() )
				{
					String relationship = (String)relationKeys.nextElement();

					NewTableAccessList accessList = new NewTableAccessList(-1, a_table_id,relationship);

					if ( relationship.equals("PRIVATE") )
					{
						accessList.setAddRow();
						accessList.setDeleteRow();
						accessList.setReadLatestOfTable();
						accessList.setWriteLatestOfTable();
						accessList.setReadWriteLatestOfMyRows();
					}
					accessLists.add(accessList );

				}
			}


			NewTableAccessList publicAccessList = new NewTableAccessList(-1,a_table_id,"PUBLIC");
			//publicAccessList.setAdministerTable();
			publicAccessList.setAdministerColumn();
			publicAccessList.setAddRow();
			publicAccessList.setDeleteRow();
			publicAccessList.setReadLatestOfTable();
			publicAccessList.setWriteLatestOfTable();
			publicAccessList.setReadWriteLatestOfMyRows();

			NewTableAccessList creatorAccessList = new NewTableAccessList(-1,a_table_id,"CREATOR");
			creatorAccessList.setAdministerTable();
			creatorAccessList.setAdministerColumn();
			creatorAccessList.setAddRow();
			creatorAccessList.setDeleteRow();
			creatorAccessList.setReadLatestOfTable();
			creatorAccessList.setWriteLatestOfTable();
			creatorAccessList.setReadWriteLatestOfMyRows();

			accessLists.add(	creatorAccessList );
			accessLists.add(	publicAccessList);


		  if ( accessLists.size()  > 0 )
		  {

			  TableManager.addAccesstoTable
							(
								   connection,
								   a_table_id,
								   accessLists,
								   tid
							 );

		  }



           tm.commitTransaction();
        }
        catch ( SQLException e )
        {
           e.printStackTrace();
           try
           {
                tm.rollbackTransaction();
           }
           catch( SQLException sqlfatal )
           {
               sqlfatal.printStackTrace();
           }
        }
        catch ( SystemException s)
		{
		  s.printStackTrace();
		   try
		   {
				tm.rollbackTransaction();
		   }
		   catch( SQLException sqlfatal )
		   {
			   sqlfatal.printStackTrace();
		   }
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

		String redirectURL = "/MyTables?tableId="+a_table_id+"&wbid="+wbid+"&ViewPreference="+ViewPreference+"&action=editTable";
		res.sendRedirect(req.getContextPath() + redirectURL);

    }
	public void addColumnAccess()
	throws ServletException, IOException
	{
		int columnId = Integer.parseInt(req.getParameter("selectedColumnId"));
		int tableId = Integer.parseInt(req.getParameter("tableId"));
		String tableName = req.getParameter("tableName");
		String ViewPreference = req.getParameter("ViewPreference");

		Connection connection = null;
		TransactionManager tm = null;
		try
		{
		    DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
            connection = databaseloader.getConnection();
            tm = new TransactionManager(connection, userId);
            int tid = tm.startTransaction();
 			ColumnManager.addNewColumnAccess(connection,columnId, tid);
           	tm.commitTransaction();
        }
        catch ( SQLException e )
        {
           e.printStackTrace();
           try
           {
                tm.rollbackTransaction();
           }
           catch( SQLException sqlfatal )
           {
               sqlfatal.printStackTrace();
           }
        }
        catch ( Exception e )
        {
			e.printStackTrace();
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


		req.setAttribute("tableId", new Integer(tableId));
		req.setAttribute("tableName", tableName);
		req.setAttribute("ViewPreference", ViewPreference );


		String redirectURL = "/MyTables?tableId="+tableId+
					"&tableName="+tableName+
					"&ViewPreference="+ViewPreference+
					"&action=editColumnAccess";
		res.sendRedirect(req.getContextPath() + redirectURL);

	}
	public void deleteColumnAccess()
	throws ServletException, IOException
	{
		int columnId = Integer.parseInt(req.getParameter("selectedColumnId"));
		int tableId = Integer.parseInt(req.getParameter("tableId"));
		String tableName = req.getParameter("tableName");
		String ViewPreference = req.getParameter("ViewPreference");

		Connection connection = null;
		TransactionManager tm = null;
		try
		{
		    DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
            connection = databaseloader.getConnection();
            tm = new TransactionManager(connection, userId);
            int tid = tm.startTransaction();
 			ColumnManager.deleteColumnAccess(connection,columnId,tid);
           	tm.commitTransaction();
        }
        catch ( SQLException e )
        {
           e.printStackTrace();
           try
           {
                tm.rollbackTransaction();
           }
           catch( SQLException sqlfatal )
           {
               sqlfatal.printStackTrace();
           }
        }
        catch ( Exception e )
        {
			e.printStackTrace();
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


		req.setAttribute("tableId", new Integer(tableId));
		req.setAttribute("tableName", tableName);
		req.setAttribute("ViewPreference", ViewPreference );


		String redirectURL = "/MyTables?tableId="+tableId+
					"&tableName="+tableName+
					"&ViewPreference="+ViewPreference+
					"&action=editColumnAccess";
		res.sendRedirect(req.getContextPath() + redirectURL);

	}
	public void commitColumnAccess()
	throws ServletException, IOException
	{

		System.out.println("Dumping column access params");
		Enumeration names = req.getParameterNames();
		java.util.Map m = req.getParameterMap();
		for ( ; names.hasMoreElements() ;) {
				 String s = (String)names.nextElement();
		         System.out.println("param-->" +s+"===="+m.get(s));
		     }


		String[] cols = req.getParameterValues("AllColumns");
		String[] rels = req.getParameterValues("AllRels");
		Vector columns = new Vector();
		Vector relationships = new Vector();
		Vector access = new Vector();

		for(int i=0; i<cols.length; i++)
		{
			for(int j=0; j<rels.length; j++)
			{
				columns.add(cols[i]);
				relationships.add(rels[j]);
				System.out.println(rels[j]);
				String relStr = rels[j].replace(' ', '_');
				//relStr = rels[j].replace('-', '_');
				relStr = relStr.replace('-', '_');
				System.out.println("rel=="+relStr);
				System.out.println("rel=="+req.getParameter(relStr+"_"+cols[i]));
				access.add(req.getParameter(relStr+"_"+cols[i]));
			}
		}

		int tableId = Integer.parseInt(req.getParameter("tableId"));
		String tableName = req.getParameter("tableName");
		String ViewPreference = req.getParameter("ViewPreference");

		Connection connection = null;
		TransactionManager tm = null;
		try
		{
			DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
			connection = databaseloader.getConnection();
			tm = new TransactionManager(connection, userId);
			int tid = tm.startTransaction();
			ColumnManager.saveColumnAccess(connection, tableId, columns,relationships,access,tid);
			tm.commitTransaction();
		}
		catch ( SQLException e )
		{
		   e.printStackTrace();
		   try
		   {
				tm.rollbackTransaction();
		   }
		   catch( SQLException sqlfatal )
		   {
			   sqlfatal.printStackTrace();
		   }
		}
		catch ( Exception e )
		{
			e.printStackTrace();
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


		req.setAttribute("tableId", new Integer(tableId));
		req.setAttribute("tableName", tableName);
		req.setAttribute("ViewPreference", ViewPreference );


		String redirectURL = "/MyTables?tableId="+tableId+
					"&tableName="+tableName+
					"&ViewPreference="+ViewPreference+
					"&action=editColumnAccess";
		res.sendRedirect(req.getContextPath() + redirectURL);

	}
	public void editColumnAccess()throws ServletException, IOException
	{
		tableId = Integer.parseInt(req.getParameter("tableId"));
		String tableName = req.getParameter("tableName");
		String ViewPreference = req.getParameter("ViewPreference");
		HttpSession hs = req.getSession(true);

		int userId = ((Integer)hs.getAttribute("userId")).intValue();
		int memberId = ((Integer)hs.getAttribute("memberId")).intValue();

		Connection connection = null;
		try
		{
			DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
			connection = databaseloader.getConnection();
			Hashtable  relationships = TableManager.getNeighborhoodRelationshipsForTable( connection, tableId);
			req.setAttribute("relationships", relationships);
			TableColumnInfo tableColumnInfo = ColumnManager.getTableColumnInfo(
												connection,
												tableId,
												-1,
												userId,
												memberId,
												-1,
												null);
			ColumnAccessList cal = ColumnManager.getColumnAccess(connection,tableId);
			req.setAttribute("ColumnAccessList", cal);
			req.setAttribute("tableColumnInfo", tableColumnInfo);
			req.setAttribute("tableId", new Integer(tableId));
			req.setAttribute("tableName", tableName);
			req.setAttribute("ViewPreference", ViewPreference);

			System.out.println("sending to edit access page for table id = " +tableId + " name = " +tableName + " ViewPreference = " + ViewPreference);
		}
		catch (SystemException s)
		{
			s.printStackTrace();
			req.setAttribute("com.boardwalk.exception.SystemException", s);
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

	sc.getRequestDispatcher("/jsp/collaboration/tables/edit_column_access.jsp"). forward(req,res);

	}
	public void editTableAccess()throws ServletException, IOException
	{
		tableId = Integer.parseInt(req.getParameter("tableId"));
		String tableName = req.getParameter("tableName");
		String ViewPreference = req.getParameter("ViewPreference");
		Connection connection = null;
		try
		{
			DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
			connection = databaseloader.getConnection();
			Hashtable  relationships = TableManager.getNeighborhoodRelationshipsForTable( connection, tableId);
			req.setAttribute("relationships", relationships);
			Hashtable accessLists = TableManager.getTableAccess( connection, tableId);

			req.setAttribute("accessControlLists", accessLists);
			req.setAttribute("tableId", new Integer(tableId));
			req.setAttribute("tableName", tableName);
			req.setAttribute("ViewPreference", ViewPreference);


			System.out.println("sending to edit access page for table id = " +tableId + " name = " +tableName + " ViewPreference = " + ViewPreference);



		}
		catch (SystemException s)
		{
			s.printStackTrace();
			req.setAttribute("com.boardwalk.exception.SystemException", s);
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

		sc.getRequestDispatcher("/jsp/collaboration/tables/edit_table_access.jsp"). forward(req,res);

	}

	 public void editTableAdmin()throws ServletException, IOException
	    {
					tableId = Integer.parseInt(req.getParameter("tableId"));
					String tableName = req.getParameter("tableName");

					String ViewPreference = req.getParameter("ViewPreference");

					req.setAttribute("tableId", new Integer(tableId));
					req.setAttribute("tableName", tableName);
					req.setAttribute("ViewPreference", ViewPreference);

					System.out.println(" editTableAdmin viewpreference "  + ViewPreference );


					System.out.println("sending to edit admin page for table id = " +tableId + " name = " +tableName );
				   sc.getRequestDispatcher("/jsp/collaboration/tables/edit_table_admin.jsp"). forward(req,res);

	}

	 public void editTableUIPreferences()throws ServletException, IOException
	    {
					tableId = Integer.parseInt(req.getParameter("tableId"));

					String tableName = req.getParameter("tableName");
					String ViewPreference = req.getParameter("ViewPreference");

					Connection connection = null;

				try
				{
					DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
					connection = databaseloader.getConnection();

					Hashtable  UIPreferences = TableManager.getTableActionUIValues( connection, tableId);
					TableInfo tbi = TableManager.getTableInfo(connection,userId,tableId);


					req.setAttribute("UIpreferences", UIPreferences);
					req.setAttribute("tableId", new Integer(tableId));
					req.setAttribute("tableName", tableName);
					req.setAttribute("tabledescription", tbi.getTablePurpose() );
					req.setAttribute("ViewPreference", ViewPreference);

					System.out.println("sending to edit UI Preferfences  page for table id = " +tableId + " name = " +tableName );

				}
				catch (SystemException s)
				{
					s.printStackTrace();
					req.setAttribute("com.boardwalk.exception.SystemException", s);
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


				sc.getRequestDispatcher("/jsp/collaboration/tables/edit_table_UI_Preferences.jsp"). forward(req,res);

	}
    public void viewTable()
    throws ServletException, IOException
    {

		tableId = Integer.parseInt(req.getParameter("tableId"));
		String ViewPreference = req.getParameter("ViewPreference");
		String QueryPreference = req.getParameter("QueryPreference");
		String  baselineIdStr =  req.getParameter("baselineId");
		String formMode = req.getParameter("formMode");

		System.out.println(" baselineIdStr " + baselineIdStr );

		int baselineId = -1;
		if ( baselineIdStr != null )
		{
			baselineId = Integer.parseInt( baselineIdStr );
		}

		System.out.println(" View preference from the browser " + ViewPreference );
		System.out.println(" QueryPreference from the browser " + QueryPreference );
		System.out.println(" baselineId " + baselineId );

		if ( ViewPreference == null )
		ViewPreference = "";

		if ( QueryPreference == null  || QueryPreference.trim().equals("") )
			QueryPreference = QueryPreferenceType.ROWS_BY_ROW_SEQ_ID;

		ViewPreference = ViewPreference.trim();
		QueryPreference = QueryPreference.trim();

		req.setAttribute("TableId", new Integer(tableId));

		tableName = "Unknown Table Name TBCh";

		req.setAttribute("TableName", tableName);
		req.setAttribute("title", "Table Details");
		TableContents tbcon = null;
        TableInfo tbi = null;
        Connection connection = null;
        Hashtable  UIPreferences = null;
        Hashtable tablesUsingLkpForCol = null;
        FormDefinition fd = null;
        try
        {
			DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
			connection = databaseloader.getConnection();
			System.out.println(" sending request to get tablecontents" );
			tbcon = TableManager.getTableContents(connection,
											  tableId,
											  userId,
											  memberid,
											  nhId,
											  baselineId,
											  ViewPreference,
											  QueryPreference,
											  new Vector(),
											  false,
												-1,
												10000000,
												-1,
												10000000
											  );


			System.out.println(" sending request to get getTableInfo" );
			tbi = TableManager.getTableInfo(connection,userId,tableId);
			System.out.println(" got  request to get getTableInfo" );

			System.out.println(" sending request to get UIPreferences" );
			UIPreferences = TableManager.getTableActionUIValues( connection, tableId);
			System.out.println(" got  request to get UIPreferences" );
			int defTableId = FormManager.getDefinitionTable(connection, tableId, nhId);
			req.setAttribute("formTableId", new Integer(defTableId));
			int criteriaTableId = TableViewManager.getCriteriaTable(connection, tableId, userId);
			req.setAttribute("criteriaTableId", new Integer(criteriaTableId));
			if (formMode != null && formMode.equals("true"))
			{
				if (defTableId > -1)
				{
					fd = FormManager.getDefinition(connection,defTableId ,userId,memberid,nhId);
				}
			}
			else
			{
				fd = null;
			}

             req.setAttribute("TableContents", tbcon);
			 req.setAttribute("TableInfo", tbi);
			 req.setAttribute("UIPreferences", UIPreferences );
			 if (fd != null)
			 {
			 	req.setAttribute("FormDefinition", fd);

			 }
			 System.out.println(" sending request to get tablesUsingLkpForCol" );
			 tablesUsingLkpForCol = TableManager.getTablesUsingLookup(connection, tableId);
			 System.out.println(" got request to get tablesUsingLkpForCol" );

			 System.out.println("MyTables.editTable()::tablesUsingLkpForCol = " + tablesUsingLkpForCol);
			 req.setAttribute("TablesUsingLkpForCol", tablesUsingLkpForCol);


        }
        catch ( BoardwalkException e )
        {
		   e.printStackTrace();
           req.setAttribute("com.boardwalk.exception.BoardwalkException", e);

        }
        catch (SystemException s)
		{
			s.printStackTrace();
		    req.setAttribute("com.boardwalk.exception.SystemException", s);
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



        req.setAttribute("ViewPreference", ViewPreference );
		req.setAttribute("QueryPreference", QueryPreference );
		if ( baselineId > 0 )
		{
			req.setAttribute("baselineId", new Integer(baselineId));
		}

		sc.getRequestDispatcher("/jsp/collaboration/tables/viewTable.jsp").
							    forward(req,res);
	}


	public static  String getValuesOfLookUpTable(int tableIdVal, int lookupColId)throws ServletException, IOException
	{
	    String strRetValue="";
		Connection connection = null;
		try
		{
			DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
	        connection		= databaseloader.getConnection();
			Statement stmt	= connection.createStatement();

			String lsSql = " SELECT STRING_VALUE FROM BW_CELL, BW_TBL, BW_ROW, BW_COLUMN "+
						   " WHERE BW_TBL.ID = "+tableIdVal+" AND BW_CELL.BW_ROW_ID = BW_ROW.ID "+
						   " AND BW_CELL.BW_COLUMN_ID = BW_COLUMN.ID AND BW_ROW.BW_TBL_ID = BW_TBL.ID AND BW_COLUMN.ID = "+lookupColId+" "+
						   " AND BW_COLUMN.BW_TBL_ID = BW_TBL.ID AND BW_ROW.IS_ACTIVE = 1 AND BW_COLUMN.IS_ACTIVE = 1 "+
						   " ORDER BY BW_ROW.SEQUENCE_NUMBER";

//						   System.out.println(" sql for "+lsSql);

			ResultSet rs	= stmt.executeQuery(lsSql);

			StringBuffer sbRetValue= new StringBuffer(40);

			String str ="";

//			sbRetValue.append(" ");
//			sbRetValue.append("^");

			while (rs.next())
			{
			   str = rs.getString("STRING_VALUE");
			   if(str == null)
				   str="";
			   sbRetValue.append(str);
			   sbRetValue.append("^");
			}
			int len = sbRetValue.length();

			strRetValue = sbRetValue.toString().substring(0,len-1);
		}
        catch (SQLException sql)
		{
			sql.printStackTrace();
			return "-1";
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
		return strRetValue;
    }


    public void editTable()
    throws ServletException, IOException
    {
		tableId = Integer.parseInt(req.getParameter("tableId"));
		String ViewPreference = req.getParameter("ViewPreference");
		String QueryPreference = req.getParameter("QueryPreference");
		String  baselineIdStr =  req.getParameter("baselineId");
		String formMode = req.getParameter("formMode");
		String asOfTidStr = req.getParameter("asOfTid");
		String asOfDateStr = req.getParameter("asOfDate");
		String compTidStr = req.getParameter("compTid");
		String compDateStr = req.getParameter("compDate");
		String[] reqCols = req.getParameterValues("reqCols");
		String reqColsCSV = null;
		String[] reqColsComp = req.getParameterValues("reqColsComp");
		String reqColsCompCSV = null;
		boolean trackState = false;
		String trackStateStr = req.getParameter("trackState");
		if (trackStateStr != null && trackStateStr.equals("true"))
		{
			trackState = true;
		}

		boolean ShowinExcel = false;
		String ExcelDump = req.getParameter("excelDump");
		if(ExcelDump != null && ExcelDump.equals("true"))
			ShowinExcel = true;

		int asOfTid = -1;
		if (asOfTidStr != null && !asOfTidStr.equals("null"))
		{
			asOfTid = Integer.parseInt(asOfTidStr);
		}

		int compTid = -1;
		if (compTidStr != null && !compTidStr.equals("null"))
		{
			compTid = Integer.parseInt(compTidStr);
		}

		long asOfDate = -1;
		if (asOfDateStr != null && !asOfDateStr.equals("null"))
		{
			System.out.println("asOfDate = " + asOfDateStr);
			asOfDate = Long.parseLong(asOfDateStr);
		}
		long compDate = -1;
		if (compDateStr != null && !compDateStr.equals("null"))
		{
			compDate = Long.parseLong(compDateStr);
			System.out.println("compDate = " + compDateStr);
		}
		java.util.Date d = new java.util.Date();
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		cal.setTime(d);

		String period = req.getParameter("period");
		boolean compareWithLatest = false;
		if (period != null)
		{
			if (!period.equals("Custom"))
				compareWithLatest = true;
			// compare with latest information
			if (period.equals("LastChange"))
			{
				asOfDate = cal.getTime().getTime();
				compDate = cal.getTime().getTime();
			}
			else if (period.equals("Week"))
			{
				cal.add(Calendar.DATE, -7);
				compDate = cal.getTime().getTime();
				asOfDate = d.getTime();
			}
			else if (period.equals("Month"))
			{
				cal.add(Calendar.MONTH, -1);
				compDate = cal.getTime().getTime();
				asOfDate = d.getTime();
			}
			else if (period.equals("Quarter"))
			{
				cal.add(Calendar.MONTH, -3);
				compDate = cal.getTime().getTime();
				asOfDate = d.getTime();
			}
			else if (period.equals("Year"))
			{
				cal.add(Calendar.YEAR, -1);
				compDate = cal.getTime().getTime();
				asOfDate = d.getTime();
			}
		}

		if (reqCols != null)
		{
			for (int rci = 0; rci < reqCols.length; rci++)
			{
				String colidStr = reqCols[rci];
				try
				{
					Integer.parseInt(colidStr);
					if (rci == 0)
					{
						reqColsCSV = new String();
						reqColsCSV = reqColsCSV + colidStr;
					}
					else
					{
						reqColsCSV = reqColsCSV + "," + colidStr;
					}
				}
				catch (Exception e)
				{
					reqColsCSV = null;
					break;
				}
			}
		}
		System.out.println("reqColsCSV=" + reqColsCSV);
		if (reqColsComp != null)
		{
			for (int rci = 0; rci < reqColsComp.length; rci++)
			{
				String colidStr = reqColsComp[rci];
				try
				{
					Integer.parseInt(colidStr);
					if (rci == 0)
					{
						reqColsCompCSV = new String();
						reqColsCompCSV = reqColsCompCSV + colidStr;
					}
					else
					{
						reqColsCompCSV = reqColsCompCSV + "," + colidStr;
					}
				}
				catch (Exception e)
				{
					reqColsCompCSV = null;
					break;
				}
			}
		}
		System.out.println("reqColsCompCSV=" + reqColsCompCSV);
		//System.out.println(" baselineIdStr " + baselineIdStr );

		int baselineId = -1;
		if ( baselineIdStr != null )
		{
			baselineId = Integer.parseInt( baselineIdStr );
		}

		System.out.println(" View preference from the browser " + ViewPreference );
		System.out.println(" QueryPreference from the browser " + QueryPreference );
		System.out.println(" baselineId " + baselineId );

		if ( ViewPreference == null )
		ViewPreference = "";

		if ( QueryPreference == null  || QueryPreference.trim().equals("") )
			QueryPreference = QueryPreferenceType.ROWS_BY_ROW_SEQ_ID;

		ViewPreference = ViewPreference.trim();
		QueryPreference = QueryPreference.trim();

		req.setAttribute("TableId", new Integer(tableId));

		tableName = "Unknown Table Name TBCh";

		req.setAttribute("TableName", tableName);
		req.setAttribute("title", "Table Details");
		TableContents tbcon = null;
		TableContents tbchg = null;
        TableInfo tbi = null;
        Connection connection = null;
        Hashtable  UIPreferences = null;
        Hashtable tablesUsingLkpForCol = null;
        FormDefinition fd = null;
		int criteriaTableId = -1;
        try
        {
			int atid = asOfTid;
			long adate = asOfDate;
			if (compareWithLatest == true) // get the latest information (faster)
			{
				System.out.println("Comparing with LATEST");
				atid = -1;
				adate = -1;
			}
			DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
			connection = databaseloader.getConnection();
			System.out.println(" sending request to get tablecontents asOf " +
								atid + ":" + adate +
								"comparing with " +
								compTid + ":" + compDate);

			criteriaTableId = TableViewManager.getCriteriaTable(connection, tableId, userId);
			System.out.println("Using criterea table id = " + criteriaTableId);
			//String lsRowQuery = TableViewManager.getRowQuery(connection, tableId, userId, criteriaTableId);

			System.out.println("Got tbCon  Browser ");
			if(criteriaTableId == -1)
			{
				// Used for EditTable , then Compare table,
				tbcon = TableManager.getTableContents_t(  connection,
														  tableId,
														  userId,
														  memberid,
														  nhId,
														  baselineId,
														  ViewPreference,
														  QueryPreference,
														  new Vector(),
														  false,
														  -1,
														  10000000,
														  -1,
														  10000000,
														  atid,
														  adate,
														  -1,
														  -1,
														  reqColsCSV
														 );

			}
			else
			{
				// New Function for getting the table content based on criterea table
//				(int tableId, int userId, int memberId, int nhId, int critTableId, int baselineId, String view)
				tbcon = TableViewManager.getFiltredTableContentsForBrowser(tableId,
																		   userId,
																		   memberid,
																		   nhId,
																		   criteriaTableId,
																		   baselineId,
																		   ViewPreference,
																		   asOfTid,
																		   asOfDate,
																		   -1,
																		   -1);
			}
			System.out.println("Got tbcon " + tbcon.getRowIds().size() + " rows " + tbcon.getColumnNames().size() + " columns ");

			if (compTid > -1 || compDate > -1 )
			{
				System.out.println("Got tbchg  BrowserChanged ");
				System.out.println("Go to tbchg compTid "+ compTid + " compDate "+ compDate +" criteriaTableId  "+criteriaTableId );

				if(criteriaTableId == -1 )
				{
					tbchg = TableManager.getTableContents_t(  connection,
															  tableId,
															  userId,
															  memberid,
															  nhId,
															  baselineId,
															  ViewPreference,
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
				}
				else
				{
					// asOfTid, asOfDate, compTid, compDate

					tbchg = TableViewManager.getFiltredTableContentsForBrowserChanged(tableId, userId, memberid, nhId, criteriaTableId, baselineId, ViewPreference, asOfTid, asOfDate, compTid, compDate, period);
				}

				System.out.println("Got tbchg  " + tbchg.getRowIds().size() + " rows " + tbchg.getColumnNames().size() + " columns ");

				req.setAttribute("TableChanges" , tbchg);
				req.setAttribute("format","excel");
				// search for ordered columns
				if (trackState == true)
				{
					Hashtable htc = tbchg.getColumnsByColumnId();
					if (htc != null)
					{
						Vector cvec = new Vector(htc.keySet());
						Iterator ci = cvec.iterator();
						while (ci.hasNext())
						{
							Integer cid = (Integer)ci.next();
							Column col = (Column)htc.get(cid);
							if (col.getOrderedColumnId() != -1)
							{
								Hashtable ordCol = ColumnManager.getOrderedColumnValues(connection,
														col.getOrderedColumnId());
								req.setAttribute("OrdCol"+col.getId(), ordCol);
								req.setAttribute("format","excel");
							}
						}
					}
				}
			}

			System.out.println(" sending request to get getTableInfo" );
			tbi = TableManager.getTableInfo(connection,userId,tableId);
			System.out.println(" got  request to get getTableInfo" );

			System.out.println(" sending request to get UIPreferences" );
			UIPreferences = TableManager.getTableActionUIValues( connection, tableId);
			System.out.println(" got  request to get UIPreferences" );
			int defTableId = FormManager.getDefinitionTable(connection, tableId, nhId);
			req.setAttribute("formTableId", new Integer(defTableId));
//			int criteriaTableId = TableViewManager.getCriteriaTable(connection, tableId);
			req.setAttribute("criteriaTableId", new Integer(criteriaTableId));
			if (formMode != null && formMode.equals("true"))
			{
				if (defTableId > -1)
				{
					fd = FormManager.getDefinition(connection,defTableId ,userId,memberid,nhId);
				}
			}
			else
			{
				fd = null;
			}

             req.setAttribute("TableContents", tbcon);
			 req.setAttribute("TableInfo", tbi);
			 req.setAttribute("UIPreferences", UIPreferences );
			 if (fd != null)
			 {
			 	req.setAttribute("FormDefinition", fd);

			 }

			 System.out.println(" sending request to get tablesUsingLkpForCol" );
			 tablesUsingLkpForCol = TableManager.getTablesUsingLookup(connection, tableId);
			 System.out.println(" got request to get tablesUsingLkpForCol" );

			 System.out.println("MyTables.editTable()::tablesUsingLkpForCol = " + tablesUsingLkpForCol);
			 req.setAttribute("TablesUsingLkpForCol", tablesUsingLkpForCol);


        }
        catch ( BoardwalkException e )
        {
		   e.printStackTrace();
           req.setAttribute("com.boardwalk.exception.BoardwalkException", e);

        }
        catch (SystemException s)
		{
			s.printStackTrace();
		    req.setAttribute("com.boardwalk.exception.SystemException", s);
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



        req.setAttribute("ViewPreference", ViewPreference );
		req.setAttribute("QueryPreference", QueryPreference );
		if ( baselineId > 0 )
		{
			req.setAttribute("baselineId", new Integer(baselineId));
		}

		if(ShowinExcel)
			req.setAttribute("ShowinExcel", "true" );
		else
			req.setAttribute("ShowinExcel", "false" );

		sc.getRequestDispatcher("/jsp/collaboration/tables/edit_table.jsp").forward(req,res);

	}


    public void openTableBaseline()
    throws ServletException, IOException
    {

		HttpSession hs = req.getSession(true);
		int memberId = ((Integer)hs.getAttribute("memberId")).intValue();
		int userId = ((Integer)hs.getAttribute("userId")).intValue();

		tableId = Integer.parseInt(req.getParameter("tableId"));
        int baselineId = Integer.parseInt(req.getParameter("baselineId"));
		req.setAttribute("TableId", new Integer(tableId));
        req.setAttribute("baselineId", new Integer(baselineId));
        tableName = "Unknown Table Name TBCh";



        req.setAttribute("TableName", tableName);
        TableContents tbcon = null;
        TableInfo tbi = null;
        Connection connection = null;

        	String ViewPreference = req.getParameter("ViewPreference");
			String QueryPreference = req.getParameter("QueryPreference");

			System.out.println(" View preference from the browser " + ViewPreference );
			System.out.println(" QueryPreference from the browser " + QueryPreference );

			if ( ViewPreference == null )
				ViewPreference = "";

			if ( QueryPreference == null  || QueryPreference.trim().equals("") )
						QueryPreference = QueryPreferenceType.ROWS_BY_ROW_SEQ_ID;


			ViewPreference = ViewPreference.trim();
	        QueryPreference = QueryPreference.trim();




        try
        {
            DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
            connection = databaseloader.getConnection();
            Vector orderBy = new Vector();
			orderBy.add( SortType.ROWS_AND_COLUMNS );

      	    tbcon = TableManager.getTableContents(  connection,
													tableId,
													userId,
													memberid,
													nhId,
													baselineId,
													ViewPreference,
													QueryPreference,
													new Vector(),
													false,
													-1,
													10000000,
													-1,
													10000000
											  );
	         tbi = TableManager.getTableInfo(connection,userId,tableId);

        }
        catch ( Exception e )
        {
           e.printStackTrace();
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

		int columnLength = 90;

        int headerLength = 660;

		int buffer = 200;

		int columnLengths = columnLength* tbcon.getColumnNames().size();

		int totalLength = columnLengths;


		int startingPointHeader = 0;
		int startingPointColumns = 0;



		if ( totalLength > headerLength )
		{
			startingPointHeader = totalLength/2 - ( headerLength/2) + ( buffer/2) ;
			startingPointColumns = buffer/2;
		}
		else
		{
			startingPointHeader = ( buffer/2 );
			startingPointColumns = ( headerLength + buffer )/2 - ( totalLength/2 );
		}

		req.setAttribute("TableContents", tbcon);
		req.setAttribute("TableInfo", tbi);
		req.setAttribute("baselineId", new Integer(baselineId));
		req.setAttribute("ViewPreference", ViewPreference );
		req.setAttribute("QueryPreference", QueryPreference );

		sc.getRequestDispatcher("/jsp/collaboration/tables/edit_table.jsp").forward(req,res);
    }

    public void  tableAccessCommit()
    throws ServletException, IOException
    {

         int a_table_id = -1;
		// get table name and whiteboard id
		a_table_id = Integer.parseInt(req.getParameter("tableid"));
		String ViewPreference = req.getParameter("ViewPreference");
		int selNhid = Integer.parseInt(req.getParameter("selNhid"));
		String tableName = req.getParameter("tableName");
		System.out.println("tableAccessCommit = " +tableId + " name = " +tableName + " ViewPreference = " + ViewPreference);

     Connection connection = null;
     TransactionManager tm = null;
     try
      {

		    Enumeration pNames = req.getParameterNames();

		    while ( pNames.hasMoreElements() )
		    {
				String Pname = (String)pNames.nextElement();
				String value  = req.getParameter(Pname);

				System.out.println(" PName = " + Pname + " value = " +  value);

			}

            DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
            connection = databaseloader.getConnection();
            tm = new TransactionManager(connection, userId);
            int tid = tm.startTransaction();

            Vector updateAccessLists = new Vector();
            Vector newAccessLists = new Vector();


            Hashtable  relationships = NeighborhoodManager.getNeighborhoodRelationships( connection, selNhid);

			//CREATOR, PUBLIC

			Enumeration relationKeys = relationships.keys();

			if ( relationships.size() > 0 )
			{
					while ( relationKeys.hasMoreElements() )
					{
							String relationship = (String)relationKeys.nextElement();

						   System.out.println(" processing access for relationship " + relationship );

							int accessId = Integer.parseInt( req.getParameter(relationship+"_id"));

							NewTableAccessList accessList = new NewTableAccessList(accessId, a_table_id,relationship);

							 if( req.getParameter(relationship+"_ACL_canAdministerTable").equals("on"))
							 	accessList.setAdministerTable();


							if(req.getParameter(relationship+"_ACL_canAdministerColumn").equals("on"))
							accessList.setAdministerColumn();

							if(req.getParameter(relationship+"_ACL_canAddRow").equals("on"))
							accessList.setAddRow();

							if(req.getParameter(relationship+"_ACL_canDeleteRow").equals("on"))
							accessList.setDeleteRow();

							if(req.getParameter(relationship+"_ACL_canReadWriteOnMyLatestView").equals("on"))
							accessList.setReadWriteOnMyLatestView();

							if(req.getParameter(relationship+"_ACL_canReadLatestViewOfAll").equals("on"))
							accessList.setReadLatestViewOfAll();

							if(req.getParameter(relationship+"_ACL_canReadLatestViewOfAllChildren").equals("on"))
							accessList.setReadLatestViewOfAllChildren();

							if(req.getParameter(relationship+"_ACL_canReadLatestOfTable").equals("on"))
							accessList.setReadLatestOfTable();

							if(req.getParameter(relationship+"_ACL_canWriteLatestOfTable").equals("on"))
							accessList. setWriteLatestOfTable();

							if(req.getParameter(relationship+"_ACL_canReadWriteLatestOfMyRows").equals("on"))
							accessList.setReadWriteLatestOfMyRows();


							if(req.getParameter(relationship+"_ACL_canReadLatestofMyGroup").equals("on"))
							accessList.setReadMyGroup();

							if(req.getParameter(relationship+"_ACL_canReadWriteLatestofMyGroup").equals("on"))
							accessList.setReadWriteMyGroup();

							if(req.getParameter(relationship+"_ACL_canReadLatestofMyGroupAndImmediateChildren").equals("on"))
							accessList.setReadMyGroupAndImmediateChildren();

							if(req.getParameter(relationship+"_ACL_canReadWriteLatestofMyGroupAndImmediateChildren").equals("on"))
							accessList.setReadWriteMyGroupAndImmediateChildren();

							if(req.getParameter(relationship+"_ACL_canReadLatestofMyGroupAndAllChildren").equals("on"))
							accessList. setReadMyGroupAndAllChildren();

							if(req.getParameter(relationship+"_ACL_canReadWriteLatestofMyGroupAndAllChildren").equals("on"))
							accessList.setReadWriteMyGroupAndAllChildren();









							if ( accessId < 0 )
									newAccessLists.add(	accessList );
							else
									updateAccessLists.add(	accessList );

					}
			}

				String publicRelationshipValues  = req.getParameter("PUBLIC_ACL_canAdministerTable");
				String creatorRelationshipValues  = req.getParameter("CREATOR_ACL_canAdministerTable");



			if ( publicRelationshipValues != null && !publicRelationshipValues.trim().equals("") )
			{
							String relationship ="PUBLIC";
							int  accessId = Integer.parseInt( req.getParameter(relationship+"_id"));
							NewTableAccessList accessList = new NewTableAccessList(accessId, a_table_id,relationship);

							 if( req.getParameter(relationship+"_ACL_canAdministerTable").equals("on"))
								accessList.setAdministerTable();


							if(req.getParameter(relationship+"_ACL_canAdministerColumn").equals("on"))
							accessList.setAdministerColumn();

							if(req.getParameter(relationship+"_ACL_canAddRow").equals("on"))
							accessList.setAddRow();

							if(req.getParameter(relationship+"_ACL_canDeleteRow").equals("on"))
							accessList.setDeleteRow();

							if(req.getParameter(relationship+"_ACL_canReadWriteOnMyLatestView").equals("on"))
							accessList.setReadWriteOnMyLatestView();

							if(req.getParameter(relationship+"_ACL_canReadLatestViewOfAll").equals("on"))
							accessList.setReadLatestViewOfAll();

							if(req.getParameter(relationship+"_ACL_canReadLatestViewOfAllChildren").equals("on"))
							accessList.setReadLatestViewOfAllChildren();

							if(req.getParameter(relationship+"_ACL_canReadLatestOfTable").equals("on"))
							accessList.setReadLatestOfTable();

							if(req.getParameter(relationship+"_ACL_canWriteLatestOfTable").equals("on"))
							accessList. setWriteLatestOfTable();

							if(req.getParameter(relationship+"_ACL_canReadWriteLatestOfMyRows").equals("on"))
							accessList.setReadWriteLatestOfMyRows();

							if(req.getParameter(relationship+"_ACL_canReadLatestofMyGroup").equals("on"))
							accessList.setReadMyGroup();

							if(req.getParameter(relationship+"_ACL_canReadWriteLatestofMyGroup").equals("on"))
							accessList.setReadWriteMyGroup();

							if(req.getParameter(relationship+"_ACL_canReadLatestofMyGroupAndImmediateChildren").equals("on"))
							accessList.setReadMyGroupAndImmediateChildren();

							if(req.getParameter(relationship+"_ACL_canReadWriteLatestofMyGroupAndImmediateChildren").equals("on"))
							accessList.setReadWriteMyGroupAndImmediateChildren();

							if(req.getParameter(relationship+"_ACL_canReadLatestofMyGroupAndAllChildren").equals("on"))
							accessList. setReadMyGroupAndAllChildren();

							if(req.getParameter(relationship+"_ACL_canReadWriteLatestofMyGroupAndAllChildren").equals("on"))
							accessList.setReadWriteMyGroupAndAllChildren();


								if ( accessId < 0 )
																newAccessLists.add(	accessList );
														else
									updateAccessLists.add(	accessList );


			}


			if ( creatorRelationshipValues != null && !creatorRelationshipValues.trim().equals("") )
			{
							String relationship ="CREATOR";
							int accessId = Integer.parseInt( req.getParameter(relationship+"_id"));
							NewTableAccessList accessList = new NewTableAccessList(accessId, a_table_id,relationship);

							 if( req.getParameter(relationship+"_ACL_canAdministerTable").equals("on"))
								accessList.setAdministerTable();


							if(req.getParameter(relationship+"_ACL_canAdministerColumn").equals("on"))
							accessList.setAdministerColumn();

							if(req.getParameter(relationship+"_ACL_canAddRow").equals("on"))
							accessList.setAddRow();

							if(req.getParameter(relationship+"_ACL_canDeleteRow").equals("on"))
							accessList.setDeleteRow();

							if(req.getParameter(relationship+"_ACL_canReadWriteOnMyLatestView").equals("on"))
							accessList.setReadWriteOnMyLatestView();

							if(req.getParameter(relationship+"_ACL_canReadLatestViewOfAll").equals("on"))
							accessList.setReadLatestViewOfAll();

							if(req.getParameter(relationship+"_ACL_canReadLatestViewOfAllChildren").equals("on"))
							accessList.setReadLatestViewOfAllChildren();

							if(req.getParameter(relationship+"_ACL_canReadLatestOfTable").equals("on"))
							accessList.setReadLatestOfTable();

							if(req.getParameter(relationship+"_ACL_canWriteLatestOfTable").equals("on"))
							accessList. setWriteLatestOfTable();

							if(req.getParameter(relationship+"_ACL_canReadWriteLatestOfMyRows").equals("on"))
							accessList.setReadWriteLatestOfMyRows();


							if(req.getParameter(relationship+"_ACL_canReadLatestofMyGroup").equals("on"))
							accessList.setReadMyGroup();

							if(req.getParameter(relationship+"_ACL_canReadWriteLatestofMyGroup").equals("on"))
							accessList.setReadWriteMyGroup();

							if(req.getParameter(relationship+"_ACL_canReadLatestofMyGroupAndImmediateChildren").equals("on"))
							accessList.setReadMyGroupAndImmediateChildren();

							if(req.getParameter(relationship+"_ACL_canReadWriteLatestofMyGroupAndImmediateChildren").equals("on"))
							accessList.setReadWriteMyGroupAndImmediateChildren();

							if(req.getParameter(relationship+"_ACL_canReadLatestofMyGroupAndAllChildren").equals("on"))
							accessList. setReadMyGroupAndAllChildren();

							if(req.getParameter(relationship+"_ACL_canReadWriteLatestofMyGroupAndAllChildren").equals("on"))
							accessList.setReadWriteMyGroupAndAllChildren();

							System.out.println("accesslist for creator");
						//	accessList.print();

							if ( accessId < 0 )
															newAccessLists.add(	accessList );
													else
									updateAccessLists.add(	accessList );
			}

		  if ( newAccessLists.size()  > 0 )
		  {

				  TableManager.addAccesstoTable
			  						(
										   connection,
										   a_table_id,
										   newAccessLists,
										   tid
                        			 );

		  }

		    if ( updateAccessLists.size()  > 0 )
		  	{
					System.out.println("Updating " + updateAccessLists.size() + " access lists " );
		  			  TableManager.updateAccesstoTable
		  			  						(
		  										   connection,
		  										   a_table_id,
		  										   updateAccessLists,
		  										   tid
		                          			 );

		  }



           tm.commitTransaction();
        }
        catch ( SQLException e )
        {
           e.printStackTrace();
           try
           {
                tm.rollbackTransaction();
           }
           catch( SQLException sqlfatal )
           {
               sqlfatal.printStackTrace();
           }
        }
        catch ( SystemException s)
		{
		  s.printStackTrace();
		   try
		   {
				tm.rollbackTransaction();
		   }
		   catch( SQLException sqlfatal )
		   {
			   sqlfatal.printStackTrace();
		   }
        }
        catch ( Exception e )
        {
			e.printStackTrace();
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


		req.setAttribute("tableId", new Integer(tableId));
		req.setAttribute("tableName", tableName);
		req.setAttribute("ViewPreference", ViewPreference );



	String redirectURL = "/MyTables?tableId="+tableId+"&ViewPreference="+ViewPreference+"&action=editTable";
		  res.sendRedirect(req.getContextPath() + redirectURL);




    }


public void  commitTableUIPreferences()
    throws ServletException, IOException
    {

         int a_table_id = -1;
		// get table name and whiteboard id
	a_table_id = Integer.parseInt(req.getParameter("tableid"));
	String ViewPreference = req.getParameter("ViewPreference");

	String tableDescr  = req.getParameter("tableDescr");
	String tableName = req.getParameter("tableName");


     Connection connection = null;
     TransactionManager tm = null;
     try
      {

		    Enumeration pNames = req.getParameterNames();
		    DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
            connection = databaseloader.getConnection();
            tm = new TransactionManager(connection, userId);
            int tid = tm.startTransaction();

            Vector updateActionValues = new Vector();
            Vector newActionValues = new Vector();


            Hashtable  actionValues  = TableActionUIPreference.getActionToDefaultValues();

			Enumeration actionKeys = actionValues.keys();

			if ( actionValues.size() > 0 )
			{
					while ( actionKeys.hasMoreElements() )
					{
							String action = (String)actionKeys.nextElement();

						   System.out.println(" processing action  " + action );

							int actionId = Integer.parseInt( req.getParameter(action+"id"));
							int action_UIId = Integer.parseInt( req.getParameter(action+"UI_id"));
							String  action_UI_String = req.getParameter(action+"UI_String");




							TableActionUIPreference tbUIP = new TableActionUIPreference(
	   													   action_UIId,
														  a_table_id,
														 action_UI_String,
														  action,
														 actionId
														  );



							if ( action_UIId < 0 )
							{
									newActionValues.add(	tbUIP );
								}
							else
							{
									updateActionValues.add(	tbUIP );
								}

					}
			}


		  if ( newActionValues.size()  > 0 )
		  {
				 System.out.println(" new actions = " + newActionValues.size() );
				 TableManager.createTableActionUIValues( connection, a_table_id,newActionValues, tid );

		  }

		    if ( updateActionValues.size()  > 0 )
		  	{
				  System.out.println("Update actions = " + updateActionValues.size() );
				 TableManager.updateTableActionUIValues( connection, a_table_id,updateActionValues, tid );

		  }

 			TableManager.updateTableDescription(
																								    connection,
																								    a_table_id,
																								    tableName,
																								    tableDescr
										   												);


           tm.commitTransaction();
        }
        catch ( SQLException e )
        {
           e.printStackTrace();
           try
           {
                tm.rollbackTransaction();
           }
           catch( SQLException sqlfatal )
           {
               sqlfatal.printStackTrace();
           }
        }
        catch ( Exception e )
        {
			e.printStackTrace();
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


		  req.setAttribute("tableId", new Integer(tableId));
		  req.setAttribute("tableName", tableName);
		  req.setAttribute("ViewPreference", ViewPreference );


		  String redirectURL = "/MyTables?tableId="+tableId+"&ViewPreference="+ViewPreference+"&action=editTable";
		  res.sendRedirect(req.getContextPath() + redirectURL);

    }


	public void  commitTableProperties()
    throws ServletException, IOException
    {

		int a_table_id = -1;
		// get table name and whiteboard id
		a_table_id = Integer.parseInt(req.getParameter("tableId"));
		String ViewPreference = req.getParameter("ViewPreference");
		String tableDescr  = req.getParameter("tableDescr");
		String tableName = req.getParameter("tableName");
		int	formTableId = Integer.parseInt(req.getParameter ("formTableId"));
		int criteriaTableId = Integer.parseInt(req.getParameter("criteriaTableId"));

		if ( tableDescr == null )
			tableDescr = " " ;

		if ( tableDescr.equals("") )
					tableDescr = " " ;

		if ( tableName == null || tableName.trim().equals("") )
		{
			 BoardwalkException bwe = new BoardwalkException( 12007 );
			 req.setAttribute("com.boardwalk.exception.BoardwalkException", bwe);
			String redirectURL = "/MyTables?tableId="+a_table_id+"&ViewPreference="+ViewPreference+"&action=editTable";
			res.sendRedirect(req.getContextPath() + redirectURL);

		}
		Connection connection = null;
		TransactionManager tm = null;
		try
		{
		    DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
            connection = databaseloader.getConnection();
            tm = new TransactionManager(connection, userId);
            int tid = tm.startTransaction();

			System.out.println(" saving properties " + tableName + " tableDescr = " + tableDescr );

 			TableManager.updateTableDescription(
												connection,
												a_table_id,
												tableName,
												tableDescr
												);
           	tm.commitTransaction();

           	// save the form definition
           	tid = tm.startTransaction();

           	FormManager.addFormDefinition(
									connection,
									a_table_id,
									formTableId,
									tid);
			// save the criteria table definition
			TableViewManager.setCriteriaTable(
						connection,
						a_table_id,
						criteriaTableId,
						tid);

			tm.commitTransaction();



        }
        catch ( SQLException e )
        {
           e.printStackTrace();
           try
           {
                tm.rollbackTransaction();
           }
           catch( SQLException sqlfatal )
           {
               sqlfatal.printStackTrace();
           }
        }
        catch ( Exception e )
        {
			e.printStackTrace();
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


		req.setAttribute("tableId", new Integer(tableId));
		req.setAttribute("tableName", tableName);
		req.setAttribute("ViewPreference", ViewPreference );


		String redirectURL = "/MyTables?tableId="+a_table_id+"&ViewPreference="+ViewPreference+"&action=editTable";
		System.out.println("MyTables::commitTableProperties() sendRedirect="+ redirectURL);
		res.sendRedirect(req.getContextPath() + redirectURL);

    }





public void  commitCopyTable()
    throws ServletException, IOException {

	HttpSession hs = req.getSession(true);
	int memberId = ((Integer)hs.getAttribute("memberId")).intValue();


      int a_table_id = -1;
	// get table name and whiteboard id
	tableName = req.getParameter("tableName");
    String tableDescr = req.getParameter("tableDescr");
	int wbid = Integer.parseInt(req.getParameter("wbid"));
	int source_table_id = Integer.parseInt(req.getParameter("source_table_id"));
	int baselineId = Integer.parseInt(req.getParameter("baselineId"));


	String ViewPreference = req.getParameter("ViewPreference");



	System.out.println("copyStructure"+req.getParameter("copyStructure") );
	System.out.println("copyAccess"+ req.getParameter("copyAccess"));
	System.out.println("copyLatestContent"+ req.getParameter("copyLatestContent"));
	System.out.println("copyDesignValues"+ req.getParameter("copyDesignValues"));
	System.out.println("copyUIPreferences"+ req.getParameter("copyUIPreferences"));
	System.out.println("copyDeactivatedContent"+ req.getParameter("copyDeactivatedContent"));


	boolean copyStructure =  req.getParameter("copyStructure") != null ? true:false;
	boolean copyAccess = req.getParameter("copyAccess")!= null ? true:false;
	boolean copyLatestContent = req.getParameter("copyLatestContent")!= null ? true:false;
	boolean copyDesignValues = req.getParameter("copyDesignValues")!= null ? true:false;
	boolean copyUIPreferences = req.getParameter("copyUIPreferences")!= null ? true:false;
	boolean copyDeactivatedContent = req.getParameter("copyDeactivatedContent")!= null ? true:false;




     Connection connection = null;
     TransactionManager tm = null;
     try
      {
            DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
            connection = databaseloader.getConnection();
            tm = new TransactionManager(connection, userId);
            int tid = tm.startTransaction();


			if ( baselineId == -1 )
			{
				 a_table_id = TableManager.copyTable
										(
										   connection,
										   source_table_id,
										   wbid,
										   tableName,
										   tableDescr,
										   copyStructure,
										   copyLatestContent,
										   copyDesignValues,
										   copyUIPreferences,
										   copyDeactivatedContent,
										   copyAccess,
										   ViewPreference,
										   memberId,
										   tid
										 );

		}
		else
		{
				 a_table_id = TableManager.copyTableFromBaseline
												(
												   connection,
												   source_table_id,
												   baselineId,
												   wbid,
												   tableName,
												   tableDescr,
												   copyStructure,
												   copyLatestContent,
												   copyDesignValues,
												   copyUIPreferences,
												   copyAccess,
												   ViewPreference,
												   memberId,
												   tid
										 );
			}
           	tm.commitTransaction();
        }
        catch ( SQLException e )
        {
           e.printStackTrace();
           try
           {
                tm.rollbackTransaction();
           }
           catch( SQLException sqlfatal )
           {
               sqlfatal.printStackTrace();
           }
        }
        catch ( SystemException s)
		{
		  s.printStackTrace();
		   try
		   {
				tm.rollbackTransaction();
		   }
		   catch( SQLException sqlfatal )
		   {
			   sqlfatal.printStackTrace();
		   }
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

			 String redirectURL = "/MyTables?tableId="+a_table_id+"&wbid="+wbid+"&ViewPreference="+ViewPreference+"&action=editTable";
			res.sendRedirect(req.getContextPath() + redirectURL);

    }



    public void addRow()
    throws ServletException, IOException {
	tableId = Integer.parseInt(req.getParameter("tableId"));
	int wbid = Integer.parseInt(req.getParameter("wbid"));
	String ViewPreference = req.getParameter("ViewPreference");
	String designValues = req.getParameter("designValues");
	String rowIdParam = req.getParameter("rowId");

	HttpSession hs = req.getSession(true);
	int nhId = ((Integer)hs.getAttribute("nhId")).intValue();


	int rowId = -1;

	if ( rowIdParam != null )
	{
		   if (  !rowIdParam.trim().equals("") )
				rowId = Integer.parseInt(rowIdParam);
	}

		boolean updateDesignValues = false;


		if ( designValues != null && ! designValues.trim().equals("") )
		{
				if ( designValues.trim().equals("true"))
				updateDesignValues = true;
		}



	// update the table database from user input
	// updateTableDB();
        Connection connection = null;
        TransactionManager tm = null;
        try
        {
            DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
            connection = databaseloader.getConnection();
            tm = new TransactionManager(connection, userId);
            int tid = tm.startTransaction();

			TableManager.lockTableForUpdate( connection, tableId);

            if ( rowId == -1 && ViewPreference.equals( ViewPreferenceType.MY_ROWS) )
			{
					System.out.println(" Calling getAfterRowIdforMyRows " );
					rowId = TableManager.getAfterRowIdforMyRows( connection,tableId,userId );
			}
			else
			if ( rowId == -1 && ViewPreference.equals( ViewPreferenceType.LATEST_ROWS_OF_ALL_USERS_IN_MY_NH) )
			{
					System.out.println(" Calling getAfterRowIdforMyGroupRows " );
					rowId = TableManager.getAfterRowIdforMyGroupRows( connection,tableId,nhId );
			}
			else
			if ( rowId == -1 && ViewPreference.equals( ViewPreferenceType.LATEST_ROWS_OF_ALL_USERS_IN_MY_NH_AND_IMM_CHD) )
			{
					System.out.println(" Calling getAfterRowIdforMyGroupAndChildrenRows " );
					rowId = TableManager.getAfterRowIdforMyGroupAndChildrenRows( connection,tableId,nhId );
			}
			else
			if ( rowId == -1 && ViewPreference.equals( ViewPreferenceType.LATEST_ROWS_OF_ALL_USERS_IN_MY_NH_AND_ALL_CHD) )
			{
					System.out.println(" Calling getAfterRowIdforMyGroupAndAllChildrenRows " );
					rowId = TableManager.getAfterRowIdforMyGroupAndAllChildrenRows( connection,tableId,nhId );
			}
            System.out.println(" Tableid = " +  tableId + " after rowId = " +  rowId + " tid = " + tid );
            TableManager.createRow(connection, tableId, "", rowId, 1, tid );
			TableManager.resequenceRows(connection, tableId);
			TableManager.updateUserExportTid(connection, tableId, userId, tid);
            tm.commitTransaction();

        }
        catch ( SQLException e )
        {
           e.printStackTrace();
           try
           {
                tm.rollbackTransaction();
                String m_failureReason =  " The table is being updated by a different user, please try later";
				BoardwalkException bwe = new BoardwalkException( 12008 );
				req.setAttribute("com.boardwalk.exception.BoardwalkException", bwe);

           }
           catch( SQLException sqlfatal )
           {
               sqlfatal.printStackTrace();
           }
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

        // Return to the edit table page
		    String redirectURL = "/MyTables?tableId="+tableId+"&wbid="+wbid+"&ViewPreference="+ViewPreference+"&action=editTable&designValues="+designValues;
			res.sendRedirect(req.getContextPath() + redirectURL);
    }


	public void  changeOwnershipForm()throws ServletException, IOException
	{
		System.out.println("Assign Row Ownership Form");
		HttpSession hs = req.getSession(true);
		int memberId = ((Integer)hs.getAttribute("memberId")).intValue();
		int userId = ((Integer)hs.getAttribute("userId")).intValue();

		String[] rowIds = req.getParameterValues("rowId") ;
		String tableId =req.getParameter("tableId");
		String  wbid = req.getParameter("wbid");
		String ViewPreference = req.getParameter("ViewPreference");
		String designValues = req.getParameter("designValues");



		req.setAttribute("tableId", tableId);
		req.setAttribute("rowId", rowIds);
		req.setAttribute("wbid", wbid);
		req.setAttribute("ViewPreference", ViewPreference);
		req.setAttribute("designValues", designValues);

		sc.getRequestDispatcher("/jsp/collaboration/tables/assign_row_owner.jsp"). forward(req,res);

	}


    public void changeRowOwnership()
	    throws ServletException, IOException {

		String[] rowIds =  req.getParameterValues("rowId") ;
		String emailAddress = req.getParameter("emailAddress");
		tableId = Integer.parseInt(req.getParameter("tableId"));
		int wbid = Integer.parseInt(req.getParameter("wbid"));
		String ViewPreference = req.getParameter("ViewPreference");
		String designValues = req.getParameter("designValues");


		// update the table database from user input
		// updateTableDB();
		Connection connection = null;
		TransactionManager tm = null;
		int result = -1;
		boolean failure = false;
		try
		{
			DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
			connection = databaseloader.getConnection();
			tm = new TransactionManager(connection, userId);
			int tid = tm.startTransaction();

			if ( rowIds != null && rowIds.length > 0 )
			{
				System.out.println(" rowIds  = " +  rowIds.length);
				for ( int s = 0; s < rowIds.length; s++ )
				{

					System.out.println(" rowId = " +  rowIds[s] + " emailAddress = " +  emailAddress + " tid = " + tid );
					result = TableManager.changeRowOwnership( connection,  Integer.parseInt(rowIds[s]), emailAddress, tid);
					if ( result != 0 )
					{
						failure = true;
						break;
					}
				}
			}

			if  ( failure == false )
				tm.commitTransaction();
			else
				tm.rollbackTransaction();

		}
		catch ( SQLException e )
		{
			e.printStackTrace();
			try
			{
				tm.rollbackTransaction();
			}
			catch( SQLException sqlfatal )
			{
			   sqlfatal.printStackTrace();
			}
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

		if  (  failure == false  )
		{
			// Return to the edit table page
			String redirectURL = "/MyTables?tableId="+tableId+"&wbid="+wbid+"&ViewPreference="+ViewPreference+"&action=editTable&designValues="+designValues;
			res.sendRedirect(req.getContextPath() + redirectURL);
		}
		else
		{
			String redirectURL = "/MyTables?tableId="+tableId+"&wbid="+wbid+"&ViewPreference="+ViewPreference+"&action=editTable&designValues="+designValues;
			res.sendRedirect(req.getContextPath() + redirectURL);
		}
    }

    public void lockTable()

	throws ServletException, IOException
	{
		tableId = Integer.parseInt(req.getParameter("tableId"));
		int wbid = Integer.parseInt(req.getParameter("wbid"));
		String ViewPreference = req.getParameter("ViewPreference");
		String designValues = req.getParameter("designValues");

		Connection connection = null;
		TransactionManager tm = null;
		try
		{
			DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
			connection = databaseloader.getConnection();
			tm = new TransactionManager(connection, userId);
			int tid = tm.startTransaction();

			TableManager. lockTable( connection,  tableId, tid);
			tm.commitTransaction();




		}
		catch ( SQLException e )
		{
		   e.printStackTrace();
		   try
		   {
				tm.rollbackTransaction();
		   }
		   catch( SQLException sqlfatal )
		   {
			   sqlfatal.printStackTrace();
		   }
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

		 String redirectURL = "/MyTables?tableId="+tableId+"&wbid="+wbid+"&ViewPreference="+ViewPreference+"&action=editTable&designValues="+designValues;
		 res.sendRedirect(req.getContextPath() + redirectURL);

    }


public void unlockTable()

		    throws ServletException, IOException {

			tableId = Integer.parseInt(req.getParameter("tableId"));
			int wbid = Integer.parseInt(req.getParameter("wbid"));
					String ViewPreference = req.getParameter("ViewPreference");
		String designValues = req.getParameter("designValues");

		        Connection connection = null;
		        TransactionManager tm = null;
		        try
		        {
		            DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
		            connection = databaseloader.getConnection();
		            tm = new TransactionManager(connection, userId);
		            int tid = tm.startTransaction();

					TableManager. unlockTable( connection,  tableId, tid);
					tm.commitTransaction();

		        }
		        catch ( SQLException e )
		        {
		           e.printStackTrace();
		           try
		           {
		                tm.rollbackTransaction();
		           }
		           catch( SQLException sqlfatal )
		           {
		               sqlfatal.printStackTrace();
		           }
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

				 String redirectURL = "/MyTables?tableId="+tableId+"&wbid="+wbid+"&ViewPreference="+ViewPreference+"&action=editTable&designValues="+designValues;
				 res.sendRedirect(req.getContextPath() + redirectURL);

    }



    public void purgeRow()
    {
        // get the table id and the row id
	tableId = Integer.parseInt(req.getParameter("tableId"));
	int wbid = Integer.parseInt(req.getParameter("wbid"));
	String ViewPreference = req.getParameter("ViewPreference");
	String designValues = req.getParameter("designValues");

		boolean updateDesignValues = false;


		if ( designValues != null && ! designValues.trim().equals("") )
		{
				if ( designValues.trim().equals("true"))
				updateDesignValues = true;
		}
		String rowIds[] = (String[])req.getParameterValues("rowId");
        Connection connection = null;
        TransactionManager tm = null;
        try
        {
            DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
            connection = databaseloader.getConnection();
            tm = new TransactionManager(connection, userId);
            int tid = tm.startTransaction();

            if ( rowIds != null && rowIds.length > 0 )
			{
				for ( int s = 0; s < rowIds.length; s++ )
				{
					 System.out.println("Deactivating rowid " + rowIds[s] );
					 RowManager.deactivateRow(connection, Integer.parseInt(rowIds[s]), tid);
				}
			}
			TableManager.updateUserExportTid(connection, tableId, userId, tid);
            tm.commitTransaction();
        }
        catch ( SystemException sys )
        {
           sys.printStackTrace();
           try
           {
                tm.rollbackTransaction();
           }
           catch( SQLException sqlfatal )
           {
               sqlfatal.printStackTrace();
           }
        }
        catch ( SQLException e )
        {
           e.printStackTrace();
           try
           {
                tm.rollbackTransaction();
           }
           catch( SQLException sqlfatal )
           {
               sqlfatal.printStackTrace();
           }
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



        try
        {
            // return to table edit mode
           	    String redirectURL = "/MyTables?tableId="+tableId+"&wbid="+wbid+"&ViewPreference="+ViewPreference+"&action=editTable&designValues="+designValues;
				res.sendRedirect(req.getContextPath() + redirectURL);
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }
	//////////////////////////////////////////////////////////////////////////
	// Data Form /////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////
	public void dataForm()
	throws ServletException, IOException {
		System.out.println("Go to dataform mode");
		// get the table id and the row id
		tableId = Integer.parseInt(req.getParameter("tableId"));
		String ViewPreference = req.getParameter("ViewPreference");
		String QueryPreference = req.getParameter("QueryPreference");
		String  baselineIdStr =  req.getParameter("baselineId");

		System.out.println(" baselineIdStr " + baselineIdStr );

		int baselineId = -1;
		if ( baselineIdStr != null )
		{
			baselineId = Integer.parseInt( baselineIdStr );
		}
		System.out.println(" View preference from the browser " + ViewPreference );
		System.out.println(" QueryPreference from the browser " + QueryPreference );
		System.out.println(" baselineId " + baselineId );

		HttpSession hs = req.getSession(true);
		int memberId = ((Integer)hs.getAttribute("memberId")).intValue();
		int userNhId =  ((Integer)hs.getAttribute("nhId")).intValue();

		RowContents rowcon = null;
		Connection connection = null;
		TableInfo tbi = null;
		FormDefinition fd = null;
		TableContents tbcon = null;
		try
		{
			DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
			connection = databaseloader.getConnection();
			tbi = TableManager.getTableInfo(connection,userId,tableId);
			fd = FormManager.getDefinition(connection,1/*HARDCODED*/ ,userId,memberId,userNhId);
			tbcon = TableManager.getTableContents(connection,
												  tableId,
												  userId,
												  memberid,
												  userNhId,
												  baselineId,
												  ViewPreference,
												  QueryPreference,
												  new Vector(),
												  false,
													-1,
													10000000,
													-1,
													10000000
                                                  );
		}
		catch ( Exception e )
		{
			e.printStackTrace();
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

		req.setAttribute("TableContents", tbcon);
		req.setAttribute("TableInfo", tbi);
		req.setAttribute("FormDefinition", fd);

		sc.getRequestDispatcher("/jsp/collaboration/tables/dataform.jsp").
							forward(req, res);
	}

    //////////////////////////////////////////////////////////////////////////
    // Edit Row///////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////
    public void editRow()
    throws ServletException, IOException {
		// get the table id and the row id
		tableId = Integer.parseInt(req.getParameter("tableId"));
		int rowId = Integer.parseInt(req.getParameter("rowId"));

		RowContents rowcon = null;
		Connection connection = null;
		TableInfo tbi = null;
		try
		{
			DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
			connection = databaseloader.getConnection();
			rowcon = RowManager.getRowContents(connection,rowId, userId,true,-1);
			tbi = TableManager.getTableInfo(connection,userId,tableId);
		}
		catch ( Exception e )
		{
			e.printStackTrace();
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

		req.setAttribute("RowContents", rowcon);
		req.setAttribute("TableInfo", tbi);

		sc.getRequestDispatcher("/jsp/collaboration/tables/edit_row.jsp").
							forward(req, res);
    }

    public void updateTableCell()
    throws ServletException, IOException {
	// get the table id and the row id
	tableId = Integer.parseInt(req.getParameter("tableId"));
	int rowId = Integer.parseInt(req.getParameter("rowId"));
	int cellForUpdate = Integer.parseInt(req.getParameter("cellForUpdate"));
	int selectedTable = Integer.parseInt(req.getParameter("selectedTable"));

	RowContents rowcon = null;
        Connection connection = null;
        try
        {
            DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
            connection = databaseloader.getConnection();
            rowcon = RowManager.getRowContents(connection,rowId,userId,true,-1);
            Hashtable cellsByColumn = rowcon.getCellsByColumnName();
            Enumeration columns = cellsByColumn.keys();
            while (columns.hasMoreElements()) {
                Cell c = (Cell)cellsByColumn.get(columns.nextElement());
                if (c.getId() == cellForUpdate)
                    c.setTableValue(selectedTable);
            }

        }
        catch ( Exception e )
        {
           e.printStackTrace();
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

        req.setAttribute("RowContents", rowcon);
	sc.getRequestDispatcher("/jsp/collaboration/tables/edit_row.jsp").
							    forward(req, res);
    }

    public void selectTable()
    {
			System.out.println("selectTable");
            tableId = Integer.parseInt(req.getParameter("tableId"));
            int rowId = Integer.parseInt(req.getParameter("rowId"));
            Connection connection = null;
        try
        {
            DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
            connection = databaseloader.getConnection();
            Hashtable wbTables = TableManager.getWbTablesForSelection(connection,tableId,
            userId,
            1, // status
            1 // access
            );
            req.setAttribute("wbTables", wbTables);
            sc.getRequestDispatcher("/jsp/collaboration/tables/select_table.jsp"). forward(req, res);
        }
        catch ( Exception e )
        {
           e.printStackTrace();
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

public void selectTableForLookup()
    {

        Connection connection = null;
        try
        {
            DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
            connection = databaseloader.getConnection();
            Hashtable wbTables = TableManager.getWbLookupTablesForSelection(connection,tableId,
            userId,
            1, // status
            1 // access
            );
            req.setAttribute("wbTables", wbTables);
            sc.getRequestDispatcher("/jsp/collaboration/tables/select_lookup_table.jsp"). forward(req, res);
        }
        catch ( Exception e )
        {
           e.printStackTrace();
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

    public void commitRow()
    throws ServletException, IOException {
		tableId = Integer.parseInt(req.getParameter("tableId"));
		int rowId = Integer.parseInt(req.getParameter("rowId"));

        Enumeration paramNames = req.getParameterNames();

        Vector    vec_cellContents = new Vector();

        while( paramNames.hasMoreElements())
        {
            String paramName = (String)paramNames.nextElement();
            if ( paramName.startsWith("prevCell") )
            {
                String cellId  = paramName.substring(8);
                String newVal = req.getParameter("cell" + cellId);
                String oldVal = req.getParameter(paramName);
                String cellType = req.getParameter("typeCell"+cellId);
                String oldCellType = req.getParameter("prevtypeCell"+cellId);
                if ( !newVal.equals(oldVal) || !cellType.equals(oldCellType)  )
                {
                    CellContents cc = new CellContents(Integer.parseInt(cellId), cellType, newVal );
                  //  cc.printCellContents();
                    vec_cellContents.addElement(cc);
                }
            }
        }
        if ( vec_cellContents.size() > 0 )
        {
            Connection connection = null;
            TransactionManager tm = null;
            try
            {
                DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
                connection = databaseloader.getConnection();
                tm = new TransactionManager( connection, userId);
                int tid = tm.startTransaction();
                RowManager.commitRow(connection, tid, vec_cellContents);
				TableManager.updateUserExportTid(connection, tableId, userId, tid);
                tm.commitTransaction();

            }
            catch ( Exception e )
            {
               try
               {
                tm.rollbackTransaction();
               }
               catch( SQLException sql )
               {
                   sql.printStackTrace();
               }
               e.printStackTrace();
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
        // return to table edit mode
		editTable();
    }




	public void commitCells()
    throws ServletException, IOException
    {

	tableId = Integer.parseInt(req.getParameter("tableId"));
	int wbid = Integer.parseInt(req.getParameter("wbid"));
	int bucketNumber = Integer.parseInt(req.getParameter("bucketNumber"));
	String ViewPreference = req.getParameter("ViewPreference");
	String tComment = req.getParameter("tableComment");
	String tDescription = null;

	boolean updateDesignValues = false;


	if ( ViewPreference!= null && ViewPreference.trim().equals("DESIGN") )
	{
		updateDesignValues = true;
	}

	Enumeration paramNames = req.getParameterNames();

	Vector    vec_cellContents = new Vector();
	String queryString = new String();
	while( paramNames.hasMoreElements())
	{
		String paramName = (String)paramNames.nextElement();
		if ( paramName.startsWith("pCell") )
		{
			String cellId  = paramName.substring(5);
			String newVal = req.getParameter("Cell" + cellId);
			String oldVal = req.getParameter(paramName);
			String cellType = req.getParameter("CellType"+cellId);
			String dirtyFlagCell = req.getParameter("dirtyCell"+cellId);
			System.out.println( cellId + ":" + newVal + ":" + oldVal + ":" + dirtyFlagCell + ":" );
			if ( dirtyFlagCell.equals("true")  )
			{
				CellContents cc = new CellContents(Integer.parseInt(cellId), cellType, newVal );
				cc.printCellContents();
				vec_cellContents.addElement(cc);
			}
		}
		else if (paramName.startsWith("cc"))
		{
			queryString = queryString + "&" + paramName + "=" +  req.getParameter(paramName);
		}
		else if (paramName.startsWith("bucket"))
		{
			queryString = queryString + "&" + paramName + "=" +  req.getParameter(paramName);
		}
		else if (paramName.startsWith("portlet"))
		{
			queryString = queryString + "&" + paramName + "=" +  req.getParameter(paramName);
		}
	}

	//System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++");
	//System.out.println(queryString);

	Connection connection = null;
	TransactionManager tm = null;
	if ( vec_cellContents.size() > 0 )
	{

		try
		{
			DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
			connection = databaseloader.getConnection();
			tm = new TransactionManager( connection, userId);
			int tid = tm.startTransaction( "Updated Table Content", tComment);
			TableManager.commitCellsByCellId(connection, tid, vec_cellContents, updateDesignValues);
			TableManager.updateUserExportTid(connection, tableId, userId, tid);
			tm.commitTransaction();

		}
		catch ( Exception e )
		{
		   try
		   {
			tm.rollbackTransaction();
		   }
		   catch( SQLException sql )
		   {
			   sql.printStackTrace();
		   }
		   e.printStackTrace();
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
	else
	{
		if (tComment != null && !tComment.equals(""))
		{
			try
			{
				DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
				connection = databaseloader.getConnection();
				tm = new TransactionManager( connection, userId);
				int tid = tm.startTransaction( "Added a comment", tComment);
				tm.commitTransaction();

			}
			catch ( Exception e )
			{
			   try
			   {
				tm.rollbackTransaction();
			   }
			   catch( SQLException sql )
			   {
				   sql.printStackTrace();
			   }
			   e.printStackTrace();
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

	String redirectURL = "/MyTables?tableId="+tableId+"&wbid="+wbid+"&ViewPreference="+ViewPreference+"&action=editTable";
	redirectURL = redirectURL + queryString;

	res.sendRedirect(req.getContextPath() + redirectURL);

	}

    public void commitTableEdits()
	    throws ServletException, IOException
	{
		tableId = Integer.parseInt(req.getParameter("tableId"));
		int wbid = Integer.parseInt(req.getParameter("wbid"));
		String ViewPreference = req.getParameter("ViewPreference");


	     Enumeration paramNames = req.getParameterNames();
	     while( paramNames.hasMoreElements())
		 {
	            String paramName = (String)paramNames.nextElement();

		  }

	    String redirectURL = "/MyTables?tableId="+tableId+"&wbid="+wbid+"&ViewPreference="+ViewPreference+"&action=editTable";
		res.sendRedirect(req.getContextPath() + redirectURL);

    }




 //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //// Rename Column
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void updateColumn()
    {
        // get the table id and the column id
		tableId = Integer.parseInt(req.getParameter("tableId"));
		String ViewPreference = req.getParameter("ViewPreference");
		String designValues = req.getParameter("designValues");
		int colId = Integer.parseInt(req.getParameter("selectedColumnId"));
		String columnName = req.getParameter("columnName");
		String columnType = req.getParameter("columnType");
		String columnDefaultValue = req.getParameter("columnDefaultValue");
		String previousColumnDefaultValue = req.getParameter("previousColumnDefaultValue");
		int trackColId = Integer.parseInt(req.getParameter("trackLevelTableColumnId"));
		int lookupColId = Integer.parseInt(req.getParameter("lookupTableColumnId"));
		int lookupTableId = Integer.parseInt(req.getParameter("lookupTableId"));
		int trackingTableId = Integer.parseInt(req.getParameter("trackLevelTableId"));

		int colIntDefValue = 0;
		double colDoubleDefValue = 0;
		String colStrDefValue = " ";

		if ( 			previousColumnDefaultValue != null
				&& !previousColumnDefaultValue.trim().equals("")
				&& columnDefaultValue != null
				&& !columnDefaultValue.trim().equals("")
				&& ! previousColumnDefaultValue.equals( columnDefaultValue) )
		{
				if (columnType.equals("STRING"))
				{
					colStrDefValue = columnDefaultValue;
				}
				else if (columnType.equals("INTEGER"))
				{
					try
					{
						colIntDefValue = Integer.parseInt(columnDefaultValue);
					}
					catch( Exception e )
					{
						colIntDefValue =0;
					}
				}
				else if (columnType.equals("FLOAT"))
				{
					try
					{
						colDoubleDefValue = Double.parseDouble(columnDefaultValue);
					}
					catch( Exception e )
					{
						colDoubleDefValue =0.0;
					}
				}
		}
		else if ( columnDefaultValue != null && !columnDefaultValue.trim().equals(""))
		{
				if (columnType.equals("STRING"))
					{
						colStrDefValue = columnDefaultValue;
					}
					else if (columnType.equals("INTEGER"))
					{
						try
						{
							colIntDefValue = Integer.parseInt(columnDefaultValue);
						}
						catch( Exception e )
						{
							colIntDefValue =0;
						}
					}
					else if (columnType.equals("FLOAT"))
					{
						try
						{
							colDoubleDefValue = Double.parseDouble(columnDefaultValue);
						}
						catch( Exception e )
						{
							colDoubleDefValue =0.0;
						}
				}

		}

		System.out.println( "columnType=" + columnType);
		System.out.println( "previousColumnDefaultValue=" + previousColumnDefaultValue);
		System.out.println( "columnDefaultValue=" + columnDefaultValue);
		System.out.println( "colStrDefValue=" + colStrDefValue);
		System.out.println( "colIntDefValue=" + colIntDefValue);
		System.out.println( "colDoubleDefValue=" + colDoubleDefValue);


        Connection connection = null;
        TransactionManager tm = null;
        try
        {
            DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
            connection = databaseloader.getConnection();
            tm = new TransactionManager(connection, userId);
            int tid = tm.startTransaction();
            ColumnManager.updateColumn(connection, colId, columnName,colStrDefValue,colIntDefValue, colDoubleDefValue,  lookupTableId, trackingTableId,tid, lookupColId,trackColId );
            tm.commitTransaction();

        }
        catch ( SystemException sys )
        {
           sys.printStackTrace();
           try
           {
                tm.rollbackTransaction();
           }
           catch( SQLException sqlfatal )
           {
               sqlfatal.printStackTrace();
           }
        }
        catch ( SQLException e )
        {
           e.printStackTrace();
           try
           {
                tm.rollbackTransaction();
           }
           catch( SQLException sqlfatal )
           {
               sqlfatal.printStackTrace();
           }
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



        try
        {
            // return to table edit mode
           	    String redirectURL = "/MyTables?tableId="+tableId+"&ViewPreference="+ViewPreference+"&action=editTable&designValues="+designValues;
				res.sendRedirect(req.getContextPath() + redirectURL);
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //// Delete Column
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void deleteColumn()
    {
        // get the table id and the column id
		tableId = Integer.parseInt(req.getParameter("tableId"));
		int wbid = Integer.parseInt(req.getParameter("wbid"));
		String ViewPreference = req.getParameter("ViewPreference");
		String designValues = req.getParameter("designValues");
		int colId = Integer.parseInt(req.getParameter("selectedColumnId"));

        Connection connection = null;
        TransactionManager tm = null;
        try
        {
            DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
            connection = databaseloader.getConnection();
            tm = new TransactionManager(connection, userId);
            int tid = tm.startTransaction();
           	TableManager.lockTableForUpdate( connection, tableId);
            ColumnManager.deleteColumn(connection, colId, tid);
			TableManager.updateUserExportTid(connection, tableId, userId, tid);
            tm.commitTransaction();

        }
        catch ( SQLException e )
		{
		   e.printStackTrace();
		   try
		   {
				tm.rollbackTransaction();
				String m_failureReason =  " The table is being updated by a different user, please try later";
				BoardwalkException bwe = new BoardwalkException( 12008 );
				req.setAttribute("com.boardwalk.exception.BoardwalkException", bwe);
		   }
		   catch( SQLException sqlfatal )
		   {
			   sqlfatal.printStackTrace();
		   }
        }
        catch ( SystemException sys )
        {
           sys.printStackTrace();
           try
           {
                tm.rollbackTransaction();
           }
           catch( SQLException sqlfatal )
           {
               sqlfatal.printStackTrace();
           }
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



        try
        {
            // return to table edit mode
           	    String redirectURL = "/MyTables?tableId="+tableId+"&wbid="+wbid+"&ViewPreference="+ViewPreference+"&action=editTable&designValues="+designValues;
				res.sendRedirect(req.getContextPath() + redirectURL);
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }


    public void addColumn()
    throws ServletException, IOException {

	tableId = Integer.parseInt(req.getParameter("tableId"));

	req.setAttribute("TableId", new Integer(tableId));
	ColumnTypeManager cManager = new ColumnTypeManager();
	Vector columnTypes = cManager.getcolumntypelist();

      Iterator i = columnTypes.iterator();


        while(i.hasNext()) {
	    Columntype c = (Columntype)i.next();

	}
	req.setAttribute("columnTypes", columnTypes);

        String columnName = "ColumnName";

        req.setAttribute("columnName", columnName);

	sc.getRequestDispatcher("/jsp/collaboration/tables/add_column.jsp").
		forward(req, res);
    }



    public void commitColumn(boolean addAnother)
    throws ServletException, IOException
    {
		int afterColId = -1;
		int lookupTableId = -1;
		int lookupTableColumnId = -1;

		int trackLevelTableId = -1;
		int trackLevelTableColumnId = -1;

		// get the request parameters
		tableId = Integer.parseInt(req.getParameter("tableId"));
		String columnType = (String)req.getParameter("columnType");
		String columnName = (String)req.getParameter("columnName");
		String columnDefaultValue = (String)req.getParameter("columnDefaultValue");
		String selectedColIdStr = req.getParameter("selectedColumnId");
		String lookupTableIdStr = req.getParameter("lookupTableId");
		String lookupTableColumnIdStr = req.getParameter("lookupTableColumnId");

		String trackLevelTableIdStr = req.getParameter("trackLevelTableId");
		String trackLevelTableColumnIdStr = req.getParameter("trackLevelTableColumnId");


		if (selectedColIdStr != null)
		{
			afterColId = Integer.parseInt(selectedColIdStr);
		}

		if (lookupTableIdStr != null && lookupTableColumnIdStr != null)
		{
			lookupTableId = Integer.parseInt(lookupTableIdStr);
			lookupTableColumnId = Integer.parseInt(lookupTableColumnIdStr);
		}

		if (trackLevelTableIdStr != null && trackLevelTableColumnIdStr != null)
			{
				trackLevelTableId = Integer.parseInt(trackLevelTableIdStr);
				trackLevelTableColumnId = Integer.parseInt(trackLevelTableColumnIdStr);
		}


		System.out.println("commitColumn()::lookupTableId" + lookupTableId);
		System.out.println("commitColumn()::lookupTableColumnId" + lookupTableColumnId);
		System.out.println("commitColumn()::trackLevelTableId" + trackLevelTableId);
		System.out.println("commitColumn()::trackLevelTableColumnId" + trackLevelTableColumnId);

		//System.out.println("commitColumn()::Inserting column after column id" + afterColId);

		String ViewPreference = req.getParameter("ViewPreference");

		String 		stringDefaultValue = " ";
		int 			intDefaultValue = 0;
		double 	doubleDefaultValue = 0.0;
		int  			tableDefaultValue = -1;
		String 		CellDefaultValue = "";


		if ( columnDefaultValue != null )
		{

			if ( columnType.equals("STRING") )
			{
				stringDefaultValue = columnDefaultValue;
			}
			else if ( columnType.equals("FLOAT") )
			{
				if ( columnDefaultValue != null  && !columnDefaultValue.trim().equals("") )
				{
					doubleDefaultValue = Double.parseDouble(	columnDefaultValue );
				}

			}
			else if( columnType.equals("INTEGER")  )
			{
				if ( columnDefaultValue != null && !columnDefaultValue.trim().equals(""))
				{
					intDefaultValue = Integer.parseInt(	columnDefaultValue );
				}
			}

		}



		Connection connection = null;
		TransactionManager tm = null;
		//updateTableDB();
        try
		{
			DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
			connection = databaseloader.getConnection();

			tm = new TransactionManager( connection,userId);
			int tid = tm.startTransaction();


			TableManager.lockTableForUpdate( connection, tableId);
			 // see if default column access needs to be set
			 boolean setDefaultAccess = false;

			 //String param = getServletConfig().getInitParameter("BW_RESTRICT_NEW_COLUMN");
			 //System.out.println("BW_RESTRICT_NEW_COLUMN="+param);
			 //if (param != null)
			 //{
			 //   setDefaultAccess = true;
			 //   System.out.println("Restricting column by default");
			 //}
		 	 // create the column
			int columnId = TableManager.createColumn
								(connection,
								 tableId,
								 columnName,
								 columnType,
								 stringDefaultValue,
								 intDefaultValue,
								 doubleDefaultValue,
								 tableDefaultValue,
								 CellDefaultValue,
								 lookupTableId,
								 lookupTableColumnId,
								 trackLevelTableId,
								 trackLevelTableColumnId,
								 afterColId,
								 1,
								 tid,
								 setDefaultAccess,
								 -1
								 );
			 //System.out.println(" columnId " + columnId);

			TableManager.resequenceColumns(connection, tableId);

			TableManager.updateUserExportTid(connection, tableId, userId, tid);

			tm.commitTransaction();
		}
		catch ( SQLException e )
		{
		   e.printStackTrace();
		   try
		   {
				tm.rollbackTransaction();
				String m_failureReason =  " The table is being updated by a different user, please try later";
				BoardwalkException bwe = new BoardwalkException( 12008 );
				req.setAttribute("com.boardwalk.exception.BoardwalkException", bwe);

		   }
		   catch( SQLException sqlfatal )
		   {
			   sqlfatal.printStackTrace();
		   }
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		   try
		   {
				tm.rollbackTransaction();
		   }
		   catch( SQLException sql )
		   {
			   sql.printStackTrace();
		   }
		   e.printStackTrace();
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

        if (addAnother == true)
			addColumn();
		else
		{
			String redirectURL = "/MyTables?tableId="+tableId+"&ViewPreference="+ViewPreference+"&action=editTable";
			res.sendRedirect(req.getContextPath() + redirectURL);

		}
    }

public void chooseNhForTableContents()
throws ServletException, IOException
{
		HttpSession hs = req.getSession(true);
		int memberId = ((Integer)hs.getAttribute("memberId")).intValue();
		int userId = ((Integer)hs.getAttribute("userId")).intValue();
		int nhId =  ((Integer)hs.getAttribute("nhId")).intValue();

		tableId = Integer.parseInt(req.getParameter("tableId"));
		req.setAttribute("tableId", new Integer(tableId));

		String ViewPreference = req.getParameter("ViewPreference");
		String QueryPreference = req.getParameter("QueryPreference");
		String showChildrenNhsOnly = req.getParameter("showChildrenNhsOnly");

		boolean showChildrenOnly = false;

		if ( showChildrenNhsOnly != null && showChildrenNhsOnly.equals("true") )
		{
			showChildrenOnly = true;
		}


		System.out.println(" View preference from the browser " + ViewPreference );
		System.out.println(" QueryPreference from the browser " + QueryPreference );
		System.out.println(" showChildrenNhsOnly from the browser " + showChildrenNhsOnly );


		if ( ViewPreference == null )
			ViewPreference = "";

		if ( QueryPreference == null )
			QueryPreference = "";

		ViewPreference = ViewPreference.trim();
		QueryPreference = QueryPreference.trim();

		req.setAttribute("ViewPreference", ViewPreference);
		req.setAttribute("QueryPreference", QueryPreference);

		Connection connection = null;
		Vector Nhtree = null;
		try
		{

				DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
				 connection = databaseloader.getConnection();

				if ( ( ViewPreference.equals(ViewPreferenceType.LATEST_VIEW_OF_ALL_USERS_IN_ANY_NH)  || ViewPreference.equals(ViewPreferenceType.LATEST_ROWS_OF_ALL_USERS_IN_ANY_NH)) &&  !showChildrenOnly )
				{
						 Nhtree = NeighborhoodManager.getNeighborhoodTree( connection, userId);
				}
				else
				if ( ViewPreference.equals(ViewPreferenceType.LATEST_VIEW_OF_ALL_USERS_IN_ANY_NH)  || ViewPreference.equals(ViewPreferenceType.LATEST_ROWS_OF_ALL_USERS_IN_ANY_NH) && showChildrenOnly )
				{
						 Nhtree = NeighborhoodManager.getNeighborhoodTreeUnderSpecificNeighborhood(connection, nhId);
				}
				else
				{
					 		Nhtree = NeighborhoodManager.getNeighborhoodTreeUnderSpecificNeighborhood(connection, nhId);
				}
		}
		catch ( Exception e )
		  {
			 e.printStackTrace();
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
		req.setAttribute("nhTitle","Neighborhood Tree");
		req.setAttribute("nhTree",Nhtree);

	    sc.getRequestDispatcher("/jsp/collaboration/tables/choose_nh_for_table_query.jsp").
		forward(req, res);

}



  public void displayInPlainHTML()
  throws ServletException, IOException
      {

  	tableId = Integer.parseInt(req.getParameter("tableId"));
  	req.setAttribute("TableId", new Integer(tableId));

  	int nhId = -1;
  	boolean queryOnChildrenOfNh = false;
  	Vector selectedNhIds = new Vector();


	String ViewPreference = req.getParameter("ViewPreference");
	String QueryPreference = req.getParameter("QueryPreference");

	System.out.println(" View preference from the browser " + ViewPreference );
	System.out.println(" QueryPreference from the browser " + QueryPreference );

	if ( ViewPreference == null )
		ViewPreference = "";

	if ( QueryPreference == null )
		QueryPreference = "";

	ViewPreference = ViewPreference.trim();
	QueryPreference = QueryPreference.trim();



	if (			 ViewPreference.equals( ViewPreferenceType.LATEST_VIEW_OF_ALL_USERS_IN_ANY_NH)
			||		 ViewPreference.equals( ViewPreferenceType.LATEST_VIEW_OF_ALL_USERS_IN_ANY_CHILDREN_NH)
			||		 ViewPreference.equals( ViewPreferenceType.LATEST_ROWS_OF_ALL_USERS_IN_ANY_NH)
			||		 ViewPreference.equals( ViewPreferenceType.LATEST_ROWS_OF_ALL_USERS_IN_MY_NH_AND_IMM_CHD)
			||		 ViewPreference.equals( ViewPreferenceType.LATEST_ROWS_OF_ALL_USERS_IN_MY_NH_AND_ALL_CHD)

		)
{
			String queryOnChildrenOfNhParam =  req.getParameter("queryOnChildrenOfNh");
			if ( queryOnChildrenOfNhParam.equals("true") )
					queryOnChildrenOfNh = true;
			Integer  selNhid = null;

			for (Enumeration en=req.getParameterNames();  en.hasMoreElements(); )
		 {
							         String name = (String)en.nextElement();

										//            System.out.println(">>>>>> passed name: " + name + " value: " + value);

							         int index = name.lastIndexOf("selNH");

									if ( index != -1 && index== 0 && ! name.equals("selNhid")  )
									{
										  selNhid = new Integer(req.getParameter(name));
										  selectedNhIds.addElement(selNhid);
										  break;

									}
				}


		System.out.println(" displayInPlainHTML nhid " + selNhid + "  queryOnChildrenOfNh " + queryOnChildrenOfNh );

}

   TableContents tbcon = null;
   TableInfo tbi = null;
   Connection connection = null;



          try
          {
              DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
              connection = databaseloader.getConnection();


               tbcon = TableManager.getTableContents(connection,
			                                                    tableId,
			                                                    userId,
			                                                    memberid,
			                                                    nhId,
			                                                    -1,
																ViewPreference,
																QueryPreference,
																selectedNhIds,
																queryOnChildrenOfNh,
																-1,
																10000000,
																-1,
																10000000
                                                  );

              tbi = TableManager.getTableInfo(connection,userId,tableId);
              tableName =tbi.getTableName();

			  req.setAttribute("TableName", tableName);
			  req.setAttribute("title", "Table " + tableName);

          }
          catch ( Exception e )
          {
             e.printStackTrace();
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
   		req.setAttribute("TableContents", tbcon);
        req.setAttribute("TableInfo", tbi);
        req.setAttribute("position", "false");


       req.setAttribute("ViewPreference", ViewPreference );
		req.setAttribute("QueryPreference", QueryPreference );


       sc.getRequestDispatcher("/jsp/collaboration/tables/display_table_in_html_table.jsp").
  	   forward(req,res);
  	}

public void addRowViaFormRequest()
throws ServletException, IOException
{
	tableId = Integer.parseInt(req.getParameter("tableId"));
	tableName = req.getParameter("tableName");
	String rowId =  req.getParameter("rowId");
	String ViewPreference  =  req.getParameter("ViewPreference");
	String redirectURL = "/MyTables?tableId="+tableId+"&ViewPreference="+ViewPreference+"&rowId="+rowId+"&action=addRowUsingAForm";
				res.sendRedirect(req.getContextPath() + redirectURL);
}



public void addRowUsingAForm()
    throws ServletException, IOException
    {
		tableId = Integer.parseInt(req.getParameter("tableId"));
		tableName = req.getParameter("tableName");
		String rowId =  req.getParameter("rowId");
		String ViewPreference  =  req.getParameter("ViewPreference");

		req.setAttribute("TableId", new Integer(tableId));
		req.setAttribute("TableName", tableName);
		req.setAttribute("title", "Add New Row");
		req.setAttribute("rowId", rowId);
		req.setAttribute("ViewPreference", ViewPreference);

		HttpSession hs = req.getSession(true);
		int memberId = ((Integer)hs.getAttribute("memberId")).intValue();
		int userId = ((Integer)hs.getAttribute("userId")).intValue();
		int nhId =  ((Integer)hs.getAttribute("nhId")).intValue();


		TableColumnInfo tbcolInfo = null;
        Connection connection = null;

        try
        {
			DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
			connection = databaseloader.getConnection();
			//System.out.println("Invoking getColumnsByTable " );
			tbcolInfo = ColumnManager.getTableColumnInfo(connection,tableId,-1,userId,memberId,-1,null);
			//System.out.println("Done getColumnsByTable " );
			if ( tbcolInfo != null )
			{
				req.setAttribute("TableColumnInfo", tbcolInfo);
			}
        }
        catch (SystemException s)
		{
			s.printStackTrace();
		    req.setAttribute("com.boardwalk.exception.SystemException", s);
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
		sc.getRequestDispatcher("/jsp/collaboration/tables/edit_row.jsp").
							    forward(req,res);
	}

	public void commitNewRow()
	    throws ServletException, IOException
	    {

		System.out.println("commitNewRow");
		tableId = Integer.parseInt(req.getParameter("tableId"));
		String ViewPreference = req.getParameter("ViewPreference");
		String tComment = req.getParameter("tableComment");
		String rowIdParam = req.getParameter("rowId");

		HttpSession hs = req.getSession(true);
		int nhId = ((Integer)hs.getAttribute("nhId")).intValue();


		int rowId = -1;

		if ( rowIdParam != null &&  !rowIdParam.equals("null"))
		{
			   if (  !rowIdParam.trim().equals("") )
					rowId = Integer.parseInt(rowIdParam);
		}



		// update the table database from user input
		// updateTableDB();
	        Connection connection = null;
	        TransactionManager tm = null;
	        try
	        {
	            DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
	            connection = databaseloader.getConnection();
	            tm = new TransactionManager(connection, userId);

	           int tid = tm.startTransaction( "Updated Table Content", tComment);

				TableManager.lockTableForUpdate( connection, tableId);

	            if ( rowId == -1 && ViewPreference.equals( ViewPreferenceType.MY_ROWS) )
				{
						System.out.println(" Calling getAfterRowIdforMyRows " );
						rowId = TableManager.getAfterRowIdforMyRows( connection,tableId,userId );
				}
				else
				if ( rowId == -1 && ViewPreference.equals( ViewPreferenceType.LATEST_ROWS_OF_ALL_USERS_IN_MY_NH) )
				{
						System.out.println(" Calling getAfterRowIdforMyGroupRows " );
						rowId = TableManager.getAfterRowIdforMyGroupRows( connection,tableId,nhId );
				}
				else
				if ( rowId == -1 && ViewPreference.equals( ViewPreferenceType.LATEST_ROWS_OF_ALL_USERS_IN_MY_NH_AND_IMM_CHD) )
				{
						System.out.println(" Calling getAfterRowIdforMyGroupAndChildrenRows " );
						rowId = TableManager.getAfterRowIdforMyGroupAndChildrenRows( connection,tableId,nhId );
				}
				else
				if ( rowId == -1 && ViewPreference.equals( ViewPreferenceType.LATEST_ROWS_OF_ALL_USERS_IN_MY_NH_AND_ALL_CHD) )
				{
						System.out.println(" Calling getAfterRowIdforMyGroupAndAllChildrenRows " );
						rowId = TableManager.getAfterRowIdforMyGroupAndAllChildrenRows( connection,tableId,nhId );
				}
	            System.out.println(" Tableid = " +  tableId + " after rowId = " +  rowId + " tid = " + tid );
	            int newRowId = TableManager.createRowA(connection, tableId, "", rowId, tid );
	 			System.out.println(" Tableid = " +  tableId + " after rowId = " +  rowId + " tid = " + tid  + "  rowId = " + newRowId);
				RowContents rowContents = RowManager.getRowContents( connection, newRowId ,-1, true, -1 );
				Vector columnNames = rowContents.getColumnNames();
				Hashtable cellsByColumnName = rowContents.getCellsByColumnName();

				System.out.println( columnNames );
				System.out.println( "********************" );
				System.out.println( cellsByColumnName );

				Enumeration paramNames = req.getParameterNames();

				Vector    vec_cellContents = new Vector();

				while( paramNames.hasMoreElements())
				{
					String paramName = (String)paramNames.nextElement();
					if ( paramName.startsWith("Cell") )
					{
						String columnId  = paramName.substring(4);
						String newVal = req.getParameter("Cell" + columnId);
						String cellType = req.getParameter("Type"+columnId);
						String columnName = req.getParameter("ColumnName"+columnId);
						System.out.println(" from jsp Cell for paramName  = " + paramName + " column Id = " + columnId + " name = " + columnName + "  cell type " + cellType  + " newVal = " + newVal  );
						Cell cellFromDb = (Cell)cellsByColumnName.get(columnName);

						if ( cellFromDb != null )
						{
							System.out.println(" Cell for column " + columnName + " is cell id " + cellFromDb.getId()  );
						}
						else
						{
								System.out.println(" Cell for column " + columnName + " is null " );
						}
						System.out.println( "********************" );

						CellContents cc = new CellContents(cellFromDb.getId(), cellType, newVal );
						//cc.printCellContents();
						vec_cellContents.addElement(cc);

					}

				}

			TableManager.commitCellsByCellId(connection, tid, vec_cellContents, false);
			TableManager.updateUserExportTid(connection, tableId, userId, tid);
			tm.commitTransaction();
		}
		catch ( Exception e )
				{
					e.printStackTrace();
				   try
				   {
					tm.rollbackTransaction();
				   }
				   catch( SQLException sql )
				   {
					   sql.printStackTrace();
				   }
				   e.printStackTrace();
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

		String redirectURL = "/MyTables?tableId="+tableId+"&ViewPreference="+ViewPreference+"&action=editTable";
		res.sendRedirect(req.getContextPath() + redirectURL);

		}



    public void editTableDone()
    throws ServletException, IOException {
	tableId = Integer.parseInt(req.getParameter("tableId"));
	req.setAttribute("TableId", new Integer(tableId));
//	int wbid = bwdb.getWbidByTableId(tableId);
	// direct the user to the whiteboard page
//	sc.getRequestDispatcher(
//		"/Whiteboard?wbid="+wbid).forward(req,res);
    }

    public void removeTable() throws ServletException, IOException
    {
		HttpSession hs = req.getSession(true);
		int memberId = ((Integer)hs.getAttribute("memberId")).intValue();
		int userId = ((Integer)hs.getAttribute("userId")).intValue();

		tableId = Integer.parseInt(req.getParameter("tableId"));
        int wbid = Integer.parseInt(req.getParameter("wbid"));
		req.setAttribute("TableId", new Integer(tableId));
        Connection connection = null;
        TransactionManager tm = null;
		//updateTableDB();

        try
        {
                DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
                connection = databaseloader.getConnection();

                TableAccessList tbl= TableManager.getTableAccessForMember( connection, memberId, tableId );
                TableLockInfo tblock = TableManager.isTableLocked( connection,  tableId );
				System.out.println("getTableAccessForMember returned the following access for memberId " + memberId );
			//	tbl.print();


                if ( tbl.canAdministerTable() )
                {
						TableManager.lockTableForUpdate( connection, tableId);


						if ( tblock.isLocked() )
						{
								if (  tblock.getLockedByUserId() == userId )
								{
									tm = new TransactionManager( connection,userId);
									int tid = tm.startTransaction();
									TableManager.purgeTable ( connection,tableId );
									tm.commitTransaction();
								}
								else
								{
										String message = "The table is locked by  " + tblock.getLockedByuser()  + " since " +  tblock.getLockTime();
										throw new BoardwalkException(12006,message);

								}
						}
						else
						{
									tm = new TransactionManager( connection,userId);
									int tid = tm.startTransaction();
									TableManager.purgeTable ( connection,tableId );
									tm.commitTransaction();
						}
				}
				else
				{
					System.out.println("No access to the table for this user ");
					throw new BoardwalkException(10005);
				}

        }
        catch ( SQLException e )
		{
		   e.printStackTrace();
		   try
		   {
				tm.rollbackTransaction();
				String m_failureReason =  " The table is being updated by a different user, please try later";
				BoardwalkException bwe = new BoardwalkException( 12008 );
				req.setAttribute("com.boardwalk.exception.BoardwalkException", bwe);

		   }
		   catch( SQLException sqlfatal )
		   {
			   sqlfatal.printStackTrace();
		   }
        }
         catch ( BoardwalkException e )
		{
		   req.setAttribute("com.boardwalk.exception.BoardwalkException", e);

        }
        catch (SystemException e1) {
            e1.printStackTrace();
            try
            {
                tm.rollbackTransaction();
            }
            catch( SQLException sqe )
            {
                sqe.printStackTrace();
            }
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

        sc.getRequestDispatcher("/Whiteboard?wbid="+wbid).forward(req,res);
    }

	public void getRowVersions() throws ServletException, IOException
	{
		long endDate;
		long startDate;

		String endDateStr = req.getParameter("endDate");
		String startDateStr = req.getParameter("startDate");
		java.util.Date d = new java.util.Date();
		endDate = d.getTime();
		startDate = 0;

		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		cal.setTime(d);

		String period = req.getParameter("period");
		if (period.equals("Week"))
		{
			cal.add(Calendar.DATE, -7);
			startDate = cal.getTime().getTime();
		}
		else if (period.equals("Month"))
		{
			cal.add(Calendar.MONTH, -1);
			startDate = cal.getTime().getTime();
		}
		else if (period.equals("Quarter"))
		{
			cal.add(Calendar.MONTH, -3);
			startDate = cal.getTime().getTime();
		}
		else if (period.equals("Year"))
		{
			cal.add(Calendar.YEAR, -1);
			startDate = cal.getTime().getTime();

		}
		else if (period.equals("Custom") && endDateStr != null && startDateStr != null)
		{
			endDate = Long.parseLong(endDateStr);
			startDate = Long.parseLong(startDateStr);
		}

		HttpSession hs = req.getSession(true);
		String rowIds =  req.getParameter("rowIds");
		String columnIds =  req.getParameter("columnids");
		String baseline =  req.getParameter("baseline");
		int userId = ((Integer)hs.getAttribute("userId")).intValue();
		int	memberid = ((Integer)hs.getAttribute("memberId")).intValue();
		int nhId = ((Integer)hs.getAttribute("nhId")).intValue();
		tableId = Integer.parseInt(req.getParameter("tableId"));

		String execlDump =  req.getParameter("execlDump");

		System.out.println("Number of modified row cell versions between startdate = "  + startDate + " end date = " + endDate + " Row ids = " + rowIds + " baseline = "+baseline);

		Connection connection = null;
		try
		{
			DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
			connection = databaseloader.getConnection();

			Vector columnList			= new Vector();
			ArrayList rowCellVersions	= new ArrayList();
			Vector lsLatestRows			= new Vector();
			ArrayList laReportTable		= new ArrayList();

			//ArrayList OrgRow = new ArrayList();
			try
			{
				// To get the column details...
				TableColumnInfo tci = ColumnManager.getTableColumnInfo(connection,tableId,-1,userId,memberid,-1,"");
				columnList = tci.getColumnVector();
				//System.out.println("get the column details...");

				// To get the modified row cells
				rowCellVersions = TableManager.getRowVersions( connection,rowIds,startDate,endDate,columnIds,baseline,tableId,userId,memberid);
				//System.out.println("get the modified row cells");

				lsLatestRows = RowManager.getRowContents( connection,rowIds,columnIds,tableId,userId,memberid);

				int liNoRows		= lsLatestRows.size();
				int modCount		= 0;
				int liRowId			= 0;
				int col				= 0;
				int liCellsinRow	= 0;
				Transaction trans	= null;
				VersionedCell modCell = null;
				boolean lbFirstRow	= true;
				int txid			= 0;

				Vector RowCells				= new Vector();
				ArrayList laReportRowsCells	= new ArrayList();


				//System.out.println("#### Table "+liNoRows);
				for(int row = 0; row < liNoRows ; row++)
				{
					RowCells	= (Vector) lsLatestRows.get(row);
					//System.out.println("#### No of Cells in Org Row "+RowCells.size());
					//System.out.println("#### No of Cells Mod in Org Row "+rowCellVersions.size());

					liCellsinRow = RowCells.size();
					lbFirstRow = true;

					VersionedCell cell	= (VersionedCell) RowCells.elementAt(0);
					liRowId				= cell.getRowId();

					while(modCount < rowCellVersions.size())
					{
						//System.out.println("#### mod cell index  "+modCount);
						boolean skipRow = false;
						boolean formulaModified = false;
						if(modCount < rowCellVersions.size())
								modCell		= (VersionedCell) rowCellVersions.get(modCount);
						trans	= modCell.getTransaction();
						txid	= trans.getId();
						long CreatedOn = trans.getCreatedOnTime();
						for(col = 0; col < liCellsinRow; col++)
						{
							if(modCount < rowCellVersions.size())
								modCell		= (VersionedCell) rowCellVersions.get(modCount);
							trans	= modCell.getTransaction();

							//System.out.println("#### col index "+col);
							//System.out.println("#### in loop modCount "+modCount);
							//System.out.println("#### txid "+txid);
							//System.out.println("#### trans.getId() "+trans.getId());

							cell	= (VersionedCell) RowCells.elementAt(col);
							//System.out.println("cell "+cell);
							if(liRowId == modCell.getRowId() && txid == trans.getId())
							{
								//System.out.println("##### latest cell "+cell.getId());
								//System.out.println("##### modifed cell "+modCell.getId() );

								if (cell.getId() == modCell.getId() )
								{

									String lsDescription =	"";
									if(cell.getFormula().equals(modCell.getFormula()))
									{

									formulaModified = false;
									lsDescription =			"changed value from '"+
															cell.getValueAsString() +
															"' to '"+
															modCell.getValueAsString()+"'";
									}

									else
									{
										  formulaModified = true;

											lsDescription = "changed value from '"+
															cell.getValueAsString() +
															"' to '"+
															modCell.getValueAsString()+
															"'..  changed Formula from '"+
															cell.getFormula() +
															"' to '"+
															modCell.getFormula()+"'";

									}

									modCell.setDescription(lsDescription);
									modCell.setformulaModified(formulaModified);
									VersionedCell copy = copyCell(modCell);
									RowCells.setElementAt(copy,col);
									laReportRowsCells.add(modCell);
									modCount++;
									//System.out.println("modCell "+modCell);
									//System.out.println("copy "+copy);
									//System.out.println("$$$ "+modCell.getValueAsString()+"\t "+lsDescription);

									if(modCount < rowCellVersions.size())
										modCell = (VersionedCell) rowCellVersions.get(modCount);
								}
								else
								{
									cell.setDescription("");
									laReportRowsCells.add(cell);
									//System.out.print("$$$ "+cell.getValueAsString()+"\t ");
								}
							}
							else
							{
								cell.setDescription("");
								laReportRowsCells.add(cell);
								//System.out.print("$$$ "+cell.getValueAsString()+"\t ");
							}
						}
						lbFirstRow = false;
						//System.out.println("");
						//System.out.println(modCell.getRowId() +" != "+liRowId);
						if(modCell.getRowId() != liRowId)
						{
							laReportTable.add(laReportRowsCells);
							laReportRowsCells = new ArrayList();
							break;
						}
					}
					if(laReportRowsCells.size() > 0 )
						laReportTable.add(laReportRowsCells);

				 } // transactions
				//System.out.println("get the modified row cells");
				laReportTable = filterRows(laReportTable, liCellsinRow, startDate, endDate);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}

			req.setAttribute("columnHeader", columnList);
			req.setAttribute("table", laReportTable);
			req.setAttribute("startDate", new Long(startDate));
			req.setAttribute("endDate", new Long(endDate));
			req.setAttribute("columnIds", getArrayFromStrTok(columnIds, ","));

			req.setAttribute("rowIds", rowIds);
			req.setAttribute("columnids", columnIds);
			req.setAttribute("baseline", baseline);
			req.setAttribute("tableId", new Integer(tableId));
			req.setAttribute("period", period);
			req.setAttribute("execlDump", execlDump);
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

		sc.getRequestDispatcher("/jsp/collaboration/tables/rowVersions.jsp"). forward(req,res);
	}

	public VersionedCell copyCell(VersionedCell a_cell)
	{
		VersionedCell newCell = new VersionedCell (a_cell.getId(), a_cell.getColumnId(), a_cell.getColumnName(),
								a_cell.getRowId(), a_cell.getType(), a_cell.getValueAsString(), a_cell.getIntValue(),
    							a_cell.getDoubleValue(), a_cell.getTableValue(), a_cell.getTableName(),
								a_cell.getTransaction(), a_cell.getFormula());
		newCell.setDescription(a_cell.getDescription());
		return newCell;
	}

	public ArrayList filterRows(ArrayList asTable, int aiColumnLength, long alStartDate, long alEndDate)
	{
		VersionedCell Cell = null;
		Transaction trans1 = null;
		ArrayList laFinalTable	= new ArrayList();
		ArrayList laNewRowCells = new ArrayList();
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		java.sql.Timestamp startDate	= new java.sql.Timestamp(alStartDate);
		java.sql.Timestamp endDate		= new java.sql.Timestamp(alEndDate);
		int liCellinTable = asTable.size();

		for(int i = 0; i < liCellinTable; i++)
		{
			ArrayList laRowsCells = (ArrayList) asTable.get(i);
			int rows = laRowsCells.size()/aiColumnLength;
			//System.out.println("######## No. of cells in a row in report "+laRowsCells.size());
			int index = 0;
			for(int j = 0; j < rows; j++)
			{
				boolean skipRow = true;

				for(int col = 0; col < aiColumnLength; col++)
				{
					if(index < laRowsCells.size())
					{
						//System.out.println("### index in for "+index);
						Cell = (VersionedCell) laRowsCells.get(index);
						trans1	= Cell.getTransaction();
						java.sql.Timestamp createdDate = new java.sql.Timestamp(trans1.getCreatedOnTime());

						if(!Cell.getDescription().equals("") && startDate.compareTo(createdDate) < 0 && endDate.compareTo(createdDate) > 0 )
						{
							//System.out.println(startDate + " <= " + createdDate + " && " + endDate + " >= "  +createdDate );
							//System.out.println((startDate.compareTo(createdDate)) + " && " + (endDate.compareTo(createdDate)) );
							skipRow = false;
							index = index + aiColumnLength - col;
							break;
						}
						index++;
					}
					else
						break;
				}
				//System.out.println("### index "+index);
				index = index-aiColumnLength;

				if(!skipRow)
				{
					for(int col = 0; col < aiColumnLength; col++)
					{
						if(index < laRowsCells.size())
						{
							Cell = (VersionedCell) laRowsCells.get(index);
							laNewRowCells.add(Cell);
							index++;
						}
					}
				}
				else
					index = index+aiColumnLength;
			}
			laFinalTable.add(laNewRowCells);
			laNewRowCells = new ArrayList();
		}

		return laFinalTable;
	}

	/**
		This method returns a String array by splitting a string based on a token.
	**/
	public static String[] getArrayFromStrTok(String str, String tok)
	{
		//------------how to use this function-----------------------------
		//String arr[] = getArrayFromStrTok("this, is, a test, string", ",");
		StringTokenizer st = new StringTokenizer(str,tok);
		String arr[] = new String[st.countTokens()];
		int i=0;
		while (st.hasMoreTokens())
		{
			arr[i++] = st.nextToken();
		}

		return arr;
	}

    public void doGet (HttpServletRequest request,
	    HttpServletResponse response)throws ServletException, IOException

    {
	doPost(request, response);
    }



}
