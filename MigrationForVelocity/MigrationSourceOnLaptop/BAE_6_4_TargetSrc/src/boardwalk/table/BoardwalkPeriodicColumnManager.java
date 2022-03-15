package boardwalk.table;

import java.sql.*;
import java.util.*;
import boardwalk.connection.*;

import com.boardwalk.exception.BoardwalkException;
import com.boardwalk.table.*;
import com.boardwalk.database.*;
import boardwalk.common.*;

public abstract class BoardwalkPeriodicColumnManager
{
	public abstract String getNewPeriod();
	public abstract String getPreviousPeriod();

	public abstract Vector getNewPeriods();
	public abstract Vector getPreviousPeriods();
	public abstract Vector getColumnsTomodify();
	public abstract ArrayList getStatusCells();

	public int setNewPeriods(Connection bwcon, int userId, String asTableId)
	throws Exception
	{
		// Periodic column list
		HashMap columnByTable	= null;
		Vector tableList		= null;
		Statement stmt			= null;
		String lsSql			= "";
		BoardwalkPeriodicColumn  pc = null;
		String	tableArray[]	= null;
		
		if ( getPreviousPeriods().size() > 0 )
		{
			pc = (BoardwalkPeriodicColumn) getPreviousPeriods().firstElement();
			columnByTable	= getColumns(bwcon, pc.getName(), asTableId);
			tableList		= new Vector(columnByTable.keySet());
		}
		else
		{
			String TempStr = "";
			TempStr = asTableId.substring(1);
			TempStr = TempStr.substring(0,TempStr.length()-1);
			tableArray = TempStr.split(",");
			tableList	= new Vector();
			for (int i=0; i< tableArray.length ;i++ )
			{
				System.out.println(" Table Id Where Period is To be Shifted "+asTableId);
				tableList.add(new Integer(tableArray[i]));
			}
		}

		// Stored procedure to be executed before Period Shift
		exceutePrePeriodShiftProcedure(bwcon);

		Iterator ppi			= getPreviousPeriods().iterator();
		TransactionManager tm	= null;
		int tid					= -1; // Was set to zero previously

		try
		{
			tm	= new TransactionManager(bwcon, userId);
			tid = tm.startTransaction("PS","Periodic Shift");
			resetPrevTimetoCurrent();

			// initialize the new periodic set
			Iterator npi			= getNewPeriods().iterator();
			HashMap tableAccess		= null;
			boolean lbColumnAdded	= false;
			int liPrevColumnId		= -1;
			// create new columns if neccessary
			while (npi.hasNext())
			{
				pc = (BoardwalkPeriodicColumn)npi.next();
				//System.out.println("Create Column  = " + pc.getName()+" "+pc.getPrevColumnName());
				// check if it already exists
				if (!getPreviousPeriods().contains(pc))
				{
					// create the column
					Iterator ti = tableList.iterator();
					while (ti.hasNext())
					{
						int tableId = ((Integer)ti.next()).intValue();
//						liPrevColumnId = getPrevColumnId(bwcon, pc.getPrevColumnName(), tableId);
						System.out.println("Creating column g2 " + pc.getName() + " in table " + tableId);
						
						TableManager.createColumn(
									bwcon,
									tableId,
									pc.getName(),
									"STRING",
									"0",
									1,1.0,-1,null,-1,-1,-1,-1,
									liPrevColumnId, 1,
									tid,
									true,
									-1
									);

						/*TableManager.createColumnXL(
									bwcon,
									tableId,
									pc.getName(),
									liPrevColumnId, 1,
									tid
									);*/


						System.out.println("Successfully created column in table id = " + tableId+" column name is >"+pc.getName());
						lbColumnAdded = true;
						TableManager.resequenceColumns(bwcon, tableId);

						// Process other instructions applicable to the column here
					}
				}
			}

			System.out.println("Time to Add columns >> "+getElapsedTime());

			// get the access control for all the tables
			tableAccess = new HashMap();
			Iterator ti = tableList.iterator();

			while(ti.hasNext())
			{
				int tableId				= ((Integer) ti.next()).intValue();
				ColumnAccessList cal	= ColumnManager.getColumnAccess(bwcon, tableId);
				tableAccess.put(new Integer(tableId), cal);
				//System.out.println("Got Table access for tableid = " + tableId);
			}

			tm.commitTransaction();

			// This block is seprated so that we do not have wrong avlues of col_access set
			// We had this problem in nokia when the columna addition and setting access was done in same transaction

			// set access control for columns
			npi = getColumnsTomodify().iterator();
			while (npi.hasNext())
			{
				tm = new TransactionManager(bwcon, userId);

				tid = tm.startTransaction("PS","Periodic Shift");
				pc = (BoardwalkPeriodicColumn)npi.next();

				//System.out.println("Processing column "+pc.getName()+" Access "+pc.getAccess());
				if(pc.getAccess() < 0)
					continue;
				columnByTable = getColumns(bwcon, pc.getName(), asTableId);

				//System.out.println("Setting access control for " + pc.getName() + " to " + pc.getAccess());
				ti = tableList.iterator();

				while(ti.hasNext())
				{
					Integer tableId = (Integer)ti.next();

					//System.out.println("Got the Table id: "+tableId);

					Vector columnIds	= new Vector();
					Vector rels			= new Vector();
					Vector access		= new Vector();

					int columnId = ((Integer)columnByTable.get(tableId)).intValue();
					//System.out.println ("tableid = " + tableId + " columnId = " + columnId);
					ColumnAccessList cal = (ColumnAccessList) tableAccess.get(tableId);
					int columnAccess = 2;
					Integer ca = (Integer) cal.getAccess().get("PUBLIC" + ":" + columnId);
					if (ca != null)
						columnAccess = ca.intValue();

					if (columnAccess != pc.getAccess())
					{
						// ColumnManager.updateColumnAccess(bwcon, columnId, "PUBLIC", pc.getAccess(), tid);
						columnIds.addElement( new Integer(columnId));
						rels.addElement( "PUBLIC");
						access.addElement( new Integer( pc.getAccess()));
					}
					ColumnManager.updateColumnAccessBatch(bwcon, columnIds, rels, access,  tid);
				}

				tm.commitTransaction();
			}

			// Process Instructions for other columns
			// This instruction processing could be required once while adding the columns
			// and later on while setting features on other columns while are just modified.

			// This will be required so that any attribute changed during the course
			// will be set/reset on initialized here.

			// Ideally all this needs to happen in a single transaction
			// Currenlty there are two transactions which deal with this, one for column add/delete part
			// and other for setting up of access rights
			resetPrevTimetoCurrent();

			Iterator oci = getColumnsTomodify().iterator();
			tm = new TransactionManager(bwcon, userId);
			tid = tm.startTransaction("PS","Periodic Shift");

			while (oci.hasNext())
			{
				pc = (BoardwalkPeriodicColumn) oci.next();

//				columnByTable	= getColumns(bwcon, pc.getName(), asTableId);
//				tableList		= new Vector(columnByTable.keySet());

				//System.out.println("other columns > Processing column "+pc.getName());

				// Process each instruction applicable to the columns.
				// Some instructions may not fit here
				ti = tableList.iterator();
				int count = 0;

				while(ti.hasNext())
				{
					Integer tableId = (Integer)ti.next();
					//System.out.println("Processing column "+pc.getName()+" in table "+tableId);
					resetPrevTimetoCurrent();
					processColumns(bwcon, pc, tableId.intValue(), tid);
				}
			}
			updateStatusValues(bwcon, getStatusCells(), tid);
//			getStatusCells

			// BW_UPD_CELL_FROM_RCSV
			System.out.println("Time to Process columns >> "+asTableId);

			String TempStr = "";
			TempStr = asTableId.substring(1);
			TempStr = TempStr.substring(0,TempStr.length()-1);


			updateTableFromRCSV(bwcon, tid, Integer.parseInt (TempStr), userId);
			// Delete column block is now at end.
			// with this we are able to process columns which are likely to be deleted

			System.out.println("Time to Process columns >> "+getElapsedTime());

			resetPrevTimetoCurrent();

			while (ppi.hasNext())
			{
				// delete columns that are no longer in play
				pc = (BoardwalkPeriodicColumn)ppi.next();

				if (!getNewPeriods().contains(pc))
				{
					// We delete columns based on name for performance
					// Currently calling the SP SFET_DEL_COLUMN_BY_NAME which was written for Nokia.
					// We can rename the SP SFET_DEL_COLUMN_BY_NAME to DEL_COLUMN_BY_NAME

					// Some instructions may not fit here
				//	columnByTable	= getColumns(bwcon, pc.getName(), asTableId);
				//	tableList		= new Vector(columnByTable.keySet());

					ti = tableList.iterator();
					while(ti.hasNext())
					{
						Integer tableId = (Integer) ti.next();
						System.out.println("Table id is ass  >> "+tableId.intValue());

						ColumnManager.deleteColumn(bwcon, pc.getName(), tid, tableId.intValue());
					}
				}
			}

			System.out.println("Time to Delete columns >> "+getElapsedTime());

			tm.commitTransaction();
		}
		catch (Exception e)
		{
			tm.commitTransaction();
			System.out.println("There was a problem shifting to the new period set, aborting the transaction");
			e.printStackTrace();
			try
			{
				tm.rollbackTransaction();
			}
			catch (SQLException sqe)
			{
				sqe.printStackTrace();
			}

			throw e;
		}
		finally
		{
			try
			{
				if ( stmt != null ) {
					stmt.close();
					stmt = null;
				}
			}
			catch(SQLException sqlexception1)
			{
				sqlexception1.printStackTrace();
			}
		}

		try
		{
			exceutePostPeriodShiftProcedure(bwcon);			
		}
		catch (Exception exe)
		{
			exe.printStackTrace();
		}

		return tid;
	}


