package org.dbrain.yaw.http.server.config;

import javax.servlet.Filter;

/**
 * Created with IntelliJ IDEA.
 * User: epoitras
 * Date: 15/07/13
 * Time: 10:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class ServletFilterConfig {

    private final Filter instance;

    private final String pathSpec;

    public ServletFilterConfig( Filter instance, String pathSpec ) {
        this.instance = instance;
        this.pathSpec = pathSpec;
    }

    public String getPathSpec() {
        return pathSpec;
    }

    public Filter getInstance() {
        return instance;
    }

}
