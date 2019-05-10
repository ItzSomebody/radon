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
