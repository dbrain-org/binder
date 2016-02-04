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

package org.dbrain.binder.system.app;

import org.dbrain.binder.app.App;
import org.dbrain.binder.app.Binder;
import org.dbrain.binder.app.Module;
import org.dbrain.binder.app.ServiceConfigurator;
import org.glassfish.hk2.api.DynamicConfiguration;
import org.glassfish.hk2.api.DynamicConfigurationService;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

/**
 * Created by epoitras on 3/8/15.
 */
public class SimpleBinder implements Binder {

    private final App                  app;
    private final BindingStack         bindingStack;
    private       DynamicConfiguration dc;

    @Inject
    public SimpleBinder( App app, BindingStack bindingStack ) {
        this.app = app;
        this.bindingStack = bindingStack;
        dc = app.getInstance( DynamicConfigurationService.class ).createDynamicConfiguration();
    }

    @Override
    public <T> ServiceConfigurator.Scoped<T> bind( Class<T> implementationClass ) {
        return new ScopedServiceConfiguratorImpl<>( app, bindingStack, dc, implementationClass );
    }

    @Override
    public <T> ServiceConfigurator.Instance<T> bind( T implementation ) {
        return new InstanceServiceConfiguratorImpl<>( app, bindingStack, dc, ((Class<T>)implementation.getClass()) ).providedBy( () -> implementation ).in( Singleton.class );
    }

    @Override
    public <T extends Module> T bindModule( Class<T> componentClass ) {
        T module = app.getOrCreateInstance( componentClass );
        bindingStack.pushModule( module::configure );
        return module;
    }


    public BindingStack getBindingContext() {
        return bindingStack;
    }

    /**
     * Commit the binder into the app.
     */
    public void commit() throws Exception {

        // Allows all components to complete.
        // This is a tricky loop since components can register even more service as they configure.
        List<Module> binders = bindingStack.empty();
        while ( binders != null ) {
            for ( Module b : binders ) {
                b.configure( this );
            }

            // Continue emptying the stack until there is no more services to bind.
            binders = bindingStack.empty();
        }

        dc.commit();
    }

}
