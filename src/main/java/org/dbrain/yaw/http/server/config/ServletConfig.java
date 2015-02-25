package org.dbrain.yaw.http.server.config;

import javax.servlet.Servlet;

/**
 * Created with IntelliJ IDEA.
 * User: epoitras
 * Date: 15/07/13
 * Time: 10:25 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ServletConfig {

    public static ServletConfig of( String pathSpec, Servlet instance ) {
        return new InstanceServletConfig( pathSpec, instance );
    }

    public void accept( Visitor v );

    public class InstanceServletConfig implements ServletConfig {

        private final Servlet instance;

        private final String pathSpec;

        public InstanceServletConfig( String pathSpec, Servlet instance ) {
            this.instance = instance;
            this.pathSpec = pathSpec;
        }

        public String getPathSpec() {
            return pathSpec;
        }

        public Servlet getInstance() {
            return instance;
        }

        @Override
        public void accept( Visitor v ) {
            v.visit( this );
        }
    }

    public interface Visitor {
        void visit( InstanceServletConfig servletDef );
    }
}
