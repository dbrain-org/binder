package org.dbrain.yaw.http.server.system.filters;

import org.dbrain.yaw.http.server.system.HttpContext;
import org.dbrain.yaw.scope.module.ScopeModule;
import org.dbrain.yaw.scope.system.ScopeRegistry;
import org.dbrain.yaw.scope.system.ScopeRegistryProvider;

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
    public void doFilter( ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain ) throws IOException, ServletException {
        HttpServletRequest hsr = (HttpServletRequest) servletRequest;

        HttpContext.enter( hsr );
        try ( ScopeRegistryProvider requestProvider = getRequestScope( hsr ); ScopeRegistryProvider sessionProvider = getSessionScope( hsr ); ) {

            ScopeModule.REQUEST_SCOPE_REGISTRY.set( requestProvider );
            ScopeModule.SESSION_SCOPE_REGISTRY.set( sessionProvider );
            filterChain.doFilter( servletRequest, servletResponse );
        } catch( Exception e) {
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
