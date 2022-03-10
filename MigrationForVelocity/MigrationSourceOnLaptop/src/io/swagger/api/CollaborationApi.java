 package io.swagger.api;

import io.swagger.api.CollaborationApiService;
import io.swagger.api.factories.CollaborationApiServiceFactory;
import io.swagger.annotations.ApiParam;
import io.swagger.model.Collaboration;
import io.swagger.model.ErrorRequestObject;
import io.swagger.model.GridInfo;
import io.swagger.model.Whiteboard;
import io.swagger.api.NotFoundException;
import javax.servlet.ServletConfig;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.*;

@Path("/collaboration")

@Produces({ "application/json" })
@io.swagger.annotations.Api(description = "the collaboration API")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-10-03T07:16:22.724Z")
public class CollaborationApi  {
   private final CollaborationApiService delegate;

   public CollaborationApi(@Context ServletConfig servletContext) {
      CollaborationApiService delegate = null;

      if (servletContext != null) {
         String implClass = servletContext.getInitParameter("CollaborationApi.implementation");
         if (implClass != null && !"".equals(implClass.trim())) {
            try {
               delegate = (CollaborationApiService) Class.forName(implClass).newInstance();
            } catch (Exception e) {
               throw new RuntimeException(e);
            }
         } 
      }

      if (delegate == null) {
         delegate = CollaborationApiServiceFactory.getCollaborationApi();
      }

      this.delegate = delegate;
   }

