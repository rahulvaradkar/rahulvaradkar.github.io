package com.boardwalk.table;

import java.util.*;
import java.io.*;

import com.boardwalk.whiteboard.*;
import com.boardwalk.collaboration.*;
import com.boardwalk.neighborhood.*;

import com.boardwalk.database.DatabaseLoader;
import com.boardwalk.exception.*;
import com.boardwalk.database.*;
import com.boardwalk.user.*;
import com.boardwalk.excel.*;
import boardwalk.common.*;
import com.boardwalk.query.*;

import java.sql.*;                  // JDBC package
import javax.sql.*;                 // extended JDBC package



public class TableManager{

    // BW_GET_TBLS_BY_WB( WB_ID, USER_ID, TABLE_STATUS, TABLE_ACCESS );

    private static String CALL_BW_CR_TBL="{CALL BW_CR_TBL(?,?,?,?,?,?,?,?,?,?,?)}";
    private static String CALL_BW_CP_TBL_FROM_LATEST="{CALL BW_CP_TBL_FROM_LATEST(?,?,?,?,?,?,?,?,?,?,?,?,?)}";
	private static String CALL_BW_CP_TBL_FROM_BL="{CALL BW_CP_TBL_FROM_BL(?,?,?,?,?,?,?,?,?,?,?,?,?)}";

	private static String CALL_BW_LOCK_TBL="{CALL BW_LOCK_TBL(?,?)}";
    private static String CALL_BW_IS_TBL_LOCKED="{CALL BW_IS_TBL_LOCKED(?)}";
    private static String CALL_BW_UNLOCK_TBL="{CALL BW_UNLOCK_TBL(?,?)}";



    private static String CALL_BW_GET_TBLS_BY_WB="{CALL BW_GET_TBLS_BY_WB(?,?)}";
	private static String CALL_BW_GET_TBLS_BY_WB_AND_BL="{CALL BW_GET_TBLS_BY_WB_AND_BL(?,?,?)}";
	private static String CALL_BW_GET_TBL_INFO="{CALL BW_GET_TBLINFO(?)}";
	private static String CALL_BW_UPD_TBL_PROPERTIES="{CALL BW_UPD_TBL_PROPERTIES(?,?,?)}";

	private static String CALL_BW_GET_NH_RELS_FOR_TBL="{CALL BW_GET_NH_RELS_FOR_TBL(?)}";

    private static String CALL_BW_CR_ROW_WITH_SEQID="{CALL BW_CR_ROW_WITH_SEQID(?,?,?,?,?)}";//OLD
	private static String CALL_BW_CR_ROW = "{CALL BW_CR_ROW(?,?,?,?,?,?)}";
	private static String CALL_BW_RESEQUENCE_ROWS = "{CALL BW_RESEQUENCE_ROWS(?)}";
	private static String CALL_BW_CR_ROW_BEFORE="{CALL BW_CR_ROW_BEFORE(?,?,?,?,?,?)}";

	private static String CALL_BW_UPD_ROW_OWNERSHIP="{CALL BW_UPD_ROW_OWNERSHIP(?,?,?,?)}";
 	private static String CALL_BW_GET_AFTER_RID_FOR_MY_ROWS="{CALL BW_GET_AFTER_RID_FOR_MY_ROWS(?,?,?)}";
	private static String CALL_BW_GET_AFTER_RID_FOR_MY_GROUP_ROWS="{CALL BW_GET_AFTER_RID_FOR_MY_GROUP_ROWS(?,?,?)}";
	private static String CALL_BW_GET_AF_RID_FOR_GROUP_A_IMCHD="{CALL BW_GET_AF_RID_FOR_GROUP_A_IMCHD(?,?,?)}";
	private static String CALL_BW_GET_AF_RID_FOR_GROUP_A_CHD="{CALL BW_GET_AF_RID_FOR_GROUP_A_CHD(?,?,?)}";

	private static String CALL_BW_RESEQUENCE_COLUMNS = "{CALL BW_RESEQUENCE_COLUMNS(?)}";
	private static String CALL_BW_CR_COLUMN_BEFORE = "{CALL BW_CR_COLUMN_BEFORE(?,?,?,?,?,?,?,?,?,?,?,?)}";
    private static String CALL_BW_CR_STRING_COLUMN="{CALL BW_CR_STRING_COLUMN(?,?,?,?,?,?,?,?,?,?,?,?,?)}";
 	private static String CALL_BW_CR_INTEGER_COLUMN="{CALL BW_CR_INTEGER_COLUMN(?,?,?,?,?,?,?,?,?,?,?,?)}";
    private static String CALL_BW_CR_FLOAT_COLUMN="{CALL BW_CR_FLOAT_COLUMN(?,?,?,?,?,?,?,?,?,?,?,?)}";


   	private static String CALL_BW_UPD_STRING_CELL_BY_R_AND_C="{CALL BW_UPD_STRING_CELL_BY_R_AND_C(?,?,?,?,?)}";
    private static String CALL_BW_UPD_DOUBLE_CELL_BY_R_AND_C="{CALL BW_UPD_DOUBLE_CELL_BY_R_AND_C(?,?,?,?,?)}";
    private static String CALL_BW_UPD_INTEGER_CELL_BY_R_AND_C="{CALL BW_UPD_INTEGER_CELL_BY_R_AND_C(?,?,?,?,?)}";
    private static String CALL_BW_UPD_TBL_CELL_BY_R_AND_C="{CALL BW_UPD_TBL_CELL_BY_R_AND_C(?,?,?,?,?)}";


 	private static String CALL_BW_UPD_STR_CL_DV_BY_R_AND_C="{CALL BW_UPD_STR_CL_DV_BY_R_AND_C(?,?,?,?)}";
    private static String CALL_BW_UPD_DBL_CL_DV_BY_R_AND_C="{CALL BW_UPD_DBL_CL_DV_BY_R_AND_C(?,?,?,?,?)}";
    private static String CALL_BW_UPD_INT_CELLDV_BY_R_AND_C="{CALL BW_UPD_INT_CELLDV_BY_R_AND_C(?,?,?,?,?)}";
    private static String CALL_BW_UPD_TBL_CELL_DV_BY_R_AND_C="{CALL BW_UPD_TBL_CELL_DV_BY_R_AND_C(?,?,?,?,?)}";



    private static String CALL_BW_UPD_STRING_CELL="{CALL BW_UPD_STRING_CELL(?,?,?,?)}";
	private static String CALL_BW_UPD_DOUBLE_CELL="{CALL BW_UPD_DOUBLE_CELL(?,?,?)}";
	private static String CALL_BW_UPD_INTEGER_CELL="{CALL BW_UPD_INTEGER_CELL(?,?,?)}";
	private static String CALL_BW_UPD_TBL_CELL="{CALL BW_UPD_TBL_CELL(?,?,?)}";

	private static String CALL_BW_UPD_DESIGN_STRING_CELL="{CALL BW_UPD_STRING_CELL_DV(?,?,?)}";
	private static String CALL_BW_UPD_DESIGN_DOUBLE_CELL="{CALL BW_UPD_DOUBLE_CELL_DV(?,?,?)}";
	private static String CALL_BW_UPD_DESIGN_INTEGER_CELL="{CALL BW_UPD_INTEGER_CELL_DV(?,?,?)}";
	private static String CALL_BW_UPD_DESIGN_TBL_CELL="{CALL BW_UPD_TBL_CELL_DV(?,?,?)}";

	private static String CALL_BW_CHECK_ENUMERATED_VALUE="{CALL BW_CHECK_ENUMERATED_VALUE(?,?,?)}";


	public static final String CALL_BW_CR_TBL_ACCESS = "{CALL BW_CR_TBL_ACCESS(?,?,?,?,?,?)}";
	public static final String CALL_BW_UPD_TBL_ACCESS = "{CALL BW_UPD_TBL_ACCESS(?,?,?,?,?,?,?)}";
	public static final String CALL_BW_DEL_TBL_ACCESS = "{CALL BW_DEL_TBL_ACCESS(?,?,?,?,?,?)}";
	private static String CALL_BW_GET_TBL_ACCESS_FOR_MEMBER="{CALL BW_GET_TBL_ACCESS_FOR_MEMBER(?,?)}";
	private static String CALL_BW_GET_TBL_ACCESS="{CALL  BW_GET_TBL_ACCESS(?)}";
	private static String CALL_BW_CR_STRING_VALUE="{CALL  BW_CR_STRING_VALUE(?,?,?,?)}";

	public static final String CALL_BW_CR_TBL_ACTION_UI_VALUES = "{CALL BW_CR_TBL_ACTION_UI_VALUES(?,?,?,?)}";
	public static final String CALL_BW_UPD_TBL_ACTION_UI_VALUES = "{CALL BW_UPD_TBL_ACTION_UI_VALUES(?,?,?,?)}";
	public static final String CALL_BW_GET_TBL_ACTION_UI_VALUES = "{CALL BW_GET_TBL_ACTION_UI_VALUES(?)}";


	private static String CALL_BW_GET_ROWS_AND_CELLS_BY_TBL_ID="{CALL BW_GET_ROWS_AND_CELLS_BY_TBL_ID(?)}";
	//TBR BW_GET_CELLS_FOR_TBL
	private static String CALL_BW_GET_DES_CELL_VALS_BY_TBL="{CALL BW_GET_DES_CELL_VALS_BY_TBL (?,?,?)}";
	//TBR BW_GET_DCELLS_FOR_TBL

/* &&&&&&&&&&&& */

	private static String CALL_BW_GET_ROW_COLUMN_CELLS="{CALL BW_GET_ROW_COLUMN_CELLS(?,?,?,?,?,?,?)}";
	private static String CALL_BW_GET_TBL_DESIGN="{CALL BW_GET_TBL_DESIGN(?,?,?,?,?,?,?)}";
	private static String CALL_BW_GET_TBL_FOR_NH="{CALL BW_GET_TBL_FOR_NH(?,?,?,?,?,?,?,?,?,?)}";
	private static String CALL_BW_GET_TBL_FOR_NH_I_CHD="{CALL BW_GET_TBL_FOR_NH_I_CHD(?,?,?,?,?,?,?,?,?,?)}";
	private static String CALL_BW_GET_TBL_FOR_NH_A_CHD="{CALL BW_GET_TBL_FOR_NH_A_CHD(?,?,?,?,?,?,?,?,?,?)}";
	private static String CALL_BW_GET_TBL_FOR_USER="{CALL BW_GET_TBL_FOR_USER(?,?,?,?,?,?,?,?,?)}";

	private static String CALL_BW_GET_USER_TBL="{CALL BW_GET_USER_TBL(?,?,?,?)}";
	private static String CALL_BW_GET_USER_TBL_ALL_USERS="{CALL BW_GET_USER_TBL_ALL_USERS(?,?,?)}";
	private static String CALL_BW_GET_USER_TBL_FOR_NH="{CALL BW_GET_USER_TBL_FOR_NH(?,?,?,?)}";
	private static String CALL_BW_GET_USER_TBL_FOR_NH_CHD="{CALL BW_GET_USER_TBL_FOR_NH_CHD(?,?,?,?)}";

	private static String CALL_BW_GET_TBL="{CALL BW_GET_TBL(?,?,?,?,?,?,?,?,?)}";
	private static String CALL_BW_GET_TBL_BL="{CALL BW_GET_TBL_BL(?,?,?,?,?,?,?,?)}";
	private static String CALL_BW_GET_TBL_BL_FOR_NH="{CALL BW_GET_TBL_BL_FOR_NH(?,?,?,?,?,?,?,?,?)}";
	private static String CALL_BW_GET_TBL_BL_FOR_NH_I_CHD="{CALL BW_GET_TBL_BL_FOR_NH_I_CHD(?,?,?,?,?,?,?,?,?)}";
	private static String CALL_BW_GET_TBL_BL_FOR_NH_A_CHD="{CALL BW_GET_TBL_BL_FOR_NH_A_CHD(?,?,?,?,?,?,?,?,?)}";
	private static String CALL_BW_GET_TBL_BL_FOR_USER="{CALL BW_GET_TBL_BL_FOR_USER(?,?,?,?,?,?,?,?)}";
	private static String CALL_BW_GET_TBL_L="{CALL BW_GET_TBL_L(?,?,?,?,?)}";

/* &&&&&&&&&&&& */

	private static String CALL_BW_GET_ROWCELLS_BY_TBL_AND_U="{CALL BW_GET_ROWCELLS_BY_TBL_AND_U(?,?)}";
	private static String CALL_BW_GET_ROWCELLS_FOR_USER= "{CALL BW_GET_ROWCELLS_FOR_USER(?,?)}";
	// BW_GET_TBL_USER
	private static String CALL_BW_GET_ROWCELLS_BY_USER= "{CALL BW_GET_ROWCELLS_BY_USER(?)}";
	private static String CALL_BW_GET_TBL_CONTENTS_BY_BL="{CALL BW_GET_TBL_CONTENTS_BY_BL(?,?)}";


private static String CALL_BW_GET_ROWCELLS_BY_NHU_AND_CHD= "{CALL BW_GET_ROWCELLS_BY_NHU_AND_CHD(?,?)}";
//BW_GET_TBL_FOR_NH_A_CHD
private static String CALL_BW_GET_RCELLS_BY_USRS_OF_NH= "{CALL BW_GET_RCELLS_BY_USRS_OF_NH(?,?)}";
//BW_GET_TBL_FOR_NH



private static String CALL_BW_GET_TBLS_USING_LKP= "{CALL BW_GET_TBLS_USING_LKP(?)}";
private static String CALL_BW_GET_TBLS_USING_LKP_CL= "{CALL BW_GET_TBLS_USING_LKP_CL(?,?)}";
private static String CALL_BW_LOCK_TBL_FOR_UPD = "{CALL BW_LOCK_TBL_FOR_UPD(?)}";
private static String CALL_BW_CR_NEW_ROWS_FOR_TBL = "{CALL BW_CR_NEW_ROWS_FOR_TBL(?)}";
private static String CALL_BW_CR_NEW_CELLS = "{CALL BW_CR_NEW_CELLS(?,?)}";
private static String CALL_BW_DEACTIVATE_ROWS = "{CALL BW_DEACTIVATE_ROWS(?,?,?,?,?,?)}";
private static String CALL_BW_EXPORT = "{CALL BW_EXPORT(?,?,?,?)}";

private static String CALL_BW_GET_RC_CELLS_AT_EXPORT = "{CALL BW_GET_RC_CELLS_AT_EXPORT(?,?,?)}";

	public static long prevTime = -1;



    public TableManager() {}


	public static void updateUserExportTid(Connection connection, int tableId, int userId, int tid)
		throws SQLException
	{
		String query = "{CALL BW_UPD_USER_EXPORT_TID(?,?,?)}";
		CallableStatement cs = connection.prepareCall(query);
		cs.setInt(1, tableId);
		cs.setInt(2, userId);
		cs.setInt(3, tid);
		cs.executeUpdate();

		cs.close();
		cs = null;
	}

    public static Hashtable getCellVersions(
											Connection connection,
											int cellId ,
											int rowId,
											int colId,
											long sdate,
											long edate) throws SQLException
    {
		ResultSet resultset = null;
		PreparedStatement preparedstatement = null;
		Hashtable ht = new Hashtable();

		try
		{
			String CALL_BW_GET_CELL_VERSIONS="{CALL BW_GET_CELL_VERSIONS(?,?,?,?,?)}";
			preparedstatement = connection.prepareStatement(CALL_BW_GET_CELL_VERSIONS);
			preparedstatement.setInt(1,cellId);
			preparedstatement.setInt(2,rowId);
			preparedstatement.setInt(3,colId);

			Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
			preparedstatement.setTimestamp(4, new java.sql.Timestamp(sdate), cal);
			preparedstatement.setTimestamp(5, new java.sql.Timestamp(edate), cal);
			resultset = preparedstatement.executeQuery();

			while ( resultset.next() )
			{
				int a_cellId = resultset.getInt("CELL_ID");
				String a_cellType = resultset.getString("CELL_TYPE");
				String a_stringValue = resultset.getString("CELL_STRING_VALUE");
				int a_intValue = resultset.getInt("CELL_INTEGER_VALUE");
				float a_doubleValue = resultset.getFloat("CELL_DOUBLE_VALUE");
				int a_tableValue = resultset.getInt("CELL_TBL_VALUE");
				int a_trans_id = resultset.getInt("TRANSACTION_ID");
				java.sql.Timestamp a_createdOnDate = resultset.getTimestamp("CREATED_ON",cal);
			//	java.sql.Date creationDate = new java.sql.Date( a_createdOnDate);
				int a_createdByUserid = resultset.getInt("CREATED_BY");
				String tdescription = resultset.getString("DESCRIPTION");
				String tcomment = resultset.getString("COMMENT_");
				String a_createdByUser = resultset.getString("EMAIL_ADDRESS");

				Transaction cellTransaction = new Transaction(a_trans_id, a_createdByUserid,
													a_createdByUser,a_createdOnDate.getTime(),tdescription, tcomment);

				int a_rowid = resultset.getInt("ROW_ID");
				int a_columnid = resultset.getInt("COLUMN_ID");
				String a_columnName = resultset.getString("COLUMN_NAME");
				String a_tableName  = resultset.getString("TABLE_NAME");
				String cellFormula = resultset.getString("FORMULA");

				VersionedCell cell = new VersionedCell(a_cellId, a_columnid, a_columnName, a_rowid,
						a_cellType, a_stringValue, a_intValue, a_doubleValue, a_tableValue, a_tableName,
						cellTransaction, cellFormula  );

				ht.put(new Integer(a_trans_id), cell);

			}
		}
		catch(SQLException sqlexception)
		{
			sqlexception.printStackTrace();
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
				throw sqlexception1;
			}
        }

