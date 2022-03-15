package io.swagger.api;

import io.swagger.api.UserApiService;
import io.swagger.api.factories.UserApiServiceFactory;
import io.swagger.annotations.ApiParam;
import io.swagger.model.ErrorRequestObject;
import io.swagger.model.Membership;
import io.swagger.model.ResponseInfo;
import io.swagger.model.User;
import io.swagger.api.NotFoundException;
import javax.servlet.ServletConfig;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.*;

@Path("/user")

@Produces({ "application/json" })
@io.swagger.annotations.Api(description = "the user API")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-10-03T07:16:22.724Z")
public class UserApi  {
   private final UserApiService delegate;

   public UserApi(@Context ServletConfig servletContext) {
      UserApiService delegate = null;

      if (servletContext != null) {
         String implClass = servletContext.getInitParameter("UserApi.implementation");
         if (implClass != null && !"".equals(implClass.trim())) {
            try {
               delegate = (UserApiService) Class.forName(implClass).newInstance();
            } catch (Exception e) {
               throw new RuntimeException(e);
            }
         } 
      }

      if (delegate == null) {
         delegate = UserApiServiceFactory.getUserApi();
      }

      this.delegate = delegate;
   }

    @GET
    
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Get all users in the system", notes = "Gets all users in the system, can be filtered by active/inactive users", response = User.class, responseContainer = "List", authorizations = {
        @io.swagger.annotations.Authorization(value = "bwAuth")
    }, tags={ "user", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "successful operation. Existing userlist with matching activeflag", response = User.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 401, message = "Invalid Authorization", response = ErrorRequestObject.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 500, message = "Error fetching User List", response = ErrorRequestObject.class, responseContainer = "List") })
    public Response userGet(@ApiParam(value = "") @QueryParam("active") Boolean active
,@Context SecurityContext securityContext, @HeaderParam("Authorization") String authBase64String)
    throws NotFoundException {
        return delegate.userGet(active,securityContext,authBase64String);
    }
    @GET
    @Path("/memberships")
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Get all user memberships in the system", notes = "Gets all user memberships in the system. User can Get only his/her memberships details. So {email} and user in Authrization must match.", response = Membership.class, responseContainer = "List", authorizations = {
        @io.swagger.annotations.Authorization(value = "bwAuth")
    }, tags={ "user", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "successful operation. Returns user memberships", response = Membership.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "Invalid input. Null or missing email", response = ResponseInfo.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 401, message = "Invalid Authorization", response = ResponseInfo.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 403, message = "server understood the request but refuses to authorize it.", response = ResponseInfo.class, responseContainer = "List") })
    public Response userMembershipsGet(@ApiParam(value = "",required=true) @PathParam("email") String email
,@Context SecurityContext securityContext, @HeaderParam("Authorization") String authBase64String)
    throws NotFoundException {
        return delegate.userMembershipsGet(email,securityContext, authBase64String);
    }
    @POST
    
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Create new user", notes = "", response = User.class, responseContainer = "List", authorizations = {
        @io.swagger.annotations.Authorization(value = "bwAuth")
    }, tags={ "user", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "successful operation", response = User.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "Invalid input", response = ErrorRequestObject.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 401, message = "Invalid Authorization", response = ErrorRequestObject.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 409, message = "Failed to update user profile for UserId. Reason could be trying to create Duplicate entities", response = ErrorRequestObject.class, responseContainer = "List") })
    public Response userPost(@ApiParam(value = "User creation details" ,required=true) User user
,@Context SecurityContext securityContext, @HeaderParam("Authorization") String authBase64String)
    throws NotFoundException {
        return delegate.userPost(user,securityContext,authBase64String);
    }
    @PUT
    
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Update user profile", notes = "", response = String.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "bwAuth")
    }, tags={ "user", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "successful operation", response = String.class),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "Invalid input", response = ErrorRequestObject.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 401, message = "Invalid Authorization", response = ErrorRequestObject.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 409, message = "Failed to update user profile for UserId. Reason could be trying to create Duplicate entities", response = ErrorRequestObject.class, responseContainer = "List") })
    public Response userPut(@ApiParam(value = "User creation details" ,required=true) User user
,@Context SecurityContext securityContext, @HeaderParam("Authorization") String authBase64String)
    throws NotFoundException {
        return delegate.userPut(user,securityContext, authBase64String);
    }
    @DELETE
    @Path("/{userId}")
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "De-activate user by ID", notes = "", response = String.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "bwAuth")
    }, tags={ "user", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "successful operation", response = String.class),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "Invalid input", response = ErrorRequestObject.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 401, message = "Invalid Authorization", response = ErrorRequestObject.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "User not found", response = ErrorRequestObject.class, responseContainer = "List") })
    public Response userUserIdDelete(@ApiParam(value = "",required=true) @PathParam("userId") Integer userId
,@Context SecurityContext securityContext, @HeaderParam("Authorization") String authBase64String)
    throws NotFoundException {
        return delegate.userUserIdDelete(userId,securityContext, authBase64String);
    }
    @GET
    @Path("/{userId}")
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Get a specific user profile from the systems", notes = "", response = User.class, responseContainer = "List", authorizations = {
        @io.swagger.annotations.Authorization(value = "bwAuth")
    }, tags={ "user", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "successful operation", response = User.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "Invalid input", response = ErrorRequestObject.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 401, message = "Invalid Authorization", response = ErrorRequestObject.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "User not found", response = ErrorRequestObject.class, responseContainer = "List") })
    public Response userUserIdGet(@ApiParam(value = "",required=true) @PathParam("userId") Integer userId
,@Context SecurityContext securityContext, @HeaderParam("Authorization") String authBase64String)
    throws NotFoundException {
        return delegate.userUserIdGet(userId,securityContext, authBase64String);
    }
}
