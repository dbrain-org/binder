package org.dbrain.yaw.http.server;

import org.dbrain.yaw.http.server.config.HttpServerConfig;

/**
 * Created by epoitras on 17/09/14.
 */
public interface HttpServerFactory<T> {

    public T build( HttpServerConfig def );

}
