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

import org.dbrain.binder.http.conf.ConnectorConf;
import org.dbrain.binder.http.conf.HttpConnectorConf;
import org.dbrain.binder.http.conf.HttpServerConf;
import org.dbrain.binder.http.conf.ServletContextConf;
import org.dbrain.binder.system.app.QualifiedComponent;

public abstract class AbstractHttpServerComponent<T extends AbstractHttpServerComponent> extends QualifiedComponent<T> {

    private HttpServerConf building = new HttpServerConf();

    public T listen( Integer port ) {
        return listen( HttpConnectorBuilder.of( port ) );
    }

    public T listen( ConnectorConf config ) {
        if ( config != null ) {
            building.getEndPoints().add( config );
        }
        return self();
    }

    public T serve( ServletContextConf servletContext ) {
        if ( servletContext != null ) {
            building.getServletContexts().add( servletContext );
        }
        return self();
    }

    protected HttpServerConf getHttpServerConfig() {
        if ( building.getEndPoints().size() == 0 ) {
            building.getEndPoints().add( new HttpConnectorConf() );
        }
        return building;
    }

}
