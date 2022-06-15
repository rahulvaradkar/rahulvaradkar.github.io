package servlets;
/*
 *  xlPackagingServiceLogic.java
 */

//import com.boardwalk.exception.BoardwalkException;
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

public class xlPackagingServiceLogic extends xlServiceLogic
{
	public final static String Seperator = new Character((char)1).toString();
	public final static String ContentDelimeter = new Character((char)2).toString();
	public final static String DataBlockSeperator = new Character((char)3).toString();
	public final static String PipeDelimeter = new Character((char)124).toString();

    private static String CALL_BW_GET_ALL_MEMBERSHIPS_INFO = "{CALL BW_GET_ALL_MEMBERSHIPS_INFO}";
    private static String CALL_BW_GET_NHS_AT_LEVEL_0 = "{CALL BW_GET_NHS_AT_LEVEL_0(?)}";


	//Added by RahulV on 30-Jan-2022
	private static String CALL_BW_GET_MIGRATION_INFO_VELOCITY = "{CALL BW_GET_MIGRATION_INFO_VELOCITY()}";


	//Added by RahulV on 25-Feb-2022
	private static String CALL_BW_GET_ROW_COLUMN_IDS_OF_CUBOID = "{CALL BW_GET_ROW_COLUMN_IDS_OF_CUBOID(?)}";


	//Added by RahulV on 14-March-2022
	private static String CALL_BW_GET_MIGRATION_INFO_FOR_NH_VELOCITY = "{CALL BW_GET_MIGRATION_INFO_FOR_NH_VELOCITY(?)}";

	//Added by RahulV on 15-March-2022 TO RETAIN OR SET OWNERSHIPS WHILE OBJECT CREATION
	private static String CALL_BW_GET_MIGRATION_INFO_WITH_OWNERSHIP = "{CALL BW_GET_MIGRATION_INFO_WITH_OWNERSHIP()}";

	//Added by RahulV on 18-May-2022 TO GET LIST OF SQL OBJECT IE. STORED PROCEDURES AND SQL TABLES TO BE MIGRATED
	private static String CALL_BW_GET_BCP_OBJECT_LIST_FOR_MIGRATION = "{CALL BW_GET_BCP_OBJECT_LIST_FOR_MIGRATION()}";


	//Added by RahulV on 19-May-2022 TO GET THE DDL OF SQL TABLE OBJECT THAT IS TO BE MIGRATED
	private static String CALL_BW_GET_SQL_TABLE_SCRIPT = "{CALL BW_GET_SQL_TABLE_SCRIPT(?)}";
	private static String CALL_BW_GET_SQL_SP_SCRIPT = "{CALL BW_GET_SQL_SP_SCRIPT(?)}";

	//Added by RahulV on 20-May-2022 TO GET THE DDL OF SQL TABLE OBJECT THAT IS TO BE MIGRATED
	private static String CALL_BW_GET_EXT_QUERIES = "{CALL BW_GET_EXT_QUERIES()}";

	//Added by RahulV on 28-May-2022 TO GET THE DDL OF SQL TABLE OBJECT THAT IS TO BE MIGRATED
	private static String CALL_BW_GET_NH_ANCESTORS_FOR_MIGRATION = "{CALL BW_GET_NH_ANCESTORS_FOR_MIGRATION(?)}";

	private static String PIPE_CHAR = "|";

	HttpServletRequest req;
	HttpServletResponse res;

	Connection connection = null;
	BoardwalkConnection bwcon = null;

	static StringBuffer sb = null;
    int userId; //
    
    public xlPackagingServiceLogic(xlPackagingService srv) {
        super(srv);
    }
	
