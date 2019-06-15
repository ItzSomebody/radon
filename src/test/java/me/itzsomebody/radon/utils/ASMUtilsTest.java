package me.itzsomebody.radon.utils;

import me.itzsomebody.radon.exceptions.RadonException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.powermock.api.mockito.PowerMockito;

public class ASMUtilsTest {
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void testIsReturn() {
        Assert.assertFalse(ASMUtils.isReturn(Opcodes.NOP));
        Assert.assertFalse(ASMUtils.isReturn(Opcodes.GETSTATIC));
        Assert.assertTrue(ASMUtils.isReturn(Opcodes.ARETURN));
    }

    @Test
    public void testIsInstruction() {
        Assert.assertFalse(ASMUtils.isInstruction(
                PowerMockito.mock(FrameNode.class)));
        Assert.assertFalse(ASMUtils.isInstruction(
                PowerMockito.mock(LabelNode.class)));
        Assert.assertFalse(ASMUtils.isInstruction(
                PowerMockito.mock(LineNumberNode.class)));
        Assert.assertTrue(ASMUtils.isInstruction(
                PowerMockito.mock(InsnNode.class)));
    }

    @Test
    public void testIsIntInsn() {
        Assert.assertTrue(ASMUtils.isIntInsn(new LdcInsnNode(Opcodes.NOP)));
        Assert.assertFalse(ASMUtils.isIntInsn(new LdcInsnNode(null)));
        Assert.assertFalse(ASMUtils.isIntInsn(null));
    }

    @Test
    public void testIsLongInsn() {
        Assert.assertTrue(ASMUtils.isLongInsn(new LdcInsnNode((long) Opcodes.NOP)));
        Assert.assertFalse(ASMUtils.isLongInsn(new LdcInsnNode(null)));
    }

    @Test
    public void testIsFloatInsn() {
        Assert.assertTrue(
                ASMUtils.isFloatInsn(new LdcInsnNode((float) Opcodes.NOP)));

        Assert.assertFalse(ASMUtils.isFloatInsn(new LdcInsnNode(null)));
    }

    @Test
    public void testIsDoubleInsn() {
        Assert.assertTrue(
                ASMUtils.isDoubleInsn(new LdcInsnNode((double) Opcodes.NOP)));

        Assert.assertFalse(ASMUtils.isDoubleInsn(new LdcInsnNode(null)));
    }

    @Test
    public void testGetNumberInsnInt() {
        AbstractInsnNode actual = ASMUtils.getNumberInsn(4);
        Assert.assertEquals(Opcodes.ICONST_4, actual.getOpcode());

        actual = ASMUtils.getNumberInsn(6);
        Assert.assertEquals(Opcodes.BIPUSH, actual.getOpcode());

        actual = ASMUtils.getNumberInsn(8198);
        Assert.assertEquals(Opcodes.SIPUSH, actual.getOpcode());

        actual = ASMUtils.getNumberInsn(134_217_734);
        Assert.assertEquals(Opcodes.LDC, actual.getOpcode());
    }

    @Test
    public void testGetNumberInsnLong() {
        AbstractInsnNode actual = ASMUtils.getNumberInsn(0L);
        Assert.assertEquals(Opcodes.LCONST_0, actual.getOpcode());

        actual = ASMUtils.getNumberInsn(2L);
        Assert.assertEquals(Opcodes.LDC, actual.getOpcode());
    }

    @Test
    public void testGetNumberInsnFloat() {
        AbstractInsnNode actual = ASMUtils.getNumberInsn(0.0f);
        Assert.assertEquals(Opcodes.FCONST_0, actual.getOpcode());

        actual = ASMUtils.getNumberInsn(11.0f);
        Assert.assertEquals(Opcodes.LDC, actual.getOpcode());
    }

    @Test
    public void testGetNumberInsnDouble() {
        AbstractInsnNode actual = ASMUtils.getNumberInsn(0.0);
        Assert.assertEquals(Opcodes.DCONST_0, actual.getOpcode());

        actual = ASMUtils.getNumberInsn(3.0);
        Assert.assertEquals(Opcodes.LDC, actual.getOpcode());
    }

    @Test
    public void testGetIntegerFromInsn() {
        Assert.assertEquals(Opcodes.ACONST_NULL,
                ASMUtils.getIntegerFromInsn(new InsnNode(Opcodes.ICONST_1)));

        Assert.assertEquals(Opcodes.ACONST_NULL,
                ASMUtils.getIntegerFromInsn(new IntInsnNode(Opcodes.ICONST_1, 1)));

        Assert.assertEquals(Opcodes.ACONST_NULL,
                ASMUtils.getIntegerFromInsn(new LdcInsnNode(Opcodes.ACONST_NULL)));

        thrown.expect(RadonException.class);
        ASMUtils.getIntegerFromInsn(new InsnNode(Opcodes.NOP));
    }

    @Test
    public void testGetLongFromInsn() {
        Assert.assertEquals((long) Opcodes.NOP,
                ASMUtils.getLongFromInsn(new InsnNode(Opcodes.LCONST_0)));

        Assert.assertEquals((long) Opcodes.ACONST_NULL,
                ASMUtils.getLongFromInsn(new LdcInsnNode(1L)));

        thrown.expect(RadonException.class);
        ASMUtils.getLongFromInsn(new InsnNode(Opcodes.NOP));
    }

    @Test
    public void testGetFloatFromInsn() {
        Assert.assertEquals((float) Opcodes.NOP,
                ASMUtils.getFloatFromInsn(new InsnNode(Opcodes.FCONST_0)), 0);

        Assert.assertEquals((float) Opcodes.ACONST_NULL,
                ASMUtils.getFloatFromInsn(new LdcInsnNode(1.0f)), 0);

        thrown.expect(RadonException.class);
        ASMUtils.getFloatFromInsn(new InsnNode(Opcodes.NOP));
    }

    @Test
    public void testGetDoubleFromInsn() {
        Assert.assertEquals((double) Opcodes.NOP,
                ASMUtils.getDoubleFromInsn(new InsnNode(Opcodes.DCONST_0)), 0);

        Assert.assertEquals((double) Opcodes.ACONST_NULL,
                ASMUtils.getDoubleFromInsn(new LdcInsnNode(1.0)), 0);

        thrown.expect(RadonException.class);
        ASMUtils.getDoubleFromInsn(new InsnNode(Opcodes.NOP));
    }

    @Test
    public void testGetGenericMethodDesc() {
        Assert.assertEquals("(ILjava/lang/Object;)[I",
                ASMUtils.getGenericMethodDesc("(ILjava/lang/Integer;)[I"));
    }
}
