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
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.FieldInsnNode;
import xyz.itzsomebody.codegen.WrappedType;

import static xyz.itzsomebody.codegen.expressions.IRExpressions.*;

public class IRGetFieldExpressionTester {
    @Test
    public void testGetObjectField() throws Exception {
        var fin = (FieldInsnNode) getField(nullConst(WrappedType.from(TestClass.class)), TestClass.class.getField("exampleField")).getInstructions().compile().get(1);
        Assert.assertEquals(Opcodes.GETFIELD, fin.getOpcode());
        Assert.assertEquals(Type.getInternalName(TestClass.class), fin.owner);
        Assert.assertEquals("exampleField", fin.name);
        Assert.assertEquals("Ljava/lang/String;", fin.desc);
    }

    @Test
    public void testGetPrimitiveField() throws Exception {
        var fin = (FieldInsnNode) getField(nullConst(WrappedType.from(TestClass.class)), TestClass.class.getField("examplePrimitiveField")).getInstructions().compile().get(1);
        Assert.assertEquals(Opcodes.GETFIELD, fin.getOpcode());
        Assert.assertEquals(Type.getInternalName(TestClass.class), fin.owner);
        Assert.assertEquals("examplePrimitiveField", fin.name);
        Assert.assertEquals("B", fin.desc);
    }

    @Test
    public void testGetObjectStatic() throws Exception {
        var fin = (FieldInsnNode) getStatic(TestClass.class.getField("exampleStaticField")).getInstructions().compile().get(0);
        Assert.assertEquals(Opcodes.GETSTATIC, fin.getOpcode());
        Assert.assertEquals(Type.getInternalName(TestClass.class), fin.owner);
        Assert.assertEquals("exampleStaticField", fin.name);
        Assert.assertEquals("Ljava/lang/String;", fin.desc);
    }

    @Test
    public void testGetPrimitiveStatic() throws Exception {
        var fin = (FieldInsnNode) getStatic(TestClass.class.getField("exampleStaticPrimitiveField")).getInstructions().compile().get(0);
        Assert.assertEquals(Opcodes.GETSTATIC, fin.getOpcode());
        Assert.assertEquals(Type.getInternalName(TestClass.class), fin.owner);
        Assert.assertEquals("exampleStaticPrimitiveField", fin.name);
        Assert.assertEquals("B", fin.desc);
    }

    public static class TestClass {
        public String exampleField;
        public byte examplePrimitiveField;
        public static String exampleStaticField;
        public static byte exampleStaticPrimitiveField;
    }
}
