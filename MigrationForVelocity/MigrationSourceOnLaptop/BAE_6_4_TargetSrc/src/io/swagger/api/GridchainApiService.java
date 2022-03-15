package io.swagger.api;

import io.swagger.api.*;
import io.swagger.model.*;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import io.swagger.model.CellBuffer;
import io.swagger.model.ErrorRequestObject;
import io.swagger.model.GridChain;
import io.swagger.model.GridTransaction;
import io.swagger.model.ResponseInfo;

import java.util.List;
import io.swagger.api.NotFoundException;

import java.io.InputStream;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.validation.constraints.*;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-11-26T08:53:47.129Z")
public abstract class GridchainApiService {
    public abstract Response gridchainGridIdGet(Integer gridId, @NotNull String viewPref, @NotNull String chainWindow, String activityPeriod, Long startDate, Long endDate, Long localTimeAfter111970, Integer startTxId, Integer endTxId, String filter,SecurityContext securityContext, String authBase64String) throws NotFoundException;
    public abstract Response gridchainGridIdPut(Integer gridId,CellBuffer cellBufferRequest,SecurityContext securityContext, String authBase64String) throws NotFoundException;
    public abstract Response gridchainGridIdTransactionsGet(Integer gridId, @NotNull String viewPref, @NotNull String reportType, String activityPeriod, Long startDate, Long endDate, Long localTimeAfter111970, Integer startTxId, Integer endTxId, Integer importTxId, SecurityContext securityContext, String authBase64String) throws NotFoundException;
}
