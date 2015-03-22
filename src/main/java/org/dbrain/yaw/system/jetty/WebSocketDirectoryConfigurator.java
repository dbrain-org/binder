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

package org.dbrain.yaw.system.jetty;

import org.dbrain.yaw.app.App;
import org.dbrain.yaw.directory.ServiceDirectory;
import org.dbrain.yaw.system.http.server.StandardScopeExtension;
import org.eclipse.jetty.websocket.api.extensions.ExtensionConfig;
import org.eclipse.jetty.websocket.jsr356.JsrExtension;

import javax.websocket.Extension;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;
import java.util.List;

/**
 * Created by epoitras on 3/21/15.
 */
public class WebSocketDirectoryConfigurator extends ServerEndpointConfig.Configurator {

    private final ServiceDirectory                  directory;
    private final ServerEndpointConfig.Configurator delegate;

    public WebSocketDirectoryConfigurator( ServiceDirectory directory, ServerEndpointConfig.Configurator delegate ) {
        this.directory = directory;
        this.delegate = delegate;
    }

    @Override
    public String getNegotiatedSubprotocol( List<String> supported, List<String> requested ) {
        return delegate.getNegotiatedSubprotocol( supported, requested );
    }

    @Override
    public List<Extension> getNegotiatedExtensions( List<Extension> installed, List<Extension> requested ) {
        List<Extension> result = delegate.getNegotiatedExtensions( installed, requested );

        ExtensionConfig standardScopeExtension = new ExtensionConfig( StandardScopeExtension.NAME );
        standardScopeExtension.setParameter( StandardScopeExtension.PARAM_APP,
                                             directory.getInstance( App.class ).getName() );
        result.add( new JsrExtension( standardScopeExtension ));
        return result;
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
        return directory.getJitInstance( endpointClass );
        //return delegate.getEndpointInstance( endpointClass );
    }
}
