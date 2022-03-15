package servlets;
/*
 *  This presents a list of collaboration available to a user
 */
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import com.boardwalk.table.*;
import com.boardwalk.excel.*;
import com.boardwalk.exception.*;
import com.boardwalk.database.*;
import java.sql.*;                  // JDBC package
import javax.sql.*;                 // extended JDBC packa
import com.boardwalk.member.Member;
import com.boardwalk.user.UserManager;

//  JSON Changes
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileReader;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
//  JSON Changes

public class xlImportChangesServiceJsonSimulator extends xlServiceJsonSimulator implements SingleThreadModel
{

	public final static String Seperator = new Character((char)1).toString();
	public final static String ContentDelimeter = new Character((char)2).toString();

	public void service(HttpServletRequest request,
		HttpServletResponse response)throws ServletException, IOException
	{
		String responseBuffer = "Failure";
		String failureReason = "";
		Vector xlErrorCells = new Vector();
		getElapsedTime();
		//String buff = getRequestBuffer(request).toString();
		//System.out.println(buff);
		System.out.println("Time to read the buffer = " + getElapsedTime());

		// Get the database connection
		int tableId = -1;
		TransactionManager tm = null;
		Connection connection = null;
		PreparedStatement stmt = null;
		try
		{
			//  JSON Changes		
			//String[] buffArray = buff.split(ContentDelimeter); //  
			/* FileWriter file = new FileWriter("d:/tomcat7/webapps/EPortal/Staging_JSON/JSON_Data_File.txt");
			file.write(buff);
			file.flush();
			file.close();
			BoardwalkJsonReaderJsonAPI bjrf=new BoardwalkJsonReaderJsonAPI();
			bjrf.filewrite("d:/tomcat7/webapps/EPortal/Staging_JSON/JSON_Data_File.txt");
			BoardwalkJsonReaderJsonAPI bjr=new BoardwalkJsonReaderJsonAPI("d:/tomcat7/webapps/EPortal/Staging_JSON/JSON_Data_File.json");
			System.out.println("header data"+bjr.jsonreader("HeaderData")); */

			//String[] requestInfo = buffArray[0].split(Seperator); // 
							List<String> listheader = new ArrayList<String>();
				String[] headerInfo=null;
				response.setContentType("application/json");
				response.setCharacterEncoding("UTF-8");

				PrintWriter out = response.getWriter();

				BufferedReader brr = new BufferedReader(new InputStreamReader(request.getInputStream()));
				StringBuilder sb = new StringBuilder();
				String json = "";
				out.println(json);
				if (brr != null) {
					json = brr.readLine();
					System.out.println("json==" + json);

				}

			JSONParser parser = new JSONParser();

				
					JSONObject jsonob = (JSONObject) parser.parse(json);
					JSONArray headerData = (JSONArray) jsonob.get("HeaderData");
					String[] requestInfo = new String[headerData.size()];
			  for (int i = 0; i < headerData.size(); i++) {
				         requestInfo[i]= (String) headerData.get(i);
				         
				          System.out.println("headerarr"+ requestInfo[i]);
				     }
			//String[] requestInfo = bjr.jsonreader("HeaderData");
			//  JSON Changes		

			int userId = Integer.parseInt(requestInfo[0]);
			String userName = requestInfo[1];
			//String userPassword = requestInfo[2];
			int memberId = Integer.parseInt(requestInfo[3]);
			int nhId = Integer.parseInt(requestInfo[4]);
			tableId = Integer.parseInt(requestInfo[5]);
			int baselineId = Integer.parseInt(requestInfo[6]);
			String view = requestInfo[7];
			int importTid = Integer.parseInt(requestInfo[8]);
			int exportTid = Integer.parseInt(requestInfo[9]);
			int mode = Integer.parseInt(requestInfo[10]);
			int synch = Integer.parseInt(requestInfo[11]);

			HashMap localRowHash = new HashMap();
			//  JSON Changes		
			//if (buffArray.length > 1)
			JSONArray ColumnData = (JSONArray) jsonob.get("ColumnData");
			System.out.println("test  ==="+ColumnData.size());
				String[] rowInfo = new String[ColumnData.size()];
				     for (int i = 0; i < ColumnData.size(); i++) {
				         //stringArray1[i]= (String) ColumnData.get(i);
				    	 rowInfo[i]=String.valueOf(ColumnData.get(i));
				          System.out.println("ColumnData"+ rowInfo[i]);
				     }
					 
					 	System.out.println("test  ===");
			if(rowInfo.length>1)
			{
				//String[] rowInfo = buffArray[1].split(Seperator);
				
				
			//	System.out.println("column data in rowinfo="+bjr.jsonreader("ColumnData"));
						
			//	String[] rowInfo =bjr.jsonreader("ColumnData");
				//  JSON Changes		

				for (int ri = 0; ri < rowInfo.length; ri++)
				{
					String rowIdStr = rowInfo[ri];
					if (!rowIdStr.equalsIgnoreCase(""))
					{
						int rowId = -1;
						rowId = Integer.parseInt(rowIdStr);
						localRowHash.put(new Integer(rowId), new Integer(rowId));
					}
				}
			}
			System.out.println("Time to create localRowHash from buffer = " + getElapsedTime());
			int maxTransactionId = importTid;

			// Start a connection
			DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
			connection = databaseloader.getConnection();

			// try the old sheet check
			if (synch == 0 && (view == null || view.trim().equals("") || view.equalsIgnoreCase("Latest")))
			{
				oldSheetCheck(connection, tableId, memberId, userId, exportTid, view);
				System.out.println("Time to perform old sheet check = " + getElapsedTime());
			}
			/*
			// authenticate the user
			Member memberObj = UserManager.authenticateMember(connection, userName,userPassword, memberId);
			if (memberObj == null)
			{
				System.out.println("Authentication failed for user : " + userName);
				responseBuffer = "Failure";//TBD : Description of the Error
				xlErrorCells.add( new xlErrorNew( tableId, 0, 0, 11005));
				throw new BoardwalkException(11005);
			}
			else
			{
				System.out.println("Authentication succeeded for user : " + userName);
				nhId = memberObj.getNeighborhoodId();
			}
			*/
			System.out.println("Time to authenticate user = " + getElapsedTime());

			// Check access control :: TBD
			TableInfo tinfo = TableManager.getTableInfo(connection, userId, tableId);
			TableAccessList ftal = TableViewManagerJsonSimulator.getSuggestedAccess(connection, tinfo, userId, memberId, nhId);
			if (view == null || view.trim().equals(""))
			{
				view = ftal.getSuggestedViewPreferenceBasedOnAccess();
				System.out.println("Suggested view pref = " + view);
				if (view == null)
				{
					xlErrorCells.add(new xlErrorNew(tableId, 0, 0, 10005));
					throw new BoardwalkException(10005);
				}
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

			// see if there is a criterea table associated with this table
			int criteriaTableId = TableViewManagerJsonSimulator.getCriteriaTable(connection, tableId, userId);
			System.out.println("Using criterea table id = " + criteriaTableId);
			StringBuffer resData = new StringBuffer(10000000);
			//  JSON Changes
			JSONArray resJsonData=new JSONArray();
			JSONObject jsonobj = new JSONObject();
			//  JSON Changes
			System.out.println("Time(sec) to check access = " + getElapsedTime());
			// Get the columns
			Vector colv = ColumnManager.getXlColumnsForImport(connection, tableId, userId, memberId);
			System.out.println("Time(sec) to execute column query for = " + getElapsedTime());
			HashMap colHash = new HashMap();
			//ColObjsByColId = new HashMap();
			Iterator ci = colv.iterator();
			//while (ci.hasNext())
			//{
			//    xlColumn_import coli = (xlColumn_import)ci.next();
			//    ColObjsByColId.put(new Integer(coli.getId()), coli);
			//}
			// columns
			System.out.println("Fetched columns : " + colv.size() + " time taken = " + getElapsedTime());

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
				//  JSON Changes
				//resData.append(col.getId() + Seperator);
				//resData.append(col.getName() + Seperator);
				resJsonData.add(col.getId());
				resJsonData.add(col.getName());
				//  JSON Changes
				// Mark New Columns, or Deleted Columns
				if (col.getCreationTid() > importTid)
				{
					//  JSON Changes
					//resData.append("N" + Seperator);
					resJsonData.add("N");
					//  JSON Changes
				}
				else if (col.getAccessTid() > importTid && col.getAccess() > 0)
				{
					//  JSON Changes
					//resData.append("N" + Seperator);
					resJsonData.add("N");
					//  JSON Changes
				}
				else
				{
					//  JSON Changes
					//resData.append(Seperator);
					resJsonData.add(" ");
					//  JSON Changes
				}
				//System.out.println("putting in hash column = " + col.getId());
				colHash.put(new Integer(col.getId()), col);
			}
			//  JSON Changes
			//resData.replace(resData.length() - 1,resData.length(), ContentDelimeter);
			JSONArray resJsonRowData=new JSONArray();
			//  JSON Changes
			System.out.println("Time(sec) to fetch columns = " + getElapsedTime());
			//System.out.println(resData.toString());
			// Get the rows
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
			//condition added ashishB
			if (criteriaTableId > 0 && viewIsDynamic)
			{
				tbrowInfo = RowManager.getTableRows(connection, tableId, userId, nhId, baselineId, view, 1, -1, -1);
			}
			else
			{
				tbrowInfo = RowManager.getTableRows(connection, tableId, userId, nhId, baselineId, view, 1, -1, -1);
			}
			Vector rowv = tbrowInfo.getRowVector();
			Hashtable rowHash = tbrowInfo.getRowHash();
			// rows
			//System.out.println("transaction start");
			tm = new TransactionManager(connection, userId);
			int tid = tm.startTransaction("Import changes for table id = " + tableId, "");
			tm.commitTransaction();
			tm = null;
			//System.out.println("transaction commit");
			stmt = connection.prepareStatement("UPDATE BW_ROW SET OWNER_TID = ? WHERE ID = ?");
			int numNewRows = 0;
			for (int r = 0; r < rowv.size(); r++)
			{
				Row rowObject = (Row)rowv.elementAt(r);
				int rowId = rowObject.getId();
				if (maxTransactionId < rowObject.getCreationTid())
				{
					maxTransactionId = rowObject.getCreationTid();
				}

				if (maxTransactionId < rowObject.getOwnershipAssignedTid())
				{
					maxTransactionId = rowObject.getOwnershipAssignedTid();
				}
				//  JSON Changes
				//resData.append(rowId + Seperator);
				resJsonRowData.add(rowId);
				//  JSON Changes
				//if (rowObject.getCreationTid() > importTid || rowObject.getOwnershipAssignedTid() > importTid)
				if (localRowHash.get(new Integer(rowId)) == null)
				{
					//  JSON Changes
					//resData.append("N" + Seperator);
					resJsonRowData.add("N");
					//  JSON Changes
					stmt.setInt(1, tid);
					stmt.setInt(2, rowId);
					stmt.addBatch();
					numNewRows++;
				}
				else
				{
					//  JSON Changes
					//resData.append(Seperator);
					resJsonRowData.add(" ");
					//  JSON Changes
				}
			}
			//  JSON Changes
			//resData.replace(resData.length() - 1, resData.length(), ContentDelimeter);
			JSONArray resJsonCellData=new JSONArray();
			//  JSON Changes
			if (numNewRows > 0)
			{
				stmt.executeBatch();
				stmt.clearBatch();
			}
			stmt.close();
			stmt = null;
			System.out.println("Time(sec) to fetch rows = " + getElapsedTime());
			//System.out.println(resData.toString());
			// Get the cells TBD : views other than latest
			String q = null;
			if (synch == 0)
			{
				q = "{CALL BW_IMPORT_CHANGES(?,?,?,?,?,?,?)}";
				System.out.println("Calling BW_IMPORT_CHANGES ");
			}
			else
			{
				q = "{CALL BW_IMPORT_CHANGES_ALL(?,?,?,?,?,?,?)}";
				System.out.println("Calling BW_IMPORT_CHANGES_ALL ");
			}
			//cellv = TableManager.getLatestCellsForTable(connection, m_tableid, userId, memberId, nhId, ViewPreference);
			stmt = connection.prepareStatement(q);
			stmt.setInt(1, tableId);
			stmt.setInt(2, userId);
			stmt.setInt(3, memberId);
			stmt.setInt(4, nhId);
			stmt.setString(5, view);
			stmt.setInt(6, importTid);
			stmt.setInt(7, tid);

			System.out.println("tableId = " + tableId);
			System.out.println("userId = " + userId);
			System.out.println("memberId = " + memberId);
			System.out.println("nhId = " + nhId);
			System.out.println("view = " + view);
			System.out.println("importTid = " + importTid);
			System.out.println("newTid = " + tid);
			System.out.println("mode = " + mode);
			System.out.println("synch = " + synch);

			ResultSet rs = stmt.executeQuery ();
			System.out.println("Time(sec) to execute cell query = " + getElapsedTime());
			System.out.println("resJsonCellData before while="+resJsonCellData.toString());
			while (rs.next())
			{
				String sval = rs.getString(1);
				String fmla = rs.getString(2);
				int rowId = rs.getInt(3);
				int colId = rs.getInt(4);
				if (maxTransactionId < rs.getInt(5))
				{
					maxTransactionId = rs.getInt(5);
				}
				if (rowHash.get(new Integer(rowId)) == null)
					continue;
				xlColumn_import col = (xlColumn_import)colHash.get(new Integer(colId));
				if (col == null)
					continue;
				if (fmla == null || fmla.indexOf("=") < 0 || mode == 1)
				{
					fmla = "";
				}
				else
				{
					fmla = fmla.trim();
				}

				//System.out.println("Got column for id = " + colId);
				int colAccess = col.getAccess();
				int cellAccess = java.lang.Math.min(raccess, colAccess);
				//  JSON Changes
				//resData.append(rowId + Seperator + colId + Seperator + sval.trim() + Seperator + fmla + Seperator + cellAccess + Seperator);
				System.out.println("resJsonCellData in while="+resJsonCellData.toString());
				resJsonCellData.add(rowId);
				resJsonCellData.add(colId);
				resJsonCellData.add(sval.trim());
				resJsonCellData.add(fmla);
				resJsonCellData.add(cellAccess);
			}
			//System.out.println(resData.toString());
			//resData.replace(resData.length() - 1, resData.length(), ContentDelimeter); //  JSON Changes
			//  JSON Changes
			//System.out.println(resData.toString());
			stmt.close();
			rs.close();
			rs = null;
			stmt = null;
			System.out.println("Time(sec) to fetch changed cells = " + getElapsedTime());

			int maxdeletedcell_tid;
			maxdeletedcell_tid = 0;
			ResultSet rs1 = null;


			try
			{
				stmt = connection.prepareStatement
					("SELECT MAX(BW_ROW.TX_ID) FROM BW_ROW WHERE BW_ROW.TX_ID > ? AND BW_ROW.BW_TBL_ID = ? AND BW_ROW.IS_ACTIVE = 0 UNION SELECT MAX(BW_COLUMN.TX_ID) FROM BW_COLUMN WHERE BW_COLUMN.TX_ID > ? AND BW_COLUMN.BW_TBL_ID = ? AND BW_COLUMN.IS_ACTIVE = 0");
				stmt.setInt(1, importTid);
				stmt.setInt(2, tableId);
				stmt.setInt(3, importTid);
				stmt.setInt(4, tableId);
				rs1 = stmt.executeQuery ();
				while( rs1.next() )
				{
					if(rs1.getInt(1) > maxdeletedcell_tid)
						maxdeletedcell_tid = rs1.getInt(1);
				}
				rs1.close();
				stmt.close();
				stmt = null;
			}
			catch(Exception e11)
			{	e11.printStackTrace();
				try
				{
					rs1.close();
					stmt.close();
					stmt = null;

				}
				catch (Exception e12)
				{
					e12.printStackTrace();
				}
			}

			System.out.println("Time(sec) to getmaxtid for deleted cells = " + getElapsedTime());




			if ( maxdeletedcell_tid > maxTransactionId )
			{
				maxTransactionId = maxdeletedcell_tid;
				System.out.println("maxtid reset by cell deactivation to = " + maxTransactionId);
			}


			System.out.println("maxtid = " + maxTransactionId);

			// write the header to the response
			StringBuffer resHeader = new StringBuffer();
			//  JSON Changes
			//resHeader.append("Success" + Seperator);
			//resHeader.append(colv.size() + Seperator);
			//resHeader.append(rowv.size() + Seperator);
			//resHeader.append(maxTransactionId + ContentDelimeter);
			JSONArray resJsonHeader = new JSONArray();
			resJsonHeader.add("Success");
			resJsonHeader.add(colv.size());
			resJsonHeader.add(rowv.size());
			resJsonHeader.add(maxTransactionId);
			System.out.println("resJsonHeader="+resJsonHeader.toString());
			System.out.println("resjsondata="+resJsonData.toString());
			System.out.println("resJsonRowData="+resJsonRowData.toString());
			System.out.println("resJsonCellData="+resJsonCellData.toString());
			jsonobj.put("headerdata",resJsonHeader);
			jsonobj.put("columndata",resJsonData);
			jsonobj.put("rowdata",resJsonRowData);
			jsonobj.put("celldata",resJsonCellData);
			
			System.out.println("json object"+jsonobj.toString());
			response.getWriter().print(jsonobj);
			String[] jskeys={"headerdata","columndata","rowdata","celldata"};
			StringBuffer temptestdata=new StringBuffer();
			for(int js=0;js<jsonobj.size();js++)
			{
			JSONArray test=new JSONArray();
			test=(JSONArray) jsonobj.get(jskeys[js]);
			for(int jsa=0;jsa<test.size();jsa++)
			{
			//String spaceelim=(String)test.get(jsa);
		//	System.out.println("values="+test.get(jsa));
			if(test.get(jsa)!=" " && jsa!=test.size()-1){
				temptestdata.append(test.get(jsa));
				temptestdata.append(Seperator);
					
			}
				//temptestdata=temptestdata+test.get(jsa)+Seperator;
			else if(jsa==test.size()-1)
			{
			if(test.get(jsa)!=" ")
			//temptestdata=temptestdata;
			temptestdata.append(test.get(jsa));
			//temptestdata=temptestdata+test.get(jsa);
			}
			else if(test.get(jsa)==" ")
				temptestdata.append(Seperator);
			//temptestdata=temptestdata+Seperator;
			}
			temptestdata.append(ContentDelimeter);
			//temptestdata=temptestdata+ContentDelimeter;
			
			}
			System.out.println("temptestdata="+temptestdata.toString());
			
			responseBuffer=temptestdata.toString();

			//responseBuffer = resHeader.toString() + resData.toString();
			System.out.println("reponse san= "+responseBuffer);
			//  JSON Changes
		}
		catch (BoardwalkException bwe)
		{
			if (xlErrorCells.size() <= 0)
			{
				responseBuffer = "Failure";//TBD : Description of the Error
				xlErrorCells.add(new xlErrorNew(tableId, 0, 0, bwe.getErrorCode()));
			}
			StringBuffer errorBuffer = new StringBuffer();

			for (int errorIndex = 0; errorIndex < xlErrorCells.size(); errorIndex++)
			{
				xlErrorNew excelError = (xlErrorNew)(xlErrorCells.elementAt(errorIndex));
				errorBuffer.append(excelError.buildTokenString());
			}
			errorBuffer.append(Seperator);
			failureReason = errorBuffer.toString();
			System.out.println(" Failure Reason *****" + failureReason);

			try
			{
				if (tm != null)
					tm.rollbackTransaction();
			}
			catch (Exception e1)
			{
				e1.printStackTrace();
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
		}
		finally
		{
			// close the connection
			try
			{
				if (connection != null)
					connection.close();
			}
			catch (SQLException sql)
			{
				sql.printStackTrace();
			}

			// The response
			if(failureReason.length() == 0)
			{
				//commitResponseBuffer(responseBuffer, response);
				System.out.println("Time to prepare response = " + getElapsedTime());
			}
			else
			{
				failureReason = "FAILURE"+ContentDelimeter+failureReason;
				//commitResponseBuffer(failureReason, response);
				System.out.println("Time to prepare response = " + getElapsedTime());
			}
		}
	}
}
