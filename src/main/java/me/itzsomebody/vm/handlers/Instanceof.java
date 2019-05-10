package me.itzsomebody.vm.handlers;

import me.itzsomebody.vm.VM;
import me.itzsomebody.vm.datatypes.JInteger;

public class Instanceof extends Handler {
    @Override
    public void handle(VM vm, Object[] operands) throws Throwable {
        Class clazz = VM.getClazz((String) operands[0]);

        Object ref = vm.pop().asObj();
        if (ref == null) // Null can be casted to anything
            return;

        vm.push(new JInteger(clazz.isInstance(ref)));
    }
}