	public HashMap getColumns(Connection connection, String name, String asTableIdsPS)
	throws SQLException
	{
		// The table
		HashMap colXtable 		= new HashMap();
		ResultSet resultset 	= null;
		PreparedStatement stmt 	= null;
		String lsSql 			= "";
		String TempStr 			= "";
		TempStr 				= asTableIdsPS.substring(1);
		TempStr 				= TempStr.substring(0,TempStr.length()-1);

		try
		{
			lsSql = " SELECT DISTINCT BW_TBL.ID, BW_COLUMN.ID FROM BW_TBL, BW_COLUMN WHERE BW_COLUMN.BW_TBL_ID = BW_TBL.ID AND BW_COLUMN.NAME = ? AND BW_COLUMN.BW_TBL_ID IN ( ?)";
			
			stmt = connection.prepareStatement(lsSql);
			stmt.setString(1, name);
			stmt.setString(2, TempStr);
			resultset = stmt.executeQuery();
			while(resultset.next())
			{
				int tableId		= resultset.getInt(1);
				int columnId	= resultset.getInt(2);
				colXtable.put(new Integer(tableId), new Integer(columnId));
				//System.out.println("getColumns got the column id: "+columnId +" and the table id: "+tableId);
			}
		}
		catch (SQLException e)
		{
			throw e;
		}
		finally
		{
			try
			{
				if(resultset != null)
				{
					resultset.close();
					resultset = null;
				}
				if(stmt != null)
				{
					stmt.close();
					stmt = null;
				}
			}
			catch (SQLException sq2)
			{
				sq2.printStackTrace();
			}
		}

		return colXtable;
	}

