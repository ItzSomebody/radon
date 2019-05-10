package me.itzsomebody.vm.handlers;

import me.itzsomebody.vm.VM;

public class Pop extends Handler {
    @Override
    public void handle(VM vm, Object[] operands) {
        vm.pop();
    }
}
