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
 * Defines methods to bind services into an application.
 */
public interface Binder {

    /**
     * Start binging a service from the specific implementation class.
     */
    <T> ServiceConfigurator.Scoped<T> bind( Class<T> implementationClass );

    /**
     * Start binging a service from the specific implementation instance.
     */
    <T> ServiceConfigurator.Instance<T> bind( T implementation );

    /**
     * Start binding a new module.
     */
    <T extends Module> T bindModule( Class<T> componentClass );

}
