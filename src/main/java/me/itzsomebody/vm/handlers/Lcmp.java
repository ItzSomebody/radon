package me.itzsomebody.vm.handlers;

import me.itzsomebody.vm.VM;
import me.itzsomebody.vm.datatypes.JInteger;

public class Lcmp extends Handler {
    @Override
    public void handle(VM vm, Object[] operands) {
        long first, second, result;

        vm.pop();
        second = vm.pop().asLong();

        vm.pop();
        first = vm.pop().asLong();

        result = first - second;

        if (result == 0)
            vm.push(new JInteger(0));
        else if (result > 0)
            vm.push(new JInteger(1));
        else
            vm.push(new JInteger(-1));
    }
}
