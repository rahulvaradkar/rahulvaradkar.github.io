package com.boardwalk.table;

import java.util.*;
import java.io.*;

import com.boardwalk.whiteboard.*;
import com.boardwalk.collaboration.*;
import com.boardwalk.neighborhood.*;

import com.boardwalk.database.DatabaseLoader;
import com.boardwalk.exception.*;
import com.boardwalk.database.*;
import com.boardwalk.user.*;
import com.boardwalk.excel.*;
import boardwalk.common.*;

import java.sql.*;                  // JDBC package
import javax.sql.*;   

public class SharedNameSelection 
{

	private static String CALL_BW_CR_STRING_VALUE="{CALL  BW_CR_STRING_VALUE(?,?,?,?,?)}";

	public static double createStringValueId(Connection connection, int sharedRowId,int sharedColid,String sharedNamevalue, int tx_id) throws SQLException
	{

		CallableStatement callablestatement	= null;
		double string_Value_Id				= -1;
		
		try
		{
			callablestatement = connection.prepareCall(CALL_BW_CR_STRING_VALUE);
			callablestatement.setInt(1, sharedRowId);
			callablestatement.setInt(2, sharedColid);
			callablestatement.setString(3, sharedNamevalue);
			callablestatement.setInt(4, tx_id);
			callablestatement.registerOutParameter(5, java.sql.Types.NUMERIC);

			callablestatement.execute();
			
			string_Value_Id = callablestatement.getLong(5);

			callablestatement.close();	
		}
		catch( SQLException sqe )
		{
			sqe.printStackTrace();
			try
			{
				if(connection != null)
					connection.close();
				if(callablestatement !=null)
					callablestatement.close();	
			}
			catch( SQLException sql2 )
			{
				sql2.printStackTrace();
			}
		}

		return string_Value_Id;
	  }

	   public static boolean populateTblNamedSelection(Connection connection, double stringValueId, ArrayList rowIds, ArrayList columnIds) throws SQLException
	   {
//		    Connection connection		= null;
			PreparedStatement statement	= null;
			boolean hasPopulated	= false;
			int selRowid = -1;
			int selColid = -1;
			int batchSize = 1000;
			int batchCounter = 0;
			int[] UpdateCount = new int[1000];

			try
			{

				String lsSql = "INSERT INTO BW_NAMED_SELECTION VALUES (?,?,?)";

				statement = connection.prepareStatement(lsSql);

				for (int ci = 0 ; ci < rowIds.size() ; ci++ )
				{
					selRowid = Integer.parseInt(rowIds.get(ci).toString());
					selColid = Integer.parseInt(columnIds.get(ci).toString());
					statement.setDouble(1, stringValueId);
					statement.setInt(2, selRowid);
					statement.setInt(3, selColid);
					statement.addBatch();
					batchCounter = batchCounter + 1;

					if (batchCounter == batchSize)
					{
						UpdateCount = statement.executeBatch();
						statement.clearBatch();
						batchCounter = 0;
					}
				}

				UpdateCount = statement.executeBatch();
//				statement.clearBatch();
//				batchCounter = 0;

				if(UpdateCount.length > 0)
					hasPopulated = true;

			}
			catch( SQLException sqe )
			{

				sqe.printStackTrace();
				try
				{
					if(connection != null)
						connection.close();
				}
				catch( SQLException sql2 )
				{
					sql2.printStackTrace();
				}
			}
			return hasPopulated;
		}

		
		public static String getNamesSelection(int sharedRowId,int sharedColid, int importid , int userid) 
		{
			Connection connection				= null;
			PreparedStatement preparedstatement	= null;
			ResultSet rs						= null;

			String Seperator		= new Character((char)1).toString();
			String ContentDelimeter = new Character((char)2).toString();

			int stringValueId	= -1;
			int selRowId		= -1;
			int selColId		= -1;
			int selTxId			= -1;
			
			StringBuffer sb		= new StringBuffer();
			StringBuffer sbs	= new StringBuffer();

			try
			{
				DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
				connection	= databaseloader.getConnection();

				String CALL_BW_GET_NAMES_SELECTION="{CALL BW_GET_NAMED_SELECTION(?,?,?,?)}";
				preparedstatement = connection.prepareStatement(CALL_BW_GET_NAMES_SELECTION);
				preparedstatement.setInt(1,sharedRowId);
				preparedstatement.setInt(2,sharedColid);
				preparedstatement.setInt(3,importid);
				preparedstatement.setInt(4,userid);

				rs = preparedstatement.executeQuery();

				while ( rs.next() )
				{
					selRowId = rs.getInt(1);
					selColId = rs.getInt(2);
					selTxId = rs.getInt(3);
					sb.append(selRowId + Seperator + selColId + Seperator + selTxId + ContentDelimeter);
				}

				if (!sb.toString().equals(""))
					sbs.append("Success" + ContentDelimeter + sb.substring(0, sb.length()-1));
				else
					sbs.append("Success" + ContentDelimeter);
				
			}
			catch( SQLException sqe )
			 {
				sbs.append("Failure" + ContentDelimeter);
				sqe.printStackTrace();
			 }
			finally
			{
				try
				{
					if(connection != null)
						connection.close();
				}
				catch( SQLException sql2 )
				{
						sql2.printStackTrace();
				}
			}
			return sbs.toString();

		}



};
