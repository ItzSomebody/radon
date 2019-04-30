package me.itzsomebody.vm.handlers;

import java.lang.reflect.Field;
import me.itzsomebody.vm.VM;
import me.itzsomebody.vm.VMException;
import me.itzsomebody.vm.datatypes.JDouble;
import me.itzsomebody.vm.datatypes.JFloat;
import me.itzsomebody.vm.datatypes.JInteger;
import me.itzsomebody.vm.datatypes.JLong;
import me.itzsomebody.vm.datatypes.JObject;
import me.itzsomebody.vm.datatypes.JTop;

public class VirtGet extends Handler {
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

        Object ref = vm.pop().asObj();

        if ("int".equals(type.getName()))
            vm.push(new JInteger(field.getInt(ref)));
        else if ("long".equals(type.getName())) {
            vm.push(new JLong(field.getLong(ref)));
            vm.push(JTop.getTop());
        }
        else if ("float".equals(type.getName()))
            vm.push(new JFloat(field.getFloat(ref)));
        else if ("double".equals(type.getName())) {
            vm.push(new JDouble(field.getDouble(ref)));
            vm.push(JTop.getTop());
        }
        else if ("byte".equals(type.getName()))
            vm.push(new JInteger(field.getByte(ref)));
        else if ("short".equals(type.getName()))
            vm.push(new JInteger(field.getShort(ref)));
        else if ("char".equals(type.getName()))
            vm.push(new JInteger(field.getChar(ref)));
        else if ("boolean".equals(type.getName()))
            vm.push(new JInteger(field.getBoolean(ref)));
        else
            vm.push(new JObject(field.get(ref)));
    }
}
