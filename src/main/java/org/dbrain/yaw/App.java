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

import org.dbrain.yaw.system.config.Configurator;
import org.dbrain.yaw.system.lifecycle.YawClassAnalyzer;
import org.dbrain.yaw.system.txs.TransactionBinder;
import org.glassfish.hk2.api.ServiceLocatorFactory;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;

import java.lang.annotation.Annotation;
import java.util.Objects;
import java.util.UUID;

/**
 * Created by epoitras on 3/4/15.
 */
public class App implements AutoCloseable {

    private static final ServiceLocatorFactory serviceLocatorFactory = ServiceLocatorFactory.getInstance();

    private final String                               name;
    private final org.glassfish.hk2.api.ServiceLocator delegate;

    public App() {
        this( UUID.randomUUID().toString() );
    }

    public App( String name ) {
        this.name = name;
        this.delegate = serviceLocatorFactory.create( name );
        ServiceLocatorUtilities.addClasses( delegate, YawClassAnalyzer.class );
        delegate.setDefaultClassAnalyzerName( YawClassAnalyzer.YAW_ANALYZER_NAME );
        ServiceLocatorUtilities.enablePerThreadScope( delegate );
        ServiceLocatorUtilities.addOneConstant( delegate, this, name );
        ServiceLocatorUtilities.bind( delegate, new TransactionBinder() );

    }

    /**
     * @return The name of the application.
     */
    public String getName() {
        return name;
    }

    /**
     * Start the configuration of a new component.
     */
    public <T extends Configurator> T configure( Class<T> configuratorClass ) {
         return delegate.create( configuratorClass );
    }


    public <T> T getInstance( Class<T> serviceClass ) {
        T result = delegate.getService( serviceClass );
        Objects.requireNonNull( result,
                                "Service of class " + serviceClass.getName() + " is not found in application " + getName() + "." );
        return result;
    }

    public <T> T getInstance( Class<T> serviceClass, String name ) {
        T result = delegate.getService( serviceClass, name );
        Objects.requireNonNull( result,
                                "Service of class " + serviceClass.getName() + " is not found in application " + getName() + "." );
        return result;
    }

    public <T> T getInstance( Class<T> serviceClass, Annotation qualifiers ) {
        T result = delegate.getService( serviceClass, qualifiers );
        Objects.requireNonNull( result,
                                "Service of class " + serviceClass.getName() + " is not found in application " + getName() + "." );
        return result;
    }


    @Override
    public void close() throws Exception {
        delegate.shutdown();
    }
}
