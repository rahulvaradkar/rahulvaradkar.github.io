

package com.boardwalk.table;

import java.util.*;
import java.io.*;

public class Table {

   int    	m_id;
   String  	m_name;
   String 	m_purpose;
   int  	m_access;
   int  	m_neighborhood;
   int  	m_status;
   int  	m_wb_id;
   float 		m_sequence_number;
   String m_viewPreference;


  public Table (int a_id, int a_wb_id, String a_name, String a_purpose, int a_access, int a_neighborhood, int a_status, float a_sequence_number, String a_viewPreference ) {
    m_id = a_id;
    m_wb_id = a_wb_id;
    m_name    = a_name;
    m_purpose = a_purpose;
    m_access   = a_access;
    m_neighborhood = a_neighborhood;
    m_status = a_status;
    m_sequence_number = a_sequence_number;
    m_viewPreference = a_viewPreference;
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

  public String getAccessLevel() {

      if ( m_access == 0 )
          return "NO";
      else
          if ( m_access == 1 )
              return "RO";
          else
              return "RW";


  }

  public int getNeighborhood () {
  return m_neighborhood;
  }

  public int getStatus() {
   return m_status;
  }

  public float getSequenceNumber() {
   return m_sequence_number;
  }

  public int getWhiteboardId() {
	return m_wb_id;
  }

   public String  getDefaultViewPreference() {
  	return m_viewPreference;
  }

  public void printTable()
  {
    System.out.println( " ID = " + m_id + " NAME " + m_name + " PURPOSE " + m_purpose + " ACCESS " + m_access + " STATUS " + m_status  + " WHITEBOARD " + m_wb_id  + " NEIGHBORHOOD " + m_neighborhood + " SEQUENCE_NUMBER " + m_sequence_number + " VIEW PREFERENCE" + m_viewPreference);
  }

};




