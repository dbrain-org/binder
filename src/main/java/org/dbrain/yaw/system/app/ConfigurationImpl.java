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

package org.dbrain.yaw.system.app;

import org.dbrain.yaw.app.App;
import org.dbrain.yaw.app.Configuration;
import org.dbrain.yaw.app.Feature;
import org.dbrain.yaw.app.ServiceConfigurator;
import org.glassfish.hk2.api.DynamicConfiguration;
import org.glassfish.hk2.api.DynamicConfigurationService;
import org.glassfish.hk2.api.ServiceLocator;

import javax.inject.Inject;

/**
 * Created by epoitras on 3/8/15.
 */
public class ConfigurationImpl implements Configuration {

    static ThreadLocal<ConfigurationImpl> CURRENT_SESSION = new ThreadLocal<>();

    private final App                  app;
    private       DynamicConfiguration dc;

    @Inject
    public ConfigurationImpl( App app ) {
        this.app = app;
        dc = app.getInstance( DynamicConfigurationService.class ).createDynamicConfiguration();
    }

    @Override
    public <T> ServiceConfigurator<T> bind( Class<T> implementationClass ) {
        return new ConfiguratorImpl<>( app, dc, implementationClass );
    }

    @Override
    public <T> ServiceConfigurator<T> defineService( T implementation ) {
        return bind( (Class<T>) implementation.getClass() ).providedBy( implementation );
    }

    @Override
    public <T extends Feature> T addFeature( Class<T> featureClass ) {
        CURRENT_SESSION.set( this );
        try {
            return app.getInstance( ServiceLocator.class ).create( featureClass );
        } finally {
            CURRENT_SESSION.set( null );
        }
    }

    @Override
    public void commit() {
        dc.commit();
    }


}
