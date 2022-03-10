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

import java.sql.*;                  // JDBC package
import javax.sql.*;                 // extended JDBC package



public class TableManagerXL{

    // BW_GET_TBLS_BY_WB( WB_ID, USER_ID, TABLE_STATUS, TABLE_ACCESS );
/*
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
	private static String CALL_BW_GET_TBL_ACCESS="{CALL  BW_GET_TBL_ACCESS(?)}";

	public static final String CALL_BW_CR_TBL_ACTION_UI_VALUES = "{CALL BW_CR_TBL_ACTION_UI_VALUES(?,?,?,?)}";
	public static final String CALL_BW_UPD_TBL_ACTION_UI_VALUES = "{CALL BW_UPD_TBL_ACTION_UI_VALUES(?,?,?,?)}";
	public static final String CALL_BW_GET_TBL_ACTION_UI_VALUES = "{CALL BW_GET_TBL_ACTION_UI_VALUES(?)}";


	private static String CALL_BW_GET_ROWS_AND_CELLS_BY_TBL_ID="{CALL BW_GET_ROWS_AND_CELLS_BY_TBL_ID(?)}";
	//TBR BW_GET_CELLS_FOR_TBL
	private static String CALL_BW_GET_DES_CELL_VALS_BY_TBL="{CALL BW_GET_DES_CELL_VALS_BY_TBL (?,?,?)}";
	//TBR BW_GET_DCELLS_FOR_TBL

/* &&&&&&&&&&&& */

/*
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
/*
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

*/

	private static String CALL_BW_GET_TBL_ACCESS_FOR_MEMBER="{CALL BW_GET_TBL_ACCESS_FOR_MEMBER(?,?)}";


    public TableManagerXL() {}


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



