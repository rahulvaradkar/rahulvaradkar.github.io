package servlets;

/* *
	This servlet will check if there is any valid significant update after this particular Tx_id.
*/
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import com.boardwalk.util.*;
import com.boardwalk.database.*;
import com.boardwalk.table.*;
import boardwalk.table.*;
import com.boardwalk.exception.*;
import boardwalk.connection.*;
import com.boardwalk.query.*;
import boardwalk.common.*;

import java.sql.*;      // JDBC package
import javax.sql.*;     // extended JDBC package
import java.text.*;

public class Get_Boardwalk_Template_PropLogic extends xlServiceLogic
{
	private static String CALL_BW_EXTERNAL_QUERY="{CALL BW_EXTERNAL_QUERY(?)}";

	Connection connection	= null;

	String lsReportTitle	= "";
	DBcall lsQueryString;
	String lsQueryID        = "";
	String lsParameters		="";
	int liUserId			= -1;
	long liDateTime			= -1;
	String lsUserName		= "";

	public Get_Boardwalk_Template_PropLogic(Get_Boardwalk_Template_Prop srv)
	{
        super(srv);
		System.out.println("Inside Get_Boardwalk_Template_Prop" );
		liDateTime = Calendar.getInstance().getTimeInMillis();
	}

	public void doPost (HttpServletRequest request,	HttpServletResponse response)   throws ServletException, IOException
	{
		try
		{
			String fullString = getRequestBuffer(request).toString();
			//System.out.println(" fullString "+fullString);

			String[] receivedTokens = fullString.split(Seperator);

			liUserId		= Integer.parseInt(receivedTokens[0]); // User Id
			lsUserName		= receivedTokens[1]; // User Name
			lsReportTitle	= receivedTokens[2]; // Report Title
			lsQueryID		= receivedTokens[3]; // Sql Query ID 
			lsParameters	= receivedTokens[4]; // Query Parameters

			//System.out.println(" In servlet userId "+ liUserId);
			//System.out.println(" In servlet lsUserName "+ lsUserName);
			//System.out.println(" In servlet lsReportTitle "+ lsReportTitle);
			//System.out.println(" In servlet lsQueryID "+ lsQueryID);
			//System.out.println(" In servlet lsParameters "+ lsParameters);

			lsQueryString = getExternalDefinedQuery(Integer.parseInt(lsQueryID), lsParameters);
			//lsQueryString	= receivedTokens[3]; // Sql query ID

			//System.out.println(" In servlet userId "+ liUserId);
			//System.out.println(" In servlet lsUserName "+ lsUserName);
			//System.out.println(" In servlet lsReportTitle "+ lsReportTitle);
			System.out.println(" In servlet lsQueryString "+ lsQueryString);
			//System.out.println(" In servlet lsParameters "+ lsParameters);

			String lsResponseStr = getPartValidationBuffer(lsReportTitle, lsQueryString, lsParameters, liUserId);
			//System.out.println(" lsResponseStr = "+lsResponseStr);
			commitResponseBuffer(lsResponseStr, response);
		}
		catch (SQLException sql)
		{
			sql.printStackTrace();
			return;
		} 
		catch (Exception eq)
		{
			eq.printStackTrace();
			return;
		}
		finally
		{
			//
		}
	}

	public void doGet (HttpServletRequest request, HttpServletResponse response)  throws ServletException, IOException
	{
		doPost(request, response);
	}

