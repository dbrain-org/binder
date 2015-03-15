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

package org.dbrain.yaw.directory;

import org.dbrain.yaw.app.App;
import org.dbrain.yaw.directory.artifacts.JitBean;
import org.dbrain.yaw.system.app.AppImpl;
import org.glassfish.hk2.api.ServiceLocator;
import org.junit.Test;

import static org.junit.Assert.*;

public class ServiceDirectory_jit_Test {

    App buildApp() {
        return new AppImpl(  );
    }


    @Test
    public void testGetJitInstance() throws Exception {
        try ( App app = buildApp() ) {
            JitBean jitClass  = app.getJitInstance( JitBean.class );

            assertEquals( app.getInstance( App.class ), jitClass.getInjectedConstructor1() );
            assertEquals( app.getInstance( ServiceLocator.class ), jitClass.getInjectedConstructor2() );
            assertEquals( app.getInstance( App.class ), jitClass.getInjectedField1() );

        }
    }
}