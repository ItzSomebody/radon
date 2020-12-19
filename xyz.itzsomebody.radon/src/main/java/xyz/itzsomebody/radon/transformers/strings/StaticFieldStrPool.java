package xyz.itzsomebody.radon.transformers.strings;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;
import xyz.itzsomebody.radon.transformers.Transformers;
import xyz.itzsomebody.radon.utils.logging.RadonLogger;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Takes all the strings in a class and stuffs them into a method which initializes a static field when
 * <clinit> is invoked.
 *
 * @author itzsomebody
 */
public class StaticFieldStrPool extends StringTransformer {
    @Override
    public void transform() {
        AtomicInteger count = new AtomicInteger();

        classStream().filter(this::notExcluded).forEach(classWrapper -> {
            classWrapper.methodStream().filter(mw -> notExcluded(mw) && mw.hasInstructions()).forEach(methodWrapper -> {
                int leeway = methodWrapper.getLeewaySize();
                MethodNode methodNode = methodWrapper.getMethodNode();

                for (AbstractInsnNode current : methodNode.instructions.toArray()) {
                    if (leeway <= 10) {
                        return;
                    }

                    if (current instanceof LdcInsnNode && ((LdcInsnNode) current).cst instanceof String) {
                        // todo
                    }
                }
            });
        });

        RadonLogger.info("Pooled " + count.get() + " strings");
    }

    @Override
    public String getConfigName() {
        return Transformers.POOL_STRINGS_TO_STATIC_FIELD.getConfigName();
    }
}
