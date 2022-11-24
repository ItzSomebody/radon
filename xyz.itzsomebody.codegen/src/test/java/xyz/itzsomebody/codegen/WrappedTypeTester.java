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
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import xyz.itzsomebody.codegen.exceptions.UncompilableNodeException;

public class WrappedTypeTester {
    @Test
    public void testGetAbsent() {
        Assert.assertNotNull(WrappedType.getAbsent());
    }

    @Test
    public void testIsInterface() {
        Assert.assertTrue(WrappedType.from(Dummy.class).isInterface());
        Assert.assertFalse(WrappedType.from(int.class).isInterface());
        Assert.assertFalse(WrappedType.from(String.class).isInterface());
    }

    @Test
    public void testIsPrimitive() {
        Assert.assertTrue(WrappedType.from(boolean.class).isPrimitive());
        Assert.assertTrue(WrappedType.from(char.class).isPrimitive());
        Assert.assertTrue(WrappedType.from(byte.class).isPrimitive());
        Assert.assertTrue(WrappedType.from(short.class).isPrimitive());
        Assert.assertTrue(WrappedType.from(int.class).isPrimitive());
        Assert.assertTrue(WrappedType.from(float.class).isPrimitive());
        Assert.assertTrue(WrappedType.from(long.class).isPrimitive());
        Assert.assertTrue(WrappedType.from(double.class).isPrimitive());

        Assert.assertFalse(WrappedType.from(int[].class).isPrimitive());
        Assert.assertFalse(WrappedType.from(String.class).isPrimitive());
        Assert.assertFalse(WrappedType.from(Integer.class).isPrimitive());
    }

    @Test
    public void testGetPrimitiveTypeForBoxedPrimitive() {
        Assert.assertEquals(WrappedType.from(boolean.class), WrappedType.from(Boolean.class).getPrimitiveType());
        Assert.assertEquals(WrappedType.from(char.class), WrappedType.from(Character.class).getPrimitiveType());
        Assert.assertEquals(WrappedType.from(byte.class), WrappedType.from(Byte.class).getPrimitiveType());
        Assert.assertEquals(WrappedType.from(short.class), WrappedType.from(Short.class).getPrimitiveType());
        Assert.assertEquals(WrappedType.from(int.class), WrappedType.from(Integer.class).getPrimitiveType());
        Assert.assertEquals(WrappedType.from(float.class), WrappedType.from(Float.class).getPrimitiveType());
        Assert.assertEquals(WrappedType.from(long.class), WrappedType.from(Long.class).getPrimitiveType());
        Assert.assertEquals(WrappedType.from(double.class), WrappedType.from(Double.class).getPrimitiveType());
    }

    @Test(expected = UncompilableNodeException.class)
    public void testGetPrimitiveTypeForNonBoxedPrimitive() {
        WrappedType.from(int.class).getPrimitiveType();
    }

    @Test
    public void testIsBoxed() {
        Assert.assertTrue(WrappedType.from(Boolean.class).isBoxed());
        Assert.assertTrue(WrappedType.from(Character.class).isBoxed());
        Assert.assertTrue(WrappedType.from(Byte.class).isBoxed());
        Assert.assertTrue(WrappedType.from(Short.class).isBoxed());
        Assert.assertTrue(WrappedType.from(Integer.class).isBoxed());
        Assert.assertTrue(WrappedType.from(Float.class).isBoxed());
        Assert.assertTrue(WrappedType.from(Long.class).isBoxed());
        Assert.assertTrue(WrappedType.from(Double.class).isBoxed());

        Assert.assertFalse(WrappedType.from(Integer[].class).isBoxed());
        Assert.assertFalse(WrappedType.from(int.class).isBoxed());
        Assert.assertFalse(WrappedType.from(String.class).isBoxed());
    }

    @Test
    public void testIsArray() {
        Assert.assertTrue(WrappedType.from(int[].class).isArray());
        Assert.assertTrue(WrappedType.from(String[].class).isArray());

        Assert.assertFalse(WrappedType.from(int.class).isArray());
        Assert.assertFalse(WrappedType.from(String.class).isArray());
    }

