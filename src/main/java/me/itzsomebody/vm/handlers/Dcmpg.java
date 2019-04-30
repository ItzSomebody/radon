package me.itzsomebody.vm.handlers;

import me.itzsomebody.vm.VM;
import me.itzsomebody.vm.datatypes.JInteger;

public class Dcmpg extends Handler {
    @Override
    public void handle(VM vm, Object[] operands) {
        double first, second, result;

        vm.pop();
        second = vm.pop().asLong();

        vm.pop();
        first = vm.pop().asLong();

        if (Double.isNaN(first) || Double.isNaN(second)) {
            vm.push(new JInteger(1));
            return;
        }

        result = first - second;

        if (result == 0)
            vm.push(new JInteger(0));
        else if (result > 0)
            vm.push(new JInteger(1));
        else
            vm.push(new JInteger(-1));
    }
}
