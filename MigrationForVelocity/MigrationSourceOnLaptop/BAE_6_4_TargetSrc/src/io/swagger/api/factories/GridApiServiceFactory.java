package io.swagger.api.factories;

import io.swagger.api.GridApiService;
import io.swagger.api.impl.GridApiServiceImpl;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2018-05-30T16:47:27.652Z")
public class GridApiServiceFactory {
    private final static GridApiService service = new GridApiServiceImpl();

    public static GridApiService getGridApi() {
        return service;
    }
}
