
package com.boardwalk.table;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;

import com.boardwalk.database.DatabaseLoader;
import com.boardwalk.exception.NeighborhoodException;
import com.boardwalk.exception.SystemException;
import com.boardwalk.logic.RowCriteria;
import com.boardwalk.model.IColumnDal;
import com.boardwalk.dal.DalWrapper;
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

	public static RowClassification getRowClassification(Connection connection, int tableId) throws SystemException
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

	public static int getAccessTable(Connection connection, int tableId, int userId)
			throws SystemException
		{
			CallableStatement cs = null;
			int accTableId = -1;
			try
			{
				//String query = "SELECT BW_CRIT_TBL.BW_CR_TBL_ID FROM BW_CRIT_TBL WHERE BW_CRIT_TBL.BW_TBL_ID = ?";
				cs = connection.prepareCall("{CALL BW_GET_ACCESS_TABLE(?,?,?)}");
				cs.setInt(1, tableId);
				cs.setInt(2, userId);
				cs.registerOutParameter (3, java.sql.Types.INTEGER );

				cs.execute ();
				accTableId = cs.getInt(3);
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

			System.out.println("getAccessTable() = " + accTableId);
			return accTableId;
		}

	
	public static int getCriteriaTable(Connection connection, int tableId, int userId)
		throws SQLException
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
		finally
		{
			cs.close();
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
			//Modified by Lakshman on 20171108 to avoid self joins on BW_CELL for performance gain - START
			String rowQuery = getRowQuery(connection, tableId, userId, crTableId, true, viewPref, "TABLE");
			String query = rowQuery +
							"	SELECT " +
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
							"FROM #ACCESSIBLE_ROWS AS BWROW, " +
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
							"ORDER BY BW_ROW.SEQUENCE_NUMBER, COL.SEQUENCE_NUMBER " +
							" DROP TABLE #ACCESSIBLE_ROWS " +
							" IF OBJECT_ID('tempdb..#CELL_TEMP') IS NOT NULL DROP TABLE #CELL_TEMP " ;
			//Modified by Lakshman on 20171108 to avoid self joins on BW_CELL for performance gain
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
				//Modified by Lakshman on 20171108 to avoid self joins on BW_CELL for performance gain - START
				query = asRowQuery +
						" SELECT BWROW.ID AS ROW_ID, " +
						"		BWROW.NAME AS ROW_NAME, " +
						"		BWROW.SEQUENCE_NUMBER AS ROW_SEQUENCE_NUMBER, " +
						"		BWROW.OWNER_ID, " +
						"		BWROW.IS_ACTIVE, " +
						"		BWROW.TX_ID AS ROW_TID, " +
						"		BWROW.OWNER_TID AS ROW_OWNER_TID, " +
						"		BW_ROW_CREATOR_TX.CREATED_BY AS ROW_CREATOR_ID, " +
						"		BW_ROW_OWNER.EMAIL_ADDRESS AS ROW_OWNER_NAME " +
						"FROM 	#ACCESSIBLE_ROWS AS R, " +
						"		BW_ROW AS BWROW, BW_TXS AS BW_ROW_CREATOR_TX,	 " +
						"		BW_USER AS BW_ROW_OWNER " +
						"WHERE  BWROW.IS_ACTIVE = 1 " +
						"AND   	BW_ROW_CREATOR_TX.TX_ID = BWROW.TX_ID " +
						"AND    BW_ROW_OWNER.ID = BWROW.OWNER_ID " +
						"AND	BWROW.ID = R.ROWID " +	//Modified by Lakshman on 20181004 to fix Issue Id: 14274
						"ORDER BY BWROW.SEQUENCE_NUMBER "+
						"DROP TABLE #ACCESSIBLE_ROWS "+
						" IF OBJECT_ID('tempdb..#CELL_TEMP') IS NOT NULL DROP TABLE #CELL_TEMP ";
				//Modified by Lakshman on 20171108 to avoid self joins on BW_CELL for performance gain - END
			}
			else
			{
				// With baseline
				//Modified by Lakshman on 20171108 to avoid self joins on BW_CELL for performance gain - START
				query = asRowQuery +
						" SELECT BWROW.ID AS ROW_ID, BWROW.NAME AS ROW_NAME,"+
						"		BW_BL_ROW.SEQUENCE_NUMBER AS ROW_SEQUENCE_NUMBER,"+
						"		BW_BL_ROW.OWNER_ID, IS_ACTIVE = 1, BWROW.TX_ID AS ROW_TID,"+
						"		BW_BL_ROW.OWNER_TID AS ROW_OWNER_TID, "+
						"		BW_ROW_CREATOR_TX.CREATED_BY AS ROW_CREATOR_ID, "+
						"		BW_ROW_OWNER.EMAIL_ADDRESS AS ROW_OWNER_NAME "+
						" FROM 	#ACCESSIBLE_ROWS AS RQ, " +
						" 		BW_ROW AS BWROW,BW_TXS AS BW_ROW_CREATOR_TX,"+
						"		BW_USER AS BW_ROW_OWNER,"+
						"		BW_BL_ROW "+
						" WHERE BWROW.BW_TBL_ID = "+a_table_id +
						" AND	BWROW.ID = BW_BL_ROW.ROW_ID"+
						" AND   BW_BL_ROW.BASELINE_ID = "+a_basline_id +
						" AND	BW_ROW_CREATOR_TX.TX_ID = BW_BL_ROW.TX_ID"+
						" AND	BWROW.ID = RQ.ROWID " +		//Modified by Lakshman on 20181004 to fix Issue Id: 14274
						" AND   BW_ROW_OWNER.ID = BW_BL_ROW.OWNER_ID "+
						" ORDER BY BWROW.SEQUENCE_NUMBER "+
						" DROP TABLE #ACCESSIBLE_ROWS "+
						" IF OBJECT_ID('tempdb..#CELL_TEMP') IS NOT NULL DROP TABLE #CELL_TEMP ";
				//Modified by Lakshman on 20171108 to avoid self joins on BW_CELL for performance gain - END
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
					System.out.println("Inside getFiltredTableRows_T: lsSqlStr: "+ lsSqlStr + asOfTid);

					lsSqlStr = " SELECT BW_TXS.CREATED_ON FROM BW_TXS WHERE BW_TXS.TX_ID = ? ";
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
						System.out.println("SQLException: "+sqlexception.getErrorCode() );
					}
				}
				else
				{
					tsTranDate = new java.sql.Timestamp(asOfDate);
					System.out.println("SQLException: "+tsTranDate);
				}

				//Modified by Lakshman on 20171108 to avoid self joins on BW_CELL for performance gain - START
				lsSqlStr = 	asRowQuery +
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
							" BW_ROW AS BWROW,"+
							" BW_TXS AS BW_ROW_CREATOR_TX,"+
							" BW_USER AS BW_ROW_OWNER, "+
							" BW_getStatusTransactions(?,?) AS STX,"+
							" BW_CELL_STATUS,"+
							" BW_CELL "+
							" WHERE "+
							" BWROW.ID = BW_CELL.BW_ROW_ID "+
							" AND BW_CELL.ID = BW_CELL_STATUS.BW_CELL_ID "+
							" AND BW_CELL.ID = STX.CELL_ID "+
							" AND BW_CELL_STATUS.TX_ID = STX.TX_ID "+
							" AND BWROW.BW_TBL_ID = ? " +
							" AND BW_ROW_CREATOR_TX.TX_ID = BWROW.TX_ID "+
							" AND BW_ROW_OWNER.ID = BWROW.OWNER_ID "+
							" AND BWROW.ID = RQ.ROWID "+
							" ORDER BY BWROW.SEQUENCE_NUMBER "+
							" DROP TABLE #ACCESSIBLE_ROWS "+
							" IF OBJECT_ID('tempdb..#CELL_TEMP') IS NOT NULL DROP TABLE #CELL_TEMP ";
			}

