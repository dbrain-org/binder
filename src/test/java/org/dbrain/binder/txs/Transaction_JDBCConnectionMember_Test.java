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

package org.dbrain.binder.txs;

import org.dbrain.binder.app.App;
import org.dbrain.binder.app.Binder;
import org.dbrain.binder.jdbc.JdbcDatasourceComponent;
import org.dbrain.binder.jdbc.JdbcDatasourceModule;
import org.dbrain.binder.jdbc.JdbcDriverDatasourceComponent;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.api.ServiceLocator;
import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Test generic transaction scenarios.
 */
public class Transaction_JDBCConnectionMember_Test {


    @Test
    public void testSingleMember() throws Exception {

        App app = App.create();
        app.configure( ( Binder binder ) -> {

            binder.bindComponent( JdbcDriverDatasourceComponent.class ) //
                    .named( "prov1" ) //
                    .withProvider( () -> {
                        try {
                            return DriverManager.getConnection( "jdbc:h2:mem:prov1", "", "" );
                        } catch ( Exception e ) {
                            throw new IllegalStateException( e );
                        }
                    } );

            binder.bindComponent( JdbcDriverDatasourceComponent.class ) //
                    .named( "prov2" ) //
                    .withProvider( () -> {
                        try {
                            return DriverManager.getConnection( "jdbc:h2:mem:prov2", "", "" );
                        } catch ( Exception e ) {
                            throw new IllegalStateException( e );
                        }
                    } );

            binder.bindComponent( JdbcDatasourceComponent.class ) //
                    .named( "prov3" )//
                    .dataSource( JdbcConnectionPool.create( "jdbc:h2:mem:prov3", "sa", "sa" ) );

            binder.bindModule( JdbcDatasourceModule.class ) //
                    .named( "prov4" )
                    .dataSource( JdbcConnectionPool.create( "jdbc:h2:mem:prov4", "sa", "sa" ) );

        } );

        TransactionControl txCtrl = app.getInstance( TransactionControl.class );

        try ( Transaction tx = txCtrl.start() ) {
            assertEquals( TransactionState.ACTIVE, tx.getStatus() );

            Connection connection1_1 = app.getInstance( Connection.class, "prov1" );
            Connection connection1_2 = app.getInstance( Connection.class, "prov1" );
            Connection connection2_1 = app.getInstance( Connection.class, "prov2" );
            Connection connection3_1 = app.getInstance( Connection.class, "prov3" );
            Connection connection4_1 = app.getInstance( Connection.class, "prov4" );

            ServiceHandle sh = app.getInstance( ServiceLocator.class ).getServiceHandle( Connection.class, "prov1" );

            assertEquals( connection1_1, connection1_2 );
            assertNotEquals( connection1_1, connection2_1 );
            assertNotEquals( connection1_1, connection3_1 );
            assertNotEquals( connection2_1, connection3_1 );
            tx.commit();

        }

        app.close();
    }

}
