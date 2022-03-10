

package com.boardwalk.table;

import java.util.*;
import java.io.*;

public class TableCellValue {

   int    	m_id;
   String  	m_name;
   String m_viewPreference;


  public TableCellValue (int a_id, String a_name, String a_viewPreference ) {
    m_id = a_id;
    m_name    = a_name;
    m_viewPreference = a_viewPreference;
  }

  public int getId () {
  return m_id;
  }

  public String getName () {
  return m_name;
  }


   public String  getViewPreference() {
  	return m_viewPreference;
  }



};




