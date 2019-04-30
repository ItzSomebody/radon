package me.itzsomebody.vm.handlers;

import me.itzsomebody.vm.VM;
import me.itzsomebody.vm.datatypes.JInteger;

public class Fcmpg extends Handler {
    @Override
    public void handle(VM vm, Object[] operands) {
        float first, second, result;

        second = vm.pop().asFloat();
        first = vm.pop().asFloat();

        if (Float.isNaN(first) || Float.isNaN(second)) {
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
