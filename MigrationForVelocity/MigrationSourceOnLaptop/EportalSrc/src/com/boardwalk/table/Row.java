package com.boardwalk.table;

import java.util.*;
import java.io.*;

public class Row {
    int     m_id;
    float     m_sequence_number;
    String  m_row_name;
    int m_creation_tid;
    int m_owner_userId;
    int m_isActive;
    int m_owner_tid;
    int m_creator_id;
    String  m_owner_name;



    public Row(int a_id, String a_row_name,float a_sequence_number, int a_creation_tid, int a_owner_userId,int a_isActive,int a_owner_tid, int a_creator_id, String a_owner_name) {
        m_id        = a_id;
        m_row_name  = a_row_name;
        m_sequence_number    = a_sequence_number;

        m_creation_tid  = a_creation_tid;
		m_owner_userId    = a_owner_userId;
        m_isActive  = a_isActive;
        m_owner_tid = a_owner_tid;
        m_creator_id = a_creator_id;
        m_owner_name = a_owner_name;
    }


    public Row() {
    }


    public int getId () {
	return m_id;
    }

    public int getCreationTid ()
    {
		return m_creation_tid;
    }

    public int getCreatorUserId ()
    {
			return m_creator_id;
    }

    public int getOwnershipAssignedTid ()
    {
			return m_owner_tid;
    }

     public int getOwnerUserId ()
     {
		return m_owner_userId;
    }

      public String  getOwnerName ()
      {
			return m_owner_name;
     }

     public int getIsActive ()
     {
		return m_isActive;
    }

    public String getName() {
        return m_row_name;
    }

	public float getSequenceNumber()
	{
		return m_sequence_number;
	}

    public boolean equals(Object r) {
	if (((Row)r).getId() == m_id)
	    return true;
	else
	    return false;
    }

    public int hashCode() {
	return m_id;
    }

     public void print() {

        System.out.println("ROW ID = " + m_id + " ROW_NAME = " + m_row_name + " getCreationTid= " + getCreationTid() + " getOwnershipAssignedTid " + getOwnershipAssignedTid()  );
    }
};


