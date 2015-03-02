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

package org.dbrain.yaw.system.http.server.utils;

import java.net.URI;

/**
 * Created by epoitras on 18/09/14.
 */
public class ResourceSandbox {

    private final URI rootUri;

    public ResourceSandbox( URI rootUri ) {
        if ( rootUri == null || !rootUri.isAbsolute() || rootUri.isOpaque() ) {
            throw new IllegalArgumentException();
        }
        this.rootUri = rootUri;
    }

    /**
     * Get the resource as an uri.
     */
    public URI getResource( URI trailingUri ) {
        if ( trailingUri == null || trailingUri.isAbsolute() || trailingUri.isOpaque() ) {
            throw new IllegalArgumentException();
        }
        URI resourceUri = rootUri.resolve( trailingUri ).normalize();
        URI relativeUri = rootUri.relativize( resourceUri );

        // Relative uri is not in the scope of the root Uri.
        if ( relativeUri.isAbsolute() ) {
            throw new IllegalArgumentException();
        }
        return resourceUri;
    }

    /**
     * Get the resource as an uri.
     */
    public URI getResource( String trailingUri ) {
        return getResource( URI.create( trailingUri ) );
    }

}
