package servlets;
/*

 *  Added on 09-February-2021 by Rahul
 *  xlMigrationService.java
 */

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import com.boardwalk.database.DatabaseLoader;
import com.boardwalk.database.*;
import com.boardwalk.table.ColumnManager;
import com.boardwalk.table.TableManager;
import com.boardwalk.user.*;
//import com.boardwalk.member.Member;
import com.boardwalk.member.*;
import com.boardwalk.exception.*;
import boardwalk.neighborhood.*;
import boardwalk.collaboration.*;
import boardwalk.table.*;

import com.boardwalk.neighborhood.*;
import com.boardwalk.collaboration.*;
import com.boardwalk.whiteboard.*;

import boardwalk.common.*;			//added for BcpLogManager
import boardwalk.connection.BoardwalkConnection;
import boardwalk.connection.BoardwalkConnectionManager;

import java.sql.*;                  // JDBC package
import javax.sql.*;                 // extended JDBC packa


public class xlMigrationService extends xlService implements SingleThreadModel
{
	public final static String Seperator = new Character((char)1).toString();
	public final static String ContentDelimeter = new Character((char)2).toString();
	public final static String DataBlockSeperator = new Character((char)3).toString();
	public final static String PipeDelimeter = new Character((char)124).toString();


	private static String PIPE_CHAR = "|";

	private static String CALL_INSERT_DIAGRAM_LINE="{CALL InsertDiagramLine(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
	private static String CALL_INSERT_NH_LIST_LINE="{CALL InsertNhListLine(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";

	private static String CALL_CREATE_DIAGRAM="{CALL CREATE_DIAGRAM(?,?,?,?,?)}";
	private static String CALL_GET_DIAGRAMS="{CALL GET_DIAGRAMS(?)}";

	private static String CALL_GET_DIAGRAM="{CALL GET_DIAGRAM(?)}";
	private static String CALL_GET_DIAGRAM_RECTANGLES="{CALL GET_DIAGRAM_RECTANGLES(?)}";
	private static String CALL_GET_DIAGRAM_NH_LIST="{CALL GET_DIAGRAM_NH_LIST(?)}";


	private static String CALL_GET_MIGRATION_INFO="{CALL BW_GET_MIGRATION_INFO_EXT}";

	HttpServletRequest req;
	HttpServletResponse res;


	static StringBuffer sb = null;
	Connection connection = null;
	BoardwalkConnection bwcon = null;

