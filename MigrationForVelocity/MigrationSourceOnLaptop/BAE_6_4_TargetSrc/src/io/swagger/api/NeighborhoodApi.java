package io.swagger.api;

import io.swagger.api.NeighborhoodApiService;
import io.swagger.api.factories.NeighborhoodApiServiceFactory;
import io.swagger.annotations.ApiParam;
import io.swagger.model.Collaboration;
import io.swagger.model.ErrorRequestObject;
import io.swagger.model.Member;
import io.swagger.model.Neighborhood;
import io.swagger.model.Relation;
import io.swagger.api.NotFoundException;
import javax.servlet.ServletConfig;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.*;

@Path("/neighborhood")

@Produces({ "application/json" })
@io.swagger.annotations.Api(description = "the neighborhood API")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-10-03T07:16:22.724Z")
public class NeighborhoodApi  {
   private final NeighborhoodApiService delegate;

   public NeighborhoodApi(@Context ServletConfig servletContext) {
      NeighborhoodApiService delegate = null;

      if (servletContext != null) {
         String implClass = servletContext.getInitParameter("NeighborhoodApi.implementation");
         if (implClass != null && !"".equals(implClass.trim())) {
            try {
               delegate = (NeighborhoodApiService) Class.forName(implClass).newInstance();
            } catch (Exception e) {
               throw new RuntimeException(e);
            }
         } 
      }

      if (delegate == null) {
         delegate = NeighborhoodApiServiceFactory.getNeighborhoodApi();
      }

      this.delegate = delegate;
   }

