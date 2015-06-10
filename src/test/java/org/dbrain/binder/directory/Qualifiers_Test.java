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

import org.dbrain.binder.directory.artifacts.SomeQualifier;
import org.dbrain.binder.system.util.AnnotationBuilder;
import org.junit.Test;

import javax.inject.Named;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Created by epoitras on 6/9/15.
 */
public class Qualifiers_Test {

    @Test
    public void testBasicQualifier() throws Exception {
        assertEquals( Qualifiers.from( "test" ).build(), Qualifiers.from( "test" ).build() );
        assertEquals( Qualifiers.from( "test" ).build(), Qualifiers.from( AnnotationBuilder.of( Named.class, "test" ) ).build() );
        assertEquals( 1, Qualifiers.from( "test" ).build().size() );
        assertEquals( Named.class, Qualifiers.from( "test" ).build().iterator().next().annotationType() );
        assertEquals( SomeQualifier.class,
                      Qualifiers.from( SomeQualifier.class ).build().iterator().next().annotationType() );
    }

    @Test
    public void testNotEqualsQualifiers() throws Exception {
        assertNotEquals( Qualifiers.from( "test1" ).build(), Qualifiers.from( "test2" ).build() );
        assertNotEquals( Qualifiers.from( "test1" ).build(), Qualifiers.from( SomeQualifier.class ).build() );
    }

    @Test
    public void testMultipleQualifier() throws Exception {
        assertEquals( Qualifiers.from( "test" ).qualifiedWith( SomeQualifier.class ).build(), Qualifiers.from( SomeQualifier.class ).named(
                "test" ).build() );
        assertEquals( 2, Qualifiers.from( "test" ).qualifiedWith( SomeQualifier.class ).build().size() );
    }

    @Test
    public void testNotEqualsMultipleQualifier() throws Exception {
        assertNotEquals( Qualifiers.from( "test1" ).qualifiedWith( SomeQualifier.class ).build(),
                         Qualifiers.from( SomeQualifier.class ).named( "test2" ).build() );
    }

}