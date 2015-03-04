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

package org.dbrain.yaw.system.txs.http;

import org.dbrain.yaw.system.txs.TransactionManager;
import org.dbrain.yaw.txs.Transaction;
import org.dbrain.yaw.txs.TransactionState;

import javax.inject.Provider;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: epoitras
 * Date: 15/07/13
 * Time: 8:18 AM
 * To change this template use File | Settings | File Templates.
 */
public class TransactionFilter implements Filter {

    private final Provider<TransactionManager> transactionManager;

    public TransactionFilter( Provider<TransactionManager> transactionManager ) {
        this.transactionManager = transactionManager;
    }

    @Override
    public void init( FilterConfig filterConfig ) throws ServletException {
    }

    @Override
    public void doFilter( ServletRequest servletRequest,
                          ServletResponse servletResponse,
                          FilterChain filterChain ) throws IOException, ServletException {

        TransactionManager c = transactionManager.get();
        try ( Transaction tx = c.start() ) {

            // Filter.
            filterChain.doFilter( servletRequest, servletResponse );

            // When there is no exception, we COMMIT.
            if ( tx.getStatus() == TransactionState.ACTIVE ) {
                tx.commit();
            }

        }
    }

    @Override
    public void destroy() {
    }
}
