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

package org.dbrain.app.http.artifacts;

import org.glassfish.jersey.client.JerseyClientBuilder;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.client.Client;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * Created by epoitras on 3/19/15.
 */
public class CrippledHttpsClient {

    /**
     * @return
     * @throws Exception
     */
    public static Client create() throws Exception {

        JerseyClientBuilder jcb = new JerseyClientBuilder();

        // configure the SSLContext with a TrustManager
        SSLContext ctx = SSLContext.getInstance( "TLS" );
        ctx.init( new KeyManager[0], new TrustManager[]{ new CripledTrustManagerForTest() }, new SecureRandom() );

        jcb.sslContext( ctx );
        jcb.hostnameVerifier( ( s, sslSession ) -> true );

        return jcb.build();
    }

    /**
     * Created by epoitras on 3/18/15.
     */
    public static class CripledTrustManagerForTest implements X509TrustManager {

        @Override
        public void checkClientTrusted( X509Certificate[] arg0, String arg1 ) throws CertificateException { }

        @Override
        public void checkServerTrusted( X509Certificate[] arg0, String arg1 ) throws CertificateException { }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

    }
}
