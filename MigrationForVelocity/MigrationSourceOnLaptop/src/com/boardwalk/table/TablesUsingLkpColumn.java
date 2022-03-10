package com.boardwalk.table;

import java.util.*;
import java.io.*;

public class TablesUsingLkpColumn
{
	int _tableUsingLookupId;
	String _tableUsingLookupName;
	String _tableUsingLookupPurpose;
	int _columnUsingLookupId;
	String _columnUsingLookupName;

	public TablesUsingLkpColumn(int tableUsingLookupId,
								String tableUsingLookupName,
								String tablesUsingLookupPurpose,
								int columnUsingLookupId,
								String columnUsingLookupName)
	{
		_tableUsingLookupId = tableUsingLookupId;
		_tableUsingLookupName = tableUsingLookupName;
		_tableUsingLookupPurpose = tablesUsingLookupPurpose;
		_columnUsingLookupId = columnUsingLookupId;
		_columnUsingLookupName = columnUsingLookupName;

	}

	public int getTableUsingLookup_Id()
	{
		return _tableUsingLookupId;
	}

	public String getTableUsingLookup_Name()
	{
		return _tableUsingLookupName;
	}

	public String getTableUsingLookup_Purpose()
	{
		return _tableUsingLookupPurpose;
	}

	public int getColumnUsingLookup()
	{
		return _columnUsingLookupId;
	}

	public String  getColumnUsingLookupName()
		{
			return _columnUsingLookupName;
	}



};