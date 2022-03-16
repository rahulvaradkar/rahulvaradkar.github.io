package servlets;
/*
 *  xlAdminServiceJson.java
 */

import com.google.gson.Gson;

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

//import com.boardwalk.wizard.*;
//import com.boardwalk.user.*;

public class xlAdminServiceJson extends xlService implements SingleThreadModel
{
	public final static String Seperator = new Character((char)1).toString();
	public final static String ContentDelimeter = new Character((char)2).toString();
//	public final static String PipeDelimeter = new Character((char)124).toString();

    private static String CALL_BW_GET_ALL_MEMBERSHIPS_INFO = "{CALL BW_GET_ALL_MEMBERSHIPS_INFO}";
    private static String CALL_BW_GET_NHS_AT_LEVEL_0 = "{CALL BW_GET_NHS_AT_LEVEL_0(?)}";
	private static String CALL_BW_UPD_COL_ACCESS = "{CALL BW_UPD_COL_ACCESS(?,?,?,?)}";
	private static String CALL_BW_BRECTDEFINITION_LOAD = "{CALL BW_BRECTDEFINITION_LOAD()}";

	//Added by RahulV on 21-February-2015	
	private static String CALL_BW_GET_COL_ACCESS_FOR_UPDATE = "{CALL BW_GET_COL_ACCESS_FOR_UPDATE(?)}";
	private static String CALL_BW_GET_COL_ACCESS_FOR_UPDATE_ALL_CUBOIDS = "{CALL BW_GET_COL_ACCESS_FOR_UPDATE_ALL_CUBOIDS()}";
	private static String CALL_BW_GET_ALL_COL_ACCESS_FOR_CUBOID  = "{CALL BW_GET_ALL_COL_ACCESS_FOR_CUBOID (?)}";


	//Added by RahulV on 09-April-2015	
	private static String CALL_BW_SELECT_CUBOID  = "{CALL BW_SELECT_CUBOID (?)}";
	private static String CALL_BW_SELECT_BRECT  = "{CALL BW_SELECT_BRECT (?)}";
	private static String CALL_BW_USER_ROLE_MAPPING_LOAD  = "{CALL BW_USER_ROLE_MAPPING_LOAD}";

	private static String PIPE_CHAR = "|";

	HttpServletRequest req;
	HttpServletResponse res;

	Connection connection = null;
	BoardwalkConnection bwcon = null;

	static StringBuffer sb = null;

	int userId;

	
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
		String userName;
		String relationName;
		int collabId;
		int wbId;
		int tableId;
		int nhId;
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
//ResetUserPassword(UserId, OldPwd, NewPwd)
//ChangeRowOwner(rowId,  UserName)
//GetTableAccess(TableId)
//SetTableAccess(TableId)
//RenameTable(tableId, newTableName)
//RenameNh(nhId, newNhName)
//GetCollaborationTreeForAllNh
//PurgeTable
//GetAllNeighborhoodTree()
//GetColumnAccessOnTable()
//SetColumnAccessOnTable()
//GetCuboidColumnAccessForAllCuboids()
//GetAllColumnAccessForCuboid()
//GetRelationShipInfo()

		bwcon = getBoardwalkConnection();
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
							responseBuffer = "SUCCESS" + Seperator +  "New Collaboration with name " + collabName + " is successfuly created." + ContentDelimeter + collabId  ;
						break;

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

					case 3:						//DeleteCollaboration
						wrkstr = st.nextToken(Seperator);
						collabName = wrkstr;
						wrkstr = st.nextToken(Seperator);
						collabId = Integer.parseInt(wrkstr);

						if (DeleteCollaboration (bwcon, collabId))
							responseBuffer = "SUCCESS" + Seperator +  "The Collaboration with name '" + collabName + "' is successfuly deleted." + ContentDelimeter + collabId  ;
						
						break;
					
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
					
					case 6:						//CreateWhiteboard
						wrkstr = st.nextToken(Seperator);
						collabId = Integer.parseInt(wrkstr);
						wrkstr = st.nextToken(Seperator);
						collabName = wrkstr;
						wrkstr = st.nextToken(Seperator);
						wbName = wrkstr;
						wbId = CreateWhiteboard (bwcon, collabId, wbName);
						if (wbId != -1)
							responseBuffer = "SUCCESS" + Seperator +  "New Whiteboard with name '" + wbName + "' is successfuly created under Collaboration '" + collabName + "'." + ContentDelimeter + wbId  ;
						break;

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
							responseBuffer = "SUCCESS" + Seperator +  "The Whiteboard with name '" + wbName + "' is successfuly deleted from Collobarion '" + collabName + "'." + ContentDelimeter + wbId  ;
						
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
							responseBuffer = "SUCCESS" + Seperator +  "New Table with name '" + tableName + "' is successfuly created under Whiteboard '" + wbName + "' of Collaboration '" + collabName + "'." + ContentDelimeter + tableId  ;
						break;
					
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
							responseBuffer = "SUCCESS" + Seperator +  "The Table '" + tableName + "' present under Whiteboard '" + wbName + "' of Collaboration '" + collabName + "' is successfuly deleted." + ContentDelimeter + tableId  ;
						
						break;

