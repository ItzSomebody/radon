package xyz.itzsomebody.codegen.expressions.predefined;

import org.junit.Assert;
import org.junit.Test;
import org.objectweb.asm.Opcodes;

import static xyz.itzsomebody.codegen.expressions.IRExpressions.*;

public class IRSetArrayElementExpressionTester {
    @Test
    public void testStoreBooleanElement() {
        var block = setArrayElement(nullConst(boolean.class), intConst(0), booleanConst(false)).getInstructions();
        var insns = block.compile();

        Assert.assertEquals(Opcodes.ACONST_NULL, insns.get(0).getOpcode());
        Assert.assertEquals(Opcodes.ICONST_0, insns.get(1).getOpcode());
        Assert.assertEquals(Opcodes.ICONST_0, insns.get(2).getOpcode());
        Assert.assertEquals(Opcodes.IASTORE, insns.get(3).getOpcode());
    }

    @Test
    public void testStoreByteElement() {
        var block = setArrayElement(nullConst(byte.class), intConst(0), intConst(0)).getInstructions();
        var insns = block.compile();

        Assert.assertEquals(Opcodes.ACONST_NULL, insns.get(0).getOpcode());
        Assert.assertEquals(Opcodes.ICONST_0, insns.get(1).getOpcode());
        Assert.assertEquals(Opcodes.ICONST_0, insns.get(2).getOpcode());
        Assert.assertEquals(Opcodes.BASTORE, insns.get(3).getOpcode());
    }

    @Test
    public void testStoreShortElement() {
        var block = setArrayElement(nullConst(short.class), intConst(0), intConst(0)).getInstructions();
        var insns = block.compile();

        Assert.assertEquals(Opcodes.ACONST_NULL, insns.get(0).getOpcode());
        Assert.assertEquals(Opcodes.ICONST_0, insns.get(1).getOpcode());
        Assert.assertEquals(Opcodes.ICONST_0, insns.get(2).getOpcode());
        Assert.assertEquals(Opcodes.SASTORE, insns.get(3).getOpcode());
    }

    @Test
    public void testStoreCharElement() {
        var block = setArrayElement(nullConst(char.class), intConst(0), intConst(0)).getInstructions();
        var insns = block.compile();

        Assert.assertEquals(Opcodes.ACONST_NULL, insns.get(0).getOpcode());
        Assert.assertEquals(Opcodes.ICONST_0, insns.get(1).getOpcode());
        Assert.assertEquals(Opcodes.ICONST_0, insns.get(2).getOpcode());
        Assert.assertEquals(Opcodes.CASTORE, insns.get(3).getOpcode());
    }

    @Test
    public void testStoreIntElement() {
        var block = setArrayElement(nullConst(int.class), intConst(0), intConst(0)).getInstructions();
        var insns = block.compile();

        Assert.assertEquals(Opcodes.ACONST_NULL, insns.get(0).getOpcode());
        Assert.assertEquals(Opcodes.ICONST_0, insns.get(1).getOpcode());
        Assert.assertEquals(Opcodes.ICONST_0, insns.get(2).getOpcode());
        Assert.assertEquals(Opcodes.IASTORE, insns.get(3).getOpcode());
    }

    @Test
    public void testStoreLongElement() {
        var block = setArrayElement(nullConst(long.class), intConst(0), longConst(0L)).getInstructions();
        var insns = block.compile();

        Assert.assertEquals(Opcodes.ACONST_NULL, insns.get(0).getOpcode());
        Assert.assertEquals(Opcodes.ICONST_0, insns.get(1).getOpcode());
        Assert.assertEquals(Opcodes.LCONST_0, insns.get(2).getOpcode());
        Assert.assertEquals(Opcodes.LASTORE, insns.get(3).getOpcode());
    }

    @Test
    public void testStoreFloatElement() {
        var block = setArrayElement(nullConst(float.class), intConst(0), floatConst(0F)).getInstructions();
        var insns = block.compile();

        Assert.assertEquals(Opcodes.ACONST_NULL, insns.get(0).getOpcode());
        Assert.assertEquals(Opcodes.ICONST_0, insns.get(1).getOpcode());
        Assert.assertEquals(Opcodes.FCONST_0, insns.get(2).getOpcode());
        Assert.assertEquals(Opcodes.FASTORE, insns.get(3).getOpcode());
    }

    @Test
    public void testStoreDoubleElement() {
        var block = setArrayElement(nullConst(double.class), intConst(0), doubleConst(0D)).getInstructions();
        var insns = block.compile();

        Assert.assertEquals(Opcodes.ACONST_NULL, insns.get(0).getOpcode());
        Assert.assertEquals(Opcodes.ICONST_0, insns.get(1).getOpcode());
        Assert.assertEquals(Opcodes.DCONST_0, insns.get(2).getOpcode());
        Assert.assertEquals(Opcodes.DASTORE, insns.get(3).getOpcode());
    }

    @Test
    public void testStoreObjectElement() {
        var block = setArrayElement(nullConst(String.class), intConst(0), nullConst(String.class)).getInstructions();
        var insns = block.compile();

        Assert.assertEquals(Opcodes.ACONST_NULL, insns.get(0).getOpcode());
        Assert.assertEquals(Opcodes.ICONST_0, insns.get(1).getOpcode());
        Assert.assertEquals(Opcodes.ACONST_NULL, insns.get(2).getOpcode());
        Assert.assertEquals(Opcodes.AASTORE, insns.get(3).getOpcode());
    }
}
