package me.itzsomebody.radon.transformers.obfuscators.antidebug;

/**
 * Used to hold the information needed for enabling the {@link Antidebug} transformer.
 *
 * @author vovanre
 */
public class AntidebugSetup {
    private final String message;
    private final boolean blockJavaAgent;

    public AntidebugSetup(String message, boolean blockJavaAgent) {
        this.message = message;
        this.blockJavaAgent = blockJavaAgent;
    }

    public String getMessage() {
        return message;
    }

    public boolean isBlockJavaAgent() {
        return blockJavaAgent;
    }
}