					case 11:					//GetCollabForNH
						wrkstr = st.nextToken(Seperator);
						nhId = Integer.parseInt(wrkstr);
						String CollabforNh = "";
						CollabforNh = GetCollaborationTreeForNh(connection, bwcon, nhId);
						//responseBuffer = "SUCCESS" + Seperator +  "The Neighborhood of " + nhId + "is \n" + tree + ContentDelimeter + nhId  ;
						responseBuffer = "SUCCESS" + ContentDelimeter +  CollabforNh + ContentDelimeter + nhId  ;
						break;

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
								responseBuffer = "SUCCESS" + Seperator +  "New Neighborhood with name '" + nhName + "' is successfuly created under Neighborhood '" + parentNHName + "'." + ContentDelimeter + nhId  ;
							else
								responseBuffer = "SUCCESS" + Seperator +  "New Neighborhood with name '" + nhName + "' is successfuly created at level 0." + ContentDelimeter + nhId  ;
						}
						break;

					case 15:					//DeleteNeighborhood
						wrkstr = st.nextToken(Seperator);
						nhId = Integer.parseInt(wrkstr);
						wrkstr = st.nextToken(Seperator);
						nhName = wrkstr;
						if (DeleteNeighborhood(bwcon, nhId))
							responseBuffer = "SUCCESS" + Seperator +  "The Neighborhood '" + nhName + "' is successfuly deleted." + ContentDelimeter + nhId  ;
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
						try
						{

							System.out.println("before Calling BoardwalkNeighborhoodManager.deleteNeighborhood nhId=" + memberId );
							BoardwalkNeighborhoodManager.deleteMember(bwcon, memberId);
							System.out.println("after Calling BoardwalkNeighborhoodManager.deleteNeighborhood nhId=" + memberId );

							DeleteMembership(bwcon, memberId);
							responseBuffer = "SUCCESS" + Seperator +  "The Membership is successfuly deleted." + ContentDelimeter + memberId  ;

						}
						catch (BoardwalkException e)
						{
							responseBuffer = "FAILURE" + ContentDelimeter +  e.getMessage() +  "\nFailed to delete Membership." + ContentDelimeter   ;
						}
						
						break;

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
					
					case 22:					//CreateNewUser
						String user = st.nextToken(Seperator);
						String pwd = st.nextToken(Seperator);
						String firstname = st.nextToken(Seperator);
						String lastname = st.nextToken(Seperator);
						
						userId = CreateNewUser(bwcon, user, pwd, firstname, lastname);
						System.out.println("Case22: New user id=" + userId);	
						if (userId != -1)
						{	
							//ResetPassword(connection, user);
							responseBuffer = "SUCCESS" + Seperator +  "New User with name '" + firstname + " " + lastname + "' is successfuly created." + ContentDelimeter + userId  ;
						}
						break;

					case 23:					//GetUserList
						String userList = "";
						userList = GetUserList(bwcon);
						//responseBuffer = "SUCCESS" + Seperator +  "The Neighborhood of " + nhId + "is \n" + tree + ContentDelimeter + nhId  ;
						responseBuffer = "SUCCESS" + Seperator +  userList   ;
						break;

					case 24:					//ResetUserPassword
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
							responseBuffer = "SUCCESS" + Seperator +  "The Table '" + tableName + "' present under Whiteboard '" + wbName + "' of Collaboration '" + collabName + "' is successfuly renamed as '" + newTableName + "'." + ContentDelimeter + tableId  ;

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
							responseBuffer = "SUCCESS" + Seperator +  "The Neighborhood '" + nhName + "' is successfuly renamed as '" + newNhName + "'." + ContentDelimeter + nhId  ;

						break;

					case 30:
						String allMembershipInfo = "";
						try
						{
							allMembershipInfo = GetAllMembershipsInfo(connection);
							System.out.println("allMembershipInfo : " + allMembershipInfo);
							//responseBuffer = "SUCCESS" + Seperator +  "The Neighborhood of " + nhId + "is \n" + tree + ContentDelimeter + nhId  ;
							responseBuffer = "SUCCESS" + ContentDelimeter +  allMembershipInfo + ContentDelimeter   ;
						}
						catch (Exception e)
						{
							responseBuffer = "FAILURE" + ContentDelimeter +  e.getMessage() +  "\nFailed to retrieve all Membership Information. Try again" + ContentDelimeter   ;
						}
						
						break;
				
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

					case 35:			//GetColumnAccessOnTable()
						wrkstr = st.nextToken(Seperator);
						tableId = Integer.parseInt(wrkstr);
						String columnAccess = "";
						try
						{
							//columnAccess = GetColumnAccessOnTable(connection, tableId);
							columnAccess = getColumnAccessForUpdate(connection, tableId);

							//responseBuffer = "SUCCESS" + Seperator +  "The Neighborhood of " + nhId + "is \n" + tree + ContentDelimeter + nhId  ;
							responseBuffer = "SUCCESS" + ContentDelimeter +  columnAccess + ContentDelimeter + tableId ;
						}
						catch (Exception e)
						{
							responseBuffer = "FAILURE" + ContentDelimeter +  e.getMessage() +  "\nFailed to retrieve all Column Access on Cuboid. Try again" + ContentDelimeter   ;
						}
						break;


					case 36:			//SetColumnAccessOnTable()
						wrkstr = st.nextToken(Seperator);
						tableId = Integer.parseInt(wrkstr);
						wrkstr = st.nextToken(Seperator);
						columnAccess = "";
						columnAccess = wrkstr;
						System.out.println("table id : " + tableId);
						System.out.println("Column Access : " + columnAccess);
						try
						{
							//columnAccess = GetColumnAccessOnTable(connection, tableId);
							
							SetColumnAccessOnTable(bwcon, connection, columnAccess);					// original
							responseBuffer = "SUCCESS" + ContentDelimeter +  columnAccess + ContentDelimeter + tableId ;
						}
						catch (Exception e)
						{
							responseBuffer = "FAILURE" + ContentDelimeter +  e.getMessage() +  "\nFailed to retrieve all Column Access on Cuboid. Try again" + ContentDelimeter   ;
						}
						break;

					case 37:		//GetCuboidColumnAccessForAllCuboids

						try
						{
							//columnAccess = GetColumnAccessOnTable(connection, tableId);
							columnAccess = "";
							columnAccess = GetCuboidColumnAccessForAllCuboids(connection);					// original
							// getColumnAccessForUpdate(connection, 
							//columnAccess = SetColumnAccessOnTable(connection, columnAccess,userId);		// by Arun
							//responseBuffer = "SUCCESS" + Seperator +  "The Neighborhood of " + nhId + "is \n" + tree + ContentDelimeter + nhId  ;
							responseBuffer = "SUCCESS" + ContentDelimeter +  columnAccess + ContentDelimeter ;
						}
						catch (Exception e)
						{
							responseBuffer = "FAILURE" + ContentDelimeter +  e.getMessage() +  "\nFailed to retrieve all Column Access for all Cuboids. Try again" + ContentDelimeter   ;
						}
						break;

					case 38:		//GetAllColumnAccessForCuboid
						wrkstr = st.nextToken(Seperator);
						tableId = Integer.parseInt(wrkstr);
						try
						{
							//columnAccess = GetColumnAccessOnTable(connection, tableId);
							columnAccess = "";
							columnAccess = GetAllColumnAccessForCuboid(connection, tableId);					// original
							// getColumnAccessForUpdate(connection, 
							//columnAccess = SetColumnAccessOnTable(connection, columnAccess,userId);		// by Arun
							//responseBuffer = "SUCCESS" + Seperator +  "The Neighborhood of " + nhId + "is \n" + tree + ContentDelimeter + nhId  ;
							responseBuffer = "SUCCESS" + ContentDelimeter +  columnAccess + ContentDelimeter ;
						}
						catch (Exception e)
						{
							responseBuffer = "FAILURE" + ContentDelimeter +  e.getMessage() +  "\nFailed to retrieve all Column Access for all Cuboids. Try again" + ContentDelimeter   ;
						}
						break;


					case 39:		//GetRelationShipInfo()
						wrkstr = st.nextToken(Seperator);
						nhId = Integer.parseInt(wrkstr);
						wrkstr = st.nextToken(Seperator);
						userId = Integer.parseInt(wrkstr);
						String str = "";
						try
						{

						   NhName	 nm = null;
						   Vector nhRel = null;

							System.out.println("nhId=" + nhId +  "   .... userId=" + userId );

							if ( nhId != -1 )
							{
							   nm = NeighborhoodManager.getNeighborhoodNameById(connection, nhId);
							   nhRel = NeighborhoodManager.getNeighborhoodRelations(connection,nhId);
							   Vector nhTree = NeighborhoodManager.getNeighborhoodTree(connection, userId);

								str = processRelation( nm, nhRel, nhTree);

								System.out.println("str..........." + str);
							}

						//responseBuffer = "FAILURE" + ContentDelimeter +  e.getMessage() +  "\nFailed to retrieve Relationship Info. Try again" + ContentDelimeter   ;
							responseBuffer = "SUCCESS" + ContentDelimeter +  str ;
						}
						catch (Exception e)
						{
							responseBuffer = "FAILURE" + ContentDelimeter +  e.getMessage() +  "\nFailed to retrieve Relationship Info. Try again" + ContentDelimeter   ;
						}
						break;
						
					case 40:		//AddNewRelation()
						wrkstr = st.nextToken(Seperator);
						nhId = Integer.parseInt(wrkstr);
						wrkstr = st.nextToken(Seperator);
						userId = Integer.parseInt(wrkstr);
						wrkstr = st.nextToken(Seperator);
						relation = wrkstr;
						wrkstr = st.nextToken(Seperator);
						selNhIds = wrkstr;

						System.out.println("Add New Relation");
						System.out.println("nhId : " + nhId);
						System.out.println("userId : " + userId);
						System.out.println("relation : " + relation);
						System.out.println("selNhIds : " + selNhIds);


