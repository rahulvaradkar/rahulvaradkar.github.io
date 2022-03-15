// Decompiled by Decafe PRO - Java Decompiler
// Classes: 1   Methods: 7   Fields: 1

package com.boardwalk.database;

import java.io.PrintStream;
import java.sql.*;
import java.util.Properties;
import java.io.*;
import javax.sql.DataSource;
import java.lang.Runtime;

public class DatabaseLoader
{

    public static boolean  databaseInit = false;
    //public static String databasehostname = null;
//    public static String portnumber = null;
    public static String databasetype = null;
    public static QueryLoader queryLoader = null;
    public static String databasename = null;
	public static String InstanceName = null;
	public static String user = null;
	public static String password = null;
	public static String server = null;
	public static String port = null;
	public static String sqlpath = null;
	public static String sourcexml = null;
	public static String targetxml = null;
    public static String jdbcConnectionString = null;
	public static String databaseStatus = null;
	public static javax.servlet.ServletContext servletcontext = null;
    public static String templatedir = null;

    // Boardwalk defaults
    public static boolean default_column_access = false;
    public static int default_column_access_creator = 2;
	public static int default_column_access_private = 2;
	public static int default_column_access_domain = 2;
	public static int default_column_access_children = 2;
	public static int default_column_access_custom = 2;


   public static void initDatabase(Properties properties)
   {
	   try
	   {
			System.out.println("initDatabase calledd*****************************");
			if ( ! databaseInit && properties.size() > 0 )
			{
				databaseInit = true;
				 // DriverManager.setLogWriter(null);

				databasename = properties.getProperty("databasename");
				InstanceName = properties.getProperty("InstanceName");
				user = properties.getProperty("user");
				password = properties.getProperty("password");
				server = properties.getProperty("server");
				port = properties.getProperty("port");
				databasetype = properties.getProperty("databasetype");
				sqlpath = properties.getProperty("sqlpath");
				templatedir = properties.getProperty("templatedir");
				sourcexml = properties.getProperty("sourcexml");
				targetxml = properties.getProperty("targetxml");

				//DriverManager.registerDriver(new com.microsoft.jdbc.sqlserver.SQLServerDriver());
				//jdbcConnectionString = "jdbc:microsoft:sqlserver://"+server+":"+port+";DatabaseName="+databasename+";sendStringParametersAsUnicode=true"+ ";user="+user+";password="+password;
				Class.forName("net.sourceforge.jtds.jdbc.Driver");
				jdbcConnectionString = "jdbc:jtds:sqlserver://"+server+":"+port;
				
				if (!((InstanceName.trim().equals("")) || InstanceName.trim().equalsIgnoreCase("default"))) jdbcConnectionString = jdbcConnectionString + ";instance=" + InstanceName;
				
				jdbcConnectionString = jdbcConnectionString + ";DatabaseName="+databasename+";sendStringParametersAsUnicode=true"+ ";user="+user+";password="+password;
				System.out.println("jdbcConnectionString --------------> "+jdbcConnectionString);
				queryLoader = new  QueryLoader(properties);

			// jdbcConnectionString = "jdbc:microsoft:sqlserver://"+databasehostname+":"+ portnumber+";SelectMethod=cursor;DatabaseName="+databasename+";user="+user+";password="+ password;
			// odbc--> jdbcConnectionString = "jdbc:odbc:"+ databasename+";user="+user+";password="+password+";EXEC="+"T"+";XSM="+"SA"+";DBQ="+"BW";

			}
	   }
	   catch(Exception exception)
	   {
		   exception.printStackTrace();
	   }
    }

    public DatabaseLoader(Properties properties)
    {
		System.out.println("DatabaseLoader called*****************************");
		System.out.println("jdbcConnectionString g2 " + jdbcConnectionString);
        if(! databaseInit)
            initDatabase(properties);
    }


	public DatabaseLoader(Properties properties, javax.servlet.ServletContext sc)
	{
		System.out.println("DatabaseLoader called*****************************");
		if(! databaseInit)
		{
			servletcontext = sc;
			initDatabase(properties);
		}
    }

    public static PreparedStatement getPreparedStatementFromPreLoadedQueries(String queryName, Connection connection )
    throws SQLException
    {

		String query = queryLoader.getQueryString(queryName);
		PreparedStatement preparedstatement = connection.prepareStatement(query);
		return preparedstatement;

	}

	public static String getDatabaseType()
    {
		return databasetype;
	}

	public static String getSQLPath()
	{
			return sqlpath;
	}

	public static String getSourceDistributionXML()
	{
		return sourcexml;
	}

	public static String getTargetDistributionXML()
	{
		return targetxml;
	}

    public Connection getConnection()
        throws SQLException
    {
		Connection conn = null;
		try
		{
   		  	conn =  DriverManager.getConnection(jdbcConnectionString);
   		  	//conn.setAutoCommit(true);
	  	}
	  	catch( SQLException sqe )
	  	{
			DatabaseLoader.databaseStatus="There is a Database connection problem, either the database is down or the connection parameters are wrong";
			throw sqe;
		}
	     DatabaseLoader.databaseStatus = "";
		 return conn;
    }

    public static void main(String args[])
        throws Exception
    {
        DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
        Connection conn = databaseloader.getConnection();
    }

}










