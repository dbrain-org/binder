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

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

import javax.servlet.Servlet;
import java.util.ArrayList;
import java.util.List;

/**
 * Builder for simple web applications using jax-rs / jersey.
 */
public class WebApplicationBuilder {

    private List<Object> resources = new ArrayList<>();


    public WebApplicationBuilder add( Object resource ) {
        resources.add( resource );
        return this;
    }

    public Servlet build() {
        ResourceConfig rc = new ResourceConfig();

        for ( Object o : resources ) {
            if ( o instanceof Class ) {
                rc.register( (Class) o );
            } else {
                rc.register( o );
            }
        }
        return new ServletContainer( rc );
    }

}
