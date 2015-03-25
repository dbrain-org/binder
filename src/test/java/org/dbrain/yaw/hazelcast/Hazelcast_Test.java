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

package org.dbrain.yaw.hazelcast;

import com.hazelcast.config.Config;
import com.hazelcast.core.DistributedObjectEvent;
import com.hazelcast.core.DistributedObjectListener;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import org.junit.Test;

import java.util.Queue;

/**
 * Created by epoitras on 3/22/15.
 *
 * Look at this:
 * http://renzhi.ca/?p=15
 */
public class Hazelcast_Test {

    @Test
    public void testName() throws Exception {
        Config cfg = new Config();
        HazelcastInstance instance = Hazelcast.newHazelcastInstance( cfg );
        HazelcastInstance instance2 = Hazelcast.newHazelcastInstance( cfg );

        IMap<Integer, String> mapCustomers = instance.getMap( "customers" );
        mapCustomers.put( 1, "Joe" );
        mapCustomers.put( 2, "Ali" );
        mapCustomers.put( 3, "Avi" );

        System.out.println( "Customer with key 1: " + mapCustomers.get( 1 ) );
        System.out.println( "Map Size:" + mapCustomers.size() );

        Queue<String> queueCustomers = instance.getQueue( "customers" );
        queueCustomers.offer( "Tom" );
        queueCustomers.offer( "Mary" );
        queueCustomers.offer( "Jane" );

        queueCustomers = instance2.getQueue( "customers" );
        System.out.println( "First customer: " + queueCustomers.poll() );
        System.out.println( "Second customer: " + queueCustomers.peek() );
        System.out.println( "Queue size: " + queueCustomers.size() );

    }
}
