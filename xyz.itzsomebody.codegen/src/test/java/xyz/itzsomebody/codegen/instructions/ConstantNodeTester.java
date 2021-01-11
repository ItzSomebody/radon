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

package xyz.itzsomebody.codegen.instructions;

import org.junit.Assert;
import org.junit.Test;
import org.objectweb.asm.Opcodes;
import xyz.itzsomebody.codegen.WrappedType;

public class ConstantNodeTester { // todo: maybe test dynamic const?
    @Test
    public void testNullConst() {
        Assert.assertNull(ConstantNode.nullConst().getValue());
        Assert.assertEquals(Opcodes.ACONST_NULL, ConstantNode.nullConst().getNode().getOpcode());
    }

    @Test
    public void testBooleanConst() {
        Assert.assertTrue((Boolean) ConstantNode.booleanConst(true).getValue());
        Assert.assertNotEquals(Opcodes.ICONST_0, ConstantNode.booleanConst(true).getNode().getOpcode());
        Assert.assertFalse((Boolean) ConstantNode.booleanConst(false).getValue());
        Assert.assertEquals(Opcodes.ICONST_0, ConstantNode.booleanConst(false).getNode().getOpcode());
    }

    @Test
    public void testIntConst() {
        Assert.assertEquals(Integer.MIN_VALUE, ConstantNode.intConst(Integer.MIN_VALUE).getValue());
        Assert.assertEquals(Opcodes.LDC, ConstantNode.intConst(Integer.MIN_VALUE).getNode().getOpcode());
        Assert.assertEquals(Opcodes.SIPUSH, ConstantNode.intConst(Short.MIN_VALUE).getNode().getOpcode());
        Assert.assertEquals(Opcodes.BIPUSH, ConstantNode.intConst(Byte.MIN_VALUE).getNode().getOpcode());
        Assert.assertEquals(Opcodes.ICONST_M1, ConstantNode.intConst(-1).getNode().getOpcode());
    }

    @Test
    public void testFloatConst() {
        Assert.assertEquals(Float.MIN_VALUE, ConstantNode.floatConst(Float.MIN_VALUE).getValue());
        Assert.assertEquals(Opcodes.LDC, ConstantNode.floatConst(Float.MIN_VALUE).getNode().getOpcode());
        Assert.assertEquals(Opcodes.FCONST_0, ConstantNode.floatConst(0F).getNode().getOpcode());
    }

    @Test
    public void testDoubleConst() {
        Assert.assertEquals(Double.MIN_VALUE, ConstantNode.doubleConst(Double.MIN_VALUE).getValue());
        Assert.assertEquals(Opcodes.LDC, ConstantNode.doubleConst(Double.MIN_VALUE).getNode().getOpcode());
        Assert.assertEquals(Opcodes.DCONST_0, ConstantNode.doubleConst(0D).getNode().getOpcode());
    }

    @Test
    public void testStringConst() {
        Assert.assertEquals("tucks", ConstantNode.stringConst("tucks").getValue());
        Assert.assertEquals(Opcodes.LDC, ConstantNode.stringConst("tux").getNode().getOpcode());
    }

    @Test
    public void testClassConst() {
        Assert.assertEquals(WrappedType.from(String.class), ConstantNode.classConst(WrappedType.from(String.class)).getValue());
    }
}
