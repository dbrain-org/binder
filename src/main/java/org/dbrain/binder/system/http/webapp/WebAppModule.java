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

package org.dbrain.binder.system.http.webapp;

import org.dbrain.binder.app.Binder;
import org.dbrain.binder.app.Module;
import org.dbrain.binder.system.app.SystemConfiguration;
import org.glassfish.hk2.api.ServiceLocator;

import javax.inject.Inject;
import javax.servlet.ServletContextListener;

/**
 * Created by epoitras on 3/13/15.
 */
public class WebAppModule implements Module {

    private final ServiceLocator serviceLocator;

    @Inject
    public WebAppModule( ServiceLocator serviceLocator ) {
        this.serviceLocator = serviceLocator;
    }

    @Override
    public void configure( Binder binder ) throws Exception {
        binder.bind( new WebAppConfigServletContextListener( serviceLocator ) )
              .to( ServletContextListener.class )
              .qualifiedBy( SystemConfiguration.class );
    }
}
