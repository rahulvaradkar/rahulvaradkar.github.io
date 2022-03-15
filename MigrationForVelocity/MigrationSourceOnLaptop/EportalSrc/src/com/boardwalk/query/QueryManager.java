package com.boardwalk.query;

import java.util.*;
import java.io.*;

import com.boardwalk.database.*;
import com.boardwalk.exception.*;
import com.boardwalk.table.*;


import java.sql.*;                  // JDBC package
import javax.sql.*;                 // extended JDBC package



public class QueryManager{

    private static String CALL_BW_GET_CELL_VERSIONS="{CALL BW_GET_CELL_VERSIONS(?,?,?,?)}";
    private static String CALL_BW_GET_PREVIOUS_CELL_VERSION="{CALL BW_GET_PREVIOUS_CELL_VERSION(?)}";
    private static String CALL_BW_GET_MAX_TID_FOR_DATE="{CALL BW_GET_MAX_TID_FOR_DATE(?,?,?)}";
    private static String CALL_BW_GET_CELL_VER_BEF_TID="{CALL BW_GET_CELL_VER_BEF_TID(?,?)}";
    private static String CALL_BW_GET_CL_AT_LAST_EXP_FOR_USR="{CALL BW_GET_CL_AT_LAST_EXP_FOR_USR(?,?,?)}";
 	private static String CALL_BW_GET_CL_VERS_FOR_C_FOR_DATE="{CALL BW_GET_CL_VERS_FOR_C_FOR_DATE(?,?)}";
	private static String CALL_BW_GET_PREV_CL_VERS_FOR_C="{CALL BW_GET_PREV_CL_VERS_FOR_C(?)}";
    private static String CALL_BW_GET_PREVIOUS_TBL_CONFIG="{CALL BW_GET_PREVIOUS_TBL_CONFIG(?)}";





    public QueryManager() {}


	public static QueryResultSet processQuery( Connection connection, Query query ) throws QuerySyntaxException
	{

		QueryResultSet resultSet = null;

		if ( query.getType().equals( Query.CELL_QUERY_TYPE ) )
		{
			Scope scope = (Scope)query.getScope().elementAt(0);

			if (  ! scope.getScopeType().equals( Scope.CELL_SCOPE ) )
			{
				throw new QuerySyntaxException(" No Cell scope in the Query  for Cell" );
			}

			int cellId = scope.getScopeId();
			String startDate = null;
			String endDate = null;
			String likeValue = null;
			boolean previousConfiguration = false;

			Vector constrs = query.getConstraints();

			if ( constrs.size() > 0 )
			{
				for ( int i = 0; i < constrs.size(); i++ )
				{
					Constraint constr = (Constraint)constrs.elementAt(i);
					if ( constr.getConstraintType().equals(Constraint.DATE_KEYWORD))
					{
						if ( constr.getConstraintOperator().equals(Constraint.MORETHAN_EQUALTO_OPERATOR ))
						{
							startDate = constr.getConstraintValue();
						}
						else
						{
							endDate = constr.getConstraintValue();
						}
					}

					if ( constr.getConstraintType().equals(Constraint.STRINGLIKE_OPERATOR))
					{
						likeValue = constr.getConstraintValue();
					}

					if  ( constr.getConstraintType().equals(Constraint.PREVIOUS_CONFIGURATION))
					{
						previousConfiguration = true;
					}

				}
			}

			if ( query.isGetAllVersions() )
			{

				try
				{
					resultSet =  getCellVersions( connection, cellId , startDate, endDate,likeValue);
				}
				catch( SystemException sysexp )
				{
					throw new QuerySyntaxException( " Query failed due to System error " + sysexp.getMessage() );
				}
			}
			else
			{
				if  ( previousConfiguration )
				{
					try
					{
						resultSet =  getPreviousCellVersion( connection, cellId);
					}
					catch( SystemException sysexp )
					{
						throw new QuerySyntaxException( " Query failed due to System error " + sysexp.getMessage() );
					}
				}
				else
					return null;

			}
		}
		else
		{
			return null;
		}

		return resultSet;
	}





	public static TableConfiguration  processTableConfigurationQuery( Connection connection, Query query ) throws QuerySyntaxException,SystemException
	{
		TableConfiguration tableConfiguration = null;

		if ( query.getType().equals( Query.TABLE_QUERY_TYPE ) )
		{
			Scope scope = (Scope)query.getScope().elementAt(0);

			if (  ! scope.getScopeType().equals( Scope.TABLE_SCOPE ) )
			{
				throw new QuerySyntaxException(" No Table  scope in the Query  for Table" );
			}

			int tableId = scope.getScopeId();
			String startDate = null;
			String endDate = null;
			boolean previousConfiguration = false;

			Vector constrs = query.getConstraints();

			if ( constrs.size() > 0 )
			{
				for ( int i = 0; i < constrs.size(); i++ )
				{
					Constraint constr = (Constraint)constrs.elementAt(i);
					if ( constr.getConstraintType().equals(Constraint.DATE_KEYWORD))
					{
						if ( constr.getConstraintOperator().equals(Constraint.MORETHAN_EQUALTO_OPERATOR ))
						{
							startDate = constr.getConstraintValue();
						}
						else
						{
							endDate = constr.getConstraintValue();
						}
					}
					else
					{
						if  ( constr.getConstraintType().equals(Constraint.PREVIOUS_CONFIGURATION))
						{
								previousConfiguration = true;
						}
					}
				}
			}

			if  (  previousConfiguration == false )
			{
				tableConfiguration =  getTableConfiguration( connection,  tableId,  startDate);
			}
			else
			{
				tableConfiguration =  getPreviousTableConfiguration( connection,  tableId);
			}
		}
		return tableConfiguration;
	}


