/*
 * @(#)BoardwalkColumn.java	1.0
 *
 * Copyright 2004 BoardwalkTech, Inc. All rights reserved.
 * BoardwalkTech/CONFIDENTIAL. Use is subject to license terms.
 */

package boardwalk.table;

import java.util.*;
import com.boardwalk.table.Column;
/**
 * BoardwalkColumn object contains information about column in Boardwalk
 * system
 */
 public class BoardwalkColumn
 {
    protected Column col;

	private BoardwalkColumn(){}

	protected BoardwalkColumn(Column a_col)
	{
		col = a_col;
	}

	public Column getCol()
	{
		return this.col;
	}

	public int getId()
	{
		return col.getId();
	}

	public String getName()
	{
		return col.getColumnName();
	}

	public String getDefaultValue()
	{
		return col.getDefaultStringValue();
	}

	public int getCreationTransactionId()
	{
		return col.getCreationTid();
	}

	public int  getLookupTableId()
	{
		return col.getLookupTableId();
	}

	public int  getLookupColumnId()
	{
		return col.getLookupColumnId();
	}

	public int  getSourceColumnId()
	{
		return col.getSourceColumnId();
	}
 };