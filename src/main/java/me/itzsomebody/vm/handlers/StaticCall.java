/*
 * Radon - An open-source Java obfuscator
 * Copyright (C) 2019 ItzSomebody
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

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

public class StaticCall extends Handler {
    @Override
    public void handle(VM vm, Object[] operands) throws Throwable {
        String ownerName = (String) operands[0];
        String name = (String) operands[1];
        String[] paramsAsStrings = ((String) operands[2]).split("\u0001\u0001");
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

        try {
            if (!"void".equals(returnType)) {
                if ("int".equals(returnType))
                    vm.push(new JInteger((Integer) method.invoke(null, args)));
                else if ("long".equals(returnType)) {
                    vm.push(new JLong((Long) method.invoke(null, args)));
                    vm.push(JTop.getTop());
                } else if ("float".equals(returnType))
                    vm.push(new JFloat((Float) method.invoke(null, args)));
                else if ("double".equals(returnType)) {
                    vm.push(new JDouble((Double) method.invoke(null, args)));
                    vm.push(JTop.getTop());
                } else if ("byte".equals(returnType))
                    vm.push(new JInteger((Byte) method.invoke(null, args)));
                else if ("char".equals(returnType))
                    vm.push(new JInteger((Character) method.invoke(null, args)));
                else if ("short".equals(returnType))
                    vm.push(new JInteger((Short) method.invoke(null, args)));
                else if ("boolean".equals(returnType))
                    vm.push(new JInteger((Boolean) method.invoke(null, args)));
                else
                    vm.push(new JObject(method.invoke(null, args)));
            } else
                method.invoke(null, args);
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