	public static ColumnConfiguration  processOrderedColumnQuery( Connection connection, Query query, TableContents tbcon , String viewPreference ) throws QuerySyntaxException,SystemException
	{

		ColumnConfiguration columnConfiguration = null;

		if ( query.getType().equals( Query.COLUMN_QUERY_TYPE ) )
		{
			Scope scope = (Scope)query.getScope().elementAt(0);

			if (  ! scope.getScopeType().equals( Scope.COLUMN_SCOPE ) )
			{
				throw new QuerySyntaxException(" No Column  scope in the Query  for Columns" );
			}

			int columnId = scope.getScopeId();
			String startDate = null;
			String endDate = null;
			boolean previousConfiguration = false;

			Vector constrs = query.getConstraints();

			if ( constrs.size() > 0 )
			{
				for ( int i = 0; i < constrs.size(); i++ )
				{
					Constraint constr = (Constraint)constrs.elementAt(i);
					if ( constr.getConstraintType().equals(Constraint.DATE_KEYWORD))
					{
							if ( constr.getConstraintOperator().equals(Constraint.MORETHAN_EQUALTO_OPERATOR ))
							{
								startDate = constr.getConstraintValue();
							}
							else
							{
								endDate = constr.getConstraintValue();
							}
					}
					else
					{
						if  ( constr.getConstraintType().equals(Constraint.PREVIOUS_CONFIGURATION))
						{
								previousConfiguration = true;
						}
					}
				}
			}

			Column column = (Column)tbcon.getColumnsByColumnId().get( new Integer(columnId) );
			System.out.println("Track Changes: for ordered column" + column.getColumnName());
			Hashtable columnAsOfDate  = null;
			if ( previousConfiguration == false )
			{
				columnAsOfDate  =  getColumnConfiguration( connection,  columnId,  startDate);
			}
			else
			{
				columnAsOfDate  =  getPreviousColumnConfiguration( connection,  columnId);

			}

			//java.util.Date todaysDate = new java.util.Date();
			Calendar cal = new GregorianCalendar();
			String tDate = (cal.get(Calendar.MONTH) +1) + "/" + cal.get(Calendar.DAY_OF_MONTH) + "/" + cal.get(Calendar.YEAR)   + "  " + cal.get(Calendar.HOUR_OF_DAY)  + ":" + cal.get(Calendar.MINUTE)  ;
			System.out.println("Todays date is : " + tDate);
			System.out.println("Oredered column id  : " + column.getOrderedColumnId());
			Hashtable orderedcolumnAsOfToday  =  getOrderedColumnConfiguration( connection,  column.getOrderedColumnId(),  tDate);
			System.out.println("processOrderedColumnQuery: orderedcolumnAsOfToday = " + orderedcolumnAsOfToday);


			if ( previousConfiguration == false )
			{
					 columnConfiguration = new ColumnConfiguration( columnAsOfDate, orderedcolumnAsOfToday, tbcon, columnId, viewPreference, startDate, false );
			}
			else
			{
					columnConfiguration = new ColumnConfiguration( columnAsOfDate, orderedcolumnAsOfToday, tbcon, columnId, viewPreference, null, true );
			}
		}
		return columnConfiguration;
	}