	String migrationUserName;
	int migrationUserId;

	
    public void service (HttpServletRequest request, HttpServletResponse response) 	throws ServletException, IOException
    {

		req = request;
		res = response;

		String buf = getRequestBuffer(request).toString();
		//System.out.println("Data from client" + buf);

		StringTokenizer st;
		st = new StringTokenizer(buf);

		String action;

		String wrkstr;
		action = st.nextToken(Seperator);

		System.out.println("action="+action);


		String failureReason = "";
		String responseBuffer = "";

		if (  action.equals("GET_MIGRATION_INFO") )
		{
			StringBuffer sbResp = new StringBuffer();
			CallableStatement callableStatement = null;
			ResultSet rs = null;
			ResultSet newRs = null;

			System.out.println("selection action ..........GET_MIGRATION_INFO");
			try
			{
				DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
				connection = databaseloader.getConnection();

				String diagType = null;

				callableStatement = connection.prepareCall(CALL_GET_MIGRATION_INFO);

				rs = callableStatement.executeQuery();
				sbResp.append("success");
				while (rs.next())
				{
					sbResp.append( ContentDelimeter + rs.getString(1) + Seperator + rs.getString(2) + Seperator + rs.getString(3))  ;
				}

				sbResp.append( ContentDelimeter );


				callableStatement = null;

				sbResp.deleteCharAt(sbResp.length()-1);
				responseBuffer = sbResp.toString();

			}
			catch( SQLException sql ) 
			{
				sql.printStackTrace();
				 commitResponseBuffer("failure"
									+ xlService.ContentDelimeter
									+ "Failed to GET MIGRATION INFO", response );
			}
			finally
			{
				try
				{
					if ( callableStatement != null )
						callableStatement.close();
					if (rs != null)
						rs.close();
					if (newRs != null)
						newRs.close();
					if ( connection != null )
						connection.close();
				}
				catch( SQLException sql )
				{
					sql.printStackTrace();
				}
				System.out.println("responseBuffer : " + responseBuffer);
				commitResponseBuffer(responseBuffer, response);

			}
		}


		if (  action.equals("RUN_MIGRATION_COMMANDS") )
		{

			DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
			try 
			{
				connection = databaseloader.getConnection();
			} 
			catch (SQLException e2) 
			{
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			
			System.out.println("Connection " + connection);
	
			System.out.println("Connection is set !!!");
			
			String migrationCommands;
			migrationCommands = st.nextToken(DataBlockSeperator);

			migrationUserName = st.nextToken(Seperator);
			migrationUserName = migrationUserName.substring(1);

			String migrationUserPassword;
			migrationUserPassword = st.nextToken(Seperator);

			System.out.println("migrationUserName: " + migrationUserName);
			System.out.println("migrationUserPassword: " + migrationUserPassword);

//			bwcon = getBoardwalkConnection(userName, password);

			System.out.println("migrationUserName : " + migrationUserName);
			User suser = UserManager.getUser(connection, migrationUserName);
			migrationUserId = suser.getId();
			System.out.println("migrationUserId : " + migrationUserId);

			TransactionManager tm = null;
			int tid = -1 ;

			migrationCommands = migrationCommands.substring(1);

			System.out.println("migrationCommands: " + migrationCommands);

			String[] receivedCommands = migrationCommands.split(ContentDelimeter);
			String currCommand;
			boolean blnNhSecure = true;
			int parentNhId;


			StringBuffer sbResp = new StringBuffer();

			sbResp.append("success");

			for (int i = 0; i < receivedCommands.length; i++) 
			{
				String userName ;
				int userId;

				System.out.println("command " + i + ": " + receivedCommands[i]);
				currCommand = receivedCommands[i];
				String[] commandElements = currCommand.split(Seperator);
				String Seq = commandElements[0];
				String Params = commandElements[1];
				String command = commandElements[2];
				
				System.out.println("current Command: " + currCommand);
				System.out.println("Seq: " + Seq);
				System.out.println("Params: " + Params);
				System.out.println("command: " + command);

				String[] paramEle = Params.split("\\|");

		        NeighborhoodLevel nhl_0 = null;
		        NeighborhoodLevel_1 nhl_1 = null;
		        NeighborhoodLevel_2 nhl_2 = null;
		        NeighborhoodLevel_3 nhl_3 = null;


				if (!Seq.equals("9"))		//CreateCuboid command
				{
					try 
					{
						tm = new TransactionManager( connection, migrationUserId);
						tid = tm.startTransaction();
					} 
					catch (SQLException e1) 
					{
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}


				if (command.equals("CreateNH_0"))
				{
					String nh0Name;
					nh0Name = Params;
					System.out.println("Creating NH_0 : " + nh0Name + " started.");
					try 
					{
						nhl_0 = NeighborhoodManagerLevel_0.createNeighborhood(connection, nh0Name, tid, blnNhSecure);
						sbResp.append( ContentDelimeter + "Creation of NH_0 : " + nh0Name +  Seperator + "Successful")  ;
					} 
					catch (SystemException | NeighborhoodException e) 
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
						sbResp.append( ContentDelimeter + "Creation of NH_0 : " + nh0Name +  Seperator + "Failed")  ;
					}
					System.out.println("Creating NH_0 : " + nh0Name + " Done.");
				}
				else if (command.equals("CreateNH_1"))
				{
					String nh0Name, nh1Name;
					nh0Name = paramEle[0];
					nh1Name = paramEle[1];

					System.out.println("nh0Name : " + nh0Name );
					System.out.println("nh1Name : " + nh1Name );
					System.out.println("Creating NH_1 : " + nh1Name + " started.");
					//get nh_0 info
					NeighborhoodLevelId nhl_id_0;
					try 
					{
						nhl_id_0 = NeighborhoodManager.getNeighborhoodLevelId(connection, nh0Name.trim(), " ", " ", " ", 0);
						int nh_0_id = nhl_id_0.getId();
						System.out.println("nh_0_id : " + nh_0_id );
						System.out.println("nh_0_name : " + nhl_id_0.getName() );

						parentNhId = nh_0_id;
						System.out.println("Creating nh1 : nh1Name " + nh1Name);
						System.out.println("Creating nh1 : parentNhId " + parentNhId);
						
						nhl_1 = NeighborhoodManagerLevel_1.createNeighborhood(connection, nh1Name, parentNhId , tid, blnNhSecure);
						sbResp.append( ContentDelimeter + "Creation of NH_1 : " + nh1Name +  Seperator + "Successful")  ;
					} 
					catch (SystemException | NeighborhoodException e) 
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
						sbResp.append( ContentDelimeter + "Creation of NH_1 : " + nh1Name +  Seperator + "Failed")  ;
					}
					System.out.println("Creating NH_1 : " + nh1Name + " Done.");
				}
				else if (command.equals("CreateNH_2"))
				{
					String nh0Name, nh1Name, nh2Name;
					nh0Name = paramEle[0];
					nh1Name = paramEle[1];
					nh2Name = paramEle[2];

					System.out.println("Creating NH_2 : " + nh2Name + " started.");
					//get nh_1 info
					NeighborhoodLevelId nhl_id_1;
					try {
						nhl_id_1 = NeighborhoodManager.getNeighborhoodLevelId(connection, nh0Name.trim(), nh1Name.trim(), " ", " ", 1);
						System.out.println("Parent NH Name: " + nhl_id_1.getName());
						int nh_1_id = nhl_id_1.getId();
						parentNhId = nh_1_id;
	                    nhl_2 = NeighborhoodManagerLevel_2.createNeighborhood(connection, nh2Name, parentNhId, tid, blnNhSecure);
						sbResp.append( ContentDelimeter + "Creation of NH_2 : " + nh2Name +  Seperator + "Successful")  ;
					} 
					catch (SystemException | NeighborhoodException e) 
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
						sbResp.append( ContentDelimeter + "Creation of NH_2 : " + nh2Name +  Seperator + "Failed")  ;
					}
					System.out.println("Creating NH_2 : " + nh2Name + " Done.");
				}
				else if (command.equals("CreateNH_3"))
				{
					String nh0Name, nh1Name, nh2Name, nh3Name;
					nh0Name = paramEle[0];
					nh1Name = paramEle[1];
					nh2Name = paramEle[2];
					nh3Name = paramEle[3];

					System.out.println("Creating NH_3 : " + nh3Name + " started.");
					//get nh_2 info
					NeighborhoodLevelId nhl_id_2;
					try 
					{
						nhl_id_2 = NeighborhoodManager.getNeighborhoodLevelId(connection, nh0Name.trim(), nh1Name.trim(), nh2Name.trim(), " ", 2);
						System.out.println("Parent NH Name: " + nhl_id_2.getName());
						int nh_2_id = nhl_id_2.getId();
						parentNhId = nh_2_id ;
	                    nhl_3 = NeighborhoodManagerLevel_3.createNeighborhood(connection, nh3Name, parentNhId, tid, blnNhSecure);
						sbResp.append( ContentDelimeter + "Creation of NH_3 : " + nh3Name +  Seperator + "Successful")  ;
					} 
					catch (SystemException | NeighborhoodException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						sbResp.append( ContentDelimeter + "Creation of NH_3 : " + nh3Name +  Seperator + "Failed")  ;
					}
					System.out.println("Creating NH_3 : " + nh3Name + " Done.");

				}
				else if (command.equals("CreateCollab"))
				{
					String nh0Name, nh1Name, nh2Name, nh3Name,  collabName;
					nh0Name = paramEle[0];
					nh1Name = paramEle[1];
					nh2Name = paramEle[2];
					nh3Name = paramEle[3];
					userName = paramEle[4];
					collabName = paramEle[5];
					
					System.out.println("nh0Name : >" + nh0Name + "<");
					System.out.println("nh1Name : >" + nh1Name + "<");
					System.out.println("nh2Name : >" + nh2Name + "<");
					System.out.println("nh3Name : >" + nh3Name + "<");
					System.out.println("userName : " + userName);
					System.out.println("collabName : " + collabName);

					int nhLevel = -1;
					
					if (!nh0Name.isEmpty() && !nh1Name.isEmpty() && !nh2Name.isEmpty() && !nh3Name.isEmpty() )
						nhLevel = 3;
					else if (!nh0Name.isEmpty() && !nh1Name.isEmpty() && !nh2Name.isEmpty() && nh3Name.isEmpty() )
						nhLevel = 2;
					else if (!nh0Name.isEmpty() && !nh1Name.isEmpty() && nh2Name.isEmpty() && nh3Name.isEmpty() )
						nhLevel = 1;
					else if (!nh0Name.isEmpty() && nh1Name.isEmpty() && nh2Name.isEmpty() && nh3Name.isEmpty() )
						nhLevel = 0;
					
					NeighborhoodLevelId nhl;
					int nhId = -1;
					try {
						System.out.println("Before getting neighborhood levelId");
						System.out.println("nhLevel: " + nhLevel);
						nhl = NeighborhoodManager.getNeighborhoodLevelId(connection, nh0Name.trim(), nh1Name.trim(), nh2Name.trim(), nh3Name.trim(), nhLevel);
						nhId = nhl.getId();
						System.out.println("nhId based on neighborhood: " + nhId);
					} catch (SystemException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					System.out.println("before getting userid : " + userName);
					User user = UserManager.getUser(connection, userName);
					userId = user.getId();
					System.out.println("after getting userId : " + userId);

					Hashtable memberships = null;
					Enumeration memberIds = null ;
					try
					{
						memberships  = UserManager.getMembershipsForUser(connection, userId );
						memberIds = memberships.keys();
						System.out.println("memberships.size : " + memberships.size());
					}
					catch ( Exception e )
					{
					   e.printStackTrace();
					}

					int memberId = -1 ;
					int membernhId = -1;
					String nhName;
					if (memberships.size() == 0 )
					{
						System.out.println("Memberships not found");
					}
					else
					{
						if (  memberships.size() > 0 )
						{
							System.out.println("Checking membership...");
							boolean membershipFound = false;
							for (int ii=0; ii < memberships.size(); ii++)
							{
								memberId =((Integer) memberIds.nextElement()).intValue();
								membernhId =((Member) memberships.get(        new Integer(memberId)   )).getNeighborhoodId();
								nhName = ((Member) memberships.get(        new Integer(memberId)   )).getNeighborhoodName();
								System.out.println("nhId based on membershiop : " + membernhId +   " nhname: " + nhName);
								System.out.println("nhId :" + nhId);
								if (nhId == membernhId)
								{
									System.out.println("Membership found.");
									membershipFound = true;
									break;
								}
							}

							if (membershipFound == true)
							{
								try
								{
									System.out.println("Creating collab : " + collabName);
									int collabId;
									collabId = CollaborationManager.createCollaboration(connection, collabName, "Collab created by Migration", memberId, tid, 1);
									System.out.println("Collab created successfully : " + collabId);
									sbResp.append( ContentDelimeter + "Creation of Collaboration : " + collabName +  Seperator + "Successful")  ;
								}
								catch (Exception e)
								{
									e.printStackTrace();
									sbResp.append( ContentDelimeter + "Creation of Collaboration : " + collabName +  Seperator + "Failed")  ;
									System.out.println("Failed to create Collaboration : " + collabName);
								}
							}
							else
								System.out.println("Membership NOT found.");
						}
					}
				}
				else if (command.equals("CreateWb"))
				{
					String nh0Name, nh1Name, nh2Name, nh3Name,  collabName, wbName;
					nh0Name = paramEle[0];
					nh1Name = paramEle[1];
					nh2Name = paramEle[2];
					nh3Name = paramEle[3];
					collabName = paramEle[4];
					wbName = paramEle[5];
					
					System.out.println("nh0Name : >" + nh0Name + "<");
					System.out.println("nh1Name : >" + nh1Name + "<");
					System.out.println("nh2Name : >" + nh2Name + "<");
					System.out.println("nh3Name : >" + nh3Name + "<");
					System.out.println("collabName : " + collabName);
					System.out.println("wbName : " + wbName);

					int nhLevel = -1;
					
					if (!nh0Name.isEmpty() && !nh1Name.isEmpty() && !nh2Name.isEmpty() && !nh3Name.isEmpty() )
						nhLevel = 3;
					else if (!nh0Name.isEmpty() && !nh1Name.isEmpty() && !nh2Name.isEmpty() && nh3Name.isEmpty() )
						nhLevel = 2;
					else if (!nh0Name.isEmpty() && !nh1Name.isEmpty() && nh2Name.isEmpty() && nh3Name.isEmpty() )
						nhLevel = 1;
					else if (!nh0Name.isEmpty() && nh1Name.isEmpty() && nh2Name.isEmpty() && nh3Name.isEmpty() )
						nhLevel = 0;

					//Get neighborhood ID
					NeighborhoodLevelId nhl;
					int nhId = -1;
					try {
						System.out.println("Before getting neighborhood levelId");
						System.out.println("nhLevel: " + nhLevel);
						nhl = NeighborhoodManager.getNeighborhoodLevelId(connection, nh0Name.trim(), nh1Name.trim(), nh2Name.trim(), nh3Name.trim(), nhLevel);
						nhId = nhl.getId();
						System.out.println("nhId based on neighborhood: " + nhId);

						//Get Collaboration ID
						int collabId = -1;
						Vector cl = CollaborationManager.getCollaborationsOfNeighborhood(connection, nhId);
						collabId = -1;
						Iterator cli = cl.iterator();
						while (cli.hasNext())				// check if collaboration already exists
						{
							CollaborationTreeNode ctn = (CollaborationTreeNode)cli.next();
							//collabList.addElement(new Integer(ctn.getId()));

							if (collabName.trim().equals(ctn.getName()))
							{
								collabId = ctn.getId();		// collaboration exists
								break;
							}
						}

						if (collabId == -1)
							System.out.println("Collaboration not found ");
						else
						{
							int wbId = -1;
							wbId = WhiteboardManager.createWhiteboard(connection, wbName,0, 2, 0, collabId, tid, 1);
			
							if (wbId != -1)
							{
								System.out.println("Whiteboard Created : " + wbId);
								sbResp.append( ContentDelimeter + "Creation of Whiteboard : " + wbName +  Seperator + "Successful")  ;
							}
						}
					} 
					catch (Exception e) 
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
						System.out.println("Failed to create Whiteboard");
						sbResp.append( ContentDelimeter + "Creation of Whiteboard : " + wbName +  Seperator + "Failed")  ;
					}
				}

				else if (command.equals("CreateCuboid"))
				{
					String nh0Name, nh1Name, nh2Name, nh3Name,  collabName, wbName, cuboidName;
					nh0Name = paramEle[0];
					nh1Name = paramEle[1];
					nh2Name = paramEle[2];
					nh3Name = paramEle[3];
					collabName = paramEle[4];
					wbName = paramEle[5];
					cuboidName = paramEle[6];
					
					int nh0MemberId ;

					//Creating cuboid using BOARDWALK_APPLICATION 's Neighborhood-0 MEMBERSHIP
					nh0MemberId = getNeighborhood0MembershipId(migrationUserName, nh0Name);

					bwcon = getBoardwalkConnection(migrationUserName, migrationUserPassword, nh0MemberId);

					System.out.println("Membership of Neighborhood Level 0 of User " + migrationUserName + " is : " + nh0MemberId); 

					System.out.println("nh0Name : >" + nh0Name + "<");
					System.out.println("nh1Name : >" + nh1Name + "<");
					System.out.println("nh2Name : >" + nh2Name + "<");
					System.out.println("nh3Name : >" + nh3Name + "<");
					System.out.println("collabName : " + collabName);
					System.out.println("wbName : " + wbName);

					int nhLevel = -1;
					
					if (!nh0Name.isEmpty() && !nh1Name.isEmpty() && !nh2Name.isEmpty() && !nh3Name.isEmpty() )
						nhLevel = 3;
					else if (!nh0Name.isEmpty() && !nh1Name.isEmpty() && !nh2Name.isEmpty() && nh3Name.isEmpty() )
						nhLevel = 2;
					else if (!nh0Name.isEmpty() && !nh1Name.isEmpty() && nh2Name.isEmpty() && nh3Name.isEmpty() )
						nhLevel = 1;
					else if (!nh0Name.isEmpty() && nh1Name.isEmpty() && nh2Name.isEmpty() && nh3Name.isEmpty() )
						nhLevel = 0;

					//Get neighborhood ID
					NeighborhoodLevelId nhl;
					int nhId = -1;
					try 
					{
						System.out.println("Before getting neighborhood levelId");
						System.out.println("nhLevel: " + nhLevel);
						nhl = NeighborhoodManager.getNeighborhoodLevelId(connection, nh0Name.trim(), nh1Name.trim(), nh2Name.trim(), nh3Name.trim(), nhLevel);
						nhId = nhl.getId();
						System.out.println("nhId based on neighborhood: " + nhId);

						//Get Collaboration ID
						int collabId = -1;
						Vector cl = CollaborationManager.getCollaborationsOfNeighborhood(connection, nhId);
						collabId = -1;
						Iterator cli = cl.iterator();
						while (cli.hasNext())				// check if collaboration already exists
						{
							CollaborationTreeNode ctn = (CollaborationTreeNode)cli.next();
							//collabList.addElement(new Integer(ctn.getId()));

							if (collabName.trim().equals(ctn.getName()))
							{
								collabId = ctn.getId();		// collaboration exists
								break;
							}
						}

						if (collabId == -1)
							System.out.println("Collaboration not found ");
						else
						{
							//Get Whiteboard ID
							int wbId = -1;
							BoardwalkCollaborationNode bcn = BoardwalkCollaborationManager.getCollaborationTree(bwcon, collabId);
							System.out.println("Sucessfully fetched the collab tree from the database");
							System.out.println("Collaboration : " + bcn.getName());							

							Vector wv = bcn.getWhiteboards();
							Iterator wvi = wv.iterator();

							while ( wvi.hasNext())
							{
								BoardwalkWhiteboardNode bwn = (BoardwalkWhiteboardNode)wvi.next();
								System.out.println("\tWhiteboard = " + bwn.getName());
								if (wbName.equals(bwn.getName()))
								{
									wbId= bwn.getId();
									System.out.println("WhiteboardID : " + wbId);
									break;
								}
							}							
							
							if (wbId == -1)
								System.out.println("Whiteboard Not found. Failed to create Cuboid");
							else
							{
								//Create Cuboid
								int tableId = -1;
								tableId = BoardwalkTableManager.createTable(bwcon, collabId, wbId, cuboidName, "Added through Migration process");
								if (tableId != -1)
								{
									System.out.println("Cuboid Created Successfully: " + cuboidName);
									sbResp.append( ContentDelimeter + "Creation of Cuboid : " + cuboidName +  Seperator + "Successful")  ;
								}
							}
						}
					} 
					catch (Exception e) 
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
						System.out.println("Failed to create Cuboid");
						sbResp.append( ContentDelimeter + "Creation of Cuboid : " + cuboidName +  Seperator + "Failed")  ;
					}
				
				}
				else if (command.equals("CreateUser"))
				{
					String firstName;
					String lastName;
					String email; 
					String extUserName;
					String pwd;
					firstName = paramEle[0];
					lastName = paramEle[1];
					email = paramEle[2];
					extUserName = paramEle[3];
					pwd = "0";
					int activeFlag = 1;
					userId = -1;
					try
					{
						NewUser nu = new NewUser(email, pwd, firstName, lastName, activeFlag);
						userId = UserManager.createUser(connection, nu);
						if (userId > 0)
						{
							sbResp.append( ContentDelimeter + "Creation of User : " + email +  Seperator + "Successful")  ;
							System.out.println("Created new User : " + email);
						}
					}
					catch (Exception e)
					{
						e.printStackTrace();
						sbResp.append( ContentDelimeter + "Creation of User : " + email +  Seperator + "Failed")  ;
						System.out.println("Failed to create new User : " + email);
					}
				}
				else if (command.equals("CreateMember"))
				{
					String nh0Name, nh1Name, nh2Name, nh3Name, email;
					nh0Name = paramEle[0];
					nh1Name = paramEle[1];
					nh2Name = paramEle[2];
					nh3Name = paramEle[3];
					email = paramEle[4];
					
					System.out.println("nh0Name : >" + nh0Name + "<");
					System.out.println("nh1Name : >" + nh1Name + "<");
					System.out.println("nh2Name : >" + nh2Name + "<");
					System.out.println("nh3Name : >" + nh3Name + "<");
					System.out.println("email : " + email);

					System.out.println("before getting userid : " + email);
					User user = UserManager.getUser(connection, email);
					userId = user.getId();
					System.out.println("after getting userId : " + userId);

					int nhLevel = -1;
					String nhPath = null;
					
					if (!nh0Name.isEmpty() && !nh1Name.isEmpty() && !nh2Name.isEmpty() && !nh3Name.isEmpty() )
					{
						nhLevel = 3;
						nhPath = nh0Name + "->" + nh1Name + "->" + nh2Name + "->" + nh3Name ;
					}
					else if (!nh0Name.isEmpty() && !nh1Name.isEmpty() && !nh2Name.isEmpty() && nh3Name.isEmpty() )
					{
						nhLevel = 2;
						nhPath = nh0Name + "->" + nh1Name + "->" + nh2Name ;
					}
					else if (!nh0Name.isEmpty() && !nh1Name.isEmpty() && nh2Name.isEmpty() && nh3Name.isEmpty() )
					{
						nhLevel = 1;
						nhPath = nh0Name + "->" + nh1Name ;
					}
					else if (!nh0Name.isEmpty() && nh1Name.isEmpty() && nh2Name.isEmpty() && nh3Name.isEmpty() )
					{
						nhLevel = 0;
						nhPath = nh0Name ;
					}

					NeighborhoodLevelId nhl;
					try {
						System.out.println("Before getting neighborhood levelId");
						System.out.println("nhLevel: " + nhLevel);
						nhl = NeighborhoodManager.getNeighborhoodLevelId(connection, nh0Name.trim(), nh1Name.trim(), nh2Name.trim(), nh3Name.trim(), nhLevel);
						int nhId = nhl.getId();
						System.out.println("after getting neighborhood levelId");
						//int memberId = BoardwalkNeighborhoodManager.createMember(bwcon, nhId, userId);
						System.out.println("nhId: " + nhId);
						System.out.println("userId: " + userId);
						System.out.println("tId: " + tid);
						int memberId = -1;
						memberId = MemberManager.createMember(connection, tid, userId, nhId);

						if (memberId > 0)
						{
							sbResp.append( ContentDelimeter + "Creation of new Member : " + email  + " under nhPath : " + nhPath  +  Seperator + "Successful")  ;
							System.out.println("new Member created under nhPath " + nhPath + " : " + memberId);
						}
					} 
					catch (SystemException e) 
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
						sbResp.append( ContentDelimeter + "Creation of new Member : " + email  + " under nhPath : " + nhPath  +  Seperator + "Failed")  ;
					}
				}

