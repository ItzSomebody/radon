package xyz.itzsomebody.codegen.expressions.predefined;

import org.junit.Assert;
import org.junit.Test;
import org.objectweb.asm.Opcodes;

import java.util.stream.IntStream;

import static xyz.itzsomebody.codegen.expressions.IRExpressions.*;

public class IRArrayLengthTester {
    @Test
    public void testForPrimitiveArray() {
        var block = arrayLength(newArray(int.class, intConst(0), intConst(1))).getInstructions();
        var insns = block.compile();
        var expectedOpcodes = new int[]{
                Opcodes.ICONST_2,
                Opcodes.NEWARRAY,
                Opcodes.DUP,
                Opcodes.ICONST_0,
                Opcodes.ICONST_0,
                Opcodes.IASTORE,
                Opcodes.DUP,
                Opcodes.ICONST_1,
                Opcodes.ICONST_1,
                Opcodes.IASTORE,
                Opcodes.ARRAYLENGTH
        };

        IntStream.range(0, insns.size()).forEach(i -> Assert.assertEquals(expectedOpcodes[i], insns.get(i).getOpcode()));
    }

    @Test
    public void testForStringArray() {
        var block = arrayLength(newArray(String.class, stringConst("tux"), stringConst("tucks"))).getInstructions();
        var insns = block.compile();
        var expectedOpcodes = new int[]{
                Opcodes.ICONST_2,
                Opcodes.ANEWARRAY,
                Opcodes.DUP,
                Opcodes.ICONST_0,
                Opcodes.LDC,
                Opcodes.AASTORE,
                Opcodes.DUP,
                Opcodes.ICONST_1,
                Opcodes.LDC,
                Opcodes.AASTORE,
                Opcodes.ARRAYLENGTH
        };

        IntStream.range(0, insns.size()).forEach(i -> Assert.assertEquals(expectedOpcodes[i], insns.get(i).getOpcode()));
    }
}