     public static TableAccessList getTableAccessForMember( Connection connection, int a_member_id, int table_id ) throws SystemException {
	        ResultSet resultset = null;
	        PreparedStatement preparedstatement = null;
	        Hashtable relationshipToAccess = new Hashtable();
	        Vector accessList = null;

	         TableAccessList tbACL  = null;

	        try
	        {
				preparedstatement = connection.prepareStatement(CALL_BW_GET_TBL_ACCESS_FOR_MEMBER);
				preparedstatement.setInt(1,table_id);
				preparedstatement.setInt(2,a_member_id);

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


// Ths method is called to Display Table Updates Report for given Duration
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
		try {
			String CALL_BW_GET_TBL_TXLIST  = null;
			if (baseline == true)
			{
				CALL_BW_GET_TBL_TXLIST = "{CALL BW_GET_TBL_TXLIST(?,?,?,?,?,?,?,?)}";
				System.out.println("Calling BW_GET_TBL_TXLIST");
			}
			else
			{
				CALL_BW_GET_TBL_TXLIST = "{CALL BW_GET_TBL_TXLIST_NOBL(?,?,?,?,?,?,?,?)}";
			}
			preparedstatement = a_connection.prepareStatement(CALL_BW_GET_TBL_TXLIST);
			preparedstatement.setInt(1, tableId);
			preparedstatement.setInt(2, stid);
			System.out.println("stid = " + stid);
			preparedstatement.setInt(3, etid);
			Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
			preparedstatement.setTimestamp(4, new java.sql.Timestamp(sdate), cal);
			preparedstatement.setTimestamp(5, new java.sql.Timestamp(edate), cal);
			preparedstatement.setInt(6, userId);
			preparedstatement.setInt(7, nhId);
			preparedstatement.setString(8, viewPref);
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



// Ths method is called to Display Table Updates Report After Given Import Id 
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
			String CALL_BW_GET_TBL_TXLIST_AFTER_IMPORT  = null;
			Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));

			CALL_BW_GET_TBL_TXLIST_AFTER_IMPORT = "{CALL BW_GET_TBL_TXLIST_AFTER_IMPORT(?,?,?,?,?)}";
			System.out.println("Calling BW_GET_TBL_TXLIST_AFTER_IMPORT");

			preparedstatement = a_connection.prepareStatement(CALL_BW_GET_TBL_TXLIST_AFTER_IMPORT);
			preparedstatement.setInt(1, tableId);
			preparedstatement.setInt(2, stid);
			preparedstatement.setInt(3, userId);
			preparedstatement.setInt(4, nhId);
			preparedstatement.setString(5, viewPref);

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

		// flags which decide the sql query to run

		System.out.println("+++++++++++ TableManagerXL -->getTableContents_t() function");

		int rowState = 1;
		int colState = 1;

//		boolean getDesignValues = false;
		boolean getLatest = false;
//		boolean getLatestRowsCreatedByUser= false;
//		boolean getAllLatestRowsCreatedByUserSortByUser= false;
//		boolean getLatestByUser= false;
//		boolean getLatestByAllUsers= false;
//		boolean getLookupTable = false;
		boolean getBaseline = false;
		boolean getTableAsOf = false;
		boolean getTableDelta = false;

		// Rows based
//		boolean getLatestRowsCreatedBySpecificNh= false;
//		boolean getLatestRowsCreatedByAllChildrenNh= false;
//		boolean getRowsCreatedByMyNh= false;
//		boolean getRowsCreatedByMyNhAndImmediateChildren= false;
//		boolean getRowsCreatedByMyNhAndAllChildren= false;

		// table based
//		boolean getLatestBySpecificNh = false;
//		boolean getLatestBySpecificChildrenNh = false;

		TableAccessList tbl = null;

		long prevTime = System.currentTimeMillis();

		//System.out.println("requested columns = " + requestedColumns);


		//System.out.println("ViewPreference=" + ViewPreference+ ":");
		//System.out.println("QueryPreference=" + QueryPreference+ ":");

		tbl = getTableAccessForMember( connection, memberId, a_tableid );

		System.out.println("Time(sec) to getTableAccess in getTableContents = " + (System.currentTimeMillis()-prevTime)/1000F);
		prevTime = System.currentTimeMillis();

		// System.out.println("getTableAccessForMember returned the following access for memberId " + memberId );

		System.out.println("+++++++++ viewpreferences = " + ViewPreference);

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

		System.out.println("+++++++++++++++++++++++++==== getTableAsOf = " + getTableAsOf );
		System.out.println("+++++++++++++++++++++++++==== getTableDelta  = " + getTableDelta );

		if (getTableAsOf == true)//latest
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
		System.out.println("tbrowInfo--------------->  "+tbrowInfo);
		}

