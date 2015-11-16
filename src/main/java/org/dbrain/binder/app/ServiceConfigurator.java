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
public interface ServiceConfigurator<T> extends Component {

    ServiceConfigurator<T> toInstance( T instance );

    ServiceConfigurator<T> providedBy( ServiceProvider<T> provider );

    ServiceConfigurator<T> disposedBy( ServiceDisposer<T> disposer );

    ServiceConfigurator<T> to( Type type );

    ServiceConfigurator<T> qualifiedBy( Annotation quality );

    ServiceConfigurator<T> qualifiedBy( Class<? extends Annotation> quality );

    ServiceConfigurator<T> qualifiedBy( Iterable<Annotation> quality );

    ServiceConfigurator<T> named( String name );

    ServiceConfigurator<T> in( Class<? extends Annotation> scope );

    ServiceConfigurator<T> useProxy();

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
