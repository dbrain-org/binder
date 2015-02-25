package org.dbrain.yaw.http.server.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import org.dbrain.yaw.http.server.system.HttpContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Created with IntelliJ IDEA.
 * User: epoitras
 * Date: 29/06/13
 * Time: 8:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class HttpModule extends AbstractModule {

    @Override
    protected void configure() {
    }


    @Provides
    private HttpServletRequest getHttpServletRequest() {
        return HttpContext.getCurrentRequest();
    }

    @Provides
    private HttpSession getHttpSession( HttpServletRequest request ) {
        if ( request != null ) {
            return request.getSession();
        } else {
            return null;
        }
    }
}
