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

package org.dbrain.yaw.scope;

import java.util.Arrays;

/**
 * DisposeException occurs when disposing of a resources and an error occurs. It is expected that the cause is always specified.
 */
public class DisposeException extends RuntimeException {

    private Throwable[] innerExceptions;

    protected DisposeException() {
    }

    public DisposeException( String message, Throwable[] innerExceptions ) {
        super( message );
        this.innerExceptions = innerExceptions;
    }

    public DisposeException( Throwable[] innerExceptions ) {
        super();
        this.innerExceptions = innerExceptions;
    }

    public DisposeException( String message, Throwable innerException ) {
        this( message, new Throwable[]{ innerException } );
    }

    public DisposeException( Throwable innerException ) {
        this( new Throwable[]{ innerException } );
    }


    /**
     * Gets the inner exceptions that caused this DisposeException.
     */
    public Throwable[] getInnerExceptions() {
        if ( innerExceptions != null ) {
            return Arrays.copyOf( innerExceptions, innerExceptions.length );
        } else {
            return new Throwable[0];
        }
    }

}
