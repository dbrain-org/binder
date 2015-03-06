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

package org.dbrain.yaw.txs;

import org.dbrain.yaw.App;
import org.dbrain.yaw.system.modules.JdbcConnectionBinder;
import org.dbrain.yaw.system.txs.TransactionManager;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.Test;

import javax.inject.Provider;
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

        Provider<Connection> provider1 = () -> {
            try {
                return DriverManager.getConnection( "jdbc:h2:mem:prov1", "", "" );
            } catch ( Exception e ) {
                throw new IllegalStateException( e );
            }
        };

        Provider<Connection> provider2 = () -> {
            try {
                return DriverManager.getConnection( "jdbc:h2:mem:prov2", "", "" );
            } catch ( Exception e ) {
                throw new IllegalStateException( e );
            }
        };


        App app = new App();

        TransactionControl txCtrl = app.getInstance( TransactionControl.class );
        TransactionManager manager = app.getInstance( TransactionManager.class );

        assertEquals( txCtrl, manager );

        ServiceLocatorUtilities.bind( app.getInstance( ServiceLocator.class ),
                                      new JdbcConnectionBinder( "prov1", provider1 ) );

        ServiceLocatorUtilities.bind( app.getInstance( ServiceLocator.class ),
                                      new JdbcConnectionBinder( "prov2", provider2 ) );

        try ( Transaction tx = txCtrl.start() ) {
            assertEquals( TransactionState.ACTIVE, tx.getStatus() );

            Connection connection1 = app.getInstance( Connection.class, "prov1" );
            Connection connection2 = app.getInstance( Connection.class, "prov2" );

            assertNotEquals( connection1, connection2 );
            tx.commit();

        }

        app.close();
    }

}
