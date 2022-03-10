package io.swagger.api.factories;

import io.swagger.api.GridchainApiService;
import io.swagger.api.impl.GridchainApiServiceImpl;


public class GridchainApiServiceFactory {
    private final static GridchainApiService service = new GridchainApiServiceImpl();

    public static GridchainApiService getGridchainApi() {
        return service;
    }
}
