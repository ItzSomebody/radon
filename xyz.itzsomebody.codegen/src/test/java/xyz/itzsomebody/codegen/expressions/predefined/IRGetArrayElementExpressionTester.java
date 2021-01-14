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

public class IRGetArrayElementExpressionTester {
    @Test
    public void testLoadBooleanElement() {
        var block = getArrayElement(nullConst(boolean.class), intConst(0)).getInstructions();
        var insns = block.compile();

        Assert.assertEquals(Opcodes.ACONST_NULL, insns.get(0).getOpcode());
        Assert.assertEquals(Opcodes.ICONST_0, insns.get(1).getOpcode());
        Assert.assertEquals(Opcodes.IALOAD, insns.get(2).getOpcode());
    }

    @Test
    public void testLoadByteElement() {
        var block = getArrayElement(nullConst(byte.class), intConst(0)).getInstructions();
        var insns = block.compile();

        Assert.assertEquals(Opcodes.ACONST_NULL, insns.get(0).getOpcode());
        Assert.assertEquals(Opcodes.ICONST_0, insns.get(1).getOpcode());
        Assert.assertEquals(Opcodes.BALOAD, insns.get(2).getOpcode());
    }

    @Test
    public void testLoadShortElement() {
        var block = getArrayElement(nullConst(short.class), intConst(0)).getInstructions();
        var insns = block.compile();

        Assert.assertEquals(Opcodes.ACONST_NULL, insns.get(0).getOpcode());
        Assert.assertEquals(Opcodes.ICONST_0, insns.get(1).getOpcode());
        Assert.assertEquals(Opcodes.SALOAD, insns.get(2).getOpcode());
    }

    @Test
    public void testLoadCharElement() {
        var block = getArrayElement(nullConst(char.class), intConst(0)).getInstructions();
        var insns = block.compile();

        Assert.assertEquals(Opcodes.ACONST_NULL, insns.get(0).getOpcode());
        Assert.assertEquals(Opcodes.ICONST_0, insns.get(1).getOpcode());
        Assert.assertEquals(Opcodes.CALOAD, insns.get(2).getOpcode());
    }

    @Test
    public void testLoadIntElement() {
        var block = getArrayElement(nullConst(int.class), intConst(0)).getInstructions();
        var insns = block.compile();

        Assert.assertEquals(Opcodes.ACONST_NULL, insns.get(0).getOpcode());
        Assert.assertEquals(Opcodes.ICONST_0, insns.get(1).getOpcode());
        Assert.assertEquals(Opcodes.IALOAD, insns.get(2).getOpcode());
    }

    @Test
    public void testLoadLongElement() {
        var block = getArrayElement(nullConst(long.class), intConst(0)).getInstructions();
        var insns = block.compile();

        Assert.assertEquals(Opcodes.ACONST_NULL, insns.get(0).getOpcode());
        Assert.assertEquals(Opcodes.ICONST_0, insns.get(1).getOpcode());
        Assert.assertEquals(Opcodes.LALOAD, insns.get(2).getOpcode());
    }

    @Test
    public void testLoadFloatElement() {
        var block = getArrayElement(nullConst(float.class), intConst(0)).getInstructions();
        var insns = block.compile();

        Assert.assertEquals(Opcodes.ACONST_NULL, insns.get(0).getOpcode());
        Assert.assertEquals(Opcodes.ICONST_0, insns.get(1).getOpcode());
        Assert.assertEquals(Opcodes.FALOAD, insns.get(2).getOpcode());
    }

    @Test
    public void testLoadDoubleElement() {
        var block = getArrayElement(nullConst(double.class), intConst(0)).getInstructions();
        var insns = block.compile();

        Assert.assertEquals(Opcodes.ACONST_NULL, insns.get(0).getOpcode());
        Assert.assertEquals(Opcodes.ICONST_0, insns.get(1).getOpcode());
        Assert.assertEquals(Opcodes.DALOAD, insns.get(2).getOpcode());
    }

    @Test
    public void testLoadObjectElement() {
        var block = getArrayElement(nullConst(String.class), intConst(0)).getInstructions();
        var insns = block.compile();

        Assert.assertEquals(Opcodes.ACONST_NULL, insns.get(0).getOpcode());
        Assert.assertEquals(Opcodes.ICONST_0, insns.get(1).getOpcode());
        Assert.assertEquals(Opcodes.AALOAD, insns.get(2).getOpcode());
    }
}
