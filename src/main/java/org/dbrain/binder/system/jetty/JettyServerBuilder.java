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

package org.dbrain.binder.system.jetty;

import org.dbrain.binder.directory.ServiceDirectory;
import org.dbrain.binder.directory.ServiceKey;
import org.dbrain.binder.http.conf.CredentialsConf;
import org.dbrain.binder.http.conf.FormLocationConf;
import org.dbrain.binder.http.conf.ServletAppSecurityConf;
import org.dbrain.binder.http.conf.ServletConf;
import org.dbrain.binder.http.conf.ServletContextConf;
import org.dbrain.binder.http.conf.ServletFilterConf;
import org.dbrain.binder.http.conf.WebSocketConfiguredServerConf;
import org.dbrain.binder.http.conf.WebSocketServerConf;
import org.dbrain.binder.http.conf.WebSocketServiceServerConf;
import org.dbrain.binder.system.app.SystemConfiguration;
import org.dbrain.binder.system.jetty.websocket.JsrScopedSessionFactory;
import org.dbrain.binder.system.jetty.websocket.WebSocketInjectorConfigurator;
import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.security.authentication.FormAuthenticator;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.security.Constraint;
import org.eclipse.jetty.websocket.jsr356.server.ServerContainer;
import org.eclipse.jetty.websocket.server.WebSocketUpgradeFilter;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.http.HttpSessionListener;
import javax.websocket.server.ServerEndpointConfig;
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
public class JettyServerBuilder {

    private final ServiceDirectory locator;

    public JettyServerBuilder( ServiceDirectory locator ) {
        this.locator = locator;
    }

    public void configureFilter( ServletContextHandler result, ServletFilterConf def ) {
        def.accept( new ServletFilterConf.Visitor() {

            @Override
            public void visit( ServletFilterConf.ServletFilterInstanceDef servletDef ) {
                result.addFilter( new FilterHolder( servletDef.getInstance() ),
                                  servletDef.getPathSpec(),
                                  EnumSet.of( DispatcherType.REQUEST ) );
            }

            @Override
            public void visit( ServletFilterConf.ServletFilterClassDef servletDef ) {
                Filter instance = locator.getOrCreateInstance( servletDef.getFilterClass() );
                result.addFilter( new FilterHolder( instance ),
                                  servletDef.getPathSpec(),
                                  EnumSet.of( DispatcherType.REQUEST ) );
            }

        } );
    }

    public void configureServlet( ServletContextHandler result, ServletConf def ) {
        def.accept( new ServletConf.Visitor() {
            @Override
            public void visit( ServletConf.InstanceServletDef servletDef ) {
                ServletHolder servletHolder = new ServletHolder( servletDef.getInstance() );
                //servletHolder.setInitParameter(  );
                result.addServlet( servletHolder, servletDef.getPathSpec() );
            }
        } );
    }

    public FormAuthenticator createFormAuthenticator( FormLocationConf def ) {
        return new FormAuthenticator( def.getUrl(),
                                      def.getErrorURL(),
                                      false /* no, do not dispatch but redirect (302, 301?) to the error url */ );
    }

    public HashLoginService createHashLoginService( CredentialsConf def ) {
        return new HashLoginService( def.getRealm(), def.getFile() );
    }

