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

import org.dbrain.binder.app.App;
import org.dbrain.binder.directory.artifacts.InjectedBean;
import org.dbrain.binder.directory.artifacts.SimpleService;
import org.dbrain.binder.directory.artifacts.SomeQualifier;
import org.junit.Test;

import javax.inject.Singleton;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Created by epoitras on 3/4/15.
 */
public class ServiceDirectory_Test {

    private App buildApp() {

        App app = App.create();
        app.configure( ( binder ) -> {

            binder.bind( SimpleService.class ) //
                    .named( "toto" ) //
                    .to( SimpleService.class );

            binder.bind( SimpleService.class ) //
                    .named( "toto" ) //
                    .qualifiedBy( SomeQualifier.class ) //
                    .to( SimpleService.class ) //
                    .in( Singleton.class );

            binder.bind( SimpleService.class ) //
                    .to( SimpleService.class ) //
                    .in( Singleton.class );

            binder.bind( InjectedBean.class ) //
                    .to( InjectedBean.class );

        } );

        return app;
    }


    @Test
    public void testLocate() throws Exception {

        try ( App app = buildApp() ) {

            InjectedBean s1i1 = app.locate( InjectedBean.class );
            SimpleService s2i1 = app.locate( SimpleService.class, SomeQualifier.class );
            SimpleService s2i2 = app.locate( SimpleService.class, SomeQualifier.class );
            SimpleService s2i3 = app.locate( ServiceKey.from( SimpleService.class ) //
                                                     .named( "toto" ) //
                                                     .qualifiedBy( SomeQualifier.class ) //
                                                     .build() //
                                           );
            SimpleService s2i4 = app.getInstance( SimpleService.class, Qualifiers.from( SomeQualifier.class).build() );

            // Got no string registered
            String s3 = app.locate( String.class );

            assertNotNull( s1i1 );
            assertNotNull( s2i1 );
            assertNotNull( s2i2 );
            assertNotNull( s2i3 );
            assertEquals( s2i1, s2i2 );
            assertEquals( s2i1, s2i3 );
            assertEquals( s2i1, s2i4 );
            assertNull( s3 );

        }

    }

    @Test
    public void testGetInstance() throws Exception {

        try ( App app = buildApp() ) {

            InjectedBean s1i1 = app.getInstance( InjectedBean.class );
            SimpleService s2i1 = app.getInstance( SimpleService.class, SomeQualifier.class );
            SimpleService s2i2 = app.getInstance( SimpleService.class, SomeQualifier.class );
            SimpleService s2i3 = app.getInstance( ServiceKey.from( SimpleService.class ) //
                                                          .named( "toto" ) //
                                                          .qualifiedBy( SomeQualifier.class ) //
                                                          .build() //
                                                );
            SimpleService s2i4 = app.getInstance( SimpleService.class, Qualifiers.from( SomeQualifier.class).build() );

            assertNotNull( s1i1 );
            assertNotNull( s2i1 );
            assertNotNull( s2i2 );
            assertNotNull( s2i3 );
            assertEquals( s2i1, s2i2 );
            assertEquals( s2i1, s2i3 );
            assertEquals( s2i1, s2i4 );

        }

    }

    @Test( expected = NullPointerException.class )
    public void testGetInstance_fail1() throws Exception {

        try ( App app = buildApp() ) {

            app.getInstance( String.class );

        }

    }

    @Test( expected = NullPointerException.class )
    public void testGetInstance_fail2() throws Exception {

        try ( App app = buildApp() ) {

            app.getInstance( String.class, SomeQualifier.class );

        }

    }

    @Test( expected = NullPointerException.class )
    public void testGetInstance_fail3() throws Exception {

        try ( App app = buildApp() ) {

            app.getInstance( String.class, "toto" );

        }

    }

    @Test( expected = NullPointerException.class )
    public void testGetInstance_fail4() throws Exception {

        try ( App app = buildApp() ) {

            app.getInstance( ServiceKey.of( String.class ) );

        }

    }

    @Test
    public void testListServices() throws Exception {

        try ( App app = buildApp() ) {

            List<SimpleService> l1 = app.listServices( SimpleService.class, SomeQualifier.class );
            assertEquals( 1, l1.size() );

            List<SimpleService> l2 = app.listServices( SimpleService.class, "toto" );
            assertEquals( 2, l2.size() );

            List<SimpleService> l3 = app.listServices( SimpleService.class );
            assertEquals( 3, l3.size() );

            List<SimpleService> l4 = app.listServices( SimpleService.class, Qualifiers.from( SomeQualifier.class ).build() );
            assertEquals( 1, l4.size() );

            List<SimpleService> l5 = app.listServices( SimpleService.class, Qualifiers.from( "toto" ).build() );
            assertEquals( 2, l5.size() );

            List<SimpleService> l6 = app.listServices( SimpleService.class, Qualifiers.newBuilder().build() );
            assertEquals( 3, l6.size() );

        }
    }


}
