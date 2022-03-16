package com.boardwalk.table;

import java.sql.*;
import javax.sql.*;
import com.boardwalk.database.*;
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import com.boardwalk.exception.*;
import com.boardwalk.database.DatabaseLoader;


public abstract class BWTableObjects
{
	String m_tableName;
	public int m_colCount;
	public int m_rowCount;
	int m_tableId;
	public String[][] m_getTableContents;
	public String arrTableValues[][];
	public BWTableObjects()
	{}

	public BWTableObjects(String tableName)
	{
	 m_tableId = getTableId(tableName);
	}


	public void init()
	{
		try
		{

		 m_colCount = getColumnsCount(m_tableId);
		// System.out.println("<<<<<<<<<<<<<<Value of columns >>>>>>>>>>>>"+m_colCount);
		 m_rowCount = getRowsCount(m_tableId);
		 //System.out.println("<<<<<<<<<<<<<<Value of Rows>>>>>>>>>>>>"+m_rowCount);
		 m_getTableContents = getTableElements(m_tableId);
		 //System.out.println("<<<<<<<<<<<<<<Value of Table Elements >>>>>>>>>>>>"+m_getTableContents);

		}

        catch( Exception ex )
        {
			ex.printStackTrace();
		}


	}

	public int getTableId(String a_tableName)
	{
		Connection connection = null;
		int retTableId = 0;
		try
		{
		DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
		connection = databaseloader.getConnection();
		Statement stmt	= connection.createStatement();
		//String lsSql = "SELECT ID FROM BW_TBL WHERE NAME = '"+a_tableName+"'";
		ResultSet rs	= stmt.executeQuery("SELECT ID FROM BW_TBL WHERE NAME = '"+a_tableName+"'");
		while(rs.next())
			{
			retTableId = rs.getInt("ID");
			}
		}
		catch (SQLException sql)
		{
			sql.printStackTrace();

        }
        catch( Exception ex )
        {
			ex.printStackTrace();
		}
        finally
        {
          try
          {
            connection.close();
          }
          catch ( SQLException sql )
          {
            sql.printStackTrace();
          }
		}
		return retTableId;

	}
	public int getColumnsCount(int a_tableId) throws ServletException, IOException
	{
		int retColCount =0;
		Connection connection = null;
		try
		{
			//System.out.println("::::::::Inside the getColumnsCount:::::::::");
		DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
		connection = databaseloader.getConnection();
		Statement stmt	= connection.createStatement();
		String lsSql1 = "SELECT  COUNT(*) COLCOUNT FROM BW_COLUMN WHERE BW_TBL_ID = '"+a_tableId+"' AND IS_ACTIVE = 1 ";
		ResultSet rs1	= stmt.executeQuery(lsSql1);
		while(rs1.next())
			{
			retColCount = rs1.getInt("COLCOUNT");
			}

		}
		catch (SQLException sql)
		{
			sql.printStackTrace();

        }
        catch( Exception ex )
        {
			ex.printStackTrace();
		}
        finally
        {
          try
          {
            connection.close();
          }
          catch ( SQLException sql )
          {
            sql.printStackTrace();
          }
        }
		return retColCount;

	}

	public  int getRowsCount(int a_tableId) throws ServletException, IOException
	{
		int retRowCount = 0;
		Connection connection = null;
		try
		{
			//System.out.println("::::::::Inside the getRowsCount::::::::");
		DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
		connection = databaseloader.getConnection();
		Statement stmt	= connection.createStatement();
		String lsSql2 = "SELECT  COUNT(*) ROW1COUNT FROM BW_ROW WHERE BW_TBL_ID = '"+a_tableId+"' AND IS_ACTIVE = 1 ";
		ResultSet rs2	= stmt.executeQuery(lsSql2);
		while(rs2.next())
			{
			retRowCount = rs2.getInt("ROW1COUNT");
			}

		}
		 catch (SQLException sql)
		{
			sql.printStackTrace();

        }
        catch( Exception ex )
        {
			ex.printStackTrace();
		}
        finally
        {
          try
          {
            connection.close();
          }
          catch ( SQLException sql )
          {
            sql.printStackTrace();
          }
        }
		return retRowCount;
	}

	public String[][] getTableElements(int a_tableId) throws ServletException, IOException
	{

		Connection connection = null;
		boolean retElements = false;
		arrTableValues = new String [m_rowCount][m_colCount];

		try
		{

		//System.out.println("::::::::Inside the getTableElements::::");
		DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
		connection = databaseloader.getConnection();
		Statement stmt	= connection.createStatement();
		String lsSql3 =  " SELECT STRING_VALUE FROM BW_CELL, BW_TBL, BW_ROW, BW_COLUMN "+
						   " WHERE BW_TBL.ID = '"+a_tableId+"' AND BW_CELL.BW_ROW_ID = BW_ROW.ID "+
						   " AND BW_CELL.BW_COLUMN_ID = BW_COLUMN.ID AND BW_ROW.BW_TBL_ID = BW_TBL.ID AND "+
						   " BW_COLUMN.BW_TBL_ID = BW_TBL.ID AND BW_ROW.IS_ACTIVE = 1 AND BW_COLUMN.IS_ACTIVE = 1"+
							"ORDER BY BW_ROW.SEQUENCE_NUMBER,BW_COLUMN.SEQUENCE_NUMBER";
		ResultSet rs3	= stmt.executeQuery(lsSql3);
		retElements = true;

		boolean isNext1 = rs3.next();
		while (isNext1)
		{
		for(int i = 0; i < m_rowCount; i++ )
			{
				for(int j= 0 ; j< m_colCount; j++)
				{
					if(isNext1)
					{
						arrTableValues[i][j] = rs3.getString("STRING_VALUE");
						//System.out.println("??????????values of table contents?????????"+arrTableValues[i][j]);
						isNext1 = rs3.next();
					}

				}
			}
		}
	}
	catch (SQLException sql)
		{
			sql.printStackTrace();

        }
        catch( Exception ex )
        {
			ex.printStackTrace();
		}
        finally
        {
          try
          {
            connection.close();
          }
          catch ( SQLException sql )
          {
            sql.printStackTrace();
          }
        }
		return arrTableValues;
	}

	public static int getNameofNh(String selNhName)
	{
		//System.out.println("value of selNhid for Nameeeeee"+selNhName);
	Connection connection = null;
	int nhId = -1;
	try
	{
		DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
		connection = databaseloader.getConnection();
		Statement stmt	= connection.createStatement();
	    String query = "SELECT ID FROM BW_NH WHERE NAME = '"+selNhName+"'";
		ResultSet rs4	= stmt.executeQuery(query);
		while (rs4.next())
		{
			nhId = rs4.getInt("ID");
		}

	}
	catch( Exception ex )
        {
			ex.printStackTrace();
		}
		return nhId;
	}



}

