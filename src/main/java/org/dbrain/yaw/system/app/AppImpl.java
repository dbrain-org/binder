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
import org.dbrain.yaw.system.lifecycle.BaseClassAnalyzer;
import org.dbrain.yaw.system.scope.StandardScopeFeature;
import org.dbrain.yaw.system.txs.TransactionBinder;
import org.glassfish.hk2.api.ServiceLocatorFactory;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;

import java.lang.annotation.Annotation;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Created by epoitras on 3/4/15.
 */
public class AppImpl implements App {

    private static final ServiceLocatorFactory serviceLocatorFactory = ServiceLocatorFactory.getInstance();

    private final String                               name;
    private final org.glassfish.hk2.api.ServiceLocator delegate;

    public AppImpl() {
        this( UUID.randomUUID().toString() );
    }

    public AppImpl( String name ) {
        this.name = name;
        this.delegate = serviceLocatorFactory.create( name );
        ServiceLocatorUtilities.addClasses( delegate, BaseClassAnalyzer.class );
        delegate.setDefaultClassAnalyzerName( BaseClassAnalyzer.YAW_ANALYZER_NAME );
        ServiceLocatorUtilities.addOneConstant( delegate, this, name );

        Configuration session = new ConfigurationImpl( this );
        session.defineService( ConfigurationImpl.class ) //
                .providedBy( () -> ConfigurationImpl.CURRENT_SESSION.get() ) //
                .servicing( Configuration.class ) //
                .complete();
        session.commit();

        ServiceLocatorUtilities.enablePerThreadScope( delegate );
        ServiceLocatorUtilities.bind( delegate, new TransactionBinder() );

        session = startConfiguration();
        session.addFeature( StandardScopeFeature.class ).complete();
        session.commit();
    }

    /**
     * @return The name of the application.
     */
    @Override
    public String getName() {
        return name;
    }

    @Override
    public void configure( Consumer<Configuration> configurator ) {
        Configuration session = startConfiguration();
        configurator.accept( session );
        session.commit();
    }

    /**
     * @return Start a new session of configuration.
     */
    @Override
    public Configuration startConfiguration() {
        return new ConfigurationImpl( this );
    }


    @Override
    public <T> T getInstance( Class<T> serviceClass ) {
        T result = delegate.getService( serviceClass );
        Objects.requireNonNull( result,
                                "Service of class " + serviceClass.getName() + " is not found in application " + getName() + "." );
        return result;
    }

    @Override
    public <T> T getInstance( Class<T> serviceClass, String name ) {
        T result = delegate.getService( serviceClass, name );
        Objects.requireNonNull( result,
                                "Service of class " + serviceClass.getName() + " is not found in application " + getName() + "." );
        return result;
    }

    @Override
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