		return ht;
	}


	    public static ArrayList getRowVersions(
											Connection connection,
											String rowIds ,
											long sdate,
											long edate,
											String columnIds,
											String baseLine,
											int aitableId,
											int aiuserId,
											int aimemberid) throws SQLException
    {
		ResultSet resultset = null;
		PreparedStatement preparedstatement = null;
		ArrayList lvModRowsCells = new ArrayList();
		int RowId = 0;
		System.out.println("rowIds	:"+rowIds);
		System.out.println("sdate	:"+sdate);
		System.out.println("edate	:"+edate);
		System.out.println("columnIds	:"+columnIds);
		System.out.println("aitableId	:"+aitableId);
		System.out.println("aiuserId	:"+aiuserId);
		System.out.println("aimemberid	:"+aimemberid);
		try
		{
			System.out.println("##### baseLine "+baseLine);
			Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
			if(baseLine.equals("-1"))
			{
				String CALL_BW_GET_CELL_VERSIONS="{CALL BW_GET_ROW_VERSIONS(?,?,?,?,?,?,?)}";
				//String CALL_BW_GET_CELL_VERSIONS="{CALL BW_GET_ROW_VERSIONS1(?,?,?,?)}";
				preparedstatement = connection.prepareStatement(CALL_BW_GET_CELL_VERSIONS);
				preparedstatement.setString(1,rowIds);
				preparedstatement.setTimestamp(2, new java.sql.Timestamp(sdate), cal);
				preparedstatement.setTimestamp(3, new java.sql.Timestamp(edate), cal);
				preparedstatement.setString(4,columnIds);
				preparedstatement.setInt(5,aitableId);
				preparedstatement.setInt(6,aiuserId);
				preparedstatement.setInt(7,aimemberid);
			}
			else
			{
				String CALL_BW_GET_CELL_VERSIONS="{CALL BW_GET_BASELINE_VERSIONS(?,?,?,?,?,?)}";

				preparedstatement = connection.prepareStatement(CALL_BW_GET_CELL_VERSIONS);
				if(baseLine.equals("ALL"))
					preparedstatement.setString(1,"");
				else
					preparedstatement.setString(1,baseLine);
				preparedstatement.setString(2,rowIds);
				preparedstatement.setString(3,columnIds);
				preparedstatement.setInt(4,aitableId);
				preparedstatement.setInt(5,aiuserId);
				preparedstatement.setInt(6,aimemberid);
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
				int a_trans_id = resultset.getInt("TRANSACTION_ID");
				java.sql.Timestamp a_createdOnDate = resultset.getTimestamp("CREATED_ON",cal);
			//	java.sql.Date creationDate = new java.sql.Date( a_createdOnDate);
				int a_createdByUserid = resultset.getInt("CREATED_BY");
				String tdescription = resultset.getString("DESCRIPTION");
				String bldescription = resultset.getString("BLDESCRIPTION");

				String tcomment = resultset.getString("COMMENT_");
				String a_createdByUser = resultset.getString("EMAIL_ADDRESS");

				Transaction cellTransaction = new Transaction(a_trans_id, a_createdByUserid,
													a_createdByUser,a_createdOnDate.getTime(),tdescription, tcomment);

				int a_rowid = resultset.getInt("ROW_ID");
				int a_columnid = resultset.getInt("COLUMN_ID");
				String a_columnName = resultset.getString("COLUMN_NAME");
				String a_tableName  = resultset.getString("TABLE_NAME");
				String cellFormula = resultset.getString("FORMULA");

				VersionedCell cell = new VersionedCell(a_cellId, a_columnid, a_columnName, a_rowid,
						a_cellType, a_stringValue, a_intValue, a_doubleValue, a_tableValue, a_tableName,
						cellTransaction, cellFormula  );
				cell.setbaselineDesc(bldescription);
				lvModRowsCells.add(cell);
			}
		}
		catch(SQLException sqlexception)
		{
			sqlexception.printStackTrace();
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
				throw sqlexception1;
			}
        }

		return lvModRowsCells;
	}


	public static Hashtable getTransactionList( Connection a_connection,
											  	int tableId,
	                                            int stid,
	                                            int etid,
	                                            long sdate,
	                                            long edate,
	                                            int userId,
	                                            int nhId,
	                                            String viewPref,
	                                            boolean baseline)
	    throws SQLException
	{

		PreparedStatement preparedstatement = null;
		ResultSet resultset = null;
		Hashtable tlist = new Hashtable();
		try
		{
			String CALL_BW_GET_TBL_TXLIST  = null;

			System.out.println("Calling BW_GET_TBL_TXLIST Tablemanager Normal");

			getElapsedTime();

			int criteriaTableId = TableViewManager.getCriteriaTable(a_connection, tableId, userId);
			String rowQuery = TableViewManager.getRowQuery(a_connection, tableId, userId, criteriaTableId, false, viewPref);
			String lsSql	= "";
			Calendar cal	= Calendar.getInstance(TimeZone.getTimeZone("GMT"));

			if(criteriaTableId == -1)
			{
				if (baseline == true)
				{
					CALL_BW_GET_TBL_TXLIST = "{CALL BW_GET_TBL_TXLIST(?,?,?,?,?,?,?,?)}";
					System.out.println("Without criteriaTableId baseline");
				}
				else
				{
					CALL_BW_GET_TBL_TXLIST = "{CALL BW_GET_TBL_TXLIST_NOBL(?,?,?,?,?,?,?,?)}";
					System.out.println("Calling BW_GET_TBL_TXLIST_NOBL (no criteria)");
				}

				preparedstatement = a_connection.prepareStatement(CALL_BW_GET_TBL_TXLIST);

				preparedstatement.setInt(1, tableId);
				System.out.println("tableId = " + tableId);
				preparedstatement.setInt(2, stid);
				System.out.println("stid = " + stid);
				preparedstatement.setInt(3, etid);
				System.out.println("etid = " + etid);
				preparedstatement.setTimestamp(4, new java.sql.Timestamp(sdate), cal);
				preparedstatement.setTimestamp(5, new java.sql.Timestamp(edate), cal);
				preparedstatement.setInt(6, userId);
				System.out.println("userId = " + userId);
				preparedstatement.setInt(7, nhId);
				System.out.println("nhId = " + nhId);
				preparedstatement.setString(8, viewPref);
				System.out.println("viewPref = " + viewPref);
			}
			else
			{
				if(baseline == false)
				{
					System.out.println("With criteriaTableId NO baseline");

					lsSql = QueryMaker.getFiltredTransactionListNoBL(rowQuery);

					System.out.println("Check here TX query " + lsSql);

					System.out.println(" tableId" + tableId);
					System.out.println(" stid" + stid);
					System.out.println(" userId " + userId);

					preparedstatement = a_connection.prepareStatement(lsSql);

					//preparedstatement.setInt(1, tableId);
					//preparedstatement.setInt(2, stid);
					//preparedstatement.setInt(3, userId);

					preparedstatement.setInt(1, tableId);
					preparedstatement.setInt(2, stid);
					preparedstatement.setInt(3, userId);

					//preparedstatement.setInt(4, tableId);
					//preparedstatement.setInt(5, stid);
					//preparedstatement.setInt(6, userId);

					//preparedstatement.setInt(7, tableId);
					//preparedstatement.setInt(8, stid);
					//preparedstatement.setInt(9, userId);

				}
				else
				{
					System.out.println("With criteriaTableId baseline");
					lsSql = QueryMaker.getFiltredTransactionListBL(rowQuery);
					System.out.println("lsSql = " + lsSql);
					preparedstatement = a_connection.prepareStatement(lsSql);

					preparedstatement.setTimestamp(1, new java.sql.Timestamp(sdate), cal);
					preparedstatement.setTimestamp(2, new java.sql.Timestamp(edate), cal);

					preparedstatement.setTimestamp(3, new java.sql.Timestamp(sdate), cal);
					preparedstatement.setTimestamp(4, new java.sql.Timestamp(edate), cal);

					preparedstatement.setTimestamp(5, new java.sql.Timestamp(sdate), cal);
					preparedstatement.setTimestamp(6, new java.sql.Timestamp(edate), cal);

					preparedstatement.setTimestamp(7, new java.sql.Timestamp(sdate), cal);
					preparedstatement.setTimestamp(8, new java.sql.Timestamp(edate), cal);

					preparedstatement.setInt(9, tableId);
					preparedstatement.setTimestamp(10, new java.sql.Timestamp(sdate), cal);
					preparedstatement.setTimestamp(11, new java.sql.Timestamp(edate), cal);

					preparedstatement.setInt(12, tableId);
					preparedstatement.setTimestamp(13, new java.sql.Timestamp(sdate), cal);
					preparedstatement.setTimestamp(14, new java.sql.Timestamp(edate), cal);

					preparedstatement.setInt(15, tableId);
					preparedstatement.setTimestamp(16, new java.sql.Timestamp(sdate), cal);
					preparedstatement.setTimestamp(17, new java.sql.Timestamp(edate), cal);
				}
			}

			resultset = preparedstatement.executeQuery ();

		    while (resultset.next())
		    {
				int tid = resultset.getInt(1);
				if (baseline == false) //NOBL
				{
					if (tid <= stid)//only transactions after import tid are interesting
					{
						continue;
					}
				}
				java.sql.Timestamp updatedOn = resultset.getTimestamp(2, cal);
				String updatedBy = resultset.getString(3);
				String comment = resultset.getString(4);
				String action = resultset.getString(5);
				Transaction t = new Transaction(tid, -1, updatedBy, updatedOn.getTime(), action, comment);
				Vector vt = (Vector)tlist.get(new Integer (tid));
				if (vt == null)
				{
					vt = new Vector();
				}
				vt.addElement(t);
				tlist.put (new Integer(tid), vt);
			}
		}
		catch( SystemException se )
		{
			se.printStackTrace();
		}
		catch( SQLException sql1 )
		{
			throw sql1;
		}
		finally
		{
			try
			{
				preparedstatement.close();
				if (resultset != null)
					resultset.close();
			}
			catch( SQLException sql2 )
			{
				throw sql2;
			}
		}
		System.out.println("Time to the list of Trans = " + getElapsedTime());
		return tlist;
    }

	public static void createCellsNewTable(Connection connection,
											Vector xlRowColCells,
											int tid
										)
	throws SQLException
	{
		PreparedStatement stmt = null;


		try
		{
			long prevTime = System.currentTimeMillis();
/*
			System.out.println("Started Updating xl cells ");
			stmt = connection.prepareStatement(
				" update bw_string_value " +
				" set string_value = ? "+
				" where bw_string_value.bw_row_id = ? "+
				" and bw_string_value.bw_column_id = ? "
				);
			Iterator xlrcci = xlRowColCells.iterator();
			while(xlrcci.hasNext())
			{
				RowColumnCell xlrcc = (RowColumnCell)xlrcci.next();

				stmt.setString(1, xlrcc.getStringValue());
				stmt.setInt(2, xlrcc.getRowId());
				stmt.setInt(3, xlrcc.getColumnId());

				stmt.addBatch();
			}
			// submit the batch for execution
			int[] updateCounts = stmt.executeBatch();
			stmt.close();
			System.out.println("Time(sec) to update string_value = " + (System.currentTimeMillis()-prevTime)/1000F);
			prevTime = System.currentTimeMillis();
*/
//////////////////////////////////////////////////////////////////////////////////////////
/*
			System.out.println("Time(sec) to update xl cells = " + (System.currentTimeMillis()-prevTime)/1000F);
			prevTime = System.currentTimeMillis();

			System.out.println("Started Updating string_value ");
			stmt = connection.prepareStatement(
				"update bw_string_value set string_value = bw_cell.string_value, bw_cell_id = bw_cell.id from bw_cell where bw_string_value.bw_cell_id = bw_cell.id and bw_cell.bw_row_id = ? and bw_cell.bw_column_id =? ");
			stmt.setInt(1, xlrcc.getRowId());
			stmt.setInt(2, xlrcc.getColumnId());
			stmt.executeUpdate();
			stmt.close();
			System.out.println("Time(sec) to update string_value = " + (System.currentTimeMillis()-prevTime)/1000F);
			prevTime = System.currentTimeMillis();
*/
			/////////////////////////////////////////////////////////////////////////////////////////////

			stmt = connection.prepareCall(
					"{CALL BW_CR_STRING_CELL(?,?,?,?,?)}");
			Iterator xlrcci = xlRowColCells.iterator();
			while(xlrcci.hasNext())
			{
				RowColumnCell xlrcc = (RowColumnCell)xlrcci.next();

				stmt.setInt(1, xlrcc.getRowId());
				stmt.setInt(2, xlrcc.getColumnId());
				stmt.setString(3, xlrcc.getStringValue());
				if (xlrcc.getFormula() == null || xlrcc.getFormula().equals(" ") || xlrcc.getFormula().equals("") )
				{
					//System.out.println ("Creating string value with null formula = " + xlrcc.getFormula());
					stmt.setString(4, null);
				}
				else
				{
					stmt.setString(4, xlrcc.getFormula());
					//System.out.println ("Creating string value with formula = " + xlrcc.getFormula());
				}
				stmt.setInt(5, tid);

				stmt.addBatch();
			}
			// submit the batch for execution
			int[] updateCounts = stmt.executeBatch();
			stmt.close();

			/////////////////////////////////////////////////////////////////////////////////////
			/*
			System.out.println("Started Inserting xl cells ");

			stmt = connection.prepareStatement(
					"insert into bw_cell ( bw_row_id,bw_column_id,cell_type, string_value, tx_id) values "+
					" (?, ?,  'STRING', ?, ?) ");
			Iterator xlrcci = xlRowColCells.iterator();
			while(xlrcci.hasNext())
			{
				RowColumnCell xlrcc = (RowColumnCell)xlrcci.next();

				stmt.setInt(1, xlrcc.getRowId());
				stmt.setInt(2, xlrcc.getColumnId());
				stmt.setString(3, xlrcc.getStringValue());
				stmt.setInt(4, tid);

				stmt.addBatch();
			}
			// submit the batch for execution
			int[] updateCounts = stmt.executeBatch();
			stmt.close();


			System.out.println("Time(sec) to Insert xl cells = " + (System.currentTimeMillis()-prevTime)/1000F);
			prevTime = System.currentTimeMillis();



			// the string_value table
			PreparedStatement stmt2 = connection.prepareStatement(
								"insert into bw_string_value "+
								" select bw_cell.id, bw_cell.string_value, ? "+
								" from bw_cell " +
								" where bw_cell.tx_id = ?");
			stmt2.setInt(1, tid);
			stmt2.setInt(2, tid);
			stmt2.executeUpdate();
			stmt2.close();

			System.out.println("Time(sec) to insert string values  = " + (System.currentTimeMillis()-prevTime)/1000F);
			prevTime = System.currentTimeMillis();
			*/
			/*
			System.out.println("Started Updating cells ");
			// update the cell tables
			PreparedStatement stmt3 = connection.prepareStatement(
				" update bw_cell " +
				" set " +
				" string_value = bw_string_value.string_value, " +
				" bw_stringvalue_id = bw_string_value.id " +
				" from bw_string_value " +
				" where bw_cell.id = bw_string_value.bw_cell_id ");
			stmt3.executeUpdate();
			stmt3.close();
			System.out.println("Time(sec) to update cells  = " + (System.currentTimeMillis()-prevTime)/1000F);
			prevTime = System.currentTimeMillis();
			*/


		}
		catch( SQLException sql1 )
		{
			throw sql1;
		}

	}

    public static void createRowsNewTable(Connection connection,
										int m_tableid,
										int tid,
										int userId,
										Vector xlRows
									)
	throws SQLException
	{
		
		PreparedStatement stmt = connection.prepareStatement(
					"INSERT INTO BW_ROW VALUES (?, ?, ?, ?, ?, ?, ?)");
		try
		{
			for ( int rowIndex = 0; rowIndex < xlRows.size(); rowIndex++ )
			{
				xlRow xlrow = (xlRow)xlRows.elementAt(rowIndex);

				Integer rowAddress = new Integer(xlrow.getRowAddress());
				stmt.setString(1, rowAddress.toString());
				stmt.setInt(2, m_tableid);
				stmt.setInt(3, tid);
				stmt.setFloat(4, rowAddress.floatValue());
				stmt.setInt(5, 1);
				stmt.setInt(6, userId);
				stmt.setInt(7, tid);
				
				stmt.addBatch();
			}

			// submit the batch for execution
			int[] updateCounts = stmt.executeBatch();

		}
		catch( SQLException sql1 )
		{
			throw sql1;
		}
		finally
		{
			try
			{
				stmt.close();
			}
			catch( SQLException sql2 ) {
				throw sql2;
			}
		}
	}
	public static ArrayList createRowsNewTable(Connection connection,
									int m_tableid,
									int tid,
									int userId,
									int numRows
								)
	throws SQLException
	{
		ArrayList rowIds = new ArrayList(numRows);
		PreparedStatement stmt = connection.prepareStatement(
					"INSERT INTO BW_ROW VALUES (?, ?, ?, ?, ?, ?, ?)");
		try
		{
			for (int rowIndex = 0; rowIndex < numRows; rowIndex++)
			{
				stmt.setString(1, "");
				stmt.setInt(2, m_tableid);
				stmt.setInt(3, tid);
				stmt.setFloat(4, rowIndex + 1);
				stmt.setInt(5, 1);
				stmt.setInt(6, userId);
				stmt.setInt(7, tid);
				stmt.addBatch();
			}

			// submit the batch for execution
			int[] updateCounts = stmt.executeBatch();

		}
		catch (SQLException sql1)
		{
			System.out.println("TableManager::createRowsNewTable() -> SQLException in creating new rows in batch "); 
			throw sql1;
		}
		finally
		{
			try
			{
				if (stmt != null)
					stmt.close();
				stmt = null;
			}
			catch (SQLException sql2)
			{
				throw sql2;
			}
		}

		// get back the new row ids
		ResultSet resultset = null;
		try
		{
			String q = "select id from bw_row where tx_id = ?";
			stmt = connection.prepareStatement(q);
			stmt.setInt(1, tid);
			resultset = stmt.executeQuery();
			while (resultset.next())
			{
				int rowId = resultset.getInt(1);
				rowIds.add(new Integer(rowId));
			}

		}
		catch (SQLException sql1)
		{
			System.out.println("TableManager::createRowsNewTable() -> SQLException in getting rowids back ");
			throw sql1;
		}
		finally
		{
			try
			{
				if (stmt != null)
					stmt.close();
				if (resultset != null)
					resultset.close();
			}
			catch (SQLException sql2)
			{
				throw sql2;
			}
		}

		return rowIds;
	}

	public static void exportCells(  Connection a_connection,
									int tableId,
									int xlTid,
									int userId,
									int tid
								)
	throws SQLException
	{

		CallableStatement callablestatement = null;
		try {
				/*
    				@list ntext,
					@tbl_id integer,
					@xltid integer,
					@userid integer,
					@tid integer
				*/
				callablestatement = a_connection.prepareCall(CALL_BW_EXPORT);
				callablestatement.setInt(1, tableId);
				callablestatement.setInt(2, xlTid);
				callablestatement.setInt(3, userId);
				callablestatement.setInt(4, tid);


				int l = callablestatement.executeUpdate();

			}
			catch( SQLException sql1 ) {
				throw sql1;
			}
			finally {
				try {
					callablestatement.close();
				}
				catch( SQLException sql2 ) {
					throw sql2;
				}
			}
	}

	public static void deactivateRows(  Connection a_connection,
										String rowBuf,
										String sep,
										int txid,
										String ViewPref,
										int user_id,
										int a_table_id
									)
	throws SQLException {

		CallableStatement callablestatement = null;
		try {
			/*
				@array varchar(4000),
				@separator char(1),
				@txid Integer,
				@view_preference varchar(256),
				@user_id integer,
				@tbl_id integer
			*/
			callablestatement = a_connection.prepareCall(CALL_BW_DEACTIVATE_ROWS);
			callablestatement.setString(1, rowBuf);
			callablestatement.setString(2, sep);
			callablestatement.setInt(3, txid);
			callablestatement.setString(4, ViewPref);
			callablestatement.setInt(5, user_id);
			callablestatement.setInt(6, a_table_id);


			int l = callablestatement.executeUpdate();

		}
		catch( SQLException sql1 ) {
			throw sql1;
		}
		finally {
			try {
				callablestatement.close();
			}
			catch( SQLException sql2 ) {
				throw sql2;
			}
		}
	}


	public static void createNewCells( Connection a_connection, int a_tableid, int tid)
    throws SQLException {

        CallableStatement callablestatement = null;
        try {

            callablestatement = a_connection.prepareCall(CALL_BW_CR_NEW_CELLS);
            callablestatement.setInt(1, a_tableid);
            callablestatement.setInt(2, tid);
            int l = callablestatement.executeUpdate();

        }
        catch( SQLException sql1 )
        {
			sql1.printStackTrace();
            throw sql1;
        }
        finally {
            try {
                callablestatement.close();
            }
            catch( SQLException sql2 ) {
                throw sql2;
            }
        }
    }


	public static void lockTableForUpdate(Connection connection, int table_id) throws SQLException
	{
		PreparedStatement preparedstatement = null;
		ResultSet rset = null;

		try
		{
			if ( DatabaseLoader.getDatabaseType().trim().equalsIgnoreCase("ORACLE"))
			{
				preparedstatement = DatabaseLoader.getPreparedStatementFromPreLoadedQueries("BW_LOCK_TBL_FOR_UPD", connection );
				preparedstatement.setInt(1,table_id);

			}
			else
			{
				preparedstatement = connection.prepareStatement(CALL_BW_LOCK_TBL_FOR_UPD);
				preparedstatement.setInt(1,table_id);
			}
			rset = preparedstatement.executeQuery();
			System.out.println("GOOOOOOOOT A LOOOOOOOOCK ON TABLE " + table_id);
		}
		catch(SQLException sqlexception)
		{
			sqlexception.printStackTrace();
			throw sqlexception;
		}
		finally
		{
			try
			{
				if ( rset != null )
				{
						rset.close();
				}

				if ( preparedstatement != null )
				{
						preparedstatement.close();
				}
			}
			catch(SQLException sqlexception1)
			{
				sqlexception1.printStackTrace();
			}
		}

	}

	public static Hashtable getTablesUsingLookup ( Connection connection, int table_id)
	throws SystemException
	{
		ResultSet resultset = null;
		PreparedStatement preparedstatement = null;
		Hashtable ht = new Hashtable();

		try {
			if ( DatabaseLoader.getDatabaseType().trim().equalsIgnoreCase("ORACLE"))
			{
				preparedstatement = DatabaseLoader.getPreparedStatementFromPreLoadedQueries("BW_GET_TBLS_USING_LKP", connection );
				preparedstatement.setInt(1,table_id);

			}
			else
			{
				preparedstatement = connection.prepareStatement(CALL_BW_GET_TBLS_USING_LKP);
				preparedstatement.setInt(1,table_id);
			}
			resultset = preparedstatement.executeQuery();
			/*
				LKP_COL.ID AS LKP_COLUMN_ID,
				TBL_W_LKP.ID TBL_W_LKP_ID,
				TBL_W_LKP.NAME TBL_W_LKP_NAME,
				COL_W_LKP.ID COL_W_LKP_ID
			*/
			while ( resultset.next() ) {
				int lookupColumnId = resultset.getInt("LKP_COLUMN_ID");
				int tableUsingLookupId = resultset.getInt("TBL_W_LKP_ID");
				String tableUsingLookupName = resultset.getString("TBL_W_LKP_NAME");
				String tableUsingLookupPurpose = resultset.getString("TBL_W_LKP_PURPOSE");
				int columnUsingLookupId = resultset.getInt("COL_W_LKP_ID");
				String columnUsingLookupName = resultset.getString("COL_W_LKP_NAME");

				TablesUsingLkpColumn tulc = new TablesUsingLkpColumn(tableUsingLookupId,
																	 tableUsingLookupName,
																	 tableUsingLookupPurpose,
																	 columnUsingLookupId,columnUsingLookupName);

				Vector tablesVector = (Vector)ht.get( new Integer(lookupColumnId));
				if (tablesVector == null)
				{
					tablesVector = new Vector();
				}

				tablesVector.addElement(tulc);

				ht.put(new Integer(lookupColumnId), tablesVector);
			}


		}
		catch(SQLException sqlexception) {
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
				throw new SystemException(sqlexception1);
			}
		}
		return ht;

	}

	public static Hashtable getTablesAndColumnsUsingLookup ( Connection connection, int table_id, int col_id)
		throws SystemException
	{
		ResultSet resultset = null;
		PreparedStatement preparedstatement = null;
		Hashtable ht = new Hashtable();
		System.out.println("fetching lookup columns for tbl " + table_id + " " + col_id );

		try {
			if ( DatabaseLoader.getDatabaseType().trim().equalsIgnoreCase("ORACLE"))
			{
				preparedstatement = DatabaseLoader.getPreparedStatementFromPreLoadedQueries("BW_GET_TBLS_USING_LKP_CL", connection );
				preparedstatement.setInt(1,table_id);
				preparedstatement.setInt(2,col_id);

			}
			else
			{
				preparedstatement = connection.prepareStatement(CALL_BW_GET_TBLS_USING_LKP_CL);
				preparedstatement.setInt(1,table_id);
				preparedstatement.setInt(2,col_id);
			}
			resultset = preparedstatement.executeQuery();

			/*
				LKP_COL.ID AS LKP_COLUMN_ID,
				TBL_W_LKP.ID TBL_W_LKP_ID,
				TBL_W_LKP.NAME TBL_W_LKP_NAME,
				COL_W_LKP.ID COL_W_LKP_ID
			*/

			while ( resultset.next() )
			{
				int lookupColumnId = resultset.getInt("LKP_COLUMN_ID");
				int tableUsingLookupId = resultset.getInt("TBL_W_LKP_ID");
				String tableUsingLookupName = resultset.getString("TBL_W_LKP_NAME");
				String tableUsingLookupPurpose = resultset.getString("TBL_W_LKP_PURPOSE");
				int columnUsingLookupId = resultset.getInt("COL_W_LKP_ID");
				String columnUsingLookupName = resultset.getString("COL_W_LKP_NAME");


				TablesUsingLkpColumn tulc = new TablesUsingLkpColumn(tableUsingLookupId,
																	 tableUsingLookupName,
																	 tableUsingLookupPurpose,
																	 columnUsingLookupId,columnUsingLookupName);
					System.out.println("got lookup columns in tbl " + tableUsingLookupName  + " " + columnUsingLookupName );
				Vector tablesVector = (Vector)ht.get( new Integer(col_id));
				if (tablesVector == null)
				{
					tablesVector = new Vector();
				}

				tablesVector.addElement(tulc);
				ht.put(new Integer(col_id), tablesVector);
			}

			return ht;
		}
		catch(SQLException sqlexception) {
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
				throw new SystemException(sqlexception1);
			}
		}

	}


    public static Hashtable getWbTablesForSelection( Connection connection,
						    int table_id,
                                                    int a_user_id,
                                                    int a_status,
                                                    int a_access )
    throws SystemException
    {
        String CALL_BW_GET_WBTABLES_FOR_SELECTION="{CALL BW_GET_WBTABLES_FOR_SELECTION(?,?,?,?)}";
        ResultSet resultset = null;
        PreparedStatement preparedstatement = null;
        Hashtable ht = new Hashtable();

        try {
            preparedstatement = connection.prepareStatement(CALL_BW_GET_WBTABLES_FOR_SELECTION);
            preparedstatement.setInt(1,a_user_id);
            preparedstatement.setInt(2,table_id);
            preparedstatement.setInt(3,a_access);
            preparedstatement.setInt(4,a_status);
            resultset = preparedstatement.executeQuery();
            while ( resultset.next() ) {
                int a_collaboration_id = resultset.getInt("COLLABID");
                int a_wbid = resultset.getInt("WBID");
                String a_name = resultset.getString("WBNAME");
                int q_status = resultset.getInt("WBSTATUS");
                int a_sequence_number = resultset.getInt("WBSEQ");
                int a_table_id = resultset.getInt("TID");
                int a_neighborhood_id = resultset.getInt("NHID");
                String a_table_name = resultset.getString("TNAME");
                String a_table_purpose = resultset.getString("TPURPOSE");
                int a_table_status = resultset.getInt("TSTATUS");
                int a_table_sequence_number = resultset.getInt("TSEQ");
                int a_table_access = resultset.getInt("ACCESS");
                String 	a_table_viewPreference = resultset.getString("VIEW_PREFERENCE_TYPE");



                Whiteboard wb = new Whiteboard(a_wbid, a_collaboration_id, a_name, a_neighborhood_id, a_status, a_sequence_number);
                Vector tablesVector = (Vector)ht.get(wb);
                if (tablesVector == null)
                    tablesVector = new Vector();
                Table t = new Table( a_table_id, a_wbid, a_table_name, a_table_purpose, a_table_access, a_neighborhood_id, a_table_status, a_table_sequence_number, a_table_viewPreference  );
                tablesVector.addElement(t);

                ht.put(wb, tablesVector);
            }

            return ht;
        }
        catch(SQLException sqlexception) {
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
                throw new SystemException(sqlexception1);
            }
        }
    }


    public static Hashtable getWbLookupTablesForSelection( Connection connection,
							    int table_id,
	                                                    int a_user_id,
	                                                    int a_status,
	                                                    int a_access )
	    throws SystemException
	    {
	        String CALL_BW_GET_LOOKUP_WBTABLES_FOR_SELECTION="{CALL BW_GET_LOOKUP_WBTABLES_FOR_SELECTION(?,?,?,?)}";
	        ResultSet resultset = null;
	        PreparedStatement preparedstatement = null;
	        Hashtable ht = new Hashtable();

	        try {
	            preparedstatement = connection.prepareStatement(CALL_BW_GET_LOOKUP_WBTABLES_FOR_SELECTION);
	            preparedstatement.setInt(1,a_user_id);
	            preparedstatement.setInt(2,table_id);
	            preparedstatement.setInt(3,a_access);
	            preparedstatement.setInt(4,a_status);
	            resultset = preparedstatement.executeQuery();
	            while ( resultset.next() ) {
	                int a_collaboration_id = resultset.getInt("COLLABID");
	                int a_wbid = resultset.getInt("WBID");
	                String a_name = resultset.getString("WBNAME");
	                int q_status = resultset.getInt("WBSTATUS");
	                int a_sequence_number = resultset.getInt("WBSEQ");
	                int a_table_id = resultset.getInt("TID");
	                int a_neighborhood_id = resultset.getInt("NHID");
	                String a_table_name = resultset.getString("TNAME");
	                String a_table_purpose = resultset.getString("TPURPOSE");
	                int a_table_status = resultset.getInt("TSTATUS");
	                int a_table_sequence_number = resultset.getInt("TSEQ");
	                int a_table_access = resultset.getInt("ACCESS");
	                String 	a_table_viewPreference = resultset.getString("VIEW_PREFERENCE_TYPE");



	                Whiteboard wb = new Whiteboard(a_wbid, a_collaboration_id, a_name, a_neighborhood_id, a_status, a_sequence_number);
	                Vector tablesVector = (Vector)ht.get(wb);
	                if (tablesVector == null)
	                    tablesVector = new Vector();
	                Table t = new Table( a_table_id, a_wbid, a_table_name, a_table_purpose, a_table_access, a_neighborhood_id, a_table_status, a_table_sequence_number, a_table_viewPreference  );
	                tablesVector.addElement(t);

	                ht.put(wb, tablesVector);
	            }

	            return ht;
	        }
	        catch(SQLException sqlexception) {
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
	                throw new SystemException(sqlexception1);
	            }
	        }
    }

    public static TableInfo getTableInfo( Connection connection, int a_user_id, int a_table_id ) throws SystemException {
        ResultSet resultset = null;
        PreparedStatement preparedstatement = null;
        TableInfo tbi = null;
        try {
			if ( DatabaseLoader.getDatabaseType().trim().equalsIgnoreCase("ORACLE"))
			{
				preparedstatement = DatabaseLoader.getPreparedStatementFromPreLoadedQueries("BW_GET_TBL_INFO", connection );
				preparedstatement.setInt(1,a_table_id);

			}
			else
			{
				preparedstatement = connection.prepareStatement(CALL_BW_GET_TBL_INFO);
				preparedstatement.setInt(1,a_table_id);
			}

			resultset = preparedstatement.executeQuery();
			Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
            if ( resultset.next() ) {
                int a_collab_id;
                String a_name;
                String a_purpose;
                String q_nh_name;

                int a_wb_id;
                String a_wb_name;


                String  a_table_name;
                String  a_table_purpose;
                String  a_table_manager;

                int cr_tid = -1;
                int cr_by_id = 0;
                String cr_by_user = "";
                long cr_time = 0;

                int is_locked = 0;
                int lock_tid = 0;
                int locked_by_id = 0;
                String locked_by_user = "";
                long locked_time = 0;



                a_collab_id = resultset.getInt("ID");
                a_name = resultset.getString("NAME");
                a_purpose = resultset.getString("PURPOSE");
                q_nh_name = resultset.getString("COLLAB_NH_NAME");
				a_wb_name = resultset.getString("WB_NAME");
                a_wb_id = resultset.getInt("WB_ID");
                a_table_name = resultset.getString("BW_TBL_NAME");
                a_table_purpose = resultset.getString("BW_TBL_PURPOSE");
                String a_default_ViewPreference = resultset.getString("VIEW_PREFERENCE_TYPE");
		      	cr_tid = resultset.getInt("TX_ID");
		        cr_by_id = resultset.getInt("CREATED_BY_ID");
				cr_by_user = resultset.getString("CREATED_BY");
	    		cr_time = resultset.getTimestamp("CREATED_TIME", cal).getTime();
		   		is_locked = resultset.getInt("IS_LOCKED");
		      	lock_tid = resultset.getInt("LOCK_TX_ID");
		        locked_by_id = resultset.getInt("LOCKED_BY_ID");
				locked_by_user = resultset.getString("LOCKED_BY");
	    		locked_time = resultset.getTimestamp("LOCK_UNLOCK_TIME",cal).getTime();


                tbi = new TableInfo(    a_collab_id,
                                        a_name,
                                        a_purpose,
                                        q_nh_name,
                                        a_wb_id,
                                        a_wb_name,
                                        a_table_id,
                                        a_table_name,
                                        a_table_purpose,
                                        a_default_ViewPreference,
                                        cr_tid,
                                        cr_by_id,
                                        cr_by_user,
                                        cr_time,
                                        is_locked,
                                        lock_tid,
                                        locked_by_id,
                                        locked_by_user,
                                        locked_time
                                    );


            }
                return tbi;
        }
        catch(SQLException sqlexception)
        {

					sqlexception.printStackTrace();

		            throw new SystemException(sqlexception);
        }

        catch(Exception exception)
        {
			exception.printStackTrace();
			return null;
        }
        finally
        {
            try {
                resultset.close();
                preparedstatement.close();
            }
            catch(SQLException sqlexception1) {
                throw new SystemException(sqlexception1);
            }
        }
    }



	public static int  copyTable
						(
							Connection  connection,
							int  source_table_id,
							int  wbid,
							String tableName,
							String  tableDescr,
							boolean  copyStructure,
							boolean  copyLatestContent,
							boolean  copyDesignValues,
							boolean  copyUIPreferences,
							boolean  copyAccess,
							boolean  copyDeactivatedContent,
							String  viewPreference,
							int  memberId,
							int  tid
						 )
	throws SystemException
	{

		int m_table_id = -1;
		int i_copyStructure = copyStructure? 1:0;
		int i_copyLatestContent= copyLatestContent? 1:0;
		int i_copyDesignValues= copyDesignValues? 1:0;
		int i_copyUIPreferences= copyUIPreferences? 1:0;
		int i_copyAccess= copyAccess? 1:0;
		int i_copyDeactivatedContent= copyDeactivatedContent? 1:0;

		CallableStatement callablestatement = null;

		try
		{
			callablestatement = connection.prepareCall(CALL_BW_CP_TBL_FROM_LATEST);
			callablestatement.setInt(1,source_table_id);
			callablestatement.setInt(2,i_copyAccess);
			callablestatement.setInt(3,i_copyUIPreferences);
			callablestatement.setInt(4,i_copyStructure);
			callablestatement.setInt(5,i_copyLatestContent);
			callablestatement.setInt(6,i_copyDeactivatedContent);
			callablestatement.setInt(7,i_copyDesignValues);
			callablestatement.setInt(8,wbid);
			callablestatement.setString(9,tableName);
			callablestatement.setString(10,tableDescr);
			callablestatement.setInt(11,memberId);
			callablestatement.setInt(12,tid);
			callablestatement.registerOutParameter(13,java.sql.Types.INTEGER);
			int result = callablestatement.executeUpdate();
			m_table_id= callablestatement.getInt(13);
			callablestatement.close();

	}
	catch(SQLException sqlexception)
	{
		throw new SystemException(sqlexception);
	}
	finally
	{
		try
		{
			if (callablestatement != null )
					callablestatement.close();
		}
		catch(SQLException sqlexception1) {
			throw new SystemException(sqlexception1);
		}
	}

		return m_table_id;

	}



	public static int  copyTableFromBaseline
						(
							Connection  connection,
							int  source_table_id,
							int baselineId,
							int  wbid,
							String tableName,
							String  tableDescr,
							boolean  copyStructure,
							boolean  copyLatestContent,
							boolean  copyDesignValues,
							boolean  copyUIPreferences,
							boolean  copyAccess,
							String  viewPreference,
							int  memberId,
							int  tid
						)
	 throws SystemException
	 {

			int m_table_id = -1;
			int i_copyStructure = copyStructure? 1:0;
			int i_copyLatestContent= copyLatestContent? 1:0;
			int i_copyDesignValues= copyDesignValues? 1:0;
			int i_copyUIPreferences= copyUIPreferences? 1:0;
			int i_copyAccess= copyAccess? 1:0;
			CallableStatement callablestatement = null;

			try
			{

				callablestatement = connection.prepareCall(CALL_BW_CP_TBL_FROM_BL);
				callablestatement.setInt(1,source_table_id);
				callablestatement.setInt(2,baselineId);
				callablestatement.setInt(3,i_copyAccess);
				callablestatement.setInt(4,i_copyUIPreferences);
				callablestatement.setInt(5,i_copyStructure);
				callablestatement.setInt(6,i_copyLatestContent);
				callablestatement.setInt(7,i_copyDesignValues);
				callablestatement.setInt(8,wbid);
				callablestatement.setString(9,tableName);
				callablestatement.setString(10,tableDescr);
				callablestatement.setInt(11,memberId);
				callablestatement.setInt(12,tid);
				callablestatement.registerOutParameter(13,java.sql.Types.INTEGER);

				int result = callablestatement.executeUpdate();
				m_table_id= callablestatement.getInt(13);
				callablestatement.close();

		}
		catch(SQLException sqlexception)
		{
			throw new SystemException(sqlexception);
		}
		finally
		{
			try
			{
				callablestatement.close();
			}
			catch(SQLException sqlexception1) {
				throw new SystemException(sqlexception1);
			}
		}

			return m_table_id;

	 }


     public static TableAccessList getTableAccessForMember( Connection connection, int a_member_id, int table_id ) throws SystemException {
	        ResultSet resultset = null;
	        PreparedStatement preparedstatement = null;
	        Hashtable relationshipToAccess = new Hashtable();
	        Vector accessList = null;

	         TableAccessList tbACL  = null;

	        try
	        {
				if ( DatabaseLoader.getDatabaseType().trim().equalsIgnoreCase("ORACLE"))
				{
					preparedstatement = DatabaseLoader.getPreparedStatementFromPreLoadedQueries("BW_GET_TBL_ACCESS_FOR_MEMBER", connection );
					preparedstatement.setInt(1,a_member_id);
					preparedstatement.setInt(2,table_id);
					preparedstatement.setInt(3,a_member_id);
					preparedstatement.setInt(4,table_id);
					preparedstatement.setInt(5,a_member_id);
					preparedstatement.setInt(6,table_id);
					preparedstatement.setInt(7,table_id);

				}
				else
				{
					preparedstatement = connection.prepareStatement(CALL_BW_GET_TBL_ACCESS_FOR_MEMBER);
					preparedstatement.setInt(1,table_id);
					preparedstatement.setInt(2,a_member_id);
				}

				preparedstatement.executeQuery();

				resultset = preparedstatement.getResultSet();

				while( resultset.next() )
				{
					int    a_acl_id;
					int    a_table_id;
					String  a_access_relationship;
					int  a_acl;
					String a_object_Type;
					int a_object_id;



					a_acl_id = resultset.getInt("ID");
					a_access_relationship = resultset.getString("REL");
					a_table_id = resultset.getInt("TABLE_ID");
					a_object_Type = resultset.getString("NHA_OBJECT_TYPE");
					a_object_id = resultset.getInt("NHA_OBJECT_ID");
					a_acl = resultset.getInt("ACCESS_");

					TableAccessList tbacl = new TableAccessList( a_acl_id, a_table_id, a_access_relationship, a_acl );

					Vector relationshipACLs = (Vector)relationshipToAccess.get( a_access_relationship );

					if ( relationshipACLs != null )
					{
							relationshipACLs.add(tbacl);
					}
					else
					{
						relationshipACLs = new Vector();
						relationshipACLs.add(tbacl);
						relationshipToAccess.put( a_access_relationship, relationshipACLs );
					}
				}


				Enumeration keys = relationshipToAccess.keys();



				if ( !keys.hasMoreElements())
				{
					tbACL  =  new  TableAccessList(0, table_id,"COMBINATION",0);
				}
				else  if ( keys.hasMoreElements() )
				{


					if ( relationshipToAccess.size() > 0 )
					{
						 int acl = 0;

						 for ( ; keys.hasMoreElements() ;)
						{
								 Object key = 	keys.nextElement();
								TableAccessList tACL = (TableAccessList)( (Vector)	relationshipToAccess.get(key)).elementAt(0);
								acl = acl | tACL.getACL();
						}

						 tbACL  = new  TableAccessList(   0, table_id,"COMBINATION",acl);

					}
					else
					{
							Object key = 	keys.nextElement();
							tbACL = 	 (TableAccessList) ( (Vector)relationshipToAccess.get(key)).elementAt(0);
					}
				}

	        }
	        catch(SQLException sqlexception) {
	            throw new SystemException(sqlexception);
	        }
	        finally {
	            try {
					if ( resultset != null )
	                resultset.close();

	                if ( preparedstatement != null )
	                preparedstatement.close();
	            }
	            catch(SQLException sqlexception1) {
	                throw new SystemException(sqlexception1);
	            }
	        }

	        return tbACL;
    }


	public static Hashtable getNeighborhoodRelationshipsForTable(Connection connection, int tableid)throws SystemException
	{
		Hashtable relationships = new Hashtable();
		PreparedStatement preparedstatement = null;
		ResultSet resultset = null;
		// System.out.println("getNeighborhoodRelationships for nh for table" + tableid);
		 try
		   {
			   if ( DatabaseLoader.getDatabaseType().trim().equalsIgnoreCase("ORACLE"))
				{
					preparedstatement = DatabaseLoader.getPreparedStatementFromPreLoadedQueries("BW_GET_NH_RELS_FOR_TBL", connection );
					preparedstatement.setInt(1,tableid);

				}
				else
				{
					preparedstatement = connection.prepareCall(CALL_BW_GET_NH_RELS_FOR_TBL);
					preparedstatement.setInt(1, tableid);
				}

				resultset = preparedstatement.executeQuery();
				while( resultset.next() )
				{
					int  targetNHId = resultset.getInt("ID");
					String name = resultset.getString("NAME");
					String relationship = resultset.getString("REL");
					NeighborhoodId nh = new NeighborhoodId(targetNHId, name);

					if ( relationships.get(relationship ) == null )
					{
						relationships.put(relationship, new Vector());
					}

				   ( (Vector)relationships.get(relationship )).add(nh);

				}
			}
			catch(SQLException sqlexception)
			{
				throw new SystemException(sqlexception);
			}
			finally
			{
					try
					{
						if ( resultset!= null )
							resultset.close();

						if ( preparedstatement != null )
						   preparedstatement.close();
					}
					catch(SQLException sqlexception1)
					{
						throw new SystemException(sqlexception1);
					}
			}

	return relationships;
}

     public static Hashtable getTableAccess( Connection connection, int table_id ) throws SystemException {
	        ResultSet resultset = null;
	        PreparedStatement preparedstatement = null;
	        Hashtable  relationshipToAccess = new Hashtable();


	        try
	        {
				if ( DatabaseLoader.getDatabaseType().trim().equalsIgnoreCase("ORACLE"))
				{
					preparedstatement = DatabaseLoader.getPreparedStatementFromPreLoadedQueries("BW_GET_TBL_ACCESS", connection );
					preparedstatement.setInt(1,table_id);

				}
				else
				{
					preparedstatement = connection.prepareStatement(CALL_BW_GET_TBL_ACCESS);
					preparedstatement.setInt(1,table_id);
				}
	            resultset = preparedstatement.executeQuery();

	           while( resultset.next() )
	            {
							int    a_acl_id;
							int    a_table_id;
							String  a_access_relationship;
							int  a_acl;
							String a_object_Type;
							int a_object_id;



							a_acl_id = resultset.getInt("ID");
							a_access_relationship = resultset.getString("REL");
							a_table_id = resultset.getInt("TABLE_ID");
							a_object_Type = resultset.getString("OBJECT_TYPE");
							a_object_id = resultset.getInt("OBJECT_ID");
							a_acl = resultset.getInt("ACCESS_");

							TableAccessList tbacl = new TableAccessList( a_acl_id, a_table_id, a_access_relationship, a_acl );
					//		// System.out.println("access from db for table");
					//		tbacl.print();
							relationshipToAccess.put( a_access_relationship, tbacl);
	            }


	        }
	        catch(SQLException sqlexception) {
	            throw new SystemException(sqlexception);
	        }
			catch(Exception exception) {
	            exception.printStackTrace();
	        }
	        finally
	        {
	            try
	            {
					if ( resultset!= null )
	                	resultset.close();
	                if ( preparedstatement!= null )
	                	preparedstatement.close();
	            }
	            catch(SQLException sqlexception1)
	            {
	                throw new SystemException(sqlexception1);
	            }
	        }

	        return relationshipToAccess;
    }



	   public static void  addAccesstoTable(
										   Connection connection,
										   int a_table_id,
										   Vector accessLists,
										   int tid
                        			 )
		throws SQLException
		{

			CallableStatement callablestatement = null;
			try
			{

				if ( accessLists.size () > 0 )
				{

					for ( int a = 0; a < accessLists.size () ; a++ )
					{

							/*

							BW_CR_TBL_ACCESS
							(
								@TABLE_ID INTEGER,
								@REL NVARCHAR(128),
								@OBJECT_TYPE NVARCHAR(128),
								@OBJECT_ID INTEGER,
								@ACCESS    INTEGER,
								@TX_ID INTEGER
								)

							*/
							NewTableAccessList tbacl = (NewTableAccessList)accessLists.elementAt(a);
							//tbacl.print();
							//// System.out.println("");
							callablestatement = connection.prepareCall(CALL_BW_CR_TBL_ACCESS);

							callablestatement.setInt(1, tbacl.getTableId());
							callablestatement.setString(2, tbacl.getRelationship());
							callablestatement.setString(3, "TABLE");
							callablestatement.setInt(4, tbacl.getTableId());
							callablestatement.setInt(5, tbacl.getACL());
							callablestatement.setInt(6, tid);

							callablestatement.executeUpdate();

							callablestatement.close();

							callablestatement = null;

						}
					}
			}
			catch( SQLException sql1 )
			{
				throw sql1;
			}
			finally
			{
				try
				{
					if ( callablestatement != null )
						callablestatement.close();
				}
				catch( SQLException sql2 ) {
					throw sql2;
				}
			}


			 }

	public static void  setTableAccess(
										Connection connection,
										int a_table_id,
										TableAccessList tbacl,
										int tid
										)
	throws SQLException
	{

		CallableStatement callablestatement = null;
		try
		{

			callablestatement = connection.prepareCall(CALL_BW_UPD_TBL_ACCESS);
			callablestatement.setInt(1, tbacl.getId());
			callablestatement.setInt(2, tbacl.getTableId());
			callablestatement.setString(3, tbacl.getRelationship());
			callablestatement.setString(4, "TABLE");
			callablestatement.setInt(5, tbacl.getTableId());
			callablestatement.setInt(6, tbacl.getACL());
			callablestatement.setInt(7, tid);
			callablestatement.executeUpdate();
		}
		catch( SQLException sql1 )
		{
			throw sql1;
		}
		finally
		{
			try
			{
				if ( callablestatement != null )
				callablestatement.close();
			}
			catch( SQLException sql2 ) {
				throw sql2;
			}
		}
	}


