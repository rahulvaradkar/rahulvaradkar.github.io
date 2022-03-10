
package com.boardwalk.table;
import java.util.*;
import java.io.*;
import java.lang.*;
import com.boardwalk.database.DatabaseLoader;
import com.boardwalk.exception.NeighborhoodException;
import com.boardwalk.exception.SystemException;
import com.boardwalk.database.*;
import com.boardwalk.excel.*;
import com.boardwalk.query.*;

import java.sql.*;                  // JDBC package
import javax.sql.*;                 // extended JDBC package


/**
 *
 * @author  administrator
 * @version
 */
public class TableHistoryManager {

	public final static String Seperator = new Character((char)1).toString();
	public final static String ContentDelimeter = new Character((char)2).toString();
	public final static String DataBlockDelimeter = new Character((char)3).toString();
	public static long prevTime = -1;

	public static String getCompleteTableWithChanges(Connection connection,
												int tableId,
												String reqColIds,
												int userId,
												int memberId,
												int nhId,
												int asOfTxId,
												String view,
												long difference_in_MiliSec
											) throws SQLException, SystemException
	{
			StringBuffer colIdData		= new StringBuffer();
			StringBuffer colSequence	= new StringBuffer();
			StringBuffer colNames		= new StringBuffer();

			StringBuffer rowIdData		= new StringBuffer();
			StringBuffer rowSeqence		= new StringBuffer();

			StringBuffer sendBuffer		= new StringBuffer();
			StringBuffer resData		= new StringBuffer(10000000);
			StringBuffer fmlData		= new StringBuffer();

			StringBuffer changedData	= new StringBuffer();
			StringBuffer statusData		= new StringBuffer();

			StringBuffer chgStatusResData	= new StringBuffer();
			StringBuffer chgStatusFmlData	= new StringBuffer();

			StringBuffer resHeader = new StringBuffer();

			String dataStr			= "";
			String statusDataStr	= "";

			Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));

			getElapsedTime();

			PreparedStatement stmt = null;
			ResultSet rs = null;

			try
			{
				// Collect the Active column IDs first
				System.out.println("calling...........BW_GET_TBL_BEF_T..........");
				System.out.println("1.tableid " +  tableId);
				System.out.println("2. asofTxid " +  asOfTxId);
				System.out.println("3. userId " +  userId);
				System.out.println("4. memberId " +  memberId);
				System.out.println("5. nhId " +  nhId);
				System.out.println("6. view " + view);

				int criteriaTableId = TableViewManager.getCriteriaTable(connection, tableId, userId);
				System.out.println("Using criteriaTableId = " + criteriaTableId);
				String rowQuery = "";

				if (criteriaTableId > -1)
					rowQuery = TableViewManager.getRowQuery(connection, tableId, userId, criteriaTableId, false, view, "TABLE"); //Modified by Lakshman on 20171108 to avoid self joins on BW_CELL for performance gain
//					System.out.println("rowQuery = " + rowQuery);

				if(criteriaTableId == -1)
				{
					stmt = connection.prepareStatement("{CALL BW_GET_TBL_BEF_T(?,?,?,?,?,?)}");
					stmt.setInt(1, tableId);
					stmt.setInt(2, asOfTxId);
					stmt.setInt(3, userId);
					stmt.setInt(4, memberId);
					stmt.setInt(5, nhId);
					stmt.setString(6, view);
				}
				else
				{
					String lsSql = QueryMaker.getFiltredQueryBefXL(rowQuery);

					stmt = connection.prepareStatement(lsSql);
//					System.out.println ("BW_GET_TBL_BEF_T = " + lsSql);
					/*Modified by Lakshman on 20171107 for fixing the Table history for a Link Export TX_ID: Issue ID = 4060 - START*/
					stmt.setInt(1, asOfTxId);
					
					stmt.setInt(2, tableId);
					stmt.setInt(3, userId);
					stmt.setInt(4, memberId);
					stmt.setInt(5, asOfTxId);

					stmt.setInt(6, tableId);
					stmt.setInt(7, userId);
					stmt.setInt(8, memberId);
					stmt.setInt(9, asOfTxId);

					stmt.setInt(10, asOfTxId);
					
					stmt.setInt(11, tableId);
					stmt.setInt(12, userId);
					stmt.setInt(13, memberId);
					stmt.setInt(14, asOfTxId);

					stmt.setInt(15, tableId);
					stmt.setInt(16, userId);
					stmt.setInt(17, memberId);
					stmt.setInt(18, asOfTxId);
					/*Modified by Lakshman on 20171107 for fixing the Table history for a Link Export TX_ID: Issue ID = 4060 - END*/
				}

				rs = stmt.executeQuery();

				int currColumn = -1;
				boolean  rowProcessed = false;
				/* In fact we can remove the rowid , colids  */
				int colId			= -1;
				int rowId			= -1;
				int ColSeq			= -1;
				float RowSeq		= 0.0f;
				String cellval		= "";
				String cellFormula	= "";
				String ColName		= "";

				while(rs.next())
				{
					colId		= rs.getInt(1);
					rowId		= rs.getInt(2);
					ColSeq		= rs.getInt(3);
					RowSeq		= rs.getFloat(4);
					cellval		= rs.getString(5);
					cellFormula	= rs.getString(6);
					ColName		= rs.getString(7);

					if (cellFormula == null)
						cellFormula = "";

					if (currColumn == colId )
					{
						if (cellFormula == null)
							cellFormula = "";

						resData.append(cellval + Seperator);
						fmlData.append(cellFormula + Seperator);
					}
					else
					{
						colIdData.append(colId + Seperator);
						colNames.append(ColName + Seperator);
						colSequence.append(ColSeq + Seperator);

						if (currColumn != -1)
						{
							rowProcessed = true;
							resData.deleteCharAt(resData.length()-1);
							resData.append(ContentDelimeter);
							fmlData.deleteCharAt(fmlData.length()-1);
							fmlData.append(ContentDelimeter);
						}

						if (cellFormula == null)
							cellFormula = "";

						resData.append(cellval + Seperator);
						fmlData.append(cellFormula + Seperator);

						currColumn = colId;
					}

					if (rowProcessed == false)
					{
						rowIdData.append(rowId + Seperator);
						rowSeqence.append(RowSeq + Seperator);
					}
				}

				//Adding contentDelimeter to Data and Formula data
				if (resData.length() > 0)
					resData.deleteCharAt(resData.length()-1);

				resData.append(ContentDelimeter);

				if (fmlData.length() > 0)
					fmlData.deleteCharAt(fmlData.length()-1);

				fmlData.append(ContentDelimeter);

				// Appending formulae to data String
				resData.append(fmlData.toString());

				//Adding contentDelimeter to Row ids and Row order data
				if (rowIdData.length() > 0)
					rowIdData.deleteCharAt(rowIdData.length()-1);

				rowIdData.append(ContentDelimeter);

				if (rowSeqence.length() > 0)
					rowSeqence.deleteCharAt(rowSeqence.length()-1);

				rowSeqence.append(ContentDelimeter);

				// Appending row Order to Row ids
				rowIdData.append(rowSeqence.toString());

				if (rowIdData.length() > 0)
					rowIdData.deleteCharAt(rowIdData.length()-1);		//rpv

				if (resData.length() > 0)						// rpv
					resData.deleteCharAt(resData.length()-1);

				stmt.close();
				rs.close();
				stmt	= null;
				rs		= null;

				System.out.println("Time to get Bef Changes= " + getElapsedTime());

				System.out.println("calling...........BW_GET_VALUE_CHANGES_FOR_TID..........");
				System.out.println("1.tableid " +  tableId);
				System.out.println("2. asofTxid " +  asOfTxId);
				System.out.println("3. userId " +  userId);
				System.out.println("4. memberId " +  memberId);
				System.out.println("5. nhId " +  nhId);
				System.out.println("6. view " + view);

				if(criteriaTableId == -1)
				{
					stmt = connection.prepareStatement("{CALL BW_GET_VALUE_CHANGES_FOR_TID(?,?,?,?,?,?)}");
					stmt.setInt(1, tableId);
					stmt.setInt(2, asOfTxId);
					stmt.setInt(3, userId);
					stmt.setInt(4, memberId);
					stmt.setInt(5, nhId);
					stmt.setString(6, view);
				}
				else
				{
					String lsSql = QueryMaker.getFiltredQueryValueChangesXL(rowQuery);
					stmt = connection.prepareStatement(lsSql);

					stmt.setInt(1, tableId);
					stmt.setInt(2, userId);
					stmt.setInt(3, memberId);

					stmt.setInt(4, asOfTxId);
					stmt.setInt(5, asOfTxId);

					stmt.setInt(6, tableId);
					stmt.setInt(7, userId);
					stmt.setInt(8, memberId);

					stmt.setInt(9, asOfTxId);
					stmt.setInt(10, asOfTxId);
				}

				rs = stmt.executeQuery();

				int CurrCol = -1 ;

				rowId							= -1;
				colId							= -1;
				String cellValue				= "";
				java.sql.Timestamp CreatedOn	= null;
				Long CreatedOnLng					= null;
				String	Comment_				= "";
				String	CreatedBy				= "";
				String Formula					= "";
				ColName							= "";
				ColSeq							= -1;
				RowSeq							= 0.0f;

				while(rs.next())
				{
					rowId			= rs.getInt(1);
					colId			= rs.getInt(2);
					cellValue		= rs.getString(3);
					CreatedOn		= (rs.getTimestamp(5, cal));
					//CreatedOn		= (rs.getTime(5, cal)) + difference_in_MiliSec;					
					Comment_		= rs.getString(6);
					CreatedBy		= rs.getString(7);
					Formula			= rs.getString(8);
					ColName			= rs.getString(9);
					ColSeq			= rs.getInt(10);
					RowSeq			= rs.getFloat(11);

					if (Formula == null)
						Formula = "";

					if(CurrCol != colId)
						CurrCol = colId;
					CreatedOnLng =CreatedOn.getTime() + difference_in_MiliSec;
					changedData.append(rowId + Seperator);
					changedData.append(colId + Seperator);
					changedData.append(cellValue + Seperator);
					//changedData.append(CreatedOn + Seperator);
					changedData.append(CreatedOnLng + Seperator);
					changedData.append(Comment_ + Seperator);
					changedData.append(CreatedBy + Seperator);
					changedData.append(Formula + Seperator);
					changedData.append(ColSeq + Seperator);
					changedData.append(RowSeq + Seperator);
					changedData.append(ColName + ContentDelimeter);

				}

				if (changedData.length() > 0)
					changedData.deleteCharAt(changedData.length()-1);

				stmt.close();
				rs.close();
				stmt	= null;
				rs		= null;

				System.out.println("Time to get Value Changes= " + getElapsedTime());

				System.out.println("calling...........BW_GET_STATUS_CHANGES_FOR_TID..........");
				System.out.println("1.tableid " +  tableId);
				System.out.println("2. asofTxid " +  asOfTxId);
				System.out.println("3. userId " +  userId);
				System.out.println("4. memberId " +  memberId);
				System.out.println("5. nhId " +  nhId);
				System.out.println("6. view " + view);

				if(criteriaTableId == -1)
				{
					stmt = connection.prepareStatement("{CALL BW_GET_STATUS_CHANGES_FOR_TID(?,?,?,?,?,?)}");
					stmt.setInt(1, tableId);
					stmt.setInt(2, asOfTxId);
					stmt.setInt(3, userId);
					stmt.setInt(4, memberId);
					stmt.setInt(5, nhId);
					stmt.setString(6, view);
				}
				else
				{
					String lsSql = QueryMaker.getFiltredQueryStatusChangesXL(rowQuery);
					stmt = connection.prepareStatement(lsSql);
					//System.out.println(" << lsSql >>"+lsSql);

					stmt.setInt(1, tableId);
					stmt.setInt(2, userId);
					stmt.setInt(3, memberId);
					stmt.setInt(4, asOfTxId);

					stmt.setInt(5, tableId);
					stmt.setInt(6, userId);
					stmt.setInt(7, memberId);
					stmt.setInt(8, asOfTxId);
				}

				rs = stmt.executeQuery();

				CurrCol			= -1;
				rowProcessed	= false;

				statusData.append("");

				rowId			= -1;
				colId			= -1;
				int activeFlag	= -1;
				CreatedOn		= null;
				Comment_		= "";
				CreatedBy		= "";
				int colSeq		= -1;
				float rowSeq	= 0.0f;

				while(rs.next())
				{
					rowId			= rs.getInt(1);
					colId			= rs.getInt(2);
					activeFlag		= rs.getInt(3);
					CreatedOn		= rs.getTimestamp(5, cal);
					//CreatedOn		= rs.getTimestamp(5, cal).getTime;
					Comment_		= rs.getString(6);
					CreatedBy		= rs.getString(7);
					colSeq			= rs.getInt(8);
					rowSeq			= rs.getFloat(9);

					statusData.append(rowId + Seperator);
					statusData.append(colId + Seperator);
					statusData.append(activeFlag + Seperator);
					statusData.append(CreatedOn + Seperator);
					statusData.append(Comment_ + Seperator);
					statusData.append(CreatedBy + Seperator);
					statusData.append(colSeq + Seperator);
					statusData.append(rowSeq + ContentDelimeter);
				}

				if (statusData.length() > 0)
					statusData.deleteCharAt(statusData.length()-1);

				stmt.close();
				rs.close();
				stmt = null;
				rs = null;
			}
			catch (Exception e)
			{
				resHeader.append("Failure" + DataBlockDelimeter);
				e.printStackTrace();
				return null;
			}
			finally
			{
				if (stmt != null)
				{
					stmt.close();
					stmt = null;
				}
				if (rs != null)
				{
					rs.close();
					rs = null;
				}
			}

			System.out.println("Time to get Status Changes= " + getElapsedTime());

			if(colIdData.length() > 0)
			{
				resHeader.append("Success" + DataBlockDelimeter);
				colIdData = colIdData.deleteCharAt(colIdData.length()-1);
			}

			if(colNames.length() > 0)
				colNames = colNames.deleteCharAt(colNames.length()-1);

			if(colSequence.length() > 0)
				colSequence = colSequence.deleteCharAt(colSequence.length()-1);

			resHeader.append(colIdData.toString() +  ContentDelimeter + colNames.toString()  + ContentDelimeter + colSequence.toString()  + ContentDelimeter);

			return resHeader.toString() +  rowIdData.toString()   + DataBlockDelimeter  + resData.toString() + DataBlockDelimeter + changedData.toString() + DataBlockDelimeter + statusData.toString() + DataBlockDelimeter ;
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

	public static String compareTwoTransactions(Connection connection,
												int tableId,
												String reqColIds,
												int userId,
												int memberId,
												int nhId,
												int asOfTxId,
												int compTid,
												String view,
												long difference_in_MiliSec
											) throws SQLException, SystemException
	{
			getElapsedTime();

			StringBuffer colIdData		= new StringBuffer();
			StringBuffer colSequence	= new StringBuffer();
			StringBuffer colNames		= new StringBuffer();

			StringBuffer rowIdData		= new StringBuffer();
			StringBuffer rowSeqence		= new StringBuffer();

			StringBuffer sendBuffer		= new StringBuffer();
			StringBuffer resData		= new StringBuffer(10000000);
			StringBuffer fmlData		= new StringBuffer();

			StringBuffer changedData	= new StringBuffer();
			StringBuffer statusData		= new StringBuffer();

			StringBuffer chgStatusResData	= new StringBuffer();
			StringBuffer chgStatusFmlData	= new StringBuffer();

			// write the header to the response
			StringBuffer resHeader = new StringBuffer();

			String dataStr			= "" ;
			String statusDataStr	= "";

			Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));

			PreparedStatement stmt = null;
			ResultSet rs = null;

			int CurrCol		= -1 ;
			String rowQuery = "" ;

			try
			{
				int criteriaTableId = TableViewManager.getCriteriaTable(connection, tableId, userId);
				if (criteriaTableId > -1)
					rowQuery = TableViewManager.getRowQuery(connection, tableId, userId, criteriaTableId, false, view, "TABLE"); //Modified by Lakshman on 20171108 to avoid self joins on BW_CELL for performance gain
//					System.out.println("rowQuery = " + rowQuery);

				String lsSql = "";

				System.out.println("calling...........BW_GET_TBL_AT_T..........");
				System.out.println("1.tableid " +  tableId);
				System.out.println("2. compTid " +  compTid);
				System.out.println("3. userId " +  userId);
				System.out.println("4. memberId " +  memberId);
				System.out.println("5. nhId " +  nhId);
				System.out.println("6. view " + view);

				if(criteriaTableId == -1)
				{
					stmt = connection.prepareStatement("{CALL BW_GET_TBL_AT_T(?,?,?,?,?,?)}");
					stmt.setInt(1, tableId);
					stmt.setInt(2, compTid);
					stmt.setInt(3, userId);
					stmt.setInt(4, memberId);
					stmt.setInt(5, nhId);
					stmt.setString(6, view);
				}
				else
				{
					lsSql	= QueryMaker.getFiltredQueryTBLCmpXL(rowQuery);
					stmt	= connection.prepareStatement(lsSql);
//					System.out.println(" << lsSql >>"+lsSql);

					stmt.setInt(1, tableId);
					stmt.setInt(2, userId);
					stmt.setInt(3, memberId);
					stmt.setInt(4, compTid);

					stmt.setInt(5, tableId);
					stmt.setInt(6, userId);
					stmt.setInt(7, memberId);
					stmt.setInt(8, compTid);
				}

				rs = stmt.executeQuery();

				int currColumn = -1;
				boolean  rowProcessed = false;

				int colId			= -1;
				int rowId			= -1;
				int ColSeq			= -1;
				Float RowSeq		= 0.0f;
				String cellval		= "";
				String cellFormula	= "";
				String ColName		= "";

				while(rs.next())
				{
					colId		= rs.getInt(1);
					rowId		= rs.getInt(2);
					ColSeq		= rs.getInt(3);
					RowSeq		= rs.getFloat(4);
					cellval		= rs.getString(5);
					cellFormula	= rs.getString(6);
					ColName		= rs.getString(7);

					if (cellFormula == null)
						cellFormula = "";

					if (currColumn == colId )
					{
						if (cellFormula == null)
							cellFormula = "";

						resData.append(cellval + Seperator);
						fmlData.append(cellFormula + Seperator);
					}
					else
					{
						colIdData.append(colId + Seperator);
						colNames.append(ColName + Seperator);
						colSequence.append(ColSeq + Seperator);

						if (currColumn != -1)
						{
							rowProcessed = true;
							resData.deleteCharAt(resData.length()-1);
							resData.append(ContentDelimeter);
							fmlData.deleteCharAt(fmlData.length()-1);
							fmlData.append(ContentDelimeter);
						}

						if (cellFormula == null)
							cellFormula = "";

						resData.append(cellval + Seperator);
						fmlData.append(cellFormula + Seperator);

						currColumn = colId;
					}

					if (rowProcessed == false)
					{
						rowIdData.append(rowId + Seperator);
						rowSeqence.append(RowSeq + Seperator);
					}
				}

				System.out.println("Time to get table at T = " + getElapsedTime());

				//Adding contentDelimeter to Data and Formula data
				if (resData.length() > 0)
					resData.deleteCharAt(resData.length()-1);

				resData.append(ContentDelimeter);

				if (fmlData.length() > 0)
					fmlData.deleteCharAt(fmlData.length()-1);

				fmlData.append(ContentDelimeter);

				// Appending formulae to data String
				resData.append(fmlData.toString());

				if (resData.length() > 0)						// rpv
					resData.deleteCharAt(resData.length()-1);

				//Adding contentDelimeter to Row ids and Row order data
				if (rowIdData.length() > 0)
					rowIdData.deleteCharAt(rowIdData.length()-1);

				rowIdData.append(ContentDelimeter);

				if (rowSeqence.length() > 0)
					rowSeqence.deleteCharAt(rowSeqence.length()-1);

				rowSeqence.append(ContentDelimeter);

				// Appending row Order to Row ids
				rowIdData.append(rowSeqence.toString());

				if (rowIdData.length() > 0)
					rowIdData.deleteCharAt(rowIdData.length()-1);		//rpv

				stmt.close();
				rs.close();
				stmt	= null;
				rs		= null;

				///////////////////////////////// End of ...........BW_GET_TBL_AT_T..........

				System.out.println("calling...........BW_GET_VALUE_CHANGES_BETN_TID..........");
				System.out.println("1.tableid " +  tableId);
				System.out.println("2. compTid " +  compTid);
				System.out.println("3. asofTxid " +  asOfTxId);
				System.out.println("4. userId " +  userId);
				System.out.println("5. memberId " +  memberId);
				System.out.println("6. nhId " +  nhId);
				System.out.println("7. view " + view);

				if(criteriaTableId == -1)
				{
					stmt = connection.prepareStatement("{CALL BW_GET_VALUE_CHANGES_BETN_TID(?,?,?,?,?,?,?)}");
					stmt.setInt(1, tableId);
					stmt.setInt(2, compTid);
					stmt.setInt(3, asOfTxId);
					stmt.setInt(4, userId);
					stmt.setInt(5, memberId);
					stmt.setInt(6, nhId);
					stmt.setString(7, view);
				}
				else
				{
					lsSql	= QueryMaker.getFiltredQueryValueChangesCmpXL(rowQuery);
					stmt	= connection.prepareStatement(lsSql);
					//System.out.println(" << lsSql >>"+lsSql);

					stmt.setInt(1, tableId);
					stmt.setInt(2, userId);
					stmt.setInt(3, memberId);

					stmt.setInt(4, compTid);
					stmt.setInt(5, asOfTxId);

					stmt.setInt(6, tableId);
					stmt.setInt(7, userId);
					stmt.setInt(8, memberId);

					stmt.setInt(9, compTid);
					stmt.setInt(10, asOfTxId);
				}

				rs = stmt.executeQuery();
				currColumn = -1;
				//Loop through the available result sets.
				rowId		= -1;
				colId		= -1;
				String cellValue	= "";
				java.sql.Timestamp CreatedOn	= null;
				String Comment_	= "";
				String CreatedBy = "";
				String Formula	= "";
				ColSeq		= -1;
				RowSeq		= 0.0f;
				ColName		= "";

				while(rs.next())
				{
					rowId				= rs.getInt(1);
					colId				= rs.getInt(2);
					cellValue			= rs.getString(3);
					CreatedOn			= rs.getTimestamp(5, cal);
					Long CreatedOnLng					= null;
					Comment_			= rs.getString(6);
					CreatedBy			= rs.getString(7);
					Formula				= rs.getString(8);
					ColSeq				= rs.getInt(9);
					RowSeq				= rs.getFloat(10);
					ColName				= rs.getString(11);

					if (Formula == null)
						Formula = "";

					if(currColumn != colId)
						CurrCol = colId;
					CreatedOnLng =CreatedOn.getTime() + difference_in_MiliSec;
					changedData.append(rowId + Seperator);
					changedData.append(colId + Seperator);
					changedData.append(cellValue + Seperator);
					//changedData.append(CreatedOn + Seperator);
					changedData.append(CreatedOnLng + Seperator);
					changedData.append(Comment_ + Seperator);
					changedData.append(CreatedBy + Seperator);
					changedData.append(Formula + Seperator);
					changedData.append(ColSeq + Seperator);
					changedData.append(RowSeq + Seperator);
					changedData.append(ColName + ContentDelimeter);

				}

				System.out.println("Time to get table value changes at T = " + getElapsedTime());
				//System.out.println("Changed Data " + changedData.toString());
				if (changedData.length() > 0)
					changedData.deleteCharAt(changedData.length()-1);

				///////////////////////////// Start of  ............BW_GET_STATUS_CHANGES_BETN_TID///////////////////////////////////////////////////////
				stmt.close();
				rs.close();
				stmt = null;
				rs = null;

				System.out.println("calling...........BW_GET_STATUS_CHANGES_BETN_TID..........");
				System.out.println("1.tableid " +  tableId);
				System.out.println("2. compTid " +  compTid);
				System.out.println("3. asofTxid " +  asOfTxId);
				System.out.println("4. userId " +  userId);
				System.out.println("5. memberId " +  memberId);
				System.out.println("6. nhId " +  nhId);
				System.out.println("7. view " + view);

				if(criteriaTableId == -1)
				{
					stmt = connection.prepareStatement("{CALL BW_GET_STATUS_CHANGES_BETN_TID(?,?,?,?,?,?,?)}");
					stmt.setInt(1, tableId);
					stmt.setInt(2, compTid);
					stmt.setInt(3, asOfTxId);
					stmt.setInt(4, userId);
					stmt.setInt(5, memberId);
					stmt.setInt(6, nhId);
					stmt.setString(7, view);
				}
				else
				{
					lsSql	= QueryMaker.getFiltredQueryStatusChangesCmpXL(rowQuery);
					stmt	= connection.prepareStatement(lsSql);
//					System.out.println(" << lsSql >>"+lsSql);

					stmt.setInt(1, tableId);
					stmt.setInt(2, userId);
					stmt.setInt(3, memberId);
					stmt.setInt(4, compTid);
					stmt.setInt(5, asOfTxId);
				}

				rs = stmt.executeQuery();

				currColumn		= -1;
				rowProcessed	= false;

//				System.out.println(".......Printing  status data ....");

				statusData.append("");

				rowId				= -1;
				colId				= -1;
				int activeFlag		= -1;
				CreatedOn			= null;
				Comment_			= "";
				CreatedBy			= "";
				ColSeq				= -1;
				RowSeq				= 0.0f;

				while(rs.next())
				{
					rowId			= rs.getInt(1);
					colId			= rs.getInt(2);
					activeFlag		= rs.getInt(3);
					CreatedOn		= rs.getTimestamp(5, cal);
					Comment_		= rs.getString(6);
					CreatedBy		= rs.getString(7);
					ColSeq			= rs.getInt(8);
					RowSeq			= rs.getFloat(9);

					statusData.append(rowId + Seperator);
					statusData.append(colId + Seperator);
					statusData.append(activeFlag + Seperator);
					statusData.append(CreatedOn + Seperator);
					statusData.append(Comment_ + Seperator);
					statusData.append(CreatedBy + Seperator);
					statusData.append(ColSeq + Seperator);
					statusData.append(RowSeq + ContentDelimeter);
				}

				if (statusData.length() > 0)
					statusData.deleteCharAt(statusData.length()-1);

				stmt.close();
				rs.close();
				stmt = null;
				rs = null;
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return null;
			}
			finally
			{
				if (stmt != null)
				{
					stmt.close();
					stmt = null;
				}
				if (rs != null)
				{
					rs.close();
					rs = null;
				}
			}

			System.out.println("Time to get Status Changes= " + getElapsedTime());


			if(colIdData.length() > 0)
			{
				resHeader.append("Success" + DataBlockDelimeter);
				colIdData = colIdData.deleteCharAt(colIdData.length()-1);
			}

			if(colNames.length() > 0)
				colNames = colNames.deleteCharAt(colNames.length()-1);

			if(colSequence.length() > 0)
				colSequence = colSequence.deleteCharAt(colSequence.length()-1);

			System.out.println("Time to get prepare headers= " + getElapsedTime());

			resHeader.append(colIdData.toString() +  ContentDelimeter + colNames.toString() + ContentDelimeter + colSequence.toString()  + ContentDelimeter);

