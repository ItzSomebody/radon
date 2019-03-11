package me.itzsomebody.radon.transformers.shrinkers;

import java.util.concurrent.atomic.AtomicInteger;
import me.itzsomebody.radon.utils.AccessUtils;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

public class SyntheticAccessRemover extends Shrinker {
    @Override
    public void transform() {
        AtomicInteger counter = new AtomicInteger();

        getClassWrappers().stream().filter(classWrapper -> !excluded(classWrapper)).forEach(classWrapper -> {
            ClassNode classNode = classWrapper.classNode;

            if (AccessUtils.isSynthetic(classNode.access)) {
                classNode.access &= ~ACC_SYNTHETIC;
                counter.incrementAndGet();
            }

            classWrapper.methods.stream().filter(methodWrapper -> !excluded(methodWrapper)).forEach(methodWrapper -> {
                MethodNode methodNode = methodWrapper.methodNode;

                if (AccessUtils.isSynthetic(methodNode.access) || AccessUtils.isBridge(methodNode.access)) {
                    methodNode.access &= ~(ACC_SYNTHETIC | ACC_BRIDGE);
                    counter.incrementAndGet();
                }
            });

            classWrapper.fields.stream().filter(fieldWrapper -> !excluded(fieldWrapper)).forEach(fieldWrapper -> {
                FieldNode fieldNode = fieldWrapper.fieldNode;

                if (AccessUtils.isSynthetic(fieldNode.access)) {
                    fieldNode.access &= ~ACC_SYNTHETIC;
                    counter.incrementAndGet();
                }
            });
        });
    }

    @Override
    public String getName() {
        return "Synthetic Access Remover";
    }
}
