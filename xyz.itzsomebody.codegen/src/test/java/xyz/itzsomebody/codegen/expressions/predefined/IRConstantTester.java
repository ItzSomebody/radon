package xyz.itzsomebody.codegen.expressions.predefined;

import org.junit.Assert;
import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;

import static xyz.itzsomebody.codegen.expressions.IRExpressions.*;

public class IRConstantTester { // todo: test constantdynamic:tm:
    @Test
    public void testNullConst() {
        var block = nullConst(String.class).getInstructions();
        var insns = block.compile();

        Assert.assertEquals(Opcodes.ACONST_NULL, insns.get(0).getOpcode());
    }

    @Test
    public void testIntConst() {
        Assert.assertEquals(Opcodes.ICONST_0, intConst(0).getInstructions().compile().get(0).getOpcode());
        Assert.assertEquals(Opcodes.BIPUSH, intConst(Byte.MAX_VALUE).getInstructions().compile().get(0).getOpcode());
        Assert.assertEquals(Byte.MAX_VALUE, ((IntInsnNode) intConst(Byte.MAX_VALUE).getInstructions().compile().get(0)).operand);
        Assert.assertEquals(Opcodes.SIPUSH, intConst(Short.MAX_VALUE).getInstructions().compile().get(0).getOpcode());
        Assert.assertEquals(Short.MAX_VALUE, ((IntInsnNode) intConst(Short.MAX_VALUE).getInstructions().compile().get(0)).operand);
        Assert.assertEquals(Opcodes.LDC, intConst(Integer.MAX_VALUE).getInstructions().compile().get(0).getOpcode());
        Assert.assertEquals(Integer.MAX_VALUE, ((LdcInsnNode) intConst(Integer.MAX_VALUE).getInstructions().compile().get(0)).cst);
    }

    @Test
    public void testLongConst() {
        Assert.assertEquals(Opcodes.LCONST_0, longConst(0L).getInstructions().compile().get(0).getOpcode());
        Assert.assertEquals(Opcodes.LDC, longConst(Long.MAX_VALUE).getInstructions().compile().get(0).getOpcode());
        Assert.assertEquals(Long.MAX_VALUE, ((LdcInsnNode) longConst(Long.MAX_VALUE).getInstructions().compile().get(0)).cst);
    }

    @Test
    public void testFloatConst() {
        Assert.assertEquals(Opcodes.FCONST_0, floatConst(0F).getInstructions().compile().get(0).getOpcode());
        Assert.assertEquals(Opcodes.LDC, floatConst(Float.MAX_VALUE).getInstructions().compile().get(0).getOpcode());
        Assert.assertEquals(Float.MAX_VALUE, ((LdcInsnNode) floatConst(Float.MAX_VALUE).getInstructions().compile().get(0)).cst);
    }

    @Test
    public void testDoubleConst() {
        Assert.assertEquals(Opcodes.DCONST_0, doubleConst(0D).getInstructions().compile().get(0).getOpcode());
        Assert.assertEquals(Opcodes.LDC, doubleConst(Double.MAX_VALUE).getInstructions().compile().get(0).getOpcode());
        Assert.assertEquals(Double.MAX_VALUE, ((LdcInsnNode) doubleConst(Double.MAX_VALUE).getInstructions().compile().get(0)).cst);
    }

    @Test
    public void testStringConst() {
        Assert.assertEquals(Opcodes.LDC, stringConst("tux").getInstructions().compile().get(0).getOpcode());
        Assert.assertEquals("tux", ((LdcInsnNode) stringConst("tux").getInstructions().compile().get(0)).cst);
    }

    @Test
    public void testClassConst() {
        Assert.assertEquals(Opcodes.LDC, classConst(String.class).getInstructions().compile().get(0).getOpcode());
        Assert.assertEquals(Type.getType(String.class), ((LdcInsnNode) classConst(String.class).getInstructions().compile().get(0)).cst);
    }
}
