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

package org.dbrain.yaw.app;

import org.dbrain.yaw.app.App;
import org.dbrain.yaw.app.Configurator;
import org.glassfish.hk2.api.DynamicConfiguration;
import org.glassfish.hk2.api.DynamicConfigurationService;

import javax.inject.Inject;

/**
 * Created by epoitras on 3/8/15.
 */
public class ConfigurationSession {

    private final App app;
    private DynamicConfiguration dc;

    @Inject
    public ConfigurationSession( App app ) {
        this.app = app;
        dc = app.getInstance( DynamicConfigurationService.class ).createDynamicConfiguration();
    }

    public <T> Configurator<T> configure( Class<T> implementationClass ) {
        return new Configurator<>( app, dc, implementationClass );
    }

    public <T> Configurator<T> configure( T implementation ) {
        return configure( (Class<T>)implementation.getClass() ).providedBy( implementation );
    }

    public void commit() {
        dc.commit();
    }



}
