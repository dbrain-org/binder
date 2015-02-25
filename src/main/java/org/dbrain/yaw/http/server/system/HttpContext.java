package org.dbrain.yaw.http.server.system;

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
