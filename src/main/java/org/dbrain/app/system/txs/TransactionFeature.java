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

import org.dbrain.app.conf.Configuration;
import org.dbrain.app.conf.Feature;
import org.dbrain.app.lifecycle.TransactionScoped;
import org.dbrain.app.system.txs.jdbc.JdbcConnectionWrapper;
import org.dbrain.app.txs.TransactionControl;
import org.glassfish.hk2.api.Context;
import org.glassfish.hk2.api.TypeLiteral;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by epoitras on 3/5/15.
 */
public class TransactionFeature implements Feature{

    private Configuration config;

    @Inject
    public TransactionFeature( Configuration config ) {
        this.config = config;
    }

    @Override
    public void complete() {

        config.bind( TransactionManager.class )
                .to( TransactionControl.class )
                .to( new TypeLiteral<Context<TransactionScoped>>() {}.getType() )
                .in( Singleton.class )
                .complete();

        config.bind( JdbcConnectionWrapper.class )
                .to( TransactionMember.Wrapper.class )
                .in( Singleton.class )
                .complete();

    }

}
