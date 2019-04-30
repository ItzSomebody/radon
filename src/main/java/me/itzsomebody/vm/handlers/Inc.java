package me.itzsomebody.vm.handlers;

import me.itzsomebody.vm.VM;
import me.itzsomebody.vm.datatypes.JInteger;

public class Inc extends Handler {
    @Override
    public void handle(VM vm, Object[] operands) {
        int index = (Integer) operands[0];
        int operand = (Integer) operands[1];

        vm.storeRegister(new JInteger(vm.loadRegister(index).asInt() + operand), index);
    }
}