    public static QueryResultSet getCellVersions(
			Connection connection, int cellId , String startDate,
			String endDate,String likeValue) throws SystemException
    {
		ResultSet resultset = null;
		PreparedStatement preparedstatement = null;
		Hashtable ht = new Hashtable();
		Vector columnNames = new Vector();

		String a_cellType = null;
		String a_tableName = null;
		String a_columnName = null;
		Vector vecOfCellsPerRow = new Vector();

		String description = "Versions of the selected Cell of type ";
		String dateDescription = "";
		System.out.println("Startdate = " + startDate + " End date = " + endDate);
		dateDescription = ", on and after date " + startDate;
		dateDescription = dateDescription + ",  on and before date " + endDate;
		if ( likeValue == null )
		{
			likeValue = "%";
		}

		System.out.println("CELLID = " + cellId +  "Startdate = " + startDate + " End date = " + endDate + " Like Value = " + likeValue );
		QueryResultSet bwResultSet = null;

		try
		{
			if ( DatabaseLoader.getDatabaseType().trim().equalsIgnoreCase("ORACLE"))
			{
				java.sql.Date sDate = new java.sql.Date(java.sql.Date.parse(startDate));
				java.sql.Date eDate = new java.sql.Date(java.sql.Date.parse(endDate));
				System.out.println("CELLID = " + cellId +  "Startdate = " + sDate.toString() + " End date = " + eDate.toString() + " Like Value = " + likeValue );

				preparedstatement = DatabaseLoader.getPreparedStatementFromPreLoadedQueries("BW_GET_CELL_VERSIONS", connection );
				preparedstatement.setInt(1,cellId);
				preparedstatement.setString(2,likeValue);
				preparedstatement.setDate(3,sDate);
				preparedstatement.setDate(4,eDate);
				preparedstatement.setInt(5,cellId);
				preparedstatement.setString(6,likeValue);
				preparedstatement.setDate(7,sDate);
				preparedstatement.setDate(8,eDate);
				preparedstatement.setInt(9,cellId);
				preparedstatement.setString(10,likeValue);
				preparedstatement.setDate(11,sDate);
				preparedstatement.setDate(12,eDate);
			}
			else
			{
				preparedstatement = connection.prepareStatement(CALL_BW_GET_CELL_VERSIONS);
				preparedstatement.setInt(1,cellId);
				preparedstatement.setString(2,startDate);
				preparedstatement.setString(3,endDate);
				preparedstatement.setString(4,likeValue);
			}
			resultset = preparedstatement.executeQuery();

			Vector newRow = null;


			while ( resultset.next() )
			{
				newRow = new Vector();

				int a_cellId = resultset.getInt("CELL_ID");
				a_cellType = resultset.getString("CELL_TYPE");
				String a_stringValue = resultset.getString("CELL_STRING_VALUE");
				int a_intValue = resultset.getInt("CELL_INTEGER_VALUE");
				float a_doubleValue = resultset.getFloat("CELL_DOUBLE_VALUE");
				int a_tableValue = resultset.getInt("CELL_TBL_VALUE");
				int a_trans_id = resultset.getInt("TRANSACTION_ID");
				String a_createdOnDate = resultset.getString("CREATED_ON");
			//	java.sql.Date creationDate = new java.sql.Date( a_createdOnDate);
				int a_createdByUserid = resultset.getInt("CREATED_BY");
				String tdescription = "";
				String tcomment = "";
				tdescription = resultset.getString("DESCRIPTION");
				tcomment = resultset.getString("COMMENT_");
				String a_createdByUser = resultset.getString("EMAIL_ADDRESS");

				Transaction cellTransaction = new Transaction(a_trans_id, a_createdByUserid,
													a_createdByUser,a_createdOnDate,tdescription, tcomment);

				int a_rowid = resultset.getInt("ROW_ID");
				int a_columnid = resultset.getInt("COLUMN_ID");
				a_columnName = resultset.getString("COLUMN_NAME");
				a_tableName = resultset.getString("TABLE_NAME");
				String cellFormula = resultset.getString("FORMULA");

				VersionedCell cell = new VersionedCell(a_cellId, a_columnid, a_columnName, a_rowid,
						a_cellType, a_stringValue, a_intValue, a_doubleValue, a_tableValue, a_tableName,
						cellTransaction, cellFormula  );

				newRow.addElement(cell);

				vecOfCellsPerRow.addElement( newRow );
			}

			System.out.println("Got number of rows  " + vecOfCellsPerRow.size() );
			description = description + a_cellType + " from Table " + a_tableName + " and ColumnName  " + a_columnName + dateDescription;
			columnNames.addElement( a_columnName );
			bwResultSet = new QueryResultSet(  vecOfCellsPerRow, columnNames, description );

		}
		catch(SQLException sqlexception)
		{
			sqlexception.printStackTrace();
			throw new SystemException(sqlexception);
		}
		finally {
			try {
				if ( resultset != null ) {
					resultset.close();
				}
				if ( preparedstatement != null ) {
					preparedstatement.close();
				}
			}
			catch(SQLException sqlexception1) {

				sqlexception1.printStackTrace();
				throw new SystemException(sqlexception1);
			}
        }

		return bwResultSet;

	}

	public static QueryResultSet getPreviousCellVersion( Connection connection, int cellId ) throws SystemException
	{
		ResultSet resultset = null;
		PreparedStatement preparedstatement = null;
		Hashtable ht = new Hashtable();
		Vector columnNames = new Vector();

		String a_cellType = null;
		String a_tableName = null;
		String a_columnName = null;
		Vector vecOfCellsPerRow = new Vector();

		String description = "Previous versions of the selected Cell of type ";
		String dateDescription = "";



		QueryResultSet bwResultSet = null;

		try
		{
			if ( DatabaseLoader.getDatabaseType().trim().equalsIgnoreCase("ORACLE"))
			{
				preparedstatement = DatabaseLoader.getPreparedStatementFromPreLoadedQueries("BW_GET_PREVIOUS_CELL_VERSION", connection );
				preparedstatement.setInt(1,cellId);
				preparedstatement.setInt(2,cellId);
				preparedstatement.setInt(3,cellId);
				preparedstatement.setInt(4,cellId);
				preparedstatement.setInt(5,cellId);
				preparedstatement.setInt(6,cellId);
			}
			else
			{
				preparedstatement = connection.prepareStatement(CALL_BW_GET_PREVIOUS_CELL_VERSION);
				preparedstatement.setInt(1,cellId);
			}


			resultset = preparedstatement.executeQuery();

			Vector newRow = null;


			while ( resultset.next() )
			{
				newRow = new Vector();
				int a_cellId = resultset.getInt("CELL_ID");
				a_cellType = resultset.getString("CELL_TYPE");
				String a_stringValue = resultset.getString("CELL_STRING_VALUE");
				int a_intValue = resultset.getInt("CELL_INTEGER_VALUE");
				float a_doubleValue = resultset.getFloat("CELL_DOUBLE_VALUE");
				int a_tableValue = resultset.getInt("CELL_TBL_VALUE");
				int a_trans_id = resultset.getInt("TX_ID");
				String a_createdOnDate = resultset.getString("CREATED_ON");
			//	java.sql.Date creationDate = new java.sql.Date( a_createdOnDate);
				int a_createdByUserid = resultset.getInt("CREATED_BY");
				String tdescription = "";
				String tcomment = "";
				tdescription = resultset.getString("DESCRIPTION");
				tcomment = resultset.getString("COMMENT_");
				String a_createdByUser = resultset.getString("EMAIL_ADDRESS");

				Transaction cellTransaction = new Transaction(a_trans_id, a_createdByUserid,
															a_createdByUser,a_createdOnDate,tdescription, tcomment);

				int a_rowid = resultset.getInt("ROW_ID");
				int a_columnid = resultset.getInt("COLUMN_ID");
				a_columnName = resultset.getString("COLUMN_NAME");
				a_tableName = resultset.getString("TABLE_NAME");
				String cellFormula = resultset.getString("FORMULA");

				VersionedCell cell = new VersionedCell(a_cellId, a_columnid, a_columnName, a_rowid, a_cellType,
						a_stringValue, a_intValue, a_doubleValue, a_tableValue, a_tableName,
						cellTransaction , cellFormula );

				newRow.addElement(cell);

				vecOfCellsPerRow.addElement( newRow );
			}

			System.out.println("Got number of rows  " + vecOfCellsPerRow.size() );
			description = description + a_cellType + " from Table " + a_tableName + " and ColumnName  " + a_columnName + dateDescription;
			columnNames.addElement( a_columnName );
			bwResultSet = new QueryResultSet(  vecOfCellsPerRow, columnNames, description );

		}
		catch(SQLException sqlexception)
		{
			sqlexception.printStackTrace();
			throw new SystemException(sqlexception);
		}
		finally {
			try {
				if ( resultset != null ) {
					resultset.close();
				}
				if ( preparedstatement != null ) {
					preparedstatement.close();
				}
			}
			catch(SQLException sqlexception1) {

				sqlexception1.printStackTrace();
				throw new SystemException(sqlexception1);
			}
		}

		return bwResultSet;
	}


