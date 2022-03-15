package servlets;
/*
 *  This presents a list of collaboration available to a user
 */
import java.io.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;
import java.text.*;


import com.boardwalk.database.DatabaseLoader;
import com.boardwalk.exception.NeighborhoodException;
import com.boardwalk.exception.SystemException;
import com.boardwalk.collaboration.CollaborationManager;
import com.boardwalk.collaboration.Collaboration;
import com.boardwalk.collaboration.CollaborationTreeNode;
import com.boardwalk.whiteboard.WhiteboardManager;
import com.boardwalk.whiteboard.WhiteboardTreeNode;
import com.boardwalk.whiteboard.*;
import com.boardwalk.neighborhood.NeighborhoodManager;
import com.boardwalk.database.*;
import com.boardwalk.table.*;
import com.boardwalk.excel.*;
import com.boardwalk.user.UserManager;
import com.boardwalk.member.Member;
import com.boardwalk.exception.*;
import servlets.MyTables;

import java.util.Date;
import java.sql.*;                  // JDBC package
import javax.sql.*;                 // extended JDBC packa

public class xlGetRowUpdates extends xlService implements SingleThreadModel
{

	public final static String Seperator = new Character((char)1).toString();
	public final static String ContentDelimeter = new Character((char)2).toString();
	StringTokenizer st;
	int userId;
	String userName;
	String userPassword;
	int nhId;
	int  memberId;
	int tableId;
	String m_ViewPreference;
	String m_SortPreference;
	String fileSavedAt;
	long rowCount;
	long columnCount;
	long transactionId;
	long local_offset;
	long difference_in_MiliSec;

	String m_period;
	String m_StartDate;
	String m_EndDate;
	String rowIds;
	String columnIds;
	String baseLine;

	HttpServletRequest req;
	HttpServletResponse res;

    public void service (HttpServletRequest request, HttpServletResponse response) 	throws ServletException, IOException
    {
		req = request;
		res = response;

        BoardwalkMessages bwMsgs = new BoardwalkMessages();

		StringBuffer responseToUpdate = new StringBuffer();
        String responseBuffer = null;

		String buf = getRequestBuffer(request).toString();
		//System.out.println("Data from client" + buf);
		st = new StringTokenizer( buf );

		String wrkstr;

		wrkstr = st.nextToken (Seperator);
		tableId = Integer.parseInt(wrkstr);

		wrkstr = st.nextToken (Seperator);
		m_period = wrkstr;

		wrkstr = st.nextToken (Seperator);
		m_StartDate = wrkstr;

		wrkstr = st.nextToken (Seperator);
		m_EndDate = wrkstr;

		wrkstr = st.nextToken (Seperator);
		rowIds = wrkstr;

		wrkstr = st.nextToken (Seperator);
		columnIds = wrkstr;

		wrkstr = st.nextToken (Seperator);
		baseLine = wrkstr;

		wrkstr = st.nextToken (Seperator);
		local_offset = Long.parseLong(wrkstr);


		Calendar cal_GMT = Calendar.getInstance(TimeZone.getTimeZone("GMT"));

		long server_Millis = cal_GMT.getTimeInMillis();

		difference_in_MiliSec = local_offset - server_Millis;

		System.out.println("Local Server (gmt) in miliSeconds is " + server_Millis );

		System.out.println("The difference in Server and Clietnis " + (local_offset - server_Millis ));

		getRowUpdates();
    }

