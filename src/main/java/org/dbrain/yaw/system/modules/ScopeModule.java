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

package org.dbrain.yaw.system.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.OutOfScopeException;
import com.google.inject.Provider;
import com.google.inject.Scope;
import org.dbrain.yaw.scope.RequestScoped;
import org.dbrain.yaw.scope.SessionScoped;
import org.dbrain.yaw.system.scope.ScopeRegistry;
import org.dbrain.yaw.system.scope.ScopeRegistryProvider;

/**
 * Guice module that provides implementation of Request and Session scopes.
 */
public class ScopeModule extends AbstractModule {

    public static final ThreadLocal<ScopeRegistryProvider> SESSION_SCOPE_REGISTRY = new ThreadLocal<>();
    public static final ThreadLocal<ScopeRegistryProvider> REQUEST_SCOPE_REGISTRY = new ThreadLocal<>();

    @Override
    protected void configure() {

        bindScope( RequestScoped.class, new Scope() {
            @Override
            public <T> Provider<T> scope( final Key<T> key, final Provider<T> unscoped ) {
                return () -> {
                    ScopeRegistryProvider provider = REQUEST_SCOPE_REGISTRY.get();
                    ScopeRegistry registry = provider != null ? provider.get() : null;
                    if ( registry != null ) {
                        return registry.get( key, unscoped );
                    } else {
                        throw new OutOfScopeException( "Request scope not available." );
                    }
                };
            }
        } );

        bindScope( SessionScoped.class, new Scope() {
            @Override
            public <T> Provider<T> scope( final Key<T> key, final Provider<T> unscoped ) {
                return () -> {
                    ScopeRegistryProvider provider = SESSION_SCOPE_REGISTRY.get();
                    ScopeRegistry registry = provider != null ? provider.get() : null;
                    if ( registry != null ) {
                        return registry.get( key, unscoped );
                    } else {
                        throw new OutOfScopeException( "Session scope not available." );
                    }
                };
            }
        } );
    }
}
