package servlets;

/*
 * Sarang 06/27/05
 * Manage Boardwalk Collaborations
 *
 *
 */

import java.io.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import com.boardwalk.database.DatabaseLoader;
import com.boardwalk.exception.NeighborhoodException;
import com.boardwalk.exception.SystemException;
import com.boardwalk.collaboration.CollaborationManager;
import com.boardwalk.collaboration.Collaboration;
import com.boardwalk.collaboration.CollaborationTreeNode;
import com.boardwalk.whiteboard.WhiteboardManager;
import com.boardwalk.whiteboard.WhiteboardTreeNode;
import com.boardwalk.neighborhood.NeighborhoodManager;
import com.boardwalk.database.*;
import com.boardwalk.table.*;
import com.boardwalk.excel.*;
import com.boardwalk.user.UserManager;
import com.boardwalk.member.Member;
import com.boardwalk.exception.*;

import java.util.Date;
import java.sql.*;                  // JDBC package
import javax.sql.*;                 // extended JDBC packa


public class http_vb_getTableInfo extends xlService
implements SingleThreadModel
{

	public final static String Seperator = new Character((char)1).toString();
	public final static String ContentDelimeter = new Character((char)2).toString();
	StringTokenizer st;
	int userId;
	String userName;
	String userPassword;
	int nhId;
	int  memberId;
	String nhName;
	int tid;
	String m_ViewPreference;
	String m_SortPreference;
	String fileSavedAt;
	int rowCount;
	int columnCount;
	int transactionId;
	xlError xle;

    public void service (HttpServletRequest request,
	    					HttpServletResponse response) 
	throws ServletException, IOException
    {
		xle = null;
        BoardwalkMessages bwMsgs = new BoardwalkMessages();

		StringBuffer responseToUpdate = new StringBuffer();
        String responseBuffer = null;


		String buf = getRequestBuffer(request).toString();
		st = new StringTokenizer( buf );

		System.out.println("Login User");
        if ( loginUser() )
        {
            int action;
            // 1 : Create a New Collaboration
            // 2 : Create a New Table in a Collaboration
            // 3 : Remove a Table from a Collaboration
            // 4 : Change Properties (Name & Descr) for a table in Collaboration
            // 5 : Get Collaboration (Collab Info and All Tables + Info)
            // 6 : Change Collab Properties
			System.out.println("user is valid");

			String wrkstr;
			// requested action
			wrkstr = st.nextToken (Seperator);
			action = Integer.parseInt(wrkstr);
			if (action == 1)
			{
				getTableInfo(response);
			}



		}
		else
		{
			System.out.println("user is invalid");
			String invalid = new String("userinvalid");
			commitResponseBuffer(invalid, response);
		}
    }




public static int getNhId(String userNh, int level)
	{
	System.out.println("::::::::::::Inside getNhid:::::::::::"+userNh+"level"+level);
	int nhID = -1;
	int Cnt = 0;
	Connection connection = null;
		try
		{
			DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
	        connection = databaseloader.getConnection();
			Statement stmt		= connection.createStatement();
			String query = "SELECT ID FROM BW_NH WHERE NAME = '"+userNh+"' AND NEIGHBORHOOD_LEVEL ='"+level+"'";
			System.out.println("---------------Value of query------"+query);
			ResultSet rs		= stmt.executeQuery("SELECT ID FROM BW_NH WHERE NAME = '"+userNh+"' AND NEIGHBORHOOD_LEVEL ='"+level+"'");
			while (rs.next())
			{
				Cnt++;
				nhID		= rs.getInt("ID");
				System.out.println(":::::::::Value of NhId:::::::::::::"+nhID);
			}
		}
		catch (Exception sqe)
		{
			sqe.printStackTrace();
		}
		if(Cnt > 1)
		{
			System.out.println(":::::::::Value of Cnt:::::::::::::");
			return nhID = 0;
		}
		else
		return nhID ;

    }

	public static int getTargetNhForGivenLevel(int Nhid , int level)
	{
		System.out.println("Value of level"+level);
		Connection connection = null;
		int TargetNhid = -1;
		PreparedStatement preparedstatement = null;
        ResultSet rs = null;
		int a_level_2_id = -1;
			int a_level_3_id = -1;
		try
		{
			DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
	        connection = databaseloader.getConnection();
			 preparedstatement = connection.prepareStatement("{CALL BW_GET_TARGET_NH_FOR_GIVEN_LEVEL(?)}");
             preparedstatement.setInt(1,Nhid);
             rs = preparedstatement.executeQuery();
             while ( rs.next() ) {
				  a_level_2_id = rs.getInt("ID_LEVEL_2");
				  a_level_3_id = rs.getInt("ID_LEVEL_3");
				  System.out.println("**********8Value of LevelNhid**********" +a_level_2_id+ "level3" +a_level_3_id);
			 }
		}
		catch (Exception sqe)
		{
			sqe.printStackTrace();
		}
		if(level == 1)
		{
			 TargetNhid = getTargetNhId(a_level_2_id,level);

			 System.out.println("**********Value of Level1**********");
			 return TargetNhid;
		}
		else
		{
			TargetNhid = getTargetNhId(a_level_3_id,level);
			System.out.println("**********Value of Level**********");
			return TargetNhid;
		}



	}

	public static int getTargetNhId(int a_level_id ,int level)
	{
		Connection connection = null;
		ResultSet rs =null;
		int TargetNhid =-1;
		try
		{
			DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
	        connection = databaseloader.getConnection();
			Statement stmt		= connection.createStatement();
			if(level == 1)
			{
					rs= stmt.executeQuery("SELECT  NEIGHBORHOOD_ID FROM BW_NH_LEVEL_2 WHERE ID = '"+a_level_id+"'");
			}
			else
			{
					rs= stmt.executeQuery("SELECT NEIGHBORHOOD_ID FROM BW_NH_LEVEL_3 WHERE ID = '"+a_level_id+"'");
			}

		  while ( rs.next() )
		  {
			  TargetNhid = rs.getInt("NEIGHBORHOOD_ID");
		  }
		}
		catch (Exception sqe)
		{
			sqe.printStackTrace();
		}
		return TargetNhid;

	}

	public void getTableInfo(HttpServletResponse response)
	{
		 String tablefield = null;
		 Connection connection = null;
		 String wrkstr = st.nextToken(Seperator);
		 try
	        {
			 	DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
	            connection = databaseloader.getConnection();
	            StringBuffer responseToUpdate = new StringBuffer();
				String responseBuffer = null;
				responseToUpdate.append("Success" + Seperator);
				while ( wrkstr != null && !wrkstr.trim().equals (""))
				{

					System.out.println("table string" + wrkstr);
					StringTokenizer tbl = new StringTokenizer(wrkstr);


					tablefield = tbl.nextToken (xlService.ContentDelimeter);
			        int tableId = Integer.parseInt(tablefield);

			        tablefield = tbl.nextToken (xlService.ContentDelimeter);
			        m_ViewPreference = tablefield;

			        tablefield = tbl.nextToken (xlService.ContentDelimeter);
			        int importTid = Integer.parseInt(tablefield);

			        tablefield = tbl.nextToken (xlService.ContentDelimeter);
			        int exportTid = Integer.parseInt(tablefield);

			        tablefield = tbl.nextToken (xlService.ContentDelimeter);
			        fileSavedAt = tablefield;

			        long filesavedAtTimeinMs;
			        java.text.SimpleDateFormat sformat = new java.text.SimpleDateFormat("MM/dd/yyyy hh:mm:ss a z");
					Date fileSavedDate = sformat.parse(fileSavedAt);
					filesavedAtTimeinMs = fileSavedDate.getTime();


			        System.out.println("tableid=" + tableId + "m_viewpref=" +m_ViewPreference + "importtid"+importTid +"exporttid"+exportTid );

			        com.boardwalk.table.TableInfo tInfo = TableManager.getTableInfo( connection,userId,tableId);
		            Hashtable txSinceImport = TableManager.getTransactionList(  connection,
																tableId,
																importTid,
																-1,
																0,
																(new java.util.Date()).getTime(),
																userId,
																nhId,
																m_ViewPreference,
																false);

		            System.out.println("got x new transactions" + txSinceImport.size());

					Vector txids = new Vector(txSinceImport.keySet());
					Collections.sort(txids);
					Vector lastTransaction = null;

					if ( exportTid < 1  )
					{
						exportTid = importTid;
					}

					String importTxTime = TransactionManager.getTransactionTimeStamp(connection,importTid);



					String exportTxTime = TransactionManager.getTransactionTimeStamp(connection,exportTid);

					Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
					long currentTimeMs = cal.getTime().getTime();
					System.out.println("currenttime in millis" + cal.getTime().getTime());
					String elpTime = "";
					if ( txSinceImport.size() > 0 )
					{
						lastTransaction= (Vector) txSinceImport.get( txids.elementAt(txids.size()-1) );
						com.boardwalk.database.Transaction t = (com.boardwalk.database.Transaction)lastTransaction.elementAt(0);
						//if ( t.getId() > importTid )
						//{
							// client needs to be updated...
							responseToUpdate.append(tInfo.getTableId() + ContentDelimeter);
				   			responseToUpdate.append(tInfo.getTableName() + ContentDelimeter);
				   			responseToUpdate.append(tInfo.getTablePurpose() + ContentDelimeter);
				   			responseToUpdate.append(t.getId()+ ContentDelimeter);
				   			responseToUpdate.append(t.getCreatedByUserAddress()+ ContentDelimeter);
				   			responseToUpdate.append(t.getCreatedOn()+ ContentDelimeter);
				   			responseToUpdate.append(t.getComment()+ ContentDelimeter);
				   			//responseToUpdate.append(timeDifference(t.getCreatedOn(),currentTimeMs, false )+ ContentDelimeter);
				   			responseToUpdate.append(txSinceImport.size() + ContentDelimeter);

				   			responseToUpdate.append(importTid+ ContentDelimeter);
				   			responseToUpdate.append(importTxTime+ ContentDelimeter);
				   			//responseToUpdate.append(timeDifference(importTxTime,currentTimeMs ,false)+ ContentDelimeter);
				   			responseToUpdate.append(exportTid+ ContentDelimeter);
				   			responseToUpdate.append(exportTxTime+ ContentDelimeter);
				   			//responseToUpdate.append(timeDifference(exportTxTime,currentTimeMs,false )+ ContentDelimeter);
				   			//responseToUpdate.append(timeDifference(exportTxTime,filesavedAtTimeinMs,false )+ ContentDelimeter);
				   			responseToUpdate.append(Seperator);
						//}
					}
					else
					{


						responseToUpdate.append(tInfo.getTableId() + ContentDelimeter);
			   			responseToUpdate.append(tInfo.getTableName() + ContentDelimeter);
			   			responseToUpdate.append(tInfo.getTablePurpose() + ContentDelimeter);
			   			responseToUpdate.append(importTid+ ContentDelimeter);
			   			responseToUpdate.append(importTxTime+ ContentDelimeter);
			   			//responseToUpdate.append(timeDifference(importTxTime,currentTimeMs,false )+ ContentDelimeter);
			   			responseToUpdate.append(exportTid+ ContentDelimeter);
			   			responseToUpdate.append(exportTxTime+ ContentDelimeter);
			   			//responseToUpdate.append(timeDifference(exportTxTime,currentTimeMs,false )+ ContentDelimeter);
			   			//responseToUpdate.append(timeDifference(exportTxTime,filesavedAtTimeinMs,false )+ ContentDelimeter);
			   			responseToUpdate.append(Seperator);

					}
					try
					{
						wrkstr = st.nextToken(Seperator);
					}
					catch(java.util.NoSuchElementException ne)
					{
						wrkstr = null;
					}
				}
				responseBuffer = responseToUpdate.toString();
				commitResponseBuffer(responseBuffer, response);
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                connection.close();
            }
            catch( SQLException sql )
            {
                sql.printStackTrace();
            }

        }

	}

	private String timeDifference(String start, long currentTimeMs, boolean returnAsString )
	{
		//start = "2005-11-21 01:38:01";



		try
		{
			java.text.SimpleDateFormat sformat = new java.text.SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS z");
			Date startDate = sformat.parse(start + " GMT");

			System.out.println("Compare startlong=" + startDate.getTime() + " endlong=" + currentTimeMs);
			System.out.println("diff end-start=" + ( currentTimeMs - startDate.getTime()));
			int timediffinsecs = (int)(currentTimeMs - startDate.getTime())/1000;
			if ( returnAsString == true )
			{
				return calcHMS(timediffinsecs);
			}
			else
			{
				return ""+timediffinsecs;
			}
		}
		catch( Exception e )
		{
			e.printStackTrace();
			return "";
		}
	}


	private String calcHMS(int timeInSeconds) {
	      int hours, minutes, seconds, days, years;
	      days = 0;
	      years = 0;

	      hours = timeInSeconds / 3600;
	      timeInSeconds = timeInSeconds - (hours * 3600);
	      minutes = timeInSeconds / 60;
	      timeInSeconds = timeInSeconds - (minutes * 60);
	      seconds = timeInSeconds;

	      if ( hours > 24 )
	      days = hours/24;

	      if ( days > 365 )
		  years = days/365;

	      String timeelapsed = "";

	      if ( years > 0 )
	      {
	    	  timeelapsed = years + " years ago";
	      }
	      else
	      {
	    	  if ( days > 0 )
	    	  {
	    		  timeelapsed = days + " days ago";
	    	  }
	    	  else
	    	  {
	    		  if ( hours > 0 )
	    		  {
	    			  timeelapsed = hours + " hour " + minutes + " minutes(s) ago";
	    		  }
	    		  else
	    			  if ( minutes > 0 )
	    				  timeelapsed = minutes + " minute(s) ago";
	    			  else
	    				  timeelapsed = timeInSeconds + " sec(s) ago";
	    	  }
	      }
	      return timeelapsed;
	  }

    public boolean  loginUser()
	{


		String wrkstr;
		wrkstr = st.nextToken (Seperator);
		userId = Integer.parseInt(wrkstr);

		wrkstr = st.nextToken (Seperator);
		userName = wrkstr;

		wrkstr = st.nextToken (Seperator);
		userPassword = wrkstr;

		wrkstr = st.nextToken (Seperator);
		memberId = Integer.parseInt(wrkstr);

		wrkstr = st.nextToken (Seperator);
		nhId = Integer.parseInt(wrkstr);

		wrkstr = st.nextToken (Seperator);
		nhName =wrkstr;

		Connection connection = null;
		try
		{
			DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
			connection = databaseloader.getConnection();

			if (  userName == null  || userName == ""  || userPassword ==null || userPassword == "" )
			{
				return false;
			}
			else
			{
				System.out.println("Authenticating User : " + userName + ":" + userPassword);

				int db_userId = UserManager.authenticateUser(connection, userName,userPassword);

				if ( userId != -1 && userId == db_userId )
				{
					return true;
				}
				else
				{
					return false;
				}

			}
		}
		catch ( Exception e )
		{
		   e.printStackTrace();
		   return false;
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
			return false;
		  }
			// System.out.println("End loginUser : " + getElapsedTime());
		}
	}

}
