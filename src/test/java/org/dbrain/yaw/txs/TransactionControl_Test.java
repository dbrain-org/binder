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

import org.dbrain.yaw.app.App;
import org.dbrain.yaw.txs.impl.TestMember;
import org.dbrain.yaw.txs.features.TestMemberFeature;
import org.junit.Assert;
import org.junit.Test;

import java.io.PrintWriter;

import static org.junit.Assert.assertEquals;

/**
 * Created by epoitras on 3/5/15.
 */
public class TransactionControl_Test {

    public App buildApp() {
        App app = new App();

        app.addFeature( TestMemberFeature.class ).named( "Test" ).printWriter( new PrintWriter( System.out ) ).commit();

        return app;
    }


    @Test
    public void testAcquire() throws Exception {

        App app = buildApp();

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
}
