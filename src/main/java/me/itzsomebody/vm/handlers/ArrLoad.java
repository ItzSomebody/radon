package me.itzsomebody.vm.handlers;

import me.itzsomebody.vm.VM;
import me.itzsomebody.vm.datatypes.JDouble;
import me.itzsomebody.vm.datatypes.JLong;
import me.itzsomebody.vm.datatypes.JTop;
import me.itzsomebody.vm.datatypes.JWrapper;

public class ArrLoad extends Handler {
    @Override
    public void handle(VM vm, Object[] operands) {
        int index = vm.pop().asInt();
        JWrapper ref = vm.pop();
        JWrapper value = ref.get(index, (Integer) operands[0] == 0);

        vm.push(value.copy());

        if (value instanceof JLong || value instanceof JDouble)
            vm.push(JTop.getTop());
    }
}
