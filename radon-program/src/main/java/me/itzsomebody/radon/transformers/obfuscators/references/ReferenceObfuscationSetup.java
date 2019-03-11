package me.itzsomebody.radon.transformers.obfuscators.references;

public class ReferenceObfuscationSetup {
    private boolean hideFieldsWithIndy;
    private boolean hideMethodsWithIndy;
    private boolean hideFieldsWithReflection;
    private boolean hideMethodsWithReflection;
    private boolean ignoreJava8ClassesForReflection;

    public void setHideFieldsWithIndy(boolean hideFieldsWithIndy) {
        this.hideFieldsWithIndy = hideFieldsWithIndy;
    }

    public boolean isHideFieldsWithIndy() {
        return hideFieldsWithIndy;
    }

    public void setHideMethodsWithIndy(boolean hideMethodsWithIndy) {
        this.hideMethodsWithIndy = hideMethodsWithIndy;
    }

    public boolean isHideMethodsWithIndy() {
        return hideMethodsWithIndy;
    }

    public void setHideFieldsWithReflection(boolean hideFieldsWithReflection) {
        this.hideFieldsWithReflection = hideFieldsWithReflection;
    }

    public boolean isHideFieldsWithReflection() {
        return hideFieldsWithReflection;
    }

    public void setHideMethodsWithReflection(boolean hideMethodsWithReflection) {
        this.hideMethodsWithReflection = hideMethodsWithReflection;
    }

    public boolean isHideMethodsWithReflection() {
        return hideMethodsWithReflection;
    }

    public void setIgnoreJava8ClassesForReflection(boolean ignoreJava8ClassesForReflection) {
        this.ignoreJava8ClassesForReflection = ignoreJava8ClassesForReflection;
    }

    public boolean isIgnoreJava8ClassesForReflection() {
        return ignoreJava8ClassesForReflection;
    }
}
