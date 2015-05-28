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

package org.dbrain.binder.txs.impl;

import org.dbrain.binder.lifecycle.TransactionScoped;
import org.dbrain.binder.system.txs.TransactionMember;
import org.dbrain.binder.txs.TransactionException;

import java.io.PrintWriter;

/**
 * Created by epoitras on 3/2/15.
 */
@TransactionScoped
public class TestMember implements TransactionMember {

    private final PrintWriter printWriter;
    private final String      name;
    private final boolean     failOnFlush;
    private final boolean     failOnCommit;

    public TestMember( PrintWriter printWriter, String name, boolean failOnFlush, boolean failOnCommit ) {
        this.printWriter = printWriter;
        this.name = name;
        this.failOnFlush = failOnFlush;
        this.failOnCommit = failOnCommit;
    }

    @Override
    public void flush() throws TransactionException {
        if ( failOnFlush ) {
            throw new IllegalStateException();
        }
        printWriter.print( "flush:" + name + ";" );
    }

    @Override
    public void commit() throws TransactionException {
        if ( failOnCommit ) {
            throw new IllegalStateException();
        }
        printWriter.print( "commit:" + name + ";" );
    }

    @Override
    public void rollback() throws TransactionException {
        printWriter.print( "rollback:" + name + ";" );
    }
}
