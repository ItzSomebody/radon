package me.itzsomebody.radon.transformers.obfuscators;

import me.itzsomebody.radon.exclusions.ExclusionType;
import me.itzsomebody.radon.transformers.Transformer;

/**
 * This applies some type of integrity-aware code. Currently, there are two
 * types of anti-tampers: passive and active. The active anti-tamper will
 * actively search for modifications to the JAR and crash the JVM. The
 * passive anti-tamper will modify its environment based on random
 * components of the program.
 *
 * @author ItzSomebody
 */
public class AntiTamper extends Transformer {
    private static final int PASSIVE = 1;
    private static final int ACTIVE = 2;
    private int type;

    @Override
    public void transform() {
        // TODO
    }

    @Override
    public String getName() {
        return "Anti-Tamper";
    }

    @Override
    protected ExclusionType getExclusionType() {
        return ExclusionType.ANTI_TAMPER;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setType(String mode) {
        if (mode.equalsIgnoreCase("passive"))
            setType(PASSIVE);
        else if (mode.equalsIgnoreCase("active"))
            setType(ACTIVE);
    }
}
