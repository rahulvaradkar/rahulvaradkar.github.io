package com.boardwalk.excel;

import java.util.*;
import java.io.*;
import com.boardwalk.util.*;

public class xlRow {

    int     m_id;
    int   m_RowAddress;
    float     m_sequence_number;
    int m_previousRowId;
    int m_previousRowAddress;
	String  m_action;



    public xlRow(
								int  a_RowAddress,
								int a_id,
    							float a_sequence_number,
    							int a_previousRowId,
    							int a_previousRowAddress,
    							String a_action
    						 )
   {
        m_id        = a_id;
        m_sequence_number    = a_sequence_number;
        m_RowAddress = a_RowAddress;
		m_previousRowId    = a_previousRowId;
		m_previousRowAddress = a_previousRowAddress;
		m_action = a_action;

    }

    public xlRow( String parseWorkString )
	{


				/*
				Form1.appendTableBuffer rowAddress
				Form1.appendTableBuffer ContentDelimeter
				Form1.appendTableBuffer rowId
				Form1.appendTableBuffer ContentDelimeter
				Form1.appendTableBuffer sequenceNumber
				Form1.appendTableBuffer ContentDelimeter
				Form1.appendTableBuffer previousRowAddress
				Form1.appendTableBuffer ContentDelimeter
				Form1.appendTableBuffer previousRowId
				Form1.appendTableBuffer ContentDelimeter
				Form1.appendTableBuffer action*/

			StringTokenizer st = new StringTokenizer (parseWorkString);
			m_RowAddress = Integer.parseInt(st.nextToken (Constants.ContentDelimeter));
			m_id        =  Integer.parseInt(st.nextToken (Constants.ContentDelimeter));
			m_sequence_number    = Integer.parseInt(st.nextToken (Constants.ContentDelimeter));
			m_previousRowAddress = Integer.parseInt(st.nextToken (Constants.ContentDelimeter));
			m_previousRowId    = Integer.parseInt(st.nextToken (Constants.ContentDelimeter));
			m_action = st.nextToken ();
	}




    public xlRow()
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

    public void setId (int a_id)
   {
			m_id = a_id;
    }

     public void  setSequenceNumber (int a_SequenceNumber)
	{
		m_sequence_number = a_SequenceNumber;
    }




   public float getSequenceNumber()
   {
		return m_sequence_number;
   }

   public int getPreviousRowId()
   {
   		return m_previousRowId;
   }

	 public int  getRowAddress()
	{
		return m_RowAddress;
	}



	public int  getPreviousRowAddress()
   {
   		return m_previousRowAddress;
   }

   public String getAction()
      {
      		return m_action;
   }




    public boolean equals(Object c)
    {
		if (((xlRow)c).getId() == m_id)
			return true;
		else
			return false;
    }

    public int hashCode() {
	return m_id;
    }

    public void print() {
        System.out.println("Row ID = " + m_id +  "  ADDRESS = " + m_RowAddress  + " SEQUENCE = " + m_sequence_number + "  PREVIOUS ADDRESS = " + m_previousRowAddress + " PREVIOUS ID = " + m_previousRowId  +  " ACTION = " + m_action );
    }


};