//			System.out.println(" getFiltredTableRows query = " + lsSqlStr);
			ps = connection.prepareStatement(lsSqlStr);

			ps.setInt(1, aTableId);
			ps.setTimestamp(2, tsTranDate, cal);
			ps.setInt(3, aTableId);
			//Modified by Lakshman on 20171108 to avoid self joins on BW_CELL for performance gain - END

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

//	public static HashMap getCriteriaRestrictions(Connection connection, int userId, int a_cr_table_id, int target_tbl_id, String view) 
//			throws SQLException
//	{
//		HashMap criteriaRestrictions = new HashMap();
//		
//		ResultSet rs = null;
//		CallableStatement cs = null;
//		String query = null;
//		try
//		{
//			cs = connection.prepareCall("{CALL BW_GET_FILTER_ACCESS_FOR_USER(?,?,?)}");
//			cs.setInt(1, a_cr_table_id);
//			cs.setInt(2, userId);
//			cs.setInt(3, target_tbl_id);
//			rs = cs.executeQuery();
//			
//			while (rs.next())
//			{
//				String val = rs.getString(1);
//				int qid = rs.getInt(2);
//				String colName = rs.getString(3);
//				int trgColid = rs.getInt(4);
//
//
//			}
//		}
//		finally
//		{
//				if (rs != null) rs.close();
//				if (cs != null) cs.close();
//		}
//
//		
//		
//		return criteriaRestrictions;
//	}

	public static HashMap getCriteriaForUser(Connection connection, int userId, int a_cr_table_id, int target_tbl_id, String view) throws SQLException
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
				//Modified by Lakshman on 20171108 to avoid self joins on BW_CELL for performance gain - START
				String rq = getRowQuery(connection, a_cr_table_id, userId, base_criteria_table, true, view, "TABLE");
				query =	rq + 
						" SELECT  VALCELL.STRING_VALUE AS VAL, ROW.ID AS ROW, VALCOL.NAME AS COL, TARGET_COL.ID TRGCOL " +
						"FROM	#ACCESSIBLE_ROWS AS ROW, " +
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
						"AND  TARGET_COL.NAME = CRIT_COL.NAME " +
						
						"DROP TABLE #ACCESSIBLE_ROWS " + 
						" IF OBJECT_ID('tempdb..#CELL_TEMP') IS NOT NULL DROP TABLE #CELL_TEMP ";
					//Modified by Lakshman on 20171108 to avoid self joins on BW_CELL for performance gain - END

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
						"AND 	VIEWCELL.STRING_VALUE = ? " + //Modified by Lakshman on 20190219 to fix SQL Injection
						"AND  TARGET_TBL.ID = ? " +
						"AND  TARGET_COL.BW_TBL_ID = TARGET_TBL.ID " +
						"AND  TARGET_COL.IS_ACTIVE = 1 " +
						"AND  TARGET_COL.NAME = DATACOL.NAME " +
						"AND  BW_ROW.IS_ACTIVE = 1 ";
				System.out.println("query to fetch criteria = " + query);
				ps = connection.prepareStatement(query);
				ps.setInt(1, a_cr_table_id);
				ps.setInt(2, userId);
				ps.setString(3, view); //Modified by Lakshman on 20190219 to fix SQL Injection
				ps.setInt(4, target_tbl_id);
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
		finally
		{
			rs.close();
			ps.close();
		}

		System.out.println("End : getCriteriaForUser()");
		return criteria;
	}

	public static String getRowQuery(Connection connection, HashMap criteria, int a_table_id, boolean active, String resultType)
	{
		return TableViewManager.getRowQuery(connection, criteria, a_table_id, active, resultType, null);
	}

	public static String getRowQuery(Connection connection, HashMap criteria, int a_table_id, boolean active, String resultType, ArrayList<Integer> resultList)
	{
		RowCriteria rc = new RowCriteria(new DalWrapper(connection), resultList);
		String query = rc.getAccessibleRowSet(criteria, a_table_id, active, resultType);
		System.out.println("Inside TableViewManager.getRowQuery: rowQuery: " + query);
		return query;
	}

	public static String getRowQuery(Connection connection, int a_table_id, int userId, int a_cr_table_id, boolean active, String view, String resultType) //Modified by Lakshman on 20171108 to avoid self joins on BW_CELL for performance gain
	throws SQLException
	{
		return TableViewManager.getRowQuery(connection, a_table_id, userId, a_cr_table_id, active, view, resultType, null);
	}

	public static String getRowQuery(Connection connection, int a_table_id, int userId, int a_cr_table_id, boolean active, String view, String resultType, ArrayList<Integer> resultList) //Modified by Lakshman on 20171108 to avoid self joins on BW_CELL for performance gain
	throws SQLException
	{
		String query = null;
		HashMap criteria = null;
		System.out.println("view.indexOf = " + view.indexOf("?"));
		if (view.indexOf("?") == 0)
		{
			criteria = getCriteriaForDynamicView(view);
		}
		else
		{
			criteria = getCriteriaForUser(connection, userId, a_cr_table_id, a_table_id, view);
		}
		// Now create the query to fetch the data
		query = getRowQuery(connection, criteria, a_table_id, active, resultType, resultList); //Modified by Lakshman on 20171108 to avoid self joins on BW_CELL for performance gain
		System.out.println("rowQuery =>> " + query);
		return query;
	}


	public static HashMap getCriteriaForDynamicView(String view)
	{
		HashMap criteria = new HashMap();
		String[] rows = view.substring(1).trim().split("\\|\\|"); //Added to fix Issue Id: 2705
		for (int i = 0; i < rows.length; i++)
		{
			String[] cols = rows[i].split("&&"); //Added to fix Issue Id: 2705

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

	public static HashMap getColumnAcccess(Connection connection, 
			int tableId, int accTableId, int userId) throws NumberFormatException, SQLException
	{
		HashMap colAccess = new HashMap();
		CallableStatement cstmt = null;
		ResultSet rs = null;
		System.out.println("TableViewManager::getColumnAccess()");
		try {
			cstmt = connection.prepareCall("{CALL BW_GET_ACCESS_FOR_USER(?,?,?)}");
			cstmt.setInt(1,tableId);
			cstmt.setInt(2, accTableId);
			cstmt.setInt(3, userId);
			cstmt.execute();
			rs = cstmt.getResultSet();
			int defAccess = 0;
			if (rs.next()) 
			{
				defAccess = Integer.parseInt(rs.getString(1));
				colAccess.put(new Integer(-1), new Integer(defAccess));
			}
			rs.close();
			cstmt.getMoreResults();
			rs = cstmt.getResultSet();
			while(rs.next())
			{
				int colId = rs.getInt(1);
				String accessString = rs.getString(2);
				int access = defAccess;
				if (!accessString.trim().equals("")) // default access where not specified
				{
					if (accessString.matches("\\d\\?.*")) // conditional access
					{
						colAccess.put(new Integer(colId), accessString);
						System.out.println(colId + ":" + accessString);
					}
					else
					{
						try
						{
							access = Integer.parseInt(accessString); //overide default column access

							colAccess.put(new Integer(colId), new Integer(access));
							System.out.println(colId + ":" + access);
						}
						catch (NumberFormatException nfe)
						{
							System.out.println("Cannot parse access for column id = " + colId + ". Access String = " + accessString + ". Restricting Access to column");
						}
					}
				}
				else
				{
					colAccess.put(new Integer(colId), new Integer(defAccess));
					System.out.println(colId + ":" + access);
				}
			}
		} 
		finally
		{
			try {
				if (rs != null) rs.close();
				if (cstmt != null) cstmt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
		
		return colAccess;		
	}
	
	public static HashSet getRestrictedColumnsForImport(Connection connection, 
			int tableId, int accTableId, int userId)
	{
		HashSet rcList = new HashSet();
		CallableStatement cstmt = null;
		ResultSet rs = null;
		
		try {
			int defaultAccess = 0;
			cstmt = connection.prepareCall("{CALL BW_GET_ACCESS_FOR_USER(?,?,?)}");
			cstmt.setInt(1,tableId);
			cstmt.setInt(2, accTableId);
			cstmt.setInt(3, userId);
			cstmt.execute();
			rs = cstmt.getResultSet();
			if (rs.next()) defaultAccess = Integer.parseInt(rs.getString(1));
			rs.close();
			cstmt.getMoreResults();
			rs = cstmt.getResultSet();
			System.out.println("Default access = " + defaultAccess);
			while(rs.next())
			{
				int colId = rs.getInt(1);
				String accessString = rs.getString(2);
				try
				{
					int access = defaultAccess;
					if (!accessString.trim().equals("")) // default access where not specified
						access = Integer.parseInt(accessString);
					if (access == 0)
					{
						rcList.add(new Integer(colId));
						System.out.println("Added colid=" + colId + " to restricted columnList");
					}
				}
				catch(NumberFormatException e)
				{
					continue;
//					System.out.println("Parsing access string " + accessString);
//					Pattern pattern = Pattern.compile("((.*):(\\d),?)+");
//					Matcher matcher = pattern.matcher(accessString);
//					int numMatches = matcher.groupCount();
//					for (int i = 1; i<numMatches; i = i+2) 
//					{
//						System.out.println("col=" + matcher.group(i) + " access=" + matcher.group(i+1));
//						if (matcher.group(i+1).equals("0"))
//							rcList.add(new Integer(colId));
//					}
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			try {
				if (rs != null) rs.close();
				if (cstmt != null) cstmt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
		
		return rcList;
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
			 //Added to fix Issue Id: 2705
			if( view.contains("'")){
				view = view.replace("'","''");
			}
			
			//System.out.println("view = " + view);
			//System.out.println("baselineId = " + baselineId);
			//System.out.println("mode = " + mode);
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
			int accessTableId = TableViewManager.getAccessTable(connection, tableId, userId);
			HashSet restrColumnList = new HashSet();
			if (accessTableId > 0)
			{
				System.out.println("Using access table id = " + accessTableId);
				// read the access for the user
				restrColumnList = TableViewManager.getRestrictedColumnsForImport(connection, tableId, accessTableId, userId);
			}

			StringBuffer resData = new StringBuffer();

			// Get the columns
			Vector colv  = ColumnManager.getXlColumnsForImport(connection, tableId, userId, memberId);
			
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
				if (restrColumnList != null && restrColumnList.size() > 0)
				{
					if (restrColumnList.contains(new Integer(col.getId())))
					{
						System.out.println("Skip restricted column " + col.getName());
						continue;
					}
				}
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
			/* Condition Added By Asfak - START - 29 Jun 2014 */
			if (criteriaTableId == -1 && !viewIsDynamic)
			{
				tbrowInfo = RowManager.getTableRows(connection, tableId, userId, nhId, baselineId, view, 1, -1, -1);
			}
			else if (criteriaTableId > 0 && viewIsDynamic)
			{
				tbrowInfo = RowManager.getTableRows(connection, tableId, userId, nhId, baselineId, view, 1, -1, -1);
			}
			else
			{
				tbrowInfo = RowManager.getTableRows(connection, tableId, userId, nhId, baselineId, view, 1, -1, -1);
			}
			/* Condition Added By Asfak - END - 29 Jun 2014 */
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
			else if (criteriaTableId > 0 && viewIsDynamic) /* Condition Added By Asfak - START - 29 Jun 2014 */
			{
				System.out.println("CALL BW_GET_FiltredTableCELLS_DYNAMIC ---> " + tableId + "," + userId + "," + memberId + "," + view);
				q = "{CALL BW_GET_FiltredTableCELLS_DYNAMIC(?,?,?,?)}";
				stmt = connection.prepareStatement(q);

				stmt.setInt(1, tableId);
				stmt.setInt(2, userId);
				stmt.setInt(3, memberId);
				stmt.setString(4, view);
				results = stmt.execute();
			}
			else
			{
				System.out.println("CALL BW_GET_FiltredTableCELLS_DYNAMIC ---> " + tableId + "," + userId + "," + memberId + "," + view);
				q = "{CALL BW_GET_FiltredTableCELLS_DYNAMIC(?,?,?,?)}";
				stmt = connection.prepareStatement(q);

				stmt.setInt(1, tableId);
				stmt.setInt(2, userId);
				stmt.setInt(3, memberId);
				stmt.setString(4, view);
				results = stmt.execute();
			}/* Condition Added By Asfak - END - 29 Jun 2014 */
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
							if (restrColumnList != null && restrColumnList.size() > 0 
									&& restrColumnList.contains(new Integer(col.getId())))
							{
								System.out.println("Skipping data for restricted column = " + col.getId());
								for (int r = 0; r < rowv.size(); r++)
								{
									rs.next();
								}
								continue;
							}
							//System.out.println("getting column data for id = " + col.getId());
							int colAccess = col.getAccess();
							for (int r = 0; r < rowv.size(); r++)
							{
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
								
								//g2 121516 start
								String cellFormula = null;
								cellFormula=rs.getString(2);
								int celltid = rs.getInt (3);
								if (maxTransactionId < celltid)
									maxTransactionId = celltid;
								int cellAccess = java.lang.Math.min(raccess, colAccess);

								if (cellFormula == null || mode == 1)
								{
									cellFormula = cellval.trim();
									//g2 121516 end
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
						//System.out.println("Update data displayed here. + updcount " + updcount);
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
			resHeader.append(colv.size() - restrColumnList.size() + Seperator);
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
		System.out.println("Inside getFiltredTableContentsForBrowser" );
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

		if (view == null || view.trim().length() > 0)
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
			lsRowQuery = TableViewManager.getRowQuery(connection, tableId, userId, critTableId, true, view, "TABLE"); //Modified by Lakshman on 20171108 to avoid self joins on BW_CELL for performance gain
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
					//System.out.println("Error:: Vector not created for cells");
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

		if (view == null || view.trim().length() > 0)
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
			lsRowQuery = TableViewManager.getRowQuery(connection, tableId, userId, critTableId, false, view, "TABLE"); //Modified by Lakshman on 20171108 to avoid self joins on BW_CELL for performance gain
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
					//System.out.println("Error:: Vector not created for cells");
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