				if (!Seq.equals("9"))
				{
					try 
					{
						tm.commitTransaction();
					} 
					catch (SQLException e) 
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

			System.out.println("receivedCommands.length: " + receivedCommands.length);

			//sbResp.deleteCharAt(sbResp.length()-1);
			responseBuffer = sbResp.toString();
			System.out.println("responseBuffer : " + responseBuffer);
			commitResponseBuffer(responseBuffer, response);

			
		}


		if (  action.equals("GET DIAGRAM") )
		{
			StringBuffer sbResp = new StringBuffer();
			CallableStatement callableStatement = null;
			ResultSet rs = null;
			ResultSet newRs = null;

			System.out.println("selection action ..........GET DIAGRAM");
			try
			{
				DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
				connection = databaseloader.getConnection();

				int diagId;
				diagId = Integer.parseInt(st.nextToken(Seperator));
				System.out.println("diagId ........" + diagId );

				String diagType = null;

				callableStatement = connection.prepareCall(CALL_GET_DIAGRAM);
				callableStatement.setInt(1, diagId);

	
				rs = callableStatement.executeQuery();
				sbResp.append("success");
				while (rs.next())
				{
					diagType = rs.getString(4);
					sbResp.append( ContentDelimeter + rs.getInt(1) + Seperator + rs.getString(2) + Seperator + rs.getString(3) + Seperator + rs.getString(4) + Seperator + rs.getString(5) + Seperator + rs.getDate(6))  ;
				}

				sbResp.append( ContentDelimeter );


				callableStatement = null;


				StringBuffer sbResp2 = new StringBuffer();
				int rectCount = 0;
				callableStatement = connection.prepareCall(CALL_GET_DIAGRAM_RECTANGLES);
				callableStatement.setInt(1, diagId);

				newRs = callableStatement.executeQuery();
				while (newRs.next())
				{
					rectCount = rectCount + 1;
					sbResp2.append(  newRs.getInt(1) + Seperator + newRs.getInt(2) + Seperator + newRs.getString(3) + Seperator + newRs.getString(4)); 
					sbResp2.append(  Seperator + newRs.getString(5) + Seperator + newRs.getString(6) + Seperator + newRs.getString(7) );
					sbResp2.append(  Seperator + newRs.getString(8) + Seperator + newRs.getString(9) + Seperator + newRs.getString(10) );
					sbResp2.append(  Seperator + newRs.getString(11) + Seperator + newRs.getString(12) + Seperator + newRs.getString(13) ); 
					sbResp2.append(  Seperator + newRs.getString(14) + Seperator + newRs.getString(15) + Seperator + newRs.getString(16) ) ;
					sbResp2.append(  Seperator + newRs.getString(17) + Seperator + newRs.getString(18) + Seperator + newRs.getString(19) ) ;
					sbResp2.append(  Seperator + newRs.getString(20) + Seperator + newRs.getString(21) + Seperator + newRs.getString(22) ) ;
					sbResp2.append(  Seperator + newRs.getString(23) + Seperator + newRs.getString(24) + Seperator + newRs.getString(25) + Seperator + newRs.getString(26) +  ContentDelimeter ) ;
				}
//				callableStatement.close();
				sbResp.append( rectCount + ContentDelimeter + sbResp2.toString());


				if (diagType.equals("NhSetup"))
				{
					callableStatement = null;

					StringBuffer sbResp3 = new StringBuffer();
					int nhListCount = 0;

					callableStatement = connection.prepareCall(CALL_GET_DIAGRAM_NH_LIST);
					callableStatement.setInt(1, diagId);

					newRs = callableStatement.executeQuery();
					while (newRs.next())
					{
						nhListCount = nhListCount + 1;
						sbResp3.append(  newRs.getInt(1) + Seperator + newRs.getString(2) + Seperator + newRs.getString(3) + Seperator + newRs.getString(4)); 
						sbResp3.append(  Seperator + newRs.getString(5) + Seperator + newRs.getString(6) + Seperator + newRs.getString(7) );
						sbResp3.append(  Seperator + newRs.getString(8) + Seperator + newRs.getString(9) + Seperator + newRs.getString(10) );
						sbResp3.append(  Seperator + newRs.getString(11) + Seperator + newRs.getString(12) + Seperator + newRs.getString(13) ); 
						sbResp3.append(  Seperator + newRs.getString(14) + Seperator + newRs.getString(15) + Seperator + newRs.getString(16) ) ;
						sbResp3.append(  Seperator + newRs.getString(17) + Seperator + newRs.getInt(18) +  ContentDelimeter ) ;
					}
					sbResp.append( nhListCount + ContentDelimeter + sbResp3.toString());
	//				callableStatement.close();
				}
				
				sbResp.deleteCharAt(sbResp.length()-1);
				responseBuffer = sbResp.toString();

			}
			catch( SQLException sql ) 
			{
				sql.printStackTrace();
				 commitResponseBuffer("failure"
									+ xlService.ContentDelimeter
									+ "Failed to Get Diagram", response );
			}
			finally
			{
				try
				{
					if ( callableStatement != null )
						callableStatement.close();
					if (rs != null)
						rs.close();
					if (newRs != null)
						newRs.close();
					if ( connection != null )
						connection.close();
				}
				catch( SQLException sql )
				{
					sql.printStackTrace();
				}

				commitResponseBuffer(responseBuffer, response);

			}
		}		// end of GET DIAGRAM



