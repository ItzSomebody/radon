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
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import xyz.itzsomebody.codegen.WrappedType;

import java.util.Collections;

import static xyz.itzsomebody.codegen.expressions.IRExpressions.newInstance;

public class IRNewInstanceExpressionTester {
    @Test
    public void testNewInstanceViaConstructor() throws Exception {
        var insns = newInstance(String.class.getConstructor()).getInstructions().compile();
        var newOp = (TypeInsnNode) insns.get(0);
        Assert.assertEquals("java/lang/String", newOp.desc);
        Assert.assertEquals(Opcodes.DUP, insns.get(1).getOpcode());
        var invoke = (MethodInsnNode) insns.get(2);
        Assert.assertEquals("java/lang/String", invoke.owner);
        Assert.assertEquals("<init>", invoke.name);
        Assert.assertEquals("()V", invoke.desc);
    }

    @Test
    public void testNewInstanceViaWrappedType() throws Exception {
        var insns = newInstance(WrappedType.from(String.class), Collections.emptyList(), Collections.emptyList()).getInstructions().compile();
        var newOp = (TypeInsnNode) insns.get(0);
        Assert.assertEquals("java/lang/String", newOp.desc);
        Assert.assertEquals(Opcodes.DUP, insns.get(1).getOpcode());
        var invoke = (MethodInsnNode) insns.get(2);
        Assert.assertEquals("java/lang/String", invoke.owner);
        Assert.assertEquals("<init>", invoke.name);
        Assert.assertEquals("()V", invoke.desc);
    }
}
