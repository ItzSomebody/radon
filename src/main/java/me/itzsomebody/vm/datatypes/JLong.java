package me.itzsomebody.vm.datatypes;

public class JLong extends JWrapper {
    private final long value;

    public JLong(long value) {
        this.value = value;
    }

    @Override
    public long asLong() {
        return value;
    }

    @Override
    public Object asObj() {
        return value;
    }

    @Override
    public JWrapper copy() {
        return new JLong(value);
    }
}
