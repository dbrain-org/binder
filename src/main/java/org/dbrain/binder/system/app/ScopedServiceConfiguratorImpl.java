/*
 * Copyright [2016] [Eric Poitras]
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

package org.dbrain.binder.system.app;

import org.dbrain.binder.app.App;
import org.dbrain.binder.app.ServiceConfigurator;
import org.glassfish.hk2.api.DynamicConfiguration;

/**
 * Service configuration description.
 */
public class ScopedServiceConfiguratorImpl<T> extends ServiceConfiguratorImpl<T, ScopedServiceConfiguratorImpl<T>> implements ServiceConfigurator.Scoped<T> {

    public ScopedServiceConfiguratorImpl( App app, BindingStack cc, DynamicConfiguration dc, Class<T> serviceProviderClass ) {
        super( app, cc, dc, serviceProviderClass );
    }

}
