package com.boardwalk.table;

import java.util.*;
import java.io.*;

public class TableInfo {
    int     m_wb_id;
    String  m_wb_name;
    int     m_collab_id;
    String  m_collab_name;
    String  m_collab_purpose;
    String  m_neighborhood;
    int 	m_table_id;
    String  m_table_name;
    String  m_table_purpose;
    String 	m_defaultViewPreference;
    int     m_cr_tid;
    int     m_cr_by_id;
    String  m_cr_by_user;
    long  	m_cr_time;
    boolean m_is_locked;
    int     m_lock_tid;
    int     m_locked_by_id;
    String  m_locked_by_user;
    long  	m_locked_time;



	public TableInfo( int a_collab_id,
					  String a_collab_name,
					  String a_collab_purpose,
					  String a_neighborhood,
					  int a_wb_id,
					  String a_wb_name,
					  int a_table_id,
					  String a_table_name,
					  String a_table_purpose,
					  String a_defaultViewPreference,
					  int a_cr_tid,
					  int a_cr_by_id,
					  String a_cr_by_user,
					  long a_cr_time,
					  int a_is_locked,
					  int a_lock_tid,
					  int a_locked_by_id,
					  String a_locked_by_user,
					  long a_locked_time)
	{
		m_collab_id = a_collab_id;
		m_collab_name    = a_collab_name;
		m_collab_purpose = a_collab_purpose;
		m_neighborhood = a_neighborhood;

		m_wb_id=a_wb_id;
		m_wb_name=a_wb_name;

		m_table_id = a_table_id;
		m_table_name    = a_table_name;
		m_table_purpose = a_table_purpose;
		m_defaultViewPreference = a_defaultViewPreference;
		m_cr_tid = a_cr_tid;
		m_cr_by_id = a_cr_by_id;
		m_cr_by_user = a_cr_by_user;
		m_cr_time = a_cr_time;

		if ( a_is_locked == 1 )
			m_is_locked = true;
		else
			m_is_locked = false;
		m_lock_tid = a_lock_tid;
		m_locked_by_id = a_locked_by_id;
		m_locked_by_user = a_locked_by_user;
		m_locked_time = a_locked_time;


	}

	public int getCollaborationId ()
	{
	 return m_collab_id;
	}

	public int getWhiteboardId ()
	{
	return m_wb_id;
	}

	public String getCollaborationName ()
	{
	return m_collab_name;
	}

	public String getWhiteboardName ()
	{
	return m_wb_name;
	}

	public String getCollaborationPurpose ()
	{
	return m_collab_purpose;
	}
	public String getNeighborhood ()
	{
	return m_neighborhood;
	}

	public int getTableId ()
	{
	  return m_table_id;
	}
	public String getTableName ()
	{
	  return m_table_name;
	}
	public String getTablePurpose ()
	{
	  return m_table_purpose;
	}
	public String getTableDefaultViewPreference ()
	{
	  return m_defaultViewPreference;
	}

	public int   getCreateTid ()
	{
		return m_cr_tid;
	}
	public int   getCreateUserId ()
	{
		return m_cr_by_id;
	}
	public String getCreateUser ()
	{
		return m_cr_by_user;
	}
	public long getCreateTime ()
	{
		return m_cr_time;
	}

	public boolean  isLocked ()
	{
		return m_is_locked;
	}
	public int   lockTid ()
	{
		return m_lock_tid;
	}
	public int   getLockedByUserId ()
	{
		return m_locked_by_id;
	}
	public String getLockedByuser ()
	{
		return m_locked_by_user;
	}
	public long getLockTime ()
	{
		return m_locked_time;
	}
};


