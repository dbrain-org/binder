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
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: epoitras
 * Date: 15/07/13
 * Time: 10:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class ServletContextConf {

    private final Boolean session;

    private final String contextPath;

    private final List<ServletConf> servlets;

    private final List<ServletFilterConf> filters;

    private final List<WebSocketServerConf> webSockets;

    private final ServletContextSecurityConf security;

    public ServletContextConf( String contextPath,
                               List<ServletConf> servlets,
                               List<ServletFilterConf> filters,
                               List<WebSocketServerConf> webSockets, Boolean session, ServletContextSecurityConf security ) {
        this.contextPath = contextPath;
        this.webSockets = webSockets;
        this.session = session;
        this.servlets = Collections.unmodifiableList( new ArrayList<>( servlets ) );
        this.filters = Collections.unmodifiableList( new ArrayList<>( filters ) );
        this.security = security;
    }

    public String getContextPath() {
        return contextPath;
    }

    public List<ServletConf> getServlets() {
        return servlets;
    }

    public List<ServletFilterConf> getFilters() {
        return filters;
    }

    public List<WebSocketServerConf> getWebSockets() {
        return webSockets;
    }

    public Boolean getSession() {
        return session;
    }

    public ServletContextSecurityConf getSecurity() {
        return security;
    }
}