		if (  action.equals("GET ALL DIAGRAMS") )
		{
			StringBuffer sbResp = new StringBuffer();
			CallableStatement callableStatement = null;
			ResultSet rs = null;

			System.out.println("selection action ..........GET ALL DIAGRAMS");
			try
			{
				DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
				connection = databaseloader.getConnection();

				String strRoleUser;
				strRoleUser = st.nextToken(Seperator);
				System.out.println("strRoleUser ........" + strRoleUser );


				callableStatement = connection.prepareCall(CALL_GET_DIAGRAMS);
				callableStatement.setString(1, strRoleUser);

				rs = callableStatement.executeQuery();
				responseBuffer = "success";
				while (rs.next())
				{
					responseBuffer = responseBuffer + ContentDelimeter + rs.getInt(1) + Seperator + rs.getString(2) + Seperator + rs.getString(3) + Seperator + rs.getString(4) + Seperator + rs.getString(5)  + Seperator + rs.getDate(6)  ;
				}

				callableStatement.close();
				rs.close();


			}
			catch( SQLException sql ) 
			{
				sql.printStackTrace();
				 commitResponseBuffer("failure"
									+ xlService.ContentDelimeter
									+ "Failed to Get All Diagram", response );
			}
			finally
			{
				try
				{
					if ( callableStatement != null )
						callableStatement.close();
					if (rs != null)
						rs.close();
					if ( connection != null )
						connection.close();
				}
				catch( SQLException sql )
				{
					sql.printStackTrace();
				}

				commitResponseBuffer(responseBuffer, response);

			}
		}		// end of GET ALL DIAGRAMS
		

