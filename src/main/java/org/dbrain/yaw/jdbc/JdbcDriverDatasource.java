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

import org.dbrain.yaw.app.Configuration;
import org.dbrain.yaw.scope.TransactionScoped;
import org.dbrain.yaw.system.app.QualifiedFeature;

import javax.inject.Inject;
import javax.inject.Provider;
import java.sql.Connection;


/**
 * Created by epoitras on 3/5/15.
 */
public class JdbcDriverDatasource extends QualifiedFeature<JdbcDriverDatasource> {

    private final Configuration config;

    private Provider<Connection> connectionProvider;

    @Inject
    public JdbcDriverDatasource( Configuration config ) {
        this.config = config;
    }

    @Override
    protected JdbcDriverDatasource self() {
        return this;
    }

    public JdbcDriverDatasource withProvider( Provider<Connection> connectionProvider ) {
        this.connectionProvider = connectionProvider;
        return this;
    }

    @Override
    public void complete() {

        config.addService( Connection.class ) //
                .providedBy( connectionProvider::get ) //
                .qualifiedBy( getQualifiers() ) //
                .servicing( Connection.class ) //
                .in( TransactionScoped.class ) //
                .complete();

    }

}
