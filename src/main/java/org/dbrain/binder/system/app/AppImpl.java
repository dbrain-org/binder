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
import org.dbrain.binder.app.Module;
import org.dbrain.binder.directory.Qualifiers;
import org.dbrain.binder.directory.ServiceKey;
import org.dbrain.binder.system.http.server.HttpStandardScopeModule;
import org.dbrain.binder.system.http.webapp.WebAppModule;
import org.dbrain.binder.system.lifecycle.BaseClassAnalyzer;
import org.dbrain.binder.system.scope.StandardScopeModule;
import org.dbrain.binder.system.txs.TransactionModule;
import org.dbrain.binder.system.util.AnnotationBuilder;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.ServiceLocatorFactory;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;

import javax.inject.Named;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Created by epoitras on 3/4/15.
 */
public class AppImpl implements App {

    private static final ServiceLocatorFactory serviceLocatorFactory = ServiceLocatorFactory.getInstance();

    private final String                               name;
    private final org.glassfish.hk2.api.ServiceLocator delegate;
    private final ThreadLocal<SimpleBinder> currentBinder = new ThreadLocal<>();

    public AppImpl() {
        this( UUID.randomUUID().toString() );
    }

    // TODO @epoitras Does not works in a "multi-threaded" concurrent scenario.
    public AppImpl( String name ) {
        this.name = name;
        this.delegate = serviceLocatorFactory.create( name );

        ServiceLocatorUtilities.addClasses( delegate, BaseClassAnalyzer.class );
        delegate.setDefaultClassAnalyzerName( BaseClassAnalyzer.BINDER_ANALYZER_NAME );
        ServiceLocatorUtilities.addOneConstant( delegate, this );

        configure( binder -> binder.bind( BindingStack.class ) //
                                   .to( BindingStack.class ) //
                                   .providedBy( () -> currentBinder.get().getBindingContext() ) );

        ServiceLocatorUtilities.enablePerThreadScope( delegate );

        configure( binder -> {
            binder.bindModule( TransactionModule.class );
            binder.bindModule( StandardScopeModule.class );
        } );

        configure( binder -> {
            binder.bindModule( HttpStandardScopeModule.class );
            binder.bindModule( WebAppModule.class );
        } );

    }

    /**
     * @return The name of the application.
     */
    @Override
    public String getName() {
        return name;
    }

    @Override
    public void configure( Module configurator ) {
        try {
            SimpleBinder binder = startConfiguration();
            configurator.configure( binder );
            commitConfiguration();
        } catch ( Exception e ) {
            throw new MultiException( e );
        }
    }

    @Override
    public void configure( Class<? extends Module> ... modules ) {
        for ( Class<? extends Module> module: modules ) {
            configure( getOrCreateInstance( module ) );
        }
    }

    /**
     * @return Start a new session of configuration.
     */
    private SimpleBinder startConfiguration() {
        if ( currentBinder.get() != null ) {
            throw new IllegalStateException( "Cannot configure twice on the same thread at the same time." );
        }
        currentBinder.set( new SimpleBinder( this, new BindingStack() ) );
        return currentBinder.get();
    }

    private void commitConfiguration() throws Exception {
        if ( currentBinder.get() == null ) {
            throw new IllegalStateException( "No configuration to commit." );
        }
        try {
            currentBinder.get().commit();
        } finally {
            currentBinder.set( null );
        }
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
    public <T> T locate( Class<T> serviceClass, Class<? extends Annotation> qualifier ) {
        return delegate.getService( serviceClass, AnnotationBuilder.of( qualifier ) );
    }

    @Override
    public <T> T locate( ServiceKey<T> serviceKey ) {
        Qualifiers qualifiers = serviceKey.getQualifiers();
        T result;
        if ( qualifiers.size() > 0 ) {
            result = delegate.getService( serviceKey.getServiceType(), qualifiers.toArray() );
        } else {
            result = delegate.getService( serviceKey.getServiceType() );
        }
        return result;
    }

    @Override
    public <T> T locate( Class<T> serviceClass, Qualifiers qualifiers ) {
        T result;
        if ( qualifiers.size() > 0 ) {
            result = delegate.getService( serviceClass, qualifiers.toArray() );
        } else {
            result = delegate.getService( serviceClass );
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
    public <T> T getInstance( Class<T> serviceClass, Class<? extends Annotation> qualifier ) {
        T result = locate( serviceClass, qualifier );
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
    public <T> T getInstance( Class<T> serviceClass, Qualifiers qualifiers ) {
        T result = locate( serviceClass, qualifiers );
        Objects.requireNonNull( result,
                                "Service of class " + serviceClass + " is not found in application " + getName() + "." );
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
    public <T> List<T> listServices( Class<T> serviceClass, Qualifiers qualifiers ) {
        return delegate.getAllServices( serviceClass, qualifiers.toArray() );
    }

    @Override
    public void close() throws Exception {
        delegate.shutdown();
    }
}
