/*
 * RowContents.java
 *
 * Created on March 27, 2000, 8:16 AM
 */

package com.boardwalk.table;
import java.util.*;
import java.io.*;

/**
 *
 * @author  administrator
 * @version 
 */
public class RowContents {

  Vector    m_columnNames; // ordered set of columns ...should be used to access the m_cellsByColumnName hashtable
  Hashtable m_cellsByColumnName; // bucketed by Column Name to access Cell 
    
  public RowContents ( Vector a_columnNames, Hashtable a_cellsByColumnName )
  {
        m_columnNames   = a_columnNames;
        m_cellsByColumnName  = a_cellsByColumnName;
  }
  
  public Vector getColumnNames()
  {
    return m_columnNames;
  }
  
  public Hashtable getCellsByColumnName()
  {
    return m_cellsByColumnName;
  }
}
