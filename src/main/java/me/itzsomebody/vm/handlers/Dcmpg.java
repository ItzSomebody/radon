package me.itzsomebody.vm.handlers;

import me.itzsomebody.vm.VM;
import me.itzsomebody.vm.datatypes.JInteger;

public class Dcmpg extends Handler {
    @Override
    public void handle(VM vm, Object[] operands) {
        vm.pop();
        double second = vm.pop().asLong();

        vm.pop();
        double first = vm.pop().asLong();

        if (Double.isNaN(first) || Double.isNaN(second)) {
            vm.push(new JInteger(1));
            return;
        }

        double result = first - second;

        if (result == 0)
            vm.push(new JInteger(0));
        else if (result > 0)
            vm.push(new JInteger(1));
        else
            vm.push(new JInteger(-1));
    }
}
