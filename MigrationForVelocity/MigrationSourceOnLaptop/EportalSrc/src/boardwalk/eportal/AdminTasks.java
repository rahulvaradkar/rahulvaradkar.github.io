package boardwalk.eportal;


import com.boardwalk.database.DatabaseLoader;
import boardwalk.connection.*;
import com.boardwalk.exception.*;
import java.io.*;
import java.sql.*;
import java.util.*;				//for Properties
import com.boardwalk.database.*;
import com.boardwalk.collaboration.*;
import boardwalk.collaboration.*;
import com.boardwalk.whiteboard.*;
import boardwalk.table.*;
import boardwalk.neighborhood.*;
import com.boardwalk.neighborhood.*;

//Added by Rahul Varadkar on 13-FEB-2018 for Link Import Table
import com.boardwalk.table.*;		
import com.boardwalk.member.Member;
import com.boardwalk.user.UserManager;
import com.boardwalk.excel.*;
//Added by Rahul Varadkar on 13-FEB-2018 for Link Import Table



//Added by Rahul Varadkar on 20-FEB-2018 for Refresh Table
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
//Added by Rahul Varadkar on 20-FEB-2018 for Refresh Table

/**
 * Service to manage a Admin Task of Boardwalk Collabroation Platform.
 */
//  JSON Changes for link Export
//import org.json.JSONArray;
//import org.json.JSONObject;
//  JSON Changes for link Export


import java.util.regex.Matcher;		//Added for Submit Cuboid by Rahul Varadkar on  21-MARCH-2018
import java.util.regex.Pattern;		//Added for Submit Cuboid by Rahul Varadkar on  21-MARCH-2018


