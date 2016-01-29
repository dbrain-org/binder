/*
 * Copyright [2016] [Eric Poitras]
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
import org.dbrain.binder.system.app.QualifiedComponent;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.sql.DataSource;
import java.sql.Connection;


/**
 * This component will bind the following services:
 * Datasource: singleton
 * Connection: transaction scoped
 */
public class JdbcDatasourceModule extends QualifiedModule<JdbcDatasourceModule> {

    private DataSource dataSource;

    @Override
    protected JdbcDatasourceModule self() {
        return this;
    }

    public JdbcDatasourceModule dataSource( DataSource dataSource ) {
        this.dataSource = dataSource;
        return this;
    }

    @Override
    public void configure( Binder binder ) throws Exception {
        Qualifiers qualifiers = getQualifiers();
        binder.bind( DataSource.class ) //
                .to( DataSource.class ) //
                .providedBy( () -> dataSource ) //
                .qualifiedBy( qualifiers ) //
                .in( Singleton.class );

        binder.bind( Connection.class ) //
                .to( Connection.class ) //
                .providedBy( () -> dataSource.getConnection() )//
                .qualifiedBy( qualifiers ) //
                .in( TransactionScoped.class );
    }
}
