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

package org.dbrain.binder.txs.features;

import org.dbrain.binder.app.Binder;
import org.dbrain.binder.directory.Qualifiers;
import org.dbrain.binder.lifecycle.TransactionScoped;
import org.dbrain.binder.system.app.QualifiedComponent;
import org.dbrain.binder.system.txs.TransactionMember;
import org.dbrain.binder.txs.impl.TestMember;

import javax.inject.Inject;
import java.io.PrintWriter;


/**
 * Created by epoitras on 3/5/15.
 */
public class TestMemberComponent extends QualifiedComponent<TestMemberComponent> {

    private PrintWriter pw;
    private String      name;
    private boolean failOnFlush  = false;
    private boolean failOncommit = false;


    @Inject
    public TestMemberComponent( Binder.BindingContext cc ) {
        super();
        cc.onBind( ( binder ) -> {
            Qualifiers qualifiers = buildQualifiers();
            binder.bindService( TestMember.class ) //
                    .qualifiedBy( qualifiers ) //
                    .providedBy( () -> new TestMember( pw, name, failOnFlush, failOncommit ) ) //
                    .to( TestMember.class ) //
                    .to( TransactionMember.class ) //
                    .in( TransactionScoped.class );
        } );
    }

    @Override
    protected TestMemberComponent self() {
        return this;
    }

    public TestMemberComponent printWriter( PrintWriter pw ) {
        this.pw = pw;
        return self();
    }

    public TestMemberComponent named( String name ) {
        this.name = name;
        return super.named( name );
    }

    public TestMemberComponent failOnFlush() {
        this.failOnFlush = true;
        return self();
    }

    public TestMemberComponent failOnCommit() {
        this.failOncommit = true;
        return self();
    }

}
