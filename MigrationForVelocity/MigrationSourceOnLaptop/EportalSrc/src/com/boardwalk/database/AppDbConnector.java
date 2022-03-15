package com.boardwalk.database;

import java.sql.*;
import java.util.*;
import java.io.*;
import java.lang.Runtime;
import javax.sql.DataSource;
import java.util.Properties;
import java.io.PrintStream;

import com.boardwalk.test.*;
import boardwalk.connection.*;
import com.boardwalk.exception.BoardwalkException;

// This class is meant for Process based connections
public class AppDbConnector
{
    public static String msDBServer		= "";
	public static String msDBServerPort = "";
	public static String msDBName		= "";
	public static String msDBUser		= "";
	public static String msDBPassword	= "";

	public static String msUser			= "";
	public static String msPassword		= "";

	public static String msViewPref		= "";
	public static String msSortPref		= "";
	public static String msMemberId		= "";
	public static int msUserId			= -1;
	public static String msCollabDescription	= "";

	public static String sqlpath		= "";
    public static String jdbcConnectionString = null;
	public Connection conn=null;

	public static Properties configProp = null;
    public static Connection connection = null;

	public AppDbConnector(Properties apProperties)
	{
		try
		{
			AppDbConnPropLoader appDbConnPropLoader = new AppDbConnPropLoader();

			msDBServer		= appDbConnPropLoader.getStringPropsValue("servermachine", "", apProperties);
			msDBServerPort  = appDbConnPropLoader.getStringPropsValue("serverport", "", apProperties);
			msDBName		= appDbConnPropLoader.getStringPropsValue("serverdbname", "", apProperties);
			msDBUser		= appDbConnPropLoader.getStringPropsValue("userdbname", "", apProperties);
			msDBPassword	= appDbConnPropLoader.getStringPropsValue("userdbpassword", "", apProperties);
			msUser			= appDbConnPropLoader.getStringPropsValue("username", "su@nokia.com", apProperties);
			msPassword		= appDbConnPropLoader.getStringPropsValue("userpassword", "su", apProperties);
			msMemberId		= appDbConnPropLoader.getStringPropsValue("usermemberid", "-1", apProperties);

			msViewPref		= appDbConnPropLoader.getStringPropsValue("viewpref", "", apProperties);
			msSortPref		= appDbConnPropLoader.getStringPropsValue("sortpref", "", apProperties);
			msUserId			= appDbConnPropLoader.getIntPropsValue("userid", -1, apProperties);
			msCollabDescription	= appDbConnPropLoader.getStringPropsValue("collabdescription", "", apProperties);
		}
		catch(Exception e)
		{
			System.out.println("Problem registering JDBC driver 1");
		}
	}

	public static int getUserId()
	{
		return msUserId;
	}

	public static BoardwalkConnection getConnection(String asMemberId) throws SQLException
	{
        try
        {
           // DriverManager.registerDriver(new com.microsoft.jdbc.sqlserver.SQLServerDriver());
		   Class.forName("net.sourceforge.jtds.jdbc.Driver");
        }
        catch(Exception e)
        {
            System.out.println("Problem registering JDBC driver 2");
        }
        //String jdbcConnectionString = "jdbc:microsoft:sqlserver://"+msDBServer+":"+msDBServerPort+";DatabaseName="+msDBName+";user="+msDBUser+";password="+msDBPassword+";SelectMethod=direct";
		  String jdbcConnectionString = "jdbc:jtds:sqlserver://"+msDBServer+":"+msDBServerPort+";DatabaseName="+msDBName+";sendStringParametersAsUnicode=true"+ ";user="+msDBUser+";password="+msDBPassword;
        try
        {
            connection =  DriverManager.getConnection(jdbcConnectionString);
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