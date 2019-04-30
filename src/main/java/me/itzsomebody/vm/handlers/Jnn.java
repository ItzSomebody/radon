package me.itzsomebody.vm.handlers;

import me.itzsomebody.vm.VM;

public class Jnn extends Handler {
    @Override
    public void handle(VM vm, Object[] operands) {
        int jumpTo = vm.pop().asInt();

        if (vm.pop() != null)
            vm.setPc(jumpTo);
    }
}
