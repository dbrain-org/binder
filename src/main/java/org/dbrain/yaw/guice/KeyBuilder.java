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

package org.dbrain.yaw.guice;

import com.google.inject.Key;
import com.google.inject.name.Names;

import java.lang.annotation.Annotation;

/**
 * Created by epoitras on 3/4/15.
 */
public class KeyBuilder<T> {

    private final Class<T>          serviceClass;
    private       Class<Annotation> qualifier;
    private       String            name;

    public static <U> KeyBuilder<U> from( Class<U> serviceClass ) {
        return new KeyBuilder<>( serviceClass );
    }

    public static <U> Key<U> of( Class<U> serviceClass, Class<Annotation> qualifier ) {
        return from( serviceClass ).qualified( qualifier ).build();
    }

    public static <U> Key<U> of( Class<U> serviceClass, String named ) {
        return from( serviceClass ).named( named ).build();
    }

    private KeyBuilder( Class<T> serviceClass ) {
        this.serviceClass = serviceClass;
    }

    public KeyBuilder<T> qualified( Class<Annotation> qualified ) {
        this.qualifier = qualified;
        return this;
    }

    public KeyBuilder<T> named( String named ) {
        this.name = named;
        return this;
    }

    public Key<T> build() {
        if ( name != null && qualifier != null ) {
            throw new IllegalStateException( "Cannot have a name and a qualifier at the same time." );
        }
        if ( name != null ) {
            return Key.get( serviceClass, Names.named( name ) );
        } else if ( qualifier != null ) {
            return Key.get( serviceClass, qualifier );
        } else {
            return Key.get( serviceClass );
        }
    }

}
