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

        if (ownerName.equals("int"))
            field.setInt(ref, value.asInt());
        else if (ownerName.equals("long"))
            field.setLong(ref, value.asLong());
        else if (ownerName.equals("float"))
            field.setFloat(ref, value.asFloat());
        else if (ownerName.equals("double"))
            field.setDouble(ref, value.asDouble());
        else if (ownerName.equals("byte"))
            field.setByte(ref, value.asByte());
        else if (ownerName.equals("short"))
            field.setShort(ref, value.asShort());
        else if (ownerName.equals("char"))
            field.setChar(ref, value.asChar());
        else if (ownerName.equals("boolean"))
            field.setBoolean(ref, value.asBool());
        else
            field.set(ref, value.asObj());
    }
}
