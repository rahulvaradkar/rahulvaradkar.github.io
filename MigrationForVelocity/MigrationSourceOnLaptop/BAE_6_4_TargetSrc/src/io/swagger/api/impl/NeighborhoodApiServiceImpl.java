package io.swagger.api.impl;

import io.swagger.api.*;
import io.swagger.model.Collaboration;
import io.swagger.model.ErrorRequestObject;
import io.swagger.model.Member;
import io.swagger.model.Neighborhood;
import io.swagger.model.Relation;
import java.util.ArrayList;
import java.util.List;
import io.swagger.api.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.validation.constraints.*;
import boardwalk.connection.BoardwalkConnection;
import boardwalk.rest.NeighborhoodManagement;
import boardwalk.rest.bwAuthorization; 

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-05-10T09:36:40.808Z")
public class NeighborhoodApiServiceImpl extends NeighborhoodApiService {
    @Override
    public Response neighborhoodGet( String neighborhoodSpec, SecurityContext securityContext, String authBase64String) throws NotFoundException {
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }
   
    
//  @GET
//  @Path("/{nhId}/collaboration")
	@Override
	public Response neighborhoodNhIdCollaborationGet(Integer nhId, SecurityContext securityContext, String authBase64String) throws NotFoundException {
      // do some magic!
		ErrorRequestObject erb;
		ArrayList <ErrorRequestObject> erbs = new ArrayList<ErrorRequestObject>();
    	BoardwalkConnection bwcon = null;
		ArrayList<Integer> memberNh = new ArrayList<Integer>();
		ArrayList<Integer> statusCode = new ArrayList<Integer>();

		if (authBase64String == null)
		{	
			erb = new ErrorRequestObject(); erb.setError("Missing Authorization in Header"); erb.setPath("Header:Authorization"); 
			erb.setProposedSolution("Authorization Header should contain user:pwd:nhPath as Base64 string");
			erbs.add(erb);
			return Response.status(401).entity(erbs).build();				//401: Missing Authorization
		}
    	else
    	{
    		ArrayList <ErrorRequestObject> ErrResps = new ArrayList<ErrorRequestObject>();
        	//Connection connection = null;
    		
    		bwcon = bwAuthorization.AuthenticateUser(authBase64String, memberNh, ErrResps);
    		if (!ErrResps.isEmpty())
    		{
    			return Response.status(401).entity(ErrResps).build();		//401: Authorization Failed
    		}
    	}

		System.out.println("nhId ->" + nhId);
		if (nhId <= 0)
		{	
			erb = new ErrorRequestObject(); erb.setError("IsNegative"); erb.setPath("nhId"); 
			erb.setProposedSolution("You must enter an Existing Neighborhood ID. It should be a Positive Number.");
			erbs.add(erb);
		}

	   	if (erbs.size() == 0)
	   	{
   			ArrayList<Collaboration> collabList;
	  	 	ArrayList <ErrorRequestObject> ErrResps = new ArrayList<ErrorRequestObject>();
	  	 	collabList = NeighborhoodManagement.neighborhoodNhIdCollaborationGet(nhId, ErrResps ,authBase64String, bwcon, memberNh, statusCode);
    	
	  	 	//404	: The Neighborhood Not Found
	  	 	//500	: Server Error fetching Collaborations
	  	 	//200	: Success. Collaboration List is returned
	  	 	
	  	 	if (ErrResps.size() > 0)
	  	 	{
				int scode = statusCode.get(0);
				return Response.status(scode).entity(ErrResps).build();   	
	  	 	}
	    	else
	        	return Response.status(200).entity(collabList).build();		//200: 	Success. Collaboration List is returned.
	   	}
	   	else
	   	{
	       	return Response.status(400).entity(erbs).build();				//400:	Bad Request. Negative nhId 
	   	}    
//      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
	}
    
  
//  @DELETE
//  @Path("/{nhId}")
    @Override
    public Response neighborhoodNhIdDelete(Integer nhId, SecurityContext securityContext, String authBase64String) throws NotFoundException {
        // do some magic!
    	BoardwalkConnection bwcon = null;
   		ErrorRequestObject erb;
		ArrayList <ErrorRequestObject> erbs = new ArrayList<ErrorRequestObject>();
		ArrayList<Integer> memberNh = new ArrayList<Integer>();
		ArrayList<Integer> statusCode = new ArrayList<Integer>();

		System.out.println("nhId ->" + nhId);

		if (authBase64String == null)
		{	
			erb = new ErrorRequestObject(); erb.setError("Missing Authorization in Header"); erb.setPath("Header:Authorization"); 
			erb.setProposedSolution("Authorization Header should contain user:pwd:nhPath as Base64 string");
			erbs.add(erb);
			return Response.status(401).entity(erbs).build();				//401: Missing Authorization
		}
    	else
    	{
    		ArrayList <ErrorRequestObject> ErrResps = new ArrayList<ErrorRequestObject>();
        	//Connection connection = null;
    		
    		bwcon = bwAuthorization.AuthenticateUser(authBase64String, memberNh, ErrResps);
    		if (!ErrResps.isEmpty())
    		{
    			return Response.status(401).entity(ErrResps).build();		//401: Authorization Failed
    		}
    	}
		
		if (nhId <= 0)
  		{	
  			erb = new ErrorRequestObject(); erb.setError("IsNegative"); erb.setPath("nhId"); 
  			erb.setProposedSolution("You must enter an Existing Neighborhood ID. It should be a Positive Number.");
  			erbs.add(erb);
  		}

  	   	if (erbs.size() == 0)
  	   	{
	  	 	ArrayList <ErrorRequestObject> ErrResps = new ArrayList<ErrorRequestObject>();
	  	 	String retMsg = "";
	  	 	retMsg = NeighborhoodManagement.neighborhoodNhIdDelete(nhId, ErrResps ,authBase64String, bwcon, memberNh, statusCode);
	    	
  		  	//404 : NeighborhoodId NOT FOUND
  		  	//500 : Failed to Delete Neighborhood. BoardwalkException: Contact Boardwalk Support.
	  	 	//200: Success. Neighborhood Deleted Successfully. All it's dependent objects are purged successfully.

	  	 	if (ErrResps.size() > 0)
	  	 	{
				int scode = statusCode.get(0);
				return Response.status(scode).entity(ErrResps).build();   	
	  	 	}
	    	else
	        	return Response.status(200).entity(retMsg).build();		//200: 	Success. Neighborhood Deleted Successfully. All it's dependent objects are purged successfully.
  	   	}
  	   	else
  	   	{
  	       	return Response.status(400).entity(erbs).build();		//400 : Negative nhId
  	   	}    
//          return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();    
  	}

//	@GET
//	@Path("/{nhId}")
    @Override
    public Response neighborhoodNhIdGet(Integer nhId, SecurityContext securityContext, String authBase64String) throws NotFoundException {
        // do some magic!
		ErrorRequestObject erb;
		ArrayList <ErrorRequestObject> erbs = new ArrayList<ErrorRequestObject>();
    	BoardwalkConnection bwcon = null;
		ArrayList<Integer> memberNh = new ArrayList<Integer>();
		ArrayList<Integer> statusCode = new ArrayList<Integer>();
		
 		if (authBase64String == null)
 		{	
 			erb = new ErrorRequestObject(); erb.setError("Missing Authorization in Header"); erb.setPath("Header:Authorization"); 
 			erb.setProposedSolution("Authorization Header should contain user:pwd:nhPath as Base64 string");
 			erbs.add(erb);
			return Response.status(401).entity(erbs).build();		//401: Missing Authorization
		}
    	else
    	{
    		ArrayList <ErrorRequestObject> ErrResps = new ArrayList<ErrorRequestObject>();
        	//Connection connection = null;
    		
    		bwcon = bwAuthorization.AuthenticateUser(authBase64String, memberNh, ErrResps);
    		if (!ErrResps.isEmpty())
    		{
    			return Response.status(401).entity(ErrResps).build();		//401: Authorization Failed
    		}
    	}
 		 
		System.out.println("nhId ->" + nhId);
		if (nhId <= 0)
 		{	
 			erb = new ErrorRequestObject(); erb.setError("IsNegative"); erb.setPath("nhId"); 
 			erb.setProposedSolution("You must enter an Existing Neighborhood ID. It should be a Positive Number.");
 			erbs.add(erb);
 		}

 	   	if (erbs.size() == 0)
 	   	{
   			ArrayList<Neighborhood> nhList;
	  	 	ArrayList <ErrorRequestObject> ErrResps = new ArrayList<ErrorRequestObject>();
	    	nhList = NeighborhoodManagement.neighborhoodNhIdGet(nhId, ErrResps ,authBase64String, bwcon, memberNh, statusCode);

	    	//200: Success Neighborhood collection returned.
	    	//500:	Server-side Error fetching Neighborhood.
	    	//404:	The Neighborhood Not Found.

	  	 	if (ErrResps.size() > 0)
	  	 	{
				int scode = statusCode.get(0);
				return Response.status(scode).entity(ErrResps).build();   	
	  	 	}
	    	else
	        	return Response.status(200).entity(nhList).build();		//200: 	Success. Neighborhood collection returned.
 	   	}
 	   	else
 	   	{
 	       	return Response.status(400).entity(erbs).build();		//400 : Negative nhId
 	   	}    
        //return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();    }
    }

