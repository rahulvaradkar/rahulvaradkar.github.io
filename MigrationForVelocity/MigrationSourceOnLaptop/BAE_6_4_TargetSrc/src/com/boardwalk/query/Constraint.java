package com.boardwalk.query;

import java.util.*;
import java.io.*;

import com.boardwalk.database.*;
import com.boardwalk.exception.*;
import com.boardwalk.table.*;


import java.sql.*;                  // JDBC package
import javax.sql.*;                 // extended JDBC package



public class Constraint{

	  public final static String DATE_KEYWORD="DATE";
	  public final static String PREVIOUS_CONFIGURATION="PREVIOUS_CONFIGURATION";

	  public final static String STRINGLIKE_OPERATOR = "LIKE";
	  public final static String LESSTHAN_EQUALTO_OPERATOR = "LESSTHAN_EQUALTO";
	  public final static String MORETHAN_EQUALTO_OPERATOR = "MORETHAN_EQUALTO";
	  public final static String EQUALTO_OPERATOR = "EQUAL";



	  String m_ConstraintType;
	  String m_ConstraintOperator;
	  String m_ConstraintValue;

	  public Constraint(String a_ConstraintType, String a_ConstraintOperator, String a_ConstraintValue )
	  {

		  m_ConstraintType = a_ConstraintType;
		  m_ConstraintOperator = a_ConstraintOperator;
		  m_ConstraintValue = a_ConstraintValue;
	  }

	  public String getConstraintType()
	  {
		  return m_ConstraintType;
	  }

	  public String getConstraintOperator()
	  {
	  	  return m_ConstraintOperator;
	  }

	  public String getConstraintValue()
	  {
	       return m_ConstraintValue;
	  }

};


