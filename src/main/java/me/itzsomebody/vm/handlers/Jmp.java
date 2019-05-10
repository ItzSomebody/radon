package me.itzsomebody.vm.handlers;

import me.itzsomebody.vm.VM;

public class Jmp extends Handler {
    @Override
    public void handle(VM vm, Object[] operands) {
        vm.setPc(vm.pop().asInt());
    }
}
