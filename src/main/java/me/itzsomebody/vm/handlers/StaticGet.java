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

public class StaticGet extends Handler {
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

        if ("int".equals(type.getName()))
            vm.push(new JInteger(field.getInt(null)));
        else if ("long".equals(type.getName())) {
            vm.push(new JLong(field.getLong(null)));
            vm.push(JTop.getTop());
        } else if ("float".equals(type.getName()))
            vm.push(new JFloat(field.getFloat(null)));
        else if ("double".equals(type.getName())) {
            vm.push(new JDouble(field.getDouble(null)));
            vm.push(JTop.getTop());
        } else if ("byte".equals(type.getName()))
            vm.push(new JInteger(field.getByte(null)));
        else if ("short".equals(type.getName()))
            vm.push(new JInteger(field.getShort(null)));
        else if ("char".equals(type.getName()))
            vm.push(new JInteger(field.getChar(null)));
        else if ("boolean".equals(type.getName()))
            vm.push(new JInteger(field.getBoolean(null)));
        else
            vm.push(new JObject(field.get(null)));
    }
}
