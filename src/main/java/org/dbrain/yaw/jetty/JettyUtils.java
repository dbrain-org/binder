package org.dbrain.yaw.jetty;

import org.dbrain.yaw.http.server.config.CredentialsDef;
import org.dbrain.yaw.http.server.config.FormLocationDef;
import org.dbrain.yaw.http.server.config.ServletAppSecurityDef;
import org.dbrain.yaw.http.server.config.ServletContextConfig;
import org.dbrain.yaw.http.server.config.ServletConfig;
import org.dbrain.yaw.http.server.config.WebSocketConfig;
import org.dbrain.yaw.http.server.config.ServletFilterConfig;
import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.security.authentication.FormAuthenticator;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.security.Constraint;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;

import javax.servlet.DispatcherType;
import javax.websocket.server.ServerContainer;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: epoitras
 * Date: 15/07/13
 * Time: 10:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class JettyUtils {

    public static FilterHolder createFilterHolder( ServletFilterConfig def ) {
        FilterHolder result = new FilterHolder( def.getInstance() );
        return result;
    }

    public static void configureServlet( ServletContextHandler result, ServletConfig def ) {
        def.accept( new ServletConfig.Visitor() {
            @Override
            public void visit( ServletConfig.InstanceServletConfig servletDef ) {
                result.addServlet( new ServletHolder( servletDef.getInstance() ), servletDef.getPathSpec() );
            }
        } );
    }

    public static FormAuthenticator createFormAuthenticator( FormLocationDef def ) {
        return new FormAuthenticator( def.getUrl(),
                                      def.getErrorURL(),
                                      false /* no, do not dispatch but redirect (302, 301?) to the error url */ );
    }

    public static HashLoginService createHashLoginService( CredentialsDef def ) {
        return new HashLoginService( def.getRealm(), def.getFile() );
    }

    public static ConstraintSecurityHandler createConstraintSecurityHandler( ServletAppSecurityDef def ) {

        ConstraintMapping cm = new ConstraintMapping();
        cm.setPathSpec( def.getPathSpec() );
        cm.setConstraint( ConstraintSecurityHandler.createConstraint( Constraint.__FORM_AUTH,
                                                                      true, /* yes, must authenticate */
                                                                      new String[]{
                                                                              def.getCredentialsDef().getSingleRole()
                                                                      },
                                                                      Constraint.DC_NONE ) );

        ConstraintSecurityHandler sh = new ConstraintSecurityHandler();
        sh.setRealmName( def.getCredentialsDef().getRealm() );
        sh.setAuthenticator( createFormAuthenticator( def.getFormLocationDef() ) );
        sh.setLoginService( createHashLoginService( def.getCredentialsDef() ) );
        sh.setConstraintMappings( Arrays.asList( cm ) );

        return sh;
    }

    public static void configureWebSocket( ServerContainer serverContainer,
                                           WebSocketConfig webSocketDef ) throws Exception {
        webSocketDef.accept( new WebSocketConfig.Visitor() {
            @Override
            public void visit( WebSocketConfig.EndpointClassWebSocketConfig endpointClassWebSocketDef ) throws Exception {
                serverContainer.addEndpoint( endpointClassWebSocketDef.getEndpointClass() );
            }

            @Override
            public void visit( WebSocketConfig.ServerEndpointConfigWebSocketConfig serverEndpointConfig ) throws Exception {
                serverContainer.addEndpoint( serverEndpointConfig.getConfig() );
            }
        } );

    }


    public static Handler configureServletContextHandler( Server server, ServletContextConfig config ) {

        ServletContextHandler servletContextHandler = new ServletContextHandler( ( config.getSecurity() != null ) ? ServletContextHandler.SESSIONS : ServletContextHandler.NO_SESSIONS );
        servletContextHandler.setContextPath( config.getContextPath() );
        servletContextHandler.setServer( server );

        // Initialize javax.websocket layer
        if ( config.getWebSockets().size() > 0 ) {
            ServerContainer wscontainer = WebSocketServerContainerInitializer.configureContext( servletContextHandler );

            for ( WebSocketConfig wsd : config.getWebSockets() ) {
                try {
                    configureWebSocket( wscontainer, wsd );
                } catch ( Exception e ) {
                    throw new RuntimeException( e );
                }

            }
        }

        if ( config.getSecurity() != null ) {
            // no url rewritting to put session id in the path
            // TODO kilantzis - put the line below somewhere else?
            servletContextHandler.setInitParameter( "org.eclipse.jetty.servlet.SessionIdPathParameterName", "none" );
            servletContextHandler.setSecurityHandler( createConstraintSecurityHandler( config.getSecurity() ) );
        }

        for ( ServletFilterConfig filterDef : config.getFilters() ) {
            servletContextHandler.addFilter( createFilterHolder( filterDef ),
                                      filterDef.getPathSpec(),
                                      EnumSet.of( DispatcherType.REQUEST ) );
        }

        for ( ServletConfig servletDef : config.getServlets() ) {
            configureServlet( servletContextHandler, servletDef );
        }

        return servletContextHandler;
    }

    public static Handler configureServletContextsHandler( Server server, List<ServletContextConfig> defs ) {
        if ( defs == null || defs.size() == 0 ) {
            return null;
        } else if ( defs.size() == 1 ) {
            return configureServletContextHandler( server, defs.get( 0 ) );
        } else {
            HandlerList result = new HandlerList();
            result.setServer( server );
            for ( ServletContextConfig def : defs ) {
                result.addHandler( configureServletContextHandler( server, def ) );
            }
            return result;
        }
    }


}
