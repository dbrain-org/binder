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

package org.dbrain.binder.txs;

/**
 * Interface to control transactions.
 */
public interface TransactionControl {

    /**
     * @return The current transaction.
     */
    Transaction current();

    /**
     * @return A newly created "ACTIVE" transaction.
     *
     * @throws org.dbrain.binder.txs.exceptions.TransactionAlreadyStartedException if there is already a transaction.
     */
    Transaction start() throws TransactionException;

}
