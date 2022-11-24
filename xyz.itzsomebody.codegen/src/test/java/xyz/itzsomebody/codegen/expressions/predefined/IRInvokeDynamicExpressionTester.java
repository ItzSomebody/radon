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
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import xyz.itzsomebody.codegen.WrappedHandle;
import xyz.itzsomebody.codegen.WrappedType;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Collections;
import java.util.List;

import static xyz.itzsomebody.codegen.expressions.IRExpressions.invokeDynamic;
import static xyz.itzsomebody.codegen.expressions.IRExpressions.stringConst;

public class IRInvokeDynamicExpressionTester {
    @Test
    public void testInvokeDynamicWithMethodBootstrap() throws Exception {
        var method = IRInvokeDynamicExpressionTester.class.getMethod("dummyBootstrap", MethodHandles.Lookup.class, String.class, MethodType.class);
        var block = invokeDynamic(
                "tucks",
                List.of(stringConst("tux"), stringConst("tuks")),
                List.of(WrappedType.from(String.class), WrappedType.from(String.class)),
                WrappedType.from(String.class),
                method,
                Collections.emptyList()
        ).getInstructions();
        var insns = block.compile();

        Assert.assertEquals("tux", ((LdcInsnNode) insns.get(0)).cst);
        Assert.assertEquals("tuks", ((LdcInsnNode) insns.get(1)).cst);
        Assert.assertEquals(Opcodes.INVOKEDYNAMIC, insns.get(2).getOpcode());

        var indy = (InvokeDynamicInsnNode) insns.get(2);
        var handle = indy.bsm;
        Assert.assertEquals("tucks", indy.name);
        Assert.assertEquals("(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", indy.desc);
        Assert.assertEquals(Type.getInternalName(IRInvokeDynamicExpressionTester.class), handle.getOwner());
        Assert.assertEquals("dummyBootstrap", handle.getName());
        Assert.assertEquals("(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;", handle.getDesc());
    }

    @Test
    public void testInvokeDynamicWithWrappedHandleBootstrap() throws Exception {
        var method = IRInvokeDynamicExpressionTester.class.getMethod("dummyBootstrap", MethodHandles.Lookup.class, String.class, MethodType.class);
        var block = invokeDynamic(
                "tucks",
                List.of(stringConst("tux"), stringConst("tuks")),
                List.of(WrappedType.from(String.class), WrappedType.from(String.class)),
                WrappedType.from(String.class),
                WrappedHandle.getInvokeStaticHandle(method),
                Collections.emptyList()
        ).getInstructions();
        var insns = block.compile();

        Assert.assertEquals("tux", ((LdcInsnNode) insns.get(0)).cst);
        Assert.assertEquals("tuks", ((LdcInsnNode) insns.get(1)).cst);
        Assert.assertEquals(Opcodes.INVOKEDYNAMIC, insns.get(2).getOpcode());

        var indy = (InvokeDynamicInsnNode) insns.get(2);
        var handle = indy.bsm;
        Assert.assertEquals("tucks", indy.name);
        Assert.assertEquals("(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", indy.desc);
        Assert.assertEquals(Type.getInternalName(IRInvokeDynamicExpressionTester.class), handle.getOwner());
        Assert.assertEquals("dummyBootstrap", handle.getName());
        Assert.assertEquals("(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;", handle.getDesc());
    }

    public static CallSite dummyBootstrap(MethodHandles.Lookup lookup, String name, MethodType type) {
        return null;
    }
}
