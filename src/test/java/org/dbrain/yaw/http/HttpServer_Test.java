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

import org.dbrain.yaw.http.sample.SampleServlet;
import org.dbrain.yaw.http.server.HttpServerBuilder;
import org.dbrain.yaw.http.server.ServletContextBuilder;
import org.dbrain.yaw.http.server.defs.ServletDef;
import org.dbrain.yaw.system.jetty.JettyServerFactory;
import org.eclipse.jetty.server.Server;
import org.junit.Test;

/**
 * Created by epoitras on 10/09/14.
 */
public class HttpServer_Test {

    @Test
    public void testHttpServer() throws Exception {
        ServletContextBuilder servletContext = new ServletContextBuilder( "/" );
        servletContext.serve( ServletDef.of( "/*", new SampleServlet() ) );

        Server server = new HttpServerBuilder() //
                .listen( 40001 ) //
                .serve( servletContext.build() ) //
                .build( new JettyServerFactory() );

        server.start();

        //        Object content = new URL( "http://localhost:8080/" ).openConnection();
        //        Assert.assertEquals( SampleServlet.CONTENT, content.toString() );
    }
}
