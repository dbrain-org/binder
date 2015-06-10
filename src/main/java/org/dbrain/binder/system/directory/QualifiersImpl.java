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

package org.dbrain.binder.system.directory;

import org.dbrain.binder.directory.Qualifiers;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Implementations of the Qualifiers interface.
 */
public class QualifiersImpl implements Qualifiers {

    private final Set<Annotation> delegate;

    public QualifiersImpl( Collection<Annotation> delegate ) {
        this.delegate = Collections.unmodifiableSet( new HashSet<>( delegate ) );
    }

    @Override
    public Iterator<Annotation> iterator() {
        return delegate.iterator();
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public Annotation[] toArray() {
        return delegate.toArray( new Annotation[size()] );
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( o == null || !( o instanceof Qualifiers ) ) return false;

        QualifiersImpl that = (QualifiersImpl) o;

        if ( !delegate.equals( that.delegate ) ) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }
}
