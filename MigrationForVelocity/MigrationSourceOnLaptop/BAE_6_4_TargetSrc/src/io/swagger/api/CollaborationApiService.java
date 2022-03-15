package io.swagger.api;

import io.swagger.model.Collaboration;
import io.swagger.model.Whiteboard;
import io.swagger.api.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-10-03T07:16:22.724Z")
public abstract class CollaborationApiService {
    public abstract Response collaborationCollabIdDelete(Integer collabId,SecurityContext securityContext, String authBase64String) throws NotFoundException;
    public abstract Response collaborationCollabIdGridsGet(Integer collabId,SecurityContext securityContext, String authBase64String) throws NotFoundException;
   	public abstract Response collaborationCollabIdWhiteboardGet(Integer collabId,SecurityContext securityContext, String authBase64String) throws NotFoundException;
    public abstract Response collaborationCollabIdWhiteboardPost(Integer collabId,Whiteboard wb,SecurityContext securityContext, String authBase64String) throws NotFoundException;
    public abstract Response collaborationCollabIdWhiteboardWhiteboardIdDelete(Integer collabId,Integer whiteboardId,SecurityContext securityContext, String authBase64String) throws NotFoundException;
    public abstract Response collaborationCollabIdWhiteboardWhiteboardIdGridsGet(Integer collabId,Integer whiteboardId,SecurityContext securityContext, String authBase64String) throws NotFoundException;
    public abstract Response collaborationPost(Collaboration collab,SecurityContext securityContext, String authBase64String) throws NotFoundException;
}
