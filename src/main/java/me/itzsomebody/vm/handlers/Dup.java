package me.itzsomebody.vm.handlers;

import me.itzsomebody.vm.VM;
import me.itzsomebody.vm.datatypes.JWrapper;

public class Dup extends Handler {
    @Override
    public void handle(VM vm, Object[] operands) {
        switch ((Integer) operands[0]) {
            case 0: { // DUP
                JWrapper value = vm.pop();
                vm.push(value);
                vm.push(value);
                break;
            }
            case 1: { // DUP_X1
                JWrapper first = vm.pop();
                JWrapper second = vm.pop();
                vm.push(first);
                vm.push(second);
                vm.push(first);
                break;
            }
            case 2: { // DUP_X2
                JWrapper first = vm.pop();
                JWrapper second = vm.pop();
                JWrapper third = vm.pop();
                vm.push(first);
                vm.push(second);
                vm.push(third);
                vm.push(first);
                break;
            }
            case 3: { // DUP2
                JWrapper first = vm.pop();
                JWrapper second = vm.pop();
                vm.push(first);
                vm.push(second);
                vm.push(first);
                vm.push(second);
                break;
            }
            case 4: { // DUP2_X1
                JWrapper first = vm.pop();
                JWrapper second = vm.pop();
                JWrapper third = vm.pop();
                vm.push(second);
                vm.push(first);
                vm.push(third);
                vm.push(second);
                vm.push(first);
                break;
            }
            case 5: { // DUP2_X2
                JWrapper first = vm.pop();
                JWrapper second = vm.pop();
                JWrapper third = vm.pop();
                JWrapper fourth = vm.pop();
                vm.push(second);
                vm.push(first);
                vm.push(fourth);
                vm.push(third);
                vm.push(second);
                vm.push(first);
                break;
            }
        }
    }
}
