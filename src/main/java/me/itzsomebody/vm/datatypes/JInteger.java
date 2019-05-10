package me.itzsomebody.vm.datatypes;

public class JInteger extends JWrapper {
    private final int value;

    public JInteger(boolean value) {
        this.value = (value) ? 1 : 0;
    }

    public JInteger(int value) {
        this.value = value;
    }

    @Override
    public int asInt() {
        return value;
    }

    @Override
    public byte asByte() {
        return (byte) value;
    }

    @Override
    public char asChar() {
        return (char) value;
    }

    @Override
    public short asShort() {
        return (short) value;
    }

    @Override
    public boolean asBool() {
        return value != 0;
    }

    @Override
    public Object asObj() {
        return value;
    }

    @Override
    public JWrapper copy() {
        return new JInteger(value);
    }
}
