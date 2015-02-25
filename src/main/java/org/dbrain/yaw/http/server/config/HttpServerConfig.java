package org.dbrain.yaw.http.server.config;

import java.util.ArrayList;
import java.util.List;

/**
 * Definition of an Http Server.
 */
public class HttpServerConfig {

    private List<EndpointConfig> endPoints = new ArrayList<>( 1 );

    private List<ServletContextConfig> servletContexts = new ArrayList<>( 1 );

    public List<EndpointConfig> getEndPoints() {
        return endPoints;
    }

    public void setEndPoints( List<EndpointConfig> endPoints ) {
        this.endPoints = endPoints;
    }

    public List<ServletContextConfig> getServletContexts() {
        return servletContexts;
    }

    public void setServletContexts( List<ServletContextConfig> servletContexts ) {
        this.servletContexts = servletContexts;
    }
}
