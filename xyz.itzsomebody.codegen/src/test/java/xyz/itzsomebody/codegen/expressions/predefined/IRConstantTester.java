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

package xyz.itzsomebody.codegen.expressions.predefined;

import org.junit.Assert;
import org.junit.Test;
import org.objectweb.asm.ConstantDynamic;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import xyz.itzsomebody.codegen.WrappedHandle;
import xyz.itzsomebody.codegen.WrappedType;

import java.lang.invoke.MethodHandles;
import java.util.Collections;

import static xyz.itzsomebody.codegen.expressions.IRExpressions.*;

public class IRConstantTester {
    @Test
    public void testDynamicConst() throws Exception {
        var block = dynamicConst("tux", WrappedType.from(String.class), WrappedHandle.getInvokeStaticHandle(IRConstantTester.class.getMethod("dynamicTester", MethodHandles.Lookup.class, String.class, Class.class)), Collections.emptyList()).getInstructions();
        var insn = block.compile().getFirst();

        Assert.assertTrue(insn instanceof LdcInsnNode && ((LdcInsnNode) insn).cst instanceof ConstantDynamic);

        var dynamic = (ConstantDynamic) ((LdcInsnNode) insn).cst;
        var handle = dynamic.getBootstrapMethod();

        Assert.assertEquals(Type.getInternalName(IRConstantTester.class), handle.getOwner());
        Assert.assertEquals("dynamicTester", handle.getName());
        Assert.assertEquals("(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/Class;)Ljava/lang/String;", handle.getDesc());
        Assert.assertEquals("tux", dynamic.getName());
        Assert.assertEquals("Ljava/lang/String;", dynamic.getDescriptor());
    }

    @Test
    public void testNullConst() {
        var block = nullConst(String.class).getInstructions();
        var insns = block.compile();

        Assert.assertEquals(Opcodes.ACONST_NULL, insns.get(0).getOpcode());
    }

    @Test
    public void testIntConst() {
        Assert.assertEquals(Opcodes.ICONST_0, intConst(0).getInstructions().compile().get(0).getOpcode());
        Assert.assertEquals(Opcodes.BIPUSH, intConst(Byte.MAX_VALUE).getInstructions().compile().get(0).getOpcode());
        Assert.assertEquals(Byte.MAX_VALUE, ((IntInsnNode) intConst(Byte.MAX_VALUE).getInstructions().compile().get(0)).operand);
        Assert.assertEquals(Opcodes.SIPUSH, intConst(Short.MAX_VALUE).getInstructions().compile().get(0).getOpcode());
        Assert.assertEquals(Short.MAX_VALUE, ((IntInsnNode) intConst(Short.MAX_VALUE).getInstructions().compile().get(0)).operand);
        Assert.assertEquals(Opcodes.LDC, intConst(Integer.MAX_VALUE).getInstructions().compile().get(0).getOpcode());
        Assert.assertEquals(Integer.MAX_VALUE, ((LdcInsnNode) intConst(Integer.MAX_VALUE).getInstructions().compile().get(0)).cst);
    }

    @Test
    public void testLongConst() {
        Assert.assertEquals(Opcodes.LCONST_0, longConst(0L).getInstructions().compile().get(0).getOpcode());
        Assert.assertEquals(Opcodes.LDC, longConst(Long.MAX_VALUE).getInstructions().compile().get(0).getOpcode());
        Assert.assertEquals(Long.MAX_VALUE, ((LdcInsnNode) longConst(Long.MAX_VALUE).getInstructions().compile().get(0)).cst);
    }

    @Test
    public void testFloatConst() {
        Assert.assertEquals(Opcodes.FCONST_0, floatConst(0F).getInstructions().compile().get(0).getOpcode());
        Assert.assertEquals(Opcodes.LDC, floatConst(Float.MAX_VALUE).getInstructions().compile().get(0).getOpcode());
        Assert.assertEquals(Float.MAX_VALUE, ((LdcInsnNode) floatConst(Float.MAX_VALUE).getInstructions().compile().get(0)).cst);
    }

    @Test
    public void testDoubleConst() {
        Assert.assertEquals(Opcodes.DCONST_0, doubleConst(0D).getInstructions().compile().get(0).getOpcode());
        Assert.assertEquals(Opcodes.LDC, doubleConst(Double.MAX_VALUE).getInstructions().compile().get(0).getOpcode());
        Assert.assertEquals(Double.MAX_VALUE, ((LdcInsnNode) doubleConst(Double.MAX_VALUE).getInstructions().compile().get(0)).cst);
    }

    @Test
    public void testStringConst() {
        Assert.assertEquals(Opcodes.LDC, stringConst("tux").getInstructions().compile().get(0).getOpcode());
        Assert.assertEquals("tux", ((LdcInsnNode) stringConst("tux").getInstructions().compile().get(0)).cst);
    }

    @Test
    public void testClassConst() {
        Assert.assertEquals(Opcodes.LDC, classConst(String.class).getInstructions().compile().get(0).getOpcode());
        Assert.assertEquals(Type.getType(String.class), ((LdcInsnNode) classConst(String.class).getInstructions().compile().get(0)).cst);
    }

    public static String dynamicTester(MethodHandles.Lookup lookup, String name, Class<?> type) {
        return "test";
    }
}
