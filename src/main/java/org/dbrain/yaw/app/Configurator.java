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

import org.dbrain.yaw.system.lifecycle.BaseClassAnalyzer;
import org.dbrain.yaw.system.util.AnnotationBuilder;
import org.glassfish.hk2.api.ClassAnalyzer;
import org.glassfish.hk2.api.Descriptor;
import org.glassfish.hk2.api.DynamicConfiguration;
import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.api.FactoryDescriptors;
import org.glassfish.hk2.api.HK2Loader;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.PerLookup;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.AbstractActiveDescriptor;
import org.glassfish.hk2.utilities.ActiveDescriptorBuilder;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.glassfish.hk2.utilities.reflection.ParameterizedTypeImpl;

import javax.inject.Named;
import javax.inject.Provider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Service configuration description.
 */
public class Configurator<T> {

    private final App app;

    private final DynamicConfiguration dc;

    private final Class<T> serviceProviderClass;

    private Provider<T> provider;

    private Consumer<T> disposer;

    private final Set<Type> services = new HashSet<>();

    private final Set<Annotation> qualifiers = new HashSet<>();

    private Class<? extends Annotation> scope;

    public Configurator( App app, DynamicConfiguration dc, Class<T> serviceProviderClass ) {
        Objects.requireNonNull( app );
        Objects.requireNonNull( dc );
        Objects.requireNonNull( serviceProviderClass );
        this.app = app;
        this.dc = dc;
        this.serviceProviderClass = serviceProviderClass;
    }

    public Configurator<T> providedBy( final T instance ) {
        return providedBy( () -> instance );
    }

    public Configurator<T> providedBy( Provider<T> provider ) {
        this.provider = provider;
        return this;
    }

    public Configurator<T> disposedBy( Consumer<T> disposer ) {
        this.disposer = disposer;
        return this;
    }

    public Configurator<T> servicing( Type type ) {
        services.add( type );
        return this;
    }

    public Configurator<T> qualifiedBy( Annotation quality ) {
        qualifiers.add( quality );
        return this;
    }

    public Configurator<T> named( String name ) {
        return qualifiedBy( AnnotationBuilder.from( Named.class ).value( name ).build() );
    }

    public Configurator<T> in( Class<? extends Annotation> scope ) {
        this.scope = scope;
        return this;
    }


    public void done() {

        // Retrieve an instance of the service locator.
        ServiceLocator sl = app.getInstance( ServiceLocator.class );

        Factory factory = null;
        if ( provider != null || disposer != null ) {
            Provider<T> finalProvider = provider;
            Consumer<T> finalDisposer = disposer;
            if ( finalProvider == null ) {
                finalProvider = new StandardProvider<>( app, serviceProviderClass );
            }
            if ( finalDisposer == null ) {
                finalDisposer = new StandardDisposer<>( app, serviceProviderClass );
            }
            factory = new StandardFactory<>( finalProvider, finalDisposer );
        }

        AbstractActiveDescriptor factoryDescriptor;
        ActiveDescriptorBuilder serviceDescriptor;
        if ( factory != null ) {
            factoryDescriptor = BuilderHelper.createConstantDescriptor( factory );
            factoryDescriptor.addContractType( Factory.class );
            serviceDescriptor = BuilderHelper.activeLink( factory.getClass() );
        } else {
            factoryDescriptor = null;
            serviceDescriptor = BuilderHelper.activeLink( serviceProviderClass );
        }

        for ( Type service : services ) {
            serviceDescriptor.to( service );
            if ( factoryDescriptor != null ) {
                factoryDescriptor.addContractType( new ParameterizedTypeImpl( Factory.class, service ) );
            }
        }
        for ( Annotation a : qualifiers ) {
            serviceDescriptor.qualifiedBy( a );
            if ( factoryDescriptor != null ) {
                factoryDescriptor.addQualifierAnnotation( a );
            }
        }
        if ( scope != null ) {
            serviceDescriptor.in( scope );
        } else {
            serviceDescriptor.in( PerLookup.class );
        }

        if ( factoryDescriptor == null ) {
            dc.bind( sl.reifyDescriptor( serviceDescriptor.build() ) );
        } else {
            dc.bind( new FactoryDescriptorsImpl( factoryDescriptor, serviceDescriptor.buildProvideMethod() ) );
        }

    }

    private static class StandardProvider<T> implements Provider<T> {

        private final ServiceLocator serviceLocator;
        private final Class<T>       implClass;

        public StandardProvider( App app, Class<T> implClass ) {
            this.serviceLocator = app.getInstance( ServiceLocator.class );
            this.implClass = implClass;
        }

        @Override
        public T get() {
            return serviceLocator.create( implClass );
        }
    }

    private static class StandardDisposer<T> implements Consumer<T> {

        private final App      app;
        private final Class<T> implClass;

        public StandardDisposer( App app, Class<T> implClass ) {
            this.app = app;
            this.implClass = implClass;
        }

        @Override
        public void accept( T t ) {
            if ( t != null ) {
                ClassAnalyzer analyzer = app.getInstance( ClassAnalyzer.class, BaseClassAnalyzer.YAW_ANALYZER_NAME );
                Method disposeMethod = analyzer.getPreDestroyMethod( implClass );
                try {
                    disposeMethod.invoke( t );
                } catch ( Exception e ) {
                    throw new MultiException( e );
                }

            }
        }
    }

    private static class StandardFactory<T> implements Factory<T> {

        private Provider<T> provider;

        private Consumer<T> disposer;

        public StandardFactory( Provider<T> provider, Consumer<T> disposer ) {
            this.provider = provider;
            this.disposer = disposer;
        }

        @Override
        public T provide() {
            return provider.get();
        }

        @Override
        public void dispose( T instance ) {
            disposer.accept( instance );
        }
    }

    private static class FactoryDescriptorsImpl implements FactoryDescriptors {
        private final Descriptor serviceDescriptor;
        private final Descriptor factoryDescriptor;

        public FactoryDescriptorsImpl( Descriptor serviceDescriptor, Descriptor factoryDescriptor ) {
            this.serviceDescriptor = serviceDescriptor;
            this.factoryDescriptor = factoryDescriptor;
        }

        @Override
        public Descriptor getFactoryAsAService() {
            return serviceDescriptor;
        }

        @Override
        public Descriptor getFactoryAsAFactory() {
            return factoryDescriptor;
        }

        @Override
        public String toString() {
            return "FactoryDescriptorsImpl(\n" +
                    serviceDescriptor + ",\n" + factoryDescriptor + ",\n\t" + System.identityHashCode( this ) + ")";
        }
    }

}
