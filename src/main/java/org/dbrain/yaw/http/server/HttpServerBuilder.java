package org.dbrain.yaw.http.server;

import org.dbrain.yaw.http.server.config.HttpEndpointConfig;
import org.dbrain.yaw.http.server.config.EndpointConfig;
import org.dbrain.yaw.http.server.config.HttpServerConfig;
import org.dbrain.yaw.http.server.config.ServletContextConfig;

public class HttpServerBuilder {

    private HttpServerConfig building = new HttpServerConfig();

    public HttpServerBuilder() {
    }

    public HttpServerBuilder add( EndpointConfig config ) {
        if ( config != null ) {
            building.getEndPoints().add( config );
        }
        return this;
    }

    public HttpServerBuilder add( ServletContextConfig servletContext ) {
        if ( servletContext != null ) {
            building.getServletContexts().add( servletContext );
        }
        return this;
    }

    public HttpServerConfig buildConfig() {
        if ( building.getEndPoints().size() == 0 ) {
            building.getEndPoints().add( new HttpEndpointConfig() );
        }


        return building;
    }

    public <T> T build( HttpServerFactory<T> factory ) {
        return factory.build( buildConfig() );
    }





}
