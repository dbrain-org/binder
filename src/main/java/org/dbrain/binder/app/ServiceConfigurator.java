/*
 * Copyright [2016] [Eric Poitras]
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

package org.dbrain.binder.app;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * Configure a single service description.
 */
public interface ServiceConfigurator<T, U extends ServiceConfigurator<T, U>> {

    U disposedBy( ServiceDisposer<T> disposer );

    U to( Type type );

    U qualifiedBy( Annotation quality );

    U qualifiedBy( Class<? extends Annotation> quality );

    U qualifiedBy( Iterable<Annotation> quality );

    U named( String name );

    U useProxy();

    /**
     * Configure an instance-based service.
     */
    interface Instance<T> extends ServiceConfigurator<T, Instance<T>> {
    }

    /**
     * Configure a provider-based service description.
     */
    interface Scoped<T> extends ServiceConfigurator<T, Scoped<T>> {

        Scoped<T> providedBy( ServiceProvider<T> provider );

        Scoped<T> in( Class<? extends Annotation> scope );

    }

    /**
     * A disposer that can fail miserably by throwing an exception.
     */
    @FunctionalInterface
    interface ServiceDisposer<T> {

        /**
         * @return An instance of the service, or null.
         *
         * @throws Exception If the dispose failed.
         */
        void dispose( T instance ) throws Exception;

    }

    /**
     * A provider that can fail miserably by throwing an exception.
     */
    @FunctionalInterface
    interface ServiceProvider<T> {

        /**
         * @return An instance of the service.
         *
         * @throws Exception If the creation failed.
         */
        T get() throws Exception;

    }
}
