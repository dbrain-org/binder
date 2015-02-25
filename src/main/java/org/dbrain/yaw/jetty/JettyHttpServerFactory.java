package org.dbrain.yaw.jetty;

import org.dbrain.yaw.http.server.config.EndpointConfig;
import org.dbrain.yaw.http.server.HttpServerFactory;
import org.dbrain.yaw.http.server.config.HttpServerConfig;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;

/**
 * Created by epoitras on 17/09/14.
 */
public class JettyHttpServerFactory implements HttpServerFactory<Server> {

    @Override
    public Server build( HttpServerConfig def ) {

        // Build the server first.
        Server server = new Server();

        // Configure the connectors
        for ( EndpointConfig connector: def.getEndPoints() ) {
            JettyConnectors.configureConnector( server, connector );
        }

        // Configure the servlet contexts
        Handler handler = JettyUtils.configureServletContextsHandler( server, def.getServletContexts() );
        if ( handler != null ) {
            server.setHandler( handler );
        }

        return server;
    }

}
