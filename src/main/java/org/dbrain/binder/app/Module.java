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

package org.dbrain.binder.app;

/**
 * Internal functional interface to used to configure an application.
 *
 * Would have used Consumer if java not have checked exceptions.
 */
@FunctionalInterface
public interface Module {

    /**
     * Accept a configuration, allowing to configure the App.
     */
    void configure( Binder binder ) throws Exception;

}