	public static Hashtable  getCellVersions( Connection connection, int tid, int table_id) throws SystemException,SQLException
	{
		ResultSet resultset = null;
		PreparedStatement preparedstatement = null;
		Hashtable ht = new Hashtable();

		try
		{
			if ( DatabaseLoader.getDatabaseType().trim().equalsIgnoreCase("ORACLE"))
			{
				preparedstatement = DatabaseLoader.getPreparedStatementFromPreLoadedQueries("BW_GET_CELL_VER_BEF_TID", connection );
				preparedstatement.setInt(1,table_id);
				preparedstatement.setInt(2,tid);
				preparedstatement.setInt(3,tid);
				preparedstatement.setInt(4,table_id);
				preparedstatement.setInt(5,tid);
				preparedstatement.setInt(6,table_id);
				preparedstatement.setInt(7,tid);
				preparedstatement.setInt(8,tid);
				preparedstatement.setInt(9,table_id);
				preparedstatement.setInt(10,tid);
				preparedstatement.setInt(11,table_id);
				preparedstatement.setInt(12,tid);
				preparedstatement.setInt(13,tid);
				preparedstatement.setInt(14,table_id);
				preparedstatement.setInt(15,tid);
			}
			else
			{
				preparedstatement = connection.prepareStatement(CALL_BW_GET_CELL_VER_BEF_TID);
				preparedstatement.setInt(1,table_id);
				preparedstatement.setInt(2,tid);
			}


			resultset = preparedstatement.executeQuery();


			while ( resultset.next() )
			{

				int a_cellId = resultset.getInt("CELL_ID");
				String a_cellType = resultset.getString("CELL_TYPE");
				String a_stringValue = resultset.getString("CELL_STRING_VALUE");
				int a_intValue = resultset.getInt("CELL_INTEGER_VALUE");
				float a_doubleValue = resultset.getFloat("CELL_DOUBLE_VALUE");
				int a_tableValue = resultset.getInt("CELL_TBL_VALUE");
				int a_trans_id = resultset.getInt("TX_ID");
				String description = "";
				String comment = "";
				description = resultset.getString("DESCRIPTION");
				comment = resultset.getString("COMMENT_");
				String created_by = resultset.getString("EMAIL_ADDRESS");
				String created_on = resultset.getString("CREATED_ON");
				int a_rowId = resultset.getInt("BW_ROW_ID");
				int a_columnId = resultset.getInt("BW_COLUMN_ID");

				OriginalCell cell = new OriginalCell(
					a_cellId, a_cellType, a_stringValue, a_intValue,
					a_doubleValue, a_tableValue, a_trans_id);
				// System.out.println("Adding orginial Cell");
				// cell.printCell();
				ht.put(new Integer(a_cellId), cell);
			}
		}
		catch(SQLException sqlexception)
		{
			throw sqlexception;
		}
		finally {
			try {
				if ( resultset != null ) {
					resultset.close();
				}
				if ( preparedstatement != null ) {
					preparedstatement.close();
				}

			}
			catch(SQLException sqlexception1) {

				sqlexception1.printStackTrace();
				throw new SystemException(sqlexception1);
			}
		}

		return ht;
	}


