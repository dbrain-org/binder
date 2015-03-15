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
import org.dbrain.yaw.directory.artifacts.InjectedBean;
import org.dbrain.yaw.directory.artifacts.SimpleService;
import org.dbrain.yaw.directory.artifacts.SomeQualifier;
import org.dbrain.yaw.system.app.AppImpl;
import org.dbrain.yaw.system.util.AnnotationBuilder;
import org.glassfish.hk2.api.DynamicConfiguration;
import org.glassfish.hk2.api.DynamicConfigurationService;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.junit.Assert;
import org.junit.Test;

import javax.inject.Named;
import javax.inject.Singleton;

/**
 * Created by epoitras on 3/4/15.
 */
public class ServiceDirectory_Test {


    @Test
    public void testBasicUseCase() throws Exception {

        try ( App app = new AppImpl() ) {
            ServiceLocator sl = app.getInstance( ServiceLocator.class );

            DynamicConfiguration dc = sl.getService( DynamicConfigurationService.class ).createDynamicConfiguration();

            dc.bind( BuilderHelper.activeLink( SimpleService.class )  //
                             .named( "toto" )                     //
                             .to( SimpleService.class )            //
                             .build() );


            dc.bind( BuilderHelper.activeLink( SimpleService.class ) //
                             .to( SimpleService.class )          //
                             .in( Singleton.class ) //
                             .build() );

            dc.bind( BuilderHelper.activeLink( SimpleService.class )   //
                             .named( "toto" )                      //
                             .qualifiedBy( AnnotationBuilder.of( SomeQualifier.class ) ) //
                             .to( SimpleService.class )                             //
                             .build() );
            dc.commit();


            Assert.assertEquals( 1, sl.getAllServices( SimpleService.class, AnnotationBuilder.of( SomeQualifier.class ) ).size() );
            Assert.assertEquals( 2,
                                 sl.getAllServices( SimpleService.class,
                                                    AnnotationBuilder.of( Named.class, "toto" ) ).size() );
            Assert.assertEquals( 3, sl.getAllServices( SimpleService.class ).size() );

            InjectedBean ti = sl.createAndInitialize( InjectedBean.class );
            Assert.assertNotNull( ti.getService1() );
            Assert.assertNotNull( ti.service2 );


        }
    }


}