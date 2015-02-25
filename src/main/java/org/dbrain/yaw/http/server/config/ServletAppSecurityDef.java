package org.dbrain.yaw.http.server.config;

/**
 * Servlet application should reply to authenticated requests.
 * This definition is for a form based authentication.
 *
 * @author kilantzis
 */
public class ServletAppSecurityDef {

    private final String pathSpec;

    private final FormLocationDef formLocationDef;
    private final CredentialsDef credentialsDef;

    public ServletAppSecurityDef(String contextPath, FormLocationDef formLocationDef, CredentialsDef credentialsDef ) {
        this.pathSpec = contextPath;
        this.formLocationDef = formLocationDef;
        this.credentialsDef = credentialsDef;
    }

    public String getPathSpec() {
        return pathSpec;
    }

    public FormLocationDef getFormLocationDef() {
        return formLocationDef;
    }

    public CredentialsDef getCredentialsDef() {
        return credentialsDef;
    }
}