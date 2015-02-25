package org.dbrain.yaw.scope.system;

import javax.inject.Provider;

/**
 * Created with IntelliJ IDEA.
 * User: epoitras
 * Date: 10/07/13
 * Time: 9:19 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ScopeRegistryProvider extends Provider<ScopeRegistry>, AutoCloseable {}
