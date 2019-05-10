package me.itzsomebody.vm.handlers;

import java.lang.reflect.Array;
import me.itzsomebody.vm.VM;
import me.itzsomebody.vm.datatypes.JInteger;

public class ArrLength extends Handler {
    @Override
    public void handle(VM vm, Object[] operands) {
        vm.push(new JInteger(Array.getLength(vm.pop().asObj())));
    }
}
