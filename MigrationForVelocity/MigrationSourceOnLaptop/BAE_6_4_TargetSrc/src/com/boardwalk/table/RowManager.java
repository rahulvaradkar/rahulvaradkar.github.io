/*
 * RowManager.java
 *
 * Created on March 27, 2000, 8:15 AM
 */

package com.boardwalk.table;
import java.util.*;
import java.io.*;

import com.boardwalk.database.DatabaseLoader;
import com.boardwalk.exception.NeighborhoodException;
import com.boardwalk.exception.SystemException;
import com.boardwalk.database.*;

import java.sql.*;                  // JDBC package
import javax.sql.*;                 // extended JDBC package


/**
 *
 * @author  administrator
 * @version
 */
public class RowManager {
    private static String CALL_BW_GET_ROWCELLS_BY_ROW_ID="{CALL BW_GET_ROWCELLS_BY_ROW_ID(?)}";
	private static String CALL_BW_GET_ROWCELLS="{CALL BW_GET_ROWCELLS(?,?,?,?,?)}";
    private static String CALL_BW_GET_ROWS_BY_TBL_ID="{CALL BW_GET_ROWS_BY_TBL_ID(?)}";
    private static String CALL_BW_UPD_STRING_CELL="{CALL BW_UPD_STRING_CELL(?,?,?,?)}";
    private static String CALL_BW_UPD_DOUBLE_CELL="{CALL BW_UPD_DOUBLE_CELL(?,?,?,?)}";
    private static String CALL_BW_UPD_INTEGER_CELL="{CALL BW_UPD_INTEGER_CELL(?,?,?,?)}";
    private static String CALL_BW_UPD_TBL_CELL="{CALL BW_UPD_TBL_CELL(?,?,?,?)}";

	private static String CALL_BW_UPD_ROW_OWNERSHIP="{CALL BW_UPD_ROW_OWNERSHIP(?,?,?,?)}";



	private static String CALL_BW_GET_TBL_ROWS_USER="{CALL BW_GET_TBL_ROWS_USER(?,?,?,?,?)}";
	/*
		@TABLE_ID INTEGER,
		@USER_ID INTEGER,
		@ROW_STATE INTEGER,
		@START_ROW_SEQ_ID [FLOAT],
		@END_ROW_SEQ_ID [FLOAT] */

	private static String CALL_BW_GET_TBL_ROWS="{CALL BW_GET_TBL_ROWS(?,?,?,?)}";
	/*
	@TABLE_ID INTEGER,
	@ROW_STATE INTEGER,
	@START_ROW_SEQ_ID [FLOAT],
	@END_ROW_SEQ_ID [FLOAT]
	*/
	
	private static String CALL_BW_GET_TBL_FILTERED_ROWS="{CALL BW_GET_FiltredTableRows_DYNAMIC(?,?,?)}";
	/*
	@TABLE_ID INTEGER,
	@USER_ID INTEGER,
	@VIEW [NVARCHAR]
	*/

	private static String CALL_BW_GET_TBL_ROWS_NH="{CALL BW_GET_TBL_ROWS_NH(?,?,?,?,?)}";
	/*
	 BW_GET_TBL_ROWS_NH
	(
		@TABLE_ID INTEGER,
		@NH_ID INTEGER,
		@ROW_STATE INTEGER,
		@START_ROW_SEQ_ID [FLOAT],
	@END_ROW_SEQ_ID [FLOAT]
	*/

	private static String CALL_BW_GET_TBL_ROWS_NH_I_CHD="{CALL BW_GET_TBL_ROWS_NH_I_CHD(?,?,?,?,?)}";
		/*
		 BW_GET_TBL_ROWS_NH_I_CHD
		(
			@TABLE_ID INTEGER,
			@NH_ID INTEGER,
			@ROW_STATE INTEGER,
			@START_ROW_SEQ_ID [FLOAT],
		@END_ROW_SEQ_ID [FLOAT]
	*/

	private static String CALL_BW_GET_TBL_ROWS_NH_A_CHD="{CALL BW_GET_TBL_ROWS_NH_A_CHD(?,?,?,?,?)}";
		/*
		 BW_GET_TBL_ROWS_NH_A_CHD
		(
			@TABLE_ID INTEGER,
			@NH_ID INTEGER,
			@ROW_STATE INTEGER,
			@START_ROW_SEQ_ID [FLOAT],
		@END_ROW_SEQ_ID [FLOAT]
	*/


private static String CALL_BW_GET_TBL_ROWS_BL="{CALL BW_GET_TBL_ROWS_BL(?,?,?,?)}";
	/*
		BW_GET_TBL_ROWS_BL
(
	@TABLE_ID INTEGER,
	@START_ROW_SEQ_ID [FLOAT],
	@END_ROW_SEQ_ID [FLOAT],
	@BASELINE_ID INTEGER
	)
	*/

	private static String CALL_BW_GET_TBL_ROWS_BL_USER="{CALL BW_GET_TBL_ROWS_BL_USER(?,?,?,?,?)}";
	/*
	 BW_GET_TBL_ROWS_BL_USER
	(
		@TABLE_ID INTEGER,
		@USER_ID INTEGER,
		@START_ROW_SEQ_ID [FLOAT],
		@END_ROW_SEQ_ID [FLOAT],
		@BASELINE_ID INTEGER
	*/

