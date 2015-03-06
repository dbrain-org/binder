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

package org.dbrain.yaw.system.modules;

import org.dbrain.yaw.scope.TransactionScoped;
import org.dbrain.yaw.system.txs.TransactionManager;
import org.dbrain.yaw.system.txs.TransactionMember;
import org.dbrain.yaw.system.txs.jdbc.JdbcConnectionWrapper;
import org.dbrain.yaw.txs.TransactionControl;
import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.Context;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.api.TypeLiteral;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.annotation.Annotation;

/**
 * Created by epoitras on 3/5/15.
 */
public class TransactionBinder extends AbstractBinder{

    @Override
    protected void configure() {
        bind( TransactionManager.class )
                .to( TransactionControl.class )
                .to( TransactionManager.class )
                .in( Singleton.class );

        bind( TransactionContext.class )
                .to( new TypeLiteral<Context<TransactionScoped>>() {} )
                .in( Singleton.class );

        bindAsContract( JdbcConnectionWrapper.class )
                .to( TransactionMember.Wrapper.class )
                .in( Singleton.class );
    }

    public static class TransactionContext implements Context<TransactionScoped> {

        private boolean active = true;
        private final TransactionManager transactionManager;

        @Inject
        public TransactionContext( TransactionManager transactionManager ) {
            this.transactionManager = transactionManager;
        }

        @Override
        public Class<? extends Annotation> getScope() {
            return TransactionScoped.class;
        }

        @Override
        public <U> U findOrCreate( ActiveDescriptor<U> activeDescriptor, ServiceHandle<?> root ) {
            return transactionManager.get( activeDescriptor, () -> activeDescriptor.create( root ) );

        }

        @Override
        public boolean containsKey( ActiveDescriptor<?> descriptor ) {
            return transactionManager.contains( descriptor );
        }

        @Override
        public void destroyOne( ActiveDescriptor<?> descriptor ) {
            // TODO ?!
        }

        @Override
        public boolean supportsNullCreation() {
            return false;
        }

        @Override
        public boolean isActive() {
            return transactionManager.current() != null && active;
        }

        @Override
        public void shutdown() {
            active = false;
        }
    }

}
