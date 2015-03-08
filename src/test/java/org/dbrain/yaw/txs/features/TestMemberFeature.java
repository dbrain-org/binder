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

package org.dbrain.yaw.txs.features;

import org.dbrain.yaw.scope.TransactionScoped;
import org.dbrain.yaw.system.config.BaseQualifiedConfigurator;
import org.dbrain.yaw.system.txs.TransactionMember;
import org.dbrain.yaw.txs.impl.TestMember;
import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.hk2.utilities.binding.ScopedBindingBuilder;

import javax.inject.Inject;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;


/**
 * Created by epoitras on 3/5/15.
 */
public class TestMemberFeature extends BaseQualifiedConfigurator<TestMemberFeature> {

    private final ServiceLocator app;

    private PrintWriter pw;
    private String name;
    private boolean failOnFlush = false;
    private boolean failOncommit = false;


    @Inject
    public TestMemberFeature( ServiceLocator app ) {
        this.app = app;
    }

    @Override
    protected TestMemberFeature self() {
        return this;
    }

    public TestMemberFeature printWriter( PrintWriter pw ) {
        this.pw = pw;
        return self();
    }

    public TestMemberFeature named( String name ) {
        this.name = name;
        return super.named( name );
    }

    public TestMemberFeature failOnFlush() {
        this.failOnFlush = true;
        return self();
    }

    public TestMemberFeature failOnCommit() {
        this.failOncommit = true;
        return self();
    }

    public void commit() {

        ServiceLocatorUtilities.bind( app, new Binder() );

    }

    public class TestMemberFactory implements Factory<TestMember> {

        @Override
        public TestMember provide() {
            return new TestMember( pw, name, failOnFlush, failOncommit );
        }

        @Override
        public void dispose( TestMember instance ) {
        }
    }


    private class Binder extends AbstractBinder {

        @Override
        public void configure() {
            ScopedBindingBuilder sbb = bindFactory( new TestMemberFactory() ) //
                    .to( TestMember.class ) //
                    .to( TransactionMember.class ) //
                    .in( TransactionScoped.class );

            for ( Annotation a: getQualifiers() ) {
                sbb.qualifiedBy( a );
            }
        }
    }
}
