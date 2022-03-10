// Decompiled by Decafe PRO - Java Decompiler
// Classes: 1   Methods: 7   Fields: 1

package com.boardwalk.database;

import java.io.PrintStream;
import java.sql.*;
import java.util.Properties;
import java.io.*;
import javax.sql.DataSource;
import java.lang.Runtime;

import org.apache.commons.codec.binary.Base64;

public class DatabaseLoader
{

    public static boolean  databaseInit = false;
    public static String databasetype = null;
    public static QueryLoader queryLoader = null;
    public static String databasename = null;
	public static String InstanceName = null;
	public static String user = null;
	public static String password = null;
	public static String credentialsEncrypted = null; //Added by Lakshman on 20190118 to fix the Issue Id 15721
	public static String server = null;
	public static String port = null;
	public static String sqlpath = null;
	public static String sourcexml = null;
	public static String targetxml = null;
    public static String jdbcConnectionString = null;
	public static String databaseStatus = null;
	public static javax.servlet.ServletContext servletcontext = null;
	public static String templatedir = null;
	
	private static boolean enableSsl = false;
	private static boolean encrypt = false;
	private static boolean trustServerCertificate = false;
	private static String hostNameInCertificate = "";

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
			System.out.println("Inside DatabaseLoader.initDatabase");
			if ( ! databaseInit && properties.size() > 0 )
			{
				databaseInit = true;
				// DriverManager.setLogWriter(null);

				databasename = properties.getProperty("databasename");
				InstanceName = properties.getProperty("InstanceName");
				user = properties.getProperty("user");
				password = properties.getProperty("password");
				credentialsEncrypted = properties.getProperty("credentialsEncrypted");
				server = properties.getProperty("server");
				port = properties.getProperty("port");
				databasetype = properties.getProperty("databasetype");
				sqlpath = properties.getProperty("sqlpath");
				templatedir = properties.getProperty("templatedir");
				sourcexml = properties.getProperty("sourcexml");
				targetxml = properties.getProperty("targetxml");

				if (properties.containsKey("enableSsl")) {
					if (Boolean.parseBoolean(properties.getProperty("enableSsl"))) {
						enableSsl = true;
						encrypt = Boolean.parseBoolean(properties.getProperty("encrypt"));
						trustServerCertificate = Boolean.parseBoolean(properties.getProperty("trustServerCertificate"));
						hostNameInCertificate = properties.getProperty("hostNameInCertificate");
					}
				}

				//Added by Lakshman on 20190118 to fix the Issue Id 15721
				if(Boolean.parseBoolean(credentialsEncrypted))
				{
					System.out.println("Inside DatabaseLoader. Database credentials are encrypted");

					byte[] userDecrypted = Base64.decodeBase64(user);
					user = new String(userDecrypted);

					byte[] passwordDecrypted = Base64.decodeBase64(password);
					password = new String(passwordDecrypted);
					
					//System.out.println("Inside DatabaseLoader. Database credentials: " + user + '/' + password);
				}

				//DriverManager.registerDriver(new com.microsoft.jdbc.sqlserver.SQLServerDriver());
				//jdbcConnectionString = "jdbc:microsoft:sqlserver://"+server+":"+port+";DatabaseName="+databasename+";sendStringParametersAsUnicode=true"+ ";user="+user+";password="+password;

				//Class.forName("net.sourceforge.jtds.jdbc.Driver");
				//jdbcConnectionString = "jdbc:jtds:sqlserver://" + server + ":" + port;

				/*
				//Code for SSL - START
				//With JTDS
				Class.forName("net.sourceforge.jtds.jdbc.Driver");
				jdbcConnectionString = "jdbc:jtds:sqlserver://" + server + ":" + port;
				jdbcConnectionString = jdbcConnectionString + ";DatabaseName=" + databasename + ";encrypt=True;user=" + user + ";password=" + password;
				jdbcConnectionString = jdbcConnectionString + ";ssl=require";

				//With JDBS
				Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
				jdbcConnectionString = "jdbc:sqlserver://" + server + ":" + port;
				jdbcConnectionString = jdbcConnectionString + ";DatabaseName=" + databasename + ";encrypt=True;user=" + user + ";password=" + password;
				//Code for SSL - END
				*/

				/*
				if (!((InstanceName.trim().equals("")) || InstanceName.trim().equalsIgnoreCase("default")))
					jdbcConnectionString = jdbcConnectionString + ";instance=" + InstanceName;
				
				jdbcConnectionString += ";DatabaseName="+databasename+";sendStringParametersAsUnicode=true"+ ";user="+user+";password="+password;
				jdbcConnectionString += ";encrypt=true;hostNameInCertificate=*.database.windows.net;loginTimeout=30";
				//System.out.println("jdbcConnectionString --------------> "+jdbcConnectionString);
				
				jdbcConnectionString="jdbc:jtds:sqlserver://bw-eny-sb.database.windows.net:1433;database=BW-ENY-SB;user=bwappuser@bw-eny-sb;password=Boardwalk@1032";
				jdbcConnectionString+= ";encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;;ssl=require";
				*/

				//090918 g2
				Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver"); 
				jdbcConnectionString = "jdbc:sqlserver://" + server;
				//jdbcConnectionString = jdbcConnectionString + ":" + port;
				
		        if ((!InstanceName.trim().equals("")) && (!InstanceName.trim().equalsIgnoreCase("default"))) {
					jdbcConnectionString = jdbcConnectionString + "\\" + InstanceName;
				} else {
					jdbcConnectionString = jdbcConnectionString + ":" + port;
				}
				
				jdbcConnectionString = jdbcConnectionString + ";DatabaseName=" + databasename + ";loginTimeout=30;sendStringParametersAsUnicode=true" + ";user=" + user;
				
				if (enableSsl) {
					jdbcConnectionString+= ";encrypt=" + encrypt + ";trustServerCertificate=" + trustServerCertificate + ";hostNameInCertificate=" + hostNameInCertificate;
				}
				
		        System.out.println(jdbcConnectionString);
		        jdbcConnectionString+= ";password=" + password;
				queryLoader = new QueryLoader(properties);

				//jdbcConnectionString = "jdbc:microsoft:sqlserver://"+databasehostname+":"+ portnumber+";SelectMethod=cursor;DatabaseName="+databasename+";user="+user+";password="+ password;
				//jdbcConnectionString = "jdbc:odbc:"+ databasename+";user="+user+";password="+password+";EXEC="+"T"+";XSM="+"SA"+";DBQ="+"BW";
			}
	   }
	   catch(Exception exception)
	   {
		   exception.printStackTrace();
	   }
    }

    public DatabaseLoader(Properties properties)
    {
		System.out.println("DatabaseLoader Called");

		//System.out.println("jdbcConnectionString g2 " + jdbcConnectionString);
        if(! databaseInit)
            initDatabase(properties);
    }

	public DatabaseLoader(Properties properties, javax.servlet.ServletContext sc)
	{
		System.out.println("DatabaseLoader Called");

		if(! databaseInit)
		{
			servletcontext = sc;
			initDatabase(properties);
		}
    }

    public static PreparedStatement getPreparedStatementFromPreLoadedQueries(String queryName, Connection connection)
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

	private Connection getConnectionRaw() throws SQLException {
		return  DriverManager.getConnection(jdbcConnectionString);
	}

	public Connection getConnectionBasic() 
		throws SQLException
	{
		Connection conn = null;

		try
		{
   		  	conn =  getConnectionRaw();
	  	}
	  	catch( SQLException sqe )
	  	{
			DatabaseLoader.databaseStatus="There is a Database connection problem. Either the database is down or the connection parameters are wrong.";
			throw sqe;
		}
		DatabaseLoader.databaseStatus = "";
		return conn;
	}

    public Connection getConnection()
        throws SQLException
    {
		Connection conn = null;

		try
		{
   		  	conn =  getConnectionRaw();
   		  	//conn.setAutoCommit(true);

			//Added by Lakshman on 20171214 to set ARITHABORT = ON from Client - START
			try
			{
				System.out.println("Database Connection Established.");

				try (Statement stmt = conn.createStatement())
				{
					try (ResultSet rs = stmt.executeQuery("SELECT CONVERT(INT, SESSIONPROPERTY('ARITHABORT'))"))
					{
						rs.next();
						System.out.println(String.format("SESSIONPROPERTY('ARITHABORT') is %d",rs.getInt(1)));
						
						if(rs.getInt(1) == 0)
						{
							String sql = "SET ARITHABORT ON";
							System.out.println(sql);
							stmt.execute(sql);
							
							//090918 g2
							sql = "SET NOCOUNT ON";
							System.out.println(sql);
							stmt.execute(sql);
						}
					}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace(System.out);
			}
			//Added by Lakshman on 20171214 to set ARITHABORT = ON from Client - END
	  	}
	  	catch( SQLException sqe )
	  	{
			DatabaseLoader.databaseStatus="There is a Database connection problem. Either the database is down or the connection parameters are wrong.";
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
