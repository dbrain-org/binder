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

package org.dbrain.app.system.txs;

import org.dbrain.app.lifecycle.TransactionScoped;
import org.dbrain.app.txs.Transaction;
import org.dbrain.app.txs.TransactionControl;
import org.dbrain.app.txs.TransactionState;
import org.dbrain.app.txs.exceptions.NoTransactionException;
import org.dbrain.app.txs.exceptions.TransactionAlreadyStartedException;
import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.Context;
import org.glassfish.hk2.api.IterableProvider;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.api.ServiceLocator;

import javax.inject.Inject;
import java.lang.annotation.Annotation;

/**
 * Implementation of the transaction control interface as well as provide a registry for the transaction
 * scope.
 */
public class TransactionManager implements TransactionControl, Context<TransactionScoped> {

    private final ServiceLocator sl;
    private final Iterable<TransactionMember.Wrapper> memberFactories;
    private final ThreadLocal<TransactionImpl> transaction = new ThreadLocal<>();


    @Inject
    public TransactionManager( ServiceLocator sl, IterableProvider<TransactionMember.Wrapper> memberFactory ) {
        this.sl = sl;
        this.memberFactories = memberFactory;
    }

    @Override
    public Transaction current() {
        return transaction.get();
    }

    @Override
    public Transaction start() {
        TransactionImpl tx = transaction.get();
        if ( tx == null ) {
            tx = new TransactionImpl( memberFactories, () -> transaction.set( null ) );
            transaction.set( tx );
        } else {
            throw new TransactionAlreadyStartedException();
        }
        return tx;
    }

    @Override
    public Class<? extends Annotation> getScope() {
        return TransactionScoped.class;
    }

    /**
     * Get or provision an instance of the service identified by the instance of Key.
     */
    @Override
    public <T> T findOrCreate( ActiveDescriptor<T> key, ServiceHandle<?> root ) {
        TransactionImpl tx = transaction.get();
        if ( tx == null ) {
            throw new NoTransactionException();
        }
        return tx.findOrCreate( key, root );
    }


    @Override
    public boolean containsKey( ActiveDescriptor key ) {
        TransactionImpl tx = transaction.get();
        if ( tx == null ) {
            throw new NoTransactionException();
        }
        return tx.containsKey( key );
    }

    @Override
    public void destroyOne( ActiveDescriptor<?> key ) {
        TransactionImpl tx = transaction.get();
        if ( tx == null ) {
            throw new NoTransactionException();
        }
        tx.destroyOne( key );
    }

    @Override
    public boolean supportsNullCreation() {
        return false;
    }

    @Override
    public boolean isActive() {
        TransactionImpl tx = transaction.get();
        return tx != null && tx.getStatus() == TransactionState.ACTIVE;
    }

    @Override
    public void shutdown() {
    }

}
