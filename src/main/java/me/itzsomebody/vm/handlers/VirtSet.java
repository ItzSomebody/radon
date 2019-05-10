package me.itzsomebody.vm.handlers;

import java.lang.reflect.Field;
import me.itzsomebody.vm.VM;
import me.itzsomebody.vm.VMException;
import me.itzsomebody.vm.datatypes.JTop;
import me.itzsomebody.vm.datatypes.JWrapper;

public class VirtSet extends Handler {
    @Override
    public void handle(VM vm, Object[] operands) throws Exception {
        String ownerName = (String) operands[0];
        String name = (String) operands[1];
        String typeName = (String) operands[2];

        Class clazz = VM.getClazz(ownerName);
        Class type = VM.getClazz(typeName);
        Field field = VM.getField(clazz, name, type);

        if (field == null)
            throw new VMException();

        JWrapper value = vm.pop();

        if (value instanceof JTop)
            value = vm.pop();

        Object ref = vm.pop().asObj();

        if ("int".equals(ownerName))
            field.setInt(ref, value.asInt());
        else if ("long".equals(ownerName))
            field.setLong(ref, value.asLong());
        else if ("float".equals(ownerName))
            field.setFloat(ref, value.asFloat());
        else if ("double".equals(ownerName))
            field.setDouble(ref, value.asDouble());
        else if ("byte".equals(ownerName))
            field.setByte(ref, value.asByte());
        else if ("short".equals(ownerName))
            field.setShort(ref, value.asShort());
        else if ("char".equals(ownerName))
            field.setChar(ref, value.asChar());
        else if ("boolean".equals(ownerName))
            field.setBoolean(ref, value.asBool());
        else
            field.set(ref, value.asObj());
    }
}
