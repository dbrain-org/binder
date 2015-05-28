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

package org.dbrain.binder.http.server;

import org.dbrain.binder.http.server.defs.HttpsConnectorDef;

import java.net.URI;

/**
 * Created with IntelliJ IDEA.
 * User: epoitras
 * Date: 15/07/13
 * Time: 9:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class HttpsConnectorBuilder extends AbstractHttpConnectorBuilder<HttpsConnectorBuilder> {

    private String host;

    private URI keyStore;

    private String keyStorePassword;

    private String keyManagerPassord;

    @Override
    public HttpsConnectorBuilder self() {
        return this;
    }

    public static HttpsConnectorBuilder from( String serverURI ) {
        return new HttpsConnectorBuilder().uri( URI.create( serverURI ) );
    }

    public static HttpsConnectorBuilder from( URI serverURI ) {
        return new HttpsConnectorBuilder().uri( serverURI );
    }

    private HttpsConnectorBuilder() {
    }

    public HttpsConnectorBuilder uri( URI serverURI ) {
        if ( serverURI != null ) {

            if ( serverURI.getScheme() != null ) {
                switch ( serverURI.getScheme().toLowerCase() ) {
                    case "https":
                        break;
                    default:
                        throw new IllegalArgumentException( "Unsupported scheme: " + serverURI.getScheme() );
                }
            }

            int port = serverURI.getPort();
            if ( port >= 0 ) {
                port( port );
            }

            host( serverURI.getHost() );
        }
        return self();
    }


    public HttpsConnectorBuilder host( String host ) {
        this.host = host;
        return self();
    }

    public HttpsConnectorBuilder keystore( URI keyStoreUri, String keyStorePassword, String keyManagerPassord ) {
        this.keyStore = keyStoreUri;
        this.keyStorePassword = keyStorePassword;
        this.keyManagerPassord = keyManagerPassord;
        return self();
    }

    public HttpsConnectorDef build() {
        Integer finalPort = getPort();
        finalPort = finalPort != null ? finalPort : 443;

        HttpsConnectorDef result = new HttpsConnectorDef();
        result.setPort( finalPort );
        result.setHost( host );
        result.setKeyStore( keyStore );
        result.setKeyManagerPassord( keyManagerPassord );
        result.setKeyStorePassword( keyStorePassword );

        return result;
    }

}