	public static TableConfiguration  getTableConfiguration(
		Connection connection, int table_id,String asOfDate ) throws SystemException
	{
		ResultSet resultset = null;
		PreparedStatement preparedstatement = null;
		CallableStatement callablestatement = null;
		Hashtable cells  = new Hashtable();
		Hashtable rowIds  = new Hashtable();
		Hashtable columnIds  = new Hashtable();

		try
		{
			// First get the transaction ID
			java.sql.Date date_ = new java.sql.Date(java.sql.Date.parse(asOfDate));

			callablestatement = connection.prepareCall(CALL_BW_GET_MAX_TID_FOR_DATE);
			callablestatement.setInt(1,table_id);
			callablestatement.setDate(2,date_);
			callablestatement.registerOutParameter(3,java.sql.Types.INTEGER);

			callablestatement.execute();

			int maxTid = callablestatement.getInt(3);
			System.out.println(" maxt id before the date" + date_  + " is " + maxTid );

			if ( DatabaseLoader.getDatabaseType().trim().equalsIgnoreCase("ORACLE"))
			{
				preparedstatement = DatabaseLoader.getPreparedStatementFromPreLoadedQueries("BW_GET_CELL_VER_BEF_TID", connection );
				preparedstatement.setInt(1,table_id);
				preparedstatement.setInt(2,maxTid);
				preparedstatement.setInt(3,maxTid);
				preparedstatement.setInt(4,table_id);
				preparedstatement.setInt(5,maxTid);
				preparedstatement.setInt(6,table_id);
				preparedstatement.setInt(7,maxTid);
				preparedstatement.setInt(8,maxTid);
				preparedstatement.setInt(9,table_id);
				preparedstatement.setInt(10,maxTid);
				preparedstatement.setInt(11,table_id);
				preparedstatement.setInt(12,maxTid);
				preparedstatement.setInt(13,maxTid);
				preparedstatement.setInt(14,table_id);
				preparedstatement.setInt(15,maxTid);
			}
			else
			{
				preparedstatement = connection.prepareStatement(CALL_BW_GET_CELL_VER_BEF_TID);
				preparedstatement.setInt(1,table_id);
				preparedstatement.setInt(2,maxTid);
			}

			resultset = preparedstatement.executeQuery();


			while ( resultset.next() )
			{
				//System.out.println("queryResult row ");
				int a_cellId = resultset.getInt("CELL_ID");
				String a_cellType = resultset.getString("CELL_TYPE");
				String a_stringValue = resultset.getString("CELL_STRING_VALUE");
				int a_intValue = resultset.getInt("CELL_INTEGER_VALUE");
				float a_doubleValue = resultset.getFloat("CELL_DOUBLE_VALUE");
				int a_tableValue = resultset.getInt("CELL_TBL_VALUE");
				int a_trans_id = resultset.getInt("TX_ID");
				String tdescription = "";
				String tcomment = "";
				tdescription = resultset.getString("DESCRIPTION");
				tcomment = resultset.getString("COMMENT_");
				String created_by = resultset.getString("EMAIL_ADDRESS");
				String created_on = resultset.getString("CREATED_ON");
				int a_rowId = resultset.getInt("BW_ROW_ID");

				Transaction cellTransaction = new Transaction(a_trans_id, -1, created_by, created_on,tdescription, tcomment);

				int a_columnId = resultset.getInt("BW_COLUMN_ID");


				VersionedCell vc =   new  VersionedCell (a_cellId, a_columnId, " "  ,  a_rowId,
							a_cellType,  a_stringValue,  a_intValue,  a_doubleValue,  a_tableValue, "  ",
							cellTransaction, null );
				cells.put(new Integer(a_cellId), vc );

				if ( ! rowIds.containsKey( new Integer(a_rowId))   )
						 rowIds.put( new Integer(a_rowId),  new Integer(a_rowId) );

				if ( ! columnIds.containsKey( new Integer(a_columnId))  )
						 columnIds.put( new Integer(a_columnId), new Integer(a_columnId) );
			}
		}
		catch(SQLException sqlexception)
		{
			sqlexception.printStackTrace();
			throw new SystemException(sqlexception);
		}
		finally {
			try {
				if ( resultset != null ) {
					resultset.close();
				}
				if ( preparedstatement != null ) {
					preparedstatement.close();
				}
				if ( callablestatement != null ) {
					callablestatement.close();
				}
			}
			catch(SQLException sqlexception1)
			{

				sqlexception1.printStackTrace();
				throw new SystemException(sqlexception1);
			}
		}

		TableConfiguration tc = new TableConfiguration( cells , columnIds, rowIds, table_id, "LATEST", asOfDate, false );

		return tc;

	}


