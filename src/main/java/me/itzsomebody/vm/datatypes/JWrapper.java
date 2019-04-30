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
