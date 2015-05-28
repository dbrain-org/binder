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

package org.dbrain.binder.system.lifecycle;

import org.glassfish.hk2.api.ClassAnalyzer;
import org.glassfish.hk2.api.MultiException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * Created by epoitras on 3/6/15.
 */
@Singleton
@Named( BaseClassAnalyzer.YAW_ANALYZER_NAME )
public class BaseClassAnalyzer implements ClassAnalyzer {

    public static final String YAW_ANALYZER_NAME = "YAW-ANALYZER";

    private ClassAnalyzer defaultAnalyzer;

    @Inject
    public BaseClassAnalyzer( @Named( DEFAULT_IMPLEMENTATION_NAME ) ClassAnalyzer defaultAnalyzer ) {
        this.defaultAnalyzer = defaultAnalyzer;
    }

    @Override
    public <T> Constructor<T> getConstructor( Class<T> clazz ) throws MultiException, NoSuchMethodException {
        return defaultAnalyzer.getConstructor( clazz );
    }

    @Override
    public <T> Set<Method> getInitializerMethods( Class<T> clazz ) throws MultiException {
        return defaultAnalyzer.getInitializerMethods( clazz );
    }

    @Override
    public <T> Set<Field> getFields( Class<T> clazz ) throws MultiException {
        return defaultAnalyzer.getFields( clazz );
    }

    @Override
    public <T> Method getPostConstructMethod( Class<T> clazz ) throws MultiException {
        return defaultAnalyzer.getPostConstructMethod( clazz );
    }

    @Override
    public <T> Method getPreDestroyMethod( Class<T> clazz ) throws MultiException {
        Method m = defaultAnalyzer.getPreDestroyMethod( clazz );
        // Search for the Close method if Closeable.
        if ( m == null ) {
            if ( AutoCloseable.class.isAssignableFrom( clazz ) ) {
                try {
                    m = clazz.getDeclaredMethod( "close" );
                } catch ( Exception e ) {
                    throw new MultiException( e );
                }
            }
        }
        return m;
    }

}
