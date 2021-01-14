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
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import xyz.itzsomebody.codegen.WrappedType;

import static xyz.itzsomebody.codegen.expressions.IRExpressions.*;

public class IRCastExpressionTester {
    @Test
    public void testCastBooleanToShort() {
        var block = cast(booleanConst(false), WrappedType.from(short.class)).getInstructions();
        var insns = block.compile();

        Assert.assertEquals(Opcodes.ICONST_0, insns.get(0).getOpcode());
    }

    @Test
    public void testCastDoubleToShort() {
        var block = cast(doubleConst(99.9999D), WrappedType.from(short.class)).getInstructions();
        var insns = block.compile();

        Assert.assertEquals(Opcodes.LDC, insns.get(0).getOpcode());
        Assert.assertEquals(Opcodes.D2I, insns.get(1).getOpcode());
        Assert.assertEquals(Opcodes.I2S, insns.get(2).getOpcode());

        var ldc = (LdcInsnNode) insns.get(0);
        Assert.assertEquals(99.9999D, ldc.cst);
    }

    @Test
    public void testCastPrimitiveToEquivalentBoxedPrimitive() {
        var block = cast(booleanConst(false), WrappedType.from(Boolean.class)).getInstructions();
        var insns = block.compile();

        Assert.assertEquals(Opcodes.ICONST_0, insns.get(0).getOpcode());
        Assert.assertEquals(Opcodes.INVOKESTATIC, insns.get(1).getOpcode());

        var invoke = (MethodInsnNode) insns.get(1);
        Assert.assertEquals("java/lang/Boolean", invoke.owner);
        Assert.assertEquals("valueOf", invoke.name);
        Assert.assertEquals("(Z)Ljava/lang/Boolean;", invoke.desc);
    }

    @Test
    public void testCastPrimitiveToNonPrimitive() {
        var block = cast(intConst(5), WrappedType.from(Number.class)).getInstructions();
        var insns = block.compile();

        Assert.assertEquals(Opcodes.ICONST_5, insns.get(0).getOpcode());
        Assert.assertEquals(Opcodes.INVOKESTATIC, insns.get(1).getOpcode());
        Assert.assertEquals(Opcodes.CHECKCAST, insns.get(2).getOpcode());

        var invoke = (MethodInsnNode) insns.get(1);
        Assert.assertEquals("java/lang/Integer", invoke.owner);
        Assert.assertEquals("valueOf", invoke.name);
        Assert.assertEquals("(I)Ljava/lang/Integer;", invoke.desc);

        var cast = (TypeInsnNode) insns.get(2);
        Assert.assertEquals("java/lang/Number", cast.desc);
    }

    @Test
    public void testCastBoxedPrimitiveToPrimitive() {
        var block = cast(cast(intConst(5), WrappedType.from(Integer.class)), WrappedType.from(long.class)).getInstructions();
        var insns = block.compile();

        Assert.assertEquals(Opcodes.ICONST_5, insns.get(0).getOpcode());
        Assert.assertEquals(Opcodes.INVOKESTATIC, insns.get(1).getOpcode());
        Assert.assertEquals(Opcodes.INVOKEVIRTUAL, insns.get(2).getOpcode());
        Assert.assertEquals(Opcodes.I2L, insns.get(3).getOpcode());

        var invoke1 = (MethodInsnNode) insns.get(1);
        Assert.assertEquals("java/lang/Integer", invoke1.owner);
        Assert.assertEquals("valueOf", invoke1.name);
        Assert.assertEquals("(I)Ljava/lang/Integer;", invoke1.desc);

        var invoke2 = (MethodInsnNode) insns.get(2);
        Assert.assertEquals("java/lang/Integer", invoke2.owner);
        Assert.assertEquals("intValue", invoke2.name);
        Assert.assertEquals("()I", invoke2.desc);
    }

    @Test
    public void testCastBoxedPrimitiveToNonPrimitive() {
        var block = cast(cast(intConst(5), WrappedType.from(Integer.class)), WrappedType.from(Number.class)).getInstructions();
        var insns = block.compile();

        Assert.assertEquals(Opcodes.ICONST_5, insns.get(0).getOpcode());
        Assert.assertEquals(Opcodes.INVOKESTATIC, insns.get(1).getOpcode());
        Assert.assertEquals(Opcodes.CHECKCAST, insns.get(2).getOpcode());

        var invoke = (MethodInsnNode) insns.get(1);
        Assert.assertEquals("java/lang/Integer", invoke.owner);
        Assert.assertEquals("valueOf", invoke.name);
        Assert.assertEquals("(I)Ljava/lang/Integer;", invoke.desc);

        var cast = (TypeInsnNode) insns.get(2);
        Assert.assertEquals("java/lang/Number", cast.desc);
    }

    @Test
    public void testCastNonPrimitiveToPrimitive() {
        // Don't try this at home lol
        var block = cast(cast(nullConst(Number.class), WrappedType.from(Integer.class)), WrappedType.from(long.class)).getInstructions();
        var insns = block.compile();

        Assert.assertEquals(Opcodes.ACONST_NULL, insns.get(0).getOpcode());
        Assert.assertEquals(Opcodes.CHECKCAST, insns.get(1).getOpcode());
        Assert.assertEquals(Opcodes.INVOKEVIRTUAL, insns.get(2).getOpcode());
        Assert.assertEquals(Opcodes.I2L, insns.get(3).getOpcode());

        var cast = (TypeInsnNode) insns.get(1);
        Assert.assertEquals("java/lang/Integer", cast.desc);

        var invoke = (MethodInsnNode) insns.get(2);
        Assert.assertEquals("java/lang/Integer", invoke.owner);
        Assert.assertEquals("intValue", invoke.name);
        Assert.assertEquals("()I", invoke.desc);
    }

    @Test
    public void testCastNonPrimitiveToNonPrimitive() {
        var block = cast(nullConst(String.class), WrappedType.from(CharSequence.class)).getInstructions();
        var insns = block.compile();

        Assert.assertEquals(Opcodes.ACONST_NULL, insns.get(0).getOpcode());
        Assert.assertEquals(Opcodes.CHECKCAST, insns.get(1).getOpcode());

        var cast = (TypeInsnNode) insns.get(1);
        Assert.assertEquals("java/lang/CharSequence", cast.desc);
    }
}