    public void service (HttpServletRequest request, HttpServletResponse response) 	throws ServletException, IOException
    {
		req = request;
		res = response;

	
		ServletConfig config = getServletConfig();
		ServletContext application = config.getServletContext();
		System.out.println("application.getRealPath(/) ............ " + application.getRealPath("/"));
		String appPath = application.getRealPath("/");

		String strUploadFolder	= config.getInitParameter("file-upload");
		strUploadFolder	= appPath + strUploadFolder;
		System.out.println("strUploadFolder ............ " + strUploadFolder);

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
		String tree;

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


					case 11:					//GetCollabForNH
						wrkstr = st.nextToken(Seperator);
						nhId = Integer.parseInt(wrkstr);
						String CollabforNh = "";
						CollabforNh = GetCollaborationTreeForNh(connection, bwcon, nhId);
						//responseBuffer = "SUCCESS" + Seperator +  "The Neighborhood of " + nhId + "is \n" + tree + ContentDelimeter + nhId  ;
						responseBuffer = "SUCCESS" + ContentDelimeter +  CollabforNh + ContentDelimeter + nhId  ;
						break;

					case 23:					//GetUserList
						String userList = "";
						userList = GetUserList(bwcon);
						//responseBuffer = "SUCCESS" + Seperator +  "The Neighborhood of " + nhId + "is \n" + tree + ContentDelimeter + nhId  ;
						responseBuffer = "SUCCESS" + Seperator +  userList   ;
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

							System.out.println("Inside xlPackagingServiceLogic");
							System.out.println("nhId=" + nhId +  "   .... userId=" + userId );

							if ( nhId != -1 )
							{
							   nm = NeighborhoodManager.getNeighborhoodNameById(connection, nhId);
							   nhRel = NeighborhoodManager.getNeighborhoodRelations(connection,nhId);
							   Vector nhTree = NeighborhoodManager.getNeighborhoodTree(connection, userId);

								str = processRelation( nm, nhRel, nhTree);
								//str = processRelation( nm, null, nhTree);

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


					case 63:				// Get Enterprisewise NH Hierarchy using User
						wrkstr = st.nextToken(Seperator);
						userId = Integer.parseInt(wrkstr);
						String EntTree = "";
						EntTree = GetEnterproseWideNeighborhoodTree(bwcon, connection, userId);
						//responseBuffer = "SUCCESS" + Seperator +  "The Neighborhood of " + nhId + "is \n" + tree + ContentDelimeter + nhId  ;
						responseBuffer = "SUCCESS" + Seperator +  EntTree + ContentDelimeter + userId  ;
						break;


					case 64:		 //GetNeighborhoodTreeWithAncestors
						wrkstr = st.nextToken(Seperator);
						nhId = Integer.parseInt(wrkstr);
						tree = "";
						tree = GetNeighborhoodTreeWithAncestors(connection, bwcon, nhId);
						//responseBuffer = "SUCCESS" + Seperator +  "The Neighborhood of " + nhId + "is \n" + tree + ContentDelimeter + nhId  ;
						responseBuffer = "SUCCESS" + Seperator +  tree + ContentDelimeter + nhId  ;
						break;


					case 71:				// Get Migration Commands list

						try
						{
							String strMigrationCommandInfo = "";
							//strMigrationCommandInfo =  GetMigrationCommandInfo(bwcon);

							//This returns Ownershiop information of Cuboid used while migration to reetain Ownship using User/Membership/Neighborhood information
							strMigrationCommandInfo =  GetMigrationCommandInfoWithOwnership(bwcon);
							System.out.println("strMigrationCommandInfo = " + strMigrationCommandInfo);
							responseBuffer = "SUCCESS" + ContentDelimeter +  strMigrationCommandInfo ;
						}
						catch (Exception e)
						{
							responseBuffer = "FAILURE" + ContentDelimeter +  e.getMessage() +  "\nFailed to retrieve Migration Commands Information. Try again" + ContentDelimeter   ;
						}
						break;


					case 72:				// Get RowIds and ColumnIds of Source Cuboid from Source Server for Migration Purpose
						wrkstr = st.nextToken(Seperator);
						tableId = Integer.parseInt(wrkstr);

						try
						{
							String strRowIdColumnIdInfo = "";
							strRowIdColumnIdInfo =  GetRowColumnIdsOfCuboidForSourceTargetMapping(connection, bwcon, tableId);
							System.out.println("strRowIdColumnIdInfo = " + strRowIdColumnIdInfo);
							responseBuffer = "SUCCESS" + ContentDelimeter +  strRowIdColumnIdInfo ;
						}
						catch (Exception e)
						{
							responseBuffer = "FAILURE" + ContentDelimeter +  e.getMessage() +  "\nFailed to retrieve RowId and ColumnId Information of Cuboid [" + tableId + "]. Try again" + ContentDelimeter   ;
						}
						break;

					case 73:				// Get All Migration Command Task needed to Migrate Selected Neighborhood 
						System.out.println("73.........sent");
						wrkstr = st.nextToken(Seperator);
						nhId = Integer.parseInt(wrkstr);
						System.out.println("nhID : " + nhId);

						try
						{
							String strNhIdMigrationCommand = "";
							strNhIdMigrationCommand =  GetMigrationTaskCommandsForNeighborhoodMigration(connection, bwcon, nhId);
							System.out.println("strNhIdMigrationCommand = " + strNhIdMigrationCommand);
							responseBuffer = "SUCCESS" + ContentDelimeter +  strNhIdMigrationCommand ;
						}
						catch (Exception e)
						{
							responseBuffer = "FAILURE" + ContentDelimeter +  e.getMessage() +  "\nFailed to retrieve Migration Tasks Commands for Migration of Neighborhood [" + nhId + "]. Try again" + ContentDelimeter   ;
						}
						break;


					case 74:				// Get All BP Objects i.e. Stored Procedure and SQL Table list for Velocity  
						System.out.println("74.........sent");

						try
						{
							String strStoredProcSQLTableDetails = "";
							strStoredProcSQLTableDetails =  GetListOfBcpSqlObjectsForMigration(connection);
							System.out.println("strStoredProcSQLTableDetails = " + strStoredProcSQLTableDetails);
							responseBuffer = "SUCCESS" + ContentDelimeter + strStoredProcSQLTableDetails  ;
						}
						catch (Exception e)
						{
							responseBuffer = "FAILURE" + ContentDelimeter +  e.getMessage() +  "\nFailed to retrieve SQL Object Details from Server." + ContentDelimeter   ;
						}
						break;


					case 75:				//  GET_BCP_OBJECT_DDL_FOR_MIGRATION Get CREATE DDL OF  i.e. Stored Procedure and SQL Table list for Velocity  
						System.out.println("75.........GET_BCP_OBJECT_DDL_FOR_MIGRATION ");

						int objectId ;
						String objectType ;
						wrkstr = st.nextToken(Seperator);
						objectId = Integer.parseInt(wrkstr);
						System.out.println("objectId : " + objectId);

						wrkstr = st.nextToken(Seperator);
						objectType = wrkstr;
						System.out.println("objectType : " + objectType);

						//CALL_BW_GET_SQL_TABLE_SCRIPT
						try
						{
							String sqlObjectDDL = "";
							sqlObjectDDL =  GetDDLScriptOfSQLObject(connection, objectId, objectType);
							System.out.println("sqlObjectDDL = " + sqlObjectDDL);
							responseBuffer = "SUCCESS" + ContentDelimeter +  sqlObjectDDL ;
						}
						catch (Exception e)
						{
							responseBuffer = "FAILURE" + ContentDelimeter +  e.getMessage() +  "\nFailed to retrieve DDL of SQL Object [" + objectId + "], [Object Type = " + objectType +  "] Try again" + ContentDelimeter   ;
						}
						break;


					case 76:				//  GET_BCP_EXTERNAL_QUERIES_FOR_MIGRATION  Get List of all External Queries for Velocity  
						System.out.println("76.........GET_BCP_EXTERNAL_QUERIES_FOR_MIGRATION");
						//CALL_BW_GET_EXT_QUERIES 							BW_GET_EXT_QUERIES 
						try
						{
							String externalQueries = "";
							externalQueries =  GetExternalQueriesOfSQLObject(connection);
							System.out.println("externalQueries = " + externalQueries);
							responseBuffer = "SUCCESS" + ContentDelimeter +  externalQueries;
						}
						catch (Exception e)
						{
							responseBuffer = "FAILURE" + ContentDelimeter +  e.getMessage() +  "\nFailed to retrieve External Queries from Database. Try again" + ContentDelimeter   ;
						}
						break;


					case 77:				//  GET_TEMPLATE_USING_BLOB_ID		 Get Template using Blob Id
						System.out.println("77.........GET_TEMPLATE_USING_BLOB_ID");

						int blobId;
						wrkstr = st.nextToken(Seperator);
						blobId = Integer.parseInt(wrkstr);

						try
						{
							String args[] = new String[4]; //FNAME,EXT,TYPE,CLIENT
							BufferedInputStream in = DownloadTemplateBlobForPackaging(connection, blobId, args);

							System.out.println("In DownloadTemplateBlobForPackaging :  fetched blob : filename = " + args[0]);

							res.setContentType(args[2]);
							res.addHeader("TEMPLATE-FILENAME", args[0].replaceAll("\\n|\\r",""));
							res.addHeader("TEMPLATE-EXTENSION", args[1]);

							res.setContentType(args[2]);
							javax.servlet.ServletOutputStream out = res.getOutputStream();
							int b;
							byte[] buffer = new byte[10240]; // 10kb buffer
							while ((b = in.read(buffer, 0, 10240)) != -1)
							{
								out.write(buffer, 0, b);
							}
							out.close();

							responseBuffer = "SUCCESS" + ContentDelimeter +  "tempatefile";
						}
						catch (Exception e)
						{
							responseBuffer = "FAILURE" + ContentDelimeter +  e.getMessage() +  "\nFailed to package Template Blob. Try again" + ContentDelimeter   ;
						}
						
						break;

					/**
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
**/

					case 16:					//GetNeighborhoodTree
						wrkstr = st.nextToken(Seperator);
						nhId = Integer.parseInt(wrkstr);
						tree = "";
						tree = GetNeighborhoodTree(bwcon, nhId);
						//responseBuffer = "SUCCESS" + Seperator +  "The Neighborhood of " + nhId + "is \n" + tree + ContentDelimeter + nhId  ;
						responseBuffer = "SUCCESS" + Seperator +  tree + ContentDelimeter + nhId  ;
						break;

/*

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
						*/
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
				System.out.println("responseBuffer : " + responseBuffer);
				System.out.println("XXXXXXXX.........Returning Response");
				commitResponseBuffer(responseBuffer, res);
			}
		}
		else
		{
			commitResponseBuffer("User Authentication/Connection Failed. \nYou have entered wrong User or Password. Try again.", res);
		}
    }


	//Added by Rahul Varadkar on 04-November-2015   Get Enterprise wide Neighborhood Tree : 63
	public static String GetEnterproseWideNeighborhoodTree(BoardwalkConnection bwcon, Connection connection, int a_userId) throws BoardwalkException 
	{
		StringBuffer sbEnt = null;
		sbEnt = new StringBuffer();				

        //Vector vNHTree = new Vector();

        // get all level 0 nh
        Vector nh0list = new Vector();
        try
        {
            nh0list = NeighborhoodManagerLevel_0.getNeighborhoodsAtLevel_0(connection, a_userId);
        }
        catch (Exception e)
        {
			e.printStackTrace();
        }


        // for each of these add it to the nh tree
        Iterator nh0Iter = nh0list.iterator();
        while (nh0Iter.hasNext()) {
            NeighborhoodLevel_0 nh0 = (NeighborhoodLevel_0)nh0Iter.next();
            int nhid = nh0.getNhId();
			String tree = "";
			tree = GetNeighborhoodTree(bwcon, nhid);
			sbEnt.append(tree);
			sbEnt.append("\n");
        }

		sbEnt.deleteCharAt(sbEnt.length()-1);
        return sbEnt.toString();
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


	//Delete Collaboration

	// NEW API RENAME COLLABORATION

	// CREATE WHITEBOARD
	
	// Delete Whiteboard


	// NEW API RENAME WHITEBOARD

	// CREATE TABLE

	// Delete Table

	// NEW API RENAME TABLE

	// CREATE NEIGHBORHOOD


	// DELETE NEIGHBORHOOD


	//NEW API RENAME NH


	//Get Neighborhood Tree with All Ancestors  NOT USED 
    public static String GetNeighborhoodTreeWithAncestors(
        Connection connection,
		BoardwalkConnection bwcon,
        int a_NhId
		) throws BoardwalkException 
	{
		try
		{
			sb = new StringBuffer();				

			int lngParentNhId = -1;
			//Get Ancestor Links
			lngParentNhId = GetAllAncestorsOfNeighborhood(connection, a_NhId);
		
			System.out.println("After calling GetAllAncestorsOfNeighborhood lngParentNhId : " + lngParentNhId);
			
			if (sb.length() > 0)
				sb.append("\n");

			Vector nh0v = BoardwalkNeighborhoodManager.getNeighborhoodTree( bwcon, a_NhId);
			Iterator nh0i = nh0v.iterator();
			while (nh0i.hasNext())
			{
				BoardwalkNeighborhoodNode bnn = (BoardwalkNeighborhoodNode)nh0i.next();
				//printNH(bnn, -1);
				printNH(bnn, lngParentNhId);
				System.out.println("sb:\n" + sb.toString());
			}

		}
//		catch (BoardwalkException bwe, SystemException se)
		catch (Exception e)
		{
			System.out.println("Error fetching  Neighborhood Tree With Ancestors: " + e.getMessage());
		}
		if (sb.length() > 0)
		{
			//System.out.println("Removing last character from SB");
			sb.deleteCharAt(sb.length()-1);
		}
		return sb.toString();

	}


	//Get Neighborhood Parents of Neighborhood 
    public static int GetAllAncestorsOfNeighborhood(
        Connection connection,
        int a_NhId
		) throws SystemException 
	{
		ResultSet rs = null;
		PreparedStatement stmt = null;
		int nhId, nhLevel, nhParent;
		String nhName ;
		int lngParentNhId = -1;
		try
		{
			stmt = connection.prepareStatement(CALL_BW_GET_NH_ANCESTORS_FOR_MIGRATION);
			stmt.setInt(1, a_NhId );

			rs = stmt.executeQuery();
			
			while (rs.next())
			{
				nhId = rs.getInt(1);
				nhName = rs.getString(2);
				nhParent = rs.getInt(3);
				nhLevel = rs.getInt(4);

				System.out.println("a_NhId : " + a_NhId + "...... nhId : " + nhId);
				System.out.println("nhParent : " + nhParent);
				if (nhId == a_NhId)
				{
					lngParentNhId = nhParent;
					System.out.println("lngParentNhId : " + lngParentNhId);
					break;
				}
				else
				{
					for (int i = 0; i<= nhLevel; i++)
					{
						System.out.print("\t");
						sb.append("\t");
					}
					System.out.println("nhName=" + nhName + " id=" + nhId);
					sb.append(nhName + "|" + nhId + "|"+ nhParent + "|"+ nhLevel + "\n" );
				}
			}
			if (sb.length() > 0)
			{
				//System.out.println("Removing last character from SB");
				sb.deleteCharAt(sb.length()-1);
			}
			//return sb.toString();
			return lngParentNhId;
		}
		catch (SQLException sqe)
		{
			System.out.println("Error in GetAllAncestorsOfNeighborhood : " + sqe.getMessage());
			throw new SystemException(sqe);
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

	// DELETE MEMBERSHIP

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

		//Activate User 05162016


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


	//Added by Rahul Varadkar on 25-February-2022
	public String GetRowColumnIdsOfCuboidForSourceTargetMapping(
		Connection connection,
		BoardwalkConnection bwcon, 
		int tableId
		) throws SystemException
	{

		StringBuffer sb = new StringBuffer();
		ResultSet rs = null;
		PreparedStatement stmt = null;

		int objectId, objectSequence;
		String objectName, objectType, orderCode  ;

        try
        {
			stmt = connection.prepareStatement(CALL_BW_GET_ROW_COLUMN_IDS_OF_CUBOID);
			stmt.setInt(1, tableId );

			rs = stmt.executeQuery();

			while (rs.next())
			{
				objectId = rs.getInt(1);			//rowid or column id
				objectName = rs.getString(2);		//blank for Row, Column Name for Column
				tableId = rs.getInt(3);				//table id
				objectSequence = rs.getInt(4);		//Row Sequence or Column Sequence
				objectType = rs.getString(5);		//BW_ROW or BW_COLUMN
				orderCode = rs.getString(6);		//ORDER CODE

				sb.append(objectId + Seperator + objectName + Seperator + tableId  + Seperator + objectSequence  + Seperator + objectType  + Seperator + orderCode + "\n");
			}

			if (sb.length() > 0)
			{
				System.out.println("Removing last character from SB");
				sb.deleteCharAt(sb.length()-1);
			}
			System.out.println("Data returned by GetRowColumnIdsOfCuboidForSourceTargetMapping");
			System.out.println(sb.toString());
			return sb.toString();
		}
		catch (SQLException sqe)
		{
			return "Error in GetRowColumnIdsOfCuboidForSourceTargetMapping";
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



	//Calling CALL_BW_GET_SQL_TABLE_SCRIPT OR CALL_BW_GET_SQL_SP_SCRIPT . Stored Proecedure returning the DDL  of Stored Procedures and SQL Tables 
	public String GetDDLScriptOfSQLObject(Connection connection, int objectId, String objectType) throws SystemException
	{

		StringBuffer sb = new StringBuffer();
		ResultSet rs = null;
		PreparedStatement stmt = null;

		try
		{
			if (objectType.equalsIgnoreCase("U"))		//SQL Table
			{
				stmt = connection.prepareStatement(CALL_BW_GET_SQL_TABLE_SCRIPT);
				stmt.setInt(1, objectId);
			}
			else if (objectType.equalsIgnoreCase("P"))		//Stored Procedure
			{
				stmt = connection.prepareStatement(CALL_BW_GET_SQL_SP_SCRIPT);
				stmt.setInt(1, objectId);
			}

			rs = stmt.executeQuery();

			String ddlSqlObject = null;
			String objectName = null;
			int objectIdReturned = -1;
			while (rs.next())
			{
				objectIdReturned = rs.getInt(1);		//Object Name
				objectName = rs.getString(2);		//Object Name
				if (objectType.equalsIgnoreCase("U"))		//SQL Table
					ddlSqlObject = rs.getString(3);		//DDL		No need to remove Comment when it is SQL TABLE
				else if (objectType.equalsIgnoreCase("P"))		//Stored Procedure
					ddlSqlObject = removeSPComments(rs.getString(3));		//DDL    Need to Remove Comment when it is SP
				sb.append(objectIdReturned + Seperator + objectName + Seperator + ddlSqlObject);
			}

			System.out.println("Data returned by GetDDLScriptOfSQLObject");
			System.out.println(sb.toString());
			return sb.toString();
		}
		catch (SQLException sqe)
		{
			System.out.println(sqe.getMessage());
			return "Error in GetDDLScriptOfSQLObject. " + sqe.getMessage();
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


	//Remove Comments written before Stored Procedure's CREATE PROCEDURE statement
	public String removeSPComments(String spDDL)
	{
		String separator ="CREATE PROCEDURE";
		int sepPos = spDDL.toUpperCase().indexOf(separator);
		System.out.println("Substring after CREATE PROCEDURE = "+ spDDL.substring(sepPos));
		return spDDL.substring(sepPos);
	}

	//Calling GET_BCP_EXTERNAL_QUERIES_FOR_MIGRATION . Stored Proecedure returning List of External Queries
	public String GetExternalQueriesOfSQLObject(Connection connection) throws SystemException
	{
		StringBuffer sb = new StringBuffer();
		ResultSet rs = null;
		PreparedStatement stmt = null;

		String queryDesc, queryCall, queryParam, params, command, insertSql;
		int queryId, sequence ;

        try
        {
			stmt = connection.prepareStatement(CALL_BW_GET_EXT_QUERIES);
			rs = stmt.executeQuery();

			while (rs.next())
			{
				queryId = rs.getInt(1);		// id
				queryDesc = rs.getString(2);	//query desc
				queryCall = rs.getString(3);	//query call
				queryParam = rs.getString(4);	//query param
				params = rs.getString(5);	//param
				insertSql = rs.getString(6);	//INSERT SQK
				command = rs.getString(7);	//command
				sequence = rs.getInt(8);	//sequnece

				sb.append(queryId + Seperator + queryDesc + Seperator + queryCall + Seperator + queryParam   + Seperator + params  + Seperator + insertSql + Seperator + command  + Seperator + sequence  + ContentDelimeter);
			}

			if (sb.length() > 0)
			{
				System.out.println("Removing last character from SB");
				sb.deleteCharAt(sb.length()-1);
			}
			System.out.println("Data returned by GetExternalQueriesOfSQLObject");
			System.out.println(sb.toString());
			return sb.toString();
		}
		catch (SQLException sqe)
		{
			return "Error in GetExternalQueriesOfSQLObject : "  + sqe.getMessage() ;
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


	//Calling CALL_BW_GET_BCP_OBJECT_LIST_FOR_MIGRATION . Stored Proecedure returning List of Stored Procedures and SQL Tables 
	public String GetListOfBcpSqlObjectsForMigration(Connection connection) throws SystemException
	{
		StringBuffer sb = new StringBuffer();
		ResultSet rs = null;
		PreparedStatement stmt = null;

		String objectName, objectType; 
		int objectId ;
		String created_date, modified_date;
		String command, params, product_or_custom;
		int sequence;
        try
        {
			stmt = connection.prepareStatement(CALL_BW_GET_BCP_OBJECT_LIST_FOR_MIGRATION);
			rs = stmt.executeQuery();

			while (rs.next())
			{
				objectId = rs.getInt(1);		//object_id
				objectName = rs.getString(2);	//object_name
				objectType = rs.getString(3);	//type
				created_date = rs.getString(4);	//create_date
				modified_date = rs.getString(5);	//modify_date
				params = rs.getString(6);	//params
				command = rs.getString(7);	//Command
				sequence = rs.getInt(8);	//Sequence
				product_or_custom = rs.getString(9);	//Product or Custom
				sb.append(objectId + Seperator + objectName + Seperator + objectType + Seperator + created_date + Seperator + modified_date + Seperator + params + Seperator + command  + Seperator + sequence + Seperator + product_or_custom + ContentDelimeter);
			}

			if (sb.length() > 0)
			{
				System.out.println("Removing last character from SB");
				sb.deleteCharAt(sb.length()-1);
			}
			System.out.println("Data returned by GetListOfBcpSqlObjectsForMigration");
			System.out.println(sb.toString());
			return sb.toString();
		}
		catch (SQLException sqe)
		{
			return "Error in GetListOfBcpSqlObjectsForMigration";
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



	//Calling CALL_BW_GET_MIGRATION_INFO_FOR_NH_VELOCITY . Stored Proecedure returning Migration Commands for Neighborhood Parents, Children, Collab, Wb, Cuboids, MEmbers and Users 
	public String GetMigrationTaskCommandsForNeighborhoodMigration(
		Connection connection,
		BoardwalkConnection bwcon, 
		int nhId ) throws SystemException
	{
		StringBuffer sb = new StringBuffer();
		ResultSet rs = null;
		PreparedStatement stmt = null;

		String migrationParam, param, command; 
		int objectId, objectSequence;

        try
        {
			stmt = connection.prepareStatement(CALL_BW_GET_MIGRATION_INFO_FOR_NH_VELOCITY);
			stmt.setInt(1, nhId );

			rs = stmt.executeQuery();

			while (rs.next())
			{
				migrationParam = rs.getString(1);	//MigrationParam
				param = rs.getString(2);			//Param
				command = rs.getString(3);			//Command
				objectId = rs.getInt(4);			//object id
				objectSequence = rs.getInt(5);		//Command Sequence

				sb.append(migrationParam + Seperator + param + Seperator + command + Seperator + objectId + Seperator + objectSequence + "\n");
			}

			if (sb.length() > 0)
			{
				System.out.println("Removing last character from SB");
				sb.deleteCharAt(sb.length()-1);
			}
			System.out.println("Data returned by GetMigrationTaskCommandsForNeighborhoodMigration");
			System.out.println(sb.toString());
			return sb.toString();
		}
		catch (SQLException sqe)
		{
			return "Error in GetMigrationTaskCommandsForNeighborhoodMigration";
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

	public BufferedInputStream DownloadTemplateBlobForPackaging(Connection connection, int blobId, String args[]) throws SystemException
	{
		try
		{
			//String args[] = new String[4]; //FNAME,EXT,TYPE,CLIENT
			BufferedInputStream in = BlobManager.getDocument(connection, blobId, args);
			return in;
		}
		catch (SQLException sql)
		{
			System.out.println("Error: " + sql.getMessage());
			throw new SystemException(sql);
		}
	}


	//Added by Rahul Varadkar on 15-March-2022. Returns Migration Commands with Ownership informtion like user/memership/neighborhood while creating cuboid
	public String GetMigrationCommandInfoWithOwnership(BoardwalkConnection bwcon) throws SystemException
	{
        ResultSet rs = null;
        CallableStatement cs  = null;
        try
        {
			cs = connection.prepareCall(CALL_BW_GET_MIGRATION_INFO_WITH_OWNERSHIP);

			System.out.println("before calling CALL_BW_GET_MIGRATION_INFO_WITH_OWNERSHIP i.e. "  + CALL_BW_GET_MIGRATION_INFO_WITH_OWNERSHIP);
			cs.execute();
            rs = cs.getResultSet();
			System.out.println("after calling CALL_BW_GET_MIGRATION_INFO_WITH_OWNERSHIP");

			int objectId, sequence;
			String migrationParam, param, command ;
			StringBuffer sb = new StringBuffer();

			System.out.println("before while rs loop");

            while ( rs.next() )
            {
				System.out.println("inside while rs loop");
                objectId = rs.getInt("ObjectId");
                sequence = rs.getInt("Sequence");
                migrationParam = rs.getString("MigrationParam");
                param = rs.getString("Param");
                command = rs.getString("Command");

				System.out.println("objectId: " + objectId);
				System.out.println("migrationParam: " + migrationParam);

				sb.append(migrationParam + Seperator + param + Seperator + command + Seperator + objectId + Seperator + sequence + "\n");
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
				displayRelationshipName = " Your Team";
			}
			if (fixedRelation.equals("PARENT") )
			{
			displayRelationshipName = " Managing Department";
			}
			if (fixedRelation.equals("DOMAIN") )
			{
				displayRelationshipName = " Company";
			}
			if (fixedRelation.equals("PEER") )
			{
				displayRelationshipName = " Peer Depts";
			}
			if (fixedRelation.equals("CHILDREN") )
			{
				displayRelationshipName = " Your Teams";
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
					sbGrp.append( (String)nhNames.get(new Integer(fixedRel_targetNhId)) + "|" + fixedRel_targetNhId + "~");
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
					sbGrp.append( (String)nhNames.get(new Integer(targetNhId)) + "|" + targetNhId + "~" );
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


}

