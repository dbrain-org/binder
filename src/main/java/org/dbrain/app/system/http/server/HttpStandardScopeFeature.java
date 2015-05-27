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

package org.dbrain.app.system.http.server;

import org.dbrain.app.conf.Configuration;
import org.dbrain.app.conf.Feature;
import org.dbrain.app.http.server.defs.ServletFilterDef;
import org.dbrain.app.system.app.SystemConfiguration;

import javax.inject.Inject;
import javax.servlet.http.HttpSessionListener;

/**
 * Created by epoitras on 3/13/15.
 */
public class HttpStandardScopeFeature implements Feature {

    private final Configuration config;

    @Inject
    public HttpStandardScopeFeature( Configuration config ) {
        this.config = config;
    }

    @Override
    public void complete() {

        ServletFilterDef scopeFilter = ServletFilterDef.of( "/*", StandardScopeFilter.class );

        config.bind( ServletFilterDef.class )
              .to( ServletFilterDef.class )
              .providedBy( scopeFilter )
              .qualifiedBy( SystemConfiguration.class )
              .complete();

        config.bind( StandardScopeSessionListener.class )
              .to( HttpSessionListener.class )
              .qualifiedBy( SystemConfiguration.class )
              .complete();

    }

}
