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

        if (type.getName().equals("int"))
            vm.push(new JInteger(field.getInt(null)));
        else if (type.getName().equals("long")) {
            vm.push(new JLong(field.getLong(null)));
            vm.push(JTop.getTop());
        }
        else if (type.getName().equals("float"))
            vm.push(new JFloat(field.getFloat(null)));
        else if (type.getName().equals("double")) {
            vm.push(new JDouble(field.getDouble(null)));
            vm.push(JTop.getTop());
        }
        else if (type.getName().equals("byte"))
            vm.push(new JInteger(field.getByte(null)));
        else if (type.getName().equals("short"))
            vm.push(new JInteger(field.getShort(null)));
        else if (type.getName().equals("char"))
            vm.push(new JInteger(field.getChar(null)));
        else if (type.getName().equals("boolean"))
            vm.push(new JInteger(field.getBoolean(null)));
        else
            vm.push(new JObject(field.get(null)));
    }
}
