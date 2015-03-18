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
import org.dbrain.yaw.directory.ServiceKey;
import org.dbrain.yaw.system.http.server.HttpStandardScopeFeature;
import org.dbrain.yaw.system.http.webapp.WebAppFeature;
import org.dbrain.yaw.system.lifecycle.BaseClassAnalyzer;
import org.dbrain.yaw.system.scope.StandardScopeFeature;
import org.dbrain.yaw.system.txs.TransactionFeature;
import org.dbrain.yaw.system.util.AnnotationBuilder;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.ServiceLocatorFactory;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Objects;
import java.util.Set;
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
        ServiceLocatorUtilities.addOneConstant( delegate, this );

        Configuration session = new ConfigurationImpl( this );
        session.bind( ConfigurationImpl.class ) //
                .to( Configuration.class ) //
                .providedBy( () -> ConfigurationImpl.CURRENT_SESSION.get() ) //
                .complete();
        session.commit();

        ServiceLocatorUtilities.enablePerThreadScope( delegate );

        session = startConfiguration();
        session.addFeature( TransactionFeature.class ).complete();
        session.addFeature( StandardScopeFeature.class ).complete();
        session.commit();

        session = startConfiguration();
        session.addFeature( HttpStandardScopeFeature.class ).complete();
        session.addFeature( WebAppFeature.class ).complete();
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
    public void configure( ConfigurationConsumer configurator ) {
        try {
            Configuration session = startConfiguration();
            configurator.accept( session );
            session.commit();
        } catch ( Exception e ) {
            throw new MultiException( e );
        }
    }

    /**
     * @return Start a new session of configuration.
     */
    public Configuration startConfiguration() {
        return new ConfigurationImpl( this );
    }

    @Override
    public <T> T getJitInstance( Class<T> serviceClass ) {
        T result = delegate.createAndInitialize( serviceClass );
        Objects.requireNonNull( result,
                                "Cannot create instance of " + serviceClass.getName() + " using application " + getName() + "." );
        return result;
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
    public <T> T getInstance( Class<T> serviceClass, Class<? extends Annotation> qualifiers ) {
        T result = delegate.getService( serviceClass, AnnotationBuilder.of( qualifiers ) );
        Objects.requireNonNull( result,
                                "Service of class " + serviceClass.getName() + " is not found in application " + getName() + "." );
        return result;
    }

    @Override
    public <T> T getInstance( ServiceKey<T> serviceKey ) {
        Set<Annotation> qualifiers = serviceKey.getQualifiers();
        T result;
        if ( qualifiers.size() > 0 ) {
            result = delegate.getService( serviceKey.getServiceType(), qualifiers.toArray( new Annotation[qualifiers.size()] ) );
        } else {
             result = delegate.getService( serviceKey.getServiceType() );
        }
        Objects.requireNonNull( result,
                                "Service of class " + serviceKey.getServiceType() + " is not found in application " + getName() + "." );
        return result;
    }

    @Override
    public <T> List<T> listServices( Class<T> serviceClass, Annotation qualifier ) {
        return delegate.getAllServices( serviceClass, qualifier );
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
