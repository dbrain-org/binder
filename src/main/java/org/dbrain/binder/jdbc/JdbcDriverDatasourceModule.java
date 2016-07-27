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

package org.dbrain.binder.jdbc;

import org.dbrain.binder.app.Binder;
import org.dbrain.binder.app.QualifiedModule;
import org.dbrain.binder.directory.Qualifiers;
import org.dbrain.binder.lifecycle.TransactionScoped;

import javax.inject.Provider;
import java.sql.Connection;


/**
 * This module will bind the Connection returned by the connection provider to the Transaction
 * scope.
 */
public class JdbcDriverDatasourceModule extends QualifiedModule<JdbcDriverDatasourceModule> {

    private Provider<Connection> connectionProvider;

    public JdbcDriverDatasourceModule withProvider( Provider<Connection> connectionProvider ) {
        this.connectionProvider = connectionProvider;
        return this;
    }

    @Override
    public void configure( Binder binder ) throws Exception {
        Qualifiers qualifiers = buildQualifiers();
        binder.bind( Connection.class ) //
              .to( Connection.class ) //
              .providedBy( connectionProvider::get ) //
              .qualifiedBy( qualifiers ) //
              .in( TransactionScoped.class );
    }
}
