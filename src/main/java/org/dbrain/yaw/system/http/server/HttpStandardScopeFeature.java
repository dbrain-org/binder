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

import org.dbrain.yaw.app.Configuration;
import org.dbrain.yaw.app.Feature;
import org.dbrain.yaw.http.server.defs.ServletFilterDef;
import org.dbrain.yaw.system.app.SystemConfiguration;
import org.dbrain.yaw.system.scope.RequestScopeContext;
import org.dbrain.yaw.system.scope.SessionScopeContext;

import javax.inject.Inject;
import javax.servlet.http.HttpSessionListener;

/**
 * Created by epoitras on 3/13/15.
 */
public class HttpStandardScopeFeature implements Feature {

    private final Configuration config;
    private final RequestScopeContext requestScopeContext;
    private final SessionScopeContext sessionScopeContext;

    @Inject
    public HttpStandardScopeFeature( Configuration config,
                                     RequestScopeContext requestScopeContext,
                                     SessionScopeContext sessionScopeContext ) {
        this.config = config;
        this.requestScopeContext = requestScopeContext;
        this.sessionScopeContext = sessionScopeContext;
    }

    @Override
    public void complete() {

        ServletFilterDef scopeFilter = new ServletFilterDef( new StandardScopeFilter( requestScopeContext, sessionScopeContext ), "/**" );

        config.bind( ServletFilterDef.class )
                .to( ServletFilterDef.class )
                .providedBy( scopeFilter )
                .qualifiedBy( SystemConfiguration.class )
                .complete();

        config.bind( HttpSessionListener.class )
                .to( HttpSessionListener.class )
                .providedBy( new StandardScopeSessionListener() )
                .qualifiedBy( SystemConfiguration.class )
                .complete();

    }

}
