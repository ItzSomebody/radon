package me.itzsomebody.vm;

import me.itzsomebody.vm.datatypes.JWrapper;

public class VMStack {
    public JWrapper[] stack;
    private final int maxSize;
    private int pointer;

    public VMStack(int maxSize) {
        this.stack = new JWrapper[maxSize];
        this.maxSize = maxSize;
        this.pointer = 0;
    }

    public void push(JWrapper wrapper) {
        stack[pointer++] = wrapper;
    }

    public JWrapper pop() {
        JWrapper wrapper = stack[--pointer];
        stack[pointer] = null;
        return wrapper;
    }

    public void clear() {
        this.stack = new JWrapper[maxSize];
        this.pointer = 0;
    }
}
