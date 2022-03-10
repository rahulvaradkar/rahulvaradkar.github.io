package io.swagger.api;

import java.util.ArrayList;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.JsonMappingException;

import io.swagger.model.ErrorRequestObject;

@Provider
public class JsonMappingExceptionMapper implements ExceptionMapper<JsonMappingException >{

    @Override
    public Response toResponse(JsonMappingException exception)
    {
 	 	ArrayList <ErrorRequestObject> ErrResps = new ArrayList<ErrorRequestObject>();
        ErrorRequestObject erb;
		erb = new ErrorRequestObject();
		erb.setError("This is an invalid request. At least one field format is not readable by the system.");
		erb.setPath(exception.getPath().toString());
		erb.setProposedSolution("Verify the JSON String at Location:" + exception.getLocation() + "Message : " + exception.getMessage() + " PathReference : " + exception.getPathReference() + " Original Messaqge : " + exception.getOriginalMessage());
		ErrResps.add(erb);
    	
    	return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(ErrResps)
                .build();
    	
    	
/*        
  		return Response
                .status(Response.Status.BAD_REQUEST)
                .entity("This is an invalid request. At least one field format is not readable by the system.")
                .type( MediaType.APPLICATION_JSON)
                .build(); 
*/    
    }
    
}