/*
 * FilterCritereon.java
 *
 * Created on Jan 29, 2007
 */

package com.boardwalk.table;

import java.util.ArrayList;
import java.util.Iterator;

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

	public void prepare() {
		fcValue = value;
		if (fcValue == null) {
			fcValue = "";
		}
		fcValue = fcValue.trim();

		operation = null;
		inList = null;
		char fcFirstChar = fcValue.length() == 0 ? '=' : fcValue.charAt(0);
		switch (fcFirstChar) {
			case '<':
				// fcMatch = handleNumeric(cellValue, fcValue, (c, f) -> { return c < f; });
				operation = (cellValue) -> { return handleNumeric(cellValue, fcValue, (c, f) -> { return c < f; }); };
				break;
			case '>':
				// fcMatch = handleNumeric(cellValue, fcValue, (c, f) -> { return c > f; });
				operation = (cellValue) -> { return handleNumeric(cellValue, fcValue, (c, f) -> { return c > f; }); };
				break;
			case '(':
				// remove brackets, split on comma, remove quotes, store in list, set operator
				fcValue = fcValue.substring(1, fcValue.length() - 1);
				String[] fcValueArr = fcValue.split(",");
				for (int ctr = 0; ctr < fcValueArr.length; ctr++) {
					inList.add(fcValueArr[ctr].replace("'", ""));
				}
				operation = (cellValue -> {
					boolean matchFound = false;
					Iterator<String> inListIter = inList.iterator();
					while (!matchFound && inListIter.hasNext()) {
						String currValue = inListIter.next();
						matchFound = matchFound || currValue.equalsIgnoreCase(cellValue);
					}
					return matchFound;
				});
				break;
			default:
				// fcMatch = fcValue.equals(cellValue);
				operation = (cellValue) -> {
					return (cellValue == null) ? false : fcValue.equalsIgnoreCase(cellValue);
				};
				break;
		}
	}

	String fcValue = null;
	Operator operation = null;
	ArrayList<String> inList = null;

	public boolean processCell(String cellValue) {
		return fcValue.equals("") ? true : operation.operate(cellValue);
	}
	
	private interface Operator {
		boolean operate(String cellValue);
	}

    private interface NumericOperator {
        boolean operate(double cellValue, double fcValue);
    }

    private boolean handleNumeric(String cellValue, String fcValue, NumericOperator o) {
        boolean result = false;
        try {
            double cellD = Double.parseDouble(cellValue);
            double fcD = Double.parseDouble(fcValue.substring(1));
            result = o.operate(fcD, cellD);
        } catch (Exception exc) { }
        return result;
    }
}
