package me.itzsomebody.radon.transformers.flow;

import me.itzsomebody.radon.transformers.AbstractTransformer;
import me.itzsomebody.radon.utils.BytecodeUtils;
import me.itzsomebody.radon.utils.LoggerUtils;
import me.itzsomebody.radon.utils.NumberUtils;
import me.itzsomebody.radon.utils.StringUtils;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * This transformer does... idk.
 * TODO: DO THIS
 *
 * @author ItzSomebody
 */
public class HeavyFlowObfuscation extends AbstractTransformer {
    /**
     * Applies obfuscation.
     */
    public void obfuscate() {
        AtomicInteger counter = new AtomicInteger();
        long current = System.currentTimeMillis();
        this.logStrings.add(LoggerUtils.stdOut("------------------------------------------------"));
        this.logStrings.add(LoggerUtils.stdOut("Started heavy flow obfuscation transformer"));
        this.classNodes().stream().filter(classNode -> !this.exempted(classNode.name, "Flow")).forEach(classNode -> {
            // TODO: DO THIS
        });
        this.logStrings.add(LoggerUtils.stdOut("Added " + counter + " instruction sets."));
        this.logStrings.add(LoggerUtils.stdOut("Finished. [" + tookThisLong(current) + "ms]"));
    }
}
