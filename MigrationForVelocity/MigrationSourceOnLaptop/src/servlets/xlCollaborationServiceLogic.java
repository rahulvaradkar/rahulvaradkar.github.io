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


public class xlCollaborationServiceLogic extends xlServiceLogic
{

	public final static String Seperator = new Character((char)1).toString();
	public final static String ContentDelimeter = new Character((char)2).toString();
	StringTokenizer st;
	int userId;
	String userName;
	//String userPassword; //Modified by Tekvision on 20180207 for Clear Text Password(Issue Id: 14241)
	int nhId;
	int  memberId;
	String nhName;
	int tid;
	String m_ViewPreference;
	String m_SortPreference;
	int rowCount;
	int columnCount;
	int transactionId;
    xlError xle;
    
    public xlCollaborationServiceLogic(xlCollaborationService srv) {
        super(srv);
    }

    public void service (HttpServletRequest request,
	    					HttpServletResponse response)
	throws ServletException, IOException
    {
		xle = null;
        StringBuffer sb = new 	StringBuffer ();
        BoardwalkMessages bwMsgs = new BoardwalkMessages();

		StringBuffer responseToUpdate = new StringBuffer();
        String responseBuffer = null;

		String buf = getRequestBuffer(request).toString();
		//System.out.println("Buffer" + buf);

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
			System.out.println("Next token "+ wrkstr);
			action = Integer.parseInt(wrkstr);
			try
			{
				if (action == 1)
				{
					responseBuffer = createCollab();
				}
				else if (action == 2)
				{
					responseBuffer = addNewTable();
				}

				else if (action == 3)
				{
					responseBuffer = removeTable();
				}
				else if (action == 4)
				{
					responseBuffer = commitTableProperties();
				}
				else if (action == 5)
				{
					responseBuffer = editCollab();
				}
			//	else if (action == 6)
				//{
				//	updateProperties(response);
			//	}
				else if (action == 7)
				{
					responseBuffer = getCollabsForNeighborhood();
				}
			//	else if (action ==8)
			//	{
			//		getCollabForTable(response);
			//	}
				else if (action == 9)
				{
					responseBuffer = getCollabForUser();
				}
				else if (action == 10)
				{
					responseBuffer = addNewTableForCollabName();
				}
				commitResponseBuffer(responseBuffer, response);
			}
			catch (BoardwalkException bwe)
			{
				try
				{
					responseToUpdate.append("Failure");
					responseBuffer = responseToUpdate.toString();

					responseToUpdate.append( Seperator + bwe.getMessage());

					responseBuffer = responseToUpdate.toString();
					commitResponseBuffer(responseBuffer, response);
				}
				catch (IOException ioe)
				{
					ioe.printStackTrace();
				}

			}

		}
		else
		{
			System.out.println("user is invalid");
			commitResponseBuffer(new String("userinvalid"), response);
		}
    }

    public String createCollab()
    throws BoardwalkException
    {
		String collabName = null;
		String collabDesc = null;
		StringTokenizer st2;
		int m_collab_n_wb[] = new int[2];
		m_collab_n_wb[0] = -1;
		m_collab_n_wb[1] = -1;
		StringBuffer responseToUpdate = new StringBuffer();
		String responseBuffer = null;

		try
		{
			String wrkstr;
			wrkstr = st.nextToken (Seperator);
			st2 = new StringTokenizer(wrkstr);
			collabName = st2.nextToken (ContentDelimeter);
			try
			{
				collabDesc = st2.nextToken (ContentDelimeter);
			}
			catch (Exception e)
			{
				collabDesc = "";
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}

		// Call
		TransactionManager tm = null;
		Connection connection = null;

		try
		{
			DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
			connection = databaseloader.getConnection();
			tm = new TransactionManager(connection, userId);
			tid = tm.startTransaction();

			System.out.println("Creating Collaboration : " + collabName);
			m_collab_n_wb[0] = CollaborationManager.createCollaboration(
															connection,
															collabName,
															collabDesc,
															memberId, // member id
															tid, // transaction id
															1 // status
															);



			tm.commitTransaction();

			responseToUpdate.append("Success");
			responseToUpdate.append( Seperator + m_collab_n_wb[0] + Seperator +
												 m_collab_n_wb[1] + Seperator);
			responseBuffer = responseToUpdate.toString();
			System.out.println("Response = " + responseBuffer);
			return responseBuffer;
		}
		catch ( Exception e1 )
		{
			try
			{
				tm.rollbackTransaction();
			} catch (Exception e){
				e.printStackTrace();
			}
			e1.printStackTrace();
			return null;
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

	}

	public String addNewTable()
	throws BoardwalkException
	{
		int collabId = -1;
		int wbId = -1;
		String tableName = null;
		String tableDesc = null;
		String ViewPreference = null;
		StringBuffer responseToUpdate = new StringBuffer();
		String responseBuffer = null;
		StringTokenizer st2;
		int a_table_id = -1;

		try
		{
			String wrkstr;
			wrkstr = st.nextToken (Seperator);
			collabId = Integer.parseInt(wrkstr);
			wrkstr = st.nextToken (Seperator);
			wbId = Integer.parseInt(wrkstr);
			wrkstr = st.nextToken (Seperator);
			st2 = new StringTokenizer(wrkstr);
			tableName = st2.nextToken (ContentDelimeter);
			try
			{
				tableDesc = st2.nextToken (ContentDelimeter);
			}
			catch (Exception e)
			{
				tableDesc = "";
			}
			ViewPreference = st2.nextToken (ContentDelimeter);
		}
		catch (NoSuchElementException nsee)
		{
			nsee.printStackTrace();
			return null;
		}

		// Call
		TransactionManager tm = null;
		Connection connection = null;

		try
		{
			DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
			connection = databaseloader.getConnection();
			tm = new TransactionManager(connection, userId);
			tid = tm.startTransaction();

			a_table_id = TableManager.createTable (
									   connection,
									   wbId,
									   tableName,
									   tableDesc,
									   2, 1, 1,ViewPreference,
									   memberId,
									   tid,
									   1
									 );

			Vector accessLists = new Vector();

			Hashtable  relationships = NeighborhoodManager.getNeighborhoodRelationships( connection, nhId);

			//CREATOR, PUBLIC

			Enumeration relationKeys = relationships.keys();

			if ( relationships.size() > 0 )
			{
				while ( relationKeys.hasMoreElements() )
				{
					String relationship = (String)relationKeys.nextElement();

					NewTableAccessList accessList = new NewTableAccessList(-1, a_table_id,relationship);

					if ( relationship.equals("PRIVATE") )
					{
						accessList.setAddRow();
						accessList.setDeleteRow();
						accessList.setReadLatestOfTable();
						accessList.setWriteLatestOfTable();
						accessList.setReadWriteLatestOfMyRows();
					}
					accessLists.add(accessList );

				}
			}

			// sak 12/06/09 increased public access for pwc
			NewTableAccessList publicAccessList = new NewTableAccessList(-1,a_table_id,"PUBLIC");
			//creatorAccessList.setAdministerTable();
			publicAccessList.setAdministerColumn();
			publicAccessList.setAddRow();
			publicAccessList.setDeleteRow();
			publicAccessList.setReadLatestOfTable();
			publicAccessList.setWriteLatestOfTable();
			publicAccessList.setReadWriteLatestOfMyRows();

			NewTableAccessList creatorAccessList = new NewTableAccessList(-1,a_table_id,"CREATOR");
			creatorAccessList.setAdministerTable();
			creatorAccessList.setAdministerColumn();
			creatorAccessList.setAddRow();
			creatorAccessList.setDeleteRow();
			creatorAccessList.setReadLatestOfTable();
			creatorAccessList.setWriteLatestOfTable();
			creatorAccessList.setReadWriteLatestOfMyRows();

			accessLists.add(	creatorAccessList );
			accessLists.add(	publicAccessList);


			if ( accessLists.size()  > 0 )
			{

			  TableManager.addAccesstoTable
									(
									connection,
									a_table_id,
									accessLists,
									tid
									);

			}

			tm.commitTransaction();

			responseToUpdate.append("Success");
			responseToUpdate.append( Seperator + a_table_id + Seperator);
			responseBuffer = responseToUpdate.toString();
			System.out.println("Response = " + responseBuffer);
			return responseBuffer;
		}
		catch ( Exception e1 )
		{
			try
			{
				tm.rollbackTransaction();
			} catch (Exception e){
				e.printStackTrace();
			}
			e1.printStackTrace();
			return null;
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
	}



public String addNewTableForCollabName()
	throws BoardwalkException
	{
		int collabId = -1;
		int wbId = -1;
		String collabName;
		String WhiteboardName;

		String tableName = null;
		String tableDesc = null;
		String ViewPreference = null;
		StringBuffer responseToUpdate = new StringBuffer();
		String responseBuffer = null;
		StringTokenizer st2;
		int a_table_id = -1;

	// Call
		TransactionManager tm = null;
		Connection connection = null;

		try
		{

			DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
			connection = databaseloader.getConnection();


			String wrkstr;
			wrkstr = st.nextToken (Seperator);
			collabName = wrkstr;
			System.out.println("Looking for collabName " + collabName + " for nhid " + nhId );
			collabId = CollaborationManager.getCollabIdByName(connection, collabName, nhId);
			System.out.println("collabId for collabName = " + collabId );

			wrkstr = st.nextToken (Seperator);
			WhiteboardName = wrkstr;
			//Vector WbVec = ((com.boardwalk.collaboration.CollaborationTreeNode)WhiteboardManager.getWhiteboardAndTablesByCollaborationAndNeighborhood( connection, collabId, memberId).elementAt(0)).getWhiteboards();;

			//Iterator wIter = WbVec.iterator();
			//System.out.println("Looking for whiteboard " + WhiteboardName );
			//while (wIter.hasNext())
			//{
			//    WhiteboardTreeNode wbtn = (WhiteboardTreeNode)wIter.next();
			//    System.out.println("Found whiteboard with name " + wbtn.getName() );
			//    if ( wbtn.getName().equalsIgnoreCase(WhiteboardName)  )
			//    {
			//        wbId = wbtn.getId();
			//        System.out.println("Match Found whiteboard " + wbId );
			//        break;
			//    }
			//}
			wbId = WhiteboardManager.getIdByCollabAndName(connection, collabId, WhiteboardName);
			System.out.println("wbId for WhiteboardName = " + collabId);
			if (collabId != -1 && wbId != -1)
			{
				wrkstr = st.nextToken(Seperator);
				st2 = new StringTokenizer(wrkstr);
				tableName = st2.nextToken(ContentDelimeter);
				//Added by sujith for Table descreption
				wrkstr = st.nextToken(Seperator);
				st2 = new StringTokenizer(wrkstr);
				//Added by sujith for Table descreption
				try
				{
					tableDesc = st2.nextToken(ContentDelimeter);
				}
				catch (Exception e)
				{
					tableDesc = "";
				}
				ViewPreference = "LATEST";



				Hashtable relationships = NeighborhoodManager.getNeighborhoodRelationships(connection, nhId);

				System.out.println("Creating a table " + tableName);

				tm = new TransactionManager(connection, userId);
				tid = tm.startTransaction();
				a_table_id = TableManager.createTable(
										   connection,
										   wbId,
										   tableName,
										   tableDesc,
										   2, 1, 1, ViewPreference,
										   memberId,
										   tid,
										   1
										 );

				Vector accessLists = new Vector();



				//CREATOR, PUBLIC

				Enumeration relationKeys = relationships.keys();

				if (relationships.size() > 0)
				{
					while (relationKeys.hasMoreElements())
					{
						String relationship = (String)relationKeys.nextElement();

						NewTableAccessList accessList = new NewTableAccessList(-1, a_table_id, relationship);

						if (relationship.equals("PRIVATE"))
						{
							accessList.setAddRow();
							accessList.setDeleteRow();
							accessList.setReadLatestOfTable();
							accessList.setWriteLatestOfTable();
							accessList.setReadWriteLatestOfMyRows();
						}
						accessLists.add(accessList);

					}
				}

				// sak 12/06/09 increased public access for pwc
				NewTableAccessList publicAccessList = new NewTableAccessList(-1, a_table_id, "PUBLIC");
				//creatorAccessList.setAdministerTable();
				publicAccessList.setAdministerColumn();
				publicAccessList.setAddRow();
				publicAccessList.setDeleteRow();
				publicAccessList.setReadLatestOfTable();
				publicAccessList.setWriteLatestOfTable();
				publicAccessList.setReadWriteLatestOfMyRows();

				NewTableAccessList creatorAccessList = new NewTableAccessList(-1, a_table_id, "CREATOR");
				creatorAccessList.setAdministerTable();
				creatorAccessList.setAdministerColumn();
				creatorAccessList.setAddRow();
				creatorAccessList.setDeleteRow();
				creatorAccessList.setReadLatestOfTable();
				creatorAccessList.setWriteLatestOfTable();
				creatorAccessList.setReadWriteLatestOfMyRows();

				accessLists.add(creatorAccessList);
				accessLists.add(publicAccessList);


				if (accessLists.size() > 0)
				{

					TableManager.addAccesstoTable
										  (
										  connection,
										  a_table_id,
										  accessLists,
										  tid
										  );

				}
			}
			else
			{
				System.out.println("Could not find the collaboration and/or whiteboard to create the table");
				throw new BoardwalkException(10010);
			}
			tm.commitTransaction();

			responseToUpdate.append("Success");
			responseToUpdate.append( Seperator + a_table_id + Seperator);
			responseBuffer = responseToUpdate.toString();
			System.out.println("Response = " + responseBuffer);
			return responseBuffer;
		}
		catch ( Exception e1 )
		{
			try
			{
				tm.rollbackTransaction();
			} catch (Exception e){
				e.printStackTrace();
			}
			e1.printStackTrace();
			return null;
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
	}


	public void deleteCollab()
	{


	}

	public String editCollab()
	{
		String wrkstr = st.nextToken(Seperator);
        int collabId = Integer.parseInt(wrkstr);
        StringBuffer responseToUpdate = new StringBuffer();
		String responseBuffer = null;
        Vector wbTables = null;
        Collaboration collab = null;
        Connection connection = null;

        try
        {
            DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
            connection = databaseloader.getConnection();
            collab = CollaborationManager.getCollaborationInfo(connection,userId, collabId);
            wbTables = WhiteboardManager.getWhiteboardAndTablesByCollaborationAndNeighborhood( connection, collabId, memberId );

            // create the response buffer
			responseToUpdate.append("Success" + Seperator);
			//collab section
   			responseToUpdate.append(collab.getId() + ContentDelimeter);
   			responseToUpdate.append(collab.getName() + ContentDelimeter);
   			responseToUpdate.append(collab.getPurpose() + ContentDelimeter);
   			responseToUpdate.append(collab.getManager() + ContentDelimeter);
   			responseToUpdate.append(collab.getNeighborhood() + ContentDelimeter);
   			responseToUpdate.append(((CollaborationTreeNode)wbTables.firstElement()).getWhiteboards().size() + ContentDelimeter);
   			responseToUpdate.append(Seperator);
			//tables section
			Iterator wIter = ((CollaborationTreeNode)wbTables.firstElement()).getWhiteboards().iterator();
			while (wIter.hasNext())
			{
				WhiteboardTreeNode wbtn = (WhiteboardTreeNode)wIter.next();
				Iterator tIter = wbtn.getTables().iterator();
				responseToUpdate.append(wbtn.getId() + ContentDelimeter);
				responseToUpdate.append(wbtn.getName() + ContentDelimeter);
				responseToUpdate.append(wbtn.getTables().size() + ContentDelimeter);
				responseToUpdate.append(Seperator);
				while (tIter.hasNext())
				{
					TableTreeNode ttn = (TableTreeNode)tIter.next();
					responseToUpdate.append(ttn.getId() + ContentDelimeter);
					responseToUpdate.append(ttn.getName() + ContentDelimeter);
					responseToUpdate.append(ttn.getPurpose() + ContentDelimeter);
					responseToUpdate.append(ttn.getAccess() + ContentDelimeter);
					responseToUpdate.append(ttn.getDefaultViewPreference() + ContentDelimeter);
					responseToUpdate.append(Seperator);
				}
			}

			responseBuffer = responseToUpdate.toString();
			System.out.println("Response = " + responseBuffer);
			return responseBuffer;
        }
        catch( Exception e ) {
            e.printStackTrace();
        }
        finally {
            try {
                connection.close();
            }
            catch( SQLException sql ) {
                sql.printStackTrace();
            }
			return null;

        }

	}

	public String getCollabForUser()
	throws BoardwalkException
	{
		String wrkstr = st.nextToken(Seperator);
        int collabId = Integer.parseInt(wrkstr);
        wrkstr = st.nextToken(Seperator);
        int a_userId = Integer.parseInt(wrkstr);
        StringBuffer responseToUpdate = new StringBuffer();
		String responseBuffer = null;
        Vector wbTables = null;
        Collaboration collab = null;
        Connection connection = null;

        try
        {
            DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
            connection = databaseloader.getConnection();
            // get the membership
            int a_memberId = -1;
            Vector membershipList = null;
			membershipList = UserManager.getMembershipListForUser(connection, a_userId);
			if (membershipList != null)
			{
				Member m = (Member)membershipList.firstElement();
				a_memberId = m.getId();
				System.out.println("MemberID = " + a_memberId);
			}
			if (a_memberId != -1)
			{
				collab = CollaborationManager.getCollaborationInfo(connection,userId, collabId);
				wbTables = WhiteboardManager.getWhiteboardAndTablesByCollaborationAndNeighborhood( connection, collabId, a_memberId );

				// create the response buffer
				responseToUpdate.append("Success" + Seperator);
				//collab section
				responseToUpdate.append(collab.getId() + ContentDelimeter);
				responseToUpdate.append(collab.getName() + ContentDelimeter);
				responseToUpdate.append(collab.getPurpose() + ContentDelimeter);
				responseToUpdate.append(collab.getManager() + ContentDelimeter);
				responseToUpdate.append(collab.getNeighborhood() + ContentDelimeter);
				responseToUpdate.append(((CollaborationTreeNode)wbTables.firstElement()).getWhiteboards().size() + ContentDelimeter);
				responseToUpdate.append(Seperator);
				//tables section
				Iterator wIter = ((CollaborationTreeNode)wbTables.firstElement()).getWhiteboards().iterator();
				while (wIter.hasNext())
				{
					WhiteboardTreeNode wbtn = (WhiteboardTreeNode)wIter.next();
					Iterator tIter = wbtn.getTables().iterator();
					responseToUpdate.append(wbtn.getId() + ContentDelimeter);
					responseToUpdate.append(wbtn.getName() + ContentDelimeter);
					responseToUpdate.append(wbtn.getTables().size() + ContentDelimeter);
					responseToUpdate.append(Seperator);
					while (tIter.hasNext())
					{
						TableTreeNode ttn = (TableTreeNode)tIter.next();
						responseToUpdate.append(ttn.getId() + ContentDelimeter);
						responseToUpdate.append(ttn.getName() + ContentDelimeter);
						responseToUpdate.append(ttn.getPurpose() + ContentDelimeter);
						responseToUpdate.append(ttn.getAccess() + ContentDelimeter);
						responseToUpdate.append(ttn.getDefaultViewPreference() + ContentDelimeter);
						responseToUpdate.append(Seperator);
					}
				}
			}
			else
			{
				responseToUpdate.append("Success" + Seperator);
			}
			responseBuffer = responseToUpdate.toString();
			System.out.println("Response = " + responseBuffer);
			return responseBuffer;
        }
        catch( Exception e ) {
            e.printStackTrace();
			return null;
        }
        finally {
            try {
                connection.close();
            }
            catch( SQLException sql ) {
                sql.printStackTrace();
            }

        }

	}


	public String commitTableProperties()
	throws BoardwalkException
	{
		String tableName = null;
		String tableDesc = null;
		StringBuffer responseToUpdate = new StringBuffer();
		String responseBuffer = null;
		StringTokenizer st2;
		int tableId = -1;

		try
		{
			String wrkstr;
			wrkstr = st.nextToken (Seperator);
			System.out.println("Changing Table Properties : " + wrkstr);
			st2 = new StringTokenizer(wrkstr);
			tableId = Integer.parseInt(st2.nextToken (ContentDelimeter));
			tableName = st2.nextToken (ContentDelimeter);
			System.out.println("Table Name = " + tableName);
			try
			{
				tableDesc = st2.nextToken (ContentDelimeter);
			}
			catch (Exception e)
			{
				tableDesc = " ";
			}
			System.out.println("Table Description = " + tableDesc);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}

		if ( tableName == null || tableName.trim().equals("") )
		{
			 throw new BoardwalkException( 12007 );

		}

		// Call
		Connection connection = null;
		TransactionManager tm = null;
		try
		{
		    DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
            connection = databaseloader.getConnection();
            tm = new TransactionManager(connection, userId);
            int tid = tm.startTransaction();

 			TableManager.updateTableDescription(
												connection,
												tableId,
												tableName,
												tableDesc
												);
           	tm.commitTransaction();

			responseToUpdate.append("Success");
			responseToUpdate.append( Seperator);
			responseBuffer = responseToUpdate.toString();
			System.out.println("Response = " + responseBuffer);
			return responseBuffer;
        }
        catch ( SQLException e )
        {
           e.printStackTrace();
           try
           {
                tm.rollbackTransaction();
           }
           catch( SQLException sqlfatal )
           {
               sqlfatal.printStackTrace();
           }
		   return null;
        }
        catch ( Exception e )
        {
			e.printStackTrace();
			return null;
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

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Remove Table
	////////////////////////////////////////////////////////////////////////////////////////////
	public String removeTable()
	throws BoardwalkException
	{
		String ViewPreference = null;
		StringBuffer responseToUpdate = new StringBuffer();
		String responseBuffer = null;
		StringTokenizer st2;
		int tableId = -1;

		try
		{
			String wrkstr;
			wrkstr = st.nextToken (Seperator);
			tableId = Integer.parseInt(wrkstr);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}

		// Call
        Connection connection = null;
        TransactionManager tm = null;
		//updateTableDB();

        try
        {
			DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
			connection = databaseloader.getConnection();

			TableAccessList tbl= TableManager.getTableAccessForMember( connection, memberId, tableId );
			TableLockInfo tblock = TableManager.isTableLocked( connection,  tableId );
			System.out.println("getTableAccessForMember returned the following access for memberId " + memberId );
		//	tbl.print();


			if ( tbl.canAdministerTable() )
			{
				TableManager.lockTableForUpdate( connection, tableId);


				if ( tblock.isLocked() )
				{
					if (  tblock.getLockedByUserId() == userId )
					{
						tm = new TransactionManager( connection,userId);
						int tid = tm.startTransaction();
						TableManager.purgeTable ( connection,tableId );
						tm.commitTransaction();

						responseToUpdate.append("Success");
						responseToUpdate.append(Seperator);
						responseBuffer = responseToUpdate.toString();
						System.out.println("Response = " + responseBuffer);
						return responseBuffer;
					}
					else
					{
						throw new BoardwalkException(12016);

					}
				}
				else
				{

					tm = new TransactionManager( connection,userId);
					int tid = tm.startTransaction();
					TableManager.purgeTable ( connection,tableId );
					tm.commitTransaction();

					responseToUpdate.append("Success");
					responseToUpdate.append( Seperator);
					responseBuffer = responseToUpdate.toString();
					System.out.println("Response = " + responseBuffer);
					return responseBuffer;
				}
			}
			else
			{
				System.out.println("No access to the table for this user ");
				throw new BoardwalkException(10005);
			}

        }
        catch ( SQLException e )
		{
		   e.printStackTrace();
		   try
		   {
				tm.rollbackTransaction();
				String m_failureReason =  " The table is being updated by a different user, please try later";
				throw new BoardwalkException( 12008 );
		   }
		   catch( SQLException sqlfatal )
		   {
			   sqlfatal.printStackTrace();
		   }
			return null;
        }
        catch (SystemException e1) {
            e1.printStackTrace();
            try
            {
                tm.rollbackTransaction();
            }
            catch( SQLException sqe )
            {
                sqe.printStackTrace();
            }
			return null;
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
	}
/*
	public void  updateProperties(HttpServletResponse response)
    throws BoardwalkException
    {
		String collabName = null;
		String collabDesc = null;
		StringBuffer responseToUpdate = new StringBuffer();
		String responseBuffer = null;
		StringTokenizer st2;
		int collabId = -1;

		try
		{
			String wrkstr;
			wrkstr = st.nextToken (Seperator);
			System.out.println("Changing Collab Properties : " + wrkstr);
			st2 = new StringTokenizer(wrkstr);
			collabId = Integer.parseInt(st2.nextToken (ContentDelimeter));
			collabName = st2.nextToken (ContentDelimeter);
			System.out.println("Collab Name = " + collabName);
			try
			{
				collabDesc = st2.nextToken (ContentDelimeter);
			}
			catch (Exception e)
			{
				collabDesc = " ";
			}
			System.out.println("Collab Description = " + collabDesc);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return;
		}

		if ( collabName == null || collabName.trim().equals("") )
		{
			 throw new BoardwalkException( 10000 );

		}

		// Call
		Connection connection = null;
		TransactionManager tm = null;
		try
		{
		    DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
            connection = databaseloader.getConnection();
            tm = new TransactionManager(connection, userId);
            int tid = tm.startTransaction();

 			CollaborationManager.updateProperties(
												connection,
												collabId,
												collabName,
												collabDesc
												);
           	tm.commitTransaction();
			try
			{
				responseToUpdate.append("Success");
				responseToUpdate.append( Seperator);
				responseBuffer = responseToUpdate.toString();
				System.out.println("Response = " + responseBuffer);
				System.out.println("Response length= " + responseBuffer.length());
				response.setContentLength ( responseBuffer.length() );
				response.getOutputStream().print(responseBuffer);
			}
			catch (IOException ioe)
			{
				throw new BoardwalkException(13001);
			}

        }
        catch ( SQLException e )
        {
           e.printStackTrace();
           try
           {
                tm.rollbackTransaction();
           }
           catch( SQLException sqlfatal )
           {
               sqlfatal.printStackTrace();
           }
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
    */

    public String getCollabsForNeighborhood()
    throws BoardwalkException
	{
		System.out.println( "Getting Collaborations for a neighborhood" );
		StringBuffer responseToUpdate = new StringBuffer();
		String responseBuffer = null;
		StringTokenizer st2;
		int   selNhid   = -1;
		try
		{
			String wrkstr;
			selNhid = Integer.parseInt(st.nextToken (Seperator));

		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}

		Connection connection  = null;
		try
		{
			DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
			connection = databaseloader.getConnection();

			if (selNhid != -1)
			{

				Vector collabList = CollaborationManager.getCollaborationsOfNeighborhood(
																		connection, selNhid);

				System.out.println("collablist for nhId " + selNhid + "  count= " + collabList.size());
				// create the response buffer
				responseToUpdate.append("Success" + Seperator);
				responseToUpdate.append(collabList.size() + Seperator);
				Iterator cIter = collabList.iterator();
				while (cIter.hasNext())
				{
					CollaborationTreeNode ctn = (CollaborationTreeNode)cIter.next();
					responseToUpdate.append(ctn.getId() + ContentDelimeter);
					responseToUpdate.append(ctn.getName() + ContentDelimeter);
					responseToUpdate.append(ctn.getPurpose() + ContentDelimeter);
					responseToUpdate.append(Seperator);
				}

				responseBuffer = responseToUpdate.toString();
				System.out.println("Response = " + responseBuffer);
				return responseBuffer;
			}
			else
			{
				return null;
			}
		}

		catch( Exception e ) {
			e.printStackTrace();
			return null;
		}
		finally {
			try {
				connection.close();
			}
			catch( SQLException sql ) {
				sql.printStackTrace();
			}
		}
    }
/*
    public void getCollabForTable(HttpServletResponse response)
    throws BoardwalkException
	{
		System.out.println( "Getting Collaborations for a neighborhood" );
		StringBuffer responseToUpdate = new StringBuffer();
		String responseBuffer = null;
		StringTokenizer st2;
		int   tableId   = -1;
		try
		{
			String wrkstr;
			tableId = Integer.parseInt(st.nextToken (Seperator));

		}
		catch (Exception e)
		{
			e.printStackTrace();
			return;
		}

		Connection connection  = null;
		try
		{
			DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
			connection = databaseloader.getConnection();

			if ( tableId != -1 )
			{

				Vector collabList = CollaborationManager.getCollaborationForTable(
																		connection, tableId);

				// create the response buffer
				responseToUpdate.append("Success" + Seperator);

				CollaborationTreeNode ctn = (CollaborationTreeNode)collabList.firstElement();
				responseToUpdate.append(ctn.getId() + ContentDelimeter);
				responseToUpdate.append(ctn.getName() + ContentDelimeter);
				responseToUpdate.append(ctn.getPurpose() + ContentDelimeter);
				responseToUpdate.append(Seperator);

				responseBuffer = responseToUpdate.toString();
				System.out.println("Response = " + responseBuffer);
				System.out.println("Response length= " + responseBuffer.length());
				response.setContentLength ( responseBuffer.length() );
				response.getOutputStream().print(responseBuffer);
			}
		}

		catch( Exception e ) {
			e.printStackTrace();
		}
		finally {
			try {
				connection.close();
			}
			catch( SQLException sql ) {
				sql.printStackTrace();
			}
		}
    }

*/

    public boolean  loginUser()
	{


		String wrkstr;
		wrkstr = st.nextToken (Seperator);
		userId = Integer.parseInt(wrkstr);

		wrkstr = st.nextToken (Seperator);
		userName = wrkstr;

		//wrkstr = st.nextToken (Seperator); //Modified by Tekvision on 20180207 for Clear Text Password(Issue Id: 14241)
		//userPassword = wrkstr; //Modified by Tekvision on 20180207 for Clear Text Password(Issue Id: 14241)

		wrkstr = st.nextToken (Seperator);
		memberId = Integer.parseInt(wrkstr);

		wrkstr = st.nextToken (Seperator);
		nhId = Integer.parseInt(wrkstr);

		//wrkstr = st.nextToken (Seperator);
		//nhName =wrkstr;

		Connection connection = null;
		try
		{
			DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
			connection = databaseloader.getConnection();

			if (  userName == null  || userName.equals("") ) //Modified by Tekvision on 20180207 for Clear Text Password(Issue Id: 14241)
			{
				return false;
			}
			else
			{
				//System.out.println("Authenticating User : " + userName + ":" + userPassword);

				int db_userId = UserManager.authenticateUser(connection, userName, false); //Modified by Tekvision on 20180207 for Clear Text Password(Issue Id: 14241)

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
