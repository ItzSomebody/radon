package me.itzsomebody.radon.transformers.obfuscators.ejector.phases;

import me.itzsomebody.radon.transformers.obfuscators.ejector.EjectorContext;

public interface IEjectPhase {
    void process(EjectorContext ejectorContext);
}