public static void  updateAccesstoTable(
										Connection connection,
										int a_table_id,
										Vector accessLists,
										int tid
										)
throws SQLException
{

	CallableStatement callablestatement = null;
	try
	{

		if ( accessLists.size () > 0 )
		{

			for ( int a = 0; a < accessLists.size () ; a++ )
			{

				/*

				BW_CR_TBL_ACCESS
				(
				@TABLE_ID INTEGER,
				@REL NVARCHAR(128),
				@OBJECT_TYPE NVARCHAR(128),
				@OBJECT_ID INTEGER,
				@ACCESS    INTEGER,
				@TX_ID INTEGER
				)

				*/
				NewTableAccessList tbacl = (NewTableAccessList)accessLists.elementAt(a);
				//		tbacl.print();
				// System.out.println("");
				callablestatement = connection.prepareCall(CALL_BW_UPD_TBL_ACCESS);
				callablestatement.setInt(1, tbacl.getId());
				callablestatement.setInt(2, tbacl.getTableId());
				callablestatement.setString(3, tbacl.getRelationship());
				callablestatement.setString(4, "TABLE");
				callablestatement.setInt(5, tbacl.getTableId());
				callablestatement.setInt(6, tbacl.getACL());
				callablestatement.setInt(7, tid);
				callablestatement.executeUpdate();
				callablestatement.close();
				callablestatement = null;

			}
		}
	}
	catch( SQLException sql1 )
	{
		throw sql1;
	}
	finally
	{
		try
		{
			if ( callablestatement != null )
			callablestatement.close();
		}
		catch( SQLException sql2 ) {
			throw sql2;
		}
	}
}

