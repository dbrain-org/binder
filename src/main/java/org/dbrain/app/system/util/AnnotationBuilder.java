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

package org.dbrain.app.system.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by epoitras on 3/5/15.
 */
public class AnnotationBuilder<A extends Annotation> {

    /**
     * Create an annotation with default values or no values from the specified class.
     */
    public static <U extends Annotation> U of( Class<U> annotationClass ) {
        return from( annotationClass ).build();
    }

    /**
     * Create an annotation with a value from the specified class.
     */
    public static <U extends Annotation> U of( Class<U> annotationClass, Object value ) {
        return from( annotationClass ).value( value ).build();
    }


    /**
     * Start creating an annotation from the specified class.
     */
    public static <U extends Annotation> AnnotationBuilder<U> from( Class<U> annotationClass ) {
        return new AnnotationBuilder<>( annotationClass );
    }

    private final Class<A>              annotationClass;
    private final Map<String, Property> properties;

    private AnnotationBuilder( Class<A> annotationClass ) {
        this.annotationClass = annotationClass;
        properties = getProperties( annotationClass );
    }

    /**
     * Retrieve all properties defined on this annotation type..
     */
    private static <A extends Annotation> LinkedHashMap<String, Property> getProperties( final Class<A> annotationType ) {
        return AccessController.doPrivileged( (PrivilegedAction<LinkedHashMap<String, Property>>) () -> {
            final LinkedHashMap<String, Property> result = new LinkedHashMap<>();

            Method[] methods = annotationType.getDeclaredMethods();
            AccessibleObject.setAccessible( methods, true );
            for ( Method method : methods ) {
                Property property = new Property( method );
                result.put( property.getName(), property );
            }
            return result;
        } );
    }

    public AnnotationBuilder<A> value( String name, Object value ) {
        Objects.requireNonNull( name );
        Property prop = properties.get( name );
        if ( prop == null ) {
            throw new IllegalArgumentException( name );
        }
        prop.setValue( value );
        return this;
    }

    public AnnotationBuilder<A> value( Object value ) {
        return value( "value", value );
    }


    private int getHashCode() {
        int hashCode = 0;

        for ( Map.Entry<String, Property> property : this.properties.entrySet() ) {
            hashCode += ( 127 * property.getKey().hashCode() ^ property.getValue().toHashCode() );
        }

        return hashCode;
    }


    public A build() {
        for ( Property p : properties.values() ) {
           if ( p.getValue() == null ) {
               throw new IllegalArgumentException( "Missing " + p.getName() + " value." );
           }
        }

        return new Proxy<>( annotationClass, properties, getHashCode() ).getProxy();
    }


    /**
     * Proxy implementation.
     */
    private static final class Proxy<A extends Annotation> implements Annotation, InvocationHandler {

        private final Class<A>              annotationType;
        private final Map<String, Property> properties;
        private final int                   hashCode;
        private final A                     proxy;

        /**
         * Build a new proxy annotation given the annotation type.
         */
        public Proxy( Class<A> annotationType, Map<String, Property> properties, int hashCode ) {
            this.annotationType = annotationType;
            this.properties = properties;
            this.hashCode = hashCode;

            this.proxy = annotationType.cast( java.lang.reflect.Proxy.newProxyInstance( annotationType.getClassLoader(),
                                                                                        new Class<?>[]{ annotationType },
                                                                                        this ) );
        }

        @Override
        public Object invoke( Object proxy, Method method, Object[] args ) throws Throwable {
            String name = method.getName();
            if ( this.properties.containsKey( name ) ) {
                return this.properties.get( name ).getValue();
            }
            return method.invoke( this, args );
        }

        @Override
        public Class<? extends Annotation> annotationType() {
            return this.annotationType;
        }

        @Override
        public boolean equals( Object obj ) {
            if ( this == obj ) return true;
            if ( obj == null || !this.annotationType.isInstance( obj ) ) return false;

            for ( Property property : properties.values() ) {
                if ( !this.properties.containsKey( property.getName() ) ) return false;
                Method m = property.getMethod();
                try {
                    Object thisValue = property.getValue();
                    Object thatValue = m.invoke( obj );
                    if ( !Objects.deepEquals( thisValue, thatValue ) ) {
                        return false;
                    }
                } catch ( Exception e ) {
                    throw new IllegalStateException( e );
                }

            }

            return true;
        }

