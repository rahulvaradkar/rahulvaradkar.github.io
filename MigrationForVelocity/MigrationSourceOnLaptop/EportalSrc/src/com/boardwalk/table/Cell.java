package com.boardwalk.table;

import java.util.*;
import java.io.*;

public class Cell {
    int     m_id;
    int     m_rowid;
    int     m_columnid;
    String  m_type;
    String  m_stringvalue;
    double  m_doublevalue;
    int     m_intvalue;
    int     m_tablevalue;
    String  m_tableName;
    String  m_formula;
    public Cell (int a_id, int a_columnid, int a_rowid, String a_type, String a_stringvalue, int a_intvalue, double a_doublevalue, int a_tablevalue, String a_tableName, String a_formula ) {
        m_id        = a_id;
        m_columnid  = a_columnid;
	    m_rowid     = a_rowid;
	    m_type      = a_type;
        if ( a_stringvalue != null )
			m_stringvalue = a_stringvalue.trim();

        m_doublevalue = a_doublevalue;
        m_intvalue      = a_intvalue;
        m_tablevalue    = a_tablevalue;
        m_tableName = a_tableName;
        m_formula = a_formula;
           //     System.out.println("Cell()::m_type = " + m_type);

       // System.out.println("Cell()::m_stringvalue = " + m_stringvalue);
    }


    public Cell () {
    }


    public int getColumnId () {
	return m_columnid;
    }

    public String getStringValue () {
	return m_stringvalue;
    }

    public int getIntValue () {
	return m_intvalue;
    }

    public double getDoubleValue () {
	return m_doublevalue;
    }

    public int getTableValue () {
	return m_tablevalue;
    }
    public void setTableValue( int tableId) {
        m_tablevalue = tableId;
    }


    public String getTableName () {
	return m_tableName;
    }

	public String getFormula()
	{
		if ( m_formula == null )
		{
			return "";
		}
		else
		{
			return m_formula.trim();
		}
	}

    public String getValueAsString() {

        String s  = new String("Problem!!!!");
   //     System.out.println("getValueAsString::m_type = " + m_type +"*****");
   //             System.out.println("getValueAsString::m_stringvalue = " + m_stringvalue +"*****");

        if (m_type.equals("STRING"))
	    s = m_stringvalue;
	else if (m_type.equals("INTEGER")){
	    int i = m_intvalue;
	    s = String.valueOf(i);
	}
	else if (m_type.equals("FLOAT")){
	    double d = m_doublevalue;
	    s = String.valueOf(d);
	}
	else if (m_type.equals("TABLE")){
	    s = String.valueOf(m_tablevalue);
	}

	return s;
    }

    public int getRowId () {
	return m_rowid;
    }

    public int getId () {
	return m_id;
    }

    public String getType () {
	return m_type;
    }

    public boolean equals(Cell c) {
	if (c.getId() == m_id)
	    return true;
	else
	    return false;
    }

    public int hashCode() {
	return m_id;
    }

};