	public DBcall getExternalDefinedQuery(int QueryId, String lsParameters) throws SQLException
	{
		System.out.println("Inside getExternalDefinedQuery");
		ResultSet resultset = null;
		PreparedStatement ps = null;
		String queryCall = null;
		String queryParam = null;

		//System.out.println("lsParameters = " + lsParameters);
		StringTokenizer prmtr = new StringTokenizer(lsParameters, "|");

		try
		{
			DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
			connection = databaseloader.getConnection();

			resultset = null;
			ps = connection.prepareStatement(CALL_BW_EXTERNAL_QUERY);
			ps.setInt(1, QueryId);
			resultset = ps.executeQuery();

			while (resultset.next())
			{
				queryCall = resultset.getString(3);
				queryParam = resultset.getString(4);
			}

			ps.close();
			ps = null;
			resultset.close();
			resultset = null;

			System.out.println("Get_Boardwalk_Template_Prop:queryCall: " + queryCall);
			//System.out.println("queryParam : " + queryParam);
			//System.out.println("queryCall before insert param : " + queryCall);

			if (queryParam == null) {
				System.out.println("Query not found for id = " + QueryId);
			}
		}
		catch( Exception e ) {
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if (ps != null)
					ps.close();
				if (resultset != null)
					resultset.close();
				ps = null;
				resultset = null;
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		return new DBcall(queryCall, queryParam);
	}

	public String getPartValidationBuffer(String asRepTitle, DBcall asQuery, String lsParameters, int aiUserId)throws SQLException
	{
		PreparedStatement	statement = null;
		ResultSet			resultset = null;

		try
		{
			DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
			connection = databaseloader.getConnection();

			if(connection == null)
				System.out.println("Connection is null" );
		}
		catch (SQLException sql)
		{
			sql.printStackTrace();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		ResultSetMetaData rsMetaData	= null;

		int liColCount				= -1;
		int liRowCount				= 0;

		String colName				= "";
		String colType				= "";
		String lsRowCountQuery		= "";

		HashMap colTypeCollection	= new HashMap();

		StringBuffer resData		= new StringBuffer(10000000);
		StringBuffer resDataTemp	= new StringBuffer(10000000);
		StringBuffer accData		= new StringBuffer();
		StringBuffer fmlData		= new StringBuffer();
		StringBuffer resHeader		= new StringBuffer();

		try
		{
			if(connection == null)
			{
				System.out.println("Connection is closed: " + connection);
			}
			else
			{
				System.out.println("asQuery.SPname = " + asQuery.SPname);
				//System.out.println("lsParameters = " + lsParameters);
				//System.out.println("asQuery.paramType " + asQuery.paramType);
				
				statement = connection.prepareStatement(asQuery.SPname);
				
				StringTokenizer prmtr = new StringTokenizer(lsParameters, "|");

				if (asQuery.paramType != null)
				{ 
					StringTokenizer qryprmtr = new StringTokenizer(asQuery.paramType, "|");

					String paramType=null;
					String paramValue=null;
					//String dQuote = "\"";
					int paramIdx = 1;
					
					while (prmtr.hasMoreElements()) {	
						paramType = qryprmtr.nextToken();
						paramValue = prmtr.nextToken();
						//System.out.println("parameter type: " + paramType);
						//System.out.println("parameter value: " + paramValue);
					
						if (paramType.equalsIgnoreCase("string")) {
							//paramValue = dQuote.concat(paramValue).concat(dQuote);
							statement.setString(paramIdx, paramValue);
						}
						else if (paramType.equalsIgnoreCase("int")) {
							statement.setInt(paramIdx, Integer.parseInt(paramValue) );
						}
						paramIdx++;
					}
				}
				
				resultset = statement.executeQuery();
				
				rsMetaData	= resultset.getMetaData();
				liColCount	= rsMetaData.getColumnCount();

				for(int i=1; i <= liColCount  ; i++)
				{
					colName = rsMetaData.getColumnName(i);
					colType = rsMetaData.getColumnTypeName(i);

					colTypeCollection.put(new Integer(i) , colType);
					resData.append(colName + Seperator); // Only column names will be sufficent
				}
				resData.append(ContentDelimeter);

				String lsColType	= "";
				String cellval		= "";
				String cellFormula	= "";
				int	cellAccess		= 2;
				int CurrRow			= -1;

				ArrayList RepRows = new ArrayList(1000);
				ArrayList SingleRow = null; 

				while(resultset.next())
				{
					CurrRow	++;
					SingleRow = new ArrayList(liColCount);

					for(int i = 1 ; i <= liColCount; i++)
					{
						//System.out.println(" Current Count > "+i);

						lsColType = (String) colTypeCollection.get(i);
						cellval		= "";
						//System.out.println(" lsColType of the coumns  "+lsColType+" and value is "+resultset.getString(i));

						if(lsColType.equals("int identity") || lsColType.equals("int") || lsColType.equals("bit") || lsColType.equals("bigint")) //for getting int type
						{
							cellval = resultset.getInt(i)+"";
							if(BoardwalkUtility.checkIfNullOrBlank(cellval) || cellval.equals("null"))
								cellval = "";
						}
						else if(lsColType.equals("datetime")) //for getting date time type
						{
							if(resultset.getDate(i) == null)
								cellval = "";
							else
								cellval = resultset.getDate(i)+"";
						}
						else if(lsColType.equals("varchar") || lsColType.equals("nvarchar") || lsColType.equals("text") || lsColType.equals("ntext")) //for getting vharchar type
						{
							if(BoardwalkUtility.checkIfNullOrBlank(resultset.getString(i)))
								cellval = "";
							else
								cellval = resultset.getString(i);
						}
						else if(lsColType.equals("double")) //for getting double type
						{
							cellval = resultset.getDouble(i)+"";
							if(BoardwalkUtility.checkIfNullOrBlank(cellval) || cellval.equals("null"))
								cellval = "";
						}
						else if(lsColType.equals("float")) //for getting float type
						{
							cellval = resultset.getFloat(i)+"";
							if(BoardwalkUtility.checkIfNullOrBlank(cellval) || cellval.equals("null"))
								cellval = "";
						}
						else if(lsColType.equals("image")) //for getting image type
						{
							//Will this case be required
							//BufferedInputStream in = new BufferedInputStream(resultset.getBinaryStream("DOC")
							cellval = "image";
						}

						if(BoardwalkUtility.checkIfNullOrBlank(cellval))
							cellval = "";

						//lsCellRC[CurrRow][i-1]= cellval;
						SingleRow.add(cellval);
					}
					RepRows.add(SingleRow);
				}

				liRowCount = CurrRow+1;
				String lsCellRC[][] = new String[liRowCount][liColCount];
				ArrayList ReadRow = null;

				for(int rowCount = 0 ; rowCount < liRowCount ; rowCount++)
				{
					ReadRow =  (ArrayList)	RepRows.get(rowCount); 
					for( int colCount = 0 ; colCount < liColCount ; colCount++)
					{
						lsCellRC[rowCount][colCount] = (String) ReadRow.get(colCount);
					}
				}

				System.out.println(" Total selected Rows "+liRowCount);

				cellFormula = "";
				cellAccess	= 2; // DefaultWrite

				// prepare the buffer
				for(int colindex=0 ; colindex < liColCount; colindex++)
				{
					for(int rowindex=0; rowindex < liRowCount; rowindex++)
					{
						cellval = lsCellRC[rowindex][colindex];
						cellFormula = cellval;

						if (rowindex == liRowCount-1) // last cell of the column
						{
							resDataTemp.append(cellval);
							fmlData.append(cellFormula);
							accData.append(cellAccess);
						}
						else
						{
							resDataTemp.append(cellval + Seperator);
							fmlData.append(cellFormula + Seperator);
							accData.append(cellAccess + Seperator);
						}
					}
					resDataTemp.append(ContentDelimeter);
					fmlData.append(ContentDelimeter);
					accData.append(ContentDelimeter);
				}

				// now prepare the contents of the rowid
				for(int i=0; i < liRowCount ; i++)
					resData.append(i+1 + Seperator);

				resData.append(ContentDelimeter);

				// Tempdata which holds the cell data
				resData.append(resDataTemp.toString());
				// Formula Data
				resData.append(fmlData.toString());
				// access
				resData.append(accData.toString());

				resHeader.append("Success" + Seperator);
				resHeader.append(asRepTitle + Seperator);
				resHeader.append("For Report Sub title " + Seperator);
				resHeader.append(liDateTime+ Seperator);  // Report Date
				resHeader.append(aiUserId + Seperator); // Report Created by
				resHeader.append(liRowCount + Seperator); // number of Rows
				resHeader.append(colTypeCollection.size() + Seperator); // number of column
			}
		}
		catch(SQLException sqlexception)
		{
			sqlexception.printStackTrace();
			throw sqlexception;
		}
		finally
		{

			try{
				if(resultset != null)
					resultset.close();
				if(statement != null)
					statement.close();
				if(connection != null)
					connection.close();
			}
			catch( SQLException sql )
			{
				sql.printStackTrace();
			}

		}

		System.out.println("Get_Boardwalk_Template_Prop:Done");

		return resHeader.toString() + ContentDelimeter + resData.toString();
	}

	class DBcall {
		String SPname;
		String paramType;
		DBcall() {}
		DBcall(String SPname, String paramType) {
			this.SPname = SPname;
			this.paramType = paramType;
		}
	}
}

