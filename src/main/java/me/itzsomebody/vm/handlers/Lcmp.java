package me.itzsomebody.vm.handlers;

import me.itzsomebody.vm.VM;
import me.itzsomebody.vm.datatypes.JInteger;

public class Lcmp extends Handler {
    @Override
    public void handle(VM vm, Object[] operands) {
        vm.pop();
        long second = vm.pop().asLong();

        vm.pop();
        long first = vm.pop().asLong();

        long result = first - second;

        if (result == 0)
            vm.push(new JInteger(0));
        else if (result > 0)
            vm.push(new JInteger(1));
        else
            vm.push(new JInteger(-1));
    }
}
