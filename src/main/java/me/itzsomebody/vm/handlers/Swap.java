package me.itzsomebody.vm.handlers;

import me.itzsomebody.vm.VM;
import me.itzsomebody.vm.datatypes.JWrapper;

public class Swap extends Handler {
    @Override
    public void handle(VM vm, Object[] operands) {
        JWrapper first = vm.pop();
        JWrapper second = vm.pop();

        vm.push(first);
        vm.push(second);
    }
}
