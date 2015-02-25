package org.dbrain.yaw.http.server.config;

import javax.websocket.server.ServerEndpointConfig;

/**
 * Created by epoitras on 11/09/14.
 */
public interface WebSocketConfig {

    void accept( Visitor v ) throws Exception;

    /**
     * Websocket defined using standard javax.websocket.* annotations.
     */
    public static EndpointClassWebSocketConfig of( Class<?> endpointClass ) {
        return new EndpointClassWebSocketConfig( endpointClass );
    }

    /**
     * Websocket defined with a server enpoint configuration.
     * @param endpointConfig
     * @return
     */
    public static ServerEndpointConfigWebSocketConfig of ( ServerEndpointConfig endpointConfig ) {
        return new ServerEndpointConfigWebSocketConfig( endpointConfig );
    }


    public class EndpointClassWebSocketConfig implements WebSocketConfig {

        private final Class<?> endpointClass;

        private EndpointClassWebSocketConfig( Class<?> endpointClass ) {
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

    public class ServerEndpointConfigWebSocketConfig implements WebSocketConfig {

        private final ServerEndpointConfig config;

        public ServerEndpointConfigWebSocketConfig( ServerEndpointConfig config ) {
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




    /**
     * Visitor for configuration.
     */
    public interface Visitor {

        void visit( EndpointClassWebSocketConfig endpointClassWebSocketDef ) throws Exception;
        void visit( ServerEndpointConfigWebSocketConfig serverEndpointConfig ) throws Exception;

    }
}
