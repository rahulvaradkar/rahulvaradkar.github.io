package io.swagger.api;

import io.swagger.model.*;
import io.swagger.api.GridchainApiService;
import io.swagger.api.factories.GridchainApiServiceFactory;
import io.swagger.annotations.ApiParam;
import io.swagger.jaxrs.*;

import io.swagger.model.CellBuffer;
import io.swagger.model.ErrorRequestObject;
import io.swagger.model.GridChain;
import io.swagger.model.GridTransaction;
import io.swagger.model.ResponseInfo;

import java.util.Map;
import java.util.List;
import io.swagger.api.NotFoundException;

import java.io.InputStream;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.servlet.ServletConfig;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.*;
import javax.validation.constraints.*;

@Path("/gridchain")

@Produces({ "application/json" })
@io.swagger.annotations.Api(description = "the gridchain API")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2019-04-03T06:43:09.693Z")
public class GridchainApi  {
   private final GridchainApiService delegate;

   public GridchainApi(@Context ServletConfig servletContext) {
      GridchainApiService delegate = null;

      if (servletContext != null) {
         String implClass = servletContext.getInitParameter("GridchainApi.implementation");
         if (implClass != null && !"".equals(implClass.trim())) {
            try {
               delegate = (GridchainApiService) Class.forName(implClass).newInstance();
            } catch (Exception e) {
               throw new RuntimeException(e);
            }
         } 
      }

      if (delegate == null) {
         delegate = GridchainApiServiceFactory.getGridchainApi();
      }

      this.delegate = delegate;
   }

