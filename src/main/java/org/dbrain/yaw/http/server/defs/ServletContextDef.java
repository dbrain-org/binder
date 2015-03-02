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

package org.dbrain.yaw.http.server.defs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: epoitras
 * Date: 15/07/13
 * Time: 10:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class ServletContextDef {

    private final String contextPath;

    private final List<ServletDef> servlets;

    private final List<ServletFilterDef> filters;

    private final List<WebSocketDef> webSockets;

    private final ServletAppSecurityDef security;

    public ServletContextDef( String contextPath,
                              List<ServletDef> servlets,
                              List<ServletFilterDef> filters,
                              List<WebSocketDef> webSockets,
                              ServletAppSecurityDef security ) {
        this.contextPath = contextPath;
        this.webSockets = webSockets;
        this.servlets = Collections.unmodifiableList( new ArrayList<>( servlets ) );
        this.filters = Collections.unmodifiableList( new ArrayList<>( filters ) );
        this.security = security;
    }

    public String getContextPath() {
        return contextPath;
    }

    public List<ServletDef> getServlets() {
        return servlets;
    }

    public List<ServletFilterDef> getFilters() {
        return filters;
    }

    public List<WebSocketDef> getWebSockets() {
        return webSockets;
    }

    public ServletAppSecurityDef getSecurity() {
        return security;
    }
}
