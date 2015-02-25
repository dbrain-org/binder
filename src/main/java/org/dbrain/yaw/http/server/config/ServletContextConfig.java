package org.dbrain.yaw.http.server.config;

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
public class ServletContextConfig {

    private final String contextPath;

    private final List<ServletConfig> servlets;

    private final List<ServletFilterConfig> filters;

    private final List<WebSocketConfig> webSockets;

    private final ServletAppSecurityDef security;

    public ServletContextConfig( String contextPath,
                                 List<ServletConfig> servlets,
                                 List<ServletFilterConfig> filters,
                                 List<WebSocketConfig> webSockets,
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

    public List<ServletConfig> getServlets() {
        return servlets;
    }

    public List<ServletFilterConfig> getFilters() {
        return filters;
    }

    public List<WebSocketConfig> getWebSockets() {
        return webSockets;
    }

    public ServletAppSecurityDef getSecurity() {
        return security;
    }
}
