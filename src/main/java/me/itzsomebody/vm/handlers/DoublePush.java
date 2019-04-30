package me.itzsomebody.vm.handlers;

import me.itzsomebody.vm.VM;
import me.itzsomebody.vm.datatypes.JDouble;
import me.itzsomebody.vm.datatypes.JTop;

public class DoublePush extends Handler {
    @Override
    public void handle(VM vm, Object[] operands) {
        vm.push(new JDouble((Double) operands[0]));
        vm.push(JTop.getTop());
    }
}
