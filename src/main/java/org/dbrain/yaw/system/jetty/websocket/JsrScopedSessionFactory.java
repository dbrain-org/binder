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

import org.dbrain.yaw.system.scope.RequestScopeContext;
import org.dbrain.yaw.system.scope.SessionScopeContext;
import org.eclipse.jetty.websocket.common.LogicalConnection;
import org.eclipse.jetty.websocket.common.SessionFactory;
import org.eclipse.jetty.websocket.common.SessionListener;
import org.eclipse.jetty.websocket.common.WebSocketSession;
import org.eclipse.jetty.websocket.common.events.EventDriver;
import org.eclipse.jetty.websocket.jsr356.ClientContainer;
import org.eclipse.jetty.websocket.jsr356.endpoints.AbstractJsrEventDriver;

import javax.inject.Inject;
import java.net.URI;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by epoitras on 3/27/15.
 */
public class JsrScopedSessionFactory implements SessionFactory {

    private AtomicLong idgen = new AtomicLong( 0 );
    private ClientContainer   container;
    private SessionListener[] listeners;

    private final RequestScopeContext requestScopeContext;
    private final SessionScopeContext sessionScopeContext;

    @Inject
    public JsrScopedSessionFactory( RequestScopeContext requestScopeContext, SessionScopeContext sessionScopeContext ) {
        this.requestScopeContext = requestScopeContext;
        this.sessionScopeContext = sessionScopeContext;
    }


    public void configure( ClientContainer container, SessionListener... sessionListeners ) {
        this.container = container;
        this.listeners = sessionListeners;
    }

    @Override
    public WebSocketSession createSession( URI requestURI, EventDriver websocket, LogicalConnection connection ) {
        return new JsrScopedSession( requestScopeContext,
                                     sessionScopeContext,
                                     connection,
                                     container,
                                     getNextId(),
                                     requestURI, websocket,
                                     listeners );
    }

    public String getNextId() {
        return String.format( "websocket-%d", idgen.incrementAndGet() );
    }

    @Override
    public boolean supports( EventDriver websocket ) {
        return ( websocket instanceof AbstractJsrEventDriver );
    }

}
