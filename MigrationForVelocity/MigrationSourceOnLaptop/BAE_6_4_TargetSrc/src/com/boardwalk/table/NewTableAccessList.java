package com.boardwalk.table;

import java.util.*;
import java.io.*;

public class NewTableAccessList{

   int     m_table_id;
   String m_relationship;
   int  m_acl = 0;
   int  m_id = -1;

  public NewTableAccessList(
	   															int  a_id, int    a_table_id,String a_relationship
	   													)
{
		m_id = a_id;
		m_table_id = a_table_id;
		m_relationship =a_relationship;
  }


  public int getId()
  {
	  	return m_id;
  }


public int getTableId () {
  return m_table_id;
  }
  public String getRelationship () {
    return m_relationship;
  }


  public int getACL () {
  return m_acl;
  }

  private boolean getAccessBit( int n )
  {
  		return ((m_acl & (1<<n)) != 0);
  }


public void setAddRow()
  {
	  m_acl = 	m_acl | (1<<0);

  }
public void setDeleteRow()
  {
	  m_acl = 	m_acl | (1<<15);

  }

  public void setAdministerColumn()
  {
	  	m_acl =  m_acl | (1<<1);
  }

    public  void setAdministerTable()
   {
    	  m_acl = 	m_acl | (1<<7);
    }


/* Latest View of the User */


  public void setReadWriteOnMyLatestView()
    {
  	  m_acl = 	m_acl | (1<<2);
  }
  public void setReadLatestViewOfAll()
    {
  	m_acl =   m_acl | (1<<3);
  }

  public void setReadLatestViewOfAllChildren()
    {
  	  m_acl = 	m_acl | (1<<4);
  }


  /* Latest View of the Table */

  public void setReadLatestOfTable()
    {
  	  m_acl = 	m_acl | (1<<5);
  }
  public void  setWriteLatestOfTable()
  {
      	m_acl =   	m_acl | (1<<8);
   }
  public void setReadWriteLatestOfMyRows()
    {
  	  m_acl =    m_acl | (1<<6);
  }

  public void setReadMyGroup()
      {
    	  m_acl = 	m_acl | (1<<9);
    }
    public void  setReadWriteMyGroup()
    {
        	m_acl =   	m_acl | (1<<10);
     }
    public void setReadMyGroupAndImmediateChildren()
      {
    	  m_acl =    m_acl | (1<<11);
  }

  public void setReadWriteMyGroupAndImmediateChildren()
      {
    	  m_acl = 	m_acl | (1<<12);
    }
    public void  setReadMyGroupAndAllChildren()
    {
        	m_acl =   	m_acl | (1<<13);
     }
    public void setReadWriteMyGroupAndAllChildren()
      {
    	  m_acl =    m_acl | (1<<14);
  }



/* Adminsitration related access */





public boolean canAddRow()
  {
	  	return getAccessBit(0);
  }

public boolean canDeleteRow()
  {
	  	return getAccessBit(15);
  }
  public boolean canAdministerColumn()
  {
	  	return getAccessBit(1);
  }

    public boolean canAdministerTable()
   {
    	  	return getAccessBit(7);
    }


/* Latest View of the User */


  public boolean canReadWriteOnMyLatestView()
    {
  	  	return getAccessBit(2);
  }
  public boolean canReadLatestViewOfAll()
    {
  	  	return getAccessBit(3);
  }

  public boolean canReadLatestViewOfAllChildren()
    {
  	  	return getAccessBit(4);
  }


  /* Latest View of the Table */

  public boolean canReadLatestOfTable()
    {
  	  	return getAccessBit(5);
  }
  public boolean canWriteLatestOfTable()
  {
      	  	return getAccessBit(8);
   }
  public boolean canReadWriteLatestOfMyRows()
    {
  	  	return getAccessBit(6);
  }

  public boolean canReadLatestofMyGroup()
      {
    	  	return getAccessBit(9);
    }
    public boolean canReadWriteLatestofMyGroup()
    {
        	  	return getAccessBit(10);
     }
    public boolean canReadLatestofMyGroupAndImmediateChildren()
      {
    	  	return getAccessBit(11);
  }

  public boolean canReadWriteLatestofMyGroupAndImmediateChildren()
      {
    	  	return getAccessBit(12);
    }
    public boolean canReadLatestofMyGroupAndAllChildren()
    {
        	  	return getAccessBit(13);
     }
    public boolean canReadWriteLatestofMyGroupAndAllChildren()
      {
    	  	return getAccessBit(14);
  }


  public void print()
  {
	  System.out.println(" ");
	  System.out.println(" Table Request Access for table" + m_table_id);
	  System.out.println(" Relationship =  " + m_relationship);
	  System.out.println(" canAddRow =  " + canAddRow() );
	  System.out.println(" canDeleteRow =  " + canDeleteRow() );
	  System.out.println(" canAdministerColumn =  " + canAdministerColumn() );
	  System.out.println(" canAdministerTable =  " + canAdministerTable() );
	  System.out.println(" canReadWriteOnMyLatestView =  " + canReadWriteOnMyLatestView() );
	  System.out.println(" canReadLatestViewOfAll =  " + canReadLatestViewOfAll() );
	  System.out.println(" canReadLatestViewOfAllChildren =  " + canReadLatestViewOfAllChildren() );
	  System.out.println(" canReadLatestOfTable =  " + canReadLatestOfTable() );
	  System.out.println(" canWriteLatestOfTable =  " + canWriteLatestOfTable() );
	  System.out.println(" canReadWriteLatestOfMyRows =  " + canReadWriteLatestOfMyRows() );

	  System.out.println(" canReadLatestofMyGroup =  " + canReadLatestofMyGroup() );
	  System.out.println(" canReadWriteLatestofMyGroup =  " + canReadWriteLatestofMyGroup() );
	  System.out.println(" canReadLatestofMyGroupAndImmediateChildren =  " + canReadLatestofMyGroupAndImmediateChildren() );

	  System.out.println(" canReadWriteLatestofMyGroupAndImmediateChildren =  " + canReadWriteLatestofMyGroupAndImmediateChildren() );
	  System.out.println(" canReadLatestofMyGroupAndAllChildren =  " + canReadLatestofMyGroupAndAllChildren() );
	  System.out.println(" canReadWriteLatestofMyGroupAndAllChildren =  " + canReadWriteLatestofMyGroupAndAllChildren() );


  }


};


