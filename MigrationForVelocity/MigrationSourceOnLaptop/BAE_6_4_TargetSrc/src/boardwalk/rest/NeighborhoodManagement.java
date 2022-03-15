package boardwalk.rest;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Vector;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import com.boardwalk.collaboration.CollaborationManager;
import com.boardwalk.database.DatabaseLoader;
import com.boardwalk.database.TransactionManager;
import com.boardwalk.exception.BoardwalkException;
import com.boardwalk.exception.SystemException;
import com.boardwalk.member.MemberManager;
import com.boardwalk.neighborhood.NeighborhoodId;
import com.boardwalk.neighborhood.NeighborhoodManager;
import com.boardwalk.neighborhood.NeighborhoodRelation;

import boardwalk.collaboration.BoardwalkCollaborationManager;
import boardwalk.collaboration.BoardwalkCollaborationNode;
import boardwalk.collaboration.BoardwalkTableNode;
import boardwalk.collaboration.BoardwalkWhiteboardNode;
import boardwalk.connection.BoardwalkConnection;
import boardwalk.connection.BoardwalkConnectionManager;
import boardwalk.neighborhood.BoardwalkMember;
import boardwalk.neighborhood.BoardwalkNeighborhoodManager;
import boardwalk.neighborhood.BoardwalkNeighborhoodNode;
import boardwalk.neighborhood.BoardwalkUser;
import boardwalk.neighborhood.BoardwalkUserManager;
import io.swagger.api.NotFoundException;
import io.swagger.model.Collaboration;
import io.swagger.model.ErrorRequestObject;
import io.swagger.model.GridInfo;
import io.swagger.model.GridInfo;
import io.swagger.model.User;
import io.swagger.model.Whiteboard;
import io.swagger.model.Neighborhood;
import io.swagger.model.Relation;
import io.swagger.model.Member;

public class NeighborhoodManagement {
	
	public NeighborhoodManagement()
	{	
	}

