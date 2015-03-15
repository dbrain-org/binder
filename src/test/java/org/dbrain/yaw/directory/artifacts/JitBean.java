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

package org.dbrain.yaw.directory.artifacts;

import org.dbrain.yaw.app.App;
import org.glassfish.hk2.api.ServiceLocator;

import javax.inject.Inject;

/**
* Created by epoitras on 3/15/15.
*/
public class JitBean {

    private final App            injectedConstructor1;
    private final ServiceLocator injectedConstructor2;

    @Inject
    private App injectedField1;


    @Inject
    public JitBean( App injectedConstructor1, ServiceLocator serviceLocator ) {
        this.injectedConstructor1 = injectedConstructor1;
        this.injectedConstructor2 = serviceLocator;
    }

    public App getInjectedConstructor1() {
        return injectedConstructor1;
    }

    public ServiceLocator getInjectedConstructor2() {
        return injectedConstructor2;
    }

    public App getInjectedField1() {
        return injectedField1;
    }
}
