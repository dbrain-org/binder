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

package org.dbrain.yaw.txs.artifacts;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import org.dbrain.yaw.scope.TransactionScoped;

import java.io.PrintWriter;
import java.io.Writer;

/**
* Created by epoitras on 3/2/15.
*/
public class TransactionTestModule extends AbstractModule {

    private final PrintWriter printWriter;

    public TransactionTestModule( Writer writer ) {
        this.printWriter = new PrintWriter( writer );
    }

    @Override
    protected void configure() {
    }

    @TransactionScoped
    @Provides
    @MemberA
    public TestMember getMemberA() {
        return new TestMember( printWriter, "MemberA" );
    }

    @TransactionScoped
    @Provides
    @MemberB
    public TestMember getMemberB() {
        return new TestMember( printWriter, "MemberB" );
    }

    @TransactionScoped
    @Provides
    @MemberC
    public TestMember getMemberC() {
        return new TestMember( printWriter, "MemberC" ).failOnFlush();
    }

    @TransactionScoped
    @Provides
    @MemberD
    public TestMember getMemberD() {
        return new TestMember( printWriter, "MemberD" ).failOnCommit();
    }

}
