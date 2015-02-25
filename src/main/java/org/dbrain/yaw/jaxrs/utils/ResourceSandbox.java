package org.dbrain.yaw.jaxrs.utils;

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
        URI resourceUri  = rootUri.resolve( trailingUri ).normalize();
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
