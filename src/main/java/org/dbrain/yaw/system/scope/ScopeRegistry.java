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

import com.google.inject.Provider;
import org.dbrain.yaw.scope.DisposeException;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Implementation of a registry with life-cycle management:
 *
 * All AutoCloseable instance registered to the scope will be close at the same time as this scope.
 */
public class ScopeRegistry implements AutoCloseable {

    private Map<Object, Object> managedObjects;

    /**
     * Called when a new object is registered.
     */
    protected <T> void registerObject( Object key, T value ) {
        if ( managedObjects == null ) {
            managedObjects = new HashMap<>();
        }
        managedObjects.put( key, value );
    }

    /**
     * Retrieve an object from the scope registry.
     */
    public synchronized <T> T get( Object key, Provider<T> unscopedProvider ) {
        if ( managedObjects != null && managedObjects.containsKey( key ) ) {
            return (T) managedObjects.get( key );
        } else {
            T value = unscopedProvider.get();
            registerObject( key, value );
            return value;
        }
    }

    /**
     * Validate that an object is defined within a scope.
     */
    public synchronized boolean contains( Object key ) {
        if ( managedObjects != null && managedObjects.containsKey( key ) ) {
            return managedObjects.containsKey( key );
        } else {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws org.dbrain.yaw.scope.DisposeException In case one or more managed Disposable has thrown an exception.
     */
    @Override
    public synchronized void close() throws DisposeException {
        if ( managedObjects != null ) {
            List<Throwable> exceptions = null;

            // Dispose of all managed objects
            for ( Object managedObject : managedObjects.values() ) {
                if ( managedObject instanceof AutoCloseable ) {
                    AutoCloseable d = (AutoCloseable) managedObject;
                    try {
                        d.close();
                    } catch ( Throwable e ) {
                        if ( exceptions == null ) {
                            exceptions = new LinkedList<>();
                        }
                        exceptions.add( e );
                    }
                }
            }

            // Forget the list
            managedObjects = null;

            // Throw an exception if one occured.
            if ( exceptions != null ) {
                throw new DisposeException( exceptions.toArray( new Throwable[exceptions.size()] ) );
            }
        }
    }
}
