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
import org.dbrain.binder.txs.exceptions.NoTransactionException;
import org.dbrain.binder.txs.exceptions.TransactionAlreadyStartedException;
import org.dbrain.binder.txs.features.TestMemberModule;
import org.dbrain.binder.txs.impl.TestMember;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.Assert;
import org.junit.Test;

import java.io.PrintWriter;

import static org.junit.Assert.assertEquals;

/**
 * Created by epoitras on 3/5/15.
 */
public class TransactionControl_Test {

    public App buildApp() {
        App app = App.create();

        app.configure( ( Binder binder ) -> {
            binder.bindModule( TestMemberModule.class ) //
                  .named( "Test" ) //
                  .printWriter( new PrintWriter( System.out ) );
        } );


        return app;
    }


    @Test
    public void testAcquire() throws Exception {

        App app = buildApp();
        ServiceLocatorUtilities.dumpAllDescriptors( app.getInstance( ServiceLocator.class ) );

        TransactionControl txCtrl = app.getInstance( TransactionControl.class );

        TestMember tm1;
        TestMember tm2;
        try ( Transaction tx = txCtrl.start() ) {
            assertEquals( TransactionState.ACTIVE, tx.getStatus() );

            tm1 = app.getInstance( TestMember.class );
            assertEquals( tm1, app.getInstance( TestMember.class ) );

            tx.commit();

        }

        try ( Transaction tx = txCtrl.start() ) {
            assertEquals( TransactionState.ACTIVE, tx.getStatus() );

            tm2 = app.getInstance( TestMember.class );
            assertEquals( tm2, app.getInstance( TestMember.class ) );

            tx.commit();
        }

        Assert.assertNotEquals( tm1, tm2 );

        app.close();
    }

    @Test( expected = TransactionAlreadyStartedException.class )
    public void testDoubleStart() throws Exception {
        try ( App app = buildApp() ) {
            TransactionControl txCtrl = app.getInstance( TransactionControl.class );
            txCtrl.start();
            txCtrl.start();
        }
    }


    @Test( expected = NoTransactionException.class )
    public void testDoubleCommit() throws Exception {
        try ( App app = buildApp() ) {
            TransactionControl txCtrl = app.getInstance( TransactionControl.class );
            Transaction tx = txCtrl.start();
            tx.commit();
            tx.commit();
        }
    }

    @Test( expected = NoTransactionException.class )
    public void testCommitRollback() throws Exception {
        try ( App app = buildApp() ) {
            TransactionControl txCtrl = app.getInstance( TransactionControl.class );
            Transaction tx = txCtrl.start();
            tx.commit();
            tx.rollback();
        }
    }


    @Test( expected = NoTransactionException.class )
    public void testDoubleRollback() throws Exception {
        try ( App app = buildApp() ) {
            TransactionControl txCtrl = app.getInstance( TransactionControl.class );
            Transaction tx = txCtrl.start();
            tx.rollback();
            tx.rollback();
        }
    }

    @Test( expected = NoTransactionException.class )
    public void testRollbackCommit() throws Exception {
        try ( App app = buildApp() ) {
            TransactionControl txCtrl = app.getInstance( TransactionControl.class );
            Transaction tx = txCtrl.start();
            tx.rollback();
            tx.commit();
        }
    }

    @Test
    public void testCommitClose() throws Exception {
        try ( App app = buildApp() ) {
            TransactionControl txCtrl = app.getInstance( TransactionControl.class );
            Transaction tx = txCtrl.start();
            tx.commit();
            tx.close();
        }
    }

    @Test( expected = NoTransactionException.class )
    public void testCloseCommit() throws Exception {
        try ( App app = buildApp() ) {
            TransactionControl txCtrl = app.getInstance( TransactionControl.class );
            Transaction tx = txCtrl.start();
            tx.close();
            tx.commit();
        }
    }


    @Test
    public void testRollbackClose() throws Exception {
        try ( App app = buildApp() ) {
            TransactionControl txCtrl = app.getInstance( TransactionControl.class );
            Transaction tx = txCtrl.start();
            tx.rollback();
            tx.close();
        }
    }

    @Test( expected = NoTransactionException.class )
    public void testCloseRollback() throws Exception {
        try ( App app = buildApp() ) {
            TransactionControl txCtrl = app.getInstance( TransactionControl.class );
            Transaction tx = txCtrl.start();
            tx.close();
            tx.rollback();
        }
    }


}
