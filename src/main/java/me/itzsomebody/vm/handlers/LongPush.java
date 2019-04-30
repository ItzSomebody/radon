package me.itzsomebody.vm.handlers;

import me.itzsomebody.vm.VM;
import me.itzsomebody.vm.datatypes.JLong;
import me.itzsomebody.vm.datatypes.JTop;

public class LongPush extends Handler {
    @Override
    public void handle(VM vm, Object[] operands) {
        vm.push(new JLong((Long) operands[0]));
        vm.push(JTop.getTop());
    }
}
