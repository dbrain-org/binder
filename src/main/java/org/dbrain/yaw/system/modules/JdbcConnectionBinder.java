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

package org.dbrain.yaw.system.modules;

import org.dbrain.yaw.scope.TransactionScoped;
import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import javax.inject.Provider;
import java.sql.Connection;

/**
 * Created by epoitras on 3/3/15.
 */
public class JdbcConnectionBinder extends AbstractBinder {

    private final String name;
    private final Provider<Connection>        unscopedConnectionProvider;

    public JdbcConnectionBinder( String name,
                                 Provider<Connection> unscopedConnectionProvider ) {
        this.name = name;
        this.unscopedConnectionProvider = unscopedConnectionProvider;
    }

    public static class ProviderFactory<T> implements Factory<T> {

        private final Provider<T> provider;

        public ProviderFactory( Provider<T> provider ) {
            this.provider = provider;
        }

        @Override
        public T provide() {
            return provider.get();
        }

        @Override
        public void dispose( T instance ) {
        }
    }

    @Override
    public void configure() {
        bindFactory( new ProviderFactory<>( unscopedConnectionProvider ) )
                .to( Connection.class )
                .named( name )
                .in( TransactionScoped.class );
    }
}