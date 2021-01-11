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
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import xyz.itzsomebody.codegen.WrappedType;

public class NewArrayNodeTester {
    @Test
    public void testPrimitiveArrayType() {
        Assert.assertEquals(Opcodes.NEWARRAY, new NewArrayNode(WrappedType.from(boolean.class)).getNode().getOpcode());
        Assert.assertEquals(Type.BOOLEAN, ((IntInsnNode) new NewArrayNode(WrappedType.from(boolean.class)).getNode()).operand);

        Assert.assertEquals(Opcodes.NEWARRAY, new NewArrayNode(WrappedType.from(char.class)).getNode().getOpcode());
        Assert.assertEquals(Type.CHAR, ((IntInsnNode) new NewArrayNode(WrappedType.from(char.class)).getNode()).operand);

        Assert.assertEquals(Opcodes.NEWARRAY, new NewArrayNode(WrappedType.from(byte.class)).getNode().getOpcode());
        Assert.assertEquals(Type.BYTE, ((IntInsnNode) new NewArrayNode(WrappedType.from(byte.class)).getNode()).operand);

        Assert.assertEquals(Opcodes.NEWARRAY, new NewArrayNode(WrappedType.from(short.class)).getNode().getOpcode());
        Assert.assertEquals(Type.SHORT, ((IntInsnNode) new NewArrayNode(WrappedType.from(short.class)).getNode()).operand);

        Assert.assertEquals(Opcodes.NEWARRAY, new NewArrayNode(WrappedType.from(int.class)).getNode().getOpcode());
        Assert.assertEquals(Type.INT, ((IntInsnNode) new NewArrayNode(WrappedType.from(int.class)).getNode()).operand);

        Assert.assertEquals(Opcodes.NEWARRAY, new NewArrayNode(WrappedType.from(long.class)).getNode().getOpcode());
        Assert.assertEquals(Type.LONG, ((IntInsnNode) new NewArrayNode(WrappedType.from(long.class)).getNode()).operand);

        Assert.assertEquals(Opcodes.NEWARRAY, new NewArrayNode(WrappedType.from(float.class)).getNode().getOpcode());
        Assert.assertEquals(Type.FLOAT, ((IntInsnNode) new NewArrayNode(WrappedType.from(float.class)).getNode()).operand);

        Assert.assertEquals(Opcodes.NEWARRAY, new NewArrayNode(WrappedType.from(double.class)).getNode().getOpcode());
        Assert.assertEquals(Type.DOUBLE, ((IntInsnNode) new NewArrayNode(WrappedType.from(double.class)).getNode()).operand);
    }

    @Test
    public void testNonPrimitiveArrayType() {
        Assert.assertEquals(Opcodes.ANEWARRAY, new NewArrayNode(WrappedType.from(String.class)).getNode().getOpcode());
        Assert.assertEquals("java/lang/String", ((TypeInsnNode) new NewArrayNode(WrappedType.from(String.class)).getNode()).desc);

        Assert.assertEquals(Opcodes.ANEWARRAY, new NewArrayNode(WrappedType.from(Integer.class)).getNode().getOpcode());
        Assert.assertEquals("java/lang/Integer", ((TypeInsnNode) new NewArrayNode(WrappedType.from(Integer.class)).getNode()).desc);
    }
}
