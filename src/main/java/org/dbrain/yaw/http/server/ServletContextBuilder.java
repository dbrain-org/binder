package org.dbrain.yaw.http.server;

import org.dbrain.yaw.http.server.config.ServletAppSecurityDef;
import org.dbrain.yaw.http.server.config.ServletContextConfig;
import org.dbrain.yaw.http.server.config.ServletConfig;
import org.dbrain.yaw.http.server.config.WebSocketConfig;
import org.dbrain.yaw.http.server.config.ServletFilterConfig;

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

    protected List<ServletConfig> servlets = new ArrayList<>();

    protected List<WebSocketConfig> webSockets = new ArrayList<>();

    protected List<ServletFilterConfig> filters = new ArrayList<>();

    protected ServletAppSecurityDef security;

    public ServletContextBuilder( String contextPath ) {
        contextPath( contextPath );
    }

    /**
     * Set the serve context path.
     *
     * @return
     */
    public ServletContextBuilder contextPath( String contextPath ) {
        this.contextPath = contextPath;
        return this;
    }

    public ServletContextBuilder serve( ServletConfig servletDef ) {
        if ( servletDef != null ) {
            servlets.add( servletDef );
        }
        return this;
    }

    public ServletContextBuilder serve( WebSocketConfig wsd ) {
        if ( wsd != null ) {
            webSockets.add( wsd );
        }
        return this;
    }

    public ServletContextBuilder filter( Filter filter, String pathSpec ) {
        filters.add( new ServletFilterConfig( filter, pathSpec ) );
        return this;
    }

    public void security( ServletAppSecurityDef security ) {
        this.security = security;
    }

    public ServletContextConfig build() {
        return new ServletContextConfig( contextPath, servlets, filters, webSockets, security );
    }

}
