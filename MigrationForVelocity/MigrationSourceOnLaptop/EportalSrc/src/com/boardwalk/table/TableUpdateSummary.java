

package com.boardwalk.table;

import java.util.*;
import java.io.*;

public class TableUpdateSummary {
	int		m_id;
	int		m_table_created_tid;
	String 	m_table_created_by;
	long 	m_table_created_on;
	int		m_last_update_tid;
	String 	m_last_update_by;
	long 	m_last_update_on;
	String	m_last_update_comment;



  public TableUpdateSummary (
	  				int a_id,
					int a_table_created_tid,
					String a_table_created_by,
					long a_table_created_on,
	  				int a_last_update_tid,
  					String a_last_update_by,
  					long a_last_update_on,
  					String a_last_update_comment )
  {
	  m_id = a_id;
	  m_table_created_tid = a_table_created_tid;
	  m_table_created_by = a_table_created_by;
	  m_table_created_on = a_table_created_on;
	  m_last_update_tid = a_last_update_tid;
	  m_last_update_by = a_last_update_by;
	  m_last_update_on = a_last_update_on;
	  m_last_update_comment = a_last_update_comment;
  }

  public int getId ()
  {
  	return m_id;
  }
  public int getTableCreatedTid ()
  {
  	return m_table_created_tid;
  }

  public String getTableCreatedBy ()
  {
  	return m_table_created_by;
  }
  public long getTableCreatedOn ()
  {
  	return m_table_created_on;
  }
  public int getLastUpdateTid ()
  {
  	return m_last_update_tid;
  }

  public String getLastUpdatedBy ()
  {
  	return m_last_update_by;
  }
  public long getLastUpdatedOn ()
  {
  	return m_last_update_on;
  }
  public String getLastUpdateComment ()
  {
  	return m_last_update_comment;
  }


};




