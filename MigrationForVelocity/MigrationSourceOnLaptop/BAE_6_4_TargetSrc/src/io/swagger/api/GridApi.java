package io.swagger.api;

import io.swagger.api.GridApiService;
import io.swagger.api.factories.GridApiServiceFactory;
import io.swagger.annotations.ApiParam;
import io.swagger.model.CellBuffer;
import io.swagger.model.ColumnChain;
import io.swagger.model.ErrorRequestObject;
import io.swagger.model.Grid;
import io.swagger.api.NotFoundException;
import javax.servlet.ServletConfig;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.*;

@Path("/grid")

@Produces({ "application/json" })
@io.swagger.annotations.Api(description = "the grid API")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2019-04-03T06:43:09.693Z")
public class GridApi  {
   private final GridApiService delegate;

   public GridApi(@Context ServletConfig servletContext) {
      GridApiService delegate = null;

      if (servletContext != null) {
         String implClass = servletContext.getInitParameter("GridApi.implementation");
         if (implClass != null && !"".equals(implClass.trim())) {
            try {
               delegate = (GridApiService) Class.forName(implClass).newInstance();
            } catch (Exception e) {
               throw new RuntimeException(e);
            }
         } 
      }

      if (delegate == null) {
         delegate = GridApiServiceFactory.getGridApi();
      }

      this.delegate = delegate;
   }

    @DELETE
    
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Delete cuboid by ID", notes = "", response = Void.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "bwAuth")
    }, tags={ "grid", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 104, message = "No Permissions to delete this object", response = ErrorRequestObject.class),
        
        @io.swagger.annotations.ApiResponse(code = 200, message = "successful operation", response = Void.class),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "Cuboid not found", response = Void.class) })
    public Response gridDelete(@ApiParam(value = "",required=true) @PathParam("gridId") Integer gridId
,@Context SecurityContext securityContext, @HeaderParam("Authorization") String authBase64String)
    throws NotFoundException {
        return delegate.gridDelete(gridId,securityContext,authBase64String);
    }
    @GET
    @Path("/{gridId}/columns")
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Download grid structure", notes = "", response = ColumnChain.class, responseContainer = "List", authorizations = {
        @io.swagger.annotations.Authorization(value = "bwAuth")
    }, tags={ "grid", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "successful operation", response = ColumnChain.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "Invalid input. Negative gridId, Null importTxId, view, baseline, mode, mode != 1 or 0", response = ErrorRequestObject.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 401, message = "Invalid Authorization", response = ErrorRequestObject.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 403, message = "Forbidden. User don't have the privileges to execute this action.", response = ErrorRequestObject.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 500, message = "Server error - getTableBuffer or getTableInfo or getCriteriaTable | StartTransaction | BW_IMPORT_CHANGES | getXlColumnsForImport | RowManager.getTableRows", response = ErrorRequestObject.class, responseContainer = "List") })
    public Response gridGridIdColumnsGet(@ApiParam(value = "",required=true) @PathParam("gridId") Integer gridId
,@Context SecurityContext securityContext, @HeaderParam("Authorization") String authBase64String)
    throws NotFoundException {
        return delegate.gridGridIdColumnsGet(gridId,securityContext,authBase64String);
    }
    @GET
    @Path("/{gridId}")
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Download latest grid data for a given cell specification", notes = "", response = CellBuffer.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "bwAuth")
    }, tags={ "grid", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "successful operation", response = CellBuffer.class),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "Invalid input. Negative gridId, Null importTxId, view, baseline, mode, mode != 1 or 0", response = ErrorRequestObject.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 401, message = "Invalid Authorization", response = ErrorRequestObject.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 403, message = "Forbidden. User don't have the privileges to execute this action.", response = ErrorRequestObject.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "GridId Not Found. Provide an existing Grid Id.", response = ErrorRequestObject.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 500, message = "Server error - getTableBuffer or getTableInfo or getCriteriaTable | StartTransaction | BW_IMPORT_CHANGES | getXlColumnsForImport | RowManager.getTableRows", response = ErrorRequestObject.class, responseContainer = "List") })
    public Response gridGridIdGet(@ApiParam(value = "",required=true) @PathParam("gridId") Integer gridId
,@ApiParam(value = "",required=true) @QueryParam("importTxId") Integer importTxId
,@ApiParam(value = "",required=true) @QueryParam("view") String view
,@ApiParam(value = "",required=true) @QueryParam("mode") Integer mode
,@ApiParam(value = "",required=true) @QueryParam("baselineId") Integer baselineId
,@Context SecurityContext securityContext, @HeaderParam("Authorization") String authBase64String)
    throws NotFoundException {
        return delegate.gridGridIdGet(gridId,importTxId,view,mode,baselineId,securityContext,authBase64String);
    }
    @POST
    
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Create a new cuboid. returns a new grid id", notes = "", response = Grid.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "bwAuth")
    }, tags={ "grid", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "successful operation, returns grid with an ID", response = Grid.class),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "Invalid input. Bad Request Negative or null collabId, wbId. Blank or null gridDesc, gridName.", response = ErrorRequestObject.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 401, message = "Invalid Authorization", response = ErrorRequestObject.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "Collaboration Id not found. Whiteboard Id not found.", response = ErrorRequestObject.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 409, message = "Grid already exists in the Whiteboard.", response = ErrorRequestObject.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 500, message = "Server error. Failed to get Neighborhood Relationships.", response = ErrorRequestObject.class, responseContainer = "List") })
    public Response gridPost(@ApiParam(value = "Cuboid creation details" ,required=true) Grid grid
,@Context SecurityContext securityContext, @HeaderParam("Authorization") String authBase64String)
    throws NotFoundException {
        return delegate.gridPost(grid,securityContext,authBase64String);
    }
    @PUT
    
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Update a grid", notes = "", response = CellBuffer.class, responseContainer = "List", authorizations = {
        @io.swagger.annotations.Authorization(value = "bwAuth")
    }, tags={ "grid", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "successful operation", response = CellBuffer.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "Invalid input. Too many errors in payload", response = ErrorRequestObject.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 401, message = "Invalid Authorization", response = ErrorRequestObject.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 403, message = "User don't have the privileges to execute this action. Add/Delete Row | Administer Columns", response = ErrorRequestObject.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "Missing elements info | cells | rowArray | columnArray | rows | columns | columnCellArrays | GridChangeBuffer - OR -  Blank Column Name - OR - Missing elements info | cells | rowArray | columnArray | rows | columns | columnCellArrays | GridChangeBuffer  - OR - Membership is Not Valid (in validateMembership)", response = ErrorRequestObject.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 409, message = "Columns are not Unique - OR - Critical updates on Server - OR- Column already exists in the Grid", response = ErrorRequestObject.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 423, message = "The resource that is being accessed is locked. The table is being updated by another user, Please try later.", response = ErrorRequestObject.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 500, message = "NhName must be Unique under a parent.", response = ErrorRequestObject.class, responseContainer = "List") })
    public Response gridPut(@ApiParam(value = "",required=true) @QueryParam("gridId") Integer gridId
,@ApiParam(value = "Cell buffer details" ,required=true) CellBuffer cellBufferRequest
,@Context SecurityContext securityContext, @HeaderParam("Authorization") String authBase64String)
    throws NotFoundException {
        return delegate.gridPut(gridId,cellBufferRequest,securityContext,authBase64String);
    }
}
