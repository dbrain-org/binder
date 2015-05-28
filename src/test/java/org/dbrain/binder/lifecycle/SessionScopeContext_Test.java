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

package org.dbrain.binder.lifecycle;

import org.dbrain.binder.App;
import org.dbrain.binder.system.lifecycle.ContextRegistry;
import org.dbrain.binder.system.scope.SessionScopeContext;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class SessionScopeContext_Test {

    @SessionScoped
    public static class TestService {}

    private App buildApp() {
        App app = App.create();
        app.configure( ( c ) -> c.bind( TestService.class )
                                 .to( TestService.class )
                                 .in( SessionScoped.class )
                                 .complete() );
        return app;
    }

    @Test
    public void testSimpleRequestScopedService() throws Exception {


        try ( App app = buildApp(); ContextRegistry session1 = new ContextRegistry(); ContextRegistry session2 = new ContextRegistry() ) {

            SessionScopeContext rsc = app.getInstance( SessionScopeContext.class );
            rsc.enter( () -> session1 );
            TestService s1 = app.getInstance( TestService.class );
            TestService s2 = app.getInstance( TestService.class );
            rsc.leave();

            rsc.enter( () -> session2 );
            TestService s3 = app.getInstance( TestService.class );
            TestService s4 = app.getInstance( TestService.class );
            rsc.leave();

            rsc.enter( () -> session1 );
            TestService s5 = app.getInstance( TestService.class );
            TestService s6 = app.getInstance( TestService.class );
            rsc.leave();


            assertEquals( s1, s2 );
            assertEquals( s3, s4 );
            assertEquals( s5, s6 );
            assertNotEquals( s1, s3 );
            assertEquals( s1, s5 );
        }
    }

    @Test( expected = Exception.class )
    public void testNoContext() throws Exception {

        try ( App app = buildApp() ) {
            try {
                app.getInstance( TestService.class );
            } catch ( Exception e ) {
                System.out.println(
                        "--------------------------------------------------------------------------------------------------------------" );
                System.out.println( "Exception thrown when there is no scope:" );
                System.out.println(
                        "--------------------------------------------------------------------------------------------------------------" );
                e.printStackTrace();
                System.out.println(
                        "--------------------------------------------------------------------------------------------------------------" );
                throw e;
            }
        }
    }

}