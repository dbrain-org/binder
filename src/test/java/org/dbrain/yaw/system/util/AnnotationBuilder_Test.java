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

package org.dbrain.yaw.system.util;

import org.dbrain.yaw.system.util.artefact.ArrayBool;
import org.dbrain.yaw.system.util.artefact.ArrayByte;
import org.dbrain.yaw.system.util.artefact.ArrayChar;
import org.dbrain.yaw.system.util.artefact.ArrayDouble;
import org.dbrain.yaw.system.util.artefact.ArrayFloat;
import org.dbrain.yaw.system.util.artefact.ArrayInt;
import org.dbrain.yaw.system.util.artefact.ArrayLong;
import org.dbrain.yaw.system.util.artefact.ArrayShort;
import org.dbrain.yaw.system.util.artefact.ArrayString;
import org.dbrain.yaw.system.util.artefact.SingleAnnotation;
import org.dbrain.yaw.system.util.artefact.SingleBool;
import org.dbrain.yaw.system.util.artefact.SingleByte;
import org.dbrain.yaw.system.util.artefact.SingleChar;
import org.dbrain.yaw.system.util.artefact.SingleDouble;
import org.dbrain.yaw.system.util.artefact.SingleFloat;
import org.dbrain.yaw.system.util.artefact.SingleInt;
import org.dbrain.yaw.system.util.artefact.SingleLong;
import org.dbrain.yaw.system.util.artefact.SingleShort;
import org.dbrain.yaw.system.util.artefact.SingleString;
import org.junit.Assert;
import org.junit.Test;

import javax.inject.Named;
import javax.ws.rs.GET;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by epoitras on 3/5/15.
 */
@Named( "TestValue" )
@ArrayBool( { true, false } )
@ArrayChar( { 'a', 'b' } )
@ArrayByte( { 1, 2, 3, 4 } )
@ArrayShort( { 1, 2, 3, 4 } )
@ArrayLong( { 1, 2, 3, 4 } )
@ArrayInt( { 1, 2, 3, 4 } )
@ArrayFloat( { 1, 2, 3, 4 } )
@ArrayDouble( { 1, 2, 3, 4 } )
@ArrayString( { "Test", "Me" } )
@SingleBool( true )
@SingleChar( 'z' )
@SingleByte( 6 )
@SingleShort( 6 )
@SingleInt( 6 )
@SingleLong( 6 )
@SingleFloat( 6 )
@SingleDouble( 6 )
@SingleString( "6" )
@SingleAnnotation( @Named( "6" ) )
public class AnnotationBuilder_Test {

    private static void twoWayAssertEquals( Object o1, Object o2 ) {
        assertNotNull( o1 );
        assertNotNull( o2 );
        assertEquals( o1, o2 );
        assertEquals( o2, o1 );
        assertEquals( o1.toString(), o2.toString() );
    }

    @Test
    public void testWithNamed() throws Exception {
        Named stdNamed = getClass().getAnnotation( Named.class );

        Named named = AnnotationBuilder.of( Named.class, "TestValue" );
        assertEquals( named.value(), "TestValue" );
        assertEquals( named.annotationType(), stdNamed.annotationType() );
        assertEquals( named.toString(), stdNamed.toString() );

        Assert.assertTrue( named.equals( stdNamed ) );
        Assert.assertTrue( stdNamed.equals( named ) );
        twoWayAssertEquals( stdNamed, named );
        assertEquals( stdNamed.hashCode(), named.hashCode() );

        Named altNamed1 = AnnotationBuilder.from( Named.class ).value( "TestValue" ).build();
        Named altNamed2 = AnnotationBuilder.from( Named.class ).value( "value", "TestValue" ).build();
        twoWayAssertEquals( stdNamed, altNamed1 );
        twoWayAssertEquals( stdNamed, altNamed2 );

    }