	public static TableConfiguration  getPreviousTableConfiguration( Connection connection,
															int table_id) throws SystemException
	{
		ResultSet resultset = null;
		PreparedStatement preparedstatement = null;
		Hashtable cells  = new Hashtable();
		Hashtable rowIds  = new Hashtable();
		Hashtable columnIds  = new Hashtable();



		try
		{
			if ( DatabaseLoader.getDatabaseType().trim().equalsIgnoreCase("ORACLE"))
			{
				preparedstatement = DatabaseLoader.getPreparedStatementFromPreLoadedQueries("BW_GET_PREVIOUS_TBL_CONFIG", connection );
				preparedstatement.setInt(1,table_id);
				preparedstatement.setInt(2,table_id);
				preparedstatement.setInt(3,table_id);
				preparedstatement.setInt(4,table_id);
				preparedstatement.setInt(5,table_id);
				preparedstatement.setInt(6,table_id);
			}
			else
			{
				preparedstatement = connection.prepareStatement(CALL_BW_GET_PREVIOUS_TBL_CONFIG);
				preparedstatement.setInt(1,table_id);
			}

			resultset = preparedstatement.executeQuery();


			while ( resultset.next() )
			{
				int a_cellId = resultset.getInt("CELL_ID");
				String a_cellType = resultset.getString("CELL_TYPE");
				String a_stringValue = resultset.getString("CELL_STRING_VALUE");
				int a_intValue = resultset.getInt("CELL_INTEGER_VALUE");
				float a_doubleValue = resultset.getFloat("CELL_DOUBLE_VALUE");
				int a_tableValue = resultset.getInt("CELL_TBL_VALUE");
				int a_trans_id = resultset.getInt("TX_ID");
				String tdescription = "";
				String tcomment = "";
				tdescription = resultset.getString("DESCRIPTION");
				tcomment = resultset.getString("COMMENT_");
				String created_by = resultset.getString("EMAIL_ADDRESS");
				String created_on = resultset.getString("CREATED_ON");

				Transaction cellTransaction = new Transaction(a_trans_id, -1, created_by, created_on, tdescription, tcomment);


				int a_rowId = resultset.getInt("BW_ROW_ID");
				int a_columnId = resultset.getInt("BW_COLUMN_ID");


				VersionedCell vc =   new  VersionedCell (a_cellId, a_columnId, " "  ,  a_rowId, a_cellType,
					a_stringValue,  a_intValue,  a_doubleValue,  a_tableValue, "  ",
					cellTransaction , null);
				cells.put(new Integer(a_cellId), vc );

				if ( ! rowIds.containsKey( new Integer(a_rowId))   )
						 rowIds.put( new Integer(a_rowId),  new Integer(a_rowId) );

				if ( ! columnIds.containsKey( new Integer(a_columnId))  )
						 columnIds.put( new Integer(a_columnId), new Integer(a_columnId) );
			}



		}
		catch(SQLException sqlexception)
		{
			sqlexception.printStackTrace();
			throw new SystemException(sqlexception);
		}
		finally {
			try {
				if ( resultset != null ) {
					resultset.close();
				}
				if ( preparedstatement != null ) {
					preparedstatement.close();
				}
			}
			catch(SQLException sqlexception1)
			{

				sqlexception1.printStackTrace();
				throw new SystemException(sqlexception1);
			}
		}

		TableConfiguration tc = new TableConfiguration( cells , columnIds, rowIds, table_id, "LATEST", null, true );

		return tc;

	}



	public static Hashtable  getColumnConfiguration( Connection connection, int column_id,String asOfDate ) throws SystemException
	{
		ResultSet resultset = null;
		PreparedStatement preparedstatement = null;
		Hashtable cells  = new Hashtable();

		try
		{
			if ( DatabaseLoader.getDatabaseType().trim().equalsIgnoreCase("ORACLE"))
			{
				java.sql.Date date_ = new java.sql.Date(java.sql.Date.parse(asOfDate));
				preparedstatement = DatabaseLoader.getPreparedStatementFromPreLoadedQueries("BW_GET_CL_VERS_FOR_C_FOR_DATE", connection );
				preparedstatement.setInt(1,column_id);
				preparedstatement.setDate(2,date_);
				preparedstatement.setInt(3,column_id);

			}
			else
			{
				preparedstatement = connection.prepareStatement(CALL_BW_GET_CL_VERS_FOR_C_FOR_DATE);
				preparedstatement.setInt(1,column_id);
				preparedstatement.setString(2,asOfDate);
			}

			resultset = preparedstatement.executeQuery();

			while ( resultset.next() )
			{

				//System.out.println("queryResult row ");


				int a_cellId = resultset.getInt("CELL_ID");
				String a_cellType = resultset.getString("CELL_TYPE");
				String a_stringValue = resultset.getString("CELL_STRING_VALUE");
				int a_intValue = resultset.getInt("CELL_INTEGER_VALUE");
				float a_doubleValue = resultset.getFloat("CELL_DOUBLE_VALUE");
				int a_tableValue = resultset.getInt("CELL_TBL_VALUE");
				int a_trans_id = resultset.getInt("TX_ID");
				String tdescription = "";
				String tcomment = "";

				tdescription = resultset.getString("DESCRIPTION");
				tcomment = resultset.getString("COMMENT_");
				String created_by = resultset.getString("EMAIL_ADDRESS");
				String created_on = resultset.getString("CREATED_ON");
				Transaction cellTransaction = new Transaction(a_trans_id, -1, created_by, created_on,tdescription, tcomment);

				int a_rowId = resultset.getInt("BW_ROW_ID");
				int a_columnId = resultset.getInt("BW_COLUMN_ID");


				VersionedCell vc =   new  VersionedCell (a_cellId, a_columnId, " "  ,  a_rowId, a_cellType,
				a_stringValue,  a_intValue,  a_doubleValue,  a_tableValue, "  ",
				cellTransaction , null );
				cells.put(new Integer(a_cellId), vc );
			}

		}
		catch(SQLException sqlexception)
		{
			sqlexception.printStackTrace();
			throw new SystemException(sqlexception);
		}
		finally {
			try {
				if ( resultset != null ) {
					resultset.close();
				}
				if ( preparedstatement != null ) {
					preparedstatement.close();
				}
			}
			catch(SQLException sqlexception1)
			{

				sqlexception1.printStackTrace();
				throw new SystemException(sqlexception1);
			}
		}

		return cells;
	}


