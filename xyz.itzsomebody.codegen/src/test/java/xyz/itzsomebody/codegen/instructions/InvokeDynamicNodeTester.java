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
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import xyz.itzsomebody.codegen.WrappedHandle;
import xyz.itzsomebody.codegen.WrappedType;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Collections;
import java.util.List;

public class InvokeDynamicNodeTester {
    @Test
    public void testInvokeDynamicWithMethod() throws Exception {
        var method = InvokeDynamicNodeTester.class.getMethod("testBootstrap", MethodHandles.Lookup.class, String.class, MethodType.class);
        var invoke = (InvokeDynamicInsnNode) InvokeDynamicNode.invokeDynamic("tux", List.of(WrappedType.from(String.class)), WrappedType.from(void.class), method, Collections.emptyList()).getNode();
        Assert.assertEquals("tux", invoke.name);
        Assert.assertEquals("(Ljava/lang/String;)V", invoke.desc);
        var handle = invoke.bsm;
        Assert.assertEquals(Type.getInternalName(InvokeDynamicNodeTester.class), handle.getOwner());
        Assert.assertEquals("testBootstrap", handle.getName());
        Assert.assertEquals("(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;", handle.getDesc());
    }

    @Test
    public void testInvokeDynamicWithWrappedHandle() throws Exception {
        var invoke = (InvokeDynamicInsnNode) InvokeDynamicNode.invokeDynamic("tux", List.of(WrappedType.from(String.class)), WrappedType.from(void.class), WrappedHandle.getInvokeStaticHandle(InvokeDynamicNodeTester.class.getMethod("testBootstrap", MethodHandles.Lookup.class, String.class, MethodType.class)), Collections.emptyList()).getNode();
        Assert.assertEquals("tux", invoke.name);
        Assert.assertEquals("(Ljava/lang/String;)V", invoke.desc);
        var handle = invoke.bsm;
        Assert.assertEquals(Type.getInternalName(InvokeDynamicNodeTester.class), handle.getOwner());
        Assert.assertEquals("testBootstrap", handle.getName());
        Assert.assertEquals("(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;", handle.getDesc());
    }

    public static CallSite testBootstrap(MethodHandles.Lookup lookup, String name, MethodType methodType) {
        return null;
    }
}
