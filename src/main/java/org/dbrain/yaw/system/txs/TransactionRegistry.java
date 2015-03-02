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

import com.google.inject.Key;
import jersey.repackaged.com.google.common.base.Preconditions;
import org.dbrain.yaw.system.scope.ScopeRegistry;
import org.dbrain.yaw.system.txs.exceptions.CommitFailedException;
import org.dbrain.yaw.system.txs.exceptions.NoActiveTransactionException;
import org.dbrain.yaw.txs.Transaction;
import org.dbrain.yaw.txs.TransactionException;
import org.dbrain.yaw.txs.TransactionState;

import java.util.ArrayList;
import java.util.List;

/**
 * A registry that keeps track of transactional objects.
 */
public class TransactionRegistry extends ScopeRegistry implements Transaction {

    private TransactionState        state   = TransactionState.RUNNING;
    private List<TransactionMember> members = null;

    @Override
    public synchronized TransactionState getStatus() {
        return state;
    }

    @Override
    protected synchronized <T> void registerObject( Key<T> key, T value ) {
        super.registerObject( key, value );
        if ( value instanceof TransactionMember ) {
            if ( members == null ) {
                members = new ArrayList<>();
            }
            members.add( (TransactionMember) value );
        }
    }

    /**
     * Flush members.
     */
    private void flushMembers( List<TransactionMember> members ) throws TransactionException {
        Preconditions.checkNotNull( members );
        for ( int i = members.size() - 1; i >= 0; i-- ) {
            members.get( i ).flush();
        }
    }

    @Override
    public synchronized void commit() throws TransactionException {
        if ( state != TransactionState.RUNNING ) {
            throw new NoActiveTransactionException();
        }
        Throwable error = null;
        if ( members != null ) {
            int i = members.size() - 1;
            try {
                flushMembers( members );

                while ( i >= 0 ) {
                    members.get( i ).commit();
                    i--;
                }
            } catch ( Throwable e ) {
                error = e;
            }

            if ( error != null ) {
                rollback( members, i );
            }

        }
        state = error == null ? TransactionState.COMMITTED : TransactionState.COMMIT_FAILED;
        if ( error != null ) {
            throw new CommitFailedException( error );
        }
    }

    private void rollback( List<TransactionMember> members, int idx ) throws TransactionException {
        Preconditions.checkNotNull( members );
        Preconditions.checkArgument( idx < members.size() );
        while ( idx >= 0 ) {
            try {
                members.get( idx ).rollback();
                idx--;
            } catch ( Throwable t ) {

            }
        }
    }


    @Override
    public synchronized void rollback() throws TransactionException {
        if ( state != TransactionState.RUNNING ) {
            throw new NoActiveTransactionException();
        }
        if ( members != null ) {
            rollback( members, members.size() - 1 );
        }
        state = TransactionState.ROLLED_BACK;
    }


}
