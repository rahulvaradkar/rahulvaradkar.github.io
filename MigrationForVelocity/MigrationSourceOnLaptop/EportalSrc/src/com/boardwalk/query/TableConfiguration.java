package com.boardwalk.query;

import java.util.*;
import java.io.*;


public class TableConfiguration
{

  Hashtable   m_rowIds;
  Hashtable   m_columns;
  Hashtable  m_cellIds;
  String m_asOfDate;
  int m_tableId;
  String m_viewPreference;
  boolean m_previousConfiguration = false;


  public TableConfiguration
		(
			Hashtable a_cellIds,
			Hashtable a_columns,
			Hashtable a_rowIds,
			int a_tableId,
			String a_viewPreference,
			String a_asOfDate,
			boolean a_previousConfiguration
		 )


  {
        m_rowIds        =  a_rowIds;
        m_columns = a_columns;
        m_cellIds = a_cellIds;
        m_tableId = a_tableId;
        m_viewPreference = a_viewPreference;
        m_asOfDate = a_asOfDate;
        m_previousConfiguration = a_previousConfiguration;

  }

  public Hashtable getRows()
  {
    return m_rowIds;
  }

  public Hashtable getCellsByCellId()
  {
    return m_cellIds;
  }

  public Hashtable getColumns()
   {
	   return m_columns;
  }

public int getTableId()
   {
	   return m_tableId;
  }

  public String  getConfigurationDate()
     {
  	   return m_asOfDate;
  }

   public String  getViewPreference()
   {
    	return m_viewPreference;
  }

  public boolean isPreviousConfiguration()
    {
  	  	return m_previousConfiguration;
  }
};