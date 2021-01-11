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
import org.objectweb.asm.tree.FieldInsnNode;
import xyz.itzsomebody.codegen.WrappedType;

public class FieldAccessNodeTester {
    @Test
    public void testGetStatic() {
        var getStatic = (FieldInsnNode) FieldAccessNode.getStatic(WrappedType.from(Integer.class), "TYPE", WrappedType.from(Class.class)).getNode();
        Assert.assertEquals(Opcodes.GETSTATIC, getStatic.getOpcode());
        Assert.assertEquals("java/lang/Integer", getStatic.owner);
        Assert.assertEquals("TYPE", getStatic.name);
        Assert.assertEquals("Ljava/lang/Class;", getStatic.desc);
    }

    @Test
    public void testPutStatic() {
        var putStatic = (FieldInsnNode) FieldAccessNode.putStatic(WrappedType.from(String.class), "hash", WrappedType.from(int.class)).getNode();
        Assert.assertEquals(Opcodes.PUTSTATIC, putStatic.getOpcode());
        Assert.assertEquals("java/lang/String", putStatic.owner);
        Assert.assertEquals("hash", putStatic.name);
        Assert.assertEquals("I", putStatic.desc);
    }

    @Test
    public void testGetField() {
        var getField = (FieldInsnNode) FieldAccessNode.getField(WrappedType.from(Integer.class), "value", WrappedType.from(int.class)).getNode();
        Assert.assertEquals(Opcodes.GETFIELD, getField.getOpcode());
        Assert.assertEquals("java/lang/Integer", getField.owner);
        Assert.assertEquals("value", getField.name);
        Assert.assertEquals("I", getField.desc);
    }

    @Test
    public void testPutField() {
        var putField = (FieldInsnNode) FieldAccessNode.putField(WrappedType.from(String.class), "value", WrappedType.from(byte[].class)).getNode();
        Assert.assertEquals(Opcodes.PUTFIELD, putField.getOpcode());
        Assert.assertEquals("java/lang/String", putField.owner);
        Assert.assertEquals("value", putField.name);
        Assert.assertEquals("[B", putField.desc);
    }
}
