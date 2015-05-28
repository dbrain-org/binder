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

package org.dbrain.binder.http.server.defs;

/**
 * Servlet application should reply to authenticated requests.
 * This definition is for a form based authentication.
 *
 * @author kilantzis
 */
public class ServletAppSecurityDef {

    private final String pathSpec;

    private final FormLocationDef formLocationDef;
    private final CredentialsDef  credentialsDef;

    public ServletAppSecurityDef( String contextPath, FormLocationDef formLocationDef, CredentialsDef credentialsDef ) {
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