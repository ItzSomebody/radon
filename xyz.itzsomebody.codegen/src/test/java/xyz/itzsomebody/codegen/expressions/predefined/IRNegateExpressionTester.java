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
import org.objectweb.asm.tree.InsnNode;
import xyz.itzsomebody.codegen.exceptions.UncompilableNodeException;

import static xyz.itzsomebody.codegen.expressions.IRExpressions.*;

public class IRNegateExpressionTester {
    @Test
    public void testNegateBoolean() {
        var insn = (InsnNode) negate(booleanConst(false)).getInstructions().compile().get(1);
        Assert.assertEquals(Opcodes.INEG, insn.getOpcode());
    }

    @Test
    public void testNegateInt() {
        var insn = (InsnNode) negate(intConst(0)).getInstructions().compile().get(1);
        Assert.assertEquals(Opcodes.INEG, insn.getOpcode());
    }

    @Test
    public void testNegateLong() {
        var insn = (InsnNode) negate(longConst(0L)).getInstructions().compile().get(1);
        Assert.assertEquals(Opcodes.LNEG, insn.getOpcode());
    }

    @Test
    public void testNegateFloat() {
        var insn = (InsnNode) negate(floatConst(0F)).getInstructions().compile().get(1);
        Assert.assertEquals(Opcodes.FNEG, insn.getOpcode());
    }

    @Test
    public void testNegateDouble() {
        var insn = (InsnNode) negate(doubleConst(0D)).getInstructions().compile().get(1);
        Assert.assertEquals(Opcodes.DNEG, insn.getOpcode());
    }

    @Test(expected = UncompilableNodeException.class)
    public void ensureExceptionThrownOnNonNegatableOperand() {
        negate(nullConst(String.class)).getInstructions();
    }
}
