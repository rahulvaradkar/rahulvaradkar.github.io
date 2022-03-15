
package com.boardwalk.table;
import java.util.*;
import java.io.*;

import com.boardwalk.database.DatabaseLoader;
import com.boardwalk.exception.NeighborhoodException;
import com.boardwalk.exception.SystemException;
import com.boardwalk.database.*;
import com.boardwalk.excel.*;
import com.boardwalk.query.*;
import boardwalk.common.BoardwalkUtility;

import java.sql.*;                  // JDBC package
import javax.sql.*;                 // extended JDBC package


/**
 *
 * @author  administrator
 * @version
 */
public class TableViewManager {
	public final static String Seperator = new Character((char)1).toString();
	public final static String ContentDelimeter = new Character((char)2).toString();

	public static RowClassification getRowClassification(Connection connection, int tableId)
		throws SystemException
	{
		ResultSet rs = null;
		RowClassification rc = new RowClassification();
		String CALL_BW_GET_ROW_CLASSIFICATION = "{CALL BW_GET_ROW_CLASSIFICATION(?)}";
		CallableStatement callableStatement = null;

		try
		{
			callableStatement = connection.prepareCall(CALL_BW_GET_ROW_CLASSIFICATION);
			callableStatement.setInt(1, tableId);
			rs = callableStatement.executeQuery ();
			while (rs.next())
			{
				int rowId = rs.getInt(1);
				String columnName = rs.getString(2);
				String value = rs.getString(3);

				rc.put(rowId, columnName, value);
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			throw new SystemException(e);
		}
		finally
		{
			try
			{
				rs.close();
				callableStatement.close();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
				throw new SystemException(e);
			}
		}

		return rc;
	}

	public static void setCriteriaTable(Connection connection, int tableId, int criteriaTableId, int tid)
		throws SystemException
	{
		String CALL_BW_CR_CRIT_DEF = "{CALL BW_CR_CRIT_DEF(?,?,?)}";
		CallableStatement callableStatement = null;

		try
		{
			callableStatement = connection.prepareCall(CALL_BW_CR_CRIT_DEF);
			callableStatement.setInt(1, tableId);
			callableStatement.setInt(2, criteriaTableId);
			callableStatement.setInt(3, tid);
			callableStatement.executeUpdate();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			throw new SystemException(e);
		}
		finally
		{
			try
			{
				callableStatement.close();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
				throw new SystemException(e);
			}
		}
	}

	public static int getCriteriaTable(Connection connection, int tableId, int userId)
		throws SystemException
	{
		CallableStatement cs = null;
		int crTableId = -1;
		try
		{
			//String query = "SELECT BW_CRIT_TBL.BW_CR_TBL_ID FROM BW_CRIT_TBL WHERE BW_CRIT_TBL.BW_TBL_ID = ?";
			cs = connection.prepareCall("{CALL BW_GET_ROLLUP_CRITERIA_TBL(?,?,?)}");
			cs.setInt(1, tableId);
			cs.setInt(2, userId);
			cs.registerOutParameter (3, java.sql.Types.INTEGER );

			cs.execute ();
			crTableId = cs.getInt(3);

		}
		catch (Exception e)
		{
			e.printStackTrace ();
			throw new SystemException(e);
		}
		finally
		{
			try
			{
				cs.close();
				cs = null;
			}
			catch (SQLException e)
			{
				e.printStackTrace ();
				throw new SystemException(e);
			}
		}

		System.out.println("getCriteriaTable() = " + crTableId);
		return crTableId;
	}
	public static Vector getLatestCellsForTable(Connection connection, int tableId, int userId,  int memberId, int nhId, String viewPref, int crTableId)
	throws SystemException
	{
		ResultSet rs = null;
		PreparedStatement ps = null;
		Vector cells = null;
		TableTreeNode currentTable = null;
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		try
		{
			String rowQuery = getRowQuery(connection, tableId, userId, crTableId, true, viewPref);
			String query = "	SELECT " +
							"	BWCELL.ID AS CELL_ID, " +
							"	BW_ROW.ID AS ROW_ID, " +
							"	c.colid AS COLUMN_ID, " +
							"	BWCELL.STRING_VALUE, " +
							"	CELL_INTEGER_VALUE = -1, " +
							"	CELL_DOUBLE_VALUE =1.1, " +
							"	BW_TXS.TX_ID, " +
							"	BW_TXS.CREATED_BY AS TX_CREATED_BY, " +
							"	BW_TXS.CREATED_ON, " +
							"	BW_TXS.DESCRIPTION, " +
							"	BW_TXS.COMMENT_, " +
							"	BW_USER.EMAIL_ADDRESS, " +
							"	BWCELL.FORMULA	 " +
							"FROM     " +
							"( " +
							rowQuery +
							" ) AS BWROW, " +
							"	BW_ROW, " +
							"	BW_CELL AS BWCELL, " +
							"	BW_TXS, " +
							"	BW_USER, " +
							"	BW_GetColumnAccess(?, ?, ?) c, " +
							"   BW_COLUMN COL " +
							"WHERE	 " +
							"		 BWCELL.BW_ROW_ID = BW_ROW.ID " +
							"	AND	 BWROW.ID = BW_ROW.ID " +
							"	AND	 BW_ROW.IS_ACTIVE = 1 " +
							"	AND  BWCELL.BW_COLUMN_ID = c.colid " +
							"	AND	 BWCELL.TX_ID = BW_TXS.TX_ID " +
							"	AND  BW_TXS.CREATED_BY = BW_USER.ID " +
							"	AND  COL.ID = C.COLID " +
							"ORDER BY BW_ROW.SEQUENCE_NUMBER, COL.SEQUENCE_NUMBER ";
//			System.out.println("query to fetch cells = " + query);
			ps = connection.prepareStatement(query);

			//ps.setInt(1, tableId);
			ps.setInt(1, tableId);
			ps.setInt(2, userId);
			ps.setInt(3, memberId);

			rs = ps.executeQuery();
			cells = new Vector(rs.getFetchSize());
			while (rs.next())
			{
				int a_rowid;
				int a_columnid;
				int a_cellid;
				String a_cellstringvalue;
				int a_cellintvalue;
				int a_celltablevalue = -1;
				double a_celldoublevalue;
				int tid = -1;
				int userid = -1;
				String emailAddress = "";
				String description = "";
				String comment = "";
				String a_celltableName = "Not Set";
				String a_cellFormula = null;

				a_rowid = rs.getInt("ROW_ID");
				a_columnid = rs.getInt("COLUMN_ID");
				a_cellid = rs.getInt("CELL_ID");
				a_cellstringvalue = rs.getString("STRING_VALUE");
				a_cellintvalue = rs.getInt("CELL_INTEGER_VALUE");
				a_celldoublevalue = rs.getDouble("CELL_DOUBLE_VALUE");
				tid = rs.getInt("TX_ID");
				userid = rs.getInt("TX_CREATED_BY");

				java.sql.Timestamp a_createdOnDate = rs.getTimestamp("CREATED_ON", cal);
				description = rs.getString("DESCRIPTION");
				comment = rs.getString("COMMENT_");
				emailAddress = rs.getString("EMAIL_ADDRESS");
				//if (getLatest == true || getTableAsOf == true || getTableDelta == true)
				//{
				a_cellFormula = rs.getString("FORMULA");
				//System.out.println("Got cell with formula " + a_cellFormula);
				//}
				Transaction cellTransaction = new Transaction(tid, userid, emailAddress, a_createdOnDate.getTime(), description, comment);
				String a_celltype;
				String a_columnname;
				a_celltype = "STRING";
				a_columnname = "";

				VersionedCell cl = new VersionedCell
													(
														a_cellid,
														a_columnid,
														a_columnname,
														a_rowid,
														a_celltype,
														a_cellstringvalue,
														a_cellintvalue,
														a_celldoublevalue,
														a_celltablevalue,
														a_celltableName,
														cellTransaction,
														a_cellFormula
													);
				cells.addElement(cl);
			}


		}
		catch (SQLException sqlexception)
		{

			throw new SystemException(sqlexception);
		}
		finally
		{
			try
			{

				if (rs != null)
				{
					rs.close();
				}

				if (ps != null)
				{
					ps.close();
				}

				rs = null;
				ps = null;
			}
			catch (SQLException sqlexception1)
			{
				throw new SystemException(sqlexception1);
			}
		}
		System.out.println("Fetched " + cells.size() + " cells");
		return cells;
	}

	public static TableRowInfo getFiltredTableRows(Connection connection, int a_table_id, int userId, String asRowQuery, int a_basline_id)
	throws SystemException
	{
		// Previously this method used to work only for normal rows without any consideration
		// for baseline. Now based on the baseline parameter we can flip the query and use the
		// same method. Later on we can modify only the where clause.
		ResultSet rs			= null;
		PreparedStatement ps	= null;
		String query			= null;
		Vector rows				= new Vector();
		Hashtable rowHash		= new Hashtable();

		try
		{
			if(a_basline_id == -1)
			{
				query = 
						" SELECT BWROW.ID AS ROW_ID, " +
						"		BWROW.NAME AS ROW_NAME, " +
						"		BWROW.SEQUENCE_NUMBER AS ROW_SEQUENCE_NUMBER, " +
						"		BWROW.OWNER_ID, " +
						"		BWROW.IS_ACTIVE, " +
						"		BWROW.TX_ID AS ROW_TID, " +
						"		BWROW.OWNER_TID AS ROW_OWNER_TID, " +
						"		BW_ROW_CREATOR_TX.CREATED_BY AS ROW_CREATOR_ID, " +
						"		BW_ROW_OWNER.EMAIL_ADDRESS AS ROW_OWNER_NAME " +
						"FROM (" + asRowQuery + ") AS R, " +
						"		BW_ROW AS BWROW, BW_TXS AS BW_ROW_CREATOR_TX,	 " +
						"		BW_USER AS BW_ROW_OWNER " +
						"WHERE  BWROW.IS_ACTIVE = 1 " +
						"AND   	BW_ROW_CREATOR_TX.TX_ID = BWROW.TX_ID " +
						"AND    BW_ROW_OWNER.ID = BWROW.OWNER_ID " +
						"AND	BWROW.ID = R.ID " +
						"ORDER BY BWROW.SEQUENCE_NUMBER ";
			}
			else
			{
				// With baseline
				query = "  CREATE TABLE  #ACCESSIBLE_ROWS " +
						" ( ROWID  INT  PRIMARY KEY NOT NULL ) " +
						" INSERT INTO #ACCESSIBLE_ROWS " + asRowQuery +

						" SELECT BWROW.ID AS ROW_ID, BWROW.NAME AS ROW_NAME,"+
						"		BW_BL_ROW.SEQUENCE_NUMBER AS ROW_SEQUENCE_NUMBER,"+
						"		BW_BL_ROW.OWNER_ID, IS_ACTIVE = 1, BWROW.TX_ID AS ROW_TID,"+
						"		BW_BL_ROW.OWNER_TID AS ROW_OWNER_TID, "+
						"		BW_ROW_CREATOR_TX.CREATED_BY AS ROW_CREATOR_ID, "+
						"		BW_ROW_OWNER.EMAIL_ADDRESS AS ROW_OWNER_NAME "+
						"FROM (" + asRowQuery + ") AS RQ, " +
						" BW_ROW AS BWROW,BW_TXS AS BW_ROW_CREATOR_TX,"+
						"		BW_USER AS BW_ROW_OWNER,"+
						"		BW_BL_ROW "+
						" WHERE BWROW.BW_TBL_ID = "+a_table_id +
						" AND	BWROW.ID = BW_BL_ROW.ROW_ID"+
						" AND   BW_BL_ROW.BASELINE_ID = "+a_basline_id +
						" AND	BW_ROW_CREATOR_TX.TX_ID = BW_BL_ROW.TX_ID"+
						" AND	BWROW.ID = RQ.ID " +
						" AND   BW_ROW_OWNER.ID = BW_BL_ROW.OWNER_ID "+
						"ORDER BY BWROW.SEQUENCE_NUMBER ";

			}

//			System.out.println(" getFiltredTableRows query = " + query);
			ps = connection.prepareStatement(query);
			//ps.setInt(1, a_table_id);
			rs = ps.executeQuery();
			while (rs.next())
			{
				int a_row_id;
				String a_row_name;
				float a_row_sequence_number;
				int row_owner_id = -1;
				int is_active = 0;
				int row_creation_tid = -1;

				a_row_id = rs.getInt("ROW_ID");
				a_row_name = rs.getString("ROW_NAME");
				a_row_sequence_number = rs.getFloat("ROW_SEQUENCE_NUMBER");
				row_owner_id = rs.getInt("OWNER_ID");
				is_active = rs.getInt("IS_ACTIVE");
				row_creation_tid = rs.getInt("ROW_TID");
				int row_ownerTid = rs.getInt("ROW_OWNER_TID");

				int row_creator_userid = rs.getInt("ROW_CREATOR_ID");
				String row_owner_name = rs.getString("ROW_OWNER_NAME");
				Row r = new Row(a_row_id, a_row_name, a_row_sequence_number, row_creation_tid, row_owner_id, is_active, row_ownerTid, row_creator_userid, row_owner_name);
				rows.add(r);
				rowHash.put(new Integer(a_row_id), r);
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new SystemException(e);
		}
		finally
		{
			try
			{
				rs.close();
				ps.close();
				rs = null;
				ps = null;
				query = null;
			}
			catch (Exception e)
			{
				e.printStackTrace();
				throw new SystemException(e);
			}
		}

		TableRowInfo tbRowInfo = new TableRowInfo(a_table_id, rows, rowHash, true);

		return tbRowInfo;

	}

	public static TableRowInfo getFiltredTableRows_T(Connection connection, int aTableId, int userId, int aNhId, String asViewPref,String asRowQuery, int aBaslineId,  int asOfTid, long asOfDate)
	throws SystemException
	{
		// Previously this method used to work only for normal rows without any consideration
		// for baseline. Now based on the baseline parameter we can flip the query and use the
		// same method. Later on we can modify only the where clause.
		ResultSet rs			= null;
		PreparedStatement ps	= null;
		String query			= null;
		Vector rows				= new Vector();
		Hashtable rowHash		= new Hashtable();
		String lsSqlStr			= "";
		long llTranDate			= 0;
		String lsTranDate		= "";
		java.sql.Timestamp	tsTranDate = null;
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		try
		{
			if(aBaslineId == -1)
			{
				if(asOfTid > 0 )
				{
					lsSqlStr = " SELECT BW_TXS.CREATED_ON FROM BW_TXS WHERE BW_TXS.TX_ID = ? ";
					System.out.println(" Value of lsSqlStr ----- >"+ lsSqlStr + asOfTid);
					ps = connection.prepareStatement(lsSqlStr);
					ps.setInt(1,asOfTid);
					rs = ps.executeQuery();

					while (rs.next())
					{
						tsTranDate = rs.getTimestamp(1,cal);
					}
					try
					{
						if (rs != null)
							rs.close();

						if (ps != null)
							ps.close();
					}
					catch(SQLException sqlexception) {
						System.out.println("sqlexception:::::::;;"+sqlexception.getErrorCode() );
					}
				}
				else
				{
					tsTranDate = new java.sql.Timestamp(asOfDate);
					System.out.println("sqlexception:::::::;;"+tsTranDate);
				}

				lsSqlStr = "  CREATE TABLE  #ACCESSIBLE_ROWS " +
						" ( ROWID  INT  PRIMARY KEY NOT NULL ) " +
						" INSERT INTO #ACCESSIBLE_ROWS " + asRowQuery +

							" SELECT DISTINCT BWROW.ID AS ROW_ID,"+
							" BWROW.NAME AS ROW_NAME,"+
							" BWROW.SEQUENCE_NUMBER AS ROW_SEQUENCE_NUMBER,"+
							" BWROW.OWNER_ID,"+
							" BW_CELL_STATUS.ACTIVE AS IS_ACTIVE,"+
							" BWROW.TX_ID AS ROW_TID,"+
							" BWROW.OWNER_TID AS ROW_OWNER_TID,"+
							" BW_ROW_CREATOR_TX.CREATED_BY AS ROW_CREATOR_ID,"+
							" BW_ROW_OWNER.EMAIL_ADDRESS AS ROW_OWNER_NAME"+
						" FROM #ACCESSIBLE_ROWS AS RQ,"+
//							" BW_getConsolidationRowSet("+aTableId+","+userId+","+aNhId+","+asViewPref+") AS ROWSET,"+
							" BW_ROW AS BWROW,"+
							" BW_TXS AS BW_ROW_CREATOR_TX,"+
							" BW_USER AS BW_ROW_OWNER, "+
							" BW_getStatusTransactions("+ aTableId +",?) AS STX,"+
							" BW_CELL_STATUS,"+
							" BW_CELL "+
						" WHERE "+
//							" BWROW.ID = ROWSET.ROWID "+
							" BWROW.ID = BW_CELL.BW_ROW_ID "+
							" AND BW_CELL.ID = BW_CELL_STATUS.BW_CELL_ID "+
							" AND BW_CELL.ID = STX.CELL_ID "+
							" AND BW_CELL_STATUS.TX_ID = STX.TX_ID "+
							" AND BWROW.BW_TBL_ID = "+ aTableId +
							" AND BW_ROW_CREATOR_TX.TX_ID = BWROW.TX_ID "+
							" AND BW_ROW_OWNER.ID = BWROW.OWNER_ID "+
							" AND BWROW.ID = RQ.ROWID "+
							" ORDER BY BWROW.SEQUENCE_NUMBER "+
							" DROP TABLE #ACCESSIBLE_ROWS";

			}

//			System.out.println(" getFiltredTableRows query = " + lsSqlStr);
			ps = connection.prepareStatement(lsSqlStr);

			ps.setTimestamp(1, tsTranDate, cal);
//			ps.setInt(1, a_table_id);
			rs = ps.executeQuery();
			while (rs.next())
			{
				int a_row_id;
				String a_row_name;
				float a_row_sequence_number;
				int row_owner_id = -1;
				int is_active = 0;
				int row_creation_tid = -1;

				a_row_id				= rs.getInt("ROW_ID");
				a_row_name				= rs.getString("ROW_NAME");
				a_row_sequence_number	= rs.getFloat("ROW_SEQUENCE_NUMBER");
				row_owner_id			= rs.getInt("OWNER_ID");
				is_active				= rs.getInt("IS_ACTIVE");
				row_creation_tid		= rs.getInt("ROW_TID");
				int row_ownerTid		= rs.getInt("ROW_OWNER_TID");

				int row_creator_userid	= rs.getInt("ROW_CREATOR_ID");
				String row_owner_name	= rs.getString("ROW_OWNER_NAME");
				Row r = new Row(a_row_id, a_row_name, a_row_sequence_number, row_creation_tid, row_owner_id, is_active, row_ownerTid, row_creator_userid, row_owner_name);
				rows.add(r);
				rowHash.put(new Integer(a_row_id), r);
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new SystemException(e);
		}
		finally
		{
			try
			{
				rs.close();
				ps.close();
				rs = null;
				ps = null;
				query = null;
			}
			catch (Exception e)
			{
				e.printStackTrace();
				throw new SystemException(e);
			}
		}

		TableRowInfo tbRowInfo = new TableRowInfo(aTableId, rows, rowHash, true);

		return tbRowInfo;

	}

	public static HashMap getCriteriaForAllUsers(Connection connection, int a_cr_table_id)
		throws SystemException
	{
		ResultSet rs = null;
		PreparedStatement ps = null;
		String query = null;
		HashMap criteriaByUser = new HashMap();
//		System.out.println("Getting criteria from table  = " + a_cr_table_id);
		try
		{
			// get the user filter criteria
			query = "SELECT USERCELL.STRING_VALUE, BW_CELL.STRING_VALUE AS VAL, BW_ROW.ID AS ROW, DATACOL.NAME AS COL " +
					"FROM " +
					"	BW_CELL, " +
					"	BW_ROW, " +
					"	BW_COLUMN AS DATACOL, " +
					"	BW_CELL AS USERCELL, " +
					"	BW_COLUMN AS USERCOL " +
					"WHERE " +
					"	BW_ROW.BW_TBL_ID = ? " +
					"AND	BW_CELL.BW_ROW_ID = BW_ROW.ID " +
					"AND	BW_CELL.BW_COLUMN_ID = DATACOL.ID " +
					"AND	DATACOL.NAME <> 'User' " +
				//"AND	BW_CELL.STRING_VALUE <> '' " +
					"AND	USERCELL.BW_ROW_ID = BW_ROW.ID " +
					"AND	USERCELL.BW_COLUMN_ID = USERCOL.ID " +
					"AND	USERCOL.NAME = 'User' ";
//			System.out.println("query to fetch criteria = " + query);
			ps = connection.prepareStatement(query);
			ps.setInt(1, a_cr_table_id);
			rs = ps.executeQuery();

			while (rs.next())
			{
				String userName = rs.getString(1);
				String val = rs.getString(2);
				int qid = rs.getInt(3);
				String colName = rs.getString(4);

				HashMap criteria = (HashMap)criteriaByUser.get(userName);
				if (criteria == null)
				{
					criteria = new HashMap();
					criteriaByUser.put(userName, criteria);
				}
				ArrayList crRow = (ArrayList)criteria.get(new Integer(qid));
				if (crRow == null)
				{
					crRow = new ArrayList();
				}
				FilterCritereon fc = new FilterCritereon(colName, val);
				crRow.add(fc);
				criteria.put(new Integer(qid), crRow);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new SystemException(e);
		}
		finally
		{
			try
			{
				rs.close();
				ps.close();
				rs = null;
				ps = null;
				query = null;
			}
			catch (Exception e)
			{
				e.printStackTrace();
				throw new SystemException(e);
			}
		}

		return criteriaByUser;
	}

// new aggregate method checked by Sanjeev 

	public static void aggregate(Connection connection, String asSourceTbl, String asTargetTbl)
		throws SystemException
	{
		ResultSet rs = null;
		PreparedStatement ps = null;
		String query = null;
		TransactionManager tm	= null;
		String responseBuffer	= "SUCCESS";
		
		try
		{
			getElapsedTime();
			HashMap aggrDefs = new HashMap();
			Vector aggrRows = new Vector();
			// get the aggregation definition
			// Both SourceTable and TargetTable are blank then its the default case
			if (BoardwalkUtility.checkIfNullOrBlank(asSourceTbl) &&  BoardwalkUtility.checkIfNullOrBlank(asTargetTbl) )
			{
			
				query = "SELECT AGGR.BW_ROW_ID, AGGR.STRING_VALUE, AGGRCOL.NAME " +
						"FROM   BW_CELL AS AGGR, BW_COLUMN AGGRCOL, BW_TBL AS AGGRTL, BW_ROW " +
						"WHERE " +
						"	AGGR.BW_COLUMN_ID = AGGRCOL.ID " +
						"AND AGGRCOL.BW_TBL_ID = AGGRTL.ID " +
						"AND AGGRTL.NAME = '__BW_AGGR_TBL' " +
						"AND AGGR.BW_ROW_ID = BW_ROW.ID " +
						"AND AGGR.ACTIVE = 1 " +

						" ORDER BY BW_ROW.SEQUENCE_NUMBER, AGGRCOL.SEQUENCE_NUMBER  ";
			}

		

			// Both SourceTable and TargetTable are not blank then we use following query
			if ( !BoardwalkUtility.checkIfNullOrBlank(asSourceTbl) && !BoardwalkUtility.checkIfNullOrBlank(asTargetTbl) )
			{

				query = "SELECT AGGR.BW_ROW_ID, AGGR.STRING_VALUE, AGGRCOL.NAME " +
						"FROM   BW_CELL AS AGGR, BW_COLUMN AGGRCOL, BW_TBL AS AGGRTL, BW_ROW, " +
						"			BW_CELL AS AGGRScrCell, BW_ROW AS AGGRScrRow, BW_COLUMN AS AGGRScrCOL, " +
						"			BW_CELL AS AGGRTrgCell, BW_ROW AS AGGRTrgRow, BW_COLUMN AS AGGRTrgCOL " +
						"WHERE " +
						"	AGGR.BW_COLUMN_ID = AGGRCOL.ID " +
						"AND AGGRCOL.BW_TBL_ID = AGGRTL.ID " +
						"AND AGGRTL.NAME = '__BW_AGGR_TBL' " +
						"AND AGGR.BW_ROW_ID = BW_ROW.ID " +
						"AND AGGR.ACTIVE = 1 " +


						"AND AGGRScrCell.BW_ROW_ID = AGGRScrRow.ID " +
						"AND AGGRScrCell.BW_ROW_ID = AGGR.BW_ROW_ID " +
						"AND AGGRScrCell.BW_COLUMN_ID = AGGRScrCOL.ID " +
						"AND AGGRScrCOL.NAME = 'SourceTable' " +
						"AND AGGRScrCell.STRING_VALUE = '"+asSourceTbl+"'" +

						"AND AGGRTrgCell.BW_ROW_ID = AGGRTrgRow.ID " +
						"AND AGGRTrgCell.BW_ROW_ID = AGGR.BW_ROW_ID " +
						"AND AGGRTrgCell.BW_COLUMN_ID = AGGRTrgCOL.ID " +
						"AND AGGRTrgCOL.NAME = 'TargetTable' " +
						"AND AGGRTrgCell.STRING_VALUE = '"+asTargetTbl+"'" +

						" ORDER BY BW_ROW.SEQUENCE_NUMBER ";
			}
			// Source table has value and Target is blank
			if ( !BoardwalkUtility.checkIfNullOrBlank(asSourceTbl) && BoardwalkUtility.checkIfNullOrBlank(asTargetTbl) )
			{
				query = "SELECT AGGR.BW_ROW_ID, AGGR.STRING_VALUE, AGGRCOL.NAME " +
						"FROM   BW_CELL AS AGGR, BW_COLUMN AGGRCOL, BW_TBL AS AGGRTL, BW_ROW, " +
						"   BW_CELL AS AGGRScrCell, BW_ROW AS AGGRScrRow, BW_COLUMN AS AGGRScrCOL " +
						"WHERE " +
						"	AGGR.BW_COLUMN_ID = AGGRCOL.ID " +
						"AND AGGRCOL.BW_TBL_ID = AGGRTL.ID " +
						"AND AGGRTL.NAME = '__BW_AGGR_TBL' " +
						"AND AGGR.BW_ROW_ID = BW_ROW.ID " +
						"AND AGGR.ACTIVE = 1 " +

						"AND AGGRScrCell.BW_ROW_ID = AGGRScrRow.ID " +
						"AND AGGRScrCell.BW_ROW_ID = AGGR.BW_ROW_ID " +
						"AND AGGRScrCell.BW_COLUMN_ID = AGGRScrCOL.ID " +
						"AND AGGRScrCOL.NAME = 'SourceTable' " +
						"AND AGGRScrCell.STRING_VALUE = '" +asSourceTbl + "'" +

						" ORDER BY BW_ROW.SEQUENCE_NUMBER ";
			}
			// Source is blank and Target is not blank
			if ( BoardwalkUtility.checkIfNullOrBlank(asSourceTbl) && !BoardwalkUtility.checkIfNullOrBlank(asTargetTbl) )
			{
				query = "SELECT AGGR.BW_ROW_ID, AGGR.STRING_VALUE, AGGRCOL.NAME " +
						"FROM   BW_CELL AS AGGR, BW_COLUMN AGGRCOL, BW_TBL AS AGGRTL, BW_ROW, " +
						"   BW_CELL AS AGGRTrgCell, BW_ROW AS AGGRTrgRow, BW_COLUMN AS AGGRTrgCOL " +
						"WHERE " +
						"	AGGR.BW_COLUMN_ID = AGGRCOL.ID " +
						"AND AGGRCOL.BW_TBL_ID = AGGRTL.ID " +
						"AND AGGRTL.NAME = '__BW_AGGR_TBL' " +
						"AND AGGR.BW_ROW_ID = BW_ROW.ID " +
						"AND AGGR.ACTIVE = 1 " +

						"AND AGGRTrgCell.BW_ROW_ID = AGGRTrgRow.ID " +
						"AND AGGRTrgCell.BW_ROW_ID = AGGR.BW_ROW_ID " +
						"AND AGGRTrgCell.BW_COLUMN_ID = AGGRTrgCOL.ID " +
						"AND AGGRTrgCOL.NAME = 'TargetTable' " +
						"AND AGGRTrgCell.STRING_VALUE = '" +asTargetTbl + "'" +

						" ORDER BY BW_ROW.SEQUENCE_NUMBER ";

			}

			System.out.println(" query -> " + query);

			try
			{
				ps = connection.prepareStatement(query);
				rs = ps.executeQuery();
			}
			catch (Exception eSql)
			{
				eSql.printStackTrace();
			}

			//ps.setInt(1, aggrTableId);

			while (rs.next())
			{
				int rowId = rs.getInt(1);
				String val = rs.getString(2);
				String col = rs.getString(3);
				col = col.trim();
			
				System.out.println(" Testing -> " + rowId + " "+ col +" " + val);

				AggregationDefinition aDef = (AggregationDefinition) aggrDefs.get(new Integer(rowId));
				if (aDef == null)
				{
					aDef = new AggregationDefinition();
					aggrDefs.put(new Integer(rowId), aDef);
				}

				if (col.equalsIgnoreCase("SourceTable"))
				{
					aDef.sourceTable = val;
				}
				else if (col.equalsIgnoreCase("SourceFilter"))
				{
					aDef.sourceFilter = val;
				}
				else if (col.equalsIgnoreCase("SourceColumns"))
				{

					System.out.println(" col name" +  col + " Val --> " +val);

						String[] SrcCols = val.split(",");		

						System.out.println(" SrcCols length " +  SrcCols.length );

					aDef.sourceColumns = SrcCols; 

				}
				else if (col.equalsIgnoreCase("TargetColumns"))
				{
					System.out.println(" col name" +  col + " Val --> " +val);

					aDef.targetColumns = val.split(",");
				}
				else if (col.equalsIgnoreCase("Operator"))
				{
					aDef.operator = val;
				}
				else if (col.equalsIgnoreCase("GroupByColumns"))
				{
					aDef.groupByColumns = val.split (",");
				}
				else if (col.equalsIgnoreCase("Type"))
				{
					aDef.type = val;
				}
				else if (col.equalsIgnoreCase("TargetTable"))
				{
					aDef.targetTable = val;
				}
				else if (col.equalsIgnoreCase("TargetFilter"))
				{
					aDef.targetFilter = val;
				}
				else if (col.equalsIgnoreCase("Transform"))
				{
					aDef.Transform = val;
				}else {
					System.out.println(" not found -> " + rowId + " "+ col +" " + val);
				}

			} // while
			if (ps != null)
			{
				ps.close();
				ps = null;
			}
			if (rs != null)
			{
				rs.close();
				rs = null;
			}
			System.out.println("Time(sec) to get aggregation definitions = " + getElapsedTime());

			// loop over the aggregation list 
			//added by shirish
			List lRowIds = new ArrayList(aggrDefs.keySet());
			Collections.sort(lRowIds);
			Iterator ai = lRowIds.iterator();

			//commented by shirish
			//Vector aDefs = new Vector(aggrDefs.values());
			//Iterator ai = aDefs.iterator();

			while (ai.hasNext())
			{
				int sourceTableId = -1;
				int sourceFilterId = -1;
				int targetTableId = -1;
				int targetFilterId = -1;

				//commented by shirish
				//AggregationDefinition aDef = (AggregationDefinition)ai.next();
				Integer liRowKey = (Integer)ai.next();
				//System.out.println("query ai.next() -----> = " + liRowKey);
				//added by shirish
				AggregationDefinition aDef = (AggregationDefinition)aggrDefs.get(liRowKey);
				
				// get the table ids
				query = "SELECT BW_TBL.ID, BW_TBL.NAME " +
						"FROM BW_TBL " +
						"WHERE " +
						"	BW_TBL.NAME IN ('" + aDef.sourceTable + "', '" + aDef.sourceFilter + "', '" + aDef.targetTable + "', '" + aDef.targetFilter + "') ";
				System.out.println("query to get table ids = " + query);
				ps = connection.prepareStatement(query);
				rs = ps.executeQuery();

				while (rs.next())
				{
					if (rs.getString(2).equalsIgnoreCase(aDef.sourceTable))
					{
						sourceTableId = rs.getInt(1);
						System.out.println("sourceTableId = " + sourceTableId);
					}
					else if (rs.getString(2).equalsIgnoreCase(aDef.sourceFilter))
					{
						sourceFilterId = rs.getInt(1);
						System.out.println("sourceFilterId = " + sourceFilterId);
						if (aDef.targetFilter.equalsIgnoreCase(aDef.sourceFilter))
						{
							targetFilterId = rs.getInt(1);
							System.out.println("targetFilterId = " + targetFilterId);
						}
					}
					else if (rs.getString(2).equalsIgnoreCase(aDef.targetTable))
					{
						targetTableId = rs.getInt(1);
						System.out.println("targetTableId = " + targetTableId);
					}
					else if (rs.getString(2).equalsIgnoreCase(aDef.targetFilter))
					{
						targetFilterId = rs.getInt(1);
						System.out.println("targetFilterId = " + targetFilterId);
					}
				}
				if (ps != null)
				{
					ps.close();
					ps = null;
				}
				if (rs != null)
				{
					rs.close();
					rs = null;
				}
				System.out.println("Time(sec) to get table ids = " + getElapsedTime());
				
				query = "select 1 from bw_txs b, BW_USER_EXPORT_TID a";
				query = query + " where a.TX_ID = b.TX_ID and ";
				query = query + " a.TX_ID in (select max(tx_id) as tx_id from BW_USER_EXPORT_TID where BW_TBL_ID in (?,?))";
				query = query + " and b.CREATED_BY = 1 ";
				query = query + " and b.COMMENT_ = 'Aggregation' ";
				query = query + " and a.bw_tbl_id = ? ";
				
				ps = connection.prepareStatement(query);
				ps.setInt(1, sourceTableId);
				ps.setInt(2, targetTableId);
				ps.setInt(3, targetTableId);
				rs = ps.executeQuery();
				
				if (rs.next())
				{
					System.out.println ("Dont do aggregation, no submit on this table z " + sourceTableId + " after last aggregation");
					System.out.println ("g2 un-comment line below this in code to enable aggr skip logic");
					//continue;
				}
				// get the row query for source table

				String srcRowQuery = getRowQuery(connection, sourceTableId, 1, sourceFilterId, true, "LATEST");
				System.out.println("srcRowQuery = " + srcRowQuery);
				System.out.println("Time(sec) to get row query for source table = " + getElapsedTime());
				// get the row query for target table

				String targetRowQuery = getRowQuery(connection, targetTableId, 1, targetFilterId, true, "LATEST");
				System.out.println("targetRowQuery = " + targetRowQuery);
				System.out.println("Time(sec) to get row query for target table = " + getElapsedTime());

				// create the aggregation query
				StringBuffer aggrQueryBuffer = new StringBuffer();
				// filtered source rows
				aggrQueryBuffer.append("\n CREATE TABLE  #SOURCE_ROWS ");
				aggrQueryBuffer.append("\n ( BW_ROW_ID  INT  PRIMARY KEY NOT NULL  ) ");
				aggrQueryBuffer.append("\n INSERT INTO #SOURCE_ROWS " + srcRowQuery);

				aggrQueryBuffer.append("\n SELECT BC.BW_ROW_ID, BC.BW_COLUMN_ID, BC.STRING_VALUE, BC.FORMULA, BC.ACTIVE INTO #DATATBL FROM BW_CELL BC, BW_COLUMN DATACOL ");
				aggrQueryBuffer.append("\n WHERE EXISTS (SELECT 1 FROM #SOURCE_ROWS R WHERE R.BW_ROW_ID = BC.BW_ROW_ID) ");
				aggrQueryBuffer.append("\n AND DATACOL.ID = BC.BW_COLUMN_ID ");
				aggrQueryBuffer.append("\n AND DATACOL.NAME IN (");
				for (int i = 0; i < aDef.sourceColumns.length; i++)
				{
					aggrQueryBuffer.append("'" + aDef.sourceColumns[i] + "'");
					if (i < aDef.sourceColumns.length - 1)
						aggrQueryBuffer.append(",");
				}
				aggrQueryBuffer.append(") ");
				aggrQueryBuffer.append("\n SELECT BC.BW_ROW_ID, BC.BW_COLUMN_ID, BC.STRING_VALUE, BC.FORMULA, BC.ACTIVE INTO #SRCTBL FROM BW_CELL BC, BW_COLUMN DATACOL ");
				aggrQueryBuffer.append("\n WHERE EXISTS (SELECT 1 FROM #SOURCE_ROWS R WHERE R.BW_ROW_ID = BC.BW_ROW_ID) ");
				aggrQueryBuffer.append("\n AND DATACOL.ID = BC.BW_COLUMN_ID ");
				aggrQueryBuffer.append("\n AND DATACOL.NAME IN (");
				for (int i = 0; i < aDef.groupByColumns.length; i++)
				{
					aggrQueryBuffer.append("'" + aDef.groupByColumns[i] + "'");
					if (i < aDef.groupByColumns.length - 1)
						aggrQueryBuffer.append(",");
				}
				aggrQueryBuffer.append(") ");

				aggrQueryBuffer.append("\n DROP TABLE #SOURCE_ROWS ");
				aggrQueryBuffer.append("\n UPDATE DATA ");
				aggrQueryBuffer.append("\n SET DATA.STRING_VALUE = '0'  ");
				aggrQueryBuffer.append("\n FROM #DATATBL DATA ");
				aggrQueryBuffer.append("\n WHERE DATA.STRING_VALUE = '-' OR DATA.STRING_VALUE = '$'   ");
				//aggrQueryBuffer.append("\n AND DATA.BW_COLUMN_ID = DATACOL.ID   ");
				aggrQueryBuffer.append("\n OR ISNUMERIC(DATA.STRING_VALUE) <> 1   ");
				/*aggrQueryBuffer.append("\n AND DATACOL.NAME IN (");
				for (int i = 0; i < aDef.targetColumns.length; i++)
				{
					aggrQueryBuffer.append("'" + aDef.targetColumns[i] + "'");
					if (i < aDef.targetColumns.length - 1)
						aggrQueryBuffer.append(",");
				}
				aggrQueryBuffer.append(") ");*/
				//aggrQueryBuffer.append("");
				
				//g2 dec9_11 To handle exponential numbers
				aggrQueryBuffer.append("\n UPDATE DATA ");
				aggrQueryBuffer.append("\n SET DATA.STRING_VALUE = CAST(CAST(STRING_VALUE AS FLOAT) AS NUMERIC)  ");
				aggrQueryBuffer.append("\n FROM #DATATBL DATA ");
				aggrQueryBuffer.append("\n WHERE  DBO.isReallyNumeric( STRING_VALUE )= 0 ");
				
				// aggregate the source data
				aggrQueryBuffer.append("\n SELECT ");
				for (int i = 0; i < aDef.groupByColumns.length; i++)
				{
					aggrQueryBuffer.append("\n GB" + i + ".STRING_VALUE AS '" + aDef.groupByColumns[i] + "',");
				}
				aggrQueryBuffer.append("\n DATACOL.NAME AS TARGET_COLUMN, ");
				aggrQueryBuffer.append("\n " + aDef.operator + "(CAST (DATA.STRING_VALUE AS " + aDef.type + ")) AS AGGR INTO #SOURCE_AGGR ");
				aggrQueryBuffer.append("\n FROM ");
				for (int i = 0; i < aDef.groupByColumns.length; i++)
				{
					aggrQueryBuffer.append("\n BW_COLUMN AS GB" + i + "COL,");
					aggrQueryBuffer.append("\n #SRCTBL AS GB" + i + ",");
				}
				aggrQueryBuffer.append("\n #DATATBL AS DATA ");
				aggrQueryBuffer.append("\n ,BW_COLUMN AS DATACOL ");
				//aggrQueryBuffer.append("\n#SOURCE_ROWS AS BWROW ");
				aggrQueryBuffer.append("\n WHERE ");
				//aggrQueryBuffer.append("\n   DATA.BW_ROW_ID = BWROW.BW_ROW_ID ");
				aggrQueryBuffer.append("\n DATA.BW_COLUMN_ID = DATACOL.ID ");
				aggrQueryBuffer.append("\n AND DATA.ACTIVE = 1 ");
				//aggrQueryBuffer.append("\nAND dbo.isreallynumeric(DATA.STRING_VALUE) = 1 ");
				//aggrQueryBuffer.append("\nAND DATA.STRING_VALUE IS NOT NULL ");
				//aggrQueryBuffer.append("\nAND DATA.STRING_VALUE <> '' ");
				aggrQueryBuffer.append("\n AND DATACOL.NAME IN (");
				for (int i = 0; i < aDef.sourceColumns.length; i++)
				{
					aggrQueryBuffer.append("'" + aDef.sourceColumns[i] + "'");
					if (i < aDef.sourceColumns.length - 1)
						aggrQueryBuffer.append(",");

				}
				aggrQueryBuffer.append(") ");
				for (int i = 0; i < aDef.groupByColumns.length; i++)
				{
					aggrQueryBuffer.append("\n AND GB" + i + ".BW_ROW_ID = DATA.BW_ROW_ID ");
					aggrQueryBuffer.append("\n AND GB" + i + ".BW_COLUMN_ID = GB" + i + "COL.ID");
					aggrQueryBuffer.append("\n AND GB" + i + "COL.NAME = '" + aDef.groupByColumns[i] + "'");
				}
				aggrQueryBuffer.append("\n GROUP BY ");
				for (int i = 0; i < aDef.groupByColumns.length; i++)
				{
					aggrQueryBuffer.append("GB" + i + ".STRING_VALUE, ");
				}
				aggrQueryBuffer.append("DATACOL.NAME ");	


				// aggregate the source data for inactive cells added by shirish on 11/21/08
				aggrQueryBuffer.append("\n INSERT INTO #SOURCE_AGGR \n SELECT ");
				for (int i = 0; i < aDef.groupByColumns.length; i++)
				{
					aggrQueryBuffer.append("\n GB" + i + ".STRING_VALUE AS '" + aDef.groupByColumns[i] + "',");
				}
				aggrQueryBuffer.append("\n DATACOL.NAME AS TARGET_COLUMN, ");
				aggrQueryBuffer.append("\n 0 AS AGGR  ");
				aggrQueryBuffer.append("\n FROM ");
				for (int i = 0; i < aDef.groupByColumns.length; i++)
				{
					aggrQueryBuffer.append("\n BW_COLUMN AS GB" + i + "COL,");
					aggrQueryBuffer.append("\n #SRCTBL AS GB" + i + ",");
				}
				aggrQueryBuffer.append("\n #DATATBL AS DATA, ");
				aggrQueryBuffer.append("\n BW_COLUMN AS DATACOL ");
				//aggrQueryBuffer.append("\n #SOURCE_ROWS AS BWROW ");
				aggrQueryBuffer.append("\n WHERE ");
				//aggrQueryBuffer.append("\n   DATA.BW_ROW_ID = BWROW.BW_ROW_ID ");
				aggrQueryBuffer.append("\n DATA.BW_COLUMN_ID = DATACOL.ID ");
				aggrQueryBuffer.append("\n AND DATA.ACTIVE = 0 ");
				//aggrQueryBuffer.append("\n AND ISNUMERIC(DATA.STRING_VALUE) = 1 ");
				//aggrQueryBuffer.append("\n AND DATA.STRING_VALUE IS NOT NULL ");
				//aggrQueryBuffer.append("\n AND DATA.STRING_VALUE <> '' ");
				aggrQueryBuffer.append("\n AND DATACOL.NAME IN (");
				for (int i = 0; i < aDef.sourceColumns.length; i++)
				{
					aggrQueryBuffer.append("'" + aDef.sourceColumns[i] + "'");
					if (i < aDef.sourceColumns.length - 1)
						aggrQueryBuffer.append(",");

				}
				aggrQueryBuffer.append(") ");
				for (int i = 0; i < aDef.groupByColumns.length; i++)
				{
					aggrQueryBuffer.append("\n AND GB" + i + ".BW_ROW_ID = DATA.BW_ROW_ID ");
					aggrQueryBuffer.append("\n AND GB" + i + ".BW_COLUMN_ID = GB" + i + "COL.ID");
					aggrQueryBuffer.append("\n AND GB" + i + "COL.NAME = '" + aDef.groupByColumns[i] + "'");
				}
				aggrQueryBuffer.append("\n GROUP BY ");
				for (int i = 0; i < aDef.groupByColumns.length; i++)
				{
					aggrQueryBuffer.append("GB" + i + ".STRING_VALUE, ");
				}
				aggrQueryBuffer.append("DATACOL.NAME ");

				aggrQueryBuffer.append("\n DROP TABLE #DATATBL ");				
				aggrQueryBuffer.append("\n DROP TABLE #SRCTBL ");				
				//aggregate the active and inactive source data added by shirish on 11/21/08
				aggrQueryBuffer.append("\n");
				aggrQueryBuffer.append("\n SELECT ");
				for (int i = 0; i < aDef.groupByColumns.length; i++)
				{
					aggrQueryBuffer.append("["+aDef.groupByColumns[i] + "],");
				}
				aggrQueryBuffer.append("\n [TARGET_COLUMN], ");
				aggrQueryBuffer.append("\n " + aDef.operator + "(CAST (AGGR AS " + aDef.type + ")) AS AGGR INTO #TOTALSOURCE_AGGR ");
				aggrQueryBuffer.append("\n FROM	#SOURCE_AGGR ");
				aggrQueryBuffer.append("\n GROUP BY ");
				for (int i = 0; i < aDef.groupByColumns.length; i++)
				{
					aggrQueryBuffer.append("["+aDef.groupByColumns[i] + "],");
				}
				aggrQueryBuffer.append("[TARGET_COLUMN] ");
	
				aggrQueryBuffer.append("\n DROP TABLE #SOURCE_AGGR ");
				
				if (!aDef.Transform.trim().equals(""))
				{
					String[] tsArray = aDef.Transform.split(",,");
								
					for (int i = 0; i < tsArray.length; i++)
					{
						String[] tsArrayNames = tsArray[i].split(",");
						System.out.println("srcMeasure g2 " + tsArrayNames[0]);
						System.out.println("trgMeasure g2 " + tsArrayNames[1]);
						
						aggrQueryBuffer.append("\n UPDATE #TOTALSOURCE_AGGR ");
						aggrQueryBuffer.append("\n SET " + aDef.groupByColumns[aDef.groupByColumns.length - 1] + " = '" + tsArrayNames[1] + "'");
						aggrQueryBuffer.append("\n WHERE " + aDef.groupByColumns[aDef.groupByColumns.length - 1] + " = '" +  tsArrayNames[0] + "'");
					}
				}
				
				for (int i = 0; i < aDef.targetColumns.length; i++)
				{
					aggrQueryBuffer.append("\n UPDATE #TOTALSOURCE_AGGR ");
					aggrQueryBuffer.append("\n SET TARGET_COLUMN = '" + aDef.targetColumns[i]  + "'");
					aggrQueryBuffer.append("\n WHERE TARGET_COLUMN = '" + aDef.sourceColumns[i]  + "'");
				}

				// get candidate rows for the target table
				aggrQueryBuffer.append("\n CREATE TABLE  #TARGET_ROWS ");
				aggrQueryBuffer.append("\n ( BW_ROW_ID  INT  PRIMARY KEY NOT NULL  ) ");
				aggrQueryBuffer.append("\n INSERT INTO #TARGET_ROWS " + targetRowQuery);

				aggrQueryBuffer.append("\n SELECT BC.BW_ROW_ID, BC.BW_COLUMN_ID, BC.STRING_VALUE, BC.FORMULA, BC.ACTIVE INTO #TRGTBL FROM BW_CELL BC, BW_COLUMN DATACOL ");
				aggrQueryBuffer.append("\n WHERE EXISTS (SELECT 1 FROM #TARGET_ROWS R WHERE R.BW_ROW_ID = BC.BW_ROW_ID) ");
				aggrQueryBuffer.append("\n AND DATACOL.ID = BC.BW_COLUMN_ID ");
				aggrQueryBuffer.append("\n AND DATACOL.NAME IN (");
				for (int i = 0; i < aDef.targetColumns.length; i++)
				{
					aggrQueryBuffer.append("'" + aDef.targetColumns[i] + "'");
					aggrQueryBuffer.append(",");
				}
				for (int i = 0; i < aDef.groupByColumns.length; i++)
				{
					aggrQueryBuffer.append("'" + aDef.groupByColumns[i] + "'");
					if (i < aDef.groupByColumns.length - 1)
						aggrQueryBuffer.append(",");
				}
				aggrQueryBuffer.append(") ");////////
				aggrQueryBuffer.append("\n DROP TABLE #TARGET_ROWS ");
				
				aggrQueryBuffer.append("\n INSERT INTO BW_RC_STRING_VALUE ");
//				aggrQueryBuffer.append("\n SELECT TARGET_CELL.BW_ROW_ID, TARGET_CELL.BW_COLUMN_ID,  CONVERT(varchar(256), CAST(AGGRDATA.AGGR AS decimal(38,2))), TARGET_CELL.FORMULA AS FORMULA, ? , 3 ");
//	Date 4/10/2012 -> Using Replace function to remove the Trailing zeros from numbers like 420.00 to look like 420  			
				aggrQueryBuffer.append("\n SELECT TARGET_CELL.BW_ROW_ID, TARGET_CELL.BW_COLUMN_ID, REPLACE( CONVERT(varchar(256), CAST(AGGRDATA.AGGR AS decimal(38,2))),  '.00',''), TARGET_CELL.FORMULA AS FORMULA, ? , 3 ");

				aggrQueryBuffer.append("\n  FROM ");
				aggrQueryBuffer.append("\n #TRGTBL AS TARGET_CELL, ");
				aggrQueryBuffer.append("\n BW_COLUMN AS TARGET_COLUMN, ");
				for (int i = 0; i < aDef.groupByColumns.length; i++)
				{
					aggrQueryBuffer.append("\n BW_COLUMN AS GB" + i + "COL,");
					aggrQueryBuffer.append("\n #TRGTBL AS GB" + i + ",");
				}
				aggrQueryBuffer.append("\n #TOTALSOURCE_AGGR AS AGGRDATA ");
				//aggrQueryBuffer.append("\n #TARGET_ROWS AS TARGET_ROW ");
				aggrQueryBuffer.append("\n WHERE ");
				aggrQueryBuffer.append(" \n TARGET_CELL.BW_COLUMN_ID = TARGET_COLUMN.ID ");
				aggrQueryBuffer.append("\n AND TARGET_COLUMN.BW_TBL_ID = ? ");
				aggrQueryBuffer.append("\n AND TARGET_COLUMN.NAME = AGGRDATA.TARGET_COLUMN ");
				//aggrQueryBuffer.append("\n AND TARGET_CELL.BW_ROW_ID = TARGET_ROW.BW_ROW_ID ");
				for (int i = 0; i < aDef.groupByColumns.length; i++)
				{
					aggrQueryBuffer.append("\n AND GB" + i + ".BW_ROW_ID = TARGET_CELL.BW_ROW_ID ");
					aggrQueryBuffer.append("\n AND GB" + i + ".BW_COLUMN_ID = GB" + i + "COL.ID ");
					aggrQueryBuffer.append("\n AND GB" + i + "COL.NAME = '" + aDef.groupByColumns[i] + "' ");
					aggrQueryBuffer.append("\n AND GB" + i + ".STRING_VALUE = AGGRDATA.[" + aDef.groupByColumns[i] + "] ");
				}
//				aggrQueryBuffer.append("\n AND  CONVERT(varchar(256), CAST(AGGRDATA.AGGR AS decimal(38,2)))  <> TARGET_CELL.STRING_VALUE ");
//	Date 4/10/2012 -> Using Replace function to remove the Trailing zeros from numbers like 420.00 to look like 420  
				aggrQueryBuffer.append("\n AND  REPLACE( CONVERT(varchar(256), CAST(AGGRDATA.AGGR AS decimal(38,2))), '.00','') <> TARGET_CELL.STRING_VALUE ");

				aggrQueryBuffer.append("\n DROP TABLE #TRGTBL ");
				aggrQueryBuffer.append("\n DROP TABLE #TOTALSOURCE_AGGR ");
				
				/*aggrQueryBuffer.append("\n INSERT INTO BW_RC_STRING_VALUE ");
				aggrQueryBuffer.append("\n SELECT R.ID, C.ID,  '0', NULL, ? , 3 ");
				aggrQueryBuffer.append("\n FROM  BW_ROW R, BW_COLUMN C ");
				aggrQueryBuffer.append("\n WHERE NOT EXISTS (SELECT 1 FROM BW_RC_STRING_VALUE RC WHERE RC.TX_ID = ? AND RC.BW_ROW_ID = R.ID AND RC.BW_COLUMN_ID = C.ID) ");
				aggrQueryBuffer.append("\n AND R.BW_TBL_ID = ? AND R.IS_ACTIVE  = 1 ");
				aggrQueryBuffer.append("\n AND C.BW_TBL_ID = ? AND C.IS_ACTIVE  = 1");
				aggrQueryBuffer.append("\n AND C.NAME IN (");
				for (int i = 0; i < aDef.targetColumns.length; i++)
				{
					aggrQueryBuffer.append("'" + aDef.targetColumns[i] + "'");
					if (i < aDef.targetColumns.length - 1)
						aggrQueryBuffer.append(",");
				}
				aggrQueryBuffer.append(") ");*/
				
				aggrQueryBuffer.append("\n\n EXEC BW_UPD_CELL_FROM_RCSV ? , ?, ?, ? ");
				
				// run the aggregation query
				query = aggrQueryBuffer.toString();
				System.out.println("Time(sec) to create aggregation query = " + getElapsedTime());

				System.out.println("aggr query = " + query);

				tm = new TransactionManager(connection, 1);
				int txid = tm.startTransaction("Aggregation", "Aggregation");

				System.out.println("txid " + txid);
				System.out.println("targetTableId " + targetTableId);
				System.out.println("1 " + 1);

				//ps = connection.prepareStatement(query);

				ps = connection.prepareStatement(query);
				ps.setInt(1, txid);
				ps.setInt(2, targetTableId);
				
				/*
				ps.setInt(3, txid);
				ps.setInt(4, txid);
				ps.setInt(5, targetTableId);
				ps.setInt(6, targetTableId);*/
			
				ps.setInt(3, txid);
				ps.setInt(4, txid);
				ps.setInt(5, targetTableId);
				ps.setInt(6, 1);
				ps.execute();
				if (ps != null)
				{
					ps.close();
					ps = null;
				}
				if (rs != null)
				{
					rs.close();
					rs = null;
				}
				tm.commitTransaction();
				
				/*try
				{
					if (tm != null)
						tm.rollbackTransaction();
				}
				catch (Exception e1)
				{
					e1.printStackTrace();
				}*/
				System.out.println("Time(sec) to run aggregation query Z = " + getElapsedTime());
			}
		}
		catch (Exception e)
		{
			try
			{
				if (tm != null)
					tm.rollbackTransaction();
			}
			catch (Exception e1)
			{
				e1.printStackTrace();
			}
			e.printStackTrace();
			throw new SystemException(e);
		}
		finally
		{
			try
			{
				if (ps != null)
				{
					ps.close();
					ps = null;
				}
				if (rs != null)
				{
					rs.close();
					rs = null;
				}
				query = null;
			}
			catch (Exception e)
			{
				responseBuffer = e.toString();
				e.printStackTrace();
				throw new SystemException(e);
			}
		}

		System.out.println("End : aggregate()");
	}



	public static HashMap getCriteriaForUser(Connection connection, int userId, int a_cr_table_id, int target_tbl_id, String view)
		throws SystemException
	{
		ResultSet rs = null;
		PreparedStatement ps = null;
		String query = null;
		HashMap criteria = new HashMap();
		try
		{
			if (view.equals("LATEST")) view = "";
			// if using a derived criteria table
			int base_criteria_table = getCriteriaTable(connection, a_cr_table_id, userId);
			if (base_criteria_table > 0)
			{
				System.out.println("Derived Criteria Table with base criteria table id = " + base_criteria_table);
				String rq = getRowQuery(connection, a_cr_table_id, userId, base_criteria_table, true, view);
				query =
						"SELECT  VALCELL.STRING_VALUE AS VAL, ROW.ID AS ROW, VALCOL.NAME AS COL, TARGET_COL.ID TRGCOL " +
						"FROM " +
						"( " +
						rq +
						") AS ROW, " +
						"    BW_CELL AS VALCELL, " +
						"    BW_COLUMN AS VALCOL, " +
						"    BW_TBL AS CRIT_TBL, " +
						"    BW_COLUMN AS CRIT_COL, " +
						"    BW_TBL AS TARGET_TBL, " +
						"    BW_COLUMN AS TARGET_COL " +
						"WHERE " +
						"     VALCELL.BW_ROW_ID = ROW.ID " +
						"AND  VALCOL.ID = VALCELL.BW_COLUMN_ID " +
						"AND  VALCELL.ACTIVE = 1 " +
						"AND  CRIT_TBL.ID = ? " +
						"AND  CRIT_COL.BW_TBL_ID = CRIT_TBL.ID " +
						"AND  CRIT_COL.NAME = VALCOL.NAME " +
						"AND  CRIT_COL.IS_ACTIVE = 1 " +
						"AND  TARGET_TBL.ID = ? " +
						"AND  TARGET_COL.BW_TBL_ID = TARGET_TBL.ID " +
						"AND  TARGET_COL.IS_ACTIVE = 1 " +
						"AND  TARGET_COL.NAME = CRIT_COL.NAME ";
					ps = connection.prepareStatement(query);
					ps.setInt(1, base_criteria_table);
					ps.setInt(2, target_tbl_id);
					rs = ps.executeQuery();
			}
			else
			{
					System.out.println("Getting row query for " + userId + " using criteria table = " + a_cr_table_id + " target_tbl_id = " + target_tbl_id);
				// get the user filter criteria
				query = "SELECT BW_CELL.STRING_VALUE AS VAL, BW_ROW.ID AS ROW, DATACOL.NAME AS COL, TARGET_COL.ID TRGCOL " +
						"FROM " +
						"	BW_CELL, " +
						"	BW_ROW, " +
						"	BW_COLUMN AS DATACOL, " +
						"	BW_CELL AS USERCELL, " +
						"	BW_COLUMN AS USERCOL, " +
						"	BW_CELL AS VIEWCELL, " +
						"	BW_COLUMN AS VIEWCOL, " +
						"	BW_USER, " +
						"   BW_TBL AS TARGET_TBL, " +
						"   BW_COLUMN AS TARGET_COL " +
						"WHERE " +
						"	BW_ROW.BW_TBL_ID = ? " +
						"AND	BW_CELL.ACTIVE = 1 " +
						"AND	BW_CELL.BW_ROW_ID = BW_ROW.ID " +
						"AND	BW_CELL.BW_COLUMN_ID = DATACOL.ID " +
						"AND	DATACOL.NAME <> 'User' " +
					//"AND	BW_CELL.STRING_VALUE <> '' " +
						"AND	USERCELL.BW_ROW_ID = BW_ROW.ID " +
						"AND	USERCELL.BW_COLUMN_ID = USERCOL.ID " +
						"AND	USERCOL.NAME = 'User' " +
						"AND 	USERCELL.STRING_VALUE = BW_USER.EMAIL_ADDRESS " +
						"AND 	BW_USER.ID = ? " +
						"AND	VIEWCELL.BW_ROW_ID = BW_ROW.ID " +
						"AND	VIEWCELL.BW_COLUMN_ID = VIEWCOL.ID " +
						"AND	VIEWCOL.NAME = 'View' " +
						"AND 	VIEWCELL.STRING_VALUE = '" + view + "'" +
						"AND  TARGET_TBL.ID = ? " +
						"AND  TARGET_COL.BW_TBL_ID = TARGET_TBL.ID " +
						"AND  TARGET_COL.IS_ACTIVE = 1 " +
						"AND  TARGET_COL.NAME = DATACOL.NAME " +
						"AND  BW_ROW.IS_ACTIVE = 1 ";
				System.out.println("query to fetch criteria = " + query);
				ps = connection.prepareStatement(query);
				ps.setInt(1, a_cr_table_id);
				ps.setInt(2, userId);
				ps.setInt(3, target_tbl_id);
				rs = ps.executeQuery();
			}

			while (rs.next())
			{
				String val = rs.getString(1);
				int qid = rs.getInt(2);
				String colName = rs.getString(3);
				int trgColid = rs.getInt(4);

				ArrayList crRow = (ArrayList)criteria.get(new Integer(qid));
				if (crRow == null)
				{
					crRow = new ArrayList();
				}
				FilterCritereon fc = new FilterCritereon(colName, val, trgColid);
				crRow.add(fc);
				criteria.put(new Integer(qid), crRow);
				//System.out.println("val "+ val);
				//System.out.println("qid "+ qid);
				//System.out.println("colName "+ colName);
			}
		}
		catch (Exception e)
		{
			System.out.println("Error in getCriteriaForUser");
			e.printStackTrace();
			throw new SystemException(e);
		}
		finally
		{
			try
			{
				rs.close();
				ps.close();
				rs = null;
				ps = null;
				query = null;
			}
			catch (Exception e)
			{
				e.printStackTrace();
				throw new SystemException(e);
			}
		}

		System.out.println("End : getCriteriaForUser()");
		return criteria;
	}

	// critera is a map of rowid : ArrayList<FilterCriteron> (AND conditions)
	public static String getRowQuery(Connection connection, HashMap criteria, int a_table_id, boolean active)
		throws SystemException
	{
		String query = null;
		Vector rows = new Vector(criteria.keySet());
		Iterator ri = rows.iterator();
		HashMap processedCriteria = new HashMap();
		while (ri.hasNext())
		{

			String q_select = " SELECT BW_ROW.ID ";
			String q_from = " FROM BW_ROW ";
			String q_where;
			if (active == true) 
				q_where = " WHERE BW_ROW.BW_TBL_ID = " + a_table_id + " AND BW_ROW.IS_ACTIVE = 1 ";
			else
				q_where = " WHERE BW_ROW.BW_TBL_ID = " + a_table_id + " ";

			String currCriterion = "";
			ArrayList crRow = (ArrayList)criteria.get(ri.next());
			Iterator cri = crRow.iterator();
			int fcColNum = 0;
			String lsFirstChar = "";
			String lsValuePortion = "";

			while (cri.hasNext())
			{
				FilterCritereon fc = (FilterCritereon)cri.next();
				if (fc.getValue() == null || fc.getValue().trim().equals(""))
					continue;

				lsFirstChar = fc.getValue().trim().substring(0,1);
				lsValuePortion = fc.getValue().trim().substring(1);
				

				
				boolean lbEquals = false;				

				if (lsFirstChar.equals("<") || lsFirstChar.equals(">") )
				{
					lbEquals = true;
				}

				q_from = q_from + " , " +
								  //"BW_COLUMN AS Col" + fcColNum + " , " +
								  "BW_CELL AS CellCol" + fcColNum + " ";

				q_where = q_where + " AND	CellCol" + fcColNum + ".BW_ROW_ID = BW_ROW.ID " +
									//" AND	Col" + fcColNum + ".NAME = '" + BoardwalkUtility.replaceSQLString(fc.getColumnName()) + "'  " +
									//" AND	Col" + fcColNum + ".BW_TBL_ID = " + a_table_id + "  " +
									" AND	CellCol" + fcColNum + ".BW_COLUMN_ID = " + fc.gettrgtColumnId() + " ";
				if (lbEquals)
				{
//					q_where = q_where + " AND CAST(CellCol" + fcColNum + ".STRING_VALUE AS decimal(38,2)) " + lsFirstChar + " " + BoardwalkUtility.replaceSQLString(lsValuePortion) + " " ;
					q_where = q_where + " AND   ISNUMERIC(CellCol" + fcColNum + ".STRING_VALUE) > 0  ";
					q_where = q_where + " AND	CAST(CellCol" + fcColNum + ".STRING_VALUE  AS NUMERIC) " + lsFirstChar + BoardwalkUtility.replaceSQLString(lsValuePortion) ;
				}
				else 
					q_where = q_where + " AND	CellCol" + fcColNum + ".STRING_VALUE = '" + BoardwalkUtility.replaceSQLString(fc.getValue()) + "' " ;

				currCriterion = currCriterion + BoardwalkUtility.replaceSQLString(fc.getColumnName()) + ":" + BoardwalkUtility.replaceSQLString(fc.getValue());
				fcColNum++;
			}
			System.out.println("Processing criterion = " + currCriterion);
			if (processedCriteria.get(currCriterion) != null)
			{
				System.out.println("Criteria already processed. Skipping...");
			}
			else
			{

				if (query == null)
				{
					query = q_select + q_from + q_where;
				}
				else
				{
					query = query + " UNION " + q_select + q_from + q_where;
				}
				processedCriteria.put(currCriterion, currCriterion);
			}

		}

		//System.out.println("rowQuery =>> " + query);
		return query;

	}


	public static String getRowQuery(Connection connection, int a_table_id, int userId, int a_cr_table_id, boolean active, String view)
	throws SystemException
	{
		String query = null;
		HashMap criteria = null;
		System.out.println("view.indexOf = " + view.indexOf("?"));
		if (view.indexOf("?") == 0)
		{
			criteria = getCriteriaForDynamicView(view);
			System.out.println("criteria =>> " + criteria.toString());
		}
		else
		{
			criteria = getCriteriaForUser(connection, userId, a_cr_table_id, a_table_id, view);
		}
		// Now create the query to fetch the data
		query = getRowQuery(connection, criteria, a_table_id, active);
		System.out.println("rowQuery =>> " + query);
		return query;
	}


	public static HashMap getCriteriaForDynamicView(String view)
	{
		HashMap criteria = new HashMap();
		String[] rows = view.substring(1).trim().split("\\|");
		for (int i = 0; i < rows.length; i++)
		{
			String[] cols = rows[i].split("&");

			ArrayList crRow = new ArrayList();
			for (int j = 0; j < cols.length; j++)
			{
				String[] cr = cols[j].split("=");
				FilterCritereon fc = new FilterCritereon(cr[0], cr[1]);
				crRow.add(fc);
			}
			criteria.put(new Integer(i), crRow);
		}

		return criteria;
	}

	public static TableAccessList getSuggestedAccess(Connection connection, TableInfo tinfo, int userId, int memberId, int nhId)
	{
		TableAccessList ftal = null;
		try
		{
			// get the access and view preference
			int tableId = tinfo.getTableId();
			Hashtable taht = TableManager.getTableAccess(connection, tableId);
			Vector relationList = new Vector(taht.keySet());
			Hashtable neighborhoodsByRelation = (Hashtable)TableManager.getNeighborhoodRelationshipsForTable(connection, tableId);

			int facl = 0;
			// get the combined access control
			Iterator ri = relationList.iterator();
			while (ri.hasNext())
			{
				String rel = (String)ri.next();
//				System.out.println("rel = " + rel);
				boolean relok = false;
				//System.out.println("Table creator id = " + tinfo.getCreateUserId());
				// is this relation applicable to the user
				if (rel.equals("CREATOR"))
				{
					if (tinfo.getCreateUserId() == userId)
					{
						relok = true;
					}
				}
				else if (rel.equals("PUBLIC"))
				{
					relok = true;
				}
				else
				{

					Vector nhList = (Vector)neighborhoodsByRelation.get(rel);
					if (nhList != null)
					{
						//System.out.println("Num Nh in Rel = " + nhList.size());
						Iterator nhi = nhList.iterator();
						while (nhi.hasNext())
						{
							com.boardwalk.neighborhood.NeighborhoodId ni = (com.boardwalk.neighborhood.NeighborhoodId)nhi.next();
							if (ni.getId() == nhId)
							{
								relok = true;
								break;
							}
						}
					}
				}

				if (relok == false)
				{
					continue;
				}

//				System.out.println(" rel is ok to use");

				if (taht.get(rel) != null)
				{
					TableAccessList tal = (TableAccessList)taht.get(rel);
//					System.out.println("rel access  = " + tal.getACL());
					facl = facl | tal.getACL();
//					System.out.println("facl = " + facl);
				}
				else
				{
					System.out.println("Could not get access for this relationship!!!!!!!!!!");
					continue;
				}
			}

			ftal = new TableAccessList(-1, tableId, null, facl);

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return ftal;
	}
	public static void createActiveRowSet(Connection connection,
											int tableId,
											int userId,
											int memberId,
											int nhId,
											int baselineId,
											String view)
	{
		CallableStatement stmt = null;

	}

	public static String getTableBuffer(Connection connection,
												int tableId,
												int userId,
												int memberId,
												int nhId,
												int baselineId,
												String view,
												int mode)
		throws SQLException
	{
		try
		{
			PreparedStatement stmt = null;
			int maxTransactionId = -1;
			int exportTid = -1;
			getElapsedTime();
			TableInfo tinfo = TableManager.getTableInfo(connection, userId, tableId);
			TableAccessList ftal = getSuggestedAccess(connection, tinfo, userId, memberId, nhId);
			if (view == null || view.trim().equals(""))
			{
				view = ftal.getSuggestedViewPreferenceBasedOnAccess();
				System.out.println("Suggested view pref = " + view);
			}
			System.out.println("view = " + view);
			System.out.println("baselineId = " + baselineId);
			System.out.println("mode = " + mode);
			// Check access control :: TBD
			int raccess = 1;
			int ACLFromDB = ftal.getACL();
			TableAccessRequest wAccess = new TableAccessRequest(tableId, view, true);
			int wACL = wAccess.getACL();
			int awACL = wACL & ACLFromDB;
			if (awACL == wACL)
			{
				raccess = 2;
				System.out.println("Rows have write access");
			}
			else
			{
				System.out.println("Rows are readonly");
			}
			// see if there is a criterea table associated with this table
			int criteriaTableId = TableViewManager.getCriteriaTable(connection, tableId, userId);
			System.out.println("Using criterea table id = " + criteriaTableId);

			StringBuffer resData = new StringBuffer();

			// Get the columns
			Vector colv = ColumnManager.getXlColumnsForImport(connection, tableId, userId, memberId);
			//ColObjsByColId = new HashMap();
			Iterator ci = colv.iterator();
			//while (ci.hasNext())
			//{
			//    xlColumn_import coli = (xlColumn_import)ci.next();
			//    ColObjsByColId.put(new Integer(coli.getId()), coli);
			//}
			// columns
			for (int c = 0; c < colv.size(); c++)
			{
				xlColumn_import col = (xlColumn_import)colv.elementAt(c);
				if (maxTransactionId < col.getCreationTid())
				{
					maxTransactionId = col.getCreationTid();
				}

				if (maxTransactionId < col.getAccessTid())
				{
					maxTransactionId = col.getAccessTid();
				}
				resData.append(col.getId() + Seperator);
				resData.append(col.getName() + Seperator);
				resData.append(col.getLookupColumnId() + Seperator);
				resData.append(col.getLookupTableId() + Seperator);
				resData.append(col.getAttributes() + Seperator);
			}
			resData.append(ContentDelimeter);
			System.out.println("Time(sec) to fetch columns = " + getElapsedTime());

			// Get the rows
			String lsRowQuery = ""; // Row query String
			// is the view dynamic (not in the criteria table), starts with ?
			boolean viewIsDynamic = false;
			if (view.indexOf("?") == 0)
			{
				System.out.println("View is dynamic = " + view);
				viewIsDynamic = true;
			}
			TableRowInfo tbrowInfo = null;
			if (criteriaTableId == -1 && !viewIsDynamic)
			{
				tbrowInfo = RowManager.getTableRows(connection, tableId, userId, nhId, baselineId, view, 1, -1, -1);
			}
			else
			{
				lsRowQuery  = getRowQuery(connection, tableId, userId, criteriaTableId, true, view);
				tbrowInfo	= TableViewManager.getFiltredTableRows(connection, tableId, userId, lsRowQuery, baselineId);
			}
			Vector rowv = tbrowInfo.getRowVector();

			// rows
			for (int r = 0; r < rowv.size(); r++)
			{
				Row rowObject = (Row)rowv.elementAt(r);

				if (maxTransactionId < rowObject.getCreationTid())
				{
					maxTransactionId = rowObject.getCreationTid();
				}

				if (maxTransactionId < rowObject.getOwnershipAssignedTid())
				{
					maxTransactionId = rowObject.getOwnershipAssignedTid();
				}
				resData.append(rowObject.getId() + Seperator);
			}
			resData.append(ContentDelimeter);
			System.out.println("Time(sec) to fetch rows = " + getElapsedTime());

			// Get the cells
			String q = null;
			String rowQuery = null;
			boolean results = false;
			if (criteriaTableId == -1 && !viewIsDynamic)
			{
				System.out.println("CALL BW_GET_TBL_LNK_IMPORT ---> " + tableId + "," + userId + "," + memberId + "," + nhId + "," + view);
				q = "{CALL BW_GET_TBL_LNK_IMPORT(?,?,?,?,?)}";
				stmt = connection.prepareStatement(q);
				stmt.setInt(1, tableId);
				stmt.setInt(2, userId);
				stmt.setInt(3, memberId);
				stmt.setInt(4, nhId);
				stmt.setString(5, view);
				results = stmt.execute();
			}
			else
			{
				q =
					"SELECT TX_ID FROM BW_USER_EXPORT_TID " +
					"WHERE BW_TBL_ID = ? " +
					"AND BW_USER_ID = ? ;" +
					" CREATE TABLE #ACCESSIBLE_COLUMNS " +
					" ( " +
					" COLID INT NOT NULL PRIMARY KEY, " +
					" ACCESS_ INT, " +
					" PREV_ACCESS INT, " +
					" ACCESS_TID INT, " +
					" SEQUENCE_NUMBER FLOAT, " +
					" ) " +
					"  " +
					" INSERT INTO #ACCESSIBLE_COLUMNS " +
					" SELECT * FROM BW_GetColumnAccess(?, ?, ?)  AS C " +
					" CREATE TABLE #ACCESSIBLE_ROWS ( ID  INT  PRIMARY KEY NOT NULL ) " +
					" INSERT INTO #ACCESSIBLE_ROWS " + lsRowQuery +
					" SELECT " +
					"	BWCELL.STRING_VALUE,  " +
					"	BWCELL.FORMULA , BWCELL.TX_ID " +
					" FROM #ACCESSIBLE_ROWS  AS BWROW, " +
					"	BW_ROW, " +
					"	BW_CELL AS BWCELL, " +
					"	#ACCESSIBLE_COLUMNS C " +
					"WHERE	 " +
					"		 BWCELL.BW_ROW_ID = BW_ROW.ID " +
					"	AND	 BWROW.ID = BW_ROW.ID " +
					"	AND	 BW_ROW.IS_ACTIVE = 1 " +
					"	AND  BWCELL.BW_COLUMN_ID = C.COLID " +
					"ORDER BY C.SEQUENCE_NUMBER, BW_ROW.SEQUENCE_NUMBER " +
					"DROP TABLE #ACCESSIBLE_ROWS " +
					"DROP TABLE #ACCESSIBLE_COLUMNS " ;
				System.out.println("Getting Cells : STRING_VALUE,FORMULA,TX_ID with query ---> " + q);
				stmt = connection.prepareStatement(q);

				//stmt.setInt(1, tableId);
				stmt.setInt(1, tableId);
				stmt.setInt(2, userId);
				stmt.setInt(3, tableId);
				stmt.setInt(4, userId);
				stmt.setInt(5, memberId);
	
				System.out.println("tableId " + tableId);
				System.out.println("userId " + userId);
				System.out.println("memberId " + memberId);			
				results = stmt.execute();
			}
			int rsCount = 0;
			int updcount = 0;
			//Loop through the available result sets.
			do
			{
				if (results)
				{
					ResultSet rs = stmt.getResultSet();

					if (rsCount == 0)// max txid
					{
						if (rs.next())
						{
							exportTid = rs.getInt(1);
							System.out.println("exportTid = " + exportTid);
							if (maxTransactionId < exportTid)
								maxTransactionId = exportTid;
						}
					}
					else // cell data
					{
						System.out.println("colv size = " + colv.size());
						System.out.println("rowv size = " + rowv.size());
						for (int c = 0; c < colv.size(); c++)
						{
							StringBuffer cellData = new StringBuffer(10000000);
							StringBuffer fmlData = new StringBuffer(10000000);
							xlColumn_import col = (xlColumn_import)colv.elementAt(c);
							int colAccess = col.getAccess();
							for (int r = 0; r < rowv.size(); r++)
							{
								System.out.println("c="+c+",r="+r);
								rs.next();
								String cellval = rs.getString(1);
								//int idx = cellval.indexOf("9999");
								//if (idx > -1)
								//{
									//System.out.println("cellval -----> .." + cellval + ".. Length ---> " + cellval.length());
									if (cellval.length() > 0)
									{
										cellval = cellval.trim();
									}
									//System.out.println("cellval -----> .." + cellval + ".. Length ---> " + cellval.length());
								//}
								
								String cellFormula = rs.getString(2);
								int celltid = rs.getInt (3);
								if (maxTransactionId < celltid)
									maxTransactionId = celltid;
								int cellAccess = java.lang.Math.min(raccess, colAccess);

								if (cellFormula == null || mode == 1)
								{
									cellFormula = "";
								}
								else
								{
									cellFormula = cellFormula.trim();
								}

								//System.out.println("cellval[" + r + "][" + c + "]= " + cellval);
								if (r == rowv.size() - 1) // last cell of the column
								{
									cellData.append(cellval);
									fmlData.append(cellFormula);
									//accData.append(cellAccess);
								}
								else
								{
									cellData.append(cellval + Seperator);
									fmlData.append(cellFormula + Seperator);
									//accData.append(cellAccess + Seperator);
								}
							}
							cellData.append(ContentDelimeter);
							fmlData.append(ContentDelimeter);
							//accData.append(ContentDelimeter);

							resData.append(cellData.toString());
							resData.append(fmlData.toString());
						}
					}
					rsCount++;
					rs.close();
					System.out.println("rsCount ----------" + rsCount);
				}
				else
				{
					updcount = stmt.getUpdateCount();
					if (updcount >= 0)
					{
						System.out.println("Update data displayed here. + updcount " + updcount);
					}
					else
					{
						System.out.println("No more results to process.");
					}
				}

				results = stmt.getMoreResults(); 
			} while (results || updcount != -1);
			stmt.close();


			System.out.println("Time(sec) to fetch cells = " + getElapsedTime());

			int maxdeletedcell_tid;
			maxdeletedcell_tid = 0;
			try
			{
				stmt = connection.prepareStatement
					("SELECT MAX(BW_ROW.TX_ID) FROM BW_ROW WHERE BW_ROW.BW_TBL_ID = ? AND BW_ROW.IS_ACTIVE = 0 UNION SELECT MAX(BW_COLUMN.TX_ID) FROM BW_COLUMN WHERE BW_COLUMN.BW_TBL_ID = ? AND BW_COLUMN.IS_ACTIVE = 0");
				stmt.setInt(1, tableId);
				stmt.setInt(2, tableId);
				ResultSet rs1 = stmt.executeQuery();
				while (rs1.next())
				{
					if (rs1.getInt(1) > maxdeletedcell_tid)
						maxdeletedcell_tid = rs1.getInt(1);
				}
				rs1.close();
				stmt.close();
				stmt = null;
				rs1 = null;
			}
			catch (Exception e11)
			{
				e11.printStackTrace();
			}

			System.out.println("Time(sec) to getmaxtid for deleted cells = " + getElapsedTime());
			if (maxdeletedcell_tid > maxTransactionId)
			{
				maxTransactionId = maxdeletedcell_tid;
				System.out.println("maxtid reset by cell deactivation to = " + maxTransactionId);
			}

			//resData.append(cellData.toString());
			//resData.append(fmlData.toString());
			//resData.append(ContentDelimeter);
			// access
			//resData.append(accData.toString());
			//resData.append(ContentDelimeter);


			// TBD : Formulae
			//// Get the formulas for this table. It will return only the Rowid, Colid and Formula where ever applicable.
			//// Will this Stored procedure be heavy ? May be we can write simple queries for the same

			//String lsQuery = "{CALL BW_GET_FORMULA_LNK_IMPORT(?,?,?,?,?)}";
			//stmt = connection.prepareStatement(lsQuery);
			//stmt.setInt(1, tableId);
			//stmt.setInt(2, userId);
			//stmt.setInt(3, memberId);
			//stmt.setInt(4, nhId);
			//stmt.setString(5, view);
			//ResultSet rs        = stmt.executeQuery();
			//String lsRowID      = "";
			//String lsColID      = "";
			//String lsFormulaVal = "";
			//Vector	lvForInfo	= new Vector();
			//StringBuffer sbFormulaStr = new StringBuffer();
			//while (rs.next())
			//{
			//    lsRowID			= rs.getString(1);
			//    lsColID			= rs.getString(2);
			//    lsFormulaVal	= rs.getString(3);

			//    lvForInfo.addElement(lsRowID);
			//    lvForInfo.addElement(lsColID);
			//    lvForInfo.addElement(lsFormulaVal);
			//}
			//int liFormulaSize = lvForInfo.size();

			//for(int liCnt = 0 ; liCnt < liFormulaSize; liCnt++)
			//{
			//    sbFormulaStr.append(lvForInfo.get(liCnt));
			//    sbFormulaStr.append(Seperator);

			//    if(liCnt == liFormulaSize -1)
			//        sbFormulaStr.append(ContentDelimeter);
			//}
			//resData.append(ContentDelimeter); commented by shirish on 9/4/07 since a content delimiter is already being appended at the end of formula buffer

			//if (accData.toString().length() > 0)
			//{
			//    resData.append(accData.toString().substring(0, accData.length() - 1));
			//}
			//resData.append(ContentDelimeter);

			// write the header to the response
			StringBuffer resHeader = new StringBuffer();
			resHeader.append("Success" + Seperator);
			resHeader.append(tableId + Seperator);
			resHeader.append(tinfo.getTableName() + Seperator);
			resHeader.append(tinfo.getTablePurpose() + Seperator);
			resHeader.append(view + Seperator);
			resHeader.append(userId + Seperator);
			resHeader.append(memberId + Seperator);
			resHeader.append(nhId + Seperator);
			resHeader.append(colv.size() + Seperator);
			resHeader.append(rowv.size() + Seperator);
			resHeader.append(maxTransactionId + Seperator);
			resHeader.append(exportTid + Seperator);
			resHeader.append(criteriaTableId + Seperator);
			resHeader.append(mode + Seperator);

			return resHeader.toString() + ContentDelimeter + resData.toString();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}


	// New Method Sanjeev normal view
	public static TableContents getFiltredTableContentsForBrowser(int tableId, int userId, int memberId, int nhId, int critTableId, int baselineId, String view , int asOfTid, long asOfDate, int compTid, long compDate)
	{
		System.out.println("getFiltredTableContentsForBrowser Started" );
		boolean getTableDelta	= false;
		boolean getTableAsOf	= false;
		boolean	getBaseline		= false;

		// Acces rights
		PreparedStatement stmt	= null;
		int maxTransactionId	= -1;
		Connection connection	= null;
		Calendar cal			= Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		TableInfo tinfo			= null;
		TableAccessList ftal	= null;
		try
		{
			DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
			connection	= databaseloader.getConnection();
			tinfo		= TableManager.getTableInfo(connection, userId, tableId);
			ftal		= getSuggestedAccess(connection, tinfo, userId, memberId, nhId);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		if (view == null || view.trim() == "")
		{
			view = ftal.getSuggestedViewPreferenceBasedOnAccess();
			System.out.println("Suggested view pref = " + view);
		}
		// Check access control :: TBD
		int raccess = 1;
		int ACLFromDB = ftal.getACL();
		TableAccessRequest wAccess = new TableAccessRequest(tableId, view, true);
		int wACL = wAccess.getACL();
		int awACL = wACL & ACLFromDB;
		if (awACL == wACL)
		{
			raccess = 2;
			System.out.println("Rows have write access");
		}
		else
		{
			System.out.println("Rows are readonly");
		}
		// If db access and passed access are same then
		if(baselineId > 0)
			getBaseline = true;

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

		String lsRowQuery = "";

		try
		{
			lsRowQuery = TableViewManager.getRowQuery(connection, tableId, userId, critTableId, true, view);
//			System.out.println(" lsRowQuerylsRowQuerylsRowQuerylsRowQuery ->"+lsRowQuery);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

//		int asOfTid				= -1;
		String requestedColumns = null;
		Vector columnNames		= new Vector();
		Hashtable cellsbyrowids = new Hashtable();
		Vector tableCells		= new Vector();
		TableContents tbc = null;
		TableColumnInfo tbcolInfo = null;
		try
		{
			tbcolInfo = ColumnManager.getTableColumnInfo(		connection,
																tableId,
																baselineId,
																userId,
																memberId,
																asOfTid,
																requestedColumns);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
//		System.out.println("Time(sec) to get columns for the table = " + (System.currentTimeMillis()-prevTime)/1000F);
//		prevTime = System.currentTimeMillis();
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

		TableRowInfo tbrowInfo = null;
		try
		{
			if (getTableAsOf == true)
			{
//				System.out.println(" getTableAsOf asOfTid "+asOfTid +" asOfDate "+ asOfDate);
				//(int tableId, int userId, int memberId, int nhId, int critTableId, int baselineId, String view, int asOfTid, long asOfDate, int compTid, long compDate)
				tbrowInfo = TableViewManager.getFiltredTableRows_T(connection, tableId, userId, nhId, view, lsRowQuery, baselineId,  asOfTid, asOfDate);
//				System.out.println(" getTableAsOf asOfTid Done ");
			}
			else if (getTableDelta == true)
			{
//				System.out.println(" getTableDelta compTid "+compTid+" compDate "+ compDate);
				tbrowInfo = TableViewManager.getFiltredTableRows_T(connection, tableId, userId, nhId, view, lsRowQuery, baselineId, compTid, compDate);
//				System.out.println(" getTableDelta compTid Done ");
			}
			else
			{
//				System.out.println(" Else ");
				tbrowInfo = TableViewManager.getFiltredTableRows(connection, tableId, userId, lsRowQuery, baselineId);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

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

		// Params for query  tableId, userId, memberId
		String lsQuery = "";

//		System.out.println("Check here tableId " + tableId+ " baselineId "+ baselineId);
		lsQuery	= QueryMaker.getFiltredQueryStringForBrowser(lsRowQuery, getBaseline, getTableAsOf, requestedColumns);

//		System.out.println("Check here query " + lsQuery);

		ResultSet resultset			= null;
//		Statement statement = null;
//		boolean isFirstRowDone		= false;
//		int     a_previousrowid		= -1;
		int noOfCells				= 0;

//		System.out.println("Check here userId" + userId+ " memberId "+ memberId);

		try
		{
			stmt = connection.prepareStatement(lsQuery);
			if(getBaseline)
			{
				stmt.setInt(1, tableId);
				stmt.setInt(2, userId);
				stmt.setInt(3, memberId);
				stmt.setInt(4, tableId);
				stmt.setInt(5, baselineId);

				stmt.setInt(6, tableId);
				stmt.setInt(7, userId);
				stmt.setInt(8, memberId);
				stmt.setInt(9, tableId);
				stmt.setInt(10, baselineId);

				stmt.setInt(11, tableId);
				stmt.setInt(12, userId);
				stmt.setInt(13, memberId);
				stmt.setInt(14, tableId);
				stmt.setInt(15, baselineId);

				stmt.setInt(16, tableId);
				stmt.setInt(17, userId);
				stmt.setInt(18, memberId);
				stmt.setInt(19, tableId);
				stmt.setInt(20, baselineId);
			}
			else
			{
				if(getTableAsOf)
				{
					java.sql.Timestamp TDATE = null;

					if(asOfTid > 0 )
						TDATE = getTimeStampForTid(connection, asOfTid);
					else
						TDATE = new java.sql.Timestamp(asOfDate);

					stmt.setInt(1, tableId);
					stmt.setInt(2, userId);
					stmt.setInt(3, memberId);

					stmt.setInt(4, tableId);
					stmt.setTimestamp(5, TDATE, cal);
					stmt.setInt(6, tableId);
					stmt.setTimestamp(7, TDATE, cal);

					stmt.setInt(8, tableId);
					stmt.setInt(9, userId);
					stmt.setInt(10, memberId);

					stmt.setInt(11, tableId);
					stmt.setTimestamp(12, TDATE, cal);
					stmt.setInt(13, tableId);
					stmt.setTimestamp(14, TDATE, cal);
				}
				else
				{
					stmt.setInt(1, tableId);
					stmt.setInt(2, userId);
					stmt.setInt(3, memberId);
					stmt.setInt(4, tableId);
				}
			}


			if ( stmt != null )
			{
				resultset = stmt.executeQuery();
			}

//			System.out.println(" resultset "+resultset);

			while ( resultset!= null && resultset.next() )
			{
				int 	a_rowid;
				int     a_columnid;
				int     a_cellid;
				String  a_cellstringvalue;
				int     a_cellintvalue;
				int     a_celltablevalue = -1;
				double  a_celldoublevalue;
				int		tid				= -1;
				int		userid			= -1;
				String emailAddress		= "";
				String description		= "";
				String comment			= "";
				String a_celltableName	= "Not Set";
				String a_cellFormula	= null;

				a_rowid				= resultset.getInt("ROW_ID");
				a_columnid			= resultset.getInt("COLUMN_ID");
				a_cellid			= resultset.getInt("CELL_ID");
				a_cellstringvalue	= resultset.getString("CELL_STRING_VALUE");
				a_cellintvalue		= resultset.getInt("CELL_INTEGER_VALUE");
				a_celldoublevalue	= resultset.getDouble("CELL_DOUBLE_VALUE");
				tid					= resultset.getInt("TX_ID");
				userid				= resultset.getInt("TX_CREATED_BY");

				java.sql.Timestamp a_createdOnDate = resultset.getTimestamp("CREATED_ON", cal);
				description			= resultset.getString("DESCRIPTION");
				comment				= resultset.getString("COMMENT_");
				emailAddress		= resultset.getString("EMAIL_ADDRESS");
				//if (getLatest == true || getTableAsOf == true || getTableDelta == true)
				//{
					a_cellFormula	= resultset.getString ("FORMULA");
					//System.out.println("Got cell with formula " + a_cellFormula);
				//}
				Transaction cellTransaction = new Transaction(tid, userid, emailAddress, a_createdOnDate.getTime(),description, comment );
				String a_celltype;
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
//				System.out.println("got  a cell with row id "+ a_rowid);
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
				tbc = new TableContents( rowIds, rowNames, columnNames, cellsbyrowids, existingCols,  SourceType.TABLE, ftal, columns, RowObjsByRowId);
			}
			else
			{
				if ( existingCols.size() > 0 )
				{

					tbc = new TableContents( new Vector(), new Vector(), columnNames, new Hashtable(),existingCols ,  SourceType.TABLE, ftal,columns,RowObjsByRowId);
				 }
				else
				{
					tbc = new TableContents( new Vector(), new Vector(), new Vector(), new Hashtable(), new Hashtable(),  SourceType.TABLE, ftal,columns,RowObjsByRowId);
				}
			}
		}
		catch(SQLException sqlexception)
		{
			sqlexception.printStackTrace();
			System.out.println("sqlexception:::::::;;"+sqlexception.getErrorCode() );
		}
		finally
		{
			try
			{

				if ( resultset != null )
					resultset.close();

				if ( stmt != null )
					stmt.close();


			}
			catch(SQLException sqlexception) {
				System.out.println("sqlexception:::::::;;"+sqlexception.getErrorCode() );
			}
		}
		System.out.println("getFiltredTableContentsForBrowser Done " );
		return tbc;
	}


	// New Method Sanjeev new Method view
	public static TableContents getFiltredTableContentsForBrowserChanged(int tableId, int userId, int memberId, int nhId, int critTableId, int baselineId, String view, int asOfTid, long asOfDate, int compTid, long compDate, String period)
	{
//		System.out.println(" asOfTid "+asOfTid);
//		System.out.println(" asOfDate "+asOfDate);
//		System.out.println(" compTid "+compTid );
//		System.out.println(" compDate "+compDate);

		boolean getTableDelta	= false;
		boolean getTableAsOf	= false;
		boolean	getBaseline		= false;
		boolean LastChange		= false;
		boolean ForPeriod		= false;

		// Acces rights
		PreparedStatement stmt	= null;
		int maxTransactionId	= -1;
		Connection connection	= null;
		Calendar cal			= Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		TableInfo tinfo			= null;
		TableAccessList ftal	= null;

		java.sql.Timestamp STDATE		= null;
		java.sql.Timestamp EDATE		= null;
		java.sql.Timestamp TableTrans	= null;

		try
		{
			DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
			connection	= databaseloader.getConnection();
			tinfo		= TableManager.getTableInfo(connection, userId, tableId);
			ftal		= getSuggestedAccess(connection, tinfo, userId, memberId, nhId);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		if (view == null || view.trim() == "")
		{
			view = ftal.getSuggestedViewPreferenceBasedOnAccess();
			System.out.println("Suggested view pref = " + view);
		}
		// Check access control :: TBD
		int raccess = 1;
		int ACLFromDB = ftal.getACL();
		TableAccessRequest wAccess = new TableAccessRequest(tableId, view, true);
		int wACL = wAccess.getACL();
		int awACL = wACL & ACLFromDB;
		if (awACL == wACL)
		{
			raccess = 2;
			System.out.println("Rows have write access");
		}
		else
		{
			System.out.println("Rows are readonly");
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

		if(asOfDate > -1 && compDate > -1 )
		{
			if(asOfDate == compDate)
				LastChange = true;
		}

		if(period != null )
		{
			if(period.equals("custom"))
				getTableDelta = true;
			else
				ForPeriod = true;
		}

		String lsRowQuery = "";

		try
		{
			lsRowQuery = TableViewManager.getRowQuery(connection, tableId, userId, critTableId, false, view);
//			System.out.println(" lsRowQuerylsRowQuerylsRowQuerylsRowQuery ->"+lsRowQuery);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

//		int asOfTid				= -1;
		String requestedColumns = null;
		Vector columnNames		= new Vector();
		Hashtable cellsbyrowids = new Hashtable();
		Vector tableCells		= new Vector();
		TableContents tbc = null;
		TableColumnInfo tbcolInfo = null;
		try
		{
			tbcolInfo = ColumnManager.getTableColumnInfo(		connection,
																tableId,
																baselineId,
																userId,
																memberId,
																asOfTid,
																requestedColumns);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		Hashtable existingCols		= tbcolInfo.getColumnHash();
		Vector columns				= tbcolInfo.getColumnVector();
		Hashtable columnIdToIndex	= new Hashtable();
		Vector CellHolder			= new Vector(columns.size());

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

		TableRowInfo tbrowInfo = null;

//		tbrowInfo = RowManager.getTableRows( connection, a_tableid , a_userid,  a_userNhId, a_baselineid, ViewPreference, rowState, rowStartSeqId, rowEndSeqId );
		try
		{
			if (getTableAsOf == true)
			{
//				System.out.println("Changed getTableAsOf asOfTid "+asOfTid +" asOfDate "+ asOfDate);
				//(int tableId, int userId, int memberId, int nhId, int critTableId, int baselineId, String view, int asOfTid, long asOfDate, int compTid, long compDate)
				tbrowInfo = TableViewManager.getFiltredTableRows_T(connection, tableId, userId, nhId, view, lsRowQuery, baselineId,  asOfTid, asOfDate);
			}
			else if (getTableDelta == true)
			{
//				System.out.println("Changed  getTableDelta compTid "+compTid+" compDate "+ compDate);
				tbrowInfo = TableViewManager.getFiltredTableRows_T(connection, tableId, userId, nhId, view, lsRowQuery, baselineId, compTid, compDate);
			}
			else
			{
//				System.out.println(" Changed Else ");
				tbrowInfo = TableViewManager.getFiltredTableRows(connection, tableId, userId, lsRowQuery, baselineId);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		Vector rowVector			= tbrowInfo.getRowVector();
		Hashtable  RowObjsByRowId	= tbrowInfo.getRowHash();
		Vector rowNames				= new Vector();
		//System.out.println("Done getRowsByTable got no of rows = "+ rowVector.size() );
		Vector rowIds				= new Vector();

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

		// Params for query  tableId, userId, memberId
		String lsQuery = "";

		String reqColids	= null;
		ResultSet resultset	= null;

		if(getTableDelta)
		{
			try
			{
				lsQuery = QueryMaker.getFiltredQueryStringForBrowserChanged(lsRowQuery, reqColids);

				// int asOfTid, long asOfDate, int compTid, long compDate

				if(asOfTid > 0 )
					EDATE = getTimeStampForTid(connection, asOfTid);
				else
					EDATE = new java.sql.Timestamp(asOfDate);

				if(compTid > 0 )
					STDATE = getTimeStampForTid(connection, compTid);
				else
					STDATE = new java.sql.Timestamp(compDate);

				if(EDATE != null && STDATE != null && STDATE.equals(EDATE))
				{ // Find the appropriate tids

					String lsSql = " select bw_txs.created_on from bw_tbl,bw_txs "+
									" where bw_tbl.id = ? and bw_tbl.tx_id = bw_txs.tx_id";

					stmt = connection.prepareStatement(lsSql);
					stmt.setInt(1,tableId);
					resultset = stmt.executeQuery();
					while (resultset.next())
					{
						TableTrans = resultset.getTimestamp(1, cal);
					}
					stmt		= null;
					resultset	= null;

					// @table_id , @d1, @EDATE, @user_id , @nh_id , @view_pref
					lsSql = " SELECT DISTINCT TOP 2(BW_TXS.CREATED_ON)"+
					" FROM BW_TXS, BW_getTableTransactions(? , ?, ?, ?, ?, ?) TXS "+
					" WHERE BW_TXS.TX_ID = TXS.TX_ID order by BW_TXS.CREATED_ON desc";

					stmt = connection.prepareStatement(lsSql);
					stmt.setInt(1,tableId);
					stmt.setTimestamp(2, TableTrans, cal);
					stmt.setTimestamp(3, EDATE, cal);
					stmt.setInt(4,userId);
					stmt.setInt(5,nhId);
					stmt.setString(6,view);
					int cnt = 0;
					resultset = stmt.executeQuery();
					while (resultset.next())
					{
						if(cnt == 0)
							EDATE = resultset.getTimestamp(1, cal);
						else
							STDATE = resultset.getTimestamp(1, cal);
						cnt++;
					}
					resultset	= null;
					stmt		= null;
				}
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}

//		System.out.println("Check here query " + lsQuery);

//		boolean isFirstRowDone		= false;
//		int     a_previousrowid		= -1;
		int noOfCells				= 0;

		try
		{
			stmt = connection.prepareStatement(lsQuery);
			stmt.setInt(1,tableId);
			stmt.setTimestamp(2, STDATE, cal);
			stmt.setTimestamp(3, EDATE, cal);

			stmt.setInt(4,tableId);
			stmt.setInt(5,userId);
			stmt.setInt(6,nhId);

			stmt.setInt(7,tableId);
			stmt.setInt(8,userId);
			stmt.setInt(9,nhId);

			if ( stmt != null )
			{
				resultset = stmt.executeQuery();
			}

			while ( resultset!= null && resultset.next() )
			{
				int 	a_rowid;
				int     a_columnid;
				int     a_cellid;
				String  a_cellstringvalue;
				int     a_cellintvalue;
				int     a_celltablevalue = -1;
				double  a_celldoublevalue;
				int		tid				= -1;
				int		userid			= -1;
				String emailAddress		= "";
				String description		= "";
				String comment			= "";
				String a_celltableName	= "Not Set";
				String a_cellFormula	= null;

				a_rowid				= resultset.getInt("ROW_ID");
				a_columnid			= resultset.getInt("COLUMN_ID");
				a_cellid			= resultset.getInt("CELL_ID");
				a_cellstringvalue	= resultset.getString("CELL_STRING_VALUE");
				a_cellintvalue		= resultset.getInt("CELL_INTEGER_VALUE");
				a_celldoublevalue	= resultset.getDouble("CELL_DOUBLE_VALUE");
				tid					= resultset.getInt("TX_ID");
				userid				= resultset.getInt("TX_CREATED_BY");

				java.sql.Timestamp a_createdOnDate = resultset.getTimestamp("CREATED_ON", cal);
				description			= resultset.getString("DESCRIPTION");
				comment				= resultset.getString("COMMENT_");
				emailAddress		= resultset.getString("EMAIL_ADDRESS");
				//if (getLatest == true || getTableAsOf == true || getTableDelta == true)
				//{
					a_cellFormula	= resultset.getString ("FORMULA");
					//System.out.println("Got cell with formula " + a_cellFormula);
				//}
				Transaction cellTransaction = new Transaction(tid, userid, emailAddress, a_createdOnDate.getTime(),description, comment );
				String a_celltype;
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
//				System.out.println("got  a cell with row id "+ a_rowid);
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
				tbc = new TableContents( rowIds, rowNames, columnNames, cellsbyrowids, existingCols,  SourceType.TABLE, ftal, columns, RowObjsByRowId);
			}
			else
			{
				if ( existingCols.size() > 0 )
				{

					tbc = new TableContents( new Vector(), new Vector(), columnNames, new Hashtable(),existingCols ,  SourceType.TABLE, ftal,columns,RowObjsByRowId);
				 }
				else
				{
					tbc = new TableContents( new Vector(), new Vector(), new Vector(), new Hashtable(), new Hashtable(),  SourceType.TABLE, ftal,columns,RowObjsByRowId);
				}
			}
		}
		catch(SQLException sqlexception)
		{
			sqlexception.printStackTrace();
			System.out.println("sqlexception:::::::;;"+sqlexception.getErrorCode() );
		}
		finally
		{
			try
			{

				if ( resultset != null )
					resultset.close();

				if ( stmt != null )
					stmt.close();


			}
			catch(SQLException sqlexception) {
				System.out.println("sqlexception:::::::;;"+sqlexception.getErrorCode() );
			}
		}

		return tbc;
	}

	// Method for getting the Transaction Date when TID is passed.
	protected static java.sql.Timestamp getTimeStampForTid(Connection connection, int tid)
	{
		java.sql.Timestamp a_createdOnDate = null;
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		String lsQuery = "";

		if(tid > 0 )
		{
			lsQuery		= " SELECT BW_TXS.CREATED_ON FROM BW_TXS WHERE BW_TXS.TX_ID = ? ";
			try
			{
				pstmt = connection.prepareStatement(lsQuery);
				pstmt.setInt(1,tid);
				rs = pstmt.executeQuery();
				Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
				while (rs.next())
				{
					a_createdOnDate = rs.getTimestamp(1, cal);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				try
				{
					pstmt.close();
					pstmt = null;
					lsQuery = null;
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		return a_createdOnDate;

	}

	static long prevTime = -1;
	private static float getElapsedTime()
	{
		if (prevTime == -1)
			prevTime = System.currentTimeMillis();
		// Get elapsed time in seconds
		float elapsedTimeSec = (System.currentTimeMillis() - prevTime) / 1000F;

		// reset time
		prevTime = System.currentTimeMillis();

		return elapsedTimeSec;
	}

}
