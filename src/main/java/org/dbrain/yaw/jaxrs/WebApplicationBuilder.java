package org.dbrain.yaw.jaxrs;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

import javax.servlet.Servlet;
import java.util.ArrayList;
import java.util.List;

/**
 * Builder for simple web applications using jax-rs / jersey.
 */
public class WebApplicationBuilder {

    private List<Object>   resources = new ArrayList<>();


    public WebApplicationBuilder add( Object resource ) {
        resources.add( resource );
        return this;
    }

    public Servlet build() {
        ResourceConfig rc = new ResourceConfig();

        for ( Object o: resources ) {
            if ( o instanceof Class ) {
                rc.register( (Class) o );
            } else {
                rc.register( o );
            }
        }
        return new ServletContainer( rc );
    }

}
