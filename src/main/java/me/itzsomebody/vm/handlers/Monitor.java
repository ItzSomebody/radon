package me.itzsomebody.vm.handlers;

import me.itzsomebody.vm.VM;
import me.itzsomebody.vm.datatypes.JWrapper;

public class Monitor extends Handler {
    @Override
    public void handle(VM vm, Object[] operands) {
        boolean lock = (Integer) operands[0] == 0;
        JWrapper wrapper = vm.pop();

        if (lock)
            wrapper.lock();
        else
            wrapper.unlock();
    }
}
