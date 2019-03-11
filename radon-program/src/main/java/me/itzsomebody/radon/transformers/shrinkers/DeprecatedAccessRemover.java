package me.itzsomebody.radon.transformers.shrinkers;

import java.util.concurrent.atomic.AtomicInteger;
import me.itzsomebody.radon.utils.AccessUtils;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

public class DeprecatedAccessRemover extends Shrinker {
    @Override
    public void transform() {
        AtomicInteger counter = new AtomicInteger();

        getClassWrappers().stream().filter(classWrapper -> !excluded(classWrapper)).forEach(classWrapper -> {
            ClassNode classNode = classWrapper.classNode;

            if (AccessUtils.isDeprecated(classNode.access)) {
                classNode.access &= ~ACC_DEPRECATED;
                counter.incrementAndGet();
            }

            classWrapper.methods.stream().filter(methodWrapper -> !excluded(methodWrapper)
                    && AccessUtils.isDeprecated(methodWrapper.methodNode.access)).forEach(methodWrapper -> {
                methodWrapper.methodNode.access &= ~ACC_DEPRECATED;
                counter.incrementAndGet();
            });

            classWrapper.fields.stream().filter(fieldWrapper -> !excluded(fieldWrapper)
                    && AccessUtils.isDeprecated(fieldWrapper.fieldNode.access)).forEach(fieldWrapper -> {
                fieldWrapper.fieldNode.access &= ~ACC_DEPRECATED;
                counter.incrementAndGet();
            });
        });
    }

    @Override
    public String getName() {
        return "Useless Access Flags Remover";
    }
}
