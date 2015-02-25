package org.dbrain.yaw.scope;


import javax.inject.Scope;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Defines a service that is bound to a request scope.
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RUNTIME)
@Scope
public @interface SessionScoped {}
