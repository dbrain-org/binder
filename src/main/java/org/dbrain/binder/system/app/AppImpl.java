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

import org.dbrain.binder.App;
import org.dbrain.binder.conf.Binder;
import org.dbrain.binder.directory.ServiceKey;
import org.dbrain.binder.system.http.server.HttpStandardScopeComponent;
import org.dbrain.binder.system.http.webapp.WebAppComponent;
import org.dbrain.binder.system.lifecycle.BaseClassAnalyzer;
import org.dbrain.binder.system.scope.StandardScopeComponent;
import org.dbrain.binder.system.txs.TransactionComponent;
import org.dbrain.binder.system.util.AnnotationBuilder;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.ServiceLocatorFactory;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;

import javax.inject.Named;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

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

    // TODO @epoitras Does not works in a "multi-threaded" concurrent scenario.
    public AppImpl( String name ) {
        this.name = name;
        this.delegate = serviceLocatorFactory.create( name );

        ServiceLocatorUtilities.addClasses( delegate, BaseClassAnalyzer.class );
        delegate.setDefaultClassAnalyzerName( BaseClassAnalyzer.YAW_ANALYZER_NAME );
        ServiceLocatorUtilities.addOneConstant( delegate, this );

        Binder session = new ConfigurationImpl( this );
        session.bind( ConfigurationImpl.class ) //
                .to( Binder.class ) //
                .providedBy( () -> ConfigurationImpl.CURRENT_SESSION.get() ) //
                .complete();
        session.commit();

        ServiceLocatorUtilities.enablePerThreadScope( delegate );

        session = startConfiguration();
        session.bindComponent(TransactionComponent.class).complete();
        session.bindComponent(StandardScopeComponent.class).complete();
        session.commit();

        session = startConfiguration();
        session.bindComponent(HttpStandardScopeComponent.class).complete();
        session.bindComponent(WebAppComponent.class).complete();
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
    public void configure( AppConfigurator configurator ) {
        try {
            Binder session = startConfiguration();
            configurator.accept( session );
            session.commit();
        } catch ( Exception e ) {
            throw new MultiException( e );
        }
    }

    /**
     * @return Start a new session of configuration.
     */
    public Binder startConfiguration() {
        return new ConfigurationImpl( this );
    }

    @Override
    public <T> T locate( Class<T> serviceClass ) {
        return delegate.getService( serviceClass );
    }

    @Override
    public <T> T locate( Class<T> serviceClass, String name ) {
        return delegate.getService( serviceClass, name );
    }

    @Override
    public <T> T locate( Class<T> serviceClass, Class<? extends Annotation> qualifiers ) {
        return delegate.getService( serviceClass, AnnotationBuilder.of( qualifiers ) );
    }

    @Override
    public <T> T locate( ServiceKey<T> serviceKey ) {
        Set<Annotation> qualifiers = serviceKey.getQualifiers();
        T result;
        if ( qualifiers.size() > 0 ) {
            result = delegate.getService( serviceKey.getServiceType(),
                                          qualifiers.toArray( new Annotation[qualifiers.size()] ) );
        } else {
            result = delegate.getService( serviceKey.getServiceType() );
        }
        return result;
    }


    @Override
    public <T> T getInstance( Class<T> serviceClass ) {
        T result = locate( serviceClass );
        Objects.requireNonNull( result,
                                "Service of class " + serviceClass.getName() + " is not found in application " + getName() + "." );
        return result;
    }

    @Override
    public <T> T getInstance( Class<T> serviceClass, String name ) {
        T result = locate( serviceClass, name );
        Objects.requireNonNull( result,
                                "Service of class " + serviceClass.getName() + " is not found in application " + getName() + "." );
        return result;
    }

    @Override
    public <T> T getInstance( Class<T> serviceClass, Class<? extends Annotation> qualifiers ) {
        T result = locate( serviceClass, qualifiers );
        Objects.requireNonNull( result,
                                "Service of class " + serviceClass.getName() + " is not found in application " + getName() + "." );
        return result;
    }

    @Override
    public <T> T getInstance( ServiceKey<T> serviceKey ) {
        T result = locate( serviceKey );
        Objects.requireNonNull( result,
                                "Service of class " + serviceKey.getServiceType() + " is not found in application " + getName() + "." );
        return result;
    }

    @Override
    public <T> T getOrCreateInstance( Class<T> serviceClass ) {
        return getOrCreateInstance( ServiceKey.of( serviceClass ) );
    }

    @Override
    public <T> T getOrCreateInstance( ServiceKey<T> serviceKey ) {
        T result = locate( serviceKey );

        // Use the Jit path only for unqualified services
        if ( result == null && serviceKey.getQualifiers().size() == 0 ) {
            result = delegate.createAndInitialize( serviceKey.getServiceClass() );
        }
        Objects.requireNonNull( result,
                                "Cannot create instance of " + serviceKey.getServiceClass()
                                                                         .getName() + " using application " + getName() + "." );
        return result;
    }

    @Override
    public <T> List<T> listServices( Class<T> serviceClass ) {
        return delegate.getAllServices( serviceClass );
    }

    @Override
    public <T> List<T> listServices( Class<T> serviceClass, String name ) {
        return delegate.getAllServices( serviceClass, AnnotationBuilder.of( Named.class, name ) );
    }

    @Override
    public <T> List<T> listServices( Class<T> serviceClass, Class<? extends Annotation> qualifier ) {
        return delegate.getAllServices( serviceClass, AnnotationBuilder.of( qualifier ) );
    }

    @Override
    public void close() throws Exception {
        delegate.shutdown();
    }
}
