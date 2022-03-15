package com.boardwalk.table;

import java.util.*;
import java.io.*;
import com.boardwalk.database.Transaction;
import boardwalk.common.*;

public class VersionedCell {
    int     m_id;
    int     m_rowid;
    int     m_columnid;
    String  m_columnName;
    String  m_type;
    String  m_stringvalue;
    double  m_doublevalue;
    int     m_intvalue;
    int     m_tablevalue;
    String  m_formula;
	boolean m_formulapresent;
	boolean m_formulamodified;

    String  m_tableName;

	String m_description = "";
	String m_baselineDesc = "";

	Transaction m_transaction;


    public VersionedCell (int a_id, int a_columnid, String a_ColumnName , int a_rowid,
    						String a_type, String a_stringvalue, int a_intvalue,
    						double a_doublevalue, int a_tablevalue, String a_tableName,
    						Transaction a_transaction,
    						String a_formula) {
        m_id        = a_id;
        m_columnid  = a_columnid;
        m_columnName = a_ColumnName;
		m_rowid     = a_rowid;
		m_type      = a_type;
		if ( a_type.trim().equals("STRING") )
		{
			if ( a_stringvalue != null )
				m_stringvalue = a_stringvalue.trim();
			else
				m_stringvalue = "";
		}
		else
			m_stringvalue = a_stringvalue;

        m_doublevalue = a_doublevalue;
        m_intvalue      = a_intvalue;
        m_tablevalue    = a_tablevalue;
        m_tableName = a_tableName;
		m_transaction = a_transaction;

		if ( BoardwalkUtility.checkIfNullOrBlank(a_formula) )
		{
			m_formulapresent=false;
			m_formula = "";
		}
		else
		{
			m_formulapresent=true;
			m_formula = a_formula.trim();
		}

    }


    public VersionedCell () {
    }

	public String getColumnName()
	{
		return m_columnName;
	}

    public int getColumnId () {
		return m_columnid;
    }

    public String getStringValue () {
		return m_stringvalue;
    }

    public int getIntValue ()
    {
		return m_intvalue;
    }

    public double getDoubleValue ()
    {
	 	return m_doublevalue;
    }

    public int getTableValue ()
    {
		return m_tablevalue;
    }

    public void setTableValue( int tableId)
    {
        m_tablevalue = tableId;
    }


    public String getTableName ()
    {
		return m_tableName;
    }

    public String getValueAsString()
    {
		String s  = new String("Problem!!!!");
		if (m_type.equals("STRING"))
		{
			s = m_stringvalue;
		}
		else if (m_type.equals("INTEGER"))
		{
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

    public String getValueAsHtmlString()
	    {
			String s  = new String("Problem!!!!");
			if (m_type.equals("STRING"))
			{
				s = m_stringvalue;
				s= s.replaceAll("\r\n|\n","<br/>");
			}
			else if (m_type.equals("INTEGER"))
			{
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

	public String getValueAsDivString()
	{
		// div does not display HMTL but string
			String s  = new String("Problem!!!!");
			if (m_type.equals("STRING"))
			{
				s = m_stringvalue;
				s= s.replaceAll("\r\n|\n"," ");
			}
			else if (m_type.equals("INTEGER"))
			{
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
		return m_formula;
	}

	public boolean getformulapresent()
	{
	return m_formulapresent;
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

	public Transaction getTransaction()
	{
		return m_transaction;
	}

    public boolean equals(VersionedCell c) {
		if (c.getId() == m_id)
			return true;
		else
			return false;
    }

    public int hashCode() {
	return m_id;
    }

    public void printCell()
    {
			System.out.println("CELL:: " + getId()  + " COLID " + getColumnId() +  " ColumnName " + m_columnName +  " ROWID " + getRowId() +  " TYPE " + getType() +  " VALUE  " + getValueAsString() + " TableName " + getTableName() +  " USER  " + getTransaction().getCreatedByUserAddress() +  " TID  " + getTransaction().getId() + " Formula " + m_formula );
	}

	public String getDescription ()
	{
		return m_description;
    }

	public void setDescription(String asDescription)
	{
		m_description = asDescription;
	}

	public boolean getformulaModified ()
	{
		return m_formulamodified;
    }

	public void setformulaModified(boolean asformulaModified)
	{
		m_formulamodified = asformulaModified;
	}

	public String getbaselineDesc ()
	{
		return m_baselineDesc;
    }

	public void setbaselineDesc(String a_baselineDesc)
	{
		m_baselineDesc = a_baselineDesc;
	}

};


