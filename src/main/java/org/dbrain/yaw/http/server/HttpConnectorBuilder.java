package org.dbrain.yaw.http.server;

import org.dbrain.yaw.http.server.config.EndpointConfig;
import org.dbrain.yaw.http.server.config.HttpEndpointConfig;
import org.dbrain.yaw.http.server.config.HttpsEndpointConfig;

import java.net.URI;

/**
 * Created with IntelliJ IDEA.
 * User: epoitras
 * Date: 15/07/13
 * Time: 9:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class HttpConnectorBuilder {

    private boolean secure = false;

    private int port = 80;

    private int securePort = 443;

    private URI keyStore;

    private String keyStorePassword;

    private String keyManagerPassord;

    private String host;

    public HttpConnectorBuilder() {
    }

    public HttpConnectorBuilder( URI serverURI ) {
        if ( serverURI != null ) {

            if ( serverURI.getScheme() != null ) {
                switch ( serverURI.getScheme().toLowerCase() ) {
                    case "https":
                        secure = true;
                        break;
                    case "http":
                        secure = false;
                        break;
                    default:
                        throw new IllegalArgumentException( "Unsupported scheme: " + serverURI.getScheme() );
                }
            }

            int port = serverURI.getPort();
            if ( port >= 0 ) {
                if ( secure ) {
                    securePort( port );
                } else {
                    port( port );
                }
            }

            host( serverURI.getHost() );
        }
    }


    public HttpConnectorBuilder port( int port ) {
        if ( port >= 0 && port < 65536 ) {
            this.port = port;
        } else {
            throw new IllegalArgumentException();
        }
        return this;
    }

    public HttpConnectorBuilder securePort( int port ) {
        if ( port >= 0 && port < 65536 ) {
            this.securePort = port;
        } else {
            throw new IllegalArgumentException();
        }
        return this;
    }

    public HttpConnectorBuilder host( String host ) {
        this.host = host;
        return this;
    }

    public HttpConnectorBuilder setupSsl( URI keyStoreUri, String keyStorePassword, String keyManagerPassord ) {
        this.keyStore = keyStoreUri;
        this.keyStorePassword = keyStorePassword;
        this.keyManagerPassord = keyManagerPassord;
        return this;
    }

    public EndpointConfig build() {

        if ( secure ) {
            HttpsEndpointConfig result = new HttpsEndpointConfig();
            result.setPort( port );
            result.setSecurePort( securePort );
            result.setHost( host );
            result.setKeyManagerPassord( keyManagerPassord );
            result.setKeyStorePassword( keyStorePassword );
            return result;
        } else {
            HttpEndpointConfig result = new HttpEndpointConfig();
            result.setPort( port );
            return result;
        }
    }

}
