package me.itzsomebody.vm.handlers;

import me.itzsomebody.vm.VM;
import me.itzsomebody.vm.datatypes.JInteger;

public class Jsr extends Handler {
    @Override
    public void handle(VM vm, Object[] operands) {
        int jumpTo = vm.pop().asInt();
        int currentPc = vm.getPc();

        vm.push(new JInteger(currentPc));
        vm.setPc(jumpTo);
    }
}
