package me.itzsomebody.vm.datatypes;

public class JFloat extends JWrapper {
    private final float value;

    public JFloat(float value) {
        this.value = value;
    }

    @Override
    public float asFloat() {
        return value;
    }

    @Override
    public Object asObj() {
        return value;
    }

    @Override
    public JWrapper copy() {
        return new JFloat(value);
    }
}
