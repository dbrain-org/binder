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

package org.dbrain.binder.system.app;

import org.glassfish.hk2.api.Factory;

import javax.inject.Provider;
import java.io.Closeable;

/**
 * Created by epoitras on 3/6/15.
 */
public class Factories {

    /**
     * Create a factory from a provider.
     */
    public static <T> Factory<T> of( final Provider<T> provider ) {
        return new Factory<T>() {
            @Override
            public T provide() {
                return provider.get();
            }

            @Override
            public void dispose( T instance ) {
                if ( instance instanceof AutoCloseable ) {
                    try {
                        ( (AutoCloseable) instance ).close();
                    } catch ( Exception e ) {
                        throw new IllegalStateException( e );
                    }

                }

            }
        };
    }

    public static <T extends AutoCloseable> Factory<T> of( final T instance ) {
        return new Factory<T>() {
            @Override
            public T provide() {
                return instance;
            }

            @Override
            public void dispose( T instance ) {
                try {
                    instance.close();
                } catch ( Exception e ) {
                    throw new IllegalStateException( e );
                }
            }
        };
    }

    public static <T extends Closeable> Factory<T> of( final T instance ) {
        return new Factory<T>() {
            @Override
            public T provide() {
                return instance;
            }

            @Override
            public void dispose( T instance ) {
                try {
                    instance.close();
                } catch ( Exception e ) {
                    throw new IllegalStateException( e );
                }
            }
        };
    }


}
