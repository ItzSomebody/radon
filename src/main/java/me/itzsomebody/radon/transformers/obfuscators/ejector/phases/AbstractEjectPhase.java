package me.itzsomebody.radon.transformers.obfuscators.ejector.phases;

import me.itzsomebody.radon.transformers.obfuscators.ejector.EjectorContext;

public abstract class AbstractEjectPhase {
    protected final EjectorContext ejectorContext;

    public AbstractEjectPhase(EjectorContext ejectorContext) {
        this.ejectorContext = ejectorContext;
    }

    public abstract void process();
}
