package me.itzsomebody.vm.handlers;

import me.itzsomebody.vm.VM;
import me.itzsomebody.vm.datatypes.JTop;
import me.itzsomebody.vm.datatypes.JWrapper;

public class ArrStore extends Handler {
    @Override
    public void handle(VM vm, Object[] operands) {
        JWrapper value = vm.pop();

        if (value instanceof JTop)
            value = vm.pop();

        int index = vm.pop().asInt();
        JWrapper ref = vm.pop();
        ref.set(value.copy(), index);
    }
}
