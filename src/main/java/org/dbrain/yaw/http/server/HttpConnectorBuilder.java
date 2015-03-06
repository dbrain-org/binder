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

package org.dbrain.yaw.http.server;

import org.dbrain.yaw.http.server.defs.HttpConnectorDef;

/**
 * Created with IntelliJ IDEA.
 * User: epoitras
 * Date: 15/07/13
 * Time: 9:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class HttpConnectorBuilder extends AbstractHttpConnectorBuilder<HttpConnectorBuilder> {

    public static HttpConnectorDef of( Integer port ) {
        return new HttpConnectorBuilder().port( port ).build();
    }

    private HttpConnectorBuilder() {
    }

    @Override
    public HttpConnectorBuilder self() {
        return this;
    }

    public HttpConnectorDef build() {
        Integer finalPort = getPort();

        // What is the final port ?
        finalPort = finalPort != null ? finalPort  : 80;

        HttpConnectorDef result = new HttpConnectorDef();
        result.setPort( finalPort );
        return result;
    }

}
