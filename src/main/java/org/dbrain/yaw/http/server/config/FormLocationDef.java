package org.dbrain.yaw.http.server.config;

/**
 * @author kilantzis
 */
public class FormLocationDef {

    private final String url;
    private final String errorURL;

    public FormLocationDef( String url, String errorURL ) {
        this.url = url;
        this.errorURL = errorURL;
    }

    public String getUrl() {
        return url;
    }

    public String getErrorURL() {
        return errorURL;
    }
}
