package org.dbrain.yaw.scope;

import java.util.Arrays;

/**
 * DisposeException occurs when disposing of a resources and an error occurs. It is expected that the cause is always specified.
 */
public class DisposeException extends RuntimeException {

    private Throwable[] innerExceptions;

    protected DisposeException() {
    }

    public DisposeException(String message, Throwable[] innerExceptions) {
        super(message);
        this.innerExceptions = innerExceptions;
    }

    public DisposeException(Throwable[] innerExceptions) {
        super();
        this.innerExceptions = innerExceptions;
    }

    public DisposeException(String message, Throwable innerException) {
        this(message, new Throwable[]{innerException});
    }

    public DisposeException(Throwable innerException) {
        this(new Throwable[]{innerException});
    }


    /**
     * Gets the inner exceptions that caused this DisposeException.
     */
    public Throwable[] getInnerExceptions() {
        if (innerExceptions != null) {
            return Arrays.copyOf(innerExceptions, innerExceptions.length);
        } else {
            return new Throwable[0];
        }
    }

}
