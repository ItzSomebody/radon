package me.itzsomebody.vm.handlers;

import me.itzsomebody.vm.VM;
import me.itzsomebody.vm.datatypes.JDouble;
import me.itzsomebody.vm.datatypes.JLong;
import me.itzsomebody.vm.datatypes.JTop;
import me.itzsomebody.vm.datatypes.JWrapper;

public class Load extends Handler {
    @Override
    public void handle(VM vm, Object[] operands) {
        JWrapper wrapper = vm.loadRegister((Integer) operands[0]);

        vm.push(wrapper.copy());

        if (wrapper instanceof JLong || wrapper instanceof JDouble)
            vm.push(JTop.getTop());
    }
}
