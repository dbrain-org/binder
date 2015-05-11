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

package org.dbrain.app.http.server.defs;

import javax.websocket.server.ServerEndpointConfig;

/**
 * Created by epoitras on 11/09/14.
 */
public interface WebSocketDef {

    /**
     * Websocket defined using standard javax.websocket.* annotations.
     */
    public static EndpointClassWebSocketDef of( Class<?> endpointClass ) {
        return new EndpointClassWebSocketDef( endpointClass );
    }

    /**
     * Websocket defined with a server enpoint configuration.
     */
    public static ServerEndpointConfigWebSocketDef of( ServerEndpointConfig endpointConfig ) {
        return new ServerEndpointConfigWebSocketDef( endpointConfig );
    }

    void accept( Visitor v ) throws Exception;


    /**
     * Visitor for configuration.
     */
    public interface Visitor {

        void visit( EndpointClassWebSocketDef endpointClassWebSocketDef ) throws Exception;

        void visit( ServerEndpointConfigWebSocketDef serverEndpointConfig ) throws Exception;

    }

    public class EndpointClassWebSocketDef implements WebSocketDef {

        private final Class<?> endpointClass;

        private EndpointClassWebSocketDef( Class<?> endpointClass ) {
            this.endpointClass = endpointClass;
        }

        public Class<?> getEndpointClass() {
            return endpointClass;
        }

        @Override
        public void accept( Visitor v ) throws Exception {
            v.visit( this );
        }
    }

    public class ServerEndpointConfigWebSocketDef implements WebSocketDef {

        private final ServerEndpointConfig config;

        public ServerEndpointConfigWebSocketDef( ServerEndpointConfig config ) {
            this.config = config;
        }

        public ServerEndpointConfig getConfig() {
            return config;
        }

        @Override
        public void accept( Visitor v ) throws Exception {
            v.visit( this );
        }
    }
}
