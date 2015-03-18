/*
 * Copyright [2015] [Eric Poitras]
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

package org.dbrain.yaw.http;

import org.dbrain.yaw.app.App;
import org.dbrain.yaw.app.Configuration;
import org.dbrain.yaw.http.server.defs.ConnectorDef;
import org.dbrain.yaw.http.server.defs.HttpServerDef;
import org.dbrain.yaw.system.jetty.JettyConnectors;
import org.dbrain.yaw.system.jetty.JettyServerBuilder;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Register a Jetty Http Server.
 *
 * Provider services:
 *   Server   : The jetty server.
 *
 */
public class JettyHttpServer extends AbstractHttpServer<JettyHttpServer> {

    private final App app;

    @Inject
    public JettyHttpServer( App app, Configuration config  ) {
        super( config );
        this.app = app;
    }

    @Override
    protected JettyHttpServer self() {
        return this;
    }

    protected Server build( HttpServerDef def ) {

        // Build the server first.
        Server server = new Server();

        // Configure the connectors
        for ( ConnectorDef connector : def.getEndPoints() ) {
            JettyConnectors.configureConnector( server, connector );
        }

        // Configure the servlet contexts
        Handler handler = new JettyServerBuilder( app ).configureServletContextsHandler( server, def.getServletContexts() );
        if ( handler != null ) {
            server.setHandler( handler );
        }

        return server;
    }


    @Override
    public void complete() {
        getConfig().bind( Server.class )
                   .providedBy( build( getHttpServerConfig() ) )
                   .disposedBy( (server) -> server.stop() )
                   .qualifiedBy( getQualifiers() )
                   .to( Server.class )
                   .in( Singleton.class )
                   .complete();

    }

}
