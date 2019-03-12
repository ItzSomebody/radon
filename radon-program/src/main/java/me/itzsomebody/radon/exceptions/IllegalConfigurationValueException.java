package me.itzsomebody.radon.exceptions;

public class IllegalConfigurationValueException extends RuntimeException {
    public IllegalConfigurationValueException(String msg) {
        super(msg);
    }

    public IllegalConfigurationValueException(String value, Class expectedType, Class gotInstead) {
        super(String.format("Value %s was expected to be %s, got %s instead.", value, expectedType,
                gotInstead.getName()));
    }
}
