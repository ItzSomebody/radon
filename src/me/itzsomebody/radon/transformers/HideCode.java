package me.itzsomebody.radon.transformers;

import me.itzsomebody.radon.utils.BytecodeUtils;
import me.itzsomebody.radon.utils.LoggerUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Transformer that applies a code hiding technique by applying synthetic modifiers to the class, fields, and methods.
 * Known to have problems with Spigot plugins with EventHandlers.
 *
 * @author ItzSomebody
 */
public class HideCode extends AbstractTransformer {
    /**
     * TODO: Indication to check for EventHandlers before attempting to add synthetic modifier.
     */
    private boolean spigotMode;

    /**
     * Constructor used to create a {@link HideCode} object.
     *
     * @param spigotMode TODO: indication to check for EventHandlers.
     */
    public HideCode(boolean spigotMode) {
        this.spigotMode = spigotMode;
    }

    /**
     * Applies obfuscation.
     */
    public void obfuscate() {
        logStrings.add(LoggerUtils.stdOut("------------------------------------------------"));
        logStrings.add(LoggerUtils.stdOut("Starting hide code transformer"));
        AtomicInteger counter = new AtomicInteger();
        long current = System.currentTimeMillis();
        classNodes().stream().filter(classNode -> !classExempted(classNode.name)).forEach(classNode -> {
            if (!BytecodeUtils.isSyntheticMethod(classNode.access)) {
                classNode.access |= ACC_SYNTHETIC;
                counter.incrementAndGet();
            }

            classNode.methods.stream().filter(methodNode -> !methodExempted(classNode.name + '.' + methodNode.name + methodNode.desc))
                    .forEach(methodNode -> {
                boolean hidOnce = false;
                if (!BytecodeUtils.isSyntheticMethod(methodNode.access)) {
                    methodNode.access |= ACC_SYNTHETIC;
                    hidOnce = true;
                }

                if (!BytecodeUtils.isBridgeMethod(methodNode.access)
                        && !methodNode.name.startsWith("<")) {
                    methodNode.access |= ACC_BRIDGE;
                    hidOnce = true;
                }

                if (hidOnce) counter.incrementAndGet();
            });

            if (classNode.fields != null)
                classNode.fields.stream().filter(fieldNode -> !fieldExempted(classNode.name + '.' + fieldNode.name)).forEach(fieldNode -> {
                    if (!BytecodeUtils.isSyntheticMethod(fieldNode.access)) {
                        fieldNode.access |= ACC_SYNTHETIC;
                        counter.incrementAndGet();
                    }
                });
        });
        logStrings.add(LoggerUtils.stdOut("Hid " + counter + " members."));
        logStrings.add(LoggerUtils.stdOut("Finished. [" + tookThisLong(current) + "ms]"));
    }
}
