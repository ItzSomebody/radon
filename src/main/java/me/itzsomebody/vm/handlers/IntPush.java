package me.itzsomebody.vm.handlers;

import me.itzsomebody.vm.VM;
import me.itzsomebody.vm.datatypes.JInteger;

public class IntPush extends Handler {
    @Override
    public void handle(VM vm, Object[] operands) {
        vm.push(new JInteger((Integer) operands[0]));
    }
}
