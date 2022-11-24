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
import org.objectweb.asm.tree.IntInsnNode;

import java.util.stream.IntStream;

import static xyz.itzsomebody.codegen.expressions.IRExpressions.*;

public class IRNewArrayExpressionTester {
    @Test
    public void testForPrimitiveArray() {
        var block = newArray(int.class, intConst(0), intConst(1)).getInstructions();
        var insns = block.compile();
        var expectedOpcodes = new int[]{
                Opcodes.ICONST_2,
                Opcodes.NEWARRAY,
                Opcodes.DUP,
                Opcodes.ICONST_0,
                Opcodes.ICONST_0,
                Opcodes.IASTORE,
                Opcodes.DUP,
                Opcodes.ICONST_1,
                Opcodes.ICONST_1,
                Opcodes.IASTORE,
        };

        IntStream.range(0, insns.size()).forEach(i -> Assert.assertEquals(expectedOpcodes[i], insns.get(i).getOpcode()));
        Assert.assertEquals(Opcodes.T_INT, ((IntInsnNode) insns.get(1)).operand);
    }

    @Test
    public void testForStringArray() {
        var block = newArray(String.class, stringConst("tux"), stringConst("tucks")).getInstructions();
        var insns = block.compile();
        var expectedOpcodes = new int[]{
                Opcodes.ICONST_2,
                Opcodes.ANEWARRAY,
                Opcodes.DUP,
                Opcodes.ICONST_0,
                Opcodes.LDC,
                Opcodes.AASTORE,
                Opcodes.DUP,
                Opcodes.ICONST_1,
                Opcodes.LDC,
                Opcodes.AASTORE,
        };

        IntStream.range(0, insns.size()).forEach(i -> Assert.assertEquals(expectedOpcodes[i], insns.get(i).getOpcode()));
    }
}