	long prevTime = -1;
	long totalTime = 0;

	/**
		This method returns the elapsed time in seconds.
	**/
    public float getElapsedTime()
    {
		if (prevTime == -1)
			prevTime = System.currentTimeMillis();
		// Get elapsed time in seconds
    	float elapsedTimeSec = (System.currentTimeMillis()-prevTime)/1000F;

    	// reset time
    	prevTime = System.currentTimeMillis();
		totalTime += elapsedTimeSec;

    	return elapsedTimeSec;
	}

	public void resetPrevTimetoCurrent()
    {
		prevTime = System.currentTimeMillis();
	}

	public float getTotalTime()
	{
    	return totalTime;
	}

	public void processColumns(Connection connection, BoardwalkPeriodicColumn pc, int aiTblId, int aiTxid)throws SQLException
	{
		PreparedStatement	prepstatement	= null;
		ResultSet			resultset		= null;

		// First instruction: Set the default access rights
		// This is handled outside this column processing

		// Second instruction: Copy Values only
		// If this field has a column name specified
		// then copy only the cell values from the specified column name to Column name
		if(!BoardwalkUtility.checkIfNullOrBlank(pc.getCopyColName()) && BoardwalkUtility.checkIfNullOrBlank(pc.getCopyFormulaColName()) && BoardwalkUtility.checkIfNullOrBlank(pc.getDefaultValue()) && BoardwalkUtility.checkIfNullOrBlank(pc.getDefaultFormula()))
		{
			// Use stored procedure COPY_CELL_VALUES

			// Assumption: Both the columns are part of the same table,
			// So this has to be done table by table
			// This can be achived by reading the columns cell value
			// and then puting them in BW_RC_STRING_VALUE and then
			// and then invoking the SP BW_UPD_CELL_FROM_RCSV
			resetPrevTimetoCurrent();
			String CALL_BW_COPY_CELL_VALUES = "{ CALL COPY_CELL_VALUES(?, ?, ?, ?) }";

            try
            {
				prepstatement = null;
				prepstatement = connection.prepareStatement(CALL_BW_COPY_CELL_VALUES);
				prepstatement.setString(1,pc.getCopyColName());
				prepstatement.setString(2,pc.getName());
				prepstatement.setInt(3,aiTblId);
				prepstatement.setInt(4,aiTxid);
				prepstatement.execute();
			}
            catch (SQLException sqlexe)
            {
                  throw sqlexe;
            }
            finally
            {
                  try
                  {
                        prepstatement.close();
                  }
                  catch (SQLException sqlexe1)
                  {
                        throw sqlexe1;
                  }
            }
			System.out.println("Time to COPY_CELL_VALUES >> "+ pc.getName()+" "+getElapsedTime());
		}

		// Third instruction: Copy Formulas only
		// If this field has a column name specified
		// Then copy cell formulas from the specified column name
		if(!BoardwalkUtility.checkIfNullOrBlank(pc.getCopyFormulaColName()) && BoardwalkUtility.checkIfNullOrBlank(pc.getCopyColName()) && BoardwalkUtility.checkIfNullOrBlank(pc.getDefaultValue()) && BoardwalkUtility.checkIfNullOrBlank(pc.getDefaultFormula()))
		{
			resetPrevTimetoCurrent();
			String CALL_BW_COPY_CELL_FORMULA = "{ CALL COPY_CELL_FORMULA(?, ?, ?, ?) }";
            try
            {
				prepstatement = null;
				prepstatement = connection.prepareStatement(CALL_BW_COPY_CELL_FORMULA);
				prepstatement.setString(1,pc.getCopyFormulaColName());
				prepstatement.setString(2,pc.getName());
				prepstatement.setInt(3,aiTblId);
				prepstatement.setInt(4,aiTxid);
				prepstatement.execute();
			}
            catch (SQLException sqlexe)
            {
                  throw sqlexe;
            }
            finally
            {
                  try
                  {
                        prepstatement.close();
                  }
                  catch (SQLException sqlexe1)
                  {
                        throw sqlexe1;
                  }
            }
			System.out.println("Time to COPY_CELL_FORMULA >> "+ pc.getName()+" "+getElapsedTime());
		}

		// Copy both data and formula only
		if(!BoardwalkUtility.checkIfNullOrBlank(pc.getCopyColName()) && !BoardwalkUtility.checkIfNullOrBlank(pc.getCopyFormulaColName()) && BoardwalkUtility.checkIfNullOrBlank(pc.getDefaultValue()) && BoardwalkUtility.checkIfNullOrBlank(pc.getDefaultFormula()))
		{
			String CALL_BW_COPY_CELL_VALUEFORMULA = "{ CALL COPY_CELL_VALUEFORMULA(?, ?, ?, ?) }";
			// Just check if the column name for copying cell value and cell formula are same.
			resetPrevTimetoCurrent();
			if(pc.getCopyColName().equals(pc.getCopyFormulaColName()))
			{
				try
				{
					prepstatement = null;
					prepstatement = connection.prepareStatement(CALL_BW_COPY_CELL_VALUEFORMULA);
					prepstatement.setString(1,pc.getCopyColName());
					prepstatement.setString(2,pc.getName());
					prepstatement.setInt(3,aiTblId);
					prepstatement.setInt(4,aiTxid);
					prepstatement.execute();
				}
				catch (SQLException sqlexe)
				{
					  throw sqlexe;
				}
				finally
				{
					  try
					  {
							prepstatement.close();
					  }
					  catch (SQLException sqlexe1)
					  {
							throw sqlexe1;
					  }
				}

			System.out.println("Time to COPY_CELL_VALUEFORMULA >> "+ pc.getName()+" "+getElapsedTime());
			}
		}

		// Copy cell data and set default formula
		if(!BoardwalkUtility.checkIfNullOrBlank(pc.getCopyColName()) && !BoardwalkUtility.checkIfNullOrBlank(pc.getDefaultFormula()) && BoardwalkUtility.checkIfNullOrBlank(pc.getCopyFormulaColName()) && BoardwalkUtility.checkIfNullOrBlank(pc.getDefaultValue()))
		{
			String CALL_COPY_CELL_VALUE_DEF_FORMULA = "{ CALL COPY_CELL_VALUE_DEF_FORMULA(?, ?, ?, ?, ?) }";
			// Just check if the column name for copying cell value and cell formula are same.
			resetPrevTimetoCurrent();
			try
			{
				prepstatement = null;
				prepstatement = connection.prepareStatement(CALL_COPY_CELL_VALUE_DEF_FORMULA);
				prepstatement.setString(1,pc.getCopyColName());
				prepstatement.setString(2,pc.getName());
				prepstatement.setString(3,pc.getDefaultFormula());
				prepstatement.setInt(4,aiTblId);
				prepstatement.setInt(5,aiTxid);
				prepstatement.execute();
			}
			catch (SQLException sqlexe)
			{
				  throw sqlexe;
			}
			finally
			{
				  try
				  {
						prepstatement.close();
				  }
				  catch (SQLException sqlexe1)
				  {
						throw sqlexe1;
				  }
			}
			System.out.println("Time to COPY_CELL_VALUE_DEF_FORMULA >> "+ pc.getName()+" "+getElapsedTime());
		}

		// Copy formula and set default value
		if(!BoardwalkUtility.checkIfNullOrBlank(pc.getCopyFormulaColName()) && !BoardwalkUtility.checkIfNullOrBlank(pc.getDefaultValue()) && BoardwalkUtility.checkIfNullOrBlank(pc.getCopyColName()) && BoardwalkUtility.checkIfNullOrBlank(pc.getDefaultFormula()))
		{
			String CALL_COPY_CELL_FORMULA_DEF_VALUE = "{ CALL COPY_CELL_FORMULA_DEF_VALUE(?, ?, ?, ?, ?) }";
			resetPrevTimetoCurrent();

            try
            {
				prepstatement = null;
				prepstatement = connection.prepareStatement(CALL_COPY_CELL_FORMULA_DEF_VALUE);
				prepstatement.setString(1,pc.getCopyFormulaColName());
				prepstatement.setString(2,pc.getName());
				prepstatement.setString(3,pc.getDefaultValue());
				prepstatement.setInt(4,aiTblId);
				prepstatement.setInt(5,aiTxid);
				prepstatement.execute();
			}
            catch (SQLException sqlexe)
            {
                  throw sqlexe;
            }
            finally
            {
                  try
                  {
                        prepstatement.close();
                  }
                  catch (SQLException sqlexe1)
                  {
                        throw sqlexe1;
                  }
            }
			System.out.println("Time to COPY_CELL_FORMULA_DEF_VALUE >> "+ pc.getName()+" "+getElapsedTime());
		}


		// Forth instruction: Default values
		// If this field has a value set
		// Then set the value above to all the cells for all the tables with this column name
		if(!BoardwalkUtility.checkIfNullOrBlank(pc.getDefaultValue()) && BoardwalkUtility.checkIfNullOrBlank(pc.getDefaultFormula()) && BoardwalkUtility.checkIfNullOrBlank(pc.getCopyFormulaColName()) && BoardwalkUtility.checkIfNullOrBlank(pc.getCopyColName()))
		{
			String CALL_BW_SET_CELL_DEF_VALUE = "{ CALL BW_SET_CELL_DEF_VALUE(?, ?, ?, ?) }";
			resetPrevTimetoCurrent();

            try
            {
				prepstatement = null;
				prepstatement = connection.prepareStatement(CALL_BW_SET_CELL_DEF_VALUE);
				prepstatement.setString(1,pc.getName());
				prepstatement.setString(2,pc.getDefaultValue());
				prepstatement.setInt(3,aiTblId);
				prepstatement.setInt(4,aiTxid);
				prepstatement.execute();
			}
            catch (SQLException sqlexe)
            {
                  throw sqlexe;
            }
            finally
            {
                  try
                  {
                        prepstatement.close();
                  }
                  catch (SQLException sqlexe1)
                  {
                        throw sqlexe1;
                  }
            }
			System.out.println("Time to BW_SET_CELL_DEF_VALUE >> "+ pc.getName()+" "+getElapsedTime());
		}

		// Fifth instruction: Default Formulae
		// If this field has a value set
		// Then set the value above to all the cells formulas for all the tables with this column name
		if(!BoardwalkUtility.checkIfNullOrBlank(pc.getDefaultFormula()) && BoardwalkUtility.checkIfNullOrBlank(pc.getDefaultValue()) && BoardwalkUtility.checkIfNullOrBlank(pc.getCopyFormulaColName()) && BoardwalkUtility.checkIfNullOrBlank(pc.getCopyColName()))
		{
			String CALL_BW_SET_CELL_DEF_FORMULA = "{ CALL BW_SET_CELL_DEF_FORMULA(?, ?, ?, ?) }";
			resetPrevTimetoCurrent();

            try
            {
				prepstatement = null;
				prepstatement = connection.prepareStatement(CALL_BW_SET_CELL_DEF_FORMULA);
				prepstatement.setString(1,pc.getName());
				prepstatement.setString(2,pc.getDefaultFormula());
				prepstatement.setInt(3,aiTblId);
				prepstatement.setInt(4,aiTxid);
				prepstatement.execute();
			}
            catch (SQLException sqlexe)
            {
                  throw sqlexe;
            }
            finally
            {
                  try
                  {
                        prepstatement.close();
                  }
                  catch (SQLException sqlexe1)
                  {
                        throw sqlexe1;
                  }
            }
			System.out.println("Time to BW_SET_CELL_DEF_FORMULA >> "+ pc.getName()+" "+getElapsedTime());
		}

		// Special case where both default are to be set
		if(!BoardwalkUtility.checkIfNullOrBlank(pc.getDefaultValue()) && !BoardwalkUtility.checkIfNullOrBlank(pc.getDefaultFormula()) && BoardwalkUtility.checkIfNullOrBlank(pc.getCopyFormulaColName()) && BoardwalkUtility.checkIfNullOrBlank(pc.getCopyColName()))
		{
			String CALL_BW_SET_CELL_DEF_VALUE_FORMULA = "{ CALL BW_SET_CELL_DEF_VALUE_FORMULA(?, ?, ?, ?, ?) }";
			resetPrevTimetoCurrent();
            try
            {
				prepstatement = null;
				prepstatement = connection.prepareStatement(CALL_BW_SET_CELL_DEF_VALUE_FORMULA);
				prepstatement.setString(1,pc.getName());
				prepstatement.setString(2,pc.getDefaultValue());
				prepstatement.setString(3,pc.getDefaultFormula());
				prepstatement.setInt(4,aiTblId);
				prepstatement.setInt(5,aiTxid);
				prepstatement.execute();
			}
            catch (SQLException sqlexe)
            {
                  throw sqlexe;
            }
            finally
            {
                  try
                  {
                        prepstatement.close();
                  }
                  catch (SQLException sqlexe1)
                  {
                        throw sqlexe1;
                  }
            }
			System.out.println("Time to BW_SET_CELL_DEF_VALUE_FORMULA >> "+ pc.getName()+" "+getElapsedTime());
		}

		// Sixth instruction: Delete Value
		// If this field has a value set
		// Then set cell values to blank for this column cells in all tables
		// Do we need to validate this entry against the Copy Value Instruction
		if(!BoardwalkUtility.checkIfNullOrBlank(pc.getDeleteValue()) && BoardwalkUtility.checkIfNullOrBlank(pc.getDeleteFormulaValue()))
		{
			// Write a SP to set value to blank for
			// BW_STRING_VALUE and BW_CELL for a column name
			String CALL_BW_DELETE_CELL_VALUE = "{ CALL BW_DELETE_CELL_VALUE(?, ?, ?) }";
			resetPrevTimetoCurrent();

            try
            {
				prepstatement = null;
				prepstatement = connection.prepareStatement(CALL_BW_DELETE_CELL_VALUE);
				prepstatement.setString(1,pc.getName());
				prepstatement.setInt(2,aiTblId);
				prepstatement.setInt(3,aiTxid);
				prepstatement.execute();
			}
            catch (SQLException sqlexe)
            {
                  throw sqlexe;
            }
            finally
            {
                  try
                  {
                        prepstatement.close();
                  }
                  catch (SQLException sqlexe1)
                  {
                        throw sqlexe1;
                  }
            }
			System.out.println("Time to BW_DELETE_CELL_VALUE >> "+ pc.getName()+" "+getElapsedTime());
		}

		// Seventh instruction: Delete Formula
		// If this field has a value set
		// Then set cell formulas to balnk for this column cells in all tables
		// Do we need to validate this entry against the Copy Formula Instruction
		if(!BoardwalkUtility.checkIfNullOrBlank(pc.getDeleteFormulaValue()) && BoardwalkUtility.checkIfNullOrBlank(pc.getDeleteValue()))
		{
			// Write a SP to set value to blank for
			// BW_FORMULA_VALUE and BW_CELL for a column name
			String CALL_BW_DELETE_CELL_FORMULA = "{ CALL BW_DELETE_CELL_FORMULA(? ,? , ?) }";
			resetPrevTimetoCurrent();

            try
            {
				prepstatement = null;
				prepstatement = connection.prepareStatement(CALL_BW_DELETE_CELL_FORMULA);
				prepstatement.setString(1,pc.getName());
				prepstatement.setInt(2,aiTblId);
				prepstatement.setInt(3,aiTxid);
				prepstatement.execute();
			}
            catch (SQLException sqlexe)
            {
                  throw sqlexe;
            }
            finally
            {
                  try
                  {
                        prepstatement.close();
                  }
                  catch (SQLException sqlexe1)
                  {
                        throw sqlexe1;
                  }
            }
			System.out.println("Time to BW_DELETE_CELL_FORMULA >> "+ pc.getName()+" "+getElapsedTime());

		}

		// Special case for both delete of Value and formula
		if(!BoardwalkUtility.checkIfNullOrBlank(pc.getDeleteValue()) && !BoardwalkUtility.checkIfNullOrBlank(pc.getDeleteFormulaValue()))
		{
			// BW_FORMULA_VALUE and BW_CELL for a column name
			String CALL_BW_DELETE_CELL_VALUE_FORMULA = "{ CALL BW_DELETE_CELL_VALUE_FORMULA(? ,? , ?) }";
			resetPrevTimetoCurrent();

            try
            {
				prepstatement = null;
				prepstatement = connection.prepareStatement(CALL_BW_DELETE_CELL_VALUE_FORMULA);
				prepstatement.setString(1,pc.getName());
				prepstatement.setInt(2,aiTblId);
				prepstatement.setInt(3,aiTxid);
				prepstatement.execute();
			}
            catch (SQLException sqlexe)
            {
                  throw sqlexe;
            }
            finally
            {
                  try
                  {
                       prepstatement.close();
                  }
                  catch (SQLException sqlexe1)
                  {
                        throw sqlexe1;
                  }
            }
			System.out.println("Time to BW_DELETE_CELL_VALUE_FORMULA >> "+ pc.getName()+" "+getElapsedTime());

		}


		// Now update the column sequence
		if(pc.getColOrder() > 0)
		{
			String CALL_BW_SET_COL_SEQ = " {CALL BW_SET_COL_SEQ(?,?,?)} ";
			resetPrevTimetoCurrent();

            try
            {
				prepstatement = null;
				prepstatement = connection.prepareStatement(CALL_BW_SET_COL_SEQ);
				prepstatement.setString(1,pc.getName());
				prepstatement.setInt(2,aiTblId);
				prepstatement.setInt(3,pc.getColOrder());

				prepstatement.execute();
			}
            catch (SQLException sqlexe)
            {
                  throw sqlexe;
            }
            finally
            {
                  try
                  {
                       prepstatement.close();
                  }
                  catch (SQLException sqlexe1)
                  {
                        throw sqlexe1;
                  }
            }
			System.out.println("Time to BW_SET_COL_SEQ >> "+ pc.getName()+" "+getElapsedTime());
		}

		// Rename Column
		if(!BoardwalkUtility.checkIfNullOrBlank(pc.getRenameValue()))
		{
			String CALL_BW_RENAME_TARGET_COLUMN = " {CALL BW_RENAME_TARGET_COLUMN(?,?,?)} ";
			resetPrevTimetoCurrent();

            try
            {
				prepstatement = null;
				prepstatement = connection.prepareStatement(CALL_BW_RENAME_TARGET_COLUMN);
				prepstatement.setString(1,pc.getName());
				prepstatement.setString(2,pc.getRenameValue());
				prepstatement.setInt(3,aiTblId);

				prepstatement.execute();
			}
            catch (SQLException sqlexe)
            {
                  throw sqlexe;
            }
            finally
            {
                  try
                  {
                       prepstatement.close();
                  }
                  catch (SQLException sqlexe1)
                  {
                        throw sqlexe1;
                  }
            }
			System.out.println("Time to BW_RENAME_TARGET_COLUMN >> "+ pc.getName()+" "+getElapsedTime());

		}



	}

