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

package org.dbrain.binder.http.artifacts;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

/**
 * Created by epoitras on 3/16/15.
 */
@Provider
public class CookieClientFilter implements ClientRequestFilter, ClientResponseFilter {

    private Cookie cookie = null;

    @Override
    public void filter( ClientRequestContext requestContext ) throws IOException {
        if ( cookie != null ) {
            requestContext.getHeaders().add( "Cookie", cookie.toString() );
        }
    }

    @Override
    public void filter( ClientRequestContext requestContext,
                        ClientResponseContext responseContext ) throws IOException {
        // copy cookies
        if ( responseContext.getCookies() != null && responseContext.getCookies().size() > 0 ) {
            // A simple addAll just for illustration (should probably check for duplicates and expired cookies)
            cookie = responseContext.getCookies().values().iterator().next();
        }

    }
}
