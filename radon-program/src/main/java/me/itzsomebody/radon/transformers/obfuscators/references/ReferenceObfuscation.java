package me.itzsomebody.radon.transformers.obfuscators.references;

import me.itzsomebody.radon.exclusions.ExclusionType;
import me.itzsomebody.radon.transformers.Transformer;

public class ReferenceObfuscation extends Transformer {
    private boolean hideMethodsWithIndyEnabled;
    private boolean hideFieldsWithIndyEnabled;

    private boolean hideMethodsWithReflectionEnabled;
    private boolean hideFieldsWithReflectionEnabled;

    private boolean ignoreJava8ClassesForReflectionEnabled;

    @Override
    public void transform() {
        // TODO
    }

    @Override
    public String getName() {
        return "Reference obfuscation";
    }

    @Override
    protected ExclusionType getExclusionType() {
        return ExclusionType.REFERENCE_OBFUSCATION;
    }

    public boolean isHideMethodsWithIndyEnabled() {
        return hideMethodsWithIndyEnabled;
    }

    public void setHideMethodsWithIndyEnabled(boolean hideMethodsWithIndyEnabled) {
        this.hideMethodsWithIndyEnabled = hideMethodsWithIndyEnabled;
    }

    public boolean isHideFieldsWithIndyEnabled() {
        return hideFieldsWithIndyEnabled;
    }

    public void setHideFieldsWithIndyEnabled(boolean hideFieldsWithIndyEnabled) {
        this.hideFieldsWithIndyEnabled = hideFieldsWithIndyEnabled;
    }

    public boolean isHideMethodsWithReflectionEnabled() {
        return hideMethodsWithReflectionEnabled;
    }

    public void setHideMethodsWithReflectionEnabled(boolean hideMethodsWithReflectionEnabled) {
        this.hideMethodsWithReflectionEnabled = hideMethodsWithReflectionEnabled;
    }

    public boolean isHideFieldsWithReflectionEnabled() {
        return hideFieldsWithReflectionEnabled;
    }

    public void setHideFieldsWithReflectionEnabled(boolean hideFieldsWithReflectionEnabled) {
        this.hideFieldsWithReflectionEnabled = hideFieldsWithReflectionEnabled;
    }

    public boolean isIgnoreJava8ClassesForReflectionEnabled() {
        return ignoreJava8ClassesForReflectionEnabled;
    }

    public void setIgnoreJava8ClassesForReflectionEnabled(boolean ignoreJava8ClassesForReflectionEnabled) {
        this.ignoreJava8ClassesForReflectionEnabled = ignoreJava8ClassesForReflectionEnabled;
    }
}
