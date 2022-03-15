package boardwalk.table;

import java.sql.*;
import java.util.*;
import boardwalk.connection.*;
import boardwalk.table.*;
import java.io.*;
import com.boardwalk.exception.BoardwalkException;
import com.boardwalk.table.*;
import com.boardwalk.database.*;
import boardwalk.common.*;

public class APP_PeriodicColumnManager extends BoardwalkPeriodicColumnManager
{
	Vector previousWeeks;
	Vector newWeeks;
	Vector columnsToModify;

	String previousWeek;
	String currentWeek;

	HashMap weeksByPeriod;
	HashMap tableAccess;
	String previousPeriod;
	String currentPeriod;

	ArrayList previousStateCells;
	ArrayList currentStateCells;

	ArrayList prevStateColumns;
	ArrayList CurrStateColumns;
	ArrayList CurrStateColumnsIds;
	ArrayList Instructions;
	HashMap	CurrStatePrdCols;

	ArrayList cellToUpdate;

	public APP_PeriodicColumnManager()
	{
		previousWeeks	= new Vector();
		newWeeks		= new Vector();
		columnsToModify	= new Vector();

		previousWeek	= null;
		currentWeek		= null;
		previousPeriod	= null;
		currentPeriod	= null;

		weeksByPeriod	= new HashMap();
		tableAccess		= new HashMap();

		previousStateCells	= new ArrayList();
		currentStateCells	= new ArrayList();

		prevStateColumns	= new ArrayList();
		CurrStateColumns	= new ArrayList();
		CurrStateColumnsIds	= new ArrayList();
		CurrStatePrdCols	= new HashMap();

		Instructions		= new ArrayList();
		cellToUpdate		= new ArrayList();
	}

