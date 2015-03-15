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

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by epoitras on 3/15/15.
 */
public class ServiceKeyImpl<T> implements ServiceKey<T> {

    private final Type            serviceType;
    private final Class<T>        serviceClass;
    private final Set<Annotation> qualifiers;

    public ServiceKeyImpl( Type serviceType, Class<T> serviceClass, Collection<Annotation> qualifiers ) {
        this.serviceType = serviceType;
        this.serviceClass = serviceClass;
        this.qualifiers = Collections.unmodifiableSet( new HashSet<>( qualifiers ) );
    }

    @Override
    public Type getServiceType() {
        return serviceType;
    }

    @Override
    public Class<T> getServiceClass() {
        return serviceClass;
    }

    @Override
    public Set<Annotation> getQualifiers() {
        return qualifiers;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( o == null || !(o instanceof ServiceKey) ) return false;

        ServiceKey that = (ServiceKey) o;

        if ( !qualifiers.equals( that.getQualifiers() ) ) return false;
        if ( !serviceType.equals( that.getServiceType() ) ) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = serviceType.hashCode();
        result = 31 * result + qualifiers.hashCode();
        return result;
    }
}
