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

import org.dbrain.binder.system.directory.QualifiersBuilderImpl;

import java.lang.annotation.Annotation;
import java.util.Collection;

/**
 * Describe qualifiers.
 */
public interface Qualifiers extends Iterable<Annotation> {

    static Builder from( String name ) {
        return new QualifiersBuilderImpl().named( name );
    }

    static Builder from( Class<? extends Annotation> quality ) {
        return new QualifiersBuilderImpl().qualifiedBy( quality );
    }

    static Builder from( Annotation quality ) {
        return new QualifiersBuilderImpl().qualifiedBy( quality );
    }

    /**
     * @return The quantity of qualifiers.
     */
    int size();

    /**
     * @return The qualifiers into the array or in a new array if the one provided is not large enough.
     */
    <T> T[] toArray( T[] a );

    /**
     * Builder for service key.
     */
    interface Builder {

        Builder qualifiedBy( Annotation quality );

        Builder qualifiedBy( Class<? extends Annotation> quality );

        Builder qualifiedBy( Iterable<Annotation> quality );

        Builder named( String name );

        Qualifiers build();

    }
}
