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
import org.dbrain.yaw.scope.DisposeException;
import org.dbrain.yaw.system.scope.ScopeRegistry;
import org.dbrain.yaw.txs.Transaction;
import org.dbrain.yaw.txs.TransactionException;
import org.dbrain.yaw.txs.TransactionState;
import org.dbrain.yaw.txs.exceptions.CommitFailedException;
import org.dbrain.yaw.txs.exceptions.NoTransactionException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * A registry that keeps track of transactional objects.
 */
public class TransactionRegistry extends ScopeRegistry implements Transaction {

    private TransactionState              state   = TransactionState.ACTIVE;
    private LinkedList<TransactionMember> members = null;

    @Override
    public synchronized TransactionState getStatus() {
        return state;
    }

    @Override
    protected synchronized <T> void registerObject( Key<T> key, T value ) {
        super.registerObject( key, value );
        if ( value instanceof TransactionMember ) {
            if ( members == null ) {
                members = new LinkedList<>();
            }
            members.addFirst( (TransactionMember) value );
        }
    }

    private List<TransactionMember> listMemberForCommit() {
        return members != null ? new ArrayList<>( members ) : new ArrayList<>();
    }

    @Override
    public synchronized void commit() throws TransactionException {
        if ( state != TransactionState.ACTIVE ) {
            throw new NoTransactionException();
        }
        Throwable error = null;
        int commitIdx = 0;
        if ( members != null ) {
            List<TransactionMember> memberToCommit = listMemberForCommit();
            int totalToCommit = memberToCommit.size();
            try {
                for ( TransactionMember member : memberToCommit ) {
                    member.flush();
                }

                    while ( commitIdx < totalToCommit ) {
                        memberToCommit.get( commitIdx ).commit();
                        commitIdx++;
                    }
            } catch ( Throwable t ) {
                error = t;
            }

            if ( commitIdx < totalToCommit ) {
                rollback( memberToCommit, commitIdx );
            }

        }
        if ( error != null ) {
            state = commitIdx > 0 ? TransactionState.PARTIAL_ROLLBACK : TransactionState.ROLLBACK;
            throw new CommitFailedException( error );
        } else {
            state = TransactionState.COMMIT;
        }
    }

    private void rollback( List<TransactionMember> members, int idx ) throws TransactionException {
        Preconditions.checkNotNull( members );
        int total = members.size();
        Preconditions.checkArgument( idx <= total );
        while ( idx < total ) {
            try {
                members.get( idx ).rollback();
                idx++;
            } catch ( Throwable t ) {
                // TODO Fix this.
            }
        }
    }

    @Override
    public synchronized void rollback() throws TransactionException {
        if ( state != TransactionState.ACTIVE ) {
            throw new NoTransactionException();
        }
        if ( members != null ) {
            rollback( members, 0);
        }
        state = TransactionState.ROLLBACK;
    }

    @Override
    public synchronized void close() throws DisposeException {
        try {
            if ( state == TransactionState.ACTIVE ) {
                rollback();
            }
        } finally {
            super.close();
        }
    }
}
