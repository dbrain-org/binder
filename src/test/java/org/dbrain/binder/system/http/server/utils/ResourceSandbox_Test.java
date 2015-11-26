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

import org.junit.Test;

import java.net.URI;

import static org.junit.Assert.assertEquals;

/**
 * Created by epoitras on 26/11/15.
 */
public class ResourceSandbox_Test {


    @Test
    public void testSimple() throws Exception {
        ResourceSandbox sandbox = new ResourceSandbox( URI.create( "http:/www.toto.com/res/" ) );
        URI uri = sandbox.getResource( "test.json" );
        assertEquals( uri, URI.create( "http:/www.toto.com/res/test.json"  ) );
    }

    @Test
    public void testSimpleOpaque() throws Exception {
        ResourceSandbox sandbox = new ResourceSandbox( URI.create( "jar:file:/test/" ) );
        URI uri = sandbox.getResource( "test.json" );
        assertEquals( uri, URI.create( "jar:file:/test/test.json"  ) );
    }


    @Test( expected = IllegalArgumentException.class )
    public void testEscapeAtempt() throws Exception {
        ResourceSandbox sandbox = new ResourceSandbox( URI.create( "http:/www.toto.com/res/" ) );
        sandbox.getResource( "a/../../secret.json" );
    }

    @Test( expected = IllegalArgumentException.class )
    public void testEscapeOpacity() throws Exception {
        ResourceSandbox sandbox = new ResourceSandbox( URI.create( "jar:file:/test/" ) );
        sandbox.getResource( "../test.json" );
    }

    @Test( expected = IllegalArgumentException.class )
    public void testUnknownOpacity() throws Exception {
        new ResourceSandbox( URI.create( "jarjar:file:/test/" ) );
    }

}