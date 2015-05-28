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

package org.dbrain.binder.directory;

import org.dbrain.binder.App;
import org.dbrain.binder.directory.artifacts.JitBean;
import org.glassfish.hk2.api.ServiceLocator;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ServiceDirectory_jit_Test {

    App buildApp() {
        return App.create();
    }


    @Test
    public void testGetJitInstance() throws Exception {
        try ( App app = buildApp() ) {
            JitBean jitClass = app.getOrCreateInstance( JitBean.class );

            assertEquals( app.getInstance( App.class ), jitClass.getInjectedConstructor1() );
            assertEquals( app.getInstance( ServiceLocator.class ), jitClass.getInjectedConstructor2() );
            assertEquals( app.getInstance( App.class ), jitClass.getInjectedField1() );

        }
    }

    @Test
    public void testGetJitInstance_serviceKey() throws Exception {
        try ( App app = buildApp() ) {
            JitBean jitClass = app.getOrCreateInstance( ServiceKey.of( JitBean.class ) );

            assertEquals( app.getInstance( App.class ), jitClass.getInjectedConstructor1() );
            assertEquals( app.getInstance( ServiceLocator.class ), jitClass.getInjectedConstructor2() );
            assertEquals( app.getInstance( App.class ), jitClass.getInjectedField1() );

        }
    }

    @Test( expected = NullPointerException.class )
    public void testGetJitInstance_qualified() throws Exception {
        try ( App app = buildApp() ) {
            JitBean jitClass = app.getOrCreateInstance( ServiceKey.of( JitBean.class, "named" ) );

            assertNull( jitClass );
        }
    }


}