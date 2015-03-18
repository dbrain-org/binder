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

import javax.servlet.Filter;

/**
 * Created with IntelliJ IDEA.
 * User: epoitras
 * Date: 15/07/13
 * Time: 10:25 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ServletFilterDef {

    public static ServletFilterDef of( String pathSpec, Filter instance ) {
        return new ServletFilterInstanceDef( pathSpec, instance );
    }

    public static ServletFilterDef of( String pathSpec, Class<? extends Filter> filterClass ) {
        return new ServletFilterClassDef( pathSpec, filterClass );
    }


    public void accept( Visitor v );

    public interface Visitor {
        void visit( ServletFilterInstanceDef servletDef );
        void visit( ServletFilterClassDef servletDef );
    }

    public class ServletFilterClassDef implements ServletFilterDef {

        private final String pathSpec;

        private final Class<? extends Filter> filterClass;

        public ServletFilterClassDef( String pathSpec, Class<? extends Filter> filterClass ) {
            this.pathSpec = pathSpec;
            this.filterClass = filterClass;
        }

        public String getPathSpec() {
            return pathSpec;
        }

        public Class<? extends Filter> getFilterClass() {
            return filterClass;
        }

        @Override
        public void accept( Visitor v ) {
            v.visit( this );
        }

    }

    public class ServletFilterInstanceDef implements ServletFilterDef {

        private final String pathSpec;

        private final Filter instance;

        public ServletFilterInstanceDef( String pathSpec, Filter instance ) {
            this.instance = instance;
            this.pathSpec = pathSpec;
        }

        public String getPathSpec() {
            return pathSpec;
        }

        public Filter getInstance() {
            return instance;
        }

        @Override
        public void accept( Visitor v ) {
            v.visit( this );
        }
    }
}
