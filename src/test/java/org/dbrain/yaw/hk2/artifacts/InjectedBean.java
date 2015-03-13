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

package org.dbrain.yaw.hk2.artifacts;

import org.dbrain.yaw.hk2.InjectionUseCase_Test;

import javax.inject.Inject;
import javax.inject.Named;

/**
* Created by epoitras on 3/13/15.
*/
public class InjectedBean {

    @Inject
    public InjectionUseCase_Test.SimpleService service1;

    @Inject
    public InjectionUseCase_Test.SimpleService service1_1;

    @Inject
    public InjectionUseCase_Test.SimpleService service1_2;


    @Inject
    @Named( "toto" )
    public InjectionUseCase_Test.SimpleService service2;

}
