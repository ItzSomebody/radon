package me.itzsomebody.radon.transformers.obfuscators.numbers;

public class NumberObfuscationSetup {
    private boolean tamperWithIntegers;
    private boolean tamperWithLongs;
    private boolean tamperWithFloats;
    private boolean tamperWithDoubles;
    private boolean splitIntoBitwise;
    private boolean splitIntoArithmetic;
    private boolean encryptUsingContext;

    public boolean isTamperWithIntegers() {
        return tamperWithIntegers;
    }

    public void setTamperWithIntegers(boolean tamperWithIntegers) {
        this.tamperWithIntegers = tamperWithIntegers;
    }

    public boolean isTamperWithLongs() {
        return tamperWithLongs;
    }

    public void setTamperWithLongs(boolean tamperWithLongs) {
        this.tamperWithLongs = tamperWithLongs;
    }

    public boolean isTamperWithFloats() {
        return tamperWithFloats;
    }

    public void setTamperWithFloats(boolean tamperWithFloats) {
        this.tamperWithFloats = tamperWithFloats;
    }

    public boolean isTamperWithDoubles() {
        return tamperWithDoubles;
    }

    public void setTamperWithDoubles(boolean tamperWithDoubles) {
        this.tamperWithDoubles = tamperWithDoubles;
    }

    public boolean isSplitIntoBitwise() {
        return splitIntoBitwise;
    }

    public void setSplitIntoBitwise(boolean splitIntoBitwise) {
        this.splitIntoBitwise = splitIntoBitwise;
    }

    public boolean isSplitIntoArithmetic() {
        return splitIntoArithmetic;
    }

    public void setSplitIntoArithmetic(boolean splitIntoArithmetic) {
        this.splitIntoArithmetic = splitIntoArithmetic;
    }

    public boolean isEncryptUsingContext() {
        return encryptUsingContext;
    }

    public void setEncryptUsingContext(boolean encryptUsingContext) {
        this.encryptUsingContext = encryptUsingContext;
    }
}
