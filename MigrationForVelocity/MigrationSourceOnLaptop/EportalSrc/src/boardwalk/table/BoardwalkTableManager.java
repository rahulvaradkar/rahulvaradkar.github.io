/*
 * @(#)TableManager.java	1.0
 *
 * Copyright 2004 BoardwalkTech, Inc. All rights reserved.
 * BoardwalkTech/CONFIDENTIAL. Use is subject to license terms.
 */
package boardwalk.table;

import java.sql.*;
import java.util.*;
import com.boardwalk.table.*;
import com.boardwalk.excel.*;
import com.boardwalk.exception.BoardwalkException;
import com.boardwalk.database.*;
import com.boardwalk.neighborhood.*;
import boardwalk.connection.*;
/**
 * Service to create and manager Boardwalk tables
 */
 public class BoardwalkTableManager
 {

	/**
	* Create a new table in the Boardwalk database
	*
	* @param connection a <code>BoardwalkConnection</code> object
	* @param collaborationId the database id of the collaboration,
	* in which the table will be created
	* @param whiteboardId the database id of the whiteboard,
	* in which the table will be created
    * @param tableName the name for the table
    * @param description a short description to go with the table
	* @return the database id of the new table
	* @exception BoardwalkException if a database access error occurs
	*/
	public static int createTable(BoardwalkConnection connection,
						   int collaborationId,
						   int whiteboardId,
						   String tableName,
						   String description)
	throws BoardwalkException
	{
		int tableId = -1;
		TransactionManager tm = null;
		try
		{
			tm = new TransactionManager(connection.getConnection(), connection.getUserId());
			int tid = tm.startTransaction();
			tableId = TableManager.createTable( connection.getConnection(),
												whiteboardId,
												tableName,
												description,
												2, 1, 1,"LATEST",
												connection.getMemberId(),
												tid,
												1);

			// set default access control
            Vector accessLists = new Vector();
            Hashtable  relationships = NeighborhoodManager.getNeighborhoodRelationships(
											connection.getConnection(),
            								connection.getNeighborhoodId());
			Enumeration relationKeys = relationships.keys();

			if ( relationships.size() > 0 )
			{
				while ( relationKeys.hasMoreElements() )
				{
					String relationship = (String)relationKeys.nextElement();
					NewTableAccessList accessList = new NewTableAccessList(-1, tableId,relationship);
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

			NewTableAccessList publicAccessList = new NewTableAccessList(-1,tableId,"PUBLIC");
			NewTableAccessList creatorAccessList = new NewTableAccessList(-1,tableId,"CREATOR");
			creatorAccessList.setAdministerTable();
			creatorAccessList.setAdministerColumn();
			creatorAccessList.setAddRow();
			creatorAccessList.setDeleteRow();
			creatorAccessList.setReadLatestOfTable();
			creatorAccessList.setWriteLatestOfTable();
			creatorAccessList.setReadWriteLatestOfMyRows();

			accessLists.add( creatorAccessList);
			accessLists.add( publicAccessList);


			if ( accessLists.size()  > 0 )
			{
				TableManager.addAccesstoTable
											(
											connection.getConnection(),
											tableId,
											accessLists,
											tid
											);
			}

			tm.commitTransaction();
		}
		catch (Exception e)
		{
			// bwe
			e.printStackTrace();
			try
			{
				tm.rollbackTransaction();
			}
			catch (SQLException sqe)
			{
				//bwe
				sqe.printStackTrace();
			}
		}

		return tableId;
	}

	/**
	* Lock a table for update in the Boardwalk database
	*
	* @param connection a <code>BoardwalkConnection</code> object
	* @param tableId the database id of the table to be deleted
	* @exception BoardwalkException if a database access error occurs
	*/
	public static void lockTable(BoardwalkConnection connection, int tableId)
	throws BoardwalkException
	{
		TransactionManager tm = null;
		try
		{
			tm = new TransactionManager(connection.getConnection(), connection.getUserId());
			int tid = tm.startTransaction();

			TableManager.lockTableForUpdate(connection.getConnection(), tableId);

			tm.commitTransaction();
		}
		catch (Exception e)
		{
			// bwe
			e.printStackTrace();
			try
			{
				tm.rollbackTransaction();
			}
			catch (SQLException sqe)
			{
				//bwe
				sqe.printStackTrace();
			}
		}
	}

	/**
	* Delete a table in the Boardwalk database
	*
	* @param connection a <code>BoardwalkConnection</code> object
	* @param tableId the database id of the table to be deleted
	* @exception BoardwalkException if a database access error occurs
	*/
	public static void deleteTable(BoardwalkConnection connection, int tableId)
	throws BoardwalkException
	{
		TransactionManager tm = null;
		try
		{
			tm = new TransactionManager(connection.getConnection(), connection.getUserId());
			int tid = tm.startTransaction();

			TableManager.purgeTable(connection.getConnection(), tableId);

			tm.commitTransaction();
		}
		catch (Exception e)
		{
			// bwe
			e.printStackTrace();
			try
			{
				tm.rollbackTransaction();
			}
			catch (SQLException sqe)
			{
				//bwe
				sqe.printStackTrace();
			}
		}
	}

	/**
	* Get <code>TableInfo</code> object given it's database id
	* @param connection a <code>BoardwalkConnection</code> object
	* @param tableId the database id
	* @return a <code>TableInfo</code> object
	* @exception BoardwalkException if a database access error occurs
	*/
/* FOR NOW
    public static TableInfo getTableInfo(BoardwalkConnection connection, int tableId)
    throws BoardwalkException
    {
		TableInfo tableInfo = null;
		try
		{
			tableInfo = TableManager.getTableInfo(connection.getConnection(),
											 connection.getUserId(),
											 tableId);
		}
		catch (Exception e)
		{

		}

		return tableInfo;
	}
*/
	/**
	* Create a new column (of type String) in a Boardwalk table
	* @param connection a <code>BoardwalkConnection</code> object
	* @param tableId the database id of table in which the column is inserted
	* @param afterColumnId the database id of the column after which the new column is inserted
	*	If the afterColumn is -1 then the new column will be inserted at the end
	* @param lookupColumnId the database id of the column to be used for values in a dropdown
	*   list
	* @param defaultValue the default value for new cells added in this column
	* @param referenceColumnId the database id of the column that the new column is based upon.
	* @return the database id of the new column
	* @exception BoardwalkException if a database access error occurs
	*/
    public static int createColumn(BoardwalkConnection connection,
								   int tableId,
								   String columnName,
								   int afterColumnId,
								   int lookupTableId,
								   int lookupColumnId,
								   String defaultValue,
								   int referenceColumnId
								   )
	throws BoardwalkException
	{
		int columnId = -1;
		TransactionManager tm = null;
		try
		{
			tm = new TransactionManager(connection.getConnection(), connection.getUserId());
			int tid = tm.startTransaction();
			columnId = TableManager.createColumn(
									connection.getConnection(),
									tableId,
									columnName,
									"STRING",
									defaultValue,
									1,1.0,-1,null,lookupTableId,lookupColumnId,-1,-1,
									afterColumnId, 1,
									tid,
									true,
									referenceColumnId
									);
			TableManager.resequenceColumns(connection.getConnection(), tableId);
			tm.commitTransaction();
		}
		catch (Exception e)
		{
			// bwe
			e.printStackTrace();
			try
			{
				tm.rollbackTransaction();
			}
			catch (SQLException sqe)
			{
				//bwe
				sqe.printStackTrace();
			}
		}

		return columnId;
	}
	public static void restrictAllColumns(
										BoardwalkConnection connection,
										int tableId)
	{
		TransactionManager tm = null;
		try
		{
			tm = new TransactionManager(connection.getConnection(), connection.getUserId());
			int tid = tm.startTransaction();

			ColumnManager.restrictAllColumns(connection.getConnection(), tableId, tid);

			tm.commitTransaction();
		}
		catch (Exception e)
		{
			// bwe
			e.printStackTrace();
			try
			{
				tm.rollbackTransaction();
			}
			catch (SQLException sqe)
			{
				//bwe
				sqe.printStackTrace();
			}
		}
	}

	public static void setColumnAccess(
								BoardwalkConnection connection,
								Vector columnIds,
								Vector relations,
								Vector access)
	{
		TransactionManager tm = null;
		try
		{
			tm = new TransactionManager(connection.getConnection(), connection.getUserId());
			int tid = tm.startTransaction();

			ColumnManager.updateColumnAccessBatch(connection.getConnection(), columnIds, relations, access,tid);

			tm.commitTransaction();
		}
		catch (Exception e)
		{
			// bwe
			e.printStackTrace();
			try
			{
				tm.rollbackTransaction();
			}
			catch (SQLException sqe)
			{
				//bwe
				sqe.printStackTrace();
			}
		}
	}

	public static Vector createUnrestrictedColumnsBatch(BoardwalkConnection connection,int tableId,Vector newColumns)
	throws BoardwalkException
	{
		Vector columnIds = null;
		TransactionManager tm = null;
		try
		{
			Vector columns = new Vector();
			for (int i = 0; i < newColumns.size(); i++)
			{
				BoardwalkNewColumn bc = (BoardwalkNewColumn)newColumns.elementAt(i);
				columns.addElement(bc.col);
			}
			tm = new TransactionManager(connection.getConnection(), connection.getUserId());
			int tid = tm.startTransaction();

			columnIds = TableManager.createStringColumnsBatch(
						connection.getConnection(), tableId, columns, tid);

			tm.commitTransaction();
		}
		catch (Exception e)
		{
			// bwe
			e.printStackTrace();
			try
			{
				tm.rollbackTransaction();
			}
			catch (SQLException sqe)
			{
				//bwe
				sqe.printStackTrace();
			}
		}

		return columnIds;
	}
    public static int createUnrestrictedColumn(BoardwalkConnection connection,
								   int tableId,
								   String columnName,
								   int afterColumnId,
								   int lookupTableId,
								   int lookupColumnId,
								   String defaultValue,
								   int referenceColumnId
								   )
	throws BoardwalkException
	{
		int columnId = -1;
		TransactionManager tm = null;
		int retryCount = 10;
		boolean transactionCompleted = false;
		do
		{
			try
			{
				tm = new TransactionManager(connection.getConnection(), connection.getUserId());
				int tid = tm.startTransaction();

				columnId = TableManager.createColumn(
										connection.getConnection(),
										tableId,
										columnName,
										"STRING",
										defaultValue,
										1,1.0,-1,null,lookupTableId,lookupColumnId,-1,-1,
										afterColumnId, 1,
										tid,
										false,
										referenceColumnId
										);
				TableManager.resequenceColumns(connection.getConnection(), tableId);
				tm.commitTransaction();
				transactionCompleted = true;
				if (retryCount < 10)
				{
					System.out.println("createColumn() transaction succeeded after " + (10 - retryCount) + " attempts");
				}
			}
			catch (SQLException e)
			{
				if (e.getErrorCode() == 1205)
				{
					retryCount--;
					System.out.println("DEADLOCK in createColumn(): RESUBMITTING");
					int randomSleep = 1000 + (int)(Math.random() * 30000);
					System.out.println("Sleeping for " + randomSleep + "ms");
					try
					{
						Thread.sleep(randomSleep);
					}
					catch(InterruptedException ie)
					{
						ie.printStackTrace();
					}
				}
				else
				{
					retryCount = 0;
					// bwe
					e.printStackTrace();
				}
			}

			finally
			{
				try
				{
					tm.rollbackTransaction();
				}
				catch (SQLException sqe)
				{
					//bwe
					sqe.printStackTrace();
				}
			}
		} while(!transactionCompleted && (retryCount > 0));

		return columnId;
	}

    public static void createCellsForNewTable(BoardwalkConnection connection,
								   int tableId,
								   Vector columnIds,
								   InputRowColumnCell cells [][]
								   )
	throws BoardwalkException
	{
		int columnId = -1;
		TransactionManager tm = null;
		try
		{
			tm = new TransactionManager(connection.getConnection(), connection.getUserId());
			int tid = tm.startTransaction();
			int numRows = cells.length;
			int numColumns = columnIds.size();

			// create the new rows
			Vector rows = new Vector();
			for (int r = 0; r < numRows; r++)
			{
				xlRow row = new  xlRow(r + 1, -1, (float)r+1, -1, r, "N");
				rows.addElement(row);
			}

			TableManager.createRowsNewTable(connection.getConnection(),
											tableId,
											tid,
											connection.getUserId(),
											rows);
			// fetch the new rows back
			String q1 = "select id, sequence_number from bw_row where bw_row.bw_tbl_id = ? and bw_row.tx_id = ? order by sequence_number";

			PreparedStatement preparedstatement = null;
			ResultSet resultset_r = null;
			try
			{
				preparedstatement = connection.getConnection().prepareStatement(q1);
				preparedstatement.setInt(1,tableId);
				preparedstatement.setInt(2,tid);
				resultset_r = preparedstatement.executeQuery();

				int nr = 0;
				while ( resultset_r.next() )
				{
					int rid = resultset_r.getInt("ID");
					float seq = resultset_r.getFloat("SEQUENCE_NUMBER");
					xlRow row = (xlRow)rows.elementAt(nr);
					row.setId(rid);
					nr++;
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
				tm.rollbackTransaction();
				return;
			}
			finally
			{
				if (preparedstatement != null)
					preparedstatement.close();
			}


			// create the cells
			Vector rowColCells = new Vector();
			for (int r = 0 ; r < numRows; r++)
			{
				for (int c = 0; c < numColumns; c++)
				{
					InputRowColumnCell ircc = (InputRowColumnCell)cells[r][c];
					int colid = ((Integer)columnIds.elementAt(c)).intValue();
					int rowid = ((xlRow)rows.elementAt(r)).getId();
					//System.out.println("cell col_id = " + colid + " row_id = " + rowid + " val = " + ircc.m_value);
					RowColumnCell rcc = new RowColumnCell( rowid,
														   colid,
														   ircc.m_type,
														   ircc.m_value,
														   ircc.m_formula
														 );
					rowColCells.addElement(rcc);

				}
			}

			TableManager.createCellsNewTable(connection.getConnection(), rowColCells, tid);
			tm.commitTransaction();
		}
		catch (Exception e)
		{
			// bwe
			e.printStackTrace();
			try
			{
				tm.rollbackTransaction();
			}
			catch (SQLException sqe)
			{
				//bwe
				sqe.printStackTrace();
			}
		}

		return;
	}


	/**
	* Set the reference column id for a given column
	* @param connection a <code>BoardwalkConnection</code> object
	* @param columnId the database id of table in which the column is inserted
	* @param referenceColumnId the default value for new cells added in this column
	* @return the database id of the new column
	* @exception BoardwalkException if a database access error occurs
	*/
    public static void setReferenceColumnId(BoardwalkConnection connection,
								   		   int columnId,
								           int referenceColumnId
								           )
	throws BoardwalkException
	{
		String upd_sql_str = "UPDATE BW_COLUMN SET SOURCE=? WHERE ID=" + columnId;
		CallableStatement updsrcstmt = null;
		try {
			updsrcstmt = connection.getConnection().prepareCall(upd_sql_str);
			updsrcstmt.setInt(1, referenceColumnId);
			updsrcstmt.executeUpdate();
		} catch (SQLException sql1) {
			sql1.printStackTrace();
		} finally {
			try {
				updsrcstmt.close();
			} catch (SQLException sql2) {
				sql2.printStackTrace();
			}
		}
	}

	/**
	* Create a new row in a Boardwalk table
	* @param connection a <code>BoardwalkConnection</code> object
	* @param tableId the database id of table in which the column is to be inserted
	* @param rowName name for the new row
	* @param afterRowId the database id of the row after which the new row is inserted
	*	If the afterRowId is -1 then the new row will be inserted as the first row
	* @return the database id of the new row
	*	will return -1 if the row could not be sucessfully created
	* @exception BoardwalkException if a database access error occurs
	*/
    public static int createRow( BoardwalkConnection connection,
    							int tableId,
    							String rowName,
    							int afterRowId)
	throws BoardwalkException
	{
		int rowId = -1;
		TransactionManager tm = null;
		try
		{
			tm = new TransactionManager(connection.getConnection(), connection.getUserId());
			int tid = tm.startTransaction();
			rowId = TableManager.createRowA( connection.getConnection(),
											 tableId,
											 rowName,
											 afterRowId,
											 tid
											);

			tm.commitTransaction();
		}
		catch (Exception e)
		{
			// bwe
			e.printStackTrace();
			try
			{
				tm.rollbackTransaction();
			}
			catch (SQLException sqe)
			{
				//bwe
				sqe.printStackTrace();
			}
		}

		return rowId;
	}
    public static int createRowBefore( BoardwalkConnection connection,
    							int tableId,
    							String rowName,
    							int afterRowId,
								int offset)
	throws BoardwalkException
	{
		int rowId = -1;
		TransactionManager tm = null;
		try
		{
			tm = new TransactionManager(connection.getConnection(), connection.getUserId());
			int tid = tm.startTransaction();
			rowId = TableManager.createRowBefore( connection.getConnection(),
											 tableId,
											 rowName,
											 afterRowId,
											 tid,
											 offset
											);

			tm.commitTransaction();
		}
		catch (Exception e)
		{
			// bwe
			e.printStackTrace();
			try
			{
				tm.rollbackTransaction();
			}
			catch (SQLException sqe)
			{
				//bwe
				sqe.printStackTrace();
			}
		}

		return rowId;
	}


	/**
	* Update column properties
	* @param connection a <code>BoardwalkConnection</code> object
	* @param columnId the database id of the column in Boardwalk
	* @param columnName the new name of the column
	* @param defaultValue the new default value for the column
	* @param lookupTableId the database id of the lookup table in Boardwalk
	* @param lookupColumnId the database id of the lookup column in Boardwalk
	* @exception BoardwalkException if a database access error occurs
	*/
    public static void updateColumn( BoardwalkConnection connection,
    							 	 int columnId,
    							 	 String columnName,
    							     String defaultValue,
    							     int lookupTableId,
    							     int lookupColumnId
    						       )
	throws BoardwalkException
	{
		TransactionManager tm = null;
		try
		{
			tm = new TransactionManager(connection.getConnection(), connection.getUserId());
			int tid = tm.startTransaction();
			ColumnManager.updateColumn( connection.getConnection(),
									 	columnId,
									 	columnName,
									 	defaultValue,
									 	1,
									 	1.0,
									 	lookupTableId,
									 	-1,
									 	tid,
									 	lookupColumnId,
									 	-1 );
			tm.commitTransaction();
		}
		catch (Exception e)
		{
			// bwe
			e.printStackTrace();
			try
			{
				tm.rollbackTransaction();
			}
			catch (SQLException sqe)
			{
				//bwe
				sqe.printStackTrace();
			}
		}
	}

	/**
	* Deactivate a column from Boardwalk
	* @param connection a <code>BoardwalkConnection</code> object
	* @param columnId the database id of the column to be deleted
	* @exception BoardwalkException if a database access error occurs
	*/
    public static void deactivateColumn( BoardwalkConnection connection,
    							 	 int columnId
    						       )
	throws BoardwalkException
	{
		TransactionManager tm = null;
		try
		{
			tm = new TransactionManager(connection.getConnection(), connection.getUserId());
			int tid = tm.startTransaction();
			ColumnManager.deleteColumn( connection.getConnection(),
									 	columnId, tid);
			tm.commitTransaction();
		}
		catch (Exception e)
		{
			// bwe
			e.printStackTrace();
			try
			{
				tm.rollbackTransaction();
			}
			catch (SQLException sqe)
			{
				//bwe
				sqe.printStackTrace();
			}
		}
	}

	/**
	* Change ownership of a row
	* @param connection a <code>BoardwalkConnection</code> object
	* @param rowId the database id of row in Boardwalk
	* @param userName login name of the user
	* @exception BoardwalkException if a database access error occurs
	*/
    public static void changeRowOwner( BoardwalkConnection connection,
    							 int rowId,
    							 String userName
    						   )
	throws BoardwalkException
	{
		TransactionManager tm = null;
		try
		{
			tm = new TransactionManager(connection.getConnection(), connection.getUserId());
			int tid = tm.startTransaction();
			TableManager.changeRowOwnership( connection.getConnection(),
											 rowId,
											 userName,
											 tid);
			tm.commitTransaction();
		}
		catch (Exception e)
		{
			// bwe
			e.printStackTrace();
			try
			{
				tm.rollbackTransaction();
			}
			catch (SQLException sqe)
			{
				//bwe
				sqe.printStackTrace();
			}
		}
	}
	/**
	* Create cells in a Boardwalk table
	* @param connection a <code>BoardwalkConnection</code> object
	* @param cells a <code>Vector</code> of <code>InputRowColumnCell</code> objects
	* @exception BoardwalkException if a database access error occurs
	*/
	public static void updateRowColumnCells ( BoardwalkConnection connection,
	 								 		  Vector cells
	 								        )
	throws BoardwalkException
	{
		Vector rowColCells = new Vector();
		Iterator rcci = cells.iterator();
		while (rcci.hasNext())
		{
			InputRowColumnCell ircc = (InputRowColumnCell)rcci.next();
			RowColumnCell rcc = new RowColumnCell( ircc.m_rowId,
												   ircc.m_columnId,
												   ircc.m_type,
												   ircc.m_value,
												   ircc.m_formula
												 );
			rowColCells.addElement(rcc);
		}

		TransactionManager tm = null;
		try
		{
			tm = new TransactionManager(connection.getConnection(), connection.getUserId());
			int tid = tm.startTransaction();

			TableManager.commitCellsByRowAndColumn( connection.getConnection(),
													tid,
													rowColCells,
													false);
			tm.commitTransaction();
		}
		catch (Exception e)
		{
			//bwe
			e.printStackTrace();
			try
			{
				tm.rollbackTransaction();
			}
			catch (SQLException sqe)
			{
				sqe.printStackTrace();
			}
		}
	}

	/**
	* Fetch the LATEST configuration of the Boardwalk table in the system
	* @param connection a <code>BoardwalkConnection</code> object
	* @param tableId the database id of the table
	* @return a <code>BoardwalkTableContents</code> object
	* @exception BoardwalkException if a database access error occurs
	*/
	public static BoardwalkTableContents  getTableContents(
									BoardwalkConnection connection,
									int tableId)
	throws BoardwalkException
	{
		BoardwalkTableContents bwtbcon = null;
		try
		{
			TableContents tbcon = TableManager.getTableContents_t(
				                             connection.getConnection(),
											 tableId ,
											 connection.getUserId(),
											 connection.getMemberId(),
											 connection.getNeighborhoodId(),
											 -1,
											 "LATEST",
											 "",
											 null,
											 false,
											 -1,
											 -1,
											 -1,
											 -1,
											 -1,
											 -1,
											 -1,
											 -1,
											 null
										    );
			bwtbcon = new BoardwalkTableContents(tbcon);
		}
		catch (Exception e)
		{
			// bwe
			e.printStackTrace();
		}

		return bwtbcon;
	}

	/**
	* Fetch the configuration of the Boardwalk table at specified time
	* @param connection a <code>BoardwalkConnection</code> object
	* @param tableId the database id of the table
	* @param time the time in ms
	* @return a <code>BoardwalkTableContents</code> object
	* @exception BoardwalkException if a database access error occurs
	*/
	public static BoardwalkTableContents  getTableContentsAtTime(
									BoardwalkConnection connection,
									int tableId,
									long time)
	throws BoardwalkException
	{
		BoardwalkTableContents bwtbcon = null;
		try
		{
			TableContents tbcon = TableManager.getTableContents_t(
				                             connection.getConnection(),
											 tableId ,
											 connection.getUserId(),
											 connection.getMemberId(),
											 connection.getNeighborhoodId(),
											 -1,
											 "LATEST",
											 "",
											 null,
											 false,
											 -1,
											 -1,
											 -1,
											 -1,
											 -1,
											 time,
											 -1,
											 -1,
											 null
										    );
			bwtbcon = new BoardwalkTableContents(tbcon);
		}
		catch (Exception e)
		{
			// bwe
			e.printStackTrace();
		}

		return bwtbcon;
	}
	/**
	* Fetch the configuration of the Boardwalk table at specified transaction
	* @param connection a <code>BoardwalkConnection</code> object
	* @param tableId the database id of the table
	* @param transactionId the database id of a <code>BoardwalkTransaction</code>
	* @return a <code>BoardwalkTableContents</code> object
	* @exception BoardwalkException if a database access error occurs
	*/
	public static BoardwalkTableContents  getTableContentsAtTransaction(
									BoardwalkConnection connection,
									int tableId,
									int transactionId)
	throws BoardwalkException
	{
		BoardwalkTableContents bwtbcon = null;
		try
		{
			TableContents tbcon = TableManager.getTableContents_t(
				                             connection.getConnection(),
											 tableId ,
											 connection.getUserId(),
											 connection.getMemberId(),
											 connection.getNeighborhoodId(),
											 -1,
											 "LATEST",
											 "",
											 null,
											 false,
											 -1,
											 -1,
											 -1,
											 -1,
											 transactionId,
											 -1,
											 -1,
											 -1,
											 null
										    );
			bwtbcon = new BoardwalkTableContents(tbcon);
		}
		catch (Exception e)
		{
			// bwe
			e.printStackTrace();
		}

		return bwtbcon;
	}
	/**
	* Diff a boardwalk table at two time instances
	* @param connection a <code>BoardwalkConnection</code> object
	* @param tableId the database id of the cell
	* @param startTime the start time comparing table versions
	* @param endTime the end time for comparing versions
	* @return a <code>BoardwalkTableContents</code> object that contains
	*   only the cells that are different at the two snapshots in time.
	*   The <code>BoardwalkCell</code> objects are cell versions for the
	*   table at the specified start time.
	* @exception BoardwalkException if a database access error occurs
	*/
	public static BoardwalkTableContents compareTableBetweenTimes(
											BoardwalkConnection connection,
											int tableId,
											long startTime,
											long endTime
											)
	throws BoardwalkException
	{
		BoardwalkTableContents bwtbcon = null;
		try
		{
			TableContents tbcon = TableManager.getTableContents_t(
				                             connection.getConnection(),
											 tableId ,
											 connection.getUserId(),
											 connection.getMemberId(),
											 connection.getNeighborhoodId(),
											 -1,
											 "LATEST",
											 "",
											 null,
											 false,
											 -1,
											 -1,
											 -1,
											 -1,
											 -1,
											 endTime,
											 -1,
											 startTime,
											 null
										    );
			bwtbcon = new BoardwalkTableContents(tbcon);
		}
		catch (Exception e)
		{
			// bwe
			e.printStackTrace();
		}

		return bwtbcon;
	}
	/**
	* Diff a boardwalk table at two transactions
	* @param connection a <code>BoardwalkConnection</code> object
	* @param tableId the database id of the cell
	* @param startTransactionId the start transaction comparing table versions
	* @param endTransactionId the end transaction for comparing versions
	* @return a <code>BoardwalkTableContents</code> object that contains
	*   only the cells that are different at the two snapshots in time.
	*   The <code>BoardwalkCell</code> objects are cell versions for the
	*   table at the specified start time.
	* @exception BoardwalkException if a database access error occurs
	*/
	public static BoardwalkTableContents compareTableBetweenTransactions(
											BoardwalkConnection connection,
											int tableId,
											int startTransactionId,
											int endTransactionId
											)
	throws BoardwalkException
	{
		BoardwalkTableContents bwtbcon = null;
		try
		{
			TableContents tbcon = TableManager.getTableContents_t(
				                             connection.getConnection(),
											 tableId ,
											 connection.getUserId(),
											 connection.getMemberId(),
											 connection.getNeighborhoodId(),
											 -1,
											 "LATEST",
											 "",
											 null,
											 false,
											 -1,
											 -1,
											 -1,
											 -1,
											 endTransactionId,
											 -1,
											 startTransactionId,
											 -1,
											 null
										    );
			bwtbcon = new BoardwalkTableContents(tbcon);
		}
		catch (Exception e)
		{
			// bwe
			e.printStackTrace();
		}

		return bwtbcon;
	}
	/**
	* Fetch the changes in a table because of a single transaction
	* @param connection a <code>BoardwalkConnection</code> object
	* @param tableId the database id of the cell
	* @param transactionId the database id of the transaction
	* @return a <code>BoardwalkTableContents</code> object that contains
	*   only the cells that are changed because of the specified transaction.
	*   The <code>BoardwalkCell</code> objects are cell versions for the
	*   table at the specified transaction.
	* @exception BoardwalkException if a database access error occurs
	*/
	public static BoardwalkTableContents getChangesForTransaction(
											BoardwalkConnection connection,
											int tableId,
											int transactionId
											)
	throws BoardwalkException
	{
		BoardwalkTableContents bwtbcon = null;
		try
		{
			TableContents tbcon = TableManager.getTableContents_t(
				                             connection.getConnection(),
											 tableId ,
											 connection.getUserId(),
											 connection.getMemberId(),
											 connection.getNeighborhoodId(),
											 -1,
											 "LATEST",
											 "",
											 null,
											 false,
											 -1,
											 -1,
											 -1,
											 -1,
											 transactionId,
											 -1,
											 transactionId,
											 -1,
											 null
										    );
			bwtbcon = new BoardwalkTableContents(tbcon);
		}
		catch (Exception e)
		{
			// bwe
			e.printStackTrace();
		}

		return bwtbcon;
	}

	/**
	* Fetch the versions of a cell during specified time period
	* @param connection a <code>BoardwalkConnection</code> object
	* @param cellId the database id of the cell
	* @param startTime the start time for fetching the versions
	* @param endTime the end time for fetching the versions
	* @return a <code>Vector</code> of <code>BoardwalkCell</code> objects
	* @exception BoardwalkException if a database access error occurs
	*/
	public static Vector getCellVersionsBetweenTimes(BoardwalkConnection connection,
											int cellId,
											long startTime,
											long endTime
											)
	throws BoardwalkException
	{
		Vector cells = new Vector();
		try
		{
			Hashtable cvht = TableManager.getCellVersions(connection.getConnection(),
														cellId,
														0,
														0,
														startTime,
														endTime);
			Vector cvtl = new Vector(cvht.keySet());
			Collections.sort(cvtl);
			Iterator cvtli = cvtl.iterator();
			while (cvtli.hasNext())
			{
				Integer tid = (Integer)cvtli.next();
				VersionedCell vc = (VersionedCell)cvht.get(tid);
				cells.addElement(new BoardwalkCell(vc));
			}
		}
		catch (Exception e)
		{
			// bwe
			e.printStackTrace();
		}

		return cells;
	}

	/**
	* Fetch the transactions on a table during specified time period
	* @param connection a <code>BoardwalkConnection</code> object
	* @param tableId the database id of the table
	* @param startTime the start time for fetching the versions
	* @param endTime the end time for fetching the versions
	* @return a <code>Vector</code> of <code>BoardwalkTransaction</code> objects
	*   The list of transaction may contain duplicate entries for the same transaction
	*   if the transaction resulted in multiple actions.
	* @exception BoardwalkException if a database access error occurs
	*/
	public static Vector getTransactionListBetweenTimes(
								BoardwalkConnection connection,
								int tableId,
								long startTime,
								long endTime)
	throws BoardwalkException
	{
		Vector txList = new Vector();
		try
		{
			Hashtable txht = TableManager.getTransactionList(
													connection.getConnection(),
													tableId,
													-1,
													-1,
													startTime,
													endTime,
													connection.getUserId(),
													connection.getNeighborhoodId(),
													"LATEST",
													true);
			Vector txhtk = new Vector(txht.keySet());
			Collections.sort(txhtk);
			Iterator txhtki = txhtk.iterator();
			while (txhtki.hasNext())
			{
				Integer txid = (Integer)txhtki.next();
				Vector txv = (Vector)txht.get(txid);
				Iterator txvi = txv.iterator();
				while (txvi.hasNext())
				{
					Transaction t = (Transaction)txvi.next();
					txList.addElement(new BoardwalkTransaction(t));
				}
			}
		}
		catch (Exception e)
		{
			//bwe
			e.printStackTrace();
		}

		return txList;
	}
	/**
	* Fetch the transactions on a table between specified transactions
	* @param connection a <code>BoardwalkConnection</code> object
	* @param tableId the database id of the table
	* @param startTransactionId the database id of start transaction for fetching the versions
	* @param endTransactionId the database id of end transaction for fetching the versions
	* @return a <code>Vector</code> of <code>BoardwalkTransaction</code> objects
	*   The list of transaction may contain duplicate entries for the same transaction
	*   if the transaction resulted in multiple actions.
	* @exception BoardwalkException if a database access error occurs
	*/
	public static Vector getTransactionListBetweenTransactions(
								BoardwalkConnection connection,
								int tableId,
								int startTransactionId,
								int endTransactionId)
	throws BoardwalkException
	{
		Vector txList = new Vector();
		try
		{
			Hashtable txht = TableManager.getTransactionList(
													connection.getConnection(),
													tableId,
													startTransactionId,
													endTransactionId,
													-1,
													-1,
													connection.getUserId(),
													connection.getNeighborhoodId(),
													"LATEST",
													true);
			Vector txhtk = new Vector(txht.keySet());
			Collections.sort(txhtk);
			Iterator txhtki = txhtk.iterator();
			while (txhtki.hasNext())
			{
				Integer txid = (Integer)txhtki.next();
				Vector txv = (Vector)txht.get(txid);
				Iterator txvi = txv.iterator();
				while (txvi.hasNext())
				{
					Transaction t = (Transaction)txvi.next();
					txList.addElement(new BoardwalkTransaction(t));
				}
			}
		}
		catch (Exception e)
		{
			//bwe
			e.printStackTrace();
		}

		return txList;
	}
	/**
	* Fetch the column information for a given table
	* @param connection a <code>BoardwalkConnection</code> object
	* @param tableId the database id of the table
	* @return a <code>Vector</code> of <code>BoardwalkColumn</code> objects
	* @exception BoardwalkException if a database access error occurs
	*/
	public static Vector getColumns(
								BoardwalkConnection connection,
								int tableId)
	throws BoardwalkException
	{
		Vector columnList = new Vector();
		try
		{
			TableColumnInfo tci =  ColumnManager.getTableColumnInfo(
											connection.getConnection(),
											tableId,
											-1,
											connection.getUserId(),
											connection.getMemberId(),
											-1,
											"");
			Vector cl = tci.getColumnVector();
			Iterator cli = cl.iterator();
			while (cli.hasNext())
			{
				Column c = (Column)cli.next();
				BoardwalkColumn bc = new BoardwalkColumn(c);
				columnList.addElement(bc);
			}
		}
		catch (Exception e)
		{
			//bwe
			e.printStackTrace();
		}

		return columnList;
	}
	/**
	* Fetch the column ids for a given table
	* @param connection a <code>BoardwalkConnection</code> object
	* @param tableId the database id of the table
	* @return a <code>Vector</code> of <code>BoardwalkColumn</code> objects
	* @exception BoardwalkException if a database access error occurs
	*/
	public static Vector getColumnsId(
								BoardwalkConnection connection,
								int tableId)
	throws BoardwalkException
	{
		Vector columnIdList = new Vector();
		try
		{
			columnIdList =  ColumnManager.getTableColumnIds(
											connection.getConnection(),
											tableId,
											-1,
											connection.getUserId(),
											connection.getMemberId(),
											-1,
											"");
		}
		catch (Exception e)
		{
			//bwe
			e.printStackTrace();
		}

		return columnIdList;
	}
	/**
	* Fetch the transaction information for a given transaction database id
	* @param connection a <code>BoardwalkConnection</code> object
	* @param transactionId the database id of the transaction
	* @return a <code>BoardwalkTransaction</code> object
	* @exception BoardwalkException if a database access error occurs
	*/
	public static BoardwalkTransaction getTransaction(
								BoardwalkConnection connection,
								int transactionId)
	throws BoardwalkException
	{
		BoardwalkTransaction  bt = null;
		try
		{
			Transaction t =  TransactionManager.getTransaction(
									connection.getConnection(),
									transactionId);
			bt = new BoardwalkTransaction(t);
		}
		catch (Exception e)
		{
			//bwe
			e.printStackTrace();
		}

		return bt;
	}
 };