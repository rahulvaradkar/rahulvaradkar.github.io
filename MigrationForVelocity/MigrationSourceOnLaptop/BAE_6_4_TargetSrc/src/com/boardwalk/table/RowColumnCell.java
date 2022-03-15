package com.boardwalk.table;

import java.util.*;
import java.io.*;

public class RowColumnCell {
    int     m_row_id;
    int     m_column_id;
    String  m_type;
    String  m_stringvalue;
    double  m_doublevalue;
    int     m_intvalue;
    int     m_tablevalue;
    String  m_formula;
    int cell_id = -1;


    public RowColumnCell(int a_row_id, int a_column_id, String a_type, String a_stringvalue, int a_intvalue, double a_doublevalue, int a_tablevalue, String a_formula) {
        m_row_id        = a_row_id;
        m_column_id        = a_column_id;
        m_type      = a_type;

		if ( a_stringvalue != null )
		m_stringvalue = a_stringvalue.trim();

        m_doublevalue = a_doublevalue;
        m_intvalue      = a_intvalue;
        m_tablevalue    = a_tablevalue;
        m_formula = a_formula;
        if ( m_formula == null )
		{
			m_formula = "";
		}
		else
		{
			m_formula = m_formula.trim();
		}
    }

    public RowColumnCell(int a_row_id, int a_column_id, String a_type, String a_valueAsString, String a_formula) {
        m_row_id        = a_row_id;
        m_column_id        = a_column_id;
        m_type      = a_type;
        setValueAsString(a_valueAsString);
        m_formula = a_formula;
        if ( m_formula == null )
		{
			m_formula = "";
		}
		else
		{
			m_formula = m_formula.trim();
		}
    }

    public RowColumnCell() {
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
	    s = m_stringvalue.trim();
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

    public void setValueAsString(String a_valueAsString) {

    if (m_type.equals("STRING"))
	        {

	   	    	 if ( a_valueAsString != null )
	           			m_stringvalue = a_valueAsString.trim();
	           	else
	           		    m_stringvalue = "";
	}
	else if (m_type.equals("INTEGER")){
	    m_intvalue = Integer.parseInt(a_valueAsString);
	}
	else if (m_type.equals("FLOAT")){
	    m_doublevalue = Double.parseDouble(a_valueAsString);
	}
	else if (m_type.equals("TABLE")){
            m_tablevalue = Integer.parseInt(a_valueAsString);
	}
    }




    public int getRowId () {
	return m_row_id;
    }
    public int getColumnId () {
	return m_column_id;
    }


    public String getType () {
	return m_type;
    }

    public void  setType (String a_type) throws java.lang.NumberFormatException {
		 if ( m_type.equals("STRING") && a_type.equals("FLOAT") )
		 {
			 m_doublevalue = java.lang.Double.parseDouble(getStringValue());
			m_type = a_type;

		 }
		 else
		 if ( m_type.equals("STRING") && a_type.equals("INTEGER")  )
		 {

			try
			{
			 	m_intvalue = java.lang.Integer.parseInt(getStringValue());
			 	m_type = a_type;
			}
			catch( java.lang.NumberFormatException ne )
			{
				System.out.println("converting string to int = " + getStringValue() );
				double dblVal = java.lang.Double.parseDouble(getStringValue());
				System.out.println("db value = " + dblVal );

				long longValue = java.lang.Math.round(dblVal);
				System.out.println("long  value = " + longValue );
				if ( longValue < java.lang.Integer.MAX_VALUE )
				{
					 m_intvalue = java.lang.Integer.parseInt(""+longValue );
				}

				System.out.println("int  value = " + m_intvalue );
				m_type = a_type;


			}

		 }
		 else
		 if (  m_type.equals("INTEGER") && a_type.equals("STRING")  )
		 {
			 	m_stringvalue = getValueAsString();
		 		m_type = a_type;
		 }
		 else
		 if (  m_type.equals("FLOAT") && a_type.equals("STRING")  )
		 {
				 m_stringvalue = getValueAsString();
		 		 m_type = a_type;
		 }
		 else
		 if (  m_type.equals("INTEGER") && a_type.equals("FLOAT")  )
		 {
			 	 m_doublevalue = java.lang.Double.parseDouble(getValueAsString());
		 		 m_type = a_type;
		 }
		 if (  m_type.equals("FLOAT") && a_type.equals("INTEGER")  )
		 {
			 	long longValue = java.lang.Math.round(m_doublevalue);
			 	if ( longValue < java.lang.Integer.MAX_VALUE )
			 	{
					 m_intvalue = java.lang.Integer.parseInt(""+longValue );
				}
		 		m_type = a_type;
		 }


    }

    public boolean equals(Object c) {
	if (((RowColumnCell)c).getRowId() == m_row_id  && ((RowColumnCell)c).getColumnId() == m_column_id )
	    return true;
	else
	    return false;
    }

    public int hashCode() {
        String code =  ""+m_row_id+m_column_id;
	return Integer.parseInt(code);
    }

	public void setId( int cellId )
	{
		cell_id = cellId;
	}

	public int getId()
	{
		return cell_id;
	}

    public void printCellContents()
    {
        System.out.println("ROW ID " + m_row_id + "COLUMN ID " + m_column_id + " CELLTYPE " + m_type + " CELLVALUE " + getValueAsString() + "FORMULA" + getFormula());
    }
};


