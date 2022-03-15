/*
 * RowClassification.java
 *
 * Created on Jan 29, 2007
 */

package com.boardwalk.table;
import java.util.*;
import java.io.*;

public class RowClassification
{
	HashMap keywordsByRowId = null;

	public RowClassification(int numRows)
	{
		keywordsByRowId = new HashMap(numRows);
	}
	
	public RowClassification()
	{
		keywordsByRowId = new HashMap();
	}

	public void put(int rowId, String columnName, String value)
	{
		HashMap keywordsByColumn = (HashMap)keywordsByRowId.get(new Integer(rowId));
		if (keywordsByColumn == null)
		{
			keywordsByColumn = new HashMap();
			keywordsByRowId.put(new Integer(rowId), keywordsByColumn);
		}
		keywordsByColumn.put(columnName, value);
	}

	public HashMap get(int rowId)
	{
		return (HashMap)keywordsByRowId.get(new Integer(rowId));
	}
}
