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

import me.itzsomebody.vm.VMException;

public class JWrapper {
    public int asInt() {
        throw new VMException(getClass().getName());
    }

    public long asLong() {
        throw new VMException(getClass().getName());
    }

    public float asFloat() {
        throw new VMException(getClass().getName());
    }

    public double asDouble() {
        throw new VMException(getClass().getName());
    }

    public byte asByte() {
        throw new VMException(getClass().getName());
    }

    public char asChar() {
        throw new VMException(getClass().getName());
    }

    public short asShort() {
        throw new VMException(getClass().getName());
    }

    public boolean asBool() {
        throw new VMException(getClass().getName());
    }

    public Object asObj() {
        throw new VMException(getClass().getName());
    }

    public void lock() {
        throw new VMException(getClass().getName());
    }

    public void unlock() {
        throw new VMException(getClass().getName());
    }

    public void init(Object value) {
        throw new VMException(getClass().getName());
    }

    public JWrapper get(int index, boolean primitive) {
        throw new VMException(getClass().getName());
    }

    public void set(JWrapper value, int index) {
        throw new VMException(getClass().getName());
    }

    public JWrapper copy() {
        throw new VMException(getClass().getName());
    }

    public JWrapper fromPrimitive(Object o) {
        if (o instanceof Integer)
            return new JInteger((Integer) o);
        if (o instanceof Long)
            return new JLong((Long) o);
        if (o instanceof Float)
            return new JFloat((Float) o);
        if (o instanceof Double)
            return new JDouble((Double) o);

        throw new VMException();
    }
}
