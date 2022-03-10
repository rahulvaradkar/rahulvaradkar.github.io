package com.boardwalk.database;

import java.sql.*;
import java.util.*;
import java.io.*;
import java.lang.Runtime;
import javax.sql.DataSource;
import java.util.Properties;
import java.io.PrintStream;

//import com.boardwalk.test.*;
import boardwalk.connection.*;
import com.boardwalk.exception.BoardwalkException;

// This class is meant for Process based connections
public class AppDbConnector
{
    static String msDBServer		= "";
	static String msDBServerPort = "";
	static String msDBName		= "";
	static String msInstanceName = "";//Changed by Santosh(Yoddha) to accept Instance Name
	static String msDBUser		= "";
	static String msDBPassword	= "";

	static String msUser			= "";
	static String msPassword		= "";

	static String msViewPref		= "";
	static String msSortPref		= "";
	static String msMemberId		= "";
	static int msUserId			= -1;
	static String msCollabDescription	= "";

	static String sqlpath		= "";
    static String jdbcConnectionString = null;

	static Properties configProp = null;
	static Connection connection = null;
	static DatabaseLoader dbLoader = null;
	

	public AppDbConnector(Properties apProperties)
	{
		try
		{
			AppDbConnPropLoader appDbConnPropLoader = new AppDbConnPropLoader();

			msDBServer		= appDbConnPropLoader.getStringPropsValue("servermachine", "", apProperties);
			msDBServerPort  = appDbConnPropLoader.getStringPropsValue("serverport", "", apProperties);
			msDBName		= appDbConnPropLoader.getStringPropsValue("serverdbname", "", apProperties);
			msInstanceName	= appDbConnPropLoader.getStringPropsValue("InstanceName", "", apProperties);//Changed by Santosh(Yoddha) to accept Instance Name
			msDBUser		= appDbConnPropLoader.getStringPropsValue("userdbname", "", apProperties);
			msDBPassword	= appDbConnPropLoader.getStringPropsValue("userdbpassword", "", apProperties);
			msUser			= appDbConnPropLoader.getStringPropsValue("username", "su@nokia.com", apProperties);
			msPassword		= appDbConnPropLoader.getStringPropsValue("userpassword", "su", apProperties);
			msMemberId		= appDbConnPropLoader.getStringPropsValue("usermemberid", "-1", apProperties);

			msViewPref		= appDbConnPropLoader.getStringPropsValue("viewpref", "", apProperties);
			msSortPref		= appDbConnPropLoader.getStringPropsValue("sortpref", "", apProperties);
			msUserId			= appDbConnPropLoader.getIntPropsValue("userid", -1, apProperties);
			msCollabDescription	= appDbConnPropLoader.getStringPropsValue("collabdescription", "", apProperties);

			CreateDatabaseLoader();
		}
		catch(Exception e)
		{
			System.out.println("Problem registering JDBC driver 1");
		}
	}

	private void CreateDatabaseLoader() {
		Properties databaseProps  = new Properties();
		databaseProps.setProperty( "databasename" , msDBName);
		databaseProps.setProperty( "InstanceName" , msInstanceName);
		databaseProps.setProperty( "user" ,  msDBUser);
		databaseProps.setProperty( "password" , msDBPassword);
		databaseProps.setProperty( "server" , msDBServer);
		databaseProps.setProperty( "port" , msDBServerPort);

		AppDbConnector.dbLoader = new DatabaseLoader(databaseProps);
}

	public static int getUserId()
	{
		return msUserId;
	}

	public static BoardwalkConnection getConnection(String asMemberId) throws SQLException
	{
	  	try
        {
			connection =  dbLoader.getConnectionBasic();
            System.out.println("Connection established successfully");
        }
        catch( SQLException sqe )
        {
            System.out.println("There is a Database connection problem "+jdbcConnectionString);
        }

        // Get an authenticated boardwalk connection
        BoardwalkConnection bwcon = null;
        try
        {
			if(asMemberId.equals(""))
				asMemberId = msMemberId;
            bwcon = BoardwalkConnectionManager.getBoardwalkConnection(connection, msUser, msPassword, (new Integer(asMemberId)).intValue());
            System.out.println("Successfully obtained authenticated Boardwalk connection");
        }
        catch(BoardwalkException bwe)
        {
            System.out.println("Authentication/Connection Failed");
        }

		return bwcon;
	}
}