//			System.out.println(" resHeader.toString() "+resHeader.toString());

			return resHeader.toString() +  rowIdData.toString()   + DataBlockDelimeter  + resData.toString() + DataBlockDelimeter + changedData.toString() + DataBlockDelimeter + statusData.toString() + DataBlockDelimeter ;
	}

	public static String getChangesAfterImport(Connection connection,
												int tableId,
												String reqColIds,
												int userId,
												int memberId,
												int nhId,
												int compTid,
												String view
											) throws SQLException, SystemException
	{

			System.out.println("...................inside TableHistoryManager.getChangesAfterImport ");
			getElapsedTime();

			StringBuffer sendBuffer		= new StringBuffer();
			StringBuffer resData		= new StringBuffer(10000000);
			StringBuffer fmlData		= new StringBuffer();

			StringBuffer rowIdData		= new StringBuffer();
			StringBuffer rowSeqence		= new StringBuffer();

			StringBuffer changedData	= new StringBuffer();
			StringBuffer statusData		= new StringBuffer();

			// write the header to the response
			StringBuffer resHeader = new StringBuffer();

			String dataStr			= "" ;
			String statusDataStr	= "";
			int dataLength ;

			StringBuffer colIdData		= new StringBuffer();
			StringBuffer colSequence	= new StringBuffer();
			StringBuffer colNames		= new StringBuffer();

			StringBuffer chgStatusResData	= new StringBuffer();
			StringBuffer chgStatusFmlData	= new StringBuffer();

			Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));

			// Get the columns
			// Get all the table columns

			PreparedStatement stmt = null;
			ResultSet rs = null;

			int CurrCol		= -1 ;
			String rowQuery = "" ;

			try
			{
				int criteriaTableId = TableViewManager.getCriteriaTable(connection, tableId, userId);
				if (criteriaTableId > -1)
					rowQuery = TableViewManager.getRowQuery(connection, tableId, userId, criteriaTableId, false, view, "TABLE"); //Modified by Lakshman on 20171108 to avoid self joins on BW_CELL for performance gain
//					System.out.println("rowQuery = " + rowQuery);

				String lsSql = "";

				System.out.println("calling...........BW_GET_TBL_AT_T..........");
				System.out.println("1.tableid " +  tableId);
				System.out.println("2. compTid " +  compTid);
				System.out.println("3. userId " +  userId);
				System.out.println("4. memberId " +  memberId);
				System.out.println("5. nhId " +  nhId);
				System.out.println("6. view " + view);

				if(criteriaTableId == -1)
				{
					stmt = connection.prepareStatement("{CALL BW_GET_TBL_AT_T(?,?,?,?,?,?)}");
					stmt.setInt(1, tableId);
					stmt.setInt(2, compTid);
					stmt.setInt(3, userId);
					stmt.setInt(4, memberId);
					stmt.setInt(5, nhId);
					stmt.setString(6, view);
				}
				else
				{
					lsSql	= QueryMaker.getFiltredQueryTBLCmpXL(rowQuery);
					stmt	= connection.prepareStatement(lsSql);
//					System.out.println(" << lsSql >>"+lsSql);

					stmt.setInt(1, tableId);
					stmt.setInt(2, userId);
					stmt.setInt(3, memberId);
					stmt.setInt(4, compTid);

					stmt.setInt(5, tableId);
					stmt.setInt(6, userId);
					stmt.setInt(7, memberId);
					stmt.setInt(8, compTid);
				}

				rs = stmt.executeQuery();

				int currColumn = -1;
				boolean  rowProcessed = false;

				int colId			= -1;
				int rowId			= -1;
				int ColSeq			= -1;
				Float RowSeq		= 0.0f;
				String cellval		= "";
				String cellFormula	= "";
				String ColName		= "";

				while(rs.next())
				{
					colId		= rs.getInt(1);
					rowId		= rs.getInt(2);
					ColSeq		= rs.getInt(3);
					RowSeq		= rs.getFloat(4);
					cellval		= rs.getString(5);
					cellFormula	= rs.getString(6);
					ColName		= rs.getString(7);

					if (cellFormula == null)
						cellFormula = "";

					if (currColumn == colId )
					{
						if (cellFormula == null)
							cellFormula = "";

						resData.append(cellval + Seperator);
						fmlData.append(cellFormula + Seperator);
					}
					else
					{
						colIdData.append(colId + Seperator);
						colNames.append(ColName + Seperator);
						colSequence.append(ColSeq + Seperator);

						if (currColumn != -1)
						{
							rowProcessed = true;
							resData.deleteCharAt(resData.length()-1);
							resData.append(ContentDelimeter);
							fmlData.deleteCharAt(fmlData.length()-1);
							fmlData.append(ContentDelimeter);
						}

						if (cellFormula == null)
							cellFormula = "";

						resData.append(cellval + Seperator);
						fmlData.append(cellFormula + Seperator);

						currColumn = colId;
					}

					if (rowProcessed == false)
					{
						rowIdData.append(rowId + Seperator);
						rowSeqence.append(RowSeq + Seperator);
					}
				}

				System.out.println("Time to get table at T = " + getElapsedTime());

				//Adding contentDelimeter to Data and Formula data
				if (resData.length() > 0)
					resData.deleteCharAt(resData.length()-1);

				resData.append(ContentDelimeter);

				if (fmlData.length() > 0)
					fmlData.deleteCharAt(fmlData.length()-1);

				fmlData.append(ContentDelimeter);

				// Appending formulae to data String
				resData.append(fmlData.toString());

				if (resData.length() > 0)						// rpv
					resData.deleteCharAt(resData.length()-1);

				//Adding contentDelimeter to Row ids and Row order data
				if (rowIdData.length() > 0)
					rowIdData.deleteCharAt(rowIdData.length()-1);

				rowIdData.append(ContentDelimeter);

				if (rowSeqence.length() > 0)
					rowSeqence.deleteCharAt(rowSeqence.length()-1);

				rowSeqence.append(ContentDelimeter);

				// Appending row Order to Row ids
				rowIdData.append(rowSeqence.toString());

				if (rowIdData.length() > 0)
					rowIdData.deleteCharAt(rowIdData.length()-1);		//rpv

				stmt.close();
				rs.close();
				stmt	= null;
				rs		= null;

				///////////////////////////////// End of ...........BW_GET_TBL_AT_T..........

				System.out.println("calling...........BW_GET_VALUE_CHANGES_AFTER_IMPORT..........");
				System.out.println("1.tableid " +  tableId);
				System.out.println("2. compTid " +  compTid);
				System.out.println("3. userId " +  userId);
				System.out.println("4. memberId " +  memberId);
				System.out.println("5. nhId " +  nhId);
				System.out.println("6. view " + view);

				if(criteriaTableId == -1)
				{
					stmt = connection.prepareStatement("{CALL BW_GET_VALUE_CHANGES_AFTER_IMPORT(?,?,?,?,?,?)}");
					stmt.setInt(1, tableId);
					stmt.setInt(2, compTid);
					stmt.setInt(3, userId);
					stmt.setInt(4, memberId);
					stmt.setInt(5, nhId);
					stmt.setString(6, view);
				}
				else
				{
					lsSql	= QueryMaker.getFiltredQueryValueChangesAfterImport(rowQuery);
					stmt	= connection.prepareStatement(lsSql);
					//System.out.println(" << lsSql >>"+lsSql);

					stmt.setInt(1, tableId);
					stmt.setInt(2, userId);
					stmt.setInt(3, memberId);
					stmt.setInt(4, compTid);

					stmt.setInt(5, tableId);
					stmt.setInt(6, userId);
					stmt.setInt(7, memberId);
					stmt.setInt(8, compTid);
				}

				rs = stmt.executeQuery();
				currColumn = -1;
				//Loop through the available result sets.
				rowId		= -1;
				colId		= -1;
				String cellValue	= "";
				java.sql.Timestamp CreatedOn	= null;
				String Comment_	= "";
				String CreatedBy = "";
				String Formula	= "";
				ColSeq		= -1;
				RowSeq		= 0.0f;
				ColName		= "";

				while(rs.next())
				{
					rowId				= rs.getInt(1);
					colId				= rs.getInt(2);
					cellValue			= rs.getString(3);
					CreatedOn			= rs.getTimestamp(5, cal);
					Comment_			= rs.getString(6);
					CreatedBy			= rs.getString(7);
					Formula				= rs.getString(8);
					ColSeq				= rs.getInt(9);
					RowSeq				= rs.getFloat(10);
					ColName				= rs.getString(11);

					if (Formula == null)
						Formula = "";

					if(currColumn != colId)
						CurrCol = colId;

					changedData.append(rowId + Seperator);
					changedData.append(colId + Seperator);
					changedData.append(cellValue + Seperator);
					changedData.append(CreatedOn + Seperator);
					changedData.append(Comment_ + Seperator);
					changedData.append(CreatedBy + Seperator);
					changedData.append(Formula + Seperator);
					changedData.append(ColSeq + Seperator);
					changedData.append(RowSeq + Seperator);
					changedData.append(ColName + ContentDelimeter);

				}

				System.out.println("Time to get table value changes at T = " + getElapsedTime());
				//System.out.println("Changed Data " + changedData.toString());
				if (changedData.length() > 0)
					changedData.deleteCharAt(changedData.length()-1);

				///////////////////////////// Start of  ............BW_GET_STATUS_CHANGES_BETN_TID///////////////////////////////////////////////////////
				stmt.close();
				rs.close();
				stmt = null;
				rs = null;

				System.out.println("calling...........BW_GET_STATUS_CHANGES_AFTER_IMPORT..........");
				System.out.println("1.tableid " +  tableId);
				System.out.println("2. compTid " +  compTid);
				System.out.println("3. userId " +  userId);
				System.out.println("4. memberId " +  memberId);
				System.out.println("5. nhId " +  nhId);
				System.out.println("6. view " + view);

				if(criteriaTableId == -1)
				{
					stmt = connection.prepareStatement("{CALL BW_GET_STATUS_CHANGES_AFTER_IMPORT(?,?,?,?,?,?)}");
					stmt.setInt(1, tableId);
					stmt.setInt(2, compTid);
					stmt.setInt(3, userId);
					stmt.setInt(4, memberId);
					stmt.setInt(5, nhId);
					stmt.setString(6, view);
				}
				else
				{
					lsSql	= QueryMaker.getFiltredQueryStatusChangesAfterImport(rowQuery);
					stmt	= connection.prepareStatement(lsSql);
//					System.out.println(" << lsSql >>"+lsSql);

					stmt.setInt(1, tableId);
					stmt.setInt(2, userId);
					stmt.setInt(3, memberId);
					stmt.setInt(4, compTid);
				}

				rs = stmt.executeQuery();

				currColumn		= -1;
				rowProcessed	= false;

				System.out.println(".......Printing  status data ....");

				statusData.append("");

				rowId				= -1;
				colId				= -1;
				int activeFlag		= -1;
				CreatedOn			= null;
				Comment_			= "";
				CreatedBy			= "";
				ColSeq				= -1;
				RowSeq				= 0.0f;

				while(rs.next())
				{
					rowId			= rs.getInt(1);
					colId			= rs.getInt(2);
					activeFlag		= rs.getInt(3);
					CreatedOn		= rs.getTimestamp(5, cal);
					Comment_		= rs.getString(6);
					CreatedBy		= rs.getString(7);
					ColSeq			= rs.getInt(8);
					RowSeq			= rs.getFloat(9);

					statusData.append(rowId + Seperator);
					statusData.append(colId + Seperator);
					statusData.append(activeFlag + Seperator);
					statusData.append(CreatedOn + Seperator);
					statusData.append(Comment_ + Seperator);
					statusData.append(CreatedBy + Seperator);
					statusData.append(ColSeq + Seperator);
					statusData.append(RowSeq + ContentDelimeter);
				}

				if (statusData.length() > 0)
						statusData.deleteCharAt(statusData.length()-1);

				stmt.close();
				rs.close();
				stmt = null;
				rs = null;
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return null;
			}
			finally
			{
				if (stmt != null)
				{
					stmt.close();
					stmt = null;
				}
				if (rs != null)
				{
					rs.close();
					rs = null;
				}
			}

			System.out.println("Time to get Status Changes= " + getElapsedTime());

			if(colIdData.length() > 0)
			{
				resHeader.append("Success" + DataBlockDelimeter);
				colIdData = colIdData.deleteCharAt(colIdData.length()-1);
			}

			if(colNames.length() > 0)
				colNames = colNames.deleteCharAt(colNames.length()-1);

			if(colSequence.length() > 0)
				colSequence = colSequence.deleteCharAt(colSequence.length()-1);


			System.out.println("Time to get prepare headers= " + getElapsedTime());

			resHeader.append(colIdData.toString() +  ContentDelimeter + colNames.toString() + ContentDelimeter + colSequence.toString()  + ContentDelimeter);

//			System.out.println(" resHeader.toString() "+resHeader.toString());

			return resHeader.toString() +  rowIdData.toString()   + DataBlockDelimeter  + resData.toString() + DataBlockDelimeter + changedData.toString() + DataBlockDelimeter + statusData.toString() + DataBlockDelimeter ;
	}
}