    @GET
    
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Get an aaray of neighborhoods", notes = "", response = Neighborhood.class, responseContainer = "List", authorizations = {
        @io.swagger.annotations.Authorization(value = "bwAuth")
    }, tags={ "neighborhood", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "successful operation", response = Neighborhood.class, responseContainer = "List") })
    public Response neighborhoodGet(@ApiParam(value = "regular expression of upto four level neighborhood hierarchy") @QueryParam("neighborhoodSpec") String neighborhoodSpec
,@Context SecurityContext securityContext, @HeaderParam("Authorization") String authBase64String)
    throws NotFoundException {
        return delegate.neighborhoodGet(neighborhoodSpec,securityContext, authBase64String);
    }
    @GET
    @Path("/{nhId}/collaboration")
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Get the collaboration heirarchy", notes = "", response = Collaboration.class, responseContainer = "List", authorizations = {
        @io.swagger.annotations.Authorization(value = "bwAuth")
    }, tags={ "collaboration", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "successful operation", response = Collaboration.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "Invalid input. Negative NhId", response = ErrorRequestObject.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 401, message = "Invalid Authorization", response = ErrorRequestObject.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "Neighborhood Id not found", response = ErrorRequestObject.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 500, message = "Failed to GET Collaborations of Neighborhood from Server", response = ErrorRequestObject.class, responseContainer = "List") })
    public Response neighborhoodNhIdCollaborationGet(@ApiParam(value = "",required=true) @PathParam("nhId") Integer nhId
,@Context SecurityContext securityContext, @HeaderParam("Authorization") String authBase64String)
    throws NotFoundException {
        return delegate.neighborhoodNhIdCollaborationGet(nhId,securityContext,authBase64String);
    }
    @DELETE
    @Path("/{nhId}")
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Delete neighborhood by ID", notes = "", response = String.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "bwAuth")
    }, tags={ "neighborhood", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "successful operation", response = String.class),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "Invalid input. Negative NhId", response = ErrorRequestObject.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 401, message = "Invalid Authorization", response = ErrorRequestObject.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "Neighborhood Not found", response = ErrorRequestObject.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 500, message = "Failed to Delete Neighborhood. BoardwalkException exception occured on server", response = ErrorRequestObject.class, responseContainer = "List") })
    public Response neighborhoodNhIdDelete(@ApiParam(value = "",required=true) @PathParam("nhId") Integer nhId
,@Context SecurityContext securityContext, @HeaderParam("Authorization") String authBase64String)
    throws NotFoundException {
        return delegate.neighborhoodNhIdDelete(nhId,securityContext,authBase64String);
    }
    @GET
    @Path("/{nhId}")
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Get the entire neighborhood heirarchy below this neighborhood", notes = "", response = Neighborhood.class, responseContainer = "List", authorizations = {
        @io.swagger.annotations.Authorization(value = "bwAuth")
    }, tags={ "neighborhood", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "successful operation", response = Neighborhood.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "Invalid input. Negative NhId", response = ErrorRequestObject.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 401, message = "Invalid Authorization", response = ErrorRequestObject.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "Neighborhood Id not found", response = ErrorRequestObject.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 500, message = "Failed to GET Neighborhood on server", response = ErrorRequestObject.class, responseContainer = "List") })
    public Response neighborhoodNhIdGet(@ApiParam(value = "",required=true) @PathParam("nhId") Integer nhId
,@Context SecurityContext securityContext, @HeaderParam("Authorization") String authBase64String)
    throws NotFoundException {
        return delegate.neighborhoodNhIdGet(nhId,securityContext,authBase64String);
    }
    @GET
    @Path("/{nhId}/member")
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Get list of members for neighborhood", notes = "", response = Member.class, responseContainer = "List", authorizations = {
        @io.swagger.annotations.Authorization(value = "bwAuth")
    }, tags={ "neighborhood", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "successful operation", response = Member.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "Invalid input. Negative NhId", response = ErrorRequestObject.class, responseContainer = "List"),

        @io.swagger.annotations.ApiResponse(code = 401, message = "Invalid Authorization", response = ErrorRequestObject.class, responseContainer = "List"),

        @io.swagger.annotations.ApiResponse(code = 404, message = "Neighborhood Id not found", response = ErrorRequestObject.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 500, message = "Failed to GET Membership details of Neighborhood", response = ErrorRequestObject.class, responseContainer = "List") })
    public Response neighborhoodNhIdMemberGet(@ApiParam(value = "",required=true) @PathParam("nhId") Integer nhId
,@Context SecurityContext securityContext, @HeaderParam("Authorization") String authBase64String)
    throws NotFoundException {
        return delegate.neighborhoodNhIdMemberGet(nhId,securityContext,authBase64String);
    }
    @DELETE
    @Path("/{nhId}/member/{memberId}")
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Delete member from neighborhood", notes = "", response = String.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "bwAuth")
    }, tags={ "neighborhood", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "successful operation", response = String.class),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "Invalid input. Negative NhId, Negative MemberId", response = ErrorRequestObject.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 401, message = "Invalid Authorization", response = ErrorRequestObject.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "Neighborhood Not found. Membership Not Found", response = ErrorRequestObject.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 500, message = "Deleting membership of Neighborhood Failed.", response = ErrorRequestObject.class, responseContainer = "List") })
    public Response neighborhoodNhIdMemberMemberIdDelete(@ApiParam(value = "",required=true) @PathParam("nhId") Integer nhId
,@ApiParam(value = "",required=true) @PathParam("memberId") Integer memberId
,@Context SecurityContext securityContext, @HeaderParam("Authorization") String authBase64String)
    throws NotFoundException {
        return delegate.neighborhoodNhIdMemberMemberIdDelete(nhId,memberId,securityContext, authBase64String);
    }
    @POST
    @Path("/{nhId}/member")
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Add a member to neighborhood", notes = "", response = Member.class, responseContainer = "List", authorizations = {
        @io.swagger.annotations.Authorization(value = "bwAuth")
    }, tags={ "neighborhood", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "successful operation", response = Member.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "Invalid input. Negative or Zero UserId, NhId", response = ErrorRequestObject.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 401, message = "Invalid Authorization", response = ErrorRequestObject.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "Neighborhood Not found. User not Found", response = ErrorRequestObject.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 500, message = "Creating new membership to Neighborhood Failed", response = ErrorRequestObject.class, responseContainer = "List") })
    public Response neighborhoodNhIdMemberPost(@ApiParam(value = "",required=true) @PathParam("nhId") Integer nhId
,@ApiParam(value = "Member creation details" ,required=true) Member member
,@Context SecurityContext securityContext, @HeaderParam("Authorization") String authBase64String)
    throws NotFoundException {
        return delegate.neighborhoodNhIdMemberPost(nhId,member,securityContext, authBase64String);
    }
    
    
    @DELETE
    @Path("/{nhId}/relation")
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Delete all custom relations from neighborhood", notes = "", response = String.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "bwAuth")
    }, tags={ "neighborhood", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "Successful operation.", response = String.class),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "Invalid input. Negative NhId, Relation Null or Missing", response = ErrorRequestObject.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 401, message = "Invalid Authorization", response = ErrorRequestObject.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "Neighborhood Not found. Relation Not Found", response = ErrorRequestObject.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 500, message = "Failed to Delete Relation.", response = ErrorRequestObject.class, responseContainer = "List") })
    public Response neighborhoodNhIdRelationDelete(@ApiParam(value = "",required=true) @PathParam("nhId") Integer nhId
,@ApiParam(value = "",required=true) @QueryParam("relation") String relation
,@Context SecurityContext securityContext , @HeaderParam("Authorization") String authBase64String)
    throws NotFoundException {
        return delegate.neighborhoodNhIdRelationDelete(nhId,relation,securityContext, authBase64String);
    }
    
    
    @GET
    @Path("/{nhId}/relation")
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Get list of relations in a neighborhood", notes = "", response = Relation.class, responseContainer = "List", authorizations = {
        @io.swagger.annotations.Authorization(value = "bwAuth")
    }, tags={ "neighborhood", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "successful operation", response = Relation.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "Invalid input. Negative NhId", response = ErrorRequestObject.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 401, message = "Invalid Authorization", response = ErrorRequestObject.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "Neighborhood Id not found", response = ErrorRequestObject.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 500, message = "Failed to GET Neighborood Relation of Neighborhood", response = ErrorRequestObject.class, responseContainer = "List") })
    public Response neighborhoodNhIdRelationGet(@ApiParam(value = "",required=true) @PathParam("nhId") Integer nhId
,@Context SecurityContext securityContext, @HeaderParam("Authorization") String authBase64String)
    throws NotFoundException {
        return delegate.neighborhoodNhIdRelationGet(nhId,securityContext,authBase64String);
    }
    
    @POST
    @Path("/{nhId}/relation")
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Add a custom relation to neighborhood", notes = "", response = String.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "bwAuth")
    }, tags={ "neighborhood", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "successful operation", response = String.class),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "Invalid input. Missing relation Name, Negative nhId, Negative nhIds in related Neighborhoods. Invalid Neighborhoods in NhList.", response = ErrorRequestObject.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 401, message = "Invalid Authorization", response = ErrorRequestObject.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "Neighborhood Not found", response = ErrorRequestObject.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 409, message = "Creation of Relation failed", response = ErrorRequestObject.class, responseContainer = "List") })
    public Response neighborhoodNhIdRelationPost(@ApiParam(value = "",required=true) @PathParam("nhId") Integer nhId
,@ApiParam(value = "Relationship creation details" ,required=true) Relation relationship
,@Context SecurityContext securityContext, @HeaderParam("Authorization") String authBase64String)
    throws NotFoundException {
        return delegate.neighborhoodNhIdRelationPost(nhId,relationship,securityContext,authBase64String);
    }

    @GET
    @Path("/{nhId}/relation/{relation}/Neighborhood")
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Get Neighborhood for the specific path.", notes = "Returns the Neighborhood for the given path", response = Neighborhood.class, responseContainer = "List", authorizations = {
        @io.swagger.annotations.Authorization(value = "bwAuth")
    }, tags={ "neighborhood", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "successful operation. A Neighborhood object", response = Neighborhood.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "Invalid input (Null or missing nhPath, email)", response = ErrorRequestObject.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 401, message = "Invalid Authorization", response = ErrorRequestObject.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 403, message = "server understood the request but refuses to authorize it.", response = ErrorRequestObject.class, responseContainer = "List") })
    public Response neighborhoodNhIdRelationRelationNeighborhoodGet(@ApiParam(value = "",required=true) @PathParam("nhId") Integer nhId
,@ApiParam(value = "",required=true) @PathParam("relation") String relation
,@Context SecurityContext securityContext, @HeaderParam("Authorization") String authBase64String)
    throws NotFoundException {
        return delegate.neighborhoodNhIdRelationRelationNeighborhoodGet(nhId,relation,securityContext, authBase64String);
    }
    @POST
    
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Creates a new neighborhood", notes = "", response = Neighborhood.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "bwAuth")
    }, tags={ "neighborhood", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "successful operation", response = Neighborhood.class),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "Invalid input. missing nhName, Negative parentNhId.", response = ErrorRequestObject.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 401, message = "Invalid Authorization", response = ErrorRequestObject.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "Parent NeighborhoodId not found", response = ErrorRequestObject.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 409, message = "NhName must be Unique under a parent.", response = ErrorRequestObject.class, responseContainer = "List") })
    public Response neighborhoodPost(@ApiParam(value = "Neighborhood creation detail" ,required=true) Neighborhood neighborhood
,@Context SecurityContext securityContext, @HeaderParam("Authorization") String authBase64String)
    throws NotFoundException {
        return delegate.neighborhoodPost(neighborhood,securityContext, authBase64String);
    }
}
