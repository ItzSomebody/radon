package me.itzsomebody.radon.transformers.shrinkers;

import java.util.concurrent.atomic.AtomicInteger;
import me.itzsomebody.radon.Logger;
import org.objectweb.asm.tree.ClassNode;

public class SignatureRemover extends Shrinker {
    @Override
    public void transform() {
        AtomicInteger counter = new AtomicInteger();

        getClassWrappers().stream().filter(classWrapper -> !excluded(classWrapper)).forEach(classWrapper -> {
            ClassNode classNode = classWrapper.classNode;

            if (classNode.signature != null) {
                classNode.signature = null;
                counter.incrementAndGet();
            }

            classWrapper.methods.stream().filter(methodWrapper -> !excluded(methodWrapper)
                    && methodWrapper.methodNode.signature != null).forEach(methodWrapper -> {
                methodWrapper.methodNode.signature = null;
                counter.incrementAndGet();
            });

            classWrapper.fields.stream().filter(fieldWrapper -> !excluded(fieldWrapper)
                    && fieldWrapper.fieldNode.signature != null).forEach(fieldWrapper -> {
                fieldWrapper.fieldNode.signature = null;
                counter.incrementAndGet();
            });
        });

        Logger.stdOut(String.format("Removed %d signatures.", counter.get()));
    }

    @Override
    public String getName() {
        return "Signature Remover";
    }
}
