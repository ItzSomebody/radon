package me.itzsomebody.vm;

public class VMTryCatch {
    private final int startPc;
    private final int endPc;
    private final int handlerPc;
    private final String type;

    public VMTryCatch(int startPc, int endPc, int handlerPc, String type) {
        this.startPc = startPc;
        this.endPc = endPc;
        this.handlerPc = handlerPc;
        this.type = type;
    }

    public int getStartPc() {
        return startPc;
    }

    public int getEndPc() {
        return endPc;
    }

    public int getHandlerPc() {
        return handlerPc;
    }

    public String getType() {
        if (type == null)
            return null;

        return type.replace('/', '.');
    }
}
