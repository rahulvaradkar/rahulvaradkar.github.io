/*
 * @(#)TableManager.java    1.0
 *
 * Copyright 2004 BoardwalkTech, Inc. All rights reserved.
 * BoardwalkTech/CONFIDENTIAL. Use is subject to license terms.
 */
package boardwalk.table;

import java.sql.*;
import java.io.*;
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
 public class BoardwalkBulkDataManager
 {
	int tid = -1;
	TransactionManager tm = null;
	BoardwalkConnection bwcon = null;
	StringBuffer newColumnBuffer = null;
	StringBuffer newRowBuffer = null;
	StringBuffer newCellBuffer = null;
	public final static String Seperator = new Character((char)1).toString();
	public final static String ContentDelimeter = new Character((char)2).toString();
	private static String CALL_BW_CR_NEW_COLUMNS = "{CALL BW_CR_NEW_COLUMNS(?)}";
	private static String CALL_BW_CR_NEW_ROWS = "{CALL BW_CR_NEW_ROWS(?)}";
	private static String CALL_BW_CR_NEW_CELLS = "{CALL BW_CR_NEW_CELLS(?,?)}";

	public BoardwalkBulkDataManager(BoardwalkConnection connection)
	{
		tid = -1;
		newColumnBuffer = null;
		newRowBuffer = null;
		newCellBuffer = null;
		tm = null;
		bwcon = connection;
	}

	public void start(BoardwalkConnection connection)
	{
		try
		{
			tm = new TransactionManager(connection.getConnection(), connection.getUserId());
			tid = tm.startTransaction();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void addNewColumn(
						int tableId,
						String columnName,
						double sequenceNumber,
						int lookupTableId,
						int lookupColumnId,
						String defaultValue,
						int referenceColumnId)
	{
		if (newColumnBuffer == null)
		{
			newColumnBuffer = new StringBuffer();
		}
		/*name, tableid, type, seqno, tid, defstr, defint, defdbl, deftbl, deflkptbl, deflkpcol, isenum, width, isactive, source*/
		newColumnBuffer.append(-1);
		newColumnBuffer.append(Seperator);
		newColumnBuffer.append(columnName);
		newColumnBuffer.append(Seperator);
		newColumnBuffer.append(tableId);
		newColumnBuffer.append(Seperator);
		newColumnBuffer.append("STRING");
		newColumnBuffer.append(Seperator);
		newColumnBuffer.append(sequenceNumber);
		newColumnBuffer.append(Seperator);
		newColumnBuffer.append(tid);
		newColumnBuffer.append(Seperator);
		newColumnBuffer.append(defaultValue);
		newColumnBuffer.append(Seperator);
		newColumnBuffer.append(1);
		newColumnBuffer.append(Seperator);
		newColumnBuffer.append(1.0);
		newColumnBuffer.append(Seperator);
		newColumnBuffer.append(-1);
		newColumnBuffer.append(Seperator);
		newColumnBuffer.append(lookupTableId);
		newColumnBuffer.append(Seperator);
		newColumnBuffer.append(lookupColumnId);
		newColumnBuffer.append(Seperator);
		if (lookupColumnId != -1)
		{
			newColumnBuffer.append(1);
			newColumnBuffer.append(Seperator);
		}
		else
		{
			newColumnBuffer.append(0);
			newColumnBuffer.append(Seperator);
		}
		newColumnBuffer.append(24);
		newColumnBuffer.append(Seperator);
		newColumnBuffer.append(1);
		newColumnBuffer.append(Seperator);
		newColumnBuffer.append(-1);
		newColumnBuffer.append(ContentDelimeter);
	}

	public void addNewRow(
					int tableId,
					double sequenceNumber,
					int ownerId
					)
	{
		if (newRowBuffer == null)
		{
			newRowBuffer = new StringBuffer();
		}
		//id,name,table,tid,seq,active,owner,ownertid,source
		newRowBuffer.append(-1);
		newRowBuffer.append(Seperator);
		newRowBuffer.append(sequenceNumber);
		newRowBuffer.append(Seperator);
		newRowBuffer.append(tableId);
		newRowBuffer.append(Seperator);
		newRowBuffer.append(tid);
		newRowBuffer.append(Seperator);
		newRowBuffer.append(sequenceNumber);
		newRowBuffer.append(Seperator);
		newRowBuffer.append(1);
		newRowBuffer.append(Seperator);
		newRowBuffer.append(ownerId);
		newRowBuffer.append(Seperator);
		newRowBuffer.append(tid);
		newRowBuffer.append(Seperator);
		newRowBuffer.append(-1);
		newRowBuffer.append(ContentDelimeter);
	}

	public void addNewCell(
					int tableId,
					double rowSequenceNumber,
					double colSequenceNumber,
					String value,
					String formula
					)
	{
		if (newCellBuffer == null)
		{
			newCellBuffer = new StringBuffer();
		}
		newCellBuffer.append(tableId);
		newCellBuffer.append(Seperator);
		newCellBuffer.append(rowSequenceNumber);
		newCellBuffer.append(Seperator);
		newCellBuffer.append(colSequenceNumber);
		newCellBuffer.append(Seperator);
		newCellBuffer.append(value);
		newCellBuffer.append(Seperator);
		newCellBuffer.append(formula);
		newCellBuffer.append(ContentDelimeter);
	}

	public void commitNewColumns()
	throws SQLException, IOException
	{
		if (newColumnBuffer == null)
		{
			return;
		}

		//declared here only to make visible to finally clause; generic reference
		Writer output = null;
		CallableStatement callablestatement = null;
		try
		{
			File colFile = File.createTempFile((new java.util.Date()).getTime()+"COL", ".txt");
			colFile.deleteOnExit();
			output = new BufferedWriter( new FileWriter(colFile) );
			output.write( newColumnBuffer.toString() );
			output.close();

			callablestatement = bwcon.getConnection().prepareCall(CALL_BW_CR_NEW_COLUMNS);
			System.out.println("column file = " + colFile.getAbsolutePath());
			callablestatement.setString(1,colFile.getAbsolutePath());

			int result = callablestatement.executeUpdate();

			callablestatement.close();

		}
		catch (Exception e)
		{
			try
			{
				System.out.println("Rolling back transaction");
				tm.rollbackTransaction();
				tm = null;
			}
			catch (SQLException sqe)
			{
				e.printStackTrace();
			}
			if (e instanceof SQLException)
				throw (SQLException)e;
			else if (e instanceof IOException)
				throw (IOException)e;
		}
		finally
		{
			try
			{
				callablestatement.close();
			}
			catch (SQLException e)
			{
				throw e;
			}
		}
	}
	public void commitNewRows()
	throws SQLException, IOException
	{
		if (newRowBuffer == null)
		{
			return;
		}

		//declared here only to make visible to finally clause; generic reference
		Writer output = null;
		CallableStatement callablestatement = null;
		try
		{
			File rowFile = File.createTempFile((new java.util.Date()).getTime()+"ROW", ".txt");
			rowFile.deleteOnExit();
			output = new BufferedWriter( new FileWriter(rowFile) );
			output.write( newRowBuffer.toString() );
			output.close();

			callablestatement = bwcon.getConnection().prepareCall(CALL_BW_CR_NEW_ROWS);
			System.out.println("row file = " + rowFile.getAbsolutePath());
			callablestatement.setString(1,rowFile.getAbsolutePath());

			int result = callablestatement.executeUpdate();

			callablestatement.close();

		}
		catch (Exception e)
		{
			try
			{
				System.out.println("Rolling back transaction");
				tm.rollbackTransaction();
				tm = null;
			}
			catch (SQLException sqe)
			{
				e.printStackTrace();
			}
			if (e instanceof SQLException)
				throw (SQLException)e;
			else if (e instanceof IOException)
				throw (IOException)e;
		}
		finally
		{
			try
			{
				callablestatement.close();
			}
			catch (SQLException e)
			{
				throw e;
			}
		}
	}
	public void commitNewCells()
	throws SQLException, IOException
	{
		if (newCellBuffer == null)
		{
			return;
		}

		//declared here only to make visible to finally clause; generic reference
		Writer output = null;
		CallableStatement callablestatement = null;
		try
		{
			File cellFile = File.createTempFile((new java.util.Date()).getTime()+"CELL", ".txt");
			cellFile.deleteOnExit();
			output = new BufferedWriter( new FileWriter(cellFile) );
			output.write( newCellBuffer.toString() );
			output.close();

			//if (1==1)return;
			callablestatement = bwcon.getConnection().prepareCall(CALL_BW_CR_NEW_CELLS);
			System.out.println("cell file = " + cellFile.getAbsolutePath());
			callablestatement.setString(1,cellFile.getAbsolutePath());
			callablestatement.setInt(2,tid);

			int result = callablestatement.executeUpdate();

			callablestatement.close();

		}
		catch (Exception e)
		{
			try
			{
				System.out.println("Rolling back transaction");
				tm.rollbackTransaction();
				tm = null;
			}
			catch (SQLException sqe)
			{
				e.printStackTrace();
			}
			if (e instanceof SQLException)
				throw (SQLException)e;
			else if (e instanceof IOException)
				throw (IOException)e;
		}
		finally
		{
			try
			{
				callablestatement.close();
			}
			catch (SQLException e)
			{
				throw e;
			}
		}
	}

	public void finish()
	{
		try
		{
			if (tm != null)
			{
				tm.commitTransaction();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
 };