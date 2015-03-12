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

package org.dbrain.yaw.scope;

import org.dbrain.yaw.app.App;
import org.dbrain.yaw.system.app.AppImpl;
import org.dbrain.yaw.system.scope.RequestScopeContext;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class RequestScopeContext_Test {

    @RequestScoped
    public static class TestService {}

    private App buildApp() {
        App app = new AppImpl();
        app.configure( ( c ) -> c.defineService( TestService.class )
                                 .servicing( TestService.class )
                                 .in( RequestScoped.class )
                                 .complete() );
        return app;
    }

    @Test
    public void testSimpleRequestScopedService() throws Exception {

        try ( App app = buildApp() ) {

            RequestScopeContext rsc = app.getInstance( RequestScopeContext.class );
            rsc.enter();
            TestService s1 = app.getInstance( TestService.class );
            TestService s2 = app.getInstance( TestService.class );
            rsc.leave();

            rsc.enter();
            TestService s3 = app.getInstance( TestService.class );
            TestService s4 = app.getInstance( TestService.class );
            rsc.leave();

            assertEquals( s1, s2 );
            assertEquals( s3, s4 );
            assertNotEquals( s1, s3 );
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

    @Test
    public void testLeaveContextOpenOnShutdown() throws Exception {
        try ( App app = buildApp() ) {
            RequestScopeContext rsc = app.getInstance( RequestScopeContext.class );
            rsc.enter();
        }
    }

}