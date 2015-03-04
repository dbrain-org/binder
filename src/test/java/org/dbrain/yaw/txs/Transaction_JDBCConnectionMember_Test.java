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

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import org.dbrain.yaw.system.modules.JdbcConnectionModule;
import org.dbrain.yaw.system.modules.TransactionModule;
import org.dbrain.yaw.txs.artifacts.MemberA;
import org.junit.Test;

import javax.inject.Provider;
import java.sql.Connection;
import java.sql.DriverManager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Test generic transaction scenarios.
 */
public class Transaction_JDBCConnectionMember_Test {

    @Test
    public void testSingleMemberCommit() throws Exception {

        Provider<Connection> unscoped = () -> {
            try {
                return DriverManager.getConnection( "jdbc:h2:mem:test", "", "" );
            } catch( Exception e ) {
                throw new IllegalStateException( e );
            }
        };

        Injector injector = Guice.createInjector( new TransactionModule(), new JdbcConnectionModule( MemberA.class, unscoped ) );
        TransactionControl txctl = injector.getInstance( TransactionControl.class );

        try ( Transaction tx = txctl.start() ) {

            // start transaction by requiring a transactional scoped member.
            Connection connection = injector.getInstance( Key.get( Connection.class, MemberA.class ) );
            assertNotNull( connection );
            assertEquals( connection, injector.getInstance( Key.get( Connection.class, MemberA.class ) ) );
            assertEquals( tx.getStatus(), TransactionState.ACTIVE );

            // Commit transaction.
            tx.commit();
            assertEquals( tx.getStatus(), TransactionState.COMMIT );

        }

    }

}
