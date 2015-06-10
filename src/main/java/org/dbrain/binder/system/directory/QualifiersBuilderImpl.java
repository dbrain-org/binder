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
import org.dbrain.binder.system.util.AnnotationBuilder;

import javax.inject.Named;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by epoitras on 3/15/15.
 */
public class QualifiersBuilderImpl implements Qualifiers.Builder {

    private final Set<Annotation> qualifiers = new HashSet<>();

    @Override
    public QualifiersBuilderImpl qualifiedWith( Annotation quality ) {
        if ( quality != null ) {
            qualifiers.add( quality );
        }
        return this;
    }

    @Override
    public QualifiersBuilderImpl qualifiedWith( Class<? extends Annotation> quality ) {
        if ( quality != null ) {
            return qualifiedWith( AnnotationBuilder.of( quality ) );
        } else {
            return this;
        }
    }

    @Override
    public QualifiersBuilderImpl qualifiedWith( Iterable<Annotation> quality ) {
        if ( quality != null ) {
            for ( Annotation a : quality ) {
                qualifiedWith( a );
            }
        }
        return this;
    }

    @Override
    public QualifiersBuilderImpl named( String name ) {
        if ( name != null ) {
            return qualifiedWith( AnnotationBuilder.of( Named.class, name ) );
        } else {
            return this;
        }
    }

    @Override
    public Qualifiers build() {
        return new QualifiersImpl( qualifiers );
    }

}
