package me.itzsomebody.vm.handlers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import me.itzsomebody.vm.VM;
import me.itzsomebody.vm.VMException;
import me.itzsomebody.vm.datatypes.JDouble;
import me.itzsomebody.vm.datatypes.JFloat;
import me.itzsomebody.vm.datatypes.JInteger;
import me.itzsomebody.vm.datatypes.JLong;
import me.itzsomebody.vm.datatypes.JObject;
import me.itzsomebody.vm.datatypes.JTop;
import me.itzsomebody.vm.datatypes.JWrapper;

public class VirtCall extends Handler {
    @Override
    public void handle(VM vm, Object[] operands) throws Throwable {
        String ownerName = (String) operands[0];
        String name = (String) operands[1];
        String[] paramsAsStrings = ((String) operands[2]).split(";");
        Class[] params;
        if (paramsAsStrings[0].equals("\u0000\u0000\u0000"))
            params = new Class[0];
        else
            params = stringsToParams(paramsAsStrings);
        Object[] args = new Object[params.length];

        Class clazz = VM.getClazz(ownerName);
        Method method = VM.getMethod(clazz, name, params);

        if (method == null)
            throw new VMException();

        String returnType = method.getReturnType().getName();

        for (int i = params.length - 1; i >= 0; i--) {
            Class param = params[i];
            JWrapper arg = vm.pop();

            if (arg instanceof JTop)
                arg = vm.pop();

            if (param == boolean.class)
                args[i] = arg.asBool();
            else if (param == char.class)
                args[i] = arg.asChar();
            else if (param == short.class)
                args[i] = arg.asShort();
            else if (param == byte.class)
                args[i] = arg.asByte();
            else
                args[i] = arg.asObj();
        }

        Object ref = vm.pop().asObj();

        try {
            if (!returnType.equals("void")) {
                if (returnType.equals("int"))
                    vm.push(new JInteger((Integer) method.invoke(ref, args)));
                else if (returnType.equals("long")) {
                    vm.push(new JLong((Long) method.invoke(ref, args)));
                    vm.push(JTop.getTop());
                } else if (returnType.equals("float"))
                    vm.push(new JFloat((Float) method.invoke(ref, args)));
                else if (returnType.equals("double")) {
                    vm.push(new JDouble((Double) method.invoke(ref, args)));
                    vm.push(JTop.getTop());
                } else if (returnType.equals("byte"))
                    vm.push(new JInteger((Byte) method.invoke(ref, args)));
                else if (returnType.equals("char"))
                    vm.push(new JInteger((Character) method.invoke(ref, args)));
                else if (returnType.equals("short"))
                    vm.push(new JInteger((Short) method.invoke(ref, args)));
                else if (returnType.equals("boolean"))
                    vm.push(new JInteger((Boolean) method.invoke(ref, args)));
                else
                    vm.push(new JObject(method.invoke(ref, args)));
            } else
                method.invoke(ref, args);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }

    private static Class[] stringsToParams(String[] s) throws ClassNotFoundException {
        Class[] classes = new Class[s.length];
        for (int i = 0; i < s.length; i++)
            classes[i] = VM.getClazz(s[i]);

        return classes;
    }
}