    @Test
    public void testCompareArray() throws Exception {

        ArrayBool stdBool = getClass().getAnnotation( ArrayBool.class );
        ArrayChar stdChar = getClass().getAnnotation( ArrayChar.class );
        ArrayByte stdByte = getClass().getAnnotation( ArrayByte.class );
        ArrayShort stdShort = getClass().getAnnotation( ArrayShort.class );
        ArrayInt stdInt = getClass().getAnnotation( ArrayInt.class );
        ArrayLong stdLong = getClass().getAnnotation( ArrayLong.class );
        ArrayFloat stdFloat = getClass().getAnnotation( ArrayFloat.class );
        ArrayDouble stdDouble = getClass().getAnnotation( ArrayDouble.class );
        ArrayString stdString = getClass().getAnnotation( ArrayString.class );

        twoWayAssertEquals( stdBool, AnnotationBuilder.of( ArrayBool.class, new boolean[]{ true, false } ) );
        twoWayAssertEquals( stdChar, AnnotationBuilder.of( ArrayChar.class, new char[]{ 'a', 'b' } ) );
        twoWayAssertEquals( stdByte, AnnotationBuilder.of( ArrayByte.class, new byte[]{ 1, 2, 3, 4 } ) );
        twoWayAssertEquals( stdShort, AnnotationBuilder.of( ArrayShort.class, new short[]{ 1, 2, 3, 4 } ) );
        twoWayAssertEquals( stdInt, AnnotationBuilder.of( ArrayInt.class, new int[]{ 1, 2, 3, 4 } ) );
        twoWayAssertEquals( stdLong, AnnotationBuilder.of( ArrayLong.class, new long[]{ 1, 2, 3, 4 } ) );
        twoWayAssertEquals( stdFloat, AnnotationBuilder.of( ArrayFloat.class, new float[]{ 1, 2, 3, 4 } ) );
        twoWayAssertEquals( stdDouble, AnnotationBuilder.of( ArrayDouble.class, new double[]{ 1, 2, 3, 4 } ) );
        twoWayAssertEquals( stdString, AnnotationBuilder.of( ArrayString.class, new String[]{ "Test", "Me" } ) );

        // TODO Those does not works...
        //        twoWayAssertEquals( stdBool, AnnotationBuilder.of( ArrayBool.class, new Boolean[]{ true, false } ) );
        //        twoWayAssertEquals( stdChar, AnnotationBuilder.of( ArrayChar.class, new Character[]{ 'a', 'b' } ) );
        //        twoWayAssertEquals( stdByte, AnnotationBuilder.of( ArrayByte.class, new Byte[]{ 1, 2, 3, 4 } ) );
        //        twoWayAssertEquals( stdShort, AnnotationBuilder.of( ArrayShort.class, new Short[]{ 1, 2, 3, 4 } ) );
        //        twoWayAssertEquals( stdInt, AnnotationBuilder.of( ArrayInt.class, new Integer[]{ 1, 2, 3, 4 } ) );
        //        twoWayAssertEquals( stdLong, AnnotationBuilder.of( ArrayLong.class, new Long[]{ 1l, 2l, 3l, 4l } ) );
        //        twoWayAssertEquals( stdFloat, AnnotationBuilder.of( ArrayFloat.class, new Float[]{ 1f, 2f, 3f, 4f } ) );
        //        twoWayAssertEquals( stdDouble, AnnotationBuilder.of( ArrayDouble.class, new Double[]{ 1d, 2d, 3d, 4d } ) );

    }

    @Test
    public void testCompareSingle() throws Exception {

        SingleBool stdBool = getClass().getAnnotation( SingleBool.class );
        SingleChar stdChar = getClass().getAnnotation( SingleChar.class );
        SingleByte stdByte = getClass().getAnnotation( SingleByte.class );
        SingleShort stdShort = getClass().getAnnotation( SingleShort.class );
        SingleInt stdInt = getClass().getAnnotation( SingleInt.class );
        SingleLong stdLong = getClass().getAnnotation( SingleLong.class );
        SingleFloat stdFloat = getClass().getAnnotation( SingleFloat.class );
        SingleDouble stdDouble = getClass().getAnnotation( SingleDouble.class );
        SingleString stdString = getClass().getAnnotation( SingleString.class );
        SingleAnnotation stdAnnotation = getClass().getAnnotation( SingleAnnotation.class );

        twoWayAssertEquals( stdBool, AnnotationBuilder.of( SingleBool.class, true ) );
        twoWayAssertEquals( stdChar, AnnotationBuilder.of( SingleChar.class, 'z' ) );
        twoWayAssertEquals( stdByte, AnnotationBuilder.of( SingleByte.class, (byte) 6 ) );
        twoWayAssertEquals( stdShort, AnnotationBuilder.of( SingleShort.class, (short) 6 ) );
        twoWayAssertEquals( stdInt, AnnotationBuilder.of( SingleInt.class, 6 ) );
        twoWayAssertEquals( stdLong, AnnotationBuilder.of( SingleLong.class, 6l ) );
        twoWayAssertEquals( stdFloat, AnnotationBuilder.of( SingleFloat.class, 6f ) );
        twoWayAssertEquals( stdDouble, AnnotationBuilder.of( SingleDouble.class, 6d ) );
        twoWayAssertEquals( stdString, AnnotationBuilder.of( SingleString.class, "6" ) );
        twoWayAssertEquals( stdAnnotation,
                            AnnotationBuilder.of( SingleAnnotation.class, AnnotationBuilder.of( Named.class, "6" ) ) );

        twoWayAssertEquals( stdBool, AnnotationBuilder.of( SingleBool.class, Boolean.TRUE ) );
        twoWayAssertEquals( stdChar, AnnotationBuilder.of( SingleChar.class, new Character( 'z' ) ) );
        twoWayAssertEquals( stdByte, AnnotationBuilder.of( SingleByte.class, new Byte( (byte) 6 ) ) );
        twoWayAssertEquals( stdShort, AnnotationBuilder.of( SingleShort.class, new Short( (short) 6 ) ) );
        twoWayAssertEquals( stdInt, AnnotationBuilder.of( SingleInt.class, new Integer( 6 ) ) );
        twoWayAssertEquals( stdLong, AnnotationBuilder.of( SingleLong.class, new Long( 6l ) ) );
        twoWayAssertEquals( stdFloat, AnnotationBuilder.of( SingleFloat.class, new Float( 6f ) ) );
        twoWayAssertEquals( stdDouble, AnnotationBuilder.of( SingleDouble.class, new Double( 6d ) ) );

    }


    @Test( expected = IllegalArgumentException.class )
    public void testSettingNonExistantField() throws Exception {
        AnnotationBuilder.from( GET.class ).value( "NonExistant" ).build();
    }

    @Test( expected = IllegalArgumentException.class )
    public void testSettingInvalidType() throws Exception {
        AnnotationBuilder.from( Named.class ).value( 1234 ).build();
    }

    @Test( expected = IllegalArgumentException.class )
    public void testMissingField() throws Exception {
        AnnotationBuilder.from( ArrayBool.class ).build();
    }
}
