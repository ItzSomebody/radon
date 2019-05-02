package me.itzsomebody.vm.handlers;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import me.itzsomebody.vm.VM;
import me.itzsomebody.vm.VMException;
import me.itzsomebody.vm.datatypes.JTop;
import me.itzsomebody.vm.datatypes.JWrapper;

public class Instantiate extends Handler {
    @Override
    public void handle(VM vm, Object[] operands) throws Throwable {
        String ownerName = (String) operands[0];
        String[] paramsAsStrings = ((String) operands[1]).split("\u0001\u0001");
        Class[] params;
        if (paramsAsStrings[0].equals("\u0000\u0000\u0000"))
            params = new Class[0];
        else
            params = stringsToParams(paramsAsStrings);
        Object[] args = new Object[params.length];

        Class clazz = VM.getClazz(ownerName);
        Constructor constructor = VM.getConstructor(clazz, params);

        if (constructor == null)
            throw new VMException();

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

        JWrapper ref = vm.pop();
        try {
            ref.init(constructor.newInstance(args));
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
