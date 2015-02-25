package org.dbrain.yaw.http.server.config;

import java.net.URI;

/**
* Created by epoitras on 17/09/14.
*/
public class HttpsEndpointConfig extends HttpEndpointConfig implements EndpointConfig {

    private int securePort = 443;

    private URI keyStore;

    private String keyStorePassword;

    private String keyManagerPassord;

    public int getSecurePort() {
        return securePort;
    }

    public void setSecurePort( int securePort ) {
        this.securePort = securePort;
    }

    public URI getKeyStore() {
        return keyStore;
    }

    public void setKeyStore( URI keyStore ) {
        this.keyStore = keyStore;
    }

    public String getKeyStorePassword() {
        return keyStorePassword;
    }

    public void setKeyStorePassword( String keyStorePassword ) {
        this.keyStorePassword = keyStorePassword;
    }

    public String getKeyManagerPassord() {
        return keyManagerPassord;
    }

    public void setKeyManagerPassord( String keyManagerPassord ) {
        this.keyManagerPassord = keyManagerPassord;
    }

}
