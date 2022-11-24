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

import static xyz.itzsomebody.codegen.expressions.IRExpressions.*;

public class IRArithmeticExpressionTester {
    @Test
    public void testIntMathGeneration() {
        var block = intAdd(intConst(5), intConst(-3)).getInstructions();
        var insns = block.compile();

        Assert.assertEquals(Opcodes.ICONST_5, insns.get(0).getOpcode());
        Assert.assertEquals(Opcodes.BIPUSH, insns.get(1).getOpcode());
        Assert.assertEquals(Opcodes.IADD, insns.get(2).getOpcode());
    }

    @Test
    public void testLongMathGeneration() {
        var block = longMod(longConst(11), longConst(1)).getInstructions();
        var insns = block.compile();

        Assert.assertEquals(Opcodes.LDC, insns.get(0).getOpcode());
        Assert.assertEquals(Opcodes.LCONST_1, insns.get(1).getOpcode());
        Assert.assertEquals(Opcodes.LREM, insns.get(2).getOpcode());
    }

    @Test
    public void testFloatMathGeneration() {
        var block = floatMul(floatConst(0F), floatConst(2.1f)).getInstructions();
        var insns = block.compile();

        Assert.assertEquals(Opcodes.FCONST_0, insns.get(0).getOpcode());
        Assert.assertEquals(Opcodes.LDC, insns.get(1).getOpcode());
        Assert.assertEquals(Opcodes.FMUL, insns.get(2).getOpcode());
    }

    @Test
    public void testDoubleMathGeneration() {
        var block = doubleSub(doubleConst(1D), doubleConst(8.9999D)).getInstructions();
        var insns = block.compile();

        Assert.assertEquals(Opcodes.DCONST_1, insns.get(0).getOpcode());
        Assert.assertEquals(Opcodes.LDC, insns.get(1).getOpcode());
        Assert.assertEquals(Opcodes.DSUB, insns.get(2).getOpcode());
    }
}
