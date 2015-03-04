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

import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.Module;
import org.dbrain.yaw.scope.TransactionScoped;
import org.dbrain.yaw.system.txs.jdbc.ConnectionToMemberAdapter;

import javax.inject.Provider;
import java.lang.annotation.Annotation;
import java.sql.Connection;

/**
 * Created by epoitras on 3/3/15.
 */
public class JdbcConnectionModule implements Module {

    private final Class<? extends Annotation> qualifier;
    private final Provider<Connection>        unscopedConnectionProvider;

    public JdbcConnectionModule( Class<? extends Annotation> qualifier,
                                 Provider<Connection> unscopedConnectionProvider ) {
        this.qualifier = qualifier;
        this.unscopedConnectionProvider = unscopedConnectionProvider;
    }

    @Override
    public void configure( Binder binder ) {
        Key<Connection> connectionKey = Key.get( Connection.class, qualifier );
        Key<ConnectionToMemberAdapter> connectionAdapterKey = Key.get( ConnectionToMemberAdapter.class, qualifier );
        Provider<ConnectionToMemberAdapter> connectionAdapterProvider = binder.getProvider( connectionAdapterKey );


        binder.bind( connectionAdapterKey ) //
                .toProvider( () -> new ConnectionToMemberAdapter( unscopedConnectionProvider.get() ) ) //
                .in( TransactionScoped.class );

        binder.bind( connectionKey ) //
                .toProvider( () -> connectionAdapterProvider.get().getConnection() ) //
                .in( TransactionScoped.class );

    }
}