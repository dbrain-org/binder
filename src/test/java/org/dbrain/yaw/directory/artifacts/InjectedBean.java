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

import javax.inject.Inject;
import javax.inject.Named;

/**
* Created by epoitras on 3/13/15.
*/
public class InjectedBean {

    @Inject
    private SimpleService service1;

    @Inject
    protected SimpleService service1_1;

    @Inject
    public SimpleService service1_2;

    @Inject
    @Named( "toto" )
    public SimpleService service2;



    public SimpleService getService1() {
        return service1;
    }

    public SimpleService getService1_1() {
        return service1_1;
    }

    public SimpleService getService1_2() {
        return service1_2;
    }

    public SimpleService getService2() {
        return service2;
    }
}
