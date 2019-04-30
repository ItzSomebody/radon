package me.itzsomebody.vm.handlers;

import me.itzsomebody.vm.VM;

public class Jne extends Handler {
    @Override
    public void handle(VM vm, Object[] operands) {
        int jumpTo = vm.pop().asInt();

        if (vm.pop() != vm.pop())
            vm.setPc(jumpTo);
    }
}
