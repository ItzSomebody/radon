package me.itzsomebody.vm;

public class Instruction {
    private final int opcode;
    private Object[] operands;

    public Instruction(int opcode, Object[] operands) {
        this.opcode = opcode;
        this.operands = operands;
    }

    public int getOpcode() {
        return opcode;
    }

    public void setOperands(Object[] operands) {
        this.operands = operands;
    }

    public Object[] getOperands() {
        return operands;
    }
}
