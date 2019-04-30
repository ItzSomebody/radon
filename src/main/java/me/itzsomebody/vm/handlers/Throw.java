package me.itzsomebody.vm.handlers;

import me.itzsomebody.vm.VM;

public class Throw extends Handler {
    @Override
    public void handle(VM vm, Object[] operands) throws Throwable {
        throw (Throwable) vm.pop().asObj();
    }
}
