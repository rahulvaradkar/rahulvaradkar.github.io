

package com.boardwalk.table;

import java.util.*;
import java.io.*;

public class TableTreeNode {

   int    	m_id;
   String  	m_name;
   String 	m_purpose;
   int  	m_access;
   int  	m_neighborhood;
   int  	m_status;
   int  	m_wb_id;
   float 	m_sequence_number;



  public TableTreeNode (int a_id, int a_wb_id, String a_name, String a_purpose, int a_access ) {
    m_id = a_id;
    m_wb_id = a_wb_id;
    m_name    = a_name;
    m_purpose = a_purpose;
    m_access   = a_access;
  }

  public int getId () {
  return m_id;
  }

  public String getName () {
  return m_name;
  }
  public String getPurpose () {
  return m_purpose;
  }

  public int getAccess () {
  return m_access;
  }

  public void  setAccess (int a_access) {
    m_access = a_access;
  }


  public int getWhiteboardId() {
	return m_wb_id;
  }

   public String  getDefaultViewPreference()
   {
	   TableAccessList tbACL = new TableAccessList( -1,
	   														  m_id,
	   														  "",
														  m_access);
  		return tbACL.getSuggestedViewPreferenceBasedOnAccess();
  }

  public void printTable()
  {
    System.out.println( " ID = " + m_id + " NAME " + m_name + " PURPOSE " + m_purpose + " ACCESS " + m_access + " STATUS " + m_status  + " WHITEBOARD " + m_wb_id  + " NEIGHBORHOOD " + m_neighborhood + " SEQUENCE_NUMBER " + m_sequence_number + " VIEW PREFERENCE" + getDefaultViewPreference() );
  }

};




