package io.swagger.api;


import io.swagger.model.CellBuffer;
import io.swagger.model.Grid;
import io.swagger.api.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.validation.constraints.*;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-10-03T07:16:22.724Z")
public abstract class GridApiService {
    public abstract Response gridDelete(Integer gridId,SecurityContext securityContext, String authBase64String) throws NotFoundException;
    public abstract Response gridGridIdColumnsGet(Integer gridId,SecurityContext securityContext, String authBase64String) throws NotFoundException;
    public abstract Response gridGridIdGet(Integer gridId, @NotNull Integer importTxId, @NotNull String view, @NotNull Integer mode, @NotNull Integer baselineId, SecurityContext securityContext, String authBase64String) throws NotFoundException;
    public abstract Response gridPost(Grid grid,SecurityContext securityContext, String authBase64String) throws NotFoundException;
    public abstract Response gridPut( @NotNull Integer gridId,CellBuffer cellBufferRequest,SecurityContext securityContext, String authBase64String) throws NotFoundException;
}
