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

import org.dbrain.binder.conf.Binder;
import org.dbrain.binder.conf.Component;
import org.dbrain.binder.system.app.SystemConfiguration;
import org.glassfish.hk2.api.ServiceLocator;

import javax.inject.Inject;
import javax.servlet.ServletContextListener;

/**
 * Created by epoitras on 3/13/15.
 */
public class WebAppComponent implements Component {

    private final Binder         config;
    private final ServiceLocator serviceLocator;

    @Inject
    public WebAppComponent(Binder config, ServiceLocator serviceLocator) {
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
