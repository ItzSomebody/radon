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

package xyz.itzsomebody.commons;

import org.junit.Assert;
import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class MaxLocalsUpdaterTester {
    @Test
    public void testParameterSizeEvaluation() {
        var node = new MethodNode();
        node.desc = "(IIIJD)V";
        node.access = Opcodes.ACC_STATIC;

        MaxLocalsUpdater.update(node);
        Assert.assertEquals(1 + 1 + 1 + 2 + 2, node.maxLocals);
    }

    @Test
    public void testEvaluationByBytecodeStatic1() {
        var node = new MethodNode();
        node.desc = "(IIIJD)V";
        node.access = Opcodes.ACC_STATIC;
        node.instructions.add(new InsnNode(Opcodes.ICONST_0));
        node.instructions.add(new VarInsnNode(Opcodes.ISTORE, 69));

        MaxLocalsUpdater.update(node);
        Assert.assertEquals(69 + 1, node.maxLocals);
    }

    @Test
    public void testEvaluationByBytecodeStatic2() {
        var node = new MethodNode();
        node.desc = "(IIIJD)V";
        node.access = Opcodes.ACC_STATIC;
        node.instructions.add(new InsnNode(Opcodes.LCONST_0));
        node.instructions.add(new VarInsnNode(Opcodes.LSTORE, 69));

        MaxLocalsUpdater.update(node);
        Assert.assertEquals(69 + 2, node.maxLocals);
    }

    @Test
    public void testEvaluationByBytecodeVirtual1() {
        var node = new MethodNode();
        node.desc = "(IIIJD)V";

        MaxLocalsUpdater.update(node);
        Assert.assertEquals(1 + 1 + 1 + 1 + 2 + 2, node.maxLocals);
    }

    @Test
    public void testEvaluationByBytecodeVirtual2() {
        var node = new MethodNode();
        node.desc = "(IIIJD)V";
        node.instructions.add(new InsnNode(Opcodes.FCONST_0));
        node.instructions.add(new VarInsnNode(Opcodes.FSTORE, 69));

        MaxLocalsUpdater.update(node);
        Assert.assertEquals(69 + 1, node.maxLocals);
    }
}
