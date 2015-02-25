package org.dbrain.yaw.jetty;

import org.dbrain.yaw.http.server.config.EndpointConfig;
import org.dbrain.yaw.http.server.config.HttpEndpointConfig;
import org.dbrain.yaw.http.server.config.HttpsEndpointConfig;
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

    private static HttpConfiguration getStandardConfiguration( HttpEndpointConfig config ) {
        HttpConfiguration httpConfiguration = new HttpConfiguration();
        httpConfiguration.setSendServerVersion( false );
        httpConfiguration.setSendXPoweredBy( false );
        httpConfiguration.setSendDateHeader( false );

        return httpConfiguration;

    }


    private static void configureCommonFields( ServerConnector connector, HttpEndpointConfig config ) {
        if ( config.getHost() != null ) {
            connector.setHost( config.getHost() );
        }
    }

    public static void configureConnector( Server server, HttpEndpointConfig config ) {

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

    public static void configureConnector( Server server, HttpsEndpointConfig config ) {

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
        sslConnector.setPort( config.getSecurePort() );
        server.addConnector( sslConnector );

    }

    public static void configureConnector( Server server, EndpointConfig connector ) {
        if ( connector instanceof HttpsEndpointConfig ) {
            configureConnector( server, (HttpsEndpointConfig) connector );
        } else if ( connector instanceof HttpEndpointConfig ) {
            configureConnector( server, (HttpEndpointConfig) connector );
        } else {
            throw new IllegalArgumentException();
        }

    }
}
