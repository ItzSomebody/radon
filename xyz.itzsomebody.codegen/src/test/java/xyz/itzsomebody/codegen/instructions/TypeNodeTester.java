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

package xyz.itzsomebody.codegen.instructions;

import org.junit.Assert;
import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.TypeInsnNode;
import xyz.itzsomebody.codegen.WrappedType;

public class TypeNodeTester {
    @Test
    public void testNewInstanceForCorrectOpcode() {
        Assert.assertEquals(Opcodes.NEW, TypeNode.newInstance(WrappedType.from(String.class)).getNode().getOpcode());
    }

    @Test
    public void testNewInstanceForValidType() {
        Assert.assertEquals("java/lang/String", ((TypeInsnNode) TypeNode.newInstance(WrappedType.from(String.class)).getNode()).desc);
    }
}
