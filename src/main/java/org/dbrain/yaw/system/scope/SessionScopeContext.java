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

package org.dbrain.yaw.system.scope;

import org.dbrain.yaw.scope.SessionScoped;
import org.dbrain.yaw.system.lifecycle.ContextRegistry;
import org.dbrain.yaw.system.lifecycle.DelegateContext;
import org.dbrain.yaw.system.lifecycle.ThreadLocalProvider;

import javax.inject.Provider;

/**
 * Implementation of the session scope.
 */
public class SessionScopeContext extends DelegateContext<SessionScoped> {

    private ThreadLocalProvider<ContextRegistry> registryProvider;

    public SessionScopeContext() {
        super( SessionScoped.class, new ThreadLocalProvider<>(), false );
        registryProvider = (ThreadLocalProvider<ContextRegistry>)getRegistryProvider();
    }

    public void enter( Provider<ContextRegistry> contextProvider ) {
        registryProvider.enter( contextProvider );
    }

    public Provider<ContextRegistry> leave() {
        return registryProvider.leave();
    }

    @Override
    public synchronized void shutdown() {
        super.shutdown();
        while ( registryProvider.size() > 0 ) {
            leave();
        }
    }
}
