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

package org.dbrain.yaw.http;

import org.dbrain.yaw.app.App;
import org.dbrain.yaw.http.artifacts.SampleServlet;
import org.dbrain.yaw.http.server.HttpsConnectorBuilder;
import org.dbrain.yaw.http.server.ServletContextBuilder;
import org.dbrain.yaw.http.server.defs.HttpsConnectorDef;
import org.dbrain.yaw.http.server.defs.ServletDef;
import org.dbrain.yaw.system.app.AppImpl;
import org.eclipse.jetty.server.Server;
import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import static org.junit.Assert.assertNotNull;

public class JettyHttpsServer_Test {

    private App buildApp() throws Exception {
        App app = new AppImpl();

        app.configure( ( config ) -> {

            ServletContextBuilder servletContext = new ServletContextBuilder( "/" );
            servletContext.serve( ServletDef.of( "/*", new SampleServlet() ) );

            HttpsConnectorDef https = HttpsConnectorBuilder.from( "https://localhost:8443" )
                                                           .keystore( getClass().getResource( "/keystore.jks" ).toURI(),
                                                                      "password",
                                                                      "password" )
                                                           .build();
            config.addFeature( JettyHttpServer.class ) //
                    .listen( https ) //
                    .serve( servletContext.build() ) //
                    .complete();

        } );

        return app;
    }


    @Test
    public void testHttpsServer() throws Exception {

        try ( App app = buildApp() ) {

            Server server = app.getInstance( Server.class );

            assertNotNull( server );
            server.start();

            Client httpClient = ClientBuilder.newClient();
            WebTarget t = httpClient.target( "https://localhost:8443/" );
            String s = t.request().get(String.class);

            Assert.assertEquals( "Hello from sample servlet.", s);
        }

    }


}