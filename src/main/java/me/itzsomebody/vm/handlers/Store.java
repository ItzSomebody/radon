package me.itzsomebody.vm.handlers;

import me.itzsomebody.vm.VM;
import me.itzsomebody.vm.datatypes.JTop;
import me.itzsomebody.vm.datatypes.JWrapper;

public class Store extends Handler {
    @Override
    public void handle(VM vm, Object[] operands) {
        JWrapper wrapper = vm.pop();
        if (wrapper instanceof JTop)
            wrapper = vm.pop();

        vm.storeRegister(wrapper.copy(), (Integer) operands[0]);
    }
}
