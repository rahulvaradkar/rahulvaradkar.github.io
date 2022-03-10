package servlets;
/*
 *  xlAdminService.java
 */
import com.boardwalk.exception.*;
import java.io.*;
import java.text.*;
import java.util.*;
import java.sql.*;
import java.util.*;
import java.util.regex.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.lang.Runtime;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;
import java.net.URLEncoder;

//import com.boardwalk.exception.BoardwalkException;
import com.boardwalk.database.*;
import com.boardwalk.database.DatabaseLoader;
import com.boardwalk.user.User;	//
import com.boardwalk.user.UserManager;	//
import com.boardwalk.util.BoardwalkSession;
import com.boardwalk.member.*;
import com.boardwalk.collaboration.*;
import com.boardwalk.whiteboard.*;
import com.boardwalk.table.*;
import com.boardwalk.neighborhood.*;

import boardwalk.connection.*;
import boardwalk.common.BoardwalkUtility;
import boardwalk.table.*;
import boardwalk.neighborhood.*;

import boardwalk.collaboration.*;
import boardwalk.table.BoardwalkTableManager;

import org.apache.commons.codec.binary.Base64;

public class xlAdminServiceLogic extends xlServiceLogic
{
	public final static String Seperator = new Character((char)1).toString();
	public final static String ContentDelimeter = new Character((char)2).toString();

    private static String CALL_BW_GET_ALL_MEMBERSHIPS_INFO = "{CALL BW_GET_ALL_MEMBERSHIPS_INFO}";
    private static String CALL_BW_GET_NHS_AT_LEVEL_0 = "{CALL BW_GET_NHS_AT_LEVEL_0(?)}";
	private static String PIPE_CHAR = "|";

	HttpServletRequest req;
	HttpServletResponse res;

	Connection connection = null;
	BoardwalkConnection bwcon = null;

	static StringBuffer sb = null;
    int userId; //
    
    public xlAdminServiceLogic(xlAdminService srv) {
        super(srv);
    }
	
