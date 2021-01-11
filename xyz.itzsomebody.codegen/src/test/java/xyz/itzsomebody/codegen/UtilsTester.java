/*
 * Radon - An open-source Java obfuscator
 * Copyright (C) 2021 ItzSomebody
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package xyz.itzsomebody.codegen;

import org.junit.Assert;
import org.junit.Test;
import org.objectweb.asm.tree.MethodNode;
import xyz.itzsomebody.codegen.instructions.ConstantNode;

import java.util.List;

public class UtilsTester {
    @Test
    public void testWrapMethodNodeParameters() {
        var dummyMethodNode = new MethodNode();
        dummyMethodNode.desc = "(Ljava/lang/String;ZBCSIJFD)V";

        List<WrappedType> wrappedTypes = Utils.wrapMethodNodeParameters(dummyMethodNode);
        List<WrappedType> expected = List.of(
                WrappedType.from(String.class),
                WrappedType.from(boolean.class),
                WrappedType.from(byte.class),
                WrappedType.from(char.class),
                WrappedType.from(short.class),
                WrappedType.from(int.class),
                WrappedType.from(long.class),
                WrappedType.from(float.class),
                WrappedType.from(double.class)
        );

        Assert.assertEquals(expected, wrappedTypes);
    }

    @Test
    public void testWrapMethodParameters() throws Exception {
        var method = String.class.getMethod("getBytes", int.class, int.class, byte[].class, int.class);

        List<WrappedType> wrappedTypes = Utils.wrapMethodParameters(method);
        List<WrappedType> expected = List.of(
                WrappedType.from(int.class),
                WrappedType.from(int.class),
                WrappedType.from(byte[].class),
                WrappedType.from(int.class)
        );

        Assert.assertEquals(expected, wrappedTypes);
    }

    @Test
    public void testWrapConstructorParameters() throws Exception {
        var constructor = String.class.getConstructor(byte[].class, String.class);

        List<WrappedType> wrappedTypes = Utils.wrapConstructorParameters(constructor);
        List<WrappedType> expected = List.of(
                WrappedType.from(byte[].class),
                WrappedType.from(String.class)
        );

        Assert.assertEquals(expected, wrappedTypes);
    }

    @Test
    public void testUnwrapMethodDescriptor() {
        var expected = "(Ljava/lang/String;ZBCSIJFD)V";
        var actual = Utils.unwrapMethodDescriptor(List.of(
                WrappedType.from(String.class),
                WrappedType.from(boolean.class),
                WrappedType.from(byte.class),
                WrappedType.from(char.class),
                WrappedType.from(short.class),
                WrappedType.from(int.class),
                WrappedType.from(long.class),
                WrappedType.from(float.class),
                WrappedType.from(double.class)), WrappedType.from(void.class)
        );

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testUnpackConstants() {
        var expected = new Object[]{null, "test", false, 0, 0L, 0F, 0D, WrappedType.from(String.class)};
        var actual = Utils.unpackConstants(List.of(
                ConstantNode.nullConst(),
                ConstantNode.stringConst("test"),
                ConstantNode.booleanConst(false),
                ConstantNode.intConst(0),
                ConstantNode.longConst(0L),
                ConstantNode.floatConst(0F),
                ConstantNode.doubleConst(0D),
                ConstantNode.classConst(WrappedType.from(String.class))
                // todo: maybe test dynamic constants?
        ));

        Assert.assertArrayEquals(expected, actual);
    }

    @Test
    public void testBoxForPrimitives() { // Test all primitive boxings
        Assert.assertEquals(WrappedType.from(Boolean.class), Utils.box(WrappedType.from(boolean.class)));
        Assert.assertEquals(WrappedType.from(Character.class), Utils.box(WrappedType.from(char.class)));
        Assert.assertEquals(WrappedType.from(Byte.class), Utils.box(WrappedType.from(byte.class)));
        Assert.assertEquals(WrappedType.from(Short.class), Utils.box(WrappedType.from(short.class)));
        Assert.assertEquals(WrappedType.from(Integer.class), Utils.box(WrappedType.from(int.class)));
        Assert.assertEquals(WrappedType.from(Long.class), Utils.box(WrappedType.from(long.class)));
        Assert.assertEquals(WrappedType.from(Float.class), Utils.box(WrappedType.from(float.class)));
        Assert.assertEquals(WrappedType.from(Double.class), Utils.box(WrappedType.from(double.class)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBoxForNonPrimitive() { // Ensure exception is thrown when boxing non-primitive
        Utils.box(WrappedType.from(String.class));
    }
}