	// BW_UPD_CELL_FROM_RCSV
	public void updateStatusValues(Connection connection, ArrayList asStatusCells, int aiTxid)throws Exception
	{
		PreparedStatement	prepstatement	= null;
		String lsSql = " INSERT INTO BW_RC_STRING_VALUE (BW_ROW_ID, BW_COLUMN_ID, STRING_VALUE, FORMULA, TX_ID, CHANGE_FLAG) VALUES (?,?,?,?,?,?) ";
		prepstatement = connection.prepareStatement(lsSql);

		try
		{
			for(int i=0 ; i < asStatusCells.size(); i++)
			{
				NewCell PnewCell = (NewCell) asStatusCells.get(i);
//				System.out.println(" PnewCell.getRowId() "+PnewCell.getRowId()+" PnewCell.getColumnId() "+PnewCell.getColumnId());
				prepstatement.setInt(1,PnewCell.getRowId());
				prepstatement.setInt(2,PnewCell.getColumnId());
				prepstatement.setString(3,"Done");
				prepstatement.setString(4,"");
				prepstatement.setInt(5,aiTxid);
				prepstatement.setInt(6,1);

				prepstatement.addBatch();
			}
			int[] rescnt = prepstatement.executeBatch();
		}
		catch (SQLException sqlexe)
		{
			throw sqlexe;
		}
		finally
		{
			try
			{
				prepstatement.close();
			}
			catch (SQLException sqlexe1)
			{
				throw sqlexe1;
			}
		}
	}

