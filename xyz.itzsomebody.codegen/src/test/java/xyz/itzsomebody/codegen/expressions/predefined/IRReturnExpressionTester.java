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

public class IRReturnExpressionTester {
    @Test
    public void testReturnBoolean() {
        Assert.assertEquals(Opcodes.IRETURN, returnMe(booleanConst(false)).getInstructions().compile().get(1).getOpcode());
    }

    @Test
    public void testReturnInt() {
        Assert.assertEquals(Opcodes.IRETURN, returnMe(intConst(0)).getInstructions().compile().get(1).getOpcode());
    }

    @Test
    public void testReturnLong() {
        Assert.assertEquals(Opcodes.LRETURN, returnMe(longConst(0L)).getInstructions().compile().get(1).getOpcode());
    }

    @Test
    public void testReturnFloat() {
        Assert.assertEquals(Opcodes.FRETURN, returnMe(floatConst(0F)).getInstructions().compile().get(1).getOpcode());
    }

    @Test
    public void testReturnDouble() {
        Assert.assertEquals(Opcodes.DRETURN, returnMe(doubleConst(0D)).getInstructions().compile().get(1).getOpcode());
    }

    @Test
    public void testReturnObject() {
        Assert.assertEquals(Opcodes.ARETURN, returnMe(nullConst(String.class)).getInstructions().compile().get(1).getOpcode());
    }

    @Test
    public void testReturnNothing() {
        Assert.assertEquals(Opcodes.RETURN, returnMe(null).getInstructions().compile().get(0).getOpcode());
    }
}
