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

package org.dbrain.binder.app;

import org.junit.Test;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class Configuration_Test {

    static class TestService1 {}

    @Test
    public void testPerLookup() throws Exception {
        try ( App app = App.create() ) {

            app.configure( session -> session.bind( TestService1.class ).to( TestService1.class ) );

            TestService1 ts1_1 = app.getInstance( TestService1.class );
            TestService1 ts1_2 = app.getInstance( TestService1.class );

            assertNotEquals( ts1_1, ts1_2 );
        }
    }

    @Test
    public void testSingletonScoped() throws Exception {
        try ( App app = App.create() ) {
            app.configure( session -> session.bind( TestService1.class ) //
                    .to( TestService1.class ) //
                    .in( Singleton.class ) );

            TestService1 ts1_1 = app.getInstance( TestService1.class );
            TestService1 ts1_2 = app.getInstance( TestService1.class );

            assertEquals( ts1_1, ts1_2 );
        }
    }

    @Test
    public void testServeInstance() throws Exception {
        TestService1 instance = new TestService1();
        try ( App app = App.create() ) {
            app.configure( session -> session.bind( instance ).to( TestService1.class ) );
            TestService1 ts1_1 = app.getInstance( TestService1.class );
            TestService1 ts1_2 = app.getInstance( TestService1.class );
            assertEquals( instance, ts1_1 );
            assertEquals( instance, ts1_2 );
        }
    }

    @Test
    public void testServeProvider() throws Exception {

        Stack<TestService1> sources = new Stack<>();
        sources.add( new TestService1() );

        Set<TestService1> disposed = new HashSet<>();

        Set<TestService1> comparable = new HashSet<>( sources );
        try ( App app = App.create() ) {
            app.configure( binder -> binder.bind( TestService1.class ) //
                    .providedBy( () -> sources.pop() ) //
                    .disposedBy( ( e ) -> disposed.add( e ) ) //
                    .to( TestService1.class ) //
                    .named( "test" ) //
                    .in( Singleton.class ) );

            Set<TestService1> provided = new HashSet<>();
            provided.add( app.getInstance( TestService1.class ) );
            provided.add( app.getInstance( TestService1.class ) );

            assertEquals( comparable, provided );
        }
        assertEquals( comparable, disposed );

    }

    @Test
    public void testDefaultConstructorWithDisposer() throws Exception {

        Set<TestService1> provided = new HashSet<>();
        Set<TestService1> disposed = new HashSet<>();
        try ( App app = App.create() ) {
            app.configure( session -> session.bind( TestService1.class ) //
                    .disposedBy( ( e ) -> disposed.add( e ) ) //
                    .to( TestService1.class ) //
                    .named( "test" ) //
                    .in( Singleton.class ) );
            provided.add( app.getInstance( TestService1.class ) );
            provided.add( app.getInstance( TestService1.class ) );

        }
        assertEquals( provided, disposed );

    }

    @Test
    public void testBindingModuleClasses() throws Exception {

        try ( App app = App.create() ) {
            app.configure( Module1.class, Module2.class );
            assertEquals( app.getInstance( String.class, "String1" ), "test" );
            assertEquals( app.getInstance( String.class, "String2" ), "test+test2" );
        }



    }

    public static class Module1 implements Module {

        @Override
        public void configure( Binder binder ) throws Exception {
            binder.bind( String.class ).named( "String1" ).toInstance( "test" ).in( Singleton.class );
        }
    }

    public static class Module2 implements Module {

        @Inject
        @Named( "String1" )
        String name;

        @Override
        public void configure( Binder binder ) throws Exception {
            binder.bind( String.class ).named( "String2" ).toInstance( name + "+test2" ).in( Singleton.class );
        }
    }

}