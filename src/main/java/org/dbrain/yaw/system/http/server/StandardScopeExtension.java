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

package org.dbrain.yaw.system.http.server;

import org.dbrain.yaw.system.lifecycle.ContextRegistry;
import org.dbrain.yaw.system.scope.RequestScopeContext;
import org.dbrain.yaw.system.scope.SessionScopeContext;
import org.eclipse.jetty.websocket.api.BatchMode;
import org.eclipse.jetty.websocket.api.WriteCallback;
import org.eclipse.jetty.websocket.api.extensions.ExtensionConfig;
import org.eclipse.jetty.websocket.api.extensions.Frame;
import org.eclipse.jetty.websocket.common.OpCode;
import org.eclipse.jetty.websocket.common.extensions.AbstractExtension;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.ServiceLocatorFactory;

/**
 * Created by epoitras on 3/21/15.
 */
public class StandardScopeExtension extends AbstractExtension {

    public static final String NAME      = "YawScopeSupport";
    public static final String PARAM_APP = "AppName";

    private final ContextRegistry sessionRegistry = new ContextRegistry();

    private RequestScopeContext requestScopeContext = null;
    private SessionScopeContext sessionScopeContext = null;

    public StandardScopeExtension() {
    }

    @Override
    public void setConfig( ExtensionConfig config ) {
        super.setConfig( config );
        String appName = config.getParameter( PARAM_APP, null );
        ServiceLocator locator = ServiceLocatorFactory.getInstance().find( appName );
        requestScopeContext = locator.getService( RequestScopeContext.class );
        sessionScopeContext = locator.getService( SessionScopeContext.class );
    }

    private void enterScope() {
        requestScopeContext.enter();
        sessionScopeContext.enter( () -> sessionRegistry );
    }

    private void leaveScope() {
        requestScopeContext.leave();
        sessionScopeContext.leave();
    }


    @Override
    public void incomingFrame( Frame frame ) {
        enterScope();
        try {
            getNextIncoming().incomingFrame( frame );
        } finally {
            leaveScope();
            if ( frame.getOpCode() == OpCode.CLOSE ) {
                sessionRegistry.close();
            }
        }

    }

    @Override
    public void outgoingFrame( Frame frame, WriteCallback callback, BatchMode batchMode ) {
        getNextOutgoing().outgoingFrame( frame, callback, batchMode );
    }

}