public class AdminTasks
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
	private static String CALL_BW_GET_COLUMN_NAMES = "{CALL BW_GET_COLUMN_NAMES (?,?)}";


	//Added by RahulV on 02-June-2015	
	private static String CALL_BW_CREATE_SQL_STRUCTURE_FROM_BRECT  = "{CALL BW_CREATE_SQL_STRUCTURE_FROM_BRECT (?, ?)}";

	//Added by RahulV on 08-June-2015
	private static String CALL_BW_SUPERMERGE_RULE_ACCESS_LOAD  = "{CALL BW_SUPERMERGE_ACCESS_LOAD()}";
	private static String CALL_BW_SUPERMERGE_CUBOID_TO_SQL = "{CALL BW_SUPERMERGE_CUBOID_TO_SQL (?,?,?,?)}";

	//Added by RahulV on 30-July-2015
	private static String CALL_BW_GET_CUBOID_ID = "{CALL BW_GET_CUBOID_ID(?,?,?,?,?,?)}";


	//Added by RahulV on 05-August-2015
	private static String CALL_BW_GET_COLLAB_CREATED_BY_USER = "{CALL BW_GET_COLLAB_CREATED_BY_USER(?)}";


	//Added by RahulV on 13-Feb-2016
	private static String CALL_BW_UPDATE_COLLAB_STRUCTURE = "{CALL BW_UPDATE_COLLAB_STRUCTURE()}";


	//Added by RahulV on 02-July-2017
	private static String CALL_BW_TABLEWISE_CELLS_REPORT = "{CALL BW_TABLEWISE_CELLS_REPORT()}";


	//Added by RahulV on 26-August-2017
	private static String CALL_BW_SM_C2S_PIVOT = "{CALL BW_SM_C2S_PIVOT(?,?)}";

	//Added by RahulV on 28-September-2017
	private static String CALL_BW_VIEW_CUBOID_DATA_FILTER = "{CALL BW_VIEW_CUBOID_DATA_FILTER(?,?,?,?)}";

	private static String PIPE_CHAR = "|";

	static Connection connection = null;
	static BoardwalkConnection bwcon = null;
	static StringBuffer sb = null;
	
	private AdminTasks()
	{

	}



	public static int CreateCollaboration (Connection connection, String collabName, String desc, int userId, int memberId) throws  BoardwalkException
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
	}


	public static int RenameCollaboration(Connection connection, int collabId, String newCollabName) throws  BoardwalkException
	{
		System.out.println("Calling CollaborationManager.renameCollaboration collabId=" + collabId + " newName=" + newCollabName );
		collabId = CollaborationManager.renameCollaboration(connection, collabId, newCollabName);
		if (collabId == -1)
		{
			throw new BoardwalkException( 10013 );
		}
		return collabId;
	}


	//Delete Collaboration
	public static boolean DeleteCollaboration (BoardwalkConnection bwcon, int collabId) throws  BoardwalkException
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


	// CREATE WHITEBOARD
	public static int CreateWhiteboard (BoardwalkConnection bwcon, int collabId, String WhiteboardName) throws  BoardwalkException
	{
		int wbId = BoardwalkCollaborationManager.createWhiteboard(bwcon, WhiteboardName, collabId );
		if (wbId == -1)
				throw new BoardwalkException( 10011 );
		return wbId;
	}

	// Delete Whiteboard
	public static boolean DeleteWhiteboard (Connection connection, int wbId) throws  BoardwalkException
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
	public static int RenameWhiteboard(Connection connection, int wbId, String newWbName) throws  BoardwalkException
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
	public static int CreateTable (BoardwalkConnection bwcon, int collabId, int wbId, String tableName, String tableDesc) throws  BoardwalkException
	{
		int tableId = BoardwalkTableManager.createTable(bwcon, collabId, wbId, tableName, tableDesc);
		if (tableId == -1)
				throw new BoardwalkException( 10012 );
		return tableId;
	}

	// Delete Table
	public static boolean DeleteTable (BoardwalkConnection bwcon, int tableId) throws  BoardwalkException
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
	public static int RenameTable(Connection connection, int tableId, String newTableName) throws  BoardwalkException
	{
		System.out.println("Calling CollaborationManager.renameTable tableId=" + tableId + " newTableName=" + newTableName );
		tableId = CollaborationManager.renameTable(connection, tableId, newTableName);
		if (tableId == -1)
		{
			throw new BoardwalkException( 10014 );
		}
		return tableId;
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



	// CREATE NEIGHBORHOOD
	public static int CreateNeighborhood(BoardwalkConnection bwcon, String nhName,  int parentNhId) throws  BoardwalkException  
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
	public static boolean DeleteNeighborhood(BoardwalkConnection bwcon, int nhId) throws  BoardwalkException  
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
	public static int RenameNh(Connection connection, int nhId, String newNhName) throws  BoardwalkException
	{
		System.out.println("Calling CollaborationManager.renameNh nhId=" + nhId + " newNhName=" + newNhName );
		nhId = CollaborationManager.renameNh(connection, nhId, newNhName);
		if (nhId == -1)
		{
			throw new BoardwalkException( 10016 );
		}
		return nhId;
	}


	// Get Neighborhood Tree
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
	public static void DeleteMembership(BoardwalkConnection bwcon, int memberId) throws BoardwalkException			//SystemException
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
				sbUserList.append(bu.getId() + "|" + bu.getUserName() + "|" + bu.getFirstName() + "|" + bu.getLastName() + "|" + bu.getActive() + "\n");
			}
		}
		catch (BoardwalkException bwe)
		{
			System.out.println("Error fetching neighborhood");
		}

		if (sbUserList.length() > 0)
		{
			System.out.println("Removing last character from sbUserList");
			sbUserList.deleteCharAt(sbUserList.length()-1);
		}
		return sbUserList.toString();
	}


	//Get Members list of Neighborhood
	public static String GetNeighborhoodMemberList(BoardwalkConnection bwcon, int nhId)
	{
		StringBuffer sbMemberList = new StringBuffer();
            try
            {
				HashMap userMap = BoardwalkUserManager.getUserHashMap(bwcon);

				Vector mList = BoardwalkNeighborhoodManager.getMemberList(bwcon, nhId);
				System.out.println("Successfully fetched membership list for nh");
				Iterator mi = mList.iterator();
				while (mi.hasNext())
				{
					BoardwalkMember bm = (BoardwalkMember)mi.next();
					if (bm.getNeighborhoodId() == nhId)
					{
					System.out.println("id="+bm.getId()+":"+
										"userId="+bm.getUserId()+":"+
										"neighborhoodId="+bm.getNeighborhoodId()+":"+
										"neighborhoodName="+bm.getNeighborhoodName());

					BoardwalkUser bu = (BoardwalkUser) userMap.get(Integer.toString(bm.getUserId()));
					System.out.println("id="+bu.getId()+":FirstName="+bu.getFirstName()+":LastName="+bu.getLastName()+":IsActive="+bu.getActive());
					sbMemberList.append(bm.getId() + "|" + bm.getUserId() + "|" + bm.getNeighborhoodId() + "|" + bu.getFirstName() + "|" + bu.getLastName() + "|" + bu.getUserName() + "|" + bu.getActive() + "\n");
					}
				}
				if (sbMemberList.length() > 0)
				{
					System.out.println("Removing last character from sbMemberList");
					sbMemberList.deleteCharAt(sbMemberList.length()-1);
				}
            }
            catch (BoardwalkException bwe)
            {
                System.out.println("Error reading membership");
            }
		return sbMemberList.toString();
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
					CollabforNh = AdminTasks.GetCollaborationTreeForNh(connection, bwcon, nhId);
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
					CollabforNh = AdminTasks.GetCollaborationTreeForNh(connection, bwcon, nhId);
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






	public static String getColumnAccessForUpdate(BoardwalkConnection bwcon, Connection connection, int tableId)
 throws BoardwalkException
	{

		BoardwalkTableContents btc = BoardwalkTableManager.getTableContents(bwcon, tableId);
		Vector cols = btc.getColumns();

		if (cols.isEmpty())
		{
				throw new BoardwalkException( 10024 );
		}


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
				//throw new SystemException(sql2);
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



	public static boolean AddNewRelation(Connection connection, int nhId, int userId, String relation, String selNhIds) throws  BoardwalkException
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



	public static boolean UpdateRelation(Connection connection, int nhId, int userId, String relation, String selNhIds) throws  BoardwalkException
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


	public static String GetBRectInformation(BoardwalkConnection bwcon) throws SystemException
	{
		PreparedStatement ps = null;
		ResultSet rs = null;
		String retstr = "";

		StringBuffer strBRectName = new StringBuffer();
		StringBuffer strBRectType = new StringBuffer();
		StringBuffer strCuboidName = new StringBuffer();
		StringBuffer strWbName = new StringBuffer();
		StringBuffer strCollabName = new StringBuffer();
		StringBuffer strNh = new StringBuffer();

		StringBuffer strDatabase = new StringBuffer();
		StringBuffer strCollabId = new StringBuffer();
		StringBuffer strWbId = new StringBuffer();
		StringBuffer strCuboidId = new StringBuffer();

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
				strDatabase.append(rs.getString(2) + "|");
				strBRectType.append(rs.getString(3) + "|");
				strCuboidName.append(rs.getString(4) + "|");
				strWbName.append(rs.getString(5) + "|");
				strCollabName.append(rs.getString(6) + "|");
				strCollabId.append(rs.getString(7) + "|");
				strWbId.append(rs.getString(8) + "|");
				strCuboidId.append(rs.getString(9) + "|");
				strNh.append(rs.getString(10) + "|");
			}


			retstr = strBRectName.toString() + "\n" + strDatabase.toString() + "\n" + strBRectType.toString() + "\n" +  strCuboidName.toString()  + "\n" + strWbName.toString() + "\n" +  strCollabName.toString() + "\n"  + strCollabId.toString() + "\n" +  strWbId.toString() + "\n" +  strCuboidId.toString() + "\n" +  strNh.toString() ;


			// brect types
			StringBuffer strMasterBRectType = new StringBuffer();
			StringBuffer strBRectTypeDesc = new StringBuffer();

			ps.setString(1, "BRECT TYPES");
			System.out.println("\n after setInt, before executeQuery..");
			rs = ps.executeQuery();
			System.out.println("\n before rs.next");
			while (rs.next())
			{
				strMasterBRectType.append(rs.getString(1) + "|");
				strBRectTypeDesc.append(rs.getString(2) + "|");
			}

			retstr = retstr +  ContentDelimeter + strMasterBRectType.toString() + Seperator + strBRectTypeDesc.toString();

			///////////
			System.out.println("\n after rs.next");

			System.out.println("retstr : " + retstr);

		}
		catch (SQLException sql1)
		{
			System.out.println(sql1.toString());
			return "Error in GetBRectInformation";
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


	public static boolean UpdateBRectDefinition(BoardwalkConnection bwcon) throws BoardwalkException
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



	public static String GetDefinedRolesUsers(BoardwalkConnection bwcon) throws SystemException
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



	public static void UpdateUserRoleChanges(BoardwalkConnection bwcon, String userUpdates) throws SystemException
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


	public static String GetTemplateManifestsFromBRectDefinition(BoardwalkConnection bwcon) throws SystemException
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



	public static String getBRectNamesOfType(BoardwalkConnection bwcon, String brectType) throws SystemException
	{
		PreparedStatement ps = null;
		ResultSet rs = null;
		String retstr = "";

		StringBuffer strBRectNames = new StringBuffer();

		try
		{
			ps = connection.prepareStatement(CALL_BW_SELECT_CUBOID);
			ps.setString(1, "BRECTDEFINITION");
			System.out.println("\n after setInt, before executeQuery..");
			rs = ps.executeQuery();
			System.out.println("\n before rs.next");
			while (rs.next())
			{
				if (rs.getString(3).equals(brectType))
				{
					strBRectNames.append(rs.getString(1) + "|");							
				}

			}

			retstr = strBRectNames.toString() ;

			///////////
			System.out.println("\n after rs.next");

			System.out.println("retstr : " + retstr);

		}
		catch (SQLException sql1)
		{
			System.out.println(sql1.toString());
			return "Error in getBRectTypes. Try again.";
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


	public static String getBRectTypes(BoardwalkConnection bwcon) throws SystemException
	{
		PreparedStatement ps = null;
		ResultSet rs = null;
		String retstr = "";

		StringBuffer strBRectType = new StringBuffer();


		try
		{
			ps = connection.prepareStatement(CALL_BW_SELECT_CUBOID);
			ps.setString(1, "BRECT TYPES");
			System.out.println("\n after setInt, before executeQuery..");
			rs = ps.executeQuery();
			System.out.println("\n before rs.next");
			while (rs.next())
			{
				strBRectType.append(rs.getString(1) + Seperator + rs.getString(2)  + "|");
			}

			retstr = strBRectType.toString() ;

			///////////
			System.out.println("\n after rs.next");

			System.out.println("retstr : " + retstr);

		}
		catch (SQLException sql1)
		{
			System.out.println(sql1.toString());
			return "Error in getBRectTypes. Try again.";
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


	// returns KEY STORE DETAILS
	public static String getKeyStoreDetails(BoardwalkConnection bwcon, String keyStoreName ) throws SystemException
	{

		PreparedStatement ps = null;
		ResultSet rs = null;
		String retstr = "";

		StringBuffer strKeyName = new StringBuffer();
		StringBuffer strKeyType = new StringBuffer();
		StringBuffer strBRectDefinition = new StringBuffer();
		StringBuffer strBRectName = new StringBuffer();
		StringBuffer strKey = new StringBuffer();

		try
		{
			ps = connection.prepareStatement(CALL_BW_SELECT_CUBOID);
			ps.setString(1, keyStoreName);
			System.out.println("\n after setInt, before executeQuery..");
			rs = ps.executeQuery();
			System.out.println("\n before rs.next");
			while (rs.next())
			{
				strKeyName.append(rs.getString(1) + Seperator);				// Instead of PIPE CHAR , seperator is used. Key contains column names separated by PIPE in data.
				strKeyType.append(rs.getString(2) + Seperator);
				strBRectName.append(rs.getString(3) + Seperator);
				strBRectDefinition.append(rs.getString(4) + Seperator);
				strKey.append(rs.getString(5) + Seperator);
			}

			retstr = strKeyName.toString() + "\n" + strKeyType.toString() + "\n" + strBRectName.toString() + "\n" +  strBRectDefinition.toString()  + "\n" + strKey.toString();
			///////////
			System.out.println("\n after rs.next");

			System.out.println("retstr : " + retstr);

		}
		catch (SQLException sql1)
		{
			System.out.println(sql1.toString());
			return "Error in getKeyStoreDetails";
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


	public static String getBRectDetails(BoardwalkConnection bwcon, String bRectDefinition ) throws SystemException
	{
		PreparedStatement ps = null;
		ResultSet rs = null;
		String retstr = "";

		StringBuffer strBRectName = new StringBuffer();
		StringBuffer strDatabaseName = new StringBuffer();
		StringBuffer strBRectType = new StringBuffer();
		StringBuffer strCuboidName = new StringBuffer();
		StringBuffer strWbName = new StringBuffer();
		StringBuffer strCollabName = new StringBuffer();
		StringBuffer strNh = new StringBuffer();

		try
		{
			ps = connection.prepareStatement(CALL_BW_SELECT_CUBOID);
			ps.setString(1, bRectDefinition);
			System.out.println("\n after setInt, before executeQuery..");
			rs = ps.executeQuery();
			System.out.println("\n before rs.next");
			while (rs.next())
			{
				strBRectName.append(rs.getString(1) + "|");
				strDatabaseName.append(rs.getString(2) + "|");
				strBRectType.append(rs.getString(3) + "|");
				strCuboidName.append(rs.getString(4) + "|");
				strWbName.append(rs.getString(5) + "|");
				strCollabName.append(rs.getString(6) + "|");
				strNh.append(rs.getString(7) + "|");
			}

			retstr = strBRectName.toString() + "\n" + strDatabaseName.toString() + "\n" + strBRectType.toString() + "\n" +  strCuboidName.toString()  + "\n" + strWbName.toString() + "\n" +  strCollabName.toString() + "\n" +  strNh.toString() ;
			///////////
			System.out.println("\n after rs.next");

			System.out.println("retstr : " + retstr);

		}
		catch (SQLException sql1)
		{
			System.out.println(sql1.toString());
			return "Error in GetBRectInformation";
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



	// retruns list of System Cuboids
	public static String getSystemCuboidsDetails(BoardwalkConnection bwcon) throws SystemException
	{
		PreparedStatement ps = null;
		ResultSet rs = null;
		String retstr = "";

		StringBuffer strCuboidName = new StringBuffer();
		StringBuffer strWbName = new StringBuffer();
		StringBuffer strCollabName = new StringBuffer();
		StringBuffer strNhName = new StringBuffer();

		try
		{
			ps = connection.prepareStatement(CALL_BW_SELECT_CUBOID);
			ps.setString(1, "SYSTEM CUBOIDS");
			System.out.println("\n after setInt, before executeQuery..");
			rs = ps.executeQuery();
			System.out.println("\n before rs.next");
			while (rs.next())
			{
				strCuboidName.append(rs.getString(1) + Seperator);				
				strWbName.append(rs.getString(2) + Seperator);
				strCollabName.append(rs.getString(3) + Seperator);
				strNhName.append(rs.getString(4) + Seperator);
			}

			retstr = strCuboidName.toString() + "\n" + strWbName.toString() + "\n" + strCollabName.toString() + "\n" +  strNhName.toString()  ;
			///////////
			System.out.println("\n after rs.next");
			System.out.println("retstr : " + retstr);

		}
		catch (SQLException sql1)
		{
			System.out.println(sql1.toString());
			return "Error in getKeyStoreDetails";
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
	

	// returns BRECT Columns list
	public static String getBRectColumns(BoardwalkConnection bwcon, String BRectName ) throws SystemException
	{
		PreparedStatement ps = null;
		ResultSet rs = null;
		String retstr = "";

		StringBuffer strColumnNames = new StringBuffer();

		try
		{
			ps = connection.prepareStatement(CALL_BW_GET_COLUMN_NAMES);
			ps.setString(1, "BRECT");
			ps.setString(2, BRectName);

			System.out.println("\n after setInt, before executeQuery..");
			rs = ps.executeQuery();
			System.out.println("\n before rs.next");
			while (rs.next())
			{
				strColumnNames.append(rs.getString(1) + Seperator);				
			}

			retstr = strColumnNames.toString() ;
			///////////
			System.out.println("\n after rs.next");

			System.out.println("retstr : " + retstr);

		}
		catch (SQLException sql1)
		{
			System.out.println(sql1.toString());
			return "Error in getKeyStoreDetails";
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




	// returns Genereate SQL Table with same structue as BRectName Columns list
	public static String generateSQLBrect(Connection connection, String BRectName , String SQLBRectName) throws SystemException
	{
		CallableStatement callablestatement = null;
		//PreparedStatement ps = null;
		//ResultSet rs = null;
		String retstr = "";

		//StringBuffer strColumnNames = new StringBuffer();

		try
		{
			callablestatement = connection.prepareCall(CALL_BW_CREATE_SQL_STRUCTURE_FROM_BRECT);
			callablestatement.setString(1, BRectName);
			callablestatement.setString(2, SQLBRectName);

			System.out.println("\n after setString, before executeUpdate..");
			callablestatement.executeUpdate();
			callablestatement.close();
			callablestatement = null;

			retstr = SQLBRectName;
			///////////
			System.out.println("\n after executeUpdate");

			System.out.println("retstr : " + retstr);

		}
		catch (SQLException sql1)
		{
			System.out.println(sql1.toString());
			return "Error in getKeyStoreDetails";
		}
		finally
		{
			try
			{
				if ( callablestatement != null )
					callablestatement.close();

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


	public static String GetSuperMergeRuleNames(BoardwalkConnection bwcon) throws SystemException
	{
		PreparedStatement ps = null;
		ResultSet rs = null;
		String retstr = "";

		StringBuffer  strRuleType;
		strRuleType = new StringBuffer();				

		StringBuffer  strRuleName;
		strRuleName = new StringBuffer();				

		try
		{
			// BW_SM_CUBOID_TO_CUBOID - Cuboid To Cuboid supermerge rules
			ps = connection.prepareStatement(CALL_BW_SELECT_CUBOID);
			ps.setString(1, "BW_SM_CUBOID_TO_CUBOID");
			System.out.println("\n after setInt, before executeQuery..");
			rs = ps.executeQuery();
			System.out.println("\n before rs.next");
			while (rs.next())
			{
				strRuleType.append("BW_SM_CUBOID_TO_CUBOID" + "|");
				strRuleName.append(rs.getString(1) + "|");
			}


			// BW_SM_SQL_TO_CUBOID - SQL To Cuboid supermerge rules
			ps.setString(1, "BW_SM_SQL_TO_CUBOID");
			System.out.println("\n after setInt, before executeQuery..");
			rs = ps.executeQuery();
			System.out.println("\n before rs.next");
			while (rs.next())
			{
				strRuleType.append("BW_SM_SQL_TO_CUBOID" + "|");
				strRuleName.append(rs.getString(1) + "|");
			}


			// BW_SM_CUBOID_TO_SQL - Cuboid To SQL supermerge rules
			ps.setString(1, "BW_SM_CUBOID_TO_SQL");
			System.out.println("\n after setInt, before executeQuery..");
			rs = ps.executeQuery();
			System.out.println("\n before rs.next");
			while (rs.next())
			{
				strRuleType.append("BW_SM_CUBOID_TO_SQL" + "|");
				strRuleName.append(rs.getString(1) + "|");
			}


			// BW_SM_CUBOID_TO_SQL - Cuboid To SQL supermerge rules
			ps.setString(1, "SM_MULTIC_2_C_RULES");
			System.out.println("\n after setInt, before executeQuery..");
			rs = ps.executeQuery();
			System.out.println("\n before rs.next");
			while (rs.next())
			{
				strRuleType.append("SM_MULTIC_2_C_RULES" + "|");
				strRuleName.append(rs.getString(1) + "|");
			}

			strRuleType.deleteCharAt(strRuleType.length()-1);
			strRuleName.deleteCharAt(strRuleName.length()-1);
			retstr = strRuleType.toString() + "\n" + strRuleName.toString() ;

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


	// Returns SuperMerge Access details from cuboid 'SUPERMERGE_RULE_ACCESS'. used in SuperMerge Access Setting form.
	public static String GetSuperMergeRuleUserAccess(BoardwalkConnection bwcon) throws SystemException
	{

		PreparedStatement ps = null;
		ResultSet rs = null;
		String retstr = "";

		StringBuffer  strSMRuleName;
		strSMRuleName = new StringBuffer();				

		StringBuffer  strSMRuleCuboidName;
		strSMRuleCuboidName = new StringBuffer();				

		StringBuffer  strUser;
		strUser = new StringBuffer();				

		StringBuffer  strIsActive;
		strIsActive = new StringBuffer();				

		try
		{
			// SUPERMERGE_RULE_ACCESS - Supermerge rule Access information
			ps = connection.prepareStatement(CALL_BW_SELECT_CUBOID);
			ps.setString(1, "SUPERMERGE_RULE_ACCESS");
			System.out.println("\n after setInt, before executeQuery..");
			rs = ps.executeQuery();
			System.out.println("\n before rs.next");
			while (rs.next())
			{
				strSMRuleName.append(rs.getString(1) + "|");
				strSMRuleCuboidName.append(rs.getString(2) + "|");
				strUser.append(rs.getString(3) + "|");
				strIsActive.append(rs.getString(4) + "|");
			}

			strSMRuleName.deleteCharAt(strSMRuleName.length()-1);
			strSMRuleCuboidName.deleteCharAt(strSMRuleCuboidName.length()-1);
			strUser.deleteCharAt(strUser.length()-1);
			strIsActive.deleteCharAt(strIsActive.length()-1);

			retstr =  strSMRuleCuboidName.toString() + "\n" + strSMRuleName.toString() + "\n" + strUser.toString()  + "\n" + strIsActive.toString() ;

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



	//Update SuperMerge Rule User Access settings.
	public static void UpdateSuperMergeUserAccessChanges(BoardwalkConnection bwcon, String superMergeRuleAccessUpdates) throws SystemException
	{
		PreparedStatement pstmt = null;
		PreparedStatement pstmt1 = null;
		CallableStatement cs  = null;
		try
		{
			System.out.println("Calling TRUNCATE TABLE BW_SUPER_MERGE_USER_ACCESS_MAPPING_TBL....");
			String TruncateSQL = "TRUNCATE TABLE BW_SUPERMERGE_USER_ACCESS";
			pstmt = connection.prepareStatement(TruncateSQL);
			pstmt.execute();
			//pstmt.close();

			String[] buffArray = superMergeRuleAccessUpdates.split(ContentDelimeter);
			System.out.println("buffArray length : " + buffArray.length );
			if (buffArray.length > 0)
			{
				System.out.println("before insert... inside bufArray.length construct ...");
				String query = "INSERT INTO BW_SUPERMERGE_USER_ACCESS (SUPERMERGE_RULE_NAME, SUPERMERGE_RULE_BRECT_NAME, USER_NAME, IS_ACTIVE) VALUES(?,?,?,?)";
				pstmt1 = connection.prepareStatement(query);

				for (int colAcc = 0; colAcc < buffArray.length; colAcc++)
				{
					String colAccStr = buffArray[colAcc];
					String[] updateArr = colAccStr.split("\\|");
					String superMergeRuleName = updateArr[0] ;
					String superMergeRuleBRectName = updateArr[1] ;
					String userLogin = updateArr[2] ;
					String action = updateArr[3] ;
					String isActive = "";
					System.out.println("userlogin : " + userLogin );
					System.out.println("superMergeRuleName : " + superMergeRuleName );
					System.out.println("superMergeRuleBRectName : " + superMergeRuleBRectName );
					System.out.println("action : " + action );
					
					if (action.toUpperCase().equals("ACTIVATE"))
						isActive = "ACTIVE";
					else if (action.toUpperCase().equals("DEACTIVATE"))
						isActive = "INACTIVE";					

					System.out.println("Inserting " + userLogin + " ... " + superMergeRuleName +  " ... " + isActive);
					pstmt1.setString(1, superMergeRuleName);
					pstmt1.setString(2, superMergeRuleBRectName);
					pstmt1.setString(3, userLogin);
					pstmt1.setString(4, isActive);
					pstmt1.addBatch();
				}

				System.out.println("Calling INSERT INTO BW_SUPER_MERGE_USER_ACCESS_MAPPING_TBL...");
				int[] rescnt = pstmt1.executeBatch();
				System.out.println("after insert...");
				//pstmt.close();
			}


			System.out.println("Calling CALL_BW_SUPERMERGE_RULE_ACCESS_LOAD....");
			
			cs = connection.prepareCall(CALL_BW_SUPERMERGE_RULE_ACCESS_LOAD);
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


	//Run Cuboid to SQL SuperMerge rule 
	public static void RunCuboidToSQLSuperMergeRule(BoardwalkConnection bwcon, String smRuleBRectName, String smRuleName, String UserName, String targetSQLTable   ) throws SystemException
	{

		CallableStatement cs  = null;
		try
		{
			System.out.println("Calling CALL_BW_SUPERMERGE_CUBOID_TO_SQL...") ;
			cs = connection.prepareCall(CALL_BW_SUPERMERGE_CUBOID_TO_SQL);
			cs.setString(1, smRuleBRectName);
			cs.setString(2, smRuleName);
			cs.setString(3, UserName);
			cs.setString(4, targetSQLTable);
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
			if ( cs != null )
				cs.close();
		    }
		    catch(SQLException sqlexception1) {
					System.out.println("throwing  sqlexception1");
			throw new SystemException(sqlexception1);
		    }
		}
	}


	public static void TruncateSQLTable(Connection connection, String sqlTableName) throws SystemException
	{
		PreparedStatement pstmt = null;
		PreparedStatement pstmt1 = null;
		CallableStatement cs  = null;
		try
		{
			System.out.println("Calling TRUNCATE TABLE ...." + sqlTableName);
			String TruncateSQL = "TRUNCATE TABLE " + sqlTableName ;
			pstmt = connection.prepareStatement(TruncateSQL);
			pstmt.execute();
			pstmt.close();
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
            }
            catch(SQLException sqlexception1) {
				System.out.println("throwing  sqlexception1");
                throw new SystemException(sqlexception1);
            }
		}
	}


	public static int GetCuboidId(Connection a_connection, String brectName) throws SQLException
	{

		CallableStatement callablestatement = null;
		int cuboidId = -1;
		try
		{

			callablestatement = a_connection.prepareCall(CALL_BW_GET_CUBOID_ID);
			callablestatement.setString(1, "BRECT");
			callablestatement.setString(2, brectName);
			callablestatement.setString(3, null);
			callablestatement.setString(4, null);
			callablestatement.setString(5, null);
			callablestatement.registerOutParameter(6, java.sql.Types.INTEGER);

			callablestatement.execute();
			cuboidId = callablestatement.getInt(6);

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
		return cuboidId;
	}



	public static String GetBRectResultSet(Connection connection, String bRectName) throws SystemException
	{
		PreparedStatement ps = null;
        ResultSet rs = null;
		StringBuffer retval = new StringBuffer();
        try
        {
			ps = connection.prepareStatement(CALL_BW_SELECT_BRECT);
			ps.setString(1, bRectName);

			System.out.println("before calling CALL_BW_SELECT_BRECT ");
			rs = ps.executeQuery();
			System.out.println("after executeQuery ");

			StringBuffer sbColNames = new StringBuffer();
			ResultSetMetaData rsmd = rs.getMetaData();

			System.out.println("after rs.getMetaData ");

			int count = rsmd.getColumnCount();
			System.out.println("rsmd.getColumnCount() : " + rsmd.getColumnCount() ); 
			for (int i = 1; i <= count; i++)
			{
				System.out.println("rsmd.getColumnName(i) : " + i + " - " + rsmd.getColumnName(i)); 
				sbColNames.append (rsmd.getColumnName(i) + Seperator);
			}
			sbColNames.deleteCharAt(sbColNames.length()-1);		// Removing last seperator

			System.out.println("sbColNames : " + sbColNames.toString());

			//initializing Stringbuffer array
			StringBuffer[] colSBArr = new StringBuffer[count];
			for( int iCol = 1; iCol <= count; iCol++ )
			{
				colSBArr[iCol-1] = new StringBuffer("");
			}

			System.out.println("after StringBuffer[] colSBArr " );

			while( rs.next()) {
				for( int iCol = 1; iCol <= count; iCol++ )
				{
					System.out.println("rs.getObject(iCol).toString() : " + ((rs.getObject(iCol) == null) ? "" : rs.getObject(iCol).toString())  )   ;
					colSBArr[iCol-1] = colSBArr[iCol-1].append( ((rs.getObject(iCol) == null) ? "" : rs.getObject(iCol).toString()) + Seperator);
				}
			}

			for( int iCol = 1; iCol <= count; iCol++ )
			{
				colSBArr[iCol-1].deleteCharAt(colSBArr[iCol-1].length()-1);		
				System.out.println("After Removing last seperator  " + colSBArr[iCol-1].toString());
			}


			System.out.println("after calling CALL_BW_SELECT_BRECT ");

			retval = retval.append(sbColNames.toString() + ContentDelimeter) ;
			for( int iCol = 0; iCol < count; iCol++ )
			{
				retval = retval.append(colSBArr[iCol].toString() + ContentDelimeter);
			}
			retval.deleteCharAt(retval.length()-1);
			System.out.println("retval : " + retval.toString());

        }
        catch(SQLException sqlexception)
        {
			System.out.println(sqlexception.toString());
            throw new SystemException(sqlexception);
        }
        catch(Exception e)
        {
			System.out.println(e.toString());
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
            catch(SQLException sqlexception1) {
				System.out.println("throwing  sqlexception1");
                throw new SystemException(sqlexception1);
            }
        }
		return retval.toString();
	}



	//Get Collaboration Tree created by User
	public static String GetCollaborationTreeCreatedbyUser(Connection connection, BoardwalkConnection bwcon, int userId ) throws BoardwalkException 
	{
		try
		{
			int collabId;
			String collabName;

			PreparedStatement ps = null;
			ResultSet rs = null;

			ps = connection.prepareStatement(CALL_BW_GET_COLLAB_CREATED_BY_USER);
			ps.setInt(1, userId);

			System.out.println("before calling CALL_BW_GET_COLLAB_CREATED_BY_USER ");
			rs = ps.executeQuery();
			System.out.println("after executeQuery ");

			sb = new StringBuffer();				

			while (rs.next())
			{
				String whiteBoard = "";
				String tableName = "";
				String Collabline="";
				int wbId;
				int tableId;

				collabName = rs.getString(1);
				collabId = rs.getInt(2);

				BoardwalkCollaborationNode bcn = BoardwalkCollaborationManager.getCollaborationTree(bwcon, collabId);
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
							Collabline = collabName + Seperator + whiteBoard + Seperator + tableName + Seperator ;
							Collabline = Collabline + collabId + Seperator + wbId + Seperator + tableId + Seperator ;
							Collabline = Collabline + collabName + "\\" + whiteBoard + "\\" + tableName ;

							BoardwalkTableContents btc = BoardwalkTableManager.getTableContents(bwcon, tableId);
							Vector cols = btc.getColumns();
							if (cols.isEmpty())
								Collabline = Collabline + Seperator + "Empty"  ;
							else
								Collabline = Collabline + Seperator + "Exported" ;

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


	//62  Get Columns of Source and Target TableID
	public static String GetColumnsOfSourceAndTarget(Connection connection, BoardwalkConnection bwcon, int sourceTableId, int targetTableId )			//throws BoardwalkException 
	{
		StringBuffer sb = new StringBuffer();
		try
		{
			BoardwalkTableContents btc = BoardwalkTableManager.getTableContents(bwcon, sourceTableId);
			Vector cols = btc.getColumns();
			Iterator colsi = cols.iterator();
			System.out.println("Columns in sourceTable : " + sourceTableId);
			while (colsi.hasNext())
			{
				BoardwalkColumn c = (BoardwalkColumn)colsi.next();
				sb.append(c.getName() + Seperator);
				System.out.println(c.getName());
			}
			if (sb.length() > 0)
			{
				System.out.println("Removing last character from SB");
				sb.deleteCharAt(sb.length()-1);
			}
			System.out.println("1...." + sb.toString());
			sb.append(ContentDelimeter);
			System.out.println("2...." + sb.toString());

			btc = BoardwalkTableManager.getTableContents(bwcon, targetTableId);
			cols = btc.getColumns();
			colsi = cols.iterator();
			System.out.println("Columns in targetTable : " + targetTableId);
			while (colsi.hasNext())
			{
				BoardwalkColumn c = (BoardwalkColumn)colsi.next();
				sb.append(c.getName() + Seperator);
				System.out.println(c.getName());
			}
			if (sb.length() > 0)
			{
				System.out.println("Removing last character from SB");
				sb.deleteCharAt(sb.length()-1);
			}
			System.out.println("3...." + sb.toString());
			//sb.append(ContentDelimeter);
			System.out.println("4...." + sb.toString());
		}
		catch(Exception e)
		{
			System.out.println("inside Exception xxxxxxxxxxxxxxxxxxxxxxxx");
			e.printStackTrace();
		}
		return sb.toString();
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
			tree = AdminTasks.GetNeighborhoodTree(bwcon, nhid);
			sbEnt.append("\n");
			sbEnt.append(tree);
        }
        return sbEnt.toString();
	}

	// Returns Cuboid wise Number of cells report
	public static String GetTableWiseCellsReport(Connection connection) 		//throws BoardwalkException 
	{

		PreparedStatement ps = null;
		ResultSet rs = null;
		String retstr = "";

		StringBuffer sb = new StringBuffer("");

		try
		{
			ps = connection.prepareStatement(CALL_BW_TABLEWISE_CELLS_REPORT);
			rs = ps.executeQuery();
			System.out.println("\n before rs.next");
			while (rs.next())
			{
				sb.append(rs.getString(2) + PIPE_CHAR + rs.getInt(1) + "\n");
			}
			if (sb.length() > 0)
			{			
					System.out.println("Removing last character from SB");
					sb.deleteCharAt(sb.length()-1);
					retstr = sb.toString() ;
			}
			else
			{
				System.out.println("No records in resuleset !!!");
				sb.append("FAILURE" + ContentDelimeter + "There are no cuboids present on Boardwalk Server.");
				System.out.println(sb.toString());
				retstr = sb.toString() ;
			}
			System.out.println("retstr : " + retstr);
		}
/*		catch (BoardwalkException bwe)
		{
			System.out.println("@@@@@@@@@@@@@@@@.........1");
			System.out.println("Boardwalk error code = " + bwe.getErrorCode());
			
			BoardwalkMessage bwmsg= new BoardwalkMessages().getBoardwalkMessage( bwe.getErrorCode() );

			System.out.println("Boardwalk message = " + bwmsg.getCause());
			System.out.println("Boardwalk potentioal solution = " + bwmsg.getPotentialSolution());
			retstr = "FAILURE" + ContentDelimeter + bwe.getErrorCode() + Seperator + bwmsg.getCause() + "\n" + bwmsg.getPotentialSolution() + ContentDelimeter;
		}
*/
		catch (SQLException sql1)
		{
			System.out.println(sql1.toString());
			retstr = "FAILURE" + ContentDelimeter +  "Could not generate Tablewise Cell Report on Server.  Please contact Boardwalk Administrator." + ContentDelimeter;
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
//				throw new SystemException(sql2);
			}
		}
		return retstr;
	}


	//Returns Neighborhood Tree of assigned Neighborhood to Nh Administrator: 64
	public static String GetNeighborhoodTreeForNhAdministrator(Connection connection, BoardwalkConnection bwcon, String userName)	throws SystemException		//throws BoardwalkException 
	{
		PreparedStatement ps = null;
		ResultSet rs = null;
		String retstr = "";

		StringBuffer sb = new StringBuffer("");
		boolean NhAdminValid = false;
		int nhId = -1;

		try
		{
			ps = connection.prepareStatement(CALL_BW_SELECT_CUBOID);
			ps.setString(1, "__NH_ADMINISTRATORS");
			System.out.println("\n after setInt, before executeQuery..");
			rs = ps.executeQuery();
			System.out.println("\n before rs.next");
			while (rs.next())
			{
				if (rs.getString(1).equals(userName))
				{
					NhAdminValid = true;
					if (sb.toString().equals(""))
					{
						System.out.println("Appending Success to sb !!!");
						//sb.append("SUCCESS" + Seperator);
					}
					nhId = Integer.parseInt(rs.getString(3));
					String tree = "";
					tree = AdminTasks.GetNeighborhoodTree(bwcon, nhId);
					sb.append(tree + "\n" );
						//responseBuffer = "SUCCESS" + Seperator +  "The Neighborhood of " + nhId + "is \n" + tree + ContentDelimeter + nhId  ;
						//responseBuffer = "SUCCESS" + Seperator +  tree + ContentDelimeter + nhId  ;
				}
			}

			if (!NhAdminValid)
			{
				System.out.println("nh admin is not valid !!!");
				//throw new BoardwalkException( 10000 );
				sb.append("FAILURE" + ContentDelimeter + "The '" + userName + "' does not have Neighborhood Administrator previledge.");
				System.out.println(sb.toString());
			}
			else
			{
				if (sb.length() > 0)
				{
					System.out.println("Removing last character from SB");
					sb.deleteCharAt(sb.length()-1);
				}
			}

			retstr = sb.toString() ;

			System.out.println("retstr : " + retstr);

		}
		catch (BoardwalkException bwe)
		{
			System.out.println("@@@@@@@@@@@@@@@@.........1");
			System.out.println("Boardwalk error code = " + bwe.getErrorCode());
			
			BoardwalkMessage bwmsg= new BoardwalkMessages().getBoardwalkMessage( bwe.getErrorCode() );

			System.out.println("Boardwalk message = " + bwmsg.getCause());
			System.out.println("Boardwalk potentioal solution = " + bwmsg.getPotentialSolution());
			retstr = "FAILURE" + ContentDelimeter + bwe.getErrorCode() + Seperator + bwmsg.getCause() + "\n" + bwmsg.getPotentialSolution() + ContentDelimeter;
		}

		catch (SQLException sql1)
		{
			System.out.println(sql1.toString());
			retstr = "FAILURE" + ContentDelimeter +  "__NH_ADMINISTRATORS is not found on Server.  Please contact Boardwalk Administrator." + ContentDelimeter;
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


	//Check if User is a SuperUser. Returns True or False
	public static boolean IsUserSuperUser(Connection connection, String userName)	throws SystemException		
	{
		PreparedStatement ps = null;
		ResultSet rs = null;

		StringBuffer sb = new StringBuffer("");
		boolean superUserValid = false;

		try
		{
			ps = connection.prepareStatement(CALL_BW_SELECT_CUBOID);
			ps.setString(1, "__SUPER_USERS");
			System.out.println("\n after setInt, before executeQuery..");
			rs = ps.executeQuery();
			System.out.println("\n before rs.next");
			while (rs.next())
			{
				if (rs.getString(1).equals(userName))
				{
					superUserValid = true;
					break;
				}
			}
			System.out.println("User is SuperUser : " + superUserValid );
		}
		catch (SQLException sql1)
		{
			System.out.println(sql1.toString());
			return superUserValid;
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
		return superUserValid;
	}




	//Check if User is a Neighborhood Administrator. Returns True or False
	public static boolean IsUserNhAdministrator(Connection connection, String userName)	throws SystemException		
	{
		PreparedStatement ps = null;
		ResultSet rs = null;

		StringBuffer sb = new StringBuffer("");
		boolean NhAdminValid = false;

		try
		{
			ps = connection.prepareStatement(CALL_BW_SELECT_CUBOID);
			ps.setString(1, "__NH_ADMINISTRATORS");
			System.out.println("\n after setInt, before executeQuery..");
			rs = ps.executeQuery();
			System.out.println("\n before rs.next");
			while (rs.next())
			{
				if (rs.getString(1).equals(userName))
				{
					NhAdminValid = true;
					break;
				}
			}
			System.out.println("User is NH Administrator : " + NhAdminValid );
		}
		catch (SQLException sql1)
		{
			System.out.println(sql1.toString());
			return NhAdminValid;
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
		return NhAdminValid;
	}




	//Check if User can Link Import. Returns True or False
	public static boolean CanUserLinkImport(Connection connection, String userName)	throws SystemException		
	{
		PreparedStatement ps = null;
		ResultSet rs = null;

		StringBuffer sb = new StringBuffer("");
		boolean canUserLinkImport = false;

		try
		{
			ps = connection.prepareStatement(CALL_BW_SELECT_CUBOID);
			ps.setString(1, "__LINK_IMPORT_USERS");
			System.out.println("\n after setInt, before executeQuery..");
			rs = ps.executeQuery();
			System.out.println("\n before rs.next");
			while (rs.next())
			{
				if (rs.getString(1).equals(userName))
				{
					canUserLinkImport = true;
					break;
				}
			}
			System.out.println("Can User Link Import : " + canUserLinkImport );
		}
		catch (SQLException sql1)
		{
			System.out.println(sql1.toString());
			return canUserLinkImport;
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
		return canUserLinkImport;
	}


	//Check if User can Link Export. Returns True or False
	public static boolean CanUserLinkExport(Connection connection, String userName)	throws SystemException		
	{
		PreparedStatement ps = null;
		ResultSet rs = null;

		StringBuffer sb = new StringBuffer("");
		boolean canUserLinkExport = false;

		try
		{
			ps = connection.prepareStatement(CALL_BW_SELECT_CUBOID);
			ps.setString(1, "__LINK_EXPORT_USERS");
			System.out.println("\n after setInt, before executeQuery..");
			rs = ps.executeQuery();
			System.out.println("\n before rs.next");
			while (rs.next())
			{
				if (rs.getString(1).equals(userName))
				{
					canUserLinkExport = true;
					break;
				}
			}
			System.out.println("Can User Link Export : " + canUserLinkExport );
		}
		catch (SQLException sql1)
		{
			System.out.println(sql1.toString());
			return canUserLinkExport;
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
		return canUserLinkExport;
	}



	//Added by Rahul Varadkar on 17-January-2016
	public static String Get__CollabStructure(BoardwalkConnection bwcon) throws SystemException
	{
		PreparedStatement ps = null;
		ResultSet rs = null;
		String retstr = "";

		StringBuffer  strNH0 = new StringBuffer();
		StringBuffer strNH1 = new StringBuffer();
		StringBuffer strNH2 = new StringBuffer();
		StringBuffer  strNH3 = new StringBuffer();
		StringBuffer strCollab = new StringBuffer();
		StringBuffer strWb = new StringBuffer();
		StringBuffer strCuboid = new StringBuffer();
		StringBuffer strRowCount = new StringBuffer();
		StringBuffer strColCount = new StringBuffer();

		StringBuffer strfname = new StringBuffer();
		StringBuffer strlname = new StringBuffer();

		StringBuffer strEmail = new StringBuffer();
		StringBuffer strExtEmail = new StringBuffer();


		try
		{
			ps = connection.prepareStatement(CALL_BW_SELECT_CUBOID);
			ps.setString(1, "__COLLAB_STRUCTURE");
			System.out.println("\n after setInt, before executeQuery..");
			rs = ps.executeQuery();
			System.out.println("\n before rs.next");
			while (rs.next())
			{
				strNH0.append(rs.getString(1) + "|");
				strNH1.append(rs.getString(2) + "|");
				strNH2.append(rs.getString(3) + "|");
				strNH3.append(rs.getString(4) + "|");
				strCollab.append(rs.getString(5) + "|");
				strWb.append(rs.getString(6) + "|");
				strCuboid.append(rs.getString(7) + "|");
				strRowCount.append(rs.getString(8) + "|");
				strColCount.append(rs.getString(9) + "|");
				strfname.append(rs.getString(10) + "|");
				strlname.append(rs.getString(11) + "|");
				strEmail.append(rs.getString(12) + "|");
				strExtEmail.append(rs.getString(13) + "|");
			}
			strNH0.deleteCharAt(strNH0.length()-1);
			strNH1.deleteCharAt(strNH1.length()-1);
			strNH2.deleteCharAt(strNH2.length()-1);
			strNH3.deleteCharAt(strNH3.length()-1);
			strCollab.deleteCharAt(strCollab.length()-1);
			strWb.deleteCharAt(strWb.length()-1);
			strCuboid.deleteCharAt(strCuboid.length()-1);
			strRowCount.deleteCharAt(strRowCount.length()-1);
			strColCount.deleteCharAt(strColCount.length()-1);
			strfname.deleteCharAt(strfname.length()-1);
			strlname.deleteCharAt(strlname.length()-1);
			strEmail.deleteCharAt(strEmail.length()-1);
			strExtEmail.deleteCharAt(strExtEmail.length()-1);

			retstr = strNH0.toString() + "\n" + strNH1.toString() + "\n" +  strNH2.toString() + "\n" +  strNH3.toString() + "\n" +  strCollab.toString() + "\n" +  strWb.toString() + "\n" +  strCuboid.toString() + "\n" +  strRowCount.toString() + "\n" +  strColCount.toString() + "\n" +  strfname.toString() + "\n" +  strlname.toString() + "\n" +  strEmail.toString() + "\n" +  strExtEmail.toString();

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


	public static boolean Update__COLLAB_STRUCTURE(BoardwalkConnection bwcon) throws BoardwalkException
	{
		//CallableStatement callableStatement = null;
		PreparedStatement	prepstatement	= null;
		int tid;

		try
		{

			TransactionManager tm = null;
			tm = new TransactionManager(bwcon.getConnection(), bwcon.getUserId());
			tid = tm.startTransaction();

			prepstatement = connection.prepareStatement(CALL_BW_UPDATE_COLLAB_STRUCTURE);

			prepstatement.execute();

			System.out.println("after calling callableStatement.executeUpdate()......."); 
			tm.commitTransaction();

			return true;
		}
		catch (SQLException sql1)
		{
			sql1.printStackTrace();
			throw new BoardwalkException(10024);
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
				throw new BoardwalkException(10024);
			}
		}
	}


	public static String GetRolesFromRoleMaster(BoardwalkConnection bwcon) throws SystemException
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

	//added by rahul on 20-june-2017
	public static String GetColumnList(BoardwalkConnection bwcon, int sourceTableId)			//throws BoardwalkException 
	{
		StringBuffer sb = new StringBuffer();
		try
		{
			BoardwalkTableContents btc = BoardwalkTableManager.getTableContents(bwcon, sourceTableId);
			Vector cols = btc.getColumns();
			Iterator colsi = cols.iterator();
			System.out.println("Columns in sourceTable : " + sourceTableId);
			while (colsi.hasNext())
			{
				BoardwalkColumn c = (BoardwalkColumn)colsi.next();
				sb.append(c.getId() + "|" + c.getName() + "|" + c.getDefaultValue()  + "\n");
				System.out.println(c.getId() + "|" + c.getName() + "|" + c.getDefaultValue()  + "\n");
			}
			if (sb.length() > 0)
			{
				System.out.println("Removing last character from SB");
				sb.deleteCharAt(sb.length()-1);
			}
			System.out.println("1...." + sb.toString());

		}
		catch(Exception e)
		{
			System.out.println("inside GetColumnList Exception xxxxxxxxxxxxxxxxxxxxxxxx");
			e.printStackTrace();
		}
		return sb.toString();
	}

	//
	public static String GetSelectColumnList(Connection connection, int sourceTableId, String ColumnsList)			//throws BoardwalkException 
	{
		StringBuffer sbData = new StringBuffer();
		ResultSet rs = null;
		PreparedStatement stmt = null;

		try
		{
			stmt = connection.prepareStatement(CALL_BW_SM_C2S_PIVOT);
			stmt.setInt(1, sourceTableId );
			stmt.setString(2, ColumnsList );

			rs = stmt.executeQuery();

			String receivedTokens[] ;
			receivedTokens	= ColumnsList.split(",");

			System.out.println("receivedTokens.length  : " + receivedTokens.length );

			int noCols = receivedTokens.length + 1;			//1 is added for RowId

			while (rs.next())
			{
				for (int i=1; i <= noCols ; i++ )
				{
					sbData.append(rs.getString(i) + "|");
				}
				sbData.deleteCharAt(sbData.length()-1);
				sbData.append("\n");
			}
			sbData.deleteCharAt(sbData.length()-1);
		}
		catch(Exception e)
		{
			System.out.println("inside GetColumnList Exception xxxxxxxxxxxxxxxxxxxxxxxx");
			e.printStackTrace();
		}
		return sbData.toString();
	}



	public static String GetSelectFilterCuboidView(Connection connection, int sourceTableId, String ColumnsList, String filterCondition, String OrderbyClause)			//throws BoardwalkException 
	{
		StringBuffer sbData = new StringBuffer();
		ResultSet rs = null;
		PreparedStatement stmt = null;

		try
		{
			stmt = connection.prepareStatement(CALL_BW_VIEW_CUBOID_DATA_FILTER);
			stmt.setInt(1, sourceTableId );
			stmt.setString(2, ColumnsList );
			stmt.setString(3, filterCondition );
			stmt.setString(4, OrderbyClause );

			rs = stmt.executeQuery();

			String receivedTokens[] ;
			receivedTokens	= ColumnsList.split(",");

			System.out.println("receivedTokens.length  : " + receivedTokens.length );

			int noCols = receivedTokens.length + 1;			//1 is added for RowId

			while (rs.next())
			{
				for (int i=1; i <= noCols ; i++ )
				{
					sbData.append(rs.getString(i) + "|");
				}
				sbData.deleteCharAt(sbData.length()-1);
				sbData.append("\n");
			}
			sbData.deleteCharAt(sbData.length()-1);
		}
		catch(Exception e)
		{
			System.out.println("inside GetColumnList Exception xxxxxxxxxxxxxxxxxxxxxxxx");
			e.printStackTrace();
		}
		return sbData.toString();
	}



	// CREATE TABLE
	public static String CreateTableByUser(Connection connection, String userName, String pwd, String nhHierarchy, int collabId, int wbId, String tableName, String tableDesc) throws  BoardwalkException
	{
		String createTblBuffer =  null;
		try
		{
			int userId = -1;
			int memberId= -1;
			int nhid= -1;
			String nhName = null;

			userId = UserManager.authenticateUser(connection, userName, pwd);

			if ( userId > 0 )
			{
				System.out.println(" Hashtable memberships check before "+userId);
				Hashtable memberships  = UserManager.getMembershipsForUser( connection, userId );
				System.out.println(" Hashtable memberships check Aftr "+userId);
				Enumeration memberIds = memberships.keys();

				if (  memberships.size() == 1 )
				{
					memberId =((Integer) memberIds.nextElement()).intValue();
					System.out.println("AdminTasks.CreateTableByUser: Single Member Id: "+memberId);
					
					nhid =((Member) memberships.get( new Integer(memberId) )).getNeighborhoodId();
					nhName = ((Member) memberships.get( new Integer(memberId) )).getNeighborhoodName();

					//commitResponseBuffer("Success:" + userId + Seperator + memberId + Seperator + nhid + Seperator + nhName + Seperator , response);
					System.out.println("status : Success");
					System.out.println("userId :" + userId);
					System.out.println("memberId : " + memberId);
					System.out.println("nhid : " + nhid);
					System.out.println("nhName : " + nhName);
					
					System.out.println("Success:" + userId + Seperator + memberId + Seperator + nhid + Seperator + nhName + Seperator);
				}
				else if (  memberships.size() > 1 )
				{
					//Added to get the Membership ID in case of Multiple Membership
					memberId = UserManager.checkMembershipStatus(connection, userId, "", nhHierarchy, -1);
					System.out.println("AdminTasks.CreateTableByUser: Multiple Member Id: "+memberId);

					if (memberId != -1)
					{
						nhid =((Member) memberships.get(new Integer(memberId))).getNeighborhoodId();
						nhName = ((Member) memberships.get(new Integer(memberId))).getNeighborhoodName();
						
						//commitResponseBuffer("Success:" + userId + Seperator + memberId + Seperator + nhid + Seperator + nhName + Seperator, response);
						System.out.println("status : Success");
						System.out.println("userId :" + userId);
						System.out.println("memberId : " + memberId);
						System.out.println("nhid : " + nhid);
						System.out.println("nhName : " + nhName);
						System.out.println("Success:" + userId + Seperator + memberId + Seperator + nhid + Seperator + nhName + Seperator);
					}
					else
					{
						BoardwalkMessage bwmsg= new BoardwalkMessages().getBoardwalkMessage( 11007 );
						//commitResponseBuffer("failure" + xlService.ContentDelimeter + bwmsg.getCause() + "," + bwmsg.getPotentialSolution(), response );
						System.out.println("status : failure");
						System.out.println("Cause" + bwmsg.getCause());
						System.out.println("PotentialSolution" + bwmsg.getPotentialSolution());
						createTblBuffer = "FAILURE" + ContentDelimeter +  bwmsg.getCause() + ", " + bwmsg.getPotentialSolution();
						return createTblBuffer;
					}
				}
				else if (  memberships.size() == 0 )
				{
					// User isn't a member of any neighborhood ...if the refering URL allows access to public users then we can show it
					// if  not the user has to join a relevant neighborhood
					 //servletOut.println("Success:"+userId+Seperator);
					BoardwalkMessage bwmsg= new BoardwalkMessages().getBoardwalkMessage( 11001 );
					//commitResponseBuffer("failure"+ xlService.ContentDelimeter+ bwmsg.getCause() + "," + bwmsg.getPotentialSolution(), response );
					System.out.println("status : failure");
					System.out.println("Cause" + bwmsg.getCause());
					System.out.println("PotentialSolution" + bwmsg.getPotentialSolution());
					createTblBuffer = "FAILURE" + ContentDelimeter +  bwmsg.getCause() + ", " + bwmsg.getPotentialSolution();
					return createTblBuffer;
				}

	            bwcon = BoardwalkConnectionManager.getBoardwalkConnection(connection, userName, pwd, memberId);

				int tableId = BoardwalkTableManager.createTable(bwcon, collabId, wbId, tableName, tableDesc);
				if (tableId == -1)
				{
					BoardwalkMessage bwmsg= new BoardwalkMessages().getBoardwalkMessage( 10012 );
					//commitResponseBuffer("failure"+ xlService.ContentDelimeter+ bwmsg.getCause() + "," + bwmsg.getPotentialSolution(), response );
					System.out.println("status : failure");
					System.out.println("Cause" + bwmsg.getCause());
					System.out.println("PotentialSolution" + bwmsg.getPotentialSolution());
					createTblBuffer = "FAILURE" + ContentDelimeter +  bwmsg.getCause() + ", " + bwmsg.getPotentialSolution();
				}
				else
				{
					createTblBuffer =  "The Cuboid '" + tableName + " is successfully created on Server with CuboidId = " + tableId;
				}
				return createTblBuffer ;

			}
			else
			{
				BoardwalkMessage bwmsg = null;
				System.out.println(" Check here userId >>>>>>>>>"+userId);

				if (userId == -1)
				{
					bwmsg= new BoardwalkMessages().getBoardwalkMessage( 11004 );
				}
				if (userId == 0)
				{
					bwmsg= new BoardwalkMessages().getBoardwalkMessage( 11006 );
				}

				//commitResponseBuffer("failure"+ xlService.ContentDelimeter+ bwmsg.getCause() + "," + bwmsg.getPotentialSolution(),response);
				System.out.println("status : failure");
				System.out.println("Cause : " + bwmsg.getCause());
				System.out.println("PotentialSolution:" +bwmsg.getPotentialSolution());
				createTblBuffer = "FAILURE" + ContentDelimeter +  bwmsg.getCause() + ", " + bwmsg.getPotentialSolution();
				return createTblBuffer;
			}
		}
		catch ( Exception e )
		{
		   e.printStackTrace();
		}

		return createTblBuffer ;

	}



	//Added by Rahul Varadkar on 13-February-2018 
	public static String LinkImportTable(Connection connection, String userName, String pwd, String nhHierarchy, int tableId)			//throws BoardwalkException 
	{
		String LinkImportBuffer = null;
		try
		{
			int userId = -1;
			int memberId= -1;
			int nhid= -1;
			int baselineId = -1;
			String view = null;
			int mode = -1;
			String nhName = null;
			// Error vector to all the Exceptions
			Vector xlErrorCells = new Vector();

			userId = UserManager.authenticateUser(connection, userName, pwd);

			if ( userId > 0 )
			{
				System.out.println(" Hashtable memberships check before "+userId);
				Hashtable memberships  = UserManager.getMembershipsForUser( connection, userId );
				System.out.println(" Hashtable memberships check Aftr "+userId);
				Enumeration memberIds = memberships.keys();

				if (  memberships.size() == 1 )
				{

					memberId =((Integer) memberIds.nextElement()).intValue();
					System.out.println("AdminTasks.LinkImportTable: Single Member Id: "+memberId);
					
					nhid =((Member) memberships.get( new Integer(memberId) )).getNeighborhoodId();
					nhName = ((Member) memberships.get( new Integer(memberId) )).getNeighborhoodName();

					//commitResponseBuffer("Success:" + userId + Seperator + memberId + Seperator + nhid + Seperator + nhName + Seperator , response);
					System.out.println("status : Success");
					System.out.println("userId :" + userId);
					System.out.println("memberId : " + memberId);
					System.out.println("nhid : " + nhid);
					System.out.println("nhName : " + nhName);
					
					System.out.println("Success:" + userId + Seperator + memberId + Seperator + nhid + Seperator + nhName + Seperator);
				}
				else
				if (  memberships.size() > 1 )
				{
					//Added to get the Membership ID in case of Multiple Membership
					memberId = UserManager.checkMembershipStatus(connection, userId, "", nhHierarchy, -1);
					System.out.println("AdminTasks.LinkImportTable: Multiple Member Id: "+memberId);

					if (memberId != -1)
					{
						nhid =((Member) memberships.get(new Integer(memberId))).getNeighborhoodId();
						nhName = ((Member) memberships.get(new Integer(memberId))).getNeighborhoodName();
						
						//commitResponseBuffer("Success:" + userId + Seperator + memberId + Seperator + nhid + Seperator + nhName + Seperator, response);
						System.out.println("status : Success");
						System.out.println("userId :" + userId);
						System.out.println("memberId : " + memberId);
						System.out.println("nhid : " + nhid);
						System.out.println("nhName : " + nhName);
						System.out.println("Success:" + userId + Seperator + memberId + Seperator + nhid + Seperator + nhName + Seperator);
					}
					else
					{
						BoardwalkMessage bwmsg= new BoardwalkMessages().getBoardwalkMessage( 11007 );
						//commitResponseBuffer("failure" + xlService.ContentDelimeter + bwmsg.getCause() + "," + bwmsg.getPotentialSolution(), response );
						System.out.println("status : failure");
						System.out.println("Cause" + bwmsg.getCause());
						System.out.println("PotentialSolution" + bwmsg.getPotentialSolution());
						LinkImportBuffer = "FAILURE" + ContentDelimeter +  bwmsg.getCause() + ", " + bwmsg.getPotentialSolution();
						return LinkImportBuffer;
					}
				}
				else
				if (  memberships.size() == 0 )
				{
					// User isn't a member of any neighborhood ...if the refering URL allows access to public users then we can show it
					// if  not the user has to join a relevant neighborhood
					 //servletOut.println("Success:"+userId+Seperator);
					BoardwalkMessage bwmsg= new BoardwalkMessages().getBoardwalkMessage( 11001 );
					//commitResponseBuffer("failure"+ xlService.ContentDelimeter+ bwmsg.getCause() + "," + bwmsg.getPotentialSolution(), response );
					System.out.println("status : failure");
					System.out.println("Cause" + bwmsg.getCause());
					System.out.println("PotentialSolution" + bwmsg.getPotentialSolution());
					LinkImportBuffer = "FAILURE" + ContentDelimeter +  bwmsg.getCause() + ", " + bwmsg.getPotentialSolution();
					return LinkImportBuffer;
				}

				//Access control checks
				TableInfo tinfo = TableManager.getTableInfo(connection, userId, tableId);
				TableAccessList ftal = TableViewManagerJsonAPI.getSuggestedAccess(connection, tinfo, userId, memberId, nhid);

				if (view == null || view.trim().equals(""))
				{
					view = ftal.getSuggestedViewPreferenceBasedOnAccess();
					System.out.println("Suggested view pref = " + view);
					if(view == null)
						view = "None";
				}
				// Check access control :: TBD
				int raccess = 1;
				int ACLFromDB = ftal.getACL();
				TableAccessRequest wAccess = new TableAccessRequest(tableId, view, true);
				int wACL = wAccess.getACL();
				int awACL = wACL & ACLFromDB;
				if (awACL == wACL)
				{
					raccess = 2;
					System.out.println("Rows have write access");
				}
				else
				{
					System.out.println("Rows are readonly");
				}

				// Check and see if the user has atleast the read access
				// if he does not have read access then throw exception.

			// authenticate the user
/*			Member memberObj = UserManager.authenticateMember(connection, userName,userPassword, memberId);
			if (memberObj == null)
			{
				System.out.println("Authentication failed for user : " + userName);
				responseBuffer = "Failure";//TBD : Description of the Error
				xlErrorCells.add( new xlErrorNew( tableId, 0, 0, 11004));
				throw new BoardwalkException(11004);
			}
			else
			{
				System.out.println("Authentication succeeded for user : " + userName);
				nhid = memberObj.getNeighborhoodId();
			
			}

			System.out.println("Time to authenticate user = " + getElapsedTime());
*/
			// This happens only when the user has no access to the said table
			// But in this case the user does not have provision to select the table
				if(view.equals("None"))
				{
					xlErrorCells.add( new xlErrorNew(tableId, 0, 0, 10005));
					throw new BoardwalkException(11005);
				}

				// get the tableBuffer
				LinkImportBuffer = TableViewManagerJsonAPI.getTableBuffer(connection, tableId, userId, memberId, nhid, baselineId, view, mode);
				//System.out.println Time to getTableBuffer
				//System.out.println("Time to getTableBuffer  = " + getElapsedTime());
/////////////////////////////
			}
			else
			{
				BoardwalkMessage bwmsg = null;
				System.out.println(" Check here userId >>>>>>>>>"+userId);

				if (userId == -1)
				{
					bwmsg= new BoardwalkMessages().getBoardwalkMessage( 11004 );
				}
				if (userId == 0)
				{
					bwmsg= new BoardwalkMessages().getBoardwalkMessage( 11006 );
				}

				//commitResponseBuffer("failure"+ xlService.ContentDelimeter+ bwmsg.getCause() + "," + bwmsg.getPotentialSolution(),response);
				System.out.println("status : failure");
				System.out.println("Cause : " + bwmsg.getCause());
				System.out.println("PotentialSolution:" +bwmsg.getPotentialSolution());
				LinkImportBuffer = "FAILURE" + ContentDelimeter +  bwmsg.getCause() + ", " + bwmsg.getPotentialSolution();
				return LinkImportBuffer;
			}
		}
		catch ( Exception e )
		{
		   e.printStackTrace();
		}
		return LinkImportBuffer;
	}


	//Added by Rahul Varadkar on 21-March-2018 
	public static String submitTable(Connection connection, int userId, String userName, String pwd, String nhHierarchy, int memberId, int nhId, int tableId, int baselineId, String view, int importTid, int exportTid, int mode, int  synch, String cuboidRows[], String cuboidCols[], String strCuboidColNames[], String jsonCuboidCellDataArr, String asTxComment, int isCritical, int criticalLevel, int numColumns, int numRows)
	{
		String submitBuffer = null;
		String failureReason = "";

		// Error vector to all the Exceptions
		Vector xlErrorCells = new Vector();
		// access variables
		boolean canAddRows = false;
		boolean canDeleteRows = false;
		boolean canAdministerColumns = false;
		StringBuffer newRowBuffer = new StringBuffer();
		StringBuffer newColBuffer = new StringBuffer();
		// access filter
		// see if there is a criterea table associated with this table
		int criteriaTableId = -1;
		int tid;
		// column access
		HashMap accCols = new HashMap();
		int defaultAccess = 2;
		HashMap colCellAccess = new HashMap();
		HashMap accessQueryXrowSet = new HashMap();
		HashMap rowIdHash = new HashMap();
		Vector xlDeleteRows = new Vector();
		HashMap colIdHash = new HashMap();
		boolean RowsDeleted = false; // This will help in detecting if a row was deleted or not.
		boolean ColsDeleted = false; // This will help in detecting if a column was deleted or not.
		ArrayList columnIds = null;

		// default access
		boolean setDefaultAccess = false;
		boolean ExceptionAdministerColumns = false;
		boolean newColsAdded = false;

		ArrayList columnNames = null;
		Vector dcv = new Vector();

		columnIds = new ArrayList(numColumns);
		columnNames = new ArrayList(numColumns);

	    //Added by Rahul Varadkar on  09-April-2018
	    JSONObject jsonSubmitResponse = new JSONObject();
	    JSONArray jsonNewRows = new JSONArray();	
		JSONObject jsonNewRow = new JSONObject();		
	    JSONArray jsonNewCols = new JSONArray();	
		JSONArray jsonNewCol = new JSONArray();		
		//Added by Rahul Varadkar on  09-April-2018			        

		StringBuffer responseToUpdate = new StringBuffer();
		String responseBuffer = null;

		// Get the database connection
		TransactionManager tm = null;
		PreparedStatement stmt = null;

		int MAX_RETRY_ATTEMPTS = 5;
		int RETRY_WAIT_TIME_MIN = 1000;
		int RETRY_WAIT_TIME_MAX = 3000;
		
		try
		{
		//	MAX_RETRY_ATTEMPTS = Integer.parseInt(getServletConfig().getInitParameter("MAX_RETRY_ATTEMPTS"));
		//	RETRY_WAIT_TIME_MIN = Integer.parseInt(getServletConfig().getInitParameter("RETRY_WAIT_TIME_MIN"));
		//	RETRY_WAIT_TIME_MAX = Integer.parseInt(getServletConfig().getInitParameter("RETRY_WAIT_TIME_MAX"));
			System.out.println("MAX_RETRY_ATTEMPTS=" + MAX_RETRY_ATTEMPTS);
			System.out.println("RETRY_WAIT_TIME_MIN=" + RETRY_WAIT_TIME_MIN);
			System.out.println("RETRY_WAIT_TIME_MAX=" + RETRY_WAIT_TIME_MAX);
		}
		catch (Exception e)
		{
			System.out.println("Deadlock parameters not set. Using defaults...");
		}


		for (int ti = 0; ti < MAX_RETRY_ATTEMPTS; ti++)
		{
			try
			{
				//processSubmitHeader...............START		

				// authenticate the user
				Member memberObj = UserManager.authenticateMember(connection, userName, pwd, memberId);
				if (memberObj == null)
				{
					System.out.println("Authentication failed for user : " + userName);
					xlErrorCells.add( new xlErrorNew( tableId, 0, 0, 11005));
					throw new BoardwalkException(11005);
				}
				else
				{
					System.out.println("Authentication succeeded for user : " + userName);
					nhId = memberObj.getNeighborhoodId();
				}
				
				// try the old sheet check
				criteriaTableId = TableViewManagerJsonAPI.getCriteriaTable(connection, tableId, userId);
				System.out.println("Using criterea table id = " + criteriaTableId);
				
				if (!(view == null || view.trim().equals("") || view.equalsIgnoreCase("Latest")))
				{
					System.out.println ("View = " + view );
					String lsRowQuery = TableViewManagerJsonAPI.getRowQuery(connection, tableId, userId, criteriaTableId, true, view);
				}
				//oldSheetCheck  removed

				/*********	g2	27may11	skipped checksignificant servlet********/
				PreparedStatement preparedstatement1 = null;
				ResultSet resultset1 = null;
				// check if there are any critical updates since last import
				// Added a check on the users so that we do not process critical update of Same user
				int critTid1 = -1;
				String query1 = " SELECT MAX(BW_SIGNIFICANT_TXS.TX_ID)" +
							   " FROM BW_SIGNIFICANT_TXS " +
							   " WHERE " +
							   " BW_TBL_ID = ? AND BW_SIGNIFICANT_TXS.CREATED_BY <> " + userId +
							   " GROUP BY  BW_TBL_ID ";
				preparedstatement1 = connection.prepareStatement(query1);
				preparedstatement1.setInt(1, tableId);
				resultset1 = preparedstatement1.executeQuery();
				String lsResponseStr = null;
				
				if (resultset1.next())
				{
					critTid1 = resultset1.getInt(1);
				}
				preparedstatement1.close();
				preparedstatement1 = null;
				if (resultset1 != null)
				{
					resultset1.close();
					resultset1 = null;
				}

				if (importTid < critTid1)
				{
					System.out.println("Found Critical updates after the last import ----g2");
					//lsResponseStr = getSignificantUpdateIds(connection, tableId, importTid, userId, nhId, view, memberId);
					throw new BoardwalkException(12017);
				}
				System.out.println("No Critical updates Found after the last import ----g2");
				/*****************/
				
				
				// see if there is a criterea table associated with this table
				//criteriaTableId = TableViewManagerJsonAPI.getCriteriaTable(connection, tableId, userId);
				//System.out.println("Using criterea table id = " + criteriaTableId);

				//	Access control checks
				TableInfo tinfo = TableManager.getTableInfo(connection, userId, tableId);
				TableAccessList ftal = TableViewManagerJsonAPI.getSuggestedAccess(connection, tinfo, userId, memberId, nhId);
				if (view == null || view.trim().equals(""))
				{
					view = ftal.getSuggestedViewPreferenceBasedOnAccess();
					System.out.println("Suggested view pref = " + view);
					if (view == null)
					{
						xlErrorCells.add(new xlErrorNew(tableId, 0, 0, 10005));
						throw new BoardwalkException(10005);
					}
				}
				// Check access control :: TBD
				int raccess = 1;
				int ACLFromDB = ftal.getACL();
				TableAccessRequest wAccess = new TableAccessRequest(tableId, view, true);
				int wACL = wAccess.getACL();
				int awACL = wACL & ACLFromDB;

				canAddRows				= ftal.canAddRow();
				canDeleteRows			= ftal.canDeleteRow();
				canAdministerColumns	= ftal.canAdministerColumn();

				// No Access to Table
				if ( awACL != wACL && canAddRows == false && canDeleteRows == false && canAdministerColumns == false)
				{
					xlErrorCells.add( new xlErrorNew(tableId, 0, 0, 10005));
					throw new BoardwalkException(10005);
				}
				
				// see if there is a criterea table associated with this table
				criteriaTableId = TableViewManagerJsonAPI.getCriteriaTable(connection, tableId, userId);
				System.out.println("Using criterea table id = " + criteriaTableId);
				int accessTableId = TableViewManagerJsonAPI.getAccessTable(connection, tableId, userId);
				if (accessTableId > 0)
				{
					Integer defAccess = new Integer(2);
					colCellAccess = TableViewManagerJsonAPI.getColumnAcccess(connection, tableId, accessTableId, userId);
					Iterator columnConditionalAccessIter = colCellAccess.keySet().iterator();
					while (columnConditionalAccessIter.hasNext())
					{
						Integer colId = (Integer) columnConditionalAccessIter.next();
						if (colCellAccess.get(colId) instanceof String){
							String accessString = (String) colCellAccess.get(colId);
							Pattern pattern = Pattern.compile("(\\d)(\\?.*)");
							Matcher matcher = pattern.matcher(accessString);
							if(matcher.matches())
							{
								int access = Integer.parseInt(matcher.group(1));
								String accessInstr = matcher.group(2);
								System.out.println("column acess for colid = " + colId + " is " + access +
										" if row matches accessQuery = " + accessInstr);

								HashSet rowSet = (HashSet) accessQueryXrowSet.get(accessInstr);
								if (rowSet == null)
								{
									String rowQuery = TableViewManagerJsonAPI.getRowQuery(connection, 
											TableViewManagerJsonAPI.getCriteriaForDynamicView(accessInstr), tableId, true);
				
									stmt = connection.prepareStatement(rowQuery);
									ResultSet rs = stmt.executeQuery();
									while (rs.next())
									{
										int rowId = rs.getInt(1);
										if (rowSet != null) {
											rowSet.add(new Integer(rowId));
										}
										else
										{
											rowSet = new HashSet();
											rowSet.add(new Integer(rowId));
											accessQueryXrowSet.put(accessInstr, rowSet);
										}
									}

									System.out.println("rowSet = " + rowSet);
								}
							}
						}
					}
					defaultAccess = (Integer) colCellAccess.get(new Integer(-1));
					System.out.println("processHeader():defaultAccess = " + defaultAccess);
				}

				//processSubmitHeader...............END		

				tm = new TransactionManager(connection, userId);
				tid = tm.startTransaction("Export Changes on table id = " + tableId, asTxComment);

				System.out.println("Export Changes on table id = " + tid);

				System.out.println("processColumns......................................................................................START");
				//processColumns......................................................................................START

				// default access
				setDefaultAccess = false;
				ExceptionAdministerColumns = false;
				newColsAdded = false;

				columnNames = null;
				dcv = new Vector();

				//int cuboidCols[], 
				//String strCuboidColNames[],

				columnIds = new ArrayList(numColumns);
				columnNames = new ArrayList(numColumns);
				//System.out.println("Column Names = " + sub);
				////String[] columnArrStr = sub.split(Seperator);
				//System.out.println("columnArrStr.length = " + columnArrStr.length);
				int prevColId = -1;
				//for (int cni = 0; cni < columnArrStr.length; cni = cni + 2)
				for (int cni = 0; cni < strCuboidColNames.length; cni = cni + 1)
				{
					int colId = -1;
					String colName = "";
					int pcOffset = 1;
					String colIdStr = null;
					//colIdStr = columnArrStr[cni];
					//colName = columnArrStr[cni + 1];

					colIdStr = cuboidCols[cni];
					colName = strCuboidColNames[cni];

					System.out.println("colIdStr = " + colIdStr);
					System.out.println("colName = " + colName);
					if (colIdStr.trim().equals(""))
					{
						if (canAdministerColumns)
						{
							try
							{
								TableManager.lockTableForUpdate(connection, tableId);
								System.out.println("Inserting column after col = " + prevColId + " with offset of " + pcOffset);
							}
							catch (Exception e)
							{
								xlErrorCells.add(new xlErrorNew(tableId, 0, 0, 12008));
								throw new BoardwalkException(12008);
							}
							try
							{
								colId = TableManager.createColumnXL(
														connection,
														tableId,
														colName, 		//columnArrStr[cni + 1],
														prevColId,
														pcOffset,
														tid
														);
								newColBuffer.append(new Integer(columnIds.size() + 1).toString() + Seperator + colId + Seperator);
								newColsAdded = true;

							    //Added by Rahul Varadkar on  09-April-2018
								jsonNewCol.add(columnIds.size()+1);
								jsonNewCol.add(colId);
								jsonNewCols.add(jsonNewCol);
							    //Added by Rahul Varadkar on  09-April-2018

								pcOffset++;
								prevColId = colId;
							}
							catch (Exception e)
							{
								// unique column violation
								xlErrorCells.add(new xlErrorNew(tableId, 0, prevColId, 12001));
								System.out.println("unique column violation");
								throw new BoardwalkException(12001);
							}
						}
						else
						{
							// User does not have access to add columns
							xlErrorCells.add(new xlErrorNew(tableId, 0, prevColId, 12010));
							ExceptionAdministerColumns = true;
							System.out.println("No access to add column");
						}
					}
					else
					{
						try
						{
							colId = Integer.parseInt(colIdStr);
							prevColId = colId;
							pcOffset = 1;
						}
						catch (NumberFormatException nfe)
						{
							colIdStr = colIdStr.substring(0, colIdStr.length() - 1);
							if (!(colIdStr.equals("")))
							{
								colId = Integer.parseInt(colIdStr);
								dcv.addElement(new Integer(colIdStr));
							}
						}
					}
					//System.out.println("Column Id = " + columnArrStr[cni] + " Name = " + columnArrStr[cni + 1]);
					columnIds.add(new Integer(colId));
					columnNames.add(colName);
					if (colIdHash.get(new Integer(colId)) == null)
					{
						colIdHash.put(new Integer(colId), new Integer(colId));
					}
				}

				if (newColsAdded == true)
				{
					if (isCritical <= 0)
					{
						if ((criticalLevel & (1 << 1)) == (1 << 1))
						{
							isCritical = 1;
							System.out.println("Transaction critical because columns added");
						}
					}
					TableManager.resequenceColumns(connection, tableId);
				}

				int ColTobeDeactivated = dcv.size();

				if (ColTobeDeactivated > 0 && canAdministerColumns == false)
				{
					// Throw exception
					// User does not have access to remove columns
					// Same message as of Add column will be shown here
					Iterator dcvi = dcv.iterator();
					while (dcvi.hasNext())
					{
						int dColId = ((Integer)dcvi.next()).intValue();
						xlErrorCells.add(new xlErrorNew(tableId, 0, dColId, 12010));
						if (ExceptionAdministerColumns == false)
							ExceptionAdministerColumns = true;
					}
					System.out.println("No access to delete column");
				}

				Iterator dcvi = dcv.iterator();
				while (dcvi.hasNext() && canAdministerColumns)
				{
					int dColId = ((Integer)dcvi.next()).intValue();
					if (colIdHash.get(new Integer(dColId)) != null)
						colIdHash.remove(new Integer(dColId));
					ColsDeleted = ColumnManager.deleteColumn(connection, dColId, tid);
				}
				if (ColTobeDeactivated > 0)
				{
					if (isCritical <= 0)
					{
						if ((criticalLevel & (1 << 2)) == (1 << 2))
						{
							isCritical = 1;
							System.out.println("Transaction critical because columns deleted");
						}
					}
				}
				System.out.println("processColumns......................................................................................END");
				//processColumns......................................................................................END

				//processRows............................................................................................................START
				System.out.println("processRows......................................................................................START");

				boolean newRowsAdded = false;

				//Vector xlErrorCells = new Vector();
				//Vector xlDeleteRows = new Vector();
				//HashMap rowIdHash = new HashMap();
				//HashMap colIdHash = new HashMap();

				// The Vectore which holds the Row ids marked for Deletion from client
				
				boolean ExceptionAddRows = false;
				boolean ExceptionDeleteRows = false;
				HashMap newRowHash = new HashMap();

				System.out.println("--------------Processing Rows--------------");
				//rowIds = new ArrayList(numRows);
				int rowId = -1;
				int prevRowId = -1;
				int prOffset = 1;
				int ri = 0;
				//int rj = sub.indexOf(Seperator);  // First substring //  JSON Changes
				int ccount = 0;
				String rowIdStr = null;
				Vector nrv = new Vector();
				boolean isDeletedRow = false;
				//int rowIdx = 0;
				//while (rj >= 0) //  JSON Changes
			//	while (ri < sub.length-1) //  JSON Changes
				while (ri < cuboidRows.length-1) 
				{
					rowId = -1;
					//  JSON Changes
					//rowIdStr = sub.substring(ri, rj);
					rowIdStr = cuboidRows[ri];
					System.out.println("rowIdStr="+rowIdStr);
					//  JSON Changes
					if (rowIdStr.trim().equals("0"))
					{
						System.out.println("rowIdStr.trim().equals(0).......found");
						if (canAddRows)
						{
							try
							{
								TableManager.lockTableForUpdate(connection, tableId);
							}
							catch (Exception sq)
							{
								xlErrorCells.add(new xlErrorNew(tableId, 0, 0, 12008));
								throw new BoardwalkException(12008);
							}

							try
							{
								newXLRow nr = new newXLRow(prevRowId, prOffset, ccount);
								nrv.addElement(nr);
								//rowId = TableManager.createRowXL(connection, tableId, "", prevRowId, prOffset, tid);
								//System.out.println("Added row with id = " + rowId);
								//newRowBuffer.append(new Integer(rowIds.size() + 1).toString() + Seperator + rowId + Seperator);
								newRowsAdded = true;
								prOffset++;
							}
							catch (Exception e)
							{
								xlErrorCells.add(new xlErrorNew(tableId, prevRowId, 0, 12001));
								throw new BoardwalkException(12001);
							}
						}
						else
						{
							xlErrorCells.add(new xlErrorNew(tableId, prevRowId, 0, 12012));
							ExceptionAddRows = true;
							System.out.println("No access to add rows");
						}
					}
					else
					{
						try
						{
							rowId = Integer.parseInt(rowIdStr);
							prevRowId = rowId;
							prOffset = 1;
						}
						catch (NumberFormatException numex)
						{
							System.out.println("In NumberFormatException : " + rowIdStr);
							rowIdStr = rowIdStr.substring(0, rowIdStr.length() - 1);
							if (!(rowIdStr.equals("")))
							{
								rowId = Integer.parseInt(rowIdStr);
								xlDeleteRows.addElement(new Integer(rowIdStr));
							}
						}
					}
					//rowIds.add(new Integer(rowId));
					if (rowId != -1 && rowIdHash.get(new Integer(ccount)) == null && isDeletedRow == false)
					{
						rowIdHash.put(new Integer(ccount), new Integer(rowId));
					}
					//rowIdx = rowIdx + 1;
					ccount++;
					//  JSON Changes
					//ri=rj+1;
					ri = ri + 1;
					//rj = sub.indexOf(Seperator, ri);   // Rest of substrings
					//System.out.println("rj 915="+rj);
				}		// End of While looop...
				//rowIdStr =sub.substring(ri);// Last substring
				rowIdStr = cuboidRows[ri];
				System.out.println("rowIdStr 919="+rowIdStr);
				//  JSON Changes

				if (rowIdStr.trim().equals(""))
				{
					if (canAddRows)
					{
						try
						{
							TableManager.lockTableForUpdate(connection, tableId);
						}
						catch (Exception sq)
						{
							xlErrorCells.add(new xlErrorNew(tableId, 0, 0, 12008));
							System.out.println("Table Lock Error");
							throw new BoardwalkException(12008);
						}
						try
						{
							newXLRow nr = new newXLRow(prevRowId, prOffset, ccount);
							nrv.addElement(nr);
							//rowId = TableManager.createRowXL(connection, tableId, "", prevRowId, prOffset, tid);
							//System.out.println("Added row with id = " + rowId);
							//newRowBuffer.append(new Integer(rowIds.size() + 1).toString() + Seperator + rowId + Seperator);
							newRowsAdded = true;
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
					}
					else
					{
						xlErrorCells.add(new xlErrorNew(tableId, prevRowId, 0, 12012));
						ExceptionAddRows = true;
						System.out.println("No access to add rows");
					}
				}
				else
				{
					//System.out.println("=============canAddRows Value of rowid============="+rowId);
					try
					{
						rowId = Integer.parseInt(rowIdStr);
						prevRowId = rowId;
						prOffset = 1;
					}
					catch (NumberFormatException exp)
					{
						System.out.println("rowIdStr 969="+rowIdStr); //  JSON Changes
						rowIdStr = rowIdStr.substring(0, rowIdStr.length() - 1);
						if (!(rowIdStr.equals("")))
						{
							rowId = Integer.parseInt(rowIdStr);
							xlDeleteRows.addElement(new Integer(rowIdStr));
						}
					}
				}
				//rowIds.add(new Integer(rowId));
				//System.out.println("Processing rowId = " + rowId);
				if (rowId != -1 && rowIdHash.get(new Integer(ccount)) == null && isDeletedRow == false)
				{
					rowIdHash.put(new Integer(ccount), new Integer(rowId));
				}
				// resequence the rows
				if (newRowsAdded == true)
				{
					System.out.println("newRowsAdded == true.....RPV");
					// add the new rows
					String query =
						"INSERT INTO BW_NEW_ROW " +
						"(PREV_ROW_ID, TX_ID, OFFSET) " +
						"VALUES " +
						"(?, ?, ?) ";
					stmt = connection.prepareStatement(query);
					Iterator nri = nrv.iterator();
					while (nri.hasNext())
					{
						newXLRow nr = (newXLRow)nri.next();
						stmt.setInt(1, nr.getPreviousRowId());
						stmt.setInt(2, tid);
						stmt.setInt(3, nr.getIndex());
						stmt.addBatch();
						System.out.println("calling query "+ query + " with PrevRowID:" + nr.getPreviousRowId() + ", tid:" + tid + ", nr.getIndex():" + nr.getIndex() );
					}
					int[] rescnt = stmt.executeBatch();
					stmt.clearBatch();
					stmt.close();
					stmt = null;

					System.out.println("Calling BW_CR_ROWS_XL(?,?,?)} -> tableId:" + tableId + ",userId:" + userId + ",tid:" + tid);

					query = "{CALL BW_CR_ROWS_XL(?,?,?)}";
					CallableStatement cstmt = connection.prepareCall(query);
					cstmt.setInt(1, tableId);
					cstmt.setInt(2, userId);
					cstmt.setInt(3, tid);
					cstmt.executeUpdate();
					cstmt.close();
					cstmt = null;


					System.out.println("SELECT BW_ROW.ID, BW_ROW.NAME FROM BW_ROW WHERE TX_ID = ?" + tid);

					// create the buffer
					query = "SELECT BW_ROW.ID, BW_ROW.NAME FROM BW_ROW WHERE TX_ID = ?";
					stmt = connection.prepareStatement(query);
					stmt.setInt(1, tid);
					ResultSet rs = stmt.executeQuery();
					while (rs.next())
					{
						jsonNewRow = new JSONObject();
						int rid = rs.getInt(1);
						int ridx = Integer.parseInt(rs.getString(2));
						rowIdHash.put(new Integer(ridx), new Integer(rid));
						//newRowBuffer.append(new Integer(rowIds.size() + 1).toString() + Seperator + rid + Seperator);
						newRowBuffer.append((ridx + 1) + Seperator + rid + Seperator);

						//Added by Rahul Varadkar on  09-April-2018				
						jsonNewRow.put("rowSequence", ridx+1);				
						jsonNewRow.put("rowId", rid);					
						jsonNewRows.add(jsonNewRow);			
						//Added by Rahul Varadkar on  09-April-2018				
					}

					stmt.close();
					rs.close();
					stmt = null;
					rs = null;

					// resequence the rows
					TableManager.resequenceRows(connection, tableId);

					if (isCritical <= 0)
					{
						System.out.println("critical level = " + criticalLevel);
						System.out.println("(1<<3) = " + (1 << 3));
						System.out.println("(criticalLevel & (1 << 3)) = " + (criticalLevel & (1 << 3)));
						if ((criticalLevel & (1 << 3)) == (1 << 3))
						{
							isCritical = 1;
							System.out.println("Transaction critical because rows added");
						}
					}
				}

				// delete the rows
				if (xlDeleteRows.size() > 0)
				{
					Iterator rowI = xlDeleteRows.iterator();
					System.out.println("canDeleteRows="+canDeleteRows+"----------------Deleting rows---------- " + xlDeleteRows.size());
					if (canDeleteRows == false)
					{
						// Throw exception
						// User does not have access to remove rows


						while (rowI.hasNext())
						{
							int delrowId = ((Integer)rowI.next()).intValue();
							xlErrorCells.add(new xlErrorNew(tableId, delrowId, 0, 12013));
							ExceptionDeleteRows = true;
						}
						System.out.println("No access to Delete rows");
					}
					else
					{
						// this will remove the row from the hash map here itself
						//while(rowI.hasNext())
						//{
						//    int delrowId = ((Integer)rowI.next()).intValue();
						//    if (rowIdHash.get(new Integer(delrowId)) != null)
						//    {
						//        rowIdHash.remove(new Integer(delrowId));
						//        System.out.println("Deleting rowid from rowIdHash : " + delrowId);
						//        System.out.println("rowidHash.size() = " + rowIdHash.size());
						//    }

						//}

						//			System.out.println("----------Inside Deactivate Row-----------"+delrowId);
						RowsDeleted = RowManager.deactivateRows(connection, xlDeleteRows, tid);
						if (isCritical <= 0)
						{
							if ((criticalLevel & (1 << 4)) == (1 << 4))
							{
								isCritical = 1;
								System.out.println("Transaction critical because rows deleted");
							}
						}
					}
				}
				//if (serverSideDeletedRows.size() > 0)
				//{
				//    Iterator rowI = serverSideDeletedRows.iterator();
				//    while(rowI.hasNext())
				//    {
				//        int delrowId = ((Integer)rowI.next()).intValue();
				//        if(rowIdHash.get(new Integer(delrowId)) != null)
				//            rowIdHash.remove(new Integer(delrowId));

				//    }
				//}

				//processRows............................................................................................................END
				System.out.println("processRows..........................................................................................END");


				System.out.println("processCells..........................................................................................START");
				//processCells.......................................................................................................START

				//Following is the sample jsonCuboidCellDataArr i.e. ChangedCellDaata
				//jsonCuboidCellDataArr = [{"RowSeq":0,"ChangeFlag":1,"CellVal":"1","ColSeq":5,"CellFmla":"1"},{"RowSeq":0,"ChangeFlag":1,"CellVal":"test","ColSeq":9,"CellFmla":"test"},{"RowSeq":1,"ChangeFlag":1,"CellVal":"dsf","ColSeq":4,"CellFmla":"dsf"},{"RowSeq":1,"ChangeFlag":1,"CellVal":"4","ColSeq":5,"CellFmla":"4"},{"RowSeq":1,"ChangeFlag":1,"CellVal":"5","ColSeq":6,"CellFmla":"5"},{"RowSeq":2,"ChangeFlag":1,"CellVal":"sd","ColSeq":0,"CellFmla":"sd"},{"RowSeq":2,"ChangeFlag":1,"CellVal":"dsf","ColSeq":1,"CellFmla":"dsf"},{"RowSeq":2,"ChangeFlag":1,"CellVal":"sd","ColSeq":2,"CellFmla":"sd"},{"RowSeq":2,"ChangeFlag":1,"CellVal":"df","ColSeq":3,"CellFmla":"df"},{"RowSeq":2,"ChangeFlag":1,"CellVal":null,"ColSeq":4,"CellFmla":null},{"RowSeq":2,"ChangeFlag":1,"CellVal":"sdffsd","ColSeq":5,"CellFmla":"sdffsd"},{"RowSeq":2,"ChangeFlag":1,"CellVal":"f","ColSeq":6,"CellFmla":"f"},{"RowSeq":2,"ChangeFlag":1,"CellVal":null,"ColSeq":7,"CellFmla":null},{"RowSeq":2,"ChangeFlag":1,"CellVal":"ffs","ColSeq":8,"CellFmla":"ffs"},{"RowSeq":2,"ChangeFlag":1,"CellVal":"fsd","ColSeq":9,"CellFmla":"fsd"},{"RowSeq":2,"ChangeFlag":1,"CellVal":null,"ColSeq":10,"CellFmla":null},{"RowSeq":2,"ChangeFlag":1,"CellVal":"f","ColSeq":11,"CellFmla":"f"},{"RowSeq":2,"ChangeFlag":1,"CellVal":"dsfds","ColSeq":12,"CellFmla":"dsfds"},{"RowSeq":2,"ChangeFlag":1,"CellVal":null,"ColSeq":13,"CellFmla":null},{"RowSeq":2,"ChangeFlag":1,"CellVal":"sdff","ColSeq":14,"CellFmla":"sdff"},{"RowSeq":2,"ChangeFlag":1,"CellVal":null,"ColSeq":15,"CellFmla":null},{"RowSeq":3,"ChangeFlag":1,"CellVal":"s","ColSeq":13,"CellFmla":"s"}]

		        JSONParser parser = new JSONParser();
		        Object parsedObj = parser.parse(jsonCuboidCellDataArr);

		        JSONArray cellDataChanges = (JSONArray) parsedObj;

            	System.out.println("cellDataChanges.size ->" + cellDataChanges.size());

				Iterator cellChangesIterator = cellDataChanges.iterator();

	            if  (cellDataChanges.size() > 0)
	            {
					ArrayList formulaIds = null;
					ArrayList strValIds = null;
					String formulaArrayAdd[] = null;
					String formulaArrayDel[] = null;
					int numCellsChanged = 0;
					//Vector xlErrorCells = new Vector();

					System.out.println("Updating cells in batch . Default access = " + defaultAccess);
					//System.out.println("Changed cell data = " + sub);
					String xlcellval = null;
					String xlFormula = null;
					int xlRowIdx = -1;
					int xlColIdx = -1;
					int xlRowId = -1;
					int xlColId = -1;
					int cellChangeFlag = 1;
					int ci = 0;
					//int cj = sub.indexOf(Seperator);  // First substring //  JSON Changes
					int cj = 0; //  JSON Changes
					ccount = 0;

					//int isCritical;
					//int criticalLevel;

					String query = " INSERT INTO BW_RC_STRING_VALUE VALUES(?, ?, ?, ?, ?, ?) ";

					stmt = connection.prepareStatement(query);

					int batchSize = 10000;
					int batchCounter = 0;

					//JSONObject cellChange ;

					//for (int i = 0; i < cellDataChanges.size(); i++) {
						//cellChange = (JSONObject) cellDataChanges.get(i); 
	
					while(cellChangesIterator.hasNext())
					{
					    String cellChangeEle = cellChangesIterator.next().toString();
					    System.out.println( "cellChangeEle ->" + cellChangeEle);
					    JSONObject jo = (JSONObject) parser.parse(cellChangeEle);

						xlRowIdx = Integer.parseInt(jo.get("RowSeq").toString());
						xlColIdx = Integer.parseInt(jo.get("ColSeq").toString());
						xlcellval = jo.get("CellVal") == null ? null : jo.get("CellVal").toString();
						xlFormula = jo.get("CellFmla")== null ? "" : jo.get("CellFmla").toString();
						cellChangeFlag = Integer.parseInt(jo.get("ChangeFlag").toString());

					    System.out.println("xlRowIdx ->" + xlRowIdx);
					    System.out.println("xlColIdx ->" + xlColIdx);
					    System.out.println("xlcellval ->" + xlcellval);
					    System.out.println("xlFormula ->" + xlFormula);
					    System.out.println("cellChangeFlag ->" + cellChangeFlag);
		
						if (xlFormula.indexOf("=") < 0)
						{
							xlFormula = null;
						}

						xlRowId = ((Integer)rowIdHash.get(new Integer(xlRowIdx))).intValue();
						xlColId = ((Integer)columnIds.get(xlColIdx)).intValue();
						System.out.println("xlRowId="+xlRowId+"xlColId="+xlColId); //  JSON Changes
						int ColAcess = defaultAccess; // assuming column access is implemented in client
						if (accCols.get(new Integer(xlColId)) != null)
							ColAcess = ((Integer)accCols.get(new Integer(xlColId))).intValue();

						// override access from access table
						if (colCellAccess != null && colCellAccess.size() > 0)
						{ 
							Object cA = colCellAccess.get(new Integer(xlColId));
							if (cA != null){
								if (cA instanceof Integer)
								{
									ColAcess = ((Integer)cA).intValue();
									System.out.println("column acess for colid = " + xlColId + " is " + ColAcess);
								}
								else
								{
									String accessString = (String)cA;
									System.out.println(accessString);
									Pattern pattern = Pattern.compile("(\\d)(\\?.*)");
									Matcher matcher = pattern.matcher(accessString);
									System.out.println("match count = " + matcher.groupCount());
									if(matcher.matches())
									{
										int access = Integer.parseInt(matcher.group(1));
										String accessInstr = matcher.group(2);
										System.out.println("column access for colid = " + xlColId + " is " + access +
												" if row matches accessQuery = " + accessInstr);
										System.out.println("Otherwise using defaultAccess = " + defaultAccess);
										System.out.println(accessQueryXrowSet.toString());
										if (((HashSet) accessQueryXrowSet.get(accessInstr)).contains(new Integer(xlRowId)))
										{
											ColAcess = access;
											System.out.println("Using access = " + ColAcess + "for cell with rowId = " + xlRowId + "matching condition " + accessInstr);
										}
									}
								}
							}
						}

						// If anything other change other than value change/formula change (row added, column added etc)
						if (cellChangeFlag > 2 && xlRowId > 0 && xlColId > 0)
						{
							stmt.setInt(1, xlRowId);
							stmt.setInt(2, xlColId);
							stmt.setString(3, xlcellval);
							stmt.setString(4, xlFormula);
							stmt.setInt(5, tid);
							stmt.setInt(6, cellChangeFlag);
							stmt.addBatch();
							numCellsChanged = numCellsChanged + 1;
							batchCounter = batchCounter + 1;
							if (batchCounter == batchSize)
							{
								int[] rescnt = stmt.executeBatch();
								stmt.clearBatch();
								batchCounter = 0;
							}

						}
						else if ((ColAcess == 2 && xlRowId > 0 && xlColId > 0) ||
								 (ColAcess == 1 && xlRowId > 0 && xlColId > 0 && xlFormula != null && cellChangeFlag == 1)) 
							// value or formula changed by user, cellChangeFlag = 1 or 2 ||
							// value changed by formula, cellChangeFlag = 1, access = 1
						{

							stmt.setInt(1, xlRowId);
							stmt.setInt(2, xlColId);
							stmt.setString(3, xlcellval);
							stmt.setString(4, xlFormula);
							stmt.setInt(5, tid);
							stmt.setInt(6, cellChangeFlag);
							stmt.addBatch();
							numCellsChanged = numCellsChanged + 1;
							batchCounter = batchCounter + 1;
							if (batchCounter == batchSize)
							{
								int[] rescnt = stmt.executeBatch();
								stmt.clearBatch();
								batchCounter = 0;
							}
						}
						else if (ColAcess == 0 || ColAcess == 1)
						{
							if (ColAcess == 1)
							{
								System.out.println("processCells():Cell access violation " +
										"rowId=" + xlRowId + " colId=" + xlColId +
										"value=" + xlcellval +
										"frmla=" + xlFormula +
										"cellChangeFlag=" + cellChangeFlag);
								if (xlRowId != -1)
									xlErrorCells.add(new xlErrorNew(tableId, xlRowId, xlColId, 12016));
							}
							else
							{
								System.out.println("New Column without Access Right Added");
								if (xlColId != -1 || xlRowId != -1)
									xlErrorCells.add(new xlErrorNew(tableId, xlRowId, xlColId, 12016));
							}
						}
						ccount++;
					}		// End of While loop

					if (batchCounter > 0) // the last batch
					{
						int[] rescnt = stmt.executeBatch();
						stmt.clearBatch();
						System.out.print(".");
					}
					stmt.close();
					stmt = null;
					query = null;
					if (numCellsChanged > 0)
					{
						if (isCritical <= 0)
						{
							if ((criticalLevel & (1 << 5)) == (1 << 5))
							{
								isCritical = 1;
								System.out.println("Transaction critical because cells changed");
							}
						}
					}
					// update the cells based on the rcsv table
					query = "{CALL BW_UPD_CELL_FROM_RCSV(?,?,?,?)}";

				}			//End OFF (cellDataChanges.size() > 0)

				//processCells.............END
				System.out.println("processCells..........................................................................................END");
				
				//  JSON Changes
				String cellColBuffer = null;

				//  JSON Changes
				String query = "{CALL BW_UPD_CELL_FROM_RCSV(?,?,?,?)}";
				CallableStatement cstmt = connection.prepareCall(query);
				cstmt.setInt(1, tid);
				cstmt.setInt(2, importTid);
				cstmt.setInt(3, tableId);
				cstmt.setInt(4, userId);
				int updCount = cstmt.executeUpdate();
				cstmt.close();
				cstmt = null;
				System.out.println("Update cells for table done. ");
				
				stmt = null;
				ResultSet rs = null;

				// filter consistency check
				if (criteriaTableId > 0)
				{
					HashSet accessibleRowsAfterSubmit = new HashSet();
					String lsRowQuery  = TableViewManagerJsonAPI.getRowQuery(connection, tableId, userId, criteriaTableId, true, "LATEST");

					stmt = connection.prepareStatement(lsRowQuery);
					rs = stmt.executeQuery();
					while (rs.next())
					{
						accessibleRowsAfterSubmit.add(new Integer(rs.getInt(1)));
					}
					
					Iterator rowIdsFromClientIter = rowIdHash.keySet().iterator();
					while (rowIdsFromClientIter.hasNext())
					{
						System.out.println ("xlExpoortChangesService::service(): xlDeleteRows = " + xlDeleteRows.toString());
						Integer rowIdFromClient = (Integer) rowIdHash.get(rowIdsFromClientIter.next());
						if (rowIdFromClient > 0 && !accessibleRowsAfterSubmit.contains(rowIdFromClient) 
								&& !xlDeleteRows.contains(rowIdFromClient)) // deleted rows will not be fetched
						{
							System.out.println("The row id = " + rowIdFromClient + " is not accessible for user");
							xlErrorCells.add(new xlErrorNew(tableId, rowIdFromClient.intValue(), 0, 12018));
							throw new BoardwalkException(12018, "Access Filter violation");
						}
					}
					rs.close();
					stmt.close();
				}

				System.out.println("xlExportChangesServiceJsonAPI: xlErrorCells.size() " + xlErrorCells.size());
				if (xlErrorCells.size() > 0)
				{
					throw new BoardwalkException(12011);
				}

				// if transaction is to be made critical
				if (isCritical > 0)
					tm.addSigTransaction(asTxComment, tableId, tid);

				tm.commitTransaction();

				ti = MAX_RETRY_ATTEMPTS;
				// create the response



				jsonSubmitResponse = new JSONObject();
		        jsonSubmitResponse.put("result", "Success");
		        jsonSubmitResponse.put("TxId", tid);
		        jsonSubmitResponse.put("NewRowsSequenceAndIds", jsonNewRows);
		        jsonSubmitResponse.put("NewColsSequenceAndIds", jsonNewCols);
		        jsonSubmitResponse.put("RowsDeleted", RowsDeleted);
		        jsonSubmitResponse.put("ColsDeleted", ColsDeleted);
		        jsonSubmitResponse.put("RowCount", rowIdHash.size());
		        jsonSubmitResponse.put("ColCount", colIdHash.size());

				System.out.println("jsonSubmitResponse : " + jsonSubmitResponse);

				responseToUpdate.append("Success" + ContentDelimeter + tid + ContentDelimeter);
				System.out.println("ResponseBufffer Line-1 ->" + "Success" + ContentDelimeter + tid + ContentDelimeter);

				// new rows
				responseToUpdate.append(newRowBuffer.toString() + ContentDelimeter);
				System.out.println("ResponseBufffer Line-2 [NewRows] ->" + newRowBuffer.toString() + ContentDelimeter);

				// new columns
				responseToUpdate.append(newColBuffer.toString() + ContentDelimeter);
				System.out.println("ResponseBufffer Line-3 [NewColumns] ->" + newColBuffer.toString() + ContentDelimeter);

				// info for delete column / row
				responseToUpdate.append(RowsDeleted + ContentDelimeter + ColsDeleted + ContentDelimeter);
				System.out.println("ResponseBufffer Line-4 [Delete Column/row] ->" +RowsDeleted + ContentDelimeter + ColsDeleted + ContentDelimeter);

				// number of rows and columns for WriteCacheFromSheet call
				responseToUpdate.append(rowIdHash.size() + ContentDelimeter + colIdHash.size() + ContentDelimeter);
				System.out.println("ResponseBufffer Line-5 [rowCount and ColumnCount] ->" + rowIdHash.size() + ContentDelimeter + colIdHash.size() + ContentDelimeter);


			}
			catch (SQLException sqe)
			{
				sqe.printStackTrace();
				// rollback the transaction
				try
				{
					if (tm != null)
						tm.rollbackTransaction();
				}
				catch (Exception e1)
				{
					e1.printStackTrace();
				}

				//deadlock exception
				if (sqe.getErrorCode() == 1205)
				{
					if (ti == MAX_RETRY_ATTEMPTS - 1)
					{
						failureReason = (new xlErrorNew(tableId, 0, 0, 13001)).buildTokenString();
						System.out.println("xlExportChangesServiceJsonAPI: Deadlock maximum attempts exhausted. Sending server busy message to client ");
					}
					else
					{
						System.out.println("xlExportChangesServiceJsonAPI: Deadlock Attempt number = " + (ti + 1) + " out of max = " + MAX_RETRY_ATTEMPTS);
						try
						{
							int sleepTime = RETRY_WAIT_TIME_MIN + (new Random()).nextInt(RETRY_WAIT_TIME_MAX - RETRY_WAIT_TIME_MIN);
							System.out.println("Sleeping for " + sleepTime + "ms");
							Thread.sleep(sleepTime);
						}
						catch (InterruptedException e2)
						{
							e2.printStackTrace();
						}
					}
				}
				else
				{
					ti = MAX_RETRY_ATTEMPTS; // dont try again
					System.out.println("submitTable SQLException sqe : dont't try again");
					failureReason = sqe.getMessage();
				}
			}
			catch (BoardwalkException bwe)
			{
				ti = MAX_RETRY_ATTEMPTS;
				System.out.println("submitTable BoardwalkException bwe : dont't try again");
				bwe.printStackTrace();
				if (xlErrorCells.size() <= 0)
				{
					xlErrorCells.add(new xlErrorNew(tableId, 0, 0, bwe.getErrorCode()));
				}
				StringBuffer errorBuffer = new StringBuffer();

				for (int errorIndex = 0; errorIndex < xlErrorCells.size(); errorIndex++)
				{
					xlErrorNew excelError = (xlErrorNew)(xlErrorCells.elementAt(errorIndex));
					errorBuffer.append(excelError.buildTokenString());
				}
				errorBuffer.append(Seperator);
				failureReason = errorBuffer.toString();
				try
				{
					// We check the null here because if the user has nop access what so ever then
					// Why create a transaction in the first place itself
					if (tm != null)
						tm.rollbackTransaction();
				}
				catch (Exception e1)
				{
					e1.printStackTrace();
				}

			}
			catch (Exception e) //unknown exeception
			{
				ti = MAX_RETRY_ATTEMPTS;
				System.out.println("submitTable Exception e -> unknown exeception: dont't try again");
				try
				{
					if (tm != null)
						tm.rollbackTransaction();
				}
				catch (Exception e1)
				{
					e1.printStackTrace();
				}
				failureReason = e.getMessage();
				e.printStackTrace();
			}

		}

		// The response
		if (failureReason.length() == 0)
		{
			//submitBuffer = responseToUpdate.toString();
			submitBuffer = jsonSubmitResponse.toString();
		}
		else
		{
			submitBuffer = "FAILURE" + ContentDelimeter + failureReason;
			System.out.println("AdminTask.SubmitTable..xlExportChangesServiceJsonAPI: failureReason = " + failureReason);
		}

		return  submitBuffer;
	}






	//Added by Rahul Varadkar on 20-February-2018 
	public static String refreshTable(Connection connection, int userId, String userName, String pwd, int memberId, int nhId, int tableId, int baselineId, String view, int importTid, int exportTid, int mode, int synch, String cuboidRows)			//throws BoardwalkException 
	{
		String refreshBuffer = null;
		String failureReason = "";
		Vector xlErrorCells = new Vector();

		// Get the database connection
		TransactionManager tm = null;
		PreparedStatement stmt = null;
		try
		{

			System.out.println("cuboidRows : " + cuboidRows);

			cuboidRows = cuboidRows.replace("[","");
			cuboidRows = cuboidRows.replace("]","");
			cuboidRows = cuboidRows.replace("\"","");

			String[] rowids =cuboidRows.split(",");

			System.out.println(Arrays.toString(rowids));
			
			HashMap localRowHash = new HashMap();
			//  JSON Changes		
			if(rowids.length > 1)
			{
				for (int ri = 0; ri < rowids.length; ri++)
				{
					String rowIdStr = rowids[ri];
					if (!rowIdStr.equalsIgnoreCase(""))
					{
						int rowId = -1;
						rowId = Integer.parseInt(rowIdStr);
						localRowHash.put(new Integer(rowId), new Integer(rowId));
					}
				}
			}
			//System.out.println("Time to create localRowHash from buffer = " + getElapsedTime());
			int maxTransactionId = importTid;

			// authenticate the user
			/* NOT REQUIRED SINCE NHID IS PASSED TO THIS FUNCTION */
			/*
			Member memberObj = UserManager.authenticateMember(connection, userName,pwd, memberId);
			if (memberObj == null)
			{
				System.out.println("Authentication failed for user : " + userName);
				responseBuffer = "Failure";//TBD : Description of the Error
				xlErrorCells.add( new xlErrorNew( tableId, 0, 0, 11005));
				throw new BoardwalkException(11005);
			}
			else
			{
				System.out.println("Authentication succeeded for user : " + userName);
				nhId = memberObj.getNeighborhoodId();
			}

			*/
			//System.out.println("Time to authenticate user = " + getElapsedTime());

			// Check access control :: TBD
			TableInfo tinfo = TableManager.getTableInfo(connection, userId, tableId);
			TableAccessList ftal = TableViewManagerJsonAPI.getSuggestedAccess(connection, tinfo, userId, memberId, nhId);
			if (view == null || view.trim().equals(""))
			{
				view = ftal.getSuggestedViewPreferenceBasedOnAccess();
				System.out.println("Suggested view pref = " + view);
				if (view == null)
				{
					xlErrorCells.add(new xlErrorNew(tableId, 0, 0, 10005));
					throw new BoardwalkException(10005);
				}
			}
			// Check access control :: TBD
			int raccess = 1;
			int ACLFromDB = ftal.getACL();
			TableAccessRequest wAccess = new TableAccessRequest(tableId, view, true);
			int wACL = wAccess.getACL();
			int awACL = wACL & ACLFromDB;
			if (awACL == wACL)
			{
				raccess = 2;
				System.out.println("Rows have write access");
			}
			else
			{
				System.out.println("Rows are readonly");
			}

			// see if there is a criterea table associated with this table
			int criteriaTableId = TableViewManagerJsonAPI.getCriteriaTable(connection, tableId, userId);
			System.out.println("Using criterea table id = " + criteriaTableId);

			StringBuffer resData = new StringBuffer(10000000);

			//  ---------------------------------  resJsonData ----------------------------------------------

			JSONArray resJsonData=new JSONArray();
			JSONObject jsonobj = new JSONObject();

			// Get the columns
			Vector colv = ColumnManager.getXlColumnsForImport(connection, tableId, userId, memberId);

			HashMap colHash = new HashMap();
			Iterator ci = colv.iterator();

			for (int c = 0; c < colv.size(); c++)
			{
				xlColumn_import col = (xlColumn_import)colv.elementAt(c);
				if (maxTransactionId < col.getCreationTid())
				{
					maxTransactionId = col.getCreationTid();
				}

				if (maxTransactionId < col.getAccessTid())
				{
					maxTransactionId = col.getAccessTid();
				}
				//  JSON Changes
				resJsonData.add(col.getId());
				resJsonData.add(col.getName());
				//  JSON Changes
				// Mark New Columns, or Deleted Columns
				if (col.getCreationTid() > importTid)
				{
					resJsonData.add("N");
				}
				else if (col.getAccessTid() > importTid && col.getAccess() > 0)
				{
					resJsonData.add("N");
				}
				else
				{
					//  JSON Changes
					//resData.append(Seperator);
					resJsonData.add(" ");
					//  JSON Changes
				}
				//System.out.println("putting in hash column = " + col.getId());
				colHash.put(new Integer(col.getId()), col);
			}

			//  JSON Changes

			//------------------------------ resJsonRowData  ----------------------------------------------

			JSONArray resJsonRowData=new JSONArray();

			// Get the rows
			boolean viewIsDynamic = false;
			if (view.indexOf("?") == 0)
			{
				System.out.println("View is dynamic = " + view);
				viewIsDynamic = true;
			}
			TableRowInfo tbrowInfo = null;
			if (criteriaTableId == -1 && !viewIsDynamic)
			{
				tbrowInfo = RowManager.getTableRows(connection, tableId, userId, nhId, baselineId, view, 1, -1, -1);
			}
			//condition added ashishB
			if (criteriaTableId > 0 && viewIsDynamic)
			{
				tbrowInfo = RowManager.getTableRows(connection, tableId, userId, nhId, baselineId, view, 1, -1, -1);
			}
			else
			{
				tbrowInfo = RowManager.getTableRows(connection, tableId, userId, nhId, baselineId, view, 1, -1, -1);
			}
			Vector rowv = tbrowInfo.getRowVector();
			Hashtable rowHash = tbrowInfo.getRowHash();
			// rows
			//System.out.println("transaction start");
			tm = new TransactionManager(connection, userId);
			int tid = tm.startTransaction("Import changes for table id = " + tableId, "");
			tm.commitTransaction();
			tm = null;
			//System.out.println("transaction commit");
			stmt = connection.prepareStatement("UPDATE BW_ROW SET OWNER_TID = ? WHERE ID = ?");
			int numNewRows = 0;
			for (int r = 0; r < rowv.size(); r++)
			{
				Row rowObject = (Row)rowv.elementAt(r);
				int rowId = rowObject.getId();
				if (maxTransactionId < rowObject.getCreationTid())
				{
					maxTransactionId = rowObject.getCreationTid();
				}

				if (maxTransactionId < rowObject.getOwnershipAssignedTid())
				{
					maxTransactionId = rowObject.getOwnershipAssignedTid();
				}
				//  JSON Changes
				//resData.append(rowId + Seperator);
				resJsonRowData.add(rowId);
				//  JSON Changes
				//if (rowObject.getCreationTid() > importTid || rowObject.getOwnershipAssignedTid() > importTid)
				if (localRowHash.get(new Integer(rowId)) == null)
				{
					//  JSON Changes
					//resData.append("N" + Seperator);
					resJsonRowData.add("N");
					//  JSON Changes
					stmt.setInt(1, tid);
					stmt.setInt(2, rowId);
					stmt.addBatch();
					numNewRows++;
				}
				else
				{
					//  JSON Changes
					//resData.append(Seperator);
					resJsonRowData.add(" ");
					//  JSON Changes
				}
			}
			//  JSON Changes
			//resData.replace(resData.length() - 1, resData.length(), ContentDelimeter);

			//    --------------- resJsonCellData ---------------------------------

			JSONArray resJsonCellData=new JSONArray();
			//  JSON Changes
			if (numNewRows > 0)
			{
				stmt.executeBatch();
				stmt.clearBatch();
			}
			stmt.close();
			stmt = null;
			//System.out.println("Time(sec) to fetch rows = " + getElapsedTime());
			//System.out.println(resData.toString());
			// Get the cells TBD : views other than latest
			String q = null;
			if (synch == 0)
			{
				q = "{CALL BW_IMPORT_CHANGES(?,?,?,?,?,?,?)}";
				System.out.println("Calling BW_IMPORT_CHANGES ");
			}
			else
			{
				q = "{CALL BW_IMPORT_CHANGES_ALL(?,?,?,?,?,?,?)}";
				System.out.println("Calling BW_IMPORT_CHANGES_ALL ");
			}
			//cellv = TableManager.getLatestCellsForTable(connection, m_tableid, userId, memberId, nhId, ViewPreference);
			stmt = connection.prepareStatement(q);
			stmt.setInt(1, tableId);
			stmt.setInt(2, userId);
			stmt.setInt(3, memberId);
			stmt.setInt(4, nhId);
			stmt.setString(5, view);
			stmt.setInt(6, importTid);
			stmt.setInt(7, tid);

			System.out.println("tableId = " + tableId);
			System.out.println("userId = " + userId);
			System.out.println("memberId = " + memberId);
			System.out.println("nhId = " + nhId);
			System.out.println("view = " + view);
			System.out.println("importTid = " + importTid);
			System.out.println("newTid = " + tid);
			System.out.println("mode = " + mode);
			System.out.println("synch = " + synch);

			ResultSet rs = stmt.executeQuery ();
			//System.out.println("Time(sec) to execute cell query = " + getElapsedTime());
			while (rs.next())
			{
				String sval = rs.getString(1);
				String fmla = rs.getString(2);
				int rowId = rs.getInt(3);
				int colId = rs.getInt(4);
				if (maxTransactionId < rs.getInt(5))
				{
					maxTransactionId = rs.getInt(5);
				}
				if (rowHash.get(new Integer(rowId)) == null)
					continue;
				xlColumn_import col = (xlColumn_import)colHash.get(new Integer(colId));
				if (col == null)
					continue;
				if (fmla == null || fmla.indexOf("=") < 0 || mode == 1)
				{
					fmla = "";
				}
				else
				{
					fmla = fmla.trim();
				}
				//System.out.println("Got column for id = " + colId);
				int colAccess = col.getAccess();
				int cellAccess = java.lang.Math.min(raccess, colAccess);
				//  JSON Changes
				//resData.append(rowId + Seperator + colId + Seperator + sval.trim() + Seperator + fmla + Seperator + cellAccess + Seperator);
				resJsonCellData.add(rowId);
				resJsonCellData.add(colId);
				resJsonCellData.add(sval.trim());
				resJsonCellData.add(fmla);
				resJsonCellData.add(cellAccess);
			}
			stmt.close();
			rs.close();
			rs = null;
			stmt = null;
			//System.out.println("Time(sec) to fetch changed cells = " + getElapsedTime());

			int maxdeletedcell_tid;
			maxdeletedcell_tid = 0;
			ResultSet rs1 = null;

			try
			{
				stmt = connection.prepareStatement
					("SELECT MAX(BW_ROW.TX_ID) FROM BW_ROW WHERE BW_ROW.TX_ID > ? AND BW_ROW.BW_TBL_ID = ? AND BW_ROW.IS_ACTIVE = 0 UNION SELECT MAX(BW_COLUMN.TX_ID) FROM BW_COLUMN WHERE BW_COLUMN.TX_ID > ? AND BW_COLUMN.BW_TBL_ID = ? AND BW_COLUMN.IS_ACTIVE = 0");
				stmt.setInt(1, importTid);
				stmt.setInt(2, tableId);
				stmt.setInt(3, importTid);
				stmt.setInt(4, tableId);
				rs1 = stmt.executeQuery ();
				while( rs1.next() )
				{
					if(rs1.getInt(1) > maxdeletedcell_tid)
						maxdeletedcell_tid = rs1.getInt(1);
				}
				rs1.close();
				stmt.close();
				stmt = null;
			}
			catch(Exception e11)
			{	e11.printStackTrace();
				try
				{
					rs1.close();
					stmt.close();
					stmt = null;

				}
				catch (Exception e12)
				{
					e12.printStackTrace();
				}
			}

			//System.out.println("Time(sec) to getmaxtid for deleted cells = " + getElapsedTime());

			if ( maxdeletedcell_tid > maxTransactionId )
			{
				maxTransactionId = maxdeletedcell_tid;
				System.out.println("maxtid reset by cell deactivation to = " + maxTransactionId);
			}
			System.out.println("maxtid = " + maxTransactionId);

			// write the header to the response
			StringBuffer resHeader = new StringBuffer();
			//  JSON Changes
			//resHeader.append("Success" + Seperator);
			//resHeader.append(colv.size() + Seperator);
			//resHeader.append(rowv.size() + Seperator);
			//resHeader.append(maxTransactionId + ContentDelimeter);
			JSONArray resJsonHeader = new JSONArray();
			resJsonHeader.add("Success");
			resJsonHeader.add(colv.size());
			resJsonHeader.add(rowv.size());
			resJsonHeader.add(maxTransactionId);
			System.out.println("resJsonHeader="+resJsonHeader.toString());
			System.out.println("resjsondata="+resJsonData.toString());
			System.out.println("resJsonRowData="+resJsonRowData.toString());
			System.out.println("resJsonCellData="+resJsonCellData.toString());
			jsonobj.put("headerdata",resJsonHeader);
			jsonobj.put("columndata",resJsonData);
			jsonobj.put("rowdata",resJsonRowData);
			jsonobj.put("celldata",resJsonCellData);
			
			System.out.println("json object"+jsonobj.toString());
			String[] jskeys={"headerdata","columndata","rowdata","celldata"};
			StringBuffer temptestdata=new StringBuffer();
			for(int js=0;js<jsonobj.size();js++)
			{
				JSONArray test=new JSONArray();
				test=(JSONArray) jsonobj.get(jskeys[js]);

				for(int jsa=0;jsa<test.size();jsa++)
				{
					//String spaceelim=(String)test.get(jsa);
					System.out.println("values="+test.get(jsa));
					if(test.get(jsa)!=" " && jsa!=test.size()-1)
					{
						temptestdata.append(test.get(jsa));
						temptestdata.append(Seperator);
					}
					//temptestdata=temptestdata+test.get(jsa)+Seperator;
					else if(jsa==test.size()-1)
					{
						if(test.get(jsa)!=" ")
						//temptestdata=temptestdata;
						temptestdata.append(test.get(jsa));
						//temptestdata=temptestdata+test.get(jsa);
					}
					else if(test.get(jsa)==" ")
						temptestdata.append(Seperator);
				//temptestdata=temptestdata+Seperator;
				}
				temptestdata.append(ContentDelimeter);
				//temptestdata=temptestdata+ContentDelimeter;
			}
			System.out.println("temptestdata="+temptestdata.toString());
			
			refreshBuffer=jsonobj.toString();

			//responseBuffer = resHeader.toString() + resData.toString();
			System.out.println("reponse san= "+refreshBuffer);
			//  JSON Changes

		}
		catch (BoardwalkException bwe)
		{
			if (xlErrorCells.size() <= 0)
			{
				refreshBuffer = "Failure";//TBD : Description of the Error
				xlErrorCells.add(new xlErrorNew(tableId, 0, 0, bwe.getErrorCode()));
			}
			StringBuffer errorBuffer = new StringBuffer();

			for (int errorIndex = 0; errorIndex < xlErrorCells.size(); errorIndex++)
			{
				xlErrorNew excelError = (xlErrorNew)(xlErrorCells.elementAt(errorIndex));
				errorBuffer.append(excelError.buildTokenString());
			}
			errorBuffer.append(Seperator);
			failureReason = errorBuffer.toString();
			System.out.println(" Failure Reason *****" + failureReason);

			try
			{
				if (tm != null)
					tm.rollbackTransaction();
			}
			catch (Exception e1)
			{
				e1.printStackTrace();
			}
		}		

		catch (Exception e)
		{
			System.out.println("Inside catch exception. need to know reason.");
			try
			{
				if (tm != null)
					tm.rollbackTransaction();
			}
			catch (Exception e1)
			{
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		finally
		{
			// close the connection
			try
			{
				if (connection != null)
					connection.close();
			}
			catch (SQLException sql)
			{
				sql.printStackTrace();
			}

			// The response
			if(failureReason.length() == 0)
			{
				//commitResponseBuffer(responseBuffer, response);
				//System.out.println("Time to prepare response = " + getElapsedTime());
			}
			else
			{
				failureReason = "FAILURE"+ContentDelimeter+failureReason;
				refreshBuffer = failureReason ;
				//commitResponseBuffer(failureReason, response);
				//System.out.println("Time to prepare response = " + getElapsedTime());
			}
		}		
		
		return refreshBuffer;
	}


	//Added by Rahul Varadkar on 24-February-2018 
	public static String LinkExportTable(Connection connection, String userName, String pwd, String nhHierarchy, int tableId, int numColumns, int numRows, String columnInfo, String columnValues, String columnFormulas)			//throws BoardwalkException 
	{
		String linkExportResponseBuffer = "" ;
		int userId;
		int memberId;
		int nhId;
		String nhName;
		String view = "LATEST";
		String query;
		// Failure String
		String failureReason = "";
		//String reqBuffer = getRequestBuffer(request).toString();
		//BoardwalkRequestReader reader = getRequestReader(request);
		//System.out.println(reqBuffer);
		//String[] fullTableArr = reqBuffer.split(ContentDelimeter);
		//System.out.println(fullTable);
		//System.out.println("xlLinkExportService: Time to read the buffer = " + getElapsedTime());

		JSONObject responseJsonBuffer = new JSONObject();;

		PreparedStatement stmt		= null;
		TransactionManager tm = null;
		int tid = -1;

		// Error vector to all the Exceptions
		Vector xlErrorCells = new Vector(); //new Vector();
		// access variables
		boolean canAddRows = false;
		boolean canDeleteRows = false;
		boolean canAdministerColumns = false;

		ArrayList	columnIds = null;
		ArrayList	rowIds = null;
		ArrayList	formulaIds = null;
		ArrayList	strValIds = null;
		String		formulaString = null;

		int MAX_RETRY_ATTEMPTS = 5;
		int RETRY_WAIT_TIME_MIN = 1000;
		int RETRY_WAIT_TIME_MAX = 3000;
		
		try
		{
		//	MAX_RETRY_ATTEMPTS = Integer.parseInt(getServletConfig().getInitParameter("MAX_RETRY_ATTEMPTS"));
		//	RETRY_WAIT_TIME_MIN = Integer.parseInt(getServletConfig().getInitParameter("RETRY_WAIT_TIME_MIN"));
		//	RETRY_WAIT_TIME_MAX = Integer.parseInt(getServletConfig().getInitParameter("RETRY_WAIT_TIME_MAX"));
			System.out.println("MAX_RETRY_ATTEMPTS=" + MAX_RETRY_ATTEMPTS);
			System.out.println("RETRY_WAIT_TIME_MIN=" + RETRY_WAIT_TIME_MIN);
			System.out.println("RETRY_WAIT_TIME_MAX=" + RETRY_WAIT_TIME_MAX);
		}
		catch (Exception e)
		{
			System.out.println("Deadlock parameters not set. Using defaults...");
		}
		StringBuffer responseToUpdate = null; //new StringBuffer();
		String responseBuffer = null;

		for (int ti = 0; ti < MAX_RETRY_ATTEMPTS; ti++)
		{
			responseToUpdate = new StringBuffer ();
			responseBuffer = null;
			
			try
			{
				//processHeader(fullTableArr[0]);
				//processLinkExportHeader(reader.getNextContent());

				// Start a connection
				DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
				connection = databaseloader.getConnection();

				userId = UserManager.authenticateUser(connection, userName, pwd);

				if ( userId > 0 )
				{
					Hashtable memberships  = UserManager.getMembershipsForUser( connection, userId );
					//Added to get the Membership ID in case of Multiple Membership
					memberId = UserManager.checkMembershipStatus(connection, userId, "", nhHierarchy, -1);
					System.out.println("AdminTasks.LinkExportTable: Multiple Member Id: "+memberId);

					if (memberId != -1)
					{
						nhId =((Member) memberships.get(new Integer(memberId))).getNeighborhoodId();
						nhName = ((Member) memberships.get(new Integer(memberId))).getNeighborhoodName();
						
						//commitResponseBuffer("Success:" + userId + Seperator + memberId + Seperator + nhid + Seperator + nhName + Seperator, response);
						System.out.println("status : Success");
						System.out.println("userId :" + userId);
						System.out.println("memberId : " + memberId);
						System.out.println("nhId : " + nhId);
						System.out.println("nhName : " + nhName);
						System.out.println("Success:" + userId + Seperator + memberId + Seperator + nhId + Seperator + nhName + Seperator);
					}
					else
					{
						BoardwalkMessage bwmsg= new BoardwalkMessages().getBoardwalkMessage( 11007 );
						//commitResponseBuffer("failure" + xlService.ContentDelimeter + bwmsg.getCause() + "," + bwmsg.getPotentialSolution(), response );
						System.out.println("status : failure");
						System.out.println("Cause" + bwmsg.getCause());
						System.out.println("PotentialSolution" + bwmsg.getPotentialSolution());
						linkExportResponseBuffer = "FAILURE" + ContentDelimeter +  bwmsg.getCause() + ", " + bwmsg.getPotentialSolution();
						return linkExportResponseBuffer;
					}

					//	Access control checks
					TableInfo tinfo = TableManager.getTableInfo(connection, userId, tableId);
					TableAccessList ftal = TableViewManager.getSuggestedAccess(connection, tinfo, userId, memberId, nhId);
					//if (view == null ||  view.trim().equals(""))
					//{
					//    view = ftal.getSuggestedViewPreferenceBasedOnAccess();
					//    System.out.println("Suggested view pref = " + view);
					//    if(view == null)
					//        view = "None";
					//}
					// Check access control :: TBD
					int raccess = 1;
					int ACLFromDB = ftal.getACL();
					TableAccessRequest wAccess = new TableAccessRequest(tableId, "LATEST", true);
					int wACL = wAccess.getACL();
					int awACL = wACL & ACLFromDB;

					canAddRows				= ftal.canAddRow();
					canDeleteRows			= ftal.canDeleteRow();
					canAdministerColumns	= ftal.canAdministerColumn();

				}
				else
				{
					BoardwalkMessage bwmsg = null;
					System.out.println(" Check here userId >>>>>>>>>"+userId);

					if (userId == -1)
					{
						bwmsg= new BoardwalkMessages().getBoardwalkMessage( 11004 );
					}
					if (userId == 0)
					{
						bwmsg= new BoardwalkMessages().getBoardwalkMessage( 11006 );
					}

					//commitResponseBuffer("failure"+ xlService.ContentDelimeter+ bwmsg.getCause() + "," + bwmsg.getPotentialSolution(),response);
					System.out.println("status : failure");
					System.out.println("Cause : " + bwmsg.getCause());
					System.out.println("PotentialSolution:" +bwmsg.getPotentialSolution());
					linkExportResponseBuffer = "FAILURE" + ContentDelimeter +  bwmsg.getCause() + ", " + bwmsg.getPotentialSolution();
					return linkExportResponseBuffer;
				}

								///////////////// not required
								// authenticate the user
					/*
										Member memberObj = UserManager.authenticateMember(connection, userName,pwd, memberId);
										if (memberObj == null)
										{
											System.out.println("Authentication failed for user : " + userName);
											xlErrorCells.add( new xlErrorNew( tableId, 0, 0, 11004));
											throw new BoardwalkException(11004);
										}
										else
										{
											System.out.println("Authentication succeeded for user : " + userName);
											nhId = memberObj.getNeighborhoodId();
											tm = new TransactionManager(connection, userId);
											tid = tm.startTransaction("Link export new table", "Link export new table");
										}
										System.out.println("Time to authenticate user = " + getElapsedTime());
					*/
								///////////////// not required
				tm = new TransactionManager(connection, userId);
				tid = tm.startTransaction("Link export new table", "Link export new table");

				if(canAdministerColumns == false)
				{
					// User does not have access to add columns
					xlErrorCells.add( new xlErrorNew( tableId,0,0,12010));
					System.out.println("No access to add column");
				}

				if(canAddRows == false)
				{
					xlErrorCells.add( new xlErrorNew( tableId,0,0,12012));
					System.out.println("No access to add rows");
				}
				System.out.println("view = " + view);
				if(view.equals("None"))
				{
					xlErrorCells.add( new xlErrorNew(tableId, 0, 0, 10005));
				}


				//processLinkExportHeader(reader.getNextContent()); ..........ENDS HERE

				//processColumns(fullTableArr[1]);
				//processLinkExportColumns(reader.getNextContent());

				// If the user has access to add new Columns then procede forward
				if(canAdministerColumns)
				{
					columnIds = new ArrayList(numColumns);
					String[] columnNames = columnInfo.split(Seperator);
					Vector columns = new Vector();
					query = " INSERT INTO BW_COLUMN " +
							   " (NAME, BW_TBL_ID, COLUMN_TYPE, SEQUENCE_NUMBER, TX_ID) " +
							   " VALUES " +
							   " (?,?,?,?,?)";

					stmt = connection.prepareStatement(query);
					// Add columns...Ignoring BWID so Starting from 1. numColumns was sent from client Ignoring bwid, so adding 1 
					for (int cni = 1; cni < numColumns+1; cni++)
					{
						System.out.println("Adding column : " + columnNames[cni]);
						stmt.setString(1, columnNames[cni]);
						stmt.setInt(2, tableId);
						stmt.setString(3, "STRING");
						stmt.setFloat(4, cni + 1);
						stmt.setInt(5, tid);
						stmt.addBatch();
					}
					int[] rescnt = stmt.executeBatch();
					stmt.clearBatch();
					stmt.close();
					stmt = null;
				}

				//HashMap columnHash = new HashMap();
				ResultSet resultset = null;
				query = "select id from bw_column where tx_id = ? order by sequence_number";
				stmt = connection.prepareStatement(query);
				stmt.setInt(1, tid);
				resultset = stmt.executeQuery();
				while (resultset.next())
				{
					int columnId = resultset.getInt(1);
					//int columnIdx = resultset.getFloat (2);
					columnIds.add (new Integer(columnId));
					//columnHash.put (new Integer(columnIdx), new Integer(columnId));
					//System.out.println("columnid = " + columnId);
				}
				stmt.close();
				stmt = null;
				resultset.close();
				resultset = null;

				//processLinkExportColumns(reader.getNextContent());  ........... ENDS HERE


				// Add rows
				System.out.println("xlLinkExportService:service() : userId = " + userId);
				System.out.println("xlLinkExportService:service() : tableId = " + tableId);
				System.out.println("xlLinkExportService:service() : tid = " + tid);
				System.out.println("xlLinkExportService:service() : numRows = " + numRows);
				System.out.println("xlLinkExportService:service() : numColumns = " + numColumns);
				if (numRows > 0)
				{
					if (canAdministerColumns && canAddRows)
						rowIds = TableManager.createRowsNewTable(connection, tableId, tid, userId, numRows);
					//	System.out.println("xlLinkExportService: Time to create rows = " + getElapsedTime());
					
					// Insert into BW_RC_STRING_VALUE
					String[] colValueArr = columnValues.split(ContentDelimeter);
					String[] colFormulaArr = columnFormulas.split(ContentDelimeter);

					// for (int i = 0; i < numColumns * 2; i = i + 2)
					
					for (int i = 0; i < numColumns ; i=i+1)
					{
						////int columnIdx = i / 2;
						System.out.println("Processing column num = " + i);
						//processColumnData(fullTableArr[i + 2], fullTableArr[i + 3], columnIdx);
						////String cellBuff = reader.getNextContent();
						////String fmlaBuff = reader.getNextContent();

						String cellBuff = colValueArr[i];
						String fmlaBuff = colFormulaArr[i];

						System.out.println("cellBuff : " + cellBuff);
						System.out.println("fmlaBuff : " + fmlaBuff);
						System.out.println("rowIds : " + rowIds);
						System.out.println("columnIds : " + columnIds);


						processLinkExportColumnData(connection, cellBuff, fmlaBuff, i, rowIds, columnIds, numRows, tid);
						cellBuff = null;
						fmlaBuff = null;
					}
					//System.out.println("xlLinkExportService: Time to insert into rcsv table = " + getElapsedTime());

					System.out.println("xlLinkExportService: xlErrorCells.size() " + xlErrorCells.size());
					if (xlErrorCells.size() > 0)
					{
						throw new BoardwalkException(12011);
					}

					query = "{CALL BW_UPD_CELL_FROM_RCSV_LINK_EXPORT(?,?,?)}";
					CallableStatement cstmt = connection.prepareCall(query);
					cstmt.setInt(1, tid);
					cstmt.setInt(2, tableId);
					cstmt.setInt(3, userId);
					int updCount = cstmt.executeUpdate();
					cstmt.close();
					cstmt = null;
				//	System.out.println("xlLinkExportService: Time to execute BW_UPD_CELL_FROM_RCSV_LINK_EXPORT = " + getElapsedTime());
				}
				// commit the transaction
				tm.commitTransaction();
				tm = null;
				//tm.rollbackTransaction(); // FOR NOW

				// create the response
				responseToUpdate.append("Success" + Seperator);
				responseToUpdate.append(numColumns + Seperator);
				responseToUpdate.append(numRows + Seperator);
				responseToUpdate.append(tid + ContentDelimeter);

				responseToUpdate.append(tableId + ContentDelimeter + memberId + ContentDelimeter);


				JSONArray resJsonHeader = new JSONArray();
				resJsonHeader.add("Success");
				resJsonHeader.add(numColumns);
				resJsonHeader.add(numRows);
				resJsonHeader.add(tid);
				resJsonHeader.add(tableId);
				resJsonHeader.add(memberId);

				System.out.println("Link Export Response : resJsonHeader = " + resJsonHeader.toString());

				StringBuffer leRowIds = new StringBuffer();
				int ri = 0;
				int ci = 0;

				for (ri = 0; ri < numRows - 1; ri++)
				{
					responseToUpdate.append(rowIds.get(ri) + Seperator);
					leRowIds.append(rowIds.get(ri) + Seperator);
				}

				if (numRows > 0)
				{
					responseToUpdate.append(rowIds.get(ri) + ContentDelimeter);//last rowid
					leRowIds.append(rowIds.get(ri));
				}
				else
				{
					responseToUpdate.append(ContentDelimeter);//last rowid
				}

				JSONArray  resJsonRowIds = new JSONArray ();
				resJsonRowIds.add(leRowIds.toString());
				System.out.println("Link Export Response : resJsonRowIds = " + resJsonRowIds.toString());


				StringBuffer leColIds = new StringBuffer();
				for (ci = 0; ci < numColumns - 1; ci++)
				{
					responseToUpdate.append(columnIds.get(ci) + Seperator);
					leColIds.append(columnIds.get(ci) + Seperator);
				}

				responseToUpdate.append(columnIds.get(ci) + ContentDelimeter);//last columnid
				leColIds.append(columnIds.get(ci));
				System.out.println("Link Export Response : leColIds = " + leColIds.toString());
				JSONArray resJsonColIds = new JSONArray();
				resJsonColIds.add(leColIds.toString());

				responseToUpdate.append(formulaString + ContentDelimeter);

				responseJsonBuffer.put("header", resJsonHeader);
				responseJsonBuffer.put("resJsonRowIds", resJsonRowIds);
				responseJsonBuffer.put("resJsonColIds", resJsonColIds);
				
				//not done
				//responseJsonBuffer.put("objCellData", objCellData);


				ti = MAX_RETRY_ATTEMPTS; // dont try again

				failureReason = "";
			}
			catch (SQLException sqe)
			{

				sqe.printStackTrace();
				try
				{
					if (tm != null)
						tm.rollbackTransaction();
				}
				catch (Exception e1)
				{
					e1.printStackTrace();
				}
				if (sqe.getErrorCode() == 1205)
				{
					if (ti == MAX_RETRY_ATTEMPTS - 1)
					{
						failureReason = (new xlErrorNew(tableId, 0, 0, 13001)).buildTokenString();
						System.out.println("xlLinkExportService: Deadlock maximum attempts exhausted. Sending server busy message to client ");
						System.out.println("xlLinkExportService:failureReason = " + failureReason);
					}
					System.out.println("xlLinkExportService:Deadlock attempt number = " + (ti + 1) + " out of max = " + MAX_RETRY_ATTEMPTS);
					//sqe.printStackTrace();
					try
					{
						int sleepTime = RETRY_WAIT_TIME_MIN + (new Random()).nextInt(RETRY_WAIT_TIME_MAX - RETRY_WAIT_TIME_MIN);
						System.out.println("Sleeping for " + sleepTime + "ms");
						Thread.sleep(sleepTime);
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}
				else
				{
					failureReason = sqe.getMessage();
					ti = MAX_RETRY_ATTEMPTS; // dont try again
				}

			}
			catch (BoardwalkException bwe)
			{
				ti = MAX_RETRY_ATTEMPTS; // dont try again
				bwe.printStackTrace();
				if (xlErrorCells.size() > 0)
				{
					StringBuffer errorBuffer = new StringBuffer();

					for (int errorIndex = 0; errorIndex < xlErrorCells.size(); errorIndex++)
					{
						xlErrorNew excelError = (xlErrorNew)(xlErrorCells.elementAt(errorIndex));
						errorBuffer.append(excelError.buildTokenString());
					}
					errorBuffer.append(Seperator);
					failureReason = errorBuffer.toString();
					try
					{
						if (tm != null)
							tm.rollbackTransaction();
					}
					catch (Exception e1)
					{
						e1.printStackTrace();
					}
				}
			}
			catch (Exception e)
			{
				ti = MAX_RETRY_ATTEMPTS; // dont try again
				e.printStackTrace();
				try
				{
					if (tm != null)
						tm.rollbackTransaction();
				}
				catch (Exception e1)
				{
					e1.printStackTrace();
				}
				failureReason = e.getMessage();
			}
			finally
			{
				// close the connection
				try
				{
					//reader.close();
					connection.close();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				// clean up
				numColumns = 0;
				numRows = 0;
				tableId = -1;
				nhId = -1;

				columnIds = null;
				rowIds = null;
				formulaIds = null;
				strValIds = null;
				formulaString = null;

				userId = -1;
				userName = "";
				//userPassword = "";
				memberId = -1;
				//formulaArray = null;
				view = null;
				query = "";
				xlErrorCells = null;
				
				canAddRows = false;
				canDeleteRows = false;
				canAdministerColumns = false;

				connection = null;
				stmt = null;
				tm = null;
				tid = -1;
			}
		}
		// The response
		if (failureReason.length() == 0)
		{
			//responseBuffer = responseToUpdate.toString();

			//linkExportResponseBuffer = responseToUpdate.toString();
			linkExportResponseBuffer = responseJsonBuffer.toString();
			//System.out.println("AdminTask.LinkExport: responseToUpdate = " + responseToUpdate.toString());
			System.out.println("AdminTask.LinkExport: linkExportResponseBuffer = " + linkExportResponseBuffer);
		}
		else
		{
			failureReason = "FAILURE" + ContentDelimeter + failureReason;
			System.out.println("AdminTask.LinkExport: failureReason = " + failureReason);
			linkExportResponseBuffer = failureReason;
		}
		return linkExportResponseBuffer; 
	}


	public static void processLinkExportColumnData(Connection connection, String cellData, String formulaData, int columnIdx, ArrayList rowIds, ArrayList columnIds, int numRows, int tid) throws SQLException 
	{

		PreparedStatement stmt		= null;

		String[] cellArr = cellData.split(Seperator);
		String[] formulaArr = formulaData.split(Seperator);
		int columnId = ((Integer)columnIds.get(columnIdx)).intValue();
		boolean emptyColumn = false;
		System.out.println("cellArr.length = " + cellArr.length);
		if (cellArr.length == 0) // empty column
		{
			emptyColumn = true;
			System.out.println("Column is empty");
		}
		boolean emptyFormulae = false;
		if (formulaArr.length == 0) // empty column
		{
			emptyFormulae = true;
			System.out.println("Formulae is empty");
		}

		// insert into bw_rc_string_value 
		String query = 
			" INSERT INTO BW_RC_STRING_VALUE " + 
			" (BW_ROW_ID, BW_COLUMN_ID, STRING_VALUE, FORMULA, TX_ID, CHANGE_FLAG) " +
			" VALUES " +
			" (?, ?, ?, ?, ?, ?) ";

		stmt = connection.prepareStatement(query);
		for (int i = 0; i < numRows; i++)
		{
			int rowId = ((Integer)rowIds.get(i)).intValue ();
			String cellValue = "";
			String formula = null;
			if (emptyColumn == false)
			{
				try
				{
					cellValue = cellArr[i];
				}
				catch (ArrayIndexOutOfBoundsException e)
				{
					cellValue = "";
				}
			}
			if (emptyFormulae == false)
			{
				try
				{
					formula = formulaArr[i];
					//if (formula.indexOf("=") < 0)					//Fix for IssueId: 
					if (formula.indexOf("=") != 0)					//Fix for IssueId: 
					{
						formula = null;
					}
				}
				catch (ArrayIndexOutOfBoundsException e)
				{
					formula = null;
				}
			}
			//System.out.println("INSERT INTO BW_RC_STRING_VALUE rowId = " + rowId + " columnId = " + columnId + " cellValue = " + cellValue + " formula = " + formula);

			stmt.setInt(1, rowId);
			stmt.setInt(2, columnId);
			stmt.setString(3, cellValue);
			stmt.setString(4, formula);
			stmt.setInt(5, tid);
			stmt.setInt(6, 12);
			stmt.addBatch();
		}
		int[] rescnt = stmt.executeBatch();
		stmt.close();
		stmt = null;

	}

/*
	public void processLinkExportHeader(String header) throws BoardwalkException, SQLException, SystemException
	{
		System.out.println("header = " + header);


		String[] headerInfo = header.split(Seperator);

		System.out.println("headerInfo[0] = " + headerInfo[0]);
		System.out.println("headerInfo[1] = " + headerInfo[1]);
		System.out.println("headerInfo[2] = " + headerInfo[2]);
		System.out.println("headerInfo[3] = " + headerInfo[3]);
		System.out.println("headerInfo[4] = " + headerInfo[4]);
		System.out.println("headerInfo[5] = " + headerInfo[5]);
		System.out.println("headerInfo[6] = " + headerInfo[6]);
		System.out.println("headerInfo[7] = " + headerInfo[7]);


		userId				= Integer.parseInt(headerInfo[0]);
		System.out.println("processHeader() : userId = " + userId);
		userName			= headerInfo[1];
		userPassword        = headerInfo[2];
		memberId			= Integer.parseInt (headerInfo[3]);
		tableId				= Integer.parseInt (headerInfo[4]);
		nhId				= Integer.parseInt (headerInfo[5]);
		numColumns			= Integer.parseInt(headerInfo[6]);
		numRows				= Integer.parseInt (headerInfo[7]);
		view = "LATEST";
		xlErrorCells = new Vector();

		// Start a connection
		DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
		connection = databaseloader.getConnection();

		//	Access control checks
		TableInfo tinfo = TableManager.getTableInfo(connection, userId, tableId);
		TableAccessList ftal = TableViewManager.getSuggestedAccess(connection, tinfo, userId, memberId, nhId);
		//if (view == null ||  view.trim().equals(""))
		//{
		//    view = ftal.getSuggestedViewPreferenceBasedOnAccess();
		//    System.out.println("Suggested view pref = " + view);
		//    if(view == null)
		//        view = "None";
		//}
		// Check access control :: TBD
		int raccess = 1;
		int ACLFromDB = ftal.getACL();
		TableAccessRequest wAccess = new TableAccessRequest(tableId, "LATEST", true);
		int wACL = wAccess.getACL();
		int awACL = wACL & ACLFromDB;

		canAddRows				= ftal.canAddRow();
		canDeleteRows			= ftal.canDeleteRow();
		canAdministerColumns	= ftal.canAdministerColumn();

// authenticate the user
		Member memberObj = UserManager.authenticateMember(connection, userName,userPassword, memberId);
		if (memberObj == null)
		{
			System.out.println("Authentication failed for user : " + userName);
			xlErrorCells.add( new xlErrorNew( tableId, 0, 0, 11004));
			throw new BoardwalkException(11004);
		}
		else
		{
			System.out.println("Authentication succeeded for user : " + userName);
			nhId = memberObj.getNeighborhoodId();
			tm = new TransactionManager(connection, userId);
			tid = tm.startTransaction("Link export new table", "Link export new table");
		}

		System.out.println("Time to authenticate user = " + getElapsedTime());

		if(canAdministerColumns == false)
		{
			// User does not have access to add columns
			xlErrorCells.add( new xlErrorNew( tableId,0,0,12010));
			System.out.println("No access to add column");
		}

		if(canAddRows == false)
		{
			xlErrorCells.add( new xlErrorNew( tableId,0,0,12012));
			System.out.println("No access to add rows");
		}
		System.out.println("view = " + view);
		if(view.equals("None"))
		{
			xlErrorCells.add( new xlErrorNew(tableId, 0, 0, 10005));
		}
	}


	public void processLinkExportColumns(String columnInfo) throws BoardwalkException, SQLException
	{
		// If the user has access to add new Columns then procede forward
		if(canAdministerColumns)
		{
			columnIds = new ArrayList(numColumns);
			String[] columnNames = columnInfo.split(Seperator);
			Vector columns = new Vector();
			query = " INSERT INTO BW_COLUMN " +
					   " (NAME, BW_TBL_ID, COLUMN_TYPE, SEQUENCE_NUMBER, TX_ID) " +
					   " VALUES " +
					   " (?,?,?,?,?)";

			stmt = connection.prepareStatement(query);
			// Add columns
			for (int cni = 0; cni < numColumns; cni++)
			{
				System.out.println("Adding column : " + columnNames[cni]);
				stmt.setString(1, columnNames[cni]);
				stmt.setInt(2, tableId);
				stmt.setString(3, "STRING");
				stmt.setFloat(4, cni + 1);
				stmt.setInt(5, tid);
				stmt.addBatch();
			}
			int[] rescnt = stmt.executeBatch();
			stmt.clearBatch();
			stmt.close();
			stmt = null;
		}

		//HashMap columnHash = new HashMap();
		ResultSet resultset = null;
		query = "select id from bw_column where tx_id = ? order by sequence_number";
		stmt = connection.prepareStatement(query);
		stmt.setInt(1, tid);
		resultset = stmt.executeQuery();
		while (resultset.next())
		{
			int columnId = resultset.getInt(1);
			//int columnIdx = resultset.getFloat (2);
			columnIds.add (new Integer(columnId));
			//columnHash.put (new Integer(columnIdx), new Integer(columnId));
			//System.out.println("columnid = " + columnId);
		}
		stmt.close();
		stmt = null;
		resultset.close();
		resultset = null;
	}

*/

};
