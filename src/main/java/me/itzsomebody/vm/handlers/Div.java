package me.itzsomebody.vm.handlers;

import me.itzsomebody.vm.VM;
import me.itzsomebody.vm.VMException;
import me.itzsomebody.vm.datatypes.JDouble;
import me.itzsomebody.vm.datatypes.JFloat;
import me.itzsomebody.vm.datatypes.JInteger;
import me.itzsomebody.vm.datatypes.JLong;
import me.itzsomebody.vm.datatypes.JTop;
import me.itzsomebody.vm.datatypes.JWrapper;

public class Div extends Handler {
    @Override
    public void handle(VM vm, Object[] operands) {
        JWrapper wrapper = vm.pop();
        if (wrapper instanceof JTop)
            wrapper = vm.pop();

        if (wrapper instanceof JInteger) {
            int first = vm.pop().asInt();
            int second = wrapper.asInt();

            vm.push(new JInteger(first / second));
            return;
        }
        if (wrapper instanceof JLong) {
            vm.pop();
            long first = vm.pop().asLong();
            long second = wrapper.asLong();

            vm.push(new JLong(first / second));
            vm.push(JTop.getTop());
            return;
        }
        if (wrapper instanceof JFloat) {
            float first = vm.pop().asFloat();
            float second = wrapper.asFloat();

            vm.push(new JFloat(first / second));
            return;
        }
        if (wrapper instanceof JDouble) {
            vm.pop();
            double first = vm.pop().asDouble();
            double second = wrapper.asDouble();

            vm.push(new JDouble(first / second));
            vm.push(JTop.getTop());
            return;
        }

        throw new VMException();
    }
}
