package me.itzsomebody.radon.transformers.shrinkers;

import java.util.concurrent.atomic.AtomicInteger;
import me.itzsomebody.radon.Logger;
import org.objectweb.asm.tree.ClassNode;

public class InvisibleTypeAnnotationsRemover extends Shrinker {
    @Override
    public void transform() {
        AtomicInteger counter = new AtomicInteger();

        getClassWrappers().stream().filter(classWrapper -> !excluded(classWrapper)).forEach(classWrapper -> {
            ClassNode classNode = classWrapper.classNode;

            if (classNode.invisibleTypeAnnotations != null) {
                counter.addAndGet(classNode.invisibleTypeAnnotations.size());
                classNode.invisibleTypeAnnotations = null;
            }

            classWrapper.fields.stream().filter(fieldWrapper -> !excluded(fieldWrapper)
                    && fieldWrapper.fieldNode.invisibleTypeAnnotations != null).forEach(fieldWrapper -> {
                counter.addAndGet(fieldWrapper.fieldNode.invisibleTypeAnnotations.size());
                fieldWrapper.fieldNode.invisibleTypeAnnotations = null;
            });

            classWrapper.methods.stream().filter(methodWrapper -> !excluded(methodWrapper)
                    && methodWrapper.methodNode.invisibleTypeAnnotations != null).forEach(methodWrapper -> {
                counter.addAndGet(methodWrapper.methodNode.invisibleTypeAnnotations.size());
                methodWrapper.methodNode.invisibleTypeAnnotations = null;
            });
        });

        Logger.stdOut(String.format("Removed %d invisible type annotations.", counter.get()));
    }

    @Override
    public String getName() {
        return "Invisible Type Annotations Remover";
    }
}
