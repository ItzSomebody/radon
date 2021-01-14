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

public class IRSetFieldExpressionTester {
    @Test
    public void testSetObjectField() throws Exception {
        var fin = (FieldInsnNode) setField(nullConst(WrappedType.from(IRSetFieldExpressionTester.TestClass.class)), IRSetFieldExpressionTester.TestClass.class.getField("exampleField"), nullConst(String.class)).getInstructions().compile().get(2);
        Assert.assertEquals(Opcodes.PUTFIELD, fin.getOpcode());
        Assert.assertEquals(Type.getInternalName(IRSetFieldExpressionTester.TestClass.class), fin.owner);
        Assert.assertEquals("exampleField", fin.name);
        Assert.assertEquals("Ljava/lang/String;", fin.desc);
    }

    @Test
    public void testSetPrimitiveField() throws Exception {
        var fin = (FieldInsnNode) setField(nullConst(WrappedType.from(IRSetFieldExpressionTester.TestClass.class)), IRSetFieldExpressionTester.TestClass.class.getField("examplePrimitiveField"), intConst(0)).getInstructions().compile().get(2);
        Assert.assertEquals(Opcodes.PUTFIELD, fin.getOpcode());
        Assert.assertEquals(Type.getInternalName(IRSetFieldExpressionTester.TestClass.class), fin.owner);
        Assert.assertEquals("examplePrimitiveField", fin.name);
        Assert.assertEquals("B", fin.desc);
    }

    @Test
    public void testSetObjectStatic() throws Exception {
        var fin = (FieldInsnNode) setStatic(IRSetFieldExpressionTester.TestClass.class.getField("exampleStaticField"), nullConst(String.class)).getInstructions().compile().get(1);
        Assert.assertEquals(Opcodes.PUTSTATIC, fin.getOpcode());
        Assert.assertEquals(Type.getInternalName(IRSetFieldExpressionTester.TestClass.class), fin.owner);
        Assert.assertEquals("exampleStaticField", fin.name);
        Assert.assertEquals("Ljava/lang/String;", fin.desc);
    }

    @Test
    public void testSetPrimitiveStatic() throws Exception {
        var fin = (FieldInsnNode) setStatic(IRSetFieldExpressionTester.TestClass.class.getField("exampleStaticPrimitiveField"), intConst(0)).getInstructions().compile().get(1);
        Assert.assertEquals(Opcodes.PUTSTATIC, fin.getOpcode());
        Assert.assertEquals(Type.getInternalName(IRSetFieldExpressionTester.TestClass.class), fin.owner);
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
