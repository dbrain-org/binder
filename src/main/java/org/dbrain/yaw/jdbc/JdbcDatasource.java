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
import org.dbrain.yaw.system.app.BaseQualifiedFeature;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.sql.DataSource;
import java.sql.Connection;


/**
 * Created by epoitras on 3/5/15.
 */
public class JdbcDatasource extends BaseQualifiedFeature<JdbcDatasource> {

    private final Configuration session;

    private DataSource dataSource;

    @Inject
    public JdbcDatasource( Configuration session ) {
        this.session = session;
    }

    @Override
    protected JdbcDatasource self() {
        return this;
    }

    public JdbcDatasource dataSource( DataSource dataSource ) {
        this.dataSource = dataSource;
        return this;
    }

    public void complete() {

        session.addService( DataSource.class ) //
                .providedBy( () -> dataSource ) //
                .servicing( DataSource.class ) //
                .qualifiedBy( getQualifiers() ) //
                .in( Singleton.class )//
                .complete();

        session.addService( Connection.class ) //
                .providedBy( () -> dataSource.getConnection() )//
                .servicing( Connection.class ) //
                .qualifiedBy( getQualifiers() ) //
                .in( TransactionScoped.class ) //
                .complete();

    }

}
