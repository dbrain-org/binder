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

package org.dbrain.yaw.system.http.server.filters;

import org.dbrain.yaw.system.modules.ScopeModule;
import org.dbrain.yaw.system.http.server.HttpContext;
import org.dbrain.yaw.system.scope.ScopeRegistry;
import org.dbrain.yaw.system.scope.ScopeRegistryProvider;

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
public class ScopeFilter implements Filter {

    /**
     * Attributes to contains the scope.
     */
    public static final String ATTRIBUTE_SCOPE = ScopeFilter.class.getName() + ".scope";


    /**
     * Get (and optionally create) a ManagedScope bound to the request.
     */
    public static ScopeRegistryProvider getRequestScope( final HttpServletRequest request ) {
        return new ScopeRegistryProvider() {

            @Override
            public ScopeRegistry get() {
                ScopeRegistry scope = (ScopeRegistry) request.getAttribute( ATTRIBUTE_SCOPE );
                if ( scope == null ) {
                    scope = new ScopeRegistry();
                    request.setAttribute( ATTRIBUTE_SCOPE, scope );
                }
                return scope;
            }

            @Override
            public void close() throws Exception {
                ScopeRegistry scope = (ScopeRegistry) request.getAttribute( ATTRIBUTE_SCOPE );
                if ( scope != null ) {
                    scope.close();
                }
            }

        };
    }

    /**
     * Create a session scope in relation to the http session via the request.
     */
    public static ScopeRegistryProvider getSessionScope( final HttpServletRequest request ) {
        return new ScopeRegistryProvider() {

            @Override
            public ScopeRegistry get() {
                HttpSession session = request.getSession();
                if ( session != null ) {
                    ScopeRegistry scope = (ScopeRegistry) session.getAttribute( ATTRIBUTE_SCOPE );

                    if ( scope == null ) {
                        scope = new ScopeRegistry();
                        session.setAttribute( ATTRIBUTE_SCOPE, scope );
                    }

                    return scope;
                } else {
                    return null;
                }
            }

            @Override
            public void close() throws Exception {
                HttpSession session = request.getSession( false );
                if ( session != null ) {
                    ScopeRegistry scope = (ScopeRegistry) session.getAttribute( ATTRIBUTE_SCOPE );
                    if ( scope != null ) {
                        scope.close();
                    }
                }
            }

        };

    }

    @Override
    public void init( FilterConfig filterConfig ) throws ServletException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void doFilter( ServletRequest servletRequest,
                          ServletResponse servletResponse,
                          FilterChain filterChain ) throws IOException, ServletException {
        HttpServletRequest hsr = (HttpServletRequest) servletRequest;

        HttpContext.enter( hsr );
        try ( ScopeRegistryProvider requestProvider = getRequestScope( hsr ); ScopeRegistryProvider sessionProvider = getSessionScope(
                hsr ) ) {

            ScopeModule.REQUEST_SCOPE_REGISTRY.set( requestProvider );
            ScopeModule.SESSION_SCOPE_REGISTRY.set( sessionProvider );
            filterChain.doFilter( servletRequest, servletResponse );
        } catch ( Exception e ) {
            throw new ServletException( e );
        } finally {

            ScopeModule.REQUEST_SCOPE_REGISTRY.set( null );
            ScopeModule.SESSION_SCOPE_REGISTRY.set( null );
            HttpContext.leave();
        }
    }

    @Override
    public void destroy() {
    }
}
