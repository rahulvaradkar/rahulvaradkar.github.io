package com.boardwalk.table;

import java.util.*;
import java.io.*;

public class TableAccessList {

   int     m_acl_id;
   int     m_table_id;
   String  m_access_relationship;
   int  m_acl;

   public TableAccessList(    int     a_acl_id,
							  int    a_table_id,
							  String  a_access_relationship,
							  int  a_acl)
	{
		m_acl_id = a_acl_id;
		m_table_id = a_table_id;
		m_access_relationship =a_access_relationship;
		m_acl = a_acl;
	}


	public int getId ()
	{
		return m_acl_id;
	}
	public int getTableId ()
	{
		return m_table_id;
	}
	public String getRelationship ()
	{
		return m_access_relationship;
	}

	public int getACL ()
	{
		return m_acl;
	}
	private boolean getAccessBit( int n )
	{
		return ((m_acl & (1<<n)) != 0);
	}
	private void setAccessBit( int n )
	{
		m_acl = m_acl ^ (1<<n);
	}
	///////////////////////////////////////////////////////////////////
	/* get access *///////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////
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
	public boolean canReadWriteLatestOfMyRows()
	{
		return getAccessBit(6);
	}
	public boolean canWriteLatestOfTable()
	{
		return getAccessBit(8);
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
   	////////////////////////////////////////////////////////
   	// Setting access///////////////////////////////////////
   	////////////////////////////////////////////////////////
	public void flipAddRowAccess()
	{
		setAccessBit(0);
	}
	public void flipDeleteRowAccess()
	{
		setAccessBit(15);
	}

	public void flipAdministerColumnAccess()
	{
		setAccessBit(1);
	}

	public void flipAdministerTableAccess()
	{
		setAccessBit(7);
	}
	/* Latest View of the User */
	public void flipReadWriteOnMyLatestViewAccess()
	{
		setAccessBit(2);
	}
	public void flipReadLatestViewOfAllAccess()
	{
		setAccessBit(3);
	}

	public void flipReadLatestViewOfAllChildrenAccess()
	{
		setAccessBit(4);
	}
  	/* Latest View of the Table */
	public void flipReadLatestOfTableAccess()
	{
		setAccessBit(5);
	}
	public void flipReadWriteLatestOfMyRowsAccess()
	{
		setAccessBit(6);
	}
	public void flipWriteLatestOfTableAccess()
	{
		setAccessBit(8);
	}
	public void flipReadLatestofMyGroupAccess()
	{
		setAccessBit(9);
	}
	public void flipReadWriteLatestofMyGroupAccess()
	{
		setAccessBit(10);
	}
	public void flipReadLatestofMyGroupAndImmediateChildrenAccess()
	{
		setAccessBit(11);
	}
	public void flipReadWriteLatestofMyGroupAndImmediateChildrenAccess()
	{
		setAccessBit(12);
	}

	public void flipReadLatestofMyGroupAndAllChildrenAccess()
	{
		setAccessBit(13);
	}

	public void flipReadWriteLatestofMyGroupAndAllChildrenAccess()
	{
		setAccessBit(14);
	}




  public String getSuggestedViewPreferenceBasedOnAccess()
  {

	if ( canReadLatestOfTable()  )
	{
		return ViewPreferenceType.LATEST;
	}
	else if ( canReadLatestofMyGroupAndAllChildren()  )
	{
		return ViewPreferenceType.LATEST_ROWS_OF_ALL_USERS_IN_MY_NH_AND_ALL_CHD;
	}
	else if ( canReadLatestofMyGroupAndImmediateChildren()  )
	{
		return ViewPreferenceType.LATEST_ROWS_OF_ALL_USERS_IN_MY_NH_AND_IMM_CHD;
	}
	else if ( canReadLatestofMyGroup()  )
	{
		return ViewPreferenceType.LATEST_ROWS_OF_ALL_USERS_IN_MY_NH;
	}
	else
	if (  canReadWriteLatestOfMyRows()  )
	{
		return ViewPreferenceType.MY_ROWS;
	}
	else if ( canReadWriteOnMyLatestView()  )
	{
		return ViewPreferenceType.LATEST_BY_USER;
	}
	else if ( canReadLatestViewOfAll()   )
	{
		return ViewPreferenceType.LATEST_VIEW_OF_ALL_USERS;
	}
	else if (  canReadLatestViewOfAllChildren()  )
	{
		return ViewPreferenceType.LATEST_VIEW_OF_ALL_CHILDREN;
	}
	else if ( canAdministerTable()  )
	{
		return ViewPreferenceType.DESIGN;
	}

	return null;
}

  public void print()
  {
	  System.out.println(" Table Access for Table Id " + getId() + " Relationship " + getRelationship() + " ACL " +  getACL() );
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


