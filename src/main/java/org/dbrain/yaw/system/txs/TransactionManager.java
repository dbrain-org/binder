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

package org.dbrain.yaw.system.txs;

import org.dbrain.yaw.system.txs.exceptions.NoActiveTransactionException;
import org.dbrain.yaw.txs.Transaction;
import org.dbrain.yaw.txs.TransactionException;
import org.dbrain.yaw.txs.TransactionState;

/**
 * Created by epoitras on 2/27/15.
 */
public class TransactionManager implements Transaction {

    private ThreadLocal<TransactionRegistry> transaction = new ThreadLocal<>();

    public TransactionRegistry getTransaction() {
        TransactionRegistry tx = transaction.get();
        if ( tx == null ) {
            tx = new TransactionRegistry();
            transaction.set( tx );
        }
        return tx;
    }


    @Override
    public TransactionState getStatus() {
        TransactionRegistry tx = transaction.get();
        if ( tx != null ) {
            return tx.getStatus();
        } else {
            return TransactionState.NONE;
        }
    }

    @Override
    public void commit() throws TransactionException {
        TransactionRegistry tx = transaction.get();
        if ( tx != null ) {
            tx.commit();
        } else {
            throw new NoActiveTransactionException();
        }
    }

    @Override
    public void rollback() throws TransactionException {
        TransactionRegistry tx = transaction.get();
        if ( tx != null ) {
            tx.rollback();
        } else {
            throw new NoActiveTransactionException();
        }
    }

    /**
     * Discard any transaction status. If a transaction is running, it will be rolled back.
     */
    public void discard() throws TransactionException {
        TransactionRegistry tx = transaction.get();
        if ( tx != null ) {
            if ( tx.getStatus() == TransactionState.RUNNING ) {
                tx.rollback();
            }
            transaction.set( null );
        }

    }


}
