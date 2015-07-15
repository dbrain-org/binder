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

package org.dbrain.binder.system.app;

import org.dbrain.binder.app.Component;
import org.dbrain.binder.directory.Qualifiers;

import java.lang.annotation.Annotation;

/**
 * Basic abstract component interfaces that defines contract to qualify a component being configured.
 */
public abstract class QualifiedComponent<T extends QualifiedComponent> implements Component {

    private Qualifiers.Builder qualifiers = Qualifiers.newBuilder();

    abstract protected T self();

    protected Qualifiers buildQualifiers() {
        return qualifiers.build();
    }

    public T qualifiedWith( Annotation a ) {
        qualifiers.qualifiedWith( a );
        return self();
    }

    public T qualifiedWith( Class<Annotation> annotationClass ) {
        qualifiers.qualifiedWith( annotationClass );
        return self();
    }

    public T named( String name ) {
        qualifiers.named( name );
        return self();
    }


}
