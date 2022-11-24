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
import org.objectweb.asm.tree.TypeInsnNode;
import xyz.itzsomebody.codegen.WrappedType;

import static xyz.itzsomebody.codegen.expressions.IRExpressions.instanceOf;
import static xyz.itzsomebody.codegen.expressions.IRExpressions.nullConst;

public class IRInstanceOfExpressionTester {
    @Test
    public void testInstanceOfClass() {
        var tin = (TypeInsnNode) instanceOf(nullConst(Integer.class), Number.class).getInstructions().compile().get(1);
        Assert.assertEquals("java/lang/Number", tin.desc);
    }

    @Test
    public void testInstanceOfWrappedType() {
        var tin = (TypeInsnNode) instanceOf(nullConst(Integer.class), WrappedType.from(Number.class)).getInstructions().compile().get(1);
        Assert.assertEquals("java/lang/Number", tin.desc);
    }
}