public static void  updateTableDescription(
										   Connection connection,
										   int a_table_id,
										   String name,
										   String description
                        			 )
		throws SQLException
		{

							        CallableStatement callablestatement = null;
							        try
							        {

																callablestatement = connection.prepareCall(CALL_BW_UPD_TBL_PROPERTIES);
																callablestatement.setInt(1, a_table_id);
																callablestatement.setString(2, name);
																callablestatement.setString(3, description);
																callablestatement.executeUpdate();
																callablestatement.close();
																callablestatement = null;


									}
									catch( SQLException sql1 )
									{
										throw sql1;
									}
									finally
									{
										try
										{
											if ( callablestatement != null )
												callablestatement.close();
										}
										catch( SQLException sql2 ) {
											throw sql2;
										}
									}


			 }


    public static Vector getTablesForWB( Connection connection, int a_wbid , int a_member_id)
    throws SystemException
    {


		ResultSet resultset = null;
		PreparedStatement preparedstatement = null;

		Vector Collaborations = new Vector();

		CollaborationTreeNode currentCollab = null;

		WhiteboardTreeNode currentWhiteboard = null;

		TableTreeNode currentTable = null;

		String currentRelationship = null;






		try {

			if ( DatabaseLoader.getDatabaseType().trim().equalsIgnoreCase("ORACLE"))
			{
				preparedstatement = DatabaseLoader.getPreparedStatementFromPreLoadedQueries("BW_GET_TBLS_BY_WB", connection );
				preparedstatement.setInt(1,a_member_id);
				preparedstatement.setInt(2,a_wbid);
				preparedstatement.setInt(3,a_member_id);
				preparedstatement.setInt(4,a_wbid);
				preparedstatement.setInt(5,a_member_id);
				preparedstatement.setInt(6,a_wbid);
				preparedstatement.setInt(7,a_wbid);
				preparedstatement.setInt(8,a_wbid);



			}
			else
			{
				preparedstatement = connection.prepareCall(CALL_BW_GET_TBLS_BY_WB);
				preparedstatement.setInt(1,a_member_id);
				preparedstatement.setInt(2,a_wbid);
			}
			resultset = preparedstatement.executeQuery();

			while ( resultset.next() )
			{
				int a_collaboration_id;
				String a_collab_name;
				String a_collab_purpose;

				int a_wb_id;
				String a_wb_name;

				int a_table_id;
				String a_table_name;
				String a_table_purpose;

				String a_relationship;
				int a_access;

				a_collaboration_id = resultset.getInt("COLLAB_ID");
				a_collab_name = resultset.getString("COLLAB_NAME");
				a_collab_purpose = resultset.getString("COLLAB_PURPOSE");
				a_wb_id = resultset.getInt("WB_ID");
				a_wb_name = resultset.getString("WB_NAME");
				a_table_name= resultset.getString("TABLE_NAME");
				a_table_purpose= resultset.getString("TABLE_PURPOSE");
				a_relationship= resultset.getString("REL");
				a_table_id = resultset.getInt("TABLE_ID");
				a_access= resultset.getInt("ACCESS_");

				if ( currentCollab == null || currentCollab.getId() != a_collaboration_id )
				{
						// Add new Collaboration
						CollaborationTreeNode ctn = new CollaborationTreeNode ( a_collaboration_id,a_collab_name, a_collab_purpose  );
						Collaborations.add( ctn );
						currentCollab = ctn;
				}

				if ( currentWhiteboard == null || currentWhiteboard.getId() != a_wb_id )
				{
						// Add new Whiteboard
						if ( a_wb_id != -1 )
						{
							WhiteboardTreeNode wbtn = new WhiteboardTreeNode ( a_wb_id, a_collaboration_id ,a_wb_name );
							currentCollab.getWhiteboards().add(wbtn);
							currentWhiteboard = wbtn;
						}
				}

				if ( currentTable == null || currentTable.getId() != a_table_id )
				{
						// Add new Table
						if ( a_table_id != -1 )
						{
							TableTreeNode tbtn = new TableTreeNode ( a_table_id, a_wb_id, a_table_name ,a_table_purpose , a_access);
							currentWhiteboard.getTables().add(tbtn);
							currentTable = tbtn;
						}
				}
				else
				{
					 if ( currentTable.getId() == a_table_id )
					 {
							int currentAccess = currentTable.getAccess();
							int newAccess = currentAccess | a_access;
							currentTable.setAccess( newAccess );
					 }
				}

			}

			return Collaborations;
		}
		catch(SQLException sqlexception) {
			throw new SystemException(sqlexception);
		}
		finally {
			try {

				if ( resultset != null )
						resultset.close();
			   if ( preparedstatement != null )
					preparedstatement.close();
			}
			catch(SQLException sqlexception1) {
				throw new SystemException(sqlexception1);
			}
		}




    }

    public static Vector getTablesForWBAndBaseline( Connection connection, int a_wbid , int a_baselineId , int a_member_id )
    throws SystemException {


        ResultSet resultset = null;
        PreparedStatement preparedstatement1 = null;
        Vector tables = new Vector();
		TableTreeNode currentTable = null;

        try {


			if ( DatabaseLoader.getDatabaseType().trim().equalsIgnoreCase("ORACLE"))
			{
				preparedstatement1 = DatabaseLoader.getPreparedStatementFromPreLoadedQueries("BW_GET_TBLS_BY_WB_AND_BL", connection );
				preparedstatement1.setInt(1,a_member_id);
				preparedstatement1.setInt(2,a_wbid);
				preparedstatement1.setInt(3,a_baselineId);
				preparedstatement1.setInt(4,a_member_id);
				preparedstatement1.setInt(5,a_wbid);
				preparedstatement1.setInt(6,a_baselineId);
				preparedstatement1.setInt(7,a_member_id);
				preparedstatement1.setInt(8,a_wbid);
				preparedstatement1.setInt(9,a_baselineId);
				preparedstatement1.setInt(10,a_wbid);
				preparedstatement1.setInt(11,a_baselineId);


			}
			else
			{
				preparedstatement1 = connection.prepareStatement(CALL_BW_GET_TBLS_BY_WB_AND_BL);

				preparedstatement1.setInt(1,a_member_id);
				preparedstatement1.setInt(2,a_wbid);
				preparedstatement1.setInt(3,a_baselineId);
			}


            resultset = preparedstatement1.executeQuery();

            while ( resultset.next() )
			{
				int a_collaboration_id;
				String a_collab_name;
				String a_collab_purpose;

				int a_wb_id;
				String a_wb_name;

				int a_table_id;
				String a_table_name;
				String a_table_purpose;

				String a_relationship;
				int a_access;

				a_collaboration_id = resultset.getInt("COLLAB_ID");
				a_collab_name = resultset.getString("COLLAB_NAME");
				a_collab_purpose = resultset.getString("COLLAB_PURPOSE");
				a_wb_id = resultset.getInt("WB_ID");
				a_wb_name = resultset.getString("WB_NAME");
				a_table_name= resultset.getString("TABLE_NAME");
				a_table_purpose= resultset.getString("TABLE_PURPOSE");
				a_relationship= resultset.getString("REL");
				a_table_id = resultset.getInt("TABLE_ID");
				a_access= resultset.getInt("ACCESS_");


				if ( currentTable == null || currentTable.getId() != a_table_id )
				{
						// Add new Table
						if ( a_table_id != -1 )
						{
							TableTreeNode tbtn = new TableTreeNode ( a_table_id, a_wb_id, a_table_name ,a_table_purpose , a_access);
							tables.add(tbtn);
							currentTable = tbtn;
						}
				}
				else
				{
					 if ( currentTable.getId() == a_table_id )
					 {
							int currentAccess = currentTable.getAccess();
							int newAccess = currentAccess | a_access;
							currentTable.setAccess( newAccess );
					 }
				}

			}


        }
        catch(SQLException sqlexception) {

            throw new SystemException(sqlexception);
        }
        finally {
            try {

				if ( resultset != null )
				{
                	resultset.close();
				}

				if ( preparedstatement1 != null )
				{
                	preparedstatement1.close();
				}


            }
            catch(SQLException sqlexception1) {
                throw new SystemException(sqlexception1);
            }
        }
        return tables;


    }

	public static Vector getLatestCellsForTable(Connection connection, int tableId, int userId, int memberId, int nhId, String viewPref)
	throws SystemException
	{
        ResultSet resultset = null;
        PreparedStatement preparedstatement1 = null;
        Vector cells = null;
		TableTreeNode currentTable = null;
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        try {

			preparedstatement1 = connection.prepareStatement(CALL_BW_GET_TBL_L);

			preparedstatement1.setInt(1,tableId);
			preparedstatement1.setInt(2,userId);
			preparedstatement1.setInt(3,memberId);
			preparedstatement1.setInt(4,nhId);
			preparedstatement1.setString(5,viewPref);

            resultset = preparedstatement1.executeQuery();
			cells = new Vector (resultset.getFetchSize());
            while ( resultset.next() )
			{
                int 	a_rowid;
                int     a_columnid;
                int     a_cellid;
                String  a_cellstringvalue;
                int     a_cellintvalue;
                int     a_celltablevalue = -1;
                double  a_celldoublevalue;
                int tid = -1;
                int userid = -1;
                String emailAddress = "";
                String description = "";
                String comment = "";
                String a_celltableName = "Not Set";
                String a_cellFormula = null;

				a_rowid = resultset.getInt("ROW_ID");
                a_columnid = resultset.getInt("COLUMN_ID");
                a_cellid = resultset.getInt("CELL_ID");
                a_cellstringvalue = resultset.getString("STRING_VALUE");
                a_cellintvalue = resultset.getInt("CELL_INTEGER_VALUE");
                a_celldoublevalue = resultset.getDouble("CELL_DOUBLE_VALUE");
                tid = resultset.getInt("TX_ID");
				userid = resultset.getInt("TX_CREATED_BY");

				java.sql.Timestamp a_createdOnDate = resultset.getTimestamp("CREATED_ON", cal);
				description = resultset.getString("DESCRIPTION");
				comment = resultset.getString("COMMENT_");
				emailAddress = resultset.getString("EMAIL_ADDRESS");
				//if (getLatest == true || getTableAsOf == true || getTableDelta == true)
				//{
					a_cellFormula = resultset.getString ("FORMULA");
					//System.out.println("Got cell with formula " + a_cellFormula);
				//}
				Transaction cellTransaction = new Transaction(tid, userid, emailAddress, a_createdOnDate.getTime(),description, comment );
				String  a_celltype;
				String a_columnname;
				a_celltype = "STRING";
				a_columnname = "";

                VersionedCell cl=  new VersionedCell
													(
														a_cellid,
														a_columnid,
														a_columnname ,
														a_rowid,
														a_celltype,
														a_cellstringvalue,
														a_cellintvalue,
														a_celldoublevalue,
														a_celltablevalue,
														a_celltableName,
														cellTransaction,
														a_cellFormula
													) ;
				cells.addElement(cl);
			}


        }
        catch(SQLException sqlexception) {

            throw new SystemException(sqlexception);
        }
        finally {
            try {

				if ( resultset != null )
				{
                	resultset.close();
				}

				if ( preparedstatement1 != null )
				{
                	preparedstatement1.close();
				}


            }
            catch(SQLException sqlexception1) {
                throw new SystemException(sqlexception1);
            }
        }

        return cells;
	}
	public static TableContents getTableContents( Connection connection,
													int a_tableid ,
													int a_userid,
													int memberId,
													int a_userNhId,
													int a_baselineid,
													String ViewPreference,
													String QueryPreference,
													Vector selectedNhIds,
													boolean	queryOnChildrenOfNh,
													double rowStartSeqId,
													double rowEndSeqId,
													double colStartSeqId,
													double colEndSeqId
													)
    throws SQLException, SystemException,BoardwalkException
    {
		return getTableContents_t ( connection,
									a_tableid ,
									a_userid,
									memberId,
									a_userNhId,
									a_baselineid,
									ViewPreference,
									QueryPreference,
									selectedNhIds,
									queryOnChildrenOfNh,
									rowStartSeqId,
									rowEndSeqId,
									colStartSeqId,
									colEndSeqId,
									-1,
									-1,
									-1,
									-1,
									null
									);
	}
	public static TableContents getTableContents_t( Connection connection,
													int a_tableid ,
													int a_userid,
													int memberId,
													int a_userNhId,
													int a_baselineid,
													String ViewPreference,
													String QueryPreference,
													Vector selectedNhIds,
													boolean	queryOnChildrenOfNh,
													double rowStartSeqId,
													double rowEndSeqId,
													double colStartSeqId,
													double colEndSeqId,
													int asOfTid,
													long asOfDate,
													int compTid,
													long compDate,
													String requestedColumns
													)
    throws SQLException, SystemException,BoardwalkException
    {
		System.out.println("********* Inside getTableContents_t ************");

		// flags which decide the sql query to run

		int rowState = 1;
		int colState = 1;

		boolean getDesignValues = false;
		boolean getLatest = false;
		boolean getLatestRowsCreatedByUser= false;
		boolean getAllLatestRowsCreatedByUserSortByUser= false;
		boolean getLatestByUser= false;
		boolean getLatestByAllUsers= false;
		boolean getLookupTable = false;
		boolean getBaseline = false;
		boolean getTableAsOf = false;
		boolean getTableDelta = false;

		// Rows based
		boolean getLatestRowsCreatedBySpecificNh= false;
		boolean getLatestRowsCreatedByAllChildrenNh= false;
		boolean getRowsCreatedByMyNh= false;
		boolean getRowsCreatedByMyNhAndImmediateChildren= false;
		boolean getRowsCreatedByMyNhAndAllChildren= false;

		// table based
		boolean getLatestBySpecificNh = false;
		boolean getLatestBySpecificChildrenNh = false;

		TableAccessList tbl = null;

		long prevTime = System.currentTimeMillis();

		//System.out.println("requested columns = " + requestedColumns);


		//System.out.println("ViewPreference=" + ViewPreference+ ":");
		//System.out.println("QueryPreference=" + QueryPreference+ ":");

		tbl = getTableAccessForMember( connection, memberId, a_tableid );

		System.out.println("Time(sec) to getTableAccess in getTableContents = " + (System.currentTimeMillis()-prevTime)/1000F);
		prevTime = System.currentTimeMillis();

		// System.out.println("getTableAccessForMember returned the following access for memberId " + memberId );

		if ( ViewPreference.equals("") )
		{

			// return deafult view from the access from db
			if ( a_baselineid > -1  )
			{
				getBaseline = true;
				//System.out.println("setting getBaseline = true;");
			}

			if ( tbl.canReadLatestOfTable()  || tbl.canWriteLatestOfTable()  )
			{
				getLatest = true;
				//System.out.println("setting getLatest = true;");
			}
			else if (  tbl.canReadLatestofMyGroup()  )
			{
				getRowsCreatedByMyNh = true;
				//System.out.println("setting getLatestRowsCreatedByUser = true;");
			}
			else if (  tbl.canReadLatestofMyGroupAndImmediateChildren()  )
			{
				getRowsCreatedByMyNhAndImmediateChildren = true;
				//System.out.println("setting getLatestRowsCreatedByUser = true;");
			}
			else if (  tbl.canReadLatestofMyGroupAndAllChildren()  )
			{
				getRowsCreatedByMyNhAndAllChildren = true;
				//System.out.println("setting getLatestRowsCreatedByUser = true;");
			}
			else if (  tbl.canReadWriteLatestOfMyRows()  )
			{
				getLatestRowsCreatedByUser = true;
				//System.out.println("setting getLatestRowsCreatedByUser = true;");
			}
			else if ( tbl.canReadWriteOnMyLatestView()  )
			{
				getLatestByUser = true;
				//System.out.println("setting getLatestByUser = true;");
			}
			else if ( tbl.canReadLatestViewOfAll()  || tbl.canReadLatestViewOfAllChildren()  )
			{
				getLatestByAllUsers = true;
				//System.out.println("setting getLatestByAllUsers = true;");
			}
			else if ( tbl.canAdministerTable() || tbl.canAdministerColumn()   )
			{
				getDesignValues = true;
				//System.out.println("setting getDesignValues = true;");
			}

		}
		else
		{
			TableAccessRequest tbareq = new TableAccessRequest( a_tableid, ViewPreference,false);
			int requestedACL = tbareq.getACL();
			int ACLFromDB = tbl.getACL();

			//System.out.println(" Requested acl " );
			//tbareq.print();
			//System.out.println(" ACLFromDB " );
			//tbl.print();


			int allowedACL = requestedACL & ACLFromDB;
			if ( allowedACL == requestedACL )
			{
				//System.out.println("User has requested access");
					//user has requested access
				if ( a_baselineid > -1  )
				{
					getBaseline = true;
					//System.out.println("setting getBaseline = true;");
				}
				if (asOfTid > -1 || asOfDate > -1)
				{
					if (compTid > -1 || compDate > -1)
					{
						getTableDelta = true;
					}
					else
					{
						getTableAsOf = true;
					}
				}
				else if (compTid > -1 || compDate > -1)
				{

					getTableDelta = true;

				}
				else if ( ViewPreference.equals(ViewPreferenceType.LATEST) )
				{
					if ( QueryPreference != null && ! QueryPreference.trim().equals("")   )
					{
						if ( QueryPreference.equals(QueryPreferenceType.ROWS_BY_USER) )
						{
							getAllLatestRowsCreatedByUserSortByUser = true;
						    //System.out.println("setting getAllLatestRowsCreatedByUserSortByUser = true;");

						}
						else
						if ( QueryPreference.equals(QueryPreferenceType.ROWS_BY_ROW_SEQ_ID) )
						{
							getLatest = true;
							//System.out.println("setting getLatest = true;");
						}
					}
					else
					{
						getLatest = true;
						//System.out.println("setting getLatest = true;");
					}
				}
				else
				if ( ViewPreference.equals(ViewPreferenceType.LATEST_ROWS_OF_ALL_USERS_IN_MY_NH  ))
				{
					getRowsCreatedByMyNh = true;
					System.out.println("setting getRowsCreatedByMyNh = true");

				}
				else
				if ( ViewPreference.equals(ViewPreferenceType.LATEST_ROWS_OF_ALL_USERS_IN_MY_NH_AND_IMM_CHD))
				{
					getRowsCreatedByMyNhAndImmediateChildren = true;
					System.out.println("setting getRowsCreatedByMyNhAndImmediateChildren = true;");

				}
				else
				if ( ViewPreference.equals(ViewPreferenceType.LATEST_ROWS_OF_ALL_USERS_IN_MY_NH_AND_ALL_CHD))
				{
					getRowsCreatedByMyNhAndAllChildren = true;
					System.out.println("setting getRowsCreatedByMyNhAndAllChildren = true;;");
				}
				else
				if ( ViewPreference.equals(ViewPreferenceType.LATEST_ROWS_OF_ALL_USERS_IN_ANY_NH)  && queryOnChildrenOfNh )
				{
					getLatestRowsCreatedByAllChildrenNh = true;
				 System.out.println("setting getLatestRowsCreatedByAllChildrenNh = true;");

				}
				else
				if ( ViewPreference.equals(ViewPreferenceType.LATEST_ROWS_OF_ALL_USERS_IN_ANY_NH)  && !queryOnChildrenOfNh )
				{
					getLatestRowsCreatedBySpecificNh = true;
					 System.out.println("setting getLatestRowsCreatedBySpecificNh = true;");
				}
				else
				if ( ViewPreference.equals(ViewPreferenceType.MY_ROWS))
				{
					getLatestRowsCreatedByUser= true;
					 System.out.println("setting getLatestRowsCreatedByUser = true;");
				}
				else
				if ( ViewPreference.equals(ViewPreferenceType.LATEST_BY_USER))
				{
					getLatestByUser = true;
					 System.out.println("setting getLatestByUser = true;");
				}
				else
				if ( ViewPreference.equals(ViewPreferenceType.LATEST_VIEW_OF_ALL_USERS))
				{
					getLatestByAllUsers = true;
					 System.out.println("setting getLatestByAllUsers = true;");

				}
				else
				if ( ViewPreference.equals(ViewPreferenceType.LATEST_VIEW_OF_ALL_CHILDREN))
				{
					getLatestByAllUsers = true;
					 System.out.println("setting getLatestByAllUsers = true;");
				}
				else
				if ( ViewPreference.equals(ViewPreferenceType.LATEST_VIEW_OF_ALL_USERS_IN_ANY_NH)  && queryOnChildrenOfNh )
				{
					getLatestBySpecificChildrenNh = true;
					 System.out.println("setting getLatestBySpecificChildrenNh = true;");

				}
				else
				if ( ViewPreference.equals(ViewPreferenceType.LATEST_VIEW_OF_ALL_USERS_IN_ANY_NH)  && !queryOnChildrenOfNh )
				{
					getLatestBySpecificNh = true;
					 System.out.println("setting getLatestBySpecificNh = true;");

				}

				else
				if ( ViewPreference.equals(ViewPreferenceType.LATEST_VIEW_OF_ALL_USERS_IN_ANY_CHILDREN_NH)  && !queryOnChildrenOfNh )
				{
					getLatestBySpecificNh = true;
					 System.out.println("setting getLatestBySpecificNh = true;");

				}
				else
				if ( ViewPreference.equals(ViewPreferenceType.LATEST_VIEW_OF_ALL_USERS_IN_ANY_CHILDREN_NH)  && queryOnChildrenOfNh )
				{
					getLatestBySpecificChildrenNh = true;
					System.out.println("setting getLatestBySpecificChildrenNh = true;");

				}
				else
				if ( ViewPreference.equals(ViewPreferenceType.LOOKUP))
				{
					getLookupTable = true;
					 System.out.println("setting getLookupTable = true;");

				}
				else
				if ( ViewPreference.equals(ViewPreferenceType.DESIGN)  )
				{
					getDesignValues = true;
					 System.out.println("setting getDesignValues = true;");
				}

				}
			else
			{
				// user doesn't have requested access send back an error
				 System.out.println("No access to the table for this user ");
				throw new BoardwalkException(10005);
			}
		}

		System.out.println("Time(sec) to determine view preference = " + (System.currentTimeMillis()-prevTime)/1000F);
		prevTime = System.currentTimeMillis();

		ResultSet resultset1 = null;
		PreparedStatement preparedstatement1 = null;
		boolean isResultsetCloseSuccesfully = false;
		TableContents tbc = null;

		Vector columnNames = new Vector();
		Hashtable cellsbyrowids = new Hashtable();
		Vector tableCells = new Vector();


		//System.out.println("Invoking getColumnsByTable " );

		TableColumnInfo tbcolInfo = ColumnManager.getTableColumnInfo(
																connection,
																a_tableid,
																a_baselineid,
																a_userid,
																memberId,
																asOfTid,
																requestedColumns);
		System.out.println("tbcolInfo--------------->  "+tbcolInfo);
		System.out.println("Time(sec) to get columns for the table = " + (System.currentTimeMillis()-prevTime)/1000F);
		prevTime = System.currentTimeMillis();
		Hashtable existingCols  = tbcolInfo.getColumnHash();
		Vector columns  = tbcolInfo.getColumnVector();
		Hashtable columnIdToIndex = new Hashtable();
		Vector CellHolder = new Vector(columns.size());

		if ( columns.size() > 0 )
		{
			for ( int cid = 0; cid < columns.size(); cid++)
			{
				Column colObj =(Column) columns.elementAt(cid);
				columnNames.addElement(colObj.getColumnName());
				columnIdToIndex.put(new Integer(colObj.getId()), new Integer(cid) );
				CellHolder.addElement(new Integer(0));
			}
		}
		System.out.println("Time(sec) to put columns in data struct = " + (System.currentTimeMillis()-prevTime)/1000F);
		prevTime = System.currentTimeMillis();
		//System.out.println("Invoking getTableRows " );
		TableRowInfo tbrowInfo = null;
		if (getTableAsOf == true)
		{
			tbrowInfo = RowManager.getTableRowsT( connection,
													a_tableid,
													a_userid,
													a_userNhId,
													a_baselineid,
													ViewPreference,
													rowStartSeqId,
													rowEndSeqId,
													asOfTid,
													asOfDate
													);
		System.out.println("<---------------tbrowInfo--------------->  "+tbrowInfo);
		}
		else if (getTableDelta == true)
		{
			// get only the rows that existed in the earlier tid
			// so that we can compare creation/deactivation of rows
			// based on the existence of rows in this set
			tbrowInfo = RowManager.getTableRowsT( connection,
													a_tableid,
													a_userid,
													a_userNhId,
													a_baselineid,
													ViewPreference,
													rowStartSeqId,
													rowEndSeqId,
													compTid,
													compDate
													);
		System.out.println("tbrowInfo else if--------------->  "+tbrowInfo);
		}
		else
		{
			tbrowInfo = RowManager.getTableRows(connection, a_tableid, a_userid, a_userNhId, a_baselineid, ViewPreference, rowState, rowStartSeqId, rowEndSeqId);
			//System.out.println("tbrowInfo else --------------->  "+tbrowInfo);
		}

		System.out.println("Time(sec) to get rows for the table = " + (System.currentTimeMillis()-prevTime)/1000F);
		prevTime = System.currentTimeMillis();

		Vector rowVector  = tbrowInfo.getRowVector();
		Hashtable  RowObjsByRowId = tbrowInfo.getRowHash();
		Vector rowNames = new Vector();
		//System.out.println("Done getRowsByTable got no of rows = "+ rowVector.size() );
		Vector rowIds = new Vector();

		if ( rowVector.size() > 0 )
		{
			for ( int rid = 0; rid < rowVector.size(); rid++)
			{

				Row rowObj =(Row) rowVector.elementAt(rid);
				rowNames.addElement(rowObj.getName());
				Integer rowId = new Integer(rowObj.getId());
				rowIds.addElement(rowId);
				cellsbyrowids.put(rowId, new Vector() ); // this is a vector that holds a vector of cells
				//((Vector)cellsbyrowids.get(rowId)).add((Vector)CellHolder.clone()); // this one holds the cells
				Vector cvtest = (Vector)cellsbyrowids.get(rowId);
				//System.out.println("Added cv for row " + rowId);
			}
		}

		System.out.println("Time(sec) to create row structures = " + (System.currentTimeMillis()-prevTime)/1000F);
		prevTime = System.currentTimeMillis();

/////////////
		try
		{
			if ( getAllLatestRowsCreatedByUserSortByUser   )
			{
				if ( DatabaseLoader.getDatabaseType().trim().equalsIgnoreCase("ORACLE"))
				{
					System.out.println("Invoking CALL_BW_GET_ROWCELLS_BY_USER with values " + a_tableid );
					preparedstatement1 = DatabaseLoader.getPreparedStatementFromPreLoadedQueries("BW_GET_ROWCELLS_BY_USER", connection );
					preparedstatement1.setInt(1,a_tableid);
					preparedstatement1.setInt(2,a_tableid);

				}
				else
				{
					if ( getBaseline == false )
					{
							preparedstatement1 = connection.prepareStatement(CALL_BW_GET_TBL);
							preparedstatement1.setInt(1,a_tableid);
							preparedstatement1.setInt(2,rowState);
							preparedstatement1.setDouble(3,rowStartSeqId);
							preparedstatement1.setDouble(4,rowEndSeqId);
							preparedstatement1.setInt(5,colState);
							preparedstatement1.setDouble(6,colStartSeqId);
							preparedstatement1.setDouble(7,colEndSeqId);
							preparedstatement1.setInt(8,a_userid);
							preparedstatement1.setInt(9,memberId);
					}
					else
					{
							preparedstatement1 = connection.prepareStatement(CALL_BW_GET_TBL_BL);
							preparedstatement1.setInt(1,a_tableid);
							preparedstatement1.setDouble(2,rowStartSeqId);
							preparedstatement1.setDouble(3,rowEndSeqId);
							preparedstatement1.setDouble(4,colStartSeqId);
							preparedstatement1.setDouble(5,colEndSeqId);
							preparedstatement1.setInt(6,a_baselineid);
							preparedstatement1.setInt(7,a_userid);
							preparedstatement1.setInt(8,memberId);
					}
				}
			}

			if ( getLatestByUser)
			{
				System.out.println("Invoking CALL_BW_GET_ROWCELLS_BY_TBL_AND_U with values " + a_tableid +" " +  a_userid );
				if ( DatabaseLoader.getDatabaseType().trim().equalsIgnoreCase("ORACLE"))
				{
					preparedstatement1 = DatabaseLoader.getPreparedStatementFromPreLoadedQueries("BW_GET_ROWCELLS_BY_TBL_AND_U", connection );
					preparedstatement1.setInt(1,a_tableid);
					preparedstatement1.setInt(2,a_userid);
					preparedstatement1.setInt(3,a_userid);
					preparedstatement1.setInt(4,a_tableid);
					preparedstatement1.setInt(5,a_userid);
					preparedstatement1.setInt(6,a_userid);
					preparedstatement1.setInt(7,a_tableid);
					preparedstatement1.setInt(8,a_userid);
					preparedstatement1.setInt(9,a_userid);
					preparedstatement1.setInt(10,a_tableid);
					preparedstatement1.setInt(11,a_tableid);
					preparedstatement1.setInt(12,a_userid);
					preparedstatement1.setInt(13,a_tableid);
					preparedstatement1.setInt(14,a_tableid);
					preparedstatement1.setInt(15,a_userid);
					preparedstatement1.setInt(16,a_tableid);
					preparedstatement1.setInt(17,a_tableid);
					preparedstatement1.setInt(18,a_userid);

				}
				else
				{
					if ( getBaseline== false )
					{
							preparedstatement1 = connection.prepareStatement(CALL_BW_GET_USER_TBL);
							preparedstatement1.setInt(1,a_tableid);
							preparedstatement1.setInt(2,a_userid);
							preparedstatement1.setInt(3,rowState);
							preparedstatement1.setInt(4,colState);
					}
					else
					{
							preparedstatement1 = connection.prepareStatement(CALL_BW_GET_TBL_BL);
							preparedstatement1.setInt(1,a_tableid);
							preparedstatement1.setDouble(2,rowStartSeqId);
							preparedstatement1.setDouble(3,rowEndSeqId);
							preparedstatement1.setDouble(4,colStartSeqId);
							preparedstatement1.setDouble(5,colEndSeqId);
							preparedstatement1.setInt(6,a_baselineid);
							preparedstatement1.setInt(7,a_userid);
							preparedstatement1.setInt(8,memberId);
					}

				}
			}
			if ( getRowsCreatedByMyNh)
			{
				if ( DatabaseLoader.getDatabaseType().trim().equalsIgnoreCase("ORACLE"))
				{
					preparedstatement1 = DatabaseLoader.getPreparedStatementFromPreLoadedQueries("BW_GET_RCELLS_BY_USRS_OF_NH", connection );
					preparedstatement1.setInt(1,a_tableid);
					preparedstatement1.setInt(2,a_tableid);
					preparedstatement1.setInt(3,a_userNhId);
				}
				else
				{
					if ( getBaseline==false )
					{
							preparedstatement1 = connection.prepareStatement(CALL_BW_GET_TBL_FOR_NH);
							preparedstatement1.setInt(1,a_tableid);
							preparedstatement1.setInt(2,a_userNhId);
							preparedstatement1.setInt(3,rowState);
							preparedstatement1.setDouble(4,rowStartSeqId);
							preparedstatement1.setDouble(5,rowEndSeqId);
							preparedstatement1.setInt(6,colState);
							preparedstatement1.setDouble(7,colStartSeqId);
							preparedstatement1.setDouble(8,colEndSeqId);
							preparedstatement1.setInt(9,a_userid);
							preparedstatement1.setInt(10,memberId);
					}
					else
					{
							preparedstatement1 = connection.prepareStatement(CALL_BW_GET_TBL_BL_FOR_NH);
							preparedstatement1.setInt(1,a_tableid);
							preparedstatement1.setInt(2,a_userNhId);
							preparedstatement1.setDouble(3,rowStartSeqId);
							preparedstatement1.setDouble(4,rowEndSeqId);
							preparedstatement1.setDouble(5,colStartSeqId);
							preparedstatement1.setDouble(6,colEndSeqId);
							preparedstatement1.setInt(7,a_baselineid);
							preparedstatement1.setInt(8,a_userid);
							preparedstatement1.setInt(9,memberId);
					}
				}
			}
			if ( getRowsCreatedByMyNhAndImmediateChildren)
			{
					if ( getBaseline==false)
					{
						preparedstatement1 = connection.prepareStatement(CALL_BW_GET_TBL_FOR_NH_I_CHD);
						preparedstatement1.setInt(1,a_tableid);
						preparedstatement1.setInt(2,a_userNhId);
						preparedstatement1.setInt(3,rowState);
						preparedstatement1.setDouble(4,rowStartSeqId);
						preparedstatement1.setDouble(5,rowEndSeqId);
						preparedstatement1.setInt(6,colState);
						preparedstatement1.setDouble(7,colStartSeqId);
						preparedstatement1.setDouble(8,colEndSeqId);
						preparedstatement1.setInt(9,a_userid);
						preparedstatement1.setInt(10,memberId);

					}
					else
					{
							preparedstatement1 = connection.prepareStatement(CALL_BW_GET_TBL_BL_FOR_NH_I_CHD);
							preparedstatement1.setInt(1,a_tableid);
							preparedstatement1.setInt(2,a_userNhId);
							preparedstatement1.setDouble(3,rowStartSeqId);
							preparedstatement1.setDouble(4,rowEndSeqId);
							preparedstatement1.setDouble(5,colStartSeqId);
							preparedstatement1.setDouble(6,colEndSeqId);
							preparedstatement1.setInt(7,a_baselineid);
							preparedstatement1.setInt(8,a_userid);
							preparedstatement1.setInt(9,memberId);

					}



			}
			if ( getRowsCreatedByMyNhAndAllChildren)
			{
					if ( getBaseline==false)
					{
							preparedstatement1 = connection.prepareStatement(CALL_BW_GET_TBL_FOR_NH_A_CHD);
							preparedstatement1.setInt(1,a_tableid);
							preparedstatement1.setInt(2,a_userNhId);
							preparedstatement1.setInt(3,rowState);
							preparedstatement1.setDouble(4,rowStartSeqId);
							preparedstatement1.setDouble(5,rowEndSeqId);
							preparedstatement1.setInt(6,colState);
							preparedstatement1.setDouble(7,colStartSeqId);
							preparedstatement1.setDouble(8,colEndSeqId);
							preparedstatement1.setInt(9,a_userid);
							preparedstatement1.setInt(10,memberId);
					}
					else
					{
							preparedstatement1 = connection.prepareStatement(CALL_BW_GET_TBL_BL_FOR_NH_A_CHD);
							preparedstatement1.setInt(1,a_tableid);
							preparedstatement1.setInt(2,a_userNhId);
							preparedstatement1.setDouble(3,rowStartSeqId);
							preparedstatement1.setDouble(4,rowEndSeqId);
							preparedstatement1.setDouble(5,colStartSeqId);
							preparedstatement1.setDouble(6,colEndSeqId);
							preparedstatement1.setInt(7,a_baselineid);
							preparedstatement1.setInt(8,a_userid);
							preparedstatement1.setInt(9,memberId);
					}
			}
			if ( getLatestRowsCreatedByAllChildrenNh)
			{
				int queryNhId = -1;
				if ( ViewPreference.equals(ViewPreferenceType.LATEST_VIEW_OF_ALL_USERS_IN_ANY_NH))
				{
					queryNhId = ( (Integer) selectedNhIds.elementAt(0)).intValue();
				}
				else
				{
					queryNhId = a_userNhId;
				}



				if ( getBaseline == true )
				{
					preparedstatement1 = connection.prepareStatement(CALL_BW_GET_TBL_BL_FOR_NH_A_CHD);
					preparedstatement1.setInt(1,a_tableid);
					preparedstatement1.setInt(2,queryNhId);
					preparedstatement1.setDouble(3,rowStartSeqId);
					preparedstatement1.setDouble(4,rowEndSeqId);
					preparedstatement1.setDouble(5,colStartSeqId);
					preparedstatement1.setDouble(6,colEndSeqId);
					preparedstatement1.setInt(7,a_baselineid);
					preparedstatement1.setInt(8,a_userid);
					preparedstatement1.setInt(9,memberId);
					System.out.println("querying nh=" + queryNhId + " query = CALL_BW_GET_TBL_BL_FOR_NH_A_CHD " );
				}
				else
				{


						preparedstatement1 = connection.prepareStatement(CALL_BW_GET_TBL_FOR_NH_A_CHD);
						preparedstatement1.setInt(1,a_tableid);
						preparedstatement1.setInt(2,queryNhId);
						preparedstatement1.setInt(3,rowState);
						preparedstatement1.setDouble(4,rowStartSeqId);
						preparedstatement1.setDouble(5,rowEndSeqId);
						preparedstatement1.setInt(6,colState);
						preparedstatement1.setDouble(7,colStartSeqId);
						preparedstatement1.setDouble(8,colEndSeqId);
						preparedstatement1.setInt(9,a_userid);
						preparedstatement1.setInt(10,memberId);
						System.out.println("querying nh=" + queryNhId + " query = CALL_BW_GET_TBL_FOR_NH_A_CHD " );
					}


			}
			if ( getLatestRowsCreatedBySpecificNh)
			{
				int selNhId = ( (Integer) selectedNhIds.elementAt(0)).intValue();
				if ( DatabaseLoader.getDatabaseType().trim().equalsIgnoreCase("ORACLE"))
				{
					preparedstatement1 = DatabaseLoader.getPreparedStatementFromPreLoadedQueries("BW_GET_RCELLS_BY_USRS_OF_NH", connection );
					preparedstatement1.setInt(1,a_tableid);
					preparedstatement1.setInt(2,a_tableid);
					preparedstatement1.setInt(3,selNhId);
				}
				else
				{
					if ( getBaseline == false )
					{
						preparedstatement1 = connection.prepareStatement(CALL_BW_GET_TBL_FOR_NH);
						preparedstatement1.setInt(1,a_tableid);
						preparedstatement1.setInt(2,selNhId);
						preparedstatement1.setInt(3,rowState);
						preparedstatement1.setDouble(4,rowStartSeqId);
						preparedstatement1.setDouble(5,rowEndSeqId);
						preparedstatement1.setInt(6,colState);
						preparedstatement1.setDouble(7,colStartSeqId);
						preparedstatement1.setDouble(8,colEndSeqId);
						preparedstatement1.setInt(9,a_userid);
						preparedstatement1.setInt(10,memberId);
						System.out.println("querying nh=" + selNhId + " query = CALL_BW_GET_TBL_FOR_NH " );
					}
					else
					{
							preparedstatement1 = connection.prepareStatement(CALL_BW_GET_TBL_BL_FOR_NH);
							preparedstatement1.setInt(1,a_tableid);
							preparedstatement1.setInt(2,selNhId);
							preparedstatement1.setDouble(3,rowStartSeqId);
							preparedstatement1.setDouble(4,rowEndSeqId);
							preparedstatement1.setDouble(5,colStartSeqId);
							preparedstatement1.setDouble(6,colEndSeqId);
							preparedstatement1.setInt(7,a_baselineid);
							preparedstatement1.setInt(8,a_userid);
							preparedstatement1.setInt(9,memberId);
							System.out.println("querying nh=" + selNhId + " query = CALL_BW_GET_TBL_BL_FOR_NH " );
					}

				}
			}

			if ( getLatestRowsCreatedByUser  )
			{
				if ( DatabaseLoader.getDatabaseType().trim().equalsIgnoreCase("ORACLE"))
				{
					System.out.println("Invoking BW_GET_ROWCELLS_FOR_USER with values " + a_tableid +" " +  a_userid );
					preparedstatement1 = DatabaseLoader.getPreparedStatementFromPreLoadedQueries("BW_GET_ROWCELLS_FOR_USER", connection );
					preparedstatement1.setInt(1,a_tableid);
					preparedstatement1.setInt(2,a_tableid);
					preparedstatement1.setInt(3,a_userid);
				}
				else
				{
					if ( getBaseline == false )
					{
								preparedstatement1 = connection.prepareStatement(CALL_BW_GET_TBL_FOR_USER);
								preparedstatement1.setInt(1,a_tableid);
								preparedstatement1.setInt(2,a_userid);
								preparedstatement1.setInt(3,memberId);
								preparedstatement1.setInt(4,rowState);
								preparedstatement1.setDouble(5,rowStartSeqId);
								preparedstatement1.setDouble(6,rowEndSeqId);
								preparedstatement1.setInt(7,colState);
								preparedstatement1.setDouble(8,colStartSeqId);
								preparedstatement1.setDouble(9,colEndSeqId);
					}
					else
					{
							preparedstatement1 = connection.prepareStatement(CALL_BW_GET_TBL_BL_FOR_USER);
							preparedstatement1.setInt(1,a_tableid);
							preparedstatement1.setInt(2,a_userid);
							preparedstatement1.setDouble(3,rowStartSeqId);
							preparedstatement1.setDouble(4,rowEndSeqId);
							preparedstatement1.setDouble(5,colStartSeqId);
							preparedstatement1.setDouble(6,colEndSeqId);
							preparedstatement1.setInt(7,a_baselineid);
							preparedstatement1.setInt(8,memberId);


					}
				}
			}



			if ( getLatest )
			{
				if ( DatabaseLoader.getDatabaseType().trim().equalsIgnoreCase("ORACLE"))
				{
					preparedstatement1 = DatabaseLoader.getPreparedStatementFromPreLoadedQueries("BW_GET_ROWS_AND_CELLS_BY_TBL_ID", connection );
					preparedstatement1.setInt(1,a_tableid);
					preparedstatement1.setInt(2,a_tableid);

				}
				else
				{
					if ( getBaseline == false )
					{
						preparedstatement1 = connection.prepareStatement(CALL_BW_GET_TBL);
						preparedstatement1.setInt(1,a_tableid);
						preparedstatement1.setInt(2,rowState);
						preparedstatement1.setDouble(3,rowStartSeqId);
						preparedstatement1.setDouble(4,rowEndSeqId);
						preparedstatement1.setInt(5,colState);
						preparedstatement1.setDouble(6,colStartSeqId);
						preparedstatement1.setDouble(7,colEndSeqId);
						preparedstatement1.setInt(8,a_userid);
						preparedstatement1.setInt(9,memberId);
					}
					else
					{
						System.out.println(" Calling BW_GET_TBL_BL with tableId = " + a_tableid + " baselineId = " + a_baselineid + " userId = " + a_userid + " memberId = " + memberId);
						preparedstatement1 = connection.prepareStatement(CALL_BW_GET_TBL_BL);
						preparedstatement1.setInt(1,a_tableid);
						preparedstatement1.setDouble(2,rowStartSeqId);
						preparedstatement1.setDouble(3,rowEndSeqId);
						preparedstatement1.setDouble(4,colStartSeqId);
						preparedstatement1.setDouble(5,colEndSeqId);
						preparedstatement1.setInt(6,a_baselineid);
						preparedstatement1.setInt(7,a_userid);
						preparedstatement1.setInt(8,memberId);
					}
				}
			}

			if ( getBaseline )
			{
				if ( DatabaseLoader.getDatabaseType().trim().equalsIgnoreCase("ORACLE"))
				{
					preparedstatement1 = DatabaseLoader.getPreparedStatementFromPreLoadedQueries("BW_GET_TBL_CONTENTS_BY_BL", connection );
					preparedstatement1.setInt(1,a_tableid);
					preparedstatement1.setInt(2,a_tableid);
					preparedstatement1.setInt(3,a_baselineid);
					preparedstatement1.setInt(4,a_baselineid);
					preparedstatement1.setInt(5,a_baselineid);
					preparedstatement1.setInt(6,a_tableid);
					preparedstatement1.setInt(7,a_tableid);
					preparedstatement1.setInt(8,a_baselineid);
					preparedstatement1.setInt(9,a_baselineid);
					preparedstatement1.setInt(10,a_baselineid);
					preparedstatement1.setInt(11,a_tableid);
					preparedstatement1.setInt(12,a_tableid);
					preparedstatement1.setInt(13,a_baselineid);
					preparedstatement1.setInt(14,a_baselineid);
					preparedstatement1.setInt(15,a_baselineid);
				}
			}

			if ( getDesignValues )
			{
				if ( DatabaseLoader.getDatabaseType().trim().equalsIgnoreCase("ORACLE"))
				{
					preparedstatement1 = DatabaseLoader.getPreparedStatementFromPreLoadedQueries("BW_GET_DES_CELL_VALS_BY_TBL", connection );
					preparedstatement1.setInt(1,a_tableid);
					preparedstatement1.setInt(2,a_tableid);
				}
				else
				{
					// System.out.println("getting design values ");
					preparedstatement1 = connection.prepareStatement(CALL_BW_GET_TBL_DESIGN);
					preparedstatement1.setInt(1,a_tableid);
					preparedstatement1.setInt(2,rowState);
					preparedstatement1.setDouble(3,rowStartSeqId);
					preparedstatement1.setDouble(4,rowEndSeqId);
					preparedstatement1.setInt(5,colState);
					preparedstatement1.setDouble(6,colStartSeqId);
					preparedstatement1.setDouble(7,colEndSeqId);
					preparedstatement1.setInt(8,a_userid);
					preparedstatement1.setInt(9,memberId);
				}

			}

			if ( getLatestByAllUsers )
			{
				if ( getBaseline == false )
				{
					TableContents designTBC = getTableContents( connection,
																a_tableid ,
																a_userid,
																memberId,
																a_userNhId,
																a_baselineid,
																ViewPreferenceType.DESIGN,
																QueryPreference,
																selectedNhIds,
																queryOnChildrenOfNh,
																-1,
																10000000,
																-1,
																10000000
																);
					TableContents tbcByAllUsers =  getLatestTableContentsByAllUsers( connection, a_tableid, false, false, selectedNhIds, designTBC, existingCols, columns,columnNames,rowIds,rowNames,RowObjsByRowId);
					return tbcByAllUsers;
				}
				else
				{
					preparedstatement1 = connection.prepareStatement(CALL_BW_GET_TBL_BL);
					preparedstatement1.setInt(1,a_tableid);
					preparedstatement1.setDouble(2,rowStartSeqId);
					preparedstatement1.setDouble(3,rowEndSeqId);
					preparedstatement1.setDouble(4,colStartSeqId);
					preparedstatement1.setDouble(5,colEndSeqId);
					preparedstatement1.setInt(6,a_baselineid);
					preparedstatement1.setInt(7,a_userid);
					preparedstatement1.setInt(8,memberId);
				}

			}


			if ( getLatestBySpecificChildrenNh )
			{
				if ( getBaseline == false )
				{
				   TableContents designTBC = getTableContents( connection,
																a_tableid ,
																a_userid,
																memberId,
																a_userNhId,
																a_baselineid,
																ViewPreferenceType.DESIGN,
																QueryPreference,
																selectedNhIds,
																queryOnChildrenOfNh,
																-1,
																10000000,
																-1,
																10000000
																);


					TableContents tbcByAllUsers =  getLatestTableContentsByAllUsers( connection, a_tableid, getLatestBySpecificNh, getLatestRowsCreatedByAllChildrenNh, selectedNhIds, designTBC, existingCols, columns,columnNames,rowIds,rowNames,RowObjsByRowId);
					return tbcByAllUsers;
				}
				else
				{
						preparedstatement1 = connection.prepareStatement(CALL_BW_GET_TBL_BL);
						preparedstatement1.setInt(1,a_tableid);
						preparedstatement1.setDouble(2,rowStartSeqId);
						preparedstatement1.setDouble(3,rowEndSeqId);
						preparedstatement1.setDouble(4,colStartSeqId);
						preparedstatement1.setDouble(5,colEndSeqId);
						preparedstatement1.setInt(6,a_baselineid);
						preparedstatement1.setInt(7,a_userid);
						preparedstatement1.setInt(8,memberId);

				}
			}

			if ( getLatestBySpecificNh)
			{
				if ( getBaseline == false )
				{
					TableContents designTBC = getTableContents( connection,
																a_tableid ,
																a_userid,
																memberId,
																a_userNhId,
																a_baselineid,
																ViewPreferenceType.DESIGN,
																QueryPreference,
																selectedNhIds,
																queryOnChildrenOfNh,
																-1,
																10000000,
																-1,
																10000000
																);
					TableContents tbcByAllUsers =  getLatestTableContentsByAllUsers( connection, a_tableid, getLatestBySpecificNh, getLatestRowsCreatedByAllChildrenNh,  selectedNhIds, designTBC, existingCols, columns,columnNames,rowIds,rowNames,RowObjsByRowId);
					return tbcByAllUsers;
				}
				else
				{
					preparedstatement1 = connection.prepareStatement(CALL_BW_GET_TBL_BL);
					preparedstatement1.setInt(1,a_tableid);
					preparedstatement1.setDouble(2,rowStartSeqId);
					preparedstatement1.setDouble(3,rowEndSeqId);
					preparedstatement1.setDouble(4,colStartSeqId);
					preparedstatement1.setDouble(5,colEndSeqId);
					preparedstatement1.setInt(6,a_baselineid);
					preparedstatement1.setInt(7,a_userid);
					preparedstatement1.setInt(8,memberId);

				}


			}

			if (getTableDelta == true)
			{
				System.out.println("getting table delta start " +
									compTid + ":" + new java.util.Date(compDate) +
									asOfTid + ":" + new java.util.Date(asOfDate));

				String CALL_BW_GET_TBL_DELTA="{CALL BW_GET_TBL_DELTA(?,?,?,?,?,?,?,?,?,?,?,?)}";
			/*
				@TABLE_ID INTEGER,
				@STX_ID INTEGER,
				@ETX_ID INTEGER,
				@START_ROW_SEQ_ID [FLOAT],
				@END_ROW_SEQ_ID [FLOAT],
				@START_COL_SEQ_ID [FLOAT],
				@END_COL_SEQ_ID [FLOAT],
				@USER_ID int,
				@MEMBER_ID int,
				@NH_ID int,
				@VIEW_PREF varchar(256)
			*/

				Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
				preparedstatement1 = connection.prepareStatement(CALL_BW_GET_TBL_DELTA);
				preparedstatement1.setInt(1,a_tableid);
				preparedstatement1.setInt(2,compTid);
				if (compDate > -1)
				{
					preparedstatement1.setTimestamp(3, new java.sql.Timestamp(compDate), cal);
				}
				else
				{
					preparedstatement1.setTimestamp(3, new java.sql.Timestamp((new java.util.Date()).getTime()), cal);
				}
				preparedstatement1.setInt(4,asOfTid);
				if (asOfDate > -1)
				{
					preparedstatement1.setTimestamp(5, new java.sql.Timestamp(asOfDate), cal);
				}
				else
				{
					preparedstatement1.setTimestamp(5, new java.sql.Timestamp((new java.util.Date()).getTime()), cal);
				}
				preparedstatement1.setDouble(6,rowStartSeqId);
				preparedstatement1.setDouble(7,rowEndSeqId);
				preparedstatement1.setString(8,requestedColumns);
				preparedstatement1.setInt(9,a_userid);
				preparedstatement1.setInt(10,memberId);
				preparedstatement1.setInt(11,a_userNhId);
				preparedstatement1.setString(12,ViewPreference);

			}

			if (getTableAsOf == true)
			{
				String CALL_BW_GET_TBL_T="{CALL BW_GET_TBL_T(?,?,?,?,?,?,?,?,?,?)}";
			/*
				@TABLE_ID INTEGER,
				@TX_ID INTEGER,
				@START_ROW_SEQ_ID [FLOAT],
				@END_ROW_SEQ_ID [FLOAT],
				@START_COL_SEQ_ID [FLOAT],
				@END_COL_SEQ_ID [FLOAT],
				@USER_ID int,
				@MEMBER_ID int,
				@NH_ID int,
				@VIEW_PREF varchar(256)
			*/
				Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
				preparedstatement1 = connection.prepareStatement(CALL_BW_GET_TBL_T);
				preparedstatement1.setInt(1,a_tableid);
				preparedstatement1.setInt(2,asOfTid);
				if (asOfDate > -1)
				{
					preparedstatement1.setTimestamp(3, new java.sql.Timestamp(asOfDate), cal);
				}
				else
				{
					preparedstatement1.setTimestamp(3, new java.sql.Timestamp((new java.util.Date()).getTime()), cal);
				}
				preparedstatement1.setDouble(4,rowStartSeqId);
				preparedstatement1.setDouble(5,rowEndSeqId);
				preparedstatement1.setString(6,requestedColumns);
				preparedstatement1.setInt(7,a_userid);
				preparedstatement1.setInt(8,memberId);
				preparedstatement1.setInt(9,a_userNhId);
				preparedstatement1.setString(10,ViewPreference);


				System.out.println("Getting table as of " + asOfTid + ":" + asOfDate);
				System.out.println("3 " + asOfDate );
				System.out.println("3 " + new java.sql.Timestamp(asOfDate).getTime() );
				System.out.println("4 " + rowStartSeqId );
				System.out.println("5 " + rowEndSeqId);
				System.out.println("6 " + requestedColumns );
				System.out.println("7 " + a_userid );
				System.out.println("8 " + memberId );
				System.out.println("9 " + a_userNhId );


			}

			System.out.println("Time(sec) to select query to fetch cells = " + (System.currentTimeMillis()-prevTime)/1000F);
			prevTime = System.currentTimeMillis();

			//System.out.println("Invoking get cells " );

			if ( preparedstatement1 != null )
			{
            	resultset1 = preparedstatement1.executeQuery();
			}

			System.out.println("Time(sec) to execute query = " + (System.currentTimeMillis()-prevTime)/1000F);
			prevTime = System.currentTimeMillis();

            boolean isFirstRowDone = false;
            int     a_previousrowid = -1;
            int noOfCells = 0;
			Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));

            while ( resultset1!= null && resultset1.next() )
            {
                int 	a_rowid;
                int     a_columnid;
                int     a_cellid;
                String  a_cellstringvalue;
                int     a_cellintvalue;
                int     a_celltablevalue = -1;
                double  a_celldoublevalue;
                int tid = -1;
                int userid = -1;
                String emailAddress = "";
                String description = "";
                String comment = "";
                String a_celltableName = "Not Set";
                String a_cellFormula = null;

				a_rowid = resultset1.getInt("ROW_ID");
                a_columnid = resultset1.getInt("COLUMN_ID");
                a_cellid = resultset1.getInt("CELL_ID");
                a_cellstringvalue = resultset1.getString("CELL_STRING_VALUE");
                a_cellintvalue = resultset1.getInt("CELL_INTEGER_VALUE");
                a_celldoublevalue = resultset1.getDouble("CELL_DOUBLE_VALUE");
                tid = resultset1.getInt("TX_ID");
				userid = resultset1.getInt("TX_CREATED_BY");

				java.sql.Timestamp a_createdOnDate = resultset1.getTimestamp("CREATED_ON", cal);
				description = resultset1.getString("DESCRIPTION");
				comment = resultset1.getString("COMMENT_");
				emailAddress = resultset1.getString("EMAIL_ADDRESS");
				//if (getLatest == true || getTableAsOf == true || getTableDelta == true)
				//{
					a_cellFormula = resultset1.getString ("FORMULA");
					//System.out.println("Got cell with formula " + a_cellFormula);
				//}
				Transaction cellTransaction = new Transaction(tid, userid, emailAddress, a_createdOnDate.getTime(),description, comment );
				String  a_celltype;
				String a_columnname;
				a_celltype = ((Column)existingCols.get(new Integer(a_columnid))).getType();
				a_columnname = ((Column)existingCols.get(new Integer(a_columnid))).getColumnName();

                VersionedCell cl=  new VersionedCell
													(
														a_cellid,
														a_columnid,
														a_columnname ,
														a_rowid,
														a_celltype,
														a_cellstringvalue,
														a_cellintvalue,
														a_celldoublevalue,
														a_celltablevalue,
														a_celltableName,
														cellTransaction,
														a_cellFormula
													) ;
				noOfCells = noOfCells + 1;
				Integer a_rowIntegerId = new Integer(a_rowid);
				//System.out.println("got  a cell with row id "+ a_rowid);
				if ( (Vector)cellsbyrowids.get(a_rowIntegerId) != null )
				{
					Vector cv = (Vector)cellsbyrowids.get(a_rowIntegerId);
					//System.out.println("cv.size() = " + cv.size());
					if (cv.size() == 0)
					{
						//System.out.println("Adding placeholder cells for row " + a_rowIntegerId);
						cv.add((Vector)CellHolder.clone()); // this one holds the cells
					}

					//System.out.println("adding a cell" );
					//cl.printCell();
					Integer columnIndex = (Integer)columnIdToIndex.get(new Integer(a_columnid));
                	( (Vector) ((Vector)cellsbyrowids.get(a_rowIntegerId) ).elementAt(0) ).setElementAt(cl,columnIndex.intValue());
				}
				else
				{
					System.out.println("Error:: Vector not created for cells");
				}
            } // while result set

            //System.out.println("got  " + noOfCells  + " cells ");


			if ( rowIds.size() > 0 )
			{
				tbc = new TableContents( rowIds, rowNames, columnNames, cellsbyrowids, existingCols,  SourceType.TABLE,tbl,columns,RowObjsByRowId);
			}
			else
			{
				if ( existingCols.size() > 0 )
				{

					tbc = new TableContents( new Vector(), new Vector(), columnNames, new Hashtable(),existingCols ,  SourceType.TABLE,tbl,columns,RowObjsByRowId);
				 }
				else
				{
					tbc = new TableContents( new Vector(), new Vector(), new Vector(), new Hashtable(), new Hashtable(),  SourceType.TABLE,tbl,columns,RowObjsByRowId);
				}
			}
			System.out.println("Time(sec) to create the TableContents object = " + (System.currentTimeMillis()-prevTime)/1000F);
			prevTime = System.currentTimeMillis();
           return tbc;

        }
		catch(SQLException sqlexception)
		{
			System.out.println("sqlexception:::::::;;"+sqlexception.getErrorCode() );
			throw sqlexception;
		}
		finally
		{
			try
			{

				if ( resultset1 != null )
					resultset1.close();

				if ( preparedstatement1 != null )
					preparedstatement1.close();


			}
			catch(SQLException sqlexception1) {
				System.out.println("sqlexception:::::::;;"+sqlexception1.getErrorCode() );
				throw new SystemException(sqlexception1);
			}
		}

    }




