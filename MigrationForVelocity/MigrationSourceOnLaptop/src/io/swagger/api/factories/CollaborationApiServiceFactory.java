package io.swagger.api.factories;

import io.swagger.api.CollaborationApiService;
import io.swagger.api.impl.CollaborationApiServiceImpl;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-05-10T09:36:40.808Z")
public class CollaborationApiServiceFactory {
    private final static CollaborationApiService service = new CollaborationApiServiceImpl();

    public static CollaborationApiService getCollaborationApi() {
        return service;
    }
}