    //@GET
    //@Path("/{nhId}/member")
    @Override
    public Response neighborhoodNhIdMemberGet(Integer nhId, SecurityContext securityContext, String authBase64String) throws NotFoundException {
        // do some magic!
		ErrorRequestObject erb;
		ArrayList <ErrorRequestObject> erbs = new ArrayList<ErrorRequestObject>();
    	BoardwalkConnection bwcon = null;
		ArrayList<Integer> memberNh = new ArrayList<Integer>();
		ArrayList<Integer> statusCode = new ArrayList<Integer>();
    	
		if (authBase64String == null)
		{	
			erb = new ErrorRequestObject(); erb.setError("Missing Authorization in Header"); erb.setPath("Header:Authorization"); 
			erb.setProposedSolution("Authorization Header should contain user:pwd:nhPath as Base64 string");
			erbs.add(erb);
			return Response.status(401).entity(erbs).build();		//401: Missing Authorization
		}
    	else
    	{
    		ArrayList <ErrorRequestObject> ErrResps = new ArrayList<ErrorRequestObject>();
        	//Connection connection = null;
    		
    		bwcon = bwAuthorization.AuthenticateUser(authBase64String, memberNh, ErrResps);
    		if (!ErrResps.isEmpty())
    		{
    			return Response.status(401).entity(ErrResps).build();		//401: Authorization Failed
    		}
    	}

		System.out.println("nhId ->" + nhId);
		if (nhId <= 0)
		{	
			erb = new ErrorRequestObject(); erb.setError("IsNegative"); erb.setPath("nhId"); 
			erb.setProposedSolution("You must enter an Existing Neighborhood ID. It should be a Positive Number.");
			erbs.add(erb);
		}
		 
	   	if (erbs.size() == 0)
	   	{
			ArrayList<Member> memberList;
	  	 	ArrayList <ErrorRequestObject> ErrResps = new ArrayList<ErrorRequestObject>();
	  	 	memberList = NeighborhoodManagement.neighborhoodNhIdMemberGet(nhId, ErrResps ,authBase64String, bwcon, memberNh, statusCode);
	    	
		  	//500 : Get Neighborhood Members Failed
		  	//404 : The Neighborhood Not Found
	  	 	//200 : Success. The member list returned
	  	 	if (ErrResps.size() > 0)
	  	 	{
				int scode = statusCode.get(0);
				return Response.status(scode).entity(ErrResps).build();   	
	  	 	}
	    	else
	        	return Response.status(200).entity(memberList).build();		//200: 	Success. Collaboration created successfully
	   	}
	   	else
	   	{
	       	return Response.status(400).entity(erbs).build();		//400: Negative nhId 
	   	}    
        //return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }
    
    //@POST
    //@Path("/{nhId}/member/{memberId}/collaboration")
/*    @Override
    public Response neighborhoodNhIdMemberMemberIdCollaborationPost(Integer nhId, Integer memberId, Collaboration collaboration, SecurityContext securityContext, String authBase64String) throws NotFoundException {
        // do some magic!
        // do some magic!
		ErrorRequestObject erb;
		ArrayList <ErrorRequestObject> erbs = new ArrayList<ErrorRequestObject>();
    	BoardwalkConnection bwcon = null;
		ArrayList<Integer> memberNh = new ArrayList<Integer>();
		ArrayList<Integer> statusCode = new ArrayList<Integer>();

		if (authBase64String == null)
		{	
			erb = new ErrorRequestObject(); erb.setError("Missing Authorization in Header"); erb.setPath("Header:Authorization"); 
			erb.setProposedSolution("Authorization Header should contain user:pwd:nhPath as Base64 string");
			erbs.add(erb);
			return Response.status(401).entity(erbs).build();		//401: Missing Authorization
		}
    	else
    	{
    		ArrayList <ErrorRequestObject> ErrResps = new ArrayList<ErrorRequestObject>();
        	//Connection connection = null;
    		
    		bwcon = bwAuthorization.AuthenticateUser(authBase64String, memberNh, ErrResps);
    		if (!ErrResps.isEmpty())
    		{
    			return Response.status(401).entity(ErrResps).build();		//401: Authorization Failed
    		}
    	}

		System.out.println("nhId ->" + nhId);
		if (nhId <= 0)
		{	
			erb = new ErrorRequestObject(); erb.setError("IsNegative"); erb.setPath("nhId"); 
			erb.setProposedSolution("You must enter an Existing Neighborhood ID. It should be a Positive Number.");
			erbs.add(erb);
		}

		if (memberId <= 0)
		{	
			erb = new ErrorRequestObject(); erb.setError("IsNegative"); erb.setPath("memberId"); 
			erb.setProposedSolution("You must enter an Existing Member ID. It should be a Positive Number.");
			erbs.add(erb);
		}
		String collabName = null;
		String collabPurpose = null;
		collabName = collaboration.getName();
		collabPurpose = collaboration.getPurpose();
		
	  	if (collabName == null)
	  	{
			erb = new ErrorRequestObject(); erb.setError("IsMissing"); erb.setPath("Collaboration.Name"); 
			erb.setProposedSolution("Collaboration Name is Missing in the Requet. Provide Collaboration Name");
			erbs.add(erb);
	  	}
	  	else if (collabName.trim().equals(""))
	  	{
			erb = new ErrorRequestObject(); erb.setError("IsBlank"); erb.setPath("Collaboration.Name"); 
			erb.setProposedSolution("Collaboration Name cannot be Blank.");
			erbs.add(erb);
	  	}

	  	if (collabPurpose == null)
	  	{
			erb = new ErrorRequestObject(); erb.setError("IsMissing"); erb.setPath("Collaboration.Purpose"); 
			erb.setProposedSolution("Collaboration Purpose is Missing in the Request. Provide Collaboration Purpose");
			erbs.add(erb);
	  	}
	  	else if (collabPurpose.trim().equals(""))
	  	{
			erb = new ErrorRequestObject(); erb.setError("IsBlank"); erb.setPath("Collaboration.Purpose"); 
			erb.setProposedSolution("Collaboration Purpose cannot be Blank.");
			erbs.add(erb);
	  	}
	  	
	   	if (erbs.size() == 0)
	   	{
	   			int collabId =  -1;
		  	 	ArrayList <ErrorRequestObject> ErrResps = new ArrayList<ErrorRequestObject>();
		  	 	collabId = NeighborhoodManagement.neighborhoodNhIdMemberMemberIdCollaborationPost(nhId, memberId, collaboration, ErrResps ,authBase64String, bwcon, memberNh, statusCode);

		  	 	//403:Forbidden. Authorization Is not allowed to create Collaboration in this Neighborhood. [MemberID mismatch] 
		  	 	//403:Forbidden. Authorization Is not allowed to create Collaboration in this Neighborhood. [NhID mismatch]		  	 	
		  	 	//404:	The Neighborhood Not Found
		  	 	//404:	The Membership Not Found
		  	 	//200: 	Success. Collaboration created successfully
		  	 	//500: 	Failed to Create New Collaboration
		  	 	
		  	 	if (ErrResps.size() > 0)
		  	 	{
					int scode = statusCode.get(0);
					return Response.status(scode).entity(ErrResps).build();   	
		  	 	}
		    	else
		        	return Response.status(200).entity(collabId).build();		//200: 	Success. Collaboration created successfully
	   	}
	   	else
	   	{
	       	return Response.status(400).entity(erbs).build();		//400: Invalid Request: Null or Missing, collabName, collabPurpose, Negative memberId, nhId
	   	}    
    	//return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }
*/
    //@DELETE
    // @Path("/{nhId}/member/{memberId}")
    @Override
    public Response neighborhoodNhIdMemberMemberIdDelete(Integer nhId, Integer memberId, SecurityContext securityContext, String authBase64String) throws NotFoundException {
        // do some magic!
		ErrorRequestObject erb;
		ArrayList <ErrorRequestObject> erbs = new ArrayList<ErrorRequestObject>();
    	BoardwalkConnection bwcon = null;
		ArrayList<Integer> memberNh = new ArrayList<Integer>();
		ArrayList<Integer> statusCode = new ArrayList<Integer>();

    	
		if (authBase64String == null)
		{	
			erb = new ErrorRequestObject(); erb.setError("Missing Authorization in Header"); erb.setPath("Header:Authorization"); 
			erb.setProposedSolution("Authorization Header should contain user:pwd:nhPath as Base64 string");
			erbs.add(erb);
			return Response.status(401).entity(erbs).build();			//401: Missing Authorization
		}
    	else
    	{
    		ArrayList <ErrorRequestObject> ErrResps = new ArrayList<ErrorRequestObject>();
        	//Connection connection = null;
    		
    		bwcon = bwAuthorization.AuthenticateUser(authBase64String, memberNh, ErrResps);
    		if (!ErrResps.isEmpty())
    		{
    			return Response.status(401).entity(ErrResps).build();	//401: Authorization Failed
    		}
    	}
		 
		System.out.println("nhId ->" + nhId);
		if (nhId <= 0)
		{	
			erb = new ErrorRequestObject(); erb.setError("IsNegative"); erb.setPath("nhId"); 
			erb.setProposedSolution("The Neighborhood you are deleting a Member from must exists. It should be a Positive Number.");
			erbs.add(erb);
		}

		if (memberId <= 0)
		{
			erb = new ErrorRequestObject(); erb.setError("IsNegative"); erb.setPath("member.userId"); 
			erb.setProposedSolution("You must enter an Existing Member ID for deleting the Membership of Neighborhood. It should be a Positive Number.");
			erbs.add(erb);
		}

		if (erbs.size() == 0)
	   	{
	  	 	ArrayList <ErrorRequestObject> ErrResps = new ArrayList<ErrorRequestObject>();
	  	 	String msgRet;
	  	 	msgRet = NeighborhoodManagement.neighborhoodNhIdMemberMemberIdDelete(nhId, memberId, ErrResps ,authBase64String , bwcon, memberNh, statusCode);
	    	
	  	 	//200:Success. Membership deleted Successfully
	  	 	//500: Server Error. Deleting membership of Neighborhood Failed
	  	 	//404: The Neighborhood Not Found
	  	 	//404: The Membership Not Found
	  	 	
	    	if (ErrResps.size() > 0)
	    	{
				int scode = statusCode.get(0);
	    		return Response.status(scode).entity(ErrResps).build();   	
	    	}
	    	else
   				return Response.status(200).entity(msgRet).build();		//200:Success. Membership deleted Successfully
	   	}
    	else
	   	{
	       	return Response.status(400).entity(erbs).build();		//400: Negative nhId, Negative memberId
	   	}    
        //return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }
    
    //@POST
    //@Path("/{nhId}/member")
    @Override
    public Response neighborhoodNhIdMemberPost(Integer nhId, Member member, SecurityContext securityContext, String authBase64String) throws NotFoundException {
        // do some magic!
    	
    	BoardwalkConnection bwcon = null;
		ErrorRequestObject erb;
		ArrayList <ErrorRequestObject> erbs = new ArrayList<ErrorRequestObject>();
		ArrayList<Integer> memberNh = new ArrayList<Integer>();
		ArrayList<Integer> statusCode = new ArrayList<Integer>();

		if (authBase64String == null)
		{	
			erb = new ErrorRequestObject(); erb.setError("Missing Authorization in Header"); erb.setPath("Header:Authorization"); 
			erb.setProposedSolution("Authorization Header should contain user:pwd:nhPath as Base64 string");
			erbs.add(erb);
			return Response.status(401).entity(erbs).build();	//401:  Authorization Missing
		}
    	else
    	{
    		ArrayList <ErrorRequestObject> ErrResps = new ArrayList<ErrorRequestObject>();
        	//Connection connection = null;
    		
    		bwcon = bwAuthorization.AuthenticateUser(authBase64String, memberNh, ErrResps);
    		if (!ErrResps.isEmpty())
    		{
    			return Response.status(401).entity(ErrResps).build();		//401:  Authorization Failed
    		}
		}
		
		System.out.println("nhId ->" + nhId);
		if (nhId <= 0)
		{	
			erb = new ErrorRequestObject(); erb.setError("IsNegative"); erb.setPath("nhId"); 
			erb.setProposedSolution("The Neighborhood you are eadding a Member must exists. It should be a Positive Number.");
			erbs.add(erb);
		}
		
		if (member.getUserId() <= 0 )
		{
			erb = new ErrorRequestObject(); erb.setError("IsNegative"); erb.setPath("member.userId"); 
			erb.setProposedSolution("You must enter an Existing User ID for adding a Membership to Neighborhood. It should be a Positive Number.");
			erbs.add(erb);
		}
	   	
		if (erbs.size() == 0)
	   	{
			ArrayList<Member> memberList;
	  	 	ArrayList <ErrorRequestObject> ErrResps = new ArrayList<ErrorRequestObject>();
	  	 	memberList = NeighborhoodManagement.neighborhoodNhIdMemberPost(nhId, member, ErrResps ,authBase64String, bwcon, memberNh, statusCode);
	  	 	
	  	 	//200: Success Membership created
	  	 	//500: Server Internal Error. Creating new membership to Neighborhood Failed
	  	 	//404: Neighborhood Not Found
	  	 	//404: User Not Found
	  	 	
	    	if (ErrResps.size() > 0)
	    	{
				int scode = statusCode.get(0);
	    		return Response.status(scode).entity(ErrResps).build();   	
	    	}
    		else
   				return Response.status(200).entity(memberList).build();		//200 : Success Success Membership created
	   	}
	   	else
	   	{
	       	return Response.status(400).entity(erbs).build();		//Bad Request: Negative nhId, Negative userId
	   	}    
        //return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }
    
    //@DELETE
    //@Path("/{nhId}/relation")
    @Override
    public Response neighborhoodNhIdRelationDelete(Integer nhId,  @NotNull String relation, SecurityContext securityContext, String authBase64String) throws NotFoundException {
        // do some magic!

		System.out.println("neighborhoodNhIdRelationDelete");
		System.out.println("nhId :" + nhId);
		System.out.println("relation :" + relation);
		//System.out.println("authBase64String :" + authBase64String);
		
    	BoardwalkConnection bwcon = null;
		ArrayList<Integer> memberNh = new ArrayList<Integer>();
		ErrorRequestObject erb;
		ArrayList <ErrorRequestObject> erbs = new ArrayList<ErrorRequestObject>();
		ArrayList<Integer> statusCode = new ArrayList<Integer>();

		if (authBase64String == null)
		{	
			erb = new ErrorRequestObject(); erb.setError("Missing Authorization in Header"); erb.setPath("Header:Authorization"); 
			erb.setProposedSolution("Authorization Header should contain user:pwd:nhPath as Base64 string");
			erbs.add(erb);
			return Response.status(401).entity(erbs).build();		//401: Missing Authorization
		}
    	else
    	{
    		ArrayList <ErrorRequestObject> ErrResps = new ArrayList<ErrorRequestObject>();
        	//Connection connection = null;
    		
    		bwcon = bwAuthorization.AuthenticateUser(authBase64String, memberNh, ErrResps);
    		if (!ErrResps.isEmpty())
    		{
    			return Response.status(401).entity(ErrResps).build();	//401:  Authorization failed
    		}
    	}
		 
		System.out.println("nhId ->" + nhId);
		if (nhId <= 0)
		{	
			erb = new ErrorRequestObject(); erb.setError("IsNegative"); erb.setPath("nhId"); 
			erb.setProposedSolution("The Neighborhood Id you are Deleting Relations of must exist. It should be a Positive Number.");
			erbs.add(erb);
		}

		if (relation == null)
		{
			erb = new ErrorRequestObject(); erb.setError("IsMissing"); erb.setPath("relation"); 
			erb.setProposedSolution("Relation Name is Missing in the Request. Provide Relation Name");
			erbs.add(erb);
		}
		else if (relation.trim().equals(""))
		{
			erb = new ErrorRequestObject(); erb.setError("IsBlank"); erb.setPath("relation"); 
			erb.setProposedSolution("Relation Name Cannot be Blank. ");
			erbs.add(erb);
		}

		if (erbs.size() == 0)
	   	{
	  	 	ArrayList <ErrorRequestObject> ErrResps = new ArrayList<ErrorRequestObject>();
	  	 	String msgRet;
	  	 	msgRet = NeighborhoodManagement.neighborhoodNhIdRelationDelete(nhId, relation, ErrResps, authBase64String, bwcon, memberNh, statusCode);

			//404 : Neighborhood not found
			//404 : Relation not found
	  	 	//500 : Server Internal Error . Failed to Delete Neighborhood Relation
			//200 : Success. Relations Deleted message
			int scode = statusCode.get(0);
	    	if (ErrResps.size() > 0)
	    		return Response.status(scode).entity(ErrResps).build();   	
	    	else
   				return Response.status(200).entity(msgRet).build();		//200 : Success message. Deleted Relation or Not found
	   	}
    	else
	   	{
	       	return Response.status(400).entity(erbs).build();		//400 : Bad Request. Negative NhId, Relation Null or Missing.
	   	}    		
      //  return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }

    //@GET
    //@Path("/{nhId}/relation")
    @Override
    public Response neighborhoodNhIdRelationGet(Integer nhId, SecurityContext securityContext, String authBase64String) throws NotFoundException {
        // do some magic!
		ErrorRequestObject erb;
		ArrayList <ErrorRequestObject> erbs = new ArrayList<ErrorRequestObject>();
    	BoardwalkConnection bwcon = null;
    	ArrayList<Integer> memberNh = new ArrayList<Integer>();
		ArrayList<Integer> statusCode = new ArrayList<Integer>();

    	
		if (authBase64String == null)
		{	
			erb = new ErrorRequestObject(); erb.setError("Missing Authorization in Header"); erb.setPath("Header:Authorization"); 
			erb.setProposedSolution("Authorization Header should contain user:pwd:nhPath as Base64 string");
			erbs.add(erb);
			return Response.status(401).entity(erbs).build();		//401: Missing Authorization
		}
    	else
    	{
    		ArrayList <ErrorRequestObject> ErrResps = new ArrayList<ErrorRequestObject>();
        	//Connection connection = null;
    		
    		
    		bwcon = bwAuthorization.AuthenticateUser(authBase64String, memberNh, ErrResps);
    		if (!ErrResps.isEmpty())
    		{
    			return Response.status(401).entity(ErrResps).build();	//401:  Authorisation failed
    		}
    	}
		 
		System.out.println("nhId ->" + nhId);
		if (nhId <= 0)
		{	
			erb = new ErrorRequestObject(); erb.setError("IsNegative"); erb.setPath("nhId"); 
			erb.setProposedSolution("The Neighborhood Id to get Relations must exist. It should be a Positive Number.");
			erbs.add(erb);
		}

		if (erbs.size() == 0)
	   	{
			ArrayList <ErrorRequestObject> ErrResps = new ArrayList<ErrorRequestObject>();
			ArrayList <Relation> rels;
			rels = NeighborhoodManagement.neighborhoodNhIdRelationGet(nhId, ErrResps, authBase64String, bwcon, memberNh, statusCode );

	    	//400: Bad Request. Negative NHID.
			//404 : Neighborhood not found
			//500 : Internal Server Error.  "Failed to GET Neighborood Relation of Neighborhood :" + nhId
			//200 : Success. Relations List returned
			
	    	if (ErrResps.size() > 0)
	    	{
				int scode = statusCode.get(0);
				return Response.status(scode).entity(ErrResps).build();   	
	    	}
	    	else
	    	{
	    		return Response.status(200).entity(rels).build();		//200: Success. Returns Neighborhood Relations
	    	}			
	   	}
    	else
	   	{
	       	return Response.status(400).entity(erbs).build();		//400: Bad Request. Negative NhId
	   	}    
    	//return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }
    
    //@POST
    //@Path("/{nhId}/relation")
    @Override
    public Response neighborhoodNhIdRelationPost(Integer nhId, Relation relationship, SecurityContext securityContext, String authBase64String) throws NotFoundException {
        // do some magic!
		ErrorRequestObject erb;
		ArrayList <ErrorRequestObject> erbs = new ArrayList<ErrorRequestObject>();
    	BoardwalkConnection bwcon = null;
		ArrayList<Integer> memberNh = new ArrayList<Integer>();
		ArrayList<Integer> statusCode = new ArrayList<Integer>();

		if (authBase64String == null)
		{	
			erb = new ErrorRequestObject(); erb.setError("Missing Authorization in Header"); erb.setPath("Header:Authorization"); 
			erb.setProposedSolution("Authorization Header should contain user:pwd:nhPath as Base64 string");
			erbs.add(erb);
			return Response.status(401).entity(erbs).build();		//401: Missing Authorization
		}
    	else
    	{
    		ArrayList <ErrorRequestObject> ErrResps = new ArrayList<ErrorRequestObject>();
        	//Connection connection = null;
    		
    		bwcon = bwAuthorization.AuthenticateUser(authBase64String, memberNh, ErrResps);
    		if (!ErrResps.isEmpty())
    		{
    			return Response.status(401).entity(ErrResps).build();	//401: Authorization Failed
    		}
		}
		 
		System.out.println("nhId ->" + nhId);
		if (nhId <= 0)
		{	
			erb = new ErrorRequestObject(); erb.setError("IsNegative"); erb.setPath("nhId"); 
			erb.setProposedSolution("The Neighborhood you are setting Relation must exists. It should be a Positive Number.");
			erbs.add(erb);
		}

		if (relationship == null)
		{
			erb = new ErrorRequestObject(); erb.setError("IsMissing"); erb.setPath("relationship"); 
			erb.setProposedSolution("Relationship Details are Missing in the Request. Provide Relationship Details");
			erbs.add(erb);
		}
		else if (relationship.getRelationName() == null)
		{
			erb = new ErrorRequestObject(); erb.setError("IsMissing"); erb.setPath("relationship.name"); 
			erb.setProposedSolution("Missing Relation Name. Provide Relation Name.");
			erbs.add(erb);
		}
		else if (relationship.getRelationName().trim().equals(""))
		{
			erb = new ErrorRequestObject(); erb.setError("IsBlank"); erb.setPath("relationship.name"); 
			erb.setProposedSolution("Relation Name Cannot be Blank. ");
			erbs.add(erb);
		}

		List <Neighborhood> nhList = relationship.getRelatedNhId();

		if (nhList.size() == 0)
		{
			erb = new ErrorRequestObject(); erb.setError("IsEmpty"); erb.setPath("relationship.relatedNhIds"); 
			erb.setProposedSolution("Related Neighborhood List Cannot be Empty. ");
			erbs.add(erb);
		}
		else
		{
			Neighborhood nh;
			for(int index=0 ; index < nhList.size(); index +=1)
			{
				nh = nhList.get(index);
				if (nh.getNhId() <= 0)
				{
					erb = new ErrorRequestObject(); erb.setError("IsNegativeOrZero"); erb.setPath("relationship.nhList[" + index + "].Id = " + nh.getNhId() ); 
					erb.setProposedSolution("Neighborhood ID must be a Positive Number of an Existing Neighborhod. ");
					erbs.add(erb);
				}
			}
		}
		
		if (erbs.size() == 0)
	   	{
	  	 	ArrayList <ErrorRequestObject> ErrResps = new ArrayList<ErrorRequestObject>();

	  	 	NeighborhoodManagement.neighborhoodNhIdRelationPost(nhId, relationship, ErrResps, authBase64String, bwcon, memberNh, statusCode);
	    	
	    	//400: Bad Request. Invalid Neighborhoods in NhList.
	  	 	//200: Success. Neighborhood Relation created successfully for all Neighborhoods in NhList.
	  	 	//409: Conflict. Creation of Relation failed 
	  	 	//404: Neighborhood Not found
	  	 	//400: Bad Request. Invalid Neighborhoods in NhList.
	  	 	int scode = statusCode.get(0);
			return Response.status(scode).entity(ErrResps).build();		
	   	}
    	else
	   	{
	       	return Response.status(400).entity(erbs).build();		//400: BAd rEquest: Missing relation Name, Missing Neighborhood List, Negative nhId, Negative nhIds in related Neighborhoods
	   	}    
    }

    //@POST
    @Override
    public Response neighborhoodPost(Neighborhood neighborhood, SecurityContext securityContext, String authBase64String) throws NotFoundException {
        // do some magic!
    	BoardwalkConnection bwcon = null;
		ErrorRequestObject erb;
		ArrayList <ErrorRequestObject> erbs = new ArrayList<ErrorRequestObject>();
		ArrayList<Integer> memberNh = new ArrayList<Integer>();
		ArrayList<Integer> statusCode = new ArrayList<Integer>();

		if (authBase64String == null)
		{	
			erb = new ErrorRequestObject(); erb.setError("Missing Authorization in Header"); erb.setPath("Header:Authorization"); 
			erb.setProposedSolution("Authorization Header should contain user:pwd:nhPath as Base64 string");
			erbs.add(erb);
			return Response.status(401).entity(erbs).build();			//401: Missing Authorization
		}
    	else
    	{
    		ArrayList <ErrorRequestObject> ErrResps = new ArrayList<ErrorRequestObject>();
        	//Connection connection = null;
    		
    		bwcon = bwAuthorization.AuthenticateUser(authBase64String, memberNh, ErrResps);
    		if (!ErrResps.isEmpty())
    		{
    			return Response.status(401).entity(ErrResps).build();		//401: Authorization Failed
    		}
    	}
		 
		String nhName = neighborhood.getNhName();
		Long objL = new Long(neighborhood.getParentNhId());
		
		int parentNhId = objL.intValue();
		boolean secure = neighborhood.isIsSecure();
		
		System.out.println("nhName ->" + nhName);

		if (nhName.trim().equals(""))
		{	
			erb = new ErrorRequestObject(); erb.setError("IsBlank"); erb.setPath("Neighborhood.Name"); 
			erb.setProposedSolution("Neighborhood name cannot be Blank");
			erbs.add(erb);
		}
		 
		if (parentNhId != -1 &&  parentNhId <= 0)			// -1 used to create Neighborhood Level 0
		{	
			erb = new ErrorRequestObject(); erb.setError("IsNegative"); erb.setPath("Neighborhood.parentNhId"); 
			erb.setProposedSolution("Parent Neighbohood Id must be either -1 OR existing Neighborhood Id.");
			erbs.add(erb);
		}
		
	   	if (erbs.size() == 0)
	   	{
   			ArrayList<Neighborhood> nhList;
	  	 	ArrayList <ErrorRequestObject> ErrResps = new ArrayList<ErrorRequestObject>();
	    	nhList = NeighborhoodManagement.neighborhoodPost(nhName, parentNhId, secure, ErrResps, authBase64String, bwcon, memberNh, statusCode );
	    	
	    	if (nhList.size() > 0)
	        	return Response.status(200).entity(nhList ).build();	//Success: list of Neighborhood
	    	else
	    	{
    			int scode = statusCode.get(0);
    			return Response.status(scode).entity(ErrResps).build();		//409: Conflict. NnName not Unique etc. 404: ParentNhId Not found. 
	    	}
	    }
	   	else
	   	{
	       	return Response.status(400).entity(erbs).build();			//Bad Request: missing nhName, Negative parentNhId
	   	}    

	}


	@Override
	public Response neighborhoodNhIdRelationRelationNeighborhoodGet(Integer nhId, String relation,
			SecurityContext securityContext, String authBase64String) throws NotFoundException {
		// TODO Auto-generated method stub
		return null;
	}
}
