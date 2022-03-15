package com.boardwalk.query;

import java.util.*;
import java.io.*;

import com.boardwalk.database.*;
import com.boardwalk.exception.*;
import com.boardwalk.table.*;


import java.sql.*;                  // JDBC package
import javax.sql.*;                 // extended JDBC package



public class Scope{

	  public final static String TABLE_SCOPE="TABLE";
	  public final static String WHITEBOARD_SCOPE="WHITEBOARD";
	  public final static String COLLABORATION_SCOPE="COLLABORATION";
	  public final static String BASELINE_SCOPE="BASELINE";
	  public final static String CELL_SCOPE="CELL";
	  public final static String COLUMN_SCOPE="COLUMN";






      public Scope(String a_ScopeType, int a_scopeId )
	  {

		  m_ScopeType = a_ScopeType;
		  m_scopeId = a_scopeId;
	  }


	  public String    m_ScopeType;

	  public int    m_scopeId;


	  public int getScopeId()
	  {

		  return m_scopeId;
	  }

	  public String getScopeType()
	  {
	  	  return m_ScopeType;
	  }

};


