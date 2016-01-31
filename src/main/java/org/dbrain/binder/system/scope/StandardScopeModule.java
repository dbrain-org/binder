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

package org.dbrain.binder.system.scope;

import org.dbrain.binder.app.Binder;
import org.dbrain.binder.app.Module;
import org.dbrain.binder.lifecycle.RequestScoped;
import org.dbrain.binder.lifecycle.SessionScoped;
import org.glassfish.hk2.api.Context;
import org.glassfish.hk2.api.TypeLiteral;

import javax.inject.Singleton;

/**
 * This feature defines both the Request and Session scopes.
 */
public class StandardScopeModule implements Module {

    @Override
    public void configure( Binder binder ) throws Exception {
        // Define the request scope
        binder.bind( RequestScopeContext.class ) //
              .to( new TypeLiteral<Context<RequestScoped>>() {}.getType() ) //
              .to( RequestScopeContext.class ) //
              .in( Singleton.class );

        // Define the session scope
        binder.bind( SessionScopeContext.class ) //
              .to( new TypeLiteral<Context<SessionScoped>>() {}.getType() ) //
              .to( SessionScopeContext.class ) //
              .in( Singleton.class );
    }
}