    public ConstraintSecurityHandler createConstraintSecurityHandler( ServletAppSecurityConf def ) {

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

    /**
     * Configure web socket definitions into the server container.
     */
    public void configureWebSocket( ServerContainer serverContainer,
                                    WebSocketServerConf webSocketDef ) throws Exception {

        webSocketDef.accept( new WebSocketServerConf.Visitor() {

            private void configureServerEndpointConfig( ServerEndpointConfig config,
                                                        ServiceKey<?> serviceKey ) throws Exception {
                config = ServerEndpointConfig.Builder.create( config.getEndpointClass(), config.getPath() ) //
                        .decoders( config.getDecoders() ) //
                        .encoders( config.getEncoders() ) //
                        .extensions( config.getExtensions() ) //
                        .subprotocols( config.getSubprotocols() ) //
                        .configurator( new WebSocketInjectorConfigurator( locator,
                                                                          config.getConfigurator(),
                                                                          serviceKey ) ) //
                        .build();

                serverContainer.addEndpoint( config );
            }

            @Override
            public void visit( WebSocketServiceServerConf endpointClassWebSocketConf ) throws Exception {
                ServerEndpointConfig config = serverContainer //
                        .getServerEndpointMetadata( endpointClassWebSocketConf.getEndpointService().getServiceClass(),
                                                    null ) //
                        .getConfig();

                // Configure the endpoint
                configureServerEndpointConfig( config, endpointClassWebSocketConf.getEndpointService() );
            }

            @Override
            public void visit( WebSocketConfiguredServerConf websocketServerEndpointConfig ) throws Exception {
                // Configure the endpoint
                configureServerEndpointConfig( websocketServerEndpointConfig.getConfig(),
                                               ServiceKey.of( websocketServerEndpointConfig.getConfig()
                                                                                           .getEndpointClass() ) );
            }
        } );

    }

    /**
     * Configure the Upgrade filter into the Servlet Context Handler.
     *
     * Taken from org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer
     */
    public ServerContainer configureContext( ServletContextHandler context ) throws ServletException {

        // Create Filter
        WebSocketUpgradeFilter filter = WebSocketUpgradeFilter.configureContext( context );

        JsrScopedSessionFactory scopedSessionFactory = locator.getOrCreateInstance( JsrScopedSessionFactory.class );

        // TODO: This is a bit edgy since it works only because the other session factory are registered
        // later in the ServerContainer. There should be a better way to do this ?
        filter.getFactory().addSessionFactory( scopedSessionFactory );

        // Create the Jetty ServerContainer implementation
        ServerContainer jettyContainer = new ServerContainer( filter,
                                                              filter.getFactory(),
                                                              context.getServer().getThreadPool() );

        scopedSessionFactory.configure( jettyContainer, jettyContainer );

        context.addBean( jettyContainer );

        // Store a reference to the ServerContainer per javax.websocket spec 1.0 final section 6.4 Programmatic Server Deployment
        context.setAttribute( javax.websocket.server.ServerContainer.class.getName(), jettyContainer );

        return jettyContainer;
    }


    public Handler configureServletContextHandler( Server server, ServletContextConf config ) {

        ServletContextHandler servletContextHandler = new ServletContextHandler( ( config.getSecurity() != null ) ? ServletContextHandler.SESSIONS : ServletContextHandler.NO_SESSIONS );
        servletContextHandler.setContextPath( config.getContextPath() );
        servletContextHandler.setServer( server );

        // Initialize javax.websocket layer
        if ( config.getWebSockets().size() > 0 ) {
            try {
                ServerContainer wscontainer = //
                        configureContext( servletContextHandler );

                for ( WebSocketServerConf wsd : config.getWebSockets() ) {
                    configureWebSocket( wscontainer, wsd );
                }

            } catch ( Exception e ) {
                throw new RuntimeException( e );
            }
        }

        if ( config.getSecurity() != null ) {
            // no url rewritting to put session id in the path
            // TODO kilantzis - put the line below somewhere else?
            servletContextHandler.setInitParameter( "org.eclipse.jetty.servlet.SessionIdPathParameterName", "none" );
            servletContextHandler.setSecurityHandler( createConstraintSecurityHandler( config.getSecurity() ) );
        }

        // Create a session handler for the server.
        SessionHandler sessionHandler = new SessionHandler();
        servletContextHandler.setSessionHandler( sessionHandler );

        // Add system servlet context listener, if any.
        locator.listServices( ServletContextListener.class, SystemConfiguration.class )
               .forEach( servletContextHandler::addEventListener );

        // Add system session listener, if any.
        if ( sessionHandler != null ) {
            locator.listServices( HttpSessionListener.class, SystemConfiguration.class )
                   .forEach( sessionHandler::addEventListener );
        }


        // Add system configuration filters, if any.
        for ( ServletFilterConf filterDef : locator.listServices( ServletFilterConf.class,
                                                                  SystemConfiguration.class ) ) {
            configureFilter( servletContextHandler, filterDef );
        }


        // Add user filters, if any.
        for ( ServletFilterConf filterDef : config.getFilters() ) {
            configureFilter( servletContextHandler, filterDef );
        }

        for ( ServletConf servletDef : config.getServlets() ) {
            configureServlet( servletContextHandler, servletDef );
        }

        return servletContextHandler;
    }

    public Handler configureServletContextsHandler( Server server, List<ServletContextConf> defs ) {
        if ( defs == null || defs.size() == 0 ) {
            return null;
        } else if ( defs.size() == 1 ) {
            return configureServletContextHandler( server, defs.get( 0 ) );
        } else {
            HandlerList result = new HandlerList();
            result.setServer( server );
            for ( ServletContextConf def : defs ) {
                result.addHandler( configureServletContextHandler( server, def ) );
            }
            return result;
        }
    }


}
