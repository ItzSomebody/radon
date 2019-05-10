package me.itzsomebody.vm.handlers;

import me.itzsomebody.vm.VM;
import me.itzsomebody.vm.datatypes.JWrapper;

public class Checkcast extends Handler {
    @Override
    public void handle(VM vm, Object[] operands) throws Throwable {
        Class clazz = VM.getClazz((String) operands[0]);
        JWrapper wrapper = vm.pop();
        Object ref = wrapper.asObj();
        stuff:
        {
            if (ref == null) // Null can be casted to anything
                break stuff;

            clazz.cast(ref);
        }
        vm.push(wrapper);
    }
}
