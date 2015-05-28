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

package org.dbrain.binder.http.server;

import org.dbrain.binder.http.server.defs.ServletAppSecurityDef;
import org.dbrain.binder.http.server.defs.ServletContextDef;
import org.dbrain.binder.http.server.defs.ServletDef;
import org.dbrain.binder.http.server.defs.ServletFilterDef;
import org.dbrain.binder.http.server.defs.WebSocketDef;

import javax.servlet.Filter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: epoitras
 * Date: 15/07/13
 * Time: 9:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class ServletContextBuilder {

    protected String contextPath;

    protected List<ServletDef> servlets = new ArrayList<>();

    protected List<WebSocketDef> webSockets = new ArrayList<>();

    protected List<ServletFilterDef> filters = new ArrayList<>();

    protected ServletAppSecurityDef security;

    public ServletContextBuilder( String contextPath ) {
        contextPath( contextPath );
    }

    /**
     * Set the serve context path.
     */
    public ServletContextBuilder contextPath( String contextPath ) {
        this.contextPath = contextPath;
        return this;
    }

    public ServletContextBuilder serve( ServletDef servletDef ) {
        if ( servletDef != null ) {
            servlets.add( servletDef );
        }
        return this;
    }

    public ServletContextBuilder serve( WebSocketDef wsd ) {
        if ( wsd != null ) {
            webSockets.add( wsd );
        }
        return this;
    }

    public ServletContextBuilder filter( String pathSpec, Filter filter ) {
        filters.add( ServletFilterDef.of( pathSpec, filter ) );
        return this;
    }

    public void security( ServletAppSecurityDef security ) {
        this.security = security;
    }

    public ServletContextDef build() {
        return new ServletContextDef( contextPath, servlets, filters, webSockets, security );
    }

}
