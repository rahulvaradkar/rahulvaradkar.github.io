/*
 * @(#)BoardwalkNewColumn.java 1.0
 *
 * Copyright 2004 BoardwalkTech, Inc. All rights reserved.
 * BoardwalkTech/CONFIDENTIAL. Use is subject to license terms.
 */

package boardwalk.table;

import java.util.*;
import com.boardwalk.table.Column;
/**
 * BoardwalkNewColumn object contains information about column in Boardwalk
 * system
 */
 public class BoardwalkNewColumn
 {
    protected Column col;

    public BoardwalkNewColumn(
		int tableId,
		String columnName,
		int afterColumnId,
		int lookupTableId,
		int lookupColumnId,
		String defaultValue,
		int referenceColumnId
	)
	{
		boolean enumerated = false;
		if (lookupTableId > 0 && lookupColumnId > 0)
			enumerated = true;
		col = new Column(
					-1,
					columnName,
					"STRING",
					-1,
					defaultValue,
					-1,
					1.0,
					-1,
					enumerated,
					null,
					lookupTableId,
					lookupColumnId,
					-1,
					0,
					-1,
					-1,
					null,
					null,
					null,
					0,
					0,
					-1,
					-1);
	}
 };