	// BW_UPD_CELL_FROM_RCSV
	public void updateTableFromRCSV(Connection connection, int aiTid, int tableId, int userId)throws Exception
	{
		PreparedStatement	prepstatement	= null;

		String CALL_BW_UPD_CELL_FROM_RCSV = "{ CALL BW_UPD_CELL_FROM_RCSV(?,?,?,?)} ";
		try
		{
			prepstatement = connection.prepareStatement(CALL_BW_UPD_CELL_FROM_RCSV);
			prepstatement.setInt(1,aiTid);
			prepstatement.setInt(2,1);
			prepstatement.setInt(3, tableId);
			prepstatement.setInt(4, userId);
			prepstatement.execute();
		}
		catch (SQLException sqlexe)
		{
			  throw sqlexe;
		}
		finally
		{
			  try
			  {
					prepstatement.close();
			  }
			  catch (SQLException sqlexe1)
			  {
					throw sqlexe1;
			  }
		}
	}

	public int getPrevColumnId(Connection connection, String name, int aiTblId)
	throws SQLException
	{
		ResultSet resultset		= null;
		PreparedStatement stmt	= null;
		int columnId			= -1;

		try
		{
			stmt = connection.prepareStatement(
				"SELECT DISTINCT BW_COLUMN.ID FROM BW_COLUMN WHERE BW_COLUMN.BW_TBL_ID = ? AND BW_COLUMN.NAME = ?");
			stmt.setInt(1, aiTblId);
			stmt.setString(2, name);

			resultset = stmt.executeQuery();
			while(resultset.next())
			{
				columnId	= resultset.getInt(1);
//				System.out.println("getColumns got the column id: "+columnId +" and the table id: "+aiTblId);
			}
		}
		catch (SQLException e)
		{
			throw e;
		}
		finally
		{
			try
			{
				if(resultset != null)
				{
					resultset.close();
					resultset = null;
				}
				if(stmt != null)
				{
					stmt.close();
					stmt = null;
				}
			}
			catch (SQLException sq2)
			{
				sq2.printStackTrace();
			}
		}

		return columnId;
	}