	public static Hashtable  getPreviousColumnConfiguration( Connection connection, int column_id ) throws SystemException
	{


		ResultSet resultset = null;
		PreparedStatement preparedstatement = null;
		Hashtable cells  = new Hashtable();
		System.out.println("QueryManager:getPreviousColumnConfiguration");


		try
		{
			if ( DatabaseLoader.getDatabaseType().trim().equalsIgnoreCase("ORACLE"))
			{
				preparedstatement = DatabaseLoader.getPreparedStatementFromPreLoadedQueries("BW_GET_PREV_CL_VERS_FOR_C", connection );
				preparedstatement.setInt(1,column_id);
				preparedstatement.setInt(2,column_id);

			}
			else
			{
				preparedstatement = connection.prepareStatement(CALL_BW_GET_PREV_CL_VERS_FOR_C);
				preparedstatement.setInt(1,column_id);
			}
			resultset = preparedstatement.executeQuery();


			while ( resultset.next() )
			{

				//System.out.println("queryResult row ");
				int a_cellId = resultset.getInt("CELL_ID");
				String a_cellType = resultset.getString("CELL_TYPE");
				String a_stringValue = resultset.getString("CELL_STRING_VALUE");
				int a_intValue = resultset.getInt("CELL_INTEGER_VALUE");
				float a_doubleValue = resultset.getFloat("CELL_DOUBLE_VALUE");
				int a_tableValue = resultset.getInt("CELL_TBL_VALUE");
				int a_trans_id = resultset.getInt("TX_ID");
				String tdescription = "";
				String tcomment = "";
				tdescription = resultset.getString("DESCRIPTION");
				tcomment = resultset.getString("COMMENT_");
				String created_by = resultset.getString("EMAIL_ADDRESS");
				String created_on = resultset.getString("CREATED_ON");
				Transaction cellTransaction = new Transaction(a_trans_id, -1, created_by, created_on, tdescription, tcomment);

				int a_rowId = resultset.getInt("BW_ROW_ID");
				int a_columnId = resultset.getInt("BW_COLUMN_ID");


				VersionedCell vc =   new  VersionedCell (a_cellId, a_columnId, " "  ,  a_rowId, a_cellType,
				a_stringValue,  a_intValue,  a_doubleValue,  a_tableValue, "  ",
				cellTransaction , null );
				cells.put(new Integer(a_cellId), vc );
			}

		}
		catch(SQLException sqlexception)
		{
			sqlexception.printStackTrace();
			throw new SystemException(sqlexception);
		}
		finally {
			try {
				if ( resultset != null ) {
					resultset.close();
				}
				if ( preparedstatement != null ) {
					preparedstatement.close();
				}
			}
			catch(SQLException sqlexception1)
			{

				sqlexception1.printStackTrace();
				throw new SystemException(sqlexception1);
			}
		}

		return cells;

	}



	public static Hashtable  getOrderedColumnConfiguration( Connection connection, int column_id,String asOfDate ) throws SystemException
	{

		// ONLY WORKS FOR A STRING COLUMN TBD
		ResultSet resultset = null;
		PreparedStatement preparedstatement = null;
		Hashtable cells  = new Hashtable();

		try
		{
			if ( DatabaseLoader.getDatabaseType().trim().equalsIgnoreCase("ORACLE"))
			{
				java.sql.Date date_ = new java.sql.Date(java.sql.Date.parse(asOfDate));
				preparedstatement = DatabaseLoader.getPreparedStatementFromPreLoadedQueries("BW_GET_CL_VERS_FOR_C_FOR_DATE", connection );
				preparedstatement.setInt(1,column_id);
				preparedstatement.setDate(2,date_);
				preparedstatement.setInt(3,column_id);

			}
			else
			{
				preparedstatement = connection.prepareStatement(CALL_BW_GET_CL_VERS_FOR_C_FOR_DATE);
				preparedstatement.setInt(1,column_id);
				preparedstatement.setString(2,asOfDate);
			}
			resultset = preparedstatement.executeQuery();
			int seqId = 0;
			Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
			while ( resultset.next() )
			{
				//System.out.println("queryResult row ");
				int a_cellId = resultset.getInt("CELL_ID");
				String a_cellType = resultset.getString("CELL_TYPE");
				String a_stringValue = resultset.getString("CELL_STRING_VALUE");
				int a_intValue = resultset.getInt("CELL_INTEGER_VALUE");
				float a_doubleValue = resultset.getFloat("CELL_DOUBLE_VALUE");
				int a_tableValue = resultset.getInt("CELL_TBL_VALUE");
				int a_trans_id = resultset.getInt("TX_ID");
				String tdescription = "";
				String tcomment = "";

				tdescription = resultset.getString("DESCRIPTION");
				tcomment = resultset.getString("COMMENT_");
				String created_by = resultset.getString("EMAIL_ADDRESS");
				java.sql.Timestamp created_on = resultset.getTimestamp("CREATED_ON",cal);
				Transaction cellTransaction = new Transaction(a_trans_id, -1, created_by, created_on.getTime(), tdescription, tcomment);

				int a_rowId = resultset.getInt("BW_ROW_ID");
				int a_columnId = resultset.getInt("BW_COLUMN_ID");

				VersionedCell vc =   new  VersionedCell (a_cellId, a_columnId, " "  ,  a_rowId, a_cellType,
					a_stringValue,  a_intValue,  a_doubleValue,  a_tableValue, "  ",
					cellTransaction , null );
				System.out.println("getOrderedColumnConfiguration: adding string value " + a_stringValue);
				cells.put(a_stringValue, new Integer( seqId) );
				seqId++;
			}

		}
		catch(SQLException sqlexception)
		{
			sqlexception.printStackTrace();
			throw new SystemException(sqlexception);
		}
		finally {
			try {
				if ( resultset != null ) {
					resultset.close();
				}
				if ( preparedstatement != null ) {
					preparedstatement.close();
				}
			}
			catch(SQLException sqlexception1)
			{

				sqlexception1.printStackTrace();
				throw new SystemException(sqlexception1);
			}
		}

		System.out.println("getOrderedColumnConfiguration :  cells = " + cells);
		return cells;
	}

public static Hashtable  getCellVersionsAtCellExport( Connection connection, int tid, int table_id, int userId) throws SystemException,SQLException
	{
		ResultSet resultset = null;
		PreparedStatement preparedstatement = null;
		Hashtable ht = new Hashtable();

		try
		{
			if ( DatabaseLoader.getDatabaseType().trim().equalsIgnoreCase("ORACLE"))
			{
				preparedstatement = DatabaseLoader.getPreparedStatementFromPreLoadedQueries("BW_GET_CL_AT_LAST_EXP_FOR_USR", connection );
				preparedstatement.setInt(1,table_id);
				preparedstatement.setInt(2,tid);
				preparedstatement.setInt(3,userId);
				preparedstatement.setInt(4,table_id);
				preparedstatement.setInt(5,table_id);
				preparedstatement.setInt(6,tid);
				preparedstatement.setInt(7,userId);
				preparedstatement.setInt(8,table_id);
				preparedstatement.setInt(9,table_id);
				preparedstatement.setInt(10,tid);
				preparedstatement.setInt(11,userId);
				preparedstatement.setInt(12,table_id);


			}
			else
			{
				preparedstatement = connection.prepareStatement(CALL_BW_GET_CL_AT_LAST_EXP_FOR_USR);
				preparedstatement.setInt(1,table_id);
				preparedstatement.setInt(2,tid);
				preparedstatement.setInt(3,userId);
			}
			resultset = preparedstatement.executeQuery();


			while ( resultset.next() )
			{

				// System.out.println("queryResult row ");


				int a_cellId = resultset.getInt("CELL_ID");

				String a_cellType = resultset.getString("CELL_TYPE");

				String a_stringValue = resultset.getString("CELL_STRING_VALUE");

				int a_intValue = resultset.getInt("CELL_INTEGER_VALUE");

				float a_doubleValue = resultset.getFloat("CELL_DOUBLE_VALUE");

				int a_tableValue = resultset.getInt("CELL_TBL_VALUE");

				int a_trans_id = resultset.getInt("TX_ID");

				OriginalCell cell = new OriginalCell(a_cellId, a_cellType, a_stringValue, a_intValue, a_doubleValue, a_tableValue, a_trans_id);
				// System.out.println("Adding orginial Cell");
				// cell.printCell();
				ht.put(new Integer(a_cellId), cell);
			}

		}
		catch(SQLException sqlexception)
		{

			throw sqlexception;
		}
		finally {
			try {
				if ( resultset != null ) {
					resultset.close();
				}
				if ( preparedstatement != null ) {
					preparedstatement.close();
				}
			}
			catch(SQLException sqlexception1) {

				sqlexception1.printStackTrace();
				throw new SystemException(sqlexception1);
			}
		}

		return ht;

	}


