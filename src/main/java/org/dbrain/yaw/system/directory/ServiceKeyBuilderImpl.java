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

package org.dbrain.yaw.system.directory;

import org.dbrain.yaw.directory.ServiceKey;
import org.dbrain.yaw.directory.ServiceKeyBuilder;
import org.dbrain.yaw.system.util.AnnotationBuilder;

import javax.inject.Named;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by epoitras on 3/15/15.
 */
public class ServiceKeyBuilderImpl<T> implements ServiceKeyBuilder<T> {

    private final Type     serviceType;
    private final Class<T> serviceClass;
    private final Set<Annotation> qualifiers = new HashSet<>();

    public ServiceKeyBuilderImpl( Type serviceType, Class<T> serviceClass ) {
        this.serviceType = serviceType;
        this.serviceClass = serviceClass;
    }

    @Override
    public ServiceKeyBuilderImpl<T> qualifiedBy( Annotation quality ) {
        if ( quality != null ) {
            qualifiers.add( quality );
        }
        return this;
    }

    @Override
    public ServiceKeyBuilderImpl<T> qualifiedBy( Class<? extends Annotation> quality ) {
        if ( quality != null ) {
            return qualifiedBy( AnnotationBuilder.of( quality ) );
        } else {
            return this;
        }
    }

    @Override
    public ServiceKeyBuilderImpl<T> qualifiedBy( Iterable<Annotation> quality ) {
        if ( quality != null ) {
            for ( Annotation a : quality ) {
                qualifiedBy( a );
            }
        }
        return this;
    }

    @Override
    public ServiceKeyBuilderImpl<T> named( String name ) {
        if ( name != null ) {
            return qualifiedBy( AnnotationBuilder.of( Named.class, name ) );
        } else {
            return this;
        }
    }

    @Override
    public ServiceKey<T> build() {
        return new ServiceKeyImpl<>( serviceType, serviceClass, qualifiers );
    }



}
