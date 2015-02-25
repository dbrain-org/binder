package org.dbrain.yaw.http.server.config;

/**
 * Created by epoitras on 17/09/14.
 */
public class HttpEndpointConfig implements EndpointConfig {

    private int port = 80;

    private String host;

    public int getPort() {
        return port;
    }

    public void setPort( int port ) {
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost( String host ) {
        this.host = host;
    }

}
