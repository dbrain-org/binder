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

import javax.inject.Provider;
import java.util.Objects;
import java.util.Stack;

/**
 * A thread local provider that keeps a stack of context.
 */
public class ThreadLocalProvider<T> implements Provider<T> {

    private ThreadLocal<Stack<Provider<T>>> contextStack = new ThreadLocal<>();

    public void enter( Provider<T> context ) {
        Objects.requireNonNull( context );
        Stack<Provider<T>> stack = contextStack.get();
        if ( stack == null ) {
            stack = new Stack<>();
            contextStack.set( stack );
        }
        stack.push( context );
    }

    public Provider<T> leave() {
        Provider<T> result;
        Stack<Provider<T>> stack = contextStack.get();
        if ( stack != null ) {
            result = stack.pop();
            if ( stack.size() == 0 ) {
                contextStack.set( null );
            }
        } else {
            throw new IllegalStateException( "The context stack is empty." );
        }
        return result;
    }

    @Override
    public T get() {
        Stack<Provider<T>> stack = contextStack.get();
        return stack != null ? stack.peek().get() : null;
    }

    public int size() {
        Stack<Provider<T>> stack = contextStack.get();
        return stack != null ? stack.size() : 0;
    }
}
