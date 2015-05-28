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

import java.net.URI;

/**
 * Created by epoitras on 17/09/14.
 */
public class HttpsConnectorDef extends HttpConnectorDef implements ConnectorDef {

    private URI keyStore;

    private String keyStorePassword;

    private String keyManagerPassord;

    public URI getKeyStore() {
        return keyStore;
    }

    public void setKeyStore( URI keyStore ) {
        this.keyStore = keyStore;
    }

    public String getKeyStorePassword() {
        return keyStorePassword;
    }

    public void setKeyStorePassword( String keyStorePassword ) {
        this.keyStorePassword = keyStorePassword;
    }

    public String getKeyManagerPassord() {
        return keyManagerPassord;
    }

    public void setKeyManagerPassord( String keyManagerPassord ) {
        this.keyManagerPassord = keyManagerPassord;
    }

}
