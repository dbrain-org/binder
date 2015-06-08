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

package org.dbrain.binder.http;

import org.dbrain.binder.app.App;
import org.dbrain.binder.http.artifacts.CookieClientFilter;
import org.dbrain.binder.http.artifacts.resources.GuidService;
import org.dbrain.binder.http.artifacts.resources.SampleResource;
import org.dbrain.binder.http.conf.ServletConf;
import org.dbrain.binder.lifecycle.RequestScoped;
import org.dbrain.binder.lifecycle.SessionScoped;
import org.eclipse.jetty.server.Server;
import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import static org.junit.Assert.assertNotNull;

public class HttpServer_scope_Test {

    private App buildApp() {
        App app = App.create();

        app.configure( ( binder ) -> {

            WebApplicationBuilder webApp = new WebApplicationBuilder();
            webApp.add( new SampleResource() );

            ServletContextBuilder servletContext = new ServletContextBuilder( "/" );
            servletContext.serve( ServletConf.of( "/*", webApp.build() ) );

            binder.bindComponent( JettyServerComponent.class ) //
                    .listen( 40001 )              //
                    .serve( servletContext.build() );

            binder.bindService( GuidService.class )
                  .to( GuidService.class )
                  .in( RequestScoped.class )
                  .named( "request" )
                  .useProxy();

            binder.bindService( GuidService.class )
                  .to( GuidService.class )
                  .in( SessionScoped.class )
                  .named( "session" )
                  .useProxy();

        } );

        return app;
    }

    @Test
    public void testHttpServer() throws Exception {

        try ( App app = buildApp() ) {

            Server server = app.getInstance( Server.class );

            assertNotNull( server );
            server.start();

            Client httpClient = ClientBuilder.newClient();
            WebTarget t = httpClient.target( "http://localhost:40001/" );
            String s = t.request().get( String.class );

            Assert.assertEquals( "Hello from application " + app.getName(), s );
        }

    }

    @Test
    public void testRequestScope() throws Exception {

        try ( App app = buildApp() ) {

            Server server = app.getInstance( Server.class );

            assertNotNull( server );
            server.start();

            Client httpClient = ClientBuilder.newClient();
            WebTarget t = httpClient.target( "http://localhost:40001/requestUid" );
            String r1 = t.request().get( String.class );
            String r2 = t.request().get( String.class );

            Assert.assertNotEquals( r1, r2 );
        }

    }

    @Test
    public void testSessionScope() throws Exception {

        try ( App app = buildApp() ) {

            Server server = app.getInstance( Server.class );

            assertNotNull( server );
            server.start();


            Client httpClient = ClientBuilder.newClient();

            httpClient.register( new CookieClientFilter() );

            WebTarget t = httpClient.target( "http://localhost:40001/sessionUid" );
            String r1 = t.request().get( String.class );
            String r2 = t.request().get( String.class );

            Assert.assertEquals( r1, r2 );
        }

    }


}