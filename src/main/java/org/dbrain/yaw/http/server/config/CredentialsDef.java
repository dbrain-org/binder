package org.dbrain.yaw.http.server.config;

/**
 * @author kilantzis
 */
public class CredentialsDef {

    private final String realm;
    private final String singleRole;
    private final String file;

    public CredentialsDef( String realm, String singleRole, String file ) {
        this.realm = realm;
        this.singleRole = singleRole;
        this.file = file;
    }

    public String getRealm() {
        return realm;
    }

    public String getSingleRole() {
        return singleRole;
    }

    public String getFile() {
        return file;
    }
}
