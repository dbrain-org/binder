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
import org.dbrain.yaw.system.txs.TransactionManager;
import org.dbrain.yaw.system.txs.exceptions.CommitFailedException;
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
 * Created by epoitras on 3/2/15.
 */
public class TransactionScope_Test {

    @Test
    public void testSingleMemberCommit() throws Exception {

        StringWriter sw = new StringWriter();
        Injector injector = Guice.createInjector( new TransactionModule(), new TransactionTestModule( sw ) );
        TransactionManager tm = injector.getInstance( TransactionManager.class );

        try {
            assertEquals( tm.getStatus(), TransactionState.NONE );

            // start transaction by requiring a transactional scoped member.
            TestMember testMember = injector.getInstance( Key.get( TestMember.class, MemberA.class ) );
            assertEquals( tm.getStatus(), TransactionState.RUNNING );

            // Commit transaction.
            tm.commit();
            assertEquals( tm.getStatus(), TransactionState.COMMITTED );

            // Discard
            tm.discard();
            assertEquals( tm.getStatus(), TransactionState.NONE );

            // Assert member calls
            assertNotNull( testMember );
            assertEquals( sw.toString(), "flush:MemberA;commit:MemberA;" );

        } finally {
            tm.discard();
        }

    }

    @Test
    public void testMultipleMemberFlow() throws Exception {

        StringWriter sw = new StringWriter();
        Injector injector = Guice.createInjector( new TransactionModule(), new TransactionTestModule( sw ) );
        TransactionManager tm = injector.getInstance( TransactionManager.class );

        try {
            assertEquals( tm.getStatus(), TransactionState.NONE );

            // start transaction by requiring a transactional scoped member.
            TestMember testMemberA = injector.getInstance( Key.get( TestMember.class, MemberA.class ) );
            assertEquals( tm.getStatus(), TransactionState.RUNNING );
            TestMember testMemberB = injector.getInstance( Key.get( TestMember.class, MemberB.class ) );
            assertEquals( tm.getStatus(), TransactionState.RUNNING );

            // Commit transaction.
            tm.commit();
            assertEquals( tm.getStatus(), TransactionState.COMMITTED );

            // Discard
            tm.discard();
            assertEquals( tm.getStatus(), TransactionState.NONE );

            // Assert member calls
            assertNotNull( testMemberA );
            assertNotNull( testMemberB );

            // Member flushed and committed in reverse order.
            assertEquals( sw.toString(), "flush:MemberB;flush:MemberA;commit:MemberB;commit:MemberA;" );

        } finally {
            tm.discard();
        }

    }

    @Test
    public void testSingleMemberRollback() throws Exception {

        StringWriter sw = new StringWriter();
        Injector injector = Guice.createInjector( new TransactionModule(), new TransactionTestModule( sw ) );
        TransactionManager tm = injector.getInstance( TransactionManager.class );

        try {
            assertEquals( tm.getStatus(), TransactionState.NONE );

            // start transaction by requiring a transactional scoped member.
            TestMember testMember = injector.getInstance( Key.get( TestMember.class, MemberA.class ) );
            assertEquals( TransactionState.RUNNING, tm.getStatus() );

            // Commit transaction.
            tm.rollback();
            assertEquals( TransactionState.ROLLED_BACK, tm.getStatus() );

            // Discard
            tm.discard();
            assertEquals( tm.getStatus(), TransactionState.NONE );

            // Assert member calls
            assertNotNull( testMember );
            assertEquals( sw.toString(), "rollback:MemberA;" );

        } finally {
            tm.discard();
        }

    }

    @Test
    public void testMultipleMemberRollback() throws Exception {

        StringWriter sw = new StringWriter();
        Injector injector = Guice.createInjector( new TransactionModule(), new TransactionTestModule( sw ) );
        TransactionManager tm = injector.getInstance( TransactionManager.class );

        try {
            assertEquals( tm.getStatus(), TransactionState.NONE );

            // start transaction by requiring a transactional scoped member.
            TestMember testMemberA = injector.getInstance( Key.get( TestMember.class, MemberA.class ) );
            assertEquals( TransactionState.RUNNING, tm.getStatus() );
            TestMember testMemberB = injector.getInstance( Key.get( TestMember.class, MemberB.class ) );
            assertEquals( TransactionState.RUNNING, tm.getStatus() );

            // Commit transaction.
            tm.rollback();
            assertEquals( TransactionState.ROLLED_BACK, tm.getStatus() );

            // Discard
            tm.discard();
            assertEquals( tm.getStatus(), TransactionState.NONE );

            // Assert member calls
            assertNotNull( testMemberA );
            assertNotNull( testMemberB );
            assertEquals( sw.toString(), "rollback:MemberB;rollback:MemberA;" );

        } finally {
            tm.discard();
        }

    }

    @Test
    public void testMultipleMemberFlowWithFailingFlush() throws Exception {

        StringWriter sw = new StringWriter();
        Injector injector = Guice.createInjector( new TransactionModule(), new TransactionTestModule( sw ) );
        TransactionManager tm = injector.getInstance( TransactionManager.class );

        try {
            assertEquals( tm.getStatus(), TransactionState.NONE );

            // start transaction by requiring a transactional scoped member.
            TestMember testMemberC = injector.getInstance( Key.get( TestMember.class, MemberC.class ) );
            assertEquals( tm.getStatus(), TransactionState.RUNNING );
            TestMember testMemberA = injector.getInstance( Key.get( TestMember.class, MemberA.class ) );
            assertEquals( tm.getStatus(), TransactionState.RUNNING );
            TestMember testMemberB = injector.getInstance( Key.get( TestMember.class, MemberB.class ) );
            assertEquals( tm.getStatus(), TransactionState.RUNNING );

            // Commit transaction.
            CommitFailedException commitException = null;
            try {
                tm.commit();
            } catch( CommitFailedException e ) {
                commitException = e;
            }
            assertNotNull( "Except FailedToCommitException.", commitException );
            assertEquals( tm.getStatus(), TransactionState.COMMIT_FAILED );

            // Discard
            tm.discard();
            assertEquals( tm.getStatus(), TransactionState.NONE );

            // Assert member calls
            assertNotNull( testMemberC );
            assertNotNull( testMemberA );
            assertNotNull( testMemberB );

            // Member flushed and committed in reverse order.
            assertEquals( sw.toString(), "flush:MemberB;flush:MemberA;rollback:MemberB;rollback:MemberA;rollback:MemberC;" );

        } finally {
            tm.discard();
        }

    }


}
