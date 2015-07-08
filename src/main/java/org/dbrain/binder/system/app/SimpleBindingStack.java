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

import org.dbrain.binder.app.Binder;
import org.dbrain.binder.app.Component;

import java.util.List;
import java.util.Stack;
import java.util.function.Consumer;

/**
 * Created by epoitras on 6/3/15.
 */
public class SimpleBindingStack implements Component.CreationContext {

    private ThreadLocal<List<Consumer<Binder>>> tlstack = new ThreadLocal<>();

    public void bindServices( Consumer<Binder> c ) {
        List<Consumer<Binder>> stack = tlstack.get();
        if ( stack == null ) {
            stack = new Stack<>();
            tlstack.set( stack );
        }
        stack.add( c );
    }

    public List<Consumer<Binder>> empty() {
        List<Consumer<Binder>> result = tlstack.get();
        if ( result != null ) {
            tlstack.set( null );
        }
        return result;
    }

}
