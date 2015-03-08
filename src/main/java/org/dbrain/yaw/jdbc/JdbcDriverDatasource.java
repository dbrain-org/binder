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

package org.dbrain.yaw.jdbc;

import org.dbrain.yaw.scope.TransactionScoped;
import org.dbrain.yaw.system.config.BaseQualifiedConfigurator;
import org.dbrain.yaw.system.lifecycle.BaseClassAnalyzer;
import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.hk2.utilities.binding.ServiceBindingBuilder;

import javax.inject.Inject;
import javax.inject.Provider;
import java.lang.annotation.Annotation;
import java.sql.Connection;


/**
 * Created by epoitras on 3/5/15.
 */
public class JdbcDriverDatasource extends BaseQualifiedConfigurator<JdbcDriverDatasource> {

    private final ServiceLocator serviceLocator;

    private Provider<Connection> connectionProvider;

    @Inject
    public JdbcDriverDatasource( ServiceLocator serviceLocator ) {
        this.serviceLocator = serviceLocator;
    }

    @Override
    protected JdbcDriverDatasource self() {
        return this;
    }

    public JdbcDriverDatasource withProvider( Provider<Connection> connectionProvider ) {
        this.connectionProvider = connectionProvider;
        return this;
    }

    public void commit() {
        //        DynamicConfiguration dc = ServiceLocatorUtilities.createDynamicConfiguration( serviceLocator );
        //
        //        AbstractActiveDescriptor<Factory<Connection>> key = BuilderHelper.createConstantDescriptor( Factories.of( connectionProvider ) );
        //        key.addContractType( Connection.class );
        //        key.setScopeAnnotation( TransactionScoped.class );
        //        for( Annotation a: getQualifiers()) {
        //            key.addQualifierAnnotation( a );
        //        }
        //        dc.bind( key );
        //
        //        dc.commit();

        ServiceLocatorUtilities.bind( serviceLocator, new Binder() );

    }

    private class ConnectionFactory implements Factory<Connection> {

        @TransactionScoped
        @Override
        public Connection provide() {
            return connectionProvider.get();
        }

        @Override
        public void dispose( Connection instance ) {
            try {
                instance.close();
            } catch ( Exception e ) {
                throw new IllegalStateException( e );
            }
        }
    }

    private class Binder extends AbstractBinder {


        @Override
        public void configure() {
            ServiceBindingBuilder<Connection> b = bindFactory( new ConnectionFactory() );
            b.to( Connection.class );
            b.in( TransactionScoped.class );
            b.analyzeWith( BaseClassAnalyzer.YAW_ANALYZER_NAME );
            for ( Annotation q : getQualifiers() ) {
                b.qualifiedBy( q );
            }
        }
    }
}
