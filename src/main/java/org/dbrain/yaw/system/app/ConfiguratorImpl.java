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
import org.dbrain.yaw.app.ServiceConfigurator;
import org.dbrain.yaw.app.ServiceDisposer;
import org.dbrain.yaw.app.ServiceProvider;
import org.dbrain.yaw.system.lifecycle.BaseClassAnalyzer;
import org.dbrain.yaw.system.util.AnnotationBuilder;
import org.glassfish.hk2.api.ClassAnalyzer;
import org.glassfish.hk2.api.Descriptor;
import org.glassfish.hk2.api.DynamicConfiguration;
import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.api.FactoryDescriptors;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.PerLookup;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.AbstractActiveDescriptor;
import org.glassfish.hk2.utilities.ActiveDescriptorBuilder;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.glassfish.hk2.utilities.reflection.ParameterizedTypeImpl;

import javax.inject.Named;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Service configuration description.
 */
public class ConfiguratorImpl<T> implements ServiceConfigurator<T> {

    private final App app;

    private final DynamicConfiguration dc;

    private final Class<T> serviceProviderClass;

    private ServiceProvider<T> provider;

    private ServiceDisposer<T> disposer;

    private final Set<Type> services = new HashSet<>();

    private final Set<Annotation> qualifiers = new HashSet<>();

    private Class<? extends Annotation> scope;

    public ConfiguratorImpl( App app, DynamicConfiguration dc, Class<T> serviceProviderClass ) {
        Objects.requireNonNull( app );
        Objects.requireNonNull( dc );
        Objects.requireNonNull( serviceProviderClass );
        this.app = app;
        this.dc = dc;
        this.serviceProviderClass = serviceProviderClass;
    }

    @Override
    public ServiceConfigurator<T> providedBy( final T instance ) {
        return providedBy( () -> instance );
    }

    @Override
    public ServiceConfigurator<T> providedBy( ServiceProvider<T> provider ) {
        this.provider = provider;
        return this;
    }

    @Override
    public ServiceConfigurator<T> disposedBy( ServiceDisposer<T> disposer ) {
        this.disposer = disposer;
        return this;
    }

    @Override
    public ServiceConfigurator<T> servicing( Type type ) {
        services.add( type );
        return this;
    }

    @Override
    public ServiceConfigurator<T> qualifiedBy( Annotation quality ) {
        qualifiers.add( quality );
        return this;
    }

    @Override
    public ServiceConfigurator<T> qualifiedBy( Iterable<Annotation> qualities ) {
        for ( Annotation quality : qualities ) {
            qualifiedBy( quality );
        }
        return this;
    }

    @Override
    public ServiceConfigurator<T> named( String name ) {
        return qualifiedBy( AnnotationBuilder.from( Named.class ).value( name ).build() );
    }

    @Override
    public ServiceConfigurator<T> in( Class<? extends Annotation> scope ) {
        this.scope = scope;
        return this;
    }


    @Override
    public void complete() {
        try {
            // Retrieve an instance of the service locator.
            ServiceLocator sl = app.getInstance( ServiceLocator.class );

            Factory factory = null;
            if ( provider != null || disposer != null ) {
                ServiceProvider<T> finalProvider = provider;
                ServiceDisposer<T> finalDisposer = disposer;
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
        } catch ( RuntimeException e ) {
            throw e;
        } catch ( Exception e ) {
            throw new MultiException( e );
        }

    }

    private static class StandardProvider<T> implements ServiceProvider<T> {

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

    private static class StandardDisposer<T> implements ServiceDisposer<T> {

        private final App      app;
        private final Class<T> implClass;

        public StandardDisposer( App app, Class<T> implClass ) {
            this.app = app;
            this.implClass = implClass;
        }

        @Override
        public void dispose( T t ) {
            if ( t != null ) {
                ClassAnalyzer analyzer = app.getInstance( ClassAnalyzer.class, BaseClassAnalyzer.YAW_ANALYZER_NAME );
                Method disposeMethod = analyzer.getPreDestroyMethod( implClass );
                try {
                    if ( disposeMethod != null ) {
                        disposeMethod.invoke( t );
                    }
                } catch ( RuntimeException e ) {
                    throw e;
                } catch ( Exception e ) {
                    throw new MultiException( e );
                }

            }
        }
    }

    private static class StandardFactory<T> implements Factory<T> {

        private ServiceProvider<T> provider;

        private ServiceDisposer<T> disposer;

        public StandardFactory( ServiceProvider<T> provider, ServiceDisposer<T> disposer ) {
            this.provider = provider;
            this.disposer = disposer;
        }

        @Override
        public T provide() {
            try {
                return provider.get();
            } catch ( RuntimeException e ) {
                throw e;
            } catch ( Exception e ) {
                throw new MultiException( e );
            }
        }

        @Override
        public void dispose( T instance ) {
            try {
                disposer.dispose( instance );
            } catch ( RuntimeException e ) {
                throw e;
            } catch ( Exception e ) {
                throw new MultiException( e );
            }
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
