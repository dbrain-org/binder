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
import org.dbrain.yaw.lifecycle.TransactionScoped;
import org.dbrain.yaw.system.app.QualifiedFeature;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.sql.DataSource;
import java.sql.Connection;


/**
 * Created by epoitras on 3/5/15.
 */
public class JdbcDatasourceFeature extends QualifiedFeature<JdbcDatasourceFeature> {

    private final Configuration session;

    private DataSource dataSource;

    @Inject
    public JdbcDatasourceFeature( Configuration session ) {
        this.session = session;
    }

    @Override
    protected JdbcDatasourceFeature self() {
        return this;
    }

    public JdbcDatasourceFeature dataSource( DataSource dataSource ) {
        this.dataSource = dataSource;
        return this;
    }

    @Override
    public void complete() {

        session.bind( DataSource.class ) //
                .to( DataSource.class ) //
                .providedBy( () -> dataSource ) //
                .qualifiedBy( getQualifiers() ) //
                .in( Singleton.class )//
                .complete();

        session.bind( Connection.class ) //
                .to( Connection.class ) //
                .providedBy( () -> dataSource.getConnection() )//
                .qualifiedBy( getQualifiers() ) //
                .in( TransactionScoped.class ) //
                .complete();

    }

}
