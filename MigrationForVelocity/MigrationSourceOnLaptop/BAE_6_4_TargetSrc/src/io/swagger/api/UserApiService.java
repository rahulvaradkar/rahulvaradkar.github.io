package io.swagger.api;

import io.swagger.model.User;
import io.swagger.api.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-10-03T07:16:22.724Z")
public abstract class UserApiService {
    public abstract Response userGet( Boolean active,SecurityContext securityContext, String authBase64String) throws NotFoundException;
    public abstract Response userMembershipsGet(String email,SecurityContext securityContext, String authBase64String) throws NotFoundException;
    public abstract Response userPost(User user,SecurityContext securityContext, String authBase64String) throws NotFoundException;
    public abstract Response userPut(User user,SecurityContext securityContext, String authBase64String) throws NotFoundException;
    public abstract Response userUserIdDelete(Integer userId,SecurityContext securityContext, String authBase64String) throws NotFoundException;
    public abstract Response userUserIdGet(Integer userId,SecurityContext securityContext, String authBase64String) throws NotFoundException;
}