	// Execute additional Stored Procedure after Periodic shift is completed sucessfully
	// Currently will not pass any parameters and will be a container to call other Stored Procedures if needed

	public void exceutePostPeriodShiftProcedure(Connection connection)throws Exception
	{
		PreparedStatement	prepstatement	= null;

		String CALL_BW_POST_PERIOD_SHIFT_PROCESS = "{ CALL BW_POST_PERIOD_SHIFT_PROCESS()} ";
		try
		{
			prepstatement = connection.prepareStatement(CALL_BW_POST_PERIOD_SHIFT_PROCESS);
			prepstatement.execute();
		}
		catch (SQLException sqlexe)
		{
			  throw sqlexe;
		}
		finally
		{
			  try
			  {
					prepstatement.close();
			  }
			  catch (SQLException sqlexe1)
			  {
					throw sqlexe1;
			  }
		}
	}

	// Execute additional Stored Procedure before Periodic shift is started 
	// Currently will not pass any parameters and will be a container to call other Stored Procedures if needed

	public void exceutePrePeriodShiftProcedure(Connection connection)throws Exception
	{
		PreparedStatement	prepstatement	= null;

		String CALL_BW_PRE_PERIOD_SHIFT_PROCESS = "{ CALL BW_PRE_PERIOD_SHIFT_PROCESS()} ";
		try
		{
			prepstatement = connection.prepareStatement(CALL_BW_PRE_PERIOD_SHIFT_PROCESS);
			prepstatement.execute();
		}
		catch (SQLException sqlexe)
		{
			  throw sqlexe;
		}
		finally
		{
			  try
			  {
					prepstatement.close();
			  }
			  catch (SQLException sqlexe1)
			  {
					throw sqlexe1;
			  }
		}
	}

}