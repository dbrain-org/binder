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

package org.dbrain.binder.system.txs.jdbc;

import org.dbrain.binder.system.txs.TransactionMember;
import org.dbrain.binder.txs.TransactionException;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by epoitras on 3/5/15.
 */
public class JdbcConnectionWrapper implements TransactionMember.Wrapper<Connection> {

    @Override
    public Class<Connection> forClass() {
        return Connection.class;
    }

    @Override
    public TransactionMember wrap( Connection instance ) {
        return new ConnectionToMemberAdapter( instance );
    }

    /**
     * Created by epoitras on 3/1/15.
     */
    public static class ConnectionToMemberAdapter implements TransactionMember {

        private final Connection connection;

        public ConnectionToMemberAdapter( Connection connection ) {
            this.connection = connection;
            try {
                connection.setAutoCommit( false );
            } catch ( SQLException e ) {
                throw new IllegalStateException( e );
            }
        }

        @Override
        public void flush() throws TransactionException {
            // Nothing to do.
        }

        @Override
        public void commit() throws TransactionException {
            try {
                connection.commit();
            } catch ( SQLException e ) {
                throw new TransactionException( e );
            }
        }

        @Override
        public void rollback() throws TransactionException {
            try {
                connection.rollback();
            } catch ( SQLException e ) {
                throw new TransactionException( e );
            }
        }
    }
}
