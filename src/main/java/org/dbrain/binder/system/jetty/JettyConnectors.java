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

package org.dbrain.binder.system.jetty;

import org.dbrain.binder.http.conf.ConnectorConf;
import org.dbrain.binder.http.conf.HttpConnectorConf;
import org.dbrain.binder.http.conf.HttpsConnectorConf;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.util.resource.FileResource;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.ssl.SslContextFactory;

/**
 * Created by epoitras on 17/09/14.
 */
public class JettyConnectors {

    private static HttpConfiguration getStandardConfiguration( HttpConnectorConf config ) {
        HttpConfiguration httpConfiguration = new HttpConfiguration();
        httpConfiguration.setSendServerVersion( false );
        httpConfiguration.setSendXPoweredBy( false );
        httpConfiguration.setSendDateHeader( false );

        return httpConfiguration;

    }


    private static void configureCommonFields( ServerConnector connector, HttpConnectorConf config ) {
        if ( config.getHost() != null ) {
            connector.setHost( config.getHost() );
        }
    }

    public static void configureConnector( Server server, HttpConnectorConf config ) {

        // Build Http Configuration
        HttpConfiguration httpConfiguration = getStandardConfiguration( config );

        // Build http connection factory
        HttpConnectionFactory httpConnectionFactory = new HttpConnectionFactory( httpConfiguration );

        // Build server connector
        ServerConnector connector = new ServerConnector( server, httpConnectionFactory );
        configureCommonFields( connector, config );
        connector.setPort( config.getPort() );
        server.addConnector( connector );

    }

    public static void configureConnector( Server server, HttpsConnectorConf config ) {

        Resource keyStoreResource = new FileResource( config.getKeyStore() );

        // Get http configuration
        HttpConfiguration httpConfiguration = getStandardConfiguration( config );

        // SSL Connection factory
        SslContextFactory sslContextFactory = new SslContextFactory();
        sslContextFactory.setKeyStoreResource( keyStoreResource );
        sslContextFactory.setKeyStorePassword( config.getKeyStorePassword() );
        sslContextFactory.setKeyManagerPassword( config.getKeyManagerPassord() );
        SslConnectionFactory sslConnectionFactory = new SslConnectionFactory( sslContextFactory,
                                                                              HttpVersion.HTTP_1_1.asString() );


        // Http Connection factory
        HttpConnectionFactory httpConnectionFactory = new HttpConnectionFactory( httpConfiguration );

        // Build the connector
        ServerConnector sslConnector = new ServerConnector( server, sslConnectionFactory, httpConnectionFactory );
        configureCommonFields( sslConnector, config );
        sslConnector.setPort( config.getPort() );
        server.addConnector( sslConnector );

    }

    public static void configureConnector( Server server, ConnectorConf connector ) {
        if ( connector instanceof HttpsConnectorConf ) {
            configureConnector( server, (HttpsConnectorConf) connector );
        } else if ( connector instanceof HttpConnectorConf ) {
            configureConnector( server, (HttpConnectorConf) connector );
        } else {
            throw new IllegalArgumentException();
        }

    }
}
