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

package org.dbrain.app.system.lifecycle;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.ServiceHandle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of a registry with life-cycle management:
 *
 * All AutoCloseable instance registered to the scope will be close at the same time as this scope.
 */
public class ContextRegistry implements AutoCloseable {

    private final Map<ActiveDescriptor, Object> managedObjects = new HashMap<>();

    public ContextRegistry() {
    }

    /**
     * Called when a new object is registered.
     */
    protected <T> void addEntry( ActiveDescriptor key, Object e ) {
        managedObjects.put( key, e );
    }


    /**
     * Retrieve an object from the scope registry.
     */
    public synchronized <T> T findOrCreate( ActiveDescriptor<T> key, ServiceHandle<?> root ) {
        if ( managedObjects.containsKey( key ) ) {
            return (T) managedObjects.get( key );
        } else {
            T value = key.create( root );
            addEntry( key, value );
            return value;
        }
    }

    /**
     * Validate that an object is defined within a scope.
     */
    public synchronized boolean containsKey( ActiveDescriptor key ) {
        return managedObjects.containsKey( key );
    }

    public synchronized void destroyOne( ActiveDescriptor key ) {
        // Must destroy object ?
        Object value = managedObjects.remove( key );
        if ( value != null ) {
            key.dispose( value );
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws org.glassfish.hk2.api.MultiException In case one or more managed Disposable has thrown an exception.
     */
    @Override
    public synchronized void close() {
        List<Throwable> exceptions = null;

        // Dispose of all managed objects
        for ( Map.Entry<ActiveDescriptor, Object> entry : new ArrayList<>( managedObjects.entrySet() ) ) {
            if ( entry.getValue() != null ) {
                entry.getKey().dispose( entry.getValue() );
            }
        }

        // Forget the list
        managedObjects.clear();

        // Throw an exception if one occured.
        if ( exceptions != null ) {
            throw new MultiException( exceptions );
        }
    }


}
