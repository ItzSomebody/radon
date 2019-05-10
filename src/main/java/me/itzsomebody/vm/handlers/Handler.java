package me.itzsomebody.vm.handlers;

import me.itzsomebody.vm.VM;

public abstract class Handler {
    public abstract void handle(VM vm, Object[] operands) throws Throwable;
}
