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
import xyz.itzsomebody.codegen.GenerationContext;

import static xyz.itzsomebody.codegen.expressions.IRExpressions.*;

public class IRSetVariableExpressionTester {
    @Test
    public void testSetIntTypeVariable() {
        var context = new GenerationContext();
        var insns = context.newVariable(byte.class).set(intConst(0)).getInstructions().compile();
        Assert.assertEquals(Opcodes.ICONST_0, insns.get(0).getOpcode());
        Assert.assertEquals(Opcodes.ISTORE, insns.get(1).getOpcode());
    }

    @Test
    public void testSetLongVariable() {
        var context = new GenerationContext();
        var insns = context.newVariable(long.class).set(longConst(0L)).getInstructions().compile();
        Assert.assertEquals(Opcodes.LCONST_0, insns.get(0).getOpcode());
        Assert.assertEquals(Opcodes.LSTORE, insns.get(1).getOpcode());
    }

    @Test
    public void testSetFloatVariable() {
        var context = new GenerationContext();
        var insns = context.newVariable(float.class).set(floatConst(0F)).getInstructions().compile();
        Assert.assertEquals(Opcodes.FCONST_0, insns.get(0).getOpcode());
        Assert.assertEquals(Opcodes.FSTORE, insns.get(1).getOpcode());
    }

    @Test
    public void testSetDoubleVariable() {
        var context = new GenerationContext();
        var insns = context.newVariable(double.class).set(doubleConst(0)).getInstructions().compile();
        Assert.assertEquals(Opcodes.DCONST_0, insns.get(0).getOpcode());
        Assert.assertEquals(Opcodes.DSTORE, insns.get(1).getOpcode());
    }

    @Test
    public void testSetObjectVariable() {
        var context = new GenerationContext();
        var insns = context.newVariable(String.class).set(stringConst("tucks")).getInstructions().compile();
        Assert.assertEquals(Opcodes.LDC, insns.get(0).getOpcode());
        Assert.assertEquals(Opcodes.ASTORE, insns.get(1).getOpcode());
    }
}
