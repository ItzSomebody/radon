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
import org.objectweb.asm.tree.MethodInsnNode;
import xyz.itzsomebody.codegen.WrappedType;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;

public class InvokeNodeTester {
    @Test
    public void testInvokeStatic() {
        var invokeStatic = (MethodInsnNode) InvokeNode.invokeStatic(WrappedType.from(String.class), "valueOf", List.of(WrappedType.from(int.class)), WrappedType.from(String.class)).getNode();
        Assert.assertEquals(Opcodes.INVOKESTATIC, invokeStatic.getOpcode());
        Assert.assertEquals("java/lang/String", invokeStatic.owner);
        Assert.assertEquals("valueOf", invokeStatic.name);
        Assert.assertEquals("(I)Ljava/lang/String;", invokeStatic.desc);
    }

    @Test
    public void testInvokeVirtual() {
        var invokeVirtual = (MethodInsnNode) InvokeNode.invokeVirtual(WrappedType.from(String.class), "hashCode", Collections.emptyList(), WrappedType.from(int.class)).getNode();
        Assert.assertEquals(Opcodes.INVOKEVIRTUAL, invokeVirtual.getOpcode());
        Assert.assertEquals("java/lang/String", invokeVirtual.owner);
        Assert.assertEquals("hashCode", invokeVirtual.name);
        Assert.assertEquals("()I", invokeVirtual.desc);
    }

    @Test
    public void testInvokeInterface() {
        var invokeInterface = (MethodInsnNode) InvokeNode.invokeInterface(WrappedType.from(CharSequence.class), "length", Collections.emptyList(), WrappedType.from(int.class)).getNode();
        Assert.assertEquals(Opcodes.INVOKEINTERFACE, invokeInterface.getOpcode());
        Assert.assertEquals("java/lang/CharSequence", invokeInterface.owner);
        Assert.assertEquals("length", invokeInterface.name);
        Assert.assertEquals("()I", invokeInterface.desc);
    }

    @Test
    public void testInvokeSpecials() {
        var invokeSpecial = (MethodInsnNode) InvokeNode.invokeSpecial(WrappedType.from(String.class), "valueOf", List.of(WrappedType.from(int.class)), WrappedType.from(String.class)).getNode();
        Assert.assertEquals(Opcodes.INVOKESPECIAL, invokeSpecial.getOpcode());
        Assert.assertEquals("java/lang/String", invokeSpecial.owner);
        Assert.assertEquals("valueOf", invokeSpecial.name);
        Assert.assertEquals("(I)Ljava/lang/String;", invokeSpecial.desc);
    }

    @Test
    public void testInvokeConstructor() {
        var invokeConstructor = (MethodInsnNode) InvokeNode.invokeConstructor(WrappedType.from(String.class), List.of(WrappedType.from(byte[].class), WrappedType.from(Charset.class))).getNode();
        Assert.assertEquals(Opcodes.INVOKESPECIAL, invokeConstructor.getOpcode());
        Assert.assertEquals("java/lang/String", invokeConstructor.owner);
        Assert.assertEquals("<init>", invokeConstructor.name);
        Assert.assertEquals("([BLjava/nio/charset/Charset;)V", invokeConstructor.desc);
    }
}
