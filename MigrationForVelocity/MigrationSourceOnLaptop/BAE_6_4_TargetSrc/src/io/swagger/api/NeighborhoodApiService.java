package io.swagger.api;

import io.swagger.model.Member;
import io.swagger.model.Neighborhood;
import io.swagger.model.Relation;
import io.swagger.api.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.validation.constraints.*;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-10-03T07:16:22.724Z")
public abstract class NeighborhoodApiService {
    public abstract Response neighborhoodGet( String neighborhoodSpec,SecurityContext securityContext, String authBase64String) throws NotFoundException;
    public abstract Response neighborhoodNhIdCollaborationGet(Integer nhId,SecurityContext securityContext, String authBase64String) throws NotFoundException;
    public abstract Response neighborhoodNhIdDelete(Integer nhId,SecurityContext securityContext, String authBase64String) throws NotFoundException;
    public abstract Response neighborhoodNhIdGet(Integer nhId,SecurityContext securityContext, String authBase64String) throws NotFoundException;
    public abstract Response neighborhoodNhIdMemberGet(Integer nhId,SecurityContext securityContext, String authBase64String) throws NotFoundException;
    public abstract Response neighborhoodNhIdMemberMemberIdDelete(Integer nhId,Integer memberId,SecurityContext securityContext, String authBase64String) throws NotFoundException;
    public abstract Response neighborhoodNhIdMemberPost(Integer nhId,Member member,SecurityContext securityContext, String authBase64String) throws NotFoundException;
    public abstract Response neighborhoodNhIdRelationDelete(Integer nhId, @NotNull String relation, SecurityContext securityContext, String authBase64String) throws NotFoundException;
    public abstract Response neighborhoodNhIdRelationGet(Integer nhId,SecurityContext securityContext, String authBase64String) throws NotFoundException;
    public abstract Response neighborhoodNhIdRelationPost(Integer nhId,Relation relationship,SecurityContext securityContext, String authBase64String) throws NotFoundException;
    public abstract Response neighborhoodNhIdRelationRelationNeighborhoodGet( Integer nhId,String relation,SecurityContext securityContext, String authBase64String) throws NotFoundException;
    public abstract Response neighborhoodPost(Neighborhood neighborhood,SecurityContext securityContext, String authBase64String) throws NotFoundException;
}
