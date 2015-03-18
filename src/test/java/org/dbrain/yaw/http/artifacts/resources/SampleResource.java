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

package org.dbrain.yaw.http.artifacts.resources;

import org.dbrain.yaw.app.App;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 * Created by epoitras on 3/15/15.
 */
@Path( "/" )
public class SampleResource {

    @Inject
    App app;

    @Inject
    @Named( "session" )
    GuidService sessionUid;

    @Inject
    @Named( "request" )
    GuidService requestUid;


    @GET
    public String get() {
        return "Hello from application " + app.getName();
    }

    @GET
    @Path( "sessionUid" )
    public String getSessionUid() {
        return sessionUid.getUuid().toString();
    }

    @GET
    @Path( "requestUid" )
    public String getRequestUid() {
        return requestUid.getUuid().toString();
    }

}