	public void getRowUpdates() throws ServletException, IOException
	{
		long endDate;
		long startDate;

		java.util.Date d = new java.util.Date();
		endDate = d.getTime();
		startDate = 0;

		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		cal.setTime(d);

		if (m_period.equals("Week"))
		{
			cal.add(Calendar.DATE, -7);
			startDate = cal.getTime().getTime();
		}
		else if (m_period.equals("Month"))
		{
			cal.add(Calendar.MONTH, -1);
			startDate = cal.getTime().getTime();
		}
		else if (m_period.equals("Quarter"))
		{
			cal.add(Calendar.MONTH, -3);
			startDate = cal.getTime().getTime();
		}
		else if (m_period.equals("Year"))
		{
			cal.add(Calendar.YEAR, -1);
			startDate = cal.getTime().getTime();
		}
		else if (m_period.equals("Custom") && m_EndDate != null && m_StartDate != null)
		{
			endDate = Long.parseLong(m_EndDate);
			startDate = Long.parseLong(m_StartDate);
			startDate = startDate - difference_in_MiliSec;
			endDate = endDate - difference_in_MiliSec;
		}


		System.out.println("++++++++++++++++ startdate = "  + startDate + " end date = " + endDate );

		System.out.println("Number of modified row cell versions between startdate = "  + startDate + " end date = " + endDate + " Row ids = " + rowIds + " baseline = "+baseLine);

		Connection connection = null;
		try
		{
			DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
			connection = databaseloader.getConnection();

			Vector columnList			= new Vector();
			ArrayList rowCellVersions	= new ArrayList();
			Vector lsLatestRows			= new Vector();
			ArrayList laReportTable		= new ArrayList();

			//ArrayList OrgRow = new ArrayList();
			try
			{
				// To get the column details...  

				if (baseLine.equals("-1"))
				{
					TableColumnInfo tci = ColumnManager.getTableColumnInfo( connection, tableId, Integer.parseInt(baseLine), userId, memberId, -1, "");

					columnList = tci.getColumnVector();
					System.out.println("++++++++ get the column details..." + columnList.size());

					// To get the modified row cells
					rowCellVersions = TableManager.getRowVersions( connection, rowIds, startDate, endDate, columnIds, baseLine, tableId, userId, memberId );
					

					System.out.println("++++++++++ get the modified row cells... " + rowCellVersions.size());
				}
				else
				{
					TableColumnInfo tci = ColumnManager.getTableColumnInfo( connection, tableId, -1, userId, memberId, -1, "");

					columnList = tci.getColumnVector();
					System.out.println("++++++++ get the column details..." + columnList.size());

					// To get the modified row cells
					rowCellVersions = TableManager.getRowVersions( connection, rowIds, startDate, endDate, columnIds, baseLine, tableId, userId, memberId );
					

					System.out.println("++++++++++ get the modified row cells... " + rowCellVersions.size());
				
				}

				lsLatestRows = RowManager.getRowContents( connection, rowIds, columnIds, tableId, userId, memberId );

				int liNoRows		= lsLatestRows.size();
				int modCount		= 0;
				int liRowId			= 0;
				int col				= 0;
				int liCellsinRow	= 0;		// # OF CELLS IN A ROW
				Transaction trans	= null;
				VersionedCell modCell = null;
				boolean lbFirstRow	= true;
				int txid			= 0;

				Vector RowCells				= new Vector();
				ArrayList laReportRowsCells	= new ArrayList();


				System.out.println("#### Table "+liNoRows);
				for(int row = 0; row < liNoRows ; row++)
				{
					RowCells	= (Vector) lsLatestRows.get(row);
					System.out.println("#### No of Cells in Org Row "+RowCells.size());
					System.out.println("#### No of Cells Mod in Org Row "+rowCellVersions.size());

					liCellsinRow = RowCells.size();
					lbFirstRow = true;

					VersionedCell cell	= (VersionedCell) RowCells.elementAt(0);
					liRowId				= cell.getRowId();

					while(modCount < rowCellVersions.size())
					{
						//System.out.println("#### mod cell index  "+modCount);
						boolean skipRow = false;
						boolean formulaModified = false;
						if(modCount < rowCellVersions.size())
								modCell	= (VersionedCell) rowCellVersions.get(modCount);
						trans	= modCell.getTransaction();
						txid	= trans.getId();
						long CreatedOn = trans.getCreatedOnTime();

						// Processing all cells in a ROW
						for(col = 0; col < liCellsinRow; col++)
						{
							if(modCount < rowCellVersions.size())
								modCell		= (VersionedCell) rowCellVersions.get(modCount);

							trans	= modCell.getTransaction();

//							System.out.println("#### col index "+col);
//							System.out.println("#### in loop modCount "+modCount);
//							System.out.println("#### txid "+txid);
//							System.out.println("#### trans.getId() "+trans.getId());

							cell	= (VersionedCell) RowCells.elementAt(col);
							
//							System.out.println("cell details ==> row id ->"+ cell.getRowId() + ", column id -> " + cell.getColumnId());
//							System.out.println("Mod-cell details ==> row id ->"+ modCell.getRowId() + ", column id -> " + modCell.getColumnId());
//							System.out.println("................................................................................");

							if(liRowId == modCell.getRowId() && txid == trans.getId())
							{
//								System.out.println("##### latest cell "+cell.getId());
//								System.out.println("##### modifed cell "+modCell.getId() );

								if (cell.getId() == modCell.getId() )
								{
									String lsDescription =	"";
									//lsDescription =			"changed value from '"+
									//						cell.getValueAsString() +
									//						"' to '"+
									//						modCell.getValueAsString()+"'";

									if(cell.getFormula().equals(modCell.getFormula()))
									{
										formulaModified = false;
										lsDescription =	"changed value from '"+
													cell.getValueAsString() +
													"' to '"+
													modCell.getValueAsString()+"'";
									}
									else
									{
										formulaModified = true;
										lsDescription = "changed value from '"+
													cell.getValueAsString() +
													"' to '"+
													modCell.getValueAsString()+
													"'..  changed Formula from '"+
													cell.getFormula() +
													"' to '"+
													modCell.getFormula()+"'";
									}

									modCell.setDescription(lsDescription);
									modCell.setformulaModified(formulaModified);
									VersionedCell copy = copyCell(modCell);
									RowCells.setElementAt(copy,col);
									laReportRowsCells.add(modCell);
									modCount++;
//									System.out.println("modCell "+modCell);
//									System.out.println("copy "+copy);
//									System.out.println("$$$ "+modCell.getValueAsString()+"\t "+lsDescription);

									if(modCount < rowCellVersions.size())
										modCell = (VersionedCell) rowCellVersions.get(modCount);
								}
								else
								{
									cell.setDescription("");
									laReportRowsCells.add(cell);
									//System.out.print("$$$ "+cell.getValueAsString()+"\t ");
								}
							}
							else
							{
								cell.setDescription("");
								laReportRowsCells.add(cell);
								//System.out.print("$$$ "+cell.getValueAsString()+"\t ");
							}
						}
						lbFirstRow = false;
						//System.out.println("");
						//System.out.println(modCell.getRowId() +" != "+liRowId);
						if(modCell.getRowId() != liRowId)
						{
							laReportTable.add(laReportRowsCells);
							laReportRowsCells = new ArrayList();
							break;
						}
					}
					if(laReportRowsCells.size() > 0 )
						laReportTable.add(laReportRowsCells);

				 } // transactions
				//System.out.println("get the modified row cells");
				laReportTable = filterRows(laReportTable, liCellsinRow, startDate, endDate);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}


			String[] arr_ColumnArray = getArrayFromStrTok(columnIds, ",") ;

			StringBuffer responseToUpdate = new StringBuffer();
			String responseBuffer = null;
			responseToUpdate.append("Success" + ContentDelimeter);

			int liColumnCount = arr_ColumnArray.length;

			if(liColumnCount < 1)
				liColumnCount = columnList.size(); 

			System.out.println("######## No. of columns in report "+liColumnCount);
			String rowColorCode = "#eeeeee";
			
			int DummyTxid		= 0;

			Vector RowCells = new Vector();
			Vector ModRowCells = new Vector();

			// Printing the table header ie. COLUMN NAMES
			System.out.println("++++++++= columnList.size = " + columnList.size());

			int liColumnIdIndex = 0;
			for(int i = 0; i < columnList.size(); i++)
			{
				Column bc = (Column)columnList.get(i);

				if(liColumnIdIndex < liColumnCount  && Integer.parseInt(arr_ColumnArray[liColumnIdIndex]) == bc.getId())
				{
					//System.out.println("1. ++++++++++ colName...... +++++ " + bc.getColumnName());
					responseToUpdate.append( bc.getColumnName() + Seperator);
					liColumnIdIndex++;
				}
				else
				{
					if(liColumnCount == 0)
					{
						//System.out.println("2. ++++++++++ colName...... +++++ " + bc.getColumnName());
						responseToUpdate.append( bc.getColumnName() + Seperator);
					}
				}
			}
			responseToUpdate.append( "Modified By" + Seperator);
			responseToUpdate.append( "Modified Date" + Seperator);
			responseToUpdate.append( "Comments");

			if(!baseLine.equals("-1"))
			{
				responseToUpdate.append(Seperator + "Baseline" + Seperator);
				responseToUpdate.append( "Baseline Description" + ContentDelimeter);
			}
			else
				responseToUpdate.append(ContentDelimeter);

			// END OF COLUM NAMES

			VersionedCell printCell = null;
			int TotalRows = laReportTable.size();
			Transaction trans1 = null;
//			int cellIndex = 0;

			int row_color				= 0;
			
			for(int i = 0; i < TotalRows; i++)
			{
				ArrayList laReportRowsCells = (ArrayList)laReportTable.get(i);
				int rows = laReportRowsCells.size()/liColumnCount;
				int index = laReportRowsCells.size();

				if (row_color==0)
					row_color=1;
				else
					row_color=0;

				//System.out.println("######## No. of cells in a row in report "+index);
				for(int j = 0; j < rows; j++)
				{			

					responseToUpdate.append( row_color + Seperator);

					//System.out.println("row "+j+"\t");
					String lsCellValue = "";
					String lsModifiedBy = "";
					String lsModifiedOn = "";
					long lngModifiedDateTime = 0 ;
					String lsComments = "";
					String lsBaseline = "";
					String lsBaselineDesc = "";
					String xlComment = "";
					index = index - liColumnCount;
					for(int col = 0; col < liColumnCount; col++)
					{
						//System.out.print("index+col "+(index+col)+"\t");
						printCell = (VersionedCell) laReportRowsCells.get(index+col);

						trans1			= printCell.getTransaction();	
						int liRowNum	= printCell.getRowId();
						if(!printCell.getDescription().equals(""))
						{		
							lsModifiedBy	= trans1.getCreatedByUserAddress();			
							SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy hh:mm:ssa");
							lngModifiedDateTime = trans1.getCreatedOnTime() + difference_in_MiliSec;
							//lsModifiedOn	= df.format(new java.sql.Timestamp(localTimeStamp));
							lsComments = trans1.getComment();
							if(lsComments == null)
								lsComments = "";
							lsBaseline = printCell.getTableName();
							lsBaselineDesc = printCell.getbaselineDesc();
						}
						lsCellValue	= printCell.getValueAsString();
						xlComment = printCell.getDescription();

						responseToUpdate.append( lsCellValue + Seperator);
						responseToUpdate.append( xlComment + Seperator);

						//cellIndex++;
					}	// END OF ALL COLUMNS DATA + EXCEL COMMENT			

					responseToUpdate.append(lsModifiedBy + Seperator);
					responseToUpdate.append("" + Seperator);
					//responseToUpdate.append(lsModifiedOn +  Seperator);
					responseToUpdate.append(lngModifiedDateTime +  Seperator);
					responseToUpdate.append("" + Seperator);
					responseToUpdate.append( lsComments + Seperator );
					responseToUpdate.append("" + Seperator);

					if(!baseLine.equals("-1"))
					{
						responseToUpdate.append( lsBaseline + Seperator);
						responseToUpdate.append("" + Seperator);
						responseToUpdate.append( lsBaselineDesc + Seperator);
						responseToUpdate.append("" + Seperator);
					}

					responseToUpdate.append( ContentDelimeter );

//					responseToUpdate.append( updatedBy + Seperator);
//					responseToUpdate.append( t.getCreatedOnTime() + difference_in_MiliSec +  Seperator);
//					responseToUpdate.append( comment );
//					responseToUpdate.append(ContentDelimeter);

				}		// END OF ROW 
			}

			if (responseToUpdate.length() > 0)
				responseToUpdate.deleteCharAt(responseToUpdate.length()-1);

			//System.out.println(" responseToIpdate is +++++++++++ " + responseToUpdate);

			responseBuffer = responseToUpdate.toString();
			//System.out.println("Response = " + responseBuffer);
			commitResponseBuffer(responseBuffer, res);

		}
		catch (SQLException sql)
		{
			sql.printStackTrace();
			return;
		}
		finally
		{
		  try
		  {
			if ( connection != null )
				connection.close();
		  }
		  catch ( SQLException sql )
		  {
			sql.printStackTrace();
		  }
		}

//		sc.getRequestDispatcher("/jsp/collaboration/tables/rowVersions.jsp"). forward(req,res);
	}


