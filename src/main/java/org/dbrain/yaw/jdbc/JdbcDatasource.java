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
import org.dbrain.yaw.system.config.Factories;
import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.hk2.utilities.binding.ScopedBindingBuilder;
import org.glassfish.hk2.utilities.binding.ServiceBindingBuilder;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.sql.DataSource;
import java.lang.annotation.Annotation;
import java.sql.Connection;


/**
 * Created by epoitras on 3/5/15.
 */
public class JdbcDatasource extends BaseQualifiedConfigurator<JdbcDatasource> {

    private final ServiceLocator app;

    private DataSource dataSource;

    @Inject
    public JdbcDatasource( ServiceLocator app ) {
        this.app = app;
    }

    @Override
    protected JdbcDatasource self() {
        return this;
    }

    public JdbcDatasource dataSource( DataSource dataSource ) {
        this.dataSource = dataSource;
        return this;
    }

    public void commit() {

        ServiceLocatorUtilities.bind( app, new Binder() );

    }


    private class Binder extends AbstractBinder {


        @Override
        public void configure() {

            Provider<Connection> connectionProvider = () -> {
                try {
                    return dataSource.getConnection();
                } catch ( Exception e ) {
                    throw new IllegalStateException(  e );
                }
            };

            ScopedBindingBuilder<DataSource> ds = bind( dataSource )
                    .to( DataSource.class );

            ServiceBindingBuilder<Connection> connection = bindFactory( new Factory<Connection>() {

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
            } );
            connection.to( Connection.class );
            for ( Annotation q : getQualifiers() ) {
                ds.qualifiedBy( q );
                connection.qualifiedBy( q );
            }
        }
    }
}
