package io.swagger.api;

import java.util.ArrayList;

import javax.json.stream.JsonParsingException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import io.swagger.model.ErrorRequestObject;

@Provider
public class JsonParseExceptionMapper implements ExceptionMapper<JsonParsingException>{

    @Override
    public Response toResponse(JsonParsingException exception)
    {
 	 	ArrayList <ErrorRequestObject> ErrResps = new ArrayList<ErrorRequestObject>();
        ErrorRequestObject erb;
		erb = new ErrorRequestObject();
		erb.setError("This is an invalid JSON. The request can not be parsed.");
		erb.setPath(exception.getMessage());
		erb.setProposedSolution("Verify the JSON String at Location:" + exception.getLocation());
		ErrResps.add(erb);
    	
    	return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(ErrResps)
                .type( MediaType.APPLICATION_JSON)
                .build();
        
    }
}
