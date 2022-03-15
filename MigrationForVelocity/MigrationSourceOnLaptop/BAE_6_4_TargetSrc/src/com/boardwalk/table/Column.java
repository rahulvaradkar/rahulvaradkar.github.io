package com.boardwalk.table;

import java.util.*;
import java.io.*;

public class Column {
	int     m_id;
	String  m_type;
	float     m_sequence_number;
	String  m_column_name;
	String m_default_string_value;
	int m_default_integer_value;
	double m_default_double_value;
	int m_default_table_value;
	boolean m_isEnumerated = false;
	Vector m_enumrations = new Vector();
	Hashtable m_enumeratedValues = new Hashtable();
	int  m_lookup_table_id;
	int  m_lookup_column_id;
	int m_col_creation_tid;
	boolean  m_is_active;
	int m_ordered_tbl_id;
	int m_ordered_column_id;
	String m_ordered_type;
	String m_ordered_column_table_name;
	String m_lookup_column_table_name;
	int m_access;
	int m_prev_access;
	int m_access_tid;
	int m_source_column_id;



    public Column(	int a_id,
					String a_column_name,
					String a_type,
					float a_sequence_number,
					String a_default_string_value,
					int a_default_integer_value,
					double a_default_double_value,
					int a_default_table_value,
					boolean a_isEnumerated,
					Vector a_enumrations,
					int a_lookup_table_id,
					int a_lookup_column_id,
					int a_col_creation_tid,
					int a_is_active,
					int a_ordered_tbl_id,
					int a_ordered_column_id,
					String a_ordered_type,
					String ordered_column_table_name,
					String lookup_column_table_name,
					int a_access,
					int a_prev_access,
					int a_access_tid,
					int a_source_column_id
    						 )
   {
        m_id        = a_id;
        m_column_name  = a_column_name;
        m_type      = a_type;
        m_sequence_number    = a_sequence_number;
        m_default_string_value = a_default_string_value;
		m_default_integer_value =a_default_integer_value;
		m_default_double_value =a_default_double_value;
		m_default_table_value = a_default_table_value;

		m_isEnumerated = a_isEnumerated;
		m_enumrations = a_enumrations;
		m_lookup_table_id = a_lookup_table_id;
		m_lookup_column_id = a_lookup_column_id;
		m_col_creation_tid  = a_col_creation_tid;

		if ( a_is_active == 1 )
		    	m_is_active = true;
		else
				m_is_active = false;

		 m_ordered_tbl_id = a_ordered_tbl_id;
		 m_ordered_column_id =  a_ordered_column_id;
    	 m_ordered_type = a_ordered_type;


    	 m_ordered_column_table_name =  ordered_column_table_name;
    	 m_lookup_column_table_name = lookup_column_table_name;

		 m_access = a_access;
		 m_prev_access = a_prev_access;
		 m_access_tid = a_access_tid;
		 m_source_column_id = a_source_column_id;
    }


    public Column()
    {

    }

    public int getId ()
    {
		return m_id;
    }

    public String getColumnName()
    {
        return m_column_name;
    }

    public String getType ()
    {
		return m_type;
    }

   public float getSequenceNumber()
   {
		return m_sequence_number;
   }
    public boolean getIsEnumerated()
    {
		return m_isEnumerated;
	}

	public Vector getEnumerations()
	{
		return m_enumrations;
	}

	public boolean checkEnumeratedValue( String value )
	{
		if ( getIsEnumerated() )
		{
				return true;
		}
		else
		{
				return true;
		}


	}

	public int  getLookupTableId()
	{
		return m_lookup_table_id;
	}

	public int  getLookupColumnId()
	{
		return m_lookup_column_id;
	}
	public String  getOrderType()
		{
			return m_ordered_type;
	}


	public int  getOrderedTableId()
		{
			return m_ordered_tbl_id;
		}

		public int  getOrderedColumnId()
		{
			return m_ordered_column_id;
	}

	public int  getCreationTid()
	{
		return m_col_creation_tid;
	}

	public boolean  getIsActive()
	{
		return m_is_active;
	}

    public boolean equals(Object c)
    {
		if (((Column)c).getId() == m_id)
			return true;
		else
			return false;
    }


    public String getDefaultStringValue () {
		return m_default_string_value;
	}

	public int getDefaultIntValue () {
		return m_default_integer_value;
	}

	public double getDefaultDoubleValue () {
		return m_default_double_value;
	}

	public int getDefaultTableValue () {
		return m_default_table_value;
	}

	public String getOrderedTableName() {
		return m_ordered_column_table_name;
	}

	public String getLookupTableName () {
		return m_lookup_column_table_name;
	}



	public String getDefaultValueAsString() {

		String s  = new String("Problem!!!!");

		if (m_type.equals("STRING"))
		s = m_default_string_value;
		else if (m_type.equals("INTEGER")){
			int i = m_default_integer_value;
			s = String.valueOf(i);
		}
		else if (m_type.equals("FLOAT")){
			double d = m_default_double_value;
			s = String.valueOf(d);
		}
		else if (m_type.equals("TABLE")){
			s = String.valueOf(m_default_table_value);
		}


		return s;
	}

	public boolean canRead()
	{
		return m_access > 0;
	}

	public boolean canWrite()
	{
		return m_access > 1;
	}
	public int getAccess()
	{
		return m_access;
	}
	public int getPrevAccess()
	{
		return m_prev_access;
	}

	public int getAccessTid()
	{
		return m_access_tid;
	}
	public int getSourceColumnId()
	{
		return m_source_column_id;
	}
    public int hashCode() {
	return m_id;
    }

    public void print() {
        System.out.println("COLUMN ID = " + m_id + " COLUMN_TYPE = " + m_type + " COLUMN_NAME = " + m_column_name  );
    }


};


