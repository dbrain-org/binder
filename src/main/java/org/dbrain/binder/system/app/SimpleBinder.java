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
import org.dbrain.binder.app.Component;
import org.dbrain.binder.app.ServiceConfigurator;
import org.glassfish.hk2.api.DynamicConfiguration;
import org.glassfish.hk2.api.DynamicConfigurationService;

import javax.inject.Inject;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by epoitras on 3/8/15.
 */
public class SimpleBinder implements Binder {

    private final App                  app;
    private final SimpleBindingContext bindingStack;
    private       DynamicConfiguration dc;

    @Inject
    public SimpleBinder( App app, SimpleBindingContext bindingStack ) {
        this.app = app;
        this.bindingStack = bindingStack;
        dc = app.getInstance( DynamicConfigurationService.class ).createDynamicConfiguration();
    }

    @Override
    public <T> ServiceConfigurator<T> bind( Class<T> implementationClass ) {
        return new BindingBuilderImpl<>( app, bindingStack, dc, implementationClass );
    }

    @Override
    public <T> ServiceConfigurator<T> bind( T implementation ) {
        return bind( (Class<T>) implementation.getClass() ).toInstance( implementation );
    }

    @Override
    public <T extends Component> T bindComponent( Class<T> componentClass ) {
        return app.getOrCreateInstance( componentClass );
    }

    public SimpleBindingContext getBindingContext() {
        return bindingStack;
    }

    /**
     * Commit the binder into the app.
     */
    public void commit() {

        // Allows all components to complete.
        // This is a tricky loop since components can register even more service as they configure.
        List<Consumer<Binder>> binders = bindingStack.empty();
        while ( binders != null ) {
            for ( Consumer<Binder> b : binders ) {
                b.accept( this );
            }

            // Continue emptying the stack until there is no more services to bind.
            binders = bindingStack.empty();
        }

        dc.commit();
    }

}
