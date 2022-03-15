package boardwalk.rest;

import io.swagger.model.CellBuffer;
import io.swagger.model.ErrorRequestObject;
import io.swagger.model.GridInfo;
import io.swagger.model.Membership;
import io.swagger.model.User;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;				//for Properties


import org.apache.commons.codec.binary.Base64;
import com.boardwalk.exception.BoardwalkException;
import com.boardwalk.exception.SystemException;
import boardwalk.connection.BoardwalkConnection;
import boardwalk.neighborhood.BoardwalkUser;
import boardwalk.neighborhood.BoardwalkUserManager;
//import com.boardwalk.user.*;
import com.boardwalk.member.*;

public class UserManagement {

    private static String CALL_BW_GET_ALL_MEMBERSHIPS_INFO = "{CALL BW_GET_ALL_MEMBERSHIPS_INFO}";

	public UserManagement()
	{	
	}

    //GET	......../user/{email}/neighborhood/{nhPath}/collaboration/{collabId}/whiteboard/{whiteboardId}/grid/{gridId}
	public static GridInfo userGetNeighborhoodCollaborationWhiteboardGrid(String email, String nhPath, int collabId, int whiteboardId, int gridId, ArrayList  <ErrorRequestObject> ErrResps, String authBase64String , BoardwalkConnection bwcon, ArrayList<Integer> memberNh)
	{

		GridInfo ginfo = null;
    	ErrorRequestObject erb;
    	// get the connection
		
		
		if  (!bwcon.getUserName().equals(email))
		{
			erb = new ErrorRequestObject();
			erb.setError("Email does not match with the Authorization.");
			erb.setPath("UserManagement.userGetNeighborhoodCollaborationWhiteboardGrids::getUserName!=email");
			erb.setProposedSolution("The login in Authorization is different than email. The Email must be same as Aughorization UserNname.");
			ErrResps.add(erb);
			System.out.println("The Email must be same as Aughorization UserNname.");				
            //retMsg = "The Email must be same as Aughorization UserNname:" + email;
			return ginfo;
		}

		int nhId = -1;
		int memberId = -1;
		int userId = -1;
		int nhLevel = -1;
		
    	Connection connection = bwcon.getConnection();
		memberId = memberNh.get(0);
		nhId = memberNh.get(1);
		userId = bwcon.getUserId();

		byte[] authSetting = Base64.decodeBase64(authBase64String);
		String auth = new String(authSetting);		
		String nhPathAuth = null;
		String[] userLogin = auth.split(":");
		nhPathAuth = userLogin[2];

		if  (!nhPath.equals(nhPathAuth))
		{
			erb = new ErrorRequestObject();
			erb.setError("Neighborhood Path does not match with the Authorization nhPath.");
			erb.setPath("UserManagement.userGetNeighborhoodCollaborationWhiteboardGrids::nhPath!=nhPathAuth");
			erb.setProposedSolution("The nhPath in Authorization is different than nhPath in Request. The nhPath must be same as Aughorization Neighbohood Path.");
			ErrResps.add(erb);
			System.out.println("The nhPath must be same as Aughorization Neighbohood Path.");				
            //retMsg = "The Email must be same as Aughorization UserNname:" + email;
			return ginfo;
		}

   		CellBuffer cbf;
  	 	
   		int importTid = -1;
   		String view = "LATEST";
   		int mode = 1;
   		int baselineId = -1;
   		ArrayList<Integer> statusCode = new ArrayList<Integer>();
   		
  	 	cbf = GridManagement.gridGridIdGet(gridId, importTid, view, mode, baselineId, ErrResps, bwcon, memberNh, statusCode);

		ginfo = cbf.getInfo();
		return ginfo;
	}

	
/*	//GET  ....../user/{email}/neighborhood/{nhPath}/collaboration/{collabId}/whiteboard/{whiteboardId}/grids
	public static ArrayList<GridNames> userGetNeighborhoodCollaborationWhiteboardGrids(String email, String nhPath, int collabId, int whiteboardId, ArrayList  <ErrorRequestObject> ErrResps, String authBase64String, BoardwalkConnection bwcon, ArrayList<Integer> memberNh , ArrayList<Integer> statusCode  )
	{
		String retMsg = null;
        // get the connection
    	ErrorRequestObject erb;
    	
		// get the connection
    	Connection connection = null;
		
		int nhId = -1;
		int memberId = -1;
		int userId = -1;
		int nhLevel = -1;
		Whiteboard wb;

		ArrayList<GridNames> grids = new ArrayList<GridNames>();
		GridNames grid;

		if  (!bwcon.getUserName().equals(email))
		{
			statusCode.add(403);
			erb = new ErrorRequestObject();
			erb.setError("Email does not match with the Authorization.");
			erb.setPath("UserManagement.userGetNeighborhoodCollaborationWhiteboardGrids::getUserName!=email");
			erb.setProposedSolution("The login in Authorization is different than email. The Email must be same as Aughorization UserNname.");
			ErrResps.add(erb);
			System.out.println("The Email must be same as Aughorization UserNname.");				
            //retMsg = "The Email must be same as Aughorization UserNname:" + email;
			return grids;
		}
		
		connection = bwcon.getConnection();
		memberId = memberNh.get(0);
		nhId = memberNh.get(1);
		userId = bwcon.getUserId();

		byte[] authSetting = Base64.decodeBase64(authBase64String);
		String auth = new String(authSetting);		
		String nhPathAuth = null;
		String[] userLogin = auth.split(":");
		nhPathAuth = userLogin[2];

		if  (!nhPath.equals(nhPathAuth))
		{
			statusCode.add(403);
			erb = new ErrorRequestObject();
			erb.setError("Neighborhood Path does not match with the Authorization nhPath.");
			erb.setPath("UserManagement.userGetNeighborhoodCollaborationWhiteboardGrids::nhPath!=nhPathAuth");
			erb.setProposedSolution("The nhPath in Authorization is different than nhPath in Request. The nhPath must be same as Aughorization Neighbohood Path.");
			ErrResps.add(erb);
			System.out.println("The nhPath must be same as Aughorization Neighbohood Path.");				
            //retMsg = "The Email must be same as Aughorization UserNname:" + email;
			return grids;
		}
		
		try
		{
	        Neighborhood nh = null;
			nh = NeighborhoodManager.getNeighborhoodById(connection, nhId);

			String nhName = nh.getName();
			String collabName = "";
			String whiteBoard = "";
			String tableName = "";
			String Collabline="";
			int wbId;
			int tableId;

			boolean collabFound = false;
			Vector<?> cl = BoardwalkCollaborationManager.getCollaborationsForNeighborhood(bwcon, nhId);
			Iterator<?> cli = cl.iterator();
			while (cli.hasNext())
			{
				if ( (Integer)cli.next() == collabId)
				{
					BoardwalkCollaborationNode bcn = BoardwalkCollaborationManager.getCollaborationTree(bwcon, collabId);
					collabName = bcn.getName();
					System.out.println("Sucessfully fetched the collab tree from the database");

					System.out.println("Collaboration = " + bcn.getName());
					Vector<?> wv = bcn.getWhiteboards();
					Iterator<?> wvi = wv.iterator();
					boolean whiteboardFound = false;
					while ( wvi.hasNext())
					{
						wb = new Whiteboard();
						whiteBoard = "";
						BoardwalkWhiteboardNode bwn = (BoardwalkWhiteboardNode)wvi.next();
						System.out.println("\tWhiteboard = " + bwn.getName());
						//whiteBoard = bwn.getName();
	
						if ( (Integer)bwn.getId() == whiteboardId)
						{
							whiteboardFound = true;

							grids = new ArrayList<GridNames>();
							
							Vector<?> tv = bwn.getTables();
							Iterator<?> tvi = tv.iterator();
							if (tvi.hasNext())
							{
								while (tvi.hasNext())
								{
									grid = new GridNames();
									BoardwalkTableNode btn = (BoardwalkTableNode)tvi.next();
									System.out.println("\t\tTable = " + btn.getName());
									grid.setId( btn.getId());
									grid.setName(btn.getName());
									grid.setPurpose(btn.getDescription());
									grids.add(grid);
								}
							}
							break;
						}
					}
					if(whiteboardFound == false)
					{
						statusCode.add(404);				//not found
						erb = new ErrorRequestObject();
						erb.setError("Whiteboard Not Found.");
						erb.setPath("UserManagement.userGetNeighborhoodCollaborationWhiteboardGrids::whiteboardFound=false");
						erb.setProposedSolution("The requested Whiteboard Does not exist.");
						ErrResps.add(erb);
						System.out.println("The requested Whiteboard Does not exist.");				
			            //retMsg = "The Email must be same as Aughorization UserNname:" + email;
						return grids;
					}
					collabFound = true;
					break;
				}
			}
			if(collabFound == false)
			{
				statusCode.add(404);				//not found
				erb = new ErrorRequestObject();
				erb.setError("Collaboration Not Found or it is not accessible to User.");
				erb.setPath("UserManagement.userGetNeighborhoodCollaborationWhiteboardGrids::collabFound=false");
				erb.setProposedSolution("The requested Collaboration Does not exist OR not accessible to User.");
				ErrResps.add(erb);
				System.out.println("The requested Collaboration Does not exist OR not accessible to User.");				
	            //retMsg = "The Email must be same as Aughorization UserNname:" + email;
				return grids;
			}
		}
		catch (NoSuchElementException nse)
		{
			System.out.println("Collaboration of this Id does not exists.");
//			throw new BoardwalkException( 10019 );
		}
		catch(Exception e)
		{
			//start here fix error here
			System.out.println("Collaboration of this Id does not exists.");
//			throw new BoardwalkException( 10019 );
		}
		return grids;
	}
	
	//GET  ....../user/{email}/neighborhood/{nhPath}/collaboration/{collabId}/whiteboards
	public static ArrayList<Whiteboard>  userGetNeighborhoodCollaborationWhiteboards( String email, String nhPath, int collabId, ArrayList  <ErrorRequestObject> ErrResps, String authBase64String, BoardwalkConnection bwcon, ArrayList<Integer> memberNh, ArrayList<Integer> statusCode)
	{
		String retMsg = null;
        // get the connection
    	ErrorRequestObject erb;

		// get the connection
    	Connection connection = null;
		
		int nhId = -1;
		int memberId = -1;
		int userId = -1;
		int nhLevel = -1;
		
		ArrayList<Whiteboard> wbs = new ArrayList<Whiteboard>();
		Whiteboard wb;

		ArrayList<GridNames> grids = new ArrayList<GridNames>();
		GridNames grid;

		if  (!bwcon.getUserName().equals(email))
		{
			statusCode.add(403);		// forbidden
			erb = new ErrorRequestObject();
			erb.setError("Email does not match with the Authorization.");
			erb.setPath("UserManagement.userGetNeighborhoodCollaborationWhiteboards::getUserName!=email");
			erb.setProposedSolution("The login in Authorization is different than email. The Email must be same as Aughorization UserNname.");
			ErrResps.add(erb);
			System.out.println("The Email must be same as Aughorization UserNname.");				
            //retMsg = "The Email must be same as Aughorization UserNname:" + email;
			return wbs;
		}
		
		connection = bwcon.getConnection();
		memberId = memberNh.get(0);
		nhId = memberNh.get(1);
		userId = bwcon.getUserId();

		byte[] authSetting = Base64.decodeBase64(authBase64String);
		String auth = new String(authSetting);		
		String nhPathAuth = null;
		String[] userLogin = auth.split(":");
		nhPathAuth = userLogin[2];

		if  (!nhPath.equals(nhPathAuth))
		{
			statusCode.add(403);		// forbidden
			erb = new ErrorRequestObject();
			erb.setError("Neighborhood Path does not match with the Authorization nhPath.");
			erb.setPath("UserManagement.userGetNeighborhoodCollaborationWhiteboards::nhPath!=nhPathAuth");
			erb.setProposedSolution("The nhPath in Authorization is different than nhPath in Request. The nhPath must be same as Aughorization Neighbohood Path.");
			ErrResps.add(erb);
			System.out.println("The nhPath must be same as Aughorization Neighbohood Path.");				
            //retMsg = "The Email must be same as Aughorization UserNname:" + email;
			return wbs;
		}

		
		try
		{
	        Neighborhood nh = null;
			nh = NeighborhoodManager.getNeighborhoodById(connection, nhId);

			String nhName = nh.getName();
			String collabName = "";
			String whiteBoard = "";
			String tableName = "";
			String Collabline="";
			int wbId;
			int tableId;

			boolean collabFound = false;
			Vector<?> cl = BoardwalkCollaborationManager.getCollaborationsForNeighborhood(bwcon, nhId);
			Iterator<?> cli = cl.iterator();
			while (cli.hasNext())
			{
				if ( (Integer)cli.next() == collabId)
				{
					BoardwalkCollaborationNode bcn = BoardwalkCollaborationManager.getCollaborationTree(bwcon, collabId);
					collabName = bcn.getName();
					System.out.println("Sucessfully fetched the collab tree from the database");
	
					wbs = new ArrayList<Whiteboard>();
					System.out.println("Collaboration = " + bcn.getName());
					Vector<?> wv = bcn.getWhiteboards();
					Iterator<?> wvi = wv.iterator();
					while ( wvi.hasNext())
					{
						wb = new Whiteboard();
						whiteBoard = "";
						BoardwalkWhiteboardNode bwn = (BoardwalkWhiteboardNode)wvi.next();
						System.out.println("\tWhiteboard = " + bwn.getName());
	
						wb.setId(bwn.getId());
						wb.setName(bwn.getName());
	
						grids = new ArrayList<GridNames>();
						
						Vector<?> tv = bwn.getTables();
						Iterator<?> tvi = tv.iterator();
						if (tvi.hasNext())
						{
							while (tvi.hasNext())
							{
								grid = new GridNames();
								BoardwalkTableNode btn = (BoardwalkTableNode)tvi.next();
								System.out.println("\t\tTable = " + btn.getName());
								grid.setId(btn.getId());
								grid.setName(btn.getName());
								grid.setPurpose(btn.getDescription());
								grids.add(grid);
							}
						}
						wb.setGridList(grids);
						wbs.add(wb);
					}
					collabFound = true;
					break;
				}
			}
			if(collabFound == false)
			{
				statusCode.add(404);			// not found
				erb = new ErrorRequestObject();
				erb.setError("Collaboration Not Found or it is not accessible to User.");
				erb.setPath("UserManagement.userGetNeighborhoodCollaborationWhiteboards::collabFound=false");
				erb.setProposedSolution("The requested Collaboration Does not exist OR not accessible to User.");
				ErrResps.add(erb);
				System.out.println("The requested Collaboration Does not exist OR not accessible to User.");				
	            //retMsg = "The Email must be same as Aughorization UserNname:" + email;
				return wbs;
			}
		}
		catch (NoSuchElementException nse)
		{
			System.out.println("Collaboration of this Id does not exists.");
//			throw new BoardwalkException( 10019 );
		}
		catch(Exception e)
		{
			//start here fix error here
			System.out.println("Collaboration of this Id does not exists.");
//			throw new BoardwalkException( 10019 );
		}
		return wbs;
	}
	
	//GET  ....../user/{email}/neighborhood/{nhPath}/collaborations
	public static ArrayList<Collaboration>  userGetNeighborhoodCollaborations( String email, String nhPath, ArrayList  <ErrorRequestObject> ErrResps, String authBase64String, BoardwalkConnection bwcon, ArrayList<Integer> memberNh)
	{
		String retMsg = null;
        // get the connection
    	ErrorRequestObject erb;

		// get the connection
    	Connection connection = null;
		
		int nhId = -1;
		int memberId = -1;
		int userId = -1;
		int nhLevel = -1;
		
		ArrayList<Collaboration> colbs = new ArrayList<Collaboration>();
		Collaboration colb;

		ArrayList<Whiteboard> wbs = new ArrayList<Whiteboard>();
		Whiteboard wb;

		ArrayList<GridNames> grids = new ArrayList<GridNames>();
		GridNames grid;

		if  (!bwcon.getUserName().equals(email))
		{
			erb = new ErrorRequestObject();
			erb.setError("Email does not match with the Authorization.");
			erb.setPath("UserManagement.userGetNeighborhoodCollaborations::getUserName!=email");
			erb.setProposedSolution("The login in Authorization is different than email. The Email must be same as Aughorization UserNname.");
			ErrResps.add(erb);
			System.out.println("The Email must be same as Aughorization UserNname.");				
            //retMsg = "The Email must be same as Aughorization UserNname:" + email;
			return colbs;
		}
		
		connection = bwcon.getConnection();
		memberId = memberNh.get(0);
		nhId = memberNh.get(1);
		userId = bwcon.getUserId();

		byte[] authSetting = Base64.decodeBase64(authBase64String);
		String auth = new String(authSetting);		
		String nhPathAuth = null;
		String[] userLogin = auth.split(":");
		nhPathAuth = userLogin[2];

		if  (!nhPath.equals(nhPathAuth))
		{
			erb = new ErrorRequestObject();
			erb.setError("Neighborhood Path does not match with the Authorization nhPath.");
			erb.setPath("UserManagement.userGetNeighborhoodCollaborations::nhPath!=nhPathAuth");
			erb.setProposedSolution("The nhPath in Authorization is different than nhPath in Request. The nhPath must be same as Aughorization Neighbohood Path.");
			ErrResps.add(erb);
			System.out.println("The nhPath must be same as Aughorization Neighbohood Path.");				
            //retMsg = "The Email must be same as Aughorization UserNname:" + email;
			return colbs;
		}

		
		try
		{
	        Neighborhood nh = null;
			nh = NeighborhoodManager.getNeighborhoodById(connection, nhId);

			String nhName = nh.getName();
			String collabName = "";
			String whiteBoard = "";

			Vector<?> cl = BoardwalkCollaborationManager.getCollaborationsForNeighborhood(bwcon, nhId);
			Iterator<?> cli = cl.iterator();
			while (cli.hasNext())
			{
				Integer collabId = (Integer)cli.next();
				BoardwalkCollaborationNode bcn = BoardwalkCollaborationManager.getCollaborationTree(bwcon, collabId.intValue());
				collabName = bcn.getName();
				System.out.println("Sucessfully fetched the collab tree from the database");

				colb = new Collaboration();
				colb.setId(bcn.getId());
				colb.setName(bcn.getName());
				colb.setPurpose(bcn.getPurpose());

				wbs = new ArrayList<Whiteboard>();
				System.out.println("Collaboration = " + bcn.getName());
				Vector<?> wv = bcn.getWhiteboards();
				Iterator<?> wvi = wv.iterator();
				while ( wvi.hasNext())
				{
					wb = new Whiteboard();
					whiteBoard = "";
					BoardwalkWhiteboardNode bwn = (BoardwalkWhiteboardNode)wvi.next();
					System.out.println("\tWhiteboard = " + bwn.getName());
					//whiteBoard = bwn.getName();

					wb.setId(bwn.getId());
					wb.setName(bwn.getName());

					//grids = new ArrayList<GridInfo>();
					grids = new ArrayList<GridNames>();
					
					Vector<?> tv = bwn.getTables();
					Iterator<?> tvi = tv.iterator();
					if (tvi.hasNext())
					{
						while (tvi.hasNext())
						{
							//grid = new GridInfo();
							grid = new GridNames();
							BoardwalkTableNode btn = (BoardwalkTableNode)tvi.next();
							System.out.println("\t\tTable = " + btn.getName());
							grid.setId( btn.getId());
							grid.setName(btn.getName());
							grid.setPurpose(btn.getDescription());
							grids.add(grid);
						}
					}
					wb.setGridList(grids);
					wbs.add(wb);
				}
				colb.setWbList(wbs);
				colbs.add(colb);
			}
		}
		catch (NoSuchElementException nse)
		{
			System.out.println("Collaboration of this Id does not exists.");
//			throw new BoardwalkException( 10019 );
		}
		catch(Exception e)
		{
			//start here fix error here
			System.out.println("Collaboration of this Id does not exists.");
//			throw new BoardwalkException( 10019 );
		}
		return colbs;
	}
	*/
	//GET	..../user/{email}/memberships
	public static ArrayList<Membership> userGetMemberships(String email, ArrayList <ErrorRequestObject> ErrResps, String authBase64String , BoardwalkConnection bwcon , ArrayList<Integer> memberNh, ArrayList<Integer> statusCode   )
	{
		String retMsg = null;
        // get the connection
    	ErrorRequestObject erb;

		// get the connection
    	Connection connection = null;
		
		int nhId = -1;
		int memberId = -1;
		int userId = -1;
		int nhLevel = -1;
		
		ArrayList<Membership> ml = new ArrayList<Membership>();

		if  (!bwcon.getUserName().equals(email))
		{
			statusCode.add(403);
			erb = new ErrorRequestObject();
			erb.setError("Email does not match with the Authorization.");
			erb.setPath("UserManagement.userGetMemberships::getUserName!=email");
			erb.setProposedSolution("The login in Authorization is different than email. The Email must be same as Aughorization UserNname.");
			ErrResps.add(erb);
			System.out.println("The Email must be same as Aughorization UserNname.");				
            //retMsg = "The Email must be same as Aughorization UserNname:" + email;
			return ml;
		}
		
		connection = bwcon.getConnection();
		memberId = memberNh.get(0);
		nhId = memberNh.get(1);
		userId = bwcon.getUserId();
		
    	Hashtable<?, ?> ht = new Hashtable<Object, Object>();
        ResultSet rs = null;
        CallableStatement cs  = null;

        try
        {
			cs = connection.prepareCall(CALL_BW_GET_ALL_MEMBERSHIPS_INFO);

			System.out.println("before calling CALL_BW_GET_ALL_MEMBERSHIPS_INFO i.e. "  + CALL_BW_GET_ALL_MEMBERSHIPS_INFO);
			cs.execute();
            rs = cs.getResultSet();
			System.out.println("after calling CALL_BW_GET_ALL_MEMBERSHIPS_INFO");

			//int memberId, userId, nhId, nhLevel;
			String firstName, lastName, Email;

			//System.out.println("before while rs loop");
			Membership ms;
			//NeighborhoodPath np;
			
            while ( rs.next() )
            {
            	if (rs.getInt("UserId") == userId)
            	{
	            	//System.out.println("inside while rs loop");
	                memberId = rs.getInt("MemberId");
	                userId = rs.getInt("UserId");
	                firstName = rs.getString("FirstName");
	                lastName = rs.getString("LastName");
	                Email = rs.getString("Email_Address");
	                nhId = rs.getInt("NhId");
	                nhLevel = rs.getInt("NhLevel");
	
	                
					Vector<?> NhPaths = com.boardwalk.neighborhood.NeighborhoodManager.getBoardwalkPaths( connection , nhId );
					System.out.println("NhPaths.size() -->" + NhPaths.size());
	//				for ( int n = 0; n < NhPaths.size(); n++ )
	//				{
	//					String nhPath = (String)NhPaths.elementAt(n);
	//					System.out.println("nhPath ->" + nhPath);
	//				}
					String nhName  = (String)NhPaths.elementAt(NhPaths.size()-1);
					String nhPath = (String)NhPaths.elementAt(0);
					System.out.println("1. nhPath:" + nhPath);
					System.out.println("2. nhName:" + nhName);
					
	    			ms = new Membership();
	    			ms.setUserEmail(Email);
	    			ms.setMemberId(memberId);
	    			ms.setNhId( nhId);
	    			//ms.setNhIdPath(np.toString());
	    			ms.setNhName(nhName);
	    			ms.setNhNamePath(nhPath);
	    			ms.setUserId(userId);
	                
	                ml.add(ms);
            	}
            }
			System.out.println("outside while rs loop");
        }
        catch(SQLException sqlexception)
        {
			System.out.println(sqlexception.toString());
            //throw new SystemException(sqlexception);
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
               // throw new SystemException(sqlexception1);
            }
        }
        return ml;
	}

	
	//DELETE	....DONE ACORDING TO TEMPLATE
	public static String userUserIdDelete(int userId, ArrayList <ErrorRequestObject> ErrResps, BoardwalkConnection bwcon)
	{
		String retMsg = null;
        // get the connection
    	ErrorRequestObject erb;

		// get the connection
    	Connection connection = null;
		connection = bwcon.getConnection();

        try
        {
            BoardwalkUser bwUser = null ;
			bwUser = BoardwalkUserManager.getUser(bwcon, userId );
			if (bwUser != null)
			{
				MemberManager.deactivateUser(  connection, userId);
	            System.out.println("Successfully desactivated userId  = " + userId);
	            retMsg = "User de-activated successfully. UserId:" + userId;
			}
			else
			{
				erb = new ErrorRequestObject();
				erb.setError("404 - User not found");
				erb.setPath("UserManagement.userUserIdDelete::getUser");
				erb.setProposedSolution("The requested user does not exist. Enter correct UserId.");
				ErrResps.add(erb);
				System.out.println("The requested user does not exist. Enter correct UserId.");				
	            retMsg = "User profile does not exists for UserId:" + userId;
			}
		}
        catch (BoardwalkException bwe)
        {
			System.out.println("Failed to deactivate User.  ErrorCode:" + bwe.getErrorCode() + ", Error Msg:" + bwe.getMessage());
			erb = new ErrorRequestObject();
			erb.setError("BoardwalkException: Contact Boardwak Support.");
			erb.setPath("UserManagement.userUserIdDelete::BoardwalkUserManager.getUser");
			erb.setProposedSolution("Boardwalk Exception. ErrorCode:" + bwe.getErrorCode() + ", Error Msg:" + bwe.getMessage() + ", Solution:" +bwe.getPotentialSolution());
			ErrResps.add(erb);
            retMsg = "Failed to deactivate User:" + userId + ", Boardwalk-ErrorCode:" + bwe.getErrorCode() + ", Error Msg:" + bwe.getMessage();
        }
        catch (SystemException se)
        {
            System.out.println("Failed to deactivate User.  Error Msg:" + se.getErrorMessage()  + ", Potential Solution:" + se.getPotentialSolution());
			erb = new ErrorRequestObject();
			erb.setError("SystemException: Contact Boardwak Support.");
			erb.setPath("UserManagement.userUserIdDelete::MemberManager.deactivateUser");
			erb.setProposedSolution("Boardwalk Exception. Error Msg:" + se.getErrorMessage() + ", Solution:" +se.getPotentialSolution());
			ErrResps.add(erb);
            retMsg = "Failed to deactivate User:" + userId + ",  Error Msg:" + se.getErrorMessage()  + ", Potential Solution:" + se.getPotentialSolution();
        }
        catch (NullPointerException npe)
        {
            System.out.println("UserID not found.  Error Msg:" + npe.getMessage()   + ", Cause:" + npe.getCause());
			erb = new ErrorRequestObject();
			erb.setError("NullPointerException: Contact Boardwak Support.");
			erb.setPath("UserManagement.userUserIdDelete::BoardwalkUserManager.getUser");
			erb.setProposedSolution("NullPointerException.  Error Msg:" + npe.getMessage()  + ", Cause:" + npe.getCause());
			ErrResps.add(erb);
            retMsg = "Failed to deactivate User:" + userId + ",  Error Msg:" + npe.getMessage()  + ", Cause:" + npe.getCause();
        }
    	finally
    	{
    		try
    		{
    			connection.close();
    		}
    		catch (Exception e)
    		{
    			e.printStackTrace();
    		}
    	}
        return retMsg;
	}
	
	//PUT		....DONE ACORDING TO TEMPLATE
	public static String userPut(User u, ArrayList <ErrorRequestObject> ErrResps, BoardwalkConnection bwcon)
	{
		String retMsg = null;
		ErrorRequestObject erb;
		Connection connection = null;
		connection = bwcon.getConnection();
		
		/// CUSTOM CODE START
		int uId = u.getUserId().intValue();
        try
        {
            BoardwalkUser bwUser = null ;
			bwUser  = BoardwalkUserManager.getUser(bwcon, uId );

			if (bwUser != null)
			{
	        	BoardwalkUserManager.updateProfile( bwcon, uId ,  u.getUserEmail(), u.getFirstName(), u.getLastName() );
	            //System.out.println("Successfully updated profile of  = " + u.getId());
	            retMsg = "Successfully updated profile of  = " + u.getUserId();
			}
			else
			{
				erb = new ErrorRequestObject();
				erb.setError("User profile does not exists for UserId:" + uId);
				erb.setPath("UserManagement.userPut::BoardwalkUserManager.getUser");
				erb.setProposedSolution("Enter Valid UserId");
				ErrResps.add(erb);
	            retMsg = "User profile does not exists for UserId:" + uId;
			}
        }
        catch (BoardwalkException bwe)
        {
			erb = new ErrorRequestObject();
			erb.setError("Failed to update user profile for UserId:" + uId);
			erb.setPath("UserManagement.userPut::BoardwalkUserManager.updateProfile");
			erb.setProposedSolution("Check User Details you are trying to Update.  ErrorCode: " + bwe.getErrorCode() + ", Error Msg:" + bwe.getMessage() + ", Solution:" +bwe.getPotentialSolution() );
			ErrResps.add(erb);
            System.out.println("Failed to update user profile. ErrorCode: " + bwe.getErrorCode() + ", Error Msg:" + bwe.getMessage() );
            retMsg = "Failed to update user profile. ErrorCode:" + bwe.getErrorCode() + ", Error Msg:" + bwe.getMessage();
        }
		/// CUSTOM CODE ENDS
		finally
		{
			try
			{
				connection.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}        
        return retMsg;
	}
	
	//POST		....DONE ACORDING TO TEMPLATE
	public static ArrayList<User> userPost(User u, ArrayList <ErrorRequestObject> ErrResps, BoardwalkConnection bwcon )
	{
        ArrayList <User> uList = new ArrayList<User>();
        ErrorRequestObject erb;
		// get the connection
    	Connection connection = null;
		connection = bwcon.getConnection();
		// custom logic starts
        int uId;
        try
        {
        	uId = BoardwalkUserManager.createUser( bwcon, u.getUserEmail(), u.getExternalUserId() ,u.getPassword(), u.getFirstName(), u.getLastName(),  1 );
            if (uId == -1)
            {
                //System.out.println("Failed to created user with = " + u.getEmail());
            	erb = new ErrorRequestObject();
            	erb.setError("Failed to created user with = " + u.getUserEmail());
            	erb.setPath("UserManagement.userPost::BoardwalkUserManager.createUser");
            	erb.setProposedSolution("Check User Details Posted and Submit again");
            	ErrResps.add(erb);
            }
            else
            {
                //System.out.println("Successfully created user with id = " + uId);
	            BoardwalkUser bwUser = null ;
				bwUser  = BoardwalkUserManager.getUser(bwcon, uId );
	
	    		io.swagger.model.User user = new User();
	    		user.setUserId(uId);
	    		user.setFirstName(bwUser.getFirstName());
	    		user.setLastName(bwUser.getLastName());
	    		user.setUserEmail(u.getUserEmail());  
	    		user.setExternalUserId(bwUser.getExtUserName());
				uList.add(user);
            }
        }
        catch (BoardwalkException bwe)
        {
        	erb = new ErrorRequestObject();
        	erb.setError("BoardwalkException Occured. Failed to created user with = " + u.getUserEmail());
        	erb.setPath("UserManagement.userPost::BoardwalkUserManager.createUser");
			erb.setProposedSolution("Boardwalk Exception. ErrorCode:" + bwe.getErrorCode() + ", Error Msg:" + bwe.getMessage() + ", Solution:" +bwe.getPotentialSolution());
        	ErrResps.add(erb);
            //System.out.println("BoardwalkException Occured. Failed to created user with = " + u.getEmail() + ". ErrorCode:" + bwe.getErrorCode() + ", Error Msg:" + bwe.getMessage() + ", Solution:" +bwe.getPotentialSolution());
        }
        	///////// custom code ends
	    finally
    	{
    		try
    		{
    			connection.close();
    		}
    		catch (Exception e)
    		{
    			e.printStackTrace();
    		}
    	}
        return uList;
	}
	
	//GET		....DONE ACORDING TO TEMPLATE
	public static ArrayList<User> userGet(boolean active, ArrayList <ErrorRequestObject> ErrResps, BoardwalkConnection bwcon)
	{
        ArrayList <User> uList = new ArrayList<User>();
        ErrorRequestObject erb;
        
		// get the connection
    	Connection connection = null;
		connection = bwcon.getConnection();

        int intActive;
        intActive = (active == true ? 1 : 0);
		System.out.println("active:" + active + "..........intActive:" + intActive);
		try
		{
			io.swagger.model.User user ;
			Vector<?> userList = BoardwalkUserManager.getUserList(bwcon);
			Iterator<?> ui = userList.iterator();
			while (ui.hasNext())
			{
				BoardwalkUser bu = (BoardwalkUser)ui.next();
				//System.out.println(bu.getId() + ":" + bu.getUserName() + ":" + bu.getFirstName() + ":" + bu.getLastName());
				user = new User();
				if (bu.getActive() == intActive )
				{
					user.setUserId(bu.getId());
					user.setFirstName(bu.getFirstName());
					user.setLastName(bu.getLastName());
					user.setUserEmail(bu.getUserName());
					user.setExternalUserId(bu.getExtUserName());
					//user.setPassword("*****"); //Security Fix - Credentials Management
					uList.add(user);
				}
			}
		}
		catch (BoardwalkException bwe)
		{
        	erb = new ErrorRequestObject();
        	erb.setError("getUserList_Failed");
        	erb.setPath("UserManagement.userGet::getUserList");
        	erb.setProposedSolution("Error fetching User List. Contact Boardwalk System Administrator");
        	ErrResps.add(erb);
			System.out.println("Error fetching User List");
            return uList;
		}
		finally
		{
			try
			{
				connection.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
        return uList;
	}
	
	//GET       ....DONE ACORDING TO TEMPLATE
	public static User userUserIdGet(int userId, ArrayList <ErrorRequestObject> ErrResps, BoardwalkConnection bwcon)
	{
		io.swagger.model.User user = null;
		ErrorRequestObject erb;
		// get the connection
    	Connection connection = null;
		connection = bwcon.getConnection();
		//Custom code starts
        BoardwalkUser bwUser = null ;
		try
		{
			bwUser  = BoardwalkUserManager.getUser(bwcon, userId );
			if (bwUser != null)
			{
				user = new User();
				//System.out.println(bwUser.getFirstName() + "......" + bwUser.getLastName());
				user.setUserId(userId);
				user.setFirstName(bwUser.getFirstName());
				user.setLastName(bwUser.getLastName());
				user.setUserEmail(bwUser.getUserName());
				user.setExternalUserId(bwUser.getExtUserName());
				//user.setPassword("*********"); //Security Fix - Credentials Management
			}
			else
			{
				erb = new ErrorRequestObject();
				erb.setError("404 - User not found");
				erb.setPath("UserManagement.userUserIdGet::getUser");
				erb.setProposedSolution("The requested user does not exist. Enter correct UserId.");
				ErrResps.add(erb);
				System.out.println("The requested user does not exist. Enter correct UserId.");
			}
		}
		catch (BoardwalkException bwe)
		{
			erb = new ErrorRequestObject();
			erb.setError("userUserIdGet_Failed");
			erb.setPath("UserManagement.userUserIdGet::getUser");
			erb.setProposedSolution("Error fetching User. Contact Boardwalk System Administrator");
			ErrResps.add(erb);
			System.out.println("Error fetching neighborhood");
		}
		catch (NullPointerException npe)
		{
			System.out.println("Error fetching neighborhood");
		}
		//Custom code ends
		finally
		{
			try
			{
				connection.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}			
		return user;
	}
}