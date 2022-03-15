package boardwalk.rest;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Vector;

import javax.validation.constraints.NotNull;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import com.boardwalk.collaboration.CollaborationManager;
import com.boardwalk.database.DatabaseLoader;
import com.boardwalk.database.TransactionManager;
import com.boardwalk.exception.BoardwalkException;
import com.boardwalk.exception.SystemException;

import boardwalk.collaboration.BoardwalkCollaborationManager;
import boardwalk.collaboration.BoardwalkCollaborationNode;
import boardwalk.collaboration.BoardwalkTableNode;
import boardwalk.collaboration.BoardwalkWhiteboardNode;
import boardwalk.connection.BoardwalkConnection;
import boardwalk.connection.BoardwalkConnectionManager;
import io.swagger.api.ApiResponseMessage;
import io.swagger.api.NotFoundException;
import io.swagger.model.Collaboration;
import io.swagger.model.ErrorRequestObject;
import io.swagger.model.GridInfo;
import io.swagger.model.Relation;
import io.swagger.model.Whiteboard;

public class CollaborationManagement {

	
	public CollaborationManagement()
	{	
		
	}

	public static ArrayList <Collaboration>  collaborationPost(String collabName, String collabPurpose, ArrayList <ErrorRequestObject> ErrResps, String authBase64String, BoardwalkConnection bwcon, ArrayList<Integer> memberNh, ArrayList<Integer> statusCode)
	{
		
		ArrayList <Collaboration> collabList = new ArrayList<Collaboration>();
		Collaboration collab;
		
		ErrorRequestObject erb;
		//ArrayList <Collaboration> collabList = new ArrayList<Collaboration>();

		// get the connection
    	Connection connection = null;
		
		int loginNhId = -1;
		int loginMemberId = -1;
		int loginUserId = -1;

		connection = bwcon.getConnection();
		loginMemberId = memberNh.get(0);
		loginNhId = memberNh.get(1);
		loginUserId = bwcon.getUserId();

		int collabId = -1;
		TransactionManager tm = null;
		try
		{
			tm = new TransactionManager(connection, loginUserId);
			int tid = tm.startTransaction();

			collabId = CollaborationManager.createCollaboration(
												connection,
												collabName,
												collabPurpose,
												loginMemberId,
												tid,
												1);
			tm.commitTransaction();
			
			com.boardwalk.collaboration.Collaboration clb = CollaborationManager.getCollaborationInfo(connection, loginUserId, collabId);
			
			collab = new Collaboration();
			collab.setCollabId(clb.getId());
			collab.setCollabName(clb.getName());
			collab.setCollabPurpose(clb.getPurpose());
			collabList.add(collab);

			statusCode.add(200);			// 200: 		Success. Collaboration created successfully
		}
		catch (BoardwalkException bwe)
		{
        	System.out.println("A Collaboration with this name already exists within this Neighborhood");
        	erb = new ErrorRequestObject();
        	erb.setError(bwe.getMessage());
        	erb.setPath("CollaborationManagement.collaborationPost::CollaborationManager.createCollaboration");
			erb.setProposedSolution("Boardwalk Exception. ErrorCode:" + bwe.getErrorCode() + ", Error Msg:" + bwe.getMessage() + ", Solution:" +bwe.getPotentialSolution());
        	ErrResps.add(erb);
        	System.out.println("Boardwalk Exception. ErrorCode:" + bwe.getErrorCode() + ", Error Msg:" + bwe.getMessage() + ", Solution:" +bwe.getPotentialSolution());
        	statusCode.add(409);			// 409:			Conflict: Collaboration already exists with this name.

			try
			{
				tm.rollbackTransaction();
			}
			catch (SQLException sqe)
			{
			}
		} 
		catch (SQLException e) 
		{
			// TODO Auto-generated catch block
		} 
		catch (SystemException e) {
			// TODO Auto-generated catch block
		}		
		return collabList;
	}
	
//  @DELETE	--- AUTHORIZATION DONE
//	Path("/{collabId}
	public static void collaborationCollabIdDelete(int collabId, ArrayList <ErrorRequestObject> ErrResps, String authBase64String, BoardwalkConnection bwcon, ArrayList<Integer> memberNh , ArrayList<Integer> statusCode)
    {
		ErrorRequestObject erb;
		//ArrayList <Collaboration> collabList = new ArrayList<Collaboration>();

		// get the connection
    	Connection connection = null;
		
		int loginNhId = -1;
		int loginMemberId = -1;
		int loginUserId = -1;

		connection = bwcon.getConnection();
		loginMemberId = memberNh.get(0);
		loginNhId = memberNh.get(1);
		loginUserId = bwcon.getUserId();
		
    	try
    	{
			//Custom Code Starts
			try
			{
				BoardwalkCollaborationNode bcn = null;
				bcn = BoardwalkCollaborationManager.getCollaborationTree(bwcon, collabId);
				
				if (bcn == null) 
				{
					//throw new NoSuchElementException("Collaboration Id NOT FOUND") ;
					throw new BoardwalkException( 10018 );
				}    		
				System.out.println("Sucessfully fetched the collab tree from the database");
	    		BoardwalkCollaborationManager.deleteCollaboration(bwcon, collabId);
	    		System.out.println("Collaboration Id [" + collabId +  "] Deleted Successfully");
	    		statusCode.add(200);		//200 : Success. Collaboration deleted successfully.
	    		return;
			}
			catch (NoSuchElementException nse)
			{
				System.out.println("CollaborationId  [" + collabId +  "] does not exist.");
				throw new BoardwalkException( 10018 );
			}
    		
    	}
		//Custom code Ends
		catch (BoardwalkException bwe)
		{
        	System.out.println("Collaboration Id not found");
        	erb = new ErrorRequestObject();
        	erb.setError("Collaboration ID NOT FOUND");
        	erb.setPath("CollaborationManagement.collaborationCollabIdDelete::BoardwalkCollaborationManager.getCollaborationTree");
			erb.setProposedSolution("Boardwalk Exception. ErrorCode:" + bwe.getErrorCode() + ", Error Msg:" + bwe.getMessage() + ", Solution:" +bwe.getPotentialSolution());
        	ErrResps.add(erb);
        	System.out.println("Boardwalk Exception. ErrorCode:" + bwe.getErrorCode() + ", Error Msg:" + bwe.getMessage() + ", Solution:" +bwe.getPotentialSolution());
        	statusCode.add(404);		//404:	Collaboration ID NOT FOUND
        	return;
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
    }

//  @GET	-- AUTHORIZATION DONE
//  @Path("/{collabId}/whiteboard")
    public static ArrayList <Collaboration>  collaborationCollabIdWhiteboardGet(int collabId, ArrayList <ErrorRequestObject> ErrResps, String authBase64String, BoardwalkConnection bwcon, ArrayList<Integer> memberNh, ArrayList<Integer> statusCode)
	{
		ErrorRequestObject erb;
		ArrayList <Collaboration> collabList = new ArrayList<Collaboration>();
		// get the connection
    	Connection connection = null;
		
		int loginNhId = -1;
		int loginMemberId = -1;
		int loginUserId = -1;

		connection = bwcon.getConnection();
		loginMemberId = memberNh.get(0);
		loginNhId = memberNh.get(1);
		loginUserId = bwcon.getUserId();
		
    	try
    	{
			//Custom Code Starts
			try
			{
				Collaboration collab = new Collaboration();
				
				ArrayList <Whiteboard> wbList = new ArrayList<Whiteboard>();
				Whiteboard wb ;
				
				BoardwalkCollaborationNode bcn = null;
				bcn = BoardwalkCollaborationManager.getCollaborationTree(bwcon, collabId);
				
    			if (bcn == null) 
    			{
					//throw new NoSuchElementException("Collaboration Id NOT FOUND") ;
    				throw new BoardwalkException( 10018 );
    			}    		
				
				System.out.println("Sucessfully fetched the collab tree from the database");
				
/*				String collabName = bcn.getName();
				String whiteBoard = "";
				String tableName = "";
				String Collabline="";
				int wbId;
				int tableId;*/

				//Long obj = new Long(bcn.getId());
				collab.setCollabId( bcn.getId());
				collab.setCollabName(bcn.getName());

				System.out.println("Collaboration = " + bcn.getName());
				Vector<?> wv = bcn.getWhiteboards();
				Iterator<?> wvi = wv.iterator();
				while ( wvi.hasNext())
				{
					wb = new Whiteboard();
					BoardwalkWhiteboardNode bwn = (BoardwalkWhiteboardNode)wvi.next();
					System.out.println("\tWhiteboard = " + bwn.getName());

					//obj = new Long(bwn.getId());
					wb.setWbId(bwn.getId());
					wb.setWbName(bwn.getName());
					wbList.add(wb);
				}
				collab.setWbList(wbList);
				collabList.add(collab);
	        	statusCode.add(200);		//200:	Success. Returns collabList with List of Whiteboards
	    		return collabList;
			}
			catch (NoSuchElementException nse)
			{
				System.out.println("Collaboration of this Id does not exist.");
				throw new BoardwalkException( 10018 );
			}
    	}
		//Custom code Ends
		catch (BoardwalkException bwe)
		{
        	System.out.println("Collaboration Id not found");
        	erb = new ErrorRequestObject();
        	erb.setError("Collaboration ID NOT FOUND");
        	erb.setPath("CollaborationManagement.collaborationCollabIdWhiteboardGet::BoardwalkCollaborationManager.getCollaborationTree");
			erb.setProposedSolution("Boardwalk Exception. ErrorCode:" + bwe.getErrorCode() + ", Error Msg:" + bwe.getMessage() + ", Solution:" +bwe.getPotentialSolution());
        	ErrResps.add(erb);
        	System.out.println("Boardwalk Exception. ErrorCode:" + bwe.getErrorCode() + ", Error Msg:" + bwe.getMessage() + ", Solution:" +bwe.getPotentialSolution());
        	statusCode.add(404);		//404:	Collaboration ID NOT FOUND
    		return collabList;
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
    }


	//  @POST
	//  @Path("/{collabId}/whiteboard")
    public static int collaborationCollabIdWhiteboardPost(int collabId, Whiteboard wb, ArrayList <ErrorRequestObject> ErrResps, String authBase64String, BoardwalkConnection bwcon, ArrayList<Integer> memberNh, ArrayList<Integer> statusCode) 
	{
    	int wbId = -1;
    	ErrorRequestObject erb;

		// get the connection
    	Connection connection = null;
		int loginNhId = -1;
		int loginMemberId = -1;
		int loginUserId = -1;

		connection = bwcon.getConnection();
		loginMemberId = memberNh.get(0);
		loginNhId = memberNh.get(1);
		loginUserId = bwcon.getUserId();

		//Custom Code Starts
		try
		{
			//Collaboration collab = new Collaboration();
			String wbName = wb.getWbName();
			
			BoardwalkCollaborationNode bcn ;
			bcn = BoardwalkCollaborationManager.getCollaborationTree(bwcon, collabId);
			
			if (bcn == null) 
			{
				System.out.println("Collaboration of this Id does not exist.");
				throw new NoSuchElementException("Collaboration Id NOT FOUND") ;
				//throw new BoardwalkException( 10018 );
			}    		
			
			boolean wbExists = false;
			Vector<?> wv = bcn.getWhiteboards();
			Iterator<?> wvi = wv.iterator();
			while ( wvi.hasNext())
			{
				BoardwalkWhiteboardNode bwn = (BoardwalkWhiteboardNode)wvi.next();
				System.out.println("\tWhiteboard = " + bwn.getName());
			
				if(bwn.getName().trim().toUpperCase().equals(wbName.trim().toUpperCase()))
				{
					wbExists = true;
					break;
				}
			}
		
			if(wbExists == true)
			{
				throw new BoardwalkException( 10011 );
			}
			else
			{
				wbId = BoardwalkCollaborationManager.createWhiteboard(bwcon, wbName, collabId);
				System.out.println("Whiteboard: " + wbName + " is successfully created under Collaboration : " + collabId);
	        	statusCode.add(200);		//200:	Success. Whiteboard created successfully. Returns WhiteboardID
	    		return wbId;
			}
		}
        catch (NoSuchElementException nse)
        {
			System.out.println("Collaboration NOT FOUND.");
			erb = new ErrorRequestObject();
			erb.setError("Collaboration does not exist for this Collaboration Id");
			erb.setPath("NeighborhoodManagement.neighborhoodNhIdMemberMemberIdCollaborationPost::BoardwalkNeighborhoodManager.getNeighborhoodTree");
			erb.setProposedSolution("Use existing Collaboration Id");
			ErrResps.add(erb);
        	statusCode.add(404);		//404:	Collaboration ID NOT FOUND
    		return wbId;
        }	 		    			
		catch (BoardwalkException bwe)
		{
        	System.out.println("Boardwalk Exception. ErrorCode:" + bwe.getErrorCode() + ", Error Msg:" + bwe.getMessage() + ", Solution:" +bwe.getPotentialSolution());
			switch(bwe.getErrorCode())
			{
				case 10011 :
					System.out.println("Whitebord with this name already Exists");
					erb = new ErrorRequestObject();
					erb.setError("Whiteboard already Exists with this name.");
					erb.setPath("CollaborationManagement.collaborationCollabIdWhiteboardPost::bcn.getWhiteboards()");
					erb.setProposedSolution("Boardwalk Exception. ErrorCode:" + bwe.getErrorCode() + ", Error Msg:" + bwe.getMessage() + ", Solution:" +bwe.getPotentialSolution());
					ErrResps.add(erb);
		        	statusCode.add(409);		//409:	Error Condition
		        	break;
			}
    		return wbId;
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
	}

    // @DELETE
    // @Path("/{collabId}/whiteboard/{whiteboardId}
    public static void collaborationCollabIdWhiteboardWhiteboardIdDelete(int collabId, int whiteboardId, ArrayList <ErrorRequestObject> ErrResps, String authBase64String, BoardwalkConnection bwcon, ArrayList<Integer> memberNh , ArrayList<Integer> statusCode) 
    {
		ErrorRequestObject erb;
		// get the connection
    	Connection connection = null;
		
		int loginNhId = -1;
		int loginMemberId = -1;
		int loginUserId = -1;

		connection = bwcon.getConnection();
		loginMemberId = memberNh.get(0);
		loginNhId = memberNh.get(1);
		loginUserId = bwcon.getUserId();
		
    	try
    	{
			//Custom Code Starts   	
			try
			{
				BoardwalkCollaborationNode bcn = null;
				bcn = BoardwalkCollaborationManager.getCollaborationTree(bwcon, collabId);
				
				if (bcn == null) 
				{
					//throw new NoSuchElementException("Collaboration Id NOT FOUND") ;
					throw new BoardwalkException( 10018 );
				}    		
				System.out.println("Sucessfully fetched the collab tree from the database");

				boolean wbExists = false;
				Vector<?> wv = bcn.getWhiteboards();
				Iterator<?> wvi = wv.iterator();
				while ( wvi.hasNext())
				{
					BoardwalkWhiteboardNode bwn = (BoardwalkWhiteboardNode)wvi.next();
					System.out.println("\tWhiteboard = " + bwn.getName());
					
					if(bwn.getId() == whiteboardId)
					{
						wbExists = true;
						break;
					}
				}
			
				if(wbExists == false)
				{
			    	System.out.println("Whiteboard Id not found");
			    	erb = new ErrorRequestObject();
			    	erb.setError("Whiteboard ID NOT FOUND");
			    	erb.setPath("CollaborationManagement.collaborationCollabIdWhiteboardWhiteboardIdDelete::BoardwalkCollaborationNode.getWhiteboards()");
					erb.setProposedSolution("Provide an existing Whiteboard in the Collaboration.");
			    	ErrResps.add(erb);
			    	statusCode.add(404);		//404: Whiteboard not found
			    	return;
				}
				else
				{
					BoardwalkCollaborationManager.deleteWhiteboard(bwcon, whiteboardId);
					System.out.println("Whiteboard: [" + whiteboardId + "] under Collaboration:[" + collabId + "] is successfully Deleted.");
			    	statusCode.add(200);		//200: Success. Whiteboard deleted successfully.
			    	return;
				}
			}
			catch (NoSuchElementException nse)
			{
				System.out.println("CollaborationId  [" + collabId +  "] does not exist.");
				throw new BoardwalkException( 10018 );
			}
		}
		//Custom code Ends
		catch (BoardwalkException bwe)
		{
	    	System.out.println("Collaboration Id not found");
	    	erb = new ErrorRequestObject();
	    	erb.setError("Collaboration ID NOT FOUND");
	    	erb.setPath("CollaborationManagement.collaborationCollabIdWhiteboardWhiteboardIdDelete::BoardwalkCollaborationManager.getCollaborationTree");
			erb.setProposedSolution("Boardwalk Exception. ErrorCode:" + bwe.getErrorCode() + ", Error Msg:" + bwe.getMessage() + ", Solution:" +bwe.getPotentialSolution());
	    	ErrResps.add(erb);
	    	System.out.println("Boardwalk Exception. ErrorCode:" + bwe.getErrorCode() + ", Error Msg:" + bwe.getMessage() + ", Solution:" +bwe.getPotentialSolution());
        	statusCode.add(404);		//404:	Collaboration ID NOT FOUND
    		return;
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
    }

    
 /*   public static ArrayList <Collaboration> collaborationGet(int nhId, ArrayList <ErrorRequestObject> ErrResps) 
    {
		ErrorRequestObject erb;
		ArrayList <Collaboration> collabList = new ArrayList<Collaboration>();

        // get the connection
    	Connection connection = null;
    	try
    	{
    		// Start a connection
    		DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
    		connection = databaseloader.getConnection();
    		// Get an authenticated boardwalk connection
    		BoardwalkConnection bwcon = null;
    		try
    		{
    			String loginName = "admin";
    			String loginPwd = "0";
    		    bwcon = BoardwalkConnectionManager.getBoardwalkConnection(connection, loginName, loginPwd, -1);
    		    System.out.println("Successfully obtained authenticated Boardwalk connection");
    		}
    		catch(BoardwalkException bwe)
    		{
    			erb = new ErrorRequestObject();
    			erb.setError("Authentication_Connection_Failure");
    			erb.setPath("CollaborationManagement.collaborationGet::getBoradwalkConnection");
    			erb.setProposedSolution("Authentication/Connection Failed. Contact Boardwalk System Administrator");
    			ErrResps.add(erb);
    		    System.out.println("Authentication/Connection Failed");
    		    return collabList;
    		}
    		//Custom Code Starts

			try
			{
				Collaboration collab = new Collaboration();
				
				ArrayList <Whiteboard> wbList = new ArrayList<Whiteboard>();
				Whiteboard wb ;
				
				Vector cl = BoardwalkCollaborationManager.getCollaborationsForNeighborhood(bwcon,nhId);
				Iterator cli = cl.iterator();
				while (cli.hasNext())
				{
					Integer collabId = (Integer)cli.next();
					BoardwalkCollaborationNode bcn = BoardwalkCollaborationManager.getCollaborationTree(bwcon, collabId.intValue());
					System.out.println("Sucessfully fetched the collab tree from the database");
					
					System.out.println("Collaboration = " + bcn.getName());
					Vector wv = bcn.getWhiteboards();
					Iterator wvi = wv.iterator();
					while ( wvi.hasNext())
					{
						BoardwalkWhiteboardNode bwn = (BoardwalkWhiteboardNode)wvi.next();
						System.out.println("\tWhiteboard = " + bwn.getName());
						Vector tv = bwn.getTables();
						Iterator tvi = tv.iterator();
						while (tvi.hasNext())
						{
							BoardwalkTableNode btn = (BoardwalkTableNode)tvi.next();
							System.out.println("\t\tTable = " + btn.getName());
						}
					}
				}
			}
			catch (BoardwalkException bwe)
			{
				System.out.println("Error fetching collaboration");
			}

			
			
			
				
				
    			if (bcn == null) 
    			{
					throw new NoSuchElementException("Collaboration Id NOT FOUND") ;
    			}    		
				
				System.out.println("Sucessfully fetched the collab tree from the database");
				
				String collabName = bcn.getName();
				String whiteBoard = "";
				String tableName = "";
				String Collabline="";
				int wbId;
				int tableId;

				Long obj = new Long(bcn.getId());
				collab.setId( obj.longValue());
				collab.setName(bcn.getName());

				System.out.println("Collaboration = " + bcn.getName());
				Vector wv = bcn.getWhiteboards();
				Iterator wvi = wv.iterator();
				while ( wvi.hasNext())
				{
					wb = new Whiteboard();
					BoardwalkWhiteboardNode bwn = (BoardwalkWhiteboardNode)wvi.next();
					System.out.println("\tWhiteboard = " + bwn.getName());

					obj = new Long(bwn.getId());
					wb.setId(obj.longValue());
					wb.setName(bwn.getName());
					wbList.add(wb);
					//collab.setWbList(wbList);
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
				collab.setWbList(wbList);
				collabList.add(collab);
			}
			catch (NoSuchElementException nse)
			{
				System.out.println("Collaboration of this Id does not exists.");
				throw new BoardwalkException( 10018 );
			}
    	}
		//Custom code Ends
		catch (BoardwalkException bwe)
		{
        	System.out.println("Collaboration Id not found");
        	erb = new ErrorRequestObject();
        	erb.setError("Collaboration ID NOT FOUND");
        	erb.setPath("CollaborationManagement.collaborationCollabIdWhiteboardGet::BoardwalkCollaborationManager.getCollaborationTree");
			erb.setProposedSolution("Boardwalk Exception. ErrorCode:" + bwe.getErrorCode() + ", Error Msg:" + bwe.getMessage() + ", Solution:" +bwe.getPotentialSolution());
        	ErrResps.add(erb);
        	System.out.println("Boardwalk Exception. ErrorCode:" + bwe.getErrorCode() + ", Error Msg:" + bwe.getMessage() + ", Solution:" +bwe.getPotentialSolution());
		}
    	
		catch (SQLException sqe)
		{
			erb = new ErrorRequestObject();
			erb.setError("SQLException:" + sqe.getCause());
			erb.setPath("CollaborationManagement.collaborationCollabIdWhiteboardGet::getConnection");
			erb.setProposedSolution("Get DBConnection failed. Contact Boardwalk System Administrator");
			ErrResps.add(erb);
			sqe.printStackTrace();
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
		return collabList;
    }*/
 /*   
    public static void collaborationPost( String neighborhoodSpec, Collaboration collaboration, ArrayList <ErrorRequestObject> ErrRespst) 
    {

    }
   */

    public static  ArrayList <GridInfo> collaborationCollabIdWhiteboardWhiteboardIdGridsGet(Integer collabId, Integer whiteboardId, ArrayList <ErrorRequestObject> ErrResps, String authBase64String, BoardwalkConnection bwcon, ArrayList<Integer> memberNh , ArrayList<Integer> statusCode)
    {
		ErrorRequestObject erb;
    	ArrayList <GridInfo> gisArr = new ArrayList <GridInfo>();
    	GridInfo gis;

		try
		{
			Collaboration collab = new Collaboration();
			boolean wbFound = false;
			ArrayList <Whiteboard> wbList = new ArrayList<Whiteboard>();
			Whiteboard wb ;
			
			BoardwalkCollaborationNode bcn = BoardwalkCollaborationManager.getCollaborationTree(bwcon, collabId);
			System.out.println("Sucessfully fetched the collab tree from the database");
			
			System.out.println("Collaboration = " + bcn.getName());
			Vector wv = bcn.getWhiteboards();
			Iterator wvi = wv.iterator();

			while ( wvi.hasNext())
			{
				BoardwalkWhiteboardNode bwn = (BoardwalkWhiteboardNode)wvi.next();
				
				if (bwn.getId() == whiteboardId)
				{
					wbFound = true;
					System.out.println("\tWhiteboard = " + bwn.getName());
					Vector tv = bwn.getTables();
					Iterator tvi = tv.iterator();
					while (tvi.hasNext())
					{
						BoardwalkTableNode btn = (BoardwalkTableNode)tvi.next();
						System.out.println("\t\tTable = " + btn.getName());
						gis = new GridInfo();
						
						gis.setGridId(btn.getId());
						gis.setGridName(btn.getName());
						gis.setGridPurpose(btn.getDescription());
						gis.setWbId(btn.getWhiteboardId());
						gisArr.add(gis);
					}
					break;
				}
			}
			
			if (!wbFound)
			{
		    	System.out.println("WhiteboardId not found");
		    	erb = new ErrorRequestObject();
		    	erb.setError("WhiteboardId not found");
		    	erb.setPath("CollaborationManagement.collaborationCollabIdWhiteboardWhiteboardIdGridsGet::bcn.getWhiteboards");
				erb.setProposedSolution("Enter Valid WhiteboardId and try again.");
		    	ErrResps.add(erb);
	        	statusCode.add(404);		//404:	WhiteboardId NOT FOUND
	    		return gisArr;
			}
			else
    		{
	        	statusCode.add(200);		//200:	List of GridInfo
		    	return gisArr;
    		}

		}
		catch (BoardwalkException bwe)
		{
	    	System.out.println("Collaboration Id not found");
	    	erb = new ErrorRequestObject();
	    	erb.setError("CollaborationId not found");
	    	erb.setPath("CollaborationManagement.collaborationCollabIdWhiteboardWhiteboardIdGridsGet::BoardwalkCollaborationManager.getCollaborationTree");
			erb.setProposedSolution("Boardwalk Exception. ErrorCode:" + bwe.getErrorCode() + ", Error Msg:" + bwe.getMessage() + ", Solution:" +bwe.getPotentialSolution());
	    	ErrResps.add(erb);
	    	System.out.println("Boardwalk Exception. ErrorCode:" + bwe.getErrorCode() + ", Error Msg:" + bwe.getMessage() + ", Solution:" +bwe.getPotentialSolution());
        	statusCode.add(404);		//404:	Collaboration ID NOT FOUND
    		return gisArr;
		}

    }
    
    public static  ArrayList <GridInfo> collaborationCollabIdGridsGet(Integer collabId, ArrayList <ErrorRequestObject> ErrResps, String authBase64String, BoardwalkConnection bwcon, ArrayList<Integer> memberNh , ArrayList<Integer> statusCode)
    {
		ErrorRequestObject erb;
    	ArrayList <GridInfo> gisArr = new ArrayList <GridInfo>();
    	GridInfo gis;
    	
		try
		{
			Collaboration collab = new Collaboration();
			
			ArrayList <Whiteboard> wbList = new ArrayList<Whiteboard>();
			Whiteboard wb ;
			
			BoardwalkCollaborationNode bcn = BoardwalkCollaborationManager.getCollaborationTree(bwcon, collabId);
			System.out.println("Sucessfully fetched the collab tree from the database");
			
			System.out.println("Collaboration = " + bcn.getName());
			Vector wv = bcn.getWhiteboards();
			Iterator wvi = wv.iterator();

			while ( wvi.hasNext())
			{
				BoardwalkWhiteboardNode bwn = (BoardwalkWhiteboardNode)wvi.next();
				System.out.println("\tWhiteboard = " + bwn.getName());
				Vector tv = bwn.getTables();
				Iterator tvi = tv.iterator();
				while (tvi.hasNext())
				{
					BoardwalkTableNode btn = (BoardwalkTableNode)tvi.next();
					System.out.println("\t\tTable = " + btn.getName());
					gis = new GridInfo();
					
					gis.setGridId(btn.getId());
					gis.setGridName(btn.getName());
					gis.setGridPurpose(btn.getDescription());
					gis.setWbId(btn.getWhiteboardId());
					gisArr.add(gis);
				}
			}
        	statusCode.add(200);		//200:	Success: List of GridInfo
    		return gisArr;
		}
		catch (BoardwalkException bwe)
		{
	    	System.out.println("Collaboration Id not found");
	    	erb = new ErrorRequestObject();
	    	erb.setError("CollaborationId not found");
	    	erb.setPath("CollaborationManagement.collaborationCollabIdGridsGet::BoardwalkCollaborationManager.getCollaborationTree");
			erb.setProposedSolution("Boardwalk Exception. ErrorCode:" + bwe.getErrorCode() + ", Error Msg:" + bwe.getMessage() + ", Solution:" +bwe.getPotentialSolution());
	    	ErrResps.add(erb);
	    	System.out.println("Boardwalk Exception. ErrorCode:" + bwe.getErrorCode() + ", Error Msg:" + bwe.getMessage() + ", Solution:" +bwe.getPotentialSolution());
        	statusCode.add(404);		//404:	Collaboration ID NOT FOUND
    		return gisArr;
		}
    }
}
	
	
