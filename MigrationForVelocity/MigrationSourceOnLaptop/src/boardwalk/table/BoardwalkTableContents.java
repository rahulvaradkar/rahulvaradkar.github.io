/*
 * @(#)BoardwalkTableContents.java	1.0
 *
 * Copyright 2004 BoardwalkTech, Inc. All rights reserved.
 * BoardwalkTech/CONFIDENTIAL. Use is subject to license terms.
 */

package boardwalk.table;

import java.util.*;
import com.boardwalk.table.*;
/**
 * BoardwalkTableContents object contains information about a Boardwalk
 * table configuration requested using the <code>BoardwalkTableManager</code>
 * service
 */
 public class BoardwalkTableContents
 {
    protected TableContents tbcon;

	private BoardwalkTableContents(){}

	protected BoardwalkTableContents(TableContents a_tbcon)
	{
		tbcon = a_tbcon;
	}

	public Vector getColumns()
	{
		Vector columns = new Vector();
		Vector cols = tbcon.getColumnsSortedBySeqNum();
		Iterator icols = cols.iterator();
		while (icols.hasNext())
		{
			Column c = (Column)icols.next();
			columns.addElement(new BoardwalkColumn(c));
		}
		return columns;
	}

	public Vector getRows()
	{
		Vector rows = new Vector();

		Vector rowIds = tbcon.getRowIds();
		Iterator irids = rowIds.iterator();
		Hashtable rrid = tbcon.getRowObjsByRowId();
		while (irids.hasNext())
		{
			Integer rid = (Integer)irids.next();
			Row r = (Row)rrid.get(rid);
			rows.addElement(new BoardwalkRow(r));
		}

		return rows;
	}

	public Vector getCellsForRow(int rowId)
	{
		Vector cells = new Vector();
		Hashtable cellsByRowId = tbcon.getCellsByRowId();
		Vector vCells = null;
		try
		{
			vCells = (Vector)(((Vector)cellsByRowId.get(new Integer(rowId))).elementAt(0));
		}
		catch (ArrayIndexOutOfBoundsException ae)
		{
			// This can happen when the table contents represents table comparisons
			// when the rows are not present at the earlier time.
			return null;
		}
		Iterator icells = vCells.iterator();
		while (icells.hasNext())
		{
			try
			{
				VersionedCell vc = (VersionedCell)icells.next();
				cells.add(new BoardwalkCell(vc));
			}
			catch (ClassCastException cce)
			{
				// This can happend when the table contents represent table comparisons
				// then the rows are present at both snapshots, but the data has not
				// changed in the particular cell

			}
		}
		return cells;
	}
 };