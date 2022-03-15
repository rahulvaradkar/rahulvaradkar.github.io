package com.boardwalk.query;

import java.util.*;
import java.io.*;

import com.boardwalk.database.*;
import com.boardwalk.exception.*;
import com.boardwalk.table.*;


import java.sql.*;                  // JDBC package
import javax.sql.*;                 // extended JDBC package



public class Query{


	  public static String 	CELL_QUERY_TYPE = "CELL";
	  public static String 	ROW_QUERY_TYPE = "ROW";
	  public static String 	TABLE_QUERY_TYPE = "TABLE";
	   public static String  TABLE_DELTA_COLUMN_QUERY_TYPE = "TABLE_DELTA_COLUMN";
	  public static String 	COLUMN_QUERY_TYPE = "COLUMN";



      Vector m_Columns = new Vector();

      Vector m_Scope = new Vector();

      Vector m_Constraints = new Vector();

      boolean  m_getLatest;
	  boolean  m_getBaselines;
	  boolean  m_getBaselinesandLatest;
	  boolean  m_getAllVersions;



	  String m_queryType;

      public Query(String a_queryType)
      {
		  m_queryType = a_queryType;
		  m_getLatest = false;
		  m_getBaselines= false;
		  m_getBaselinesandLatest= false;
	  	  m_getAllVersions= false;

	  }

	public String getType() {
		return m_queryType;
	}


	  public void addScope( Scope a_Scope )
	  {
		  m_Scope.addElement( a_Scope );
	  }

	  public void addColumn( String a_ColumnName )
	  {
	  	  m_Columns.addElement( a_ColumnName );
	  }

	  public void addConstraint( Constraint a_Constraint )
	  {
	  	  m_Constraints.addElement( a_Constraint );
	  }

	  public boolean isLatest( )
	  {
	   	  return m_getLatest;
	  }

	  public boolean isGetBaselines()
	  {
	   	  return m_getBaselines;
	  }

	  public boolean isGetBaselinesandLatest()
	  {
	  	   	  return m_getBaselinesandLatest;
	  }

	  public boolean isGetAllVersions()
	  {
	  	 return m_getAllVersions;
	  }


	  public void setisLatest(boolean a_getLatest )
	  {
		  m_getLatest = a_getLatest;
	  }

	  public void setisGetBaselines(boolean a_getBaselines)
	  {
		  m_getBaselines = a_getBaselines;
	  }

	  public void setisGetBaselinesandLatest(boolean a_getBaselinesandLatest)
	  {
			  m_getBaselinesandLatest = a_getBaselinesandLatest;
	  }

	  public void setisGetAllVersions(boolean a_getAllVersions)
	  {
		 m_getAllVersions = a_getAllVersions;
	  }


	  public Vector getScope(  )
	  {
		  return m_Scope;
	  }

	  public Vector getColumns(  )
	  {
		  return m_Columns;
	  }

	  public Vector getConstraints( )
	  {
	  	 	return m_Constraints;
	  }





};


