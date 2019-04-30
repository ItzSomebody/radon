package me.itzsomebody.vm;

import java.io.DataInputStream;

public class Stub {
    public Instruction[][] instructions;

    public Stub() throws Exception {
        DataInputStream din = new DataInputStream(Stub.class.getResourceAsStream("/radon.vm"));

        int nFunctions = din.readShort();
        instructions = new Instruction[nFunctions][];

        for (int i = 0; i < nFunctions; i++) {
            int nInstructions = din.readInt();
            Instruction[] funcInstructions = new Instruction[nInstructions];

            for (int j = 0; j < nInstructions; j++) {
                int opcode = din.readByte();
                int nOperands = din.readByte();

                Object[] operands = new Object[nOperands];

                for (int k = 0; k < nOperands; k++) {
                    int operandType = din.readByte();

                    switch (operandType) {
                        case 0: // INT
                            operands[k] = din.readInt();
                            break;
                        case 1: // LONG
                            operands[k] = din.readLong();
                            break;
                        case 2: // FLOAT
                            operands[k] = Float.intBitsToFloat(din.readInt());
                            break;
                        case 3: // DOUBLE
                            operands[k] = Double.longBitsToDouble(din.readLong());
                            break;
                        case 4: // STRING
                            operands[k] = din.readUTF();
                            break;
                        case 5: // CLASS
                            operands[k] = VM.getClazz(din.readUTF());
                            break;
                        default:
                            throw new VMException();

                    }
                }

                funcInstructions[j] = new Instruction(opcode, operands);
            }

            instructions[i] = funcInstructions;
        }
    }
}