    @GET
    @Path("/{gridId}")
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Get a section or the full grid based on specification", notes = "User can GET all Grid Component, it's status, History, Transactions done on entire grid. Information of the Grid present in the Whiteboard of the Collaboration using his/her memberships details for that Neighborhood. So {email} and user in Authrization must match. Also {nhPath} and nhPath in Authorization should match. The Grid information will have Column Names, sequence Number, Active/inactive & Access Control ( R/W ), Column Count, Row Count, Access Control for Add Row, Delete Row, Insert Column, Delete Column, Edit Data, Cuboid Properties. If user is the Owner then Accesss Control Cuboid information.", response = GridChain.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "bwAuth")
    }, tags={ "gridchain", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "successful operation", response = GridChain.class),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "Invalid input. Negative gridId, Null importTxId, view, baseline, mode, mode != 1 or 0", response = ErrorRequestObject.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 401, message = "Invalid Authorization", response = ErrorRequestObject.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 403, message = "Forbidden. User don't have the privileges to execute this action.", response = ErrorRequestObject.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "GridId Not Found. Provide an existing Grid Id.", response = ErrorRequestObject.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 500, message = "Server error - getTableBuffer or getTableInfo or getCriteriaTable | StartTransaction | BW_IMPORT_CHANGES | getXlColumnsForImport | RowManager.getTableRows", response = ErrorRequestObject.class, responseContainer = "List") })
    public Response gridchainGridIdGet(@ApiParam(value = "",required=true) @PathParam("gridId") Integer gridId
,@ApiParam(value = "",required=true) @QueryParam("viewPref") String viewPref
,@ApiParam(value = "",required=true) @QueryParam("chainWindow") String chainWindow
,@ApiParam(value = "") @QueryParam("activityPeriod") String activityPeriod
,@ApiParam(value = "") @QueryParam("startDate") Long startDate
,@ApiParam(value = "") @QueryParam("endDate") Long endDate
,@ApiParam(value = "") @QueryParam("localTimeAfter_1_11970") Long localTimeAfter111970
,@ApiParam(value = "") @QueryParam("startTxId") Integer startTxId
,@ApiParam(value = "") @QueryParam("endTxId") Integer endTxId
,@ApiParam(value = "") @QueryParam("filter") String filter
,@Context SecurityContext securityContext, @HeaderParam("Authorization") String authBase64String )
    throws NotFoundException {
        return delegate.gridchainGridIdGet(gridId,viewPref,chainWindow,activityPeriod,startDate,endDate,localTimeAfter111970,startTxId,endTxId,filter,securityContext,authBase64String);
    }

    @PUT
    @Path("/{gridId}")
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Submit Changes in GridChain. Also Returns GET GridChain BETWEENTX in response.", notes = "User submit Changes to Server as CellBuffer along with the ImportTxId. User Changes are saved to Database on Server and current ExportTxId is generated. In response GridChain is returned that has all Grid changes done between ImportTxId and current ExportTxId. The GridChain includes all changed cells, it's status, History, Transactions done on entire grid between the ImportTxId and the Current ExportTxId.", response = GridChain.class, authorizations = {
        @io.swagger.annotations.Authorization(value = "bwAuth")
    }, tags={ "gridchain", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "successful operation", response = GridChain.class),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "Invalid input. Negative gridId, Null importTxId, view, baseline, mode, mode != 1 or 0", response = ErrorRequestObject.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 401, message = "Invalid Authorization", response = ErrorRequestObject.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 403, message = "Forbidden. User don't have the privileges to execute this action.", response = ErrorRequestObject.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 500, message = "Server error - getTableBuffer or getTableInfo or getCriteriaTable | StartTransaction | BW_IMPORT_CHANGES | getXlColumnsForImport | RowManager.getTableRows", response = ErrorRequestObject.class, responseContainer = "List") })
    public Response gridchainGridIdPut(@ApiParam(value = "",required=true) @PathParam("gridId") Integer gridId
,@ApiParam(value = "Cell buffer details" ,required=true) CellBuffer cellBufferRequest
,@Context SecurityContext securityContext, @HeaderParam("Authorization") String authBase64String )
    throws NotFoundException {
        return delegate.gridchainGridIdPut(gridId,cellBufferRequest,securityContext,authBase64String);
    }

    @GET
    @Path("/{gridId}/transactions")
    
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Get cuboid transactions for time interval for a given specification", notes = "", response = GridTransaction.class, responseContainer = "List", authorizations = {
        @io.swagger.annotations.Authorization(value = "bwAuth")
    }, tags={ "gridchain", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "successful operation", response = GridTransaction.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "Invalid input. Negative GridId. Missing/Negative localTimeAfter111970. Invalid Start and End Dates. Invalid Activity Period. Missing importTxId. Missing/invalid reportType", response = ResponseInfo.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 401, message = "Invalid Authorization", response = ResponseInfo.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "GridId not found", response = ResponseInfo.class, responseContainer = "List"),
        
        @io.swagger.annotations.ApiResponse(code = 500, message = "SQLException in TableManager.getTransactionList - OR - TableManager.getTransactionListAfterImport - OR - SystemException in TableManager.getTableInfo", response = ResponseInfo.class, responseContainer = "List") })
    public Response gridchainGridIdTransactionsGet(@ApiParam(value = "",required=true) @PathParam("gridId") Integer gridId
,@ApiParam(value = "",required=true) @QueryParam("viewPref") String viewPref
,@ApiParam(value = "",required=true) @QueryParam("reportType") String reportType
,@ApiParam(value = "") @QueryParam("activityPeriod") String activityPeriod
,@ApiParam(value = "") @QueryParam("startDate") Long startDate
,@ApiParam(value = "") @QueryParam("endDate") Long endDate
,@ApiParam(value = "") @QueryParam("localTimeAfter_1_11970") Long localTimeAfter111970
,@ApiParam(value = "") @QueryParam("startTxId") Integer startTxId
,@ApiParam(value = "") @QueryParam("endTxId") Integer endTxId
,@ApiParam(value = "") @QueryParam("importTxId") Integer importTxId
,@Context SecurityContext securityContext, @HeaderParam("Authorization") String authBase64String )
    throws NotFoundException {
        return delegate.gridchainGridIdTransactionsGet(gridId,viewPref,reportType,activityPeriod,startDate,endDate,localTimeAfter111970,startTxId,endTxId, importTxId, securityContext,authBase64String);
    }
}