        @Override
        public int hashCode() {
            return hashCode;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder( "@" ).append( this.annotationType.getName() ).append( '(' );
            int counter = 0;
            for ( Map.Entry<String, Property> property : this.properties.entrySet() ) {
                if ( counter > 0 ) {
                    sb.append( ", " );
                }
                sb.append( property.getKey() ).append( '=' ).append( property.getValue().valueToString() );
                counter++;
            }
            return sb.append( ')' ).toString();
        }

        public A getProxy() {
            return this.proxy;
        }


    }

    /**
     * Property of an annotation.
     */
    static final class Property {

        private final String name;
        private final Class<?> type;
        private final Method method;
        private Object value;

        public Property( Method method ) {
            this.name = method.getName();
            this.type = method.getReturnType();
            this.method = method;
            this.value = method.getDefaultValue();
        }

        /**
         * @return the property name.
         */
        public String getName() {
            return this.name;
        }

        /**
         * @return the property value.
         */
        public Object getValue() {
            return this.value;
        }

        /**
         * @return the property method.
         */
        public Method getMethod() {
            return method;
        }

        /**
         * Sets the property value.
         *
         * @param value the property value.
         */
        public void setValue( Object value ) {
            if ( value != null &&
                    !( this.type.isAssignableFrom( value.getClass() ) ||
                            ( this.type == Boolean.TYPE && value
                    .getClass() == Boolean.class ) || ( this.type == Byte.TYPE && value.getClass() == Byte.class ) || ( this.type == Character.TYPE && value
                    .getClass() == Character.class ) || ( this.type == Double.TYPE && value.getClass() == Double.class ) || ( this.type == Float.TYPE && value
                    .getClass() == Float.class ) || ( this.type == Integer.TYPE && value.getClass() == Integer.class ) || ( this.type == Long.TYPE && value
                    .getClass() == Long.class ) || ( this.type == Short.TYPE && value.getClass() == Short.class ) ) ) {
                throw new IllegalArgumentException( "Cannot assign value of type '" + value.getClass()
                                                                                           .getName() + "' to property '" + this.name + "' of type '" + this.type
                        .getName() + "'" );
            }
            this.value = value;
        }

        /**
         * Calculates this annotation value hash code.
         */
        protected int toHashCode() {
            if ( this.value == null ) {
                return 0;
            }
            if ( !this.type.isArray() ) {
                return this.value.hashCode();
            }
            if ( this.type == byte[].class ) {
                return Arrays.hashCode( (byte[]) this.value );
            }
            if ( this.type == short[].class ) {
                return Arrays.hashCode( (short[]) this.value );
            }
            if ( this.type == int[].class ) {
                return Arrays.hashCode( (int[]) this.value );
            }
            if ( this.type == long[].class ) {
                return Arrays.hashCode( (long[]) this.value );
            }
            if ( this.type == char[].class ) {
                return Arrays.hashCode( (char[]) this.value );
            }
            if ( this.type == float[].class ) {
                return Arrays.hashCode( (float[]) this.value );
            }
            if ( this.type == double[].class ) {
                return Arrays.hashCode( (double[]) this.value );
            }
            if ( this.type == boolean[].class ) {
                return Arrays.hashCode( (boolean[]) this.value );
            }
            return Arrays.hashCode( (Object[]) this.value );
        }

        /**
         * Calculates the {@code toString} of the property value.
         *
         * @return the {@code toString} of the property value.
         */
        protected String valueToString() {
            if (!this.type.isArray()) {
                return String.valueOf(this.value);
            }

            Class<?> arrayType = this.type.getComponentType();
            if (arrayType == Boolean.TYPE) {
                return Arrays.toString((boolean[]) this.value);
            } else if (arrayType == Byte.TYPE) {
                return Arrays.toString((byte[]) this.value);
            } else if (arrayType == Character.TYPE) {
                return Arrays.toString((char[]) this.value);
            } else if (arrayType == Double.TYPE) {
                return Arrays.toString((double[]) this.value);
            } else if (arrayType == Float.TYPE) {
                return Arrays.toString((float[]) this.value);
            } else if (arrayType == Integer.TYPE) {
                return Arrays.toString((int[]) this.value);
            } else if (arrayType == Long.TYPE) {
                return Arrays.toString((long[]) this.value);
            } else if (arrayType == Short.TYPE) {
                return Arrays.toString((short[]) this.value);
            }

            return Arrays.toString( (Object[]) this.value );
        }

    }
}