/*						String[] relatedNhs ;
						relatedNhs = selNhIds.split("\\|");

						Vector targetNeighborhoods = new Vector();
						for ( int r = 0; r < relatedNhs.length; r++ )
						{
							int tnhid = Integer.parseInt(relatedNhs[r]);
							targetNeighborhoods.addElement(new Integer(tnhid));
						}

						BoardwalkNeighborhoodManager.createRelation( bwcon, nhId, relation, targetNeighborhoods);
							System.out.println("Relation " + relation + " created successfully");
							responseBuffer = "SUCCESS" + ContentDelimeter +  relation ;
*/
						AddNewRelation(connection, nhId, userId, relation, selNhIds);

						System.out.println("Relation " + relation + " created successfully");
						responseBuffer = "SUCCESS" + Seperator + "New Relation '" + relation + "' created successfully." +  ContentDelimeter +  relation ;

						break;


					case 41:		//UpdateRelation()
						wrkstr = st.nextToken(Seperator);
						nhId = Integer.parseInt(wrkstr);
						wrkstr = st.nextToken(Seperator);
						userId = Integer.parseInt(wrkstr);
						wrkstr = st.nextToken(Seperator);
						relation = wrkstr;
						wrkstr = st.nextToken(Seperator);
						selNhIds = wrkstr;

						System.out.println("Update Relation");
						System.out.println("nhId : " + nhId);
						System.out.println("userId : " + userId);
						System.out.println("relation : " + relation);
						System.out.println("selNhIds : " + selNhIds);

						if(UpdateRelation(connection, nhId, userId, relation, selNhIds))
						{
							System.out.println("Relation " + relation + " updated successfully");
							responseBuffer = "SUCCESS" + Seperator + "Relation " + relation + " updated successfully." + ContentDelimeter +  relation ;
						}
						break;

				
					case 42:		//DeleteRelation()
						wrkstr = st.nextToken(Seperator);
						nhId = Integer.parseInt(wrkstr);
						wrkstr = st.nextToken(Seperator);
						userId = Integer.parseInt(wrkstr);
						wrkstr = st.nextToken(Seperator);
						relation = wrkstr;
						//wrkstr = st.nextToken(Seperator);
						//selNhIds = wrkstr;

						System.out.println("Delete Relation");
						System.out.println("nhId : " + nhId);
						System.out.println("userId : " + userId);
						System.out.println("relation : " + relation);
						//System.out.println("selNhIds : " + selNhIds);

						BoardwalkNeighborhoodManager.deleteRelation(bwcon, nhId, relation);
						System.out.println("Relation " + relation + " deleted successfully");
						responseBuffer = "SUCCESS" + Seperator +  "Relation " + relation + " deleted successfully." + ContentDelimeter +  relation ;

						commitResponseBufferJson(responseBuffer, res);

						break;

					case 44:		//UpdateBRectDefinition()
						boolean bln = UpdateBRectDefinition(bwcon);
						if (bln)
						{
							responseBuffer = "SUCCESS" + Seperator +  "B-Rect Definition updated successfully into Cuboid. " + ContentDelimeter +  "" ;
						}
						else
						{
							responseBuffer = "FAILURE" + Seperator +  "Failed to update B-Rect Definition into Cuboid. " + ContentDelimeter +  "" ;
						}
						
						break;


					case 45:		//Get User Roles and Role Users Mappings()

						try
						{
							String strRoles = "";
							strRoles = GetRolesFromRoleMaster(bwcon);

							String strRoleUsers = "";
							strRoleUsers = GetDefinedRolesUsers(bwcon);
							System.out.println("strRoleUsers = " + strRoleUsers);
							responseBuffer = "SUCCESS" + ContentDelimeter +  strRoles + ContentDelimeter  + strRoleUsers ;
						}
						catch (Exception e)
						{
							responseBuffer = "FAILURE" + ContentDelimeter +  e.getMessage() +  "\nFailed to retrieve User Roles Information. Try again" + ContentDelimeter   ;
						}
						break;

					case 46:		//Update User Roles
						wrkstr = st.nextToken(Seperator);
						System.out.println("wrkstr=" + wrkstr);
						try
						{
							UpdateUserRoleChanges(bwcon, wrkstr);
							responseBuffer = "SUCCESS" + ContentDelimeter +  wrkstr + ContentDelimeter  ;
						}
						catch (Exception e)
						{
							responseBuffer = "FAILURE" + ContentDelimeter +  e.getMessage() +  "\nFailed to save User Roles Information. Try again" + ContentDelimeter   ;
						}
						break;

					case 47:		//Get Template Manifest Details
						try
						{
							String strTemplateManifestBrects = "";
							strTemplateManifestBrects = GetTemplateManifestsFromBRectDefinition(bwcon);
							System.out.println("strTemplateManifestBrects = " + strTemplateManifestBrects);

							//String strBRectDifinition = "";
							//strBRectDifinition = GetAllBRectDefinition(bwcon);
							//System.out.println("strBRectDifinition = " + strBRectDifinition);
							responseBuffer = "SUCCESS" + ContentDelimeter +  strTemplateManifestBrects ;
						}
						catch (Exception e)
						{
							responseBuffer = "FAILURE" + ContentDelimeter +  e.getMessage() +  "\nFailed to retrieve BRect related Information. Try again" + ContentDelimeter   ;
						}


				}// end of Switch
			}
			catch (BoardwalkException bwe)
			{
				System.out.println("XXXXXXXX.........1");
				System.out.println("Boardwalk error code = " + bwe.getErrorCode());
				
				//17-Feb2015
				BoardwalkMessage bwmsg= new BoardwalkMessages().getBoardwalkMessage( bwe.getErrorCode() );

				System.out.println("Boardwalk message = " + bwmsg.getCause());
				System.out.println("Boardwalk potentioal solution = " + bwmsg.getPotentialSolution());
				responseBuffer = "FAILURE" + Seperator + bwe.getErrorCode() + Seperator + bwmsg.getCause() + "\n" + bwmsg.getPotentialSolution()  + ContentDelimeter;

				//

				//System.out.println("Boardwalk message = " + bwe.getMessage());
				//System.out.println("Boardwalk potentioal solution = " + bwe.getPotentialSolution());
				//responseBuffer = "FAILURE" + Seperator + bwe.getErrorCode() + Seperator + bwe.getMessage() + "\n" + bwe.getPotentialSolution()  + ContentDelimeter;
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
				System.out.println("XXXXXXXX.........Returning Response");
				commitResponseBufferJson(responseBuffer, res);
			}
		}
		else
		{
			commitResponseBufferJson("User Authentication/Connection Failed. \nYou have entered wrong User or Password. Try again.", res);
		}

    }

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
			commitResponseBufferJson("There is a Database connection problem. \nContact Boardwalk Administrator for support.", res);
		}
		catch (BoardwalkException bwe)
		{
			System.out.println("Authentication/Connection Failed.");
			commitResponseBufferJson("User Authentication/Connection Failed. \nYou have entered wrong User or Password. Try again.", res);
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
			System.out.println("Throwing exception...10016"); 
			throw new BoardwalkException( 10016 );
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
			System.out.println("Removing last character from SB");
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
	public static int CreateNewUser(BoardwalkConnection bwcon, String user, String pwd, String firstname, String lastname) throws  BoardwalkException
	{
		int activeFlag = 1;
		int userId = BoardwalkUserManager.createUser(bwcon, user, pwd, firstname, lastname, activeFlag);
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
				System.out.println(bu.getId() + ":" + bu.getUserName() + ":" + bu.getFirstName() + ":" + bu.getLastName());
				sbUserList.append(bu.getId() + "|" + bu.getUserName() + "|" + bu.getFirstName() + "|" + bu.getLastName() + "\n");
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

			int memberId, userId, nhId, nhLevel;
			String firstName, lastName, emailAddress;
			StringBuffer sb = new StringBuffer();

			System.out.println("before while rs loop");

            while ( rs.next() )
            {
			System.out.println("inside while rs loop");
                memberId = rs.getInt("MemberId");
                userId = rs.getInt("UserId");
                firstName = rs.getString("FirstName");
                lastName = rs.getString("LastName");
                emailAddress = rs.getString("Email_Address");
                nhId = rs.getInt("NhId");
                nhLevel = rs.getInt("NhLevel");
				sb.append(memberId + Seperator + userId + Seperator + firstName + Seperator + lastName + Seperator + emailAddress + Seperator + nhId + Seperator + nhLevel + "\n");
				System.out.println("sb: " + sb.toString());
			}
			System.out.println("outside while rs loop");

			if (sb.length() > 0)
			{
				System.out.println("Removing last character from SB");
				sb.deleteCharAt(sb.length()-1);
			}
			System.out.println("sb before return: " + sb.toString());
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
	public void DeleteMembership(BoardwalkConnection bwcon, int memberId) throws BoardwalkException			//SystemException
	{
		System.out.println("Deleteing Membership " + memberId);
		boolean retVal = true;
		try
		{
			System.out.println("before Calling BoardwalkNeighborhoodManager.deleteNeighborhood nhId=" + memberId );
			BoardwalkNeighborhoodManager.deleteMember(bwcon, memberId);
			System.out.println("after Calling BoardwalkNeighborhoodManager.deleteNeighborhood nhId=" + memberId );
		}
        catch(Exception e)
        {
			System.out.println("rpv rpv rpv sql exception catched in xlAdminSerivce");
			System.out.println(e.toString() + "111111");
            throw new BoardwalkException(10020);
        }
		//return retVal;
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
				System.out.println("Removing last character from SB");
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
				System.out.println("Removing last character from SB");
				sb.deleteCharAt(sb.length()-1);
			}
			return sb.toString();
		}
		catch (SQLException sqe)
		{
			return "Error in GetCollaborationTreeForNhManagedByUser";
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
				System.out.println("Removing last character from SB");
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
			if (sb.length() > 0)
			{
				System.out.println("Removing last character from SB");
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



//Added by RahulV on 21-February-2015

	public static String getColumnAccessForUpdate(Connection connection, int tableId)
 throws SystemException
	{

		StringBuffer cafu;
		cafu = new StringBuffer();				

		PreparedStatement ps = null;
		ResultSet rs = null;

		try
		{
			// SP RETURNS  COLUMN_NAME | COLUMN_ID | RELATION | ACCESS
			ps = connection.prepareStatement(CALL_BW_GET_COL_ACCESS_FOR_UPDATE );
			ps.setInt(1, tableId);
			System.out.println("\n after setInt, before executeQuery..");
			rs = ps.executeQuery();
			System.out.println("\n before rs.next");
			while (rs.next())
			{
				System.out.println("\n " + rs.getString(1) + "|" + rs.getInt(2) + "|" + rs.getString(3) + "|"+ rs.getInt(4) );
				cafu.append(rs.getString(1) + "|" + rs.getInt(2) + "|" + rs.getString(3) + "|"+ rs.getInt(4) + "\n" );
			}
			System.out.println("\n after rs.next");
			System.out.println("cafu : " + cafu.toString());
		}
		catch (SQLException sql1)
		{
			System.out.println(sql1.toString());
			return "Error in getColumnAccessForUpdate";
		}
		finally
		{
			try
			{
				if ( rs != null )
					rs.close();
				if ( ps != null )
					ps.close();

			}
			catch (SQLException sql2)
			{
				System.out.println("throwing  sql2");
				throw new SystemException(sql2);
			}
		}
		return cafu.toString();
	}




	public static void SetColumnAccessOnTable(BoardwalkConnection bwcon, Connection connection, String buffer)
	throws SystemException
		{
		CallableStatement callableStatement = null;

		String[] paramArray ;
		int columnId;
		String rel;
		int access;
		int tid;

		try
		{

			TransactionManager tm = null;

			tm = new TransactionManager(bwcon.getConnection(), bwcon.getUserId());
			
			tid = tm.startTransaction();

			callableStatement = connection.prepareCall(CALL_BW_UPD_COL_ACCESS);

			String[] buffArray = buffer.split(ContentDelimeter);
			System.out.println("buffArray length : " + buffArray.length );
			if (buffArray.length > 0)
			{
				for (int colAcc = 0; colAcc < buffArray.length; colAcc++)
				{
					String colAccStr = buffArray[colAcc];
					System.out.println("colAcc : " + colAcc );
					System.out.println("colAccStr : " + colAccStr );
	
					if (!colAccStr.equalsIgnoreCase(""))
					{
						System.out.println("buffArray" + colAcc + " : " + colAccStr);
						paramArray = colAccStr.split("\\|");
						System.out.println("		paramArray " + paramArray[0] + " : " + paramArray[1] + " : " + paramArray[2]);
						rel = paramArray[0];
						columnId = Integer.parseInt(paramArray[1]);
						access = Integer.parseInt(paramArray[2]);

						System.out.println("Setting Access control for column id = " + columnId + " for relationship = " + rel + "to access = " + access);
						callableStatement.setInt(1, columnId);
						callableStatement.setString(2, rel);
						callableStatement.setInt(3, access);
						callableStatement.setInt(4, tid);
						callableStatement.addBatch();
					}
					System.out.println("before calling callableStatement.executeBatch()......."); 
					callableStatement.executeBatch();

					System.out.println("after calling callableStatement.executeBatch()......."); 

				}
			}

			tm.commitTransaction();

		}
		catch (SQLException sql1)
		{
			throw new SystemException(sql1);
		}
		finally
		{
			try
			{
				callableStatement.close();
			}
			catch (SQLException sql2)
			{
				throw new SystemException(sql2);
			}
		}

	}


	public static String GetCuboidColumnAccessForAllCuboids(Connection connection) throws SystemException
	{

		StringBuffer cafu;
		cafu = new StringBuffer();				

		String retstr = "";
		StringBuffer  col1, col2, col3, col4, col5, col6;
		col1 = new StringBuffer();				
		col2 = new StringBuffer();				
		col3 = new StringBuffer();				
		col4 = new StringBuffer();				
		col5 = new StringBuffer();				
		col6 = new StringBuffer();				

		PreparedStatement ps = null;
		ResultSet rs = null;

		try
		{
			// SP RETURNS  COLUMN_NAME | COLUMN_ID | RELATION | ACCESS
			ps = connection.prepareStatement(CALL_BW_GET_COL_ACCESS_FOR_UPDATE_ALL_CUBOIDS );
			//ps.setInt(1, tableId);
			System.out.println("\n after setInt, before executeQuery..");
			rs = ps.executeQuery();
			System.out.println("\n before rs.next");
			while (rs.next())
			{
			//	System.out.println("\n " + rs.getString(1) + "|" + rs.getInt(2) + "|" + rs.getString(3) + "|"+ rs.getInt(4) + "|" + rs.getString(5) + "|"+ rs.getInt(6) );
			//	cafu.append(rs.getString(1) + "|" + rs.getInt(2) + "|" + rs.getString(3) + "|"+ rs.getInt(4) + "|" + rs.getString(5) + "|"+ rs.getInt(6) + "\n" );
				col1.append(rs.getString(1) + "|");
				col2.append(rs.getString(2) + "|");
				col3.append(rs.getString(3) + "|");
				col4.append(rs.getString(4) + "|");
				col5.append(rs.getString(5) + "|");
				col6.append(rs.getString(6) + "|");
			}
			col1.deleteCharAt(col1.length()-1);
			col2.deleteCharAt(col2.length()-1);
			col3.deleteCharAt(col3.length()-1);
			col4.deleteCharAt(col4.length()-1);
			col5.deleteCharAt(col5.length()-1);
			col6.deleteCharAt(col6.length()-1);

			retstr = col1.toString() + "\n" + col2.toString() + "\n" + col3.toString() + "\n" + col4.toString() + "\n" + col5.toString() + "\n" + col6.toString();

			System.out.println("\n after rs.next");
			//System.out.println("cafu : " + cafu.toString());
			System.out.println("retstr : " + retstr);
		}
		catch (SQLException sql1)
		{
			System.out.println(sql1.toString());
			return "Error in getColumnAccessForUpdate";
		}
		finally
		{
			try
			{
				if ( rs != null )
					rs.close();
				if ( ps != null )
					ps.close();

			}
			catch (SQLException sql2)
			{
				System.out.println("throwing  sql2");
				throw new SystemException(sql2);
			}
		}
		//return cafu.toString();
		return retstr;
	}


	public static String GetAllColumnAccessForCuboid(Connection connection, int tableId) throws SystemException
	{

		StringBuffer cafu;
		cafu = new StringBuffer();				

		System.out.println("\n GetAllColumnAccessForCuboid.." + tableId);

		String retstr = "";
		StringBuffer  col1, col2, col3, col4, col5, col6;
		col1 = new StringBuffer();				
		col2 = new StringBuffer();				
		col3 = new StringBuffer();				
		col4 = new StringBuffer();				
		col5 = new StringBuffer();				
		col6 = new StringBuffer();				

		PreparedStatement ps = null;
		ResultSet rs = null;

		try
		{
			// SP RETURNS  COLUMN_NAME | COLUMN_ID | RELATION | ACCESS
			ps = connection.prepareStatement(CALL_BW_GET_ALL_COL_ACCESS_FOR_CUBOID );
			ps.setInt(1, tableId);
			System.out.println("\n after setInt, before executeQuery..");
			rs = ps.executeQuery();
			System.out.println("\n before rs.next");
			while (rs.next())
			{
			//	System.out.println("\n " + rs.getString(1) + "|" + rs.getInt(2) + "|" + rs.getString(3) + "|"+ rs.getInt(4) + "|" + rs.getString(5) + "|"+ rs.getInt(6) );
			//	cafu.append(rs.getString(1) + "|" + rs.getInt(2) + "|" + rs.getString(3) + "|"+ rs.getInt(4) + "|" + rs.getString(5) + "|"+ rs.getInt(6) + "\n" );
				col1.append(rs.getString(1) + "|");
				col2.append(rs.getString(2) + "|");
				col3.append(rs.getString(3) + "|");
				col4.append(rs.getString(4) + "|");
				col5.append(rs.getString(5) + "|");
				col6.append(rs.getString(6) + "|");
			}
			col1.deleteCharAt(col1.length()-1);
			col2.deleteCharAt(col2.length()-1);
			col3.deleteCharAt(col3.length()-1);
			col4.deleteCharAt(col4.length()-1);
			col5.deleteCharAt(col5.length()-1);
			col6.deleteCharAt(col6.length()-1);

			retstr = col1.toString() + "\n" + col2.toString() + "\n" + col3.toString() + "\n" + col4.toString() + "\n" + col5.toString() + "\n" + col6.toString();

			System.out.println("\n after rs.next");
			//System.out.println("cafu : " + cafu.toString());
			System.out.println("retstr : " + retstr);
		}
		catch (SQLException sql1)
		{
			System.out.println(sql1.toString());
			return "Error in getColumnAccessForUpdate";
		}
		finally
		{
			try
			{
				if ( rs != null )
					rs.close();
				if ( ps != null )
					ps.close();

			}
			catch (SQLException sql2)
			{
				System.out.println("throwing  sql2");
				throw new SystemException(sql2);
			}
		}
		//return cafu.toString();
		return retstr;
	}


	public static String processRelation( NhName	 nm, Vector nhRel, Vector nhTree)
	{
		String strReturn = "";

		System.out.println("Inside processRelation.........");

		Hashtable nhNames = new Hashtable();

		Hashtable relationshipToNeighborhoods = new Hashtable();

		if ( nhRel != null )
		{
			System.out.println("nhRel not null.........");
			Iterator neighIter = nhRel.iterator();
			
			while (neighIter.hasNext()) 
			{
				NeighborhoodRelation nhr = (NeighborhoodRelation)neighIter.next();
				int trg_nhid = nhr.getTargetNhId();
				String reln = nhr.getRelation();

				System.out.println("trg_nhid = "  +  trg_nhid + "  reln = " + reln);

				Vector neighborhoods =(Vector) relationshipToNeighborhoods.get(reln);
				if ( neighborhoods == null )
				{
					neighborhoods = new Vector();
					relationshipToNeighborhoods.put(reln,neighborhoods);
				}
				neighborhoods.add(new Integer(trg_nhid));
			}
		}
//
		int no = 0;
		if ((nhTree!= null) && (!nhTree.isEmpty())) 
		{
			// iterate over the neighborhood tree
			Iterator nhIter = nhTree.iterator();
			while (nhIter.hasNext()) 
			{
				NHTree nht = (NHTree)nhIter.next();
				int nhid = nht.getNeighborhood().getId();
				String nhName = nht.getName();
				//	System.out.println("Level 0 : " + nhName);
				Vector nh1Tree = nht.getChildren();
				no = no + 1;
				nhNames.put( new Integer(nhid), nhName);
				if (!nh1Tree.isEmpty()) 
				{
				}
				else
				{
				} // end if
				 // iterate over the neighborhood tree
				Iterator nh1Iter = nh1Tree.iterator();
				while (nh1Iter.hasNext()) 
				{
					NHTree nh1t = (NHTree)nh1Iter.next();
					int nh1id = nh1t.getNeighborhood().getId();
					String nh1Name = nh1t.getName();
					//	System.out.println("Level 1 : " + nh1Name);
						Vector nh2Tree = nh1t.getChildren();
					no = no + 1;
					nhNames.put( new Integer(nh1id), nhName+"/"+nh1Name);
				
					if (!nh2Tree.isEmpty()) 
					{
					}
					else
					{
					} // end if
					Iterator nh2Iter = nh2Tree.iterator();
					while (nh2Iter.hasNext()) 
					{
						NHTree nh2t = (NHTree)nh2Iter.next();
						int nh2id = nh2t.getNeighborhood().getId();
						String nh2Name = nh2t.getName();
						//		System.out.println("Level 2 : " + nh2Name);
						Vector nh3Tree = nh2t.getChildren();
						no = no + 1;
						nhNames.put( new Integer(nh2id), nhName+"/"+nh1Name+"/"+nh2Name);
						
						if (!nh3Tree.isEmpty()) 
						{
						}
						else
						{
						} // end if
						Iterator nh3Iter = nh3Tree.iterator();
						while (nh3Iter.hasNext()) 
						{
							NHTree nh3t = (NHTree)nh3Iter.next();
							int nh3id = nh3t.getNeighborhood().getId();
							String nh3Name = nh3t.getName();
							no = no + 1;
							nhNames.put( new Integer(nh3id), nhName+"/"+nh1Name+"/"+nh2Name+"/"+nh3Name);

						} // end while nh3
					} // end while nh2
				} // END WHILE nh1
			// Print out the level 0 Neighborhoods
			} // end while NH 0
		}//end if


		StringBuffer sbRel = new StringBuffer();
		StringBuffer sbGrp = new StringBuffer();


		no = 0;   
		Vector fixedRelations = new Vector();
		fixedRelations.add("PRIVATE");
		fixedRelations.add("PARENT");
		fixedRelations.add("DOMAIN");
		fixedRelations.add("PEER");
		fixedRelations.add("CHILDREN");
		String fixedRelation = "";
		String displayRelationshipName ="";
		
		for ( int f = 0; f < fixedRelations.size(); f++ )
		{
			
			 fixedRelation = (String)fixedRelations.elementAt(f);
			 
			  displayRelationshipName = fixedRelation;
			
			if (fixedRelation.equals("PRIVATE") )
			{
				displayRelationshipName = "Your Team";
			}
			if (fixedRelation.equals("PARENT") )
			{
			displayRelationshipName = "Managing Department";
			}
			if (fixedRelation.equals("DOMAIN") )
			{
				displayRelationshipName = "Company";
			}
			if (fixedRelation.equals("PEER") )
			{
				displayRelationshipName = "Peer Depts";
			}
			if (fixedRelation.equals("CHILDREN") )
			{
				displayRelationshipName = "Your Teams";
			}
						 
			Vector targetNhidsForFixedRelationship = (Vector)relationshipToNeighborhoods.get( fixedRelation );
			if ( targetNhidsForFixedRelationship != null && targetNhidsForFixedRelationship.size() > 0 )
			{
				no = no + 1;

				sbRel.append(displayRelationshipName + Seperator);

				System.out.println("no=" + no + ": " + displayRelationshipName);
				for ( int fn = 0; fn <  targetNhidsForFixedRelationship.size() ; fn++ )
				{
					int fixedRel_targetNhId = ((Integer) targetNhidsForFixedRelationship.elementAt(fn)).intValue();
					System.out.println("nhid=" + no + " value=" + fixedRel_targetNhId + " " + (String)nhNames.get(new Integer(fixedRel_targetNhId))); 
					sbGrp.append( fixedRel_targetNhId + "|" + (String)nhNames.get(new Integer(fixedRel_targetNhId)) + "~");
				}							
				sbGrp.append(Seperator);
			} 
			relationshipToNeighborhoods.remove(fixedRelation);
		}
//////
		Enumeration  nhRelations = relationshipToNeighborhoods.keys();
		    		
		while (nhRelations.hasMoreElements()) 
		{
			String reln = (String)nhRelations.nextElement();
			no = no + 1;
			System.out.println("reln = " + reln);
			System.out.println("no = " + no);
			
			sbRel.append(reln + Seperator);

			Vector targetNhids = (Vector)relationshipToNeighborhoods.get( reln );
			if  ( targetNhids.size() > 0 )
			{
				for ( int n = 0; n <  targetNhids.size() ; n++ )
				{
					int targetNhId = ((Integer) targetNhids.elementAt(n)).intValue();
					System.out.println("nhid="+ no  + "   value=" + targetNhId + "......" + (String)nhNames.get(new Integer(targetNhId)));
					sbGrp.append( targetNhId + "|" + (String)nhNames.get(new Integer(targetNhId)) + "~" );
				}
				sbGrp.append(Seperator);
			}
		}

//////		
		System.out.println("sbRel=" + sbRel.toString());
		System.out.println("sbGrp=" + sbGrp.toString());
		
		sbRel.append(ContentDelimeter + sbGrp.toString() + ContentDelimeter);
		
		strReturn = sbRel.toString();

		return strReturn;
	}



	public boolean AddNewRelation(Connection connection, int nhId, int userId, String relation, String selNhIds) throws  BoardwalkException
	{
		int tid;
		CallableStatement callablestatement = null;
		TransactionManager tm = null;

		try
		{
			tm = new TransactionManager(connection, userId);
			tid = tm.startTransaction();

			String CALL_BW_ADD_NEW_NH_REL = "{CALL BW_ADD_NEW_NH_REL(?,?,?,?)}";
			String CALL_BW_ADD_NEW_NH_REL_TO_OBJECTS  = "{CALL BW_ADD_NEW_NH_REL_TO_OBJECTS(?,?,?)}";

			String[] relatedNhs ;
			relatedNhs = selNhIds.split("\\|");


			for ( int r = 0; r < relatedNhs.length; r++ )
			{
				callablestatement = connection.prepareCall(CALL_BW_ADD_NEW_NH_REL);
				int targetNhId = Integer.parseInt(relatedNhs[r]);
				callablestatement.setInt(1, nhId);
				callablestatement.setString(2, relation);
				callablestatement.setInt(3, targetNhId);
				callablestatement.setInt(4, tid);
				callablestatement.executeUpdate();
				callablestatement.close();
				callablestatement = null;
			}

			callablestatement = connection.prepareCall(CALL_BW_ADD_NEW_NH_REL_TO_OBJECTS);
			callablestatement.setInt(1, nhId);
			callablestatement.setString(2, relation);
			callablestatement.setInt(3, tid);
			callablestatement.executeUpdate();

			//callablestatement.close();
			//callablestatement = null;

			tm.commitTransaction();

			return true;
		}
		catch (SQLException sql1)
		{
			try
			{
				tm.rollbackTransaction();
			}
			catch (SQLException sqe)
			{
				sqe.printStackTrace();
				throw new BoardwalkException( 10021 );
			}

			sql1.printStackTrace();
			throw new BoardwalkException(10021);
		}
		finally
		{
			try
			{
				callablestatement.close();
			}
			catch (SQLException sql2)
			{
				sql2.printStackTrace();
				throw new BoardwalkException(10021);
			}
		}
	}



	public boolean UpdateRelation(Connection connection, int nhId, int userId, String relation, String selNhIds) throws  BoardwalkException
	{
		int tid;
		CallableStatement callablestatement = null;
		TransactionManager tm = null;

		try
		{
			tm = new TransactionManager(connection, userId);
			tid = tm.startTransaction();

			String CALL_BW_ADD_NEW_NH_REL = "{CALL BW_ADD_NEW_NH_REL(?,?,?,?)}";
			String CALL_BW_DEL_FOR_UPD_NH_REL  = "{CALL BW_DEL_FOR_UPD_NH_REL(?,?)}";

			System.out.println("Calling CALL_BW_DEL_FOR_UPD_NH_REL...") ;
			callablestatement = connection.prepareCall(CALL_BW_DEL_FOR_UPD_NH_REL);
			callablestatement.setInt(1, nhId);
			callablestatement.setString(2, relation);
			callablestatement.executeUpdate();
//			callablestatement.close();
//			callablestatement = null;


			String[] relatedNhs ;
			relatedNhs = selNhIds.split("\\|");

			for ( int r = 0; r < relatedNhs.length; r++ )
			{
				System.out.println("Calling CALL_BW_ADD_NEW_NH_REL " + relatedNhs[r]);
				callablestatement = connection.prepareCall(CALL_BW_ADD_NEW_NH_REL);
				int targetNhId = Integer.parseInt(relatedNhs[r]);
				callablestatement.setInt(1, nhId);
				callablestatement.setString(2, relation);
				callablestatement.setInt(3, targetNhId);
				callablestatement.setInt(4, tid);
				callablestatement.executeUpdate();
//				callablestatement.close();
//				callablestatement = null;
			}

//			callablestatement.close();
//			callablestatement = null;

			tm.commitTransaction();

			return true;
		}
		catch (SQLException sql1)
		{
			sql1.printStackTrace();
			throw new BoardwalkException(10022);
		}
		finally
		{
			try
			{
				callablestatement.close();
			}
			catch (SQLException sql2)
			{
				sql2.printStackTrace();
				throw new BoardwalkException(10022);
			}
		}
	}


	public boolean UpdateBRectDefinition(BoardwalkConnection bwcon) throws BoardwalkException
	{
		//CallableStatement callableStatement = null;
		PreparedStatement	prepstatement	= null;
		int tid;

		try
		{

			TransactionManager tm = null;
			tm = new TransactionManager(bwcon.getConnection(), bwcon.getUserId());
			tid = tm.startTransaction();

			//callableStatement = connection.prepareCall(CALL_BW_BRECTDEFINITION_LOAD);
			prepstatement = connection.prepareStatement(CALL_BW_BRECTDEFINITION_LOAD);

			//callableStatement.execute();
			prepstatement.execute();

			System.out.println("after calling callableStatement.executeUpdate()......."); 
			tm.commitTransaction();

			return true;
		}
		catch (SQLException sql1)
		{
			sql1.printStackTrace();
			throw new BoardwalkException(10023);
		}
		finally
		{
			try
			{
				prepstatement.close();
				//callableStatement.close();
			}
			catch (SQLException sql2)
			{
				sql2.printStackTrace();
				throw new BoardwalkException(10023);
			}
		}
	}

	
	public String GetDefinedRolesUsers(BoardwalkConnection bwcon) throws SystemException
	{
		PreparedStatement ps = null;
		ResultSet rs = null;
		String retstr = "";

		StringBuffer  strRoleUsers = new StringBuffer();
		StringBuffer userLogin = new StringBuffer();
		StringBuffer isActive = new StringBuffer();


		try
		{
			ps = connection.prepareStatement(CALL_BW_SELECT_CUBOID);
			ps.setString(1, "ROLE_USER_MAPPING");
			System.out.println("\n after setInt, before executeQuery..");
			rs = ps.executeQuery();
			System.out.println("\n before rs.next");
			while (rs.next())
			{
				strRoleUsers.append(rs.getString(1) + "|");
				userLogin.append(rs.getString(2) + "|");
				isActive.append(rs.getString(3) + "|");
			}
			strRoleUsers.deleteCharAt(strRoleUsers.length()-1);
			userLogin.deleteCharAt(userLogin.length()-1);
			isActive.deleteCharAt(isActive.length()-1);

			retstr = strRoleUsers.toString() + "\n" + userLogin.toString() + "\n" +  isActive.toString()    ;

			System.out.println("\n after rs.next");
			//System.out.println("cafu : " + cafu.toString());
			System.out.println("retstr : " + retstr);

		}
		catch (SQLException sql1)
		{
			System.out.println(sql1.toString());
			return "Error in getColumnAccessForUpdate";
		}
		finally
		{
			try
			{
				if ( rs != null )
					rs.close();
				if ( ps != null )
					ps.close();

			}
			catch (SQLException sql2)
			{
				System.out.println("throwing  sql2");
				throw new SystemException(sql2);
			}
		}
		//return cafu.toString();
		return retstr;
	}

	public void UpdateUserRoleChanges(BoardwalkConnection bwcon, String userUpdates) throws SystemException
	{
		PreparedStatement pstmt = null;
		PreparedStatement pstmt1 = null;
		CallableStatement cs  = null;
		try
		{
			System.out.println("Calling TRUNCATE TABLE BW_USER_ROLE_MAPPING_TBL....");
			String TruncateSQL = "TRUNCATE TABLE BW_USER_ROLE_MAPPING_TBL";
			pstmt = connection.prepareStatement(TruncateSQL);
			pstmt.execute();
			//pstmt.close();

			String[] buffArray = userUpdates.split(ContentDelimeter);
			System.out.println("buffArray length : " + buffArray.length );
			if (buffArray.length > 0)
			{
				System.out.println("before insert... inside bufArray.length construct ...");
				String query = "INSERT INTO BW_USER_ROLE_MAPPING_TBL(USER_ROLE,USER_LOGIN,IS_ACTIVE) VALUES(?,?,?)";
				pstmt1 = connection.prepareStatement(query);

				for (int colAcc = 0; colAcc < buffArray.length; colAcc++)
				{
					String colAccStr = buffArray[colAcc];
					String[] updateArr = colAccStr.split("\\|");
					String userRole = updateArr[0] ;
					String userLogin = updateArr[1] ;
					String action = updateArr[2] ;
					String isActive = "";
					System.out.println("userlogin : " + userLogin );
					System.out.println("userrole : " + userRole );
					System.out.println("action : " + action );
					
					if (action.toUpperCase().equals("ACTIVATE"))
						isActive = "ACTIVE";
					else if (action.toUpperCase().equals("DEACTIVATE"))
						isActive = "INACTIVE";					

					System.out.println("Inserting " + userLogin + " ... " + userRole +  " ... " + isActive);
					pstmt1.setString(1, userRole);
					pstmt1.setString(2, userLogin);
					pstmt1.setString(3, isActive);
					pstmt1.addBatch();
				}

				System.out.println("Calling INSERT INTO BW_USER_ROLE_MAPPING_TBL...");
				int[] rescnt = pstmt1.executeBatch();
				System.out.println("after insert...");
				//pstmt.close();
			}


			System.out.println("Calling CALL_BW_USER_ROLE_MAPPING_LOAD....");
			
			cs = connection.prepareCall(CALL_BW_USER_ROLE_MAPPING_LOAD);
			cs.execute();
			cs.close();
			
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{

            try
            {
				if ( pstmt != null )
					pstmt.close();
				if ( cs != null )
					cs.close();
				if ( pstmt1 != null )
					pstmt1.close();
            }
            catch(SQLException sqlexception1) {
				System.out.println("throwing  sqlexception1");
                throw new SystemException(sqlexception1);
            }
		}

	}


	public String GetRolesFromRoleMaster(BoardwalkConnection bwcon) throws SystemException
	{
		PreparedStatement ps = null;
		ResultSet rs = null;
		String retstr = "";

		StringBuffer  strRoles;
		strRoles = new StringBuffer();				

		try
		{
			ps = connection.prepareStatement(CALL_BW_SELECT_CUBOID);
			ps.setString(1, "ROLES");
			System.out.println("\n after setInt, before executeQuery..");
			rs = ps.executeQuery();
			System.out.println("\n before rs.next");
			while (rs.next())
			{

				strRoles.append(rs.getString(1) + "|");
			}
			strRoles.deleteCharAt(strRoles.length()-1);

			retstr = strRoles.toString() ;

			System.out.println("\n after rs.next");
			//System.out.println("cafu : " + cafu.toString());
			System.out.println("retstr : " + retstr);

		}
		catch (SQLException sql1)
		{
			System.out.println(sql1.toString());
			return "Error in getColumnAccessForUpdate";
		}
		finally
		{
			try
			{
				if ( rs != null )
					rs.close();
				if ( ps != null )
					ps.close();

			}
			catch (SQLException sql2)
			{
				System.out.println("throwing  sql2");
				throw new SystemException(sql2);
			}
		}
		//return cafu.toString();
		return retstr;
	}



	public String GetTemplateManifestsFromBRectDefinition(BoardwalkConnection bwcon) throws SystemException
	{
		PreparedStatement ps = null;
		ResultSet rs = null;
		String retstr = "";

		StringBuffer  strBRectName, strBRectType, strCuboidName, strWbName, strCollabName, strNhName, templateManifestBRect;
		strBRectName = new StringBuffer();				
		strBRectType = new StringBuffer();				
		strCuboidName = new StringBuffer();				
		strWbName = new StringBuffer();				
		strCollabName = new StringBuffer();				
		strNhName = new StringBuffer();				
		templateManifestBRect = new StringBuffer();		// template manifest brectnames
		try
		{
			ps = connection.prepareStatement(CALL_BW_SELECT_CUBOID);
			ps.setString(1, "BRECTDEFINITION");
			System.out.println("\n after setInt, before executeQuery..");
			rs = ps.executeQuery();
			System.out.println("\n before rs.next");
			while (rs.next())
			{
				strBRectName.append(rs.getString(1) + "|");
				strBRectType.append(rs.getString(3) + "|");
				strCuboidName.append(rs.getString(4) + "|");
				strWbName.append(rs.getString(5) + "|");
				strCollabName.append(rs.getString(6) + "|");
				strNhName.append(rs.getString(7) + "|");

				if (rs.getString(3).toUpperCase().equals("TEMPLATE_MANIFEST"))
				{
					templateManifestBRect.append(rs.getString(1) + "|");
				}
			}
			strBRectName.deleteCharAt(strBRectName.length()-1);
			strBRectType.deleteCharAt(strBRectType.length()-1);
			strCuboidName.deleteCharAt(strCuboidName.length()-1);
			strWbName.deleteCharAt(strWbName.length()-1);
			strCollabName.deleteCharAt(strCollabName.length()-1);
			strNhName.deleteCharAt(strNhName.length()-1);
			templateManifestBRect.deleteCharAt(templateManifestBRect.length()-1);

			retstr = strBRectName.toString() + Seperator + strBRectType.toString() + Seperator + strCuboidName.toString() + Seperator + strWbName.toString() + Seperator +  strCollabName.toString() + Seperator + strNhName.toString() + ContentDelimeter + templateManifestBRect.toString() ;

			System.out.println("\n after rs.next");
			System.out.println("retstr : " + retstr);


		}
		catch (SQLException sql1)
		{
			System.out.println(sql1.toString());
			return "Error in getColumnAccessForUpdate";
		}
		finally
		{
			try
			{
				if ( rs != null )
					rs.close();
				if ( ps != null )
					ps.close();

			}
			catch (SQLException sql2)
			{
				System.out.println("throwing  sql2");
				throw new SystemException(sql2);
			}
		}

		return retstr;

	}
/*
	public void ResetPassword(Connection connection, String Email)
	{

		ServletConfig config = getServletConfig();

		String msSmtpServer;
		String msSmtpPort;
		String msUserName;
		String msPassword;

		msSmtpServer	= getServletConfig().getInitParameter("smptserver");
		msSmtpPort		= getServletConfig().getInitParameter("smtpport");
		msUserName		= getServletConfig().getInitParameter("username");
		msPassword		= getServletConfig().getInitParameter("password");

		PasswordGenerator pg = new PasswordGenerator();
		String newPassword = pg.randomstring(8);
		boolean success = UserManager.updatePassword( connection, Email, newPassword);
					
								
		String mailBody = "Hi";
		mailBody = mailBody + "\n";
		mailBody = mailBody + "Your login information for Boardwalk is as follows" + "\n";
		mailBody = mailBody + "\n";
		mailBody = mailBody + "Login Name:" + Email + "\n";
		mailBody = mailBody + "Password:" + newPassword + "\n";
		mailBody = mailBody + "\n";
		mailBody = mailBody + "\n";
		mailBody = mailBody + "Please don't reply to this system generated email" + "\n";

		if(success)
		{
			try
			{
				Properties props = new Properties();
				System.out.println("######### Using smtp host = " + msSmtpServer);
				SMTPAuthenticatorUserBulk auth = new SMTPAuthenticatorUserBulk();
				props.put("mail.smtp.host", msSmtpServer);
				props.put("mail.smtp.port", msSmtpPort);
				props.put("mail.smtp.auth", "true");
				//props.put("mail.smtp.auth", "true");
				Session session = Session.getDefaultInstance(props, auth);
				session.setDebug(true);
				//Mailer.send(username, "admin@boardwalktech.com", "Boardwalk Password", mailBody);
				try
				{
					// create a message
					MimeMessage msg = new MimeMessage(session);
					msg.setFrom(new InternetAddress(msUserName));
					InternetAddress[] address = {new InternetAddress(Email)};
					msg.setRecipients(Message.RecipientType.TO, address);

					msg.setSubject("New Password for the Boardwalk application");
					msg.setSentDate(new java.util.Date());

					MimeBodyPart mbp1 = new MimeBodyPart();
					mbp1.setText(mailBody);
					Multipart mp = new MimeMultipart();
					mp.addBodyPart(mbp1);
					msg.setContent(mp);

					// send the message
					Transport.send(msg);
				}
				catch (MessagingException mex)
				{
					success = false;
					mex.printStackTrace();
					Exception ex = null;
					if ((ex = mex.getNextException()) != null)
					{
						ex.printStackTrace();
					}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
				//bwUserRequest.result = "FAILURE";
				//bwUserRequest.comment = bwUserRequest.comment + "\n" + e.getMessage();
	
			}
		}
					
	}
*/

}
