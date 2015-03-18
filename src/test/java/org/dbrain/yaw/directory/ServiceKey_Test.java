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

package org.dbrain.yaw.directory;

import org.dbrain.yaw.app.App;
import org.dbrain.yaw.lifecycle.SessionScoped;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

public class ServiceKey_Test {


    @Test
    public void testOf() throws Exception {
        assertNotNull( ServiceKey.of( App.class ) );
        assertNotNull( ServiceKey.of( App.class, "named" ) );
        assertNotNull( ServiceKey.from( App.class ).build() );
    }

    @Test
    public void testEquals() throws Exception {
        assertEquals( ServiceKey.of( App.class ), ServiceKey.of( App.class ) );
        assertEquals( ServiceKey.of( App.class, "named" ), ServiceKey.of( App.class, "named" ) );
        assertEquals( ServiceKey.from( App.class ).build(), ServiceKey.of( App.class ) );
        assertEquals( ServiceKey.from( App.class ).qualifiedBy( SessionScoped.class ).build(), ServiceKey.of( App.class, SessionScoped.class ) );

        assertEquals( ServiceKey.of( App.class ).hashCode(), ServiceKey.of( App.class ).hashCode() );
        assertEquals( ServiceKey.of( App.class, "named" ).hashCode(), ServiceKey.of( App.class, "named" ).hashCode() );
        assertEquals( ServiceKey.from( App.class ).build().hashCode(), ServiceKey.of( App.class ).hashCode() );
        assertNotEquals( ServiceKey.of( App.class ), ServiceKey.of( ServiceKey_Test.class ) );
        assertNotEquals( ServiceKey.of( App.class, "named" ), ServiceKey.of( App.class, "notNamed" ) );
        assertNotEquals( ServiceKey.from( App.class ).build(), ServiceKey.of( ServiceKey_Test.class ) );

    }
}