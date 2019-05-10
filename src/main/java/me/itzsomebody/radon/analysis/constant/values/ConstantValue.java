package me.itzsomebody.radon.analysis.constant.values;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;

import java.util.Objects;


public final class ConstantValue extends AbstractValue {
    private final Object value;

    private ConstantValue(AbstractInsnNode insnNode, Type type, Object value) {
        super(insnNode, type);
        this.value = value;
    }

    public static ConstantValue fromInteger(AbstractInsnNode insnNode, int value) {
        return new ConstantValue(insnNode, Type.INT_TYPE, value);
    }

    public static ConstantValue fromLong(AbstractInsnNode insnNode, long value) {
        return new ConstantValue(insnNode, Type.LONG_TYPE, value);
    }

    public static ConstantValue fromFloat(AbstractInsnNode insnNode, float value) {
        return new ConstantValue(insnNode, Type.FLOAT_TYPE, value);
    }

    public static ConstantValue fromDouble(AbstractInsnNode insnNode, double value) {
        return new ConstantValue(insnNode, Type.DOUBLE_TYPE, value);
    }

    public static ConstantValue fromString(AbstractInsnNode insnNode, String value) {
        return new ConstantValue(insnNode, Type.getType(String.class), value);
    }

    public int intValue() {
        return (Integer) this.value;
    }

    public long longValue() {
        return (Long) this.value;
    }

    public float floatValue() {
        return (Float) this.value;
    }

    public double doubleValue() {
        return (Double) this.value;
    }

    public String stringValue() {
        return (String) this.value;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public boolean isConstant() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConstantValue)) return false;
        if (!super.equals(o)) return false;
        ConstantValue that = (ConstantValue) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), value);
    }
}