    @DELETE
    @Path("/{collabId}")
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Delete collaboration by ID", notes = "", response = String.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "bwAuth")
    }, tags={ "collaboration", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "successful operation", response = String.class),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "Invalid input. Negative CollabId", response = ErrorRequestObject.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 401, message = "Invalid Authorization", response = ErrorRequestObject.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "Collaboration Id Not found", response = ErrorRequestObject.class, responseContainer = "List") })
    public Response collaborationCollabIdDelete(@ApiParam(value = "",required=true) @PathParam("collabId") Integer collabId
,@Context SecurityContext securityContext,  @HeaderParam("Authorization") String authBase64String)
    throws NotFoundException {
        return delegate.collaborationCollabIdDelete(collabId,securityContext,authBase64String);
    }
    @GET
    @Path("/{collabId}/grids")
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Get the list of all Grids present in the Whiteboard of the Collaboration in Neighborhood that user can access using his/her Neighborhood memberships.", notes = "User can GET the list of Grids present in the Whiteboard of the Collaboration using his/her memberships details for that Neighborhood.", response = GridInfo.class, responseContainer = "List", authorizations = {
        @io.swagger.annotations.Authorization(value = "bwAuth")
    }, tags={ "collaboration", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "successful operation", response = GridInfo.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "Invalid input. Null or missing collabId", response = ErrorRequestObject.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 401, message = "Invalid Authorization", response = ErrorRequestObject.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "CollabId Not found.", response = ErrorRequestObject.class, responseContainer = "List") })
    public Response collaborationCollabIdGridsGet(@ApiParam(value = "",required=true) @PathParam("collabId") Integer collabId
,@Context SecurityContext securityContext,  @HeaderParam("Authorization") String authBase64String)
    throws NotFoundException {
        return delegate.collaborationCollabIdGridsGet(collabId,securityContext, authBase64String);
    }
    @GET
    @Path("/{collabId}/whiteboard")
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Get the whiteboards for a collaboration", notes = "", response = Whiteboard.class, responseContainer = "List", authorizations = {
        @io.swagger.annotations.Authorization(value = "bwAuth")
    }, tags={ "collaboration", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "successful operation", response = Whiteboard.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "Invalid input. Negative CollabId", response = ErrorRequestObject.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 401, message = "Invalid Authorization", response = ErrorRequestObject.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "Collaboration Id not found", response = ErrorRequestObject.class, responseContainer = "List") })
    public Response collaborationCollabIdWhiteboardGet(@ApiParam(value = "",required=true) @PathParam("collabId") Integer collabId
,@Context SecurityContext securityContext,  @HeaderParam("Authorization") String authBase64String)
    throws NotFoundException {
        return delegate.collaborationCollabIdWhiteboardGet(collabId,securityContext,authBase64String);
    }
    @POST
    @Path("/{collabId}/whiteboard")
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Create new Whiteboard", notes = "", response = Integer.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "bwAuth")
    }, tags={ "collaboration", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "successful operation. WhiteboardId returned.", response = Integer.class),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "Invalid input. Negative collabId. Null or Missing or Empty Whiteboard Name.", response = ErrorRequestObject.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 401, message = "Invalid Authorization", response = ErrorRequestObject.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "Collaboration Id Not found.", response = ErrorRequestObject.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 409, message = "Creating new Whiteboard in Collaboration Failed. Whiteboard already exists.", response = ErrorRequestObject.class, responseContainer = "List") })
    public Response collaborationCollabIdWhiteboardPost(@ApiParam(value = "",required=true) @PathParam("collabId") Integer collabId
,@ApiParam(value = "Whiteboard creation details" ,required=true) Whiteboard wb
,@Context SecurityContext securityContext,  @HeaderParam("Authorization") String authBase64String)
    throws NotFoundException {
        return delegate.collaborationCollabIdWhiteboardPost(collabId,wb,securityContext,authBase64String);
    }
    @DELETE
    @Path("/{collabId}/whiteboard/{whiteboardId}")
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Delete whiteboard by ID", notes = "", response = String.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "bwAuth")
    }, tags={ "collaboration", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "successful operation", response = String.class),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "Invalid input. Negative CollabId, Negative WhiteboardId", response = ErrorRequestObject.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 401, message = "Invalid Authorization", response = ErrorRequestObject.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "CollabId Not found. Whiteboard Not Found", response = ErrorRequestObject.class, responseContainer = "List") })
    public Response collaborationCollabIdWhiteboardWhiteboardIdDelete(@ApiParam(value = "",required=true) @PathParam("collabId") Integer collabId
,@ApiParam(value = "",required=true) @PathParam("whiteboardId") Integer whiteboardId
,@Context SecurityContext securityContext,  @HeaderParam("Authorization") String authBase64String)
    throws NotFoundException {
        return delegate.collaborationCollabIdWhiteboardWhiteboardIdDelete(collabId,whiteboardId,securityContext,authBase64String);
    }
    @GET
    @Path("/{collabId}/whiteboard/{whiteboardId}/grids")
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Get the list of all Grids present in the Whiteboard of the Collaboration in Neighborhood that user can access using his/her Neighborhood memberships.", notes = "User can GET the list of Grids present in the Whiteboard of the Collaboration using his/her memberships details for that Neighborhood.", response = GridInfo.class, responseContainer = "List", authorizations = {
        @io.swagger.annotations.Authorization(value = "bwAuth")
    }, tags={ "collaboration", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "successful operation", response = GridInfo.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "Invalid input. Null or missing collabId, wbId", response = ErrorRequestObject.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 401, message = "Invalid Authorization", response = ErrorRequestObject.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "CollabId or WhiteboardId Not found.", response = ErrorRequestObject.class, responseContainer = "List") })
    public Response collaborationCollabIdWhiteboardWhiteboardIdGridsGet(@ApiParam(value = "",required=true) @PathParam("collabId") Integer collabId
,@ApiParam(value = "",required=true) @PathParam("whiteboardId") Integer whiteboardId
,@Context SecurityContext securityContext,  @HeaderParam("Authorization") String authBase64String)
    throws NotFoundException {
        return delegate.collaborationCollabIdWhiteboardWhiteboardIdGridsGet(collabId,whiteboardId,securityContext, authBase64String);
    }
    @POST
    
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Create new collaboration", notes = "", response = Collaboration.class, responseContainer = "List", authorizations = {
        @io.swagger.annotations.Authorization(value = "bwAuth")
    }, tags={ "collaboration", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "successful operation", response = Collaboration.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "Null or missing or empty collabName", response = ErrorRequestObject.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 401, message = "Invalid Authorization", response = ErrorRequestObject.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 409, message = "Collaboration already exists with this name.", response = ErrorRequestObject.class, responseContainer = "List") })
    public Response collaborationPost(@ApiParam(value = "Collaboration creation details" ,required=true) Collaboration collab
,@Context SecurityContext securityContext,  @HeaderParam("Authorization") String authBase64String)
    throws NotFoundException {
        return delegate.collaborationPost(collab,securityContext, authBase64String);
    }
}
