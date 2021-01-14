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
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.MethodInsnNode;

import static xyz.itzsomebody.codegen.expressions.IRExpressions.*;

public class IRInvocationExpressionTester {
    @Test
    public void testInvokeStatic() throws Exception {
        var min = (MethodInsnNode) invokeStatic(DummyClass.class.getMethod("dummyStatic", String.class, int[].class)).getInstructions().compile().get(0);
        Assert.assertEquals(Opcodes.INVOKESTATIC, min.getOpcode());
        Assert.assertEquals(Type.getInternalName(DummyClass.class), min.owner);
        Assert.assertEquals("dummyStatic", min.name);
        Assert.assertEquals("(Ljava/lang/String;[I)V", min.desc);
    }

    @Test
    public void testInvokeVirtual() throws Exception {
        var min = (MethodInsnNode) invokeVirtual(nullConst(DummyClass.class), DummyClass.class.getMethod("dummyMethod", String.class, byte.class)).getInstructions().compile().get(1);
        Assert.assertEquals(Opcodes.INVOKEVIRTUAL, min.getOpcode());
        Assert.assertEquals(Type.getInternalName(DummyClass.class), min.owner);
        Assert.assertEquals("dummyMethod", min.name);
        Assert.assertEquals("(Ljava/lang/String;B)V", min.desc);
    }

    @Test
    public void testInvokeInterface() throws Exception {
        var min = (MethodInsnNode) invokeVirtual(nullConst(DummyInterface.class), DummyInterface.class.getMethod("dummyInterfaceMethod", String.class, boolean.class)).getInstructions().compile().get(1);
        Assert.assertEquals(Opcodes.INVOKEINTERFACE, min.getOpcode());
        Assert.assertEquals(Type.getInternalName(DummyInterface.class), min.owner);
        Assert.assertEquals("dummyInterfaceMethod", min.name);
        Assert.assertEquals("(Ljava/lang/String;Z)V", min.desc);
    }

    public interface DummyInterface {
        void dummyInterfaceMethod(String tux, boolean tucks);
    }

    public static class DummyClass implements DummyInterface {
        public void dummyMethod(String tux, byte tucks) {
        }

        public static void dummyStatic(String tux, int[] tucks) {
        }

        @Override
        public void dummyInterfaceMethod(String tux, boolean tucks) {
        }
    }
}
