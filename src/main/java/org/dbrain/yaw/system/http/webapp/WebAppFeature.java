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

package org.dbrain.yaw.system.http.webapp;

import org.dbrain.yaw.app.Configuration;
import org.dbrain.yaw.app.Feature;
import org.dbrain.yaw.system.app.SystemConfiguration;
import org.glassfish.hk2.api.ServiceLocator;

import javax.inject.Inject;
import javax.servlet.ServletContextListener;

/**
 * Created by epoitras on 3/13/15.
 */
public class WebAppFeature implements Feature {

    private final Configuration  config;
    private final ServiceLocator serviceLocator;

    @Inject
    public WebAppFeature( Configuration config, ServiceLocator serviceLocator ) {
        this.config = config;
        this.serviceLocator = serviceLocator;
    }

    @Override
    public void complete() {

        config.bind( ServletContextListener.class )
              .to( ServletContextListener.class )
              .providedBy( new WebAppConfigServletContextListener( serviceLocator ) )
              .qualifiedBy( SystemConfiguration.class )
              .complete();

    }

}
