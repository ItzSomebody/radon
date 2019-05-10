package me.itzsomebody.vm.handlers;

import me.itzsomebody.vm.VM;

public class Kill extends Handler {
    @Override
    public void handle(VM vm, Object[] operands) {
        vm.setExecuting(false);
    }
}
