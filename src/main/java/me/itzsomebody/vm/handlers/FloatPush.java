package me.itzsomebody.vm.handlers;

import me.itzsomebody.vm.VM;
import me.itzsomebody.vm.datatypes.JFloat;

public class FloatPush extends Handler {
    @Override
    public void handle(VM vm, Object[] operands) {
        vm.push(new JFloat((Float) operands[0]));
    }
}