		if (  action.equals("CREATE DIAGRAM") )			//Create NH Diagram
		{

			int lines = Integer.parseInt(st.nextToken(Seperator));
			StringBuffer sbResp = new StringBuffer();
			System.out.println("lines .........." + lines);

			CallableStatement callableStatement = null;

			System.out.println("selection action ..........CREATE DIAGRAM");
			try
			{
				DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
				connection = databaseloader.getConnection();

				String diagLine;
				String colSeqInfo = null;
				StringTokenizer sti = null;

				String diagHeader;
				diagHeader = st.nextToken(ContentDelimeter);

				StringTokenizer dh;
				dh = new StringTokenizer(diagHeader);


				String diagName, diagDesc, diagType, CreatedBy ;
				diagName = dh.nextToken(Seperator);
				diagDesc = dh.nextToken(Seperator);
				diagType = dh.nextToken(Seperator);
				CreatedBy = dh.nextToken(Seperator);
				System.out.println("diagName ........" + diagName );
				System.out.println("diagDesc ........" + diagDesc );
				System.out.println("diagType ........" + diagType );
				System.out.println("CreatedBy ........" + CreatedBy );

				int diagId;
				diagId = CreateNewDiagram(connection, diagName, diagDesc, diagType, CreatedBy);

				System.out.println("diagId ........" + diagId );
				System.out.println("lines ........" + lines );

				if (diagId > 0)
				{

					callableStatement = connection.prepareCall(CALL_INSERT_DIAGRAM_LINE);

					for (int col=2; col <= lines ; col++)
					{
						diagLine = "";
						diagLine = st.nextToken(ContentDelimeter);
						System.out.println("diagLine ........" + diagLine );

						sti = new StringTokenizer(diagLine);

						callableStatement.setInt(1, diagId);
						callableStatement.setString(2, sti.nextToken(Seperator));
						callableStatement.setString(3, sti.nextToken(Seperator));
						callableStatement.setString(4, sti.nextToken(Seperator));
						callableStatement.setString(5, sti.nextToken(Seperator));
						callableStatement.setString(6, sti.nextToken(Seperator));
						callableStatement.setString(7, sti.nextToken(Seperator));
						callableStatement.setString(8, sti.nextToken(Seperator));
						callableStatement.setString(9, sti.nextToken(Seperator));
						callableStatement.setString(10, sti.nextToken(Seperator));
						callableStatement.setString(11, sti.nextToken(Seperator));
						callableStatement.setString(12, sti.nextToken(Seperator));
						callableStatement.setString(13, sti.nextToken(Seperator));
						callableStatement.setString(14, sti.nextToken(Seperator));
						callableStatement.setString(15, sti.nextToken(Seperator));
						callableStatement.setString(16, sti.nextToken(Seperator));
						callableStatement.setString(17, sti.nextToken(Seperator));
						callableStatement.setString(18, sti.nextToken(Seperator));
						callableStatement.setString(19, sti.nextToken(Seperator));
						callableStatement.setString(20, sti.nextToken(Seperator));
						callableStatement.setString(21, sti.nextToken(Seperator));
						callableStatement.setString(22, sti.nextToken(Seperator));
						callableStatement.setString(23, sti.nextToken(Seperator));
						callableStatement.setString(24, sti.nextToken(Seperator));
						callableStatement.setString(25, sti.nextToken(Seperator));

						callableStatement.addBatch();
					}
					callableStatement.executeBatch();

					System.out.println("Diagram lines inserted.");

					int nhLines = Integer.parseInt(st.nextToken(ContentDelimeter));
					System.out.println("nhlines.... " + nhLines);

					String nhLine;

					callableStatement = null;
					callableStatement = connection.prepareCall(CALL_INSERT_NH_LIST_LINE);
					for (int nol=2; nol <= nhLines ; nol++)
					{
						nhLine = "";
						nhLine = st.nextToken(ContentDelimeter);
						System.out.println("nhLine ........" + nhLine );

						sti = new StringTokenizer(nhLine);

						callableStatement.setInt(1, diagId);
						callableStatement.setString(2, sti.nextToken(Seperator));
						callableStatement.setString(3, sti.nextToken(Seperator));
						callableStatement.setString(4, sti.nextToken(Seperator));
						callableStatement.setString(5, sti.nextToken(Seperator));
						callableStatement.setString(6, sti.nextToken(Seperator));
						callableStatement.setString(7, sti.nextToken(Seperator));
						callableStatement.setString(8, sti.nextToken(Seperator));
						callableStatement.setString(9, sti.nextToken(Seperator));
						callableStatement.setString(10, sti.nextToken(Seperator));
						callableStatement.setString(11, sti.nextToken(Seperator));
						callableStatement.setString(12, sti.nextToken(Seperator));
						callableStatement.setString(13, sti.nextToken(Seperator));
						callableStatement.setString(14, sti.nextToken(Seperator));
						callableStatement.setString(15, sti.nextToken(Seperator));
						callableStatement.setString(16, sti.nextToken(Seperator));
						callableStatement.setString(17, sti.nextToken(Seperator));
						callableStatement.setInt(18, Integer.parseInt(sti.nextToken(Seperator)));

						callableStatement.addBatch();
					}
					callableStatement.executeBatch();

					System.out.println("Nh lines inserted.");

					sbResp.append(diagName + " is exported successfully." + "\n");

					commitResponseBuffer("success"
								+ xlService.ContentDelimeter
								+ sbResp.toString(), response );
				}
			}

			catch( SQLException sql ) 
			{
				sql.printStackTrace();
				 commitResponseBuffer("failure"
									+ xlService.ContentDelimeter
									+ "Failed to Create Diagram", response );
			}
			finally
			{
				try
				{
					if ( callableStatement != null )
						callableStatement.close();

					if ( connection != null )
						connection.close();
				}
				catch( SQLException sql )
				{
					sql.printStackTrace();
				}
			}
		}		// end of CREATE DIAGRAM


	
		if (  action.equals("CREATE CL DIAGRAM") )			//Create Cuboid Link Diagram
		{

			int lines = Integer.parseInt(st.nextToken(Seperator));
			StringBuffer sbResp = new StringBuffer();
			System.out.println("lines .........." + lines);

			CallableStatement callableStatement = null;

			System.out.println("selection action ..........CREATE CL DIAGRAM");
			try
			{
				DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
				connection = databaseloader.getConnection();

				String diagLine;
				String colSeqInfo = null;
				StringTokenizer sti = null;

				String diagHeader;
				diagHeader = st.nextToken(ContentDelimeter);

				StringTokenizer dh;
				dh = new StringTokenizer(diagHeader);


				String diagName, diagDesc, diagType, CreatedBy ;
				diagName = dh.nextToken(Seperator);
				diagDesc = dh.nextToken(Seperator);
				diagType = dh.nextToken(Seperator);
				CreatedBy = dh.nextToken(Seperator);
				System.out.println("diagName ........" + diagName );
				System.out.println("diagDesc ........" + diagDesc );
				System.out.println("diagType ........" + diagType );
				System.out.println("CreatedBy ........" + CreatedBy );

				int diagId;
				diagId = CreateNewDiagram(connection, diagName, diagDesc, diagType, CreatedBy);

				System.out.println("diagId ........" + diagId );
				System.out.println("lines ........" + lines );

				if (diagId > 0)
				{

					callableStatement = connection.prepareCall(CALL_INSERT_DIAGRAM_LINE);

					for (int col=2; col <= lines ; col++)
					{
						diagLine = "";
						diagLine = st.nextToken(ContentDelimeter);
						System.out.println("diagLine ........" + diagLine );

						sti = new StringTokenizer(diagLine);

						callableStatement.setInt(1, diagId);
						callableStatement.setString(2, sti.nextToken(Seperator));
						callableStatement.setString(3, sti.nextToken(Seperator));
						callableStatement.setString(4, sti.nextToken(Seperator));
						callableStatement.setString(5, sti.nextToken(Seperator));
						callableStatement.setString(6, sti.nextToken(Seperator));
						callableStatement.setString(7, sti.nextToken(Seperator));
						callableStatement.setString(8, sti.nextToken(Seperator));
						callableStatement.setString(9, sti.nextToken(Seperator));
						callableStatement.setString(10, sti.nextToken(Seperator));
						callableStatement.setString(11, sti.nextToken(Seperator));
						callableStatement.setString(12, sti.nextToken(Seperator));
						callableStatement.setString(13, sti.nextToken(Seperator));
						callableStatement.setString(14, sti.nextToken(Seperator));
						callableStatement.setString(15, sti.nextToken(Seperator));
						callableStatement.setString(16, sti.nextToken(Seperator));
						callableStatement.setString(17, sti.nextToken(Seperator));
						callableStatement.setString(18, sti.nextToken(Seperator));
						callableStatement.setString(19, sti.nextToken(Seperator));
						callableStatement.setString(20, sti.nextToken(Seperator));
						callableStatement.setString(21, sti.nextToken(Seperator));
						callableStatement.setString(22, sti.nextToken(Seperator));
						callableStatement.setString(23, sti.nextToken(Seperator));
						callableStatement.setString(24, sti.nextToken(Seperator));
						callableStatement.setString(25, sti.nextToken(Seperator));

						callableStatement.addBatch();
					}
					callableStatement.executeBatch();

					System.out.println("Diagram lines inserted.");

					sbResp.append(diagName + " is exported successfully." + "\n");

					commitResponseBuffer("success"
								+ xlService.ContentDelimeter
								+ sbResp.toString(), response );
				}
			}

			catch( SQLException sql ) 
			{
				sql.printStackTrace();
				 commitResponseBuffer("failure"
									+ xlService.ContentDelimeter
									+ "Failed to Create Diagram", response );
			}
			finally
			{
				try
				{
					if ( callableStatement != null )
						callableStatement.close();

					if ( connection != null )
						connection.close();
				}
				catch( SQLException sql )
				{
					sql.printStackTrace();
				}
			}
		}		// end of CREATE CUBOID LINK DIAGRAM

	
	}




	public static int CreateNewDiagram(Connection connection, String diagName, String diagDesc, String diagType, String CreatedBy) throws SQLException
	{
		CallableStatement callablestatement = null;
		int diagId = -1;
		try
		{
			callablestatement = connection.prepareCall(CALL_CREATE_DIAGRAM);
			callablestatement.setString(1, diagName);
			callablestatement.setString(2, diagDesc);
			callablestatement.setString(3, diagType);
			callablestatement.setString(4, CreatedBy);
			callablestatement.registerOutParameter(5, java.sql.Types.INTEGER);

			callablestatement.execute();
			diagId = callablestatement.getInt(5);
		}
		catch (SQLException sql1)
		{
			  throw sql1;
		}
		finally
		{
			  try
			  {
					callablestatement.close();
			  }
			  catch (SQLException sql2)
			  {
					throw sql2;
			  }
		}
		return diagId;
	}


	public BoardwalkConnection getBoardwalkConnection(String userName, String Password, int memberId) throws IOException
	{
		try
		{
			DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
			connection = databaseloader.getConnection();
			bwcon = BoardwalkConnectionManager.getBoardwalkConnection(connection, userName, Password, memberId);
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
		System.out.println("bwcon : " + bwcon);
		System.out.println("bwcon.getMemberId() : " + bwcon.getMemberId());

		return bwcon;
	}


	public int getNeighborhood0MembershipId(String userName, String nh0Name)
	{
		String nh1Name, nh2Name, nh3Name,  collabName;
		nh1Name = "";
		nh2Name = "";
		nh3Name = "";

		System.out.println("nh0Name : >" + nh0Name + "<");
		System.out.println("nh1Name : >" + nh1Name + "<");
		System.out.println("nh2Name : >" + nh2Name + "<");
		System.out.println("nh3Name : >" + nh3Name + "<");
		System.out.println("userName : " + userName);

		int nhLevel = 0;
					
		NeighborhoodLevelId nhl;
		int nhId = -1;
		try {
			System.out.println("Before getting neighborhood levelId");
			System.out.println("nhLevel: " + nhLevel);
			nhl = NeighborhoodManager.getNeighborhoodLevelId(connection, nh0Name.trim(), nh1Name.trim(), nh2Name.trim(), nh3Name.trim(), nhLevel);
			nhId = nhl.getId();
			System.out.println("nhId based on neighborhood: " + nhId);
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("before getting userid : " + userName);
		User user = UserManager.getUser(connection, userName);
		int userId = user.getId();
		System.out.println("after getting userId : " + userId);

		Hashtable memberships = null;
		Enumeration memberIds = null ;
		try
		{
			memberships  = UserManager.getMembershipsForUser(connection, userId );
			memberIds = memberships.keys();
			System.out.println("memberships.size : " + memberships.size());
		}
		catch ( Exception e )
		{
		   e.printStackTrace();
		}

		int memberId = -1 ;
		int membernhId = -1;
		int retMembernhId = -1;
		String nhName;
		if (memberships.size() == 0 )
		{
			System.out.println("Memberships not found");
		}
		else
		{
			if (  memberships.size() > 0 )
			{
				System.out.println("Checking membership...");
				boolean membershipFound = false;
				for (int ii=0; ii < memberships.size(); ii++)
				{
					memberId =((Integer) memberIds.nextElement()).intValue();
					membernhId =((Member) memberships.get(        new Integer(memberId)   )).getNeighborhoodId();
					nhName = ((Member) memberships.get(        new Integer(memberId)   )).getNeighborhoodName();
					System.out.println("nhId based on membership : " + membernhId +   " nhname: " + nhName);
					System.out.println("nhId :" + nhId);
					if (nhId == membernhId)
					{
						System.out.println("Membership found.");
						membershipFound = true;
						retMembernhId = membernhId;
						break;
					}
				}

				if (membershipFound == false)
					System.out.println("Membership NOT found.");
			}
		}

		return retMembernhId;
	}
}