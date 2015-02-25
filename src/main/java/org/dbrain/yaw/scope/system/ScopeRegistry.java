package org.dbrain.yaw.scope.system;

import com.google.inject.Key;
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
 *
 */
public class ScopeRegistry implements AutoCloseable {

    private Map<Key<?>, Object> managedObjects;

    public <T> T get( Key<T> key, Provider<T> unscopedProvider ) {
        if ( managedObjects != null && managedObjects.containsKey( key ) ) {
            return (T)managedObjects.get( key );
        } else {
            if (managedObjects == null ) {
                managedObjects = new HashMap<>();
            }
            T value = unscopedProvider.get();
            managedObjects.put( key, value );
            return value;
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws epic.scopes.DisposeException In case one or more managed Disposable has thrown an exception.
     */
    @Override
    public void close() throws DisposeException {
        if (managedObjects != null) {
            List<Throwable> exceptions = null;

            // Dispose of all managed objects
            for (Object managedObject : managedObjects.values()) {
                if (managedObject instanceof AutoCloseable) {
                    AutoCloseable d = (AutoCloseable) managedObject;
                    try {
                        d.close();
                    } catch (Throwable e) {
                        if (exceptions == null) {
                            exceptions = new LinkedList<>();
                        }
                        exceptions.add(e);
                    }
                }
            }

            // Forget the list
            managedObjects = null;

            // Throw an exception if one occured.
            if (exceptions != null) {
                throw new DisposeException(exceptions.toArray(new Throwable[exceptions.size()]));
            }
        }
    }
}
