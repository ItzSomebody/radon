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

package me.itzsomebody.vm.datatypes;

import java.lang.reflect.Array;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import me.itzsomebody.vm.VMException;

public class JObject extends JWrapper {
    private Lock lock = new ReentrantLock();
    private Object value;

    public JObject(Object value) {
        this.value = value;
    }

    @Override
    public Object asObj() {
        return value;
    }

    @Override
    public void lock() {
        lock.lock();
    }

    @Override
    public void unlock() {
        lock.unlock();
    }

    @Override
    public void init(Object value) {
        this.value = value;
    }

    public JWrapper get(int index, boolean primitive) {
        Object val = Array.get(value, index);

        if (primitive)
            if (val.getClass() == Integer.class)
                return new JInteger((Integer) val);
            else if (val.getClass() == Long.class)
                return new JLong((Long) val);
            else if (val.getClass() == Float.class)
                return new JFloat((Float) val);
            else if (val.getClass() == Double.class)
                return new JDouble((Double) val);
            else if (val.getClass() == Byte.class)
                return new JInteger((Byte) val);
            else if (val.getClass() == Short.class)
                return new JInteger((Short) val);
            else if (val.getClass() == Character.class)
                return new JInteger((Character) val);
            else if (val.getClass() == Boolean.class)
                return new JInteger((Boolean) val);
            else
                throw new VMException();
        else
            return new JObject(val);
    }

    public void set(JWrapper wrapper, int index) {
        Array.set(value, index, wrapper.asObj());
    }

    @Override
    public JWrapper copy() {
        return this;
    }
}
