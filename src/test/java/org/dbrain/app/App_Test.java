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

package org.dbrain.app;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by epoitras on 3/4/15.
 */
public class App_Test {

    @Test
    public void testBasicCreation() throws Exception {

        App app = App.create();

        ServiceLocator locator = app.getInstance( ServiceLocator.class );
        Assert.assertNotNull( locator );
        ServiceLocatorUtilities.dumpAllDescriptors( locator );

    }

    @Test
    public void testNamedCreation() throws Exception {

        try ( App app1 = App.getOrCreate( "testCreation" );
                App app2 = App.getOrCreate( "testCreation" ) ) {

            Assert.assertEquals( app1, app2 );
        }

    }

    /**
     * Create twice the same app and ensure they are not the same instances.
     */
    @Test
    public void testNamedCreation2() throws Exception {
        App app1;App app2;

        try ( App app = App.getOrCreate( "testCreation" ) ) {
            app1 = app;
        }

        try ( App app = App.getOrCreate( "testCreation" ) ) {
            app2 = app;
        }

        Assert.assertNotEquals( app1, app2 );

    }




}
