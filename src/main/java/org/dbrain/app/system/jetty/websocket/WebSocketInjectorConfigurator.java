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

package org.dbrain.app.system.jetty.websocket;

import org.dbrain.app.directory.ServiceDirectory;
import org.dbrain.app.directory.ServiceKey;

import javax.websocket.Extension;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;
import java.util.List;

/**
 * Support injection into Websocket instances.
 */
public class WebSocketInjectorConfigurator extends ServerEndpointConfig.Configurator {

    private final ServiceDirectory                  directory;
    private final ServerEndpointConfig.Configurator delegate;
    private final ServiceKey<?>                     serviceKey;

    public WebSocketInjectorConfigurator( ServiceDirectory directory,
                                          ServerEndpointConfig.Configurator delegate,
                                          ServiceKey<?> serviceKey ) {
        this.directory = directory;
        this.delegate = delegate;
        this.serviceKey = serviceKey;
    }

    @Override
    public String getNegotiatedSubprotocol( List<String> supported, List<String> requested ) {
        return delegate.getNegotiatedSubprotocol( supported, requested );
    }

    @Override
    public List<Extension> getNegotiatedExtensions( List<Extension> installed, List<Extension> requested ) {
        return delegate.getNegotiatedExtensions( installed, requested );
    }

    @Override
    public boolean checkOrigin( String originHeaderValue ) {
        return delegate.checkOrigin( originHeaderValue );
    }

    @Override
    public void modifyHandshake( ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response ) {
        delegate.modifyHandshake( sec, request, response );
    }

    @Override
    public <T> T getEndpointInstance( Class<T> endpointClass ) throws InstantiationException {
        return endpointClass.cast( directory.getOrCreateInstance( serviceKey.getServiceClass() ) );
    }
}
