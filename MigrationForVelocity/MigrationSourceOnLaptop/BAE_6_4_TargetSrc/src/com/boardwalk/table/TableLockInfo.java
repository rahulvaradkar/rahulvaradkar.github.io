package com.boardwalk.table;

import java.util.*;
import java.io.*;

public class TableLockInfo {

    int m_table_id;
   boolean  m_is_locked;
    int             m_lock_tid;
    int               m_locked_by_id;
    String         m_locked_by_user;
    String         m_locked_time;


    public TableLockInfo( int a_table_id, int  a_is_locked,
       									 int            a_lock_tid,
    									int           a_locked_by_id,
    									String         a_locked_by_user,
    									String         a_locked_time)
    									{

    m_table_id = a_table_id;
     if ( a_is_locked == 1 )
		 	m_is_locked = true;
		 else
	 	m_is_locked = false;
     m_lock_tid = a_lock_tid;
     m_locked_by_id = a_locked_by_id;
     m_locked_by_user = a_locked_by_user;
      m_locked_time =a_locked_time;


  }





  public int getTableId () {
  return m_table_id;
  }

 public int   getLockedByUserId () {
      return m_locked_by_id;
  }




 public boolean  isLocked () {
  return m_is_locked;
  }

  public int   lockTid () {
    return m_lock_tid;
  }



 public String getLockedByuser () {
  return m_locked_by_user;
  }


  public String getLockTime () {
  	return m_locked_time;
  }




};


