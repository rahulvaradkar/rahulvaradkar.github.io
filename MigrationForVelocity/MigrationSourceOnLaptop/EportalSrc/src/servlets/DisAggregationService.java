package servlets;
/*
 *  This presents a list of collaboration available to a user
 */
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import com.boardwalk.exception.*;
import com.boardwalk.database.*;
import java.sql.*;                  // JDBC package
import javax.sql.*;                 // extended JDBC packa
import boardwalk.common.*;
import com.boardwalk.table.*;


public class DisAggregationService extends xlService implements SingleThreadModel
{
	public void service(HttpServletRequest request,
		HttpServletResponse response)throws ServletException, IOException
	{

		Connection connection = null;
		String responseBuffer = "FAILURE";

		response.setContentType("text/plain");
		ServletOutputStream servletOut = response.getOutputStream();
		try
		{
			System.out.println("Inside DisAggregationService");
			// parse the request

			// read the request
			//int aggrTblId = Integer.parseInt(request.getParameter("aggrTblId"));
			//System.out.println("Using aggregation definition in bw table id = " + aggrTblId);

			// Start a connection
			DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
			connection = databaseloader.getConnection();



			disAggregate(connection);

			responseBuffer = "SUCCESS";

		}
		catch (Exception e)
		{
			e.printStackTrace();

			responseBuffer = e.toString();
		}
		finally
		{
			try
			{
				if (connection != null)
					connection.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			response.setContentLength(responseBuffer.length());
			servletOut.print(responseBuffer);
			servletOut.close();

		}
	}
	public void disAggregate(Connection connection)
		throws SystemException
	{
		TransactionManager tm = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		String query = null;

		try
		{
			getElapsedTime();
			HashMap aggrDefs = new HashMap();
			Vector aggrRows = new Vector();
			// get the aggregation definition
			query = "SELECT AGGR.BW_ROW_ID, AGGR.STRING_VALUE, AGGRCOL.NAME " +
					"FROM   BW_CELL AS AGGR, BW_COLUMN AGGRCOL, BW_TBL AS AGGRTL, BW_ROW " +
					"WHERE " +
					"	AGGR.BW_COLUMN_ID = AGGRCOL.ID " +
					"AND AGGRCOL.BW_TBL_ID = AGGRTL.ID " +
					"AND AGGRTL.NAME = '__BW_DISAGGR_TBL' " +
					"AND AGGR.BW_ROW_ID = BW_ROW.ID " +
					"AND AGGR.ACTIVE = 1 " +
					"ORDER BY BW_ROW.SEQUENCE_NUMBER ";

			ps = connection.prepareStatement(query);
			//ps.setInt(1, aggrTableId);
			rs = ps.executeQuery();
			while (rs.next())
			{
				int rowId = rs.getInt(1);
				String val = rs.getString(2);
				String col = rs.getString(3);

				DisAggregationDefinition aDef = (DisAggregationDefinition)aggrDefs.get(new Integer(rowId));
				if (aDef == null)
				{
					aDef = new DisAggregationDefinition();
					aggrDefs.put(new Integer(rowId), aDef);
					aggrRows.addElement(new Integer(rowId));
				}
				
				if (col.equalsIgnoreCase("SourceTable"))
				{
					aDef.sourceTable = val.trim();
				}
				else if (col.equalsIgnoreCase("SourceFilter"))
				{
					aDef.sourceFilter = val.trim();
				}
				else if (col.equalsIgnoreCase("SourceSpecifiedKeyColumns"))
				{
					aDef.sourceSpecifiedKeyColumns = val.trim().split(",");
				}
				else if (col.equalsIgnoreCase("SourceOtherKeyColumns"))
				{
					if (!val.trim().equals(""))
					{
						aDef.sourceOtherKeyColumns = val.trim().split(",");
					}
				}
				else if (col.equalsIgnoreCase("TargetTable"))
				{
					aDef.targetTable = val.trim();
				}
				else if (col.equalsIgnoreCase("TargetFilter"))
				{
					aDef.targetFilter = val.trim();
				}
				else if (col.equalsIgnoreCase("TargetKeyColumns"))
				{
					aDef.targetKeyColumns = val.trim().split(",");
				}
				else if (col.equalsIgnoreCase("DistributionTable"))
				{
					aDef.distributionTable = val.trim();
				}
				else if (col.equalsIgnoreCase("DistributionFilter"))
				{
					aDef.distributionFilter = val.trim();
				}
				else if (col.equalsIgnoreCase("TargetColumns"))
				{
					aDef.targetColumns = val.trim().split(",");
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
			System.out.println("Time(sec) to get dis-aggregation definitions = " + getElapsedTime());
			// loop over the aggregation list 
			//Vector aDefs = new Vector(aggrDefs.values());
			Iterator ai = aggrRows.iterator();
			while (ai.hasNext())
			{
				// Start a transaction
				tm = new TransactionManager(connection, 1);
				int tid = tm.startTransaction("DisAggregation", "DisAggregation");

				int sourceTableId = -1;
				int sourceFilterId = -1;
				int targetTableId = -1;
				int targetFilterId = -1;
				int distributionTableId = -1;
				int distributionFilterId = -1;
				Integer rowIdInt = (Integer)ai.next();
				System.out.println("Processing aggr row id = " + rowIdInt);
				DisAggregationDefinition aDef = (DisAggregationDefinition)aggrDefs.get(rowIdInt);
				// get the table ids
				query = "SELECT BW_TBL.ID, BW_TBL.NAME " +
						"FROM BW_TBL " +
						"WHERE " +
						"	BW_TBL.NAME IN ('" + 
						aDef.sourceTable + "', '" 
						+ aDef.sourceFilter + "', '" 
						+ aDef.targetTable + "', '" 
						+ aDef.targetFilter + "', '" 
						+ aDef.distributionTable + "', '" 
						+ aDef.distributionFilter + "') ";
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
						if (aDef.distributionFilter.equalsIgnoreCase(aDef.sourceFilter))
						{
							distributionFilterId = rs.getInt(1);
							System.out.println("distributionFilterId = " + distributionFilterId);
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
					else if (rs.getString(2).equalsIgnoreCase(aDef.distributionTable))
					{
						distributionTableId = rs.getInt(1);
						System.out.println("distributionTableId = " + distributionTableId);
					}
					else if (rs.getString(2).equalsIgnoreCase(aDef.distributionFilter))
					{
						distributionFilterId = rs.getInt(1);
						System.out.println("distributionFilterId = " + distributionFilterId);
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
				// get the row query for source table
				String srcRowQuery = TableViewManager.getRowQuery(connection, sourceTableId, 1, sourceFilterId, true, "LATEST");
				System.out.println("srcRowQuery = " + srcRowQuery);
				System.out.println("Time(sec) to get row query for source table = " + getElapsedTime());
				// get the row query for target table
				String targetRowQuery = TableViewManager.getRowQuery(connection, targetTableId, 1, targetFilterId, true, "LATEST");
				System.out.println("targetRowQuery = " + targetRowQuery);
				System.out.println("Time(sec) to get row query for target table = " + getElapsedTime());
				// Get the row query for the distribution table
				String distrRowQuery = TableViewManager.getRowQuery(connection, distributionTableId, 1, distributionFilterId, true, "LATEST");
				System.out.println("distrRowQuery = " + distrRowQuery);
				System.out.println("Time(sec) to get row query for distribution table = " + getElapsedTime());

				// create the aggregation query
				StringBuffer aggrQueryBuffer = new StringBuffer();
				// The Source Table
				aggrQueryBuffer.append("\nCREATE TABLE  #SOURCE_ROWS ");
				aggrQueryBuffer.append("\n( BW_ROW_ID  INT  PRIMARY KEY NOT NULL  ) ");
				aggrQueryBuffer.append("\nINSERT INTO #SOURCE_ROWS " + srcRowQuery);
				aggrQueryBuffer.append("\nSELECT ");
				for (int i = 0; i < aDef.sourceSpecifiedKeyColumns.length; i++)
				{
					aggrQueryBuffer.append("\n GB" + i + ".STRING_VALUE AS '" + aDef.sourceSpecifiedKeyColumns[i] + "',");
				}
				if (aDef.sourceOtherKeyColumns != null)
				{
					for (int i = 0, j = aDef.sourceSpecifiedKeyColumns.length; i < aDef.sourceOtherKeyColumns.length; i++, j++)
					{
						aggrQueryBuffer.append("\n GB" + j + ".STRING_VALUE AS '" + aDef.sourceOtherKeyColumns[i] + "',");
					}
				}
				aggrQueryBuffer.append("\nDATACOL.NAME AS TARGET_COLUMN, ");
				aggrQueryBuffer.append("\nCAST (DATA.STRING_VALUE AS NUMERIC) AS VALUE INTO #SOURCE_DATA ");
				aggrQueryBuffer.append("\nFROM ");
				for (int i = 0; i < aDef.sourceSpecifiedKeyColumns.length; i++)
				{
					aggrQueryBuffer.append("\nBW_COLUMN AS GB" + i + "COL,");
					aggrQueryBuffer.append("\nBW_CELL AS GB" + i + ",");
				}
				if (aDef.sourceOtherKeyColumns != null)
				{
					for (int i = 0, j = aDef.sourceSpecifiedKeyColumns.length; i < aDef.sourceOtherKeyColumns.length; i++, j++)
					{
						aggrQueryBuffer.append("\nBW_COLUMN AS GB" + j + "COL,");
						aggrQueryBuffer.append("\nBW_CELL AS GB" + j + ",");
					}
				}
				aggrQueryBuffer.append("\nBW_CELL AS DATA, ");
				aggrQueryBuffer.append("\nBW_COLUMN AS DATACOL, ");
				aggrQueryBuffer.append("\n#SOURCE_ROWS AS BWROW ");
				aggrQueryBuffer.append("\nWHERE ");
				aggrQueryBuffer.append("\n   DATA.BW_ROW_ID = BWROW.BW_ROW_ID ");
				aggrQueryBuffer.append("\nAND DATA.BW_COLUMN_ID = DATACOL.ID ");
				aggrQueryBuffer.append("\nAND DATA.ACTIVE = 1 ");
				aggrQueryBuffer.append("\nAND DBO.ISREALLYNUMERIC(DATA.STRING_VALUE) = 1 ");
				aggrQueryBuffer.append("\nAND DATA.STRING_VALUE IS NOT NULL ");
				aggrQueryBuffer.append("\nAND DATA.STRING_VALUE <> '' ");
				aggrQueryBuffer.append("\nAND DATACOL.NAME IN (");
				for (int i = 0; i < aDef.targetColumns.length; i++)
				{
					aggrQueryBuffer.append("'" + aDef.targetColumns[i].trim() + "'");
					if (i < aDef.targetColumns.length - 1)
						aggrQueryBuffer.append(",");

				}
				aggrQueryBuffer.append(") ");
				for (int i = 0; i < aDef.sourceSpecifiedKeyColumns.length; i++)
				{
					aggrQueryBuffer.append("\nAND GB" + i + ".BW_ROW_ID = BWROW.BW_ROW_ID");
					aggrQueryBuffer.append("\nAND GB" + i + ".BW_COLUMN_ID = GB" + i + "COL.ID");
					aggrQueryBuffer.append("\nAND GB" + i + "COL.NAME = '" + aDef.sourceSpecifiedKeyColumns[i].trim() + "'");
				}
				if (aDef.sourceOtherKeyColumns != null)
				{
					for (int i = 0, j = aDef.sourceSpecifiedKeyColumns.length; i < aDef.sourceOtherKeyColumns.length; i++, j++)
					{
						aggrQueryBuffer.append("\nAND GB" + j + ".BW_ROW_ID = BWROW.BW_ROW_ID");
						aggrQueryBuffer.append("\nAND GB" + j + ".BW_COLUMN_ID = GB" + j + "COL.ID");
						aggrQueryBuffer.append("\nAND GB" + j + "COL.NAME = '" + aDef.sourceOtherKeyColumns[i].trim() + "'");
					}
				}

				// The distribution table
				aggrQueryBuffer.append("\nCREATE TABLE  #DISTR_ROWS ");
				aggrQueryBuffer.append("\n( BW_ROW_ID  INT  PRIMARY KEY NOT NULL  ) ");
				aggrQueryBuffer.append("\nINSERT INTO #DISTR_ROWS " + distrRowQuery);
				aggrQueryBuffer.append("\nSELECT ");
				for (int i = 0; i < aDef.targetKeyColumns.length; i++)
				{
					aggrQueryBuffer.append("\n GB" + i + ".STRING_VALUE AS '" + aDef.targetKeyColumns[i].trim() + "',");
				}
				//aggrQueryBuffer.append("\nDATACOL.NAME AS TARGET_COLUMN, ");
				aggrQueryBuffer.append("\nCAST (DATA.STRING_VALUE AS NUMERIC) AS AVERAGE INTO #DIST_TABLE ");
				aggrQueryBuffer.append("\nFROM ");
				for (int i = 0; i < aDef.targetKeyColumns.length; i++)
				{
					aggrQueryBuffer.append("\nBW_COLUMN AS GB" + i + "COL,");
					aggrQueryBuffer.append("\nBW_CELL AS GB" + i + ",");
				}
				aggrQueryBuffer.append("\nBW_CELL AS DATA, ");
				aggrQueryBuffer.append("\nBW_COLUMN AS DATACOL, ");
				aggrQueryBuffer.append("\n#DISTR_ROWS AS BWROW ");
				aggrQueryBuffer.append("\nWHERE ");
				aggrQueryBuffer.append("\n   DATA.BW_ROW_ID = BWROW.BW_ROW_ID ");
				aggrQueryBuffer.append("\nAND DATA.BW_COLUMN_ID = DATACOL.ID ");
				aggrQueryBuffer.append("\nAND DATA.ACTIVE = 1 ");
				aggrQueryBuffer.append("\nAND DBO.ISREALLYNUMERIC(DATA.STRING_VALUE) = 1 ");
				aggrQueryBuffer.append("\nAND DATA.STRING_VALUE IS NOT NULL ");
				aggrQueryBuffer.append("\nAND DATA.STRING_VALUE <> '' ");
				aggrQueryBuffer.append("\nAND DATACOL.NAME = 'Average' "); // CURRENTLY HARDCODED TO AVERAGE
				//for (int i = 0; i < aDef.targetColumns.length; i++)
				//{
				//    aggrQueryBuffer.append("'" + aDef.targetColumns[i].trim() + "'");
				//    if (i < aDef.targetColumns.length - 1)
				//        aggrQueryBuffer.append(",");

				//}
				//aggrQueryBuffer.append(") ");
				for (int i = 0; i < aDef.targetKeyColumns.length; i++)
				{
					aggrQueryBuffer.append("\nAND GB" + i + ".BW_ROW_ID = BWROW.BW_ROW_ID");
					aggrQueryBuffer.append("\nAND GB" + i + ".BW_COLUMN_ID = GB" + i + "COL.ID");
					aggrQueryBuffer.append("\nAND GB" + i + "COL.NAME = '" + aDef.targetKeyColumns[i].trim() + "'");
				}

				// AVERAGE AGGREGATE/SUM TABLE
				aggrQueryBuffer.append("\nSELECT ");

				for (int i = 0; i < aDef.sourceSpecifiedKeyColumns.length; i++)
				{
					aggrQueryBuffer.append("\nDIST.[" + aDef.sourceSpecifiedKeyColumns[i].trim() + "], ");
				}
				aggrQueryBuffer.append("\n'OTHER' AS OTHER, SUM(AVERAGE) AS TOTAL");
				aggrQueryBuffer.append("\nINTO #SUM_TABLE");
				aggrQueryBuffer.append("\nFROM #DIST_TABLE AS DIST");
				if (aDef.sourceOtherKeyColumns != null)
				{
					for (int i = 0; i < aDef.sourceOtherKeyColumns.length; i++)
					{
						if (i == 0)
						{
							aggrQueryBuffer.append("\nWHERE");
							aggrQueryBuffer.append("\nNOT EXISTS (SELECT * FROM #SOURCE_DATA AS SRC WHERE ");
							aggrQueryBuffer.append("\nDIST.[" + aDef.sourceOtherKeyColumns[i].trim() + "] = SRC.[" + aDef.sourceOtherKeyColumns[i].trim() + "]");
						}
						else
						{
							aggrQueryBuffer.append("\nAND DIST.[" + aDef.sourceOtherKeyColumns[i].trim() + "] = SRC.[" + aDef.sourceOtherKeyColumns[i].trim() + "]");
						}
					}
					aggrQueryBuffer.append("\n)");
				}
				aggrQueryBuffer.append("\nGROUP BY ");
				for (int i = 0; i < aDef.sourceSpecifiedKeyColumns.length; i++)
				{
					if (i < aDef.sourceSpecifiedKeyColumns.length - 1)
						aggrQueryBuffer.append("\nDIST.[" + aDef.sourceSpecifiedKeyColumns[i].trim() + "], ");
					else
						aggrQueryBuffer.append("\nDIST.[" + aDef.sourceSpecifiedKeyColumns[i].trim() + "] ");
				}


				// The Base Table
				aggrQueryBuffer.append("\nCREATE TABLE  #BASE_ROWS ");
				aggrQueryBuffer.append("\n( BW_ROW_ID  INT  PRIMARY KEY NOT NULL  ) ");
				aggrQueryBuffer.append("\nINSERT INTO #BASE_ROWS " + targetRowQuery);
				aggrQueryBuffer.append("\nSELECT DATA.BW_ROW_ID, DATA.BW_COLUMN_ID, ");
				for (int i = 0; i < aDef.targetKeyColumns.length; i++)
				{
					aggrQueryBuffer.append("\n GB" + i + ".STRING_VALUE AS '" + aDef.targetKeyColumns[i].trim() + "',");
				}
				aggrQueryBuffer.append("\nDATACOL.NAME AS TARGET_COLUMN, ");
				aggrQueryBuffer.append("\nDATA.STRING_VALUE AS VALUE INTO #BASE_TABLE ");
				aggrQueryBuffer.append("\nFROM ");
				for (int i = 0; i < aDef.targetKeyColumns.length; i++)
				{
					aggrQueryBuffer.append("\nBW_COLUMN AS GB" + i + "COL,");
					aggrQueryBuffer.append("\nBW_CELL AS GB" + i + ",");
				}
				aggrQueryBuffer.append("\nBW_CELL AS DATA, ");
				aggrQueryBuffer.append("\nBW_COLUMN AS DATACOL, ");
				aggrQueryBuffer.append("\n#BASE_ROWS AS BWROW ");
				aggrQueryBuffer.append("\nWHERE ");
				aggrQueryBuffer.append("\n   DATA.BW_ROW_ID = BWROW.BW_ROW_ID ");
				aggrQueryBuffer.append("\nAND DATA.BW_COLUMN_ID = DATACOL.ID ");
				aggrQueryBuffer.append("\nAND DATA.ACTIVE = 1 ");
				//aggrQueryBuffer.append("\nAND DBO.ISREALLYNUMERIC(DATA.STRING_VALUE) = 1 ");
				//aggrQueryBuffer.append("\nAND DATA.STRING_VALUE IS NOT NULL ");
				//aggrQueryBuffer.append("\nAND DATA.STRING_VALUE <> '' ");
				aggrQueryBuffer.append("\nAND DATACOL.NAME IN (");
				for (int i = 0; i < aDef.targetColumns.length; i++)
				{
					aggrQueryBuffer.append("'" + aDef.targetColumns[i].trim() + "'");
					if (i < aDef.targetColumns.length - 1)
						aggrQueryBuffer.append(",");

				}
				aggrQueryBuffer.append(") ");
				for (int i = 0; i < aDef.targetKeyColumns.length; i++)
				{
					aggrQueryBuffer.append("\nAND GB" + i + ".BW_ROW_ID = BWROW.BW_ROW_ID");
					aggrQueryBuffer.append("\nAND GB" + i + ".BW_COLUMN_ID = GB" + i + "COL.ID");
					aggrQueryBuffer.append("\nAND GB" + i + "COL.NAME = '" + aDef.targetKeyColumns[i].trim() + "'");
				}

				// FINALLY, DISAGGREATE
				aggrQueryBuffer.append("\nINSERT INTO BW_RC_STRING_VALUE");
				aggrQueryBuffer.append("\nSELECT BASE.BW_ROW_ID, ");
				aggrQueryBuffer.append("\nBASE.BW_COLUMN_ID, ");
				aggrQueryBuffer.append("\nCAST((DIST.Average / SUMTBL.TOTAL) * SRCTBL.VALUE AS VARCHAR(256)) AS NEWVALUE, NULL AS FORMULA,  ? AS TXID");
				aggrQueryBuffer.append("\nFROM ");
				aggrQueryBuffer.append("\n#BASE_TABLE as BASE,");
				aggrQueryBuffer.append("\n#DIST_TABLE AS DIST,");
				aggrQueryBuffer.append("\n#SUM_TABLE as SUMTBL,");
				aggrQueryBuffer.append("\n#SOURCE_DATA AS SRCTBL");
				aggrQueryBuffer.append("\nWHERE ");
				aggrQueryBuffer.append("\nSRCTBL.TARGET_COLUMN = BASE.TARGET_COLUMN");
				for (int i = 0; i < aDef.targetKeyColumns.length; i++)
				{
					aggrQueryBuffer.append("\nAND BASE.[" + aDef.targetKeyColumns[i].trim() + "] = DIST.[" + aDef.targetKeyColumns[i].trim() + "]");
				}

				for (int i = 0; i < aDef.sourceSpecifiedKeyColumns.length; i++)
				{
					aggrQueryBuffer.append("\nAND DIST.[" + aDef.sourceSpecifiedKeyColumns[i].trim() + "] = SUMTBL.[" + aDef.sourceSpecifiedKeyColumns[i].trim() + "]");
				}

				for (int i = 0; i < aDef.sourceSpecifiedKeyColumns.length; i++)
				{
					aggrQueryBuffer.append("\nAND SRCTBL.[" + aDef.sourceSpecifiedKeyColumns[i].trim() + "] = BASE.[" + aDef.sourceSpecifiedKeyColumns[i].trim() + "]");
				}

				if (aDef.sourceOtherKeyColumns != null)
				{ 
					for (int i = 0; i < aDef.sourceOtherKeyColumns.length; i++)
					{
						aggrQueryBuffer.append("\nAND (SRCTBL.[" + aDef.sourceOtherKeyColumns[i].trim() + "] LIKE '%OTHER%' OR SRCTBL.[" + aDef.sourceOtherKeyColumns[i].trim() + "] LIKE '%ALL%')");
					}
					aggrQueryBuffer.append("\nAND NOT EXISTS (SELECT * FROM #SOURCE_DATA AS SRCDATA WHERE ");
					for (int i = 0; i < aDef.sourceOtherKeyColumns.length; i++)
					{
						if (i == 0)
						{
							aggrQueryBuffer.append("\nSRCDATA.[" + aDef.sourceOtherKeyColumns[i].trim() + "] = BASE.[" + aDef.sourceOtherKeyColumns[i].trim() + "]");
						}
						else
						{
							aggrQueryBuffer.append("\nAND SRCDATA.[" + aDef.sourceOtherKeyColumns[i].trim() + "] = BASE.[" + aDef.sourceOtherKeyColumns[i].trim() + "]");
						}
					}
					aggrQueryBuffer.append("\n)");
				}

				// UPDATE THE DATABASE
				aggrQueryBuffer.append("\nEXEC BW_UPD_CELL_FROM_RCSV ?");

				aggrQueryBuffer.append("\nDROP TABLE #SOURCE_ROWS ");
				aggrQueryBuffer.append("\nDROP TABLE #DISTR_ROWS ");
				aggrQueryBuffer.append("\nDROP TABLE #BASE_ROWS ");
				aggrQueryBuffer.append("\nDROP TABLE #SOURCE_DATA ");
				aggrQueryBuffer.append("\nDROP TABLE #DIST_TABLE ");
				aggrQueryBuffer.append("\nDROP TABLE #SUM_TABLE ");
				aggrQueryBuffer.append("\nDROP TABLE #BASE_TABLE ");

				// run the aggregation query
				query = aggrQueryBuffer.toString();
				System.out.println("Time(sec) to create aggregation query = " + getElapsedTime());

				tm.commitTransaction();
				System.out.println("aggr query = " + query);
				ps = connection.prepareStatement(query);
				ps.setInt(1, tid);
				ps.setInt(2, tid);
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
				System.out.println("Time(sec) to run aggregation query = " + getElapsedTime() + " with tid = " + tid);
			}
		}
		catch (Exception e)
		{
			try
			{
				tm.rollbackTransaction();
			}
			catch (SQLException e2)
			{
				e2.printStackTrace();
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
				e.printStackTrace();
				throw new SystemException(e);
			}
		}

		System.out.println("End : disaggregate()");
	}
	
}

