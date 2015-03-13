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

package org.dbrain.yaw.system.http.server;

import org.dbrain.yaw.system.lifecycle.ContextRegistry;
import org.dbrain.yaw.system.scope.RequestScopeContext;
import org.dbrain.yaw.system.scope.SessionScopeContext;

import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Http filers that allows to use @SessionScoped and @RequestScoped in association with an http end-point.
 */
public class StandardScopeFilter implements Filter {

    private final RequestScopeContext requestScopeContext;
    private final SessionScopeContext sessionScopeContext;

    @Inject
    public StandardScopeFilter( RequestScopeContext requestScopeContext, SessionScopeContext sessionScopeContext ) {
        this.requestScopeContext = requestScopeContext;
        this.sessionScopeContext = sessionScopeContext;
    }

    @Override
    public void init( FilterConfig filterConfig ) throws ServletException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void doFilter( ServletRequest servletRequest,
                          ServletResponse servletResponse,
                          FilterChain filterChain ) throws IOException, ServletException {

        requestScopeContext.enter();

        // Go fetch the scope registry in the session.
        sessionScopeContext.enter( () -> {
            HttpServletRequest httpRequest = (HttpServletRequest)servletRequest;
            HttpSession session = httpRequest.getSession( false );
            if ( session != null ) {
                ContextRegistry registry = (ContextRegistry) session.getAttribute( ContextRegistry.class.getName() );
                if ( registry == null ) {
                    registry = new ContextRegistry();
                    session.setAttribute( ContextRegistry.class.getName(), registry );
                }
                return registry;
            } else {
                return null;
            }
        });
        try {
            filterChain.doFilter( servletRequest, servletResponse );
        } finally {
            requestScopeContext.leave();
            sessionScopeContext.leave();
        }
    }

    @Override
    public void destroy() {
    }

}
