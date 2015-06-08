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
import org.dbrain.binder.txs.exceptions.CommitFailedException;
import org.dbrain.binder.txs.features.TestMemberComponent;
import org.dbrain.binder.txs.impl.TestMember;
import org.junit.Test;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Test generic transaction scenarios.
 */
public class Transaction_Test {

    private App buildApp( Writer writer ) {
        App app = App.create();

        PrintWriter pw = new PrintWriter( writer );
        app.configure( ( Binder binder ) -> {
            binder.bindComponent( TestMemberComponent.class ).named( "MemberA" ).printWriter( pw );
            binder.bindComponent( TestMemberComponent.class ).named( "MemberB" ).printWriter( pw );
            binder.bindComponent( TestMemberComponent.class ).named( "MemberC" ).printWriter( pw ).failOnFlush();
            binder.bindComponent( TestMemberComponent.class ).named( "MemberD" ).printWriter( pw ).failOnCommit();
        } );

        return app;
    }


    @Test
    public void testSingleMemberCommit() throws Exception {

        StringWriter sw = new StringWriter();

        App app = buildApp( sw );
        TransactionControl txctl = app.getInstance( TransactionControl.class );

        try ( Transaction tx = txctl.start() ) {

            // start transaction by requiring a transactional scoped member.
            TestMember testMemberA = app.getInstance( TestMember.class, "MemberA" );

            // Commit transaction.
            tx.commit();
            assertEquals( tx.getStatus(), TransactionState.COMMIT );

            // Discard
            tx.close();
            assertEquals( tx.getStatus(), TransactionState.COMMIT );

            // Assert member calls
            assertNotNull( testMemberA );
            assertEquals( sw.toString(), "flush:MemberA;commit:MemberA;" );

        }

    }

    @Test
    public void testMultipleMemberFlow() throws Exception {

        StringWriter sw = new StringWriter();
        App injector = buildApp( sw );
        TransactionControl ctrl = injector.getInstance( TransactionControl.class );

        try ( Transaction tx = ctrl.start() ) {

            // start transaction by requiring a transactional scoped member.
            TestMember testMemberA = injector.getInstance( TestMember.class, "MemberA" );
            assertEquals( tx.getStatus(), TransactionState.ACTIVE );
            TestMember testMemberB = injector.getInstance( TestMember.class, "MemberB" );
            assertEquals( tx.getStatus(), TransactionState.ACTIVE );

            // Commit transaction.
            tx.commit();
            assertEquals( tx.getStatus(), TransactionState.COMMIT );

            // Discard
            tx.close();
            assertEquals( tx.getStatus(), TransactionState.COMMIT );

            // Assert member calls
            assertNotNull( testMemberA );
            assertNotNull( testMemberB );

            // Member flushed and committed in reverse order.
            assertEquals( sw.toString(), "flush:MemberB;flush:MemberA;commit:MemberB;commit:MemberA;" );

        }

    }

    @Test
    public void testSingleMemberRollback() throws Exception {

        StringWriter sw = new StringWriter();
        App injector = buildApp( sw );
        TransactionControl ctrl = injector.getInstance( TransactionControl.class );

        try ( Transaction tx = ctrl.start() ) {

            // start transaction by requiring a transactional scoped member.
            TestMember testMember = injector.getInstance( TestMember.class, "MemberA" );
            assertEquals( TransactionState.ACTIVE, tx.getStatus() );

            // Commit transaction.
            tx.rollback();
            assertEquals( TransactionState.ROLLBACK, tx.getStatus() );

            // Discard
            tx.close();
            assertEquals( tx.getStatus(), TransactionState.ROLLBACK );

            // Assert member calls
            assertNotNull( testMember );
            assertEquals( sw.toString(), "rollback:MemberA;" );

        }

    }

    @Test
    public void testMultipleMemberRollback() throws Exception {

        StringWriter sw = new StringWriter();
        App injector = buildApp( sw );
        TransactionControl ctrl = injector.getInstance( TransactionControl.class );

        try ( Transaction tx = ctrl.start() ) {

            // start transaction by requiring a transactional scoped member.
            TestMember testMemberA = injector.getInstance( TestMember.class, "MemberA" );
            assertEquals( TransactionState.ACTIVE, tx.getStatus() );
            TestMember testMemberB = injector.getInstance( TestMember.class, "MemberB" );
            assertEquals( TransactionState.ACTIVE, tx.getStatus() );

            // Commit transaction.
            tx.rollback();
            assertEquals( TransactionState.ROLLBACK, tx.getStatus() );

            // Discard
            tx.close();
            assertEquals( tx.getStatus(), TransactionState.ROLLBACK );

            // Assert member calls
            assertNotNull( testMemberA );
            assertNotNull( testMemberB );
            assertEquals( "rollback:MemberB;rollback:MemberA;", sw.toString() );

        }

    }

