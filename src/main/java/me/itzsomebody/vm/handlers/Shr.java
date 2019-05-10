package me.itzsomebody.vm.handlers;

import me.itzsomebody.vm.VM;
import me.itzsomebody.vm.VMException;
import me.itzsomebody.vm.datatypes.JInteger;
import me.itzsomebody.vm.datatypes.JLong;
import me.itzsomebody.vm.datatypes.JTop;
import me.itzsomebody.vm.datatypes.JWrapper;

public class Shr extends Handler {
    @Override
    public void handle(VM vm, Object[] operands) {
        JWrapper second = vm.pop();
        JWrapper first = vm.pop();

        if (first instanceof JInteger) {
            vm.push(new JInteger(first.asInt() >> second.asInt()));
            return;
        }

        if (first instanceof JLong) {
            vm.pop();
            vm.push(new JLong(first.asLong() >> second.asInt()));
            vm.push(JTop.getTop());
            return;
        }

        throw new VMException();
    }
}
