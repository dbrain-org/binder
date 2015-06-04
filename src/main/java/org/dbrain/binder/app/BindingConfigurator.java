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

package org.dbrain.binder.app;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * Configure a single service description.
 */
public interface BindingConfigurator<T> extends ServiceConfigurator {

    BindingConfigurator<T> providedBy( T instance );

    BindingConfigurator<T> providedBy( ServiceProvider<T> provider );

    BindingConfigurator<T> disposedBy( ServiceDisposer<T> disposer );

    BindingConfigurator<T> to( Type type );

    BindingConfigurator<T> qualifiedBy( Annotation quality );

    BindingConfigurator<T> qualifiedBy( Class<? extends Annotation> quality );

    BindingConfigurator<T> qualifiedBy( Iterable<Annotation> quality );

    BindingConfigurator<T> named( String name );

    BindingConfigurator<T> in( Class<? extends Annotation> scope );

    BindingConfigurator<T> useProxy();

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