    @Test
    public void testMultipleMemberFlowWithFailingFlush() throws Exception {

        StringWriter sw = new StringWriter();
        App injector = buildApp( sw );
        TransactionControl ctrl = injector.getInstance( TransactionControl.class );

        try ( Transaction tx = ctrl.start() ) {

            // start transaction by requiring a transactional scoped member.
            TestMember testMemberC = injector.getInstance( TestMember.class, "MemberC" );
            assertEquals( tx.getStatus(), TransactionState.ACTIVE );
            TestMember testMemberA = injector.getInstance( TestMember.class, "MemberA" );
            assertEquals( tx.getStatus(), TransactionState.ACTIVE );
            TestMember testMemberB = injector.getInstance( TestMember.class, "MemberB" );
            assertEquals( tx.getStatus(), TransactionState.ACTIVE );

            // Commit transaction.
            CommitFailedException commitException = null;
            try {
                tx.commit();
            } catch ( CommitFailedException e ) {
                commitException = e;
            }
            assertNotNull( "Except FailedToCommitException.", commitException );
            assertEquals( tx.getStatus(), TransactionState.ROLLBACK );

            // Assert member calls
            assertNotNull( testMemberC );
            assertNotNull( testMemberA );
            assertNotNull( testMemberB );

            // Member flushed and committed in reverse order.
            assertEquals( sw.toString(),
                          "flush:MemberB;flush:MemberA;rollback:MemberB;rollback:MemberA;rollback:MemberC;" );

        }

    }

    @Test
    public void testSingleMemberWithFailingCommit() throws Exception {

        StringWriter sw = new StringWriter();
        App injector = buildApp( sw );
        TransactionControl ctrl = injector.getInstance( TransactionControl.class );

        try ( Transaction tx = ctrl.start() ) {

            // start transaction by requiring a transactional scoped member.
            TestMember testMemberD = injector.getInstance( TestMember.class, "MemberD" );
            assertEquals( tx.getStatus(), TransactionState.ACTIVE );

            // Commit transaction.
            CommitFailedException commitException = null;
            try {
                tx.commit();
            } catch ( CommitFailedException e ) {
                commitException = e;
            }
            assertNotNull( "Except FailedToCommitException.", commitException );
            assertEquals( tx.getStatus(), TransactionState.ROLLBACK );

            // Assert member calls
            assertNotNull( testMemberD );

            // Member flushed and committed in reverse order.
            assertEquals( "flush:MemberD;rollback:MemberD;", sw.toString() );

        }

    }

    @Test
    public void testMultipleMembersWithFailingCommitCompleteRollback() throws Exception {

        StringWriter sw = new StringWriter();
        App injector = buildApp( sw );
        TransactionControl ctrl = injector.getInstance( TransactionControl.class );

        try ( Transaction tx = ctrl.start() ) {

            // start transaction by requiring a transactional scoped member.
            TestMember testMemberA = injector.getInstance( TestMember.class, "MemberA" );
            assertEquals( tx.getStatus(), TransactionState.ACTIVE );

            // start transaction by requiring a transactional scoped member.
            TestMember testMemberD = injector.getInstance( TestMember.class, "MemberD" );
            assertEquals( tx.getStatus(), TransactionState.ACTIVE );

            // Commit transaction.
            CommitFailedException commitException = null;
            try {
                tx.commit();
            } catch ( CommitFailedException e ) {
                commitException = e;
            }
            assertNotNull( "Except FailedToCommitException.", commitException );
            assertEquals( tx.getStatus(), TransactionState.ROLLBACK );

            // Assert member calls
            assertNotNull( testMemberA );
            assertNotNull( testMemberD );

            // Member flushed and committed in reverse order.
            assertEquals( "flush:MemberD;flush:MemberA;rollback:MemberD;rollback:MemberA;", sw.toString() );

        }

    }

    @Test
    public void testMultipleMembersWithFailingCommitPartialRollback() throws Exception {

        StringWriter sw = new StringWriter();
        App injector = buildApp( sw );
        TransactionControl ctrl = injector.getInstance( TransactionControl.class );

        try ( Transaction tx = ctrl.start() ) {


            // start transaction by requiring a transactional scoped member.
            TestMember testMemberD = injector.getInstance( TestMember.class, "MemberD" );
            assertEquals( tx.getStatus(), TransactionState.ACTIVE );

            // start transaction by requiring a transactional scoped member.
            TestMember testMemberA = injector.getInstance( TestMember.class, "MemberA" );
            assertEquals( tx.getStatus(), TransactionState.ACTIVE );

            // Commit transaction.
            CommitFailedException commitException = null;
            try {
                tx.commit();
            } catch ( CommitFailedException e ) {
                commitException = e;
            }
            assertNotNull( "Except FailedToCommitException.", commitException );
            assertEquals( TransactionState.PARTIAL_ROLLBACK, tx.getStatus() );

            // Assert member calls
            assertNotNull( testMemberA );
            assertNotNull( testMemberD );

            // Member flushed and committed in reverse order.
            assertEquals( "flush:MemberA;flush:MemberD;commit:MemberA;rollback:MemberD;", sw.toString() );

        }

    }

}
