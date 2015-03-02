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

package org.dbrain.yaw.http.server;

import org.dbrain.yaw.http.server.defs.ConnectorDef;
import org.dbrain.yaw.http.server.defs.HttpConnectorDef;
import org.dbrain.yaw.http.server.defs.HttpServerDef;
import org.dbrain.yaw.http.server.defs.ServletContextDef;
import org.dbrain.yaw.http.server.factories.HttpServerFactory;

public class HttpServerBuilder {

    private HttpServerDef building = new HttpServerDef();

    public HttpServerBuilder() {
    }

    public HttpServerBuilder listen( Integer port ) {
        return listen( HttpConnectorBuilder.of( port ) );
    }

    public HttpServerBuilder listen( ConnectorDef config ) {
        if ( config != null ) {
            building.getEndPoints().add( config );
        }
        return this;
    }

    public HttpServerBuilder serve( ServletContextDef servletContext ) {
        if ( servletContext != null ) {
            building.getServletContexts().add( servletContext );
        }
        return this;
    }

    public HttpServerDef buildConfig() {
        if ( building.getEndPoints().size() == 0 ) {
            building.getEndPoints().add( new HttpConnectorDef() );
        }
        return building;
    }

    public <T> T build( HttpServerFactory<T> factory ) {
        return factory.build( buildConfig() );
    }


}
