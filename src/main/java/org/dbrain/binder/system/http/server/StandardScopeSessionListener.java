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

package org.dbrain.binder.system.http.server;

import org.dbrain.binder.system.lifecycle.ContextRegistry;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * Created by epoitras on 3/13/15.
 */
class StandardScopeSessionListener implements HttpSessionListener {

    @Override
    public void sessionCreated( HttpSessionEvent se ) {
    }

    @Override
    public void sessionDestroyed( HttpSessionEvent se ) {
        ContextRegistry registry1 = (ContextRegistry) se.getSession().getAttribute( ContextRegistry.class.getName() );
        if ( registry1 != null ) {
            registry1.close();
        }
    }
}
