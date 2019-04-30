package me.itzsomebody.vm.handlers;

import me.itzsomebody.vm.VM;

public class Checkcast extends Handler {
    @Override
    public void handle(VM vm, Object[] operands) throws Throwable {
        Class clazz = VM.getClazz((String) operands[0]);

        Object ref = vm.pop().asObj();
        if (ref == null) // Null can be casted to anything
            return;

        clazz.cast(ref);
    }
}
