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

package org.dbrain.binder.directory;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * Directory service interface.
 */
public interface ServiceDirectory {

    /**
     * Locate a service of the specified class.
     *
     * @return A service or null if none is found.
     */
    <T> T locate( Class<T> serviceClass );

    /**
     * Locate a named service of the specified class.
     *
     * @return A service or null if none is found.
     */
    <T> T locate( Class<T> serviceClass, String name );

    /**
     * Locate a qualified service of the specified class.
     *
     * @return A service or null if none is found.
     */
    <T> T locate( Class<T> serviceClass, Class<? extends Annotation> qualifiers );

    /**
     * Locate a service identified by the key.
     *
     * @return A service or null if none is found.
     */
    <T> T locate( ServiceKey<T> serviceKey );

    <T> T getInstance( Class<T> serviceClass );

    <T> T getInstance( Class<T> serviceClass, String name );

    <T> T getInstance( Class<T> serviceClass, Class<? extends Annotation> qualifiers );

    <T> T getInstance( ServiceKey<T> serviceKey );

    /**
     * @return Query registry for the specified service. If not found, attempt to
     * create a new unmanaged instance.
     */
    <T> T getOrCreateInstance( Class<T> serviceClass );

    /**
     * Get a service instance, or create an unmanaged one if no service is registered.
     * The unmanaged service will be created only if the key is unqualified.
     */
    <T> T getOrCreateInstance( ServiceKey<T> serviceKey );

    <T> List<T> listServices( Class<T> serviceClass );

    <T> List<T> listServices( Class<T> serviceClass, String name );

    <T> List<T> listServices( Class<T> serviceClass, Class<? extends Annotation> qualifier );

}