	public static Hashtable  getCellVersionsAtCellExport_sarang( Connection connection, int tid, int table_id, int userId) throws SystemException,SQLException
	{
		ResultSet resultset = null;
		PreparedStatement preparedstatement = null;
		Hashtable ht = new Hashtable();

		try
		{
			if ( DatabaseLoader.getDatabaseType().trim().equalsIgnoreCase("ORACLE"))
			{
				preparedstatement = DatabaseLoader.getPreparedStatementFromPreLoadedQueries("BW_GET_CL_AT_LAST_EXP_FOR_USR", connection );
				preparedstatement.setInt(1,table_id);
				preparedstatement.setInt(2,tid);
				preparedstatement.setInt(3,userId);
				preparedstatement.setInt(4,table_id);
				preparedstatement.setInt(5,table_id);
				preparedstatement.setInt(6,tid);
				preparedstatement.setInt(7,userId);
				preparedstatement.setInt(8,table_id);
				preparedstatement.setInt(9,table_id);
				preparedstatement.setInt(10,tid);
				preparedstatement.setInt(11,userId);
				preparedstatement.setInt(12,table_id);


			}
			else
			{
				preparedstatement = connection.prepareStatement(CALL_BW_GET_CL_AT_LAST_EXP_FOR_USR);
				preparedstatement.setInt(1,table_id);
				preparedstatement.setInt(2,tid);
				preparedstatement.setInt(3,userId);
			}
			resultset = preparedstatement.executeQuery();


			while ( resultset.next() )
			{

				// System.out.println("queryResult row ");


				int a_cellId = resultset.getInt("CELL_ID");

				String a_cellType = resultset.getString("CELL_TYPE");

				String a_stringValue = resultset.getString("CELL_STRING_VALUE");

				int a_intValue = resultset.getInt("CELL_INTEGER_VALUE");

				float a_doubleValue = resultset.getFloat("CELL_DOUBLE_VALUE");

				int a_tableValue = -1;

				int a_trans_id = resultset.getInt("TX_ID");

				OriginalCell cell = new OriginalCell(a_cellId, a_cellType, a_stringValue, a_intValue, a_doubleValue, a_tableValue, a_trans_id);
				// System.out.println("Adding orginial Cell");
				// cell.printCell();
				ht.put(new Integer(a_cellId), cell);
			}

		}
		catch(SQLException sqlexception)
		{

			throw sqlexception;
		}
		finally {
			try {
				if ( resultset != null ) {
					resultset.close();
				}
				if ( preparedstatement != null ) {
					preparedstatement.close();
				}
			}
			catch(SQLException sqlexception1) {

				sqlexception1.printStackTrace();
				throw new SystemException(sqlexception1);
			}
		}

		return ht;
	}

};


