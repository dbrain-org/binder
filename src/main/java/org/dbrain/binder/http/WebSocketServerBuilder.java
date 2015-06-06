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

import org.dbrain.binder.directory.ServiceKey;
import org.dbrain.binder.http.conf.WebSocketConfiguredServerConf;
import org.dbrain.binder.http.conf.WebSocketServiceServerConf;

import javax.websocket.Decoder;
import javax.websocket.Encoder;
import javax.websocket.Extension;
import javax.websocket.server.ServerEndpointConfig;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Builder for WebSocket server endpoint.
 */
public class WebSocketServerBuilder {

    /**
     * Websocket defined using standard javax.websocket.* annotations.
     */
    public static WebSocketServiceServerConf of( Class<?> endpointClass ) {
        return new WebSocketServiceServerConf( ServiceKey.of( endpointClass ) );
    }

    /**
     * Websocket service defined using standard javax.websocket.* annotations.
     */
    public static WebSocketServiceServerConf of( ServiceKey<?> endpointService ) {
        return new WebSocketServiceServerConf( endpointService );
    }

    /**
     * Websocket defined with a server enpoint configuration.
     */
    public static WebSocketConfiguredServerConf of( ServerEndpointConfig endpointConfig ) {
        return new WebSocketConfiguredServerConf( endpointConfig );
    }

    public static WebSocketServerBuilder from( Class<?> endpointClass, String path ) {
        return new WebSocketServerBuilder().endpointClass( endpointClass ).path( path );
    }

    private Class<?> endpointClass;
    private String   path;
    private List<String>                   subprotocols = Collections.emptyList();
    private List<Extension>                extensions   = Collections.emptyList();
    private List<Class<? extends Encoder>> encoders     = Collections.emptyList();
    private List<Class<? extends Decoder>> decoders     = Collections.emptyList();

    private WebSocketServerBuilder() {
    }

    public WebSocketServerBuilder path( String path ) {
        this.path = path;
        return this;
    }

    public WebSocketServerBuilder endpointClass( Class<?> endpointClass ) {
        this.endpointClass = endpointClass;
        return this;
    }

    public WebSocketServerBuilder subprotocols( String... subprotocols ) {
        this.subprotocols.clear();
        for ( String subprotocol : subprotocols ) {
            this.subprotocols.add( subprotocol );
        }
        return this;
    }

    public WebSocketServerBuilder addExtension( Extension extension ) {
        this.extensions.add( extension );
        return this;
    }

    public WebSocketServerBuilder addEncoder( Class<? extends Encoder> encoder ) {
        this.encoders.add( encoder );
        return this;
    }

    public WebSocketServerBuilder addDecoder( Class<? extends Decoder> decoder ) {
        this.decoders.add( decoder );
        return this;
    }

    public WebSocketConfiguredServerConf build() {

        return of( ServerEndpointConfig.Builder.create( endpointClass, path )
                                               .subprotocols( new ArrayList<>( subprotocols ) )
                                               .extensions( new ArrayList<>( extensions ) )
                                               .encoders( new ArrayList<>( encoders ) )
                                               .decoders( new ArrayList<>( decoders ) )
                                               .build() );

    }

}
