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

package org.dbrain.app.txs;

import org.dbrain.app.App;
import org.dbrain.app.conf.Configuration;
import org.dbrain.app.system.app.AppImpl;
import org.dbrain.app.txs.features.TestMemberFeature;
import org.dbrain.app.txs.impl.TestMember;
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
        App app = new AppImpl();

        app.configure( ( Configuration config ) -> {
            config.addFeature( TestMemberFeature.class ) //
                    .named( "Test" ) //
                    .printWriter( new PrintWriter( System.out ) ) //
                    .complete();
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
}
