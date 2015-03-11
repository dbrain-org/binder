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

package org.dbrain.yaw;

import org.dbrain.yaw.app.App;
import org.dbrain.yaw.system.app.AppImpl;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by epoitras on 3/4/15.
 */
public class App_Test {

    @Test
    public void testBasi6cCreation() throws Exception {

        App app = new AppImpl();

        ServiceLocator locator = app.getInstance( ServiceLocator.class );
        Assert.assertNotNull( locator );
        ServiceLocatorUtilities.dumpAllDescriptors( locator );

    }

    @Test(expected = NullPointerException.class )
    public void testInstanceNotFound() throws Exception {
        App app = new AppImpl();
        app.getInstance( App_Test.class );
    }

}