	// Set the required collections for processing the tables
	public boolean processState(Connection connection, int aiStateTblId, String asCurrentState, String asNextState, String asTableIdString)
	{
		// Assumptions : Previous state 'State', Current State 'State2'
		// get the change state for the above two states
		// Compare it with each other to identify the columns to be added and deleted
		try
		{
			previousStateCells	= PeriodicColumnStateTableReader.getState(connection, aiStateTblId, asCurrentState);
			currentStateCells	= PeriodicColumnStateTableReader.getState(connection, aiStateTblId, asNextState);

			boolean addColsToPrev = false;

			for(int i=0; i < previousStateCells.size(); i++)
			{
				NewCell pCell = (NewCell) previousStateCells.get(i);
				//System.out.println("newcell" + pCell.getColName() + ":" + pCell.getStringValue());

				if( addColsToPrev && pCell.getColName().equals("Status") )
				{
					addColsToPrev = false;
					break;
				}

				if(addColsToPrev && pCell.getStringValue().length() > 0 )
					prevStateColumns.add(pCell.getStringValue());

				if(pCell.getColName().equals("Instruction") && pCell.getStringValue().equals("Change Set"))
					addColsToPrev = true;
			}

			if(PeriodicColumnStateTableReader.validateState(connection, prevStateColumns, asTableIdString) == false)
			{
				System.out.println("Invalid Current State, Please check the States");
				return false;
			}

			int instCnt = 0;
			boolean newInstruction	= false;
			boolean addColsToCurrent		= false;

			boolean instDefAccess	= false;
			boolean instCopyValues	= false;
			boolean instCopyFormula	= false;
			boolean instDefValue	= false;
			boolean instDefFormula	= false;
			boolean instDelValue	= false;
			boolean instDelFormula	= false;
			boolean instRename		= false;

			String  PrevColName		= "";

			for(int i=0; i < currentStateCells.size(); i++)
			{
				NewCell pCell = (NewCell) currentStateCells.get(i);

				if( addColsToCurrent && pCell.getColName().equals("Status") )
					addColsToCurrent = false;

				if(addColsToCurrent)
				{
					CurrStateColumns.add(pCell.getStringValue());
//					CurrStateColumnsIds.add(pCell.getColumnId());Before for version 1.5
					CurrStateColumnsIds.add(new Integer(pCell.getColumnId()));
					BoardwalkPeriodicColumn bc  = new BoardwalkPeriodicColumn(pCell.getColumnId(), pCell.getStringValue(),PrevColName, i+1);
					CurrStatePrdCols.put(new Integer(pCell.getColumnId()), bc);
					columnsToModify.add(bc);
					PrevColName = pCell.getStringValue();
				}

				if( pCell.getColName().equals("Instruction") && pCell.getStringValue().equals("Change Set") )
					addColsToCurrent = true;

				if( newInstruction && pCell.getColName().equals("Status") )
					newInstruction = false;

				if( pCell.getColName().equals("Status") )
					cellToUpdate.add(pCell);

				if( newInstruction == false && pCell.getColName().equals("Instruction") )
				{
					newInstruction = true;

					instDefAccess	= false;
					instCopyValues	= false;
					instCopyFormula	= false;
					instDefValue	= false;
					instDefFormula	= false;
					instDelValue	= false;
					instDelFormula	= false;
					instRename		= false;

					if(pCell.getStringValue().equals("Default Access") )
						instDefAccess = true;

					if(pCell.getStringValue().equals("Copy Values") )
						instCopyValues = true;

					if(pCell.getStringValue().equals("Copy Formulae") )
						instCopyFormula = true;

					if(pCell.getStringValue().equals("Default Values") )
						instDefValue = true;

					if(pCell.getStringValue().equals("Default Formulae") )
						instDefFormula = true;

					if(pCell.getStringValue().equals("Delete Values") )
						instDelValue = true;

					if(pCell.getStringValue().equals("Delete Formulae") )
						instDelFormula = true;

					if(pCell.getStringValue().equals("Rename") )
						instRename = true;
				}

				if(newInstruction)
				{
					if(addColsToCurrent == false)
					{
						BoardwalkPeriodicColumn bc  = (BoardwalkPeriodicColumn) CurrStatePrdCols.get(new Integer(pCell.getColumnId()));

						if(bc != null)
						{
							if(pCell.getColumnId() == bc.getColumnId())
							{
								if(!BoardwalkUtility.checkIfNullOrBlank(pCell.getStringValue()))
								{
									if(instDefAccess)
										bc.setAccess(pCell.getStringValue());

									if(instCopyValues)
										bc.setCopyColName(pCell.getStringValue());

									if(instCopyFormula)
										bc.setCopyFormulaColName(pCell.getStringValue());

									if(instDefValue)
										bc.setDefaultValue(pCell.getStringValue());

									if(instDefFormula)
										bc.setDefaultFormula(pCell.getStringValue());

									if(instDelValue)
										bc.setDeleteValue(pCell.getStringValue());

									if(instDelFormula)
										bc.setDeleteFormulaValue(pCell.getStringValue());

									if(instRename)
										bc.setRenameValue(pCell.getStringValue());
								}
							}

						}
					}
				}
			}

			boolean lbColFound = false;

			for(int i=0 ; i < prevStateColumns.size(); i++)
			{
				// Compare Previous and Current columns to get the columns to be removed
				lbColFound = false;
				for(int j=0 ; j < CurrStateColumns.size(); j++)
				{
					if((prevStateColumns.get(i).toString().trim()).equals(CurrStateColumns.get(j).toString().trim()) )
					{
						lbColFound = true;
						break;
					}
				}
				if(lbColFound == false)
				{
					BoardwalkPeriodicColumn bc  = new BoardwalkPeriodicColumn(0,prevStateColumns.get(i).toString().trim(),"",-1);
					previousWeeks.add(bc);
				}
			}

			for(int i=0 ; i < CurrStateColumns.size(); i++)
			{
				// Compare Current and Previous columns to get the columns to be added.

				lbColFound = false;
				for(int j=0 ; j < prevStateColumns.size(); j++)
				{
					if((CurrStateColumns.get(i).toString().trim()).equals(prevStateColumns.get(j).toString().trim()) )
					{
						lbColFound = true;
						break;
					}
				}
				if(lbColFound == false)
				{
					BoardwalkPeriodicColumn bc  = (BoardwalkPeriodicColumn) CurrStatePrdCols.get(new Integer( CurrStateColumnsIds.get(i).toString()));
					newWeeks.add(bc);
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
		}

		return true;
	}

	public String getNewPeriod()
	{
		return currentWeek;
	}

	public String getPreviousPeriod()
	{
		return previousWeek;
	}

	public Vector getNewPeriods()
	{
		return newWeeks;
	}

	public Vector getPreviousPeriods()
	{
		return previousWeeks;
	}

	public Vector getColumnsTomodify()
	{
		return columnsToModify;
	}

	public ArrayList getStatusCells()
	{
		return cellToUpdate;
	}

	public static void main(String[] args)
	{
		int liStateTableId		= -1;
		String lsCurrStateName	= "";
		String lsNextStateName	= "";
		String lsTableIdsForPS	= "";

		if(args.length <=0 )
			return;
		String lsPropertyPath	= args[0];

		liStateTableId	= Integer.parseInt(args[1]);
		lsCurrStateName	= args[2];
		lsNextStateName	= args[3];
		lsTableIdsForPS	= args[4];

		Properties configProp		= new Properties();
		BoardwalkConnection bwcon	= null;
		Connection connection		= null;
		AppDbConnector objAppDbConn	= null;

		try
		{
			InputStream is = new FileInputStream(lsPropertyPath);
			configProp.load(is);
			System.out.println(" App: boardwalk.properties Properties file loaded.");

			objAppDbConn = new AppDbConnector(configProp);
			bwcon = objAppDbConn.getConnection("");
			connection = bwcon.getConnection();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.out.println(" App: Can't read boardwalk.properties properties file 1."+lsPropertyPath);
			return;
		}

		try
		{
			System.out.println("Starting Periodic Columns Realignment");

			APP_PeriodicColumnManager pcm = new APP_PeriodicColumnManager();

			// set new period set for short term plans
			// 9 is table Id and 2 is user Id
			if(pcm.processState(connection, liStateTableId, lsCurrStateName, lsNextStateName, lsTableIdsForPS))
			{
				pcm.setNewPeriods(connection, objAppDbConn.getUserId(), lsTableIdsForPS);
				System.out.println("Done with Periodic Columns Realignment");
			}
			else
				System.out.println("Errors in processing Current State, Please Check ");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				connection.close();
			}
			catch(SQLException sqe)
			{
				sqe.printStackTrace();
			}
		}
	}

}