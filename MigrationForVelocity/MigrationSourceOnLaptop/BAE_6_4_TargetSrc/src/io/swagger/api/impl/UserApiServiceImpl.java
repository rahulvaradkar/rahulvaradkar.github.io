package io.swagger.api.impl;

import io.swagger.api.*;
import io.swagger.model.*;

import java.util.ArrayList;
import io.swagger.api.NotFoundException;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import boardwalk.connection.BoardwalkConnection;
import boardwalk.rest.UserManagement;
import boardwalk.rest.bwAuthorization;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-06-09T04:12:45.675Z")
public class UserApiServiceImpl extends UserApiService {
	
	//  /user/{email}/memberships
    @Override
    public Response userMembershipsGet(String email, SecurityContext securityContext, String authBase64String) throws NotFoundException {
        // do some magic!
    		
       	System.out.println("email : " + email);

    	BoardwalkConnection bwcon = null;
    	StringBuffer invReqMsg = new StringBuffer(500);
    	ArrayList<Integer> memberNh = new ArrayList<Integer>();
		ArrayList<Integer> statusCode = new ArrayList<Integer>();

    	ArrayList <ErrorRequestObject> erbs  = new ArrayList<ErrorRequestObject>();
    	ErrorRequestObject erb;

    	ArrayList <RequestErrorInfo> reqeis = new ArrayList<RequestErrorInfo>();
    	RequestErrorInfo reqei = new RequestErrorInfo();

    	ArrayList <ResponseErrorInfo> reseis = new ArrayList<ResponseErrorInfo>();
    	ResponseErrorInfo resei ;
			
		if (authBase64String == null)
		{	
 			erbs  = new ArrayList<ErrorRequestObject>();
 			
			erb = new ErrorRequestObject(); 
			erb.setError("Missing Authorization in Header"); 
			erb.setPath("Header:Authorization"); 
			erb.setProposedSolution("Authorization Header should contain user:pwd:nhPath as Base64 string");
			erbs.add(erb);

			invReqMsg.append("Authorization in Header not Found. ");
			
			reqei = new RequestErrorInfo();
			reqei.setErrorMessage("Authorization in Header not Found");
			reqei.setErrorDetails( erbs);
			reqeis.add(reqei);

 	    	ResponseInfo ri = new ResponseInfo();
	    	ri.setStatus("Authorization Failed.");
	    	ri.setMessage(invReqMsg.toString().trim());
	    	ri.setInvalidRequestDetails(reqeis);
	        return Response.status(401).entity(ri).build();    			//Authorization missing
		}
    	else
    	{
    		ArrayList <ErrorRequestObject> ErrResps = new ArrayList<ErrorRequestObject>();
        	//Connection connection = null;
    		
    		bwcon = bwAuthorization.AuthenticateUser(authBase64String, memberNh, ErrResps);
    		if (!ErrResps.isEmpty())
    		{

    			reqei = new RequestErrorInfo();
    			reqei.setErrorMessage("Authorization Failed");
    			reqei.setErrorDetails( ErrResps);
    			reqeis.add(reqei);
    			
     	    	ResponseInfo ri = new ResponseInfo();
    	    	ri.setStatus("Authorization Failure");
    	    	ri.setMessage("Authorization Failed");
    	    	ri.setInvalidRequestDetails(  reqeis);
    			
    			return Response.status(401).entity(ri).build();			//Authorization failure
    		}
    	}

		if (email == null)
		{
			erbs  = new ArrayList<ErrorRequestObject>();
			
			erb = new ErrorRequestObject(); erb.setError("IsMissing"); erb.setPath("User.email"); 
			erb.setProposedSolution("Enter Email");
			erbs.add(erb);

			invReqMsg.append("Missing Email. ");
			
			reqei = new RequestErrorInfo();
			reqei.setErrorMessage("Missing Email.");
			reqei.setErrorDetails( erbs);
			reqeis.add(reqei);
		}
		else if (email.trim().equals(""))
    	{	
			erbs  = new ArrayList<ErrorRequestObject>();
			
			erb = new ErrorRequestObject(); erb.setError("IsBlank"); erb.setPath("User.email"); 
			erb.setProposedSolution("Enter Email");
			erbs.add(erb);

			invReqMsg.append("Blank Email. ");
			
			reqei = new RequestErrorInfo();
			reqei.setErrorMessage("Blank Email.");
			reqei.setErrorDetails( erbs);
			reqeis.add(reqei);
    	}
		    		
		if (reqeis.size() == 0)
		{
       	 	ArrayList <ErrorRequestObject> ErrResps = new ArrayList<ErrorRequestObject>();

    		ArrayList<Membership> ml;
        	ml = UserManagement.userGetMemberships(email, ErrResps, authBase64String, bwcon, memberNh, statusCode);
       	 	
        	System.out.println("ml.size :"+ ml.size());
        	System.out.println("ErrResps.size :"+ ErrResps.size());
        	
        	if (ml.size() > 0)
        		return Response.status(200).entity(ml).build();			//Success: returns list of Memberships
    		else
    		{
    			
    			int scode = statusCode.get(0);
    			
		    	resei = new ResponseErrorInfo();		    	
		    	resei.setErrorMessage("Errors on Server");
		    	resei.setErrorDetails(ErrResps);
		    	reseis.add(resei);
		    	
		    	ResponseInfo ri = new ResponseInfo();
		    	ri.setStatus("Failure");
		    	ri.setFailureDetails(reseis);
	    		return Response.status(scode).entity(ri).build();  	// 403: Forbidden Access if Email and nHpATH does not match with authorization
	    	}
    	}
    	else
    	{
	    	ResponseInfo ri = new ResponseInfo();
	    	ri.setStatus("Invalid Request");
	    	ri.setMessage(invReqMsg.toString().trim());
	    	ri.setInvalidRequestDetails(reqeis);
	        return Response.status(400).entity(ri).build();			//400: BAd rEquest: missing Email
    	}
    		    	
        //return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }
    
    
    
    //GET  ....../user/{email}/neighborhood/{nhPath}/collaboration/{collabId}/whiteboards
/*    @Override
    public Response userEmailNeighborhoodNhPathCollaborationCollabIdWhiteboardsGet(String email, String nhPath, Integer collabId, SecurityContext securityContext, String authBase64String) throws NotFoundException {
        // do some magic!
    	
    	System.out.println("email : " + email);
      	System.out.println("nhPath : " + nhPath);
      	System.out.println("collabId : " + collabId);

    	BoardwalkConnection bwcon = null;
		ErrorRequestObject erb;
		ArrayList <ErrorRequestObject> erbs = new ArrayList<ErrorRequestObject>();
		ArrayList<Integer> memberNh = new ArrayList<Integer>();
		ArrayList<Integer> statusCode = new ArrayList<Integer>();

		//System.out.println("authBase64String : " + authBase64String);
			
		if (authBase64String == null)
		{	
			erb = new ErrorRequestObject(); erb.setError("Missing Authorization in Header"); erb.setPath("Header:Authorization"); 
			erb.setProposedSolution("Authorization Header should contain user:pwd:nhPath as Base64 string");
			erbs.add(erb);
			return Response.status(401).entity(erbs).build();
		}
    	else
    	{
    		ArrayList <ErrorRequestObject> ErrResps = new ArrayList<ErrorRequestObject>();
        	//Connection connection = null;
    		
    		bwcon = bwAuthorization.AuthenticateUser(authBase64String, memberNh, ErrResps);
    		if (!ErrResps.isEmpty())
    		{
    			return Response.status(401).entity(ErrResps).build();
    		}
		}

		if (email == null)
		{
    		erb = new ErrorRequestObject(); erb.setError("IsMissing"); erb.setPath("User.email");
    		erb.setProposedSolution("Enter Email");
    		erbs.add(erb);
		}
		else if (email.trim().equals(""))
    	{	
    		erb = new ErrorRequestObject(); erb.setError("IsBlank"); erb.setPath("User.email");
    		erb.setProposedSolution("Enter Email");
    		erbs.add(erb);
    	}

		if (nhPath == null)
		{
    		erb = new ErrorRequestObject(); erb.setError("IsMissing"); erb.setPath("User.nhPath");
    		erb.setProposedSolution("Enter nhPath");
    		erbs.add(erb);
		}
		else if (nhPath.trim().equals(""))
    	{	
    		erb = new ErrorRequestObject(); erb.setError("IsBlank"); erb.setPath("User.nhPath");
    		erb.setProposedSolution("Enter nhPath");
    		erbs.add(erb);
    	}

		if (collabId <= 0)
		{	
			erb = new ErrorRequestObject(); erb.setError("IsNegative"); erb.setPath("collabId"); 
			erb.setProposedSolution("You must enter an Existing Collaboration ID. It should be a Positive Number.");
			erbs.add(erb);
		}

    	if (erbs.size() == 0)
    	{
       	 	ArrayList <ErrorRequestObject> ErrResps = new ArrayList<ErrorRequestObject>();

    		ArrayList<Whiteboard> wbs;
    		
        	wbs = UserManagement.userGetNeighborhoodCollaborationWhiteboards(email, nhPath, collabId, ErrResps, authBase64String, bwcon, memberNh, statusCode);
       	 	
        	System.out.println("wbs.size :"+ wbs.size());
        	System.out.println("ErrResps.size :"+ ErrResps.size());
        	
        	if (wbs.size() > 0)
        		return Response.status(200).entity(wbs).build();		//Success: returns whiteboard list
    		else
    		{
    			int scode = statusCode.get(0);
    			return Response.status(scode).entity(ErrResps).build();		// 404: CollabId Not found. OR. 403: Forbidden Access if Email and nHpATH does not match with authorization
    		}
    	}
    	else
    	{
        	return Response.status(400).entity(erbs).build();		//Bad Request: missing nhPath, Email, Negative CollabId 
    	}    	
    	    	
    }

    */
    
    //GET  /user/{email}/neighborhood/{nhPath}/collaboration/{collabId}/whiteboard/{whiteboardId}/grids
/*    @Override
	public Response userEmailNeighborhoodNhPathCollaborationCollabIdWhiteboardWhiteboardIdGridsGet(String email, String nhPath, Integer collabId, Integer whiteboardId, SecurityContext securityContext, String authBase64String) throws NotFoundException {
		// TODO Auto-generated method stub

    	System.out.println("email : " + email);
      	System.out.println("nhPath : " + nhPath);
      	System.out.println("collabId : " + collabId);
      	System.out.println("whiteboardId : " + whiteboardId);

    	BoardwalkConnection bwcon = null;
		ErrorRequestObject erb;
		ArrayList <ErrorRequestObject> erbs = new ArrayList<ErrorRequestObject>();
		ArrayList<Integer> memberNh = new ArrayList<Integer>();
		ArrayList<Integer> statusCode = new ArrayList<Integer>();

		//System.out.println("authBase64String : " + authBase64String);
			
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

		if (email == null)
		{
    		erb = new ErrorRequestObject(); erb.setError("IsMissing"); erb.setPath("User.email");
    		erb.setProposedSolution("Enter Email");
    		erbs.add(erb);
		}
		else if (email.trim().equals(""))
    	{	
    		erb = new ErrorRequestObject(); erb.setError("IsBlank"); erb.setPath("User.email");
    		erb.setProposedSolution("Enter Email");
    		erbs.add(erb);
    	}

		if (nhPath == null)
		{
    		erb = new ErrorRequestObject(); erb.setError("IsMissing"); erb.setPath("User.nhPath");
    		erb.setProposedSolution("Enter nhPath");
    		erbs.add(erb);
		}
		else if (nhPath.trim().equals(""))
    	{	
    		erb = new ErrorRequestObject(); erb.setError("IsBlank"); erb.setPath("User.nhPath");
    		erb.setProposedSolution("Enter nhPath");
    		erbs.add(erb);
    	}

		if (collabId <= 0)
		{	
			erb = new ErrorRequestObject(); erb.setError("IsNegative"); erb.setPath("collabId"); 
			erb.setProposedSolution("You must enter an Existing Collaboration ID. It should be a Positive Number.");
			erbs.add(erb);
		}
		 		
		if (whiteboardId <= 0)
		{	
			erb = new ErrorRequestObject(); erb.setError("IsNegative"); erb.setPath("whiteboardId"); 
			erb.setProposedSolution("You must enter an Existing Whiteboard ID. It should be a Positive Number.");
			erbs.add(erb);
		}

    	if (erbs.size() == 0)
    	{
       	 	ArrayList <ErrorRequestObject> ErrResps = new ArrayList<ErrorRequestObject>();

    		ArrayList<GridNames> grids;
        	grids = UserManagement.userGetNeighborhoodCollaborationWhiteboardGrids(email, nhPath, collabId, whiteboardId,  ErrResps, authBase64String, bwcon, memberNh, statusCode);
       	 	
        	System.out.println("wbs.size :"+ grids.size());
        	System.out.println("ErrResps.size :"+ ErrResps.size());
        	
        	if (grids.size() > 0)
        		return Response.status(200).entity(grids).build();			//Success returns List of GridNames
    		else
    		{
    			int scode = statusCode.get(0);
    			return Response.status(scode).entity(ErrResps).build();		// 404: CollabId, WbId Not found. OR. 403: Forbidden Access if Email and nHpATH does not match with authorization
    		}
    	}
    	else
    	{
        	return Response.status(400).entity(erbs).build();		//Bad Request: missing nhPath, Email, collabId, wbId
    	}    	
	}
*/
    //GET	......../user/{email}/neighborhood/{nhPath}/collaboration/{collabId}/whiteboard/{whiteboardId}/grid/{gridId}
/*     @Override
    public Response userEmailNeighborhoodNhPathCollaborationCollabIdWhiteboardWhiteboardIdGridGridIdGet(String email, String nhPath, Integer collabId, Integer whiteboardId, Integer gridId, SecurityContext securityContext, String authBase64String) throws NotFoundException {
       	System.out.println("email : " + email);
       	System.out.println("nhPath : " + nhPath);
       	System.out.println("collabId : " + collabId);
       	System.out.println("whiteboardId : " + whiteboardId);
       	System.out.println("gridId : " + gridId);

    	BoardwalkConnection bwcon = null;
 		ErrorRequestObject erb;
 		ArrayList <ErrorRequestObject> erbs = new ArrayList<ErrorRequestObject>();
		ArrayList<Integer> memberNh = new ArrayList<Integer>();

		
 		//System.out.println("authBase64String : " + authBase64String);
 			
 		if (authBase64String == null)
 		{	
 			erb = new ErrorRequestObject(); erb.setError("Missing Authorization in Header"); erb.setPath("Header:Authorization"); 
 			erb.setProposedSolution("Authorization Header should contain user:pwd:nhPath as Base64 string");
 			erbs.add(erb);
			return Response.status(401).entity(erbs).build();
		}
    	else
    	{
    		ArrayList <ErrorRequestObject> ErrResps = new ArrayList<ErrorRequestObject>();
        	//Connection connection = null;
    		
    		bwcon = bwAuthorization.AuthenticateUser(authBase64String, memberNh, ErrResps);
    		if (!ErrResps.isEmpty())
    		{
    			return Response.status(401).entity(ErrResps).build();
    		}
    	}

 		if (email == null)
 		{
     		erb = new ErrorRequestObject(); erb.setError("IsMissing"); erb.setPath("User.email");
     		erb.setProposedSolution("Enter Email");
     		erbs.add(erb);
 		}
 		else if (email.trim().equals(""))
     	{	
     		erb = new ErrorRequestObject(); erb.setError("IsBlank"); erb.setPath("User.email");
     		erb.setProposedSolution("Enter Email");
     		erbs.add(erb);
     	}

 		if (nhPath == null)
 		{
     		erb = new ErrorRequestObject(); erb.setError("IsMissing"); erb.setPath("User.nhPath");
     		erb.setProposedSolution("Enter nhPath");
     		erbs.add(erb);
 		}
 		else if (nhPath.trim().equals(""))
     	{	
     		erb = new ErrorRequestObject(); erb.setError("IsBlank"); erb.setPath("User.nhPath");
     		erb.setProposedSolution("Enter nhPath");
     		erbs.add(erb);
     	}

 		if (collabId <= 0)
 		{	
 			erb = new ErrorRequestObject(); erb.setError("IsNegative"); erb.setPath("collabId"); 
 			erb.setProposedSolution("You must enter an Existing Collaboration ID. It should be a Positive Number.");
 			erbs.add(erb);
 		}

 		if (whiteboardId <= 0)
 		{	
 			erb = new ErrorRequestObject(); erb.setError("IsNegative"); erb.setPath("whiteboardId"); 
 			erb.setProposedSolution("You must enter an Existing Whiteboard ID. It should be a Positive Number.");
 			erbs.add(erb);
 		}
 		
 		if (gridId <= 0)
 		{	
 			erb = new ErrorRequestObject(); erb.setError("IsNegative"); erb.setPath("gridId"); 
 			erb.setProposedSolution("You must enter an Existing Grid ID. It should be a Positive Number.");
 			erbs.add(erb);
 		}
 		
     	if (erbs.size() == 0)
     	{
    	 	ArrayList <ErrorRequestObject> ErrResps = new ArrayList<ErrorRequestObject>();

     		GridInfo gi;
         	gi = UserManagement.userGetNeighborhoodCollaborationWhiteboardGrid(email, nhPath, collabId, whiteboardId, gridId, ErrResps, authBase64String, bwcon, memberNh);
        	 	
         	System.out.println("gi.name :"+ gi.getName() );
         	System.out.println("ErrResps.size :"+ ErrResps.size());
         	
         	if (gi != null) 
				return Response.status(200).entity(gi).build();		//Success: returns GridInfo
			else 
				return Response.status(403).entity(ErrResps).build(); // Server understood the request but refuses to authorize it.
     	}
     	else
     	{
         	return Response.status(400).entity(erbs).build();	//Bad Request: missing nhPath, Email, CollabId, WbId, GridId
     	}    	        
    }
*/
     
     //GET.....  /user/{email}/neighborhood/{nhPath}/collaboration/{collabId}/whiteboard/{whiteboardId}/gridchain/{gridId}
/*     @Override
     public Response userEmailNeighborhoodNhPathCollaborationCollabIdWhiteboardWhiteboardIdGridchainGridIdGet(String email, String nhPath, Integer collabId, Integer whiteboardId, Integer gridId, SecurityContext securityContext, String authBase64String) throws NotFoundException {
         // do some magic!
         return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
     }
 */    
    //	/user/{email}/neighborhood/{nhPath}/collaborations
/*    @Override
    public Response userEmailNeighborhoodNhPathCollaborationsGet(String email, String nhPath, SecurityContext securityContext, String authBase64String) throws NotFoundException {
        // do some magic!
    	
      	System.out.println("email : " + email);
      	System.out.println("nhPath : " + nhPath);

    	BoardwalkConnection bwcon = null;
		ErrorRequestObject erb;
		ArrayList <ErrorRequestObject> erbs = new ArrayList<ErrorRequestObject>();
		ArrayList<Integer> memberNh = new ArrayList<Integer>();

		//System.out.println("authBase64String : " + authBase64String);
			
		if (authBase64String == null)
		{	
			erb = new ErrorRequestObject(); erb.setError("Missing Authorization in Header"); erb.setPath("Header:Authorization"); 
			erb.setProposedSolution("Authorization Header should contain user:pwd:nhPath as Base64 string");
			erbs.add(erb);
			return Response.status(401).entity(erbs).build();
		}
    	else
    	{
    		ArrayList <ErrorRequestObject> ErrResps = new ArrayList<ErrorRequestObject>();
        	//Connection connection = null;
    		
    		bwcon = bwAuthorization.AuthenticateUser(authBase64String, memberNh, ErrResps);
    		if (!ErrResps.isEmpty())
    		{
    			return Response.status(401).entity(ErrResps).build();
    		}
    	}

		if (email == null)
		{
    		erb = new ErrorRequestObject(); erb.setError("IsMissing"); erb.setPath("User.email");
    		erb.setProposedSolution("Enter Email");
    		erbs.add(erb);
		}
		else if (email.trim().equals(""))
    	{	
    		erb = new ErrorRequestObject(); erb.setError("IsBlank"); erb.setPath("User.email");
    		erb.setProposedSolution("Enter Email");
    		erbs.add(erb);
    	}

		if (nhPath == null)
		{
    		erb = new ErrorRequestObject(); erb.setError("IsMissing"); erb.setPath("User.nhPath");
    		erb.setProposedSolution("Enter nhPath");
    		erbs.add(erb);
		}
		else if (nhPath.trim().equals(""))
    	{	
    		erb = new ErrorRequestObject(); erb.setError("IsBlank"); erb.setPath("User.nhPath");
    		erb.setProposedSolution("Enter nhPath");
    		erbs.add(erb);
    	}
		
    	if (erbs.size() == 0)
    	{
       	 	ArrayList <ErrorRequestObject> ErrResps = new ArrayList<ErrorRequestObject>();

    		ArrayList<Collaboration> cl;
        	cl = UserManagement.userGetNeighborhoodCollaborations(email, nhPath, ErrResps, authBase64String, bwcon, memberNh);
       	 	
        	System.out.println("cl.size :"+ cl.size());
        	System.out.println("ErrResps.size :"+ ErrResps.size());
        	
        	if (cl.size() > 0)
        		return Response.status(200).entity(cl).build();		// Success. returns collaborations list
    		else
    		{
    			return Response.status(403).entity(ErrResps).build();	//  server understood the request but refuses to authorize it.
    		}
    	}
    	else
    	{
        	return Response.status(400).entity(erbs).build();	//Bad Request: missing nhPath, Email
    	}    	
    }
*/
    @Override
    public Response userGet( Boolean active, SecurityContext securityContext, String authBase64String) throws NotFoundException {
    	System.out.println("active : " + active);

    	BoardwalkConnection bwcon = null;
		ErrorRequestObject erb;
		ArrayList <ErrorRequestObject> erbs = new ArrayList<ErrorRequestObject>();

		//System.out.println("authBase64String : " + authBase64String);
			
		if (authBase64String == null)
		{	
			erb = new ErrorRequestObject(); erb.setError("Missing Authorization in Header"); erb.setPath("Header:Authorization"); 
			erb.setProposedSolution("Authorization Header should contain user:pwd:nhPath as Base64 string");
			erbs.add(erb);
			return Response.status(401).entity(erbs).build();
		}
    	else
    	{
    		ArrayList <ErrorRequestObject> ErrResps = new ArrayList<ErrorRequestObject>();
        	//Connection connection = null;
    		
    		ArrayList<Integer> memberNh = new ArrayList<Integer>();
    		bwcon = bwAuthorization.AuthenticateUser(authBase64String, memberNh, ErrResps);
    		if (!ErrResps.isEmpty())
    		{
    			return Response.status(401).entity(ErrResps).build();
    		}
    	}
		
    	if (erbs.size() == 0)
    	{
       	 	ArrayList <ErrorRequestObject> ErrResps = new ArrayList<ErrorRequestObject>();

    		ArrayList<User> ul;
        	ul = UserManagement.userGet(active, ErrResps, bwcon);
       	 	
        	System.out.println("ul.size :"+ ul.size());
        	System.out.println("ErrResps.size :"+ ErrResps.size());
        	
        	if (ul.size() > 0)
        		return Response.status(200).entity(ul).build();				// success list of active/inactive users
    		else
    		{
    			return Response.status(500).entity(ErrResps).build();		// something has gone wrong on the website's server, but the server could not be more specific on what the exact problem is.
    		}
    	}
    	else
    	{
        	return Response.status(400).entity(erbs).build();
    	}
		
  //      return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }
    @Override
    public Response userPost(User user, SecurityContext securityContext, String authBase64String) throws NotFoundException {
    	
    	BoardwalkConnection bwcon = null;
    	
		ArrayList <ErrorRequestObject> erbs = new ArrayList<ErrorRequestObject>();
		ErrorRequestObject erb;

    	//System.out.println("authBase64String : " + authBase64String);
		
		if (authBase64String == null)
		{	
			erb = new ErrorRequestObject(); erb.setError("Missing Authorization in Header"); erb.setPath("Header:Authorization"); 
			erb.setProposedSolution("Authorization Header should contain user:pwd:nhPath as Base64 string");
			erbs.add(erb);
			return Response.status(401).entity(erbs).build();
		}
    	else
    	{
    		ArrayList <ErrorRequestObject> ErrResps = new ArrayList<ErrorRequestObject>();
        	//Connection connection = null;
    		
    		ArrayList<Integer> memberNh = new ArrayList<Integer>();
    		bwcon = bwAuthorization.AuthenticateUser(authBase64String, memberNh, ErrResps);
    		if (!ErrResps.isEmpty())
    		{
    			return Response.status(401).entity(ErrResps).build();
    		}
    	}

		String Email = user.getUserEmail();
		String ExternalId = user.getExternalUserId();
		String FirstName = user.getFirstName();
		String LastName = user.getLastName();
		String Password = user.getPassword();
		
		if (Email == null)
		{
    		erb = new ErrorRequestObject(); erb.setError("IsMissing"); erb.setPath("User.email");
    		erb.setProposedSolution("Enter Email");
    		erbs.add(erb);
		}
		else if (Email.trim().equals(""))
    	{	
    		erb = new ErrorRequestObject(); erb.setError("IsBlank"); erb.setPath("User.email");
    		erb.setProposedSolution("Enter Email");
    		erbs.add(erb);
    	}
		
		if (ExternalId == null)
		{
    		erb = new ErrorRequestObject(); erb.setError("IsMissing"); erb.setPath("User.ExternalId");
    		erb.setProposedSolution("Enter ExternalId");
    		erbs.add(erb);
		}
		else if (ExternalId.trim().equals(""))
    	{	
    		erb = new ErrorRequestObject(); erb.setError("IsBlank"); erb.setPath("User.ExternalId"); 
    		erb.setProposedSolution("Enter ExternalId");
    		erbs.add(erb);
    	}
		
		if (FirstName == null)
		{
    		erb = new ErrorRequestObject(); erb.setError("IsMissing"); erb.setPath("User.FirstName");
    		erb.setProposedSolution("Enter FirstName");
    		erbs.add(erb);
		}
		else if (FirstName.trim().equals(""))
    	{	
    		erb = new ErrorRequestObject(); erb.setError("IsBlank"); erb.setPath("User.FirstName");
    		erb.setProposedSolution("Enter First Name");
    		erbs.add(erb);
    	}
    	
		if (LastName == null)
		{
    		erb = new ErrorRequestObject(); erb.setError("IsMissing"); erb.setPath("User.LastName");
    		erb.setProposedSolution("Enter LastName");
    		erbs.add(erb);
		}
		else if (LastName.trim().equals(""))
    	{	
    		erb = new ErrorRequestObject(); erb.setError("IsBlank"); erb.setPath("User.LastName"); 
    		erb.setProposedSolution("Enter Last Name");
    		erbs.add(erb);
    	}
    	
		if (Password == null)
		{
    		erb = new ErrorRequestObject(); erb.setError("IsMissing"); erb.setPath("User.Password");
    		erb.setProposedSolution("Enter Password");
    		erbs.add(erb);
		}
    	else if (Password.trim().equals(""))
    	{	
    		erb = new ErrorRequestObject(); erb.setError("IsBlank"); erb.setPath("User.Password"); 
    		erb.setProposedSolution("Enter Password");
    		erbs.add(erb);
    	}

    	if (erbs.size() == 0)
    	{
       	 	ArrayList <ErrorRequestObject> ErrResps = new ArrayList<ErrorRequestObject>();
    		ArrayList<User> ul;
        	ul = UserManagement.userPost(user, ErrResps, bwcon);
        	System.out.println("ul.size :"+ ul.size());
        	System.out.println("ErrResps.size :"+ ErrResps.size());
        	
        	if (ul.size() > 0)
        		return Response.status(200).entity(ul).build();		//Successfully created user
    		else
    		{
    			return Response.status(409).entity(ErrResps).build();	//409 Conflict. The request could not be completed due to a conflict with the current state of the target resource. This code is used in situations where the user might be able to resolve the conflict and resubmit the request.
    		}
    	}
    	else
    	{
        	return Response.status(400).entity(erbs).build();
    	}
    }


    @Override
    public Response userPut(User user, SecurityContext securityContext, String authBase64String) throws NotFoundException {
    	
    	BoardwalkConnection bwcon = null;
		ErrorRequestObject erb;
		ArrayList <ErrorRequestObject> erbs = new ArrayList<ErrorRequestObject>();

		//System.out.println("authBase64String : " + authBase64String);
			
		if (authBase64String == null)
		{	
			erb = new ErrorRequestObject(); erb.setError("Missing Authorization in Header"); erb.setPath("Header:Authorization"); 
			erb.setProposedSolution("Authorization Header should contain user:pwd:nhPath as Base64 string");
			erbs.add(erb);
			return Response.status(401).entity(erbs).build();		//401: Missing Authorisation
		}
    	else
    	{
    		ArrayList <ErrorRequestObject> ErrResps = new ArrayList<ErrorRequestObject>();
        	//Connection connection = null;
    		
    		ArrayList<Integer> memberNh = new ArrayList<Integer>();
    		bwcon = bwAuthorization.AuthenticateUser(authBase64String, memberNh, ErrResps);
    		if (!ErrResps.isEmpty())
    		{
    			return Response.status(401).entity(ErrResps).build();	//401:  Authorisation failed
    		}
    	}

		String Email = user.getUserEmail();
		String ExternalId = user.getExternalUserId();
		String FirstName = user.getFirstName();
		String LastName = user.getLastName();
		String Password = user.getPassword();

		if (Email == null)
		{
    		erb = new ErrorRequestObject(); erb.setError("IsMissing"); erb.setPath("User.email");
    		erb.setProposedSolution("Enter Email");
    		erbs.add(erb);
		}
		else if (Email.trim().equals(""))
    	{	
    		erb = new ErrorRequestObject(); erb.setError("IsBlank"); erb.setPath("User.email");
    		erb.setProposedSolution("Enter Email");
    		erbs.add(erb);
    	}
		
		if (ExternalId == null)
		{
    		erb = new ErrorRequestObject(); erb.setError("IsMissing"); erb.setPath("User.ExternalId");
    		erb.setProposedSolution("Enter ExternalId");
    		erbs.add(erb);
		}
		else if (ExternalId.trim().equals(""))
    	{	
    		erb = new ErrorRequestObject(); erb.setError("IsBlank"); erb.setPath("User.ExternalId"); 
    		erb.setProposedSolution("Enter ExternalId");
    		erbs.add(erb);
    	}
		
		if (FirstName == null)
		{
    		erb = new ErrorRequestObject(); erb.setError("IsMissing"); erb.setPath("User.FirstName");
    		erb.setProposedSolution("Enter FirstName");
    		erbs.add(erb);
		}
		else if (FirstName.trim().equals(""))
    	{	
    		erb = new ErrorRequestObject(); erb.setError("IsBlank"); erb.setPath("User.FirstName");
    		erb.setProposedSolution("Enter First Name");
    		erbs.add(erb);
    	}
    	
		if (LastName == null)
		{
    		erb = new ErrorRequestObject(); erb.setError("IsMissing"); erb.setPath("User.LastName");
    		erb.setProposedSolution("Enter LastName");
    		erbs.add(erb);
		}
		else if (LastName.trim().equals(""))
    	{	
    		erb = new ErrorRequestObject(); erb.setError("IsBlank"); erb.setPath("User.LastName"); 
    		erb.setProposedSolution("Enter Last Name");
    		erbs.add(erb);
    	}
    	
		if (Password == null)
		{
    		erb = new ErrorRequestObject(); erb.setError("IsMissing"); erb.setPath("User.Password");
    		erb.setProposedSolution("Enter Password");
    		erbs.add(erb);
		}
    	else if (Password.trim().equals(""))
    	{	
    		erb = new ErrorRequestObject(); erb.setError("IsBlank"); erb.setPath("User.Password"); 
    		erb.setProposedSolution("Enter Password");
    		erbs.add(erb);
    	}

    	if (erbs.size() == 0)
    	{
	  	 	ArrayList <ErrorRequestObject> ErrResps = new ArrayList<ErrorRequestObject>();
			String o;
			o = UserManagement.userPut(user, ErrResps, bwcon);
  			if (ErrResps.size() > 0)
  				return Response.status(409).entity(ErrResps).build();   	//The request could not be completed due to a conflict with the current state of the target resource. This code is used in situations where the user might be able to resolve the conflict and resubmit the request.
    		else
    		{
		        return Response.status(200).entity(o).build();
    		}
		}
    	else
    	{
        	return Response.status(400).entity(erbs).build();						//	400, message = "Invalid input (Bad Request)",
    	}
        // do some magic!
//        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }

    @Override
    public Response userUserIdDelete(Integer userId, SecurityContext securityContext, String authBase64String) throws NotFoundException {
        // do some magic!
    	BoardwalkConnection bwcon = null;
    	
		ErrorRequestObject erb;
		ArrayList <ErrorRequestObject> erbs = new ArrayList<ErrorRequestObject>();

		//System.out.println("authBase64String : " + authBase64String);
		
		if (authBase64String == null)
		{	
			erb = new ErrorRequestObject(); erb.setError("Missing Authorization in Header"); erb.setPath("Header:Authorization"); 
			erb.setProposedSolution("Authorization Header should contain user:pwd:nhPath as Base64 string");
			erbs.add(erb);
			return Response.status(401).entity(erbs).build();
		}
    	else
    	{
    		ArrayList <ErrorRequestObject> ErrResps = new ArrayList<ErrorRequestObject>();
        	//Connection connection = null;
    		
    		ArrayList<Integer> memberNh = new ArrayList<Integer>();
    		bwcon = bwAuthorization.AuthenticateUser(authBase64String, memberNh, ErrResps);
    		if (!ErrResps.isEmpty())
    		{
    			return Response.status(401).entity(ErrResps).build();
    		}
    	}
		
		if (userId <= 0)
		{	
			erb = new ErrorRequestObject(); erb.setError("IsNegativeOrZero"); erb.setPath("userId"); 
			erb.setProposedSolution("UserId must be Positive Integer");
			erbs.add(erb);
		}

    	if (erbs.size() == 0)
    	{
	  	 	ArrayList <ErrorRequestObject> ErrResps = new ArrayList<ErrorRequestObject>();
	    	String o;
			o = UserManagement.userUserIdDelete(userId, ErrResps, bwcon);

	    	if (ErrResps.size() == 0)
	        	return Response.status(200).entity(o).build();				//successfully de-activated user
	    	else
	    		return Response.status(404).entity(ErrResps).build();   	//user not found
    	}
    	else
    	{
        	return Response.status(400).entity(erbs).build();				//bad request
    	}
    }
    
    @Override
    public Response userUserIdGet(Integer userId, SecurityContext securityContext, String authBase64String) throws NotFoundException {
        // do some magic!

    	BoardwalkConnection bwcon = null;
    	ErrorRequestObject erb;
		ArrayList <ErrorRequestObject> erbs = new ArrayList<ErrorRequestObject>();

    	//System.out.println("authBase64String : " + authBase64String);

    	if (authBase64String == null)
		{	
			erb = new ErrorRequestObject(); erb.setError("Missing Authorization in Header"); erb.setPath("Header:Authorization"); 
			erb.setProposedSolution("Authorization Header should contain user:pwd:nhPath as Base64 string");
			erbs.add(erb);
			return Response.status(401).entity(erbs).build();
		}
    	else
    	{
    		ArrayList <ErrorRequestObject> ErrResps = new ArrayList<ErrorRequestObject>();
        	//Connection connection = null;
    		
    		ArrayList<Integer> memberNh = new ArrayList<Integer>();
    		bwcon = bwAuthorization.AuthenticateUser(authBase64String, memberNh, ErrResps);
    		if (!ErrResps.isEmpty())
    		{
    			return Response.status(401).entity(ErrResps).build();
    		}
    	}
		 
		
    	if (userId <= 0)
    	{	
    		erb = new ErrorRequestObject(); erb.setError("IsNegativeOrZero"); erb.setPath("userId"); 
    		erb.setProposedSolution("UserId must be Positive Integer");
    		erbs.add(erb);
    	}
		 
    	if (erbs.size() == 0)
    	{
	 	 	ArrayList <ErrorRequestObject> ErrResps = new ArrayList<ErrorRequestObject>();
	    	User user = UserManagement.userUserIdGet(userId, ErrResps, bwcon);
	    	
	    	if (user != null)
	        	return Response.status(200).entity(user).build();
	    	else
	    		return Response.status(404).entity(ErrResps).build();   	
    	}
    	else
    	{
        	return Response.status(400).entity(erbs).build();
    	}
    }







/*	@Override
	public Response userEmailNeighborhoodNhPathCollaborationCollabIdWhiteboardWhiteboardIdGridGridIdGet(String email,
			String nhPath, Integer collabId, Integer whiteboardId, Integer gridId, SecurityContext securityContext,
			String authBase64String) throws NotFoundException {
		// TODO Auto-generated method stub
		return null;
	}*/
}
