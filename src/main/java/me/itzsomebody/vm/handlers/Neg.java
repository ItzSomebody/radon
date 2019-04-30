package me.itzsomebody.vm.handlers;

import me.itzsomebody.vm.VM;
import me.itzsomebody.vm.VMException;
import me.itzsomebody.vm.datatypes.JDouble;
import me.itzsomebody.vm.datatypes.JFloat;
import me.itzsomebody.vm.datatypes.JInteger;
import me.itzsomebody.vm.datatypes.JLong;
import me.itzsomebody.vm.datatypes.JTop;
import me.itzsomebody.vm.datatypes.JWrapper;

public class Neg extends Handler {
    @Override
    public void handle(VM vm, Object[] operands) {
        JWrapper wrapper = vm.pop();
        if (wrapper instanceof JTop)
            wrapper = vm.pop();

        if (wrapper instanceof JInteger) {
            vm.push(new JInteger(-wrapper.asInt()));
            return;
        }
        if (wrapper instanceof JLong) {
            vm.push(new JLong(-wrapper.asLong()));
            vm.push(JTop.getTop());
            return;
        }
        if (wrapper instanceof JFloat) {
            vm.push(new JFloat(-wrapper.asFloat()));
            return;
        }
        if (wrapper instanceof JDouble) {
            vm.push(new JDouble(-wrapper.asDouble()));
            vm.push(JTop.getTop());
            return;
        }

        throw new VMException();
    }
}
