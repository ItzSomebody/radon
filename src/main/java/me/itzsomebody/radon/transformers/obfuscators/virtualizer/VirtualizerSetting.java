package me.itzsomebody.radon.transformers.obfuscators.virtualizer;

public enum VirtualizerSetting {
    VM_TYPE(String.class);

    private final Class expectedType;

    VirtualizerSetting(Class expectedType) {
        this.expectedType = expectedType;
    }

    public Class getExpectedType() {
        return expectedType;
    }

    public String getName() {
        return name().toLowerCase();
    }
}