    public void service (HttpServletRequest request, HttpServletResponse response) 	throws ServletException, IOException
    {
		req = request;
		res = response;
	
		StringTokenizer st;

		String buf = getRequestBuffer(request).toString();
		//System.out.println("Data from client" + buf);
		st = new StringTokenizer(buf);

		int action;

		String wrkstr;
		wrkstr = st.nextToken(Seperator);

		action = Integer.parseInt(wrkstr);

		System.out.println("action="+action);

		String collabName, newCollabName;
		String wbName, newWbName;
		String desc;
		String tableName, newTableName;
		String nhName;
		//String userName;
		String relationName;
		int collabId;
		int wbId;
		int tableId;
		int nhId;
		//int userId;
		int parentNhId;
		int memberId;
		int targetNhId;
		int rowId;
		
		String relation;
		String selNhIds;

//CreateCollaboration(CollabName, Description)
//RenameCollaboration(CollabId, newCollabName)
//DeleteCollaboration(CollabId)
//GetCollaborationTreeUsingId(CollabId)
//GetCollaborationTreeUsingName(CollabName)
//CreateWhiteboard(CollabId,  WhiteBoardName)
//RenameWhiteBoard(wbId, wbName)
//DeleteWhiteboard(wbId)
//CreateTable(CollabName, CollabId,  WhiteBoardName, WhiteBoardId, TableName, TableDescription)
//DeleteTable(TableId)
//GetCollabForNH(NhId)
//GetUserMemberships(UserName)
//GetUserMemberships(UserId)
//CreateNeighborhood(NhName, ParentNhId))
//DeleteNeighborhood(NhId)
//GetNeighborhoodTree(NhId)
//CreateMembershipForNeighborhood(NhId, UserId)
//DeleteMembership(MemberId)
//GetMembershipListForNeighborhood(NhId)
//CreateRelationship(NhId, RelationName, TargetNhIds)
//DeleteRelationship(NhId, RelationName)
//CreateNewUser(email, firstname, lastname, address)
//GetUserList()
//UpdateUserPassword(UserId, OldPwd, NewPwd)
//ChangeRowOwner(rowId,  UserName)
//GetTableAccess(TableId)
//SetTableAccess(TableId)
//RenameTable(tableId, newTableName)
//RenameNh(nhId, newNhName)
//GetCollaborationTreeForAllNh
//PurgeTable
//GetAllNeighborhoodTree()
		
		
		HttpSession hs = ((HttpServletRequest)request).getSession(false);
		int userId = (int) hs.getAttribute("userId"); //u.getId();
		System.out.println("xlAdminService::  userId:"+userId);
		
		bwcon = getBoardwalkConnection(userId);
		//bwcon = getBoardwalkConnection();
		String failureReason = "";
		String responseBuffer = "";

		if (bwcon != null)
		{
			System.out.println("Successfully obtained authenticated Boardwalk Connection");
			try
			{
				switch(action)
				{
					case 1:						//CreateCollaboration
						wrkstr = st.nextToken(Seperator);
						collabName = wrkstr;
						wrkstr = st.nextToken(Seperator);
						desc = wrkstr;
						wrkstr = st.nextToken(Seperator);
						userId = Integer.parseInt(wrkstr);
						wrkstr = st.nextToken(Seperator);
						memberId = Integer.parseInt(wrkstr);

//						collabId = CreateCollaboration (bwcon, collabName, desc);
						collabId = CreateCollaboration(connection, collabName, desc, userId, memberId);
						if (collabId != -1)
							responseBuffer = "SUCCESS" + Seperator +  "New Collaboration with name " + collabName + " is successfully created." + ContentDelimeter + collabId  ;
						break;

					/**
						case 2:						//RenameCollaboration"
						wrkstr = st.nextToken(Seperator);
						collabId = Integer.parseInt(wrkstr);
						wrkstr = st.nextToken(Seperator);
						collabName = wrkstr;
						wrkstr = st.nextToken(Seperator);
						newCollabName = wrkstr;

						collabId = RenameCollaboration (connection, collabId, newCollabName);
						if (collabId != -1)
							responseBuffer = "SUCCESS" + Seperator +  "The Collaboration '" + collabName + "' is successfully renamed  as '" + newCollabName + "'." + ContentDelimeter + collabId  ;
						break;
					**/
	
					case 3:						//DeleteCollaboration
						wrkstr = st.nextToken(Seperator);
						collabName = wrkstr;
						wrkstr = st.nextToken(Seperator);
						collabId = Integer.parseInt(wrkstr);

						if (DeleteCollaboration (bwcon, collabId))
							responseBuffer = "SUCCESS" + Seperator +  "The Collaboration with name '" + collabName + "' is successfully deleted." + ContentDelimeter + collabId  ;
						
						break;
					/**
					case 4:						//GetCollaborationTreeUsingId
						wrkstr = st.nextToken(Seperator);
						collabId = Integer.parseInt(wrkstr);
						String collabTree = "";
						collabTree = GetCollaborationTreeUsingId(bwcon, collabId);
						responseBuffer = "SUCCESS" + ContentDelimeter +  collabTree + ContentDelimeter + collabId  ;
						break;
					
					case 5:						//GetCollaborationTreeUsingName")) {
						wrkstr = st.nextToken(Seperator);
						collabName = wrkstr;
						//call to GetCollaborationTreeUsingName(collabName);
						break;
					**/
					case 6:						//CreateWhiteboard
						wrkstr = st.nextToken(Seperator);
						collabId = Integer.parseInt(wrkstr);
						wrkstr = st.nextToken(Seperator);
						collabName = wrkstr;
						wrkstr = st.nextToken(Seperator);
						wbName = wrkstr;
						wbId = CreateWhiteboard (bwcon, collabId, wbName);
						if (wbId != -1)
							responseBuffer = "SUCCESS" + Seperator +  "New Whiteboard with name '" + wbName + "' is successfully created under Collaboration '" + collabName + "'." + ContentDelimeter + wbId  ;
						break;
						
					/**
					case 7:						//RenameWhiteBoard
						wrkstr = st.nextToken(Seperator);
						collabName = wrkstr;
						wrkstr = st.nextToken(Seperator);
						wbId = Integer.parseInt(wrkstr);
						wrkstr = st.nextToken(Seperator);
						wbName = wrkstr;
						wrkstr = st.nextToken(Seperator);
						newWbName = wrkstr;

						wbId = RenameWhiteboard(connection, wbId, newWbName);
						if (wbId != -1)
							responseBuffer = "SUCCESS" + Seperator +  "The Whiteboard '" + wbName + "' under Collaboration '" + collabName + "' is successfully renamed  as '" + newWbName + "'." + ContentDelimeter + wbId  ;
						break;
					**/

					case 8:						//DeleteWhiteboard
						wrkstr = st.nextToken(Seperator);
						collabName = wrkstr;
						wrkstr = st.nextToken(Seperator);
						collabId = Integer.parseInt(wrkstr);
						wrkstr = st.nextToken(Seperator);
						wbName = wrkstr;
						wrkstr = st.nextToken(Seperator);
						wbId = Integer.parseInt(wrkstr);
						if (DeleteWhiteboard(connection, wbId))
							responseBuffer = "SUCCESS" + Seperator +  "The Whiteboard with name '" + wbName + "' is successfully deleted from Collobarion '" + collabName + "'." + ContentDelimeter + wbId  ;
						
						break;
					
					case 9:						//CreateTable
						wrkstr = st.nextToken(Seperator);
						collabName = wrkstr;
						wrkstr = st.nextToken(Seperator);
						collabId = Integer.parseInt(wrkstr);
						wrkstr = st.nextToken(Seperator);
						wbName = wrkstr;
						wrkstr = st.nextToken(Seperator);
						wbId = Integer.parseInt(wrkstr);
						wrkstr = st.nextToken(Seperator);
						tableName = wrkstr;
						wrkstr = st.nextToken(Seperator);
						desc = wrkstr;
						tableId = CreateTable (bwcon, collabId, wbId, tableName, desc);
						if (tableId != -1)
							responseBuffer = "SUCCESS" + Seperator +  "New Table with name '" + tableName + "' is successfully created under Whiteboard '" + wbName + "' of Collaboration '" + collabName + "'." + ContentDelimeter + tableId  ;
						break;
					/**
					case 10:					//DeleteTable
						wrkstr = st.nextToken(Seperator);
						collabName = wrkstr;
						wrkstr = st.nextToken(Seperator);
						collabId = Integer.parseInt(wrkstr);
						wrkstr = st.nextToken(Seperator);
						wbName = wrkstr;
						wrkstr = st.nextToken(Seperator);
						wbId = Integer.parseInt(wrkstr);
						wrkstr = st.nextToken(Seperator);
						tableName = wrkstr;
						wrkstr = st.nextToken(Seperator);
						tableId = Integer.parseInt(wrkstr);
						if (DeleteTable(bwcon, tableId))
							responseBuffer = "SUCCESS" + Seperator +  "The Table '" + tableName + "' present under Whiteboard '" + wbName + "' of Collaboration '" + collabName + "' is successfully deleted." + ContentDelimeter + tableId  ;
						
						break;
					**/
					case 11:					//GetCollabForNH
						wrkstr = st.nextToken(Seperator);
						nhId = Integer.parseInt(wrkstr);
						String CollabforNh = "";
						CollabforNh = GetCollaborationTreeForNh(connection, bwcon, nhId);
						//responseBuffer = "SUCCESS" + Seperator +  "The Neighborhood of " + nhId + "is \n" + tree + ContentDelimeter + nhId  ;
						responseBuffer = "SUCCESS" + ContentDelimeter +  CollabforNh + ContentDelimeter + nhId  ;
						break;
/**
					case 12:					//GetUserMembershipsUsingName
						wrkstr = st.nextToken(Seperator);
						userName = wrkstr;
						//call to GetUserMembershipsUsingName(userName);
						break;

					case 13:					//GetUserMembershipsUsingId
						wrkstr = st.nextToken(Seperator);
						userId = Integer.parseInt(wrkstr);
						//call to GetUserMembershipsUsingId(userId);
						break;
**/
					case 14:					//CreateNeighborhood
						wrkstr = st.nextToken(Seperator);
						nhName = wrkstr;
						wrkstr = st.nextToken(Seperator);
						parentNhId = Integer.parseInt(wrkstr);
						wrkstr = st.nextToken(Seperator);
						String parentNHName = wrkstr ;
						System.out.println("calling CreateNeighborhood");
						nhId = CreateNeighborhood(bwcon, nhName, parentNhId);
						System.out.println("Case 14 : Returned NHid=" + nhId);	
						if (nhId != -1)
						{	
							if (parentNhId > 0)
								responseBuffer = "SUCCESS" + Seperator +  "New Neighborhood with name '" + nhName + "' is successfully created under Neighborhood '" + parentNHName + "'." + ContentDelimeter + nhId  ;
							else
								responseBuffer = "SUCCESS" + Seperator +  "New Neighborhood with name '" + nhName + "' is successfully created at level 0." + ContentDelimeter + nhId  ;
						}
						else
						{
							//System.out.println("Case14: New nhId=" + nhId);
							responseBuffer = "Failure" + Seperator +  "New Neighborhood with name '" + nhName + "' is failed to create. Please choose different name and try again."  + Seperator + "10016" + ContentDelimeter + nhId   ;
						}
						break;

					case 15:					//DeleteNeighborhood
						wrkstr = st.nextToken(Seperator);
						nhId = Integer.parseInt(wrkstr);
						wrkstr = st.nextToken(Seperator);
						nhName = wrkstr;
						if (DeleteNeighborhood(bwcon, nhId))
							responseBuffer = "SUCCESS" + Seperator +  "The Neighborhood '" + nhName + "' is successfully deleted." + ContentDelimeter + nhId  ;
						break;

					case 16:					//GetNeighborhoodTree
						wrkstr = st.nextToken(Seperator);
						nhId = Integer.parseInt(wrkstr);
						String tree = "";
						tree = GetNeighborhoodTree(bwcon, nhId);
						//responseBuffer = "SUCCESS" + Seperator +  "The Neighborhood of " + nhId + "is \n" + tree + ContentDelimeter + nhId  ;
						responseBuffer = "SUCCESS" + Seperator +  tree + ContentDelimeter + nhId  ;
						break;

					case 17:					//CreateMembershipForNeighborhood
						wrkstr = st.nextToken(Seperator);
						nhId = Integer.parseInt(wrkstr);
						wrkstr = st.nextToken(Seperator);
						userId = Integer.parseInt(wrkstr);

						memberId = CreateMembershipForNeighborhood(bwcon, nhId, userId);
						System.out.println("Case17: New Member id=" + userId);	
						if (memberId != -1)
						{	
							responseBuffer = "SUCCESS" + Seperator +  "New membership is created for user " + userId + " in Neighborhood :" + nhId + "." + ContentDelimeter + memberId  ;
						}
						break;

					case 18:					//DeleteMembership
						wrkstr = st.nextToken(Seperator);
						memberId = Integer.parseInt(wrkstr);
						if (DeleteMembership(bwcon, memberId))
							responseBuffer = "SUCCESS" + Seperator +  "The Membership is successfully deleted." + ContentDelimeter + memberId  ;
						else
							responseBuffer = "FAILURE" + Seperator +  "Membership cannot be deleted. Please contact Boardwalk Administrator." + Seperator + "Delete Membership Failure"; //Added by Lakshman on 20181005 to fix the Issue Id: 14324
						break;
					/**
					case 19:					//GetMembershipListForNeighborhood
						wrkstr = st.nextToken(Seperator);
						nhId = Integer.parseInt(wrkstr);
						//call to GetMembershipListForNeighborhood(nhId);
						break;
					case 20:					//CreateRelationship
						wrkstr = st.nextToken(Seperator);
						nhId = Integer.parseInt(wrkstr);
						wrkstr = st.nextToken(Seperator);
						relationName = wrkstr;
						wrkstr = st.nextToken(Seperator);
						targetNhId = Integer.parseInt(wrkstr);
						//call to CreateRelationship(nhId, relationName, targetNhId);

						break;
					case 21:					//DeleteRelationship
						wrkstr = st.nextToken(Seperator);
						nhId = Integer.parseInt(wrkstr);
						wrkstr = st.nextToken(Seperator);
						relationName = wrkstr;
						//call to DeleteRelationship(nhId, relationName);
						break;
					**/
					case 22:					//CreateNewUser
						String user = st.nextToken(Seperator);
						String extUser = st.nextToken(Seperator); // for external user id - sujith 05/11/2016
						String pwd = st.nextToken(Seperator);
						String firstname = st.nextToken(Seperator);
						String lastname = st.nextToken(Seperator);
						
						userId = CreateNewUser(bwcon, user, extUser, pwd, firstname, lastname);
						//System.out.println(bwcon +" " + user +" " +  extUser +" " +  pwd +" " +  firstname +" " +  lastname);
						System.out.println("Case22: New user id=" + userId);	
						if (userId != -1)
						{	
							responseBuffer = "SUCCESS" + Seperator +  "New user with name '" + firstname + " " + lastname + "' is successfully created." + ContentDelimeter + userId  ;
						}
						break;
					
					case 23:					//GetUserList
						String userList = "";
						userList = GetUserList(bwcon);
						//responseBuffer = "SUCCESS" + Seperator +  "The Neighborhood of " + nhId + "is \n" + tree + ContentDelimeter + nhId  ;
						responseBuffer = "SUCCESS" + Seperator +  userList   ;
						break;
					/**
					case 24:					//UpdateUserPassword
						wrkstr = st.nextToken(Seperator);
						userId = Integer.parseInt(wrkstr);
						String oldPwd = st.nextToken(Seperator);
						String newPwd = st.nextToken(Seperator);
						//call to UpdateUserPassword(userId, oldPwd, newPwd);
						break;
					case 25:					//ChangeRowOwner
						wrkstr = st.nextToken(Seperator);
						rowId = Integer.parseInt(wrkstr);
						userName = st.nextToken(Seperator);
						//call to ChangeRowOwner(rowId, userName);
						break;
					case 26:					//GetTableAccess
						wrkstr = st.nextToken(Seperator);
						tableId = Integer.parseInt(wrkstr);
						//call to GetTableAccess(tableId);
						break;
					case 27:					//SetTableAccess
						wrkstr = st.nextToken(Seperator);
						tableId = Integer.parseInt(wrkstr);
						//call to SetTableAccess(tableId);
						
					
					case 28:					//RenameTable
						wrkstr = st.nextToken(Seperator);
						collabName = wrkstr;
						wrkstr = st.nextToken(Seperator);
						wbName = wrkstr;
						wrkstr = st.nextToken(Seperator);
						tableId = Integer.parseInt(wrkstr);
						wrkstr = st.nextToken(Seperator);
						tableName = wrkstr;
						wrkstr = st.nextToken(Seperator);
						newTableName = wrkstr;

						tableId = RenameTable(connection, tableId, newTableName);
						if (tableId != -1)
							responseBuffer = "SUCCESS" + Seperator +  "The Table '" + tableName + "' present under Whiteboard '" + wbName + "' of Collaboration '" + collabName + "' is successfully renamed as '" + newTableName + "'." + ContentDelimeter + tableId  ;

						break;
					
					case 29:					//RenameNh
						wrkstr = st.nextToken(Seperator);
						nhId = Integer.parseInt(wrkstr);
						wrkstr = st.nextToken(Seperator);
						nhName = wrkstr;
						wrkstr = st.nextToken(Seperator);
						String newNhName = wrkstr;

						nhId = RenameNh(connection, nhId, newNhName);
						if (nhId != -1)
							responseBuffer = "SUCCESS" + Seperator +  "The Neighborhood '" + nhName + "' is successfully renamed as '" + newNhName + "'." + ContentDelimeter + nhId  ;

						break;
					**/
					case 30:
						String allMembershipInfo = "";
						try
						{
							allMembershipInfo = GetAllMembershipsInfo(connection);
							//System.out.println("allMembershipInfo : " + allMembershipInfo);
							//responseBuffer = "SUCCESS" + Seperator +  "The Neighborhood of " + nhId + "is \n" + tree + ContentDelimeter + nhId  ;
							responseBuffer = "SUCCESS" + ContentDelimeter +  allMembershipInfo + ContentDelimeter   ;
						}
						catch (Exception e)
						{
							responseBuffer = "FAILURE" + ContentDelimeter +  e.getMessage() +  "\nFailed to retrieve all Membership Information. Try again" + ContentDelimeter   ;
						}
						
						break;
					/**
					case 31:
						String AllNhCollabTree = "";
						try
						{
							AllNhCollabTree = GetCollaborationTreeForAllNh(connection, bwcon);
							responseBuffer = "SUCCESS" + ContentDelimeter +  AllNhCollabTree + ContentDelimeter   ;
						}
						catch (Exception e)
						{
							responseBuffer = "FAILURE" + ContentDelimeter +  e.getMessage() +  "\nFailed to retrieve all Neighborhood Collaborations. Try again" + ContentDelimeter   ;
						}
						break;
					
					case 32:			//Get Collaboration Tree For Nh ManagedBy User
						wrkstr = st.nextToken(Seperator);
						userId = Integer.parseInt(wrkstr);
						String NhCollabTreeForUser = "";
						try
						{
							NhCollabTreeForUser = GetCollaborationTreeForNhManagedByUser(connection, bwcon, userId);
							responseBuffer = "SUCCESS" + ContentDelimeter +  NhCollabTreeForUser + ContentDelimeter + userId  ;
						}
						catch (Exception e)
						{
							responseBuffer = "FAILURE" + ContentDelimeter +  e.getMessage() +  "\nFailed to retrieve all Neighborhood Collaborations managed by User. Try again" + ContentDelimeter   ;
						}
						break;
				
					case 33:			// Purge Table
						wrkstr = st.nextToken(Seperator);
						userId = Integer.parseInt(wrkstr);
						wrkstr = st.nextToken(Seperator);
						tableName = wrkstr;
						wrkstr = st.nextToken(Seperator);
						tableId = Integer.parseInt(wrkstr);
						try
						{
							TransactionManager tm = null;
							tm = new TransactionManager( connection,userId);
							int tid = tm.startTransaction();
							TableManager.purgeTable ( connection,tableId );
							tm.commitTransaction();
							responseBuffer = "SUCCESS" + ContentDelimeter +  "The Cuboid '" + tableName + "' purged successfully." + ContentDelimeter + tableId  ;
						}
						catch (Exception e)
						{
							responseBuffer = "FAILURE" + ContentDelimeter +  e.getMessage() +  "\nFailed to retrieve all Neighborhood Collaborations managed by User. Try again" + ContentDelimeter   ;
						}
						break;


					case 34:			//GetAllNeighborhoodTree		
						wrkstr = st.nextToken(Seperator);
						nhId = Integer.parseInt(wrkstr);
						String nhAlltree = "";
						nhAlltree = GetAllNeighborhoodTree(connection, nhId);
						//responseBuffer = "SUCCESS" + Seperator +  "The Neighborhood of " + nhId + "is \n" + tree + ContentDelimeter + nhId  ;
						responseBuffer = "SUCCESS" + ContentDelimeter +  nhAlltree + ContentDelimeter + nhId  ;
						break;
					**/
					case 63:  			//Deactivate User
						String usermail = st.nextToken(Seperator);
						//System.out.println("test usermail="+usermail);
						int status=deactivateUser(connection,usermail);
						if (status==1)
						{
							responseBuffer = "SUCCESS" + Seperator +  "User " + usermail +" deactivated successfully."   ;
						}
						else
						{
							responseBuffer = "FAILURE" + Seperator +  "User deactivation failed. Please try again later."   ;
						}
						
						break;
					
					case 64:			//Activate user
						usermail = st.nextToken(Seperator);
						//System.out.println("test usermail="+usermail);
						status=activateUser(connection,usermail);
						if (status==1)
						{
							responseBuffer = "SUCCESS" + Seperator +  "User " + usermail +" activated successfully."   ;
						}
						else
						{
							responseBuffer = "FAILURE" + Seperator +  "User activation failed. Please try again later."   ;
						}
						
						break;	

					case 65:			//Activate user
						usermail = st.nextToken(Seperator);
						status=UserManager.unlockUser(connection,usermail);
						if (status==1)
						{
							responseBuffer = "SUCCESS" + Seperator +  "User unlocked successfully."   ;
						}
						else
						{
							responseBuffer = "FAILURE" + Seperator +  "Unlocking user failed."   ;
						}
						
						break;	

					case 66:			//Force to Change Password
						usermail = st.nextToken(Seperator);
						status=UserManager.forceChangePassword(connection,usermail);
						if (status==1)
						{
							responseBuffer = "SUCCESS" + Seperator +  "User updated to force password successfully."   ;
						}
						else
						{
							responseBuffer = "FAILURE" + Seperator +  "Forcing user to change password failed."   ;
						}
						
						break;	
				}
			}
			catch (BoardwalkException bwe)
			{
				//System.out.println("XXXXXXXX.........1");
				System.out.println("Boardwalk error code = " + bwe.getErrorCode());
				System.out.println("Boardwalk message = " + bwe.getMessage());
				System.out.println("Boardwalk potentioal solution = " + bwe.getPotentialSolution());
				responseBuffer = "FAILURE" + Seperator + bwe.getErrorCode() + Seperator + bwe.getMessage() + "\n" + bwe.getPotentialSolution()  + ContentDelimeter;
			}

			finally
			{
				try
				{
					if (connection != null)
						connection.close();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				//System.out.println("XXXXXXXX.........Returning Response");
				commitResponseBuffer(responseBuffer, res);
			}
		}
		else
		{
			commitResponseBuffer("User Authentication/Connection Failed. \nYou have entered wrong User or Password. Try again.", res);
		}
    }
/**
	public BoardwalkConnection getBoardwalkConnection() throws IOException
	{
		try
		{
			DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
			connection = databaseloader.getConnection();
			bwcon = BoardwalkConnectionManager.getBoardwalkConnection(connection, "admin", "0", -1);
		}
		catch (SQLException sqe)
		{
			System.out.println("There is a Database connection problem.");
			commitResponseBuffer("There is a Database connection problem. \nContact Boardwalk Administrator for support.", res);
		}
		catch (BoardwalkException bwe)
		{
			System.out.println("Authentication/Connection Failed.");
			commitResponseBuffer("User Authentication/Connection Failed. \nYou have entered wrong User or Password. Try again.", res);
		}
		return bwcon;
	}
**/

	// modified by shirish to pull user from session object
	public BoardwalkConnection getBoardwalkConnection(int userId) throws IOException
	{
		PreparedStatement preparedstatement = null;
		ResultSet rs = null;
		String userName = null;
		try
		{
			DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
			connection = databaseloader.getConnection();
			String usrNameQry = "SELECT EMAIL_ADDRESS FROM BW_USER WHERE ID = ?" ;
			preparedstatement = connection.prepareStatement(usrNameQry);

			preparedstatement.setInt(1,userId);
			rs = preparedstatement.executeQuery();
			while ( rs.next() )
				userName = rs.getString("EMAIL_ADDRESS");
			
			preparedstatement.close();
			preparedstatement = null;
			
			//System.out.println("userName ->:"+userName); // TODO:REMOVE
			//bwcon = BoardwalkConnectionManager.getBoardwalkConnection(connection, "su@apple.com", "0", -1, "ORACLE");
			//bwcon = BoardwalkConnectionManager.getBoardwalkConnection(connection, userId, userName,  -1, "ORACLE");
			bwcon = BoardwalkConnectionManager.getBoardwalkConnection(connection, userId, userName, -1);
		}
		catch (SQLException sqe)
		{
			System.out.println("There is a Database connection problem.");
			commitResponseBuffer("There is a Database connection problem. \nContact Boardwalk Administrator for support.", res);
		}
		catch (BoardwalkException bwe)
		{
			System.out.println("Authentication/Connection Failed.");
			commitResponseBuffer("User Authentication/Connection Failed. \nYou have entered wrong User or Password. Try again.", res);
		}
		finally
		{
			try
			{
				if (preparedstatement != null)
				{
					preparedstatement.close();
				}
			}
			catch( SQLException sql )
			{
				sql.printStackTrace();
			}
		}
		return bwcon;
	}

	//Create Collaboration
	public int CreateCollaboration (Connection connection, String collabName, String desc, int userId, int memberId) throws  BoardwalkException
	{
		int collabId = -1;
		TransactionManager tm = null;
		try
		{
			tm = new TransactionManager(connection, userId);
			int tid = tm.startTransaction();

			collabId = CollaborationManager.createCollaboration(
												connection,
												collabName,
												desc,
												memberId,
												tid,
												1);
			tm.commitTransaction();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			try
			{
				tm.rollbackTransaction();
			}
			catch (SQLException sqe)
			{
				sqe.printStackTrace();
				throw new BoardwalkException( 10000 );
			}
				throw new BoardwalkException( 10000 );
		}

//		if (collabId == -1)
//				throw new BoardwalkException( 10000 );

		return collabId;


/*
		int collabId = BoardwalkCollaborationManager.createCollaboration(bwcon,
									collabName, desc);
		if (collabId == -1)
				throw new BoardwalkException( 10000 );
		return collabId;
*/
	}

	//Delete Collaboration
	public boolean DeleteCollaboration (BoardwalkConnection bwcon, int collabId) throws  BoardwalkException
	{
		boolean retVal = true;
		try
		{
			BoardwalkCollaborationManager.deleteCollaboration(bwcon, collabId);
		}
		catch (Exception e)
		{
			System.out.println(e.toString());
			retVal = false;
		}
		return retVal;
	}

	// NEW API RENAME COLLABORATION
	/**
	public int RenameCollaboration(Connection connection, int collabId, String newCollabName) throws  BoardwalkException
	{
		System.out.println("Calling CollaborationManager.renameCollaboration collabId=" + collabId + " newName=" + newCollabName );
		collabId = CollaborationManager.renameCollaboration(connection, collabId, newCollabName);
		if (collabId == -1)
		{
			throw new BoardwalkException( 10013 );
		}
		return collabId;
	}
	**/
	// CREATE WHITEBOARD
	
	public int CreateWhiteboard (BoardwalkConnection bwcon, int collabId, String WhiteboardName) throws  BoardwalkException
	{
		int wbId = BoardwalkCollaborationManager.createWhiteboard(bwcon, WhiteboardName, collabId );
		if (wbId == -1)
				throw new BoardwalkException( 10011 );
		return wbId;
	}
	
	// Delete Whiteboard
	public boolean DeleteWhiteboard (Connection connection, int wbId) throws  BoardwalkException
	{
		boolean retVal = true;
		try
		{
			WhiteboardManager.purgeWhiteboard(connection, wbId);
		}
		catch (Exception e)
		{
			System.out.println(e.toString());
			retVal = false;
		}
		return retVal;
	}

	// NEW API RENAME WHITEBOARD
	/**
	public int RenameWhiteboard(Connection connection, int wbId, String newWbName) throws  BoardwalkException
	{
		System.out.println("Calling CollaborationManager.renameWhiteboard wbId=" + wbId + " newWbName=" + newWbName );
		wbId = CollaborationManager.renameWhiteboard(connection, wbId, newWbName);
		if (wbId == -1)
		{
			throw new BoardwalkException( 10014 );
		}
		return wbId;
	}
	**/

	// CREATE TABLE
	public int CreateTable (BoardwalkConnection bwcon, int collabId, int wbId, String tableName, String tableDesc) throws  BoardwalkException
	{
		int tableId = BoardwalkTableManager.createTable(bwcon, collabId, wbId, tableName, tableDesc);
		if (tableId == -1)
				throw new BoardwalkException( 10012 );
		return tableId;
	}

	// Delete Table
	public boolean DeleteTable (BoardwalkConnection bwcon, int tableId) throws  BoardwalkException
	{
		System.out.println("Deleteing table " + tableId);
		boolean retVal = true;
		try
		{
			System.out.println("before .deelteTable call");
			BoardwalkTableManager.deleteTable(bwcon, tableId);
			System.out.println("after .deelteTable call");		}
		catch (Exception e)
		{
			System.out.println(e.toString());
			retVal = false;
		}
		return retVal;
	}


	// NEW API RENAME TABLE
	/**
	public int RenameTable(Connection connection, int tableId, String newTableName) throws  BoardwalkException
	{
		System.out.println("Calling CollaborationManager.renameTable tableId=" + tableId + " newTableName=" + newTableName );
		tableId = CollaborationManager.renameTable(connection, tableId, newTableName);
		if (tableId == -1)
		{
			throw new BoardwalkException( 10014 );
		}
		return tableId;
	}
	**/

	// CREATE NEIGHBORHOOD
	public int CreateNeighborhood(BoardwalkConnection bwcon, String nhName,  int parentNhId) throws  BoardwalkException  
	{
		int nhId ;

		System.out.println("Calling BoardwalkNeighborhoodManager.createNeighborhood nhName=" + nhName + " parentNhId=" + parentNhId );
		nhId = BoardwalkNeighborhoodManager.createNeighborhood(bwcon, nhName, false, parentNhId);
		System.out.println("After Calling BoardwalkNeighborhoodManager.createNeighborhood nhId=" + nhId);
		if (nhId == 0)
		{
			nhId = -1;
			System.out.println("Throwing exception...10016" ); 
			//throw new BoardwalkException( 10016 );
			//System.out.println("After Throwing exception...10016");			
		}
		return nhId;
	}

	// DELETE NEIGHBORHOOD
	public boolean DeleteNeighborhood(BoardwalkConnection bwcon, int nhId) throws  BoardwalkException  
	{
		System.out.println("Deleteing Neighborhood " + nhId);
		boolean retVal = true;
		try
		{
			System.out.println("before Calling BoardwalkNeighborhoodManager.deleteNeighborhood nhId=" + nhId );
			BoardwalkNeighborhoodManager.deleteNeighborhood(bwcon, nhId);
			System.out.println("after Calling BoardwalkNeighborhoodManager.deleteNeighborhood nhId=" + nhId );
		}
		catch (Exception e)
		{
			System.out.println(e.toString());
			retVal = false;
		}
		return retVal;
	}

	//NEW API RENAME NH
	/**
	public int RenameNh(Connection connection, int nhId, String newNhName) throws  BoardwalkException
	{
		System.out.println("Calling CollaborationManager.renameNh nhId=" + nhId + " newNhName=" + newNhName );
		nhId = CollaborationManager.renameNh(connection, nhId, newNhName);
		if (nhId == -1)
		{
			throw new BoardwalkException( 10016 );
		}
		return nhId;
	}
	**/


    public static String GetNeighborhoodTree(
        BoardwalkConnection bwcon,
        int a_NhId
		) throws BoardwalkException 
	{
		try
		{
			sb = new StringBuffer();				
			Vector nh0v = BoardwalkNeighborhoodManager.getNeighborhoodTree( bwcon, a_NhId);
			Iterator nh0i = nh0v.iterator();
			while (nh0i.hasNext())
			{
				BoardwalkNeighborhoodNode bnn = (BoardwalkNeighborhoodNode)nh0i.next();
				printNH(bnn, -1);
				System.out.println("sb:\n" + sb.toString());
			}

		}
		catch (BoardwalkException bwe)
		{
			System.out.println("Error fetching neighborhood");
		}
		if (sb.length() > 0)
		{
			//System.out.println("Removing last character from SB");
			sb.deleteCharAt(sb.length()-1);
		}
		return sb.toString();
	}


	//Generate StringBuffer of NH Structure as 'NhName|NhId|ParentNhId|Level'
    private static void printNH(BoardwalkNeighborhoodNode bnn, int parent)
    {
		int par; 
		int level;
		for (int i = 0; i<= bnn.getNeighborhood().getLevel(); i++)
		{
			System.out.print("\t");
			sb.append("\t");
		}
		par = bnn.getNeighborhood().getId();
		level = bnn.getNeighborhood().getLevel();
		System.out.println(bnn.getNeighborhood().getName() + " id=" + bnn.getNeighborhood().getId());
		sb.append(bnn.getNeighborhood().getName() + "|" + bnn.getNeighborhood().getId() + "|"+ parent + "|"+ level + "\n" );

		Vector children = bnn.getChildren();
		Iterator ci = children.iterator();
		while (ci.hasNext())
		{
			BoardwalkNeighborhoodNode bnnc = (BoardwalkNeighborhoodNode)ci.next();
			printNH(bnnc, par);
		}
	}

	//API Create New User
	public static int CreateNewUser(BoardwalkConnection bwcon, String user, String extUser, String pwd, String firstname, String lastname) throws  BoardwalkException
	{
		int activeFlag = 1;
		int userId = BoardwalkUserManager.createUser(bwcon, user, extUser, pwd, firstname, lastname, activeFlag);
		if (userId == -1)
				throw new BoardwalkException( 11003 );

		System.out.println("Successfully created user with id = " + userId);
		return userId;
	}


	//API Get UserList	
    public static String GetUserList(BoardwalkConnection bwcon) throws BoardwalkException 
	{
		StringBuffer sbUserList = new StringBuffer();
		try
		{
			Vector userList = BoardwalkUserManager.getUserList(bwcon);
			Iterator ui = userList.iterator();
			while (ui.hasNext())
			{
				BoardwalkUser bu = (BoardwalkUser)ui.next();
				//System.out.println(bu.getId() + ":" + bu.getUserName() + ":" + bu.getExtUserName() + ":" + bu.getFirstName() + ":" + bu.getLastName() + ":" + bu.getActive());
				//getExtUserName
				//sbUserList.append(bu.getId() + "|" + bu.getUserName() + "|" + bu.getFirstName() + "|" + bu.getLastName() + "\n");
				sbUserList.append(bu.getId() + "|" + bu.getUserName()  + "|" + bu.getExtUserName() + "|" + bu.getFirstName() + "|" + bu.getLastName() + "|" + bu.getActive() + "\n");
			}
		}
		catch (BoardwalkException bwe)
		{
			System.out.println("Error fetching neighborhood");
		}
		return sbUserList.toString();
	}

	//Get ALL Membership Info
	public static String GetAllMembershipsInfo( Connection connection)
	throws SystemException
	{
        ResultSet rs = null;
        CallableStatement cs  = null;
        try
        {
			cs = connection.prepareCall(CALL_BW_GET_ALL_MEMBERSHIPS_INFO);

			System.out.println("before calling CALL_BW_GET_ALL_MEMBERSHIPS_INFO i.e. "  + CALL_BW_GET_ALL_MEMBERSHIPS_INFO);
			cs.execute();
            rs = cs.getResultSet();
			System.out.println("after calling CALL_BW_GET_ALL_MEMBERSHIPS_INFO");

			int memberId, userId, nhId, nhLevel, active; //Modified by Lakshman on 20180329 to fix the Issue Id: 14264
			String firstName, lastName, emailAddress;
			StringBuffer sb = new StringBuffer();

			System.out.println("before while rs loop");

            while ( rs.next() )
            {
				//System.out.println("inside while rs loop");
                memberId = rs.getInt("MemberId");
                userId = rs.getInt("UserId");
                firstName = rs.getString("FirstName");
                lastName = rs.getString("LastName");
                emailAddress = rs.getString("Email_Address");
                nhId = rs.getInt("NhId");
                nhLevel = rs.getInt("NhLevel");
				active = rs.getInt("active"); //Modified by Lakshman on 20180329 to fix the Issue Id: 14264

				sb.append(memberId + Seperator + userId + Seperator + firstName + Seperator + lastName + Seperator + emailAddress + Seperator + nhId + Seperator + nhLevel + Seperator + active + "\n"); //Modified by Lakshman on 20180329 to fix the Issue Id: 14264
				//System.out.println("sb: " + sb.toString());
			}
			System.out.println("outside while rs loop");

			if (sb.length() > 0)
			{
				//System.out.println("Removing last character from SB");
				sb.deleteCharAt(sb.length()-1);
			}
			//System.out.println("sb before return: " + sb.toString());
			return sb.toString();

        }
        catch(SQLException sqlexception)
        {
			System.out.println(sqlexception.toString());
            throw new SystemException(sqlexception);
        }
        finally
        {
            try
            {
				if ( rs != null )
					rs.close();
				if ( cs != null )
					cs.close();
            }
            catch(SQLException sqlexception1) {
				System.out.println("throwing  sqlexception1");
                throw new SystemException(sqlexception1);
            }
        }
	}

	// Create Membershiup for User in Neighborhood
	public static int CreateMembershipForNeighborhood( BoardwalkConnection bwcon, int nhId, int userId)
	throws BoardwalkException
	{
		int memberId = BoardwalkNeighborhoodManager.createMember(bwcon, nhId, userId);
		if (memberId == -1)
				throw new BoardwalkException( 10017 );
		System.out.println("Successfully created Membership for User:" + userId + " under Nh:" + nhId);
		return memberId;		
	}


	// DELETE MEMBERSHIP
	public boolean DeleteMembership(BoardwalkConnection bwcon, int memberId) throws  BoardwalkException  
	{
		System.out.println("Deleteing Membership " + memberId);
		boolean retVal = true;
		try
		{
			//System.out.println("before Calling BoardwalkNeighborhoodManager.deleteNeighborhood nhId=" + memberId );
			BoardwalkNeighborhoodManager.deleteMember(bwcon, memberId);
			//System.out.println("after Calling BoardwalkNeighborhoodManager.deleteNeighborhood nhId=" + memberId );
		}
		catch (Exception e)
		{
			System.out.println(e.toString());
			retVal = false;
		}
		return retVal;
	}

	//GET COLLABORATION TREE USING COLLAB ID
    public static String GetCollaborationTreeUsingId(
        BoardwalkConnection bwcon,
        int collabId
		) throws BoardwalkException 
	{
		try
		{
			sb = new StringBuffer();				
			BoardwalkCollaborationNode bcn =
				BoardwalkCollaborationManager.getCollaborationTree(bwcon, collabId);
			System.out.println("Sucessfully fetched the collab tree from the database");
			
			String collabName = bcn.getName();
			String whiteBoard = "";
			String tableName = "";
			String Collabline="";
			int wbId;
			int tableId;

			System.out.println("Collaboration = " + bcn.getName());
			Vector wv = bcn.getWhiteboards();
			Iterator wvi = wv.iterator();

			while ( wvi.hasNext())
			{
				whiteBoard = "";
				BoardwalkWhiteboardNode bwn = (BoardwalkWhiteboardNode)wvi.next();
				System.out.println("\tWhiteboard = " + bwn.getName());
				whiteBoard = bwn.getName();
				wbId= bwn.getId();

				Vector tv = bwn.getTables();
				Iterator tvi = tv.iterator();

				if (tvi.hasNext())
				{
					while (tvi.hasNext())
					{
						tableName = "";
						BoardwalkTableNode btn = (BoardwalkTableNode)tvi.next();
						System.out.println("\t\tTable = " + btn.getName());
						tableName = btn.getName();
						tableId = btn.getId();
						Collabline = collabName + Seperator + whiteBoard + Seperator + tableName + Seperator ;
						Collabline = Collabline + collabId + Seperator + wbId + Seperator + tableId + Seperator ;
						Collabline = Collabline + collabName + "\\" + whiteBoard + "\\" + tableName ;
						sb.append(Collabline + "\n");
					}
				}
				else
				{
					Collabline = collabName + Seperator + whiteBoard + Seperator + " " + Seperator ;
					Collabline = Collabline + collabId + Seperator + wbId + Seperator + " " + Seperator;
					Collabline = Collabline + collabName + "\\" + whiteBoard ;
					sb.append(Collabline + "\n");
				}
			}

			if (sb.length() > 0)
			{
				//System.out.println("Removing last character from SB");
				sb.deleteCharAt(sb.length()-1);
			}
			return sb.toString();

		}
		catch (NoSuchElementException nse)
		{
			System.out.println("Collaboration of this Id does not exists.");
			throw new BoardwalkException( 10018 );
		}
	}


	// NEW API. Get All Neighborhood Collaboration Tree Managed by User
	public static String GetCollaborationTreeForNhManagedByUser(
		Connection connection,
        BoardwalkConnection bwcon,
		int userId 
		) throws BoardwalkException , SystemException
	{
		StringBuffer sb = new StringBuffer();
		ResultSet rs = null;
		PreparedStatement stmt = null;
		int nhId, nhLevel;
		String CollabforNh, nhName ;

		try
		{
			stmt = connection.prepareStatement(CALL_BW_GET_NHS_AT_LEVEL_0);
			stmt.setInt(1, userId );

			rs = stmt.executeQuery();
			
			while (rs.next())
			{
				nhId = rs.getInt(1);
				nhName = rs.getString(2);
				nhLevel = 0;
				CollabforNh = "";
				try
				{
					System.out.println("calling GetCollaborationTreeForNh for NH: " + nhId);
					CollabforNh = GetCollaborationTreeForNh(connection, bwcon, nhId);
					if (CollabforNh.equals(""))
					{
						CollabforNh = nhName + Seperator + " " + Seperator + " " + Seperator + " " + Seperator ;
						CollabforNh = CollabforNh + nhId + Seperator + " " + Seperator + " " + Seperator + " " + Seperator;
						CollabforNh = CollabforNh + " "  ;
					}
					System.out.println("CollabforNh for NH: " + nhId + " :: " + CollabforNh);

				}
				catch (Exception e)
				{
					CollabforNh = nhName + Seperator + " " + Seperator + " " + Seperator + " " + Seperator ;
					CollabforNh = CollabforNh + nhId + Seperator + " " + Seperator + " " + Seperator + " " + Seperator;
					CollabforNh = CollabforNh + " "  ;
					System.out.println("Exception:::::::" + CollabforNh);
				}
				sb.append(CollabforNh + "\n");
			}
			if (sb.length() > 0)
			{
				//System.out.println("Removing last character from SB");
				sb.deleteCharAt(sb.length()-1);
			}
			return sb.toString();
		}
		catch (SQLException sqe)
		{
			return "Error in GetCollaborationTreeForAllNh";
		}
		finally
        {
            try
            {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
            }
            catch(SQLException sqlexception1) {
				System.out.println("throwing  sqlexception1");
                throw new SystemException(sqlexception1);
            }
        }

	}
		
		//Deactivate User 05162016
		public static int deactivateUser(Connection connection , String usermail)
			{
	
				try
					{	
						System.out.println("DEACTIVATE USER");
						//bwUserRequest.print();
						User u = UserManager.getUser(connection,  usermail);
						if (u != null && u.getId() > -1 )
						{
							System.out.println("DEACTIVATE USER" + u.getId() );
							MemberManager.deactivateUser( connection, u.getId() );
							return 1;
						}
						return 0;
					}
					catch (Exception e)
					{
						e.printStackTrace();
						
						return 0;
					}
					
			}
			
			//Activate User 05162016
		public static int activateUser(Connection connection , String usermail)
			{
	
				try
					{	
						System.out.println("ACTIVATE USER");
						//bwUserRequest.print();
						User u = UserManager.getdeactivatedUser(connection,  usermail);
						if (u != null && u.getId() > -1 )
						{
							System.out.println("ACTIVATE USER" + u.getId() );
							MemberManager.activateUser( u.getId() );
							return 1;
						}
						return 0;
					}
					catch (Exception e)
					{
						e.printStackTrace();
						
						return 0;
					}
					
			}

	// NEW API. Get All Neighborhood Collaboration Tree
    public static String GetCollaborationTreeForAllNh(
		Connection connection,
        BoardwalkConnection bwcon
		) throws BoardwalkException , SystemException
	{
		StringBuffer sb = new StringBuffer();
		String query = null;
		ResultSet rs = null;
		PreparedStatement stmt = null;
		int nhId, nhLevel;
		String CollabforNh, nhName ;

		try
		{
			query = "SELECT ID, NAME, NEIGHBORHOOD_LEVEL FROM BW_NH WHERE IS_ACTIVE = 1";
			stmt = connection.prepareStatement(query);
			rs = stmt.executeQuery();
			
			while (rs.next())
			{
				nhId = rs.getInt(1);
				nhName = rs.getString(2);
				nhLevel = rs.getInt(3);
				CollabforNh = "";
				try
				{
					System.out.println("calling GetCollaborationTreeForNh for NH: " + nhId);
					CollabforNh = GetCollaborationTreeForNh(connection, bwcon, nhId);
					if (CollabforNh.equals(""))
					{
						CollabforNh = nhName + Seperator + " " + Seperator + " " + Seperator + " " + Seperator ;
						CollabforNh = CollabforNh + nhId + Seperator + " " + Seperator + " " + Seperator + " " + Seperator;
						CollabforNh = CollabforNh + " "  ;
					}
					System.out.println("CollabforNh for NH: " + nhId + " :: " + CollabforNh);

				}
				catch (Exception e)
				{
					CollabforNh = nhName + Seperator + " " + Seperator + " " + Seperator + " " + Seperator ;
					CollabforNh = CollabforNh + nhId + Seperator + " " + Seperator + " " + Seperator + " " + Seperator;
					CollabforNh = CollabforNh + " "  ;
					System.out.println("Exception:::::::" + CollabforNh);
				}
				sb.append(CollabforNh + "\n");
			}
			if (sb.length() > 0)
			{
				//System.out.println("Removing last character from SB");
				sb.deleteCharAt(sb.length()-1);
			}
			return sb.toString();
		}
		catch (SQLException sqe)
		{
			return "Error in GetCollaborationTreeForAllNh";
		}
		finally
        {
            try
            {
				if ( rs != null )
					rs.close();
				if ( stmt != null )
					stmt.close();
            }
            catch(SQLException sqlexception1) {
				System.out.println("throwing  sqlexception1");
                throw new SystemException(sqlexception1);
            }
        }
	}


	//NEW API Collab Tree for NH
    public static String GetCollaborationTreeForNh(
		Connection connection,
        BoardwalkConnection bwcon,
		int nhId
		) throws BoardwalkException 
	{
		try
		{
	        Neighborhood nh = null;
			nh = NeighborhoodManager.getNeighborhoodById(connection, nhId);

			sb = new StringBuffer();				
			
			String nhName = nh.getName();
			String collabName = "";
			String whiteBoard = "";
			String tableName = "";
			String Collabline="";
			int wbId;
			int tableId;

			Vector cl = BoardwalkCollaborationManager.getCollaborationsForNeighborhood(bwcon, nhId);
			Iterator cli = cl.iterator();

			while (cli.hasNext())
			{
				Integer collabId = (Integer)cli.next();
				BoardwalkCollaborationNode bcn = BoardwalkCollaborationManager.getCollaborationTree(bwcon, collabId.intValue());
				collabName = bcn.getName();
				System.out.println("Sucessfully fetched the collab tree from the database");

				System.out.println("Collaboration = " + bcn.getName());
				Vector wv = bcn.getWhiteboards();
				Iterator wvi = wv.iterator();
				
				//Modified by Lakshman on 20180903 to fix the Issue Id: 14279
				if (wvi.hasNext())
				{
					while ( wvi.hasNext())
					{
						whiteBoard = "";
						BoardwalkWhiteboardNode bwn = (BoardwalkWhiteboardNode)wvi.next();
						System.out.println("\tWhiteboard = " + bwn.getName());
						whiteBoard = bwn.getName();
						wbId= bwn.getId();

						Vector tv = bwn.getTables();
						Iterator tvi = tv.iterator();

						if (tvi.hasNext())
						{
							while (tvi.hasNext())
							{
								tableName = "";
								BoardwalkTableNode btn = (BoardwalkTableNode)tvi.next();
								System.out.println("\t\tTable = " + btn.getName());
								tableName = btn.getName();
								tableId = btn.getId();

								Collabline = nhName + Seperator + collabName + Seperator + whiteBoard + Seperator + tableName + Seperator ;
								Collabline = Collabline + nhId + Seperator + collabId + Seperator + wbId + Seperator + tableId + Seperator ;
								Collabline = Collabline + collabName + "\\" + whiteBoard + "\\" + tableName ;
								sb.append(Collabline + "\n");
							}
						}
						else
						{
							Collabline = nhName + Seperator + collabName + Seperator + whiteBoard + Seperator + " " + Seperator ;
							Collabline = Collabline + nhId + Seperator + collabId + Seperator + wbId + Seperator + " " + Seperator;
							Collabline = Collabline + collabName + "\\" + whiteBoard ;
							sb.append(Collabline + "\n");
						}
					}
				}
				//Added by Lakshman on 20180903 to fix the Issue Id: 14279
				else
				{
					Collabline = nhName + Seperator + collabName + Seperator + " " + Seperator + " " + Seperator ;
					Collabline = Collabline + nhId + Seperator + collabId + Seperator + " " + Seperator + " " + Seperator;
					Collabline = Collabline + collabName;
					sb.append(Collabline + "\n");
				}
			}

			if (sb.length() > 0)
			{
				//System.out.println("Removing last character from SB");
				sb.deleteCharAt(sb.length()-1);
			}
			return sb.toString();
		}
		catch (NoSuchElementException nse)
		{
			System.out.println("Collaboration of this Id does not exists.");
			throw new BoardwalkException( 10019 );
		}
		catch(Exception e)
		{
			//start here fix error here
			System.out.println("Collaboration of this Id does not exists.");
			throw new BoardwalkException( 10019 );
		}
	}
	


	//To Generate StringBuffer of NH Information as bwNHInfo = 'NhName|NhId|ParentNhId|Level'
	//sb.append(bnn.getNeighborhood().getName() + "|" + bnn.getNeighborhood().getId() + "|"+ parent + "|"+ level + "\n" );

	//To Generate StringBuffer of NH Structure as bwNHStructure = 'Nh0|Nh1|Nh2|Nh3|Nh0Id|Nh1Id|Nh2Id|Nh3Id|NhHierarchy
	//sb.append(bnn.getNeighborhood().getName() + "|" + bnn.getNeighborhood().getId() + "|"+ parent + "|"+ level + "\n" );

	public static String GetAllNeighborhoodTree(
        Connection connection,
        int a_NhId
		) throws BoardwalkException 
	{
		try
		{

			StringBuffer nh0sb, nh1sb, nh2sb, nh3sb, bwNHStructuresb, bwNHInfosb;
			

			int nhCount = 0;

			sb = new StringBuffer();			
			bwNHStructuresb = new StringBuffer();
			bwNHInfosb = new StringBuffer();

			Vector nhTree = NeighborhoodManager.getNeighborhoodTree(connection, 1);

			Iterator nhIter = nhTree.iterator();
			while (nhIter.hasNext())
			{
				//nh0sb = new StringBuffer();
				//nh1sb = new StringBuffer();
				//nh2sb = new StringBuffer();
				//nh3sb = new StringBuffer();

				nhCount ++;
				NHTree nht = (NHTree)nhIter.next();
				Neighborhood nh0 =  nht.getNeighborhood();
				Vector nh1Tree = nht.getChildren();

				//nh0sb.append("/" + nh0.getName() + Seperator + "/" + nh0.getId() + Seperator + "0");
				//System.out.println("0 level - " + nh0sb.toString());
				//sb.append(nh0sb.toString() + ContentDelimeter);

				//bwNHInfo = 'NhName|NhId|ParentNhId|Level'
				bwNHInfosb.append(nh0.getName() + PIPE_CHAR + nh0.getId() + PIPE_CHAR + "-1" + PIPE_CHAR + "0" + Seperator);

				//bwNHStructure = 'Nh0|Nh1|Nh2|Nh3|Nh0Id|Nh1Id|Nh2Id|Nh3Id|NhHierarchy
				bwNHStructuresb.append(nh0.getName() + PIPE_CHAR + PIPE_CHAR + PIPE_CHAR + PIPE_CHAR + nh0.getId() + PIPE_CHAR + PIPE_CHAR + PIPE_CHAR + PIPE_CHAR + nh0.getName() + Seperator);

				//sb.append("0L:" + nh0.getName() + "\n");

				if (!nh1Tree.isEmpty())
				{

					Iterator nh1Iter = nh1Tree.iterator();
					while (nh1Iter.hasNext())
					{
						//nh1sb = new StringBuffer();
						nhCount++;
						NHTree nh1t = (NHTree)nh1Iter.next();
						Neighborhood nh1 =  nh1t.getNeighborhood();
		
						//nh1sb.append("/" +  nh0.getName() + "/" +  nh1.getName() + Seperator + "/" +  nh0.getId() + "/" +  nh1.getId()  + Seperator + "1");
						//System.out.println("1 level - " + nh1sb.toString());
						//sb.append(nh1sb.toString() + ContentDelimeter);

						//bwNHInfo = 'NhName|NhId|ParentNhId|Level'
						bwNHInfosb.append(nh1.getName() + PIPE_CHAR + nh1.getId() + PIPE_CHAR + nh0.getId() + PIPE_CHAR + "1" + Seperator);

						//bwNHStructure = 'Nh0|Nh1|Nh2|Nh3|Nh0Id|Nh1Id|Nh2Id|Nh3Id|NhHierarchy
						bwNHStructuresb.append(nh0.getName() + PIPE_CHAR + nh1.getName() + PIPE_CHAR + PIPE_CHAR + PIPE_CHAR + nh0.getId() + PIPE_CHAR  + nh1.getId() + PIPE_CHAR + PIPE_CHAR + PIPE_CHAR + nh0.getName() + "/" + nh1.getName() + Seperator);

						Vector nh2Tree = nh1t.getChildren();


						if (!nh2Tree.isEmpty())
						{
							Iterator nh2Iter = nh2Tree.iterator();
							while (nh2Iter.hasNext())
							{
								//nh2sb = new StringBuffer();
								nhCount++;
								NHTree nh2t = (NHTree)nh2Iter.next();
								Neighborhood nh2 =  nh2t.getNeighborhood();

								//nh2sb.append("/" +  nh0.getName() + "/" +  nh1.getName() + "/" +  nh2.getName() + Seperator + "/" +  nh0.getId() + "/" +  nh1.getId() + "/" +  nh2.getId() + Seperator + "2");
								//System.out.println("2 level - " + nh2sb.toString());
								//sb.append(nh2sb.toString() + ContentDelimeter);

								//bwNHInfo = 'NhName|NhId|ParentNhId|Level'
								bwNHInfosb.append(nh2.getName() + PIPE_CHAR + nh2.getId() + PIPE_CHAR + nh1.getId() + PIPE_CHAR + "2" + Seperator);

								//bwNHStructure = 'Nh0|Nh1|Nh2|Nh3|Nh0Id|Nh1Id|Nh2Id|Nh3Id|NhHierarchy
								bwNHStructuresb.append(nh0.getName() + PIPE_CHAR + nh1.getName() + PIPE_CHAR + nh2.getName() + PIPE_CHAR + PIPE_CHAR + nh0.getId() + PIPE_CHAR  + nh1.getId() + PIPE_CHAR + nh2.getId() + PIPE_CHAR + PIPE_CHAR + nh0.getName() + "/" + nh1.getName() + "/" + nh2.getName() + Seperator);

								Vector nh3Tree = nh2t.getChildren();

								if (!nh3Tree.isEmpty())
								{
									Iterator nh3Iter = nh3Tree.iterator();
									while (nh3Iter.hasNext())
									{
										//nh3sb = new StringBuffer();
										nhCount++;
										NHTree nh3t = (NHTree)nh3Iter.next();
										Neighborhood nh3 =  nh3t.getNeighborhood();

										//nh3sb.append("/" + nh0.getName() + "/" +  nh1.getName() + "/" +  nh2.getName() + "/" +  nh3.getName() + Seperator + "/" + nh0.getId() + "/" +  nh1.getId() + "/" +  nh2.getId() + "/" +  nh3.getId() + Seperator + "3");
										//sb.append("..1L:" + nh1.getName() );
										//sb.append(nh3sb.toString() + ContentDelimeter);

										//bwNHInfo = 'NhName|NhId|ParentNhId|Level'
										bwNHInfosb.append(nh3.getName() + PIPE_CHAR + nh3.getId() + PIPE_CHAR + nh2.getId() + PIPE_CHAR + "3" + Seperator);

										//bwNHStructure = 'Nh0|Nh1|Nh2|Nh3|Nh0Id|Nh1Id|Nh2Id|Nh3Id|NhHierarchy
										bwNHStructuresb.append(nh0.getName() + PIPE_CHAR + nh1.getName() + PIPE_CHAR + nh2.getName() + PIPE_CHAR + nh3.getName() + PIPE_CHAR + nh0.getId() + PIPE_CHAR  + nh1.getId() + PIPE_CHAR + nh2.getId() + PIPE_CHAR + nh3.getId() + PIPE_CHAR + nh0.getName() + "/" + nh1.getName() + "/" + nh2.getName() + "/" + nh3.getName() + Seperator);

										//System.out.println("3 level - " + nh3sb.toString());
										//sb.append("..3L:" + nh3.getName() );

									}
								}// if
							}// while
						}// if
					}//while
				}// if
			} // while
			if (bwNHInfosb.length() > 0)
			{
				System.out.println("Removing last character from bwNHInfosb");
				bwNHInfosb.deleteCharAt(bwNHInfosb.length()-1);
			}
			if (bwNHStructuresb.length() > 0)
			{
				System.out.println("Removing last character from bwNHStructuresb");
				bwNHStructuresb.deleteCharAt(bwNHStructuresb.length()-1);
			}
			sb.append(bwNHStructuresb + ContentDelimeter + bwNHInfosb);

			System.out.println("sb:\n" + sb.toString());
		}
		catch (Exception bwe)
		{
			System.out.println("Error fetching neighborhood");
		}
//		if (sb.length() > 0)
//		{
//			System.out.println("Removing last character from SB");
//			sb.deleteCharAt(sb.length()-1);
//		}
		return sb.toString();
	}


}

