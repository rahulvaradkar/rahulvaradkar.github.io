/*
 * @(#)BoardwalkCell.java	1.0
 *
 * Copyright 2004 BoardwalkTech, Inc. All rights reserved.
 * BoardwalkTech/CONFIDENTIAL. Use is subject to license terms.
 */

package boardwalk.table;

import java.util.*;
import com.boardwalk.table.VersionedCell;
/**
 * BoardwalkCell object contains information about a cell in Boardwalk
 * system.
 */
 public class BoardwalkCell
 {
    protected  VersionedCell cell;

	private BoardwalkCell(){}

	protected BoardwalkCell(VersionedCell a_cell)
	{
		cell = a_cell;
	}

	public VersionedCell getCell()
	{
		return this.cell;
	}

	public String getColumnName()
	{
		return cell.getColumnName();
	}

    public int getColumnId () {
		return cell.getColumnId();
    }

    public String getStringValue () {
		return cell.getStringValue();
    }

	public String getFormula()
	{
		return cell.getFormula();
	}

    public int getRowId () {
		return cell.getRowId();
    }

    public int getId () {
		return cell.getId();
    }

	public BoardwalkTransaction getTransaction()
	{
		return new BoardwalkTransaction(cell.getTransaction());
	}
 };