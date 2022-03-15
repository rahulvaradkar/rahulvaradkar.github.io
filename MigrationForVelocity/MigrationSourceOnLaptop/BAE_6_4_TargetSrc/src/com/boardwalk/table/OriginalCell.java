package com.boardwalk.table;

import java.util.*;
import java.io.*;

public class OriginalCell {

    int     m_id;
    String  m_stringvalue;
    double  m_doublevalue;
    int     m_intvalue;
    int     m_tablevalue;
    int     m_tid;
    String m_type;

    public OriginalCell (int a_id, String a_type, String a_stringvalue, int a_intvalue, double a_doublevalue, int a_tablevalue, int a_tid)
    {
        m_id        = a_id;
        if ( a_stringvalue != null )
			m_stringvalue = a_stringvalue.trim();

        m_doublevalue = a_doublevalue;
        m_intvalue      = a_intvalue;
        m_tablevalue    = a_tablevalue;
        m_tid = a_tid;
        m_type = a_type;
    }


    public OriginalCell () {
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

    public String getValueAsString()
    {

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

    public int getId () {
	return m_id;
    }

     public int getTransactionId () {
		return m_tid;
    }

    public boolean equals(OriginalCell c) {
	if (c.getId() == m_id && c.getTransactionId() == m_tid)
	    return true;
	else
	    return false;
    }

    public int hashCode() {
	return m_id;
    }
  public void printCell()
    {
			System.out.println("ORIGINAL CELL:: " + getId()  +  " TYPE " + m_type +  " VALUE  " + getValueAsString() +  " TID  " + getTransactionId()  );
	}
};


