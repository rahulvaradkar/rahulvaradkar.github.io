package com.boardwalk.table;

import java.util.*;
import java.io.*;

public class TableAccessRequest{

   int     m_table_id;
   int  m_acl = 0;
   String ViewPreference;
   	public static final String LATEST = "LATEST";
   	public static final String MY_ROWS = "MY_ROWS";
   	public static final String LATEST_BY_USER = "LATEST_BY_USER";
   	public static final String LATEST_VIEW_OF_ALL_USERS = "LATEST_VIEW_OF_ALL_USERS";
   	public static final String LATEST_VIEW_OF_ALL_CHILDREN = "LATEST_VIEW_OF_ALL_CHILDREN";
   	public static final String LOOKUP = "LOOKUP";
	public static final String DESIGN = "DESIGN";

   public TableAccessRequest(
	   															int    a_table_id,
														  		String a_viewPreference,
														  		boolean requestedWriteAccess
														    )
{

    m_table_id = a_table_id;
	ViewPreference =a_viewPreference;

	if ( ViewPreference.equals(ViewPreferenceType.LATEST) )
	{
		  		setReadLatestOfTable();
		  		if ( requestedWriteAccess )
		  		{
					setWriteLatestOfTable();
				}
	}
	else
	if ( ViewPreference.equals(ViewPreferenceType.MY_ROWS))
	{
			setReadWriteLatestOfMyRows();

	}
	else
	if ( ViewPreference.equals(ViewPreferenceType.LATEST_BY_USER))
	{
	   setReadWriteOnMyLatestView();

	}
	else
	if ( ViewPreference.equals(ViewPreferenceType.LATEST_VIEW_OF_ALL_USERS))
	{
	  setReadLatestViewOfAll();

	}
else
if ( ViewPreference.equals(ViewPreferenceType.LATEST_VIEW_OF_ALL_CHILDREN))
	{
	   setReadLatestViewOfAllChildren();

	}
else
	if ( ViewPreference.equals(ViewPreferenceType.LOOKUP))
	{
		 	setReadLatestOfTable();
		 	if ( requestedWriteAccess )
			{
				setWriteLatestOfTable();
			}
	}
	else
		if ( ViewPreference.equals(ViewPreferenceType.DESIGN))
	{
		  	setAdministerTable();
		  	if ( requestedWriteAccess )
			{
				setAdministerTable();
			}
	}
	else
			if ( ViewPreference.equals(ViewPreferenceType.LATEST_VIEW_OF_ALL_USERS_IN_ANY_NH))
		{
			  	setReadLatestViewOfAll();

	}
	else
			if ( ViewPreference.equals(ViewPreferenceType.LATEST_VIEW_OF_ALL_USERS_IN_ANY_CHILDREN_NH))
		{
			   setReadLatestViewOfAllChildren();

	}
	else
	 if ( ViewPreference.equals(ViewPreferenceType.LATEST_ROWS_OF_ALL_USERS_IN_ANY_NH))
	{
			  	setReadLatestOfTable();
			  	setReadLatestofMyGroupAndImmediateChildren();
			  	setReadLatestofMyGroupAndAllChildren();
			  	if ( requestedWriteAccess )
				{
					setWriteLatestOfTable();
					setReadWriteLatestofMyGroupAndImmediateChildren();
					setReadWriteLatestofMyGroupAndAllChildren();

				}


	}
	else
	 if ( ViewPreference.equals(ViewPreferenceType.LATEST_ROWS_OF_ALL_USERS_IN_MY_NH))
	{
				setReadLatestofMyGroup();
				if ( requestedWriteAccess )
				{
					setReadWriteLatestofMyGroup();
				}
	}
	else
	 if ( ViewPreference.equals(ViewPreferenceType.LATEST_ROWS_OF_ALL_USERS_IN_MY_NH_AND_IMM_CHD))
	{
				setReadLatestofMyGroupAndImmediateChildren();
				if ( requestedWriteAccess )
				{
					setReadWriteLatestofMyGroupAndImmediateChildren();
				}
	}
	else
	 if ( ViewPreference.equals(ViewPreferenceType.LATEST_ROWS_OF_ALL_USERS_IN_MY_NH_AND_ALL_CHD))
	{
				setReadLatestofMyGroupAndAllChildren();
				if ( requestedWriteAccess )
				{
					setReadWriteLatestofMyGroupAndAllChildren();
				}
	}
}



  public int getACL () {
  return m_acl;
  }

  private boolean getAccessBit( int n )
  {
  		return ((m_acl & (1<<n)) != 0);
  }


private void setAddRow()
  {
	  m_acl = 	m_acl | (1<<0);

  }
private void setDeleteRow()
  {
	  m_acl = 	m_acl | (15<<0);

  }

  private void setAdministerColumn()
  {
	  	m_acl =  m_acl | (1<<1);
  }

    private  void setAdministerTable()
   {
    	  m_acl = 	m_acl | (1<<7);
    }


/* Latest View of the User */


  private void setReadWriteOnMyLatestView()
    {
  	  m_acl = 	m_acl | (1<<2);
  }
  private void setReadLatestViewOfAll()
    {
  	m_acl =   m_acl | (1<<3);
  }

  private void setReadLatestViewOfAllChildren()
    {
  	  m_acl = 	m_acl | (1<<4);
  }


  /* Latest View of the Table */

  private void setReadLatestOfTable()
    {
  	  m_acl = 	m_acl | (1<<5);
  }
  private void  setWriteLatestOfTable()
  {
      	m_acl =   	m_acl | (1<<8);
   }
  private void setReadWriteLatestOfMyRows()
  {
  	  m_acl =    m_acl | (1<<6);
  }


    private void setReadLatestofMyGroup()
      {
    	  m_acl = 	m_acl | (1<<9);
    }

    private void setReadWriteLatestofMyGroup()
	      {
	    	  m_acl = 	m_acl | (1<<10);
    }

    private void  setReadLatestofMyGroupAndImmediateChildren()
    {
        	m_acl =   	m_acl | (1<<11);
     }

      private void  setReadWriteLatestofMyGroupAndImmediateChildren()
	     {
	         	m_acl =   	m_acl | (1<<12);
     }


	private void setReadLatestofMyGroupAndAllChildren()
	  {
    	  m_acl =    m_acl | (1<<13);
  }

  private void setReadWriteLatestofMyGroupAndAllChildren()
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
	  System.out.println(" Table Request Access");
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


