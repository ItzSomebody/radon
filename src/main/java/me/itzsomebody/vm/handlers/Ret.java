package me.itzsomebody.vm.handlers;

import me.itzsomebody.vm.VM;

public class Ret extends Handler {
    @Override
    public void handle(VM vm, Object[] operands) {
        vm.setPc(vm.loadRegister((Integer) operands[0]).asInt());
    }
}