		if (getTableDelta == true)
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
			tbrowInfo = RowManager.getTableRows( connection, a_tableid , a_userid,  a_userNhId, a_baselineid, ViewPreference, rowState, rowStartSeqId, rowEndSeqId );
			System.out.println("tbrowInfo else --------------->  "+tbrowInfo);
		}


		System.out.println("Time(sec) to get rows for the table = " + (System.currentTimeMillis()-prevTime)/1000F);
		prevTime = System.currentTimeMillis();

		Vector rowVector  = tbrowInfo.getRowVector();
		Hashtable  RowObjsByRowId = tbrowInfo.getRowHash();
		Vector rowNames = new Vector();
		//System.out.println("Done getRowsByTable got no of rows = "+ rowVector.size() );
		Vector rowIds = new Vector();

		System.out.println(".......................rowVector.size() = " + rowVector.size());

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

		System.out.println(	"tableid " + ":" + a_tableid + "\n" +
							"asOfTid : " + asOfTid + "\n" +
							"compTid : " + compTid + "\n" +
							"Comp Date : " + new java.util.Date(compDate) + "\n" +
							"As of Date " + ": " + new java.util.Date(asOfDate) + "\n" +
							"requestedColumns: " + requestedColumns + "\n" +
							"a_userid : " + a_userid + "\n" +
							"memberId : " + memberId + "\n" +
							"a_userNhId : " + a_userNhId + "\n" +
							"ViewPreference : " + ViewPreference + "\n" );
		
		try
		{

// need thIS RAHULVARADKAR
			if (getTableDelta == true)
			{
				System.out.println("Calling...................................BW_GET_TBL_DELTA_XL................");
				String CALL_BW_GET_TBL_DELTA="{CALL BW_GET_TBL_DELTA_XL(?,?,?,?,?,?,?,?,?,?)}";
			/*
				@TABLE_ID INTEGER,
				@STX_ID INTEGER,
				@SDATE DATETIME,
				@ETX_ID INTEGER,
				@EDATE DATETIME,
				@REQ_COL_IDS VARCHAR(2048),
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
				
				preparedstatement1.setString(6,requestedColumns);
				preparedstatement1.setInt(7,a_userid);
				preparedstatement1.setInt(8,memberId);
				preparedstatement1.setInt(9,a_userNhId);
				preparedstatement1.setString(10,ViewPreference);

			}

//need ths RAHUL VARADKAR
			if (getTableAsOf == true)
			{

				System.out.println("Calling......................................................BW_GET_TBL_T_XL................");

				String CALL_BW_GET_TBL_T="{CALL BW_GET_TBL_T_XL(?,?,?,?,?,?,?,?)}";
			/*
				@TABLE_ID INTEGER,
				@TX_ID INTEGER,
				@TDATE DATETIME,
			--	@START_ROW_SEQ_ID [FLOAT],
			--	@END_ROW_SEQ_ID [FLOAT],
				@REQ_COL_IDS VARCHAR(2048),
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
				preparedstatement1.setString(4,requestedColumns);
				preparedstatement1.setInt(5,a_userid);
				preparedstatement1.setInt(6,memberId);
				preparedstatement1.setInt(7,a_userNhId);
				preparedstatement1.setString(8,ViewPreference);


				System.out.println("Getting table as of " + asOfTid + ":" + asOfDate);
				System.out.println("1 a_tableid  :" + a_tableid);
				System.out.println("2 asOfTid : " + asOfTid);
				System.out.println("3 " + asOfDate );
				System.out.println("3 " + new java.sql.Timestamp(asOfDate).getTime() );
				System.out.println("4 " + requestedColumns );
				System.out.println("5 " + a_userid );
				System.out.println("6 " + memberId );
				System.out.println("7 " + a_userNhId );
				System.out.println("8 " + ViewPreference );
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
//                int     a_cellintvalue;
                int     a_celltablevalue = -1;
//RPV                double  a_celldoublevalue;
                int tid = -1;
                int userid = -1;
                String emailAddress = "";
                String description = "";
                String comment = "";
                String a_celltableName = "Not Set";
                String a_cellFormula = null;

				a_columnid = -1;
				a_rowid = resultset1.getInt("ROW_ID");
//                a_columnid = resultset1.getInt("COLUMN_ID");
                a_cellid = resultset1.getInt("CELL_ID");
                a_cellstringvalue = resultset1.getString("CELL_STRING_VALUE");
//RPV                a_cellintvalue = resultset1.getInt("CELL_INTEGER_VALUE");
//RPV                a_celldoublevalue = resultset1.getDouble("CELL_DOUBLE_VALUE");
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
//RPV				a_celltype = ((Column)existingCols.get(new Integer(a_columnid))).getType();
//RPV				a_columnname = ((Column)existingCols.get(new Integer(a_columnid))).getColumnName();

				a_celltype = "String";
				a_columnname = "";

                VersionedCell cl=  new VersionedCell
													(
														a_cellid,
														a_columnid,
														a_columnname ,
														a_rowid,
														a_celltype,
														a_cellstringvalue,
														-1,								//RPV a_cellintvalue,
														-1,								//RPV a_celldoublevalue,
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
					//System.out.println("Error:: Vector not created for cells");
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


//fromhere onwards all lines are removed. ...
//private static TableContents getLatestTableContentsByAllUsers(
};


