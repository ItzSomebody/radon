package me.itzsomebody.radon.transformers.obfuscators.virtualizer;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import me.itzsomebody.vm.Instruction;
import org.objectweb.asm.Type;

public class StubCreator {
    private static final int INT = 0;
    private static final int LONG = 1;
    private static final int FLOAT = 2;
    private static final int DOUBLE = 3;
    private static final int STRING = 4;
    private static final int CLASS = 5;

    private List<List<Instruction>> instructionLists;
    private ByteArrayOutputStream out;

    public StubCreator() {
        instructionLists = new ArrayList<>();
        out = new ByteArrayOutputStream();
    }

    public void addInstructionList(List<Instruction> list) {
        instructionLists.add(list);
    }

    public byte[] createStub() throws IOException {
        DataOutputStream dos = new DataOutputStream(out);

        dos.writeShort(instructionLists.size());

        for (List<Instruction> list : instructionLists) {
            dos.writeInt(list.size());

            for (Instruction instruction : list) {
                dos.writeByte(instruction.getOpcode());
                dos.writeByte(instruction.getOperands().length);

                for (Object operand : instruction.getOperands()) {
                    if (operand instanceof Integer) {
                        dos.writeByte(INT);
                        dos.writeInt((Integer) operand);
                    } else if (operand instanceof Long) {
                        dos.writeByte(LONG);
                        dos.writeLong((Long) operand);
                    } else if (operand instanceof Float) {
                        dos.writeByte(FLOAT);
                        dos.writeFloat((Float) operand);
                    } else if (operand instanceof Double) {
                        dos.writeByte(DOUBLE);
                        dos.writeDouble((Double) operand);
                    } else if (operand instanceof String) {
                        dos.writeByte(STRING);
                        dos.writeUTF((String) operand);
                    } else if (operand instanceof Type) {
                        dos.writeByte(CLASS);

                        Type type = (Type) operand;

                        if (type.getSort() == Type.ARRAY)
                            dos.writeUTF(type.getInternalName());
                        else
                            dos.writeUTF(type.getClassName());
                    }
                }
            }
        }

        return out.toByteArray();
    }
}
