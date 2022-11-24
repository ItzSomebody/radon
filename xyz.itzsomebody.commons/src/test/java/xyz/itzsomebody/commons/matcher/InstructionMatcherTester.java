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

package xyz.itzsomebody.commons.matcher;

import org.junit.Assert;
import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import xyz.itzsomebody.commons.matcher.rules.InvocationRule;
import xyz.itzsomebody.commons.matcher.rules.OpcodeRule;

import java.nio.charset.Charset;

public class InstructionMatcherTester {
    @Test
    public void testMatchMethodInvocationAndArgs() {
        var insns = new InsnList();
        insns.add(new LdcInsnNode("tucks"));
        insns.add(new LdcInsnNode("tux"));
        insns.add(new InsnNode(Opcodes.ACONST_NULL));
        insns.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/String", "getBytes", Type.getInternalName(Charset.class), false));

        var pattern = new InstructionPattern(
                new OpcodeRule((node) -> node instanceof LdcInsnNode && ((LdcInsnNode) node).cst.equals("tux"), Opcodes.LDC),
                new OpcodeRule(Opcodes.ACONST_NULL),
                new InvocationRule(Opcodes.INVOKEVIRTUAL, "java/lang/String", "getBytes", Type.getInternalName(Charset.class))
        );
        var matcher = pattern.matcher(insns.getFirst());
        Assert.assertFalse(matcher.matches());
        Assert.assertTrue(matcher.find());

        var captured = matcher.getCaptured(0);
        insns.remove(insns.getFirst());
        for (var i = 0; i < insns.size(); i++) {
            Assert.assertEquals(insns.get(i), captured.get(i));
        }
    }
}
