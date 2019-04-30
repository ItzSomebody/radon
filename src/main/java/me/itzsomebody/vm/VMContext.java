package me.itzsomebody.vm;

import me.itzsomebody.vm.datatypes.JWrapper;

public class VMContext {
    private final VMStack stack;
    private final JWrapper[] registers;
    private final int offset;
    private VMTryCatch[] catches;

    public VMContext(int maxStack, int nRegisters, int offset) {
        this.stack = new VMStack(maxStack);
        this.registers = new JWrapper[nRegisters];
        this.offset = offset;
    }

    public VMContext(int maxStack, int nRegisters, int offset, VMTryCatch[] catches) {
        this(maxStack, nRegisters, offset);
        this.catches = catches;
    }

    public VMStack getStack() {
        return stack;
    }

    public JWrapper[] getRegisters() {
        return registers;
    }

    public void initRegister(JWrapper wrapper, int index) {
        registers[index] = wrapper;
    }

    public int getOffset() {
        return offset;
    }

    public VMTryCatch[] getCatches() {
        return catches;
    }
}
