package me.itzsomebody.vm.datatypes;

public class JDouble extends JWrapper {
    private final double value;

    public JDouble(double value) {
        this.value = value;
    }

    @Override
    public double asDouble() {
        return value;
    }

    @Override
    public Object asObj() {
        return value;
    }

    @Override
    public JWrapper copy() {
        return new JDouble(value);
    }
}