/*
 * FilterCritereon.java
 *
 * Created on Jan 29, 2007
 */

package com.boardwalk.table;

public class FilterCritereon
{
	String columnName;
	int trgtColumnId;
	String value;
	String operator;

	public FilterCritereon(String _columnName, String _value)
	{
		columnName = _columnName ;
		value = _value;
		operator = "=";
		trgtColumnId = -1;
	}
	
	public FilterCritereon(String _columnName, String _value, int _trgtColumnId)
	{
		columnName = _columnName ;
		value = _value;
		operator = "=";
		trgtColumnId = _trgtColumnId;
	}

	public int gettrgtColumnId()
	{
		return trgtColumnId;
	}
	
	public void settrgtColumnId(int _trgtColumnId)
	{
		trgtColumnId = _trgtColumnId;
	}
	
	public String getColumnName()
	{
		return columnName;
	}

	public String getValue()
	{
		return value;
	}

	public String getOperator()
	{
		return operator;
	}
	
}
