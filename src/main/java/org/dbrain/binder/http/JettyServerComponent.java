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

package org.dbrain.binder.http;

import org.dbrain.binder.app.App;
import org.dbrain.binder.app.Binder;
import org.dbrain.binder.http.conf.ConnectorConf;
import org.dbrain.binder.http.conf.HttpServerConf;
import org.dbrain.binder.system.jetty.JettyConnectors;
import org.dbrain.binder.system.jetty.JettyServerBuilder;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Register a Jetty Http Server.
 *
 * Provided services:
 * Server   : The jetty server.
 */
public class JettyServerComponent extends AbstractHttpServerComponent<JettyServerComponent> {

    private final App app;

    @Inject
    public JettyServerComponent( App app, Binder.BindingContext cc ) {
        this.app = app;
        cc.onBind( ( binder ) -> {
            binder.bindService( Server.class )
                  .providedBy( build( getHttpServerConfig() ) )
                  .disposedBy( ( server ) -> server.stop() )
                  .qualifiedBy( buildQualifiers() )
                  .to( Server.class )
                  .in( Singleton.class );
        } );
    }

    @Override
    protected JettyServerComponent self() {
        return this;
    }

    protected Server build( HttpServerConf def ) {

        // Build the server first.
        Server server = new Server();

        // Configure the connectors
        for ( ConnectorConf connector : def.getEndPoints() ) {
            JettyConnectors.configureConnector( server, connector );
        }

        // Configure the servlet contexts
        Handler handler = new JettyServerBuilder( app ).configureServletContextsHandler( server,
                                                                                         def.getServletContexts() );
        if ( handler != null ) {
            server.setHandler( handler );
        }

        return server;
    }

}
