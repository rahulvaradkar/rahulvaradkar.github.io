import java.sql.*;
import java.util.*;
import boardwalk.connection.*;
import boardwalk.table.*;
import com.boardwalk.exception.BoardwalkException;

public class TableTest
{
	public static void main(String args[])
	{
		if (args[0] == null)
		{
			System.out.println("No arguments specified");
		}
		// get the connection
		Connection connection = null;
		try
		{
			DriverManager.registerDriver(new com.microsoft.jdbc.sqlserver.SQLServerDriver());
		}
		catch(Exception e)
		{
			System.out.println("Problem registering JDBC driver");
		}
		String jdbcConnectionString = "jdbc:microsoft:sqlserver://localhost:4809;DatabaseName=BWDEVL1.5;user=BOARDWALK_APPLICATION_USER;password=BOARDWALK_APPLICATION_USER";

		try
		{
			connection =  DriverManager.getConnection(jdbcConnectionString);
			System.out.println("Connection established successfully");
		}
		catch( SQLException sqe )
		{
			System.out.println("There is a Database connection problem");
		}

		// Get an authenticated boardwalk connection
		BoardwalkConnection bwcon = null;
		try
		{
			bwcon = BoardwalkConnectionManager.getBoardwalkConnection(connection, "admin", "admin", -1);
			System.out.println("Successfully obtained authenticated Boardwalk connection");
		}
		catch(BoardwalkException bwe)
		{
			System.out.println("Authentication/Connection Failed");
		}


		if (args[0].equals("-n"))
		{
			if (args[1] == null)
			{
				System.out.println("Please enter valid table name");
			}
			String tblName = args[1];
			// hardcoded stuff
			int collabId = 25;
			int wbid = 50;
			int tableId = -1;
			int tableId2 = -1;
			try
			{
				// create table for lookup purpose
				tableId2 = BoardwalkTableManager.createTable(bwcon, 25, 50, "lkp_"+tblName,
										"Lookup Table for " + tblName);
				System.out.println("Successfully created a lookup table = " + tableId2);

				// Create new columns in the table
				int[] columnIds2 = new int[2];
				columnIds2[0] = BoardwalkTableManager.createColumn(bwcon, tableId2,"ID",-1,-1,-1,"Default1");
				columnIds2[1] = BoardwalkTableManager.createColumn(bwcon, tableId2,"Value",columnIds2[0],-1,-1,"Default2");
				System.out.println("Successfully created columns for lookup table");

				// create new rows in the table
				int[] rowIds2 = new int[3];
				rowIds2[0] = BoardwalkTableManager.createRow( bwcon,tableId2,"1",-1);
				rowIds2[1] = BoardwalkTableManager.createRow( bwcon,tableId2,"2",rowIds2[0]);
				rowIds2[2] = BoardwalkTableManager.createRow( bwcon,tableId2,"3",rowIds2[1]);
				System.out.println("Successfully created rows for lookuptable");

				// create new cells in the lookup table
				Vector cells2 = new Vector();
				InputRowColumnCell ircc1 = new InputRowColumnCell ( rowIds2[0],columnIds2[0],
																	"STRING",
																	"1",
																	null);
				cells2.addElement(ircc1);
				InputRowColumnCell ircc2 = new InputRowColumnCell ( rowIds2[0],columnIds2[1],
																	"STRING",
																	"Choice1",
																	null);
				cells2.addElement(ircc2);
				InputRowColumnCell ircc3 = new InputRowColumnCell ( rowIds2[1],columnIds2[0],
																	"STRING",
																	"2",
																	null);
				cells2.addElement(ircc3);
				InputRowColumnCell ircc4 = new InputRowColumnCell ( rowIds2[1],columnIds2[1],
																	"STRING",
																	"Choice2",
																	null);
				cells2.addElement(ircc4);
				InputRowColumnCell ircc5 = new InputRowColumnCell ( rowIds2[2],columnIds2[0],
																	"STRING",
																	"3",
																	null);
				cells2.addElement(ircc5);
				InputRowColumnCell ircc6 = new InputRowColumnCell ( rowIds2[2],columnIds2[1],
																	"STRING",
																	"Choice3",
																	null);
				cells2.addElement(ircc6);

				try
				{
					BoardwalkTableManager.updateRowColumnCells(bwcon, cells2);
				}
				catch(BoardwalkException bwe)
				{
					bwe.printStackTrace();
				}

				// Create a new table in the boardwalk system
				tableId = BoardwalkTableManager.createTable(bwcon, 25, 50, tblName, tblName);
				System.out.println("Successfully created a table id = " + tableId);

				// Create new columns in the table
				int[] columnIds = new int[3];
				columnIds[0] = BoardwalkTableManager.createColumn(bwcon, tableId,"A",-1,-1,-1,"Default1");
				columnIds[1] = BoardwalkTableManager.createColumn(bwcon, tableId,"B",columnIds[0],-1,-1,"Default2");
				columnIds[2] = BoardwalkTableManager.createColumn(bwcon, tableId,"C",columnIds[1],tableId2,columnIds2[1],"Default3");
				System.out.println("Successfully created columns");

				// create new rows in the table
				int[] rowIds = new int[3];
				rowIds[0] = BoardwalkTableManager.createRow( bwcon,tableId,"1",-1);
				rowIds[1] = BoardwalkTableManager.createRow( bwcon,tableId,"2",rowIds[0]);
				rowIds[2] = BoardwalkTableManager.createRow( bwcon,tableId,"3",rowIds[1]);
				System.out.println("Successfully created rows");

				// create new cells in the table
				Vector cells = new Vector();
				for (int i = 0 ; i < 3 ; i++)
				{
					for (int j = 0; j < 3 ; j++)
					{
						InputRowColumnCell ircc = new InputRowColumnCell ( rowIds[i],
																		   columnIds[j],
																		   "STRING",
																		   ""+i+j,
																		   null);
						cells.addElement(ircc);
					}
				}

				try
				{
					BoardwalkTableManager.updateRowColumnCells(bwcon, cells);
				}
				catch(BoardwalkException bwe)
				{
					bwe.printStackTrace();
				}


				// assign rows in the table to users

			}
			catch (BoardwalkException bwe)
			{
				System.out.println("Error creating the table, maybe table with same name exists");
			}
		}
		else if (args[0].equals("-d"))
		{
			int tableId = -1;
			if (args[1] == null)
			{
				System.out.println("Error in syntax");
			}
			try
			{
				tableId = Integer.parseInt(args[1]);
			}
			catch (Exception e)
			{
				System.out.println("Not a valid table id");
			}
			// delete the table
			try
			{
				BoardwalkTableManager.deleteTable(bwcon, tableId);
				System.out.println("Sucessfully deleted the table from the database");
			}
			catch (BoardwalkException bwe)
			{
				System.out.println("Error deleting table, maybe the tableid is incorrect or there is a baseline");
			}
		}
		else if (args[0].equals("-ar"))
		{
			int tableId = -1;
			int rowId = -1;
			String userName = null;
			if (args[1] == null || args[2] == null || args[3] == null)
			{
				System.out.println("Error in syntax");
			}
			try
			{
				tableId = Integer.parseInt(args[1]);
				rowId = Integer.parseInt(args[2]);
				userName = args[3];
			}
			catch (Exception e)
			{
				System.out.println("Not a valid input");
			}
			// delete the table
			try
			{
				BoardwalkTableManager.changeRowOwner(
									bwcon,
    							 	rowId,
    							 	userName
    						   		);
				System.out.println("Sucessfully changed owner");
			}
			catch (BoardwalkException bwe)
			{
				System.out.println("Error assigning row");
			}
		}
		else if (args[0].equals("-g"))
		{
			int tableId = -1;
			if (args[1] == null)
			{
				System.out.println("Error in syntax");
			}
			try
			{
				tableId = Integer.parseInt(args[1]);
			}
			catch (Exception e)
			{
				System.out.println("Not a valid table id");
			}
			// get the table contents
			try
			{
				BoardwalkTableContents bwtbcon = BoardwalkTableManager.getTableContents(
																			bwcon, tableId);
				System.out.println("Sucessfully fetched the table from the database");

				// get the columns
				System.out.print("\t\t");
				Vector columns = bwtbcon.getColumns();
				Iterator icols = columns.iterator();
				while (icols.hasNext())
				{
					BoardwalkColumn c = (BoardwalkColumn)icols.next();
					System.out.print(c.getName() + ":" + c.getId() + ",");
				}
				System.out.print("\n");
				// get the rows and cells
				Vector rows = bwtbcon.getRows();
				Iterator irows = rows.iterator();
				while (irows.hasNext())
				{
					BoardwalkRow r = (BoardwalkRow)irows.next();
					System.out.print(r.getId() + ":" + r.getOwner()+ "\t");
					Vector cells = bwtbcon.getCellsForRow(r.getId());
					Iterator icells = cells.iterator();
					while (icells.hasNext())
					{
						BoardwalkCell cell = (BoardwalkCell)icells.next();
						System.out.print(cell.getStringValue() + ",");
					}
					System.out.print("\n");
				}

			}
			catch (BoardwalkException bwe)
			{
				System.out.println("Error fetching table");
			}
		}
		else if (args[0].equals("-c"))
		{
			int tableId = -1;
			if (args[1] == null)
			{
				System.out.println("Error in syntax");
			}
			try
			{
				tableId = Integer.parseInt(args[1]);
			}
			catch (Exception e)
			{
				System.out.println("Not a valid table id");
			}
			// get the table contents
			try
			{
				BoardwalkTableContents bwtbcon = BoardwalkTableManager.getTableContents(
											bwcon,
											tableId);
				System.out.println("Sucessfully fetched the changed cells from the database");

				// get the columns
				Vector columns = bwtbcon.getColumns();
				Iterator icols = columns.iterator();
				while (icols.hasNext())
				{
					BoardwalkColumn c = (BoardwalkColumn)icols.next();
				}
				System.out.print("\n");
				// get the rows and cells
				Vector rows = bwtbcon.getRows();
				Iterator irows = rows.iterator();
				while (irows.hasNext())
				{
					BoardwalkRow r = (BoardwalkRow)irows.next();
					Vector cells = bwtbcon.getCellsForRow(r.getId());
					Iterator icells = cells.iterator();
					while (icells.hasNext())
					{
						BoardwalkCell cell = (BoardwalkCell)icells.next();

						// get the change log for this cell
						System.out.println("****Change Log for Cell id = " + cell.getId()+"****");
						long sDate = 0; // all the cell version from t=0
						long eDate = (new java.util.Date()).getTime();		// to now
						Vector cvl = BoardwalkTableManager.getCellVersionsBetweenTimes(
																	bwcon,
																	cell.getId(),
																	sDate,
																	eDate);
						Iterator cvli = cvl.iterator();
						while (cvli.hasNext())
						{
							BoardwalkCell bc = (BoardwalkCell)cvli.next();
							System.out.println("Value changed to " + bc.getStringValue() +
												" by " + bc.getTransaction().getUserName() +
												" on " + new java.util.Date(bc.getTransaction().getTime()));
						}
					}
				}
			}
			catch (BoardwalkException bwe)
			{
				System.out.println("Error comparing table, maybe the tableid is incorrect");
			}
		}
		else if (args[0].equals("-t"))
		{
			int tableId = -1;
			if (args[1] == null)
			{
				System.out.println("Error in syntax");
			}
			try
			{
				tableId = Integer.parseInt(args[1]);
			}
			catch (Exception e)
			{
				System.out.println("Not a valid table id");
			}
			try
			{
				Vector txList = BoardwalkTableManager.getTransactionListBetweenTimes(
														bwcon,
														tableId,
														0,
														Calendar.getInstance().getTime().getTime());
				Iterator txi = txList.iterator();
				while (txi.hasNext())
				{
					BoardwalkTransaction tx = (BoardwalkTransaction)txi.next();
					System.out.println(
							tx.getId() + ":" +
							tx.getUserName() + ":" +
							new java.util.Date(tx.getTime()) + ":" +
							tx.getAction());
				}
			}
			catch (BoardwalkException bwe)
			{
				System.out.println("Problem fetching transactions");
			}
		}
		else if (args[0].equals("-ctx2"))
		{
			int tableId = -1;
			int stx = -1;
			int etx = -1;
			if (args[1] == null || args[2] == null || args[3] == null)
			{
				System.out.println("Error in syntax");
			}
			try
			{
				tableId = Integer.parseInt(args[1]);
				stx = Integer.parseInt(args[2]);
				etx = Integer.parseInt(args[3]);
			}
			catch (Exception e)
			{
				System.out.println("Not a valid transaction ids");
			}
			try
			{
				BoardwalkTableContents bwtbcon =
									BoardwalkTableManager.compareTableBetweenTransactions(
																bwcon,
																tableId,
																stx,
																etx);
				System.out.println("Sucessfully fetched the changed cells from the database");
				Vector txList = BoardwalkTableManager.getTransactionListBetweenTransactions(
														bwcon,
														tableId,
														stx,
														etx);
				BoardwalkTransaction tx1 = (BoardwalkTransaction)txList.firstElement();
				BoardwalkTransaction tx2 = (BoardwalkTransaction)txList.lastElement();
				// get the columns
				Vector columns = bwtbcon.getColumns();
				Iterator icols = columns.iterator();
				while (icols.hasNext())
				{
					BoardwalkColumn c = (BoardwalkColumn)icols.next();
				}
				System.out.print("\n");
				// get the rows and cells
				Vector rows = bwtbcon.getRows();
				Iterator irows = rows.iterator();
				while (irows.hasNext())
				{
					BoardwalkRow r = (BoardwalkRow)irows.next();
					Vector cells = bwtbcon.getCellsForRow(r.getId());
					if (cells != null) // could be null if no cells changed in row
					{
						Iterator icells = cells.iterator();
						while (icells.hasNext())
						{
							BoardwalkCell cell = (BoardwalkCell)icells.next();

							// get the change log for this cell
							System.out.println("****Change Log for Cell id = " + cell.getId()+"****");

							Vector cvl = BoardwalkTableManager.getCellVersionsBetweenTimes(
																		bwcon,
																		cell.getId(),
																		tx1.getTime(),
																		tx2.getTime());
							Iterator cvli = cvl.iterator();
							while (cvli.hasNext())
							{
								BoardwalkCell bc = (BoardwalkCell)cvli.next();
								System.out.println("Value changed to " + bc.getStringValue() +
													" by " + bc.getTransaction().getUserName() +
													" on " + new java.util.Date(bc.getTransaction().getTime()));
							}
						}
					}
				}
			}
			catch (BoardwalkException bwe)
			{
				System.out.println("Error comparing table, maybe the tableid is incorrect");
			}
		}
		else if (args[0].equals("-ctx1"))
		{
			int tableId = -1;
			int stx = -1;
			if (args[1] == null || args[2] == null)
			{
				System.out.println("Error in syntax");
			}
			try
			{
				tableId = Integer.parseInt(args[1]);
				stx = Integer.parseInt(args[2]);
			}
			catch (Exception e)
			{
				System.out.println("Not a valid transaction ids");
			}
			try
			{
				BoardwalkTableContents bwtbcon =
									BoardwalkTableManager.getChangesForTransaction(
																bwcon,
																tableId,
																stx);
				System.out.println("Sucessfully fetched the changed cells from the database");

				// get the columns
				Vector columns = bwtbcon.getColumns();
				Iterator icols = columns.iterator();
				while (icols.hasNext())
				{
					BoardwalkColumn c = (BoardwalkColumn)icols.next();
				}
				System.out.print("\n");
				// get the rows and cells
				Vector rows = bwtbcon.getRows();
				Iterator irows = rows.iterator();
				while (irows.hasNext())
				{
					BoardwalkRow r = (BoardwalkRow)irows.next();
					Vector cells = bwtbcon.getCellsForRow(r.getId());
					if (cells != null) // could be null if no cells changed in row
					{
						Iterator icells = cells.iterator();
						while (icells.hasNext())
						{
							BoardwalkCell cell = (BoardwalkCell)icells.next();

							// get the change log for this cell
							System.out.println("****Change Log for Cell id = " + cell.getId()+"****");
							long sDate = 0; // all the cell version from t=0
							long eDate = (new java.util.Date()).getTime();		// to now
							Vector cvl = BoardwalkTableManager.getCellVersionsBetweenTimes(
																		bwcon,
																		cell.getId(),
																		sDate,
																		eDate);
							Iterator cvli = cvl.iterator();
							while (cvli.hasNext())
							{
								BoardwalkCell bc = (BoardwalkCell)cvli.next();
								System.out.println("Value changed to " + bc.getStringValue() +
													" by " + bc.getTransaction().getUserName() +
													" on " + new java.util.Date(bc.getTransaction().getTime()));
							}
						}
					}
				}
			}
			catch (BoardwalkException bwe)
			{
				System.out.println("Error comparing table, maybe the tableid is incorrect");
			}
		}
		else
		{
			System.out.println("Invalid arguments");
		}

	}

};
