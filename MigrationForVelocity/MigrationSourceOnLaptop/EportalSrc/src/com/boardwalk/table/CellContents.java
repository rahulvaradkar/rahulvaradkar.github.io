package com.boardwalk.table;

import java.util.*;
import java.io.*;

public class CellContents {
    int     m_id;
    String  m_type;
    String  m_stringvalue;
    double  m_doublevalue;
    int     m_intvalue;
    int     m_tablevalue;

    public CellContents(int a_id, String a_type, String a_stringvalue, int a_intvalue, double a_doublevalue, int a_tablevalue) {
        m_id        = a_id;
        m_type      = a_type;

        if ( a_stringvalue != null )
        	m_stringvalue = a_stringvalue.trim();

        m_doublevalue = a_doublevalue;
        m_intvalue      = a_intvalue;
        m_tablevalue    = a_tablevalue;
    }

    public CellContents(int a_id, String a_type, String a_valueAsString) {
        m_id        = a_id;
        m_type      = a_type;
        setValueAsString(a_valueAsString);
    }

    public CellContents() {
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

    public String getValueAsString() {

        String s  = new String("Problem!!!!");
        if (m_type.equals("STRING"))
	    s = m_stringvalue;
	else if (m_type.equals("INTEGER"))
	{
	    int i = m_intvalue;
	    s = String.valueOf(i);
	}
	else if (m_type.equals("FLOAT"))
	{
	    double d = m_doublevalue;
	    s = String.valueOf(d);
	}
	else if (m_type.equals("TABLE"))
	{
	    s = String.valueOf(m_tablevalue);
	}

	return s;
    }

    public void setValueAsString(String a_valueAsString) {

     if (m_type.equals("STRING"))
     {

	    	 if ( a_valueAsString != null )
        			m_stringvalue = a_valueAsString.trim();
        	else
        		    m_stringvalue = "";
	}
	else if (m_type.equals("INTEGER"))
	{
		if ( a_valueAsString == null ||  a_valueAsString.equals("") ||  a_valueAsString.equals(" ")  )
		{
			a_valueAsString = "0";
		}
		a_valueAsString = a_valueAsString.trim();
		System.out.println("INTEGER value string ="+a_valueAsString+"::");
	    m_intvalue = Integer.parseInt(a_valueAsString);
	}
	else if (m_type.equals("FLOAT"))
	{
		System.out.println("FLOAT value string ="+a_valueAsString+"::");
		if ( a_valueAsString == null ||  a_valueAsString.equals("") ||  a_valueAsString.equals(" "))
		{
			a_valueAsString = "0";
		}
		a_valueAsString = a_valueAsString.trim();
		System.out.println("FLOAT value string ="+a_valueAsString+"::");
	    m_doublevalue = Double.parseDouble(a_valueAsString);
	}
	else if (m_type.equals("TABLE"))
	{
		    a_valueAsString = a_valueAsString.trim();
            m_tablevalue = Integer.parseInt(a_valueAsString);
	}
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

    public void printCellContents()
    {
        System.out.println("CELLID " + m_id + " CELLTYPE " + m_type + " CELLVALUE " + getValueAsString() );
    }
};