	static ArrayList <Neighborhood> NhIdGet; 	

	
    //@POST		-- CREATE COLLABORATION USING NH_ID AND MEMBER_ID
    //@Path("/{nhId}/member/{memberId}/collaboration")
	public static int neighborhoodNhIdMemberMemberIdCollaborationPost(int nhId, int memberId, Collaboration collaboration, ArrayList <ErrorRequestObject> ErrResps, String authBase64String, BoardwalkConnection bwcon, ArrayList<Integer> memberNh, ArrayList<Integer> statusCode)
	{
		ErrorRequestObject erb;
		int newCollabId = -1;
		
		// get the connection
    	Connection connection = null;
		
		int loginNhId = -1;
		int loginMemberId = -1;
		int loginUserId = -1;

		connection = bwcon.getConnection();
		loginMemberId = memberNh.get(0);
		loginNhId = memberNh.get(1);
		loginUserId = bwcon.getUserId();

		if (loginMemberId != memberId)
		{
			erb = new ErrorRequestObject();
			erb.setError("Authorization Is not allowed to create Collaboration in this Neighborhood. [MemberID mismatch]");
			erb.setPath("NeighborhoodManagement.neighborhoodNhIdMemberMemberIdCollaborationPost::bwAuthorization.AuthenticateUser");
			erb.setProposedSolution("Authorization Neighbrohood Membership Doest not match. Provide correct Authorization and Try Again.");
			ErrResps.add(erb);
		}

		if (loginNhId != nhId)
		{
			erb = new ErrorRequestObject();
			erb.setError("Authorization Is not allowed to create Collaboration in this Neighborhood. [NhID mismatch]");
			erb.setPath("NeighborhoodManagement.neighborhoodNhIdMemberMemberIdCollaborationPost::bwAuthorization.AuthenticateUser");
			erb.setProposedSolution("Authorization Neighbrohood Membership Doest not match. Provide correct Authorization and Try Again.");
			ErrResps.add(erb);
		}
		
		if (!ErrResps.isEmpty())
		{
			statusCode.add(403);	//403:	Forbidden. 		Authorization Is not allowed to create Collaboration in this Neighborood. Authorization Is not allowed to create Collaboration in this Neighborood. [NhID mismatch]
			return newCollabId;
		}
		
		//Custom Code Starts
		try
		{
			int userId = -1;
			Vector<?> nh0v = BoardwalkNeighborhoodManager.getNeighborhoodTree( bwcon, nhId);
			if (nh0v.isEmpty()) 
			{
				throw new NoSuchElementException("Neighborhood NOT FOUND") ;
			}    		

			boolean memberValid = false;
			Vector<?> mList = BoardwalkNeighborhoodManager.getMemberList(bwcon, nhId);
			System.out.println("Successfully fetched membership list for nh");
			Iterator<?> mi = mList.iterator();
			while (mi.hasNext())
			{
				BoardwalkMember bm = (BoardwalkMember)mi.next();
				if (bm.getId() == memberId)
				{
					userId = bm.getUserId();
					memberValid = true;
					break;
				}
			}
			
			if (memberValid == true )
			{
				String collabName;
				String collabDesc;
				collabName = collaboration.getCollabName();
				collabDesc = collaboration.getCollabPurpose();
				
				//Both Neighborhood and Membership Are Valid. Create Collaboration here....
	    		TransactionManager tm = null;
	    		try
	    		{
	    			tm = new TransactionManager(connection, userId);
	    			int tid = tm.startTransaction();
	    			
	    			newCollabId = CollaborationManager.createCollaboration(connection, collabName, collabDesc, memberId, tid, 1);
	    			tm.commitTransaction();
	    			statusCode.add(200);		//200: 		Success. Collaboration created successfully
	    			return newCollabId ;
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
			}
			else
			{
				throw new NullPointerException("Membership NOT FOUND") ;
			}
			//System.out.println("New Collaboration is created successfully using Membership : " + memberId + " of Neighborhood :" + nhId);
		}
		catch (BoardwalkException bwe)
		{
        	//System.out.println("Failed to create New Collaboration using Membership:" +   memberId + " of Neighborhood :" + nhId);
        	erb = new ErrorRequestObject();
        	erb.setError("Failed to Create New Collaboration");
        	erb.setPath("NeighborhoodManagement.neighborhoodNhIdMemberMemberIdCollaborationPost::CollaborationManager.createCollaboration");
			erb.setProposedSolution("Boardwalk Exception. ErrorCode:" + bwe.getErrorCode() + ", Error Msg:" + bwe.getMessage() + ", Solution:" +bwe.getPotentialSolution());
        	ErrResps.add(erb);
        	System.out.println("Boardwalk Exception. ErrorCode:" + bwe.getErrorCode() + ", Error Msg:" + bwe.getMessage() + ", Solution:" +bwe.getPotentialSolution());
			statusCode.add(500);		//500: 		Failed to Create New Collaboration
			return newCollabId ;
		}
        catch (NoSuchElementException nse)
        {
            System.out.println("The Neighborhood Not Found");
			erb = new ErrorRequestObject();
			erb.setError("NeighborhoodId NOT FOUND ");
			erb.setPath("NeighborhoodManagement.neighborhoodNhIdMemberMemberIdCollaborationPost::BoardwalkNeighborhoodManager.getNeighborhoodTree");
			erb.setProposedSolution("The NeighborhoodId NOT FOUND. You must provide an existing Neigborhood Id.");
			ErrResps.add(erb);
			statusCode.add(404);		//404: 		The Neighborhood Not Found
			return newCollabId ;
		}	 		    			
        catch (NullPointerException npe)
        {
            System.out.println("npe.getMessage(): " + npe.getMessage());
            System.out.println("The Membership Not Found");
			erb = new ErrorRequestObject();
			erb.setError("The Membership NOT FOUND");
			erb.setPath("NeighborhoodManagement.neighborhoodNhIdMemberMemberIdCollaborationPost::BoardwalkNeighborhoodManager.getMemberList");
			erb.setProposedSolution("You must provide an existing MemberId.");
			ErrResps.add(erb);
			statusCode.add(404);		//404: 		The Membership Not Found
			return newCollabId ;
        }	 		    			
		//Custom code Ends
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

    //@GET	-- AUTHORIZATION DONE
    //@Path("/{nhId}/relation")
	public static ArrayList <Relation> 	neighborhoodNhIdRelationGet(int nhId, ArrayList <ErrorRequestObject> ErrResps, String authBase64String, BoardwalkConnection bwcon, ArrayList<Integer> memberNh, ArrayList<Integer> statusCode)
	{
		ErrorRequestObject erb;
		ArrayList <Relation> rels = new ArrayList<Relation>();

        // get the connection
    	Connection connection = null;
		connection = bwcon.getConnection();
    	
		//Custom Code Starts
		try 
		{
			Vector<?> nh0v = BoardwalkNeighborhoodManager.getNeighborhoodTree( bwcon, nhId);
			if (nh0v.isEmpty()) 
			{
				throw new NoSuchElementException("Neighborhood NOT FOUND") ;
			}    		
			
			Relation rel ;
			ArrayList <Neighborhood> relNh;
			Neighborhood nh;
			
			ArrayList<Integer> level_parent;
			
			Hashtable<?, ?>  relationships = NeighborhoodManager.getNeighborhoodRelationships(connection ,nhId );
			Enumeration<?> relationKeys = relationships.keys();
			NeighborhoodId nid;
			Vector<?> v ;
			int intLevel;
			int intParentNh;
			
			if ( relationships.size() > 0 )
			{
				while ( relationKeys.hasMoreElements() )
				{
					rel = new Relation();

					String relationship = (String)relationKeys.nextElement();
					rel.setRelationName(relationship);
					relNh = new ArrayList <Neighborhood>();
					
					v = (Vector<?>) relationships.get(relationship);

					for(int vsize=0; vsize < v.size(); vsize+=1)
					{
						nid = (NeighborhoodId) v.get(vsize);
						System.out.println("id -> "   + nid.getId() + " name -> " + nid.getName());
						intLevel = 0;
						intParentNh = 0;
		    			level_parent = new ArrayList<Integer>();

						GetNhLevelAndParent(connection, nid.getId(), level_parent );
						System.out.println("After GetNhLevelAndParent, intLevel :" + level_parent.get( 0) + " , intParentNh :" + level_parent.get(1));

						//Long obj = new Long(nid.getId());

						nh = new Neighborhood();
						nh.setNhId(nid.getId());
						nh.setNhName(nid.getName());
						
						//obj = new Long(level_parent.get(0));
						nh.setLevel(level_parent.get(0));
						
						//obj = new Long(level_parent.get(1));
						nh.setParentNhId(level_parent.get(1));
						nh.setIsSecure(true);
						relNh.add(nh);
					}
					rel.setRelatedNhId(relNh);
					rels.add(rel);
				}
			}
        	statusCode.add(200);		//200 : Success return rels
        	return rels;
		}
		catch (BoardwalkException bwe)
		{
        	statusCode.add(500);		//500 : Internal Server Error
        	System.out.println("Failed to GET Neighborood Relation of Neighborhood :" + nhId);
        	erb = new ErrorRequestObject();
        	erb.setError("Failed to GET Neighborhood Relations");
        	erb.setPath("NeighborhoodManagement.neighborhoodNhIdRelationGet::BoardwalkNeighborhoodManager.getNeighborhoodTree");
			erb.setProposedSolution("Boardwalk Exception. ErrorCode:" + bwe.getErrorCode() + ", Error Msg:" + bwe.getMessage() + ", Solution:" +bwe.getPotentialSolution());
        	ErrResps.add(erb);
        	System.out.println("Boardwalk Exception. ErrorCode:" + bwe.getErrorCode() + ", Error Msg:" + bwe.getMessage() + ", Solution:" +bwe.getPotentialSolution());
        	return rels;
		}
		
    	catch (SystemException sqe)
		{
    		System.out.println(sqe.getErrorMessage());
		}
        catch (NoSuchElementException nse)
        {
        	statusCode.add(404);		//404 : Neighborhood not found
            System.out.println("The Neighborhood Not Found");
			erb = new ErrorRequestObject();
			erb.setError("NeighborhoodId NOT FOUND ");
			erb.setPath("NeighborhoodManagement.neighborhoodNhIdRelationGet::BoardwalkNeighborhoodManager.getNeighborhoodTree");
			erb.setProposedSolution("The NeighborhoodId NOT FOUND. You must provide an existing Neigborhood Id.");
			ErrResps.add(erb);
			return rels;
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
		return rels;
	}
	
	
	public static void GetNhLevelAndParent(Connection connection, int nhId, ArrayList<Integer> level_parent) 
	{
		Vector<?> NhPathIds = com.boardwalk.neighborhood.NeighborhoodManager.getBoardwalkPathIds( connection , nhId );
		System.out.println("NhPathIds.size() -->" + NhPathIds.size());
		for ( int n = 0; n < NhPathIds.size(); n++ )
		{
			String nhPathId = (String)NhPathIds.elementAt(n);
			System.out.println("nhPathId ->" + nhPathId);
		}
		int nhLevel; int parentNhId;
		
		if (NhPathIds.size() == 1)
		{
			nhLevel = 0;
			parentNhId = -1;
		}
		else
		{
			nhLevel = NhPathIds.size()-1;
			System.out.println("nhLevel -> " + nhLevel);
			//For example 23\45\94\34 for Level-3, 23\45\94 for Level-2. 23\45 for Level-1,   23 for Level-0
			String nhpath = (String) NhPathIds.elementAt(0);	
			System.out.println("Top nhpath ->" + nhpath);
			String nhpathArr[] = nhpath.split("\\\\");
			parentNhId = Integer.parseInt(nhpathArr[nhLevel-1]);
			System.out.println("parentNhId -> (nhpathArr[nhLevel-1] -> " + parentNhId);
		}
		System.out.println("parentNhId ->" + parentNhId);
		level_parent.add(nhLevel);
		level_parent.add(parentNhId);
	
	}
	
	
    //@POST  -- AUTHORIZATION DONE
    //@Path("/{nhId}/relation")
    public static String neighborhoodNhIdRelationPost(int nhId, Relation relationship, ArrayList <ErrorRequestObject> ErrResps, String authBase64String, BoardwalkConnection bwcon, ArrayList<Integer> memberNh, ArrayList<Integer> statusCode )
    {
    	String strRet = null;
        ErrorRequestObject erb;
        
        ErrorRequestObject f_erb;
        ErrorRequestObject s_erb;
        ArrayList <ErrorRequestObject> F_ErrResps = new  ArrayList <ErrorRequestObject>();
        ArrayList <ErrorRequestObject> S_ErrResps = new  ArrayList <ErrorRequestObject>();

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
			
			Vector<?> nh0v = BoardwalkNeighborhoodManager.getNeighborhoodTree( bwcon, nhId);
			if (nh0v.isEmpty()) 
			{
				throw new NoSuchElementException("Neighborhood NOT FOUND") ;
			}    		

			String relation = relationship.getRelationName();
			List <Neighborhood> nhList = relationship.getRelatedNhId();
			Neighborhood nh;
            Vector<Integer> targetNeighborhoods = new Vector<Integer>();

            int r_nhCount = nhList.size();
            Long obj ;
            int r_nhId;
            boolean blnInvalid = false;
            
            for(int index=0 ; index < nhList.size(); index +=1)
			 {
    			nh = nhList.get(index);  
    			obj = new Long(nh.getNhId());
    			r_nhId = obj.intValue();
    			
//				 if (nhId == r_nhId)
//				 {
// 					f_erb = new ErrorRequestObject(); f_erb.setError("SettingRelationWithOwnId"); f_erb.setPath("relationship.nhList[" + index + "].Id : "+ r_nhId); 
// 					f_erb.setProposedSolution("Neighbohood Cannot Set Relation with it's Own Id.");
// 					F_ErrResps.add(f_erb);
// 					blnInvalid = true;
//				 }
				 if (r_nhId <= 0)
				 {
					f_erb = new ErrorRequestObject(); f_erb.setError("IsNegativeOrZero"); f_erb.setPath("relationship.nhList[" + index + "].Id : " + r_nhId); 
					f_erb.setProposedSolution("Neighborhood ID must be a Positive Number of an Existing Neighborhod. ");
					F_ErrResps.add(f_erb);
					blnInvalid = true;
				 }
				 else
				 {
					 nh0v = BoardwalkNeighborhoodManager.getNeighborhoodTree( bwcon, r_nhId);
	    			if (nh0v.isEmpty()) 
	    			{
     					f_erb = new ErrorRequestObject(); f_erb.setError("Error:Neighborhood NOT FOUND"); f_erb.setPath("relationship.nhList[" + index + "].Id : " + r_nhId); 
     					f_erb.setProposedSolution("NhId must be an Existing Neighborhood");
     					F_ErrResps.add(f_erb);
     					blnInvalid = true;
	    			}    		
	    			else
	    			{
	    				targetNeighborhoods.addElement(r_nhId);
	    			}
				 }
			 }

            if (r_nhCount != targetNeighborhoods.size())
            {	//All Neighborhoods in nhList are not Valid.
            	statusCode.add(400);			//400: Bad Request. Invalid Neighborhoods in NhList.
            	erb = new ErrorRequestObject(); erb.setError("Failed to create Neighborhood Relations for Neighborhood List"); erb.setPath("relationship.nhList[]"); 
					erb.setProposedSolution("Neighbohrood List must have all Valid Neighborhoods.");
					ErrResps.add(erb );
					
				for(int ifailure=0; ifailure < F_ErrResps.size(); ifailure+=1)
				{
					ErrResps.add(F_ErrResps.get(ifailure));
				}
				return strRet;
            }
            else			// i.e. (r_nhCount == targetNeighborhoods.size())
            {
        		BoardwalkNeighborhoodManager.createRelation(bwcon, nhId, relation, targetNeighborhoods );

            	//All Relations are successfully created
            	statusCode.add(200);			//200: Success. Neighborhood Relation created successfully for all Neighborhoods in NhList.
            	erb = new ErrorRequestObject(); erb.setError("All Neighborhood Relations Created Successfully"); erb.setPath("relationship.nhList[].Id"); 
				erb.setProposedSolution("Relations created successfully for all Neighborhood Ids.");
				ErrResps.add(erb );

	            for(int tindex=0 ; tindex < targetNeighborhoods.size(); tindex +=1)
    			 {
	            	s_erb = new ErrorRequestObject(); s_erb.setError("Success"); s_erb.setPath("relationship.nhList[" + tindex + "].Id : "+targetNeighborhoods.get(tindex)); 
 					s_erb.setProposedSolution("Neighborhood Relations created successfully");
 					ErrResps.add(s_erb);
    			 }
				return strRet;
			}
		}
		catch (BoardwalkException bwe)
		{
        	statusCode.add(409);			//409: Conflict
        	System.out.println("Creation of Neighborhood Relation Failed");
        	erb = new ErrorRequestObject();
        	erb.setError("Creation of Neighborhood Relation Failed");
        	erb.setPath("NeighborhoodManagement.neighborhoodNhIdRelationPost::BoardwalkNeighborhoodManager.createRelation");
			erb.setProposedSolution("Boardwalk Exception. ErrorCode:" + bwe.getErrorCode() + ", Error Msg:" + bwe.getMessage() + ", Solution:" +bwe.getPotentialSolution());
        	ErrResps.add(erb);
        	System.out.println("Boardwalk Exception. ErrorCode:" + bwe.getErrorCode() + ", Error Msg:" + bwe.getMessage() + ", Solution:" +bwe.getPotentialSolution());
		}
        catch (NoSuchElementException nse)
        {
        	statusCode.add(404);			//404: Neighborhood Not found
            System.out.println("The Neighborhood Not Found");
			erb = new ErrorRequestObject();
			erb.setError("NeighborhoodId NOT FOUND ");
			erb.setPath("NeighborhoodManagement.neighborhoodNhIdRelationPost::BoardwalkNeighborhoodManager.getNeighborhoodTree");
			erb.setProposedSolution("The NeighborhoodId NOT FOUND. You must provide an existing Neigborhood Id.");
			ErrResps.add(erb);
        }	 		    			
		//Custom code Ends
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
    	return strRet;
    }
  
    
    //@DELETE	-- AUTHORIZATION DONE
    //@Path("/{nhId}/relation/{member}")
    public static String neighborhoodNhIdRelationDelete (int nhId, String relation, ArrayList <ErrorRequestObject> ErrResps, String authBase64String, BoardwalkConnection bwcon, ArrayList<Integer> memberNh, ArrayList<Integer> statusCode  )
    {
		String retMsg = null;
        ErrorRequestObject erb;
        // get the connection
        
		// get the connection
    	Connection connection = null;
		
		int memberNhId = -1;
		int memberId = -1;
		int userId = -1;

		connection = bwcon.getConnection();
		memberId = memberNh.get(0);
		memberNhId = memberNh.get(1);
		userId = bwcon.getUserId();
		//Custom Code Starts

		try 
		{
			Vector<?> nh0v = BoardwalkNeighborhoodManager.getNeighborhoodTree( bwcon, nhId);
			if (nh0v.isEmpty()) 
			{
				throw new NoSuchElementException("Neighborhood NOT FOUND") ;
			}    		
			
			Relation rel ;
			ArrayList <Neighborhood> relNh;
			Neighborhood nh;
			
			ArrayList<Integer> level_parent;
			
			Hashtable<?, ?> relationships = new Hashtable<Object, Object>();
			try 
			{
				relationships = NeighborhoodManager.getNeighborhoodRelationships(connection, nhId );
			} 
			catch (SystemException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			Enumeration<?> relationKeys = relationships.keys();
			
			if (relationships.containsKey(relation))
			{
				System.out.println("relation found ");
				BoardwalkNeighborhoodManager.deleteRelation(bwcon, nhId, relation);
				statusCode.add(404);		//404 : Not Found. Relation not found
				retMsg = "Relation [" + relation + "] Deleted Successfully";
				//retMsg = ("Relation " + relation + " deleted successfully");
			}
			else
			{
				statusCode.add(200);		//200 : Success
				retMsg = "Relation NOT found";
				System.out.println("relation NOT found ");
			}
			return retMsg;
		}
		catch(BoardwalkException bwe)
		{
        	statusCode.add(500);			//500 : Server Internal Error . Failed to Delete Neighbourhood
			System.out.println("Failed to Delete Neighbourhood.  ErrorCode:" + bwe.getErrorCode() + ", Error Msg:" + bwe.getMessage());
			erb = new ErrorRequestObject();
			erb.setError("BoardwalkException: Contact Boardwak Support.");
			erb.setPath("NeighborhoodManagement.neighborhoodNhIdRelationDelete::BoardwalkNeighborhoodManager.deleteNeighborhood");
			erb.setProposedSolution("Boardwalk Exception. ErrorCode:" + bwe.getErrorCode() + ", Error Msg:" + bwe.getMessage() + ", Solution:" +bwe.getPotentialSolution());
			ErrResps.add(erb);
            retMsg = "Failed to Delete Neighborhood Relation:" + relation + " of Neighborhood : " + nhId + ", Boardwalk-ErrorCode:" + bwe.getErrorCode() + ", Error Msg:" + bwe.getMessage();
		}
        catch (NoSuchElementException nse)
        {
        	statusCode.add(404);			//404 : Not Found . Neighbhorhood not found
        	System.out.println("The Neighborhood Not Found");
			erb = new ErrorRequestObject();
			erb.setError("NeighborhoodId NOT FOUND ");
			erb.setPath("NeighborhoodManagement.neighborhoodNhIdRelationDelete::BoardwalkNeighborhoodManager.getNeighborhoodTree");
			erb.setProposedSolution("The NeighborhoodId NOT FOUND. You must provide an existing Neigborhood Id.");
			ErrResps.add(erb);
			return retMsg;
        }	 		    			
		
		//Custom Code Ends
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
    
    //@DELETE	-- AUTHORIZATION DONE
    //@Path("/{nhId}/member/{member}")
	public static String neighborhoodNhIdMemberMemberIdDelete(int nhId, int memberId, ArrayList <ErrorRequestObject> ErrResps, String authBase64String, BoardwalkConnection bwcon, ArrayList<Integer> memberNh, ArrayList<Integer> statusCode)
	{
		String strRet = null;
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
			Vector<?> nh0v = BoardwalkNeighborhoodManager.getNeighborhoodTree( bwcon, nhId);
			if (nh0v.isEmpty()) 
			{
				throw new NoSuchElementException("Neighborhood NOT FOUND") ;
			}    		

			boolean memberValid = false;
			Vector<?> mList = BoardwalkNeighborhoodManager.getMemberList(bwcon, nhId);
			System.out.println("Successfully fetched membership list for nh");
			Iterator<?> mi = mList.iterator();
			while (mi.hasNext())
			{
				BoardwalkMember bm = (BoardwalkMember)mi.next();
				if (bm.getId() == memberId)
				{
					memberValid = true;
					break;
				}
			}
			
			if (memberValid == true )
			{
    			BoardwalkNeighborhoodManager.deleteMember(bwcon, memberId);
			}
			else
			{
				throw new NullPointerException("Membership NOT FOUND") ;
			}
			System.out.println("Membership deleted Successfully form Neighborhood :" + nhId);
			strRet = "Neighborhood Membership : " + memberId + " of Neighborhood: " + nhId + " is Successfully Deleted.";
			statusCode.add(200);		//200:	Success. Membership deleted Successfully
			return strRet;
		}
		catch (BoardwalkException bwe)
		{
        	System.out.println("Deleting membership of Neighborhood Failed");
        	erb = new ErrorRequestObject();
        	erb.setError("Deleting membership of Neighborhood Failed");
        	erb.setPath("NeighborhoodManagement.neighborhoodNhIdMemberMemberIdDelete::BoardwalkNeighborhoodManager.getNeighborhoodTree");
			erb.setProposedSolution("Boardwalk Exception. ErrorCode:" + bwe.getErrorCode() + ", Error Msg:" + bwe.getMessage() + ", Solution:" +bwe.getPotentialSolution());
        	ErrResps.add(erb);
        	System.out.println("Boardwalk Exception. ErrorCode:" + bwe.getErrorCode() + ", Error Msg:" + bwe.getMessage() + ", Solution:" +bwe.getPotentialSolution());
        	statusCode.add(500);			//500: Server Error. Deleting membership of Neighborhood Failed
        	return strRet;
		}
        catch (NoSuchElementException nse)
        {
            System.out.println("The Neighborhood Not Found");
			erb = new ErrorRequestObject();
			erb.setError("NeighborhoodId NOT FOUND ");
			erb.setPath("NeighborhoodManagement.neighborhoodNhIdMemberMemberIdDelete::BoardwalkNeighborhoodManager.getNeighborhoodTree");
			erb.setProposedSolution("The NeighborhoodId NOT FOUND. You must provide an existing Neigborhood Id.");
			ErrResps.add(erb);
        	statusCode.add(404);			//404: The Neighborhood Not Found
        	return strRet;
        }	 		    			
        catch (NullPointerException npe)
        {
            System.out.println("npe.getMessage(): " + npe.getMessage());
            System.out.println("The Membership Not Found");
			erb = new ErrorRequestObject();
			erb.setError("The Membership NOT FOUND");
			erb.setPath("NeighborhoodManagement.neighborhoodNhIdMemberMemberIdDelete::BoardwalkNeighborhoodManager.getMemberList");
			erb.setProposedSolution("You must provide an existing MemberId.");
			ErrResps.add(erb);
        	statusCode.add(404);			//404: The Membership Not Found
        	return strRet;
        }	 		    			
		//Custom code Ends
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
    	//return strRet;
	}
	
    //@POST	-- AUTHORIZATION DONE
    //@Path("/{nhId}/member")
	public static ArrayList<Member> neighborhoodNhIdMemberPost(int nhId, Member member, ArrayList <ErrorRequestObject> ErrResps, String authBase64String, BoardwalkConnection bwcon, ArrayList<Integer> memberNh, ArrayList<Integer> statusCode )
	{
		ArrayList <Member> memberList = new ArrayList<Member>();
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
			Vector<?> nh0v = BoardwalkNeighborhoodManager.getNeighborhoodTree( bwcon, nhId);
			if (nh0v.isEmpty()) 
			{
				throw new NoSuchElementException("Neighborhood NOT FOUND") ;
			}    		

			int memberUserId = member.getUserId();
			Long obj = new Long(memberUserId );

			BoardwalkUser bu = null;
			bu = BoardwalkUserManager.getUser(bwcon, obj.intValue());
			if (bu == null)
			{
				throw new NullPointerException("User NOT FOUND") ;
			}
			
			int memberId = BoardwalkNeighborhoodManager.createMember(bwcon, nhId, obj.intValue());
			if (memberId == -1)
				throw new BoardwalkException(10017);
			else
			{
				Member m = new Member();
				m.setIsActive(true);
				//obj = new Long(memberId);
				m.setMemberId(memberId);
				//obj = new Long(nhId);
				m.setNhId(nhId);
				m.setUserId(memberUserId);
				memberList.add(m);
			}
			System.out.println("Successfully created Membership for User:" + memberUserId + " under Nh:" + nhId);
			statusCode.add(200);		//200: Success Membership created
			return memberList;
		}
		catch (BoardwalkException bwe)
		{
        	erb = new ErrorRequestObject();
        	erb.setError("Creating new membership to Neighborhood Failed");
        	erb.setPath("NeighborhoodManagement.neighborhoodNhIdMemberPost::BoardwalkNeighborhoodManager.getNeighborhoodTree");
			erb.setProposedSolution("Boardwalk Exception. ErrorCode:" + bwe.getErrorCode() + ", Error Msg:" + bwe.getMessage() + ", Solution:" +bwe.getPotentialSolution());
        	ErrResps.add(erb);
        	System.out.println("Boardwalk Exception. ErrorCode:" + bwe.getErrorCode() + ", Error Msg:" + bwe.getMessage() + ", Solution:" +bwe.getPotentialSolution());
			statusCode.add(500);		//500: Server Internal Error
            return memberList;
		}
        catch (NoSuchElementException nse)
        {
            System.out.println("The Neighborhood Not Found");
			erb = new ErrorRequestObject();
			erb.setError("NeighborhoodId NOT FOUND ");
			erb.setPath("NeighborhoodManagement.neighborhoodNhIdMemberPost::BoardwalkNeighborhoodManager.getNeighborhoodTree");
			erb.setProposedSolution("The NeighborhoodId NOT FOUND. You must provide an existing Neigborhood Id.");
			ErrResps.add(erb);
			statusCode.add(404);		//404: Neighborhood Not Found
            return memberList;
        }	 		    			
        catch (NullPointerException npe)
        {
            System.out.println("npe.getMessage(): " + npe.getMessage());
            System.out.println("The User Not Found");
			erb = new ErrorRequestObject();
			erb.setError("The User NOT FOUND");
			erb.setPath("NeighborhoodManagement.neighborhoodNhIdMemberPost::BoardwalkUserManager.getUser");
			erb.setProposedSolution("You must provide an existing UserId.");
			ErrResps.add(erb);
			statusCode.add(404);		//404: User Not Found
            return memberList;
        }	 		    			
		//Custom Code Ends
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
	
	//@DELETE -- AUTHORIZATION DONE
    //@Path("/{nhId}")
	public static String neighborhoodNhIdDelete(int nhId, ArrayList <ErrorRequestObject> ErrResps, String authBase64String, BoardwalkConnection bwcon , ArrayList<Integer> memberNh, ArrayList<Integer> statusCode)
	{
		String retMsg = null;
        ErrorRequestObject erb;
        // get the connection
    	Connection connection = null;
		
		int memberNhId = -1;
		int memberId = -1;
		int userId = -1;

		connection = bwcon.getConnection();
		memberId = memberNh.get(0);
		memberNhId = memberNh.get(1);
		userId = bwcon.getUserId();

		//Custom Code Starts
		boolean retVal = true;
		try
		{
			Vector<?> nh0v = BoardwalkNeighborhoodManager.getNeighborhoodTree( bwcon, nhId);
			if (nh0v.isEmpty()) 
			{
				throw new NoSuchElementException("Neighborhood NOT FOUND") ;
			}    			
			BoardwalkNeighborhoodManager.deleteNeighborhood(bwcon, nhId );
			retMsg = "Neighborhood Deleted Successfully. All it's dependent objects are purged successfully.";
			statusCode.add(200);		//200: Success. Neighborhood Deleted Successfully. All it's dependent objects are purged successfully.
			return retMsg;
		}
        catch (BoardwalkException bwe)
        {
			System.out.println("Failed to Delete Neighbourhood.  ErrorCode:" + bwe.getErrorCode() + ", Error Msg:" + bwe.getMessage());
			erb = new ErrorRequestObject();
			erb.setError("BoardwalkException: Contact Boardwak Support.");
			erb.setPath("NeighborhoodManagement.neighborhoodNhIdDelete::BoardwalkNeighborhoodManager.deleteNeighborhood");
			erb.setProposedSolution("Boardwalk Exception. ErrorCode:" + bwe.getErrorCode() + ", Error Msg:" + bwe.getMessage() + ", Solution:" +bwe.getPotentialSolution());
			ErrResps.add(erb);
            retMsg = "Failed to Delete Neighborhood:" + nhId + ", Boardwalk-ErrorCode:" + bwe.getErrorCode() + ", Error Msg:" + bwe.getMessage();
			statusCode.add(500);		//500 : Failed to Delete Neighborhood. BoardwalkException: Contact Boardwak Support.
			return retMsg;
        }
        catch (NoSuchElementException nse)
        {
            System.out.println("The Neighborhood Not Found");
			erb = new ErrorRequestObject();
			erb.setError("NeighborhoodId NOT FOUND ");
			erb.setPath("NeighborhoodManagement.neighborhoodNhIdDelete::BoardwalkNeighborhoodManager.deleteNeighborhood");
			erb.setProposedSolution("The NeighborhoodId NOT FOUND. You must provide an existing Neigborhood Id.");
			ErrResps.add(erb);
			retMsg = "Failed to Delete Neighborhood:" + nhId + ". Neighborhood NOT FOUND.";
			statusCode.add(404);		//404 : NeighborhoodId NOT FOUND
			return retMsg;
        }    		
		//Custom Code Ends
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

	//@GET	-- -- AUTHORIZATION DONE
	//@Path("/{nhId}/member")
	public static ArrayList<Member> neighborhoodNhIdMemberGet(int nhId, ArrayList <ErrorRequestObject> ErrResps, String authBase64String, BoardwalkConnection bwcon, ArrayList<Integer> memberNh, ArrayList<Integer> statusCode)
	{
        ArrayList <Member> memberList = new ArrayList<Member>();
        ErrorRequestObject erb;
		// get the connection
    	Connection connection = null;
		
		int memberNhId = -1;
		int memberId = -1;
		int userId = -1;

		connection = bwcon.getConnection();
		memberId = memberNh.get(0);
		memberNhId = memberNh.get(1);
		userId = bwcon.getUserId();
		
		//Custom Code Starts
		try
		{
			Vector<?> nh0v = BoardwalkNeighborhoodManager.getNeighborhoodTree( bwcon, nhId);
			if (nh0v.isEmpty()) 
			{
				throw new NoSuchElementException("Neighborhood NOT FOUND") ;
			}    			
			Integer obj;
			io.swagger.model.Member mem ;
			Vector<?> mList = BoardwalkNeighborhoodManager.getMemberList(bwcon, nhId);
			System.out.println("Successfully fetched membership list for nh");
			Iterator<?> mi = mList.iterator();
			while (mi.hasNext())
			{
				BoardwalkMember bm = (BoardwalkMember)mi.next();
				mem = new io.swagger.model.Member();
    			//obj = new Integer(bm.getId());
				mem.setMemberId(bm.getId());
				//obj = new Integer(bm.getUserId());
				mem.setUserId(bm.getUserId());
    			//obj = new Integer(bm.getNeighborhoodId());
				mem.setNhId(bm.getNeighborhoodId());
				memberList.add(mem);
			}
			statusCode.add(200);		//200 : Success. The member list returned
			return memberList;
		}
		catch (BoardwalkException bwe)
		{
        	erb = new ErrorRequestObject();
        	erb.setError("Get Neighborhood Members Failed");
        	erb.setPath("NeighborhoodManagement.neighborhoodNhIdMemberGet::BoardwalkNeighborhoodManager.getNeighborhoodTree");
			erb.setProposedSolution("Boardwalk Exception. ErrorCode:" + bwe.getErrorCode() + ", Error Msg:" + bwe.getMessage() + ", Solution:" +bwe.getPotentialSolution());
        	ErrResps.add(erb);
        	System.out.println("Boardwalk Exception. ErrorCode:" + bwe.getErrorCode() + ", Error Msg:" + bwe.getMessage() + ", Solution:" +bwe.getPotentialSolution());
			statusCode.add(500);		//500 : Get Neighborhood Members Failed
            return memberList;
		}
        catch (NoSuchElementException nse)
        {
            System.out.println("The Neighborhood Not Found");
			erb = new ErrorRequestObject();
			erb.setError("NeighborhoodId NOT FOUND ");
			erb.setPath("NeighborhoodManagement.neighborhoodNhIdMemberGet::BoardwalkNeighborhoodManager.getNeighborhoodTree");
			erb.setProposedSolution("The NeighborhoodId NOT FOUND. You must provide an existing Neigborhood Id.");
			ErrResps.add(erb);
			statusCode.add(404);		//404 : The Neighborhood Not Found
			return memberList;
        }	 	
		//Custom Code Ends
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
	
	//@GET	-- AUTHORIZATION DONE
    //@Path("/{nhId}")
	public static ArrayList<Neighborhood> neighborhoodNhIdGet(int nhId, ArrayList <ErrorRequestObject> ErrResps, String authBase64String, BoardwalkConnection bwcon, ArrayList<Integer> memberNh, ArrayList<Integer> statusCode)
	{
        ErrorRequestObject erb;
		// get the connection
    	Connection connection = null;
		
		int memberNhId = -1;
		int memberId = -1;
		int userId = -1;

		connection = bwcon.getConnection();
		memberId = memberNh.get(0);
		memberNhId = memberNh.get(1);
		userId = bwcon.getUserId();
		
		//Custom Code Starts
		try
		{
			NhIdGet = new ArrayList<Neighborhood>();

			Vector<?> nh0v = BoardwalkNeighborhoodManager.getNeighborhoodTree( bwcon, nhId);
			if (nh0v.isEmpty()) 
			{
				throw new NoSuchElementException("Parent Neighborhood NOT FOUND") ;
			}
			else
			{
    			Iterator<?> nh0i = nh0v.iterator();
    			while (nh0i.hasNext())
    			{
    				BoardwalkNeighborhoodNode bnn = (BoardwalkNeighborhoodNode)nh0i.next();
    				
    				Vector<?> NhPaths = com.boardwalk.neighborhood.NeighborhoodManager.getBoardwalkPaths( connection , nhId );
					System.out.println("NhPaths.size() -->" + NhPaths.size());
    				for ( int n = 0; n < NhPaths.size(); n++ )
					{
						String nhPath = (String)NhPaths.elementAt(n);
						System.out.println("nhPath ->" + nhPath);
					}
    				Vector<?> NhPathIds = com.boardwalk.neighborhood.NeighborhoodManager.getBoardwalkPathIds( connection , nhId );
					System.out.println("NhPathIds.size() -->" + NhPathIds.size());
    				for ( int n = 0; n < NhPathIds.size(); n++ )
					{
						String nhPathId = (String)NhPathIds.elementAt(n);
						System.out.println("nhPathId ->" + nhPathId);
					}
    				int nhLevel; int parentNhId;
    				
    				if (NhPathIds.size() == 1)
    				{
    					nhLevel = 0;
    					parentNhId = -1;
    				}
    				else
    				{
    					nhLevel = NhPathIds.size()-1;
    					System.out.println("nhLevel -> " + nhLevel);
    					//For example 23\45\94\34 for Level-3, 23\45\94 for Level-2. 23\45 for Level-1,   23 for Level-0
    					String nhpath = (String) NhPathIds.elementAt(0);	
    					System.out.println("Top nhpath ->" + nhpath);
    					String nhpathArr[] = nhpath.split("\\\\");
    					parentNhId = Integer.parseInt(nhpathArr[nhLevel-1]);
    					System.out.println("parentNhId -> (nhpathArr[nhLevel-1] -> " + parentNhId);
    				}
    				System.out.println("parentNhId ->" + parentNhId);
    				
    				collectNH(bnn, parentNhId);
    				System.out.println("NhIdGet.size() ->"  + NhIdGet.size());
    			}
    			statusCode.add(200);		//200: Success Neighborhood collection returned.
    			return NhIdGet;
			}
		}
		catch (BoardwalkException bwe)
		{
			System.out.println("Error fetching neighborhood");
			erb = new ErrorRequestObject();
			erb.setError("Error fetching neighborhood");
			erb.setPath("NeighborhoodManagement.neighborhoodNhIdGet::BoardwalkNeighborhoodManager.getNeighborhoodTree");
			erb.setProposedSolution("Serverside Error. Could not fetch Neighborhood. Report Administratior");
			ErrResps.add(erb);
			statusCode.add(500);		//500:	Server-side Error fetching Neighborhood
			return NhIdGet;
		}
        catch (NoSuchElementException nse)
        {
            System.out.println("The Neighborhood Not Found");
			erb = new ErrorRequestObject();
			erb.setError("NeighborhoodId NOT FOUND ");
			erb.setPath("NeighborhoodManagement.neighborhoodNhIdGet::BoardwalkNeighborhoodManager.getNeighborhoodTree");
			erb.setProposedSolution("The NeighborhoodId NOT FOUND. You must provide an existing Neigborhood Id.");
			ErrResps.add(erb);
			statusCode.add(404);		//404:	The Neighborhood Not Found.
			return NhIdGet;
        }
		//Custom Code Ends
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


    private static void collectNH(BoardwalkNeighborhoodNode bnn, int parent)
    {
		int par; 
		int level;
		for (int i = 0; i<= bnn.getNeighborhood().getLevel(); i++)
		{
			System.out.print("\t");
//			sb.append("\t");
		}

		//Integer obj;
		Neighborhood newNh = new Neighborhood();
		newNh.setNhId(bnn.getNeighborhood().getId());
		newNh.setLevel(bnn.getNeighborhood().getLevel());
		newNh.setNhName(bnn.getNeighborhood().getName());
		newNh.setParentNhId(parent);
		newNh.setIsSecure(bnn.getNeighborhood().isSecure());
		
		NhIdGet.add(newNh);
		
		par = bnn.getNeighborhood().getId();
		level = bnn.getNeighborhood().getLevel();
		System.out.println(bnn.getNeighborhood().getName() + " id=" + bnn.getNeighborhood().getId());
//		sb.append(bnn.getNeighborhood().getName() + "|" + bnn.getNeighborhood().getId() + "|"+ parent + "|"+ level + "\n" );

		Vector<?> children = bnn.getChildren();
		Iterator<?> ci = children.iterator();
		while (ci.hasNext())
		{
			BoardwalkNeighborhoodNode bnnc = (BoardwalkNeighborhoodNode)ci.next();
			collectNH(bnnc, par);
		}
	}	
	
	
	//Generate StringBuffer of NH Structure as 'NhName|NhId|ParentNhId|Level'
    private static void printNH(BoardwalkNeighborhoodNode bnn, int parent)
    {
		int par; 
		int level;
		for (int i = 0; i<= bnn.getNeighborhood().getLevel(); i++)
		{
			System.out.print("\t");
//			sb.append("\t");
		}
		par = bnn.getNeighborhood().getId();
		level = bnn.getNeighborhood().getLevel();
		System.out.println(bnn.getNeighborhood().getName() + " id=" + bnn.getNeighborhood().getId());
//		sb.append(bnn.getNeighborhood().getName() + "|" + bnn.getNeighborhood().getId() + "|"+ parent + "|"+ level + "\n" );

		Vector<?> children = bnn.getChildren();
		Iterator<?> ci = children.iterator();
		while (ci.hasNext())
		{
			BoardwalkNeighborhoodNode bnnc = (BoardwalkNeighborhoodNode)ci.next();
			printNH(bnnc, par);
		}
	}	

	
    //@POST	-- AUTHORIZATION DONE
	public static ArrayList<Neighborhood>  neighborhoodPost(String nhName, int parentNhId, boolean isSecure,  ArrayList <ErrorRequestObject> ErrResps, String authBase64String, BoardwalkConnection bwcon, ArrayList<Integer> memberNh, ArrayList<Integer> statusCode )
	{
        ArrayList <Neighborhood> nhList = new ArrayList<Neighborhood>();
        ErrorRequestObject erb;

		// get the connection
    	Connection connection = null;
		connection = bwcon.getConnection();

		try 
		{
			int level = 0 ;
			//Check if Parent NeighborhoodId Exists. If exists then get Nh Level.
			if (parentNhId != -1)
			{
    			Vector<?> nh0v = BoardwalkNeighborhoodManager.getNeighborhoodTree( bwcon, parentNhId);
    			if (nh0v.isEmpty()) 
    			{
					throw new NoSuchElementException("Parent Neighborhood NOT FOUND") ;
    			}
    			else {
        			Iterator<?> nh0i = nh0v.iterator();
    				BoardwalkNeighborhoodNode bnn = (BoardwalkNeighborhoodNode)nh0i.next();
    				level = bnn.getNeighborhood().getLevel();
    			}
			}	
			
			int nhId = -1;
			nhId = BoardwalkNeighborhoodManager.createNeighborhood( bwcon, nhName, isSecure, parentNhId);

			if (nhId <= 0)
			{
				throw new BoardwalkException( 10016 );
/*	        	erb = new ErrorRequestObject();
	        	erb.setError("createNeighborhood Failed");
	        	erb.setPath("NeighborhoodManagement.neighborhoodPost::NeighborhoodManagement.createNeighborhood");
				erb.setProposedSolution("Boardwalk Exception. ErrorCode:" + bwe.getErrorCode() + ", Error Msg:" + bwe.getMessage() + ", Solution:" +bwe.getPotentialSolution());
	        	ErrResps.add(erb);
	        	System.out.println("Boardwalk Exception. ErrorCode:" + bwe.getErrorCode() + ", Error Msg:" + bwe.getMessage() + ", Solution:" +bwe.getPotentialSolution());
	            return nhList;
*/			}
			else
			{
				Neighborhood nh = new Neighborhood();
				//Integer obj = new Integer(nhId);
				nh.setNhId(nhId);
				nh.setNhName(nhName);
				nh.setIsSecure(isSecure);
	
				System.out.println("nhName:"+ nhName);
				System.out.println("nhId:"+ nhId);
				System.out.println("isSecure:"+ isSecure);
				
				if (parentNhId != -1)
				{
					level += 1;
	    			//obj = new Integer(level);
	    			nh.setLevel(level);
	    			//obj = new Integer(parentNhId);
	    			nh.setParentNhId(parentNhId);
	    			System.out.println("ParentNhId:"+ parentNhId);
	    			System.out.println("level:"+ level);
				}
				else	
				{
	    			//obj = new Integer(parentNhId);
	    			nh.setParentNhId(parentNhId);
	    			//obj = new Integer(0);
	    			nh.setLevel(0);
	    			System.out.println("ParentNhId:"+ parentNhId);
	    			System.out.println("level:"+ 0);
				}
				nhList.add(nh);
			}
		}
		catch (BoardwalkException bwe)
		{
        	statusCode.add(409);			//Conflict 
        	erb = new ErrorRequestObject();
        	erb.setError("createNeighborhood Failed");
        	erb.setPath("NeighborhoodManagement.neighborhoodPost::NeighborhoodManagement.createNeighborhood");
			erb.setProposedSolution("Boardwalk Exception. ErrorCode:" + bwe.getErrorCode() + ", Error Msg:" + bwe.getMessage() + ", Solution:" +bwe.getPotentialSolution());
        	ErrResps.add(erb);
        	System.out.println("Boardwalk Exception. ErrorCode:" + bwe.getErrorCode() + ", Error Msg:" + bwe.getMessage() + ", Solution:" +bwe.getPotentialSolution());
            return nhList;
		}
        catch (NullPointerException npe)
        {
        	statusCode.add(409);			//Conflict 
            System.out.println("Failed to create Nh : " + nhName + ". Neighborhood name must be Unique under the Parent. You are trying to create a Duplicate Neighborhood");
			erb = new ErrorRequestObject();
			erb.setError("Failed to Create Neighbornood:" + nhName + ",  Error Msg:" + npe.getMessage()  + ", Cause:" + npe.getCause());
			erb.setPath("NeighborhoodManagement.neighborhoodPost::BoardwalkNeighborhoodManager.createNeighborhood");
			erb.setProposedSolution("Neighborhood name must be Unique under the Parent. You are trying to create a Duplicate Neighborhood");
			ErrResps.add(erb);
        }
        catch (NoSuchElementException nse)
        {
        	statusCode.add(404);			//ParentNhid NOT FOUND
            System.out.println("The Parent Neighborhood Not Found");
			erb = new ErrorRequestObject();
			erb.setError("Failed to Create Neighbornood: Parent NeighborhoodId NOT FOUND ");
			erb.setPath("NeighborhoodManagement.neighborhoodPost::BoardwalkNeighborhoodManager.getNeighborhoodTree");
			erb.setProposedSolution("The Parent NeighborhoodId NOT FOUND. You must provide an existing Parent Neigborhood Id.");
			ErrResps.add(erb);
        }
		//Custom Code Ends
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
		return nhList;
	}


//	@GET	-- AUTHORIZATION DONE
//  @Path("/{nhId}/collaboration")
	public static ArrayList<Collaboration> neighborhoodNhIdCollaborationGet(Integer nhId, ArrayList<ErrorRequestObject> ErrResps, String authBase64String, BoardwalkConnection bwcon , ArrayList<Integer> memberNh , ArrayList<Integer> statusCode) 
	{
        ArrayList <Collaboration> collabList = new ArrayList <Collaboration>() ;
        ArrayList <Whiteboard> wbList ;
        ArrayList <GridInfo> giList ;
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

			Vector<?> nh0v = BoardwalkNeighborhoodManager.getNeighborhoodTree( bwcon, nhId);
			if (nh0v.isEmpty()) 
			{
				throw new NoSuchElementException("Neighborhood NOT FOUND") ;
			}    		
			
			Long obj;
			collabList = new ArrayList<Collaboration>();

			Collaboration collab;
			Whiteboard wb ;
			//GridInfo gi;
			GridInfo gi;
			
			Vector<?> cl = BoardwalkCollaborationManager.getCollaborationsForNeighborhood(bwcon,nhId);
			Iterator<?> cli = cl.iterator();
			while (cli.hasNext())
			{
				collab = new Collaboration();
				
				Integer collabId = (Integer)cli.next();
				BoardwalkCollaborationNode bcn = BoardwalkCollaborationManager.getCollaborationTree(bwcon, collabId.intValue());
				System.out.println("Sucessfully fetched the collab tree from the database");

				//Setting Collab Information
				//obj = new Long(bcn.getId());
				collab.setCollabId(bcn.getId());
				collab.setCollabName(bcn.getName());
				
				System.out.println("Collaboration = " + bcn.getName());

				wbList = new ArrayList<Whiteboard>();
				Vector<?> wv = bcn.getWhiteboards();
				Iterator<?> wvi = wv.iterator();
				while ( wvi.hasNext())
				{
					BoardwalkWhiteboardNode bwn = (BoardwalkWhiteboardNode)wvi.next();
					System.out.println("\tWhiteboard = " + bwn.getName());
					wb = new Whiteboard();
					//obj = new Long(bwn.getId());
					wb.setWbId(bwn.getId());
					wb.setWbName(bwn.getName());
					
					Vector<?> tv = bwn.getTables();
					Iterator<?> tvi = tv.iterator();
					//giList = new ArrayList<GridInfo>();
					giList = new ArrayList<GridInfo>();
					while (tvi.hasNext())
					{
						//gi = new GridInfo();
						gi = new GridInfo();
						BoardwalkTableNode btn = (BoardwalkTableNode)tvi.next();
						System.out.println("\t\tTable = " + btn.getName());

						gi.setGridId( btn.getId());
						gi.setGridName(btn.getName());
						gi.setGridPurpose(btn.getDescription());
						giList.add(gi);
					}
					wb.setGridList(giList);
					wbList.add(wb);
				}
				collab.setWbList(wbList);
				collabList.add(collab);
			}
			statusCode.add(200);		//200	: Success. Collaboration List is returned
			return collabList;
		}
        catch (NoSuchElementException nse)
        {
            System.out.println("The Neighborhood Not Found");
			erb = new ErrorRequestObject();
			erb.setError("NeighborhoodId NOT FOUND ");
			erb.setPath("NeighborhoodManagement.neighborhoodNhIdCollaborationGet::BoardwalkNeighborhoodManager.getNeighborhoodTree");
			erb.setProposedSolution("The NeighborhoodId NOT FOUND. You must provide an existing Neigborhood Id.");
			ErrResps.add(erb);
			statusCode.add(404);		//404	: The Neighborhood Not Found
			return collabList;
        }	 		    			
		catch (BoardwalkException bwe)
		{
			System.out.println("Error fetching Collaborations.  ErrorCode:" + bwe.getErrorCode() + ", Error Msg:" + bwe.getMessage());
			erb = new ErrorRequestObject();
			erb.setError("Error fetching Collaborations");
			erb.setPath("NeighborhoodManagement.neighborhoodNhIdCollaborationGet::BoardwalkNeighborhoodManager.getCollaborationsForNeighborhood");
			erb.setProposedSolution("Boardwalk Exception. ErrorCode:" + bwe.getErrorCode() + ", Error Msg:" + bwe.getMessage() + ", Solution:" + bwe.getPotentialSolution());
			ErrResps.add(erb);
			statusCode.add(500);		//500	: Server Error fetching Collaborations
			return collabList;
		}
			//Custom Code Ends
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
}
