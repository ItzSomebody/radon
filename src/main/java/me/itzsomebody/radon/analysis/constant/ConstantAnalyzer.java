package me.itzsomebody.radon.analysis.constant;


import me.itzsomebody.radon.analysis.constant.values.AbstractValue;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.analysis.Analyzer;

public class ConstantAnalyzer extends Analyzer<AbstractValue> {
    public ConstantAnalyzer() {
        super(new ConstantInterpreter(Opcodes.ASM7));
    }
}
