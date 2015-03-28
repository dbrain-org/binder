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

package org.dbrain.yaw.system.jetty.websocket;

import org.dbrain.yaw.system.lifecycle.ContextRegistry;
import org.dbrain.yaw.system.scope.RequestScopeContext;
import org.dbrain.yaw.system.scope.SessionScopeContext;
import org.eclipse.jetty.websocket.api.extensions.Frame;
import org.eclipse.jetty.websocket.common.LogicalConnection;
import org.eclipse.jetty.websocket.common.SessionListener;
import org.eclipse.jetty.websocket.common.events.EventDriver;
import org.eclipse.jetty.websocket.jsr356.ClientContainer;
import org.eclipse.jetty.websocket.jsr356.JsrSession;

import java.net.URI;

/**
 * Created by epoitras on 3/27/15.
 */
public class JsrScopedSession extends JsrSession {

    private final RequestScopeContext requestScopeContext;
    private final SessionScopeContext sessionScopeContext;
    private final ContextRegistry scopeRegistry = new ContextRegistry();


    public JsrScopedSession( RequestScopeContext requestScopeContext,
                             SessionScopeContext sessionScopeContext,
                             LogicalConnection connection,
                             ClientContainer container,
                             String id,
                             URI requestURI,
                             EventDriver websocket,
                             SessionListener... sessionListeners ) {
        super( requestURI, websocket, connection, container, id, sessionListeners );
        this.requestScopeContext = requestScopeContext;
        this.sessionScopeContext = sessionScopeContext;
    }

    @Override
    public void notifyClose( int statusCode, String reason ) {
        super.notifyClose( statusCode, reason );

        // Discard the session scope registry.
        scopeRegistry.close();

    }

    @Override
    protected void doStop() throws Exception {
        super.doStop();
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    @Override
    public void incomingFrame( Frame frame ) {
        requestScopeContext.enter();

        // Go fetch the scope registry in the session.
        sessionScopeContext.enter( () -> scopeRegistry );
        try {
            super.incomingFrame( frame );
        } finally {
            requestScopeContext.leave();
            sessionScopeContext.leave();
        }
    }
}
