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

package xyz.itzsomebody.codegen.expressions;

import org.junit.Assert;
import org.junit.Test;
import org.objectweb.asm.Opcodes;
import xyz.itzsomebody.codegen.GenerationContext;

public class IRVariableTester {
    @Test
    public void testGetIntTypeVariable() {
        var context = new GenerationContext();
        var insns = context.newVariable(byte.class).getInstructions().compile();
        Assert.assertEquals(Opcodes.ILOAD, insns.get(0).getOpcode());
    }

    @Test
    public void testGetLongVariable() {
        var context = new GenerationContext();
        var insns = context.newVariable(long.class).getInstructions().compile();
        Assert.assertEquals(Opcodes.LLOAD, insns.get(0).getOpcode());
    }

    @Test
    public void testGetFloatVariable() {
        var context = new GenerationContext();
        var insns = context.newVariable(float.class).getInstructions().compile();
        Assert.assertEquals(Opcodes.FLOAD, insns.get(0).getOpcode());
    }

    @Test
    public void testGetDoubleVariable() {
        var context = new GenerationContext();
        var insns = context.newVariable(double.class).getInstructions().compile();
        Assert.assertEquals(Opcodes.DLOAD, insns.get(0).getOpcode());
    }

    @Test
    public void testGetObjectVariable() {
        var context = new GenerationContext();
        var insns = context.newVariable(String.class).getInstructions().compile();
        Assert.assertEquals(Opcodes.ALOAD, insns.get(0).getOpcode());
    }
}
