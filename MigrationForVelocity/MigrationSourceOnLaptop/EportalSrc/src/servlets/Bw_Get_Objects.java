package servlets;
//import org.apache.log4j.Logger;
/*
 *  This presents a list of collaboration available to a user
 */
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import com.boardwalk.database.DatabaseLoader;
import com.boardwalk.database.*;
import com.boardwalk.user.UserManager;
import com.boardwalk.user.User;
import com.boardwalk.member.Member;
import com.boardwalk.exception.*;
import boardwalk.common.*;
import java.sql.*;                  // JDBC package
import javax.sql.*;                 // extended JDBC packa

public class Bw_Get_Objects extends xlService   implements
 SingleThreadModel   {

    //int id;
    String Header="Neighborhood_ID:Neighborhood0:Neighborhood1:Neighborhood2:Neighborhood3:Collaboration_ID:Collaboration_Name:Whiteboard_ID:Whiteboard_Name:Cuboid_ID:Cuboid_Name";
	public final static String Seperator = new Character((char)1).toString();
	public final static String ContentDelimeter = new Character((char)2).toString();
	String userName;
	int userId;

    public void service (HttpServletRequest request,
	    HttpServletResponse response)throws ServletException, IOException
    {
		String fullString = getRequestBuffer(request).toString();
		//System.out.println(" fullString "+fullString);

		String[] receivedTokens = fullString.split(Seperator);

		//m_userId & VBA.Chr(1) & m_userName & VBA.Chr(1) & _strReportName & VBA.Chr(1) & strQuery
		userId		= Integer.parseInt(receivedTokens[0]); // User Id
		userName		= receivedTokens[1]; 
		//System.out.println("username="+userName+"id="+userId);
		System.out.println("at before while bw_get_objects");

		Connection connection = null;
			
		try
		{
			DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
			connection = databaseloader.getConnection();

			PreparedStatement preparedstatement = null;
			ResultSet resultset 				= null;
			int liRowCount						= 0;
			int liColCount 						= -1;
			String colName						= "";
			String colType						= "";
			String lsRowCountQuery				= "";

			HashMap colTypeCollection			= new HashMap();

			StringBuffer resData				= new StringBuffer(10000000);
			StringBuffer resDataTemp			= new StringBuffer(10000000);
			StringBuffer accData				= new StringBuffer();
			StringBuffer fmlData				= new StringBuffer();
			StringBuffer resHeader 				= new StringBuffer();
			long date 							=  Calendar.getInstance().getTimeInMillis();

			ResultSetMetaData rsMetaData		= null;//from get prop
       
            preparedstatement 	= connection.prepareStatement("{CALL BW_GET_OBJECTS()}");
            resultset 			= preparedstatement.executeQuery();
			rsMetaData			= resultset.getMetaData();

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

			System.out.println("BW_GET_OBJECTS: Before Loop");

			ArrayList RepRows = new ArrayList(1000);
			ArrayList SingleRow = null; 

			while(resultset.next())
			{
				CurrRow	++;
				SingleRow = new ArrayList(liColCount);

				for(int i = 1 ; i <= liColCount; i++)
				{
				lsColType = (String) colTypeCollection.get(i);
					cellval		= "";
					 if(lsColType.equals("varchar") || lsColType.equals("nvarchar") || lsColType.equals("text")) //for getting vharchar type
					{
						if(BoardwalkUtility.checkIfNullOrBlank(resultset.getString(i)))
							cellval = "";
						else
							cellval = resultset.getString(i);
					}
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

			for(int colindex=0 ; colindex < liColCount; colindex++)
			{
				for(int rowindex=0; rowindex < liRowCount; rowindex++)
				{
					cellval = lsCellRC[rowindex][colindex];
					cellFormula = cellval;

					if (rowindex == liRowCount-1) // last cell of the column
					{
						resDataTemp.append(cellval);
					}
					else
					{
						resDataTemp.append(cellval + Seperator);
					}
				}
				resDataTemp.append(ContentDelimeter);
			}

			for(int i=0; i < liRowCount ; i++)
			resData.append(i+1 + Seperator);

			resData.append(ContentDelimeter);

			// Tempdata which holds the cell data
			resData.append(resDataTemp.toString());
			//System.out.println( "Success"+ Seperator+"Sheet_NHCWCStructure"+Seperator+"subTitle"+ Seperator+date+ Seperator+userName+ Seperator+liRowCount+  Seperator+14+ Seperator + resData.toString());
			commitResponseBuffer("Success"+ Seperator+"Sheet_NHCWCStructure"+Seperator+"subTitle"+ Seperator+date+ Seperator+userName+ Seperator+liRowCount+Seperator+15+ ContentDelimeter + resData.toString() ,response);
        }

		catch ( Exception e )
		{
		   e.printStackTrace();
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
    }
}
