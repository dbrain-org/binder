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

package org.dbrain.yaw.hk2;

import com.fasterxml.jackson.databind.module.SimpleSerializers;
import org.glassfish.hk2.api.DynamicConfiguration;
import org.glassfish.hk2.api.DynamicConfigurationService;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.ServiceLocatorFactory;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.Assert;
import org.junit.Test;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

/**
 * Created by epoitras on 3/4/15.
 */
public class InjectionUseCase_Test {



    @Test
    public void testBasicUseCase() throws Exception {

        ServiceLocator sl = ServiceLocatorFactory.getInstance().create( "default" );

        ServiceLocatorUtilities.addClasses( sl, SimpleService.class );
//        DynamicConfigurationService dcs = sl.getService( DynamicConfigurationService.class );
//        DynamicConfiguration dc = dcs.createDynamicConfiguration();
//        dc.bind( BuilderHelper.activeLink( SimpleService.class ).in( Singleton.class ).build() );
//        dc.commit();

        for ( int i = 0; i < 10000; i++ ) {
            SimpleService test = sl.getService( SimpleService.class );
        }
        List<ServiceHandle<SimpleService>> handles = sl.getAllServiceHandles( SimpleService.class );

        for ( ServiceHandle<SimpleService> handle : handles ) {
            SimpleService service = handle.getService();
        }
    }

    public static class SimpleService {

        @Inject
        public SimpleService( ServiceLocator sl ) {
            Assert.assertNotNull( sl );
        }

    }


}