	private static String CALL_BW_GET_TBL_ROWS_BL_NH="{CALL BW_GET_TBL_ROWS_BL_NH(?,?,?,?,?)}";
	/*
	BW_GET_TBL_ROWS_BL_NH
	(
		@TABLE_ID INTEGER,
		@NH_ID INTEGER,
		@START_ROW_SEQ_ID [FLOAT],
		@END_ROW_SEQ_ID [FLOAT],
		@BASELINE_ID INTEGER
)
	*/

	private static String CALL_BW_GET_TBL_ROWS_BL_NH_I_CHD="{CALL BW_GET_TBL_ROWS_BL_NH_I_CHD(?,?,?,?,?)}";
		/*
		 BW_GET_TBL_ROWS_BL_NH_I_CHD
		(
			@TABLE_ID INTEGER,
			@NH_ID INTEGER,
			@START_ROW_SEQ_ID [FLOAT],
			@END_ROW_SEQ_ID [FLOAT],
			@BASELINE_ID INTEGER
)
	*/

	private static String CALL_BW_GET_TBL_ROWS_BL_NH_A_CHD="{CALL BW_GET_TBL_ROWS_BL_NH_A_CHD(?,?,?,?,?)}";
		/*
		  BW_GET_TBL_ROWS_BL_NH_A_CHD
		 (
		 	@TABLE_ID INTEGER,
		 	@NH_ID INTEGER,
		 	@START_ROW_SEQ_ID [FLOAT],
		 	@END_ROW_SEQ_ID [FLOAT],
		 	@BASELINE_ID INTEGER
)
	*/








