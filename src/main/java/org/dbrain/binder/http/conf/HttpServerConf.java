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

package org.dbrain.binder.http.conf;

import java.util.ArrayList;
import java.util.List;

/**
 * Definition of an Http Server.
 */
public class HttpServerConf {

    private List<ConnectorConf> endPoints = new ArrayList<>( 1 );

    private List<ServletContextConf> servletContexts = new ArrayList<>( 1 );

    public List<ConnectorConf> getEndPoints() {
        return endPoints;
    }

    public void setEndPoints( List<ConnectorConf> endPoints ) {
        this.endPoints = endPoints;
    }

    public List<ServletContextConf> getServletContexts() {
        return servletContexts;
    }

    public void setServletContexts( List<ServletContextConf> servletContexts ) {
        this.servletContexts = servletContexts;
    }
}
