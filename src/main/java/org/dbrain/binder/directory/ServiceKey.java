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

package org.dbrain.binder.directory;

import org.dbrain.binder.system.directory.ServiceKeyBuilderImpl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * Describe a service.
 */
public interface ServiceKey<T> {

    static <U> Builder<U> from( Class<U> clazz ) {
        return new ServiceKeyBuilderImpl<>( clazz, clazz );
    }

    static <U> ServiceKey<U> of( Class<U> clazz ) {
        return from( clazz ).build();
    }

    static <U> ServiceKey<U> of( Class<U> clazz, String named ) {
        return from( clazz ).named( named ).build();
    }

    static <U> ServiceKey<U> of( Class<U> clazz, Class<? extends Annotation> quality ) {
        return from( clazz ).qualifiedBy( quality ).build();
    }

    Type getServiceType();

    Class<T> getServiceClass();

    Qualifiers getQualifiers();

    /**
     * Builder for service key.
     */
    interface Builder<T> {

        Builder<T> qualifiedBy( Annotation quality );

        Builder<T> qualifiedBy( Class<? extends Annotation> quality );

        Builder<T> qualifiedBy( Iterable<Annotation> quality );

        Builder<T> named( String name );

        ServiceKey<T> build();

    }
}
