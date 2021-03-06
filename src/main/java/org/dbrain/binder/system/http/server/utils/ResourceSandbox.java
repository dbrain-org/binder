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

package org.dbrain.binder.system.http.server.utils;

import java.net.URI;

/**
 * Created by epoitras on 18/09/14.
 */
public class ResourceSandbox {

    private String prefix = null;
    private final URI sandbox = URI.create( "http://sandbox.com/a/" );
    private final URI rootUri;

    public ResourceSandbox( URI rootUri ) {
        if ( rootUri == null || !rootUri.isAbsolute() ) {
            throw new IllegalArgumentException( rootUri.toString() );
        }
        if ( rootUri.isOpaque() ) {
            if ( rootUri.getScheme().equalsIgnoreCase( "jar" ) ) {
                rootUri = URI.create( rootUri.getRawSchemeSpecificPart() );
                prefix = "jar";
            } else {
                throw new IllegalArgumentException( "Unsupported opaque URI: " + rootUri );
            }
        }
        this.rootUri = rootUri;
    }

    /**
     * Get the resource as an uri.
     */
    public URI getResource( URI trailingUri ) {
        if ( trailingUri == null || trailingUri.isAbsolute() || trailingUri.isOpaque() ) {
            throw new IllegalArgumentException( trailingUri.toString() );
        }
        if ( !sandbox.resolve( trailingUri ).normalize().getPath().startsWith( sandbox.getPath() ) ) {
            throw new IllegalArgumentException( trailingUri.toString() );
        }
        URI result = URI.create( rootUri.toString() ).resolve( trailingUri ).normalize();

        return prefix != null ? URI.create( prefix + ":" + result.toString() ) : result;
    }

    /**
     * Get the resource as an uri.
     */
    public URI getResource( String trailingUri ) {
        return getResource( URI.create( trailingUri ) );
    }

}
