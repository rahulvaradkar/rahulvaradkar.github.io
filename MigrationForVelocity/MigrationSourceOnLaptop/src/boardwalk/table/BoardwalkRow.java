/*
 * @(#)BoardwalkRow.java	1.0
 *
 * Copyright 2004 BoardwalkTech, Inc. All rights reserved.
 * BoardwalkTech/CONFIDENTIAL. Use is subject to license terms.
 */

package boardwalk.table;

import java.util.*;
import com.boardwalk.table.Row;
/**
 * BoardwalkRow object contains information about a row in Boardwalk
 * system
 */
 public class BoardwalkRow
 {
    protected Row row;

	private BoardwalkRow(){}

	protected BoardwalkRow(Row a_row)
	{
		row = a_row;
	}

	public Row getRow()
	{
		return this.row;
	}

	public int getId()
	{
		return row.getId();
	}

	public String getName()
	{
		return row.getName();
	}

	public String getOwner()
	{
		return row.getOwnerName();
	}

	public int getOwnerId()
	{
		return row.getOwnerUserId();
	}
 };