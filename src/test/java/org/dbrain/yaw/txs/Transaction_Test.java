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
import org.dbrain.yaw.system.modules.TransactionModule;
import org.dbrain.yaw.txs.artifacts.MemberD;
import org.dbrain.yaw.txs.exceptions.CommitFailedException;
import org.dbrain.yaw.txs.artifacts.MemberA;
import org.dbrain.yaw.txs.artifacts.MemberB;
import org.dbrain.yaw.txs.artifacts.MemberC;
import org.dbrain.yaw.txs.artifacts.TestMember;
import org.dbrain.yaw.txs.artifacts.TransactionTestModule;
import org.junit.Test;

import java.io.StringWriter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Test generic transaction scenarios.
 */
public class Transaction_Test {

    @Test
    public void testSingleMemberCommit() throws Exception {

        StringWriter sw = new StringWriter();
        Injector injector = Guice.createInjector( new TransactionModule(), new TransactionTestModule( sw ) );
        TransactionControl txctl = injector.getInstance( TransactionControl.class );

        try ( Transaction tx = txctl.start() ) {

            // start transaction by requiring a transactional scoped member.
            TestMember testMember = injector.getInstance( Key.get( TestMember.class, MemberA.class ) );
            assertEquals( tx.getStatus(), TransactionState.ACTIVE );

            // Commit transaction.
            tx.commit();
            assertEquals( tx.getStatus(), TransactionState.COMMIT );

            // Discard
            tx.close();
            assertEquals( tx.getStatus(), TransactionState.COMMIT );

            // Assert member calls
            assertNotNull( testMember );
            assertEquals( sw.toString(), "flush:MemberA;commit:MemberA;" );

        }

    }

    @Test
    public void testMultipleMemberFlow() throws Exception {

        StringWriter sw = new StringWriter();
        Injector injector = Guice.createInjector( new TransactionModule(), new TransactionTestModule( sw ) );
        TransactionControl ctrl = injector.getInstance( TransactionControl.class );

        try (Transaction tx = ctrl.start() ) {

            // start transaction by requiring a transactional scoped member.
            TestMember testMemberA = injector.getInstance( Key.get( TestMember.class, MemberA.class ) );
            assertEquals( tx.getStatus(), TransactionState.ACTIVE );
            TestMember testMemberB = injector.getInstance( Key.get( TestMember.class, MemberB.class ) );
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
        Injector injector = Guice.createInjector( new TransactionModule(), new TransactionTestModule( sw ) );
        TransactionControl ctrl = injector.getInstance( TransactionControl.class );

        try (Transaction tx = ctrl.start() ) {

            // start transaction by requiring a transactional scoped member.
            TestMember testMember = injector.getInstance( Key.get( TestMember.class, MemberA.class ) );
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
        Injector injector = Guice.createInjector( new TransactionModule(), new TransactionTestModule( sw ) );
        TransactionControl txctl = injector.getInstance( TransactionControl.class );

        try ( Transaction tx = txctl.start() ) {

            // start transaction by requiring a transactional scoped member.
            TestMember testMemberA = injector.getInstance( Key.get( TestMember.class, MemberA.class ) );
            assertEquals( TransactionState.ACTIVE, tx.getStatus() );
            TestMember testMemberB = injector.getInstance( Key.get( TestMember.class, MemberB.class ) );
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
        Injector injector = Guice.createInjector( new TransactionModule(), new TransactionTestModule( sw ) );
        TransactionControl txctl = injector.getInstance( TransactionControl.class );

        try ( Transaction tx = txctl.start() ) {

            // start transaction by requiring a transactional scoped member.
            TestMember testMemberC = injector.getInstance( Key.get( TestMember.class, MemberC.class ) );
            assertEquals( tx.getStatus(), TransactionState.ACTIVE );
            TestMember testMemberA = injector.getInstance( Key.get( TestMember.class, MemberA.class ) );
            assertEquals( tx.getStatus(), TransactionState.ACTIVE );
            TestMember testMemberB = injector.getInstance( Key.get( TestMember.class, MemberB.class ) );
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
        Injector injector = Guice.createInjector( new TransactionModule(), new TransactionTestModule( sw ) );
        TransactionControl txctl = injector.getInstance( TransactionControl.class );

        try ( Transaction tx = txctl.start() ) {

            // start transaction by requiring a transactional scoped member.
            TestMember testMemberD = injector.getInstance( Key.get( TestMember.class, MemberD.class ) );
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
            assertEquals( sw.toString(),
                          "flush:MemberD;rollback:MemberD;" );

        }

    }

    @Test
    public void testMultipleMembersWithFailingCommitCompleteRollback() throws Exception {

        StringWriter sw = new StringWriter();
        Injector injector = Guice.createInjector( new TransactionModule(), new TransactionTestModule( sw ) );
        TransactionControl txctl = injector.getInstance( TransactionControl.class );

        try ( Transaction tx = txctl.start() ) {

            // start transaction by requiring a transactional scoped member.
            TestMember testMemberA = injector.getInstance( Key.get( TestMember.class, MemberA.class ) );
            assertEquals( tx.getStatus(), TransactionState.ACTIVE );

            // start transaction by requiring a transactional scoped member.
            TestMember testMemberD = injector.getInstance( Key.get( TestMember.class, MemberD.class ) );
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
            assertEquals( sw.toString(),
                          "flush:MemberD;flush:MemberA;rollback:MemberD;rollback:MemberA;" );

        }

    }

    @Test
    public void testMultipleMembersWithFailingCommitPartialRollback() throws Exception {

        StringWriter sw = new StringWriter();
        Injector injector = Guice.createInjector( new TransactionModule(), new TransactionTestModule( sw ) );
        TransactionControl txctl = injector.getInstance( TransactionControl.class );

        try ( Transaction tx = txctl.start() ) {


            // start transaction by requiring a transactional scoped member.
            TestMember testMemberD = injector.getInstance( Key.get( TestMember.class, MemberD.class ) );
            assertEquals( tx.getStatus(), TransactionState.ACTIVE );

            // start transaction by requiring a transactional scoped member.
            TestMember testMemberA = injector.getInstance( Key.get( TestMember.class, MemberA.class ) );
            assertEquals( tx.getStatus(), TransactionState.ACTIVE );

            // Commit transaction.
            CommitFailedException commitException = null;
            try {
                tx.commit();
            } catch ( CommitFailedException e ) {
                commitException = e;
            }
            assertNotNull( "Except FailedToCommitException.", commitException );
            assertEquals( tx.getStatus(), TransactionState.PARTIAL_ROLLBACK );

            // Assert member calls
            assertNotNull( testMemberA );
            assertNotNull( testMemberD );

            // Member flushed and committed in reverse order.
            assertEquals( sw.toString(),
                          "flush:MemberA;flush:MemberD;commit:MemberA;rollback:MemberD;" );

        }

    }

}
