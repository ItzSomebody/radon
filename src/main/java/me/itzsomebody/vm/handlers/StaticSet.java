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

import java.lang.reflect.Field;
import me.itzsomebody.vm.VM;
import me.itzsomebody.vm.VMException;
import me.itzsomebody.vm.datatypes.JTop;
import me.itzsomebody.vm.datatypes.JWrapper;

public class StaticSet extends Handler {
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

        if ("int".equals(ownerName))
            field.setInt(null, value.asInt());
        else if ("long".equals(ownerName))
            field.setLong(null, value.asLong());
        else if ("float".equals(ownerName))
            field.setFloat(null, value.asFloat());
        else if ("double".equals(ownerName))
            field.setDouble(null, value.asDouble());
        else if ("byte".equals(ownerName))
            field.setByte(null, value.asByte());
        else if ("short".equals(ownerName))
            field.setShort(null, value.asShort());
        else if ("char".equals(ownerName))
            field.setChar(null, value.asChar());
        else if ("boolean".equals(ownerName))
            field.setBoolean(null, value.asBool());
        else
            field.set(null, value.asObj());
    }
}
