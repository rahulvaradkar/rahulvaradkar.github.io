package com.boardwalk.excel;

import java.util.*;
import java.io.*;
import com.boardwalk.util.*;

public class xlColumn {

	int m_id;
	int m_ColumnAddress;
	String m_type;
	float m_sequence_number;
	String  m_column_name;
	int m_previousColId;
	int m_previousColumnAddress;
	String  m_action;



    public xlColumn(
						int  a_ColumnAddress,
						int a_id,
						String a_column_name,
						String a_type,
						float a_sequence_number,
						int a_previousColId,
						int a_previousColumnAddress,
						String a_action
					 )
   {
        m_id        = a_id;
        m_column_name  = a_column_name;
        m_type      = a_type;
        m_sequence_number    = a_sequence_number;
        m_ColumnAddress = a_ColumnAddress;

		m_previousColId    = a_previousColId;
		m_previousColumnAddress = a_previousColumnAddress;
		m_action = a_action;

    }

    public xlColumn( String parseWorkString )
	{


				/*
				Form1.appendTableBuffer columnAddress
				Form1.appendTableBuffer ContentDelimeter
				Form1.appendTableBuffer colId
				Form1.appendTableBuffer ContentDelimeter
				Form1.appendTableBuffer colName
				Form1.appendTableBuffer ContentDelimeter
				Form1.appendTableBuffer colType
				Form1.appendTableBuffer ContentDelimeter
				Form1.appendTableBuffer sequenceNumber
				Form1.appendTableBuffer ContentDelimeter
				Form1.appendTableBuffer previousColumnAddress
				Form1.appendTableBuffer ContentDelimeter
				Form1.appendTableBuffer previousColumnId
				Form1.appendTableBuffer ContentDelimeter
				Form1.appendTableBuffer action
				Form1.appendTableBuffer Seperator*/

			StringTokenizer st = new StringTokenizer (parseWorkString);
			m_ColumnAddress = Integer.parseInt(st.nextToken (Constants.ContentDelimeter));
			m_id        =  Integer.parseInt(st.nextToken (Constants.ContentDelimeter));
			m_column_name  = st.nextToken (Constants.ContentDelimeter);;
			m_type      = st.nextToken (Constants.ContentDelimeter);
			m_sequence_number    = Integer.parseInt(st.nextToken (Constants.ContentDelimeter));
			m_previousColumnAddress = Integer.parseInt(st.nextToken (Constants.ContentDelimeter));
			m_previousColId    = Integer.parseInt(st.nextToken (Constants.ContentDelimeter));
			m_action = st.nextToken ();
	}




    public xlColumn()
    {

    }

    public String getBuffer()
    {
			return null;

	}


    public int getId ()
    {
		return m_id;
    }

    public void  setId (int a_id)
	    {
			m_id = a_id;
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

   public int getPreviousColId()
   {
   		return m_previousColId;
   }

	 public int  getColumnAddress()
	{
		return m_ColumnAddress;
	}



	public int  getPreviousColumnAddress()
   {
   		return m_previousColumnAddress;
   }

   public String getAction()
      {
      		return m_action;
   }




    public boolean equals(Object c)
    {
		if (((xlColumn)c).getId() == m_id)
			return true;
		else
			return false;
    }

    public int hashCode() {
	return m_id;
    }

    public void print() {
        System.out.println("COLUMN ID = " + m_id + " TYPE = " + m_type + "  NAME = " + m_column_name   + "  ADDRESS = " + m_ColumnAddress  + " SEQUENCE = " + m_sequence_number + "  PREVIOUS ADDRESS = " + m_previousColumnAddress + " PREVIOUS ID = " + m_previousColId  +  " ACTION = " + m_action );
    }


};