private static TableContents getLatestTableContentsByAllUsers(
															  Connection connection,
															  int a_tableid ,
															  boolean getLatestBySpecificNh,
															  boolean getLatestRowsCreatedByAllChildrenNh,
															  Vector selectedNhIds,
															  TableContents designTBC,
															  Hashtable exisitngcolumns,
															  Vector columns,
															  Vector columnNames,
															  Vector rowIds,
															  Vector rowNames,
															  Hashtable RowObjsByRowId
														   )
    throws SQLException,SystemException
    {


		int rowState = 1;
		double rowStartSeqId = -1;
		double rowEndSeqId = 100000000;
		int colState = 1;
		double colStartSeqId = -1;
		double colEndSeqId = 100000000;


        ResultSet resultset1 = null;
        PreparedStatement preparedstatement1 = null;
        boolean isResultsetCloseSuccesfully = false;
        TableContents tbc = null;


        Hashtable cellsbyrowids = new Hashtable();
        Vector tableCells = new Vector();

		/// Default design Values
		Vector a_rowIds = designTBC.getRowIds();
		Vector a_rowNames = designTBC.getRowNames();
		Vector a_columnNames = designTBC.getColumnNames();
		Hashtable a_VectorsOfCellsByRowid = designTBC.getCellsByRowId();  // Rowid--> Vector(s) --> Vector of Cells Ordered by Columns
		Hashtable a_columns = designTBC.getColumnsByColumnId();

		Hashtable columnIdToIndexPosition = new Hashtable();
		for ( int c = 0; c < columns.size(); c++ )
		{
			Column col = (Column)( columns.elementAt(c) );
			columnIdToIndexPosition.put(new Integer(col.getId()), new Integer(c) );
		}



	try
		 {

						if ( !getLatestBySpecificNh && !getLatestRowsCreatedByAllChildrenNh )
						{
								preparedstatement1 = connection.prepareStatement(CALL_BW_GET_USER_TBL_ALL_USERS);
								preparedstatement1.setInt(1,a_tableid);
								preparedstatement1.setInt(2,rowState);
								preparedstatement1.setInt(3,colState);

						}
						else if ( getLatestBySpecificNh  )
						{
									int selNhId = ( (Integer) selectedNhIds.elementAt(0)).intValue();
									preparedstatement1 = connection.prepareStatement(CALL_BW_GET_USER_TBL_FOR_NH);
									preparedstatement1.setInt(1,a_tableid);
									preparedstatement1.setInt(2,selNhId);
									preparedstatement1.setInt(3,rowState);
									preparedstatement1.setInt(4,colState);
						}
						else if ( getLatestRowsCreatedByAllChildrenNh )
						{
								int selNhId = ( (Integer) selectedNhIds.elementAt(0)).intValue();
								preparedstatement1 = connection.prepareStatement(CALL_BW_GET_USER_TBL_FOR_NH_CHD);
								preparedstatement1.setInt(1,a_tableid);
								preparedstatement1.setInt(2,selNhId);
								preparedstatement1.setInt(3,rowState);
								preparedstatement1.setInt(4,colState);
						}




			 resultset1 = preparedstatement1.executeQuery();




			int     a_currentrowid = -1;
			int     a_currentuserId = -1;


			Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
			while ( resultset1.next() )
            {
                int 	a_rowid;
                int     a_columnid;
				String a_columnname;
                int     a_cellid;
                String  a_celltype;
                String  a_cellstringvalue;
                int     a_cellintvalue;
                double  a_celldoublevalue;
                int     a_celltablevalue =-1;
                String  a_celltableName = "Not Set";

                int tid = -1;
                int userid = -1;
                String emailAddress = "";
                String description = "";
                String comment = "";

				a_rowid = resultset1.getInt("ROW_ID");
                a_columnid = resultset1.getInt("COLUMN_ID");
                a_cellid = resultset1.getInt("CELL_ID");
                a_cellstringvalue = resultset1.getString("CELL_STRING_VALUE");
                a_cellintvalue = resultset1.getInt("CELL_INTEGER_VALUE");
                a_celldoublevalue = resultset1.getDouble("CELL_DOUBLE_VALUE");
				tid = resultset1.getInt("TX_ID");
				userid = resultset1.getInt("TX_CREATED_BY");
				java.sql.Timestamp a_createdOnDate = resultset1.getTimestamp("CREATED_ON",cal);
				description = resultset1.getString("DESCRIPTION");
				comment = resultset1.getString("COMMENT_");
				emailAddress = resultset1.getString("EMAIL_ADDRESS");

				Transaction cellTransaction = new Transaction(tid, userid, emailAddress, a_createdOnDate.getTime(), description, comment);

				Integer a_rowIntegerId = new Integer(a_rowid);

				// Start processing the data


				 if ( a_currentrowid == -1 )
				 {
						a_currentrowid = a_rowid;
						a_currentuserId = userid;
						cellsbyrowids.put(a_rowIntegerId, new Vector() );
                    	Vector defaultRowValues =(Vector)( (Vector)((Vector)a_VectorsOfCellsByRowid.get(a_rowIntegerId)).elementAt(0)).clone();
                   		((Vector)cellsbyrowids.get(a_rowIntegerId)).add(defaultRowValues);

				 }
				 else
				 if ( a_currentrowid != a_rowid  )
				{
					a_currentrowid = a_rowid;
					a_currentuserId = userid;
					cellsbyrowids.put(a_rowIntegerId, new Vector() );
					Vector defaultRowValues =(Vector)( (Vector)((Vector)a_VectorsOfCellsByRowid.get(a_rowIntegerId)).elementAt(0)).clone();
					((Vector)cellsbyrowids.get(a_rowIntegerId)).add(defaultRowValues);

			  }
			   else
			 if (  a_currentuserId != userid  )
			{

				a_currentrowid = a_rowid;
				a_currentuserId = userid;
				 Vector defaultRowValues =(Vector)( (Vector)((Vector)a_VectorsOfCellsByRowid.get(a_rowIntegerId)).elementAt(0)).clone();
				 ((Vector)cellsbyrowids.get(a_rowIntegerId)).add(defaultRowValues);
			}
					a_celltype = ((Column)exisitngcolumns.get(new Integer(a_columnid))).getType();
					a_columnname = ((Column)exisitngcolumns.get(new Integer(a_columnid))).getColumnName();
					VersionedCell cl=  new VersionedCell(a_cellid, a_columnid, a_columnname , a_rowid,
														a_celltype, a_cellstringvalue, a_cellintvalue,
														a_celldoublevalue, a_celltablevalue, a_celltableName,
														cellTransaction ,null ) ;
					int colIndex = ((Integer)columnIdToIndexPosition.get(new Integer(a_columnid) )).intValue();
					((Vector) ((Vector)cellsbyrowids.get(a_rowIntegerId) ).lastElement() ).set(colIndex, cl);
          }// end of while loop

		 if  ( cellsbyrowids.size() == 0  )
		{
			  return designTBC;
		 }
		 else
		 {
				tbc = new TableContents( a_rowIds, a_rowNames, a_columnNames, cellsbyrowids,a_columns ,  SourceType.TABLE,designTBC.getTableAccessList(),columns,RowObjsByRowId);
				return tbc;
		}



        }
        catch(SQLException sqlexception) {
            throw  sqlexception;
        }
        finally {
            try {

				if ( resultset1 != null )
                	resultset1.close();

                if ( preparedstatement1 != null )
                	preparedstatement1.close();


            }
            catch(SQLException sqlexception1) {
                throw new SystemException(sqlexception1);
            }
        }

    }


	public static HashMap getRowColumnCellsAtExportForUser(Connection connection, int a_tableid, int importTransactionId, int userId)
	throws SQLException,SystemException
	{
        ResultSet resultset1 = null;
        PreparedStatement preparedstatement1 = null;
        TableContents tbc = null;
        HashMap hrcc = null;
		float elapsedTimeSec;
		long t;

        try
        {
           	preparedstatement1 = connection.prepareStatement(CALL_BW_GET_RC_CELLS_AT_EXPORT);
			preparedstatement1.setInt(1,a_tableid);
			preparedstatement1.setInt(2,importTransactionId);
			preparedstatement1.setInt(3,userId);
            t = System.currentTimeMillis();
            //resultset1.setFetchSize(25);
            resultset1 = preparedstatement1.executeQuery();
            hrcc = new HashMap(resultset1.getFetchSize());
			elapsedTimeSec = (System.currentTimeMillis()-t )/1000F;
			System.out.println("Time to execute query CALL_BW_GET_RC_CELLS_AT_EXPORT = " + elapsedTimeSec);
			t = System.currentTimeMillis();
            boolean isFirstRowDone = false;
            int     a_previousrowid = -1;

            while ( resultset1.next() ) {
                int 	a_rowid;
                int     a_row_sequence_number;
                int     a_columnid;
                int     a_column_sequence_number;
                String  a_columnname;

                int     a_cellid;
                String  a_celltype = "STRING";
                String  a_cellstringvalue;
                int     a_cellintvalue = -1;
                double  a_celldoublevalue = 1.0;
                int     a_celltablevalue = -1;
                String  a_formula;

				a_cellid = resultset1.getInt("BW_CELL_ID");
				a_cellstringvalue = resultset1.getString("STRING_VALUE");
				a_rowid = resultset1.getInt("BW_ROW_ID");
				a_columnid = resultset1.getInt("BW_COLUMN_ID");
				a_formula = resultset1.getString("FORMULA");

                RowColumnCell rcc = new  RowColumnCell(a_rowid, a_columnid, a_celltype, a_cellstringvalue, a_cellintvalue, a_celldoublevalue, a_celltablevalue,a_formula );
                Cell cell = new Cell (a_cellid, a_columnid,  a_rowid, a_celltype, a_cellstringvalue, a_cellintvalue, a_celldoublevalue, a_celltablevalue, "", a_formula );
                hrcc.put(new String(""+a_rowid+":"+a_columnid), cell);
            }
        }
        catch(SQLException sqlexception) {
			sqlexception.printStackTrace();
            throw sqlexception;
        }
        finally {
            try {
                resultset1.close();
                preparedstatement1.close();


            }
            catch(SQLException sqlexception1) {
                throw new SystemException(sqlexception1);
            }
        }
		elapsedTimeSec = (System.currentTimeMillis()-t )/1000F;
		System.out.println("Time to create hashtable CALL_BW_GET_RC_CELLS_AT_EXPORT = " + elapsedTimeSec);

        return hrcc;

	}

    public static HashMap getRowColumnCells( Connection connection, int a_tableid , int a_userid, boolean getLatest, int a_baselineid )
    throws SQLException,SystemException {
        ResultSet resultset1 = null;
        PreparedStatement preparedstatement1 = null;
        TableContents tbc = null;
        HashMap hrcc = null;
		float elapsedTimeSec;
		long t;

        try
        {
           	preparedstatement1 = connection.prepareStatement(CALL_BW_GET_ROW_COLUMN_CELLS);
			preparedstatement1.setInt(1,a_tableid);
			preparedstatement1.setInt(2,1);
			preparedstatement1.setDouble(3,1);
			preparedstatement1.setDouble(4,1);
			preparedstatement1.setInt(5,1);
			preparedstatement1.setDouble(6,1);
			preparedstatement1.setDouble(7,1);
            t = System.currentTimeMillis();
            //resultset1.setFetchSize(25);
            resultset1 = preparedstatement1.executeQuery();
            hrcc = new HashMap(resultset1.getFetchSize());
			elapsedTimeSec = (System.currentTimeMillis()-t )/1000F;
			System.out.println("Time to execute query CALL_BW_GET_ROWS_AND_CELLS_BY_TBL_ID = " + elapsedTimeSec);
			t = System.currentTimeMillis();
            boolean isFirstRowDone = false;
            int     a_previousrowid = -1;

            while ( resultset1.next() ) {
                int 	a_rowid;
                int     a_row_sequence_number;
                int     a_columnid;
                int     a_column_sequence_number;
                String  a_columnname;

                int     a_cellid;
                String  a_celltype;
                String  a_cellstringvalue;
                int     a_cellintvalue;
                double  a_celldoublevalue;
                int     a_celltablevalue = -1;
                String  a_formula;

				a_rowid = resultset1.getInt("ROW_ID");
				a_columnid = resultset1.getInt("COLUMN_ID");
				a_cellid = resultset1.getInt("CELL_ID");
				a_celltype = resultset1.getString("CELL_TYPE");
				a_cellstringvalue = resultset1.getString("CELL_STRING_VALUE");
				a_cellintvalue = resultset1.getInt("CELL_INTEGER_VALUE");
				a_celldoublevalue = resultset1.getDouble("CELL_DOUBLE_VALUE");
				a_formula = resultset1.getString("FORMULA");

                RowColumnCell rcc = new  RowColumnCell(a_rowid, a_columnid, a_celltype, a_cellstringvalue, a_cellintvalue, a_celldoublevalue, a_celltablevalue,a_formula );
                Cell cell = new Cell (a_cellid, a_columnid,  a_rowid, a_celltype, a_cellstringvalue, a_cellintvalue, a_celldoublevalue, a_celltablevalue, "", a_formula );
                hrcc.put(new String(""+a_rowid+":"+a_columnid), cell);
            }
        }
        catch(SQLException sqlexception) {
            throw sqlexception;
        }
        finally {
            try {
                resultset1.close();
                preparedstatement1.close();


            }
            catch(SQLException sqlexception1) {
                throw new SystemException(sqlexception1);
            }
        }
		elapsedTimeSec = (System.currentTimeMillis()-t )/1000F;
		System.out.println("Time to create hashtable CALL_BW_GET_ROWS_AND_CELLS_BY_TBL_ID = " + elapsedTimeSec);

        return hrcc;

    }



    public static int createRowA( Connection a_connection, int a_tableid, String a_row_name, int afterRowId,  int tid)
	    throws SQLException {

	        CallableStatement callablestatement = null;
	        int m_row_id = -1;
	        try {
	            String rowName =  "";
	            if ( ( a_row_name == null ) || ( a_row_name.equals("") )) {
	                rowName = "ROW"+tid;
	            }
	            else {
	                rowName = a_row_name;
	            }
				callablestatement = a_connection.prepareCall(CALL_BW_CR_ROW_WITH_SEQID);
	            callablestatement.setInt(1, a_tableid);
	            callablestatement.setString(2, rowName);
	            callablestatement.setInt(3, afterRowId);
	            callablestatement.setInt(4, tid);
	            callablestatement.registerOutParameter(5,java.sql.Types.INTEGER);

	           callablestatement.execute ();
	            m_row_id = callablestatement.getInt(5);

	        }
	        catch( SQLException sql1 )
	        {
	            throw sql1;
	        }
	        finally
	        {
	            try
	            {
	                callablestatement.close();
	         	}
	            catch( SQLException sql2 )
	            {
	                throw sql2;
	            }
	        }

	        return m_row_id;
    }
	
	// When rows are added with createRow(), after all the rows are inserted, you need to resequence rows using resequenceRows()
	// Also need to use BW_UPD_CELL_FROM_RCSV
	public static int createRowXL(Connection a_connection, int a_tableid, String a_row_name, int afterRowId, int prOffset, int tid)
	    throws SQLException
	{

		CallableStatement callablestatement = null;
		int m_row_id = -1;
		try
		{
			String rowName = "";
			if ((a_row_name == null) || (a_row_name.equals("")))
			{
				rowName = "ROW" + tid;
			}
			else
			{
				rowName = a_row_name;
			}
			callablestatement = a_connection.prepareCall("{CALL BW_CR_ROW_XL(?,?,?,?,?,?)}");
			callablestatement.setInt(1, a_tableid);
			callablestatement.setString(2, rowName);
			callablestatement.setInt(3, afterRowId);
			callablestatement.setInt(4, prOffset);
			callablestatement.setInt(5, tid);
			callablestatement.registerOutParameter(6, java.sql.Types.INTEGER);

			callablestatement.execute();
			m_row_id = callablestatement.getInt(6);

		}
		catch (SQLException sql1)
		{
			throw sql1;
		}
		finally
		{
			try
			{
				callablestatement.close();
			}
			catch (SQLException sql2)
			{
				throw sql2;
			}
		}

		return m_row_id;
	}

	// When rows are added with createRow(), after all the rows are inserted, you need to resequence rows using resequenceRows()
	public static int createRow(Connection a_connection, int a_tableid, String a_row_name, int afterRowId, int prOffset, int tid)
	    throws SQLException
	{

		CallableStatement callablestatement = null;
		int m_row_id = -1;
		try
		{
			String rowName = "";
			if ((a_row_name == null) || (a_row_name.equals("")))
			{
				rowName = "ROW" + tid;
			}
			else
			{
				rowName = a_row_name;
			}
			callablestatement = a_connection.prepareCall(CALL_BW_CR_ROW);
			callablestatement.setInt(1, a_tableid);
			callablestatement.setString(2, rowName);
			callablestatement.setInt(3, afterRowId);
			callablestatement.setInt(4, prOffset);
			callablestatement.setInt(5, tid);
			callablestatement.registerOutParameter(6, java.sql.Types.INTEGER);

			callablestatement.execute();
			m_row_id = callablestatement.getInt(6);

		}
		catch (SQLException sql1)
		{
			throw sql1;
		}
		finally
		{
			try
			{
				callablestatement.close();
			}
			catch (SQLException sql2)
			{
				throw sql2;
			}
		}

		return m_row_id;
	}

	public static void resequenceRows(Connection a_connection, int a_tableid)
	    throws SQLException
	{

		CallableStatement callablestatement = null;
		try
		{
			callablestatement = a_connection.prepareCall(CALL_BW_RESEQUENCE_ROWS);
			callablestatement.setInt(1, a_tableid);

			callablestatement.execute();
		}
		catch (SQLException sql1)
		{
			throw sql1;
		}
		finally
		{
			try
			{
				callablestatement.close();
			}
			catch (SQLException sql2)
			{
				throw sql2;
			}
		}
	}

	public static void resequenceColumns(Connection a_connection, int a_tableid)
	    throws SQLException
	{

		CallableStatement callablestatement = null;
		try
		{
			callablestatement = a_connection.prepareCall(CALL_BW_RESEQUENCE_COLUMNS);
			callablestatement.setInt(1, a_tableid);

			callablestatement.execute();
		}
		catch (SQLException sql1)
		{
			throw sql1;
		}
		finally
		{
			try
			{
				callablestatement.close();
			}
			catch (SQLException sql2)
			{
				throw sql2;
			}
		}
	}

	public static int getAfterRowIdforMyRows( Connection a_connection, int a_tableid, int a_userId)
	    throws SQLException
	{

		CallableStatement callablestatement = null;
		int m_after_row_id = -1;
		try {

			callablestatement = a_connection.prepareCall(CALL_BW_GET_AFTER_RID_FOR_MY_ROWS);
			callablestatement.setInt(1, a_tableid);
			callablestatement.setInt(2, a_userId);
			callablestatement.registerOutParameter(3,java.sql.Types.INTEGER);

		   callablestatement.execute ();
			m_after_row_id = callablestatement.getInt(3);

		}
		catch( SQLException sql1 )
		{
			throw sql1;
		}
		finally
		{
			try
			{
				callablestatement.close();
			}
			catch( SQLException sql2 )
			{
				throw sql2;
			}
		}
		return m_after_row_id;
    }
    public static int getAfterRowIdforMyGroupRows( Connection a_connection, int a_tableid, int a_nhId)
		    throws SQLException {

		        CallableStatement callablestatement = null;
		        int m_after_row_id = -1;
		        try {

		            callablestatement = a_connection.prepareCall(CALL_BW_GET_AFTER_RID_FOR_MY_GROUP_ROWS);
		            callablestatement.setInt(1, a_tableid);
		            callablestatement.setInt(2, a_nhId);
		            callablestatement.registerOutParameter(3,java.sql.Types.INTEGER);

		           callablestatement.execute ();
		            m_after_row_id = callablestatement.getInt(3);

		        }
		        catch( SQLException sql1 )
		        {
		            throw sql1;
		        }
		        finally
		        {
		            try
		            {
		                callablestatement.close();
		         	}
		            catch( SQLException sql2 )
		            {
		                throw sql2;
		            }
		        }
		        return m_after_row_id;

    }
    public static int getAfterRowIdforMyGroupAndChildrenRows( Connection a_connection, int a_tableid, int a_nhId)
		    throws SQLException {

		        CallableStatement callablestatement = null;
		        int m_after_row_id = -1;
		        try {

		            callablestatement = a_connection.prepareCall(CALL_BW_GET_AF_RID_FOR_GROUP_A_IMCHD);
		            callablestatement.setInt(1, a_tableid);
		            callablestatement.setInt(2, a_nhId);
		            callablestatement.registerOutParameter(3,java.sql.Types.INTEGER);

		           callablestatement.execute ();
		            m_after_row_id = callablestatement.getInt(3);

		        }
		        catch( SQLException sql1 )
		        {
		            throw sql1;
		        }
		        finally
		        {
		            try
		            {
		                callablestatement.close();
		         	}
		            catch( SQLException sql2 )
		            {
		                throw sql2;
		            }
		        }
		        return m_after_row_id;
    }
    public static int getAfterRowIdforMyGroupAndAllChildrenRows( Connection a_connection, int a_tableid, int a_nhId)
		    throws SQLException {

		        CallableStatement callablestatement = null;
		        int m_after_row_id = -1;
		        try {

		            callablestatement = a_connection.prepareCall(CALL_BW_GET_AF_RID_FOR_GROUP_A_CHD);
		            callablestatement.setInt(1, a_tableid);
		            callablestatement.setInt(2, a_nhId);
		            callablestatement.registerOutParameter(3,java.sql.Types.INTEGER);

		           callablestatement.execute ();
		            m_after_row_id = callablestatement.getInt(3);

		        }
		        catch( SQLException sql1 )
		        {
		            throw sql1;
		        }
		        finally
		        {
		            try
		            {
		                callablestatement.close();
		         	}
		            catch( SQLException sql2 )
		            {
		                throw sql2;
		            }
		        }
		        return m_after_row_id;
    }


     public static int changeRowOwnership( Connection a_connection, int a_rowId, String a_emailAddress,  int tid)
		    throws SQLException {

		        CallableStatement callableStatement = null;
		        int result = -1;

		        try {

		            callableStatement = a_connection.prepareCall(CALL_BW_UPD_ROW_OWNERSHIP);
					callableStatement.setInt(1, a_rowId);
					callableStatement.setString(2,a_emailAddress);
					callableStatement.setInt(3, tid);
	            	callableStatement.registerOutParameter(4,java.sql.Types.INTEGER);

		           callableStatement.execute ();
		            result  = callableStatement.getInt(4);

		        }
		        catch( SQLException sql1 )
		        {
		            throw sql1;
		        }
		        finally
		        {
		            try
		            {
		                callableStatement.close();
		         	}
		            catch( SQLException sql2 )
		            {
		                throw sql2;
		            }
		        }

		        return result;
	    }






    public static int createTable(
	        Connection a_connection,
	        int a_whiteboard_id,
	        String a_name,
	        String  a_purpose,
	        int a_peer_access,
	        int a_private_access,
	        int a_friend_access,
	        String a_view_preference_type,
	        int a_memberId,
	        int a_tid,
	        int a_status
	        ) throws SQLException {
	    /*
	    @WHITEBOARD_ID INTEGER,
	    @NAME NVARCHAR(32),
	    @PURPOSE NVARCHAR(128),
	    @PEER_ACCESS INTEGER,
	    @PRIVATE_ACCESS INTEGER,
	    @FRIEND_ACCESS INTEGER,
	    @TX_ID INTEGER,
	    @STATUS INTEGER,
	    @TABLE_ID INTEGER OUTPUT
	     */

			System.out.println("Create Table param a_whiteboard_id : " + a_whiteboard_id);
			System.out.println("Create Table param a_nam e: " +a_name );
			System.out.println("Create Table param a_purpose : " + a_purpose);
			System.out.println("Create Table param a_peer_access : " + a_peer_access);
			System.out.println("Create Table param a_private_access : " + a_private_access);
			System.out.println("Create Table param a_friend_access : " + a_friend_access );
			System.out.println("Create Table param a_memberId : " + a_memberId);
			System.out.println("Create Table param a_tid : " + a_tid);
			System.out.println("Create Table param a_status : " + a_status );
			System.out.println("Create Table param a_view_preference_type : " + a_view_preference_type);

			int m_table_id = -1;
	        CallableStatement callablestatement = null;
	        callablestatement = a_connection.prepareCall(CALL_BW_CR_TBL);
	        callablestatement.setInt(1,a_whiteboard_id);
	        callablestatement.setString(2,a_name);
	        callablestatement.setString(3,a_purpose);
	        callablestatement.setInt(4,a_peer_access);
	        callablestatement.setInt(5,a_private_access);
	        callablestatement.setInt(6,a_friend_access);
	        callablestatement.setInt(7,a_memberId);
	        callablestatement.setInt(8,a_tid);
	        callablestatement.setInt(9,a_status);
	        callablestatement.setString(10,a_view_preference_type);
	        callablestatement.registerOutParameter(11,java.sql.Types.INTEGER);
	        int result = callablestatement.executeUpdate();
	        m_table_id= callablestatement.getInt(11);
	        callablestatement.close();
	        return m_table_id;
    }


    public static void  lockTable
    (
        Connection a_connection,
        int a_table_id,
        int a_tid
        ) throws SQLException
        {

			CallableStatement callablestatement = null;
			callablestatement = a_connection.prepareCall(CALL_BW_LOCK_TBL);
			callablestatement.setInt(1,a_table_id);
			callablestatement.setInt(2,a_tid);
			callablestatement.executeUpdate();
			callablestatement.close();
  	 }

  	 public static void  unlockTable
	 (
		 Connection a_connection,
		 int a_table_id,
		 int a_tid
	 ) throws SQLException
	 {

		CallableStatement callablestatement = null;
	 			callablestatement = a_connection.prepareCall(CALL_BW_UNLOCK_TBL);
	 			callablestatement.setInt(1,a_table_id);
	 			callablestatement.setInt(2,a_tid);
	 			callablestatement.executeUpdate();
	 			callablestatement.close();
  	 }

  	 public static TableLockInfo  isTableLocked
	 	     (
	 	         Connection a_connection,
	 	         int a_table_id
	 	         ) throws SystemException
	 	         {

	 	 					 ResultSet resultset = null;
					        PreparedStatement preparedstatement = null;
					        TableLockInfo tbli = null;
					        try
					        {
								if ( DatabaseLoader.getDatabaseType().trim().equalsIgnoreCase("ORACLE"))
								{
									preparedstatement = DatabaseLoader.getPreparedStatementFromPreLoadedQueries("BW_IS_TBL_LOCKED", a_connection );
									preparedstatement.setInt(1,a_table_id);
								}
								else
								{
									preparedstatement = a_connection.prepareStatement(CALL_BW_IS_TBL_LOCKED);
									preparedstatement.setInt(1,a_table_id);
								}
					            resultset = preparedstatement.executeQuery();

					            if ( resultset.next() )
					            {
					                 int is_locked = 0;
					                int lock_tid = 0;
					                int locked_by_id = 0;
					                String locked_by_user = "";
					                String locked_time = "";

							   		is_locked = resultset.getInt("IS_LOCKED");
							      	lock_tid = resultset.getInt("LOCK_TX_ID");
							        locked_by_id = resultset.getInt("LOCKED_BY_ID");
									locked_by_user = resultset.getString("LOCKED_BY");
						    		locked_time = resultset.getString("LOCK_UNLOCK_TIME");


					                tbli = new TableLockInfo(
					                                        a_table_id,
					                                        is_locked,
					                                        lock_tid,
					                                        locked_by_id,
					                                        locked_by_user,
					                                        locked_time
					                                    );


					            }
					                return tbli;
					        }
					        catch(SQLException sqlexception)
					        {

										sqlexception.printStackTrace();

							            throw new SystemException(sqlexception);
					        }

					        catch(Exception exception)
					        {
								exception.printStackTrace();
								throw new SystemException(exception);
					        }
					        finally
					        {
					            try {
					                resultset.close();
					                preparedstatement.close();
					            }
					            catch(SQLException sqlexception1) {
					                throw new SystemException(sqlexception1);
					            }
        }





  	 }

	public static Vector createStringColumnsBatch(Connection connection, int tableId, Vector columns, int tid)
	throws SQLException
	{
		Vector columnIds = new Vector();
		CallableStatement callableStatement = null;
		try
		{
			callableStatement = connection.prepareCall(CALL_BW_CR_COLUMN_BEFORE);
			for ( int i = 0; i < columns.size(); i++ )
			{
				Column c = (Column)columns.elementAt(i);
				int isenum = 0;
				if (c.getLookupTableId() > 0 && c.getLookupColumnId() > 0 )
					isenum = 1;
				callableStatement.setString(1, c.getColumnName());
				callableStatement.setString(2, c.getDefaultStringValue());
				callableStatement.setInt(3, tableId);
				callableStatement.setInt(4, isenum);
				callableStatement.setInt(5, c.getLookupTableId());
				callableStatement.setInt(6, c.getLookupColumnId());
				callableStatement.setInt(7, c.getOrderedTableId());
				callableStatement.setInt(8, c.getOrderedColumnId());
				callableStatement.setInt(9, -1);
				callableStatement.setInt(10, i + 1);
				callableStatement.setInt(11, tid);
				callableStatement.registerOutParameter(12, java.sql.Types.INTEGER);
				callableStatement.addBatch();
			}
			callableStatement.executeBatch();


		}
		catch (SQLException sql1)
		{
			throw sql1;
		}
		finally
		{
			try
			{
				callableStatement.close();
			}
			catch (SQLException sql2)
			{
				throw sql2;
			}
		}

		PreparedStatement preparedstatement = null;
		ResultSet resultset = null;
		try
		{
			String q = "select id from bw_column where tx_id = ?";
			preparedstatement = connection.prepareStatement(q);
			preparedstatement.setInt(1, tid);
			resultset = preparedstatement.executeQuery();
	        while( resultset.next())
	        {
	            int columnId = resultset.getInt(1);
	            columnIds.addElement(new Integer(columnId));
			}

		}
		catch (SQLException sql1)
		{
			throw sql1;
		}
		finally
		{
			try
			{
				preparedstatement.close();
				resultset.close();
			}
			catch (SQLException sql2)
			{
				throw sql2;
			}
		}

		return columnIds;
	}

    public static int createColumn(
		Connection a_connection,
		int a_tableid,
		String a_columnName,
		String a_columnType,
		String a_default_string_value,
		int    a_default_integer_value,
		double a_default_float_value,
		int    a_default_table_value,
		String a_default_cell_value,
		int a_lookupTableId,
		int a_lookup_column_id,
		int a_trackingTableId,
		int a_tracking_column_id,
		int a_after_col_id,
		int a_after_col_offset,
		int tid,
		boolean setDefaultAccess,
		int a_source_column_id
	)
    throws SQLException
    {
        CallableStatement callablestatement = null;
        int m_column_id = -1;
        int a_is_enumerated = 0;

 		//System.out.println(" a_lookupTableId " + a_lookupTableId);
        //System.out.println(" a_lookup_column_id " + a_lookup_column_id);


		if (a_lookupTableId > 0 &&  a_lookup_column_id >0)
		{
			a_is_enumerated = 1;
		}

        try
        {

             if ( a_columnType.equals("FLOAT"))
             {
				callablestatement = a_connection.prepareCall(CALL_BW_CR_FLOAT_COLUMN);
				callablestatement.setString(1, a_columnName);
				callablestatement.setString(2, "FLOAT");
				callablestatement.setDouble(3, a_default_float_value);
				callablestatement.setInt(4, a_tableid);
				callablestatement.setInt(5, a_is_enumerated);
				callablestatement.setInt(6, a_lookupTableId);
				callablestatement.setInt(7, a_lookup_column_id);
				callablestatement.setInt(8, a_trackingTableId);
				callablestatement.setInt(9, a_tracking_column_id);
				callablestatement.setInt(10, a_after_col_id);
				callablestatement.setInt(11, tid);
				callablestatement.registerOutParameter(12, java.sql.Types.INTEGER);

			}
			else if ( a_columnType.equals("INTEGER"))
			{
				callablestatement = a_connection.prepareCall(CALL_BW_CR_INTEGER_COLUMN);
				callablestatement.setString(1, a_columnName);
				callablestatement.setString(2, "INTEGER");
				callablestatement.setInt(3, a_default_integer_value);
				callablestatement.setInt(4, a_tableid);
				callablestatement.setInt(5, a_is_enumerated);
				callablestatement.setInt(6, a_lookupTableId);
				callablestatement.setInt(7, a_lookup_column_id);
				callablestatement.setInt(8, a_trackingTableId);
				callablestatement.setInt(9, a_tracking_column_id);
				callablestatement.setInt(10, a_after_col_id);
				callablestatement.setInt(11, tid);
				callablestatement.registerOutParameter(12, java.sql.Types.INTEGER);
			}
			else if ( a_columnType.equals("STRING"))
			{
				callablestatement = a_connection.prepareCall(CALL_BW_CR_COLUMN_BEFORE);
				callablestatement.setString(1, a_columnName);
				callablestatement.setString(2, a_default_string_value);
				callablestatement.setInt(3, a_tableid);
				callablestatement.setInt(4, a_is_enumerated);
				callablestatement.setInt(5, a_lookupTableId);
				callablestatement.setInt(6, a_lookup_column_id);
				callablestatement.setInt(7, a_trackingTableId);
				callablestatement.setInt(8, a_tracking_column_id);
				callablestatement.setInt(9, a_after_col_id);
				callablestatement.setInt(10, a_after_col_offset);
				callablestatement.setInt(11, tid);
				callablestatement.registerOutParameter(12, java.sql.Types.INTEGER);
			}


            int l = callablestatement.executeUpdate();
			m_column_id = callablestatement.getInt(12);
            //System.out.println("CreateColumn()::Created column of type " + a_columnType + " columnId = " + m_column_id);
			callablestatement.close();

			//if (setDefaultAccess == true)
			//{
			//    try
			//    {
			//        ColumnManager.addNewColumnAccess(a_connection, m_column_id, tid);
			//    }
			//    catch (Exception e)
			//    {
			//        e.printStackTrace();
			//    }
			//}

        }
        catch( SQLException sql1 )
        {

            throw sql1;
        }


		return m_column_id;
    }

	public static int createColumnXL(
		Connection a_connection,
		int a_tableid,
		String a_columnName,
		int a_after_col_id,
		int a_after_col_offset,
		int tid
	)
    throws SQLException
	{
		CallableStatement callablestatement = null;
		int m_column_id = -1;

		try
		{
			callablestatement = a_connection.prepareCall("{CALL BW_CR_COLUMN_BEFORE_XL(?,?,?,?,?,?,?,?,?,?,?,?)}");
			callablestatement.setString(1, a_columnName);
			callablestatement.setString(2, null);
			callablestatement.setInt(3, a_tableid);
			callablestatement.setInt(4, 0);
			callablestatement.setInt(5, -1);
			callablestatement.setInt(6, -1);
			callablestatement.setInt(7, -1);
			callablestatement.setInt(8, -1);
			callablestatement.setInt(9, a_after_col_id);
			callablestatement.setInt(10, a_after_col_offset);
			callablestatement.setInt(11, tid);
			callablestatement.registerOutParameter(12, java.sql.Types.INTEGER);
			int l = callablestatement.executeUpdate();
			m_column_id = callablestatement.getInt(12);
			callablestatement.close();
			callablestatement = null;
		}
		catch (SQLException sql1)
		{
			throw sql1;
		}


		return m_column_id;
	}

    public static void commitCellsByRowAndColumn(Connection a_connection, int tid, Vector a_RowColumnCell, boolean updateDesignValues  )
    throws SQLException {

        CallableStatement callablestatement = null;
        CallableStatement strCallablestatement = null;
        Iterator cellItr = a_RowColumnCell.iterator();

        try {

            while ( cellItr.hasNext() ) {

                RowColumnCell rcc = (RowColumnCell)cellItr.next();
                int m_cell_id = 1;
				if( rcc.getType().equals("FLOAT" ) )
				{
					if ( updateDesignValues )
					{
						callablestatement = a_connection.prepareCall(CALL_BW_UPD_DBL_CL_DV_BY_R_AND_C);
					}
					else
					{
						callablestatement = a_connection.prepareCall(CALL_BW_UPD_DOUBLE_CELL_BY_R_AND_C);
					}
					callablestatement.setInt(1, rcc.getRowId());
					callablestatement.setInt(2, rcc.getColumnId());
					callablestatement.setDouble(3, rcc.getDoubleValue());
					callablestatement.setInt(4, tid);
					callablestatement.executeUpdate();
					callablestatement.close();
				}
				else if ( rcc.getType().equals("INTEGER" ))
				{
					if ( updateDesignValues )
					{
						callablestatement = a_connection.prepareCall(CALL_BW_UPD_INT_CELLDV_BY_R_AND_C);
					}
					else
					{
						callablestatement = a_connection.prepareCall(CALL_BW_UPD_INTEGER_CELL_BY_R_AND_C);
					}
					callablestatement.setInt(1, rcc.getRowId());
					callablestatement.setInt(2, rcc.getColumnId());
					callablestatement.setInt(3, rcc.getIntValue());
					callablestatement.setInt(4, tid);
					callablestatement.executeUpdate();
					callablestatement.close();
				}
				else // STRING Type
				{
					if (strCallablestatement == null)
					{
						if ( updateDesignValues )
						{
							strCallablestatement = a_connection.prepareCall(CALL_BW_UPD_STR_CL_DV_BY_R_AND_C);
						}
						else
						{
							strCallablestatement = a_connection.prepareCall(CALL_BW_UPD_STRING_CELL_BY_R_AND_C);
						}
					}
					//System.out.println(" Adding a string cell for batch update " + rcc.getStringValue() );
					strCallablestatement.setInt(1, rcc.getRowId());
					strCallablestatement.setInt(2, rcc.getColumnId());
					strCallablestatement.setString(3, rcc.getStringValue());
					String formula = null;
					if (rcc.getFormula().indexOf("=") > -1) // if it matches at least one non-whitespace character
					{
						formula=rcc.getFormula();
					}
					strCallablestatement.setString(4, formula);
					strCallablestatement.setInt(5, tid);
					strCallablestatement.addBatch();
				}
            }

			if (strCallablestatement != null)
			{
				//System.out.println("Committing the row-column cells");
				int[] rescnt = strCallablestatement.executeBatch();
				strCallablestatement.close();
			}
        }
        catch( SQLException sql1 ) {
            sql1.printStackTrace();
            throw sql1;
        }
        finally {

        }
    }


    public static void commitCellsByCellId( Connection a_connection, int tid, Vector a_cellContent, boolean updateDesignValues  )
	    throws SQLException
	   {

	        CallableStatement callablestatement = null;
	        // System.out.println("commitCellsByCellId::::::::::::::::");
	        Iterator cellItr = a_cellContent.iterator();
	        while ( cellItr.hasNext() ) {
	            CellContents cc = (CellContents)cellItr.next();

				if( cc.getType().equals("FLOAT" ) ) {
				  	// System.out.println("Setting float value for cell " + cc.getId() +" to " + cc.getDoubleValue());
					try {
						if ( updateDesignValues )
						{
							callablestatement = a_connection.prepareCall(CALL_BW_UPD_DESIGN_DOUBLE_CELL);
						}
						else
						{
							callablestatement = a_connection.prepareCall(CALL_BW_UPD_DOUBLE_CELL);
						}

						callablestatement.setInt(1, cc.getId());
						callablestatement.setDouble(2, cc.getDoubleValue());
						callablestatement.setInt(3, tid);

						int l = callablestatement.executeUpdate();
					}
					catch( SQLException sql1 ) {
						throw sql1;
					}
					finally {
						try {
							callablestatement.close();
						}
						catch( SQLException sql2 ) {
							throw sql2;
						}
					}
				}
				else if ( cc.getType().equals("INTEGER" ))
				{
					try
					{
						if ( updateDesignValues )
						{
							callablestatement = a_connection.prepareCall(CALL_BW_UPD_DESIGN_INTEGER_CELL);
						}
						else
						{
							callablestatement = a_connection.prepareCall(CALL_BW_UPD_INTEGER_CELL);
						}

						callablestatement.setInt(1, cc.getId());
						callablestatement.setInt(2, cc.getIntValue());
						callablestatement.setInt(3, tid);

						int l = callablestatement.executeUpdate();
					}
					catch( SQLException sql1 ) {
						throw sql1;
					}
					finally {
						try {
							callablestatement.close();
						}
						catch( SQLException sql2 ) {
							throw sql2;
						}
					}
				}
				else // STRING Type
				{
					try {
						 System.out.println("Updating a string cell" + cc.getId() + " value " + cc.getStringValue() );
						char x = (char)(13);
						char y = (char)(0);

						if ( updateDesignValues )
						{
							callablestatement = a_connection.prepareCall(CALL_BW_UPD_DESIGN_STRING_CELL);
						}
						else
						{
							callablestatement = a_connection.prepareCall(CALL_BW_UPD_STRING_CELL);
						}
						callablestatement.setInt(1, cc.getId());
						callablestatement.setString(2, cc.getStringValue());
						callablestatement.setString(3, null);
						callablestatement.setInt(4, tid);

						int l = callablestatement.executeUpdate();
					}
					catch( SQLException sql1 ) {
						throw sql1;
					}
					finally
					{
						try
						{
							callablestatement.close();
						}
						catch( SQLException sql2 ) {
							throw sql2;
						}
					}
				}
	        }
	    }

		public static void commitExcelCellsByCellId( Connection a_connection,
													int tid,
													Vector a_cellContent,
													boolean updateDesignValues  )
		throws SQLException
		{

			CallableStatement strCallablestatement = null;
			CallableStatement intCallablestatement = null;
			CallableStatement floatCallablestatement = null;

			try
			{

				Iterator cellItr = a_cellContent.iterator();
				while ( cellItr.hasNext() )
				{

					RowColumnCell cc = (RowColumnCell)cellItr.next();
					//cc.printCellContents();
					if ( cc.getType().equals("STRING" ))
					{
						//cc.printCellContents();

						if (  strCallablestatement == null )
						{
							if ( updateDesignValues )
							{
								strCallablestatement = a_connection.prepareCall(CALL_BW_UPD_DESIGN_STRING_CELL);
							}
							else
							{
								strCallablestatement = a_connection.prepareCall(CALL_BW_UPD_STRING_CELL);
							}

						}
						if ( updateDesignValues )
						{
							strCallablestatement.setInt(1, cc.getId());
							strCallablestatement.setString(2, cc.getStringValue());
							strCallablestatement.setInt(3, tid);
							strCallablestatement.addBatch();
						}
						else
						{
							strCallablestatement.setInt(1, cc.getId());
							strCallablestatement.setString(2, cc.getStringValue());
							String formula = null;
							if (cc.getFormula().indexOf("=") > -1) // if it matches at least one non-whitespace character
							{
								formula=cc.getFormula();
							}
							strCallablestatement.setString(3, formula);
							//System.out.println ("Updating string value with formula = " + formula);

							strCallablestatement.setInt(4, tid);
							strCallablestatement.addBatch();
						}
					}
				}
				if ( strCallablestatement != null )
				{
						int[] strUpdateCounts = strCallablestatement.executeBatch();
						strCallablestatement.close();
						strCallablestatement = null;
				}
				Iterator intCellItr = a_cellContent.iterator();
				while ( intCellItr.hasNext() )
				{
					RowColumnCell cc = (RowColumnCell)intCellItr.next();

					if ( cc.getType().equals("INTEGER" ))
					{
						//cc.printCellContents();
						if (  intCallablestatement == null )
						{
							if ( updateDesignValues )
							{
								intCallablestatement = a_connection.prepareCall(CALL_BW_UPD_DESIGN_INTEGER_CELL);
							}
							else
							{
								intCallablestatement = a_connection.prepareCall(CALL_BW_UPD_INTEGER_CELL);
							}
						}

						intCallablestatement.setInt(1, cc.getId());
						intCallablestatement.setInt(2, cc.getIntValue());
						intCallablestatement.setInt(3, tid);
						intCallablestatement.addBatch();
					}

				}

				if ( intCallablestatement != null )
				{
					int[] intUpdateCounts = intCallablestatement.executeBatch();
					intCallablestatement.close();
					intCallablestatement = null;
				}

				Iterator floatCellItr = a_cellContent.iterator();
				while ( floatCellItr.hasNext() )
				{
					RowColumnCell cc = (RowColumnCell)floatCellItr.next();

					if ( cc.getType().equals("FLOAT" ))
					{
						//cc.printCellContents();
						if (  floatCallablestatement == null )
						{
							if ( updateDesignValues )
							{
								floatCallablestatement = a_connection.prepareCall(CALL_BW_UPD_DESIGN_DOUBLE_CELL);
							}
							else
							{
								floatCallablestatement = a_connection.prepareCall(CALL_BW_UPD_DOUBLE_CELL);
							}
						}

						floatCallablestatement.setInt(1, cc.getId());
						floatCallablestatement.setDouble(2, cc.getDoubleValue());
						floatCallablestatement.setInt(3, tid);
						floatCallablestatement.addBatch();
					}
				}
				if ( floatCallablestatement != null )
				{
					int[] floatUpdateCounts = floatCallablestatement.executeBatch();
					floatCallablestatement.close();
					floatCallablestatement = null;
				}


			}
			catch( SQLException sql1 )
			{
				throw sql1;
			}
			finally
			{
				try
				{
							if ( strCallablestatement != null )
							{
									strCallablestatement.close();
							}
							if ( floatCallablestatement != null )
							{
									floatCallablestatement.close();
							}
							if ( intCallablestatement != null )
							{
									intCallablestatement.close();
							}

				}
				catch( SQLException sql2 )
				{
					throw sql2;
				}
			}
		}




  public static Hashtable getTableActionUIValues( Connection connection, int table_id )
	throws SystemException
 {
	        ResultSet resultset = null;
	        PreparedStatement preparedstatement = null;
	        Hashtable actionToUIPreference = new Hashtable();


	        try
	        {
				if ( DatabaseLoader.getDatabaseType().trim().equalsIgnoreCase("ORACLE"))
				{
					preparedstatement = DatabaseLoader.getPreparedStatementFromPreLoadedQueries("BW_GET_TBL_ACTION_UI_VALUES", connection );
					preparedstatement.setInt(1,table_id);
					preparedstatement.setInt(2,table_id);

				}
				else
				{
					preparedstatement = connection.prepareStatement(CALL_BW_GET_TBL_ACTION_UI_VALUES);
					preparedstatement.setInt(1,table_id);
				}
	            resultset = preparedstatement.executeQuery();
	           while( resultset.next() )
	            {
	                int    actionUI_Id;
				    String objectType;
				    int    a_table_id;
			        String  actionUI_String;
   					String  action;
   					int    action_Id;

	                actionUI_Id = resultset.getInt("ID");
	                objectType = resultset.getString("OBJECT_TYPE");
	                a_table_id = resultset.getInt("OBJECT_ID");
	                actionUI_String = resultset.getString("ACTION_UI_STRING");
	                action = resultset.getString("ACTION");
	                action_Id = resultset.getInt("ACTION_ID");

	                TableActionUIPreference tbUIP = null;

	                if ( actionUI_Id < 0 )
	                {
						tbUIP = new TableActionUIPreference( actionUI_Id, a_table_id, null, action,action_Id);
					}
					else
					{
						tbUIP = new TableActionUIPreference( actionUI_Id, a_table_id, actionUI_String, action,action_Id);
					}


				    actionToUIPreference.put( action, tbUIP );
	            }


	        }
	        catch(SQLException sqlexception)
	        {
	            throw new SystemException(sqlexception);
	        }
	        finally
	        {
	            try
	            {
	                resultset.close();
	                preparedstatement.close();
	            }
	            catch(SQLException sqlexception1)
	            {
	                throw new SystemException(sqlexception1);
	            }
	        }
	        return actionToUIPreference;
}

  public static void  createTableActionUIValues( Connection connection, int tableId,Vector actionUIValues, int tid )
 throws SQLException
		{

							        CallableStatement callablestatement = null;
							        try
							        {

																if ( actionUIValues.size () > 0 )
																{

																	for ( int a = 0; a < actionUIValues.size () ; a++ )
																	{

																			/*

																			BW_CR_TBL_ACTION_UI_VALUES
																			(
																				@TABLE_ID INTEGER,
																				@ACTION_ID BIGINT,
																				@ACTION_UI_STRING NVARCHAR(64),
																				@TX_ID AS INTEGER
																				)

																			*/
																				TableActionUIPreference tbUIP= (TableActionUIPreference)actionUIValues.elementAt(a);
//																				tbUIP.print();
																				// System.out.println("");
																				callablestatement = connection.prepareCall(CALL_BW_CR_TBL_ACTION_UI_VALUES);

																				callablestatement.setInt(1, tbUIP.getTableId());
																				callablestatement.setInt(2, tbUIP.getActionId());
																				callablestatement.setString(3, tbUIP.getActionUIString());
																				callablestatement.setInt(4, tid);

																				callablestatement.executeUpdate();
																				callablestatement.close();
																				callablestatement = null;


																		}
																	}
									}
									catch( SQLException sql1 )
									{
										throw sql1;
									}
									finally
									{
										try
										{
											if ( callablestatement != null )
												callablestatement.close();
										}
										catch( SQLException sql2 ) {
											throw sql2;
										}
									}





  }

  public static void   updateTableActionUIValues( Connection connection, int tableId, Vector actionUIValues, int tid )
	throws SQLException
		{

							        CallableStatement callablestatement = null;
							        try
							        {

																if ( actionUIValues.size () > 0 )
																{

																	for ( int a = 0; a < actionUIValues.size () ; a++ )
																	{

																			/*

																			 BW_UPD_TBL_ACTION_UI_VALUES
																				(
																					@TABLE_ID INTEGER,
																					@ID INTEGER,
																					@ACTION_UI_STRING NVARCHAR(64),
																					@TX_ID AS INTEGER
																				)



																			*/
																					TableActionUIPreference tbUIP= (TableActionUIPreference)actionUIValues.elementAt(a);
																			//		tbUIP.print();
																					// System.out.println("");
																					callablestatement = connection.prepareCall(CALL_BW_UPD_TBL_ACTION_UI_VALUES);
																					callablestatement.setInt(1, tbUIP.getTableId());
																					callablestatement.setInt(2, tbUIP.getId());
																					callablestatement.setString(3, tbUIP.getActionUIString());
																					callablestatement.setInt(4, tid);
																					callablestatement.executeUpdate();
																					callablestatement.close();
																					callablestatement = null;

																		}
																	}
									}
									catch( SQLException sql1 )
									{
										throw sql1;
									}
									finally
									{
										try
										{
											if ( callablestatement != null )
												callablestatement.close();
										}
										catch( SQLException sql2 ) {
											throw sql2;
										}
									}
  }





    public static void purgeTable(Connection connection, int tableId )
    throws SystemException {
        CallableStatement callableStatement = null;
        String BW_PURGE_TBL = "{CALL BW_PURGE_TBL(?)}";

        try {
            callableStatement = connection.prepareCall(BW_PURGE_TBL);
            callableStatement.setInt(1, tableId);
            callableStatement.executeUpdate();
        } catch (SQLException sql1) {
            throw new SystemException(sql1);
        } finally {
            try {
				if (callableStatement != null)
                	callableStatement.close();
            } catch (SQLException sql2) {
                throw new SystemException(sql2);
            }
        }
    }

	public static String getBaseLineForTableId(int tableId, String asStartDate, String asEndDate)
	{
		String lsReturnValue	= "ALL^ALL|";
		Connection connection	= null;
		Statement statement		= null;
		ResultSet rs			= null;

		try
		{
			DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
		    connection	= databaseloader.getConnection();
			statement	= null;
			rs			= null;
			//String lsSql			=	" SELECT BASELINE_ID, NAME, DESCRIPTION FROM BW_BL_TBL, BW_BL "+
			//								" where BASELINE_ID = BW_BL.ID AND TABLE_ID = "+tableId;

			String lsSql =	"SELECT BASELINE_ID, NAME, BW_BL.DESCRIPTION FROM BW_BL_TBL, BW_TXS ,BW_BL "+
							" WHERE BW_BL_TBL.TX_ID = BW_TXS.TX_ID AND BASELINE_ID = BW_BL.ID AND TABLE_ID = "+tableId;

			if(!BoardwalkUtility.checkIfNullOrBlank(asStartDate))
				lsSql +=" AND CREATED_ON > '"+asStartDate+"'";

			if(!BoardwalkUtility.checkIfNullOrBlank(asEndDate))
				lsSql +=" AND CREATED_ON < '"+asEndDate+"'";

			System.out.println("###### lsSql " + lsSql);

			statement = connection.createStatement();
			rs = statement.executeQuery(lsSql);
			while(rs.next())
			{
				lsReturnValue +=rs.getInt(1)+"^"+rs.getString(2)+"|";
			}
		}
		catch( SQLException sqe )
		{
			sqe.printStackTrace();
		}
		finally
		{
			try
			{
				if(rs != null)
					rs.close();
				if(statement != null)
					statement.close();
				if(connection != null)
					connection.close();
			}
			catch( SQLException sql2 )
			{
				sql2.printStackTrace();
			}
		}

		return lsReturnValue ;
	}

      public static int createRowBefore(Connection a_connection, int a_tableid, String a_row_name, int afterRowId, int tid, int prOffset)
          throws SQLException
      {

            CallableStatement callablestatement = null;
            int m_row_id = -1;
            try
            {
                  String rowName = "";
                  if ((a_row_name == null) || (a_row_name.equals("")))
                  {
                        rowName = "ROW" + tid;
                  }
                  else
                  {
                        rowName = a_row_name;
                  }
                  callablestatement = a_connection.prepareCall(CALL_BW_CR_ROW_BEFORE);
                  callablestatement.setInt(1, a_tableid);
                  callablestatement.setString(2, rowName);
                  callablestatement.setInt(3, afterRowId);
                  callablestatement.setInt(4, prOffset);
                  callablestatement.setInt(5, tid);
                  callablestatement.registerOutParameter(6, java.sql.Types.INTEGER);

                  callablestatement.execute();
                  m_row_id = callablestatement.getInt(6);

            }
            catch (SQLException sql1)
            {
                  throw sql1;
            }
            finally
            {
                  try
                  {
                        callablestatement.close();
                  }
                  catch (SQLException sql2)
                  {
                        throw sql2;
                  }
            }

            return m_row_id;
      }

	public static float getElapsedTime()
	{
		if (prevTime == -1)
			prevTime = System.currentTimeMillis();
		// Get elapsed time in seconds
		float elapsedTimeSec = (System.currentTimeMillis() - prevTime) / 1000F;

		// reset time
		prevTime = System.currentTimeMillis();

		return elapsedTimeSec;
	}

	// This method is called to Display Table Updates Report After Given Import Id From XL
	public static Hashtable getTransactionListAfterImport( Connection a_connection,
											  	int tableId,
	                                            int stid,
	                                            int userId,
	                                            int nhId,
	                                            String viewPref )
	    throws SQLException
	{

		PreparedStatement preparedstatement = null;
		ResultSet resultset = null;
		Hashtable tlist = new Hashtable();
		try {

			Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));

			int criteriaTableId = TableViewManager.getCriteriaTable(a_connection, tableId, userId);
