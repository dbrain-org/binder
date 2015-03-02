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

package org.dbrain.yaw.system.http.server;

import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * User: epoitras
 * Date: 29/06/13
 * Time: 8:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class HttpContext {

    private static ThreadLocal<HttpServletRequest> THREAD_CONTEXT = new ThreadLocal<>();

    public static void enter( HttpServletRequest request ) {
        if ( THREAD_CONTEXT.get() == null ) {
            THREAD_CONTEXT.set( request );
        } else {
            throw new IllegalStateException( "Cannot enter a requests context twice." );
        }
    }

    public static void leave() {
        if ( THREAD_CONTEXT.get() == null ) {
            throw new IllegalStateException( "Cannot enter a requests context twice." );
        } else {
            THREAD_CONTEXT.set( null );
        }
    }

    public static HttpServletRequest getCurrentRequest() {
        return THREAD_CONTEXT.get();
    }

}
