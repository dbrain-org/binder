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

package org.dbrain.app;

import org.dbrain.app.conf.Configuration;
import org.dbrain.app.directory.ServiceDirectory;
import org.jvnet.hk2.annotations.Contract;

/**
 * Base application class. Binds the service directory with capacity of
 * configuration and a shutdown hook.
 */
@Contract
public interface App extends AutoCloseable, ServiceDirectory {

    /**
     * The instance name. Can be used to retrieve the application by name.
     */
    String getName();

    /**
     * Allows to configure services in the application.
     */
    void configure( AppConfigurator session );

    /**
     * Internal functional interface to used to configure an application.
     *
     * Would have used Consumer if java not have checked exceptions.
     */
    @FunctionalInterface
    interface AppConfigurator {

        /**
         * Accept a configuration, allowing to configure the App.
         */
        void accept( Configuration config ) throws Exception;

    }

}
