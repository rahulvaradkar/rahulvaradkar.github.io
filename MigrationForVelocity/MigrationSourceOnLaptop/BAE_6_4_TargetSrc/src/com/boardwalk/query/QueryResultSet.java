package com.boardwalk.query;

import java.util.*;
import java.io.*;

import com.boardwalk.database.*;
import com.boardwalk.exception.*;
import com.boardwalk.table.*;


import java.sql.*;                  // JDBC package
import javax.sql.*;                 // extended JDBC package



public class QueryResultSet{


    public QueryResultSet() {}


      Vector    m_columnNames;
	  Vector    m_vectorCells;
	  String m_description;



	  public QueryResultSet ( Vector a_vectorCells, Vector a_columnNames,  String a_description )
	  {
	        m_columnNames   = a_columnNames;
	        m_vectorCells  = a_vectorCells;
	        m_description = a_description;

	  }




	  public Vector getRows()
	  {
	    return m_vectorCells;
	  }

	  public Vector getColumnNames()
	  {
	    return m_columnNames;
	  }


	    public String  getDescription(){
	  	  		  return m_description;
	  }

};


