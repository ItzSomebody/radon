package me.itzsomebody.radon.exceptions;

public class RadonException extends RuntimeException {
    public RadonException() {
        super();
    }

    public RadonException(String msg) {
        super(msg);
    }

    public RadonException(Throwable t) {
        super(t);
    }
}
