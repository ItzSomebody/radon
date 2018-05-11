package me.itzsomebody.radon.transformers.linenumbers;

import java.util.concurrent.atomic.AtomicInteger;
import me.itzsomebody.radon.transformers.AbstractTransformer;
import me.itzsomebody.radon.utils.BytecodeUtils;
import me.itzsomebody.radon.utils.LoggerUtils;
import me.itzsomebody.radon.utils.NumberUtils;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LineNumberNode;

/**
 * Transformer that applies a line number obfuscation by changing the
 * corresponding numbers linked to labels
 * to random numbers.
 *
 * @author ItzSomebody
 */
public class ObfuscateLineNumbers extends AbstractTransformer {
    /**
     * Applies obfuscation.
     */
    public void obfuscate() {
        AtomicInteger counter = new AtomicInteger();
        long current = System.currentTimeMillis();
        this.logStrings.add(LoggerUtils.stdOut("------------------------------------------------"));
        this.logStrings.add(LoggerUtils.stdOut("Started line obfuscation transformer"));
        this.classNodes().stream().filter(classNode -> !this.exempted(classNode.name, "LineNumbers")).forEach(classNode -> {
            classNode.methods.stream().filter(methodNode ->
                    !this.exempted(classNode.name + '.' + methodNode.name + methodNode.desc, "LineNumbers"))
                    .filter(methodNode -> !BytecodeUtils.isAbstractMethod(methodNode.access)).forEach(methodNode -> {
                for (AbstractInsnNode insn : methodNode.instructions.toArray()) {
                    if (insn instanceof LineNumberNode) {
                        LineNumberNode lineNumberNode = (LineNumberNode) insn;
                        methodNode.instructions.set(insn,
                                new LineNumberNode(NumberUtils.getRandomInt(Integer.MAX_VALUE), lineNumberNode.start));
                        counter.incrementAndGet();
                    }
                }
            });
        });
        this.logStrings.add(LoggerUtils.stdOut("Obfuscated " + counter + " line numbers."));
        this.logStrings.add(LoggerUtils.stdOut("Finished. [" + tookThisLong(current) + "ms]"));
    }
}