	public VersionedCell copyCell(VersionedCell a_cell)
	{
		VersionedCell newCell = new VersionedCell (a_cell.getId(), a_cell.getColumnId(), a_cell.getColumnName(),
								a_cell.getRowId(), a_cell.getType(), a_cell.getValueAsString(), a_cell.getIntValue(),
    							a_cell.getDoubleValue(), a_cell.getTableValue(), a_cell.getTableName(),
								a_cell.getTransaction(), a_cell.getFormula());
		newCell.setDescription(a_cell.getDescription());
		return newCell;
	}


	public ArrayList filterRows(ArrayList asTable, int aiColumnLength, long alStartDate, long alEndDate)
	{
		VersionedCell Cell = null;
		Transaction trans1 = null;
		ArrayList laFinalTable	= new ArrayList();
		ArrayList laNewRowCells = new ArrayList();
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		java.sql.Timestamp startDate	= new java.sql.Timestamp(alStartDate);
		java.sql.Timestamp endDate		= new java.sql.Timestamp(alEndDate);
		int liCellinTable = asTable.size();

		for(int i = 0; i < liCellinTable; i++)
		{
			ArrayList laRowsCells = (ArrayList) asTable.get(i);
			int rows = laRowsCells.size()/aiColumnLength;
			//System.out.println("######## No. of cells in a row in report "+laRowsCells.size());
			int index = 0;
			for(int j = 0; j < rows; j++)
			{		
				boolean skipRow = true;
				
				for(int col = 0; col < aiColumnLength; col++)
				{
					if(index < laRowsCells.size())
					{
						//System.out.println("### index in for "+index);
						Cell = (VersionedCell) laRowsCells.get(index);
						trans1	= Cell.getTransaction();	
						java.sql.Timestamp createdDate = new java.sql.Timestamp(trans1.getCreatedOnTime());	
						
						if(!Cell.getDescription().equals("") && startDate.compareTo(createdDate) < 0 && endDate.compareTo(createdDate) > 0 )
						{
							//System.out.println(startDate + " <= " + createdDate + " && " + endDate + " >= "  +createdDate );
							//System.out.println((startDate.compareTo(createdDate)) + " && " + (endDate.compareTo(createdDate)) );
							skipRow = false;
							index = index + aiColumnLength - col; 
							break;							
						}
						index++;
					}
					else
						break;
				}		
				//System.out.println("### index "+index);
				index = index-aiColumnLength;
				
				if(!skipRow)
				{					
					for(int col = 0; col < aiColumnLength; col++)
					{
						if(index < laRowsCells.size())
						{
							Cell = (VersionedCell) laRowsCells.get(index);
							laNewRowCells.add(Cell);
							index++;
						}
					}
				}
				else
					index = index+aiColumnLength;
			}
			laFinalTable.add(laNewRowCells);
			laNewRowCells = new ArrayList();
		}

		return laFinalTable;
	}

	/**
		This method returns a String array by splitting a string based on a token.
	**/
	public static String[] getArrayFromStrTok(String str, String tok)
	{
		//------------how to use this function-----------------------------
		//String arr[] = getArrayFromStrTok("this, is, a test, string", ",");
		StringTokenizer st = new StringTokenizer(str,tok);
		String arr[] = new String[st.countTokens()];
		int i=0;
		while (st.hasMoreTokens())
		{
			arr[i++] = st.nextToken();
		}

		return arr;
	}


}
