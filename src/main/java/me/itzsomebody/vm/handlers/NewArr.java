package me.itzsomebody.vm.handlers;

import java.lang.reflect.Array;
import me.itzsomebody.vm.VM;
import me.itzsomebody.vm.datatypes.JObject;

public class NewArr extends Handler {
    @Override
    public void handle(VM vm, Object[] operands) throws Exception {
        int size = vm.pop().asInt();
        Class clazz = VM.getClazz((String) operands[0]);
        vm.push(new JObject(Array.newInstance(clazz, size)));
    }
}