    @Test
    public void testIsIntType() {
        Assert.assertTrue(WrappedType.from(boolean.class).isIntType());
        Assert.assertTrue(WrappedType.from(char.class).isIntType());
        Assert.assertTrue(WrappedType.from(byte.class).isIntType());
        Assert.assertTrue(WrappedType.from(short.class).isIntType());
        Assert.assertTrue(WrappedType.from(int.class).isIntType());

        Assert.assertFalse(WrappedType.from(float.class).isIntType());
        Assert.assertFalse(WrappedType.from(long.class).isIntType());
        Assert.assertFalse(WrappedType.from(double.class).isIntType());
        Assert.assertFalse(WrappedType.from(int[].class).isPrimitive());
        Assert.assertFalse(WrappedType.from(String.class).isPrimitive());
        Assert.assertFalse(WrappedType.from(Integer.class).isPrimitive());
    }

    @Test
    public void testUnwrap() {
        Assert.assertEquals("Z", WrappedType.from(boolean.class).unwrap());
        Assert.assertEquals("C", WrappedType.from(char.class).unwrap());
        Assert.assertEquals("S", WrappedType.from(short.class).unwrap());
        Assert.assertEquals("B", WrappedType.from(byte.class).unwrap());
        Assert.assertEquals("I", WrappedType.from(int.class).unwrap());
        Assert.assertEquals("J", WrappedType.from(long.class).unwrap());
        Assert.assertEquals("F", WrappedType.from(float.class).unwrap());
        Assert.assertEquals("D", WrappedType.from(double.class).unwrap());
        Assert.assertEquals("Ljava/lang/String;", WrappedType.from(String.class).unwrap());
        Assert.assertEquals("[Ljava/lang/String;", WrappedType.from(String[].class).unwrap());
        Assert.assertEquals("[I", WrappedType.from(int[].class).unwrap());
    }

    @Test
    public void testFromClassName() {
        Assert.assertEquals(WrappedType.from(boolean.class), WrappedType.fromClassName("boolean", false));
        Assert.assertEquals(WrappedType.from(char.class), WrappedType.fromClassName("char", false));
        Assert.assertEquals(WrappedType.from(short.class), WrappedType.fromClassName("short", false));
        Assert.assertEquals(WrappedType.from(byte.class), WrappedType.fromClassName("byte", false));
        Assert.assertEquals(WrappedType.from(int.class), WrappedType.fromClassName("int", false));
        Assert.assertEquals(WrappedType.from(long.class), WrappedType.fromClassName("long", false));
        Assert.assertEquals(WrappedType.from(float.class), WrappedType.fromClassName("float", false));
        Assert.assertEquals(WrappedType.from(double.class), WrappedType.fromClassName("double", false));
        Assert.assertEquals(WrappedType.from(String.class), WrappedType.fromClassName("java.lang.String", false));
        Assert.assertEquals(WrappedType.from(String[].class), WrappedType.fromClassName("java.lang.String[]", false));
    }

    @Test
    public void testFromInternalName() {
        Assert.assertEquals(WrappedType.from(boolean.class), WrappedType.fromInternalName("Z", false));
        Assert.assertEquals(WrappedType.from(char.class), WrappedType.fromInternalName("C", false));
        Assert.assertEquals(WrappedType.from(short.class), WrappedType.fromInternalName("S", false));
        Assert.assertEquals(WrappedType.from(byte.class), WrappedType.fromInternalName("B", false));
        Assert.assertEquals(WrappedType.from(int.class), WrappedType.fromInternalName("I", false));
        Assert.assertEquals(WrappedType.from(long.class), WrappedType.fromInternalName("J", false));
        Assert.assertEquals(WrappedType.from(float.class), WrappedType.fromInternalName("F", false));
        Assert.assertEquals(WrappedType.from(double.class), WrappedType.fromInternalName("D", false));
        Assert.assertEquals(WrappedType.from(String.class), WrappedType.fromInternalName("java/lang/String", false));
        Assert.assertEquals(WrappedType.from(String[].class), WrappedType.fromInternalName("[Ljava/lang/String;", false));
    }

    @Test
    public void testFromClassNode() {
        var dummyClassNode = new ClassNode();
        dummyClassNode.name = "xyz/itzsomebody/codegen/WrappedTypeTester$Dummy";
        dummyClassNode.access = Opcodes.ACC_INTERFACE;
        Assert.assertEquals(WrappedType.from(Dummy.class), WrappedType.from(dummyClassNode));
    }

    private interface Dummy {
    }
}