//			String rowQuery = TableViewManager.getRowQuery(a_connection, tableId, userId, criteriaTableId,false, viewPref);
			if(criteriaTableId == -1)
			{
				String CALL_BW_GET_TBL_TXLIST_AFTER_IMPORT  = null;

				CALL_BW_GET_TBL_TXLIST_AFTER_IMPORT = "{CALL BW_GET_TBL_TXLIST_AFTER_IMPORT(?,?,?,?,?)}";
				System.out.println("Calling BW_GET_TBL_TXLIST_AFTER_IMPORT");

				preparedstatement = a_connection.prepareStatement(CALL_BW_GET_TBL_TXLIST_AFTER_IMPORT);
				preparedstatement.setInt(1, tableId);
				preparedstatement.setInt(2, stid);
				preparedstatement.setInt(3, userId);
				preparedstatement.setInt(4, nhId);
				preparedstatement.setString(5, viewPref);
			}
			else
			{
				String rowQuery = TableViewManager.getRowQuery(a_connection, tableId, userId, criteriaTableId, false, viewPref);
				String lsSql = QueryMaker.getFiltredTransactionListAfterImport(rowQuery);
				preparedstatement = a_connection.prepareStatement(lsSql);

				preparedstatement.setInt(1, stid);
				preparedstatement.setInt(2, stid);
				preparedstatement.setInt(3, stid);
				preparedstatement.setInt(4, stid);
				preparedstatement.setInt(5, tableId);
				preparedstatement.setInt(6, stid);
				preparedstatement.setInt(7, tableId);
				preparedstatement.setInt(8, stid);
				preparedstatement.setInt(9, tableId);
				preparedstatement.setInt(10, stid);

			}

			resultset = preparedstatement.executeQuery ();

			while (resultset.next())
		    {
				int tid = resultset.getInt("tx_id");
				java.sql.Timestamp updatedOn = resultset.getTimestamp("created_on", cal);
				String updatedBy = resultset.getString("created_by");
				String comment = resultset.getString("comment_");
				String action = resultset.getString("action");
				Transaction t = new Transaction(tid, -1, updatedBy, updatedOn.getTime(), action, comment);
				Vector vt = (Vector)tlist.get(new Integer (tid));
				if (vt == null)
				{
					vt = new Vector();
				}
				vt.addElement(t);
				tlist.put (new Integer(tid), vt);
			}
		}
		catch( SQLException sql1 )
		{
			throw sql1;
		}
		catch( SystemException se )
		{
			se.printStackTrace();
		}
		finally
		{
			try
			{
				preparedstatement.close();
			}
			catch( SQLException sql2 )
			{
				throw sql2;
			}
		}

		return tlist;
    }
};


