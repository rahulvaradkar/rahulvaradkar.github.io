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
import javax.sql.*;     // extended JDBC packa
import java.text.*;

public class xlSqlQuery extends xlService implements SingleThreadModel
{
	Connection connection	= null;
	PreparedStatement preparedstatement = null;
	Statement		  statementOne	= null;
	Statement		  statement		= null;
	ResultSet		  resultset		= null;
	ResultSet		  resultsetOne	= null;

	String lsReportTitle	= "";
	String lsQueryString	= "";
	int liUserId			= -1;
	long liDateTime			= -1;
	String lsUserName		= "";

	public xlSqlQuery()
	{
		System.out.println(" in servlet xlSqlQuery" );
		liDateTime = Calendar.getInstance().getTimeInMillis();
	}

	public void init(ServletConfig config) throws ServletException
	{
		super.init(config);
		// Single instance of Connection object

	}

    public void doPost (HttpServletRequest request,	HttpServletResponse response)   throws ServletException, IOException
    {
		try
		{

			String fullString = getRequestBuffer(request).toString();
			//System.out.println(" fullString "+fullString);

			String[] receivedTokens = fullString.split(Seperator);

			//m_userId & VBA.Chr(1) & m_userName & VBA.Chr(1) & _strReportName & VBA.Chr(1) & strQuery
			liUserId		= Integer.parseInt(receivedTokens[0]); // User Id
			lsUserName		= receivedTokens[1]; // User Id
			lsReportTitle	= receivedTokens[2]; // Report Title
			lsQueryString	= receivedTokens[3]; // Sql query

			System.out.println(" In servlet userId "+ liUserId);
			System.out.println(" In servlet lsUserName "+ lsUserName);
			System.out.println(" In servlet lsReportTitle "+ lsReportTitle);
			System.out.println(" In servlet lsQueryString "+ lsQueryString);

			String lsResponseStr = getPartValidationBuffer(lsReportTitle, lsQueryString, liUserId);
			commitResponseBuffer(lsResponseStr, response);
		}
		catch (SQLException sql)
		{
			sql.printStackTrace();
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

	public String getPartValidationBuffer(String asRepTitle, String asQuery, int aiUserId)throws SQLException
	{

		try
		{
			DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
			connection = databaseloader.getConnection();
			if(connection == null)
				System.out.println(" connection is null" );
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
		StringBuffer resHeader = new StringBuffer();

//		lsQueryString = " SELECT * FROM BW_TBL ";
//		int liFromPos	= lsQueryString.indexOf("FROM");
//		if(liFromPos == -1)
//			liFromPos	= lsQueryString.indexOf("from");

//		lsRowCountQuery = "SELECT Count(*) "+asQuery.substring(liFromPos);

		try
		{
			if(connection == null)
			{
				System.out.println(" connection is closed "+connection);
			}
			else
			{

//						statementOne = connection.createStatement();
//						resultsetOne = statementOne.executeQuery(asQuery);
//						while(resultsetOne.next())
//						{
//							liRowCount++;
//						}


						statement = null;
						resultset = null;

						statement = connection.createStatement();
						resultset = statement.executeQuery(asQuery);


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

//						String lsCellRC[][] = new String[liRowCount][liColCount];
						int CurrRow			= -1;

//						statement = connection.createStatement();
//						resultset = statement.executeQuery(asQuery);

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
			//					System.out.println(" lsColType of the coumns  "+lsColType+" and value is "+resultset.getString(i));

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
								else if(lsColType.equals("varchar") || lsColType.equals("nvarchar") || lsColType.equals("text")) //for getting vharchar type
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

								//System.out.println(" cellval  "+cellval);

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

					if(resultsetOne != null)
						resultsetOne.close();
					if(statementOne != null)
						statementOne.close();
					if(resultset != null)
						resultset.close();
					if(preparedstatement != null)
						preparedstatement.close();
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

		System.out.println(" resData.toString() "+resHeader.toString() + ContentDelimeter + resData.toString());

		return resHeader.toString() + ContentDelimeter + resData.toString();

	}

}

