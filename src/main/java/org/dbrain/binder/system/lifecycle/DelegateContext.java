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

package org.dbrain.binder.system.lifecycle;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.Context;
import org.glassfish.hk2.api.ServiceHandle;

import javax.inject.Provider;
import java.lang.annotation.Annotation;

/**
 * A context that delegats to a registry of service.
 */
public class DelegateContext<T extends Annotation> implements Context<T> {

    private final Class<T>                  annotation;
    private final Provider<ContextRegistry> registryProvider;
    private final boolean                   supportNullCreation;
    private boolean active = true;

    public DelegateContext( Class<T> annotation,
                            Provider<ContextRegistry> registryProvider,
                            boolean supportNullCreation ) {
        this.annotation = annotation;
        this.registryProvider = registryProvider;
        this.supportNullCreation = supportNullCreation;
    }

    protected Provider<ContextRegistry> getRegistryProvider() {
        return registryProvider;
    }

    @Override
    public synchronized Class<T> getScope() {
        return annotation;
    }

    @Override
    public synchronized <U> U findOrCreate( ActiveDescriptor<U> activeDescriptor, ServiceHandle<?> root ) {
        return registryProvider.get().findOrCreate( activeDescriptor, root );
    }

    @Override
    public synchronized boolean containsKey( ActiveDescriptor<?> descriptor ) {
        return registryProvider.get().containsKey( descriptor );
    }

    @Override
    public synchronized void destroyOne( ActiveDescriptor<?> descriptor ) {
        registryProvider.get().destroyOne( descriptor );

    }

    @Override
    public synchronized boolean supportsNullCreation() {
        return supportNullCreation;
    }

    @Override
    public synchronized boolean isActive() {
        return active && registryProvider.get() != null;
    }

    @Override
    public synchronized void shutdown() {
        active = false;
    }
}
