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
import org.dbrain.binder.http.artifacts.SampleServlet;
import org.dbrain.binder.http.conf.ServletConf;
import org.eclipse.jetty.server.Server;
import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import static org.junit.Assert.assertNotNull;

public class JettyHttpServer_Test {

    private App buildApp() throws Exception {
        App app = App.create();

        app.configure( binder -> {

            ServletContextBuilder servletContext = new ServletContextBuilder( "/" );
            servletContext.serve( ServletConf.of( "/*", new SampleServlet() ) );

            binder.bindModule( JettyServerModule.class ) //
                  .listen( 40001 )              //
                  .serve( servletContext.build() );

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

            Assert.assertEquals( "Hello from sample servlet.", s );
        }

    }


}