    /** Creates new RowManager */
    public RowManager() {
    }
	////////////////////////////////////////////////////////////////////////////////////////
	/*
	 * Sarang 02/18/05
	 * Assign rows from Excel
	 * Assumes that the all row ids between start and end row id will be
	 * assigned the a_emailAddress.
	 */
	///////////////////////////////////////////////////////////////////////////////////////
	public static int assignRows( Connection a_connection,
									int startRowId,
									int endRowId,
									String a_emailAddress,
									int tid)
	throws SQLException
	{

		CallableStatement callableStatement = null;
		int result = -1;

		try
		{
			callableStatement = a_connection.prepareCall(CALL_BW_UPD_ROW_OWNERSHIP);
			for (int rowId = startRowId; rowId <= endRowId; rowId++   )
			{
				System.out.println("Assign row : " + rowId);
				callableStatement.setInt(1, rowId);
				callableStatement.setString(2,a_emailAddress);
				callableStatement.setInt(3, tid);
				callableStatement.registerOutParameter(4,java.sql.Types.INTEGER);
				callableStatement.addBatch();

			}
			int updRes[] = callableStatement.executeBatch ();

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
	//////////////////////////////////////////////////////////////////////////////////////////////////
    public static RowContents getRowContents( Connection connection, int a_rowid , int a_userid, boolean getLatest, int a_baselineid )
    throws SystemException {
        ResultSet resultset1 = null;
        PreparedStatement preparedstatement1 = null;
        RowContents rc = null;

        Vector columnNames = new Vector();
        Hashtable cellsByColumnNames = new Hashtable();

        try {


            preparedstatement1 = connection.prepareStatement(CALL_BW_GET_ROWCELLS_BY_ROW_ID);

            preparedstatement1.setInt(1,a_rowid);

            resultset1 = preparedstatement1.executeQuery();

            while ( resultset1.next() ) {
                int     a_columnid;
                int     a_column_sequence_number;
                String  a_columnname;

                int     a_cellid;
                String  a_celltype;
                String  a_cellstringvalue;
                int     a_cellintvalue;
                double  a_celldoublevalue;
                int     a_celltablevalue;
                String  a_celltableName;
                int row_owner_id = -1;
				int is_active = 0;
                int row_creation_tid = -1;


                a_columnid = resultset1.getInt("COLUMN_ID");
                a_column_sequence_number = resultset1.getInt("COLUMN_SEQUENCE_NUMBER");
                a_columnname = resultset1.getString("COLUMN_NAME");

                a_cellid = resultset1.getInt("CELL_ID");
                a_celltype = resultset1.getString("CELL_TYPE");
                a_cellstringvalue = resultset1.getString("CELL_STRING_VALUE");
                a_cellintvalue = resultset1.getInt("CELL_INTEGER_VALUE");
                a_celldoublevalue = resultset1.getDouble("CELL_DOUBLE_VALUE");
                a_celltablevalue = resultset1.getInt("CELL_TBL_VALUE");
                a_celltableName = resultset1.getString("CELL_TBL_NAME");

                row_owner_id = resultset1.getInt("OWNER_ID");
				is_active = resultset1.getInt("IS_ACTIVE");
				row_creation_tid = resultset1.getInt("ROW_TID");
				int  col_tid = resultset1.getInt("COL_TID");
				int  col_isActive = resultset1.getInt("COL_ACTIVE");
				int row_ownerTid = resultset1.getInt("ROW_OWNER_TID");

				int row_creator_userid = resultset1.getInt("ROW_CREATOR_ID");
				String row_owner_name = resultset1.getString("ROW_OWNER_NAME");
				String a_formula = resultset1.getString("FORMULA");

                columnNames.addElement(a_columnname);

                Cell cl = new  Cell(a_cellid, a_columnid, a_rowid, a_celltype, a_cellstringvalue, a_cellintvalue, a_celldoublevalue, a_celltablevalue, a_celltableName, a_formula );
                cellsByColumnNames.put(a_columnname,cl);

            }
            rc = new RowContents( columnNames, cellsByColumnNames );
            return rc;
        }
        catch(SQLException sqlexception) {
            throw new SystemException(sqlexception);
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

    }

	//////////////////////////////////////////////////////////////////////////////////////////////////
	// This method is used specialy to handle waterfall model report
    public static Vector getRowContents( Connection connection, String a_rowids, String a_columnIds,int aitableId,int aiuserId,int aimemberid)
    throws SystemException
	{
        ResultSet resultset1 = null;
        PreparedStatement preparedstatement1 = null;

		Vector lvRows = new Vector();
		Vector lvRowsCells = new Vector();
		int RowId = 0;
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));

        try {

            preparedstatement1 = connection.prepareStatement(CALL_BW_GET_ROWCELLS);

            preparedstatement1.setString(1,a_rowids);
			preparedstatement1.setString(2,a_columnIds);
			preparedstatement1.setInt(3,aitableId);
			preparedstatement1.setInt(4,aiuserId);
			preparedstatement1.setInt(5,aimemberid);

            resultset1 = preparedstatement1.executeQuery();

            while ( resultset1.next() ) {
				int		cellRowId;
				int     a_rowid;
				int     a_columnid;
                int     a_column_sequence_number;
                String  a_columnname;

                int     a_cellid;
                String  a_celltype;
                String  a_cellstringvalue;
                int     a_cellintvalue;
                double  a_celldoublevalue;
                int     a_celltablevalue;
				String  a_celltableName;

				int row_owner_id = -1;
				int is_row_active = 0;
				int row_creation_tid = -1;
				int col_creation_tid = -1;
				int is_col_active = 0;

				int     a_celltransid = -1;
				//java.sql.Timestamp a_createdOnDate = resultset1.getTimestamp("CREATED_ON", cal);
				int a_createdBy = 0;
				String description = "";
                String comment = "";
				String emailAddress = "";


				a_rowid = resultset1.getInt("ROW_ID");
				cellRowId = a_rowid;
				a_columnid = resultset1.getInt("COLUMN_ID");
                a_column_sequence_number = resultset1.getInt("COLUMN_SEQUENCE_NUMBER");
                a_columnname = resultset1.getString("COLUMN_NAME");

                a_cellid = resultset1.getInt("CELL_ID");
                a_celltype = resultset1.getString("CELL_TYPE");
                a_cellstringvalue = "";//resultset1.getString("CELL_STRING_VALUE");
                a_cellintvalue = resultset1.getInt("CELL_INTEGER_VALUE");
                a_celldoublevalue = resultset1.getDouble("CELL_DOUBLE_VALUE");
                a_celltablevalue = resultset1.getInt("CELL_TBL_VALUE");
                a_celltableName = resultset1.getString("CELL_TBL_NAME");

				row_owner_id = resultset1.getInt("ROW_OWNER_ID");
				is_row_active = resultset1.getInt("ROW_ACTIVE");
				row_creation_tid = resultset1.getInt("ROW_TID");
				col_creation_tid = resultset1.getInt("COL_TID");
				is_col_active = resultset1.getInt("COL_ACTIVE");

				a_celltransid = resultset1.getInt("CELL_TID");
				java.sql.Timestamp a_createdOnDate = resultset1.getTimestamp("CREATED_ON", cal);
				a_createdBy = resultset1.getInt("CREATED_BY");
				description = resultset1.getString("DESCRIPTION");
                comment = resultset1.getString("COMMENT_");
				emailAddress = resultset1.getString("CELL_OWNER_NAME");

				Transaction cellTransaction = new Transaction(a_celltransid, a_createdBy, emailAddress, a_createdOnDate.getTime(),description, comment );

				VersionedCell cl=  new VersionedCell(a_cellid,a_columnid,a_columnname ,a_rowid,a_celltype,a_cellstringvalue,a_cellintvalue,a_celldoublevalue,a_celltablevalue,a_celltableName,cellTransaction,null) ;

				if(RowId == 0 )
					RowId = cellRowId;

				if(RowId != cellRowId)
				{
					lvRows.add(lvRowsCells);
					lvRowsCells = new Vector();
					RowId = cellRowId;
				}

				lvRowsCells.add(cl);
            }
			if(lvRowsCells.size() > 0)
			{
				lvRows.add(lvRowsCells);
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
                if(resultset1!= null)
					resultset1.close();
				if(preparedstatement1!= null)
					preparedstatement1.close();
            }
            catch(SQLException sqlexception1) {
                throw new SystemException(sqlexception1);
            }
        }

		return lvRows;
    }

    public static Hashtable getRowsByTable( Connection connection, int a_table_id , int a_userid, boolean getLatest, int a_baselineid )
    throws SystemException {
        ResultSet resultset1 = null;
        PreparedStatement preparedstatement1 = null;
        Hashtable rows = new Hashtable();
        try {


            preparedstatement1 = connection.prepareStatement(CALL_BW_GET_ROWS_BY_TBL_ID);

            preparedstatement1.setInt(1,a_table_id);

            resultset1 = preparedstatement1.executeQuery();

            while ( resultset1.next() ) {
                int     a_row_id;
                String  a_row_name;

                a_row_id = resultset1.getInt("ROW_ID");
                a_row_name = resultset1.getString("ROW_NAME");
                rows.put(new Integer(a_row_id), a_row_name);
            }

        }
        catch(SQLException sqlexception) {
            throw new SystemException(sqlexception);

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

        return rows;

    }



     public static Vector getRowsByTableSortedByRowId( Connection connection, int a_table_id , int a_userid, boolean getLatest, int a_baselineid )
	    throws SystemException {
	        ResultSet resultset1 = null;
	        PreparedStatement preparedstatement1 = null;
	        Vector rows = new Vector();
	        try {


	            preparedstatement1 = connection.prepareStatement(CALL_BW_GET_ROWS_BY_TBL_ID);

	            preparedstatement1.setInt(1,a_table_id);

	            resultset1 = preparedstatement1.executeQuery();

	            while ( resultset1.next() ) {
	                int     a_row_id;
	                String  a_row_name;
					float a_row_sequence_number;
					 int row_owner_id = -1;
					int is_active = 0;
                	int row_creation_tid = -1;

	                a_row_id = resultset1.getInt("ROW_ID");
	                a_row_name = resultset1.getString("ROW_NAME");
	                a_row_sequence_number = resultset1.getFloat("ROW_SEQUENCE_NUMBER");
	                 row_owner_id = resultset1.getInt("OWNER_ID");
					is_active = resultset1.getInt("IS_ACTIVE");
					row_creation_tid = resultset1.getInt("ROW_TID");
					int row_ownerTid = resultset1.getInt("ROW_OWNER_TID");

					int row_creator_userid = resultset1.getInt("ROW_CREATOR_ID");
					String row_owner_name =  resultset1.getString("ROW_OWNER_NAME");



	                Row r = new Row(a_row_id, a_row_name, a_row_sequence_number,row_creation_tid,row_owner_id,is_active, row_ownerTid,row_creator_userid,row_owner_name);
	                rows.add( r );
	            }

	        }
	        catch(SQLException sqlexception) {
	            throw new SystemException(sqlexception);

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

	        return rows;

    }

public static TableRowInfo getTableRows( Connection connection, int a_table_id , int a_userid, int a_nh_id, int a_baselineid, String viewPreference, int rowState, double startSeqId, double endSeqId )
	    throws SystemException
{
	ResultSet resultset1 = null;
	PreparedStatement preparedstatement1 = null;
	Vector rows = new Vector();
	Hashtable rowHash = new Hashtable();

	try {

			if (   viewPreference.equals( "LATEST" )  ||
					viewPreference.equals( "DESIGN" )  ||
					viewPreference.equals( "LATEST_BY_USER" )  ||
					viewPreference.equals( "LATEST_VIEW_OF_ALL_USERS" )  ||
					viewPreference.equals( "LATEST_VIEW_OF_ALL_CHILDREN" )  ||
					viewPreference.equals( "LATEST_VIEW_OF_ALL_USERS_IN_ANY_NH" )  ||
					viewPreference.equals( "LATEST_VIEW_OF_ALL_USERS_IN_ANY_CHILDREN_NH" ) ||
					(viewPreference.indexOf("?") == 0 )||
					(viewPreference.length() > 0 )
				)
			{

				if ( a_baselineid < 1 )
				{
					preparedstatement1 = connection.prepareStatement(CALL_BW_GET_TBL_FILTERED_ROWS);
					preparedstatement1.setInt(1,a_table_id);
					preparedstatement1.setInt(2,a_userid);
					preparedstatement1.setString(3,viewPreference);					
				}
				else
				{
					System.out.println("getting rows for baseline " + a_baselineid );
					preparedstatement1 = connection.prepareStatement(CALL_BW_GET_TBL_ROWS_BL);
					preparedstatement1.setInt(1,a_table_id);
					preparedstatement1.setDouble(2,startSeqId);
					preparedstatement1.setDouble(3,endSeqId);
					preparedstatement1.setInt(4,a_baselineid);
				}

			}
			else if (   viewPreference.equals( "MY_ROWS" ))
			{
				if ( a_baselineid < 1 )
				{
					preparedstatement1 = connection.prepareStatement(CALL_BW_GET_TBL_ROWS_USER);
					preparedstatement1.setInt(1,a_table_id);
					preparedstatement1.setInt(2,a_userid);
					preparedstatement1.setInt(3,rowState);
					preparedstatement1.setDouble(4,startSeqId);
					preparedstatement1.setDouble(5,endSeqId);
				}
				else
				{
					preparedstatement1 = connection.prepareStatement(CALL_BW_GET_TBL_ROWS_BL_USER);
					preparedstatement1.setInt(1,a_table_id);
					preparedstatement1.setInt(2,a_userid);
					preparedstatement1.setDouble(3,startSeqId);
					preparedstatement1.setDouble(4,endSeqId);
					preparedstatement1.setInt(5,a_baselineid);

				}
			}
			else if (   viewPreference.equals( "LATEST_ROWS_OF_ALL_USERS_IN_ANY_NH" ))
			{
				if ( a_baselineid < 1 )
				{
					preparedstatement1 = connection.prepareStatement(CALL_BW_GET_TBL_ROWS_NH);
					preparedstatement1.setInt(1,a_table_id);
					preparedstatement1.setInt(2,a_nh_id);
					preparedstatement1.setInt(3,rowState);
					preparedstatement1.setDouble(4,startSeqId);
					preparedstatement1.setDouble(5,endSeqId);
				}
				else
				{
					preparedstatement1 = connection.prepareStatement(CALL_BW_GET_TBL_ROWS_BL_NH);
					preparedstatement1.setInt(1,a_table_id);
					preparedstatement1.setInt(2,a_nh_id);
					preparedstatement1.setDouble(3,startSeqId);
					preparedstatement1.setDouble(4,endSeqId);
					preparedstatement1.setInt(5,a_baselineid);
				}
			}
			else if (   viewPreference.equals( "LATEST_ROWS_OF_ALL_USERS_IN_MY_NH" ))
			{
				if ( a_baselineid < 1 )
				{
					preparedstatement1 = connection.prepareStatement(CALL_BW_GET_TBL_ROWS_NH);
					preparedstatement1.setInt(1,a_table_id);
					preparedstatement1.setInt(2,a_nh_id);
					preparedstatement1.setInt(3,rowState);
					preparedstatement1.setDouble(4,startSeqId);
					preparedstatement1.setDouble(5,endSeqId);
				}
				else
				{
					preparedstatement1 = connection.prepareStatement(CALL_BW_GET_TBL_ROWS_BL_NH);
					preparedstatement1.setInt(1,a_table_id);
					preparedstatement1.setInt(2,a_nh_id);
					preparedstatement1.setDouble(3,startSeqId);
					preparedstatement1.setDouble(4,endSeqId);
					preparedstatement1.setInt(5,a_baselineid);
				}
			}
			else if (   viewPreference.equals( "LATEST_ROWS_OF_ALL_USERS_IN_MY_NH_AND_IMM_CHD" ))
			{
				if ( a_baselineid < 1 )
				{
					preparedstatement1 = connection.prepareStatement(CALL_BW_GET_TBL_ROWS_NH_I_CHD);
					preparedstatement1.setInt(1,a_table_id);
					preparedstatement1.setInt(2,a_nh_id);
					preparedstatement1.setInt(3,rowState);
					preparedstatement1.setDouble(4,startSeqId);
					preparedstatement1.setDouble(5,endSeqId);
				}
				else
				{
					preparedstatement1 = connection.prepareStatement(CALL_BW_GET_TBL_ROWS_BL_NH_I_CHD);
					preparedstatement1.setInt(1,a_table_id);
					preparedstatement1.setInt(2,a_nh_id);
					preparedstatement1.setDouble(3,startSeqId);
					preparedstatement1.setDouble(4,endSeqId);
					preparedstatement1.setInt(5,a_baselineid);
				}
			}
			else if (   viewPreference.equals( "LATEST_ROWS_OF_ALL_USERS_IN_MY_NH_AND_ALL_CHD" ))
			{
				if ( a_baselineid < 1 )
				{
					preparedstatement1 = connection.prepareStatement(CALL_BW_GET_TBL_ROWS_NH_A_CHD);
					preparedstatement1.setInt(1,a_table_id);
					preparedstatement1.setInt(2,a_nh_id);
					preparedstatement1.setInt(3,rowState);
					preparedstatement1.setDouble(4,startSeqId);
					preparedstatement1.setDouble(5,endSeqId);
				}
				else
				{
					preparedstatement1 = connection.prepareStatement(CALL_BW_GET_TBL_ROWS_BL_NH_A_CHD);
					preparedstatement1.setInt(1,a_table_id);
					preparedstatement1.setInt(2,a_nh_id);
					preparedstatement1.setDouble(3,startSeqId);
					preparedstatement1.setDouble(4,endSeqId);
					preparedstatement1.setInt(5,a_baselineid);
				}
			}

			resultset1 = preparedstatement1.executeQuery();

			while ( resultset1.next() )
			{
				int     a_row_id;
				String  a_row_name;
				float a_row_sequence_number;
				int row_owner_id = -1;
				int is_active = 0;
				int row_creation_tid = -1;

				a_row_id = resultset1.getInt("ROW_ID");
				a_row_name = resultset1.getString("ROW_NAME");
				a_row_sequence_number = resultset1.getFloat("ROW_SEQUENCE_NUMBER");
				 row_owner_id = resultset1.getInt("OWNER_ID");
				is_active = resultset1.getInt("IS_ACTIVE");
				row_creation_tid = resultset1.getInt("ROW_TID");
				int row_ownerTid = resultset1.getInt("ROW_OWNER_TID");

				int row_creator_userid = resultset1.getInt("ROW_CREATOR_ID");
				String row_owner_name =  resultset1.getString("ROW_OWNER_NAME");
				Row r = new Row(a_row_id, a_row_name, a_row_sequence_number,row_creation_tid,row_owner_id,is_active, row_ownerTid,row_creator_userid,row_owner_name);
				rows.add( r );
				rowHash.put( new Integer(a_row_id), r );
			}

	}
	catch(SQLException sqlexception) {
		throw new SystemException(sqlexception);

	}
	finally
	{
		try
		{
			if ( resultset1 != null )
			{
				resultset1.close();
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

	boolean rowStateB = true;

	if ( rowState == 0 )
		rowStateB = false;

	TableRowInfo tbRowInfo = new TableRowInfo(	a_table_id, rows, rowHash, rowStateB );


	return tbRowInfo;

}

public static TableRowInfo getTableRowsT( Connection connection,
										 int a_table_id ,
										 int a_userid,
										 int a_nh_id,
										 int a_baselineid,
										 String viewPreference,
										 double startSeqId,
										 double endSeqId,
										 int asOfTid,
										 long asOfDate)
throws SystemException
{
	ResultSet resultset1 = null;
	PreparedStatement preparedstatement1 = null;
	Vector rows = new Vector();
	Hashtable rowHash = new Hashtable();

	try
	{
		if ( a_baselineid < 1 )
		{
			String CALL_BW_GET_TBL_ROWS_T="{CALL BW_GET_TBL_ROWS_T(?,?,?,?,?,?,?,?)}";
/*
			@TABLE_ID INTEGER,
			@START_ROW_SEQ_ID [FLOAT],
			@END_ROW_SEQ_ID [FLOAT],
			@USER_ID INTEGER,
			@NH_ID INTEGER,
			@VIEW_PREF VARCHAR(256),
			@TX_ID INTEGER
*/
System.out.println("a_table_id "+a_table_id+" startSeqId "+startSeqId+" endSeqId "+endSeqId+" a_userid "+a_userid+" a_nh_id "+a_nh_id+" viewPreference "+viewPreference+" asOfTid "+asOfTid+" asOfDate "+asOfDate);
			preparedstatement1 = connection.prepareStatement(CALL_BW_GET_TBL_ROWS_T);
			preparedstatement1.setInt(1,a_table_id);
			preparedstatement1.setDouble(2,startSeqId);
			preparedstatement1.setDouble(3,endSeqId);
			preparedstatement1.setInt(4,a_userid);
			preparedstatement1.setInt(5,a_nh_id);
			preparedstatement1.setString(6,viewPreference);
			preparedstatement1.setInt(7,asOfTid);
			Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
			if (asOfDate > -1)
			{
				preparedstatement1.setTimestamp(8, new java.sql.Timestamp(asOfDate), cal);
			}
			else
			{
				preparedstatement1.setTimestamp(8, new java.sql.Timestamp((new java.util.Date()).getTime()), cal);
			}
		}


		resultset1 = preparedstatement1.executeQuery();

		while ( resultset1.next() )
		{
			int     a_row_id;
			String  a_row_name;
			float a_row_sequence_number;
			int row_owner_id = -1;
			int is_active = 0;
			int row_creation_tid = -1;

			a_row_id = resultset1.getInt("ROW_ID");
			a_row_name = resultset1.getString("ROW_NAME");
			a_row_sequence_number = resultset1.getFloat("ROW_SEQUENCE_NUMBER");
			row_owner_id = resultset1.getInt("OWNER_ID");
			is_active = resultset1.getInt("IS_ACTIVE");
			row_creation_tid = resultset1.getInt("ROW_TID");
			int row_ownerTid = resultset1.getInt("ROW_OWNER_TID");

			int row_creator_userid = resultset1.getInt("ROW_CREATOR_ID");
			String row_owner_name =  resultset1.getString("ROW_OWNER_NAME");
			Row r = new Row(a_row_id, a_row_name, a_row_sequence_number,row_creation_tid,row_owner_id,is_active, row_ownerTid,row_creator_userid,row_owner_name);
			rows.add( r );
			rowHash.put( new Integer(a_row_id), r );
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
			if ( resultset1 != null )
			{
				resultset1.close();
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


	TableRowInfo tbRowInfo = new TableRowInfo(	a_table_id, rows, rowHash, true );

	return tbRowInfo;
}

public static TableRowInfo getTableRowsAll( Connection connection,
										 int a_table_id ,
										 int a_userid,
										 int a_nh_id,
										 int a_baselineid,
										 String viewPreference,
										 double startSeqId,
										 double endSeqId )
throws SystemException
{
	ResultSet resultset1 = null;
	PreparedStatement preparedstatement1 = null;
	Vector rows = new Vector();
	Hashtable rowHash = new Hashtable();

	try
	{
		if ( a_baselineid < 1 )
		{
			String CALL_BW_GET_TBL_ROWS_ALL="{CALL BW_GET_TBL_ROWS_ALL(?,?,?,?,?,?)}";
/*
			@TABLE_ID INTEGER,
			@START_ROW_SEQ_ID [FLOAT],
			@END_ROW_SEQ_ID [FLOAT],
			@USER_ID INTEGER,
			@NH_ID INTEGER,
			@VIEW_PREF VARCHAR(256)
*/
			preparedstatement1 = connection.prepareStatement(CALL_BW_GET_TBL_ROWS_ALL);
			preparedstatement1.setInt(1,a_table_id);
			preparedstatement1.setDouble(2,startSeqId);
			preparedstatement1.setDouble(3,endSeqId);
			preparedstatement1.setInt(4,a_userid);
			preparedstatement1.setInt(5,a_nh_id);
			preparedstatement1.setString(6,viewPreference);

		}


		resultset1 = preparedstatement1.executeQuery();

		while ( resultset1.next() )
		{
			int     a_row_id;
			String  a_row_name;
			float a_row_sequence_number;
			int row_owner_id = -1;
			int is_active = 0;
			int row_creation_tid = -1;

			a_row_id = resultset1.getInt("ROW_ID");
			a_row_name = resultset1.getString("ROW_NAME");
			a_row_sequence_number = resultset1.getFloat("ROW_SEQUENCE_NUMBER");
			row_owner_id = resultset1.getInt("OWNER_ID");
			is_active = resultset1.getInt("IS_ACTIVE");
			row_creation_tid = resultset1.getInt("ROW_TID");
			int row_ownerTid = resultset1.getInt("ROW_OWNER_TID");

			int row_creator_userid = resultset1.getInt("ROW_CREATOR_ID");
			String row_owner_name =  resultset1.getString("ROW_OWNER_NAME");
			Row r = new Row(a_row_id, a_row_name, a_row_sequence_number,row_creation_tid,row_owner_id,is_active, row_ownerTid,row_creator_userid,row_owner_name);
			rows.add( r );
			rowHash.put( new Integer(a_row_id), r );
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
			if ( resultset1 != null )
			{
				resultset1.close();
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


	TableRowInfo tbRowInfo = new TableRowInfo(	a_table_id, rows, rowHash, true );

	return tbRowInfo;
}



    public static void commitRow( Connection a_connection, int tid, Vector a_cellContent )
    throws SQLException {

        CallableStatement callablestatement = null;
        System.out.println("Commiting row::::::::::::::::");
        Iterator cellItr = a_cellContent.iterator();
        while ( cellItr.hasNext() ) {
            CellContents cc = (CellContents)cellItr.next();

            if ( cc.getType().equals("TABLE") ) {
                System.out.println("Setting table value for cell " + cc.getId() +" to " + cc.getTableValue());
                try {
                    callablestatement = a_connection.prepareCall(CALL_BW_UPD_TBL_CELL);
                    callablestatement.setInt(1, cc.getId());
                    callablestatement.setInt(2, cc.getTableValue());
                    callablestatement.setInt(3, tid);
                    callablestatement.setInt(3, tid);
                    callablestatement.setInt(4, 0);


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
            else {
                if( cc.getType().equals("FLOAT" ) ) {
                    System.out.println("Setting float value for cell " + cc.getId() +" to " + cc.getDoubleValue());
                    try {
                        callablestatement = a_connection.prepareCall(CALL_BW_UPD_DOUBLE_CELL);
                        callablestatement.setInt(1, cc.getId());
                        callablestatement.setDouble(2, cc.getDoubleValue());
                        callablestatement.setInt(3, tid);
                        callablestatement.setInt(4, 0);

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
                else {
                    if ( cc.getType().equals("INTEGER" )) {
                        try {
                            callablestatement = a_connection.prepareCall(CALL_BW_UPD_INTEGER_CELL);
                            callablestatement.setInt(1, cc.getId());
                            callablestatement.setInt(2, cc.getIntValue());
                            callablestatement.setInt(3, tid);
                            callablestatement.setInt(4, 0);

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
                            callablestatement = a_connection.prepareCall(CALL_BW_UPD_STRING_CELL);
                            callablestatement.setInt(1, cc.getId());
                            callablestatement.setString(2, cc.getStringValue());
                            callablestatement.setInt(3, tid);
                            callablestatement.setInt(4, 0);

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
                }


            }




        }




    }







    public static void purgeRow(Connection connection, int rowId )
    throws SystemException {
        CallableStatement callableStatement = null;
        String BW_PURGE_ROW = "{CALL BW_PURGE_ROW(?)}";

        try {
            callableStatement = connection.prepareCall(BW_PURGE_ROW);
            callableStatement.setInt(1, rowId);
            callableStatement.executeUpdate();
        } catch (SQLException sql1) {
            throw new SystemException(sql1);
        } finally {
            try {
                callableStatement.close();
            } catch (SQLException sql2) {
                throw new SystemException(sql2);
            }
        }
    }



      public static void deactivateRow(Connection connection, int rowId, int tid )
	    throws SystemException {
	        CallableStatement callableStatement = null;
	        String BW_DEACTIVATE_ROW = "{CALL BW_DEACTIVATE_ROW(?,?)}";

	        try {
	            callableStatement = connection.prepareCall(BW_DEACTIVATE_ROW);
	            callableStatement.setInt(1, rowId);
	            callableStatement.setInt(2, tid);
	            callableStatement.executeUpdate();
	        } catch (SQLException sql1) {
	            throw new SystemException(sql1);
	        } finally {
	            try {
	                callableStatement.close();
	            } catch (SQLException sql2) {
	                throw new SystemException(sql2);
	            }
	        }
    }
	public static boolean deactivateRows( Connection a_connection,
									  Vector rowIds,
									int tid)
	throws SystemException
	{
		boolean lbDeleted = false;
		PreparedStatement ps = null;
		CallableStatement cs = null;
		try
		{
			ps  = a_connection.prepareStatement("INSERT INTO BW_DEL_ROW(BW_ROW_ID, TX_ID) VALUES (?, ?)");
			Iterator ri = rowIds.iterator();
			while (ri.hasNext())
			{
				int rowId = ((Integer)ri.next()).intValue();
				//System.out.println("deactivating row : " + rowId);
				ps.setInt(1, rowId);
				ps.setInt(2, tid);
				ps.addBatch();
			}
			ps.executeBatch();
			ps.close();
			ps = null;


			cs = a_connection.prepareCall("{CALL BW_DEACTIVATE_ROWS(?)}");
			cs.setInt(1, tid);
			cs.executeUpdate();
			cs.close();
			cs = null;
			lbDeleted = true;
		}
		catch( SQLException sql1 )
		{
			lbDeleted = false;
			throw new SystemException(sql1);
		}
		finally
		{
			try
			{
				if (ps != null)
					ps.close();
				if (cs != null)
					cs.close();
			}
			catch( SQLException sql2 )
			{
				lbDeleted = false;
				throw new SystemException(sql2);
			}
		}
		return lbDeleted;
	}
     public static void activateRow(Connection connection, int rowId, int tid )
		    throws SystemException {
		        CallableStatement callableStatement = null;
		        String BW_ACTIVATE_ROW = "{CALL BW_ACTIVATE_ROW(?)}";

		        try {
		            callableStatement = connection.prepareCall(BW_ACTIVATE_ROW);
		            callableStatement.setInt(1, rowId);
		            callableStatement.setInt(2, tid);
		            callableStatement.executeUpdate();
		        } catch (SQLException sql1) {
		            throw new SystemException(sql1);
		        } finally {
		            try {
		                callableStatement.close();
		            } catch (SQLException sql2) {
		                throw new SystemException(sql2);
		            }
		        }
    }


    public static void TestgetRowContents()

    {
		Connection connection = null;
        try {
            DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
            connection = databaseloader.getConnection();
            RowContents rwcon = RowManager.getRowContents(connection,8,2,true,-1);

            Vector columnnames = rwcon.getColumnNames();
            Hashtable cellsByColumnNames = rwcon.getCellsByColumnName();


            Iterator columnNms = columnnames.iterator();
            String columnList = " ";
            String cellValues = " ";
            while (columnNms.hasNext()) {
                String columnName = (String)columnNms.next();
                columnList = columnList + "      " + columnName;
                com.boardwalk.table.Cell cl = (com.boardwalk.table.Cell)cellsByColumnNames.get(columnName);
                cellValues = cellValues + cl.getValueAsString() + "[" + cl.getType() + "]" + " ";
            }

            System.out.println(" " + columnList + " " );
            System.out.println(" " + cellValues + " " );



        }
        catch( Exception e ) {
            e.printStackTrace();
        }
         finally
		      {
				  try{

					  connection.close();
				  }
				  catch( Exception e ){ e.printStackTrace(); }

	  }
    }

    public static void TestgetRowsByTable(int a_table_id)

    {
        try {
            DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
            Connection connection = databaseloader.getConnection();
            Hashtable rows = RowManager.getRowsByTable(connection,a_table_id,2,true,-1);

            Enumeration keys = rows.keys();

            System.out.println(" PRINTING ROWS FROM getRowsByTable() ");
            while ( keys.hasMoreElements() ) {
                String row_name = (String)keys.nextElement();
                System.out.println(" ROW_NAME " + row_name + " ROW_ID " + (Integer)rows.get(row_name));
            }
            System.out.println(" DONE PRINTING ROWS FROM getRowsByTable() ");



        }
        catch( Exception e ) {
            e.printStackTrace();
        }
    }

    public static void TestcommitRow(Vector a_cellContents ) {
        TransactionManager tm = null;
        try {
            DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
            Connection connection = databaseloader.getConnection();
            tm = new TransactionManager(connection,2);
            int tid = tm.startTransaction();
            RowManager.commitRow( connection, tid, a_cellContents );
            tm.commitTransaction();
        }
        catch( Exception e ) {
            try {
                tm.rollbackTransaction();
            }
            catch( SQLException sql ) {
                sql.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        /*
        CellContents ccStr = new CellContents(21,"STRING","STRING_NEW",0,0,0);
        CellContents ccInt = new CellContents(22,"INTEGER","",9,0,0);
        CellContents ccFlt = new CellContents(23,"FLOAT","",0,9.9,0);
        CellContents ccTbl = new CellContents(24,"TABLE","",0,0,5);
        Vector ccContents = new Vector();

        ccContents.addElement(ccStr);
        ccContents.addElement(ccInt);
        ccContents.addElement(ccFlt);
        ccContents.addElement(ccTbl);

        RowManager.TestcommitRow(ccContents);
        RowManager.TestgetRowContents();
         */
        RowManager.TestgetRowsByTable(3);





    }

	public static boolean isRowActive( int rowId)
	{
		boolean retisActiveRow = false;
		int isActiveRow = -1;
		PreparedStatement preparedstatement = null;
        ResultSet rs = null;
		 try
		 {
            DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
            Connection connection = databaseloader.getConnection();

			String lsQuery = "SELECT IS_ACTIVE FROM BW_ROW WHERE ID = ?";
			preparedstatement = connection.prepareStatement(lsQuery);
			preparedstatement.setInt(1,rowId);
			rs = preparedstatement.executeQuery();
			while( rs.next())
			{
				isActiveRow = rs.getInt("IS_ACTIVE");
			}
			//System.out.println("(((((((((((((((isActiveRow)))))))))))))"+isActiveRow);
			if(isActiveRow == 1)
				retisActiveRow = true;

		 }
		catch( SQLException sql )
		{
			sql.printStackTrace();
        }
		//System.out.println("(((((((((((((((isActiveRow)))))))))))))"+retisActiveRow);
		return retisActiveRow;
	}

}
